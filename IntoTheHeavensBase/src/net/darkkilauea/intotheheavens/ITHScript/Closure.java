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
public class Closure 
{
    private List<Instruction> _instructions = new ArrayList<Instruction>();
    private List<ScriptObject> _literals = new ArrayList<ScriptObject>();
    private List<Variable> _locals = new ArrayList<Variable>();
    private String _name = null;
    private Location _location = null;

    protected List<Instruction> getInstructions() 
    {
        return _instructions;
    }
    
    protected List<ScriptObject> getLiterals()
    {
        return _literals;
    }

    protected List<Variable> getLocals() 
    {
        return _locals;
    }

    public String getName() 
    {
        return _name;
    }
    
    public Location getLocation()
    {
        return _location;
    }
    
    protected Closure(Location location, String name)
    {
        _location = location;
        _name = name;
    }
    
    @Override
    public String toString()
    {
        return toString(false);
    }
    
    public String toString(boolean fullDump)
    {
        StringBuilder builder = new StringBuilder();
        
        builder.append("=====Closure=====\n");
        builder.append("Name: ").append(_name).append('\n');
        builder.append("Location: ").append(_location.getName()).append('\n');
        builder.append('\n');
        
        if (fullDump)
        {
            builder.append("*****Literals\n");
            for (ScriptObject o : _literals) 
            {
                builder.append("TYPE: ").append(o.typeString()).append(" VALUE:")
                        .append(o.toString()).append('\n');
            }
            builder.append('\n');
            
            builder.append("*****Locals\n");
            for (Variable o : _locals) 
            {
                builder.append("NAME: ").append(o.getName())
                        .append(" TYPE: ").append(o.typeString())
                        .append(" VALUE:").append(o.toString()).append('\n');
            }
            builder.append('\n');
            
            builder.append("*****Instructions\n");
            for (Instruction instruction : _instructions) 
            {
                builder.append(instruction).append('\n');
            }
            builder.append('\n');
        }
        
        return builder.toString();
    }
}
