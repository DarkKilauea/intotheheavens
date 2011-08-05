/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens;

import java.io.File;
import java.util.List;
import net.darkkilauea.intotheheavens.ITHScript.Location;
import net.darkkilauea.intotheheavens.ITHScript.LocationFileParser;

/**
 *
 * @author joshua
 */
public class MainGameMode extends GameMode implements ICommandListener
{
    private CommandLayer _commandLayer = new CommandLayer();
    private WorldState _world = new WorldState();
    private String _rootDir = "base\\";
    
    public MainGameMode(String rootDir)
    {
        _rootDir = rootDir;
    }
    
    @Override
    public boolean initialize(GameModeManager manager)
    {
        try
        {
            super.initialize(manager);

            Command helpCommand = new HelpCommand("Help");
            helpCommand.registerListener(this);

            _commandLayer.registerCommand(helpCommand);
            
            File gameDataRoot = new File(_rootDir);
            for(File file : gameDataRoot.listFiles())
            {
                if(file.isFile() && file.getName().endsWith(".txt"))
                {
                    LocationFileParser parser = new LocationFileParser(file.getPath());
                    List<Location> locations = parser.parseFile();
                    _world.getLocations().addAll(locations);
                }
            }
        }
        catch (Exception ex) 
        {
            for(IGameModeListener listener : _listeners)
            {
                listener.onTextOutput("Exception Caught: " + ex.toString());
            }
        }
        
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
        Command helpCommand = _commandLayer.getCommand("Help");
        helpCommand.unregisterListener(this);
        
        _commandLayer.unregisterCommand("Help");
        
        super.shutdown();
    }
    
    @Override
    public void injectTextInput(String input)
    {
        try
        {
            if(_commandLayer.checkCommandStringSupported(input))
            {
                _commandLayer.executeCommand(input);
            }
            else
            {
                onCommandExecuted(new Command(""));
            }
        }
        catch (Exception ex) 
        {
            for(IGameModeListener listener : _listeners)
            {
                listener.onTextOutput("Exception Caught: " + ex.toString());
            }
        }
    }

    @Override
    public void onCommandExecuted(Command command) 
    {
        String output = null;
        if(command.getName().equalsIgnoreCase("Help"))
        {
            if(command.getParameters().containsKey("Command"))
            {
                String commandName = (String)command.getParameters().get("Command");
                Command target = _commandLayer.getCommand(commandName);
                if(target != null) 
                    output = "Description: " + target.getDescription() + "\n" + target.getHelpText();
                else 
                    output = "No command of that name could be found, type \"help\" for a list of available commands.";
            }
            else
            {
                output = "List of available commands: \n";

                for(Command aCommand : _commandLayer.getCommands())
                {
                    output += aCommand.getName() + ": " + aCommand.getDescription() + "\n";
                }
                
                output = output.substring(0, output.length() - 1);
            }
        }
        else
        {
            output = "Command not recognized, type \"help\" for a list of available commands.";
        }
        
        for(IGameModeListener listener : _listeners)
        {
            listener.onTextOutput(output);
        }
    }
}
