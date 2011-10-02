/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.ITHScript;

/**
 *
 * @author joshua
 */
public final class Variable 
{
    private boolean _global = false;
    private int _intValue = 0;
    private float _floatValue = 0.0f;
    private String _stringValue = "";
    private String _name = null;
    
    public Variable(String name)
    {
        setName(name);
    }
    
    public Variable(String name, int value)
    {
        this(name);
        setValue(value);
    }
    
    public Variable(String name, float value)
    {
        this(name);
        setValue(value);
    }
    
    public Variable(String name, String value)
    {
        this(name);
        setValue(value);
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
    
    public void setValue(int value)
    {
        _intValue = value;
        _floatValue = (float)value;
        _stringValue = Integer.toString(value);
    }
    
    public void setValue(float value)
    {
        _intValue = (int)value;
        _floatValue = value;
        _stringValue = Float.toString(value);
    }
    
    public void setValue(String value)
    {
        try
        {
            _intValue = Integer.parseInt(value);
        }
        catch(Exception ex)
        {
            _intValue = 0;
        }
        
        try
        {
            _floatValue = Float.parseFloat(value);
        }
        catch(Exception ex)
        {
            _floatValue = 0;
        }
        
        _stringValue = value;
    }
    
    public int getIntegerValue()
    {
        return _intValue;
    }
    
    public float getFloatValue()
    {
        return _floatValue;
    }
    
    public String getStringValue()
    {
        return _stringValue;
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
