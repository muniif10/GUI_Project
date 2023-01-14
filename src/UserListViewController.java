import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.sql.Connection;

public class UserListViewController {

    @FXML
    private Text amountSpent;

    @FXML
    private Button bidButton;

    @FXML
    private Text currentBid;

    @FXML
    private TableColumn<Item_Bid, String> descCol;

    @FXML
    private Text description;

    @FXML
    private TableColumn<Item_Bid, String> highCol;

    @FXML
    private ImageView imageUsed;

    @FXML
    private Text itemName;

    @FXML
    private Text itemName2;

    @FXML
    private TableView<Item_Bid> itemTable;

    @FXML
    private TitledPane item_bid_pane;

    @FXML
    private TitledPane item_list_pane;

    @FXML
    private VBox leftInfoBid;

    @FXML
    private TableColumn<Item_Bid, String> nameCol;

    @FXML
    private TableColumn<Item_Bid, String> noCol;

    @FXML
    private TitledPane profile_pane;

    @FXML
    private VBox rightInfoBid;

    @FXML
    private Text username;

    @FXML
    private Text yourBid;

    User currentUser;
    Connection con;


    public void initData(User user, Connection con) {
        this.con = con;
        username.setText("Name: " + user.getUsername());
        currentUser = user;
    }

    @FXML
    void initialize() {
        ObservableList<Item_Bid> list = FXCollections.observableArrayList();
        Item_Bid item1 = new Item_Bid("1","Daiki's Bike","Renowned bike of daiki","50");
        itemTable.setItems(list);
        list.add(item1);
        nameCol.setCellValueFactory(f-> new SimpleStringProperty(f.getValue().getName()));
        descCol.setCellValueFactory(f->new SimpleStringProperty(f.getValue().getDescription()));
        noCol.setCellValueFactory(f->new SimpleStringProperty(f.getValue().getItemID()));
        highCol.setCellValueFactory(f->new SimpleStringProperty(f.getValue().getHighest_bid()));
    }
}
