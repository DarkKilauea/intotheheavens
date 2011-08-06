/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.commands;

import net.darkkilauea.intotheheavens.commands.Command;

/**
 *
 * @author joshua
 */
public interface ICommandListener 
{
    public void onCommandExecuted(Command command);
}
