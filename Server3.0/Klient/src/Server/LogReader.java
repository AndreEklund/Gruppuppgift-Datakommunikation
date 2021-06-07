package Server;

import boundary.ServerUI;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Klass som loggar all trafik och händelser
 */
public class LogReader extends Thread {
    private String filename;
    private ServerUI ui;
    private ArrayList<String> logList;

    /**
     * @param filename loggfil som servern skriver till
     */
    public LogReader(String filename) {
        this.filename = filename;
        ui = new ServerUI();
    }

    /**
     * run-metod som tillåter att kontrollera loghistorik
     * i konsolen givet ett visst intervall
     */
    @Override
    public void run() {
        LocalDateTime date1;
        LocalDateTime date2;
        String text;
        while (true) {
            ui.printMessage("\nChoose the time range for the log check:");
            ui.printMessage("Input first timepoint: (YYYY-MM-DD-hh-mm-ss)");
            text = ui.readText();
            date1 = readDateEntered(text);
            if (date1 != null) {
                ui.printMessage("Input second timepoint: (YYYY-MM-DD-hh-mm-ss)");
                text = ui.readText();
                date2 = readDateEntered(text);
                if (date2 != null) {
                    if (date2.isAfter(date1)) {
                        readLogFile(date1, date2);
                    } else {
                        ui.printMessage("Second timepoint needs to be after the first!");
                    }
                } else {
                    ui.printMessage("Invalid input!");
                }
            } else {
                ui.printMessage("Invalid input!");
            }
        }
    }

    /**
     * metoden som körs i run metoden med de två
     * datumen som utgör intervallet som parametrar
     * @param date1 första datumet
     * @param date2 andra datumet
     */
    public void readLogFile(LocalDateTime date1, LocalDateTime date2) {
        try{
            FileInputStream fis = new FileInputStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String strLine;
            String[] strLineParts;
            LocalDateTime dateLog;
            logList = new ArrayList<>();
            logList.add("Events between " + date1.toString() + " and " + date2.toString() + " :");
            while ((strLine = br.readLine()) != null)   {
                strLineParts = strLine.split("\\|");
                dateLog = readDateLog(strLineParts[0], strLineParts[1]);
                if (dateLogIsAfter(date2, dateLog)) {
                    break;
                } else if (dateLogIsWithin(date1, date2)) {
                    logList.add(strLineParts[0] + " " + strLineParts[1] + " : " + strLineParts[2]);
                }
            }
            fis.close();
            ui.printList(logList);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * returnerar en boolean och kollar
     * om loggen är inom det angivna intervallet
     * @param date1
     * @param dateLog
     * @return
     */
    public boolean dateLogIsWithin(LocalDateTime date1, LocalDateTime dateLog) {
        return dateLog.isEqual(date1) || dateLog.isAfter(date1);
    }

    /**
     * -||- - efter det angivna intervallet
     * @param date2
     * @param dateLog
     * @return
     */
    public boolean dateLogIsAfter(LocalDateTime date2, LocalDateTime dateLog) {
        return dateLog.isAfter(date2);
    }

    /**
     * läser det angivna datumet och returner ett
     * localdatetime objekt
     * @param dateTimeText
     * @return
     */
    public LocalDateTime readDateEntered(String dateTimeText) {
        LocalDateTime date = null;
        int[] intArray = strToIntArrayEntered(dateTimeText);
        if (intArray != null) {
            try {
                date = LocalDateTime.of(intArray[0], intArray[1], intArray[2], intArray[3], intArray[4], intArray[5]);
            } catch (DateTimeException e) { }
        }
        return date;
    }

    public int[] strToIntArrayEntered(String dateTimeText) {
        int[] intArray = null;
        try {
            intArray = new int[6];
            String[] parts = dateTimeText.split("-");
            intArray[0] = Integer.parseInt(parts[0]);
            intArray[1] = Integer.parseInt(parts[1]);
            intArray[2] = Integer.parseInt(parts[2]);
            intArray[3] = Integer.parseInt(parts[3]);
            intArray[4] = Integer.parseInt(parts[4]);
            intArray[5] = Integer.parseInt(parts[5]);
        } catch (Exception e) {}
        return intArray;
    }

    public LocalDateTime readDateLog(String dateText, String timeText) {
        LocalDateTime date = null;
        int[] intArray = strToIntArrayLog(dateText, timeText);
        if (intArray != null) {
            try {
                date = LocalDateTime.of(intArray[0], intArray[1], intArray[2], intArray[3], intArray[4], intArray[5]);
            } catch (DateTimeException e) {}
        }
        return date;
    }

    public int[] strToIntArrayLog(String dateText, String timeText) {
        int[] intArray = new int[6];
        try {
            String[] dateParts = dateText.split("-");
            String[] timeParts = timeText.split(":");
            intArray[0] = Integer.parseInt(dateParts[0]);
            intArray[1] = Integer.parseInt(dateParts[1]);
            intArray[2] = Integer.parseInt(dateParts[2]);
            intArray[3] = Integer.parseInt(timeParts[0]);
            intArray[4] = Integer.parseInt(timeParts[1]);
            intArray[5] = Integer.parseInt(timeParts[2]);
        } catch (Exception e) {
            intArray = null;
        }
        return intArray;
    }
}
