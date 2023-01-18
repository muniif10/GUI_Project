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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    //    Runnable updateConstantly = new Runnable() {
//        @Override
//        public void run() {
//            for (int i = 0; i < clients.size(); i++) {
//                var client = clients.get(i);
//                try {
//                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(client.getOutputStream());
//                    ArrayList<ArrayList<Object>> newList = convertListToArrayList(currentList);
//                    objectOutputStream.writeObject(newList);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
//
//    };
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
        return newList;

    }

    Item_Bid getQueryItem(int id) throws SQLException {
        PreparedStatement stm = con.prepareStatement("select itemId, itemName, userId, description, starting_price, highest_bidder from item_bid where userId = ?");
        stm.setInt(1, id);
        ResultSet resultSet = stm.executeQuery();
        resultSet.next();
        return new Item_Bid(String.valueOf(resultSet.getInt(1)), resultSet.getString(2), resultSet.getString(3), resultSet.getString(6));
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
        return new Item_Bid(String.valueOf(resultSet.getInt(1)), resultSet.getString(2), resultSet.getString(3), resultSet.getString(6));
    }

    @FXML
    void initialize() throws SQLException {
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
            Item_Bid select = listOfItems.getSelectionModel().getSelectedItem();
            itemNameText.setText(select.getName());
            descriptionText.setText(select.getDescription());
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
                currentList.add(getQueryItem(nameTF.getText()));
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

                    System.out.println("Received new connection");
                    System.out.println("Connected to client " + client.getInetAddress().getHostAddress());
                    new Thread(() -> {
                        // TODO: Update the Item_Bid to allow bidding state to be stored
                        // TODO: Modify in tandem with state, the bidder.
                        new Thread(() -> {
                            System.out.println("Starting read-loop");
                            while (true) {
                                try {
                                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                                    ArrayList<ArrayList<Object>> newList = convertListToArrayList(currentList);
                                    objectOutputStream.writeObject(newList);
                                    objectOutputStream.close();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }).start();
                        // Deals with inputStream
                        while (true) {
                            try {
                                ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());

                                ArrayList<Object> itemToUpdate = (ArrayList<Object>) objectInputStream.readUnshared();
                                if (currentList != null) {
                                    for (int i = 0; i < currentList.size(); i++) {
                                        if (currentList.get(i).getItemID().equals(itemToUpdate.get(0))) {
                                            currentList.get(i).setHighest_bid((String) itemToUpdate.get(3));
                                        }
                                    }
                                }
                                objectInputStream.close();
                            } catch (IOException | ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }

                        }

                        // What is being sent over and how to add it inside the list
                        // We send item bid and update them. XXX Not possible as Item_Bid is not serializable
                        // We send data of relation using method in UserListView.

                    }).start();


//                    new Thread(() -> {
//                        ObservableList<Item_Bid> olList = FXCollections.observableArrayList();
//                        olList.add(new Item_Bid("01", "Daiki's ", "los", "200"));
//                        olList.add(new Item_Bid("02","Minecraft","Damn","80"));
//                        ArrayList<ArrayList<Object>> damnList = convertListToArrayList(olList);
//                        try {
//                            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
//                            out.writeUnshared(damnList);
//
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }).start();


                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        continuousConnect.start();
        System.out.println("Done all things now");


    }

}
