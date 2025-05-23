package MainAppFrame;

import ClassFiles.CoffeeItemData;
import ClassFiles.ExtrasItemData;
import ClassFiles.FrappeItemData;
import ClassFiles.FruitDrinkItemData;
import Login.ControllerInterface;
import Login.LoginTest;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import javafx.scene.layout.AnchorPane;
import java.util.Set;
import java.util.HashSet;

import ClassFiles.ItemData;
import ClassFiles.MilkteaItemData;
import ClassFiles.OrderCardData;
import ClassFiles.RiceMealsItemData;
import ClassFiles.SnacksItemData;
import Databases.CRUDDatabase;
import MenuController.RiceMealController;
import MenuController.CoffeeController;
import MenuController.ExtrasController;
import MenuController.FrappeController;
import MenuController.FruitDrinkController;
import MenuController.MenuController;
import MenuController.SnacksController;
import com.mysql.cj.jdbc.Blob;
import javafx.scene.image.Image;

public class CashierFXMLController implements Initializable, ControllerInterface {

    double xOffset, yOffset;

    @FXML
    private Pane blurPane;

    @FXML
    private Label dateLbl;

    @FXML
    private Label timeLbl;

    @FXML
    private Label customerLabel;

    @FXML
    private TableColumn<ItemData, String> columnItemName;

    @FXML
    private TableColumn<ItemData, Double> columnItemPrice;

    @FXML
    private TableColumn<ItemData, Integer> columnItemQuantity;

    @FXML
    private TableColumn<ItemData, Double> columnAddOn;

    @FXML
    private TableColumn<ItemData, Double> columnPrice;

    @FXML
    private TableView<ItemData> receiptTable;

    @FXML
    private ImageView CloseButton;

    @FXML
    private Button takeOrderButton;

    @FXML
    private Button deleteItemButton;

    @FXML
    private Button deleteAllitemsButton;

    @FXML
    private Button getMenu1;

    @FXML
    private Button getMenu2;

    @FXML
    private Button getMenu3;

    @FXML
    private Button getMenu4;

    @FXML
    private Button getMenu5;

    @FXML
    private Button getMenu6;

    @FXML
    private Button getMenu7;

    @FXML
    private Stage stage;

    @FXML
    private GridPane menuGrid;

    @FXML
    private Button Logout;

    @FXML
    private Button takeOutOrderButton;

    @FXML
    private Label empName;

    @FXML
    private ImageView salesImageView;

    private Stage settlePaymentStage;

    private SettlePaymentFXMLController settlePaymentController;

    private CashierFXMLController existingCashierController;

    private volatile boolean stop = false;
    private LocalDate currentDate = LocalDate.now();

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
        System.out.println("CashierFXMLController - setEmployeeName: " + employeeName);

