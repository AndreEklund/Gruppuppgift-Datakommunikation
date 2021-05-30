package entity;

import java.io.*;
import java.util.ArrayList;

public class Contacts {
    private ArrayList<User> contactList;
    private String filename;

    public Contacts(String filename) {
        this.filename = filename;
        contactList = new ArrayList<>();

        if (new File(filename).exists()) {
            readFile();
        }
    }

    public ArrayList<User> getContactList() {
        return contactList;
    }

    public User getContactAt(int index) {
        User contact = null;
        if (index >= 0 && index < contactList.size()) {
            contact = contactList.get(index);
        }
        return contact;
    }

    public boolean addContact(User newContact) {
        for (User contact : contactList) {
            if (contact.equals(newContact)) {
                return false;
            }
        }
        contactList.add(newContact);
        return true;
    }

    public boolean removeContact(int index) {
        if (index >= 0 && index < contactList.size()) {
            contactList.remove(index);
            return true;
        }
        return false;
    }

    private void readFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename))) ) {
            int size = ois.readInt();
            for (int i = 0; i < size; i++) {
                contactList.add((User) ois.readObject());
            }
        }catch (FileNotFoundException | UnsupportedEncodingException e){
            System.out.println(e);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void writeToFile() {
        try ( ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename))) ) {
            oos.writeInt(contactList.size());
            for (User contact : contactList) {
                oos.writeObject(contact);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
