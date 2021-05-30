package boundary;

import java.util.ArrayList;
import java.util.Scanner;

public class ServerUI {

    private Scanner userInput;

    public ServerUI() {
        userInput = new Scanner(System.in);
    }

    public void printMessage(String text) {
        System.out.println(text);
    }

    public void printList(ArrayList<String> list) {
        for (String s : list) {
            System.out.println(s);
        }
    }

    public String readText() {
        return userInput.nextLine();
    }



}


