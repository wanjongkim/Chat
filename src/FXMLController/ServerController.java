package FXMLController;

import Server.Server;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class ServerController implements Initializable {

    @FXML
    private Label status;
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private Button start;
    @FXML
    private Button shutdown;
    
    private Thread serverThread;
    private Server server;
    private boolean serverRunning;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
    @FXML    //use lock for starting and ending the server
    private void initiateServer(MouseEvent e) {
        serverRunning = true;
        if(!serverRunning) {
            server = new Server(this);
            server.validateCredentials(username.getText(), password.getText());
        }
    }
    
    @FXML
    private void shutdownServer(MouseEvent e) {
        ////////////////
    }
    
    public void setStatus(String serverStatus) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                status.setText(serverStatus);
            }
        });
    }
    
    public void setServerRunning(boolean serverRunning) {
        this.serverRunning = serverRunning;
    }
}
