package com.github.zxh.classpy.classfile.bytecode;

import com.github.zxh.classpy.classfile.constant.ConstantPool;

public class InvokeDynamic extends Instruction {

    {
        u1  ("opcode");
        u2cp("index");
        u2  ("zero");
    }

    public InvokeDynamic(Opcode opcode, int pc) {
        super(opcode, pc);
    }
    
    @Override
    protected void afterRead(ConstantPool cp) {
        setDesc(getDesc() + " " + super.get("index").getDesc());
    }
    
}
