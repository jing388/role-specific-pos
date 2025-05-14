# Role-Specific POS System

A JavaFX-based point-of-sale system with role-specific interfaces for **cashiers**, **kitchen staff**, and **administrators**. The system is interconnected and uses **MySQL** as its database backend.

## 🔧 Requirements

To run and build this project, you’ll need:

1. [Zulu JDK 21+](https://www.azul.com/downloads/?version=java-21-lts&os=windows&architecture=x86-64&package=jdk)
2. [JavaFX SDK 21+](https://gluonhq.com/products/javafx/) (download and configure the `--module-path`)
3. [XAMPP](https://www.apachefriends.org/index.html) – for MySQL and PHPMyAdmin
4. MySQL Connector/J (should be added to your project libraries)

## 📦 Setup Instructions

1. Clone the repository or download the ZIP.
2. Set up JavaFX in NetBeans:
   - Go to **Project Properties > Libraries**
   - Add JavaFX SDK `lib` folder to the **Module Path**
   - In **Run VM Options**, add:
     ```
     --module-path "path\to\javafx\lib" --add-modules javafx.controls,javafx.fxml
     ```

3. Set up the database:
   - Open XAMPP and start **MySQL**
   - Import the provided SQL file (`milk_tea.sql`) using **phpMyAdmin**
   - Update database credentials in the source code if necessary

4. Run the program:
   - Navigate to the `Login` folder
   - Run the `LoginTest.java` file to start the application

## 🚀 Features Overview

### 🔐 Login Tab
- 4-digit PIN login system
- Role-based access (Cashier, Kitchen Staff, Admin)
- "Call Admin" button as a "Forgot PIN" option
- Admin can manage user roles and access

### 🍽 Main Tab (Cashier)
- Dynamic menu selection (Milk Tea, Fruit Drinks, Frappe, Coffee, Rice Meals, Snacks)
- Customization (Size, Sugar Level, Add-ons, Special Requests)
- Order type selection (Dine-in, Takeout, Delivery, Drive-thru)
- Discount & loyalty code support (manual entry)
- Receipt generation with formatted output
- Cash handling with change calculation

### 🔧 Orders Tab (Kitchen)
- Real-time order list with statuses: New, In Progress, Completed
- Order preparation time tracking
- Void/cancel option for mistaken orders

### 📊 Analytics Tab (Admin)
- Track sales metrics and order volume
- Daily, weekly, and monthly sales reports
- Inventory insights by popularity and category

### 📦 Inventory & Admin Tools
- Inventory tracking and restocking
- CRUD operations on users, coupons, and archived data

## 🚫 Limitations

- No e-wallet/debit card integration (offline/local setup only)

---

## 📌 Notes
This is a local desktop-based POS system designed for small-scale businesses. It is optimized for offline use and built for educational and practical learning purposes.
