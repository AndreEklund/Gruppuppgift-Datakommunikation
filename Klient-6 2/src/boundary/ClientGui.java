package boundary;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import control.*;

/**
 * Huvudfönster för GUI:t.
 */
public class ClientGui extends JFrame {
    private MessageClient client;
    private JTextField messageText = new JTextField();
    private JTextField tfAge = new JTextField();
    private JButton btnView = new JButton("View Message");
    private JButton btnImage = new JButton("Choose Image");
    private JButton btnSend = new JButton("Send Message");
    private JButton btnContacts = new JButton("Contacts");
    private JButton btnOnlineUsers = new JButton("Online Users");
    private JButton btnConnect = new JButton("Connect");
    private JButton btnDisconnect = new JButton("Disconnect");
    private JButton btnRemove = new JButton("Remove");
    private JList receivers;

    private JLabel imageLabel = new JLabel(" ", JLabel.CENTER);
    private DefaultListModel messageListGUI = new DefaultListModel();
    private JList<String> messages = new JList<>(messageListGUI);
    private ImageIcon image;
    private OnlineUsers onlinePanel;
    private ContactsPopup contactsPanel;
    //private String ip = "127.0.0.1";
    private String ip = "127.0.01";
    private int port = 434;

    /**
     * Konstruktor.
     * Skapar alla paneler och lägger till listeners.
     * @param client kontroller för klienten.
     */
    public ClientGui(MessageClient client) {
        setTitle("Client UI");
        setLocation(200, 150);
        setVisible(true);

        this.client = client;

        addPanels();
        initListeners();
    }

    /**
     * Lägger till panelerna.
     */
    private void addPanels() {
        JPanel panel = new JPanel(new BorderLayout());
        receivers = new JList();
        panel.add(centerPanel(),BorderLayout.CENTER);
        panel.add(messagePanel(),BorderLayout.WEST);
        panel.add(receiverPanel(),BorderLayout.EAST);

        add(panel);

        pack();
    }

    /**
     * Panelen i center.
     * @return panelen.
     */
    private JPanel centerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(sendMessagePanel(),BorderLayout.NORTH);
        panel.add(imagePanel(),BorderLayout.CENTER);
        panel.add(connectPanel(),BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Panelen med knapparna för meddelanden.
     * @return panelen.
     */
    private JPanel sendMessagePanel() {
        JPanel panel = new JPanel(new GridLayout(5,1));
        panel.add(new JLabel("Text:"));
        panel.add(messageText);
        panel.add(btnImage);
        panel.add(btnSend);
        return panel;
    }

    /**
     * Panelen för bilden.
     * @return panelen.
     */
    private JPanel imagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(imageLabel);
        return panel;
    }

    /**
     * Panelen med knapparna om anslutning.
     * @return panelen.
     */
    private JPanel connectPanel() {
        JPanel panel = new JPanel(new GridLayout(4,1));
        panel.add(btnConnect);
        panel.add(btnDisconnect);
        return panel;
    }

