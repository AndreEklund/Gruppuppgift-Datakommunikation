package entity;

import javax.swing.*;
import java.io.Serializable;

/**
 * Klass för användare.
 */
public class User implements Serializable {

    private String username;
    private ImageIcon image;
    private static final long serialVersionUID = 2525L;

    /**
     * Returnerar användarnamnet.
     * @return användarnamnet.
     */
    public String getUserName() {
        return username;
    }

    /**
     * Sätter användarnamnet.
     * @param username användarnamnet.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returnerar användarens profilbild.
     * @return användarens profilbild.
     */
    public ImageIcon getImage() {
        return image;
    }

    /**
     * Sätter användarens profilbild.
     * @param image användarens profilbild.
     */
    public void setImage(ImageIcon image) {
        this.image = image;
    }

    /**
     * Returnerar hashkoden.
     * @return hashkoden.
     */
    public int hashCode() {
        return username.hashCode();
    }

    /**
     * Kollar om två meddelanden är lika.
     * @param obj objektet att jämföra.
     * @return true om det är samma objekt, falskt annars.
     */
    public boolean equals(Object obj) {
        if(obj!=null && obj instanceof User)
            return username.equals(((User)obj).getUserName());
        return false;
    }
}