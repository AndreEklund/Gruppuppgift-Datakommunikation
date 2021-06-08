package control;

import boundary.ClientGui;
import entity.*;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.*;
import java.io.*;
import java.util.ArrayList;

/**
 * Controller klass för klienten.
 */
public class MessageClient {
    private Contacts contacts;
    private User currentUser;
    private ArrayList<Message> messageList;
    private ArrayList<User> onlineList;
    private ArrayList<User> receiverList;
    private Socket socket;
    private ClientGui gui;

    private final PropertyChangeSupport change = new PropertyChangeSupport(this);

    /**
     * Konstruktor för klienten.
     */
    public MessageClient() {
        contacts = new Contacts("files/contacts.dat");
        messageList = new ArrayList<>();
        onlineList = new ArrayList<>();
        receiverList = new ArrayList<>();
        currentUser = new User();

        gui = new ClientGui(this);
    }

    /**
     * Hämtar meddelandet från det angivna index och visar det i GUI:t.
     * @param index positionen av meddelandet i listan.
     */
    public void viewMessage(int index) {
        if (index >= 0 && index < messageList.size()) {
            Message message = messageList.get(index);
            gui.setMessage(message.getText(), message.getIcon());
        }
    }

    /**
     * Sätter listan av online användare.
     * @param onlineList listan av online användare.
     */
    public void setOnlineList(ArrayList<User> onlineList) {
        this.onlineList = onlineList;
        onlineList.remove(currentUser);
        System.out.println("onlinelist set");
    }

    /**
     * Sätter användarnamnet på användaren.
     * @param userName användarnamnet.
     */
    public void setUserName(String userName){
        currentUser.setUsername(userName);
    }

    /**
     * Sätter profilbilden på användaren.
     * @param imageIcon profilbilden.
     */
    public void setImage(ImageIcon imageIcon){
        currentUser.setImage(imageIcon);
    }

    /**
     * Ansluter till servern. Startar trådarna som skriver till och läser meddelanden från servern.
     * @param ip ip-adressen att ansluta till.
     * @param port porten att ansluta till.
     */
    public void connect(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            new WriteThread(socket, this).start();
            new ReadThread(socket, this).start();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    /**
     * Bryter anslutningen till servern. Skriver kontaktlistan till en fil på hårddisken.
     */
    public void disconnect() {
        try {
            if (socket != null && socket.isConnected()) {
                socket.close();
                onlineList.clear();
                receiverList.clear();
                gui.updateReceivers(receiverList);
                contacts.writeToFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returnerar användaren.
     * @return användaren.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Lägger till ett meddelande till listan och skickar listan till GUI:t.
     * @param message det nya meddelandet.
     */
    public void setMessageListGUI(Message message){
        messageList.add(message);
        ArrayList<String> strMessageList = new ArrayList<>();
        for (int i=0; i<messageList.size(); i++) {
            strMessageList.add(messageList.get(i).getUser().getUserName() + " " + messageList.get(i).getDateReceived().toString());
        }
        gui.setMessageListGUI(strMessageList);
    }

    /**
     * Returnerar listan av online användare.
     * @return listan av online användare.
     */
    public String[] getOnlineList() {
        String[] strOnlineList = new String[onlineList.size()];
        for (int i = 0; i < onlineList.size(); i++) {
            strOnlineList[i] = onlineList.get(i).getUserName();;
            System.out.println(strOnlineList[i]);
        }
        System.out.println("list get");
        return strOnlineList;
    }

    /**
     * Skickar ett meddelande till servern.
     * @param text Texten i meddelandet.
     * @param image Bilden i meddelandet.
     */
    public void sendMessage(String text, ImageIcon image) {
        Message message = new Message(currentUser, receiverList, text, image);
        change.firePropertyChange("message", null, message);
    }

    /**
     * Lägger till den valda användaren till kontaktlistan.
     * @param index användarens position i onlinelistan.
     * @return true om det gick att lägga till användaren, falskt annars.
     */
    public boolean addContact(int index) {
        if (index >= 0 && index < onlineList.size()) {
            if (contacts.addContact(onlineList.get(index))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returnerar kontaktlistan.
     * @return kontaktlistan.
     */
    public String[] getContactList() {
        ArrayList<User> contactList = contacts.getContactList();
        String[] strContactList = new String[contactList.size()];
        for (int i = 0; i < contactList.size(); i++) {
            strContactList[i] = contactList.get(i).getUserName();;
        }
        return strContactList;
    }

    /**
     * Lägger till en användare till mottagarlistan.
     * @param index positionen att hämta mottagaren från
     * @param list vilken lista att hämta mottagaren från. 0 från kontaktlistan, 1 från online listan
     * @return true om det gick att lägga till mottagaren, falskt annars.
     */
    public boolean addReceiver(int index, int list) {
        User newReceiver;

        if (list == 0) { // från kontaktlistan
            newReceiver = contacts.getContactAt(index);
        } else if (list == 1 ) { // från online listan
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

    /**
     * Tar bort en mottagare ur listan.
     * @param index mottagarens position i listan.
     * @return true om det gick att ta bort mottagaren, falskt annars.
     */
    public boolean removeReceiver(int index) {
        if (index >= 0 && index < receiverList.size()) {
            receiverList.remove(index);
            return true;
        }
        return false;
    }

    /**
     * Returnerar mottagarlistan.
     * @return mottagarlistan.
     */
    public ArrayList<String> getReceiverList() {
        ArrayList<String> strReceiverList = new ArrayList<>();
        for (User receiver : receiverList) {
            strReceiverList.add(receiver.getUserName());
        }
        return strReceiverList;
    }

    /**
     * Lägger till en listener för propertyChange.
     * @param listener listener att lägga till.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        change.addPropertyChangeListener(listener);
    }

    /**
     * Startar applikationen.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MessageClient messageClient = new MessageClient();

            }
        });
    }
}
    

