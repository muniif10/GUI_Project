import javafx.fxml.FXML;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class UserListViewController {
    User currentUser;
    @FXML
    public void initialize(){



    }
    public void initData(User user){
        username.setText("Name: "+user.getUsername());
        currentUser = user;
    }

    @FXML
    private Text currentBid;

    @FXML
    private Text description;

    @FXML
    private ImageView imageUsed;

    @FXML
    private Text itemName;

    @FXML
    private Text itemName2;

    @FXML
    private TitledPane item_bid_pane;

    @FXML
    private TitledPane item_list_pane;
    @FXML
    private Text username;
    @FXML
    private Text amountSpent;

    @FXML
    private TitledPane profile_pane;

    @FXML
    private Text yourBid;

}