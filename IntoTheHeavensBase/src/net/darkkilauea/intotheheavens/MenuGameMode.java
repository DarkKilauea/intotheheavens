/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens;

import net.darkkilauea.intotheheavens.ITHScript.*;

/**
 *
 * @author joshua
 */
public class MenuGameMode extends GameMode
{
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

        _commands.add(new Command("Help", 
                                  "^help\\s*(\\w+)?\\s*$", 
                                  "Lists all commands with descriptions or more detail about a single command.", 
                                  "Usage: help <command>"));
        _commands.add(new Command("New_Game", 
                                  "^new_game\\s*(\\w+)?\\s*$", 
                                  "Starts a new game, will create an initial save if a save name is specified.", 
                                  "Usage: new_game <game name>"));
        _commands.add(new Command("Save_Game", 
                                  "^save_game\\s*(\\w+)?\\s*$", 
                                  "Saves the game to the current save name, or another name if specified.", 
                                  "Usage: save_game <game name>"));
        _commands.add(new Command("Load_Game", 
                                  "^load_game\\s*(\\w+)?\\s*$", 
                                  "Loads a game from the specified save name.", 
                                  "Usage: load_game [game name]"));
        _commands.add(new Command("Quit_Game", 
                                  "^(?:quit|exit)\\s*$", 
                                  "Quits the current game.  If in the menu, also exits the application.", 
                                  "Usage: quit|exit"));
        
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
        String output = null;
        if(command.getName().equalsIgnoreCase("Help"))
        {
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
