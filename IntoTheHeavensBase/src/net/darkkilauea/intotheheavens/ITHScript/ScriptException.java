/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.ITHScript;

/**
 *
 * @author joshua
 */
public class ScriptException extends Exception 
{
    /**
     * Constructs an instance of <code>ScriptException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ScriptException(String msg, Statement throwingStatement) 
    {
        super(ScriptException.formatMessage(msg, throwingStatement)); 
    }
    
    private static String formatMessage(String msg, Statement throwingStatement)
    {
        String message = "Runtime Error: " + msg + 
                "\ncaused by statement:\n" + throwingStatement.toString() + "\nin ";
        
        StatementBlock parent = throwingStatement.getScope();
        while (parent != null)
        {
            message += parent.toString();
            
            if (parent.getLocation() != null)
            {
                message += "\nin " + parent.getLocation().toString();
            }
            
            parent = parent.getParent();
            
            if (parent != null) message += "\nin ";
        }
        
        return message;
    }
}
