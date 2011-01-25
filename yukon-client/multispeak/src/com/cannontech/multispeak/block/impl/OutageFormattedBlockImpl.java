package com.cannontech.multispeak.block.impl;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.multispeak.block.data.outage.OutageBlock;
import com.cannontech.multispeak.block.data.outage.OutageValList;
import com.cannontech.multispeak.deploy.service.FormattedBlock;

public class OutageFormattedBlockImpl extends FormattedBlockServiceImpl <OutageBlock>{

	@Override
    public FormattedBlock getFormattedBlock(List<Meter> meters) {
        List<OutageBlock> outageBlockList = new ArrayList<OutageBlock>(meters.size());

        for (Meter meter : meters) {
            OutageBlock ob = getBlock(meter);
            outageBlockList.add(ob);
        }
        
        return createFormattedBlock(outageBlockList);
    }

	@Override
    public FormattedBlock createFormattedBlock(List<OutageBlock> blocks) {
        OutageValList outageValList = new OutageValList(blocks);
        return createMspFormattedBlock(outageValList);
    }
    
	@Override
    public OutageBlock getNewBlock() {
        return new OutageBlock();
    }
    
    @Override
    public OutageBlock getBlock(Meter meter) {
        OutageBlock outageBlock = new OutageBlock();
        outageBlock.populate(meter, null);

        populateBlock(meter, outageBlock, BuiltInAttribute.BLINK_COUNT);

        return outageBlock;
    }
}
