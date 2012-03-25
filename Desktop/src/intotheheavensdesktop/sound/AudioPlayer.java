/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package intotheheavensdesktop.sound;

import java.io.File;

import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author joshua
 */
public class AudioPlayer 
{
    public enum State
    {
        ERROR,
        PLAYING,
        STOPPED,
        PAUSED
    }
    
    private State _state = State.ERROR;
    private AudioInputStream _stream = null;
    private AudioFormat _format = null;
    private SourceDataLine _line = null;
    private float _gain = 1.0f;
    
    public State getState()
    {
        return _state;
    }
    
    public AudioFormat getFormat()
    {
        return _format;
    }
    
    public float getVolume()
    {
        return _gain;
    }
    
    public void setVolume(float gain)
    {
        _gain = gain;
        
        if (_line != null)
        {
            FloatControl gainControl = (FloatControl)_line.getControl(FloatControl.Type.MASTER_GAIN);
            float db = (float) Math.log10(gain) * 20;
            gainControl.setValue(db);
        }
    }
    
    public AudioPlayer(File audioFile) throws IOException, UnsupportedAudioFileException
    {
        _stream = AudioSystem.getAudioInputStream(audioFile);
        _format = _stream.getFormat();
        _format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                                  _format.getSampleRate(),
                                  16,
                                  _format.getChannels(),
                                  _format.getChannels() * (16 / 8),
                                  _format.getSampleRate(),
                                  false);
        _stream = AudioSystem.getAudioInputStream(_format, _stream);
        _state = State.STOPPED;
    }
    
    public void play() throws LineUnavailableException, IOException
    {
        if (_line == null)
        {
            _line = AudioSystem.getSourceDataLine(_format);
            _line.open();
            setVolume(_gain);
        }
        
        _line.start();
        update();
        _state = State.PLAYING;
    }
    
    public void pause()
    {
        _line.stop();
        _state = State.PAUSED;
    }
    
    public void stop()
    {
        _line.stop();
        _line.flush();
        _line.close();
        _line = null;
        _state = State.STOPPED;
    }
    
    public void update() throws IOException
    {
        byte[] buffer = new byte[_line.available()];
        int bytesRead = _stream.read(buffer, 0, buffer.length);
        
        if (bytesRead >= 0) _line.write(buffer, 0, bytesRead);
        else
        {
            _line.drain();
            stop();
        }
    }
}
