package entity;

import java.io.*;
import java.util.ArrayList;

/**
 * Hanterar kontaktlistan.
 */
public class Contacts {
    private ArrayList<User> contactList;
    private String filename;

    /**
     * Konstruktor.
     * Läser in filen med sparade kontakter från hårddisken.
     * @param filename namnet på filen som ska läsas.
     */
    public Contacts(String filename) {
        this.filename = filename;
        contactList = new ArrayList<>();

        if (new File(filename).exists()) {
            readFile();
        }
    }

    /**
     * Returnerar kontaktlistan.
     * @return kontaktlistan.
     */
    public ArrayList<User> getContactList() {
        return contactList;
    }

    /**
     * Returnerar kontakten i en specifierad position i listan.
     * @param index positionen av kontakten.
     * @return kontakten.
     */
    public User getContactAt(int index) {
        User contact = null;
        if (index >= 0 && index < contactList.size()) {
            contact = contactList.get(index);
        }
        return contact;
    }

    /**
     * Lägger till en ny kontakt i listan.
     * @param newContact kontakten att lägga till.
     * @return true om det gick att lägga till kontakten, false annars.
     */
    public boolean addContact(User newContact) {
        for (User contact : contactList) {
            if (contact.equals(newContact)) {
                return false;
            }
        }
        contactList.add(newContact);
        return true;
    }

    /**
     * Tar bort en kontakt ur listan.
     * @param index positionen av kontakten.
     * @return true om det gick att ta bort kontakten, false annars.
     */
    public boolean removeContact(int index) {
        if (index >= 0 && index < contactList.size()) {
            contactList.remove(index);
            return true;
        }
        return false;
    }

    /**
     * Läser filen med sparade kontakter från hårddisken.
     */
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

    /**
     * Skriver kontaktlistan till filen på hårddisken.
     */
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
