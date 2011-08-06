/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author joshua
 */
public class NewGameCommand extends Command 
{
    Pattern _regex = Pattern.compile("^new_game\\s*(\\w+)?\\s*$", Pattern.CASE_INSENSITIVE);
    
    public NewGameCommand(String name)
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
            if(parameter != null) _parameters.put("Name", parameter);
            
            return true;
        }
        
        return false;
    }
    
    @Override
    public String getDescription()
    {
        return "Starts a new game, will create an initial save if a save name is specified.";
    }
    
    @Override
    public String getHelpText()
    {
        return "Usage: new_game <game name>";
    }
}
