import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AdminListViewController {

    private final ObservableList<Item_Bid> currentList = FXCollections.observableArrayList();
    ArrayList<Socket> clients = new ArrayList<>();
    User currentUser;
    Connection con;
    String userID = "1";
    @FXML
    private Button addButton;
    @FXML
    private Button deleteBut;
    @FXML
    private TextField descTF;
    @FXML
    private Text descriptionText;
    @FXML
    private TextField initTF;
    @FXML
    private HBox insertFileHere;
    @FXML
    private Text itemNameText;
    @FXML
    private TableView<Item_Bid> listOfItems;
    @FXML
    private TableColumn<Item_Bid, String> itemCol;
    @FXML
    private TextField nameTF;
    @FXML
    private ImageView photoOfItem;
    @FXML
    private Text serverConnectionLabel;
    @FXML
    private Button refreshBut;
    @FXML
    private Text clientMax;
    @FXML
    private Text soldAt;

    public static ArrayList<Object> convertItemToArrayList(Item_Bid item) {
        ArrayList<Object> newList = new ArrayList<>();
        newList.add(item.getItemID());
        newList.add(item.getName());
        newList.add(item.getDescription());
        newList.add(item.getHighest_bid());
        return newList;

    }

    public static ArrayList<ArrayList<Object>> convertListToArrayList(ObservableList<Item_Bid> list) {
        ArrayList<ArrayList<Object>> newList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            newList.add(convertItemToArrayList(list.get(i)));
        }
        return newList;
    }

    public static void broadcast() throws IOException, InternalError, InterruptedException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress group = InetAddress.getByName("224.0.0.1");
        DatagramPacket packet;
        byte[] buf = "Packet for client".getBytes();
        packet = new DatagramPacket(buf, buf.length, group, 4545);
        socket.send(packet);
        socket.close();
        Thread.sleep(100);

    }

    public void initData(User user, Connection con) {
        this.con = con;
        System.out.println("Connection set");
//        username.setText("Name: " + user.getUsername());
        currentUser = user;
    }

    ObservableList<Item_Bid> getQueryItemAll() throws SQLException {

        System.out.println("Attempting to get data from DB");
        PreparedStatement stm = con.prepareStatement("select itemId, itemName, description, starting_price from item_bid");
        ResultSet resultSet = stm.executeQuery();
        ObservableList<Item_Bid> newList = FXCollections.observableArrayList();

        while (resultSet.next()) {
            newList.add(new Item_Bid(String.valueOf(resultSet.getInt(1)), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4)));
        }
        refreshBut.setDisable(false);
        return newList;

    }

    Item_Bid getQueryItem(int id) throws SQLException {
        PreparedStatement stm = con.prepareStatement("select itemId, itemName, userId, description, starting_price, highest_bidder from item_bid where userId = ?");
        stm.setInt(1, id);
        ResultSet resultSet = stm.executeQuery();
        resultSet.next();
        return new Item_Bid(String.valueOf(resultSet.getInt(1)), resultSet.getString(2), resultSet.getString(4), resultSet.getString(6));
    }
    //    Around 6 Qs
