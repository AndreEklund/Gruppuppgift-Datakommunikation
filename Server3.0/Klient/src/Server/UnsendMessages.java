package Server;

import entity.Message;
import entity.User;

import java.util.ArrayList;
import java.util.HashMap;

public class UnsendMessages {
    private HashMap<User, ArrayList<Message>> unsend = new HashMap<>();
    //private ArrayList <Message> arrayList;
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
        // hämta ArrayList –om null skapa en och placera i unsend// lägga till Message i ArrayList}
    }
    public synchronized ArrayList<Message> getArrayList(User user) {
        return unsend.get(user);
    }
    public synchronized void remove(User user) {
        unsend.remove(user);
    }
    // fler synchronized-metoder som behövs}
}
