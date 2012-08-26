/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.ITHScript;

import java.io.*;
import java.util.Map;

/**
 *
 * @author joshua
 */
public final class Variable extends ScriptObject
{
    private boolean _global = false;
    private String _name = null;
    
    public Variable(String name)
    {
        super();
        setName(name);
    }
    
    public Variable(String name, int value)
    {
        super(value);
        setName(name);
    }
    
    public Variable(String name, double value)
    {
        super(value);
        setName(name);
    }
    
    public Variable(String name, String value)
    {
        super(value);
        setName(name);
    }
    
    public Variable(String name, Map<ScriptObject, ScriptObject> value)
    {
        super(value);
        setName(name);
    }
    
    protected Variable(String name, ScriptObject object)
    {
        super(object);
        setName(name);
    }

    public boolean isGlobal() 
    {
        return _global;
    }

    public void setGlobal(boolean global) 
    {
        _global = global;
    }
    
    public void setName(String name)
    {
        _name = name;
    }
    
    public String getName()
    {
        return _name;
    }
    
    public int getType()
    {
        return _type;
    }
    
    public void setValue(int value)
    {
        _type = ScriptObject.SOT_INTEGER;
        _intValue = value;
        _floatValue = 0;
        _stringValue = null;
        _tableValue = null;
    }
    
    public void setValue(double value)
    {
        _type = ScriptObject.SOT_FLOAT;
        _intValue = 0;
        _floatValue = value;
        _stringValue = null;
        _tableValue = null;
    }
    
    public void setValue(String value)
    {
        _type = ScriptObject.SOT_STRING;
        _intValue = 0;
        _floatValue = 0;
        _stringValue = value;
        _tableValue = null;
    }
    
    public void setValue(Map<ScriptObject, ScriptObject> value)
    {
        _type = ScriptObject.SOT_TABLE;
        _intValue = 0;
        _floatValue = 0;
        _stringValue = null;
        _tableValue = value;
    }
    
    public boolean isNull()
    {
        return _type == ScriptObject.SOT_NULL;
    }
    
    public void setNull()
    {
        _type = ScriptObject.SOT_NULL;
        _intValue = 0;
        _floatValue = 0;
        _stringValue = null;
        _tableValue = null;
    }
    
    public void copyTo(Variable other)
    {
        other._global = this._global;
        other._name = this._name;
        other._floatValue = this._floatValue;
        other._intValue = this._intValue;
        other._stringValue = this._stringValue;
        other._tableValue = this._tableValue;
    }
    
    @Override
    public void saveToStream(OutputStream stream) throws IOException
    {
        DataOutputStream output = new DataOutputStream(stream);
        
        output.writeUTF(_name);
        super.saveToStream(stream);
    }
    
    @Override
    public void loadFromStream(InputStream stream) throws IOException
    {
        DataInputStream input = new DataInputStream(stream);
        
        _name = input.readUTF();
        super.loadFromStream(stream);
    }
}
