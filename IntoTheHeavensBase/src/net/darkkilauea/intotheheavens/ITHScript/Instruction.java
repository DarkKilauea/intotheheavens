/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.ITHScript;

/**
 *
 * @author joshua
 */
public class Instruction 
{
    protected static final int COP_NONE = 0;
    protected static final int COP_EQUALS = 1;
    protected static final int COP_NOTEQUAL = 2;
    protected static final int COP_GREATER = 3;
    protected static final int COP_GREATER_EQUALS = 4;	
    protected static final int COP_LESSER = 5;
    protected static final int COP_LESSER_EQUALS = 6;
    
    protected static final int BW_NONE = 0;
    protected static final int BW_AND = 1;
    protected static final int BW_OR = 2;
    protected static final int BW_XOR = 3;
    protected static final int BW_SHIFTL = 4;
    protected static final int BW_SHIFTR = 5;
    protected static final int BW_NOT = 6;
    
    protected OpCode _op;
    protected int _arg0;
    protected int _arg1;
    protected int _arg2;
    protected int _arg3;
    
    protected Instruction(OpCode op, int arg0, int arg1, int arg2, int arg3)
    {
        _op = op;
        _arg0 = arg0;
        _arg1 = arg1;
        _arg2 = arg2;
        _arg3 = arg3;
    }
    
    protected void SetArgs(int arg0, int arg1, int arg2, int arg3)
    {
        _arg0 = arg0;
        _arg1 = arg1;
        _arg2 = arg2;
        _arg3 = arg3;
    }

    @Override
    public String toString() 
    {
        return _op + ": " + _arg0 + ", " + _arg1 + ", " + _arg2 + ", " + _arg3;
    }
}
