/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.ITHScript;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author joshua
 */
public class VirtualMachine 
{
    protected List<IVirtualMachineListener> _listeners = new ArrayList<IVirtualMachineListener>();
    protected List<Variable> _globals = new ArrayList<Variable>();
    
    public boolean registerListener(IVirtualMachineListener listener)
    {
        return _listeners.add(listener);
    }
    
    public boolean unregisterListener(IVirtualMachineListener listener)
    {
        return _listeners.remove(listener);
    }
    
    public List<Variable> getGlobals()
    {
        return _globals;
    }
    
    public void setGlobals(List<Variable> globals)
    {
        _globals = globals;
    }
    
    public void executeStatementBlock(StatementBlock block, List<Variable> args) throws ScriptException
    {   
        //Execute statements
        for(Statement stat : block.getStatements())
        {
            if (stat.getClass().equals(PrintStatement.class))
            {
                PrintStatement printStat = (PrintStatement)stat;
                
                for(IVirtualMachineListener listener : _listeners)
                {
                    listener.onInvokePrint(printStat);
                }
            }
            else if (stat.getClass().equals(GotoStatement.class))
            {
                GotoStatement gotoStat = (GotoStatement)stat;
                
                for(IVirtualMachineListener listener : _listeners)
                {
                    listener.onInvokeGoto(gotoStat);
                }
            }
        }
    }
}
