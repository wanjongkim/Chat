package Server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class SocketInfo {
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket socket;
    
    public SocketInfo(Socket socket, ObjectInputStream input, ObjectOutputStream output) {
        this.socket = socket;
        this.input = input;
        this.output = output;
    }

    /**
     * @return the input
     */
    public ObjectInputStream getInput() {
        return input;
    }

    /**
     * @param input the input to set
     */
    public void setInput(ObjectInputStream input) {
        this.input = input;
    }

    /**
     * @return the output
     */
    public ObjectOutputStream getOutput() {
        return output;
    }

    /**
     * @param output the output to set
     */
    public void setOutput(ObjectOutputStream output) {
        this.output = output;
    }

    /**
     * @return the socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * @param socket the socket to set
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }
            
}
