package com.cannontech.multispeak.block.data.load;

import java.util.List;

import com.cannontech.multispeak.block.data.FormattedBlockValueListBase;
import com.cannontech.multispeak.block.syntax.SyntaxItem;

public class LoadValList extends FormattedBlockValueListBase{

    public static SyntaxItem[] syntaxItem = new SyntaxItem[]{
            SyntaxItem.METER_NUMBER,
            SyntaxItem.LOAD_PROFILE_DEMAND,
            SyntaxItem.LOAD_PROFILE_DEMAND_DATETIME
    };

    public LoadValList(List<LoadBlock> block) {
        super(block, syntaxItem);
    }
    
    public SyntaxItem[] getSyntaxItem() {
        return syntaxItem;
    }
}
