package FXMLController;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.concurrent.Task;
import Client.Client;
import Message.MessageType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class MainClientController implements Initializable {

    @FXML
    private ScrollPane usersList;
    @FXML
    private VBox usersBox;
    @FXML
    private TextArea serverChat;
    @FXML
    private TextArea userChat;
    private Client client;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        userChat.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER) {
                    type();
                    userChat.clear();
                }
            }
        });
    }    
    
    public void type() {
        String userChat2 =  client.getUsername() + "\n" + userChat.getText();
        client.sendChatMessage(userChat2);
    }
    //send is true if the user is trying to send to other users
    //it's false if the user is receiving message from other users
    public void serverChatType(String message) {
        Platform.runLater(new Runnable() {
           @Override
           public void run() {
                String chat = serverChat.getText();
                chat += message;
                serverChat.setText(chat);
           }
        });
    }
    
    public void announceNewMem(String message, String username) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String chat = serverChat.getText();
                chat += message + "\n";
                serverChat.setText(chat);
            }
        });
        addUsersToList(Arrays.asList(username));
    }
    
    public void addUsersToList(List<String> users) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for(String user : users) {
                    Label username = new Label(user);
                    if(!username.getText().equalsIgnoreCase(client.getUsername())) {
                        ContextMenu menu = new ContextMenu();
                        MenuItem call = new MenuItem("Call");
                        call.setOnAction(new EventHandler<ActionEvent>(){
                            @Override
                            public void handle(ActionEvent event) {
                                System.out.println("Calling " + username.getText());
                                callUser(username.getText());
                            }
                        });
                        menu.getItems().add(call);
                        username.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
                            @Override
                            public void handle(ContextMenuEvent event) {
                                menu.show(username, event.getScreenX(), event.getScreenY());
                            }                        
                        });
                    }
                    usersBox.getChildren().add(username);
                }
            }
        });
    }
    //send an invite first and if the user accepts the call is established
    public void callUser(String username) {
        client.callUser(username);
    }
    
    public void setClient(Client client) {
        this.client = client;
    }
}
