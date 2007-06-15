package com.cannontech.multispeak.block.impl;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.multispeak.block.data.FormattedBlockBase;
import com.cannontech.multispeak.block.data.load.LoadBlock;
import com.cannontech.multispeak.block.data.load.LoadFormattedBlock;
import com.cannontech.multispeak.service.FormattedBlock;

public class LoadFormattedBlockImpl extends YukonFormattedBlockImpl <LoadBlock> {

    public FormattedBlock getFormattedBlock( Meter meter) {
        
        LoadBlock loadBlock = new LoadBlock();
        
        try {
            loadBlock = getBlock(meter);
        } catch (IllegalArgumentException e) {}

        FormattedBlockBase blockBase = new LoadFormattedBlock(loadBlock);
        return createMspFormattedBlock(blockBase);
    }
    
    public FormattedBlock getFormattedBlock(List<Meter> meters) {
        List<LoadBlock> loadBlockList = new ArrayList<LoadBlock>(meters.size());

        for (Meter meter : meters) {
            try {
                LoadBlock lb = getBlock(meter);
                loadBlockList.add(lb);
            } catch (IllegalArgumentException e) {}
        }
        
        FormattedBlockBase blockBase = new LoadFormattedBlock(loadBlockList);
        return createMspFormattedBlock(blockBase);
    }
    
    public FormattedBlock createFormattedBlock( LoadBlock loadBlock) {
        ArrayList<LoadBlock> block = new ArrayList<LoadBlock>();
        block.add(loadBlock);
        return createFormattedBlock(block);
    }
    
    public FormattedBlock createFormattedBlock(List<LoadBlock> block) {
        FormattedBlockBase blockBase = new LoadFormattedBlock(block);
        return createMspFormattedBlock(blockBase);
    }

    public LoadBlock getNewBlock() {
        return new LoadBlock();
    }
    
    public LoadBlock getBlock(Meter meter) {
        PointValueHolder loadProfile =
            attrDynamicDataSource.getPointValue(meter, BuiltInAttribute.LOAD_PROFILE);

        LoadBlock loadBlock = new LoadBlock(meter.getMeterNumber(),
                                            loadProfile.getValue(),
                                            loadProfile.getPointDataTimeStamp()
                                            );
        return loadBlock;
    }
}
