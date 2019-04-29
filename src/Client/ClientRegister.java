package Client;

import FXMLController.ClientRegisterController;
import Message.MessageType;
import Message.Message;

public class ClientRegister {

    private final ClientRegisterController controller;
    private Client client;
    
    public ClientRegister(ClientRegisterController controller, Client client) {
        this.controller = controller;
        this.client = client;
    }
    
    public void register(String username, String password) {
        Message message = new Message(MessageType.REGISTER);
        message.setUsername(username);
        message.setPassword(password);
        client.writeMessage(message);
        System.out.println("Test");
        Message serverMessage = client.readMessage();
        if(serverMessage.getType() == MessageType.ACCOUNT_FAILED) {
            controller.setStatus("Failed to create account");
        }
        else if(serverMessage.getType() == MessageType.ACCOUNT_CREATED) {
            controller.close();
        }
    }
    
}
