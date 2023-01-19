import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserListViewController {
    User currentUser;
    Connection con;
    Socket socket;
    List<Item_Bid> listNew;
    ArrayList<ArrayList<Object>> listFromServer;
    DataOutputStream dataOutputStream;
    ObjectInputStream objectInputStream;
    int updateSentinel = 0;
    Item_Bid currentItem;
    private String serverIP;
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
    @FXML
    private TableColumn<Item_Bid, ImageView> photoCol;
    @FXML
    private TextField bidValue;

    public static ObservableList<Item_Bid> regenerateList(ArrayList<ArrayList<Object>> oldList) {
        ObservableList<Item_Bid> newList = FXCollections.observableArrayList();
        for (int i = 0; i < oldList.size(); i++) {
            if (oldList.get(i).size() > 4) {
                newList.add(new Item_Bid((String) oldList.get(i).get(0), (String) oldList.get(i).get(1), (String) oldList.get(i).get(2), (String) oldList.get(i).get(3), (ImageView) oldList.get(i).get(4)));

            } else {
                newList.add(new Item_Bid((String) oldList.get(i).get(0), (String) oldList.get(i).get(1), (String) oldList.get(i).get(2), (String) oldList.get(i).get(3)));
            }

        }
        return newList;

    }

    public void initData(User user, Connection con) {
        this.con = con;
        username.setText("Name: " + user.getUsername());
        currentUser = user;
    }

    public void startConnection() throws IOException, ClassNotFoundException, InterruptedException {
        String serverIP;
        int serverPort;
        System.out.println("Trying to read multicast");

        try (MulticastSocket mult = new MulticastSocket(4545)) { // Create a socket to receive multicast packet
            InetAddress group = InetAddress.getByName("224.0.0.1"); // Create the multicast address group
            byte[] buf = new byte[1024]; // Data to be transfered inside the packet, can be smaller
            mult.joinGroup(group); // Assign the multicast socket to the multicast address group
            DatagramPacket packet = new DatagramPacket(buf, buf.length); // The packet to be used to store the multicast packet
            mult.receive(packet); // Awaits the packet
            serverIP = packet.getAddress().getHostAddress(); // Store the server
            mult.leaveGroup(group);
            // Close
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        socket = new Socket(serverIP, 5454);
        System.out.println("Connected." + socket.getInetAddress().getHostAddress());

        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());

        nameCol.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getName()));
        descCol.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getDescription()));
        noCol.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getItemID()));
        highCol.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getHighest_bid()));
        photoCol.setCellValueFactory(f -> new SimpleObjectProperty<>(f.getValue().getPhoto()));

        while (true) {
            Thread.sleep(200);
            getItemLists();
        }

    }

    void getItemLists() throws IOException, ClassNotFoundException {
        if (socket.isConnected()) {
            dataOutputStream.writeInt(1);
            int newVersion = new DataInputStream(socket.getInputStream()).readInt();
            System.out.println("Old value " + updateSentinel + "\n New Value " + newVersion);
            if (updateSentinel == 0) { // Case of first time being sent
                var serverList = (ArrayList<ArrayList<Object>>) objectInputStream.readUnshared();
                updateSentinel = newVersion;
                itemTable.setItems(regenerateList(serverList));
            } else if (updateSentinel == newVersion) {
                objectInputStream.readUnshared();

            } else {
                var serverList = (ArrayList<ArrayList<Object>>) objectInputStream.readUnshared();
                itemTable.setItems(regenerateList(serverList));
                updateSentinel = newVersion;
            }
        }
    }

    void updateLatest() {
        for (int i = 0; i < itemTable.getItems().size(); i++) {
            var cur = itemTable.getItems().get(i);
            if (cur.getItemID().equals(currentItem.getItemID())) {
                currentItem = cur;

            }
        }
    }
String clientName;
    @FXML
    void initialize()  {
        profile_pane.setOnMouseClicked(e->{
            PreparedStatement stm = null;
            try {
                stm = con.prepareStatement("select display_name from credentials where userId = ? ");

            stm.setInt(1, Integer.parseInt(currentUser.ID));
            ResultSet res = stm.executeQuery();
            res.next();
            clientName = res.getString(1);
            username.setText("Display Name: "+clientName);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            amountSpent.setText("");
        });

        bidButton.setOnAction(event -> {
//         Call method to verify and send data to host.
            updateLatest();
            // Compare value if correct
            if (Double.parseDouble(bidValue.getText()) > Double.parseDouble(currentItem.getHighest_bid())) {
                yourBid.setText("Your bid: " + bidValue.getText());
                currentItem.setHighest_bid(bidValue.getText());
                try {
                    dataOutputStream.writeInt(0);
                    System.out.println("Sending new bid");
                    dataOutputStream.writeUTF(currentUser.getId());
                    new ObjectOutputStream(socket.getOutputStream()).writeUnshared(AdminListViewController.convertItemToArrayList(currentItem));
                    System.out.println("sent objec");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            currentBid.setText("Current Highest Bid: " + bidValue.getText());
            yourBid.setText("Your Bid: " + bidValue.getText());
            bidValue.setText("");
        });
        itemTable.setOnMouseClicked((e) -> {
            try {
                currentItem = itemTable.getSelectionModel().getSelectedItem();
                itemName.setText(currentItem.getName());
                itemName2.setText(currentItem.getName());
                description.setText(currentItem.getDescription());
                currentBid.setText("Current Highest Bid: " + currentItem.getHighest_bid());
                yourBid.setText("Your Bid: Not yet bid");
            } catch (NullPointerException ignored) {
            }


        });
        new Thread(() -> {
            try {
                startConnection();
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                throw new RuntimeException(e);
            }

        }).start();
    }
}
