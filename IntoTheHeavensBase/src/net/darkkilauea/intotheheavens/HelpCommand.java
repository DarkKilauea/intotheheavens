/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author joshua
 */
public class HelpCommand extends Command 
{
    Pattern _regex = Pattern.compile("^help\\s*(\\w+)?\\s*$", Pattern.CASE_INSENSITIVE);
    
    public HelpCommand(String name)
    {
        super(name);
    }
    
    @Override
    public boolean willHandleCommandString(String commandText)
    {
        return _regex.matcher(commandText).matches();
    }
    
    @Override
    public boolean parseCommandString(String commandText)
    {
        super.parseCommandString(commandText);
        
        Matcher matcher = _regex.matcher(commandText);
        if(matcher.matches())
        {
            String parameter = matcher.group(1);
            if(parameter != null) _parameters.put("Command", parameter);
            
            return true;
        }
        
        return false;
    }
    
    @Override
    public String getDescription()
    {
        return "Lists all commands with descriptions or more detail about a single command.";
    }
    
    @Override
    public String getHelpText()
    {
        return "Usage: help <command>";
    }
}
