package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.List;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.Displayable;
import com.cannontech.i18n.YukonMessageSourceResolvable;

/**
 * This represents a class that can produce a list of values for use in 
 * a device configuration. The implementing classes don't necessarily need 
 * to be a Java enum, but can be any enumeration of values.
 */
public interface DeviceConfigurationInputEnumeration {

    final class DisplayableValue implements Displayable {
        final String value;
        final MessageSourceResolvable displayValue;

        public DisplayableValue(String value, MessageSourceResolvable displayValue) {
            this.value = value;
            this.displayValue = displayValue;
        }

        public DisplayableValue(String value, String messageKey) {
            this.value = value;
            this.displayValue = YukonMessageSourceResolvable.createSingleCode(messageKey);
        }

        @Override
        public MessageSourceResolvable getMessage() {
            return displayValue;
        }
        
        public String getValue() {
            return value;
        }
    }

    String getEnumOptionName();

    /**
     * Get the displayable values of the implementing class.
     * @return a list of displayable values
     */
    List<DisplayableValue> getDisplayableValues();
}
