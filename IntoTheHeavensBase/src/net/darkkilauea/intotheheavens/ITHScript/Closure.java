/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.ITHScript;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author joshua
 */
public class Closure 
{
    private int LITERAL_KEY =       0x00000001;
    private int GLOBAL_VAR_KEY =    0x00000002;
    private int INSTRUCTION_KEY =   0x00000003;
    
    private List<Instruction> _instructions = new ArrayList<Instruction>();
    private List<ScriptObject> _literals = new ArrayList<ScriptObject>();
    private List<Variable> _locals = new ArrayList<Variable>();
    private List<Variable> _globals = new ArrayList<Variable>();
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

    public List<Variable> getGlobals() 
    {
        return _globals;
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
                if (local.getName().startsWith("$")) _globals.add(local);
                else _locals.add(local);
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
    
    protected int pushVariable(String name)
    {
        int pos = _stackLocals.size();
        Variable local = new Variable(name);
        _stackLocals.add(local);
        return pos;
    }
    
    protected int getVariable(String name)
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
    
    protected boolean isVariable(int pos)
    {
        if (pos >= _stackLocals.size()) return false;
        else if (_stackLocals.get(pos).getName() != null) return true;
        else return false;
    }
    
    public void saveToStream(OutputStream stream) throws IOException
    {
        DataOutputStream output = new DataOutputStream(stream);
        
        output.writeByte(LITERAL_KEY);
        output.writeInt(_literals.size());
        for (ScriptObject literal : _literals) 
        {
            literal.saveToStream(output);
        }
        
        output.writeByte(GLOBAL_VAR_KEY);
        output.writeInt(_globals.size());
        for (Variable global : _globals) 
        {
            global.saveToStream(output);
        }
        
        output.writeByte(INSTRUCTION_KEY);
        output.writeInt(_instructions.size());
        for (Instruction instruction : _instructions) 
        {
            output.writeByte(instruction._op.value());
            output.writeInt(instruction._arg0);
            output.writeInt(instruction._arg1);
            output.writeInt(instruction._arg2);
            output.writeInt(instruction._arg3);
        }
    }
    
    public void loadFromStream(InputStream stream) throws IOException
    {
        DataInputStream input = new DataInputStream(stream);
        
        try
        {
            int token = input.readByte();
            while (token > 0)
            {
                if (token == LITERAL_KEY)
                {
                    int count = input.readInt();
                    for (int i = 0; i < count; i++)
                    {
                        ScriptObject obj = new ScriptObject();
                        obj.loadFromStream(input);
                        
                        _literals.add(obj);
                    }
                }
                else if (token == GLOBAL_VAR_KEY)
                {
                    int count = input.readInt();
                    for (int i = 0; i < count; i++)
                    {
                        Variable obj = new Variable("");
                        obj.loadFromStream(input);
                        
                        _globals.add(obj);
                    }
                }
                else if (token == INSTRUCTION_KEY)
                {
                    int count = input.readInt();
                    for (int i = 0; i < count; i++)
                    {
                        Instruction instruct = new Instruction(OpCode.OP_ERROR, 0, 0, 0, 0);
                        instruct._op = OpCode.enumForValue(input.readByte());
                        instruct._arg0 = input.readInt();
                        instruct._arg1 = input.readInt();
                        instruct._arg2 = input.readInt();
                        instruct._arg3 = input.readInt();
                        
                        _instructions.add(instruct);
                    }
                }

                token = input.readByte();
            }
        }
        catch (EOFException ex)
        {
            
        }
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
