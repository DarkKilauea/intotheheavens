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

/**
 *
 * @author joshua
 */
public class LocationFileParser 
{
    private int _token = 0;
    private Deque<Object> _stack = new ArrayDeque<Object>();
    private String _currentFile = null;
    
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
        return ((lex.getPreviousToken() == '\n') || (_token == ';'));
    }
    
    public void parseFile() throws CompileException, IOException
    {
        FileReader fileReader = new FileReader(_currentFile);
        BufferedReader reader = new BufferedReader(fileReader);
        Lexer lex = new Lexer(reader);
        
        nextToken(lex);
        while(_token > 0)
        {
            //Start of location block
            if(_token == Lexer.TK_LOCATION)
            {
                //Expect name
                if(nextToken(lex) && _token == Lexer.TK_STRING_LITERAL)
                {
                    String name = lex.getStringValue();
                    if(nextToken(lex) && _token == _blockStart)
                    {
                        Location loc = new Location(name);
                        _locations.add(loc);
                        _stack.push((Object)loc);
                        
                        nextToken(lex);
                    }
                    else throw new CompileException("Block start \"{\" expected.", _currentFile, lex.getLineNumber(), lex.getColumnNumber());
                }
                else throw new CompileException("String constant expected after location keyword.", _currentFile, lex.getLineNumber(), lex.getColumnNumber());
            }
            else if(_token == Lexer.TK_COMMAND)
            {
                //Expect name
                if(nextToken(lex) && _token == Lexer.TK_STRING_LITERAL)
                {
                    String name = lex.getStringValue();
                    if(nextToken(lex) && _token == _blockStart)
                    {
                        Location parent = getTopLocation();
                        StatementBlock handler = new StatementBlock(null, parent, name);
                        
                        parent.getCommandHandlers().add(handler);
                        _stack.push((Object)handler);
                        
                        nextToken(lex);
                    }
                    else throw new CompileException("Block start \"{\" expected.", _currentFile, lex.getLineNumber(), lex.getColumnNumber());
                }
                else throw new CompileException("String constant expected after command keyword.", _currentFile, lex.getLineNumber(), lex.getColumnNumber());
            }
            else if(_token == Lexer.TK_EVENT)
            {
                //Expect name
                if(nextToken(lex) && _token == Lexer.TK_STRING_LITERAL)
                {
                    String name = lex.getStringValue();
                    if(nextToken(lex) && _token == _blockStart)
                    {
                        Location parent = getTopLocation();
                        StatementBlock handler = new StatementBlock(null, parent, name);
                        
                        parent.getEventHandlers().add(handler);
                        _stack.push((Object)handler);
                        
                        nextToken(lex);
                    }
                    else throw new CompileException("Block start \"{\" expected.", _currentFile, lex.getLineNumber(), lex.getColumnNumber());
                }
                else throw new CompileException("String constant expected after event keyword.", _currentFile, lex.getLineNumber(), lex.getColumnNumber());
            }
            //End of a block
            else if(_token == _blockEnd)
            {
                _stack.pop();
                nextToken(lex);
            }
            else if(getTopStatementBlock() != null)
            {
                StatementBlock handler = getTopStatementBlock();
                
                List<Statement> stats = processStatement(handler, lex);
                if(stats != null) handler.getStatements().addAll(stats);
            }
            else
            {
                throw new CompileException("Cannot have a statement outside of a command or event block.", _currentFile, lex.getLineNumber(), lex.getColumnNumber());
            }
        }
        
        lex.close();
    }
    
    private List<Statement> processStatement(StatementBlock block, Lexer lex) throws IOException, CompileException
    {
        if(_token == ';') 
        {
            nextToken(lex);
            return null;
        }
        else if(_token == Lexer.TK_PRINT) return processPrintStatement(block, lex);
        else if(_token == Lexer.TK_GOTO) return processGotoStatement(block, lex);
        else if(_token == Lexer.TK_IDENTIFIER) return null;
        else throw new CompileException("Unknown statement.", _currentFile, lex.getLineNumber(), lex.getColumnNumber());
    }
    
    private List<Statement> processPrintStatement(StatementBlock block, Lexer lex) throws IOException, CompileException
    {
        nextToken(lex);
        
        List<Statement> stats = new ArrayList<Statement>();
        if(_token == Lexer.TK_STRING_LITERAL)
        {
            stats.add(new PrintStatement(block, lex.getStringValue()));
            nextToken(lex);
            if(!isEndOfStatement(lex)) throw new CompileException("Expected end of expression after statement.", _currentFile, lex.getLineNumber(), lex.getColumnNumber());
            
            return stats;
        }
        else if(_token == Lexer.TK_FLOAT)
        {
            stats.add(new PrintStatement(block, ((Float)lex.getFloatValue()).toString()));
            nextToken(lex);
            if(!isEndOfStatement(lex)) throw new CompileException("Expected end of expression after statement.", _currentFile, lex.getLineNumber(), lex.getColumnNumber());
            
            return stats;
        }
        else if(_token == Lexer.TK_INTEGER)
        {
            stats.add(new PrintStatement(block, ((Integer)lex.getIntegerValue()).toString()));
            nextToken(lex);
            if(!isEndOfStatement(lex)) throw new CompileException("Expected end of expression after statement.", _currentFile, lex.getLineNumber(), lex.getColumnNumber());
            
            return stats;
        }
        else throw new CompileException("Expected a string constant, integer, or float after print.", _currentFile, lex.getLineNumber(), lex.getColumnNumber());
    }
    
    private List<Statement> processGotoStatement(StatementBlock block, Lexer lex) throws IOException, CompileException
    {
        nextToken(lex);
        
        List<Statement> stats = new ArrayList<Statement>();
        if(_token == Lexer.TK_STRING_LITERAL)
        {
            stats.add(new GotoStatement(block, lex.getStringValue()));
            nextToken(lex);
            if(!isEndOfStatement(lex)) throw new CompileException("Expected end of expression after statement.", _currentFile, lex.getLineNumber(), lex.getColumnNumber());
            
            return stats;
        }
        else throw new CompileException("Expected string constant after goto.", _currentFile, lex.getLineNumber(), lex.getColumnNumber());
    }
    
    private boolean nextToken(Lexer lex) throws IOException
    {
        _token = lex.nextToken();
        
        if(_token > 0) return true;
        else return false;
    }
    
    private Location getTopLocation()
    {
        Location item = null;
        for(Object obj : _stack)
        {
            if(obj.getClass() == Location.class)
            {
                item = (Location)obj;
                break;
            }
        }
        
        return item;
    }
    
    private StatementBlock getTopStatementBlock()
    {
        StatementBlock item = null;
        for(Object obj : _stack)
        {
            if(StatementBlock.class.isAssignableFrom(obj.getClass()))
            {
                item = (StatementBlock)obj;
                break;
            }
        }
        
        return item;
    }
}
