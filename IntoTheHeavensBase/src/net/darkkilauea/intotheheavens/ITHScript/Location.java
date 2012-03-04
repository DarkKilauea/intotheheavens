/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.ITHScript;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author joshua
 */
public class Location 
{
    private static final int EVENT_CLOSURE_STREAM =     0x00000001;
    private static final int COMMAND_CLOSURE_STREAM =   0x00000002;
    
    private String _name = null;
    private List<Closure> _commandHandlers = new ArrayList<Closure>();
    private List<Closure> _eventHandlers = new ArrayList<Closure>();

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
     * Get the value of commandHandlers
     *
     * @return the value of commandHandlers
     */
    public List<Closure> getCommandHandlers() 
    {
        return _commandHandlers;
    }
    
    /**
     * Get the value of eventHandlers
     *
     * @return the value of eventHandlers
     */
    public List<Closure> getEventHandlers() 
    {
        return _eventHandlers;
    }
    
    public Closure getCommandHandler(String commandName)
    {
        Closure item = null;
        for(Closure handler : _commandHandlers)
        {
            if(handler.getName().equalsIgnoreCase(commandName))
            {
                item = handler;
                break;
            }
        }
        
        return item;
    }
    
    public Closure getEventHandler(String eventName)
    {
        Closure item = null;
        for(Closure handler : _eventHandlers)
        {
            if(handler.getName().equalsIgnoreCase(eventName))
            {
                item = handler;
                break;
            }
        }
        
        return item;
    }

    public Location(String name)
    {
        _name = name;
    }
    
    public void loadFromStream(InputStream stream) throws IOException, Exception
    {
        DataInputStream input = new DataInputStream(stream);
        
        _name = input.readUTF();
        
        int token = input.readByte();
        if (token == EVENT_CLOSURE_STREAM)
        {
            int count = input.readInt();
            for (int i = 0; i< count; i++)
            {
                Closure closure = new Closure(this, "");
                closure.loadFromStream(input);

                _eventHandlers.add(closure);
            }
        }
        else throw new Exception("Expected Event Handler Stream!");
        
        token = input.readByte();
        if (token == COMMAND_CLOSURE_STREAM)
        {
            int count = input.readInt();
            for (int i = 0; i< count; i++)
            {
                Closure closure = new Closure(this, "");
                closure.loadFromStream(input);

                _commandHandlers.add(closure);
            }
        }
        else throw new Exception("Expected Command Handler Stream!");
    }
    
    public void saveToStream(OutputStream stream) throws IOException
    {
        DataOutputStream output = new DataOutputStream(stream);
        
        output.writeUTF(_name);
        
        output.write(EVENT_CLOSURE_STREAM);
        output.writeInt(_eventHandlers.size());
        for (Closure closure : _eventHandlers) 
        {
            closure.saveToStream(output);
        }
        
        output.write(COMMAND_CLOSURE_STREAM);
        output.writeInt(_commandHandlers.size());
        for (Closure closure : _commandHandlers) 
        {
            closure.saveToStream(output);
        }
    }

    @Override
    public String toString() 
    {
        return "location(" + _name + ")";
    }
}
