package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList.Builder;

@Component
public final class CentronDisplayItemEnumeration implements DeviceConfigurationInputEnumeration {
    
    public enum Item {
        SLOT_DISABLED("0", ".slotDisabled"),
        NO_SEGMENTS("1", ".noSegments"),
        ALL_SEGMENTS("2", ".allSegments"),
        // Item #3 is deprecated and unused.
        CURRENT_LOCAL_TIME("4", ".currentLocalTime"),
        CURRENT_LOCAL_DATE("5", ".currentLocalDate"),
        TOTAL_KWH("6", ".totalKwh"),
        NET_KWH("7", ".netKwh"),
        DELIVERED_KWH("8", ".deliveredKwh"),
        REVERSE_KWH("9", ".reverseKwh"),
        LAST_INTERVAL_KW("10", ".lastIntervalKw"),
        PEAK_KW("11", ".peakKw"),
        PEAK_KW_DATE("12", ".peakKwDate"),
        PEAK_KW_TIME("13", ".peakKwTime"),
        LAST_INTERVAL_VOLTAGE("14", ".lastIntervalVoltage"),
        PEAK_VOLTAGE("15", ".peakVoltage"),
        PEAK_VOLTAGE_DATE("16", ".peakVoltageDate"),
        PEAK_VOLTAGE_TIME("17", ".peakVoltageTime"),
        MINIMUM_VOLTAGE("18", ".minimumVoltage"),
        MINIMUM_VOLTAGE_DATE("19", ".minimumVoltageDate"),
        MINIMUM_VOLTAGE_TIME("20", ".minimumVoltageTime"),
        TOU_RATE_A_KWH("21", ".touRateAKwh"),
        TOU_RATE_A_PEAK_KW("22", ".touRateAPeakKw"),
        TOU_RATE_A_DATE_OF_PEAK_KW("23", ".touRateADateOfPeakKw"),
        TOU_RATE_A_TIME_OF_PEAK_KW("24", ".touRateATimeOfPeakKw"),
        TOU_RATE_B_KWH("25", ".touRateBKwh"),
        TOU_RATE_B_PEAK_KW("26", ".touRateBPeakKw"),
        TOU_RATE_B_DATE_OF_PEAK_KW("27", ".touRateBDateOfPeakKw"),
        TOU_RATE_B_TIME_OF_PEAK_KW("28", ".touRateBTimeOfPeakKw"),
        TOU_RATE_C_KWH("29", ".touRateCKwh"),
        TOU_RATE_C_PEAK_KW("30", ".touRateCPeakKw"),
        TOU_RATE_C_DATE_OF_PEAK_KW("31", ".touRateCDateOfPeakKw"),
        TOU_RATE_C_TIME_OF_PEAK_KW("32", ".touRateCTimeOfPeakKw"),
        TOU_RATE_D_KWH("33", ".touRateDKwh"),
        TOU_RATE_D_PEAK_KW("34", ".touRateDPeakKw"),
        TOU_RATE_D_DATE_OF_PEAK_KW("35", ".touRateDDateOfPeakKw"),
        TOU_RATE_D_TIME_OF_PEAK_KW("36", ".touRateDTimeOfPeakKw"),
        ;
        
        private final String dbValue;
        private final String messageKey;
        
        private Item(String dbValue, String messageKey) {
            this.dbValue = dbValue;
            this.messageKey = messageKey;
        }
    }
    
    private static final String baseKey = "yukon.web.modules.tools.configs.enum.centronDisplayItem";
    
    private static final List<DisplayableValue> displayItems;
    
    static {
        Builder<DisplayableValue> displayBuilder = new Builder<>();
        
        for (Item item : Item.values()) {
            displayBuilder.add(new DisplayableValue(item.dbValue, baseKey + item.messageKey));
        }
        
        displayItems = displayBuilder.build();
    }
            
    @Override
    public List<DisplayableValue> getDisplayableValues() {
        return displayItems;
    }
    
    @Override
    public String getEnumOptionName() {
        return "CentronDisplayItem";
    }
}
