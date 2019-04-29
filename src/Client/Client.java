package Client;

import FXMLController.ClientLoginController;
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
    
    public Client(ClientLoginController loginController) {
        try {
            socket = new Socket("localhost", port);    
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.loginController = loginController;
    }
    
    @Override
    public void run() {
        
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
    }
    
    public void login(String username, String password) {
        Message message = new Message(MessageType.LOGIN);
        while(socket == null) {
            try {
                System.out.println("Sleep");
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Don't Sleep");
        writeMessage(message);
        Message serverMessage = readMessage();
        if(serverMessage.getType() == MessageType.LOGIN_SUCCEED) {
            loginController.loginSucceeded();
        }
        else if(serverMessage.getType() == MessageType.LOGIN_FAILED) {
            loginController.changeStatus("Login Failed");
        }
    
    }
}
