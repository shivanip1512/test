package com.cannontech.multispeak.block.impl;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.multispeak.block.data.load.LoadBlock;
import com.cannontech.multispeak.block.data.load.LoadValList;
import com.cannontech.multispeak.deploy.service.FormattedBlock;

public class LoadFormattedBlockImpl extends FormattedBlockServiceImpl <LoadBlock> {

    public FormattedBlock getFormattedBlock( Meter meter) {
        
        LoadBlock loadBlock = new LoadBlock();
        
        try {
            loadBlock = getBlock(meter);
        } catch (IllegalArgumentException e) {}

        LoadValList loadValList = new LoadValList(loadBlock);
        return createMspFormattedBlock(loadValList);
    }
    
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
    
    public FormattedBlock createFormattedBlock( LoadBlock loadBlock) {
        ArrayList<LoadBlock> block = new ArrayList<LoadBlock>();
        block.add(loadBlock);
        return createFormattedBlock(block);
    }
    
    public FormattedBlock createFormattedBlock(List<LoadBlock> block) {
        LoadValList loadValList = new LoadValList(block);
        return createMspFormattedBlock(loadValList);
    }

    public LoadBlock getNewBlock() {
        return new LoadBlock();
    }
    
    public LoadBlock getBlock(Meter meter) {
        LoadBlock loadBlock = new LoadBlock();
        loadBlock.populate(meter);

        try {
            PointValueHolder loadProfile =
                attrDynamicDataSource.getPointValue(meter, BuiltInAttribute.LOAD_PROFILE);
            loadBlock.populate(meter, loadProfile);
        } catch (IllegalArgumentException e) {}      
        
        try {
            PointValueHolder kVar =
                attrDynamicDataSource.getPointValue(meter, BuiltInAttribute.KVAR);
            loadBlock.populate(meter, kVar);

        } catch (IllegalArgumentException e) {}
        
        try {
            PointValueHolder voltage =
                attrDynamicDataSource.getPointValue(meter, BuiltInAttribute.VOLTAGE);
            loadBlock.populate(meter, voltage);

        } catch (IllegalArgumentException e) {}
        return loadBlock;
    }
}
