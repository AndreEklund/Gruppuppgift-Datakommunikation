package control;

import entity.Message;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class WriteThread extends Thread implements PropertyChangeListener {
    private Socket socket;
    private MessageClient messageClient;
    private ObjectOutputStream oos;
    Message message;

    public WriteThread(Socket socket, MessageClient messageClient) throws IOException {
        this.socket = socket;
        this.messageClient = messageClient;
        messageClient.addPropertyChangeListener(this);
        oos = new ObjectOutputStream(socket.getOutputStream());
    }

    @Override
    public synchronized void run() {
        try {
            // send unsent messages
            while (true) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.err.println(e);
                    interrupt();
                }

                oos.writeObject(message);
                oos.flush();
            }
        } catch (IOException e) {
            messageClient.disconnect();
        }
    }

    public synchronized void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("message") && evt.getNewValue() instanceof Message) {
            message = (Message) evt.getNewValue();
            notify();
        }
    }
}
