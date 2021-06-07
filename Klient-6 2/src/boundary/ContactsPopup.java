package boundary;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import control.MessageClient;

/**
 * Visar listan av kontakter.
 */
public class ContactsPopup extends JPanel {
    private MessageClient client;
    private JList<String> list;
    private JScrollPane s;
    private JButton btnAdd;
    private ClientGui gui;

    /**
     * Konstruktor.
     * @param client kontroller för klienten.
     * @param str listan av kontakter.
     * @param gui huvudfönstret.
     */
    public ContactsPopup(MessageClient client, String[] str, ClientGui gui) {
        this.client = client;
        this.gui = gui;
        JFrame f = new JFrame();
        JPanel p = new JPanel();
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        p.setVisible(false);

        f.add(p);
        f.setTitle("Contacts");

        list = new JList(str);

        Font font = new Font("Courier New", Font.PLAIN, 14);
        list.setFont(font);

        s = new JScrollPane(list);
        //extra saker
        s.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        s.setSize(800, 800);

        //pnlButtons.setBorder(BorderFactory.createTitledBorder(""));
        int buttonHeight = 25;  //4 = margin
        int buttonWidth = 165;
        btnAdd = new JButton("Add to receivers");
        // Dimension dim = new Dimension(buttonWidth, buttonHeight);
        // btnAdd.setSize(dim);
        btnAdd.setBounds(250, 325, 65, 25);

        f.add(btnAdd);

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
        System.out.println(list.getSelectedIndex());
        return list.getSelectedIndex();
    }

    /**
     * Lägger till listeners.
     */
    private void initListeners() {
        ActionListener listener = new ButtonListeners();
        btnAdd.addActionListener(listener);
    }

    /**
     * Hanterar vilken knapp som anropar vad.
     */
    class ButtonListeners implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == btnAdd) {
                client.addReceiver(getIndex(), 0);
                ArrayList list  = client.getReceiverList();
                System.out.println(list.size());
                gui.updateReceivers(list);
            }
        }
    }
}