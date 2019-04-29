package Server;

import Message.Message;
import Message.MessageType;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientConnection implements Runnable {
    
    private final Socket client;
    private final DBConnection db;
    private static final List<Socket> connections = new ArrayList();
    private ObjectInputStream input;
    private ObjectOutputStream output;
    
    public ClientConnection(Socket client, DBConnection db) {
        this.client = client;
        this.db = db;
    }

    @Override
    public void run() {
        try {
            input = new ObjectInputStream(client.getInputStream());
            output = new ObjectOutputStream(client.getOutputStream());
            Message clientMessage = (Message) input.readObject();
            MessageType type = clientMessage.getType();
            Message serverMessage;
            switch(type) {
                case REGISTER:
                    boolean registered = db.createAccount(clientMessage.getUsername(), clientMessage.getPassword());
                    serverMessage = registered ? new Message(MessageType.ACCOUNT_CREATED) : new Message(MessageType.ACCOUNT_FAILED);
                    output.writeObject(serverMessage);
                    break;
                case LOGIN:
                    boolean loggedIn = db.connect(clientMessage.getUsername(), clientMessage.getPassword());
                    serverMessage = loggedIn ? new Message(MessageType.LOGIN_SUCCEED) : new Message(MessageType.LOGIN_FAILED);
                    System.out.println(loggedIn);
                    output.writeObject(serverMessage);
                    if(loggedIn) {
                        Message message = new Message(MessageType.NEW_MEMBER);
                        message.setMessage(clientMessage.getUsername() + " has arrived!");
                        connections.add(client);
                        sendAllClients(message, client);
                    }
                    break;
                case CHAT:
                    Message message = new Message(MessageType.CHAT);
                    message.setMessage(clientMessage.getMessage());
                    sendAllClients(message, client);
                    break;
                default:
                    break;
            }
        } catch (IOException ex) {
            System.out.println("Connection lost");
        } catch (ClassNotFoundException ex) {
            System.out.println("Class of serializable cannot be found");
        } finally {
            if(input != null) {
                try {
                    input.close();
                } catch (IOException ex) {
                    System.out.println("Failed to close input stream");
                }
            }
            if(output != null) {
                try {
                    output.close();
                } catch (IOException ex) {
                    System.out.println("Failed to close output stream");
                }
            }
        }
    }
    
    private void sendAllClients(Message message, Socket client) {
        synchronized(connections) {
            for(Socket socket: connections) {
                if(!socket.equals(client)) {
                    ObjectOutputStream oos = null;
                    try {
                        oos = new ObjectOutputStream(socket.getOutputStream());
                        oos.writeObject(message);
                    } catch (IOException ex) {
                        Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        try {
                            if(oos != null)
                                oos.close();
                        } catch (IOException ex) {
                            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
    }
}
