package control;

import entity.*;

import java.net.*;
import java.io.*;

public class MessageClient {
    //private Ui ui;
    private User user;
    private Contacts contacts;
    private Socket socket;

    public MessageClient() {
        //ui
        //read contacts
    }

    public User getUser() {
        return user;
    }

    public void setUser() {
        if (user != null) {
            this.user = user;
        }
    }

    public void connect(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            new ReadThread(socket, this).start();
            new WriteThread(socket, this). start();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void disconnect() {
        try {
            socket.close();
            //write contact list to file
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    // add contact
    // remove contact
    
}
