import javafx.beans.property.SimpleObjectProperty;
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

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class UserListViewController {
    User currentUser;
    Connection con;
    Socket socket;
    List<Item_Bid> listNew;
    ArrayList<ArrayList<Object>> listFromServer;
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
    DataOutputStream dataOutputStream;
    ObjectInputStream objectInputStream;

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
        System.out.println("Connected."+ socket.getInetAddress().getHostAddress());

        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());

        nameCol.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getName()));
        descCol.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getDescription()));
        noCol.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getItemID()));
        highCol.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getHighest_bid()));
        photoCol.setCellValueFactory(f->new SimpleObjectProperty<>(f.getValue().getPhoto()));

        while(true){
            Thread.sleep(5000);
            getItemLists();
        }

    }
int updateSentinel = 0;


void getItemLists() throws IOException, ClassNotFoundException {
        if (socket.isConnected()){
            dataOutputStream.writeInt(1);
            int newVersion = new DataInputStream(socket.getInputStream()).readInt();
            System.out.println("Old value " + updateSentinel + "\n New Value " + newVersion);
            if (updateSentinel == 0){ // Case of first time being sent
                var serverList =(ArrayList<ArrayList<Object>>) objectInputStream.readUnshared();
                updateSentinel = newVersion;
                itemTable.setItems(regenerateList(serverList));
            } else if (updateSentinel == newVersion) {
                objectInputStream.readUnshared();

                return;
            }else {
                var serverList =(ArrayList<ArrayList<Object>>) objectInputStream.readUnshared();
                itemTable.setItems(regenerateList(serverList));
                updateSentinel = newVersion;
            }
        }
}

    @FXML
    void initialize() {
    bidButton.setOnAction(event -> {

    });
        itemTable.setOnMouseClicked((e)->{
            var item = itemTable.getSelectionModel().getSelectedItem();
            itemName.setText(item.getName());
            itemName2.setText(item.getName());
            description.setText(item.getDescription());
            currentBid.setText(item.getHighest_bid());
            yourBid.setText("Your Bid: Not yet bid");



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
