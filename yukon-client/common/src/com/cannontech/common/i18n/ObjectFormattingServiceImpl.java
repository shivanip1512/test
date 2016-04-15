package com.cannontech.common.i18n;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.exception.DisplayableRuntimeException;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.common.util.TemplateProcessorFactory;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ObjectFormattingServiceImpl implements ObjectFormattingService {
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private TemplateProcessorFactory templateProcessorFactory;

    @Override
    public String formatObjectAsString(Object value, YukonUserContext userContext) {
        MessageSourceResolvable resolvable = formatObjectAsResolvable(value, userContext);
        String result = messageSourceResolver.getMessageSourceAccessor(userContext).getMessage(resolvable);
        return result;
    }
 
    @Override
    public MessageSourceResolvable formatObjectAsResolvable(Object object, YukonUserContext userContext,
            Object... arguments) {
        if (object instanceof MessageSourceResolvable) {
            return (MessageSourceResolvable) object;
        }
        if (object instanceof Displayable) {
            return ((Displayable) object).getMessage();
        }
        if (object instanceof DisplayableEnum) {
            return new YukonMessageSourceResolvable(((DisplayableEnum) object).getFormatKey(), arguments);
        }
        if (object instanceof ResolvableTemplate) {
            String messageString = templateProcessorFactory.processResolvableTemplate((ResolvableTemplate) object,
                                                                                      userContext);
            return new YukonMessageSourceResolvable(null, arguments, messageString);
        }
        if (object instanceof DisplayableRuntimeException) {
            return ((DisplayableRuntimeException) object).getMessageSourceResolvable();
        }
        return new YukonMessageSourceResolvable(null, arguments, object != null ? Objects.toString(object) : "");
    }

    @Override
    public <T> List<T> sortDisplayableValues(T[] toSort, final T first, final T last,
                                             YukonUserContext context) {
        Function<T, T> identity = Functions.identity();
        return sort(Lists.newArrayList(toSort), first, last, identity, false, context);
    }

    @Override
    public <T> List<T> sortDisplayableValues(Iterable<T> toSort, final T first, final T last,
                                             final YukonUserContext context) {
        Function<T, T> identity = Functions.identity();
        return sort(Lists.newArrayList(toSort), first, last, identity, false, context);
    }

    @Override
    public <T, U> List<T> sortDisplayableValuesWithMapper(Iterable<T> toSort, T first, T last, Function<T, U> mapper,
        boolean descending, YukonUserContext context) {
        return sort(Lists.newArrayList(toSort), first, last, mapper, descending, context);
    }

    /**
     * Sorts the passed in list and returns it.
     */
    private <T, U> List<T> sort(List<T> retVal, final T first, final T last, final Function<T, U> mapper, final boolean descending,
                                             final YukonUserContext context) {
        if (first != null && !retVal.contains(first)) {
            retVal.add(first);
        }
        if (last != null && !retVal.contains(last)) {
            retVal.add(last);
        }

        Collections.sort(retVal, new Comparator<T>() {
            @Override
            public int compare(T t1, T t2) {
                if (t1 == t2) {
                    return 0;
                }
                if (t1 == first || t2 == last) {
                    return descending ? 1 : -1;
                }
                if (t1 == last || t2 == first) {
                    return descending ? -1 : 1;
                }
                String localName1 = formatObjectAsString(mapper.apply(t1), context);
                String localName2 = formatObjectAsString(mapper.apply(t2), context);
                int compare = localName1.compareToIgnoreCase(localName2);
                return descending ? -compare : compare;
            }
        });
        return retVal;
    }

    @Override
    public <S, T> Map<S, List<T>> sortDisplayableValues(Map<S, ? extends Iterable<T>> toSort, YukonUserContext context) {
        Map<S, List<T>> sortedResult = Maps.newLinkedHashMap();
        List<S> sortedKeys = sortDisplayableValues(
                Lists.newArrayList(toSort.keySet()), null, null, context); 
        for (S group : sortedKeys) {
            sortedResult.put(group, sortDisplayableValues(Lists.newArrayList(toSort.get(group)), null, null, context));
        }
        return sortedResult;
    }
}
