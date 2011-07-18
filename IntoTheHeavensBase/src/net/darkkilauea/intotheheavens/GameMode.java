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
    GameModeManager _manager = null;
    
    public boolean initialize(GameModeManager manager)
    {
        _manager = manager;
        
        return false;
    }
    
    public void resume()
    {
        
    }
    
    public void pause()
    {
        
    }
    
    public void shutdown()
    {
        _manager = null;
    }
}
