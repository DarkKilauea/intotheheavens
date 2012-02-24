/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.ITHScript;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author joshua
 */
public class VirtualMachine 
{
    private List<IVirtualMachineListener> _listeners = new ArrayList<IVirtualMachineListener>();
    private List<Variable> _globals = new ArrayList<Variable>();
    private int _stackSize = 64;
    
    public boolean registerListener(IVirtualMachineListener listener)
    {
        return _listeners.add(listener);
    }
    
    public boolean unregisterListener(IVirtualMachineListener listener)
    {
        return _listeners.remove(listener);
    }
    
    public List<Variable> getGlobals()
    {
        return _globals;
    }
    
    public void setGlobals(List<Variable> globals)
    {
        _globals = globals;
    }

    public int getStackSize() 
    {
        return _stackSize;
    }

    public void setStackSize(int stackSize) 
    {
        _stackSize = stackSize;
    }
    
    public void executeClosure(Closure closure, List<Variable> args) throws ScriptException
    {
        for (IVirtualMachineListener listener : _listeners) 
        {
            listener.onInvokePrint(closure.toString(true));
        }
        
        Stack<Integer> _scopes = new Stack<Integer>();
        int currentScope = 0;
        _scopes.push(currentScope);
        
        ScriptObject[] stack = new ScriptObject[_stackSize];
        Instruction i = null;
        int current = 0;
        int curLine = 1;
        try
        {
            while (current < closure.getInstructions().size())
            {
                i = closure.getInstructions().get(current);

                switch (i._op)
                {
                    case OP_LOAD:
                        stack[currentScope + i._arg0] = closure.getLiterals().get(i._arg1);
                        break;
                    case OP_LINE:
                        curLine++;
                        break;
                    case OP_GET:
                        stack[currentScope + i._arg0] = getVariable(closure.getLiterals().get(i._arg1), args);
                        break;
                    case OP_SET:
                        setVariable(closure.getLiterals().get(i._arg1), stack[currentScope + i._arg0], args);
                        break;
                    case OP_MOVE:
                        stack[currentScope + i._arg0] = stack[currentScope + i._arg1];
                        break;
                    case OP_COMPARE:
                        boolean res = compareObjects(stack[currentScope + i._arg2], stack[currentScope + i._arg1], i._arg3);
                        stack[currentScope + i._arg0] = new ScriptObject(res ? 1 : 0);
                        break;
                    case OP_ADD:
                    case OP_SUBTRACT:
                    case OP_MULTIPLY:
                    case OP_DIVIDE:
                    case OP_MODULO:
                        stack[currentScope + i._arg0] = performArithmetic(i._op, stack[currentScope + i._arg2], stack[currentScope + i._arg1]);
                        break;
                    case OP_BITWISE:
                        stack[currentScope + i._arg0] = performBitwiseOperation(stack[currentScope + i._arg2], stack[currentScope + i._arg1], i._arg3);
                        break;
                    case OP_JUMP:
                    {
                        int[] tuple = jumpToInstruction(closure, i._arg0, current, curLine);
                        current = tuple[0];
                        curLine = tuple[1];
                    }
                        break;
                    case OP_JUMP_COMPARE:
                        if (!IsTruth(stack[currentScope + i._arg1]))
                        {
                            int[] tuple = jumpToInstruction(closure, i._arg0, current, curLine);
                            current = tuple[0];
                            curLine = tuple[1];
                        }
                        break;
                    case OP_NEGATE:
                        stack[currentScope + i._arg0] = negateObject(stack[currentScope + i._arg1]);
                        break;
                    case OP_INCREMENT:
                    {
                        ScriptObject tmp = stack[currentScope + i._arg1];
                        stack[currentScope + i._arg1] = performArithmetic(OpCode.OP_ADD, stack[currentScope + i._arg1], new ScriptObject(i._arg3));
                        stack[currentScope + i._arg0] = tmp;
                    }
                        break;
                    case OP_PREFIX_INCREMENT:
                    {
                        stack[currentScope + i._arg1] = performArithmetic(OpCode.OP_ADD, stack[currentScope + i._arg1], new ScriptObject(i._arg3));
                        stack[currentScope + i._arg0] = stack[currentScope + i._arg1];
                    }
                        break;
                    case OP_LOGICAL_AND:
                        if (!IsTruth(stack[currentScope + i._arg2]))
                        {
                            stack[currentScope + i._arg0] = stack[currentScope + i._arg2];
                            int[] tuple = jumpToInstruction(closure, i._arg1, current, curLine);
                            current = tuple[0];
                            curLine = tuple[1];
                        }
                        break;
                    case OP_LOGICAL_OR:
                        if (IsTruth(stack[currentScope + i._arg2]))
                        {
                            stack[currentScope + i._arg0] = stack[currentScope + i._arg2];
                            int[] tuple = jumpToInstruction(closure, i._arg1, current, curLine);
                            current = tuple[0];
                            curLine = tuple[1];
                        }
                        break;
                    case OP_LOGICAL_NOT:
                        stack[currentScope + i._arg0] = new ScriptObject(!IsTruth(stack[currentScope + i._arg1]) ? 1 : 0);
                        break;
                    case OP_PRINT:
                        for (IVirtualMachineListener listener : _listeners) 
                        {
                            listener.onInvokePrint(stack[currentScope + i._arg0].toString());
                        }
                        break;
                    case OP_GOTO:
                        for (IVirtualMachineListener listener : _listeners) 
                        {
                            listener.onInvokeGoto(stack[currentScope + i._arg0].toString());
                        }
                        break;
                }
                current++;
            }
        }
        catch (Exception ex)
        {
            throw new ScriptException(ex.getMessage(), curLine, closure, closure.getLocation());
        }
    }
    
