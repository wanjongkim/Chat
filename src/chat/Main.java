package chat;

import Server.DBConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.persistence.Persistence;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Server.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    
     public static void main(String[] args) {
        launch(args);
         /*
        Map result = new HashMap();
        result.put( "javax.persistence.jdbc.password", password);
        
        
        AccountJpaController ac = new AccountJpaController(Persistence.createEntityManagerFactory(persistenceName));
        try {
            ac.create(new Account(new AccountPK("FirstAccount"), "password"));
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
    }
}
