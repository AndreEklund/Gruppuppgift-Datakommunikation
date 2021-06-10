package control;

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
        private User user;

        /**
         * @param socket skickas med från huvudklassen vid instansiering
         */
        public Client(Socket socket){
            this.socket = socket;
        }

        public ObjectOutputStream getOos() {
            return oos;
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

                    if (message.getText().equals("Connect") && recivers == null) {
                        connectedClient(message);
                        activeUsers();
                    }
                    if (recivers != null) {
                        noReceivers(message);
                    }
                }
            } catch (IOException e) {
                try {
                    exitClient();
                    activeUsers();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
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
            user = message.getUser();
            trafficLog.info("User " + user.getUserName() + " connected");
            clients.put(user, this);
            ObjectOutputStream oosReceiver;

            ArrayList<Message> pendingMessages = unsendMessages.getArrayList(message.getUser());
            if (pendingMessages != null) {
                for (Message m : pendingMessages) {
                    trafficLog.info("Sending unsent messages to " + user.getUserName());
                    oosReceiver = clients.get(user).getOos();
                    oosReceiver.writeObject(m);
                    oosReceiver.flush();
                    trafficLog.info("Message from " + m.getUser().getUserName() +
                            " sent to " + user.getUserName());
                }
                unsendMessages.remove(user);
            }
        }

        /**
         * Körs om recievers != null
         * Metoden loopar igenom alla mottagare och klienter
         * som är lagrade i mappen. Om mottagaren hittas så skickas meddelandet iväg,
         * annars lagras det i unsent messages(avsändare & meddelande)
         * @param message
         * @throws IOException
         */
        private void noReceivers(Message message) throws IOException {
            ObjectOutputStream oosReceiver;
            for (User receiver : recivers) {
                for (User key : clients.clients.keySet()) {
                    if (receiver.equals(key)) {
                        oosReceiver = clients.get(receiver).getOos();
                        oosReceiver.reset();
                        oosReceiver.writeObject(message);

                        oosReceiver.flush();
                        trafficLog.info("Message from " + message.getUser().getUserName() +
                                " sent to " + receiver.getUserName());
                    } else {
                        unsendMessages.put(receiver, message);
                        trafficLog.info("Message from " + message.getUser().getUserName() +
                                " added to unsent list for " + receiver.getUserName());
                    }
                }
            }
        }

        /**
         * Stänger uppkopplingen om användaren kopplar ifrån och
         * @throws IOException
         */
        private void exitClient() throws IOException {
            if (!socket.isClosed()) {
                socket.close();
            }
            if (user != null) {
                clients.remove(user);
                trafficLog.info("User " + user.getUserName() + " has disconnected.");
            } else {
                trafficLog.info("Connection failed.");
            }
        }

        /**
         * Låter användaren hämta listan med aktiva användare
         * @throws IOException
         */
        private void activeUsers() throws IOException {
            ObjectOutputStream oosReceiver;
            list.addAll(clients.clients.keySet());
            OnlineListMessage listMessage = new OnlineListMessage(list);
            for (User receiver : clients.clients.keySet()) {
                oosReceiver = clients.get(receiver).getOos();
                oosReceiver.reset();
                oosReceiver.writeObject(listMessage);
                oosReceiver.flush();
            }

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
        public synchronized void remove(User user){
            clients.remove(user);
        }
        public HashMap<User, Client> getClients() {
            return clients;
        }
    }

    public static void main(String[] args) throws IOException {
        MessageServer messageServer= new MessageServer(434);
    }
}
