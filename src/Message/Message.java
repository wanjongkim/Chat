package Message;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {
    
    private final MessageType type;
    private String message;
    private String username;
    private String userToSendTo;
    private String password;
    private List usernamesList;
    private int soundSize;
    
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
    
    public List getUsernamesList() {
        return usernamesList;
    }

    public void setUsernamesList(List usernamesList) {
        this.usernamesList = usernamesList;
    }

    public String getUserToSendTo() {
        return userToSendTo;
    }

    public void setUserToSendTo(String userToSendTo) {
        this.userToSendTo = userToSendTo;
    }

    public int getSoundSize() {
        return soundSize;
    }

    public void setSoundSize(int soundSize) {
        this.soundSize = soundSize;
    }
}
