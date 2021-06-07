package control;

import entity.Message;
import entity.OnlineListMessage;
import entity.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Använder in ObjectInputStream för att läsa meddelanden från servern.
 */
public class ReadThread extends Thread {
    private MessageClient messageClient;
    private ObjectInputStream ois;

    /**
     * Konstruktor.
     * @param socket socketen som används.
     * @param messageClient kontrollern för klienten.
     * @throws IOException om ett I/O error uppstår.
     */
    public ReadThread(Socket socket, MessageClient messageClient) throws IOException {
        this.messageClient = messageClient;
        ois = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Tråd som läser meddelanden ifrån servern.
     * Om det är en lista av aktiva användare så uppdateras listan.
     * Om det är ett meddelande, så läggs det till i meddelandelistan.
     */
    @Override
    public void run() {
        Message message;
        ArrayList<User> list;
        while (true) {
            try {
                Object o = ois.readObject();
                if (o instanceof Message) {
                    message = (Message) o;
                    if (message instanceof OnlineListMessage){
                        list = ((OnlineListMessage) message).getOnlineList();
                        System.out.println(list.get(0).getUserName());
                        messageClient.setOnlineList(list);
                    }
                    else {
                        message.setDateReceived(LocalDateTime.now());
                        messageClient.setMessageListGUI(message);
                    }
                }

            } catch (IOException | ClassNotFoundException e) {
                messageClient.disconnect();
            }

        }
    }
}
