package com.cannontech.common.dr.setup;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.collect.ImmutableMap;

public enum Loads implements DisplayableEnum {
    Load_1(1),
    Load_2(2),
    Load_3(3),
    Load_4(4),
    Load_5(5),
    Load_6(6),
    Load_7(7),
    Load_8(8);

    private final static Logger log = YukonLogManager.getLogger(Loads.class);
    private Integer loadNumber;
    private final static ImmutableMap<Integer, Loads> lookupByLoadNumber;
    
    static {
        try {
            ImmutableMap.Builder<Integer, Loads> loadNumberBuilder = ImmutableMap.builder();
            for (Loads load : values()) {
                loadNumberBuilder.put(load.loadNumber, load);
            }
            lookupByLoadNumber = loadNumberBuilder.build();
        } catch (IllegalArgumentException e) {
            log.warn("Caught exception while building lookup maps, look for a duplicate load number.", e);
            throw e;
        }
    }
    Loads(Integer loadNumber) {
        this.loadNumber = loadNumber;
    }

    public Integer getLoadNumber() {
        return loadNumber;
    }

    public static Loads getForLoads(Integer loadNumber) {
        Loads load = lookupByLoadNumber.get(loadNumber);
        checkArgument(load != null, load);
        return load;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dr.setup.loadGroup." + name();
    }
}