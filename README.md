# 🍽️ Restaurant Management & Food Ordering System (Java CLI)

A comprehensive, object-oriented console application developed in Java for managing restaurants, online food ordering, and table reservations. Built using clean layered architecture patterns with an in-memory persistence layer.

---

## 🚀 Key Features

### 👤 Customer Portal
- **Authentication**: User registration and secure login.
- **Restaurant & Menu Browsing**: Explore available restaurants, view categorized menus, prices, and item availability.
- **Cart Management**: Add items, adjust quantities, view subtotal, and empty cart.
- **Order Placement & Tracking**: Place dine-in/takeaway/delivery orders, track live order statuses, and cancel active orders.
- **Table Reservation**: Reserve dining tables with real-time seat availability checks.
- **Payment Processing**: Integrated checkout flow supporting multiple payment modes (UPI, Card, Cash on Delivery).

### 👨‍🍳 Restaurant Owner Portal
- **Restaurant Profile**: Setup and update restaurant metadata.
- **Menu Management**: Add, update, toggle availability, and remove menu items.
- **Order Processing**: Review incoming orders and update delivery/preparation states (`Placed` ➔ `Confirmed` ➔ `Preparing` ➔ `Delivered`).
- **Table Management**: Configure tables, seating capacity, and track reservations.
- **Sales & Analytics**: View customer orders and monitor overall revenue.

---

## 🏗️ Architecture & Project Structure

The project follows a clean, decoupled **Layered Architecture (3-Tier)**:

```text
.
├── Main.java                          # Application bootstrap & dependency wiring
└── src/
    └── com/
        └── rest/
            ├── CLI/                   # Presentation Layer (Console UI)
            │   ├── CustomerCli.java
            │   ├── MainMenuCli.java
            │   ├── OwnerCli.java
            │   └── TableCli.java
            ├── models/                # Domain Entities & Enums
            │   ├── Cart.java / CartItem.java
            │   ├── MenuItem.java
            │   ├── Order.java / OrderItem.java
            │   ├── Payment.java / Table.java / User.java
            │   └── ...
            ├── repository/            # Data Access Layer (In-memory Store)
            │   ├── BaseRepository.java
            │   ├── StorageManager.java
            │   ├── OrderRepository.java
            │   └── ...
            ├── services/              # Business Logic Layer
            │   ├── AuthService.java
            │   ├── CartService.java
            │   ├── MenuService.java
            │   ├── OrderService.java
            │   ├── PaymentService.java
            │   └── RestaurantService.java
            └── util/                  # Helper utilities (ConsoleReader, Currency, Time, Security)
```

---

## 🛠️ Tech Stack & Design Patterns

- **Language**: Java 8+ / 17+
- **Persistence**: In-memory repository with pre-seeded data (`StorageManager`)
- **Key OOP & Architectural Concepts**:
  - **Repository Pattern**: Clean decoupling of business logic from persistence.
  - **Dependency Injection**: Services and repositories injected via constructors in `Main.java`.
  - **Encapsulation & Polymorphism**: Interfaces like `Payable` and generic repositories (`BaseRepository<T>`).
  - **Role-Based Access Control (RBAC)**: Distinct navigation flows for `CUSTOMER` and `OWNER` roles.

---

## ⚡ Getting Started

### Prerequisites
- [Java Development Kit (JDK 8 or higher)](https://www.oracle.com/java/technologies/downloads/)
- Terminal / Command Prompt / IDE (VS Code, IntelliJ IDEA, Eclipse)

### Compilation & Execution

1. **Clone the repository**:
   ```bash
   git clone https://github.com/SHWE1530/Restaurant_Management_System.git
   cd Restaurant_Management_System
   ```

2. **Compile the project**:
   ```bash
   javac -d bin -sourcepath src Main.java
   ```

3. **Run the application**:
   ```bash
   java -cp bin Main
   ```

---

## 🤝 Contributing
Contributions, issues, and feature requests are welcome!

## 📝 License
This project is open-source and available under the MIT License.
