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
    protected List<Command> _commands = new ArrayList<Command>();
    
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
    
    public Command getCommandForName(String name)
    {
        Command item = null;
        for(Command command : _commands)
        {
            if(command.getName().equalsIgnoreCase(name))
            {
                item = command;
                break;
            }
        }
        
        return item;
    }
    
    public Command getCommandThatHandlesString(String text)
    {
        Command item = null;
        for(Command command : _commands)
        {
            if(command.willHandleCommandString(text))
            {
                item = command;
                break;
            }
        }
        
        return item;
    }
    
    protected void printToAllListeners(String message)
    {
        for(IGameModeListener listener : _listeners)
        {
            listener.onTextOutput(message);
        }
    }
}
