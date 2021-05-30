package Server;

import entity.Message;
import entity.User;

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


    public MessageServer(int port) throws IOException {
//        System.out.println("Server online!");
        initializeLogger();
        serverSocket = new ServerSocket(port);
        clients =new Clients();
        unsendMessages = new UnsendMessages();
        start();
        logReader.start();
    }


    public void run()  {
        try {
            trafficLog.info("Server started");
            while (true) {
                socket = serverSocket.accept();
                client = new Client(socket);
                client.start();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }




    private void disconnect(){

    }

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

    public class Client extends Thread implements Serializable {
        private Socket socket;
        private ObjectInputStream ois;
        private ObjectOutputStream oos;

       public Client(Socket socket) throws IOException, ClassNotFoundException {
            this.socket = socket;
       }

        @Override
        public void run() {
            try {
                ois =  new ObjectInputStream(socket.getInputStream());
                oos = new ObjectOutputStream(socket.getOutputStream());
                while (true) {


                    Message message = (Message) ois.readObject();
                    recivers = message.getReceiverList();
//                    System.out.println("Read object");

                    if (message.getText().equals("Connect")){
                        trafficLog.info("User " + message.getUser().getUserName() + " connected");
                        clients.put(message.getUser(),this);
//                        System.out.println("User connected: " + message.getUser());

                        ArrayList <Message> pendingMessages = unsendMessages.getArrayList(message.getUser());
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

                    if (message.getText().equals("ActiveUsers")){

//                        System.out.println("User list called");
                        for (User key: clients.clients.keySet()) {
                            list.add(key);
                        }
//                        System.out.println("Startar skickning av lista");
                        oos.writeObject(list);
                        trafficLog.info("Active users list sent.");

//                        System.out.println("Lista skickad");
                        list.clear();
                    }

                    if (message.getText().equals("Exit")){
                        socket.close();
                        clients.Remove(message.getUser());
                        trafficLog.info("User " + message.getUser() + " has disconnected.");
                    }

                    if (recivers!= null) {

//                        System.out.println("Message traffic");
                        for (User user: recivers) {
                            for (User key: clients.clients.keySet()) {
                                if (user.equals(key)) {
                                    oos = clients.get(user).oos;
                                    oos.writeObject(message);
//                                    System.out.println("Message = "+message.getText());
//                                    System.out.println("Message sent by = "+message.getUser().getUserName());
                                    oos.flush();
                                    trafficLog.info("Message from " + message.getUser().getUserName() +
                                            " sent to " + user.getUserName());
//                                    System.out.println("Message sent ");
                                }
                                else {
                                    unsendMessages.put(user, message);
                                    trafficLog.info("Message from " + message.getUser().getUserName() +
                                            " added to unsent list for " + user.getUserName());
                                }
                            }
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                disconnect();
            }
        }
    }//class

    private class Clients implements Serializable {
        private  HashMap<User, Client> clients =new HashMap<>();
        // egna tillägg
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
        // fler synchronized-metoder som behövs
    }//Class





}//Class
