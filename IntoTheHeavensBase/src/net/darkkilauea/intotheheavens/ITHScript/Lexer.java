/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.ITHScript;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;

/**
 *
 * @author joshua
 */
public class Lexer 
{
    public static int TK_UNKNOWN = 0;
    public static int TK_LOCATION = 258;
    public static int TK_COMMAND = 259;
    public static int TK_STRING_LITERAL = 260;
    public static int TK_INTEGER = 261;
    public static int TK_FLOAT = 262;
    public static int TK_EQ = 263;
    public static int TK_NE = 264;
    public static int TK_LE = 265;
    public static int TK_GE = 266;
    public static int TK_AND = 267;
    public static int TK_OR = 268;
    public static int TK_IF = 269;
    public static int TK_ELSE = 270;
    public static int TK_NULL = 271;
    public static int TK_MODULO = 272;
    public static int TK_UMINUS = 273;
    public static int TK_PLUSEQ = 274;
    public static int TK_MINUSEQ = 275;
    public static int TK_PLUSPLUS = 276;
    public static int TK_MINUSMINUS = 277;
    public static int TK_TRUE = 278;
    public static int TK_FALSE = 279;
    public static int TK_MULEQ = 280;
    public static int TK_DIVEQ = 281;
    public static int TK_MODEQ = 282;
    public static int TK_IDENTIFIER = 283;
    public static int TK_PRINT = 284;
    public static int TK_GOTO = 285;
    
    private Reader _reader = null;
    private char _curCharacter = '\0';
    private int _curToken = TK_UNKNOWN;
    private int _prevToken = _curToken;
    private int _curLine = 1;
    private int _curColumn = 0;
    
    private String _stringValue = "";
    private int _intValue = 0;
    private float _floatValue = 0.0f;
    
    private HashMap<String, Integer> _keywords = new HashMap<String, Integer>();
    
    public Lexer(Reader reader)
    {
        _reader = reader;
        
        _keywords.put("location", TK_LOCATION);
        _keywords.put("command", TK_COMMAND);
        _keywords.put("if", TK_IF);
        _keywords.put("else", TK_ELSE);
        _keywords.put("and", TK_AND);
        _keywords.put("or", TK_OR);
        _keywords.put("null", TK_NULL);
        _keywords.put("nil", TK_NULL);
        _keywords.put("true", TK_TRUE);
        _keywords.put("false", TK_FALSE);
        _keywords.put("yes", TK_TRUE);
        _keywords.put("no", TK_FALSE);
        _keywords.put("print", TK_PRINT);
        _keywords.put("goto", TK_GOTO);
        
        next();
    }
    
    public String getStringValue()
    {
        return _stringValue;
    }
    
    public int getIntegerValue()
    {
        return _intValue;
    }
    
    public float getFloatValue()
    {
        return _floatValue;
    }
    
    public int getLineNumber()
    {
        return _curLine;
    }
    
    public int getColumnNumber()
    {
        return _curColumn;
    }
    
    private void next()
    {
        try 
        {
            _curCharacter = (char)_reader.read();
            _curColumn++;
        } 
        catch (IOException ex) 
        {
            _curCharacter = '\0';
        }
    }
    
