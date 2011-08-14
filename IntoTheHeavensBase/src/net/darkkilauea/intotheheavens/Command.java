/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author joshua
 */
public class Command 
{
    protected List<String> _parameters = new ArrayList<String>();
    protected String _name = "";
    protected Pattern _regex = null;
    protected String _description = "";
    protected String _usageHelp = "";
    
    public Command()
    {
        
    }
    
    public Command(String name, String regex, String description, String usageHelp)
    {
        _name = name;
        _regex = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        _description = description;
        _usageHelp = usageHelp;
    }
    
    public boolean willHandleCommandString(String commandText)
    {
        return _regex.matcher(commandText).matches();
    }
    
    public boolean parseCommandString(String commandText)
    {
        Matcher matcher = _regex.matcher(commandText);
        if(matcher.matches())
        {
            int count = matcher.groupCount();
            for(int i=0; i<count; i++)
            {
                String groupValue = matcher.group(i + 1);
                if(groupValue != null) _parameters.add(groupValue);
            }
            
            return true;
        }
        
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
    
    public List<String> getParameters()
    {
        return _parameters;
    }
    
    public String getDescription()
    {
        return _description;
    }
    
    public String getUsageHelp()
    {
        return _usageHelp;
    }
}
