package FXMLController;

import Client.Client;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ClientLoginController implements Initializable {

    @FXML
    private Label status;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button login;
    @FXML
    private Button register;
    
    private Client client;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void handleLogin(MouseEvent e) {
        if(client == null)
            client = new Client(this);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                client.login(username.getText(), password.getText());    
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
    
    @FXML
    private void handleRegister(MouseEvent e) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/ClientRegister.fxml"));
        Parent root;
        try {
            root = loader.load();
            ClientRegisterController controller = loader.getController();
            if(client == null)
                client = new Client(this);
            controller.setClient(client);
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(ClientLoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void changeStatus(String serverStatus) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                status.setText(serverStatus);
            }
        });
    }
    
    public void loginSucceeded() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/MainClient.fxml"));
        Parent root;
        try {
            root = loader.load();
            MainClientController controller = loader.getController();
            if(client == null)
                client = new Client(this);
            controller.setClient(client);
            client.setMainClientController(controller);
            closeWindow();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.show();
                }
            });
            Thread clientThread = new Thread(client);
            clientThread.start();
        } catch (IOException ex) {
            Logger.getLogger(ClientLoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void closeWindow() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Stage stage = (Stage) username.getScene().getWindow();
                stage.close();
            }
        });
    }
    
}
