package com.cannontech.multispeak.block.data.status.v5;

import java.util.Collections;
import java.util.List;

import com.cannontech.multispeak.block.data.v5.FormattedBlockBase;
import com.cannontech.multispeak.block.syntax.v5.SyntaxItem;

public class ProgramStatusValList extends FormattedBlockBase {

    public static SyntaxItem[] syntaxItems = new SyntaxItem[] {
            SyntaxItem.PROGRAM_NAME,
            SyntaxItem.CURRENT_STATUS,
            SyntaxItem.START_DATETIME,
            SyntaxItem.STOP_DATETIME,
            SyntaxItem.GEAR_CHANGETIME,
            SyntaxItem.GEAR_NAME,
            SyntaxItem.CORRELATION_ID
        };
    
    public ProgramStatusValList(List<ProgramStatusBlock> block) {
        super(block);
    }
    
    public ProgramStatusValList(ProgramStatusBlock programStatusBlock) {
        this(Collections.singletonList(programStatusBlock));
    }

    @Override
    public SyntaxItem[] getSyntaxItems() {
        return syntaxItems;
    }

}
