package boundary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import control.MessageClient;

/**
 * Visar listan av aktiva användare.
 */
public class OnlineUsers extends JPanel {
    private MessageClient client;
    private JList<String> list;
    private JScrollPane s;
    private JButton btnAddToReceivers = new JButton("Add to receivers");
    private JButton btnAddToContacts = new JButton("Add to contacts");
    private ClientGui gui;

    /**
     * Konstruktor.
     * @param client kontroller för klienten.
     * @param str listan av aktiva användare.
     * @param gui huvudfönstret.
     */
    public OnlineUsers(MessageClient client, String[] str, ClientGui gui) {
        this.client = client;
        this.gui = gui;
        JFrame f = new JFrame();
        JPanel p = new JPanel(new BorderLayout());
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        p.setVisible(false);

        f.add(p);
        f.setTitle("Online users");

        list = new JList(str);

        Font font = new Font("Courier New", Font.PLAIN, 14);
        list.setFont(font);

        s = new JScrollPane(list);

        s.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        s.setSize(800, 800);

        int buttonHeight = 25;  //4 = margin
        int buttonWidth = 165;
        btnAddToReceivers = new JButton("Add to receivers");
        btnAddToContacts = new JButton("Add to contacts");
       // Dimension dim = new Dimension(buttonWidth, buttonHeight);
       // btnAddToReceivers.setSize(dim);
        btnAddToReceivers.setBounds(75, 325, 165, 25);
        btnAddToContacts.setBounds(300,325,165,25);

        f.add(btnAddToReceivers);
        f.add(btnAddToContacts);

        list.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        f.add(s);

        BorderLayout layout = new BorderLayout();
        setLayout(layout);


        f.setSize(600, 400);
        f.setLocationRelativeTo(null);
        f.setVisible(true);

        initListeners();
    }

    /**
     * Returnerar vilket index som är valt.
     * @return det valda index.
     */
    public int getIndex() {
        return list.getSelectedIndex();
    }

    /**
     * Lägger till listeners.
     */
    private void initListeners() {
        ActionListener listener = new ButtonListener();
        btnAddToReceivers.addActionListener(listener);
        btnAddToContacts.addActionListener(listener);
    }

    /**
     * Hanterar vilken knapp som anropar vad.
     */
    class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == btnAddToReceivers) {
                client.addReceiver(getIndex(), 1);
                ArrayList list  = client.getReceiverList();
               // System.out.println(list.size());
                gui.updateReceivers(list);
            } else if (e.getSource() == btnAddToContacts) {
                client.addContact(getIndex());
            }
        }
    }
}