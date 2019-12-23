package Server;

import Message.Message;
import Message.MessageType;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientConnection implements Runnable {
    
    private final Socket client;
    private final DBConnection db;
    private static final Map<String, SocketInfo> connections = new HashMap();
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private boolean running = true;
    private static final List<String> listOfUsers = new ArrayList();
    
    public ClientConnection(Socket client, DBConnection db) {
        this.client = client;
        this.db = db;
    }

    @Override
    public void run() {
        boolean firstTime = true;
        SocketInfo socketInfo = null;
        try {
            while(running) {
                if(input == null)
                    input = new ObjectInputStream(client.getInputStream());
                if(output == null)
                    output = new ObjectOutputStream(client.getOutputStream());
                if(firstTime) {
                    socketInfo = new SocketInfo(client, input, output);
                    firstTime = false;
                }
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
                        output.writeObject(serverMessage);
                        if(loggedIn) {
                            Message message = new Message(MessageType.NEW_MEMBER);
                            message.setUsername(clientMessage.getUsername());
                            message.setMessage(clientMessage.getUsername() + " has arrived!");
                            connections.put(clientMessage.getUsername(), socketInfo);
                            sendAllClients(message);
                            synchronized(listOfUsers) {    
                                Message message2 = new Message(MessageType.MEMBERS_LIST);
                                message2.setUsernamesList(listOfUsers);
                                sendSingleClient(message2);
                                listOfUsers.add(clientMessage.getUsername());
                            }
                        }
                        break;
                    case CHAT:
                        Message message = new Message(MessageType.CHAT);
                        message.setMessage(clientMessage.getMessage());
                        sendAllClients(message);
                        break;
                    case VOICECHAT:
                        byte[] sound = new byte[clientMessage.getSoundSize()];
                        input.readFully(sound, 0, sound.length);
                        sendSpecificClientSound(clientMessage, sound);
                        break;
                    default:
                        break;
                }
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
    private void sendAllClients(Message message) {
        synchronized(connections) {
            for(SocketInfo socketInfo: connections.values()) {
                try {
                    ObjectOutputStream oos = socketInfo.getOutput();
                    synchronized(oos) {
                        oos.writeObject(message);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
        }
    }
    private void sendSingleClient(Message message) {
        try {
            synchronized(output) {
                output.writeObject(message);
            }
        } catch (IOException ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void sendSpecificClient(Message message) {
        String userToSendTo = message.getUserToSendTo();
        SocketInfo info = connections.get(userToSendTo);
        ObjectOutputStream oos = info.getOutput();
        synchronized(oos) {
            try {
                oos.writeObject(message);
            } catch (IOException ex) {
                Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    private void sendSpecificClientSound(Message message, byte[] buffer) {
        String userToSendTo = message.getUserToSendTo();
        SocketInfo info = connections.get(userToSendTo);
        ObjectOutputStream oos = info.getOutput();
        synchronized(oos) {
            try {
                oos.writeObject(message);
                oos.write(buffer, 0, buffer.length);
                oos.flush();
            } catch (IOException ex) {
                Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    /*
    private static final List<SocketInfo> connections = new ArrayList();
    private void sendAllClients(Message message) {
        synchronized(connections) {
            for(SocketInfo socketInfo: connections) {
                try {
                    ObjectOutputStream oos = socketInfo.getOutput();
                    oos.writeObject(message);
                } catch (IOException ex) {
                    Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
        }
    }
    private void sendSingleClient(Message message) {
        try {
            output.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    */
}
