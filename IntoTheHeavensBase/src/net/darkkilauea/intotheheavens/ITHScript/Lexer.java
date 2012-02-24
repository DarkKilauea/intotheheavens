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
    public class Token
    {
        private int _value = 0;
        private String _stringValue = "";
        private int _intValue = 0;
        private double _floatValue = 0.0f;
        private int _line = 1;
        private int _column = 0;
               
        public Token(int token, int line, int column)
        {
            _value = token;
            _line = line;
            _column = column;
        }
        
        public Token(int token, int line, int column, String stringValue)
        {
            _value = token;
            _line = line;
            _column = column;
            
            _stringValue = stringValue;
        }
        
        public Token(int token, int line, int column, int intValue)
        {
            _value = token;
            _line = line;
            _column = column;
            
            _intValue = intValue;
        }
        
        public Token(int token, int line, int column, double floatValue)
        {
            _value = token;
            _line = line;
            _column = column;
            
            _floatValue = floatValue;
        }
        
        public int getValue()
        {
            return _value;
        }
        
        public String getStringValue()
        {
            return _stringValue;
        }

        public int getIntegerValue()
        {
            return _intValue;
        }

        public double getFloatValue()
        {
            return _floatValue;
        }

        public int getLineNumber()
        {
            return _line;
        }

        public int getColumnNumber()
        {
            return _column;
        }
    }
    
    public static final int TK_UNKNOWN = 0;
    public static final int TK_TOKENS = 57400;
    public static final int TK_STRING_LITERAL = TK_TOKENS + 1;
    public static final int TK_INTEGER = TK_TOKENS + 2;
    public static final int TK_FLOAT = TK_TOKENS + 3;
    public static final int TK_EQ = TK_TOKENS + 4;
    public static final int TK_NE = TK_TOKENS + 5;
    public static final int TK_LE = TK_TOKENS + 6;
    public static final int TK_GE = TK_TOKENS + 7;
    public static final int TK_UMINUS = TK_TOKENS + 8;
    public static final int TK_PLUSEQ = TK_TOKENS + 9;
    public static final int TK_MINUSEQ = TK_TOKENS + 10;
    public static final int TK_PLUSPLUS = TK_TOKENS + 11;
    public static final int TK_MINUSMINUS = TK_TOKENS + 12;
    public static final int TK_MULEQ = TK_TOKENS + 13;
    public static final int TK_DIVEQ = TK_TOKENS + 14;
    public static final int TK_MODEQ = TK_TOKENS + 15;
    public static final int TK_BITSHIFT_LEFT = TK_TOKENS + 16;
    public static final int TK_BITSHIFT_RIGHT = TK_TOKENS + 17;
    public static final int TK_LOCAL_VARIABLE = TK_TOKENS + 18;
    public static final int TK_GLOBAL_VARIABLE = TK_TOKENS + 19;
    
    public static final int TK_KEYWORDS = TK_TOKENS + 100;
    public static final int TK_LOCATION = TK_KEYWORDS + 1;
    public static final int TK_COMMAND = TK_KEYWORDS + 2;
    public static final int TK_IF = TK_KEYWORDS + 3;
    public static final int TK_ELSE = TK_KEYWORDS + 4;
    public static final int TK_AND = TK_KEYWORDS + 5;
    public static final int TK_OR = TK_KEYWORDS + 6;
    public static final int TK_NULL = TK_KEYWORDS + 7;
    public static final int TK_TRUE = TK_KEYWORDS + 8;
    public static final int TK_FALSE = TK_KEYWORDS + 9;
    public static final int TK_PRINT = TK_KEYWORDS + 10;
    public static final int TK_GOTO = TK_KEYWORDS + 11;
    public static final int TK_EVENT = TK_KEYWORDS + 12;
    public static final int TK_BASE = TK_KEYWORDS + 13;
    
    private Reader _reader = null;
    private char _curCharacter = '\0';
    private Token _curToken = null;
    private Token _prevToken = null;
    private int _curLine = 1;
    private int _curColumn = 0;

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
        _keywords.put("event", TK_EVENT);
        _keywords.put("base", TK_BASE);
        
        next();
    }
    
    public Token getPreviousToken()
    {
        return _prevToken;
    }
    
    public String getStringForToken(int token)
    {
        for (String key : _keywords.keySet()) 
        {
            if (_keywords.get(key).equals(token)) return key;
        }
        
        return null;
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
    
    public Token nextToken() throws IOException
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
                    _curToken = new Token('\n', _curLine, _curColumn);
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
                        return setToken(TK_EQ); 
                    }
		case '<':
                    next();
                    if (_curCharacter == '=')
                    {
                        next(); 
                        return setToken(TK_LE);
                    }
                    else if (_curCharacter == '<')
                    {
                        next();
                        return setToken(TK_BITSHIFT_LEFT);
                    }
                    else return setToken('<');
		case '>':
                    next();
                    if (_curCharacter == '=')
                    { 
                        next(); 
                        return setToken(TK_GE);
                    }
                    else if (_curCharacter == '>')
                    {
                        next();
                        return setToken(TK_BITSHIFT_RIGHT);
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
                        return setToken(readString());
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
                case ':':
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
			return new Token(TK_UNKNOWN, _curLine, _curColumn);
		default:
                {
                    if (Character.isDigit(_curCharacter)) 
                    {
                        return setToken(readNumber());
                    }
                    else if (Character.isLetterOrDigit(_curCharacter) || _curCharacter == '_' || _curCharacter == '$') 
                    {
                        return setToken(readId());
                    }
                    return setToken(TK_UNKNOWN);
		}
            }
	}
	return new Token(TK_UNKNOWN, _curLine, _curColumn);
    }
    
    private Token setToken(int t)
    {
        _prevToken = _curToken; 
        _curToken = new Token(t, _curLine, _curColumn); 
        return _curToken;
    }
    
    private Token setToken(Token t)
    {
        _prevToken = _curToken; 
        _curToken = t; 
        return _curToken;
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
    
    private Token readString()
    {
	String temp = "";
	next();
	if(_curCharacter == '\0') return new Token(TK_UNKNOWN, _curLine, _curColumn);
	for(;;) 
        {
            while(_curCharacter != '\"') 
            {
                switch(_curCharacter) 
                {
                    case '\0':
                        return new Token(TK_UNKNOWN, _curLine, _curColumn);
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
        
	return new Token(TK_STRING_LITERAL, _curLine, _curColumn, temp);
    }
    
    private Token readNumber()
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
                return new Token(TK_FLOAT, _curLine, _curColumn, Double.parseDouble(temp));
            case 0:
                return new Token(TK_INTEGER, _curLine, _curColumn, Integer.parseInt(temp));
	}
        
	return new Token(TK_UNKNOWN, _curLine, _curColumn);
    }
    
    private int getIdType(String id)
    {
        if(_keywords.containsKey(id)) return _keywords.get(id);
        else if (id.startsWith("$")) return TK_GLOBAL_VARIABLE;
        else return TK_LOCAL_VARIABLE;
    }
    
    private Token readId()
    {
        String temp = "";
        
        do 
        {
            temp = temp + _curCharacter;
            next();
	} 
        while(Character.isLetterOrDigit(_curCharacter) || _curCharacter == '_' || _curCharacter == '$');
        
        int res = getIdType(temp);
        if(res == TK_LOCAL_VARIABLE || res == TK_GLOBAL_VARIABLE) return new Token(res, _curLine, _curColumn, temp);
        else return new Token(res, _curLine, _curColumn);
    }
    
    public void close() throws IOException
    {
        _reader.close();
    }
}
