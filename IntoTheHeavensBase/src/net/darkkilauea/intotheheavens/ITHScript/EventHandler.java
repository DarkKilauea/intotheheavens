/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.ITHScript;

/**
 *
 * @author joshua
 */
public class EventHandler extends StatementBlock
{
    private String _name = null;
    
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
    
    public EventHandler(String name) 
    {
        _name = name;
    }
}
