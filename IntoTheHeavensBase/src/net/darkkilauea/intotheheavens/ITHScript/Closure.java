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
    private List<Variable> _stackLocals = new ArrayList<Variable>();
    private List<Integer> _targetStack = new ArrayList<Integer>();
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
    
    protected List<Variable> getStackLocals() 
    {
        return _stackLocals;
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
    
    protected int addStackLocal()
    {
        int pos = _stackLocals.size();
        _stackLocals.add(new Variable(null));
        
        return pos;
    }
    
    protected int pushTarget(int pos)
    {
        if (pos == -1) pos = addStackLocal();
        
        _targetStack.add(pos);
        return pos;
    }
    
    protected int getTarget(int pos)
    {
        return _targetStack.get(_targetStack.size() - 1 - pos);
    }
    
    protected int topTarget()
    {
        return getTarget(0);
    }
    
    protected int popTarget()
    {
        int pos = topTarget();
        Variable local = _stackLocals.get(pos);
        if (local.getName() == null) _stackLocals.remove(_stackLocals.size() - 1);
        _targetStack.remove(_targetStack.size() - 1);
        
        return pos;
    }
    
    protected int getStackSize()
    {
        return _stackLocals.size();
    }
    
    protected void setStackSize(int newSize)
    {
        int size = getStackSize();
        
        while (size > newSize)
        {
            size--;
            Variable local = _stackLocals.get(_stackLocals.size() - 1);
            if (local.getName() != null)
            {
                _locals.add(local);
            }
            
            _stackLocals.remove(local);
        }
    }
    
    protected int getCurrentInstructionPos()
    {
        return _instructions.size() - 1;
    }
    
    protected int getOrCreateLiteral(ScriptObject o)
    {
        int location = -1;
        
        for (int i = 0; i < _literals.size(); i++)
        {
            if (_literals.get(i).equals(o))
            {
                location = i;
                break;
            }
        }
        
        if (location < 0)
        {
            location = _literals.size();
            _literals.add(o);
        }
        
        return location;
    }
    
    protected int pushLocalVariable(String name)
    {
        int pos = _stackLocals.size();
        Variable local = new Variable(name);
        _stackLocals.add(local);
        return pos;
    }
    
    protected int getLocalVariable(String name)
    {
        int pos = _stackLocals.size() - 1;
        while (pos >= 0)
        {
            Variable local = _stackLocals.get(pos);
            if (name.equals(local.getName())) 
            {
                return pos;
            }
            
            pos--;
        }
        
        return -1;
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
