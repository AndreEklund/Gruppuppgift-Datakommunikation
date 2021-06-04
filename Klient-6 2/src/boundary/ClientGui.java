package boundary;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import control.*;

public class ClientGui extends JPanel {
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

    public ClientGui(MessageClient client) {

        this.client = client;
        setLayout(new BorderLayout());
        receivers = new JList();
        add(centerPanel(),BorderLayout.CENTER);
        add(messagePanel(),BorderLayout.WEST);
        add(receiverPanel(),BorderLayout.EAST);

        initListeners();

    }

    public ClientGui(){

    }

    private JPanel messagePanel(){
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Messages"));
        panel.setPreferredSize(new Dimension(300,400));
        panel.add(messages,BorderLayout.CENTER);
        panel.add(btnView,BorderLayout.SOUTH);
        return panel;
    }

    private JPanel receiverPanel(){
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Receivers"));
        panel.setPreferredSize(new Dimension(300,400));
        panel.add(receivers,BorderLayout.CENTER);
        panel.add(eastButtons(),BorderLayout.SOUTH);
        return panel;
    }
    private JPanel eastButtons(){
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(btnContacts,BorderLayout.WEST);
        panel.add(btnOnlineUsers,BorderLayout.CENTER);
        panel.add(btnRemove,BorderLayout.EAST);
        return panel;
    }

    private JPanel centerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(sendMessagePanel(),BorderLayout.NORTH);
        panel.add(imagePanel(),BorderLayout.CENTER);
        panel.add(connectPanel(),BorderLayout.SOUTH);
        return panel;
    }

    private JPanel sendMessagePanel(){
        JPanel panel = new JPanel(new GridLayout(5,1));
        panel.add(new JLabel("Text:"));
        panel.add(messageText);
        panel.add(btnImage);
        panel.add(btnSend);
        return panel;
    }

    private JPanel imagePanel(){
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(imageLabel);

        return panel;
    }

    public void updateReceivers(ArrayList list){
        receivers.setListData(list.toArray());

    }
    public void setMessageListGUI(ArrayList list){

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (int i=0; i<list.size(); i++){
                    messageListGUI.addElement(list.get(i));
                }
            }
        });

    }

    public void setMessage(String text, ImageIcon icon){
        MessageViewer viewer = new MessageViewer(text,icon);
    }


    private JPanel connectPanel() {
        JPanel panel = new JPanel(new GridLayout(4,1));
        panel.add(btnConnect);
        panel.add(btnDisconnect);
        return panel;
    }

    public boolean inputUserName(){
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
    }


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
                if(image == null){
                    ImageIcon imageIcon= new ImageIcon("images/IngenBild.jpg");
                    client.sendMessage(messageText.getText(), imageIcon);
                } else if (messageText.getText() == null){
                    client.sendMessage("Ingen text i meddelandet", image);
                } else{
                    client.sendMessage(messageText.getText(), image);
                }
            } else if(e.getSource()==btnOnlineUsers) {
                client.fetchActiveUsers();
                onlinePanel = new OnlineUsers(client, client.getOnlineList(), ClientGui.this);
            } else if(e.getSource()==btnContacts) {
                contactsPanel = new ContactsPopup(client, client.getContactList(), ClientGui.this);
            } else if(e.getSource()==btnRemove){
                client.removeReceiver(receivers.getSelectedIndex());
                updateReceivers(client.getReceiverList());
            } else if(e.getSource()==btnView){
                if(messages.getSelectedIndex()>=0){
                    client.viewMessage(messages.getSelectedIndex());
                }
            }
        }
    }

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

