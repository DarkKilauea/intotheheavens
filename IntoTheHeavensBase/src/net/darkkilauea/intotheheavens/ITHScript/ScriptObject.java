/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.ITHScript;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
    public static final int SOT_TABLE = 8;
    
    protected int _type;
    protected int _intValue;
    protected double _floatValue;
    protected String _stringValue;
    protected Map<ScriptObject, ScriptObject> _tableValue;
    
    protected ScriptObject(ScriptObject other)
    {
        this._type = other._type;
        this._intValue = other._intValue;
        this._floatValue = other._floatValue;
        this._stringValue = other._stringValue;
        this._tableValue = other._tableValue;
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
    
    protected ScriptObject(Map<ScriptObject, ScriptObject> value)
    {
        _type = SOT_TABLE;
        _tableValue = value;
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
            case SOT_TABLE:
                return "Table";
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
    
    public Map<ScriptObject, ScriptObject> toTable()
    {
        switch (_type)
        {
            case SOT_INTEGER:
            {
                Map<ScriptObject, ScriptObject> table = ScriptObject.newTable();
                table.put(new ScriptObject(0), new ScriptObject(_intValue));
                return table;
            }
            case SOT_FLOAT:
            {
                Map<ScriptObject, ScriptObject> table = ScriptObject.newTable();
                table.put(new ScriptObject(0), new ScriptObject(_floatValue));
                return table;
            }
            case SOT_STRING:
            {
                Map<ScriptObject, ScriptObject> table = ScriptObject.newTable();
                table.put(new ScriptObject(0), new ScriptObject(_stringValue));
                return table;
            }
            case SOT_TABLE:
                return _tableValue;
            default:
                return ScriptObject.newTable();
        }
    }
    
    public static Map<ScriptObject, ScriptObject> newTable()
    {
        return new HashMap<ScriptObject, ScriptObject>();
    }
    
    public void saveToStream(OutputStream stream) throws IOException
    {
        DataOutputStream output = new DataOutputStream(stream);
        
        output.write(_type);
        
        switch(_type)
        {
            case SOT_INTEGER:
                output.writeInt(_intValue);
                break;
            case SOT_FLOAT:
                output.writeDouble(_floatValue);
                break;
            case SOT_STRING:
                output.writeUTF(_stringValue);
                break;
            case SOT_TABLE:
                output.writeInt(_tableValue.size());
                for (Entry<ScriptObject, ScriptObject> pair : _tableValue.entrySet()) 
                {
                    pair.getKey().saveToStream(output);
                    pair.getValue().saveToStream(output);
                }
                break;
        }
    }
    
    public void loadFromStream(InputStream stream) throws IOException
    {
        DataInputStream input = new DataInputStream(stream);
        
        _type = input.read();
        
        switch(_type)
        {
            case SOT_INTEGER:
                _intValue = input.readInt();
                break;
            case SOT_FLOAT:
                _floatValue = input.readDouble();
                break;
            case SOT_STRING:
                _stringValue = input.readUTF();
                break;
            case SOT_TABLE:
                int count = input.readInt();
                for (int i = 0; i < count; i++) 
                {
                    ScriptObject key = new ScriptObject();
                    key.loadFromStream(input);
                    
                    ScriptObject value = new ScriptObject();
                    value.loadFromStream(input);
                    
                    _tableValue.put(key, value);
                }
                break;
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
                case SOT_TABLE:
                    return _tableValue.equals(other._tableValue);
                case SOT_NULL:
                    return true;
            }
        }
        
        return false;
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
            case SOT_TABLE:
            {
                StringBuilder output = new StringBuilder();
                output.append("{");
                for (Entry<ScriptObject, ScriptObject> pair : _tableValue.entrySet()) 
                {
                    if (pair.getKey()._type == SOT_STRING) 
                    {
                        output.append("\"");
                        output.append(pair.getKey().toString());
                        output.append("\"");
                    }
                    else output.append(pair.getKey().toString());
                    
                    output.append(": ");
                    
                    if (pair.getValue()._type == SOT_STRING) 
                    {
                        output.append("\"");
                        output.append(pair.getValue().toString());
                        output.append("\"");
                    }
                    else output.append(pair.getValue().toString());
                    
                    output.append(", ");
                }
                
                output.delete(output.length() - 2, output.length());
                
                output.append("}");
                
                return output.toString();
            }
            default:
                return "(null)";
        }
    }

    @Override
    public int hashCode() 
    {
        switch (_type)
        {
            case SOT_FLOAT:
                return ((Double)_floatValue).hashCode();
            case SOT_INTEGER:
                return ((Integer)_intValue).hashCode();
            case SOT_STRING:
                return _stringValue.hashCode();
            case SOT_TABLE:
                return _tableValue.hashCode();
            default:
                return 0;
        }
    }

    @Override
    public boolean equals(Object obj) 
    {
        if (obj == null) 
        {
            return false;
        }
        
        if (getClass() != obj.getClass()) 
        {
            return false;
        }
        
        return this.equals((ScriptObject)obj);
    }
}
