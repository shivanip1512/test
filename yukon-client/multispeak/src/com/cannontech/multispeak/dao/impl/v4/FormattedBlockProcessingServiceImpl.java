package com.cannontech.multispeak.dao.impl.v4;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.msp.beans.v4.FormattedBlock;
import com.cannontech.multispeak.block.v4.Block;
import com.cannontech.multispeak.dao.v4.FormattedBlockProcessingService;
import com.cannontech.multispeak.dao.v4.FormattedBlockUpdater;

public abstract class FormattedBlockProcessingServiceImpl<T extends Block> implements FormattedBlockProcessingService<T> {

    private Map<BuiltInAttribute, ReadingProcessor<T>> attributesToLoad;

    public interface ReadingProcessor<T> {
        public void apply(PointValueHolder value, T reading);
    }

    @Override
    public FormattedBlockUpdater<T> buildFormattedBlockUpdater(BuiltInAttribute attribute,
            final PointValueHolder pointValueHolder) {
        final ReadingProcessor<T> processor = attributesToLoad.get(attribute);
        if (processor == null) {
            throw new IllegalArgumentException("Attribute " + attribute + " is not supported");
        }
        return new FormattedBlockUpdater<T>() {
            @Override
            public void update(T reading) {
                processor.apply(pointValueHolder, reading);
            }
        };
    }

    @Override
    public void updateFormattedBlock(T reading, BuiltInAttribute attribute, PointValueHolder pointValueHolder) {
        final ReadingProcessor<T> processor = attributesToLoad.get(attribute);
        if (processor == null) {
            throw new IllegalArgumentException("Attribute " + attribute + " is not supported");
        }
        processor.apply(pointValueHolder, reading);

    }

    @Override
    public EnumSet<BuiltInAttribute> getAttributeSet() {
        return EnumSet.copyOf(this.attributesToLoad.keySet());
    }

    @Override
    public FormattedBlock createMspFormattedBlock(T block) {
        List<T> blocks = Collections.singletonList(block);
        return createMspFormattedBlock(blocks);
    }

    public void setAttributesToLoad(Map<BuiltInAttribute, ReadingProcessor<T>> attributesToLoad) {
        this.attributesToLoad = attributesToLoad;
    }
}
