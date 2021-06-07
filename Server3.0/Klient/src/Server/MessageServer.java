package Server;

import entity.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.*;

/**
 * Serverklass med en inre klass som lagrar klienter i en map
 */

public class MessageServer extends Thread  {

    private Socket socket;
    private ServerSocket serverSocket;
    private Client client;
    private Clients clients;
    private ArrayList<User> recivers = new ArrayList();
    private ArrayList<User> list = new ArrayList<>();
    private UnsendMessages unsendMessages;
    private final static Logger trafficLog = Logger.getLogger("traffic");
    private FileHandler trafficFile = new FileHandler("files/trafficLog.log");
    private LogReader logReader;

    /**
     * Konstruktor som instansierar objekten för socket,
     * inre klassen och osända meddelanden
     * @param port porten som socketen lyssnar på
     * @throws IOException
     */
    public MessageServer(int port) throws IOException {
        initializeLogger();
        serverSocket = new ServerSocket(port);
        clients =new Clients();
        unsendMessages = new UnsendMessages();
        start();
        logReader.start();
    }
    /**
     * run-metod som accepterar anslutningen
     * och startar servern
     */
    public void run()  {
        try {
            trafficLog.info("Server started");
            while (true) {
                socket = serverSocket.accept();
                client = new Client(socket);
                client.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * metod som kopplar från anslutning
     * vid exceptions
     */
    private void disconnect(){

    }
    /**
     * Initierar loggen där all trafik och
     * händelser lagras
     */
    public void initializeLogger() {
        trafficLog.setUseParentHandlers(false);
        trafficFile.setFormatter(new SimpleFormatter() {
            private static final String format = "%1$tF|%1$tT|%3$s %n";

            @Override
            public synchronized String format(LogRecord lr) {
                return String.format(format,
                        new Date(lr.getMillis()),
                        lr.getLevel().getLocalizedName(),
                        lr.getMessage()
                );
            }
        });
        trafficLog.addHandler(trafficFile);
        logReader = new LogReader("files/trafficLog.log");
    }

    /**
     * Inre klass som hanterar klientförfrågningar
     */
    public class Client extends Thread implements Serializable {

        private Socket socket;
        private ObjectInputStream ois;
        private ObjectOutputStream oos;

        /**
         * @param socket skickas med från huvudklassen vid instansiering
         */
        public Client(Socket socket){
            this.socket = socket;
        }
        /**
         * run-metod som körs sålänge servern är aktiv
         * hämtar förfrågningar från klienter
         */
        @Override
        public void run() {
            try {
                ois = new ObjectInputStream(socket.getInputStream());
                oos = new ObjectOutputStream(socket.getOutputStream());
                while (true) {

                    Message message = (Message) ois.readObject();
                    recivers = message.getReceiverList();

                    if (message.getText().equals("Connect")) {
                        connectedClient(message);
                    }
                    if (message.getText().equals("ActiveUsers")) {
                        activeUsers(oos);
                    }
                    if (message.getText().equals("Exit")) {
                        exitClient(message);
                    }
                    if (recivers != null) {
                        noReceivers(oos, message);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                disconnect();
            }
        }
        /**
         * Körs när klient connectar till servern och lagrar denna
         * ska hämta unsent messages vid lyckad uppkoppling
         * @param message
         * @throws IOException
         * @throws ClassNotFoundException
         */
        public void connectedClient(Message message) throws IOException, ClassNotFoundException {
            trafficLog.info("User " + message.getUser().getUserName() + " connected");
            clients.put(message.getUser(), this);

            ArrayList<Message> pendingMessages = unsendMessages.getArrayList(message.getUser());
            if (pendingMessages != null) {
                for (Message m : pendingMessages) {
                    trafficLog.info("Sending unsent messages to " + message.getUser().getUserName());
                    oos = clients.get(message.getUser()).oos;
                    oos.writeObject(m);
                    oos.flush();
                    trafficLog.info("Message from " + m.getUser().getUserName() +
                            " sent to " + message.getUser().getUserName());
                }
                unsendMessages.remove(message.getUser());
            }
        }
        /**
         * Körs om recievers != null
         * Metoden loopar igenom alla mottagare och klienter
         * som är lagrade i mappen. Om mottagaren hittas så skickas meddelandet iväg,
         * annars lagras det i unsent messages(avsändare & meddelande)
         * @param oos
         * @param message
         * @throws IOException
         */
        private void noReceivers(ObjectOutputStream oos, Message message) throws IOException {
            for (User user : recivers) {
                for (User key : clients.clients.keySet()) {
                    if (user.equals(key)) {
                        oos = clients.get(user).oos;
                        oos.writeObject(message);

                        oos.flush();
                        trafficLog.info("Message from " + message.getUser().getUserName() +
                                " sent to " + user.getUserName());
                    } else {
                        unsendMessages.put(user, message);
                        trafficLog.info("Message from " + message.getUser().getUserName() +
                                " added to unsent list for " + user.getUserName());
                    }
                }
            }
        }
        /**
         * Stänger uppkopplingen om användaren kopplar ifrån och
         * @param message
         * @throws IOException
         */
        private void exitClient(Message message) throws IOException {
            socket.close();
            clients.Remove(message.getUser());
            trafficLog.info("User " + message.getUser() + " has disconnected.");
        }
        /**
         * Låter användaren hämta listan med aktiva användare
         * @param oos
         * @throws IOException
         * @throws ClassNotFoundException
         */
        private void activeUsers(ObjectOutputStream oos) throws IOException, ClassNotFoundException {

            for (User key : clients.clients.keySet()) {
                list.add(key);
            }
            oos.writeObject(new OnlineListMessage(list));
            trafficLog.info("Active users list sent.");

            list.clear();
        }
    }
    /**
     * Inre klass som lagrar klienter i en map
     * med tillhörande metoder
     */
    private class Clients implements Serializable {
        private  HashMap<User, Client> clients =new HashMap<>();

        public synchronized void put(User user,Client client) {
            clients.put(user, client);
        }
        public synchronized Client get(User user) {
            return clients.get(user);
        }
        public synchronized void Remove(User user){
            clients.remove(user);
        }
        public HashMap<User, Client> getClients() {
            return clients;
        }
    }
}
