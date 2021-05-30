package entity;

import javax.swing.*;
import java.io.Serializable;

public class User implements Serializable {

    private String username;
    private ImageIcon image;
    private static final long serialVersionUID = 2525L;

    public User(String username, ImageIcon image){
        this.username = username;
        this.image = image;
    }

    public String getUserName() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ImageIcon getImage() {
        return image;
    }

    public void setImage(ImageIcon image) {
        this.image = image;
    }

    public int hashCode() {
        return username.hashCode();
    }

    public boolean equals(Object obj) {
        if(obj!=null && obj instanceof User)
            return username.equals(((User)obj).getUserName());
        return false;
    }
}