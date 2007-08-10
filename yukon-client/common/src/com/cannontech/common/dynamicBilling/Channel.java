package com.cannontech.common.dynamicBilling;

/**
 * Enumeration of all of the possible device channels
 */
public enum Channel {
    ONE {
        public int getNumeric() {
            return 1;
        }
    },
    TWO {
        public int getNumeric() {
            return 2;
        }
    },
    THREE {
        public int getNumeric() {
            return 3;
        }
    },
    FOUR {
        public int getNumeric() {
            return 4;
        }
    };

    /**
     * Method to get the numeric equivalent of the channel
     * @return The int representation of the channel
     */
    abstract public int getNumeric();
}
