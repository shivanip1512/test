package com.cannontech.stars.dr.appliance.model;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum ApplianceTypeEnum implements DisplayableEnum {
    DEFAULT(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DEFAULT),
    AIR_CONDITIONER(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_AIR_CONDITIONER),
    STORAGE_HEAT(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_STORAGE_HEAT),
    IRRIGATION(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_IRRIGATION),
    GRAIN_DRYER(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GRAIN_DRYER),
    HEAT_PUMP(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_HEAT_PUMP),
    GENERATOR(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GENERATOR),
    CHILLER(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_CHILLER),
    DUAL_STAGE(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DUALSTAGE),
    DUAL_FUEL(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DUAL_FUEL),
    WATER_HEATER(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_WATER_HEATER);

    private final String keyPrefix = "yukon.dr.appliance.displayname.";
    private int definitionId;

    private final static ImmutableMap<Integer, ApplianceTypeEnum> lookupByDefinitionId;
    static {
        Builder<Integer, ApplianceTypeEnum> byDefinitionIdBuilder =
            ImmutableMap.builder();

        for (ApplianceTypeEnum applianceType : values()) {
            byDefinitionIdBuilder.put(applianceType.getDefinitionId(), applianceType);
        }
        lookupByDefinitionId = byDefinitionIdBuilder.build();
    }

    private ApplianceTypeEnum(int definitionId) {
        this.definitionId = definitionId;
    }

    public int getDefinitionId() {
        return definitionId;
    }

    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }

    public static ApplianceTypeEnum getByDefinitionId(Integer definitionId) {
        return lookupByDefinitionId.get(definitionId);
    }
}
