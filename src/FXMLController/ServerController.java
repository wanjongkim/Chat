package FXMLController;

import Server.Server;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;

public class ServerController implements Initializable {

    @FXML
    private Label status;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button start;
    @FXML
    private Button shutdown;
    
    private Server server;
    private boolean serverRunning;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
    @FXML    //use lock for starting and ending the server
    private void initiateServer(MouseEvent e) {
        if(!serverRunning) {
            serverRunning = true;
            server = new Server(this);
            server.validateCredentials(username.getText(), password.getText());
        }
    }
    
    @FXML
    private void shutdownServer(MouseEvent e) {
        if(serverRunning) {
            server.shutdown();
            serverRunning = false;
            status.setText("Server has shutdown");
        }
    }
    
    public void handleCloseRequest() {
        shutdown.getScene().getWindow().setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if(serverRunning) {
                    server.shutdown();
                    serverRunning = false;
                }
            }          
        });
    }
    
    public void runServer() {
        if(server == null) {
            server = new Server(this);
        }
        else {
            Thread serverThread = new Thread(server);
            serverThread.start();
        }
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
