/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.ITHScript;

/**
 *
 * @author joshua
 */
public enum OpCode 
{
    OP_ERROR            (0x00), 
    OP_LOAD             (0x01),
    OP_LINE             (0x02),
    //OP_LOAD_FLOAT       (0x03),
    //OP_LOAD_BOOL        (0x04),
    OP_GET              (0x05),
    OP_SET              (0x06),
    OP_MOVE             (0x07),
    OP_COMPARE          (0x08),
    OP_ADD              (0x09),
    OP_SUBTRACT         (0x0A),
    OP_MULTIPLY         (0x0B),
    OP_DIVIDE           (0x0C),
    OP_MODULO           (0x0D),
    OP_BITWISE          (0x0E),
    OP_JUMP             (0x0F),
    OP_JUMP_COMPARE     (0x10),
    OP_NEGATE           (0x11),
    OP_INCREMENT        (0x12),
    OP_PREFIX_INCREMENT (0x13),
    OP_LOGICAL_AND      (0x14),
    OP_LOGICAL_OR       (0x15),
    OP_LOGICAL_NOT      (0x16),
    
    //Commands
    OP_PRINT            (0x40),
    OP_GOTO             (0x41),
    OP_CALL_BASE        (0x42);
    
    private final byte _value;
    OpCode(int value)
    {
        this._value = (byte)value;
    }
    
    protected byte value() { return _value; }
}
