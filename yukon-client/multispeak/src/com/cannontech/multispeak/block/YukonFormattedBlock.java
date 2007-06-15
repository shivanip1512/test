package com.cannontech.multispeak.block;

import java.util.List;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.multispeak.service.FormattedBlock;

public interface YukonFormattedBlock <T extends Block> {

    public FormattedBlock getFormattedBlock(Meter meter);
    
    public FormattedBlock getFormattedBlock(List<Meter> meters);
    
    public FormattedBlock createFormattedBlock(T block);
    
    public FormattedBlock createFormattedBlock(List<T> block);
    
    public T getNewBlock();
}
