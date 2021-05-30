package control;

import entity.Message;
import entity.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ReadThread extends Thread {
    private Socket socket;
    private MessageClient messageClient;
    private ObjectInputStream ois;

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
                System.out.println(o.toString());
                if (o instanceof User) {
                     list.add((User) o);
                     for (int i = 0; i < list.size(); i++) {
                         System.out.println(list.get(i).toString());
                     }
                     messageClient.setOnlineList(list);
                }
               else if (o instanceof Message) {
                   message = (Message) ois.readObject();
                   System.out.println(message.getText());
                   messageClient.addMessage(message);
               }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println(e);
            }

        }
    }
}
