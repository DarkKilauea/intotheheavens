/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens;

import net.darkkilauea.intotheheavens.ITHScript.*;
import net.darkkilauea.intotheheavens.commands.*;

/**
 *
 * @author joshua
 */
public class MenuGameMode extends GameMode implements ICommandListener
{
    private CommandLayer _commandLayer = new CommandLayer();
    private String _rootDir = "base\\";
    private String _scriptDir = _rootDir + "scripts\\";
    private String _saveDir = _rootDir + "savegames\\";
    
    public MenuGameMode(String rootDir)
    {
        if(_rootDir != null)
        {
            _rootDir = rootDir;
            _scriptDir = _rootDir + "scripts\\";
            _saveDir = _rootDir + "savegames\\";
        }
    }
    
    @Override
    public boolean initialize(GameModeManager manager)
    {
        super.initialize(manager);

        Command helpCommand = new HelpCommand("Help");
        helpCommand.registerListener(this);

        Command newGameCommand = new NewGameCommand("New_Game");
        newGameCommand.registerListener(this);

        Command saveGameCommand = new SaveGameCommand("Save_Game");
        saveGameCommand.registerListener(this);

        Command loadGameCommand = new LoadGameCommand("Load_Game");
        loadGameCommand.registerListener(this);

        Command quitGameCommand = new QuitGameCommand("Quit_Game");
        quitGameCommand.registerListener(this);

        _commandLayer.registerCommand(helpCommand);
        _commandLayer.registerCommand(newGameCommand);
        _commandLayer.registerCommand(saveGameCommand);
        _commandLayer.registerCommand(loadGameCommand);
        _commandLayer.registerCommand(quitGameCommand);
        
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
        _commandLayer.getCommand("Help").unregisterListener(this);
        _commandLayer.getCommand("New_Game").unregisterListener(this);
        _commandLayer.getCommand("Save_Game").unregisterListener(this);
        _commandLayer.getCommand("Load_Game").unregisterListener(this);
        _commandLayer.getCommand("Quit_Game").unregisterListener(this);
        
        _commandLayer.unregisterCommand("Help");
        _commandLayer.unregisterCommand("New_Game");
        _commandLayer.unregisterCommand("Save_Game");
        _commandLayer.unregisterCommand("Load_Game");
        _commandLayer.unregisterCommand("Quit_Game");
        
        super.shutdown();
    }
    
    @Override
    public void injectTextInput(String input)
    {
        try
        {
            if(_commandLayer.checkCommandStringSupported(input))
            {
                if(!_commandLayer.executeCommand(input))
                {
                    printToAllListeners("Incorrect syntax.  Type \"help <command>\" for details.");
                }
            }
            else
            {
                onCommandExecuted(new Command(""));
            }
        }
        catch (Exception ex) 
        {
            printToAllListeners("Exception Caught: " + ex.toString());
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
                    output = target.getHelpText() + "\n" + "Description: " + target.getDescription();
                else 
                    output = "No command of that name could be found, type \"help\" for a list of available commands.";
            }
            else
            {
                output = "List of available commands: \n\n";

                for(Command aCommand : _commandLayer.getCommands())
                {
                    output += aCommand.getName() + ": " + aCommand.getDescription() + "\n";
                }
                
                output = output.substring(0, output.length() - 1);
            }
        }
        else if(command.getName().equalsIgnoreCase("New_Game"))
        {
            newGame();
            return;
        }
        else if(command.getName().equalsIgnoreCase("Load_Game"))
        {
            output = "This command has not been fully implemented yet.";
        }
        else if(command.getName().equalsIgnoreCase("Save_Game"))
        {
            output = "This command has not been fully implemented yet.";
        }
        else if(command.getName().equalsIgnoreCase("Quit_Game"))
        {
            output = "This command has not been fully implemented yet.";
        }
        else
        {
            output = "Command not recognized, type \"help\" for a list of available commands.";
        }
        
        printToAllListeners(output);
    }
    
    public void newGame()
    {   
        try
        {
            WorldState world = new WorldState();
            world.loadLocations(_scriptDir);
            
            Location startLocation = world.findLocation("Start");
            if(startLocation != null) world.setCurrentLocation(startLocation);
            else throw new Exception("Could not find initial location!");
            
            for(IGameModeListener listener : _listeners)
            {
                listener.onClearOutput();
            }
            
            MainGameMode mode = (MainGameMode)_manager.getMode("Main");
            mode.loadFromWorldState(world);
            
            _manager.setActiveMode("Main");
        }
        catch (Exception ex)
        {
            printToAllListeners("Failed to start new game! \nException caught: " + ex.toString());
        }
    }
}
