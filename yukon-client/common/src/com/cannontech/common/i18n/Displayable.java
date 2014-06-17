package com.cannontech.common.i18n;

import org.springframework.context.MessageSourceResolvable;

/**
 * Implement this class to make instances usable in cti:msg2 and i:inline directly.  If you don't need the flexibility
 * to use a default you can use {@link DisplayableEnum} instead.
 */
public interface Displayable {
    /**
     * Get the message that will be used when displaying this instance.
     */
    MessageSourceResolvable getMessage();
}
