package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList.Builder;

@Component
public final class DisplayItemEnumeration implements DeviceConfigurationInputEnumeration {
    
    public enum Item {
        SLOT_DISABLED("0", ".slotDisabled"),
        NO_SEGMENTS("1", ".noSegments"),
        ALL_SEGMENTS("2", ".allSegments"),
        CURRENT_LOCAL_TIME("3", ".currentLocalTime"),
        CURRENT_LOCAL_DATE("4", ".currentLocalDate"),
        TOTAL_KWH("5", ".totalKwh"),
        NET_KWH("6", ".netKwh"),
        DELIVERED_KWH("7", ".deliveredKwh"),
        REVERSE_KWH("8", ".reverseKwh"),
        LAST_INTERVAL_KW("9", ".lastIntervalKw"),
        PEAK_KW("10", ".peakKw"),
        PEAK_KW_DATE("11", ".peakKwDate"),
        PEAK_KW_TIME("12", ".peakKwTime"),
        LAST_INTERVAL_VOLTAGE("13", ".lastIntervalVoltage"),
        PEAK_VOLTAGE("14", ".peakVoltage"),
        PEAK_VOLTAGE_DATE("15", ".peakVoltageDate"),
        PEAK_VOLTAGE_TIME("16", ".peakVoltageTime"),
        MINIMUM_VOLTAGE("17", ".minimumVoltage"),
        MINIMUM_VOLTAGE_DATE("18", ".minimumVoltageDate"),
        MINIMUM_VOLTAGE_TIME("19", ".minimumVoltageTime"),
        TOU_RATE_A_KWH("20", ".touRateAKwh"),
        TOU_RATE_A_PEAK_KW("21", ".touRateAPeakKw"),
        TOU_RATE_A_DATE_OF_PEAK_KW("22", ".touRateADateOfPeakKw"),
        TOU_RATE_A_TIME_OF_PEAK_KW("23", ".touRateATimeOfPeakKw"),
        TOU_RATE_B_KWH("24", ".touRateBKwh"),
        TOU_RATE_B_PEAK_KW("25", ".touRateBPeakKw"),
        TOU_RATE_B_DATE_OF_PEAK_KW("26", ".touRateBDateOfPeakKw"),
        TOU_RATE_B_TIME_OF_PEAK_KW("27", ".touRateBTimeOfPeakKw"),
        TOU_RATE_C_KWH("28", ".touRateCKwh"),
        TOU_RATE_C_PEAK_KW("29", ".touRateCPeakKw"),
        TOU_RATE_C_DATE_OF_PEAK_KW("30", ".touRateCDateOfPeakKw"),
        TOU_RATE_C_TIME_OF_PEAK_KW("31", ".touRateCTimeOfPeakKw"),
        TOU_RATE_D_KWH("32", ".touRateDKwh"),
        TOU_RATE_D_PEAK_KW("33", ".touRateDPeakKw"),
        TOU_RATE_D_DATE_OF_PEAK_KW("34", ".touRateDDateOfPeakKw"),
        TOU_RATE_D_TIME_OF_PEAK_KW("35", ".touRateDTimeOfPeakKw"),
        ;
        
        private final String dbValue;
        private final String messageKey;
        
        private Item(String dbValue, String messageKey) {
            this.dbValue = dbValue;
            this.messageKey = messageKey;
        }
    }
    
    private static final String baseKey = "yukon.web.modules.tools.configs.enum.displayItem";
    
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
        return "DisplayItem";
    }
}
