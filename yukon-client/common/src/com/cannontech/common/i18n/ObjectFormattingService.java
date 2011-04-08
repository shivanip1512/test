package com.cannontech.common.i18n;

import java.util.List;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.user.YukonUserContext;

public interface ObjectFormattingService {
    public String formatObjectAsString(Object object, YukonUserContext userContext);
    public MessageSourceResolvable formatObjectAsResolvable(Object object, YukonUserContext userContext);

    /**
     * Sort the list of enum values based on their localized values.
     * @param enumList The list to sort. This list will not be modified.
     * @param first If not null, this value will be forced to the top of the sorted list.
     * @param last If not null, this value will be forced to the end of the sorted list.
     * @return The sorted list.
     */
    public <T extends DisplayableEnum> List<T> sortEnumValues(T[] enumList, T first, T last,
                                                              YukonUserContext context);
}
