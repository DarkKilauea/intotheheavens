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
public class SaveGameCommand extends Command 
{
    Pattern _regex = Pattern.compile("^save_game\\s*(\\w+)?\\s*$", Pattern.CASE_INSENSITIVE);
    
    public SaveGameCommand(String name)
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
        return "Saves the game to the current save name, or another name if specified.";
    }
    
    @Override
    public String getHelpText()
    {
        return "Usage: save_game <game name>";
    }
}
