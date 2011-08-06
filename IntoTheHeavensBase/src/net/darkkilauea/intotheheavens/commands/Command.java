/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author joshua
 */
public class Command 
{
    protected Map<String, Object> _parameters = new HashMap<String, Object>();
    protected List<ICommandListener> _listeners = new ArrayList<ICommandListener>();
    protected String _name = null;
    
    public Command(String name)
    {
        _name = name;
    }
    
    public boolean willHandleCommandString(String commandText)
    {
        return false;
    }
    
    public boolean parseCommandString(String commandText)
    {
        _parameters.clear();
        
        return false;
    }
    
    public String getName()
    {
        return _name;
    }
    
    public void setName(String name)
    {
        _name = name;
    }
    
    public Map<String, Object> getParameters()
    {
        return _parameters;
    }
    
    public void setParameters(Map<String, Object> parameters)
    {
        _parameters = parameters;
    }
    
    public boolean registerListener(ICommandListener listener)
    {
        return _listeners.add(listener);
    }
    
    public boolean unregisterListener(ICommandListener listener)
    {
        return _listeners.remove(listener);
    }
    
    public void execute()
    {
        for(ICommandListener listener : _listeners)
        {
            listener.onCommandExecuted(this);
        }
    }
    
    public String getDescription()
    {
        return "";
    }
    
    public String getHelpText()
    {
        return "";
    }
}
