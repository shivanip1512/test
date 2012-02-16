package com.cannontech.common.i18n;

import java.util.List;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.user.YukonUserContext;

public interface ObjectFormattingService {
    /**
     * Convert the given object to a localized string for the given user context.  This method
     * understands how to correctly localize instances of MessageSourceResolvable, DisplayableEnum
     * and Displayable.  Other objects are simply converted to a string.
     */
    public String formatObjectAsString(Object object, YukonUserContext userContext);

    public MessageSourceResolvable formatObjectAsResolvable(Object object, YukonUserContext userContext);

    /**
     * Sort the list of objects based on their localized values. Values are localized using
     * {@link #formatObjectAsString(Object, YukonUserContext)}.
     * 
     * @param toSort The list to sort. This list will not be modified.
     * @param first If not null, this value will be forced to the top of the sorted list.
     * @param last If not null, this value will be forced to the end of the sorted list.
     * @return The sorted list.
     */
    public <T> List<T> sortDisplayableValues(T[] toSort, T first, T last, YukonUserContext context);

    /**
     * Sort the list of objects based on their localized values. Values are localized using
     * {@link #formatObjectAsString(Object, YukonUserContext)}.
     * 
     * @param toSort The list to sort. This list will not be modified.
     * @param first If not null, this value will be forced to the top of the sorted list.
     * @param last If not null, this value will be forced to the end of the sorted list.
     * @return The sorted list.
     */
    public <T> List<T> sortDisplayableValues(Iterable<T> toSort, T first, T last,
                                             YukonUserContext context);
}
