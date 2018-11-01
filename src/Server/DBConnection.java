package Server;

import Entity.Account;
import Entity.AccountPK;
import EntityController.AccountJpaController;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DBConnection {
    
    private final String persistenceName = "ChatPU";
    private final String URL = "jdbc:mysql://127.0.0.1:3306/chat?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private final String driver = "com.mysql.cj.jdbc.Driver";
    private Connection connection;
    private AccountJpaController accountController;
    
    public boolean connect(String username, String password) {
        try {
            if(connection == null || connection.isClosed()) {
                EntityManagerFactory emf = getEntityManagerFactory(username, password);
                EntityManager em = emf.createEntityManager();
                em.getTransaction().begin();
                connection = em.unwrap(Connection.class);
                em.getTransaction().commit();
                if(connection == null) {
                    return false;
                }
                accountController = new AccountJpaController(emf);
            }
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }
    
    public synchronized boolean createAccount(String username, String password) {
        try {
            accountController.create(new Account(new AccountPK(username), password));
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
    private EntityManagerFactory getEntityManagerFactory(String username, String password) {
        return Persistence.createEntityManagerFactory( persistenceName,
            getProperties(username, password) );
    }

    private Map getProperties(String username, String password) {
        Map result = new HashMap();
        result.put( "javax.persistence.jdbc.user", username );
        result.put( "javax.persistence.jdbc.password", password );
        result.put( "javax.persistence.jdbc.url", URL);
        result.put( "javax.persistence.jdbc.driver", driver);
        return result;
    }
    
    public void destroy() {
        try {
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
