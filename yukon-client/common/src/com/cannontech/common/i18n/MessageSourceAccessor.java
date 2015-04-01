/*
 * Copyright 2002-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* Copied from Spring and modified for internal use. */

package com.cannontech.common.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ApplicationObjectSupport;

/**
 * Helper class for easy access to messages from a MessageSource,
 * providing various overloaded getMessage methods.
 *
 * Makes a MessageSource easier to use because it defaults the Locale
 * and supports variable arguments. This is otherwise similar to the
 * Spring class of the same name, but renames the "defaultMessage"
 * methods  to support the varags change.
 *
 * @author Juergen Hoeller
 * @author Tom Mack
 * @since 23.10.2003
 * @see ApplicationObjectSupport#getMessageSourceAccessor
 */
public class MessageSourceAccessor {

    private final MessageSource messageSource;

    private final Locale defaultLocale;

    /**
     * Create a new MessageSourceAccessor, using the given default locale.
     * @param messageSource the MessageSource to wrap
     * @param defaultLocale the default locale to use for message access
     */
    public MessageSourceAccessor(MessageSource messageSource, Locale defaultLocale) {
        this.messageSource = messageSource;
        this.defaultLocale = defaultLocale;
    }

    /**
     * Return the default locale to use if no explicit locale has been given.
     * <p>The default implementation returns the default locale passed into the
     * corresponding constructor, or LocaleContextHolder's locale as fallback.
     * Can be overridden in subclasses.
     * @see #MessageSourceAccessor(org.springframework.context.MessageSource, java.util.Locale)
     * @see org.springframework.context.i18n.LocaleContextHolder#getLocale()
     */
    protected Locale getDefaultLocale() {
        return (this.defaultLocale != null ? this.defaultLocale : LocaleContextHolder.getLocale());
    }
    
    /**
     * Retrieve the message for the given code and the default Locale.
     * @param code code of the message
     * @return the message
     * @throws org.springframework.context.NoSuchMessageException if not found
     */
    public String getMessage(String code) throws NoSuchMessageException {
        return this.messageSource.getMessage(code, null, getDefaultLocale());
    }
    
    /**
     * Retrieve the message for the given code and the default Locale.
     * @param code code of the message
     * @param args arguments for the message, or <code>null</code> if none
     * @return the message
     * @throws org.springframework.context.NoSuchMessageException if not found
     */
    public String getMessage(String code, Object... args) throws NoSuchMessageException {
        return this.messageSource.getMessage(code, args, getDefaultLocale());
    }
    
    /**
     * @see MessageSourceAccessor.getMessage(MessageSourceResolvable resolvable, Locale locale)
     */
    public String getMessage(Displayable displayable) throws NoSuchMessageException {
        return this.messageSource.getMessage(displayable.getMessage(), getDefaultLocale());
    }
    
    /**
     * @see MessageSourceAccessor.getMessage(String code, Object... args)
     */
    public String getMessage(DisplayableEnum displayableEnum, Object... args) throws NoSuchMessageException {
        return this.messageSource.getMessage(displayableEnum.getFormatKey(), args, getDefaultLocale());
    }
    
    /**
     * @see MessageSourceAccessor.getMessage(String code, Object... args)
     * Retains original lists order.
     */
    public List<String> getMessages(List<? extends Displayable> displayables) throws NoSuchMessageException {
        List<String> messages = new ArrayList<>(displayables.size());
        for (Displayable displayable : displayables) {
            messages.add(messageSource.getMessage(displayable.getMessage(), getDefaultLocale()));
        }
        return messages;
    }
    
    /**
     * Retrieve the message for the given code and the default Locale.
     * @param code code of the message
     * @return the message
     * @throws org.springframework.context.NoSuchMessageException if not found
     */
    public String getMessageWithDefault(String code, String defaultMessage) throws NoSuchMessageException {
        return this.messageSource.getMessage(code, null, defaultMessage, getDefaultLocale());
    }
    
    /**
     * Retrieve the message for the given code and the default Locale.
     * @param code code of the message
     * @param args arguments for the message, or <code>null</code> if none
     * @return the message
     * @throws org.springframework.context.NoSuchMessageException if not found
     */
    public String getMessageWithDefault(String code, String defaultMessage, Object... args) throws NoSuchMessageException {
        return this.messageSource.getMessage(code, args, defaultMessage, getDefaultLocale());
    }
    
    /**
     * Retrieve the given MessageSourceResolvable (e.g. an ObjectError instance)
     * in the default Locale.
     * @param resolvable the MessageSourceResolvable
     * @return the message
     * @throws org.springframework.context.NoSuchMessageException if not found
     */
    public String getMessage(MessageSourceResolvable resolvable) throws NoSuchMessageException {
        return this.messageSource.getMessage(resolvable, getDefaultLocale());
    }
}
