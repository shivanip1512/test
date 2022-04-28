package com.cannontech.multispeak.data.v4;

import java.util.List;

import com.cannontech.multispeak.block.v4.Block;

public class MspBlockReturnList extends MspMeterReadingReturnList {

    private List<Block> blocks;

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

}
