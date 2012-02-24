/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens;

import java.util.ArrayList;
import java.util.List;
import net.darkkilauea.intotheheavens.ITHScript.*;

/**
 *
 * @author joshua
 */
public class MainGameMode extends GameMode implements IVirtualMachineListener
{
    private WorldState _world = new WorldState();
    private VirtualMachine _vm = new VirtualMachine();
    
    public MainGameMode()
    {
        
    }
    
    @Override
    public boolean initialize(GameModeManager manager)
    {
        super.initialize(manager);
        
        _vm.registerListener(this);
        
        return true;
    }
    
    @Override
    public void resume()
    {
        super.resume();
    }
    
    @Override
    public void pause()
    {
        super.pause();
    }
    
    @Override
    public void shutdown()
    {
        _vm.unregisterListener(this);
        
        super.shutdown();
    }
    
    @Override
    public void injectTextInput(String input)
    {
        try
        {
            String[] splitInput = input.split("\\s");
            List<Variable> args = new ArrayList<Variable>();
            for (int i = 0; i < splitInput.length; i++) 
            {
                Variable arg = new Variable("$arg" + i, splitInput[i].toLowerCase());
                
                try
                {
                    arg.setValue(arg.toFloat());
                }
                catch (Exception ex) {}
                
                try
                {
                    arg.setValue(arg.toInt());
                }
                catch (Exception ex) {}
                
                args.add(arg);
            }
            
            executeCommandHandler(args.get(0).toString(), args);
        }
        catch(ScriptException ex)
        {
            printToAllListeners(ex.getMessage());
        }
        catch(Exception ex)
        {
            printToAllListeners(ex.toString());
        }
    }
    
    public void loadFromWorldState(WorldState world)
    {
        _world = world;
        _vm.setGlobals(_world.getGlobals());
        
        try
        {
            Location curLocation = _world.getCurrentLocation();
            if(curLocation != null)
            {
                ArrayList<Variable> args = new ArrayList<Variable>(2);
                args.add(new Variable("$arg0", "OnEnter"));
                args.add(new Variable("$arg1"));

                executeEventHandler("OnEnter", args);
            }
            else throw new Exception("Starting location could not be found!");
        }
        catch(ScriptException ex)
        {
            printToAllListeners(ex.getMessage());
        }
        catch(Exception ex)
        {
            printToAllListeners(ex.toString());
        }
    }
    
    public WorldState getWorldState()
    {
        return _world;
    }
    
    private void executeCommandHandler(String name, List<Variable> args) throws ScriptException, Exception
    {
        Location curLocation = _world.getCurrentLocation();
        if(curLocation != null)
        {
            Closure handler = curLocation.getCommandHandler(name);
            if(handler != null) _vm.executeClosure(handler, args);
            else
            {
                Location defaultLoc = _world.findLocation("__DEFAULT__");
                if (defaultLoc != null)
                {
                    handler = defaultLoc.getCommandHandler(name);
                    if(handler != null) _vm.executeClosure(handler, args);
                    else
                    {
                        handler = defaultLoc.getCommandHandler("__UNHANDLED__");
                        if (handler != null) _vm.executeClosure(handler, args);
                        else throw new Exception("Could not find a handler for command '" + name + "'!");
                    }
                }
                else throw new Exception("Could not find a handler for command '" + name + "'!");
            }
        }
        else throw new Exception("Cannot execute a command outside of a location!");
    }
    
    private void executeEventHandler(String name, List<Variable> args) throws ScriptException
    {
        Location curLocation = _world.getCurrentLocation();
        if(curLocation != null)
        {
            Closure handler = curLocation.getEventHandler(name);
            if(handler != null) _vm.executeClosure(handler, args);
            else
            {
                Location defaultLoc = _world.findLocation("__DEFAULT__");
                if (defaultLoc != null)
                {
                    handler = defaultLoc.getEventHandler(name);
                    if(handler != null) _vm.executeClosure(handler, args);
                    else
                    {
                        handler = defaultLoc.getEventHandler("__UNHANDLED__");
                        if (handler != null) _vm.executeClosure(handler, args);
                    }
                }
            }
        }
    }

    @Override
    public void onInvokePrint(String message) 
    {
        try
        {
            printToAllListeners(message);
        }
        /*catch(ScriptException ex)
        {
            printToAllListeners(ex.getMessage());
        }*/
        catch(Exception ex)
        {
            printToAllListeners(ex.toString());
        }
    }

    @Override
    public void onInvokeGoto(String locationName) 
    {
        try
        {
            String oldLocationName = "";
            Location curLocation = _world.getCurrentLocation();
            if(curLocation != null)
            {
                oldLocationName = curLocation.getName();
                
                ArrayList<Variable> args = new ArrayList<Variable>(2);
                args.add(new Variable("$arg0", "OnLeave"));
                args.add(new Variable("$arg1", locationName));
                
                executeEventHandler("OnLeave", args);
            }

            Location newLocation = _world.findLocation(locationName);
            if(newLocation != null)
            {
                clearAllListeners();
                
                ArrayList<Variable> args = new ArrayList<Variable>(2);
                args.add(new Variable("$arg0", "OnEnter"));
                args.add(new Variable("$arg1", oldLocationName));
                
                _world.setCurrentLocation(newLocation);
                executeEventHandler("OnEnter", args);
            }
            else throw new ScriptException("Location \"" + locationName + "\" could not be found.");
        }
        catch(ScriptException ex)
        {
            printToAllListeners(ex.getMessage());
        }
        catch(Exception ex)
        {
            printToAllListeners(ex.toString());
        }
    }
}
