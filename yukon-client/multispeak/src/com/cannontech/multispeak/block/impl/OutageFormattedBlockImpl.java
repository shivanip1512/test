package com.cannontech.multispeak.block.impl;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.multispeak.block.data.FormattedBlockBase;
import com.cannontech.multispeak.block.data.outage.OutageBlock;
import com.cannontech.multispeak.block.data.outage.OutageFormattedBlock;
import com.cannontech.multispeak.service.FormattedBlock;

public class OutageFormattedBlockImpl extends YukonFormattedBlockImpl <OutageBlock>{

    public FormattedBlock getFormattedBlock( Meter meter) {
        OutageBlock outageBlock = getNewBlock();
        
        try {
            outageBlock = getBlock(meter);
        } catch (IllegalArgumentException e){}
        
        FormattedBlockBase blockBase = new OutageFormattedBlock(outageBlock);
        return createMspFormattedBlock(blockBase);
    }
    
    public FormattedBlock getFormattedBlock(List<Meter> meters) {
        List<OutageBlock> outageBlockList = new ArrayList<OutageBlock>(meters.size());

        for (Meter meter : meters) {
            try {
                OutageBlock ob = getBlock(meter);
                outageBlockList.add(ob);
            } catch (IllegalArgumentException e) {}
        }
        
        FormattedBlockBase blockBase = new OutageFormattedBlock(outageBlockList);
        return createMspFormattedBlock(blockBase);
    }

    public FormattedBlock createFormattedBlock( OutageBlock outageBlock) {
        ArrayList<OutageBlock> block = new ArrayList<OutageBlock>();
        block.add(outageBlock);
        return createFormattedBlock(block);
    }
    
    public FormattedBlock createFormattedBlock(List<OutageBlock> block) {
        FormattedBlockBase blockBase = new OutageFormattedBlock(block);
        return createMspFormattedBlock(blockBase);
    }
    
    public OutageBlock getNewBlock() {
        return new OutageBlock();
    }
    
    public OutageBlock getBlock(Meter meter) {
        PointValueHolder outage = 
            attrDynamicDataSource.getPointValue(meter, BuiltInAttribute.BLINK_COUNT);
        
        OutageBlock outageBlock = new OutageBlock(meter.getMeterNumber(),
                                      outage.getValue(),
                                      outage.getPointDataTimeStamp()
                                      );
        return outageBlock;
    }
}
