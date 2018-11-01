package Message;

import java.io.Serializable;

public class Message implements Serializable {
    
    private final MessageType type;
    private String message;
    private String username;
    private String password;
    
    public Message(MessageType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public MessageType getType() {
        return type;
    }
}
