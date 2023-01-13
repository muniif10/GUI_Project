import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

import javax.swing.text.html.ImageView;


public class Item_Bid {
    public Item_Bid(SimpleStringProperty itemID, ObjectProperty<ImageView> photo, SimpleStringProperty name, SimpleStringProperty description, SimpleStringProperty highest_bid, SimpleListProperty<String[]> history) {
        this.itemID = itemID;
        this.photo = photo;
        this.name = name;
        this.description = description;
        this.highest_bid = highest_bid;
        this.history = history;
    }



    private SimpleStringProperty itemID;

    private ObjectProperty<ImageView> photo;

    private SimpleStringProperty name,description,highest_bid;
    private SimpleListProperty<String[]> history = new SimpleListProperty<>(this, "history"); // String array stores Name and amount

    public String getItemID() {
        return itemID.get();
    }

    public SimpleStringProperty itemIDProperty() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID.set(itemID);
    }

    public ImageView getPhoto() {
        return photo.get();
    }

    public ObjectProperty<ImageView> photoProperty() {
        return photo;
    }

    public void setPhoto(ImageView photo) {
        this.photo.set(photo);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getDescription() {
        return description.get();
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getHighest_bid() {
        return highest_bid.get();
    }

    public SimpleStringProperty highest_bidProperty() {
        return highest_bid;
    }

    public void setHighest_bid(String highest_bid) {
        this.highest_bid.set(highest_bid);
    }

    public ObservableList<String[]> getHistory() {
        return history.get();
    }

    public SimpleListProperty<String[]> historyProperty() {
        return history;
    }

    public void setHistory(ObservableList<String[]> history) {
        this.history.set(history);
    }
}
