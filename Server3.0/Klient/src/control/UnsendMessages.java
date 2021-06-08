package control;

import entity.Message;
import entity.User;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Klass som lagrar meddelanden som skickats till
 * ej aktiva användare i en map
 */

public class UnsendMessages {

    private HashMap<User, ArrayList<Message>> unsend = new HashMap<>();

    /**
     * @param user tar in avsändaren och sparar i mapen
     * @param message tar in meddelanden och sparar dessa i en lista
     * User och tillhörande lista sparas sedan tillsammans i mapen
     * där user blir key
     */
    public synchronized void put(User user,Message message) {
        ArrayList <Message> list = getArrayList(user);
        if (list == null) {
            list = new ArrayList<>();
            list.add(message);
            unsend.put(user, list);
        }
        else {
            list.add(message);
            unsend.put(user, list);
        }
    }
    /**
     * getter för listan
     * @param user tar in ett user objekt och kollar ifall
     * den matchar värdet i mapen vid uppkoppling
     * @return
     */
    public synchronized ArrayList<Message> getArrayList(User user) {
        return unsend.get(user);
    }
    /**
     * @param user tar in ett user-objekt och tar bort meddelanden ur
     * mappen efter att de skickats
     */
    public synchronized void remove(User user) {
        unsend.remove(user);
    }
}