    /**
     * Panelen för meddelanden.
     * @return panelen.
     */
    private JPanel messagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Messages"));
        panel.setPreferredSize(new Dimension(300,400));
        panel.add(messages,BorderLayout.CENTER);
        panel.add(btnView,BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Panelen för mottagare,
     * @return panelen.
     */
    private JPanel receiverPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Receivers"));
        panel.setPreferredSize(new Dimension(300,400));
        panel.add(receivers,BorderLayout.CENTER);
        panel.add(eastButtons(),BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Panelen för knapparna för listorna.
     * @return panelen.
     */
    private JPanel eastButtons() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(btnContacts,BorderLayout.WEST);
        panel.add(btnOnlineUsers,BorderLayout.CENTER);
        panel.add(btnRemove,BorderLayout.EAST);
        return panel;
    }

    /**
     * Uppdaterar listan för mottagare.
     * @param list listan för mottagare.
     */
    public void updateReceivers(ArrayList list) {
        receivers.setListData(list.toArray());
    }

    /**
     * Sätter listan för meddelanden.
     * @param list listan för meddelanden.
     */
    public void setMessageListGUI(ArrayList list) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (int i=0; i<list.size(); i++) {
                    messageListGUI.addElement(list.get(i));
                }
            }
        });
    }

    /**
     * Visar ett meddelande.
     * @param text texten att visa.
     * @param icon bilden att visa.
     */
    public void setMessage(String text, ImageIcon icon){
        MessageViewer viewer = new MessageViewer(text,icon);
    }

    /**
     * Läser in användarens namn.
     * @return true om det var ett giltigt namn, falskt annars.
     */
    public boolean inputUserName() {
        String name = JOptionPane.showInputDialog("Username: ");
        JFileChooser fc = new JFileChooser();

        fc.addChoosableFileFilter(new ImageFilter());
        fc.setAcceptAllFileFilterUsed(false);

        int result = fc.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION){
            File file = fc.getSelectedFile();
            String sname = file.getPath();
            ImageIcon imageIcon = new ImageIcon(sname);
            if (name != null) {
                client.setUserName(name);
                client.setImage(imageIcon);
                return true;
            }
        }
        return false;
    }

    /**
     * Lägger till listeners till knapparna och fönstret.
     */
    private void initListeners() {
        ActionListener listener = new ButtonListener();
        btnSend.addActionListener(listener);
        btnView.addActionListener(listener);
        btnImage.addActionListener(listener);
        btnConnect.addActionListener(listener);
        btnDisconnect.addActionListener(listener);
        btnOnlineUsers.addActionListener(listener);
        btnContacts.addActionListener(listener);
        btnRemove.addActionListener(listener);

        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                client.disconnect();
                System.exit(0);
            }
        });
    }

    /**
     * Hanterar vilken knapp som anropar vad.
     */
    private class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if(e.getSource()==btnConnect) {
                inputUserName();
                client.connect(ip, port);
            } else if(e.getSource()==btnDisconnect) {
                client.disconnect();
            } else if(e.getSource()==btnImage) {
                JFileChooser imageChooser = new JFileChooser();

                imageChooser.addChoosableFileFilter(new ImageFilter());
                imageChooser.setAcceptAllFileFilterUsed(false);

                int result = imageChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION){
                    File file = imageChooser.getSelectedFile();
                    String sname = file.getPath();
                    ImageIcon imageIcon = new ImageIcon(sname);
                    image = imageIcon;
                    ImageIcon temp = new ImageIcon(new ImageIcon(sname).getImage().getScaledInstance(imageLabel.getWidth(),
                            imageLabel.getWidth()*image.getIconHeight()/image.getIconWidth(),Image.SCALE_SMOOTH));
                    imageLabel.setOpaque(true);
                    imageLabel.setIcon(temp);
                }
            } else if(e.getSource()==btnSend) {
                if(image == null) {
                    ImageIcon imageIcon= new ImageIcon("images/IngenBild.jpg");
                    client.sendMessage(messageText.getText(), imageIcon);
                } else if (messageText.getText() == null) {
                    client.sendMessage("Ingen text i meddelandet", image);
                } else {
                    client.sendMessage(messageText.getText(), image);
                }
            } else if(e.getSource()==btnOnlineUsers) {
                client.fetchActiveUsers();
                onlinePanel = new OnlineUsers(client, client.getOnlineList(), ClientGui.this);
            } else if(e.getSource()==btnContacts) {
                contactsPanel = new ContactsPopup(client, client.getContactList(), ClientGui.this);
            } else if(e.getSource()==btnRemove) {
                client.removeReceiver(receivers.getSelectedIndex());
                updateReceivers(client.getReceiverList());
            } else if(e.getSource()==btnView) {
                if(messages.getSelectedIndex()>=0) {
                    client.viewMessage(messages.getSelectedIndex());
                }
            }
        }
    }

    /**
     * Gör så att man kan bara välja bilder i fileChooser.
     */
    class ImageFilter extends FileFilter {
        public final static String JPEG = "jpeg";
        public final static String JPG = "jpg";
        public final static String GIF = "gif";
        public final static String TIFF = "tiff";
        public final static String TIF = "tif";
        public final static String PNG = "png";

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            String extension = getExtension(f);
            if (extension != null) {
                if (extension.equals(TIFF) ||
                        extension.equals(TIF) ||
                        extension.equals(GIF) ||
                        extension.equals(JPEG) ||
                        extension.equals(JPG) ||
                        extension.equals(PNG)) {
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }

        @Override
        public String getDescription() {
            return "Image Only";
        }

        String getExtension(File f) {
            String ext = null;
            String s = f.getName();
            int i = s.lastIndexOf('.');

            if (i > 0 &&  i < s.length() - 1) {
                ext = s.substring(i+1).toLowerCase();
            }
            return ext;
        }
    }
}

