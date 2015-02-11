package com.cannontech.multispeak.block.data.outage;

import java.util.Collections;
import java.util.List;

import com.cannontech.multispeak.block.data.FormattedBlockBase;
import com.cannontech.multispeak.block.syntax.SyntaxItem;

public class OutageValList extends FormattedBlockBase{

    public static SyntaxItem[] syntaxItems = new SyntaxItem[]{
            SyntaxItem.METER_NUMBER,
            SyntaxItem.BLINK_COUNT,
            SyntaxItem.BLINK_COUNT_DATETIME
    };

    public OutageValList(List<OutageBlock> block) {
        super(block);
    }
    
    public OutageValList(OutageBlock outageBlock) {
        this(Collections.singletonList(outageBlock));
    }
    
    @Override
    public SyntaxItem[] getSyntaxItems() {
        return syntaxItems;
    }
}
