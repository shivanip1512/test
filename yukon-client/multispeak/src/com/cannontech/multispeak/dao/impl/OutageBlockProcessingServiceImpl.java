package com.cannontech.multispeak.dao.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.multispeak.block.data.FormattedBlockBase;
import com.cannontech.multispeak.block.data.outage.OutageBlock;
import com.cannontech.multispeak.block.data.outage.OutageValList;
import com.cannontech.multispeak.deploy.service.FormattedBlock;
import com.google.common.collect.ImmutableMap;

public class OutageBlockProcessingServiceImpl  extends FormattedBlockProcessingServiceImpl<OutageBlock> {

    @PostConstruct
    public void setup() {

        ReadingProcessor<OutageBlock> blinkCountConverter = new ReadingProcessor<OutageBlock>() {
            @Override
            public void apply(PointValueHolder value, OutageBlock reading) {
                reading.setBlinkCount(value);
            }
        };

        Map<BuiltInAttribute, ReadingProcessor<OutageBlock>> attributesToLoad =  
            ImmutableMap.of(BuiltInAttribute.BLINK_COUNT, blinkCountConverter
                            );
        
        setAttributesToLoad(attributesToLoad);
    }
    
    @Override
    public OutageBlock createBlock(Meter meter) {
        OutageBlock block = new OutageBlock();
        block.setMeterNumber(meter.getMeterNumber());
        return block;
    }

    @Override
    public FormattedBlock createMspFormattedBlock(List<OutageBlock> blocks) {
        OutageValList outageValList = new OutageValList(blocks);
        return FormattedBlockBase.createMspFormattedBlock(outageValList);
    }
}
