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
    private List<StatementBlock> _commandHandlers = new ArrayList<StatementBlock>();
    private List<StatementBlock> _eventHandlers = new ArrayList<StatementBlock>();

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
    public List<StatementBlock> getCommandHandlers() 
    {
        return _commandHandlers;
    }
    
    /**
     * Get the value of eventHandlers
     *
     * @return the value of eventHandlers
     */
    public List<StatementBlock> getEventHandlers() 
    {
        return _eventHandlers;
    }
    
    public StatementBlock getCommandHandler(String commandName)
    {
        StatementBlock item = null;
        for(StatementBlock handler : _commandHandlers)
        {
            if(handler.getName().equals(commandName))
            {
                item = handler;
                break;
            }
        }
        
        return item;
    }
    
    public StatementBlock getEventHandler(String eventName)
    {
        StatementBlock item = null;
        for(StatementBlock handler : _eventHandlers)
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

    @Override
    public String toString() 
    {
        return "location(" + _name + ")";
    }
}
