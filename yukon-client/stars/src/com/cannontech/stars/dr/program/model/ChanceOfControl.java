package com.cannontech.stars.dr.program.model;

public enum ChanceOfControl {
    NONE,
    LIKELY,
    UNLIKELY;

    private static final String keyPrefix = "yukon.dr.program.displayname.chanceOfControl.";
    
    /**
     * I18N key for the display text for this action
     * @return Display key
     */
    public String getDisplayKey() {
        return keyPrefix + name();
    }
    
    public static ChanceOfControl valueOfName(String name) {
        if ("(none)".equals(name)) return NONE;
        if (LIKELY.name().equalsIgnoreCase(name)) return LIKELY;
        if (UNLIKELY.name().equalsIgnoreCase(name)) return UNLIKELY;
        throw new IllegalArgumentException("No enum with name " + name + " found");
    }
    
}
