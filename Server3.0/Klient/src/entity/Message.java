package entity;

import javax.swing.Icon;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Message implements Serializable {
    private User user;
    private ArrayList<User> receiverList;
    private String text;
    private Icon icon;
    private LocalDateTime dateSent;
    private LocalDateTime dateReceived;
    private static final long serialVersionUID = 2626L;

    public Message(User user, ArrayList<User> receiverList, String text, Icon icon) {
        this.user = user;
        this.receiverList = receiverList;
        this.text = text;
        this.icon = icon;
    }

    public User getUser() {
        return user;
    }

    public ArrayList<User> getReceiverList() {
        return receiverList;
    }

    public String getText() {
        return text;
    }

    public Icon getIcon() {
        return icon;
    }

    public LocalDateTime getDateSent() {
        return dateSent;
    }

    public void setDateSent(LocalDateTime dateSent) {
        this.dateSent = dateSent;
    }

    public LocalDateTime getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(LocalDateTime dateReceived) {
        this.dateReceived = dateReceived;
    }

}
