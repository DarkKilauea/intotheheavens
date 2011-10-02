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
public class StatementBlock 
{
    protected String _name = null;
    protected StatementBlock _parent = null;
    protected Location _location = null;
    protected List<Statement> _statements = new ArrayList<Statement>();
    protected List<Variable> _locals = new ArrayList<Variable>();
    
    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() 
    {
        return _name;
    }
    
    public StatementBlock getParent() 
    {
        return _parent;
    }
    
    public Location getLocation()
    {
        return _location;
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

    public List<Variable> getLocals() 
    {
        return _locals;
    }
    
    public StatementBlock(StatementBlock parent, Location location, String name)
    {
        _parent = parent;
        _location = location;
        _name = name;
    }

    @Override
    public String toString() 
    {
        return "block(" + _name + ")";
    }
}
