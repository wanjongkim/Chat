package Server;

import Message.Message;
import Message.MessageType;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientConnection implements Runnable {
    
    private final Socket client;
    private final DBConnection db;
    
    public ClientConnection(Socket client, DBConnection db) {
        this.client = client;
        this.db = db;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream input = (ObjectInputStream) client.getInputStream();
            ObjectOutputStream output = (ObjectOutputStream) client.getOutputStream();
            Message clientMessage = (Message) input.readObject();
            MessageType type = clientMessage.getType();
            switch(type) {
                case CREATE: 
                    boolean created = db.createAccount(clientMessage.getUsername(), clientMessage.getPassword());
                    Message serverMessage = created ? new Message(MessageType.ACCOUNT_CREATED) : new Message(MessageType.ACCOUNT_FAILED);
                    output.writeObject(serverMessage);
                    break;
                case LOGIN:
                    break;
                default:
                    break;
            }
        } catch (IOException ex) {
            System.out.println("Connection lost");
        } catch (ClassNotFoundException ex) {
            System.out.println("Class of serializable cannot be found");
        }
    }
}
