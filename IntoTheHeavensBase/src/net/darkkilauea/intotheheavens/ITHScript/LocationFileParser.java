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
                        _stack.push((Object)loc);
                        
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
                        Location parent = getTopLocation();
                        StatementBlock handler = new StatementBlock(null, parent, name);
                        
                        parent.getCommandHandlers().add(handler);
                        _stack.push((Object)handler);
                        
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
                        Location parent = getTopLocation();
                        StatementBlock handler = new StatementBlock(null, parent, name);
                        
                        parent.getEventHandlers().add(handler);
                        _stack.push((Object)handler);
                        
                        nextToken(lex);
                    }
                    else EmitCompileError("Block start \"{\" expected.");
                }
                else EmitCompileError("String constant expected after event keyword.");
            }
            //End of a block
            else if(_token.getValue() == _blockEnd)
            {
                if (_stack.size() > 0) _stack.pop();
                else EmitCompileError("End of block with no matching start.");
                
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
                EmitCompileError("Cannot have a statement outside of a command or event block.");
            }
        }
        
        lex.close();
    }
    
    private List<Statement> processStatement(StatementBlock block, Lexer lex) throws IOException, CompileException
    {
        if(_token.getValue() == ';') 
        {
            nextToken(lex);
            return null;
        }
        else if(_token.getValue() == Lexer.TK_PRINT) return processPrintStatement(block, lex);
        else if(_token.getValue() == Lexer.TK_GOTO) return processGotoStatement(block, lex);
        else if(_token.getValue() == Lexer.TK_VARIABLE) return null;
        else 
        {
            EmitCompileError("Unknown statement.");
            return null;
        }
    }
    
    private List<Statement> processPrintStatement(StatementBlock block, Lexer lex) throws IOException, CompileException
    {
        nextToken(lex);
        
        List<Statement> stats = new ArrayList<Statement>();
        if(_token.getValue() == Lexer.TK_STRING_LITERAL)
        {
            stats.add(new PrintStatement(block, _token.getStringValue()));
            nextToken(lex);
            if(!isEndOfStatement(lex)) EmitCompileError("Expected end of expression after statement.");
            
            return stats;
        }
        else if(_token.getValue() == Lexer.TK_FLOAT)
        {
            stats.add(new PrintStatement(block, ((Float)_token.getFloatValue()).toString()));
            nextToken(lex);
            if(!isEndOfStatement(lex)) EmitCompileError("Expected end of expression after statement.");
            
            return stats;
        }
        else if(_token.getValue() == Lexer.TK_INTEGER)
        {
            stats.add(new PrintStatement(block, ((Integer)_token.getIntegerValue()).toString()));
            nextToken(lex);
            if(!isEndOfStatement(lex)) EmitCompileError("Expected end of expression after statement.");
            
            return stats;
        }
        else 
        {
            EmitCompileError("Expected a string constant, integer, or float after print.");
            return null;
        }
    }
    
    private List<Statement> processGotoStatement(StatementBlock block, Lexer lex) throws IOException, CompileException
    {
        nextToken(lex);
        
        List<Statement> stats = new ArrayList<Statement>();
        if(_token.getValue() == Lexer.TK_STRING_LITERAL)
        {
            stats.add(new GotoStatement(block, _token.getStringValue()));
            nextToken(lex);
            if(!isEndOfStatement(lex)) EmitCompileError("Expected end of expression after statement.");
            
            return stats;
        }
        else 
        {
            EmitCompileError("Expected string constant after goto.");
            return null;
        }
    }
    
    private List<Statement> processExpression(Lexer lex) throws IOException, CompileException
    {
        List<Token> tokens = new ArrayList<Token>();
        
        do
        {
            nextToken(lex);
            
            switch (_token.getValue())
            {
                case '+':
                case '-':
                case '*':
                case '/':
                case '=':
                case '<':
                case '>':
                case '!':
                case '(':
                case ')':
                case '%':
                case Lexer.TK_DIVEQ:
                case Lexer.TK_EQ:
                case Lexer.TK_LE:
                case Lexer.TK_GE:
                case Lexer.TK_NE:
                case Lexer.TK_AND:
                case Lexer.TK_OR:
                case Lexer.TK_MULEQ:
                case Lexer.TK_MODEQ:
                case Lexer.TK_MINUSEQ:
                case Lexer.TK_PLUSEQ:
                case Lexer.TK_MINUSMINUS:
                case Lexer.TK_PLUSPLUS:
                case Lexer.TK_FLOAT:
                case Lexer.TK_STRING_LITERAL:
                case Lexer.TK_INTEGER:
                case Lexer.TK_VARIABLE:
                case Lexer.TK_TRUE:
                case Lexer.TK_FALSE:
                case Lexer.TK_NULL:
                    tokens.add(_token);
                    break;
                default:
                    EmitCompileError("Invalid token in expression");
                    break;
            }
        }
        while (_token.getValue() != ';');
        
        return scanExpressionAndEmitStatements(tokens);
    }
    
    private List<Statement> scanExpressionAndEmitStatements(List<Token> tokens) throws CompileException
    {
        return null;
    }
    
    private boolean nextToken(Lexer lex) throws IOException
    {
        _token = lex.nextToken();
        
        if(_token.getValue() > 0) return true;
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
