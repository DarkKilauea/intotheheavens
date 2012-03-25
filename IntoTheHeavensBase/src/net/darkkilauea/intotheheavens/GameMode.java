/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens;

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
    protected IGameModeListener _listener = null;
    
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
    
    public IGameModeListener getListener()
    {
        return _listener;
    }
    
    public void setListener(IGameModeListener listener)
    {
        _listener = listener;
    }
    
    public void injectTextInput(String input)
    {
        
    }
    
    protected void printToAllListeners(String message)
    {
        _listener.onTextOutput(message);
    }
    
    protected void clearAllListeners()
    {
        _listener.onClearOutput();
    }
}
