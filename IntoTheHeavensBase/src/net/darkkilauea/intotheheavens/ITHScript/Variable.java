/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.ITHScript;

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
    }
    
    public void setValue(double value)
    {
        _type = ScriptObject.SOT_FLOAT;
        _floatValue = value;
        _intValue = 0;
        _stringValue = null;
    }
    
    public void setValue(String value)
    {
        _type = ScriptObject.SOT_STRING;
        _stringValue = value;
        _intValue = 0;
        _floatValue = 0;
    }
    
    public boolean IsNull()
    {
        return _type == ScriptObject.SOT_NULL;
    }
    
    public void setNull()
    {
        _type = ScriptObject.SOT_NULL;
        _stringValue = null;
        _intValue = 0;
        _floatValue = 0;
    }
    
    public void copyTo(Variable other)
    {
        other._global = this._global;
        other._name = this._name;
        other._floatValue = this._floatValue;
        other._intValue = this._intValue;
        other._stringValue = this._stringValue;
    }
}
