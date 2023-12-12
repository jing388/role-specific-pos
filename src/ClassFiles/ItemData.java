package ClassFiles;

import javafx.beans.property.*;

public class ItemData {

    private final ObjectProperty<Double> displayPrice = new SimpleObjectProperty<>();

    private final IntegerProperty orderID;
    private final StringProperty itemName;
    private final DoubleProperty itemPrice;
    private final DoubleProperty price;
    private final DoubleProperty addOnPrice;
    private final DoubleProperty itemSizePrice;
    private final IntegerProperty itemQuantity;
    private final StringProperty employeeName;
    private final StringProperty dateTime;

    public ItemData(int orderID, String itemName, double price, double itemPrice, double itemSizePrice, double addOnPrice, int itemQuantity) {
        this.orderID = new SimpleIntegerProperty(orderID);
        this.itemName = new SimpleStringProperty(itemName);
        this.price = new SimpleDoubleProperty(price);
        this.itemPrice = new SimpleDoubleProperty(itemPrice);
        this.itemSizePrice = new SimpleDoubleProperty(itemSizePrice);
        this.addOnPrice = new SimpleDoubleProperty(addOnPrice);
        this.itemQuantity = new SimpleIntegerProperty(itemQuantity);
        this.employeeName = new SimpleStringProperty("");
        this.dateTime = new SimpleStringProperty("");

        if (itemSizePrice != 0) {
            displayPrice.set(itemSizePrice);
        } else {
            displayPrice.set(price);
        }
    }

    public Integer getOrderID() {
        return orderID.get();
    }

    public void setOrderID(int orderID) {
        this.orderID.set(orderID);
    }

    public String getItemName() {
        return itemName.get();
    }

    public void setItemName(String name) {
        itemName.set(name);
    }

    public Double getPrice() {
        return price.get();
    }

    public void setPrice(double Price) {
        price.set(Price);
    }

    public Double getItemPrice() {
        return itemPrice.get();
    }

    public void setItemPrice(double price) {
        itemPrice.set(price);
    }

    public Double getAddOnPrice() {
        return addOnPrice.get();
    }

    public void setAddOnPrice(double addonPrice) {
        addOnPrice.set(addonPrice);
    }

    public Double getSizePrice() {
        return itemSizePrice.get();
    }

    public void setSizePrice(double sizePrice) {
        itemSizePrice.set(sizePrice);
    }

    public Integer getItemQuantity() {
        return itemQuantity.get();
    }

    public void setItemQuantity(int quantity) {
        itemQuantity.set(quantity);
    }

    public Double getDisplayPrice() {
        return displayPrice.get();
    }

    public ObjectProperty<Double> displayPriceProperty() {
        return displayPrice;
    }

    public String getEmployeeName() {
        return employeeName.get();
    }

    public void setEmployeeName(String name) {
        employeeName.set(name);
    }

    public String getDateTime() {
        return dateTime.get();
    }

    public void setDateTime(String dateTime) {
        this.dateTime.set(dateTime);
    }

    public StringProperty itemNameProperty() {
        return itemName;
    }

    public DoubleProperty itemPriceProperty() {
        return itemPrice;
    }

    public IntegerProperty itemQuantityProperty() {
        return itemQuantity;
    }

    public StringProperty employeeNameProperty() {
        return employeeName;
    }

    public StringProperty dateTimeProperty() {
        return dateTime;
    }
}
