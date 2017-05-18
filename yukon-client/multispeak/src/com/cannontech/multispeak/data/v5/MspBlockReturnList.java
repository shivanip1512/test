package com.cannontech.multispeak.data.v5;

import java.util.List;

import com.cannontech.multispeak.block.v5.Block;
import com.cannontech.multispeak.data.MspReturnList;

public class MspBlockReturnList extends MspReturnList {

    private List<Block> blocks;

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

}
