/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.ITHScript;

/**
 *
 * @author joshua
 */
public class PrintStatement extends Statement
{
    protected String _message = null;

    /**
     * Get the value of message
     *
     * @return the value of message
     */
    public String getMessage() 
    {
        return _message;
    }

    /**
     * Set the value of message
     *
     * @param message new value of message
     */
    public void setMessage(String message) 
    {
        _message = message;
    }

    public PrintStatement(String message)
    {
        _message = message;
    }

    @Override
    public String toString() 
    {
        return "PrintStatement{" + "_message=" + _message + '}';
    }
}
