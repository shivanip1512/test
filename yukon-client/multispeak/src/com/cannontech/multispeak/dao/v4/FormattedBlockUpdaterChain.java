package com.cannontech.multispeak.dao.v4;

import java.util.List;

import com.cannontech.multispeak.block.v4.Block;
import com.google.common.collect.ImmutableList;

public class FormattedBlockUpdaterChain<T extends Block> implements FormattedBlockUpdater<T> {

    private final List<FormattedBlockUpdater<T>> chain;

    public FormattedBlockUpdaterChain() {
        chain = ImmutableList.of();
    }

    /**
     * Creates a single entry chain for a single FormattedBlockUpdater. This isn't useful
     * because the returned object is immutable and acts identically to the
     * passed in object.
     * 
     * May be called with null which will create a no-op FormattedBlockUpdater.
     * 
     * @param formattedBlockUpdater a single updater, may be null
     */
    public FormattedBlockUpdaterChain(FormattedBlockUpdater<T> formattedBlockUpdater) {
        this(formattedBlockUpdater, null);
    }

    /**
     * Creates a FormattedBlockUpdater chain that will first invoke the left's update
     * method and then invoke the right's update method. Either instance may be null in
     * which case it is simply ignored and a chain is created for the other element (or
     * an empty chain). If either instance happens to already be a chain, its elements
     * will be copied into a single new chain.
     * 
     * @param left  a FormattedBlockUpdater instance, may be null
     * @param right a FormattedBlockUpdater instance, may be null
     */
    public FormattedBlockUpdaterChain(FormattedBlockUpdater<T> left, FormattedBlockUpdater<T> right) {
        ImmutableList.Builder<FormattedBlockUpdater<T>> chainBuilder = ImmutableList.builder();
        if (left != null) {
            if (left instanceof FormattedBlockUpdaterChain) {
                chainBuilder.addAll(((FormattedBlockUpdaterChain<T>) left).chain);
            } else {
                chainBuilder.add(left);
            }
        }
        if (right != null) {
            if (right instanceof FormattedBlockUpdaterChain) {
                chainBuilder.addAll(((FormattedBlockUpdaterChain<T>) right).chain);
            } else {
                chainBuilder.add(right);
            }
        }
        this.chain = chainBuilder.build();
    }

    @Override
    public void update(T block) {
        for (FormattedBlockUpdater<T> updater : chain) {
            updater.update(block);
        }
    }

}
