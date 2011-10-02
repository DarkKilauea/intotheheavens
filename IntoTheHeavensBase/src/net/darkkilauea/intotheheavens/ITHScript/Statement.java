/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.ITHScript;

/**
 *
 * @author joshua
 */
public class Statement 
{
    protected StatementBlock _scope = null;

    public StatementBlock getScope() 
    {
        return _scope;
    }
    
    public Statement(StatementBlock scope)
    {
        _scope = scope;
    }
}