//    Total 30%
//    What is operating system, basic instruction circle or cycle?, uniprog and multiprog, relationship between user thread and
//    kernel thread, Many to many, one to one, on to many (DRAW FIGURES).
//    Process states, Scheduling algorithm (SJFT, Priority, RR), DIfference preemptive or emptive, Banker's Algo table
//    Buddy system, Page and segmentation
//    Virtual memory Algo fifo lru optimal
//    C scan, C-loop, the disk algos, the differences
//
//

    Item_Bid getQueryItem(String name) throws SQLException {
        PreparedStatement stm = con.prepareStatement("select itemId, itemName, userId, description, starting_price, highest_bidder from item_bid where itemName = ?");
        stm.setString(1, name);
        ResultSet resultSet = stm.executeQuery();
        resultSet.next();
        return new Item_Bid(String.valueOf(resultSet.getInt(1)), resultSet.getString(2), resultSet.getString(4), resultSet.getString(6));
    }

    @FXML
    void initialize() throws SQLException {
        deleteBut.setOnAction(event -> {
                    PreparedStatement stm = null;
                    try {
                        stm = con.prepareStatement("delete from item_bid where itemId = ? ");
                        stm.setInt(1, Integer.parseInt(listOfItems.getSelectionModel().getSelectedItem().getItemID()));
                        stm.executeUpdate();
                        currentList.setAll(getQueryItemAll());
//            currentList.remove(listOfItems.getSelectionModel().getSelectedItem());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                });
        itemCol.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getName()));
        listOfItems.setItems(currentList);
        refreshBut.setOnMouseClicked((MouseEvent e) -> {
            try {
                currentList.setAll(getQueryItemAll());
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        listOfItems.setOnMouseClicked(event -> {
            String clientName;
            Item_Bid select = listOfItems.getSelectionModel().getSelectedItem();
            String itemId = select.getItemID();
            try {
                PreparedStatement stm1 = con.prepareStatement("select userId from item_bid where itemId = ?");
                stm1.setInt(1, Integer.parseInt(itemId));
                ResultSet res1 = stm1.executeQuery();
                res1.next();
                userID = res1.getString(1);
                PreparedStatement stm = con.prepareStatement("select display_name from credentials where userId = ? ");
                stm.setInt(1, Integer.parseInt(userID));
                ResultSet res = stm.executeQuery();
                res.next();
                clientName = res.getString(1);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            itemNameText.setText(select.getName());
            descriptionText.setText(select.getDescription());
            soldAt.setText("Sold at: " + select.getHighest_bid());
            clientMax.setText("Client with highest bid: " + clientName);
        });
        addButton.setOnMouseClicked((MouseEvent e) -> {
            try {
                PreparedStatement stm = con.prepareStatement("insert into item_bid(itemName, userId, description, starting_price) values (?,?,?,?)");
                stm.setString(1, nameTF.getText());
                stm.setInt(2, Integer.parseInt(currentUser.ID));
                stm.setString(3, descTF.getText());
                stm.setFloat(4, Float.parseFloat(initTF.getText()));
                stm.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            try {
                currentList.setAll(getQueryItemAll());
//                currentList.add(getQueryItem(nameTF.getText()));
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });


        Thread broadcast = new Thread(() -> {
            while (true) {
                try {
                    broadcast();
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        broadcast.start();

        Thread continuousConnect = new Thread(() -> {

            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(5454);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            while (true) {
                try {
                    Socket client = serverSocket.accept();
                    clients.add(client);
                    new Thread(() -> {
                        try {
                            ObjectOutputStream outputStream = new ObjectOutputStream(client.getOutputStream());
                            DataInputStream dataInputStream = new DataInputStream(client.getInputStream());

                            while (true) {
                                var request = dataInputStream.readInt();
                                if (request == 0) {
//                                    Sending bid item
//                                    - Read ArrayList<Object>
                                    userID = dataInputStream.readUTF();
                                    var item = (ArrayList<Object>) new ObjectInputStream(client.getInputStream()).readUnshared();
                                    System.out.println("Received a new bid");
                                    var ob = new Item_Bid((String) item.get(0), (String) item.get(1), (String) item.get(2), (String) item.get(3));
                                    PreparedStatement stm = con.prepareStatement("update item_bid set starting_price = ?, userId = ? where itemId = ?;");
                                    double d = Double.parseDouble(ob.getHighest_bid());
                                    int highest = (int) d;
                                    System.out.println(ob.getItemID() + " " + highest);

                                    stm.setInt(3, Integer.parseInt(ob.getItemID()));
                                    stm.setInt(2, Integer.parseInt(userID));
                                    stm.setInt(1, Integer.valueOf(highest));
                                    stm.executeUpdate();
                                    System.out.println("Executed update query");
                                    currentList.setAll(getQueryItemAll());

                                } else if (request == 1) {
//                                    Requesting latest list
                                    ArrayList<ArrayList<Object>> data = convertListToArrayList(currentList);
                                    new DataOutputStream(client.getOutputStream()).writeInt(data.hashCode());
                                    outputStream.writeUnshared(data);


                                } else if (request == 2) {
//                                    Exiting the connection


                                }
                            }
                        } catch (IOException | ClassNotFoundException | SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }).start();


                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        continuousConnect.start();
        System.out.println("Done all things now");


    }

}
