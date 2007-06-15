package com.cannontech.multispeak.block.impl;

import com.cannontech.common.device.attribute.service.AttributeDynamicDataSource;
import com.cannontech.multispeak.block.Block;
import com.cannontech.multispeak.block.YukonFormattedBlock;
import com.cannontech.multispeak.block.data.FormattedBlockBase;
import com.cannontech.multispeak.service.FormattedBlock;

public abstract class YukonFormattedBlockImpl <T extends Block> implements YukonFormattedBlock <T>{

    public AttributeDynamicDataSource attrDynamicDataSource;
    
    public void setAttrDynamicDataSource(
            AttributeDynamicDataSource attrDynamicDataSource) {
        this.attrDynamicDataSource = attrDynamicDataSource;
    }
    
    public FormattedBlock createMspFormattedBlock(FormattedBlockBase blockBase) {
        FormattedBlock mspFormattedBlock = new FormattedBlock();
        mspFormattedBlock.setValueList(blockBase.getValList());
        mspFormattedBlock.setSeparator(blockBase.getValList().getSeparator());
        mspFormattedBlock.setValSyntax(blockBase.getValList().getValSyntax());
        return mspFormattedBlock;
    }
}
