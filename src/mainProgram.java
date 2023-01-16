import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class mainProgram extends Application {

    // Planning
    // CLIENT SECTION
    // User View of items available (TableView of object Item_Bid) -> Storing inside an Observable List
    // User View should have the column, Item ID, Photo, Name, Description,  Highest Bid.
    // To pass this into the TableView, we need the object to have those items together with their SimpleProperty attributes
    // Action can be taken by the user to bid for an item, including the bid amount.
    // The details about which item to bid on, how much was bid and who was the bidder has to be transferred.

    // SERVER SECTION
    // Admin View
    // Admin view of items available for bidding. TableView of Item_Bid) stored inside ObservableList
    //


    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent login = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("loginPage.fxml")));
//        var secondStage = new Stage();
//        secondStage.setScene(new Scene(login));
//        secondStage.show();

//        BorderPane bp = new BorderPane();
//        ObservableList<String> list = FXCollections.observableArrayList();
//        list.addAll("DAmn","dsads");
//        ComboBox<String> combo = new ComboBox<>(list);
//        bp.setCenter(combo);
        primaryStage.setTitle("Bidding Software Login Page");
        primaryStage.setScene(new Scene(login));
        primaryStage.show();
    }
}
