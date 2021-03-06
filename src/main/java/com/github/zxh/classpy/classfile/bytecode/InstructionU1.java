package com.github.zxh.classpy.classfile.bytecode;

import com.github.zxh.classpy.classfile.constant.ConstantPool;

/**
 * The instruction whose operand is U1.
 */
public class InstructionU1 extends Instruction {

    {
        u1("opcode");
        u1("operand");
    }

    public InstructionU1(Opcode opcode, int pc) {
        super(opcode, pc);
    }

    @Override
    protected void afterRead(ConstantPool cp) {
        setDesc(getDesc() + " " + super.get("operand").getDesc());
    }

}
