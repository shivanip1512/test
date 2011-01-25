package com.cannontech.multispeak.block.impl;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.multispeak.block.data.load.LoadBlock;
import com.cannontech.multispeak.block.data.load.LoadValList;
import com.cannontech.multispeak.deploy.service.FormattedBlock;

public class LoadFormattedBlockImpl extends FormattedBlockServiceImpl <LoadBlock> {

	@Override
    public FormattedBlock getFormattedBlock(List<Meter> meters) {
        List<LoadBlock> loadBlockList = new ArrayList<LoadBlock>(meters.size());

        for (Meter meter : meters) {
            LoadBlock lb = getBlock(meter);
            loadBlockList.add(lb);
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

        populateBlock(meter, loadBlock, BuiltInAttribute.LOAD_PROFILE);
        populateBlock(meter, loadBlock, BuiltInAttribute.KVAR);
        populateBlock(meter, loadBlock, BuiltInAttribute.VOLTAGE);
        populateBlock(meter, loadBlock, BuiltInAttribute.VOLTAGE_PROFILE);

        return loadBlock;
    }

}