        empName.setText(employeeName);
    }

    public void setExistingCashierController(CashierFXMLController cashierController, String employeeName, int employeeId) {
        this.existingCashierController = cashierController;
        this.employeeName = employeeName;
        this.employeeId = employeeId;
    }

    private ObservableList<MilkteaItemData> milkteaListData = FXCollections.observableArrayList();
    private ObservableList<FruitDrinkItemData> fruitdrinkListData = FXCollections.observableArrayList();
    private ObservableList<FrappeItemData> frappeListData = FXCollections.observableArrayList();
    private ObservableList<CoffeeItemData> coffeeListData = FXCollections.observableArrayList();
    private ObservableList<RiceMealsItemData> ricemealsListData = FXCollections.observableArrayList();
    private ObservableList<SnacksItemData> snacksListData = FXCollections.observableArrayList();
    private ObservableList<ExtrasItemData> extrasListData = FXCollections.observableArrayList();

    public void setTableViewAndList(TableView<ItemData> tableView, ObservableList<ItemData> dataList) {
        this.receiptTable = tableView;
        this.menuMilkteaAndFrappeListData = dataList;
    }

    private String employeeName;
    private int employeeId;

    public void setEmployee(String employeeName, int employeeId) {
        this.employeeName = employeeName;
        this.employeeId = employeeId;

        empName.setText(employeeName);
    }

    public String getEmployeeName() {
        return employeeName;
    }

    private int customerID = 0;
    private int currentCustomerID = 0; // Initialize currentCustomerID

    private boolean initialDisplayDone = false;

    public void updateCustomerID() {
        // List of table names
        List<String> tableNames = Arrays.asList("milk_tea", "fruit_drink", "frappe", "coffee", "rice_meal", "snacks", "extras");

        // List to store maximum customer IDs
        List<Integer> maxCustomerIDs = new ArrayList<>();

        // Fetch maximum customer ID from each table
        for (String tableName : tableNames) {
            int maxCustomerID = getMaxCustomerID(tableName);
            maxCustomerIDs.add(maxCustomerID);
        }

        // Find the maximum customer ID across all tables
        int maxOverallCustomerID = maxCustomerIDs.stream().max(Integer::compare).orElse(0);

        // If the maximum customer ID is 0, set customerID to 0 and increment normally
        if (maxOverallCustomerID == 0) {
            customerID = 0 + 1;
        } else {
            // Increment the maximum customer ID to get the current customer ID
            customerID = maxOverallCustomerID + 1;
        }

        // Update the customer label
        customerLabel.setText(Integer.toString(customerID));

        // Update the currentCustomerID
        currentCustomerID = customerID;
        initialDisplayDone = true;
    }

    // Method to fetch the maximum customer ID from the UNION of all tables
    private int getMaxCustomerID(String tableName) {
        int maxCustomerID = 0;

        // Use your database connection and SQL query to fetch the maximum customer ID
        try (Connection connection = database.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT MAX(customer_id) FROM " + tableName); ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                maxCustomerID = resultSet.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database exceptions
        }

        return maxCustomerID;
    }

    public void incrementCurrentCustomerID() {
        // Increment the customer ID
        customerID++;

        // Update the customer label
        customerLabel.setText(Integer.toString(customerID));

        // Update the currentCustomerID
        currentCustomerID = customerID;
        initialDisplayDone = true;
    }

    public int getCurrentCustomerID() {
        return currentCustomerID;
    }

    public Pane getMyPane() {
        return blurPane;
    }

    @FXML
    void takeOutOrder(ActionEvent event) {
        try {
            // Load the SettlePaymentFXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SettlePaymentFXML.fxml"));
            Parent root = loader.load();

            // Create a new stage for the SettlePaymentFXML
            settlePaymentStage = new Stage();

            // Set stage properties to make it transparent and non-resizable
            settlePaymentStage.initStyle(StageStyle.TRANSPARENT);
            settlePaymentStage.setResizable(false);

            // Set the scene fill to transparent
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            // Set the scene to the stage
            settlePaymentStage.setScene(scene);

            // Get the controller for SettlePaymentFXML
            SettlePaymentFXMLController settlePaymentController = loader.getController();
            settlePaymentController.setEmployeeName(employeeName);
            settlePaymentController.setExistingCashierController(this, employeeName, employeeId);

            // Set the order type
            settlePaymentController.setOrderType("Dine In");

            settlePaymentStage.show();
            blurPane.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exceptions accordingly
        }
    }

    @FXML
    void takeOrder(ActionEvent event) {
        try {
            // Load the SettlePaymentFXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SettlePaymentFXML.fxml"));
            Parent root = loader.load();

            // Create a new stage for the SettlePaymentFXML
            settlePaymentStage = new Stage();

            // Set stage properties to make it transparent and non-resizable
            settlePaymentStage.initStyle(StageStyle.TRANSPARENT);
            settlePaymentStage.setResizable(false);

            // Set the scene fill to transparent
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            // Set the scene to the stage
            settlePaymentStage.setScene(scene);

            // Get the controller for SettlePaymentFXML
            SettlePaymentFXMLController settlePaymentController = loader.getController();
            settlePaymentController.setEmployeeName(employeeName);
            settlePaymentController.setExistingCashierController(this, employeeName, employeeId);

            // Set the order type
            settlePaymentController.setOrderType("Take Out");

            settlePaymentStage.show();
            blurPane.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exceptions accordingly
        }
    }

    public Stage getSettlePaymentStage() {
        return settlePaymentStage;
    }

    private void DateLabel() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter DateFormat = DateTimeFormatter.ofPattern("E dd MMM yyyy", Locale.ENGLISH);
        String formattedDate = currentDate.format(DateFormat);
        dateLbl.setText(formattedDate);
    }

    private void Timenow() {
        Thread thread = new Thread(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
            while (!stop) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.out.println(e);
                }
                final String timenow = sdf.format(new Date());
                Platform.runLater(() -> {
                    timeLbl.setText(timenow); // This is the label
                });
            }
        });

        thread.start();
    }

    @FXML
    private void getMenu1(ActionEvent event) throws SQLException {
        milkteaListData.clear();
        milkteaListData.addAll(menuGetData());

        refreshMenuGrid();
    }

    @FXML
    private void getMenu1() throws SQLException {
        milkteaListData.clear();
        milkteaListData.addAll(menuGetData());

        refreshMenuGrid();
    }

    @FXML
    private void getMenu2(ActionEvent event) throws SQLException {
        fruitdrinkListData.clear();
        fruitdrinkListData.addAll(menuGetDataForFruitDrink());

        refreshFruitDrinkGrid();
    }

    @FXML
    private void getMenu3(ActionEvent event) throws SQLException {
        frappeListData.clear();
        frappeListData.addAll(menuGetDataForFrappe());

        refreshFrappeGrid();
    }

    @FXML
    private void getMenu4(ActionEvent event) throws SQLException {
        coffeeListData.clear();
        coffeeListData.addAll(menuGetDataForCoffee());

        refreshCoffeeGrid();
    }

    @FXML
    private void getMenu5(ActionEvent event) throws SQLException {
        ricemealsListData.clear();
        ricemealsListData.addAll(menuGetDataForRiceMeals());

        refreshRiceMealsGrid();
    }

    @FXML
    private void getMenu6(ActionEvent event) throws SQLException {
        snacksListData.clear();
        snacksListData.addAll(menuGetDataForSnacks());

        refreshSnacksGrid();
    }

    @FXML
    private void getMenu7(ActionEvent event) throws SQLException {
        extrasListData.clear();
        extrasListData.addAll(menuGetDataForExtras());

        refreshExtrasGrid();
    }

    @FXML
    private void handleMousePressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    @FXML
    private void handleMouseDragged(MouseEvent event) {
        Stage stage = (Stage) ((Pane) event.getSource()).getScene().getWindow();
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public ObservableList<MilkteaItemData> menuGetData() {

        String sql = "SELECT * FROM milktea_items";

        ObservableList<MilkteaItemData> listData = FXCollections.observableArrayList();
        Connection connect = null;
        PreparedStatement prepare = null;
        ResultSet result = null;

        try {
            connect = CRUDDatabase.getConnection();
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            while (result.next()) {
                // Replace these column names with your actual column names from the "milktea_items" table
                String itemName = result.getString("item_name");
                String addons = result.getString("addons");
                String addonsPrice = result.getString("addons_price");
                Integer smallPrice = result.getInt("small_price");
                Integer mediumPrice = result.getInt("medium_price");
                Integer largePrice = result.getInt("large_price");
                Blob image = (Blob) result.getBlob("image");
                Integer itemID = result.getInt("item_ID");
                String status = result.getString("status");
                // Create a MilkteaItemData object and add it to the list
                MilkteaItemData milkteaItemData = new MilkteaItemData(itemName, addons, addonsPrice, smallPrice, mediumPrice, largePrice, image, itemID, status);
                listData.add(milkteaItemData);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources (result, prepare, connect) if needed
            try {
                if (result != null) {
                    result.close();
                }
                if (prepare != null) {
                    prepare.close();
                }
                if (connect != null) {
                    connect.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return listData;
    }

    public ObservableList<FruitDrinkItemData> menuGetDataForFruitDrink() {

        String sql = "SELECT * FROM fruitdrink_items";

        ObservableList<FruitDrinkItemData> listData = FXCollections.observableArrayList();
        Connection connect = null;
        PreparedStatement prepare = null;
        ResultSet result = null;

        try {
            connect = CRUDDatabase.getConnection();
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            while (result.next()) {
                // Replace these column names with your actual column names from the "milktea_items" table
                String itemName = result.getString("item_name");
                Integer smallPrice = result.getInt("small_price");
                Integer mediumPrice = result.getInt("medium_price");
                Integer largePrice = result.getInt("large_price");
                String fruitFlavor = result.getString("fruit_flavor");
                String sinkers = result.getString("sinkers");
                Blob image = (Blob) result.getBlob("image");
                Integer itemID = result.getInt("item_ID");
                String status = result.getString("status");

                // Create a MilkteaItemData object and add it to the list
                FruitDrinkItemData fruitDrinkItemData = new FruitDrinkItemData(itemName, smallPrice, mediumPrice, largePrice, fruitFlavor, sinkers, image, itemID, status);
                listData.add(fruitDrinkItemData);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources (result, prepare, connect) if needed
            try {
                if (result != null) {
                    result.close();
                }
                if (prepare != null) {
                    prepare.close();
                }
                if (connect != null) {
                    connect.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return listData;
    }

    public ObservableList<FrappeItemData> menuGetDataForFrappe() {

        String sql = "SELECT * FROM frappe_items";

        ObservableList<FrappeItemData> listData = FXCollections.observableArrayList();
        Connection connect = null;
        PreparedStatement prepare = null;
        ResultSet result = null;

        try {
            connect = CRUDDatabase.getConnection();
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            while (result.next()) {
                // Replace these column names with your actual column names from the "milktea_items" table
                String itemName = result.getString("item_name");
                Integer smallPrice = result.getInt("small_price");
                Integer mediumPrice = result.getInt("medium_price");
                Integer largePrice = result.getInt("large_price");
                Blob image = (Blob) result.getBlob("image");
                Integer itemID = result.getInt("item_ID");
                String status = result.getString("status");

                // Create a MilkteaItemData object and add it to the list
                FrappeItemData frappeItemData = new FrappeItemData(itemName, smallPrice, mediumPrice, largePrice, image, itemID, status);
                listData.add(frappeItemData);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources (result, prepare, connect) if needed
            try {
                if (result != null) {
                    result.close();
                }
                if (prepare != null) {
                    prepare.close();
                }
                if (connect != null) {
                    connect.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return listData;
    }

    public ObservableList<CoffeeItemData> menuGetDataForCoffee() {

        String sql = "SELECT * FROM coffee_items";

        ObservableList<CoffeeItemData> listData = FXCollections.observableArrayList();
        Connection connect = null;
        PreparedStatement prepare = null;
        ResultSet result = null;

        try {
            connect = CRUDDatabase.getConnection();
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            while (result.next()) {
                // Replace these column names with your actual column names from the "milktea_items" table
                String itemName = result.getString("item_name");
                String type = result.getString("type");
                Integer smallPrice = result.getInt("small_price");
                Integer mediumPrice = result.getInt("medium_price");
                Integer largePrice = result.getInt("large_price");
                Blob image = (Blob) result.getBlob("image");
                Integer itemID = result.getInt("item_ID");
                String status = result.getString("status");

                // Create a MilkteaItemData object and add it to the list
                CoffeeItemData coffeeItemData = new CoffeeItemData(itemName, type, smallPrice, mediumPrice, largePrice, image, itemID, status);
                listData.add(coffeeItemData);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources (result, prepare, connect) if needed
            try {
                if (result != null) {
                    result.close();
                }
                if (prepare != null) {
                    prepare.close();
                }
                if (connect != null) {
                    connect.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return listData;
    }

    public ObservableList<RiceMealsItemData> menuGetDataForRiceMeals() {

        String sql = "SELECT * FROM ricemeals_items";

        ObservableList<RiceMealsItemData> listData = FXCollections.observableArrayList();
        Connection connect = null;
        PreparedStatement prepare = null;
        ResultSet result = null;

        try {
            connect = CRUDDatabase.getConnection();
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            while (result.next()) {
                // Replace these column names with your actual column names from the "milktea_items" table
                String itemName = result.getString("item_name");
                Integer price = result.getInt("price");

                Blob image = (Blob) result.getBlob("image");
                Integer itemID = result.getInt("item_ID");
                String status = result.getString("status");

                // Create a MilkteaItemData object and add it to the list
                RiceMealsItemData riceMealsItemData = new RiceMealsItemData(itemName, price, image, itemID, status);
                listData.add(riceMealsItemData);

            }
        } catch (SQLException e) {

            e.printStackTrace();
        } finally {
            // Close resources (result, prepare, connect) if needed
            try {
                if (result != null) {
                    result.close();
                }
                if (prepare != null) {
                    prepare.close();
                }
                if (connect != null) {
                    connect.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return listData;
    }

    public ObservableList<SnacksItemData> menuGetDataForSnacks() {

        String sql = "SELECT * FROM snacks_items";

        ObservableList<SnacksItemData> listData = FXCollections.observableArrayList();
        Connection connect = null;
        PreparedStatement prepare = null;
        ResultSet result = null;

        try {
            connect = CRUDDatabase.getConnection();
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            while (result.next()) {
                // Replace these column names with your actual column names from the "milktea_items" table
                String itemName = result.getString("item_name");
                Integer price = result.getInt("price");

                Blob image = (Blob) result.getBlob("image");
                Integer itemID = result.getInt("item_ID");
                String status = result.getString("status");

                // Create a MilkteaItemData object and add it to the list
                SnacksItemData snacksItemData = new SnacksItemData(itemName, price, image, itemID, status);
                listData.add(snacksItemData);

            }
        } catch (SQLException e) {

            e.printStackTrace();
        } finally {
            // Close resources (result, prepare, connect) if needed
            try {
                if (result != null) {
                    result.close();
                }
                if (prepare != null) {
                    prepare.close();
                }
                if (connect != null) {
                    connect.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return listData;
    }

    public ObservableList<ExtrasItemData> menuGetDataForExtras() {

        String sql = "SELECT * FROM extras_items";

        ObservableList<ExtrasItemData> listData = FXCollections.observableArrayList();
        Connection connect = null;
        PreparedStatement prepare = null;
        ResultSet result = null;

        try {
            connect = CRUDDatabase.getConnection();
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            while (result.next()) {
                // Replace these column names with your actual column names from the "milktea_items" table
                String itemName = result.getString("item_name");
                Integer price = result.getInt("price");

                Blob image = (Blob) result.getBlob("image");
                Integer itemID = result.getInt("item_ID");
                String status = result.getString("status");

                // Create a MilkteaItemData object and add it to the list
                ExtrasItemData extrasItemData = new ExtrasItemData(itemName, price, image, itemID, status);
                listData.add(extrasItemData);

            }
        } catch (SQLException e) {

            e.printStackTrace();
        } finally {
            // Close resources (result, prepare, connect) if needed
            try {
                if (result != null) {
                    result.close();
                }
                if (prepare != null) {
                    prepare.close();
                }
                if (connect != null) {
                    connect.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return listData;
    }

    public ObservableList<ItemData> menuGetMilkteaAndFrappe() {
        ObservableList<ItemData> listData = FXCollections.observableArrayList();

        String customerIDText = customerLabel.getText();

        if (customerIDText.matches("\\d+")) {
            customerID = Integer.parseInt(customerIDText);
        } else {
            // Handle the case where customerIDText is not a valid number
            // You can display an error message or set a default value
            // Example: customerID = -1; // Default value for an invalid ID
        }

        // For milk_tea
        String combinedSql = "SELECT order_id, size, item_name, '' AS price, size_price, add_ons, addons_price, final_price, quantity, date_time FROM milk_tea WHERE customer_id = ? "
                + "UNION "
                + "SELECT order_id, size, item_name, '' AS price, size_price, '' AS add_ons, '' AS addons_price, final_price, quantity, date_time FROM fruit_drink WHERE customer_id = ? "
                + "UNION "
                + "SELECT order_id, size, item_name, '' AS price, size_price, '' AS add_ons, '' AS addons_price, final_price, quantity, date_time FROM frappe WHERE customer_id = ? "
                + "UNION "
                + "SELECT order_id, size, item_name, '' AS price, size_price, '' AS add_ons, '' AS addons_price, final_price, quantity, date_time FROM coffee WHERE customer_id = ? "
                + "UNION "
                + "SELECT order_id, '' AS size, item_name, price, '' AS size_price, '' AS add_ons, '' AS addons_price, final_price, quantity, date_time FROM rice_meal WHERE customer_id = ? "
                + "UNION "
                + "SELECT order_id, '' AS size, item_name, price, '' AS size_price, '' AS add_ons, '' AS addons_price, final_price, quantity, date_time FROM snacks WHERE customer_id = ? "
                + "UNION "
                + "SELECT order_id, '' AS size, item_name, price, '' AS size_price, '' AS add_ons, '' AS addons_price, final_price, quantity, date_time FROM extras WHERE customer_id = ? "
                + "ORDER BY date_time Asc";

        try (Connection connect = database.getConnection(); PreparedStatement combinedPrepare = connect.prepareStatement(combinedSql)) {

            combinedPrepare.setInt(1, customerID);
            combinedPrepare.setInt(2, customerID);
            combinedPrepare.setInt(3, customerID);
            combinedPrepare.setInt(4, customerID);
            combinedPrepare.setInt(5, customerID);
            combinedPrepare.setInt(6, customerID);
            combinedPrepare.setInt(7, customerID);

            ResultSet combinedResult = combinedPrepare.executeQuery();
            while (combinedResult.next()) {
                int orderID = combinedResult.getInt("order_id");
                String size = combinedResult.getString("size");
                String itemName = size.trim().isEmpty() ? combinedResult.getString("item_name") : combineWithAddons(size, combinedResult.getString("item_name"), combinedResult.getString("add_ons"));
                double price = combinedResult.getDouble("price");
                double itemSizePrice = combinedResult.getDouble("size_price");
                double addOnPrice = combinedResult.getDouble("addons_price");
                double itemPrice = combinedResult.getDouble("final_price");
                int itemQuantity = combinedResult.getInt("quantity");

                ItemData item = new ItemData(orderID, itemName, price, itemPrice, itemSizePrice, addOnPrice, itemQuantity);
                listData.add(item);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return listData;
    }

    private String combineWithAddons(String size, String itemName, String addon) {
        // Check if addon is not empty or contains non-whitespace characters
        if (addon != null && !addon.trim().isEmpty()) {
            // Concatenate with milk tea item
            return size + " " + itemName + " with " + addon;
        } else {
            // Not an add-on, return as is
            return size.trim().isEmpty() ? itemName : size + " " + itemName;
        }
    }

    private ObservableList<ItemData> menuMilkteaAndFrappeListData;

    public void setupTableView() {
        menuMilkteaAndFrappeListData = menuGetMilkteaAndFrappe();

        columnItemName.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getItemName()));
        columnItemPrice.setCellValueFactory(f -> new SimpleDoubleProperty(f.getValue().getItemPrice()).asObject());
        columnItemQuantity.setCellValueFactory(f -> new SimpleIntegerProperty(f.getValue().getItemQuantity()).asObject());
        columnAddOn.setCellValueFactory(f -> new SimpleDoubleProperty(f.getValue().getAddOnPrice()).asObject());
        columnPrice.setCellValueFactory(f -> new SimpleDoubleProperty(f.getValue().getDisplayPrice()).asObject());

        // Bind the TableView to the combined ObservableList
        receiptTable.setItems(menuMilkteaAndFrappeListData);
    }

    public void onDeleteItemButtonClicked(ActionEvent event) throws SQLException {
        ItemData selectedItem = receiptTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            // Prompt user for confirmation before deleting the row
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Item");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete this item?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                deleteItem(selectedItem); // Delete the selected item from the database
                menuMilkteaAndFrappeListData.remove(selectedItem); // Remove the item from the TableView
            }
        }
    }

    private void deleteItem(ItemData item) throws SQLException {
        System.out.println("Deleting item: " + item.getItemName());

        String dltMTsql = "DELETE FROM milk_tea WHERE order_id = ?";
        String dltfrappesql = "DELETE FROM frappe WHERE order_id = ?";
        String dltFDsql = "DELETE FROM fruit_drink WHERE order_id = ?";
        String dltCoffeeSql = "DELETE FROM coffee WHERE order_id = ?";
        String dltRiceMealSql = "DELETE FROM rice_meal WHERE order_id = ?";
        String dltSnacksSql = "DELETE FROM snacks WHERE order_id = ?";
        String dltExtrasSql = "DELETE FROM extras WHERE order_id = ?";

        try (Connection connect = database.getConnection(); PreparedStatement prepare = connect.prepareStatement(dltMTsql); PreparedStatement frapprepare = connect.prepareStatement(dltfrappesql); PreparedStatement FDprepare = connect.prepareStatement(dltFDsql); PreparedStatement coffeePrepare = connect.prepareStatement(dltCoffeeSql); PreparedStatement riceMealPrepare = connect.prepareStatement(dltRiceMealSql); PreparedStatement snacksPrepare = connect.prepareStatement(dltSnacksSql); PreparedStatement extrasPrepare = connect.prepareStatement(dltExtrasSql)) {

            prepare.setInt(1, item.getOrderID());
            frapprepare.setInt(1, item.getOrderID());
            FDprepare.setInt(1, item.getOrderID());
            coffeePrepare.setInt(1, item.getOrderID());
            riceMealPrepare.setInt(1, item.getOrderID());
            snacksPrepare.setInt(1, item.getOrderID());
            extrasPrepare.setInt(1, item.getOrderID());

            int rowsAffected = prepare.executeUpdate();
            int rowsAffectedFrappe = frapprepare.executeUpdate();
            int rowsAffectedFD = FDprepare.executeUpdate();
            int rowsAffectedCoffee = coffeePrepare.executeUpdate();
            int rowsAffectedRiceMeal = riceMealPrepare.executeUpdate();
            int rowsAffectedSnacks = snacksPrepare.executeUpdate();
            int rowsAffectedExtras = extrasPrepare.executeUpdate();

            System.out.println("Rows affected: " + rowsAffected);
            System.out.println("Rows affected (frappe): " + rowsAffectedFrappe);
            System.out.println("Rows affected (fruit_drink): " + rowsAffectedFD);
            System.out.println("Rows affected (coffee): " + rowsAffectedCoffee);
            System.out.println("Rows affected (rice_meal): " + rowsAffectedRiceMeal);
            System.out.println("Rows affected (snacks): " + rowsAffectedSnacks);
            System.out.println("Rows affected (extras): " + rowsAffectedExtras);

            System.out.println("SQL Parameters (order_id only): " + item.getOrderID());
        }
    }

    public void onDeleteAllitemsButtonClicked(ActionEvent event) throws SQLException {
        // Prompt user for confirmation before deleting all items
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete All Items");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete all items?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Set<Integer> orderIDs = new HashSet<>();

            // Collect unique order IDs from the list
            for (ItemData item : menuMilkteaAndFrappeListData) {
                orderIDs.add(item.getOrderID());
            }

            // Iterate through unique order IDs and delete items
            for (int orderID : orderIDs) {
                ItemData dummyItem = new ItemData(orderID, "",0, 0.0, 0, 0, 0);
                deleteItem(dummyItem);
            }

            menuMilkteaAndFrappeListData.clear(); // Clear all items from the TableView
        }
    }

    /*
    private void deleteAllitems(ItemData item) throws SQLException {

        String deleteAllMTsql = "DELETE FROM milk_tea WHERE order_id = ?";
        String deleteAllFrappeSql = "DELETE FROM frappe WHERE order_id = ?";
        String deleteAllFDSql = "DELETE FROM fruit_drink WHERE order_id = ?";

        try (Connection connect = database.getConnection(); PreparedStatement deleteAllMTPrepare = connect.prepareStatement(deleteAllMTsql); PreparedStatement deleteAllFrappePrepare = connect.prepareStatement(deleteAllFrappeSql); PreparedStatement deleteAllFDPrepare = connect.prepareStatement(deleteAllFDSql)) {

            int orderID = item.getOrderID();

            deleteAllMTPrepare.setInt(1, orderID);
            deleteAllFrappePrepare.setInt(1, orderID);
            deleteAllFDPrepare.setInt(1, orderID);

            int rowsAffectedMT = deleteAllMTPrepare.executeUpdate();
            int rowsAffectedFrappe = deleteAllFrappePrepare.executeUpdate();
            int rowsAffectedFD = deleteAllFDPrepare.executeUpdate();

            System.out.println("Rows affected (delete all milk tea): " + rowsAffectedMT);
            System.out.println("Rows affected (delete all frappe): " + rowsAffectedFrappe);
            System.out.println("Rows affected (delete all fruit drink): " + rowsAffectedFD);
        }
    } */
    private void refreshMenuGrid() throws SQLException {

        menuGrid.getChildren().clear();
        int column = 0;
        int row = 1;

        for (MilkteaItemData milkteaItemData : milkteaListData) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/MenuFXML/Milktea.fxml"));
                AnchorPane pane = loader.load();

                // Access the controller and set the data
                MenuController menuController = loader.getController();
                menuController.setMilkteaItemData(milkteaItemData);

                if (column == 1) {
                    column = 0;
                    ++row;
                }

                menuGrid.add(pane, column++, row);
                GridPane.setMargin(pane, new Insets(5));

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void refreshFruitDrinkGrid() throws SQLException {

        menuGrid.getChildren().clear();
        int column = 0;
        int row = 1;

        for (FruitDrinkItemData fruitDrinkItemData : fruitdrinkListData) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/MenuFXML/FruitDrink.fxml"));
                AnchorPane pane = loader.load();

                // Access the controller and set the data
                FruitDrinkController fruitDrinkController = loader.getController();
                fruitDrinkController.setFruitDrinkItemData(fruitDrinkItemData);

                if (column == 1) {
                    column = 0;
                    ++row;
                }

                menuGrid.add(pane, column++, row);
                GridPane.setMargin(pane, new Insets(5));

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void refreshFrappeGrid() throws SQLException {
        menuGrid.getChildren().clear();
        int column = 0;
        int row = 1;

        for (FrappeItemData frappeItemData : frappeListData) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/MenuFXML/Frappe.fxml"));
                AnchorPane pane = loader.load();

                // Access the controller and set the data
                FrappeController frappeController = loader.getController();
                frappeController.setFrappeItemData(frappeItemData);

                if (column == 1) {
                    column = 0;
                    ++row;
                }

                menuGrid.add(pane, column++, row);
                GridPane.setMargin(pane, new Insets(5));

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void refreshCoffeeGrid() throws SQLException {
        menuGrid.getChildren().clear();
        int column = 0;
        int row = 1;

        for (CoffeeItemData coffeeItemData : coffeeListData) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/MenuFXML/Coffee.fxml"));
                AnchorPane pane = loader.load();

                // Access the controller and set the data
                CoffeeController coffeeController = loader.getController();

                coffeeController.setCoffeeItemData(coffeeItemData);

                if (column == 1) {
                    column = 0;
                    ++row;
                }

                menuGrid.add(pane, column++, row);
                GridPane.setMargin(pane, new Insets(5));

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void refreshRiceMealsGrid() throws SQLException {
        menuGrid.getChildren().clear();
        int column = 0;
        int row = 1;

        for (RiceMealsItemData riceMealsItemData : ricemealsListData) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/MenuFXML/RiceMeal.fxml"));
                AnchorPane pane = loader.load();

                // Access the controller and set the data
                RiceMealController riceMealController = loader.getController();

                riceMealController.setRiceMealsItemData(riceMealsItemData);

                if (column == 1) {
                    column = 0;
                    ++row;
                }

                menuGrid.add(pane, column++, row);
                GridPane.setMargin(pane, new Insets(5));

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void refreshSnacksGrid() throws SQLException {
        menuGrid.getChildren().clear();
        int column = 0;
        int row = 1;

        for (SnacksItemData snacksItemData : snacksListData) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/MenuFXML/Snacks.fxml"));
                AnchorPane pane = loader.load();

                // Access the controller and set the data
                SnacksController snacksController = loader.getController();

                snacksController.setSnacksItemData(snacksItemData);

                if (column == 1) {
                    column = 0;
                    ++row;
                }

                menuGrid.add(pane, column++, row);
                GridPane.setMargin(pane, new Insets(5));

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void refreshExtrasGrid() throws SQLException {
        menuGrid.getChildren().clear();
        int column = 0;
        int row = 1;

        for (ExtrasItemData extrasItemData : extrasListData) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/MenuFXML/Extras.fxml"));
                AnchorPane pane = loader.load();

                // Access the controller and set the data
                ExtrasController extrasController = loader.getController();

                extrasController.setExtrasItemData(extrasItemData);

                if (column == 1) {
                    column = 0;
                    ++row;
                }

                menuGrid.add(pane, column++, row);
                GridPane.setMargin(pane, new Insets(5));

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // The rest of your initialize method...
        try {
            getMenu1();
        } catch (SQLException ex) {
            Logger.getLogger(CashierFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            refreshMenuGrid();
        } catch (SQLException ex) {
            Logger.getLogger(CashierFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }

        CloseButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    Stage stage = (Stage) CloseButton.getScene().getWindow();
                    stage.close();

                    // Open the login window.
                    LoginTest loginTest = new LoginTest();
                    loginTest.start(new Stage());

                } catch (Exception ex) {
                    Logger.getLogger(AdminFXMLController.class
                            .getName()).log(Level.SEVERE, null, ex);
                }

            }

        }
        );

        columnItemName.setSortable(true);
        columnItemPrice.setSortable(true);
        columnItemQuantity.setSortable(true);

        try {
            updateCustomerID();
            DateLabel();
            Timenow();
            setupTableView();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
