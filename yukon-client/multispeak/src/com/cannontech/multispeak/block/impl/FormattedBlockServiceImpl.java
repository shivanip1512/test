package com.cannontech.multispeak.block.impl;

import com.cannontech.common.device.attribute.service.AttributeDynamicDataSource;
import com.cannontech.multispeak.block.Block;
import com.cannontech.multispeak.block.FormattedBlockService;
import com.cannontech.multispeak.block.data.FormattedBlockBase;
import com.cannontech.multispeak.deploy.service.FormattedBlock;

public abstract class FormattedBlockServiceImpl <T extends Block> implements FormattedBlockService <T>{

    public AttributeDynamicDataSource attrDynamicDataSource;
    
    public void setAttrDynamicDataSource(
            AttributeDynamicDataSource attrDynamicDataSource) {
        this.attrDynamicDataSource = attrDynamicDataSource;
    }
    
    public FormattedBlock createMspFormattedBlock(FormattedBlockBase blockBase) {
        FormattedBlock mspFormattedBlock = new FormattedBlock();
        mspFormattedBlock.setValueList(blockBase.getValueList());
        mspFormattedBlock.setSeparator(blockBase.getSeparator());
        mspFormattedBlock.setValSyntax(blockBase.getValSyntax());
        return mspFormattedBlock;
    }
}
