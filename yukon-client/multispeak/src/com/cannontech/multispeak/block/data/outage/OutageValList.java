package com.cannontech.multispeak.block.data.outage;

import java.util.List;

import com.cannontech.multispeak.block.data.FormattedBlockValueListBase;
import com.cannontech.multispeak.block.syntax.SyntaxItem;

public class OutageValList extends FormattedBlockValueListBase{

    public static SyntaxItem[] syntaxItem = new SyntaxItem[]{
            SyntaxItem.METER_NUMBER,
            SyntaxItem.BLINK_COUNT,
            SyntaxItem.BLINK_COUNT_DATETIME
    };

    public OutageValList(List<OutageBlock> block) {
        super(block, syntaxItem);
    }
    
    public SyntaxItem[] getSyntaxItem() {
        return syntaxItem;
    }
}
