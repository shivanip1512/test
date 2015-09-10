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
        SLOT_DISABLED("0"),
        NO_SEGMENTS("1"),
        ALL_SEGMENTS("2"),
        // Item #3 is deprecated and unused.
        CURRENT_LOCAL_TIME("4"),
        CURRENT_LOCAL_DATE("5"),
        TOTAL_KWH("6"),
        NET_KWH("7"),
        DELIVERED_KWH("8"),
        RECEIVED_KWH("9"),
        LAST_INTERVAL_KW("10"),
        PEAK_KW("11"),
        PEAK_KW_DATE("12"),
        PEAK_KW_TIME("13"),
        LAST_INTERVAL_VOLTAGE("14"),
        // Items #15-20 are not supported on C1SX meters (RFN-410cL). 
        PEAK_VOLTAGE("15", DisplayType.CENTRON_420),
        PEAK_VOLTAGE_DATE("16", DisplayType.CENTRON_420),
        PEAK_VOLTAGE_TIME("17", DisplayType.CENTRON_420),
        MINIMUM_VOLTAGE("18", DisplayType.CENTRON_420),
        MINIMUM_VOLTAGE_DATE("19", DisplayType.CENTRON_420),
        MINIMUM_VOLTAGE_TIME("20", DisplayType.CENTRON_420),
        TOU_RATE_A_KWH("21"),
        TOU_RATE_A_PEAK_KW("22"),
        TOU_RATE_A_DATE_OF_PEAK_KW("23"),
        TOU_RATE_A_TIME_OF_PEAK_KW("24"),
        TOU_RATE_B_KWH("25"),
        TOU_RATE_B_PEAK_KW("26"),
        TOU_RATE_B_DATE_OF_PEAK_KW("27"),
        TOU_RATE_B_TIME_OF_PEAK_KW("28"),
        TOU_RATE_C_KWH("29"),
        TOU_RATE_C_PEAK_KW("30"),
        TOU_RATE_C_DATE_OF_PEAK_KW("31"),
        TOU_RATE_C_TIME_OF_PEAK_KW("32"),
        TOU_RATE_D_KWH("33"),
        TOU_RATE_D_PEAK_KW("34"),
        TOU_RATE_D_DATE_OF_PEAK_KW("35"),
        TOU_RATE_D_TIME_OF_PEAK_KW("36");

        private final String dbValue;
        private final DisplayType model;
        
        public enum DisplayType {
            CENTRON_410,
            CENTRON_420
        }

        private Item(String dbValue, DisplayType model) {
            this.dbValue = dbValue;
            this.model = model;
        }
        
        private Item(String dbValue) {
            this.dbValue = dbValue;
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
            displayItems.add( new InputOption( displayItem.dbValue, messageAccessor.getMessage(displayItem)));
        }
        return displayItems;
    }

    @Override
    public SelectionType getSelectionType() {
        return SelectionType.CHOSEN;
    }
}