/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import Client.Call;
import Client.Client;
import Message.Message;
import Message.MessageType;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
public class Test {
    byte[] soundData;
    byte[] incomingSoundData;
    Socket socket;
    String address = "localhost"; //change to localhost later
    int port = 25560;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    
    
    public Test() throws IOException {
        socket = new Socket(address, port);
    }
    
    public void TestVoice() {
        boolean running = true;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
        AudioFormat format = new AudioFormat(44100.0f, 16, 2, true, false);
        TargetDataLine microphone = null;
        SourceDataLine speaker = null;
        
        try {
            microphone = AudioSystem.getTargetDataLine(format);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(Call.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            microphone.open();
        } catch (LineUnavailableException ex) {
            Logger.getLogger(Call.class.getName()).log(Level.SEVERE, null, ex);
            microphone.close();
            speaker.close();
            return;
        }
        if(microphone != null) {
            microphone.start();
        }
        else {
            System.out.println("Null microphone and speaker");
        }
        soundData = new byte[microphone.getBufferSize()];
        while(running) {
            microphone.read(soundData, 0, soundData.length);
            Message message = new Message(MessageType.VOICECHAT);
            
            try {
                oos.write(soundData, 0, soundData.length);
                System.out.println(soundData.length);
                /*
                try {
                //wait here until lock is released
                synchronized(this) {
                wait();
                }
                
                } catch (InterruptedException ex) {
                Logger.getLogger(Call.class.getName()).log(Level.SEVERE, null, ex);
                }
                speaker.write(incomingSoundData, 0, incomingSoundData.length);
                */
            } catch (IOException ex) {
                Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        microphone.close();
        speaker.close();
    }
    
    public void writeMessage(Message m) {
        try {
            if(oos == null)
                oos = new ObjectOutputStream(socket.getOutputStream());
            synchronized(oos) {
                oos.writeObject(m);
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) {
        Test t;
        try {
            t = new Test();
            
            t.TestVoice();
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
