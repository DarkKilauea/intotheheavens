/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.ITHScript;

/**
 *
 * @author joshua
 */
public class ScriptObject 
{
    public static final int SOT_NULL = 0;
    public static final int SOT_INTEGER = 1;
    public static final int SOT_FLOAT = 2;
    public static final int SOT_STRING = 4;
    
    protected int _type;
    protected int _intValue;
    protected double _floatValue;
    protected String _stringValue;
    
    protected ScriptObject(ScriptObject other)
    {
        this._type = other._type;
        this._intValue = other._intValue;
        this._floatValue = other._floatValue;
        this._stringValue = other._stringValue;
    }
    
    protected ScriptObject()
    {
        _type = SOT_NULL;
    }
    
    protected ScriptObject(int value)
    {
        _type = SOT_INTEGER;
        _intValue = value;
    }
    
    protected ScriptObject(double value)
    {
        _type = SOT_FLOAT;
        _floatValue = value;
    }
    
    protected ScriptObject(String value)
    {
        _type = SOT_STRING;
        _stringValue = value;
    }
    
    public String typeString()
    {
        switch (_type)
        {
            case SOT_NULL:
                return "NULL";
            case SOT_INTEGER:
                return "Integer";
            case SOT_FLOAT:
                return "Float";
            case SOT_STRING:
                return "String";
            default:
                return "UNKNOWN";
        }
    }
    
    public int toInt()
    {
        switch (_type)
        {
            case SOT_INTEGER:
                return _intValue;
            case SOT_FLOAT:
                return (int)_floatValue;
            case SOT_STRING:
                return Integer.parseInt(_stringValue);
            default:
                return 0;
        }
    }
    
    public double toFloat()
    {
        switch (_type)
        {
            case SOT_INTEGER:
                return (double)_intValue;
            case SOT_FLOAT:
                return _floatValue;
            case SOT_STRING:
                return Double.parseDouble(_stringValue);
            default:
                return 0.0;
        }
    }
    
    @Override
    public String toString()
    {
        switch (_type)
        { 
            case SOT_INTEGER:
                return ((Integer)_intValue).toString();
            case SOT_FLOAT:
                return ((Double)_floatValue).toString();
            case SOT_STRING:
                return _stringValue;
            default:
                return "(null)";
        }
    }
    
    public boolean equals(ScriptObject other)
    {
        if (_type == other._type)
        {
            switch (_type)
            {
                case SOT_FLOAT:
                    return _floatValue == other._floatValue;
                case SOT_INTEGER:
                    return _intValue == other._intValue;
                case SOT_STRING:
                    return _stringValue.equals(other._stringValue);
                case SOT_NULL:
                    return true;
            }
        }
        
        return false;
    }
}
