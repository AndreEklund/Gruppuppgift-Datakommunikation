package entity;

import javax.swing.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Klass för meddelanden.
 */
public class Message implements Serializable {
    private User user;
    private ArrayList<User> receiverList;
    private String text;
    private ImageIcon icon;
    private LocalDateTime dateSent;
    private LocalDateTime dateReceived;
    private static final long serialVersionUID = 2626L;

    /**
     * Konstruktor.
     * @param user användaren som skickar meddelandet.
     * @param receiverList listan av användare som ska ta emot meddelandet.
     * @param text texten i meddelandet.
     * @param icon bilden i meddelandet.
     */
    public Message(User user, ArrayList<User> receiverList, String text, ImageIcon icon) {
        this.user = user;
        this.receiverList = receiverList;
        this.text = text;
        this.icon = icon;
    }

    /**
     * Returnerar användaren.
     * @return användaren
     */
    public User getUser() {
        return user;
    }

    /**
     * Returnerar mottagarlistan.
     * @return mottagarlistan.
     */
    public ArrayList<User> getReceiverList() {
        return receiverList;
    }

    /**
     * Returnerar texten.
     * @return texten.
     */
    public String getText() {
        return text;
    }

    /**
     * Returnerar bilden.
     * @return bilden.
     */
    public ImageIcon getIcon() {
        return icon;
    }

    /**
     * Returnerar datumet som meddelandet skickades.
     * @return datumet som meddelandet skickades.
     */
    public LocalDateTime getDateSent() {
        return dateSent;
    }

    /**
     * Sätter datumet som meddelandet skickades.
     * @param dateSent datumet som meddelandet skickades.
     */
    public void setDateSent(LocalDateTime dateSent) {
        this.dateSent = dateSent;
    }

    /**
     * Returnerar datumet som meddelandet togs emot.
     * @return datumet som meddelandet togs emot.
     */
    public LocalDateTime getDateReceived() {
        return dateReceived;
    }

    /**
     * Returnerar datumet som meddelandet togs emot.
     * @param dateReceived datumet som meddelandet togs emot.
     */
    public void setDateReceived(LocalDateTime dateReceived) {
        this.dateReceived = dateReceived;
    }
}
