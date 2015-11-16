package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.List;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.input.type.InputOption;

/**
 * This represents a class that can produce a list of values for use in 
 * a device configuration. The implementing classes don't necessarily need 
 * to be a Java enum, but can be any enumeration of values.
 */
public interface DeviceConfigurationInputEnumeration {

    String getEnumOptionName();

    /**
     * Get the displayable values of the implementing class.
     * @return a list of displayable values
     */
    List<InputOption> getDisplayableValues(YukonUserContext userContext);

    default SelectionType getSelectionType() {
        return SelectionType.STANDARD;
    }

    public enum SelectionType {
        STANDARD("enumeratedType.jsp"),
        SWITCH("switchEnumType.jsp"),
        CHOSEN("chosenEnumType.jsp"),
        ;

        String renderer;

        SelectionType(String renderer) {
            this.renderer = renderer;
        }

        public String getRenderer() {
            return renderer;
        }
    }

}