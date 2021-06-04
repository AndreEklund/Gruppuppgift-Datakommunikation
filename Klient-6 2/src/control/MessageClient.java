package control;

import boundary.ClientGui;
import entity.*;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class MessageClient {
    //private Ui ui;
    private Contacts contacts;
    private User currentUser;
    private ArrayList<Message> messageList;
    private ArrayList<User> onlineList;
    private ArrayList<User> receiverList;
    private Socket socket;
    private ClientGui gui;
    private ObjectOutputStream oos;

    private final PropertyChangeSupport change = new PropertyChangeSupport(this);

    public MessageClient() {
        contacts = new Contacts("files/contacts.dat");
        messageList = new ArrayList<Message>();
        onlineList = new ArrayList<>();
        receiverList = new ArrayList<>();
        currentUser = new User();

        JFrame frame = new JFrame("Client UI");
        frame.setLocation(200, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui = new ClientGui(this);
        frame.add(gui);
        frame.pack();
        frame.setVisible(true);


    }
    public void viewMessage(int index) {
        if (index >= 0 && index < messageList.size()) {
            Message message = messageList.get(index);
            gui.setMessage(message.getText(), message.getIcon());
        }
    }

    /*public void viewMessage(int index) {
        if (index >= 0 && index < messageList.size()) {
            Message message = messageList.get(index);
            gui.setMessage(message.getText(), message.getIcon());
        }
    }*/

    public void setOnlineList(ArrayList<User> onlineList) {
        this.onlineList = onlineList;
        System.out.println("onlinelist set");
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        if (currentUser != null) {
            this.currentUser = currentUser;
        }
    }

    public void setUserName(String userName){
        currentUser.setUsername(userName);
    }

    public void setImage(ImageIcon imageIcon){
        currentUser.setImage(imageIcon);
    }

    public void connect(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(new Message(currentUser,null,"Connect",currentUser.getImage()));
            oos.writeObject(new Message(currentUser,null,"ActiveUsers",currentUser.getImage()));
            new ReadThread(socket, this).start();
            new WriteThread(socket, this, oos). start();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public ArrayList<Message> getMessageList() {
        return messageList;
    }

    public void disconnect() {
        try {
            oos.writeObject(new Message(currentUser,null,"Exit",null));
            socket.close();
            contacts.writeToFile();
        } catch (IOException e) {
            //System.err.println(e);
        }
    }

    //public void addMessage(Message message) {
      //  messageList.add(message);
        //updateMessageList();
    //}
    public void setMessageListGUI(Message message){
        messageList.add(message);
        ArrayList<String> strMessageList = new ArrayList<>();
        for (int i=0; i<messageList.size(); i++) {
            strMessageList.add(messageList.get(i).getUser().getUserName() + " " + messageList.get(i).getDateReceived().toString());
        }
        gui.setMessageListGUI(strMessageList);
    }

    public void updateMessageList() {
        ArrayList<Message> strMessageList = new ArrayList<>();
        Message message;
        for (int i = 0; i < messageList.size(); i++) {
            //message = messageList.get(i);
            //strMessageList.add(message);
        }
       // gui.updateMessages(strMessageList);
    }

    public void updateOnlineList(ArrayList list) {
        // behöver vet hur message subklassen ser ut
        // skicka till GUI:t


    }
    public void fetchActiveUsers(){
        Message message = new Message(currentUser,null,"ActiveUsers",new ImageIcon());
        change.firePropertyChange("message",null,message);
    }

    public String[] getOnlineList() {
        String[] strOnlineList = new String[onlineList.size()];
        for (int i = 0; i < onlineList.size(); i++) {
            strOnlineList[i] = onlineList.get(i).getUserName();;
            System.out.println(strOnlineList[i]);
        }
        System.out.println("list get");
        return strOnlineList;
    }

    public void sendMessage(String text, ImageIcon image) {
        Message message = new Message(currentUser, receiverList, text, image);
        change.firePropertyChange("message", null, message);
    }

    public boolean addContact(int index) {
        if (index >= 0 && index < onlineList.size()) {
            if (contacts.addContact(onlineList.get(index))) {
                return true;
            }
        }
        return false;
    }

    public boolean removeContact(int index) {
        if (contacts.removeContact(index)) {
            return true;
        }
        return false;
    }


    public String[] getContactList() {
        ArrayList<User> contactList = contacts.getContactList();
        String[] strContactList = new String[contactList.size()];
        for (int i = 0; i < contactList.size(); i++) {
            strContactList[i] = contactList.get(i).getUserName();;
        }
        return strContactList;
    }

    public boolean addReceiver(int index, int list) {
        // kan kanske göras på ett bättre sätt
        // list == 0 från kontakt listan, list == 1 från uppkopplade listan
        User newReceiver;

        if (list == 0) {
            newReceiver = contacts.getContactAt(index);
        } else if (list == 1 ) {
           //hakvar newReceiver = onlineList.get(index);
            newReceiver = onlineList.get(index);
            System.out.println("44");
            System.out.println(newReceiver.getUserName());

        } else {
            return false;
        }

        for (User receiver : receiverList) {
            if (receiver.equals(newReceiver)) {
                return false;
            }
        }
        receiverList.add(newReceiver);

        return true;
    }

    public boolean removeReceiver(int index) {
        if (index >= 0 && index < receiverList.size()) {
            receiverList.remove(index);
            return true;
        }
        return false;
    }

    public ArrayList<String> getReceiverList() {
        ArrayList<String> strReceiverList = new ArrayList<>();
        for (User receiver : receiverList) {
            strReceiverList.add(receiver.getUserName());
        }
        return strReceiverList;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        change.addPropertyChangeListener(listener);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MessageClient messageClient = new MessageClient();

            }
        });
    }
}
    

