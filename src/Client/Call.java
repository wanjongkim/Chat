package Client;

import Message.MessageType;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import Message.*;

public class Call implements Runnable {
    private AudioFormat format;
    private TargetDataLine microphone;
    private SourceDataLine speaker;
    private byte[] soundData;
    private byte[] incomingSoundData;
    private boolean running = true;
    private final Client client;
    private String userToCall;
    
    public Call(Client client, String userToCall) {
        this.client = client;
        this.userToCall = userToCall;
    }
    
    public void initiate() {
        format = new AudioFormat(44100.0f, 16, 2, true, false);
        try {
            microphone = AudioSystem.getTargetDataLine(format);
            speaker = AudioSystem.getSourceDataLine(format);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(Call.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(microphone != null) {
            soundData = new byte[microphone.getBufferSize()];
        }
    }
    
    @Override
    public void run() {
        try {
            microphone.open();
            speaker.open();
        } catch (LineUnavailableException ex) {
            Logger.getLogger(Call.class.getName()).log(Level.SEVERE, null, ex);
            microphone.close();
            speaker.close();
            return;
        }
        if(microphone != null && speaker != null) {
            microphone.start();
            speaker.start();
        }
        else {
            System.out.println("Null microphone and speaker");
        }
        
        while(running) {
            microphone.read(soundData, 0, soundData.length);
            Message message = new Message(MessageType.VOICECHAT);
            message.setSoundSize(soundData.length);
            message.setUserToSendTo(userToCall);
            message.setUsername(client.getUsername());
            client.writeSoundMessage(message, soundData);
            try {
                //wait here until lock is released
                synchronized(this) {
                    wait();
                }
                
            } catch (InterruptedException ex) {
                Logger.getLogger(Call.class.getName()).log(Level.SEVERE, null, ex);
            }
            speaker.write(incomingSoundData, 0, incomingSoundData.length);
        }
        microphone.close();
        speaker.close();
    }
    
    public void setIncomingSoundData(byte[] incomingSoundData) {
        synchronized(this) {
            notify();
        }
        this.incomingSoundData = incomingSoundData;
    }
    
    public void setRunning(boolean running) {
        this.running = running;
    }
    
    public boolean getRunning() {
        return running;
    }

    public void setUserToCall(String userToCall) {
        this.userToCall = userToCall;
    }
}
