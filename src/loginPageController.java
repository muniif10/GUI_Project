import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;


public class loginPageController {

    private final ObservableList<String> items = FXCollections.observableArrayList();
    private final Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "muniifroot");
    @FXML
    private Button loginBut;
    @FXML
    private PasswordField password;
    @FXML
    private Button resetBut;
    // Make a method to request login details from the server if correct
    // For now we use hardcoded authentication.
    @FXML
    private ComboBox<String> userType = new ComboBox<>(items);
    @FXML
    private TextField username;

    public loginPageController() throws SQLException {
    }

    @FXML
    void initialize() throws SQLException {

        if (!con.isClosed()) {
            System.out.println("Connected to database.");
            con.createStatement().execute("use gui_project");
        }
        if (con.isClosed()) {
            System.exit(999);
        }
        password.setOnAction((e) -> {
            loginBut.fire();
        });
        items.addAll("Bidder", "Bidding Host");
        userType.setItems(items);
        userType.getSelectionModel().select(0);
    }

    @FXML
    void clean(ActionEvent event) {
        username.setStyle("-fx-background-color: white");

        password.setStyle("-fx-background-color: white");
    }

    @FXML
    void goLogin(ActionEvent event) throws IOException, SQLException {

        PreparedStatement stat = con.prepareStatement("select username, password, userId, accountType from credentials where username = ? and password = ? ");
        stat.setString(1, username.getText());
        stat.setString(2, password.getText());
        username.setDisable(true);
        password.setDisable(true);
        ResultSet result = stat.executeQuery();

        boolean hasOutput = result.next();
        int accType = result.getInt(4);
        int userSelection = userType.getSelectionModel().getSelectedIndex() + 1;
        if (hasOutput && (accType == (userSelection + 1))) { // Bidder
            User userLoggedIn = new User();
            userLoggedIn.username = result.getString("username");
            userLoggedIn.ID = String.valueOf(result.getInt(3));

            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            stage.close();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserListView.fxml"));
            stage.setScene(new Scene(loader.load()));


//            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("UserListView.fxml")));
            UserListViewController controller = loader.getController();
            controller.initData(userLoggedIn, con);
            stage.setTitle("Bidding View");

//            Scene scene = new Scene(root);
//            stage.setTitle("Bidding View");
//            stage.setScene(scene);
            stage.show();
            stage.setOnCloseRequest(da -> {
                Platform.exit();
            });

        } else if (hasOutput && (accType != userSelection)) { // Host
            User userLoggedIn = new User();
            userLoggedIn.username = result.getString("username");
            userLoggedIn.ID = String.valueOf(result.getInt(3));

            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            stage.close();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminListView.fxml"));
            stage.setScene(new Scene(loader.load()));


//            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("UserListView.fxml")));
            AdminListViewController controller = loader.getController();
            controller.initData(userLoggedIn, con);
            stage.setTitle("Admin View");

//            Scene scene = new Scene(root);
//            stage.setTitle("Bidding View");
//            stage.setScene(scene);
            stage.show();
            stage.setOnCloseRequest(da -> {
                Platform.exit();
            });

        } else {
            username.setStyle("-fx-background-color: red");
            username.setText("INVALID");
            password.setStyle("-fx-background-color: red");
            password.clear();
        }


    }


    @FXML
    void resetFields(ActionEvent event) {
        username.clear();
        password.clear();
        username.setStyle("");
        password.setStyle("");
        username.setDisable(false);
        password.setDisable(false);
    }

}
