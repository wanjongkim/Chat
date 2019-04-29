package FXMLController;

import Client.Client;
import Client.ClientRegister;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ClientRegisterController implements Initializable {

    @FXML
    private Button register;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Label status;
    private ClientRegister clientRegister;
    private Client client;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
    @FXML
    private void handleRegister(MouseEvent e) {
        if(clientRegister == null)
            clientRegister = new ClientRegister(this, client);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                clientRegister.register(username.getText(), password.getText());
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
    
    public void setStatus(String serverStatus) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                status.setText(serverStatus);
            }
        });
    }
    
    public void setClient(Client client) {
        this.client = client;
    }
    
    public void close() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Stage stage = (Stage) status.getScene().getWindow();
                stage.close();
            }
        });
    }
    
}
