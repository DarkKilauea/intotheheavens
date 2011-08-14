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
    private List<CommandHandler> _commandHandlers = new ArrayList<CommandHandler>();
    private List<EventHandler> _eventHandlers = new ArrayList<EventHandler>();

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
        return _commandHandlers;
    }
    
    /**
     * Get the value of eventHandlers
     *
     * @return the value of eventHandlers
     */
    public List<EventHandler> getEventHandlers() 
    {
        return _eventHandlers;
    }
    
    public CommandHandler getCommandHandler(String commandName)
    {
        CommandHandler item = null;
        for(CommandHandler handler : _commandHandlers)
        {
            if(handler.getName().equals(commandName))
            {
                item = handler;
                break;
            }
        }
        
        return item;
    }
    
    public EventHandler getEventHandler(String eventName)
    {
        EventHandler item = null;
        for(EventHandler handler : _eventHandlers)
        {
            if(handler.getName().equals(eventName))
            {
                item = handler;
                break;
            }
        }
        
        return item;
    }

    public Location(String name)
    {
        _name = name;
    }
}
