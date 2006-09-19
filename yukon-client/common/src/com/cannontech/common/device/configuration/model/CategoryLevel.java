package com.cannontech.common.device.configuration.model;

/**
 * Enumeration of category levels. Currently these are used to determine whether
 * the category should be displayed in the device configuration creation wizard
 */
public enum CategoryLevel {
    HIGH {
        public boolean showInWizard() {
            return true;
        }
    },
    LOW {
        public boolean showInWizard() {
            return false;
        }
    };

    public static CategoryLevel getCategoryLevel(String level) {

        if (LOW.toString().equalsIgnoreCase(level)) {
            return LOW;
        } else if (HIGH.toString().equalsIgnoreCase(level)) {
            return HIGH;
        } else {
            throw new IllegalArgumentException(level + " is not a valid CategoryLevel");
        }
    }

    abstract public boolean showInWizard();
}
