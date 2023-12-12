/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package MainAppFrame;

import ClassFiles.ControllerManager;
import ClassFiles.ItemData;
import ClassFiles.ItemData2;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * FXML Controller class
 *
 * @author Gwyneth Uy
 */
public class CashierConfirmationFXMLController implements Initializable {

    @FXML
    private TableColumn<ItemData, Double> receiptPrice;

    @FXML
    private TableColumn<ItemData, Double> receiptAddOn;

    @FXML
    private TableColumn<ItemData, Double> receiptTotal;

    @FXML
    private TableColumn<ItemData, String> receiptProduct;

    @FXML
    private TableColumn<ItemData, Integer> receiptQuantity;

    @FXML
    private TableView<ItemData> receiptTV2;

    @FXML
    private Label handlerName1;

    @FXML
    private Label subTotal;

    @FXML
    private Label discount;

    @FXML
    private Label total;

    @FXML
    private Label cash;

    @FXML
    private Label change;

    @FXML
    private Label orderType;

    @FXML
    private Label orderID;

    @FXML
    private Label dateTime1;

    /**
     * Initializes the controller class.
     */
    private SettlePaymentFXMLController settlePaymentController;

    public void setSettlePaymentController(SettlePaymentFXMLController controller) {
        this.settlePaymentController = controller;
    }

    public void setOrderDetails(String handlerName, String dateTimeString, int customerID, String orderType, double subtotal, double discountApplied, double totalAmount, double cashAmount, double changeAmount) {

        // Concatenate peso sign to the values
        String pesoSign = "₱";
        String subtotalText = pesoSign + String.valueOf(subtotal);
        String discountText = (discountApplied != 0) ? "-" + pesoSign + String.valueOf(Math.abs(discountApplied)) : pesoSign + "0";
        String totalText = pesoSign + String.valueOf(totalAmount);
        String cashText = pesoSign + String.valueOf(cashAmount);
        String changeText = pesoSign + String.valueOf(changeAmount);

        dateTime1.setText(dateTimeString);
        handlerName1.setText(handlerName);
        orderID.setText(String.valueOf(customerID));
        subTotal.setText(subtotalText);
        discount.setText(discountText);
        total.setText(totalText);
        cash.setText(cashText);
        change.setText(changeText);

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        receiptTV2.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 10px;");

        System.out.println("CashierConfirmationFXMLController initialized");
        try {
            setupTableView();

        } catch (SQLException ex) {
            Logger.getLogger(CashierConfirmationFXMLController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ObservableList<ItemData> fetchOrderDetails() throws SQLException {
        ObservableList<ItemData> orderDetailsList = FXCollections.observableArrayList();

        CashierFXMLController cashierController = ControllerManager.getCashierController();
        int currentCustomerID = cashierController.getCurrentCustomerID();

        if (currentCustomerID != -1) {
            try (Connection conn = database.getConnection()) {
                if (conn != null) {
                    // Fetch orders from each table based on customer_id
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

                    try (PreparedStatement orderStmt = conn.prepareStatement(combinedSql)) {
                        // Bind parameters for all SELECT statements
                        for (int i = 1; i <= 7; i++) {
                            orderStmt.setInt(i, currentCustomerID);
                        }

                        try (ResultSet orderRs = orderStmt.executeQuery()) {
                            while (orderRs.next()) {
                                // Extract relevant order details (adjust as needed)
                                int orderId = orderRs.getInt("order_id");
                                String productName = orderRs.getString("item_name");
                                double finalPrice = orderRs.getDouble("final_price");
                                double addonsPrice = orderRs.getDouble("addons_price");
                                int quantity = orderRs.getInt("quantity");
                                double itemSizePrice = orderRs.getDouble("size_price");
                                double price = orderRs.getDouble("price");

                                // Check if size and add_ons columns are available
                                String size = orderRs.getString("size") != null ? orderRs.getString("size") : "";
                                String addons = orderRs.getString("add_ons") != null ? orderRs.getString("add_ons") : "";

                                // Create an ItemData instance with the fetched data
                                ItemData itemData = new ItemData(orderId, combineWithAddons(size, productName, addons), price, finalPrice, itemSizePrice, addonsPrice, quantity);

                                // Add the ItemData to the list
                                orderDetailsList.add(itemData);
                            }
                        }
                    }

                    // Update the table view with the fetched data
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle database-related exceptions
            }
        }
        return orderDetailsList;
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

    public void setupTableView() throws SQLException {
        ObservableList<ItemData> orderDetailsList = fetchOrderDetails();
        for (ItemData itemData : orderDetailsList) {

            System.out.println("Product Name: " + itemData.getItemName());
            System.out.println("Final Price: " + itemData.getItemPrice());
            System.out.println("Quantity: " + itemData.getItemQuantity());
        }
        try {
            receiptProduct.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getItemName()));
            receiptQuantity.setCellValueFactory(f -> new SimpleIntegerProperty(f.getValue().getItemQuantity()).asObject());
            receiptAddOn.setCellValueFactory(f -> new SimpleDoubleProperty(f.getValue().getAddOnPrice()).asObject());
            receiptTotal.setCellValueFactory(f -> new SimpleDoubleProperty(f.getValue().getItemPrice()).asObject());
            receiptPrice.setCellValueFactory(f -> new SimpleDoubleProperty(f.getValue().getDisplayPrice()).asObject());

            // Bind the TableView to the combined ObservableList
            receiptTV2.setItems(fetchOrderDetails());
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions accordingly
        }
    }

}