    private int[] jumpToInstruction(Closure closure, int delta, int current, int currentLine)
    {
        int dest = current + delta;
        int dir = delta < 0 ? -1 : 1;
        
        while (current != dest) 
        {
            Instruction i = closure.getInstructions().get(current);
            if (i._op == OpCode.OP_LINE) currentLine += dir;
            
            current += dir;
        }
        
        int[] tuple = new int[2];
        tuple[0] = dest;
        tuple[1] = currentLine;
        return tuple;
    }
    
    private boolean compareObjects(ScriptObject left, ScriptObject right, int cmp) throws Exception
    {
        int res = -1;
        
        if (left._type == right._type)
        {
            switch (left._type)
            {
                case ScriptObject.SOT_STRING:
                    res = left._stringValue.compareTo(right._stringValue);
                    break;
                case ScriptObject.SOT_INTEGER:
                    res = ((Integer)left._intValue).compareTo((Integer)right._intValue);
                    break;
                case ScriptObject.SOT_FLOAT:
                    res = ((Double)left._floatValue).compareTo((Double)right._floatValue);
                    break;
                case ScriptObject.SOT_NULL:
                    res = 0;
                    break;
                default:
                    res = -1;
                    break;
            }
        }
        else
        {
            if (left._type == ScriptObject.SOT_INTEGER && right._type == ScriptObject.SOT_FLOAT)
            {
                if (left._intValue == right._floatValue) res = 0;
                else if (left._intValue < right._floatValue) res = -1;
                else res = 1;
            }
            else if (left._type == ScriptObject.SOT_FLOAT && right._type == ScriptObject.SOT_INTEGER)
            {
                if (left._floatValue == right._intValue) res = 0;
                else if (left._floatValue < right._intValue) res = -1;
                else res = 1;
            }
            else if (left._type == ScriptObject.SOT_NULL) res = -1;
            else if (right._type == ScriptObject.SOT_NULL) res = 1;
            else throw new Exception("Comparison between " + left._type + " and " + right._type + " not allowed.");
        }
        
        switch (cmp)
        {
            case Instruction.COP_EQUALS:
                return res == 0;
            case Instruction.COP_NOTEQUAL:
                return res != 0;
            case Instruction.COP_GREATER:
                return res > 0;
            case Instruction.COP_GREATER_EQUALS:
                return res >= 0;
            case Instruction.COP_LESSER:
                return res < 0;
            case Instruction.COP_LESSER_EQUALS:
                return res <= 0;
            default:
                return false;
        }
    }
    
