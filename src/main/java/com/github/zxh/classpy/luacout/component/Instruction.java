package com.github.zxh.classpy.luacout.component;

import com.github.zxh.classpy.luacout.LuacOutComponent;
import com.github.zxh.classpy.luacout.LuacOutReader;
import com.github.zxh.classpy.luacout.lvm.OpCode;

/**
 * LuaVM instruction.
 * 31       22       13       5    0
 *  +-------+^------+-^-----+-^-----
 *  |b=9bits |c=9bits |a=8bits|opc6|
 *  +-------+^------+-^-----+-^-----
 *  |    bx=18bits    |a=8bits|opc6|
 *  +-------+^------+-^-----+-^-----
 *  |   sbx=18bits    |a=8bits|opc6|
 *  +-------+^------+-^-----+-^-----
 *  |    ax=26bits            |opc6|
 *  +-------+^------+-^-----+-^-----
 * 31      23      15       7      0
 */
public class Instruction extends LuacOutComponent {

    private int code;
    private OpCode opcode;
    private Function func;

    private int a()   {return code  >>  6 & 0b0_1111_1111;} //  8 bits
    private int c()   {return code  >> 14 & 0b1_1111_1111;} //  9 bits
    private int b()   {return code  >> 23 & 0b1_1111_1111;} //  9 bits
    private int ax()  {return code >>>  6;                } // 26 bits
    private int bx()  {return code >>> 14;                } // 18 bits
    private int sbx() {return code  >> 14;                } // 18 bits & signed

    @Override
    protected void readContent(LuacOutReader reader) {
        code = reader.readInt();
        opcode = OpCode.values()[code & 0b11_1111];
        super.setName(opcode.name());
    }

    public void expandOperands(Function func) {
        this.func = func;
        addKid(opcode.description);
        switch (opcode) {
            case OP_RETURN:
                expandReturn();
                break;
            default:
                expandRx("R(A)", a());
                expandRx("R(B)", b());
                expandRx("R(C)", c());
                expandRKx("RK(B)", b());
                expandRKx("RK(C)", c());
                break;
        }
    }

    // return R(A), ... ,R(A+B-2)
    private void expandReturn() {
        int a = a();
        int b = b();

        addKid("A => " + a);
        addKid("B => " + b);

        for (int i = a; i <= a + b - 2; i++) {
            addKid("R(" + (i) + ") => " + func.getLocVarName(i));
        }
    }

    private void expandRx(String rx, int x) {
        if (opcode.description.contains(rx)) {
            rx += " => R(" + x + ")";
            rx += " => " + func.getLocVarName(x);
            addKid(rx);
        }
    }

    private void expandRKx(String rkx, int x) {
        if (opcode.description.contains(rkx)) {
            rkx += " => RK(" + x + ")";

            if (x > 0xFF) { // constant
                rkx += " => Kst(" + (x & 0xFF) + ")";
                rkx += " => " + func.getConstant(x & 0xFF).getDesc();
            } else { // register
                rkx += " => R(" + x + ")";
                rkx += " => " + func.getLocVarName(x);
            }

            addKid(rkx);
        }
    }

    private void addKid(String name) {
        super.add(name, new LuacOutComponent());
    }

    /*
    private String describe(OpCode opcode) {
        switch (opcode) {
            case OP_LOADNIL:  return describeLoadNil();
            case OP_CALL:     return describeCall();
            case OP_TAILCALL: return describeTailCall();
            case OP_RETURN:   return describeReturn();
            case OP_TFORCALL: return describeTFORCALL();
            case OP_VARARG:   return describeVarArg();
            default:          return describeOther(opcode);
        }
    }

    // OP_LOADNIL("AB", "R(A), R(A+1), ..., R(A+B) := nil")
    private String describeLoadNil() {
        final int a = a();
        final int b = b();
        return rList(a, a+b) + " := nil";
    }

    // OP_CALL("ABC", "R(A), ... ,R(A+C-2) := R(A)(R(A+1), ... ,R(A+B-1)) ")
    private String describeCall() {
        final int a = a();
        final int b = b();
        final int c = c();
        return rList(a, a+c-2) + " := R(" + a + ")(" + rList(a+1, a+b-1) + ")";
    }

    // OP_TAILCALL("ABC", "return R(A)(R(A+1), ... ,R(A+B-1))"),
    private String describeTailCall() {
        final int a = a();
        final int b = b();
        return "return R(" + a + ")(" + rList(a+1, a+b-1) + ")";
    }

    // OP_RETURN("AB", "return R(A), ... ,R(A+B-2)"),
    private String describeReturn() {
        final int a = a();
        final int b = b();
        return "return " + rList(a, a+b-2);
    }

    // OP_TFORCALL("AC", "R(A+3), ... ,R(A+2+C) := R(A)(R(A+1), R(A+2));"),
    private String describeTFORCALL() {
        final int a = a();
        final int c = c();
        return rList(a+3, a+2+c) + " := R(" + a + ")(" + rList(a+1, a+2) + ");";
    }

    // OP_VARARG("AB", "R(A), R(A+1), ..., R(A+B-2) = vararg"),
    private String describeVarArg() {
        final int a = a();
        final int b = b();
        return rList(a, a+b-2) + " = vararg";
    }

    private static String rList(int from, int to) {
        switch (to - from) {
            case 0:  return String.format("R(%d)", from);
            case 1:  return String.format("R(%d), R(%d)", from, from+1);
            case 2:  return String.format("R(%d), R(%d), R(%d)", from, from+1, from+2);
            default: return String.format("R(%d), R(%d), ..., R(%d)", from, from+1, to);
        }
    }
    */

    // enum OpMode {iABC, iABx, iAsBx, iAx};  /* basic instruction format */
    /*
    private String describeOther() {
        String description = opcode.description;
        description = replaceRK(description);
        if (opcode.args.contains("Ax")) {
            description = description.replace("Ax", Integer.toString(ax()));
        } else if (opcode.args.contains("A")) {
            description = description.replace("A", Integer.toString(a()));
        }
        if (opcode.args.contains("sBx")) {
            description = description.replace("sBx", Integer.toString(sbx()));
        } else if (opcode.args.contains("Bx")) {
            description = description.replace("Bx", Integer.toString(bx()));
        } else if (opcode.args.contains("B")) {
            description = description.replace("B", Integer.toString(b()));
        }
        if (opcode.args.contains("C")) {
            description = description.replace("C", Integer.toString(c()));
        }
        return description;
    }

    private String replaceRK(String description) {
        if (description.contains("RK(B)")) {
            description = description.replace("RK(B)", expandRK(b()));
        }
        if (description.contains("RK(C)")) {
            description = description.replace("RK(C)", expandRK(c()));
        }
        return description;
    }

    private String expandRK(int rk) {
        if (rk > 0xFF) { // constant
            return func.getConstant(rk & 0xFF).getDesc();
        } else { // register
            return "R(" + rk + ")";
        }
    }
    */
}
