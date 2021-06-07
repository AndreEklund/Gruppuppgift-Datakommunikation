package entity;

import java.util.ArrayList;

/**
 * En message subklass som innehåller listan på aktiva användare.
 */
public class OnlineListMessage extends Message{
    ArrayList<User> onlineList;

    /**
     * Konstruktor
     * @param onlineList onlinelistan som skickas till klienten
     */
    public OnlineListMessage(ArrayList<User> onlineList) {
        super(null, null, null, null);
        this.onlineList = onlineList;
    }

    /**
     * Returnerar listan på aktiva användare.
     * @return listan på aktiva användare.
     */
    public ArrayList<User> getOnlineList() {
        return onlineList;
    }
}