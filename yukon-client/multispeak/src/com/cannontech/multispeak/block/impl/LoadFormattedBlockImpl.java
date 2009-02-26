package com.cannontech.multispeak.block.impl;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.multispeak.block.data.load.LoadBlock;
import com.cannontech.multispeak.block.data.load.LoadValList;
import com.cannontech.multispeak.deploy.service.FormattedBlock;

public class LoadFormattedBlockImpl extends FormattedBlockServiceImpl <LoadBlock> {

	@Override
    public FormattedBlock getFormattedBlock(List<Meter> meters) {
        List<LoadBlock> loadBlockList = new ArrayList<LoadBlock>(meters.size());

        for (Meter meter : meters) {
            try {
                LoadBlock lb = getBlock(meter);
                loadBlockList.add(lb);
            } catch (IllegalArgumentException e) {}
        }
        
        LoadValList loadValList = new LoadValList(loadBlockList);
        return createMspFormattedBlock(loadValList);
    }

    @Override
    public FormattedBlock createFormattedBlock(List<LoadBlock> blocks) {
        LoadValList loadValList = new LoadValList(blocks);
        return createMspFormattedBlock(loadValList);
    }

    @Override
    public LoadBlock getNewBlock() {
        return new LoadBlock();
    }
    
    @Override
    public LoadBlock getBlock(Meter meter) {
        LoadBlock loadBlock = new LoadBlock();
        loadBlock.populate(meter, null);

        try {
            PointValueHolder loadProfile =
                attrDynamicDataSource.getPointValue(meter, BuiltInAttribute.LOAD_PROFILE);
            loadBlock.populate(meter, loadProfile);
        } catch (IllegalArgumentException e) {
            CTILogger.debug("Ignoring Exception:" + e.getMessage());
        } catch (NotFoundException e) {
            CTILogger.debug("Ignoring Exception:" + e.getMessage());
        }
        
        try {
            PointValueHolder kVar =
                attrDynamicDataSource.getPointValue(meter, BuiltInAttribute.KVAR);
            loadBlock.populate(meter, kVar);

        } catch (IllegalArgumentException e) {
            CTILogger.debug("Ignoring Exception:" + e.getMessage());
        } catch (NotFoundException e) {
            CTILogger.debug("Ignoring Exception:" + e.getMessage());
        }      
        
        try {
            PointValueHolder voltage =
                attrDynamicDataSource.getPointValue(meter, BuiltInAttribute.VOLTAGE);
            loadBlock.populate(meter, voltage);

        } catch (IllegalArgumentException e) {
            CTILogger.debug("Ignoring Exception:" + e.getMessage());
        } catch (NotFoundException e) {
            CTILogger.debug("Ignoring Exception:" + e.getMessage());
        }      

        return loadBlock;
    }
}
