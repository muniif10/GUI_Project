import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;


public class sampleServer {
    public static ArrayList<Object> convertItemToArrayList(Item_Bid item){
        ArrayList<Object> newList = new ArrayList<>();
        newList.add(item.getItemID());
        newList.add(item.getName());
        newList.add(item.getDescription());
        newList.add(item.getHighest_bid());
        return newList;

    }
    public static ArrayList<ArrayList<Object>> convertListToArrayList(ObservableList<Item_Bid> list){
        ArrayList<ArrayList<Object>> newList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            newList.add(convertItemToArrayList(list.get(i)));
        }
        return newList;
    }
    Socket socket;


    public static void main(String[] args) {
        Socket socket;
        String ip;
        ObjectInputStream ois;// = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream oos ;//= new ObjectInputStream(socket.getOutputStream());
        Thread broadcast = new Thread(()->{
            while (true){
                try {
                    broadcast();
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        broadcast.start();

        Thread continuousConnect = new Thread(()->{
            while (true) {
                try {
                    ServerSocket serverSocket = new ServerSocket(5454);
                    Socket client = serverSocket.accept();
                    System.out.println("Received new connection");
                    System.out.println("Connected to client " + client.getInetAddress().getHostAddress());
                    new Thread(() -> {
                        ObservableList<Item_Bid> olList = FXCollections.observableArrayList();
                        olList.add(new Item_Bid("01", "Daiki's ", "los", "200"));
                        ArrayList<ArrayList<Object>> damnList = convertListToArrayList(olList);
                        try {
                            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                            out.writeUnshared(damnList);

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).start();

                } catch (Exception ignored) {

                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }});
        continuousConnect.start();
        System.out.println("Done all things now");
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
}
