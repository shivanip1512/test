package com.cannontech.database.data.device.configuration;

/**
 * Enumeration of item value types. These value types are used to determine
 * which type of data is valid for an item value
 */
public enum ItemValueType {
    NONE, NUMERIC, DATE, BOOLEAN, TOU;

    /**
     * Method to get the ItemValueType based on a string type
     * @param type - Type of ItemValueType
     * @return The ItemValueType for the given string
     */
    public static ItemValueType getItemValueType(String type) {

        ItemValueType[] values = ItemValueType.values();
        for (int i = 0; i < values.length; i++) {

            ItemValueType valueType = values[i];
            if (valueType.toString().equalsIgnoreCase(type)) {
                return valueType;
            }

        }

        return NONE;
    }
}
