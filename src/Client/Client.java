package Client;

import FXMLController.ClientLoginController;
import FXMLController.MainClientController;
import Message.Message;
import Message.MessageType;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client implements Runnable {
    
    private Socket socket;
    private final String address = "localhost";
    private final int port = 25560;
    private final ClientLoginController loginController;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private String username;
    private MainClientController mainClient;
    private boolean running = true;
    
    public Client(ClientLoginController loginController) {
        try {
            socket = new Socket(address, port);    
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.loginController = loginController;
    }
    
    @Override
    public void run() {
        while(running) {
            Message message;
            message = readMessage();
            
            if(null != message.getType()) switch (message.getType()) {
                case CHAT:
                    mainClient.serverChatType(message.getMessage());
                    break;
                case NEW_MEMBER:
                    mainClient.announceNewMem(message.getMessage(), message.getUsername());
                    break;
                case MEMBERS_LIST:
                    mainClient.addUsersToList(message.getUsernamesList());
                    break;
                default:
                    break;
            }
        }
    }
    
    public void writeMessage(Message message) {
        try {
            if(oos == null)
                oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Message readMessage() {
        Message serverMessage = null;
        try {
            if(ois == null)
                ois = new ObjectInputStream(socket.getInputStream());
            serverMessage = (Message) ois.readObject();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return serverMessage;
    }
    
    public void shutdown() {
        if(ois != null) {
            try {
                ois.close();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(oos != null) {
            try {
                oos.close();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(socket != null) {
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void login(String username, String password) {
        Message message = new Message(MessageType.LOGIN);
        message.setUsername(username);
        message.setPassword(password);
        int waitTime = 0;
        while(socket == null) {
            try {
                if(waitTime >= 12000) {
                    loginController.changeStatus("Login Failed");
                    return;
                }
                Thread.sleep(3000);
                waitTime += 3000;
            } catch (InterruptedException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        writeMessage(message);
        
        Message serverMessage;
        serverMessage = readMessage();
        if(serverMessage.getType() == MessageType.LOGIN_SUCCEED) {
            loginController.loginSucceeded();
            this.username = username;
        }
        else if(serverMessage.getType() == MessageType.LOGIN_FAILED) {
            loginController.changeStatus("Login Failed");
        }
    }
    
    public void sendChatMessage(String userMessage) {
        Message message = new Message(MessageType.CHAT);
        message.setMessage(userMessage);
        writeMessage(message);
        
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setMainClientController(MainClientController mainClient) {
        this.mainClient = mainClient;
    }
    
    public void setRunning(boolean running) {
        this.running = running;
    }
}
