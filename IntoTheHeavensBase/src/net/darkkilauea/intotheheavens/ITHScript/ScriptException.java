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
    public ScriptException(String msg, int line, Closure closure, Location location) 
    {
        super(ScriptException.formatMessage(msg, line, closure, location)); 
    }
    
    public ScriptException(String msg)
    {
        super(msg);
    }
    
    private static String formatMessage(String msg, int line, Closure closure, Location location)
    {
        String message = "Runtime Error: " + msg + 
                "\ncaused by line: " + line + " in " +
                "\nClosure: " + closure.getName() + " in " +
                "\nLocation: " + location.getName();
        
        return message;
    }
}
