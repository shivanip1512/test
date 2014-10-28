package com.cannontech.common.inventory;

import com.cannontech.stars.model.LiteLmHardware;
import com.google.common.base.Function;

public interface YukonInventory {

    Function<LiteLmHardware, YukonInventory> TO_INVENTORY = new Function<LiteLmHardware, YukonInventory>() {
        @Override
        public YukonInventory apply(LiteLmHardware input) {
            return input.getIdentifier();
        }
    };
    Function<YukonInventory, Integer> TO_INVENTORY_ID = new Function<YukonInventory, Integer>() {
        @Override
        public Integer apply(YukonInventory input) {
            return input.getInventoryIdentifier().getInventoryId();
        }
    };

    InventoryIdentifier getInventoryIdentifier();

}