package com.cannontech.multispeak.dao;

import java.util.List;

import com.cannontech.msp.beans.v3.MeterRead;
import com.google.common.collect.ImmutableList;

/**
 * Represents a chain of other MeterReadUpdater objects and abstracts them
 * as a single MeterReadUpdater instance. Is designed to efficiently
 * combine itself with other MeterReadUpdaterChain by directly copying 
 * other instances private data.
 */
public class MeterReadUpdaterChain implements MeterReadUpdater {
    
    private final List<MeterReadUpdater> chain;
    
    public MeterReadUpdaterChain() {
        chain = ImmutableList.of();
    }
    
    /**
     * Creates a single entry chain for a single MeterReadUpdater. This isn't useful
     * because the returned object is immutable and acts identically to the 
     * passed in object.
     * 
     * May be called with null which will create a no-op MeterReadUpdater.
     * 
     * @param meterReadUpdater a single updater, may be null
     */
    public MeterReadUpdaterChain(MeterReadUpdater meterReadUpdater) {
        this(meterReadUpdater, null);
    }

    /**
     * Creates a MeterReadUpdater chain that will first invoke the left's update
     * method and then invoke the right's update method. Either instance may be null in
     * which case it is simply ignored and a chain is created for the other element (or
     * an empty chain). If either instance happens to already be a chain, its elements 
     * will be copied into a single new chain.
     * 
     * @param left a MeterReadUpdater instance, may be null
     * @param right a MeterReadUpdater instance, may be null
     */
    public MeterReadUpdaterChain(MeterReadUpdater left, MeterReadUpdater right) {
        ImmutableList.Builder<MeterReadUpdater> chainBuilder = ImmutableList.builder();
        if (left != null) {
            if (left instanceof MeterReadUpdaterChain) {
                chainBuilder.addAll(((MeterReadUpdaterChain) left).chain);
            } else {
                chainBuilder.add(left);
            }
        }
        if (right != null) {
            if (right instanceof MeterReadUpdaterChain) {
                chainBuilder.addAll(((MeterReadUpdaterChain) right).chain);
            } else {
                chainBuilder.add(right);
            }
        }
        this.chain = chainBuilder.build();
    }
    
    @Override
    public void update(MeterRead meterRead) {
        for (MeterReadUpdater updater : chain) {
            updater.update(meterRead);
        }
    }

}
