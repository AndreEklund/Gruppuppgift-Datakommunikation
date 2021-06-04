package control;

import boundary.ClientGui;
import entity.Message;
import entity.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ReadThread extends Thread {
    private Socket socket;
    private MessageClient messageClient;
    private ObjectInputStream ois;
    private ClientGui gui;

    public ReadThread(Socket socket, MessageClient messageClient) throws IOException {
        this.socket = socket;
        this.messageClient = messageClient;
        ois = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        Message message= null;
        ArrayList<User> list= new ArrayList<>();
        while (true) {
            try {
                Object o = ois.readObject();
                if (o instanceof ArrayList){
                    list = (ArrayList) o;
                    System.out.println(list.get(0).getUserName());
                    messageClient.setOnlineList(list);
                }
               else if (o instanceof Message) {
                   message = (Message) o;
                   message.setDateReceived(LocalDateTime.now());
                   messageClient.setMessageListGUI(message);
               }
            } catch (IOException | ClassNotFoundException e) {
                messageClient.disconnect();
            }

        }
    }
}
