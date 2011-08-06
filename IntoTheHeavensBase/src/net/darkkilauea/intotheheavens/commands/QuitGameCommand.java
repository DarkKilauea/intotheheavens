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
public class QuitGameCommand extends Command 
{
    Pattern _regex = Pattern.compile("^(?:quit|exit)\\s*$", Pattern.CASE_INSENSITIVE);
    
    public QuitGameCommand(String name)
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
            return true;
        }
        
        return false;
    }
    
    @Override
    public String getDescription()
    {
        return "Quits the current game.  If in the menu, also exits the application.";
    }
    
    @Override
    public String getHelpText()
    {
        return "Usage: quit|exit";
    }
}
