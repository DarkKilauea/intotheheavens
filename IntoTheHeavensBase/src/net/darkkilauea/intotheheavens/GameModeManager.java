/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author joshua
 */
public class GameModeManager 
{
    protected Map<String, GameMode> _modes = new HashMap<String, GameMode>();
    protected GameMode _activeMode = null;
    
    public boolean registerGameMode(String name, GameMode mode)
    {
        if(mode != null && mode.initialize(this))
        {
            _modes.put(name, mode);
            return true;
        }
        
        return false;
    }
    
    public void unregisterGameMode(String name)
    {
        if(_modes.containsKey(name)) 
        {
            _modes.get(name).shutdown();
            _modes.remove(name);
        }
    }
    
    public GameMode setActiveMode(String name)
    {
        GameMode oldMode = _activeMode;
        if(oldMode != null) oldMode.pause();
        
        if(_modes.containsKey(name))
        {
            _activeMode = _modes.get(name);
            _activeMode.resume();
        }
        else _activeMode = null;
        
        return oldMode;
    }
    
    public GameMode getActiveMode()
    {
        return _activeMode;
    }
    
    public GameMode getMode(String name)
    {
        return _modes.get(name);
    }
}
