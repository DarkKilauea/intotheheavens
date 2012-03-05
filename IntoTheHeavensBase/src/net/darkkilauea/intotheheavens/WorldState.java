/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.darkkilauea.intotheheavens.ITHScript.*;

/**
 *
 * @author joshua
 */
public class WorldState 
{
    private static final int CUR_LOCATION_STREAM = 0x00000001;
    private static final int GLOBAL_VAR_VALUE_STREAM = 0x00000010;
    
    private static final int LOCATION_STREAM = 0x00000001;
    
    private Map<String, Location> _locations = new HashMap<String, Location>();
    private Map<String, Variable> _globals = new HashMap<String, Variable>();
    protected Location _currentLocation = null;

    /**
     * Get the value of locations
     *
     * @return the value of locations
     */
    public List<Location> getLocations() 
    {
        return new ArrayList<Location>(_locations.values());
    }
    
    public List<Variable> getGlobals()
    {
        return new ArrayList<Variable>(_globals.values());
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
        return _locations.get(name);
    }
    
    public Variable findGlobal(String name)
    {
        return _globals.get(name);
    }
    
    public void loadLocations(File locationDir) throws CompileException, IOException, Exception
    {
        //Load Archives first
        for(File file : locationDir.listFiles())
        {
            if(file.isFile() && file.getName().endsWith(".arc"))
            {
                loadLocationArchive(file);
            }
        }
        
        //Then load any text file locations, which will override archived locations
        for(File file : locationDir.listFiles())
        {
            if(file.isFile() && file.getName().endsWith(".loc"))
            {
                loadLocationFile(file);
            }
        }
    }
    
    public void loadLocationArchive(InputStream stream) throws IOException, Exception
    {
        GZIPInputStream compress = new GZIPInputStream(stream);
        DataInputStream input = new DataInputStream(compress);
                    
        int token = input.readByte();
        if (token == LOCATION_STREAM)
        {
            int count = input.readInt();
            for (int i = 0; i < count; i++)
            {
                Location location = new Location(null);
                location.loadFromStream(input);

                _locations.put(location.getName(), location);
                gatherGlobals(location);
            }
        }
        else throw new Exception("Expected Location Stream!");
    }
    
    public void loadLocationArchive(File file) throws IOException, Exception
    {
        FileInputStream stream = new FileInputStream(file);
        loadLocationArchive(stream);
        stream.close();
    }
    
    public void loadLocationFile(File file) throws CompileException, IOException
    {
        LocationFileParser parser = new LocationFileParser(file);
        parser.parseFile();
        
        for (Location location : parser.getLocations())
        {
            _locations.put(location.getName(), location);
            gatherGlobals(location);
        }
    }
    
    protected void gatherGlobals(Location location)
    {
        //Locate all of our globals
        for (Closure closure : location.getEventHandlers()) 
        {
            for (Variable local : closure.getGlobals())
            {
                _globals.put(local.getName(), local);
            }
        }

        for (Closure closure : location.getCommandHandlers()) 
        {
            for (Variable local : closure.getGlobals())
            {
                _globals.put(local.getName(), local);
            }
        }
    }
    
    public void archiveLocations(OutputStream stream) throws IOException
    {
        GZIPOutputStream compress = new GZIPOutputStream(stream);
        DataOutputStream output = new DataOutputStream(compress);
        
        output.write(LOCATION_STREAM);
        output.writeInt(_locations.size());
        for (Location location : _locations.values()) 
        {
            location.saveToStream(output);
        }
        
        output.flush();
        compress.finish();
    }
    
    public void saveState(OutputStream stream) throws IOException
    {
        DataOutputStream out = new DataOutputStream(stream);
        
        out.write(CUR_LOCATION_STREAM);
        out.writeInt(1);
        out.writeUTF(_currentLocation != null ? _currentLocation.getName() : "(null)");
        
        out.write(GLOBAL_VAR_VALUE_STREAM);
        out.writeInt(_globals.size());
        for (Variable var : _globals.values())
        {
            var.saveToStream(out);
        }
        
        out.flush();
    }
    
    public boolean loadState(InputStream stream) throws IOException
    {
        DataInputStream in = new DataInputStream(stream);
        
        try
        {
            int token = in.readByte();
            if (token == CUR_LOCATION_STREAM)
            {
                int count = in.readInt();
                for (int i = 0; i< count; i++)
                {
                    String locationName = in.readUTF();
                    if(locationName.equals("(null)")) _currentLocation = null;
                    else _currentLocation = findLocation(locationName);
                }
            }
            else throw new Exception("Expected Current Location Stream!");
            
            token = in.readByte();
            if (token == GLOBAL_VAR_VALUE_STREAM)
            {
                int count = in.readInt();
                for (int i = 0; i< count; i++)
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
                    else throw new Exception("Could not find variable in save file!");
                }
            }
            else throw new Exception("Expected Global Variable Value Stream!");
        }
        catch (Exception ex)
        {
            return false;
        }
        
        return true;
    }
}
