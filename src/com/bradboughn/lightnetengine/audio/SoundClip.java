
package com.bradboughn.lightnetengine.audio;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundClip {
    
    private Clip clip = null;
    private FloatControl gainControl;

    public SoundClip (String path) {
        try {
        InputStream audioSource = SoundClip.class.getResourceAsStream(path);
        InputStream bufferedIn = new BufferedInputStream(audioSource);
        AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedIn);
        AudioFormat baseFormat = ais.getFormat();
        AudioFormat decodeFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,     //Encoding Format: the audio encoding technique 
                                                   baseFormat.getSampleRate(),          //Sample Rate: number of samples per second
                                                   16,                                  //Sample Size in Bits: number of bits in each sample
                                                   baseFormat.getChannels(),            //Number of Channels: 1 for mono, 2 for stereo, etc.
                                                   baseFormat.getChannels()*2,          //Frame Size: number of bytes in each frame
                                                   baseFormat.getSampleRate(),          //Frame Rate: number of frames per second
                                                   false);                              //Big-Endian: whether date for a single sample is stored in big-Endian byte order (false=little-endian)
        AudioInputStream decodeAis = AudioSystem.getAudioInputStream(decodeFormat, ais);
        
        clip = AudioSystem.getClip();
        clip.open(decodeAis);
        
        gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    
    public void play() {
        if (clip == null) return;
        stop();
        
        clip.setFramePosition(0);
        while(!clip.isRunning()) { //just to make sure it keeps trying to play the sound, in case it ever hiccups/etc.
            clip.start();
        }
    }
    
    public void stop() {
        if (clip.isRunning()) 
            clip.stop();
    }
    
    public void close() {
        clip.stop();
        clip.drain(); //empties the audiobuffer/stream
        clip.close();
    }
    
    public void loop() {
        clip.loop(Clip.LOOP_CONTINUOUSLY); //sets it to loop, but doesn't actually start it
        clip.start();
    }
    
    public void setVolume(float value) {
        gainControl.setValue(value);
    }
    
    public boolean isRunning() {
        return clip.isRunning();
    }
    
}
