package com.github.zxh.classpy.dexfile.ids;

import com.github.zxh.classpy.dexfile.DexComponent;
import com.github.zxh.classpy.dexfile.DexReader;
import com.github.zxh.classpy.dexfile.UInt;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author zxh
 */
public class MethodIdItem extends DexComponent {

    private UInt classIdx;
    private UInt protoIdx;
    private UInt nameIdx;
    
    @Override
    protected void readContent(DexReader reader) {
        classIdx = reader.readUInt();
        protoIdx = reader.readUInt();
        nameIdx = reader.readUInt();
    }

    @Override
    public List<? extends DexComponent> getSubComponents() {
        return Arrays.asList(classIdx, protoIdx, nameIdx);
    }
    
}