/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.ITHScript;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author joshua
 */
public class Location 
{
    private String _name = null;
    private List<CommandHandler> commandHandlers = new ArrayList<CommandHandler>();

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() 
    {
        return _name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name) 
    {
        _name = name;
    }
    
    /**
     * Get the value of commandHandlers
     *
     * @return the value of commandHandlers
     */
    public List<CommandHandler> getCommandHandlers() 
    {
        return commandHandlers;
    }

    public Location(String name)
    {
        _name = name;
    }
}
