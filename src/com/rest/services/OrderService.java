package com.rest.services;

import com.rest.models.*;
import com.rest.repository.MenuRepository;
import com.rest.repository.OrderRepository;
import com.rest.repository.RestaurantRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class OrderService {
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;
    private final CartService cartService;
    private final PaymentService paymentService;

    public OrderService(OrderRepository orderRepository,
                        RestaurantRepository restaurantRepository,
                        MenuRepository menuRepository,
                        CartService cartService) {
        this(orderRepository, restaurantRepository, menuRepository, cartService, null);
    }

    public OrderService(OrderRepository orderRepository,
                        RestaurantRepository restaurantRepository,
                        MenuRepository menuRepository,
                        CartService cartService,
                        PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.menuRepository = menuRepository;
        this.cartService = cartService;
        this.paymentService = paymentService;
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public Order placeOrder(User customer, Cart cart, LocalTime orderTime) {
        return placeOrder(customer, cart, orderTime, PaymentMode.CASH_ON_DELIVERY, "Cash on Delivery");
    }

    public Order placeOrder(User customer, Cart cart, LocalTime orderTime, PaymentMode paymentMode, String paymentDetails) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        if (cart == null || cart.isEmpty()) {
            throw new IllegalStateException("Cannot place an order with an empty cart");
        }

        String restaurantId = cart.getRestaurantId();
        Restaurant restaurant = restaurantRepository.findById(restaurantId);
        if (restaurant == null) {
            throw new IllegalStateException("Restaurant not found");
        }

        if (!restaurant.isOpen()) {
            throw new IllegalStateException("Restaurant '" + restaurant.getName() + "' is currently closed.");
        }

        if (!restaurant.isWithinOperatingHours(orderTime)) {
            throw new IllegalStateException("Restaurant is outside operating hours.");
        }

        double subtotal = cart.getSubtotal();
        if (subtotal < restaurant.getMinOrderValue()) {
            throw new IllegalStateException("Order subtotal ($" + String.format("%.2f", subtotal) +
                    ") is below minimum order value ($" + String.format("%.2f", restaurant.getMinOrderValue()) + ").");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem ci : cart.getItems()) {
            MenuItem currentItem = menuRepository.findById(ci.getMenuItemId());
            if (currentItem == null || !currentItem.isAvailable()) {
                throw new IllegalStateException("Item '" + ci.getItemName() + "' is unavailable. Please remove it from cart.");
            }
            orderItems.add(new OrderItem(ci.getMenuItemId(), ci.getItemName(), ci.getUnitPrice(), ci.getQuantity()));
        }

        PaymentMode mode = paymentMode != null ? paymentMode : PaymentMode.CASH_ON_DELIVERY;
        String details = (paymentDetails != null && !paymentDetails.trim().isEmpty()) ? paymentDetails : mode.getDisplayName();

        CartService.OrderTotals totals = cartService.calculateTotals(cart);
        String orderId = orderRepository.nextId();

        String paymentId = null;
        PaymentStatus paymentStatus = (mode == PaymentMode.CASH_ON_DELIVERY) ? PaymentStatus.PENDING : PaymentStatus.PAID;

        if (paymentService != null) {
            Payment payment = paymentService.processPayment(orderId, customer.getId(), totals.getTotalAmount(), mode, details);
            paymentId = payment.getPaymentId();
            paymentStatus = payment.getStatus();
        }

        Order order = new Order(
                orderId,
                customer.getId(),
                customer.getFullName(),
                restaurant.getId(),
                restaurant.getName(),
                orderItems,
                totals.getSubtotal(),
                totals.getTax(),
                totals.getDeliveryFee(),
                totals.getTotalAmount(),
                LocalDateTime.now(),
                OrderStatus.PLACED,
                mode,
                paymentStatus,
                paymentId,
                details
        );

        orderRepository.save(order);
        cart.clear();
        return order;
    }

    public Order updateOrderStatus(String orderId, String ownerId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found");
        }
        Restaurant restaurant = restaurantRepository.findById(order.getRestaurantId());
        if (restaurant == null || !restaurant.getOwnerId().equals(ownerId)) {
            throw new SecurityException("Unauthorized: You do not own the restaurant for this order");
        }

        order.transitionTo(newStatus);

        if (newStatus == OrderStatus.COMPLETED) {
            order.setPaymentStatus(PaymentStatus.PAID);
            if (paymentService != null) {
                paymentService.markPaymentCompleted(orderId);
            }
        } else if (newStatus == OrderStatus.CANCELLED) {
            if (paymentService != null) {
                Payment payment = paymentService.refundPayment(orderId);
                if (payment != null) {
                    order.setPaymentStatus(payment.getStatus());
                }
            } else {
                order.setPaymentStatus(PaymentStatus.REFUNDED);
            }
        }

        orderRepository.save(order);
        return order;
    }

    public Order cancelOrderByCustomer(String orderId, String customerId) {
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found");
        }
        if (!order.getCustomerId().equals(customerId)) {
            throw new SecurityException("Unauthorized: You did not place this order");
        }
        if (!order.getStatus().isCancellableByCustomer()) {
            throw new IllegalStateException("Cannot cancel order in status '" + order.getStatus() + "'.");
        }

        order.transitionTo(OrderStatus.CANCELLED);

        if (paymentService != null) {
            Payment payment = paymentService.refundPayment(orderId);
            if (payment != null) {
                order.setPaymentStatus(payment.getStatus());
            }
        } else {
            order.setPaymentStatus(PaymentStatus.REFUNDED);
        }

        orderRepository.save(order);
        return order;
    }

    public Order getOrderById(String orderId) {
        return orderRepository.findById(orderId);
    }

    public List<Order> getOrdersByCustomer(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public List<Order> getOrdersForOwner(String ownerId) {
        List<Restaurant> ownerRestaurants = restaurantRepository.findByOwnerId(ownerId);
        List<String> restIds = new ArrayList<>();
        for (Restaurant r : ownerRestaurants) {
            restIds.add(r.getId());
        }

        List<Order> result = new ArrayList<>();
        for (Order o : orderRepository.getAll()) {
            if (restIds.contains(o.getRestaurantId())) {
                result.add(o);
            }
        }
        return result;
    }

    public List<Order> getOrdersByRestaurant(String restaurantId, String ownerId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId);
        if (restaurant == null || !restaurant.getOwnerId().equals(ownerId)) {
            throw new SecurityException("Unauthorized");
        }
        return orderRepository.findByRestaurantId(restaurantId);
    }

    public static class RestaurantSummary {
        private final String restaurantId;
        private final String restaurantName;
        private final int totalOrders;
        private final double totalRevenue;
        private final Map<OrderStatus, Integer> ordersByStatus;
        private final Map<String, Integer> popularItems;

        public RestaurantSummary(String restaurantId, String restaurantName, int totalOrders,
                                 double totalRevenue, Map<OrderStatus, Integer> ordersByStatus,
                                 Map<String, Integer> popularItems) {
            this.restaurantId = restaurantId;
            this.restaurantName = restaurantName;
            this.totalOrders = totalOrders;
            this.totalRevenue = totalRevenue;
            this.ordersByStatus = ordersByStatus;
            this.popularItems = popularItems;
        }

        public String getRestaurantId() {
            return restaurantId;
        }

        public String getRestaurantName() {
            return restaurantName;
        }

        public int getTotalOrders() {
            return totalOrders;
        }

        public double getTotalRevenue() {
            return totalRevenue;
        }

        public Map<OrderStatus, Integer> getOrdersByStatus() {
            return ordersByStatus;
        }

        public Map<String, Integer> getPopularItems() {
            return popularItems;
        }
    }

    public RestaurantSummary getRestaurantSummary(String restaurantId, String ownerId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId);
        if (restaurant == null || !restaurant.getOwnerId().equals(ownerId)) {
            throw new SecurityException("Unauthorized");
        }

        List<Order> orders = orderRepository.findByRestaurantId(restaurantId);
        int totalOrders = orders.size();
        double totalRevenue = 0.0;
        Map<OrderStatus, Integer> byStatus = new HashMap<>();
        for (OrderStatus s : OrderStatus.values()) {
            byStatus.put(s, 0);
        }

        Map<String, Integer> popularItems = new HashMap<>();

        for (Order o : orders) {
            byStatus.put(o.getStatus(), byStatus.get(o.getStatus()) + 1);
            if (o.getStatus() == OrderStatus.COMPLETED) {
                totalRevenue += o.getTotalAmount();
            }
            if (o.getStatus() != OrderStatus.CANCELLED) {
                for (OrderItem oi : o.getItems()) {
                    int currentCount = popularItems.containsKey(oi.getItemName()) ? popularItems.get(oi.getItemName()) : 0;
                    popularItems.put(oi.getItemName(), currentCount + oi.getQuantity());
                }
            }
        }

        return new RestaurantSummary(restaurant.getId(), restaurant.getName(), totalOrders,
                totalRevenue, byStatus, popularItems);
    }
}
