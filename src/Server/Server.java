package Server;

import FXMLController.ServerController;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Server implements Runnable {
    
    private final ServerController controller;
    private ServerSocket server;
    private final DBConnection db;
    private final int port  = 25560;
    private boolean running;
    
    public Server(ServerController controller) {
        this.controller = controller;
        db = new DBConnection();
    }
    
    @Override
    public void run() {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        running = true;
        try {
            server = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        while(running) {
            try {
                System.out.println("Waiting for connection");
                Socket client = server.accept();
                System.out.println("New connection");
                ClientConnection connection = new ClientConnection(client, db);
                executor.submit(connection);
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void validateCredentials(String username, String password) {
        ServerTask serverTask = new ServerTask(username, password);
        Thread taskThread = new Thread(serverTask);
        taskThread.start();
    }
    
    public void shutdown() {
        
    }
    
    private class ServerTask extends Task {

        private final String username;
        private final String password;
        private boolean connected;
        
        public ServerTask(String username, String password) {
            this.username = username;
            this.password = password;
        }
        
        @Override
        protected Void call() throws Exception {
            connected = db.connect(username, password);
            return null;
        }
        
        @Override
        protected void succeeded() {
            if(connected) {  
                controller.setStatus("Sever is running");
                controller.runServer();
            }
            else {
                controller.setServerRunning(false);
                controller.setStatus("Failed to start the server");
            }
        }
        
        @Override
        protected void failed() {
            controller.setServerRunning(false);
            controller.setStatus("Failed to start the server");
        }
        
    }
    
}
