package com.cannontech.multispeak.block.impl;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.multispeak.block.data.outage.OutageBlock;
import com.cannontech.multispeak.block.data.outage.OutageValList;
import com.cannontech.multispeak.deploy.service.FormattedBlock;

public class OutageFormattedBlockImpl extends FormattedBlockServiceImpl <OutageBlock>{

	@Override
    public FormattedBlock getFormattedBlock(List<Meter> meters) {
        List<OutageBlock> outageBlockList = new ArrayList<OutageBlock>(meters.size());

        for (Meter meter : meters) {
            try {
                OutageBlock ob = getBlock(meter);
                outageBlockList.add(ob);
            } catch (IllegalArgumentException e) {}
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

        try {
            PointValueHolder outage = 
                attrDynamicDataSource.getPointValue(meter, BuiltInAttribute.BLINK_COUNT);
            outageBlock.populate(meter, outage);
        } catch (IllegalArgumentException e) {
            CTILogger.debug("Ignoring Exception:" + e.getMessage());
        } catch (NotFoundException e) {
            CTILogger.debug("Ignoring Exception:" + e.getMessage());
        }
        return outageBlock;
    }
}
