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
public class CommandHandler 
{
    private String _name = null;
    private List<Statement> _statements = new ArrayList<Statement>();

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
     * Get the value of statements
     *
     * @return the value of statements
     */
    public List<Statement> getStatements() 
    {
        return _statements;
    }
    
    public CommandHandler(String name) 
    {
        _name = name;
    }
    
}