    public int nextToken() throws IOException
    {
	while(_curCharacter != '\0') 
        {
            switch(_curCharacter)
            {
		case '\t': 
                case '\r': 
                case ' ': 
                    next(); 
                    continue;
		case '\n':
                    _curLine++;
                    _prevToken = _curToken;
                    _curToken = '\n';
                    next();
                    _curColumn = 1;
                    continue;
		case '/':
                    next();
                    switch(_curCharacter)
                    {
                        case '*':
                            next();
                            consumeCommentBlock();
                            continue;	
                        case '/':
                            do 
                            { 
                                next(); 
                            } while (_curCharacter != '\n' && _curCharacter != '\0');
                            continue;
                        case '=':
                            next();
                            return setToken(TK_DIVEQ);
                        default:
                            return setToken('/');
                    }
		case '=':
                    next();
                    if (_curCharacter != '=')
                    { 
                        return setToken('=');
                    }
                    else 
                    { 
                        next(); 
                        setToken(TK_EQ); 
                    }
		case '<':
                    next();
                    if (_curCharacter == '=')
                    {
                        next(); 
                        return setToken(TK_LE);
                    }
                    else return setToken('<');
		case '>':
                    next();
                    if (_curCharacter == '=')
                    { 
                        next(); 
                        return setToken(TK_GE);
                    }
                    else return setToken('>');
		case '!':
                    next();
                    if (_curCharacter != '=')
                    { 
                        return setToken('!');
                    }
                    else 
                    { 
                        next(); 
                        return setToken(TK_NE); 
                    }
		case '"':
                    {
			int stringResult = readString();
			if(stringResult != -1)
                        {
                            return setToken(stringResult);
			}
			//Error
                    }
		case '{': 
                case '}': 
                case '(': 
                case ')': 
                case '[': 
                case ']':
		case ';': 
                case ',': 
                case '?': 
                case '^': 
                case '~':
                    int ret = _curCharacter;
                    next();
                    return setToken(ret);
		case '&':
                    next();
                    if (_curCharacter != '&')
                    { 
                        return setToken('&'); 
                    }
                    else 
                    { 
                        next(); 
                        return setToken(TK_AND); 
                    }
		case '|':
                    next();
                    if (_curCharacter != '|')
                    { 
                        return setToken('|'); 
                    }
                    else 
                    { 
                        next(); 
                        return setToken(TK_OR); 
                    }
		case '*':
                    next();
                    if (_curCharacter == '=')
                    { 
                        next(); 
                        return setToken(TK_MULEQ);
                    }
                    else return setToken('*');
		case '%':
                    next();
                    if (_curCharacter == '=')
                    { 
                        next(); 
                        return setToken(TK_MODEQ);
                    }
                    else return setToken('%');
		case '-':
                    next();
                    if (_curCharacter == '=')
                    { 
                        next(); 
                        return setToken(TK_MINUSEQ);
                    }
                    else if  (_curCharacter == '-')
                    { 
                        next(); 
                        return setToken(TK_MINUSMINUS);
                    }
                    else return setToken('-');
		case '+':
                    next();
                    if (_curCharacter == '=')
                    { 
                        next(); 
                        return setToken(TK_PLUSEQ);
                    }
                    else if (_curCharacter == '+')
                    { 
                        next(); 
                        return setToken(TK_PLUSPLUS);
                    }
                    else return setToken('+');
		case '\0':
			return 0;
		default:
                {
                    if (Character.isDigit(_curCharacter)) 
                    {
                        return setToken(readNumber());
                    }
                    else if (Character.isLetterOrDigit(_curCharacter) || _curCharacter == '_') 
                    {
                        return setToken(readId());
                    }
                    return setToken(TK_UNKNOWN);
		}
            }
	}
	return TK_UNKNOWN;
    }
    
    private int setToken(int t)
    {
        _prevToken = _curToken; 
        _curToken = t; 
        return t;
    }
    
    private void consumeCommentBlock()
    {
        boolean done = false;
        while(!done)
        {
            switch(_curCharacter)
            {
                case '*':
                {
                    next();
                    if(_curCharacter == '/')
                    {
                        done = true;
                        next();
                    }
                    continue;
                }
                case '\n':
                    _curLine++;
                    next();
                    continue;
                case '\0':
                    done = true;
                    break;
                default:
                    next();
            }
        }
    }
    
    private int readString()
    {
	String temp = "";
	next();
	if(_curCharacter == '\0') return -1;
	for(;;) 
        {
            while(_curCharacter != '\"') 
            {
                switch(_curCharacter) 
                {
                    case '\0':
                        return -1;
                    case '\n': 
                        temp = temp + _curCharacter; 
                        next(); 
                        _curLine++;
                        break;
                    default:
                        temp = temp + _curCharacter;
                        next();
                }
            }
            
            next();
            if(_curCharacter == '"') 
            {
                temp = temp + _curCharacter;
                next();
            }
            else break;
	}
        
	_stringValue = temp;
	return TK_STRING_LITERAL;
    }
    
    private int readNumber()
    {
        int type = 0; 
        String temp = "";
        while (_curCharacter == '.' || Character.isDigit(_curCharacter)) 
        {
            if(_curCharacter == '.') type = 1;
            temp = temp + _curCharacter;
            next();
        }
        
        switch(type) 
        {
            case 1:
                _floatValue = Float.parseFloat(temp);
                return TK_FLOAT;
            case 0:
                _intValue = Integer.parseInt(temp);
                return TK_INTEGER;
	}
        
	return 0;
    }
    
    private int getIdType(String id)
    {
        if(_keywords.containsKey(id)) return _keywords.get(id);
        else return TK_IDENTIFIER;
    }
    
    private int readId()
    {
        String temp = "";
        
        do 
        {
            temp = temp + _curCharacter;
            next();
	} while(Character.isLetterOrDigit(_curCharacter) || _curCharacter == '_');
        
        int res = getIdType(temp);
        if(res == TK_IDENTIFIER) _stringValue = temp;
        
        return res;
    }
    
    public void close() throws IOException
    {
        _reader.close();
    }
}
