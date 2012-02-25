/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.ITHScript;

import java.util.List;

/**
 *
 * @author joshua
 */
public interface IVirtualMachineDelegate 
{
    public void onInvokePrint(String message);
    public void onInvokeGoto(String locationName);
    public ScriptObject onCallHandler(String name, boolean self, List<Variable> args);
}
