/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.ITHScript;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import net.darkkilauea.intotheheavens.ITHScript.Lexer.Token;

/**
 *
 * @author joshua
 */
public class LocationFileParser 
{
    private Token _token = null;
    private String _currentFile = null;
    private Location _currentLocation = null;
    private Closure _currentClosure = null;
    
    private char _blockStart = '{';
    private char _blockEnd = '}';
    
    private List<Location> _locations = new ArrayList<Location>();
    private List<Variable> _globals = new ArrayList<Variable>();
    
    public List<Location> getLocations()
    {
        return _locations;
    }
    
    public List<Variable> getGlobals()
    {
        return _globals;
    }
    
    public LocationFileParser(String filename)
    {
        _currentFile = filename;
    }
    
    private boolean isEndOfStatement(Lexer lex)
    {
        return ((lex.getPreviousToken().getValue() == '\n') || (_token.getValue() == ';'));
    }
    
    private void EmitCompileError(String message) throws CompileException
    {
        throw new CompileException(message, _currentFile, _token.getLineNumber(), _token.getColumnNumber());
    }
    
    public void parseFile() throws CompileException, IOException
    {
        FileReader fileReader = new FileReader(_currentFile);
        BufferedReader reader = new BufferedReader(fileReader);
        Lexer lex = new Lexer(reader);
        
        nextToken(lex);
        while(_token.getValue() > 0)
        {
            //Start of location block
            if(_token.getValue() == Lexer.TK_LOCATION)
            {
                //Expect name
                if(nextToken(lex) && _token.getValue() == Lexer.TK_STRING_LITERAL)
                {
                    String name = _token.getStringValue();
                    if(nextToken(lex) && _token.getValue() == _blockStart)
                    {
                        Location loc = new Location(name);
                        _locations.add(loc);
                        _currentLocation = loc;
                        
                        nextToken(lex);
                    }
                    else EmitCompileError("Block start \"{\" expected.");
                }
                else EmitCompileError("String constant expected after location keyword.");
            }
            else if(_token.getValue() == Lexer.TK_COMMAND)
            {
                //Expect name
                if(nextToken(lex) && _token.getValue() == Lexer.TK_STRING_LITERAL)
                {
                    String name = _token.getStringValue();
                    if(nextToken(lex) && _token.getValue() == _blockStart)
                    {
                        Location parent = _currentLocation;
                        Closure handler = new Closure(parent, name);
                        
                        parent.getCommandHandlers().add(handler);
                        _currentClosure = handler;
                        
                        nextToken(lex);
                    }
                    else EmitCompileError("Block start \"{\" expected.");
                }
                else EmitCompileError("String constant expected after command keyword.");
            }
            else if(_token.getValue() == Lexer.TK_EVENT)
            {
                //Expect name
                if(nextToken(lex) && _token.getValue() == Lexer.TK_STRING_LITERAL)
                {
                    String name = _token.getStringValue();
                    if(nextToken(lex) && _token.getValue() == _blockStart)
                    {
                        Location parent = _currentLocation;
                        Closure handler = new Closure(parent, name);
                        
                        parent.getEventHandlers().add(handler);
                        _currentClosure = handler;
                        
                        nextToken(lex);
                    }
                    else EmitCompileError("Block start \"{\" expected.");
                }
                else EmitCompileError("String constant expected after event keyword.");
            }
            //End of a block
            else if(_token.getValue() == _blockEnd)
            {
                if (_currentLocation != null && _currentClosure != null)
                {
                    _currentClosure = null;
                }
                else if (_currentLocation != null)
                {
                    _currentLocation = null;
                }
                else EmitCompileError("End of block with no matching start.");
                
                nextToken(lex);
            }
            else if(_currentClosure != null)
            {
                //Todo: Add compiler
                EmitCompileError("Statement parsing currently disabled.");
            }
            else
            {
                EmitCompileError("Cannot have a statement outside of a command or event block.");
            }
        }
        
        lex.close();
    }
    
    private boolean nextToken(Lexer lex) throws IOException
    {
        _token = lex.nextToken();
        
        if(_token.getValue() > 0) return true;
        else return false;
    }
}
