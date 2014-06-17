package com.cannontech.common.i18n;

/**
 * Implement this class to make instances usable in cti:msg2 and i:inline directly.  If you need the flexibility
 * to use a default you can use {@link Displayable} instead.
 */
public interface DisplayableEnum {
    /**
     * Get the key for the message that will be used when displaying this instance.
     */
    String getFormatKey();
}
