/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import net.darkkilauea.intotheheavens.ITHScript.*;

/**
 *
 * @author joshua
 */
public class WorldState 
{
    private static int CUR_LOCATION_TOKEN = 0x00000001;
    private static int GLOBAL_VAR_VALUE_TOKEN = 0x00000010;
    
    private List<Location> _locations = new ArrayList<Location>();
    private List<Variable> _globals = new ArrayList<Variable>();
    protected Location _currentLocation = null;

    /**
     * Get the value of locations
     *
     * @return the value of locations
     */
    public List<Location> getLocations() 
    {
        return _locations;
    }
    
    public List<Variable> getGlobals()
    {
        return _globals;
    }
    
    /**
     * Get the value of currentLocation
     *
     * @return the value of currentLocation
     */
    public Location getCurrentLocation() 
    {
        return _currentLocation;
    }

    /**
     * Set the value of currentLocation
     *
     * @param currentLocation new value of currentLocation
     */
    public void setCurrentLocation(Location location) 
    {
        _currentLocation = location;
    }
    
    public Location findLocation(String name) 
    {
        for(Location loc : _locations)
        {
            if(loc.getName().equals(name))
            {
                return loc;
            }
        }
        
        return null;
    }
    
    public Variable findGlobal(String name)
    {
        for(Variable var : _globals)
        {
            if(var.getName().equals(name))
            {
                return var;
            }
        }
        
        return null;
    }
    
    public void loadLocations(String locationDir) throws CompileException, IOException
    {
        File gameDataRoot = new File(locationDir);
        for(File file : gameDataRoot.listFiles())
        {
            if(file.isFile() && file.getName().endsWith(".loc"))
            {
                LocationFileParser parser = new LocationFileParser(file.getPath());
                parser.parseFile();
                _locations.addAll(parser.getLocations());
            }
        }
        
        //Locate all of our globals
        for (Location location : _locations) 
        {
            for (Closure closure : location.getEventHandlers()) 
            {
                gatherGlobalsFromClosure(closure);
            }

            for (Closure closure : location.getCommandHandlers()) 
            {
                gatherGlobalsFromClosure(closure);
            }
        }
    }
    
    public void saveState(OutputStream stream) throws IOException
    {
        DataOutputStream out = new DataOutputStream(stream);
        
        out.write(CUR_LOCATION_TOKEN);
        out.writeUTF(_currentLocation != null ? _currentLocation.getName() : "(null)");
        
        for (Variable var : _globals)
        {
            out.write(GLOBAL_VAR_VALUE_TOKEN);
            var.saveToStream(out);
        }
        
        out.flush();
    }
    
    public boolean loadState(InputStream stream) throws IOException
    {
        DataInputStream in = new DataInputStream(stream);
        
        try
        {
            int token = in.read();
            while (token > 0)
            {
                if (token == CUR_LOCATION_TOKEN)
                {
                    String locationName = in.readUTF();
                    if(locationName.equals("(null)")) _currentLocation = null;
                    else _currentLocation = findLocation(locationName);
                }
                else if (token == GLOBAL_VAR_VALUE_TOKEN)
                {
                    String globalName = in.readUTF();
                    Variable global = findGlobal(globalName);
                    if (global != null)
                    {
                        int varType = in.read();
                        if (varType == ScriptObject.SOT_STRING) global.setValue(in.readUTF());
                        else if (varType == ScriptObject.SOT_INTEGER) global.setValue(in.readInt());
                        else if (varType == ScriptObject.SOT_FLOAT) global.setValue(in.readDouble());
                        else global.setNull();
                    }
                    else return false;
                }
                else return false;
                
                token = in.read();
            }
        }
        catch (EOFException ex)
        {
            
        }
        
        return true;
    }
    
    private void gatherGlobalsFromClosure(Closure closure)
    {
        for (Variable local : closure.getGlobals())
        {
            boolean exists = false;
            for (Variable global : _globals) 
            {
                if (global.getName().equals(local.getName()))
                {
                    exists = true;
                    break;
                }
            }

            if (!exists)
            {
                _globals.add(local);
            }
        }
    }
}
