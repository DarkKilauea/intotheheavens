/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.ITHScript;

/**
 *
 * @author joshua
 */
public interface IVirtualMachineListener 
{
    public void onInvokePrint(PrintStatement statement);
    public void onInvokeGoto(GotoStatement statement);
}
