/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.ITHScript;

/**
 *
 * @author joshua
 */
public class CompileException extends Exception 
{
    /**
     * Constructs an instance of <code>CompileException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public CompileException(String msg, String filename, int line, int column) 
    {
        super("[" + filename + " Line:" + line + " Col:" + column + "]: " + msg);
    }
}
