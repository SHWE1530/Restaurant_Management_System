import com.rest.CLI.MainMenuCli;
import com.rest.repository.*;
import com.rest.services.*;
import com.rest.util.ConsoleReader;

/**
 * Main application entry point for the Java Restaurant Management System.
 * Runs purely in-memory with pre-seeded demo data.
 */
public class Main {
    public static void main(String[] args) {
        // Initialize Persistence Layer (in-memory)
        StorageManager storageManager = new StorageManager();

        UserRepository userRepository = new UserRepository(storageManager);
        RestaurantRepository restaurantRepository = new RestaurantRepository(storageManager);
        MenuRepository menuRepository = new MenuRepository(storageManager);
        OrderRepository orderRepository = new OrderRepository(storageManager);
        TableRepository tableRepository = new TableRepository(storageManager);
        PaymentRepository paymentRepository = new PaymentRepository(storageManager);

        // Initialize Business Logic / Services Layer
        AuthService authService = new AuthService(userRepository);
        RestaurantService restaurantService = new RestaurantService(restaurantRepository);
        MenuService menuService = new MenuService(menuRepository, restaurantRepository);
        CartService cartService = new CartService();
        PaymentService paymentService = new PaymentService(paymentRepository);
        OrderService orderService = new OrderService(orderRepository, restaurantRepository, menuRepository, cartService, paymentService);
        TableServices tableServices = new TableServices(tableRepository);

        // Initialize User Interface / CLI Layer
        ConsoleReader reader = new ConsoleReader();

        MainMenuCli app = new MainMenuCli(
                authService,
                restaurantService,
                menuService,
                cartService,
                orderService,
                tableServices,
                reader
        );

        app.run();
    }
}