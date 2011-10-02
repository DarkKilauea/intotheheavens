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

        _commands.add(new Command("Help", 
                                  "^help\\s*(\\w+)?\\s*$", 
                                  "Lists all commands with descriptions or more detail about a single command.", 
                                  "Usage: help <command>"));
        _commands.add(new Command("North", 
                                  "^north\\s*$", 
                                  "Travels north of your current location.", 
                                  "Usage: north"));
        _commands.add(new Command("East", 
                                  "^east\\s*$", 
                                  "Travels east of your current location.", 
                                  "Usage: east"));
        _commands.add(new Command("South", 
                                  "^south\\s*$", 
                                  "Travels south of your current location.", 
                                  "Usage: south"));
        _commands.add(new Command("West", 
                                  "^west\\s*$", 
                                  "Travels west of your current location.", 
                                  "Usage: west"));
        
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
            Command command = getCommandThatHandlesString(input);
            if(command != null)
            {
                if(command.parseCommandString(input))
                {
                    onCommandExecuted(command);
                }
                else printToAllListeners("Incorrect syntax.  Type \"help <command>\" for details.");
            }
            else
            {
                onCommandExecuted(new Command());
            }
        }
        catch (Exception ex) 
        {
            printToAllListeners("Exception Caught: " + ex.toString());
        }
    }

    public void onCommandExecuted(Command command) 
    {
        if(command.getName().equalsIgnoreCase("Help"))
        {
            String output = "";
            if(command.getParameters().size() > 0)
            {
                String commandName = (String)command.getParameters().get(0);
                Command target = getCommandForName(commandName);
                if(target != null) 
                    output = target.getUsageHelp() + "\n" + "Description: " + target.getDescription();
                else 
                    output = "No command of that name could be found, type \"help\" for a list of available commands.";
            }
            else
            {
                output = "List of available commands: \n\n";

                for(Command aCommand : _commands)
                {
                    output += aCommand.getName() + ": " + aCommand.getDescription() + "\n";
                }
                
                output = output.substring(0, output.length() - 1);
            }
            
            printToAllListeners(output);
        }
        else if (command.getName().equalsIgnoreCase("North"))
        {
            executeCommandHandler("North", new ArrayList<Variable>());
        }
        else if (command.getName().equalsIgnoreCase("East"))
        {
            executeCommandHandler("East", new ArrayList<Variable>());
        }
        else if (command.getName().equalsIgnoreCase("South"))
        {
            executeCommandHandler("South", new ArrayList<Variable>());
        }
        else if (command.getName().equalsIgnoreCase("West"))
        {
            executeCommandHandler("West", new ArrayList<Variable>());
        }
        else
        {
            printToAllListeners("Command not recognized, type \"help\" for a list of available commands.");
        }
    }
    
    private void executeCommandHandler(String name, List<Variable> arguments)
    {
        try
        {
            Location curLocation = _world.getCurrentLocation();
            if(curLocation != null)
            {
                StatementBlock handler = curLocation.getCommandHandler(name);
                if(handler != null) _vm.executeStatementBlock(handler, arguments);
            }
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
        
        try
        {
            Location curLocation = _world.getCurrentLocation();
            if(curLocation != null)
            {
                ArrayList<Variable> args = new ArrayList<Variable>(1);
                args.add(new Variable("prevLocation", ""));

                StatementBlock onEnter = curLocation.getEventHandler("OnEnter");
                if(onEnter != null) _vm.executeStatementBlock(onEnter, args);
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

    @Override
    public void onInvokePrint(PrintStatement statement) 
    {
        try
        {
            printToAllListeners(statement.getMessage());
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
    public void onInvokeGoto(GotoStatement statement) 
    {
        try
        {
            String oldLocationName = "";
            Location curLocation = _world.getCurrentLocation();
            if(curLocation != null)
            {
                oldLocationName = curLocation.getName();
                StatementBlock onLeave = curLocation.getEventHandler("OnLeave");
                if(onLeave != null) 
                {
                    ArrayList<Variable> args = new ArrayList<Variable>(1);
                    args.add(new Variable("nextLocation", statement.getLocationName()));

                    _vm.executeStatementBlock(onLeave, args);
                }
            }

            Location newLocation = _world.findLocation(statement.getLocationName());
            if(newLocation != null)
            {
                clearAllListeners();

                StatementBlock onEnter = newLocation.getEventHandler("OnEnter");
                if(onEnter != null)
                {
                    ArrayList<Variable> args = new ArrayList<Variable>(1);
                    args.add(new Variable("prevLocation", oldLocationName));

                    _vm.executeStatementBlock(onEnter, args);
                }

                _world.setCurrentLocation(newLocation);
            }
            else throw new ScriptException("Location \"" + statement.getLocationName() + "\" could not be found.", statement);
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
