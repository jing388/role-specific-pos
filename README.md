# Role-Specific POS System

This JavaFX-based point-of-sale (POS) system features **role-specific interfaces** for cashiers, kitchen staff, and administrators. Built using Java, MySQL, and JavaFX, this system handles real-time ordering, kitchen queueing, and administrative analytics — all in one connected local platform.


## Requirements

To build and run this project, ensure the following are installed:

- [Zulu JDK 21+](https://www.azul.com/downloads/?version=java-21-lts&os=windows&architecture=x86-64&package=jdk)
- [JavaFX SDK](https://gluonhq.com/products/javafx/)
- [XAMPP](https://www.apachefriends.org/) (for MySQL)
- MySQL JDBC Connector


## Setup Instructions

1. **Clone or download** this repository.
2. **Configure JavaFX** in NetBeans:
   - Go to **Project Properties > Libraries**
   - Add the JavaFX SDK `lib` folder to the **Module Path**
   - Under **Run > VM Options**, add:
     ```
     --module-path "path\to\javafx\lib" --add-modules javafx.controls,javafx.fxml
     ```

3. **Set up the database:**
   - Start MySQL via XAMPP.
   - Import the provided `.sql` file using **phpMyAdmin**.
   - If you don’t have the schema, request it at: **uy.gwynethbscs2022@gmail.com**

4. **Run the app:**
   - Navigate to `Login` folder
   - Run `LoginTest.java`


## Key Features

### Login Interface (All Roles)
- PIN-based login system (4-digit)
- Role-assigned access (Admin, Cashier, Kitchen Staff)
- “Call Admin” button acts as a password recovery method
- Cashiers and Kitchen users have distinct PIN rules

### Cashier Interface
- Menu-based ordering with modifiers and custom requests
- Dynamic order types: dine-in, takeout, delivery, drive-thru
- Receipt generation with itemized customization and formatting
- Manual entry of discount codes or loyalty rewards
- Handles cash and change tracking

### Kitchen Interface
- Real-time queue of orders with status updates
- Status options: Pending, In Progress, Completed
- Tracks order preparation durations
- Cancel/void orders supported

### Admin Interface
- User management (add/update roles and accounts)
- Sales tracking and analytics (daily/weekly/monthly)
- Inventory tracking and popularity insights
- Archive and CRUD support for past orders and user data


## Note:

**This project is designed for educational and offline use. Networked deployment or cloud hosting is not required or supported.**
