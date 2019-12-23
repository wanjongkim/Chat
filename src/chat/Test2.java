/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import Client.Call;
import Client.Client;
import Message.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author Shawn
 */
public class Test2 {
    
    ObjectInputStream ois;
    TargetDataLine microphone = null;
    SourceDataLine speaker = null;
    AudioFormat format;
    Socket socket;
    
    public Message readMessage() {
        Message serverMessage = null;
        try {
            if(ois == null)
                ois = new ObjectInputStream(socket.getInputStream());
            synchronized(ois) {
                serverMessage = (Message) ois.readObject();
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return serverMessage;
    }
    
    public void initiate() {
        format = new AudioFormat(44100.0f, 16, 2, true, false);
        try {
            
            speaker = AudioSystem.getSourceDataLine(format);
            speaker.open();
            speaker.start();
        } catch (LineUnavailableException ex) {
            Logger.getLogger(Call.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void tts2() {
        try {
            ServerSocket ss = new ServerSocket(25560);
            socket = ss.accept();
            ois = new ObjectInputStream(socket.getInputStream());
            while(true) {   
                
                byte[] buf = new byte[88200];
                ois.readFully(buf, 0, buf.length);
                System.out.println(buf.length);
                speaker.write(buf, 0, buf.length);
                
            }
        } catch (IOException ex) {
            Logger.getLogger(Test2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) {
        Test2 t = new Test2();
        t.initiate();
        t.tts2();
    }
}
