package com.cannontech.multispeak.dao.v4;

import java.util.List;

import com.cannontech.msp.beans.v4.MeterReading;
import com.google.common.collect.ImmutableList;


;

public class MeterReadUpdaterChain implements MeterReadUpdater {

    private final List<MeterReadUpdater> chain;

    public MeterReadUpdaterChain() {
        chain = ImmutableList.of();
    }
    /**
     * Creates a single entry chain for a single MeterReadUpdater. This isn't
     * useful because the returned object is immutable and acts identically to
     * the passed in object. May be called with null which will create a no-op
     * MeterReadUpdater.
     * @param meterReadUpdater a single updater, may be null
     */
    public MeterReadUpdaterChain(MeterReadUpdater meterReadUpdater) {
        this(meterReadUpdater, null);
    }

    /**
     * Creates a MeterReadUpdater chain that will first invoke the left's update
     * method and then invoke the right's update method. Either instance may be
     * null in which case it is simply ignored and a chain is created for the
     * other element (or an empty chain). If either instance happens to already
     * be a chain, its elements will be copied into a single new chain.
     * @param left a MeterReadUpdater instance, may be null
     * @param right a MeterReadUpdater instance, may be null
     */
    public MeterReadUpdaterChain(MeterReadUpdater... updaters) {
        ImmutableList.Builder<MeterReadUpdater> chainBuilder = ImmutableList.builder();
        for (MeterReadUpdater updater : updaters) {
            if (updater != null) {
                if (updater instanceof MeterReadUpdaterChain) {
                    chainBuilder.addAll(((MeterReadUpdaterChain) updater).chain);
                } else {
                    chainBuilder.add(updater);
                }
            }
        }
        this.chain = chainBuilder.build();
    }
    
    @Override
    public void update(MeterReading meterRead) {

        for (MeterReadUpdater updater : chain) {
            updater.update(meterRead);
        }
    
    }

}
