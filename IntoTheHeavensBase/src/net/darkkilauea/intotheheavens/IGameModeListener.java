/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens;

/**
 *
 * @author joshua
 */
public interface IGameModeListener 
{
    public void onStateChange(GameMode.State state);
    public void onTextOutput(String output);
    public void onClearOutput();
}
