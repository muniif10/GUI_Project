import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.ImageView;

import java.io.Serializable;


public class Item_Bid extends AdminListViewController implements Serializable {
    private final SimpleStringProperty itemID;
    private final SimpleStringProperty name;
    private final SimpleStringProperty description;
    private final SimpleStringProperty highest_bid;
    private ObjectProperty<ImageView> photo;

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

    public void setPhoto(ImageView image) {
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

}