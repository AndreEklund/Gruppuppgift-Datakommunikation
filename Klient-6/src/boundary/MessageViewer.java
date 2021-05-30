package boundary;

import javax.swing.*;
import java.awt.*;

public class MessageViewer extends JPanel {
    public MessageViewer(String text, ImageIcon image) {
        JFrame f = new JFrame();
        JPanel p = new JPanel(new BorderLayout());
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        p.setVisible(false);

        f.add(p);
        f.setTitle("Message");

        JTextArea textArea = new JTextArea();
        textArea.setText(text);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setOpaque(false);
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setBackground(UIManager.getColor("Label.background"));
        textArea.setFont(UIManager.getFont("Label.font"));
        textArea.setBorder(UIManager.getBorder("Label.border"));

        JLabel lblIcon = new JLabel(" ",JLabel.CENTER);
        lblIcon.setOpaque(true);
        lblIcon.setIcon(image);
        f.add(textArea,BorderLayout.NORTH);
        f.add(lblIcon);


        f.setSize(600, 400);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}

