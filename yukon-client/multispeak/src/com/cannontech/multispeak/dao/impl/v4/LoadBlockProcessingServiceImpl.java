package com.cannontech.multispeak.dao.impl.v4;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.msp.beans.v4.FormattedBlock;
import com.cannontech.multispeak.block.data.load.v4.LoadBlock;
import com.cannontech.multispeak.block.data.load.v4.LoadValList;
import com.cannontech.multispeak.block.data.v4.FormattedBlockBase;
import com.cannontech.multispeak.dao.impl.v4.FormattedBlockProcessingServiceImpl;
import com.google.common.collect.ImmutableMap;

public class LoadBlockProcessingServiceImpl extends FormattedBlockProcessingServiceImpl<LoadBlock> {

    @PostConstruct
    public void setup() {

        ReadingProcessor<LoadBlock> loadProfileConverter = new ReadingProcessor<LoadBlock>() {
            @Override
            public void apply(PointValueHolder value, LoadBlock reading) {
                reading.setLoadProfileDemand(value);
            }
        };

        ReadingProcessor<LoadBlock> kVarConverter = new ReadingProcessor<LoadBlock>() {
            @Override
            public void apply(PointValueHolder value, LoadBlock reading) {
                reading.setkVAr(value);
            }
        };

        ReadingProcessor<LoadBlock> voltageConverter = new ReadingProcessor<LoadBlock>() {
            @Override
            public void apply(PointValueHolder value, LoadBlock reading) {
                reading.setVoltage(value);
            }
        };

        ReadingProcessor<LoadBlock> voltageProfileConverter = new ReadingProcessor<LoadBlock>() {
            @Override
            public void apply(PointValueHolder value, LoadBlock reading) {
                reading.setVoltageProfile(value);
            }
        };

        Map<BuiltInAttribute, ReadingProcessor<LoadBlock>> attributesToLoad = ImmutableMap.of(BuiltInAttribute.LOAD_PROFILE, loadProfileConverter, 
                                                                                              BuiltInAttribute.KVAR, kVarConverter,
                                                                                              BuiltInAttribute.VOLTAGE, voltageConverter, 
                                                                                              BuiltInAttribute.VOLTAGE_PROFILE, voltageProfileConverter);

        setAttributesToLoad(attributesToLoad);
    }

    @Override
    public LoadBlock createBlock(YukonMeter meter) {
        LoadBlock block = new LoadBlock();
        block.setMeterNumber(meter.getMeterNumber());
        return block;
    }

    @Override
    public FormattedBlock createMspFormattedBlock(List<LoadBlock> blocks) {
        LoadValList loadValList = new LoadValList(blocks);
        return FormattedBlockBase.createMspFormattedBlock(loadValList);
    }
}