    private ScriptObject performArithmetic(OpCode op, ScriptObject left, ScriptObject right) throws Exception
    {
        int typeMask = left._type | right._type;
                        
        switch (typeMask)
        {
            case ScriptObject.SOT_INTEGER:
                switch (op)
                {
                    case OP_ADD:
                        return new ScriptObject(left.toInt() + right.toInt());
                    case OP_SUBTRACT:
                        return new ScriptObject(left.toInt() - right.toInt());
                    case OP_MULTIPLY:
                        return new ScriptObject(left.toInt() * right.toInt());
                    case OP_DIVIDE:
                        if (right.toInt() == 0) throw new Exception("Cannot divide by zero!");
                        else 
                        {
                            if (left.toInt() % right.toInt() == 0)
                                return new ScriptObject(left.toInt() / right.toInt());
                            else
                                return new ScriptObject(left.toFloat() / right.toFloat());
                        }
                    case OP_MODULO:
                        if (right.toInt() == 0) throw new Exception("Cannot modulo by zero!");
                        else return new ScriptObject(left.toInt() % right.toInt());
                }
                break;
            case ScriptObject.SOT_FLOAT:
            case ScriptObject.SOT_INTEGER | ScriptObject.SOT_FLOAT:
                switch (op)
                {
                    case OP_ADD:
                        return new ScriptObject(left.toFloat() + right.toFloat());
                    case OP_SUBTRACT:
                        return new ScriptObject(left.toFloat() - right.toFloat());
                    case OP_MULTIPLY:
                        return new ScriptObject(left.toFloat() * right.toFloat());
                    case OP_DIVIDE:
                        if (right.toFloat() == 0.0) throw new Exception("Cannot divide by zero!");
                        else return new ScriptObject(left.toFloat() / right.toFloat());
                    case OP_MODULO:
                        if (right.toFloat() == 0.0) throw new Exception("Cannot modulo by zero!");
                        else return new ScriptObject(left.toFloat() % right.toFloat());
                }
                break;
            case ScriptObject.SOT_STRING:
            case ScriptObject.SOT_STRING | ScriptObject.SOT_INTEGER:
            case ScriptObject.SOT_STRING | ScriptObject.SOT_FLOAT:
                switch (op)
                {
                    case OP_ADD:
                        return new ScriptObject(left.toString() + right.toString());
                    case OP_SUBTRACT:
                        if (right._type == ScriptObject.SOT_INTEGER || right._type == ScriptObject.SOT_FLOAT)
                            return new ScriptObject(left.toString().substring(0, left.toString().length() - right.toInt()));
                        else
                            return new ScriptObject(left.toString().replaceAll(right.toString(), ""));
                    case OP_MULTIPLY:
                        if (right._type == ScriptObject.SOT_INTEGER || right._type == ScriptObject.SOT_FLOAT)
                        {
                            String output = "";
                            int count = right.toInt();
                            while (count-- > 0)
                            {
                                output += left.toString();
                            }
                            
                            return new ScriptObject(output);
                        }
                }
                break;  
        }
        
        String errorMsg = "";
        switch (op)
        {
            case OP_ADD:
                errorMsg += "Addition ";
                break;
            case OP_SUBTRACT:
                errorMsg += "Subtraction ";
                break;
            case OP_MULTIPLY:
                errorMsg += "Multiplication ";
                break;
            case OP_DIVIDE:
                errorMsg += "Division ";
                break;
            case OP_MODULO:
                errorMsg += "Modulo ";
                break;
        }
        
        throw new Exception(errorMsg + "between " + left.typeString() + " and " + right.typeString() + " not allowed.");
    }
    
