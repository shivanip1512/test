package com.cannontech.multispeak.block.data.load.v4;

import java.util.Collections;
import java.util.List;
import com.cannontech.multispeak.block.data.v4.FormattedBlockBase;
import com.cannontech.multispeak.block.syntax.v4.SyntaxItem;

public class LoadValList extends FormattedBlockBase{

    public static SyntaxItem[] syntaxItems = new SyntaxItem[]{
            SyntaxItem.METER_NUMBER,
            SyntaxItem.LOAD_PROFILE_DEMAND,
            SyntaxItem.LOAD_PROFILE_DEMAND_DATETIME,
            SyntaxItem.KVAR,
            SyntaxItem.KVAR_DATETIME,
            SyntaxItem.VOLTAGE,
            SyntaxItem.VOLTAGE_DATETIME,
            SyntaxItem.VOLTAGE_PROFILE,
            SyntaxItem.VOLTAGE_PROFILE_DATETIME
    };

    public LoadValList(List<LoadBlock> block) {
        super(block);
    }

    public LoadValList(LoadBlock loadBlock) {
        this(Collections.singletonList(loadBlock));
    }
    
    @Override
    public SyntaxItem[] getSyntaxItems() {
        return syntaxItems;
    }
}
