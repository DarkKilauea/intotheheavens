/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author joshua
 */
public class GameMode 
{
    public enum State
    {
        Unknown,
        Initialized,
        Running,
        Paused,
        Stopped
    }
    
    protected GameModeManager _manager = null;
    protected State _state = State.Unknown;
    protected List<IGameModeListener> _listeners = new ArrayList<IGameModeListener>();
    
    public boolean initialize(GameModeManager manager)
    {
        _manager = manager;
        _state = State.Initialized;
        
        return true;
    }
    
    public void resume()
    {
        _state = State.Running;
    }
    
    public void pause()
    {
        _state = State.Paused;
    }
    
    public void shutdown()
    {
        _manager = null;
        _state = State.Stopped;
    }
    
    public State getState()
    {
        return _state;
    }
    
    public boolean registerListener(IGameModeListener listener)
    {
        return _listeners.add(listener);
    }
    
    public boolean unregisterListener(IGameModeListener listener)
    {
        return _listeners.remove(listener);
    }
    
    public void injectTextInput(String input)
    {
        
    }
    
    protected void printToAllListeners(String message)
    {
        for(IGameModeListener listener : _listeners)
        {
            listener.onTextOutput(message);
        }
    }
    
    protected void clearAllListeners()
    {
        for(IGameModeListener listener : _listeners)
        {
            listener.onClearOutput();
        }
    }
}
