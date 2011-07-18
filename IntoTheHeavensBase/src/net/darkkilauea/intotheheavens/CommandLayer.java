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
public class CommandLayer 
{
    List<Command> _commands = new ArrayList<Command>();
    
    public void registerCommand(Command command)
    {
        _commands.add(command);
    }
    
    public void unregisterCommand(Command command)
    {
        _commands.remove(command);
    }
    
    public boolean checkCommandStringSupported(String commandText)
    {
        Command command = getCommandForCommandString(commandText);
        
        if(command != null) return true;
        else return false;
    }
    
    public Command getCommandForCommandString(String commandText)
    {
        for(Command command : _commands)
        {
            if(command.willHandleCommandString(commandText))
            {
                return command;
            }
        }
        
        return null;
    }
    
    public boolean executeCommand(String commandText)
    {
        Command command = getCommandForCommandString(commandText);
        
        if(command.parseCommandString(commandText)) 
        {
            return executeCommand(command);
        }
        
        return false;
    }
    
    public boolean executeCommand(Command command)
    {
        if(command == null) return false;
        
        command.execute();
        
        return true;
    }
}
