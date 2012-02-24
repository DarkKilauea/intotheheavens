/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.ITHScript;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
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
    
    private final char _blockStart = '{';
    private final char _blockEnd = '}';
    
    private List<Location> _locations = new ArrayList<Location>();
    private List<Variable> _globals = new ArrayList<Variable>();
    private Stack<Integer> _scopes = new Stack<Integer>();
    
    private class ExpressionState
    {
        final static int None = 0;
        final static int Expression = 1;
        final static int Local = 2;
        final static int Global = 3;
        
        int type = 0;
        int pos = 0;
        boolean noGet = false;
        
        public ExpressionState()
        {
            
        }
        
        public ExpressionState(ExpressionState other)
        {
            type=other.type;
            pos=other.pos;
            noGet=other.noGet;
        }
    }
    
    private ExpressionState _expState = new ExpressionState();
    
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
                        _currentClosure = new Closure(_currentLocation, name);
                        _currentLocation.getCommandHandlers().add(_currentClosure);
                        
                        //Process entire block
                        processStatement(lex);
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
                        _currentClosure = new Closure(_currentLocation, name);
                        _currentLocation.getEventHandlers().add(_currentClosure);
                        
                        //Process entire block
                        processStatement(lex);
                    }
                    else EmitCompileError("Block start \"{\" expected.");
                }
                else EmitCompileError("String constant expected after event keyword.");
            }
            //End of a block
            else if(_token.getValue() == _blockEnd)
            {
                if (_currentLocation != null)
                {
                    _currentLocation = null;
                }
                else EmitCompileError("End of block with no matching start.");
                
                nextToken(lex);
            }
            else
            {
                EmitCompileError("Cannot have a statement outside of a command or event block.");
            }
        }
        
        lex.close();
    }
    
    private ScriptObject expectToken(Lexer lex, int tokenValue) throws CompileException, IOException
    {
        if (_token.getValue() != tokenValue) 
        {
            String error = "";
            if (tokenValue < Lexer.TK_KEYWORDS) error = Character.toString((char)tokenValue);
            else
            {
                switch (tokenValue)
                {
                    case Lexer.TK_LOCAL_VARIABLE:
                        error = "Local Variable";
                        break;
                    case Lexer.TK_GLOBAL_VARIABLE:
                        error = "Global Variable";
                        break;
                    case Lexer.TK_STRING_LITERAL:
                        error = "String Literal";
                        break;
                    case Lexer.TK_INTEGER:
                        error = "Integer";
                        break;
                    case Lexer.TK_FLOAT:
                        error = "Float";
                        break;
                    default:
                        error = lex.getStringForToken(tokenValue);
                        break;
                }
            }
            
            EmitCompileError("Expected '" + error + "'!");
        }
        
        ScriptObject result = null;
        switch (tokenValue)
        {
            case Lexer.TK_STRING_LITERAL:
            case Lexer.TK_LOCAL_VARIABLE:
            case Lexer.TK_GLOBAL_VARIABLE:
                result = new ScriptObject(_token.getStringValue());
                break;
            case Lexer.TK_INTEGER:
                result = new ScriptObject(_token.getIntegerValue());
                break;
            case Lexer.TK_FLOAT:
                result = new ScriptObject(_token.getFloatValue());
                break;
        }
        
        nextToken(lex);
        return result;
    }
    
    private void ExpectEndOfStatement(Lexer lex) throws IOException, CompileException
    {
        if (_token.getValue() == ';') nextToken(lex);
        else if (!isEndOfStatement(lex)) EmitCompileError("Expected an end to the statement!");
    }
    
    private boolean nextToken(Lexer lex) throws IOException
    {
        _token = lex.nextToken();
        
        if(_token.getValue() > 0) return true;
        else return false;
    }
    
    private void beginScope()
    {
        _scopes.push(_currentClosure.getStackSize());
    }
    
    private void endScope()
    {
        int size = _scopes.pop();
        
        if (size != _currentClosure.getStackSize())
        {
            _currentClosure.setStackSize(size);
        }
    }
    
    private void processStatement(Lexer lex) throws IOException, CompileException
    {
        _currentClosure.getInstructions().add(new Instruction(OpCode.OP_LINE, 0, 0, 0, 0));
        
        switch (_token.getValue())
        {
            case ';':
                nextToken(lex);
                break;
            case Lexer.TK_IF:
                processIfStatement(lex);
                break;
            case Lexer.TK_PRINT:
                processPrintStatement(lex);
                break;
            case Lexer.TK_GOTO:
                processGotoStatement(lex);
                break;
            case _blockStart:
                beginScope();
                nextToken(lex);
                processStatements(lex);
                expectToken(lex, _blockEnd);
                endScope();
                break;
            default:
                processCommaExpression(lex);
                _currentClosure.popTarget();
                break;
        }
    }
    
    private void processStatements(Lexer lex) throws IOException, CompileException
    {
        while (_token.getValue() != _blockEnd)
        {
            processStatement(lex);
            if (lex.getPreviousToken().getValue() != _blockEnd && lex.getPreviousToken().getValue() != ';') ExpectEndOfStatement(lex);
        }
    }
    
    private void processIfStatement(Lexer lex) throws IOException, CompileException
    {
        int jumpPos;
        boolean hasElse = false;
        
        nextToken(lex);
        expectToken(lex, '(');
        processCommaExpression(lex);
        expectToken(lex, ')');
        
        _currentClosure.getInstructions().add(new Instruction(OpCode.OP_JUMP_COMPARE, 0, _currentClosure.popTarget(), 0, 0));
        int jnePos = _currentClosure.getCurrentInstructionPos();
        
        beginScope();
        processStatement(lex);
        
        if (_token.getValue() != _blockEnd && _token.getValue() != Lexer.TK_ELSE) ExpectEndOfStatement(lex);
        
        endScope();
        
        int endOfIfBlock = _currentClosure.getCurrentInstructionPos();
        if (_token.getValue() == Lexer.TK_ELSE)
        {
            hasElse = true;
            beginScope();
            _currentClosure.getInstructions().add(new Instruction(OpCode.OP_JUMP, 0, 0, 0, 0));
            jumpPos = _currentClosure.getCurrentInstructionPos();
            
            nextToken(lex);
            processStatement(lex);
            ExpectEndOfStatement(lex);

            endScope();
            
            _currentClosure.getInstructions().get(jumpPos)._arg0 = _currentClosure.getCurrentInstructionPos() - jumpPos;
        }
        
        _currentClosure.getInstructions().get(jnePos)._arg0 = endOfIfBlock - jnePos + (hasElse ? 1 : 0);
    }
    
    private void processPrintStatement(Lexer lex) throws IOException, CompileException
    {
        nextToken(lex);
        processExpression(lex);
        
        _currentClosure.getInstructions().add(new Instruction(OpCode.OP_PRINT, _currentClosure.popTarget(), 0, 0, 0));
    }
    
    private void processGotoStatement(Lexer lex) throws IOException, CompileException
    {
        nextToken(lex);
        processExpression(lex);
        
        _currentClosure.getInstructions().add(new Instruction(OpCode.OP_GOTO, _currentClosure.popTarget(), 0, 0, 0));
    }
    
    private void processCommaExpression(Lexer lex) throws IOException, CompileException
    {
        processExpression(lex);
        
        while (_token.getValue() == ',')
        {
            _currentClosure.popTarget();
            nextToken(lex);
            processCommaExpression(lex);
        }
    }
    
    private void processExpression(Lexer lex) throws IOException, CompileException
    {
        ExpressionState oldState = new ExpressionState(_expState);
        _expState.type = ExpressionState.Expression;
        _expState.pos = -1;
        _expState.noGet = false;
        
        processLogicalOrExpression(lex);
        
        switch (_token.getValue())
        {
            case '=':
            {
                int type = _expState.type;
                int pos = _expState.pos;
                
                if (type == ExpressionState.Expression) EmitCompileError("Assignment to an expression is not allowed.");
                
                nextToken(lex);
                processExpression(lex);
                
                if (type == ExpressionState.Local)
                {
                    int src = _currentClosure.popTarget();
                    int dst = _currentClosure.topTarget();
                    _currentClosure.getInstructions().add(new Instruction(OpCode.OP_MOVE, dst, src, 0, 0));
                }
                else if (type == ExpressionState.Global)
                {
                    int src = _currentClosure.popTarget();
                    int dst = _currentClosure.pushTarget(-1);
                    _currentClosure.getInstructions().add(new Instruction(OpCode.OP_SET, dst, pos, src, 0));
                }
                else EmitCompileError("FATAL! UNKNOWN EXPRESSION STATE!");
            }
                break;
            case Lexer.TK_MINUSEQ:
            case Lexer.TK_PLUSEQ:
            case Lexer.TK_MULEQ:
            case Lexer.TK_DIVEQ:
            case Lexer.TK_MODEQ:
            {
                int op = _token.getValue();
                int type = _expState.type;
                int pos = _expState.pos;
                
                if (type == ExpressionState.Expression) EmitCompileError("Assignment to an expression is not allowed.");
                
                nextToken(lex);
                processExpression(lex);
                
                if (type == ExpressionState.Local)
                {
                    int src = _currentClosure.popTarget();
                    int dst = _currentClosure.popTarget();
                    _currentClosure.pushTarget(dst);
                    
                    switch (op)
                    {
                        case Lexer.TK_MINUSEQ:
                            _currentClosure.getInstructions().add(new Instruction(OpCode.OP_SUBTRACT, dst, src, dst, 0));
                            break;
                        case Lexer.TK_PLUSEQ:
                            _currentClosure.getInstructions().add(new Instruction(OpCode.OP_ADD, dst, src, dst, 0));
                            break;
                        case Lexer.TK_MULEQ:
                            _currentClosure.getInstructions().add(new Instruction(OpCode.OP_MULTIPLY, dst, src, dst, 0));
                            break;
                        case Lexer.TK_DIVEQ:
                            _currentClosure.getInstructions().add(new Instruction(OpCode.OP_DIVIDE, dst, src, dst, 0));
                            break;
                        case Lexer.TK_MODEQ:
                            _currentClosure.getInstructions().add(new Instruction(OpCode.OP_MODULO, dst, src, dst, 0));
                            break;
                    }
                }
                else if (type == ExpressionState.Global)
                {
                    int src = _currentClosure.topTarget();
                    int dst = _currentClosure.pushTarget(-1);
                    _currentClosure.getInstructions().add(new Instruction(OpCode.OP_GET, dst, pos, 0, 0));
                    
                    switch (op)
                    {
                        case Lexer.TK_MINUSEQ:
                            _currentClosure.getInstructions().add(new Instruction(OpCode.OP_SUBTRACT, dst, src, dst, 0));
                            break;
                        case Lexer.TK_PLUSEQ:
                            _currentClosure.getInstructions().add(new Instruction(OpCode.OP_ADD, dst, src, dst, 0));
                            break;
                        case Lexer.TK_MULEQ:
                            _currentClosure.getInstructions().add(new Instruction(OpCode.OP_MULTIPLY, dst, src, dst, 0));
                            break;
                        case Lexer.TK_DIVEQ:
                            _currentClosure.getInstructions().add(new Instruction(OpCode.OP_DIVIDE, dst, src, dst, 0));
                            break;
                        case Lexer.TK_MODEQ:
                            _currentClosure.getInstructions().add(new Instruction(OpCode.OP_MODULO, dst, src, dst, 0));
                            break;
                    }
                    
                    _currentClosure.getInstructions().add(new Instruction(OpCode.OP_SET, dst, pos, dst, 0));
                }
                else EmitCompileError("FATAL! UNKNOWN EXPRESSION STATE!");
            }
                break;
            case '?':
            {
                nextToken(lex);
                _currentClosure.getInstructions().add(new Instruction(OpCode.OP_JUMP_COMPARE, 0, _currentClosure.popTarget(), 0, 0));
                int jumpPos = _currentClosure.getCurrentInstructionPos();
                int target = _currentClosure.pushTarget(-1);
                
                processExpression(lex);
                int firstExpression = _currentClosure.popTarget();
                if (target != firstExpression) _currentClosure.getInstructions().add(new Instruction(OpCode.OP_MOVE, target, firstExpression, 0, 0));
                int endOfFirstExpression = _currentClosure.getCurrentInstructionPos();
                
                _currentClosure.getInstructions().add(new Instruction(OpCode.OP_JUMP, 0, 0, 0, 0));
                int jumpPos2 = _currentClosure.getCurrentInstructionPos();
                
                expectToken(lex, ':');
                
                processExpression(lex);
                int secondExpression = _currentClosure.popTarget();
                if (target != secondExpression) _currentClosure.getInstructions().add(new Instruction(OpCode.OP_MOVE, target, secondExpression, 0, 0));
                
                _currentClosure.getInstructions().get(jumpPos2)._arg0 = _currentClosure.getCurrentInstructionPos() - jumpPos2;
                _currentClosure.getInstructions().get(jumpPos)._arg0 = endOfFirstExpression - jumpPos + 1;
            }
                break;
        }
        
        _expState = oldState;
    }
    
    private void processLogicalOrExpression(Lexer lex) throws IOException, CompileException
    {
        processLogicalAndExpression(lex);
        
        if (_token.getValue() == Lexer.TK_OR)
        {
            int firstExpression = _currentClosure.popTarget();
            int target = _currentClosure.pushTarget(-1);
            _currentClosure.getInstructions().add(new Instruction(OpCode.OP_LOGICAL_OR, target, 0, firstExpression, 0));
            int jumpPos = _currentClosure.getCurrentInstructionPos();
            if (target != firstExpression) _currentClosure.getInstructions().add(new Instruction(OpCode.OP_MOVE, target, firstExpression, 0, 0));
            
            nextToken(lex);
            processLogicalOrExpression(lex);
            
            int secondExpression = _currentClosure.popTarget();
            if (target != secondExpression) _currentClosure.getInstructions().add(new Instruction(OpCode.OP_MOVE, target, secondExpression, 0, 0));
            _currentClosure.getInstructions().get(jumpPos)._arg1 = _currentClosure.getCurrentInstructionPos() - jumpPos;
        }
    }
    
    private void processLogicalAndExpression(Lexer lex) throws IOException, CompileException
    {
        processBitwiseOrExpression(lex);
        
        if (_token.getValue() == Lexer.TK_AND)
        {
            int firstExpression = _currentClosure.popTarget();
            int target = _currentClosure.pushTarget(-1);
            _currentClosure.getInstructions().add(new Instruction(OpCode.OP_LOGICAL_AND, target, 0, firstExpression, 0));
            int jumpPos = _currentClosure.getCurrentInstructionPos();
            if (target != firstExpression) _currentClosure.getInstructions().add(new Instruction(OpCode.OP_MOVE, target, firstExpression, 0, 0));
            
            nextToken(lex);
            processLogicalAndExpression(lex);
            
            int secondExpression = _currentClosure.popTarget();
            if (target != secondExpression) _currentClosure.getInstructions().add(new Instruction(OpCode.OP_MOVE, target, secondExpression, 0, 0));
            _currentClosure.getInstructions().get(jumpPos)._arg1 = _currentClosure.getCurrentInstructionPos() - jumpPos;
        }
    }
    
    private void processBitwiseOrExpression(Lexer lex) throws IOException, CompileException
    {
        processBitwiseXOrExpression(lex);
        
        while (true)
        {
            if (_token.getValue() == '|')
            {
                nextToken(lex);
                processBitwiseXOrExpression(lex);
                
                int op1 = _currentClosure.popTarget();
                int op2 = _currentClosure.popTarget();
                _currentClosure.getInstructions().add(new Instruction(OpCode.OP_BITWISE, _currentClosure.pushTarget(-1), op1, op2, Instruction.BW_OR));
            }
            else break;
        }
    }
    
    private void processBitwiseXOrExpression(Lexer lex) throws IOException, CompileException
    {
        processBitwiseAndExpression(lex);
        
        while (true)
        {
            if (_token.getValue() == '^')
            {
                nextToken(lex);
                processBitwiseAndExpression(lex);
                
                int op1 = _currentClosure.popTarget();
                int op2 = _currentClosure.popTarget();
                _currentClosure.getInstructions().add(new Instruction(OpCode.OP_BITWISE, _currentClosure.pushTarget(-1), op1, op2, Instruction.BW_XOR));
            }
            else break;
        }
    }
    
    private void processBitwiseAndExpression(Lexer lex) throws IOException, CompileException
    {
        processComparativeExpression(lex);
        
        while (true)
        {
            if (_token.getValue() == '&')
            {
                nextToken(lex);
                processComparativeExpression(lex);
                
                int op1 = _currentClosure.popTarget();
                int op2 = _currentClosure.popTarget();
                _currentClosure.getInstructions().add(new Instruction(OpCode.OP_BITWISE, _currentClosure.pushTarget(-1), op1, op2, Instruction.BW_AND));
            }
            else break;
        }
    }
    
    private void processComparativeExpression(Lexer lex) throws IOException, CompileException
    {
        processBitwiseShiftExpression(lex);
        
        while (true)
        {
            switch (_token.getValue())
            {
                case Lexer.TK_EQ:
                    {
                        nextToken(lex);
                        processBitwiseShiftExpression(lex);

                        int op1 = _currentClosure.popTarget();
                        int op2 = _currentClosure.popTarget();
                        _currentClosure.getInstructions().add(new Instruction(OpCode.OP_COMPARE, _currentClosure.pushTarget(-1), op1, op2, Instruction.COP_EQUALS));
                    }
                    break;
                case Lexer.TK_NE:
                    {
                        nextToken(lex);
                        processBitwiseShiftExpression(lex);

                        int op1 = _currentClosure.popTarget();
                        int op2 = _currentClosure.popTarget();
                        _currentClosure.getInstructions().add(new Instruction(OpCode.OP_COMPARE, _currentClosure.pushTarget(-1), op1, op2, Instruction.COP_NOTEQUAL));
                    }
                    break;
                case Lexer.TK_GE:
                    {
                        nextToken(lex);
                        processBitwiseShiftExpression(lex);

                        int op1 = _currentClosure.popTarget();
                        int op2 = _currentClosure.popTarget();
                        _currentClosure.getInstructions().add(new Instruction(OpCode.OP_COMPARE, _currentClosure.pushTarget(-1), op1, op2, Instruction.COP_GREATER_EQUALS));
                    }
                    break;
                case Lexer.TK_LE:
                    {
                        nextToken(lex);
                        processBitwiseShiftExpression(lex);

                        int op1 = _currentClosure.popTarget();
                        int op2 = _currentClosure.popTarget();
                        _currentClosure.getInstructions().add(new Instruction(OpCode.OP_COMPARE, _currentClosure.pushTarget(-1), op1, op2, Instruction.COP_LESSER_EQUALS));
                    }
                    break;
                case '>':
                    {
                        nextToken(lex);
                        processBitwiseShiftExpression(lex);

                        int op1 = _currentClosure.popTarget();
                        int op2 = _currentClosure.popTarget();
                        _currentClosure.getInstructions().add(new Instruction(OpCode.OP_COMPARE, _currentClosure.pushTarget(-1), op1, op2, Instruction.COP_GREATER));
                    }
                    break;
                case '<':
                    {
                        nextToken(lex);
                        processBitwiseShiftExpression(lex);

                        int op1 = _currentClosure.popTarget();
                        int op2 = _currentClosure.popTarget();
                        _currentClosure.getInstructions().add(new Instruction(OpCode.OP_COMPARE, _currentClosure.pushTarget(-1), op1, op2, Instruction.COP_LESSER));
                    }
                    break;
                default:
                    return;
            }
        }
    }
    
    private void processBitwiseShiftExpression(Lexer lex) throws IOException, CompileException
    {
        processAdditionExpression(lex);
        
        while (true)
        {
            switch (_token.getValue())
            {
                case Lexer.TK_BITSHIFT_LEFT:
                    {
                        nextToken(lex);
                        processAdditionExpression(lex);

                        int op1 = _currentClosure.popTarget();
                        int op2 = _currentClosure.popTarget();
                        _currentClosure.getInstructions().add(new Instruction(OpCode.OP_BITWISE, _currentClosure.pushTarget(-1), op1, op2, Instruction.BW_SHIFTL));
                    }
                    break;
                case Lexer.TK_BITSHIFT_RIGHT:
                    {
                        nextToken(lex);
                        processAdditionExpression(lex);

                        int op1 = _currentClosure.popTarget();
                        int op2 = _currentClosure.popTarget();
                        _currentClosure.getInstructions().add(new Instruction(OpCode.OP_BITWISE, _currentClosure.pushTarget(-1), op1, op2, Instruction.BW_SHIFTR));
                    }
                    break;
                default:
                    return;
            }
        }
    }
    
    private void processAdditionExpression(Lexer lex) throws IOException, CompileException
    {
        processMultiplicationExpression(lex);
        
        while (true)
        {
            switch (_token.getValue())
            {
                case '+':
                    {
                        nextToken(lex);
                        processMultiplicationExpression(lex);

                        int op1 = _currentClosure.popTarget();
                        int op2 = _currentClosure.popTarget();
                        _currentClosure.getInstructions().add(new Instruction(OpCode.OP_ADD, _currentClosure.pushTarget(-1), op1, op2, 0));
                    }
                    break;
                case '-':
                    {
                        nextToken(lex);
                        processMultiplicationExpression(lex);

                        int op1 = _currentClosure.popTarget();
                        int op2 = _currentClosure.popTarget();
                        _currentClosure.getInstructions().add(new Instruction(OpCode.OP_SUBTRACT, _currentClosure.pushTarget(-1), op1, op2, 0));
                    }
                    break;
                default:
                    return;
            }
        }
    }
    
    private void processMultiplicationExpression(Lexer lex) throws IOException, CompileException
    {
        processPrefixedExpression(lex);
        
        while (true)
        {
            switch (_token.getValue())
            {
                case '*':
                    {
                        nextToken(lex);
                        processPrefixedExpression(lex);

                        int op1 = _currentClosure.popTarget();
                        int op2 = _currentClosure.popTarget();
                        _currentClosure.getInstructions().add(new Instruction(OpCode.OP_MULTIPLY, _currentClosure.pushTarget(-1), op1, op2, 0));
                    }
                    break;
                case '/':
                    {
                        nextToken(lex);
                        processPrefixedExpression(lex);

                        int op1 = _currentClosure.popTarget();
                        int op2 = _currentClosure.popTarget();
                        _currentClosure.getInstructions().add(new Instruction(OpCode.OP_DIVIDE, _currentClosure.pushTarget(-1), op1, op2, 0));
                    }
                    break;
                case '%':
                    {
                        nextToken(lex);
                        processPrefixedExpression(lex);

                        int op1 = _currentClosure.popTarget();
                        int op2 = _currentClosure.popTarget();
                        _currentClosure.getInstructions().add(new Instruction(OpCode.OP_MODULO, _currentClosure.pushTarget(-1), op1, op2, 0));
                    }
                    break;
                default:
                    return;
            }
        }
    }
    
    private void processPrefixedExpression(Lexer lex) throws IOException, CompileException
    {
        int pos = factor(lex);
        
        while (true)
        {
            switch (_token.getValue())
            {
                case Lexer.TK_MINUSMINUS:
                case Lexer.TK_PLUSPLUS:
                {
                    if (isEndOfStatement(lex)) return;
                    
                    int diff = _token.getValue() == Lexer.TK_PLUSPLUS ? 1 : -1;
                    nextToken(lex);
                    
                    if (_expState.type == ExpressionState.Expression) EmitCompileError("Incrementing or decrementing an expression is not allowed.");
                    else if (_expState.type == ExpressionState.Local)
                    {
                        int src = _currentClosure.popTarget();
                        _currentClosure.getInstructions().add(new Instruction(OpCode.OP_INCREMENT, _currentClosure.pushTarget(-1), src, 0, diff));
                    }
                    else if (_expState.type == ExpressionState.Global)
                    {
                        int pos1 = _currentClosure.pushTarget(-1);
                        int pos2 = _currentClosure.pushTarget(-1);
                        
                        _currentClosure.getInstructions().add(new Instruction(OpCode.OP_GET, pos2, _expState.pos, 0, 0));
                        _currentClosure.getInstructions().add(new Instruction(OpCode.OP_INCREMENT, pos1, pos2, 0, diff));
                        _currentClosure.getInstructions().add(new Instruction(OpCode.OP_SET, pos2, _expState.pos, 0, 0));
                    }
                    else EmitCompileError("FATAL! UNKNOWN EXPRESSION STATE!");
                }
                    return;
                default:
                    return;
            }
        }
    }
    
    private int factor(Lexer lex) throws IOException, CompileException
    {
        _expState.type = ExpressionState.Expression;
        
        switch (_token.getValue())
        {
            case Lexer.TK_STRING_LITERAL:
                _currentClosure.getInstructions().add(new Instruction(OpCode.OP_LOAD, _currentClosure.pushTarget(-1), _currentClosure.getOrCreateLiteral(new ScriptObject(_token.getStringValue())), 0, 0));
                nextToken(lex);
                break;
            case Lexer.TK_NULL:
                _currentClosure.getInstructions().add(new Instruction(OpCode.OP_LOAD, _currentClosure.pushTarget(-1), _currentClosure.getOrCreateLiteral(new ScriptObject()), 0, 0));
                nextToken(lex);
                break;
            case Lexer.TK_FLOAT:
                _currentClosure.getInstructions().add(new Instruction(OpCode.OP_LOAD, _currentClosure.pushTarget(-1), _currentClosure.getOrCreateLiteral(new ScriptObject(_token.getFloatValue())), 0, 0));
                nextToken(lex);
                break;
            case Lexer.TK_INTEGER:
                _currentClosure.getInstructions().add(new Instruction(OpCode.OP_LOAD, _currentClosure.pushTarget(-1), _currentClosure.getOrCreateLiteral(new ScriptObject(_token.getIntegerValue())), 0, 0));
                nextToken(lex);
                break;
            case Lexer.TK_TRUE:
            case Lexer.TK_FALSE:
                _currentClosure.getInstructions().add(new Instruction(OpCode.OP_LOAD, _currentClosure.pushTarget(-1), _currentClosure.getOrCreateLiteral(new ScriptObject(_token.getValue() == Lexer.TK_TRUE ? 1 : 0)), 0, 0));
                nextToken(lex);
                break;
            case Lexer.TK_LOCAL_VARIABLE:
            {
                String name = _token.getStringValue();
                
                int pos = _currentClosure.getVariable(name);
                if (pos < 0) 
                {
                    pos = _currentClosure.pushVariable(name);
                    
                    //Implicitly load NULL
                    _currentClosure.getInstructions().add(new Instruction(OpCode.OP_LOAD, pos, _currentClosure.getOrCreateLiteral(new ScriptObject()), 0, 0));
                }
                
                _currentClosure.pushTarget(pos);
                _expState.type = ExpressionState.Local;
                _expState.pos = pos;
                
                nextToken(lex);
            }
                break;
            case Lexer.TK_GLOBAL_VARIABLE:
            {
                //TODO: Figure out how to handle this
                EmitCompileError("GLOBAL VAR not current supported!");
            }
                break;
            case '-':
                nextToken(lex);
                switch (_token.getValue())
                {
                    case Lexer.TK_INTEGER:
                        _currentClosure.getInstructions().add(new Instruction(OpCode.OP_LOAD, _currentClosure.pushTarget(-1), _currentClosure.getOrCreateLiteral(new ScriptObject(-_token.getIntegerValue())), 0, 0));
                        nextToken(lex);
                        break;
                    case Lexer.TK_FLOAT:
                        _currentClosure.getInstructions().add(new Instruction(OpCode.OP_LOAD, _currentClosure.pushTarget(-1), _currentClosure.getOrCreateLiteral(new ScriptObject(-_token.getFloatValue())), 0, 0));
                        nextToken(lex);
                        break;
                    default:
                        processUnaryOperation(lex, OpCode.OP_NEGATE);
                        break;
                }
                break;
            case '!':
                nextToken(lex);
                processUnaryOperation(lex, OpCode.OP_LOGICAL_NOT);
                break;
            case '~':
                nextToken(lex);
                switch (_token.getValue())
                {
                    case Lexer.TK_INTEGER:
                        _currentClosure.getInstructions().add(new Instruction(OpCode.OP_LOAD, _currentClosure.pushTarget(-1), _currentClosure.getOrCreateLiteral(new ScriptObject(~_token.getIntegerValue())), 0, 0));
                        nextToken(lex);
                        break;
                    default:
                    {
                        processPrefixedExpression(lex);
                        int src = _currentClosure.popTarget();
                        _currentClosure.getInstructions().add(new Instruction(OpCode.OP_BITWISE, _currentClosure.pushTarget(-1), 0, src, Instruction.BW_NOT));
                    }
                        break;
                }
                break;
            case Lexer.TK_PLUSPLUS:
            case Lexer.TK_MINUSMINUS:
            {
                int diff = _token.getValue() == Lexer.TK_PLUSPLUS ? 1 : -1;
                nextToken(lex);
                
                ExpressionState oldState = new ExpressionState(_expState);
                _expState.noGet = true;
                processPrefixedExpression(lex);
                
                if (_expState.type == ExpressionState.Expression) EmitCompileError("Incrementing or decrementing an expression is not allowed.");
                else if (_expState.type == ExpressionState.Local)
                {
                    int src = _currentClosure.topTarget();
                    _currentClosure.getInstructions().add(new Instruction(OpCode.OP_PREFIX_INCREMENT, _currentClosure.pushTarget(-1), src, 0, diff));
                }
                else if (_expState.type == ExpressionState.Global)
                {
                    int pos1 = _currentClosure.pushTarget(-1);

                    _currentClosure.getInstructions().add(new Instruction(OpCode.OP_GET, pos1, _expState.pos, 0, 0));
                    _currentClosure.getInstructions().add(new Instruction(OpCode.OP_INCREMENT, pos1, pos1, 0, diff));
                    _currentClosure.getInstructions().add(new Instruction(OpCode.OP_SET, pos1, _expState.pos, 0, 0));
                }
                else EmitCompileError("FATAL! UNKNOWN EXPRESSION STATE!");
                
                _expState = oldState;
            }
                break;
            case '(':
                nextToken(lex);
                processCommaExpression(lex);
                expectToken(lex, ')');
                break;
            default:
                EmitCompileError("Expression Expected.");
                break;
        }
        
        return -1;
    }
    
    private void processUnaryOperation(Lexer lex, OpCode op) throws IOException, CompileException
    {
        processPrefixedExpression(lex);
        int src = _currentClosure.popTarget();
        _currentClosure.getInstructions().add(new Instruction(op, _currentClosure.pushTarget(-1), src, 0, 0));
    }
}
