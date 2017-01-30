package com.cannontech.multispeak.block.data.outage.v5;

import java.util.Collections;
import java.util.List;

import com.cannontech.multispeak.block.data.v5.FormattedBlockBase;
import com.cannontech.multispeak.block.syntax.v5.SyntaxItem;

public class OutageValList extends FormattedBlockBase {

    public static SyntaxItem[] syntaxItems = new SyntaxItem[] {
        SyntaxItem.METER_NUMBER,
        SyntaxItem.BLINK_COUNT,
        SyntaxItem.BLINK_COUNT_DATETIME
    };

    public OutageValList(List<OutageBlock> blocks) {
        super(blocks);
    }

    public OutageValList(OutageBlock outageBlock) {
        this(Collections.singletonList(outageBlock));
    }

    @Override
    public SyntaxItem[] getSyntaxItems() {
        return syntaxItems;
    }
}
