import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.image.ImageView;


public class Item_Bid {
    private final SimpleStringProperty itemID;
    private ObjectProperty<ImageView> photo;
    private final SimpleStringProperty name;
    private final SimpleStringProperty description;
    private final SimpleStringProperty highest_bid;
    private SimpleListProperty<String[]> history = new SimpleListProperty<>(); // String array stores Name and amount

    public Item_Bid(String itemID, String name, String description, String highest_bid) {
        this.itemID = new SimpleStringProperty(itemID);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.highest_bid = new SimpleStringProperty(highest_bid);
    }

    public Item_Bid(String itemID, String name, String description, String highest_bid, ImageView image) {
        this.itemID = new SimpleStringProperty(itemID);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.highest_bid = new SimpleStringProperty(highest_bid);
        this.history = new SimpleListProperty<>();
        this.photo = new SimpleObjectProperty<>(image);
    }

    public String getItemID() {
        return itemID.get();
    }

    public void setItemID(String itemID) {
        this.itemID.set(itemID);
    }

    public SimpleStringProperty itemIDProperty() {
        return itemID;
    }

    public ImageView getPhoto() {
        return photo.get();
    }
    public void setPhoto(ImageView image){
        this.photo = new SimpleObjectProperty<>(image);
    }


    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public String getHighest_bid() {
        return highest_bid.get();
    }

    public void setHighest_bid(String highest_bid) {
        this.highest_bid.set(highest_bid);
    }

    public SimpleStringProperty highest_bidProperty() {
        return highest_bid;
    }

    public ObservableList<String[]> getHistory() {
        return history.get();
    }

    public void setHistory(ObservableList<String[]> history) {
        this.history.set(history);
    }

    public SimpleListProperty<String[]> historyProperty() {
        return history;
    }
}
