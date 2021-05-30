package control;

import entity.*;

import javax.swing.*;
import java.util.ArrayList;

public class TestContacts {


    public static void main(String[] args) {
        Contacts contacts = new Contacts("files/contacts.dat");

        contacts.addContact(new User("Guy", new ImageIcon("images/gubbe.jpg")));
        contacts.addContact(new User("Man", new ImageIcon("images/gubbe.jpg")));

        //contacts.removeContact(0);

        ArrayList<User> contactList = contacts.getContactList();
        String str;
        for (User contact : contactList) {
            str = contact.getUserName();
            System.out.println(str);
        }

        contacts.writeToFile();
    }
}
