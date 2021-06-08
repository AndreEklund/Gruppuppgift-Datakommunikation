package control;

import entity.Message;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Använder en ObjectOutputStream för att skriva meddelande till servern.
 */
public class WriteThread extends Thread implements PropertyChangeListener {
    private MessageClient messageClient;
    private ObjectOutputStream oos;
    Message message;

    /**
     * Konstruktor.
     * @param socket socketen som används.
     * @param messageClient kontrollern för klienten.
     * @throws IOException om ett I/O error uppstår.
     */
    public WriteThread(Socket socket, MessageClient messageClient) throws IOException {
        this.messageClient = messageClient;
        messageClient.addPropertyChangeListener(this);
        oos = new ObjectOutputStream(socket.getOutputStream());
    }

    /**
     * Tråd som skickar meddelanden till servern
     * Först, skickar meddelanden med vem som har anslutit och att servern ska skicka listan av aktiva användare.
     * Sover tills den ska skriva meddelanden till servern..
     */
    @Override
    public synchronized void run() {
        try {
            oos.writeObject(new Message(messageClient.getCurrentUser(),null,"Connect",
                    messageClient.getCurrentUser().getImage()));
            oos.writeObject(new Message(messageClient.getCurrentUser(),null,"ActiveUsers",
                    messageClient.getCurrentUser().getImage()));
            while (true) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.err.println(e);
                    break;
                }

                oos.writeObject(message);
                oos.flush();
            }
        } catch (IOException e) {
            messageClient.disconnect();
        }
    }

    /**
     * Väcker tråden för att skicka ett nytt meddelande.
     * @param evt händelsen.
     */
    public synchronized void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("message") && evt.getNewValue() instanceof Message) {
            message = (Message) evt.getNewValue();
            notify();
        }
    }

}