    private ScriptObject performBitwiseOperation(ScriptObject left, ScriptObject right, int op) throws Exception
    {
        int typeMask = left._type | right._type;
        
        switch (typeMask)
        {
            case ScriptObject.SOT_INTEGER:
            case ScriptObject.SOT_FLOAT:
            case ScriptObject.SOT_INTEGER | ScriptObject.SOT_FLOAT:
                switch (op)
                {
                    case Instruction.BW_AND:
                        return new ScriptObject(left.toInt() & right.toInt());
                    case Instruction.BW_OR:
                        return new ScriptObject(left.toInt() | right.toInt());
                    case Instruction.BW_SHIFTL:
                        return new ScriptObject(left.toInt() << right.toInt());
                    case Instruction.BW_SHIFTR:
                        return new ScriptObject(left.toInt() >> right.toInt());
                    case Instruction.BW_XOR:
                        return new ScriptObject(left.toInt() ^ right.toInt());
                    case Instruction.BW_NOT:
                        return new ScriptObject(~left.toInt());
                }
                break;
        }
        
        String errorMsg = "";
        switch (op)
        {
            case Instruction.BW_AND:
                errorMsg += "AND";
                break;
            case Instruction.BW_OR:
                errorMsg += "OR";
                break;
            case Instruction.BW_SHIFTL:
                errorMsg += "SHIFT LEFT";
                break;
            case Instruction.BW_SHIFTR:
                errorMsg += "SHIFT RIGHT";
                break;
            case Instruction.BW_XOR:
                errorMsg += "EXCULSIVE OR";
                break;
            case Instruction.BW_NOT:
                errorMsg += "NOT";
                break;
        }
        
        if (op != Instruction.BW_NOT)
            throw new Exception("Bitwise " + errorMsg + " between " + left.typeString() + " and " + right.typeString() + " not allowed.");
        else
            throw new Exception("Bitwise " + errorMsg + " on " + left.typeString() + " not allowed.");
    }
    
    private boolean IsTruth(ScriptObject o)
    {
        if (o._type == ScriptObject.SOT_NULL) return false;
        else if (o._type == ScriptObject.SOT_INTEGER) return o.toInt() != 0;
        else if (o._type == ScriptObject.SOT_FLOAT) return o.toFloat() != 0.0;
        else if (o._type == ScriptObject.SOT_STRING) return o.toString().length() > 0;
        else return false;
    }
    
    private ScriptObject negateObject(ScriptObject o) throws Exception
    {
        if (o._type == ScriptObject.SOT_INTEGER) return new ScriptObject(-o.toInt());
        else if (o._type == ScriptObject.SOT_FLOAT) return new ScriptObject(-o.toFloat());
        else throw new Exception("Cannot negate a " + o.typeString() + ".");
    }
    
    private Variable getVariable(ScriptObject name, List<Variable> args) throws Exception
    {
        if (name._type == ScriptObject.SOT_STRING)
        {
            String varName = name.toString();
            
            for (Variable var : args)
            {
                if (var.getName().equals(varName)) return var;
            }
            
            for (Variable var : _globals)
            {
                if (var.getName().equals(varName)) return var;
            }
            
            throw new Exception("Couldn't find a variable with name " + varName + "!");
        }
        else throw new Exception("Cannot get variable with a name that is not a string!");
    }
    
    private void setVariable(ScriptObject name, ScriptObject value, List<Variable> args) throws Exception
    {
        Variable var = getVariable(name, args);
        
        switch (value._type)
        {
            case ScriptObject.SOT_INTEGER:
                var.setValue(value.toInt());
                break;
            case ScriptObject.SOT_FLOAT:
                var.setValue(value.toFloat());
                break;
            case ScriptObject.SOT_STRING:
                var.setValue(value.toString());
                break;
            default:
                var.setNull();
                break;
        }
    }
}
