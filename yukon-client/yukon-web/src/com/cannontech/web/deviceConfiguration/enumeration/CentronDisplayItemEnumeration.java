package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.input.type.InputOption;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;

public abstract class CentronDisplayItemEnumeration implements DeviceConfigurationInputEnumeration {

    private static final String baseKey = "yukon.web.modules.tools.configs.enum.centronDisplayItem.";

    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    public enum Item implements DisplayableEnum {
        SLOT_DISABLED,
        NO_SEGMENTS,
        ALL_SEGMENTS,
        // Item #3 is deprecated and unused.
        CURRENT_LOCAL_TIME,
        CURRENT_LOCAL_DATE,
        TOTAL_KWH,
        NET_KWH,
        DELIVERED_KWH,
        RECEIVED_KWH,
        LAST_INTERVAL_KW,
        PEAK_KW,
        PEAK_KW_DATE,
        PEAK_KW_TIME,
        LAST_INTERVAL_VOLTAGE,
        // Items #15-20 are not supported on C1SX meters (RFN-410cL).
        PEAK_VOLTAGE(DisplayType.CENTRON_420),
        PEAK_VOLTAGE_DATE(DisplayType.CENTRON_420),
        PEAK_VOLTAGE_TIME(DisplayType.CENTRON_420),
        MINIMUM_VOLTAGE(DisplayType.CENTRON_420),
        MINIMUM_VOLTAGE_DATE(DisplayType.CENTRON_420),
        MINIMUM_VOLTAGE_TIME(DisplayType.CENTRON_420),
        TOU_RATE_A_KWH,
        TOU_RATE_A_PEAK_KW,
        TOU_RATE_A_DATE_OF_PEAK_KW,
        TOU_RATE_A_TIME_OF_PEAK_KW,
        TOU_RATE_B_KWH,
        TOU_RATE_B_PEAK_KW,
        TOU_RATE_B_DATE_OF_PEAK_KW,
        TOU_RATE_B_TIME_OF_PEAK_KW,
        TOU_RATE_C_KWH,
        TOU_RATE_C_PEAK_KW,
        TOU_RATE_C_DATE_OF_PEAK_KW,
        TOU_RATE_C_TIME_OF_PEAK_KW,
        TOU_RATE_D_KWH,
        TOU_RATE_D_PEAK_KW,
        TOU_RATE_D_DATE_OF_PEAK_KW,
        TOU_RATE_D_TIME_OF_PEAK_KW;

        private final DisplayType model;

        public enum DisplayType {
            CENTRON_410,
            CENTRON_420
        }

        private Item(DisplayType model) {
            this.model = model;
        }

        private Item() {
            model = DisplayType.CENTRON_410;
        }

        public static List<Item> get410Values() {

            return Lists.newArrayList(values())
                    .stream()
                    .filter(new Predicate<Item>() {
                        @Override
                        public boolean test(Item item) {
                            return item.model == DisplayType.CENTRON_410;
                        }
                    })
                    .collect(Collectors.toList());
        }

        @Override
        public String getFormatKey() {
            return baseKey + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name()) ;
        }
    }

    public List<InputOption> getDisplayableValues(YukonUserContext userContext, List<Item> supportedItems) {
        MessageSourceAccessor messageAccessor = messageResolver.getMessageSourceAccessor(userContext);

        List<InputOption> displayItems = new ArrayList<>();

        for (Item displayItem : supportedItems) {
            displayItems.add( new InputOption( displayItem.name(), messageAccessor.getMessage(displayItem)));
        }
        return displayItems;
    }

    @Override
    public SelectionType getSelectionType() {
        return SelectionType.CHOSEN;
    }
}