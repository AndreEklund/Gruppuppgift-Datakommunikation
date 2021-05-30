package control;

import entity.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

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
        Message message;
        while (true) {
            try {
                message = (Message)ois.readObject();
                // view message or update online list
            } catch (IOException | ClassNotFoundException e) {
                System.err.println(e);
            }

        }
    }
}
