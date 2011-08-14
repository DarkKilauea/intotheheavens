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
public class StatementBlock 
{
    protected List<Statement> _statements = new ArrayList<Statement>();
    
    /**
     * Get the value of statements
     *
     * @return the value of statements
     */
    public List<Statement> getStatements() 
    {
        return _statements;
    }
}
