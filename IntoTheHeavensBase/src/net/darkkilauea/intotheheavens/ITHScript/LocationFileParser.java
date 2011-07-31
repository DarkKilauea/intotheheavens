/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.ITHScript;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Stack;

/**
 *
 * @author joshua
 */
public class LocationFileParser 
{
    private int _token = 0;
    private Stack _stack = new Stack();
    
    private char _blockStart = '{';
    private char _blockEnd = '}';
    
    public boolean parseFile(String filename) throws FileNotFoundException, CompileException, IOException
    {
        FileReader fileReader = new FileReader(filename);
        LineNumberReader reader = new LineNumberReader(fileReader);
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
                        Location loc = new Location();
                        _stack.push(loc);
                    }
                    else throw new CompileException("Block start \"{\" expected.", filename, reader.getLineNumber());
                }
                else throw new CompileException("Identifier expected after location keyword.", filename, reader.getLineNumber());
            }
            else if(_token == Lexer.TK_COMMAND)
            {
                //Expect name
                if(nextToken(lex) && _token == Lexer.TK_STRING_LITERAL)
                {
                    String name = lex.getStringValue();
                    if(nextToken(lex) && _token == _blockStart)
                    {
                        CommandHandler handler = new CommandHandler();
                        _stack.push(handler);
                    }
                    else throw new CompileException("Block start \"{\" expected.", filename, reader.getLineNumber());
                }
                else throw new CompileException("Identifier expected after command keyword.", filename, reader.getLineNumber());
            }
            //End of a block
            else if(_token == _blockEnd)
            {
                _stack.pop();
            }
        }
        
        lex.close();
        return true;
    }
    
    private boolean nextToken(Lexer lex) throws IOException
    {
        _token = lex.nextToken();
        
        if(_token > 0) return true;
        else return false;
    }
}
