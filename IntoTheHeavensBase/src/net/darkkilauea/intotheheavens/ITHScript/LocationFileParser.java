/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.ITHScript;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
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
    private Deque _stack = new ArrayDeque();
    private String _currentFile = null;
    
    private char _blockStart = '{';
    private char _blockEnd = '}';
    
    public LocationFileParser(String filename)
    {
        _currentFile = filename;
    }
    
    public List<Location> parseFile() throws FileNotFoundException, CompileException, IOException
    {
        List<Location> locations = new ArrayList<Location>();
        FileReader fileReader = new FileReader(_currentFile);
        BufferedReader reader = new BufferedReader(fileReader);
        Lexer lex = new Lexer(reader);
        
        while(nextToken(lex))
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
                        locations.add(loc);
                        _stack.push(loc);
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
                        CommandHandler handler = new CommandHandler(name);
                        
                        Location parent = getTopLocation();
                        parent.getCommandHandlers().add(handler);
                        _stack.push(handler);
                    }
                    else throw new CompileException("Block start \"{\" expected.", _currentFile, lex.getLineNumber(), lex.getColumnNumber());
                }
                else throw new CompileException("String constant expected after command keyword.", _currentFile, lex.getLineNumber(), lex.getColumnNumber());
            }
            //End of a block
            else if(_token == _blockEnd)
            {
                _stack.pop();
            }
            else if(getTopCommandHandler() != null)
            {
                CommandHandler handler = getTopCommandHandler();
                
                Statement stat = processStatement(lex);
                if(stat != null) handler.getStatements().add(stat);
            }
        }
        
        lex.close();
        return locations;
    }
    
    private Statement processStatement(Lexer lex) throws IOException, CompileException
    {
        if(_token == Lexer.TK_PRINT) return processPrintStatement(lex);
        else if(_token == Lexer.TK_GOTO) return processGotoStatement(lex);
        else if(_token == ';') return null;
        else throw new CompileException("Unknown statement.", _currentFile, lex.getLineNumber(), lex.getColumnNumber());
    }
    
    private Statement processPrintStatement(Lexer lex) throws IOException, CompileException
    {
        nextToken(lex);
        
        if(_token == Lexer.TK_STRING_LITERAL)
        {
            return new PrintStatement(lex.getStringValue());
        }
        else if(_token == Lexer.TK_FLOAT)
        {
            return new PrintStatement(((Float)lex.getFloatValue()).toString());
        }
        else if(_token == Lexer.TK_INTEGER)
        {
            return new PrintStatement(((Integer)lex.getIntegerValue()).toString());
        }
        else throw new CompileException("Expected a string constant, integer, or float after print.", _currentFile, lex.getLineNumber(), lex.getColumnNumber());
    }
    
    private Statement processGotoStatement(Lexer lex) throws IOException, CompileException
    {
        nextToken(lex);
        
        if(_token == Lexer.TK_STRING_LITERAL)
        {
            return new Statement();
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
    
    private CommandHandler getTopCommandHandler()
    {
        CommandHandler item = null;
        for(Object obj : _stack)
        {
            if(obj.getClass() == CommandHandler.class)
            {
                item = (CommandHandler)obj;
                break;
            }
        }
        
        return item;
    }
}
