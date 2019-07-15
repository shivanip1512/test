package com.cannontech.i18n;

import java.util.Collection;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import com.google.common.collect.Lists;
import com.google.common.collect.ObjectArrays;

/**
 * Yukon extension of DefaultMessageSourceResolvable with convenience
 * constructor
 */
public class YukonMessageSourceResolvable extends DefaultMessageSourceResolvable {
    private static final long serialVersionUID = 3775006143046764578L;
    
    public YukonMessageSourceResolvable() {
        super("");
    }

    public YukonMessageSourceResolvable(MessageSourceResolvable resolvable) {
        super(resolvable);
    }

    public YukonMessageSourceResolvable(String code) {
        super(code);
    }

    public YukonMessageSourceResolvable(String[] codes, Object[] arguments, String defaultMessage) {
        super(codes, arguments, defaultMessage);
    }

    public YukonMessageSourceResolvable(String[] codes, Object[] arguments) {
        super(codes, arguments);
    }

    public YukonMessageSourceResolvable(String[] codes, String defaultMessage) {
        super(codes, defaultMessage);
    }

    public YukonMessageSourceResolvable(String[] codes) {
        super(codes);
    }
    
    public YukonMessageSourceResolvable(String code, Object... args) {
        super(new String[] { code }, args);
    }

    public static MessageSourceResolvable createSingleCode(String code) {
        return new YukonMessageSourceResolvable(code);
    }
    
    public static MessageSourceResolvable createSingleCodeWithArgumentList(String code, Iterable<? extends Object> args) {
        return new YukonMessageSourceResolvable(new String[] { code }, toArray(args, Object.class));
    }

    public static MessageSourceResolvable createSingleCodeWithArguments(String code, Object... args) {
        return new YukonMessageSourceResolvable(code, args);
    }
    
    public static MessageSourceResolvable createSingleCodeWithArgumentListWithDefault(String code, Iterable<? extends Object> args, String defaultMessage) {
        return new YukonMessageSourceResolvable(new String[] { code }, toArray(args, Object.class), defaultMessage);
    }
    
    public static MessageSourceResolvable createDefault(String code, String defaultMessage) {
        return new YukonMessageSourceResolvable(new String[] { code }, new Object[]{}, defaultMessage);
    }



    public static MessageSourceResolvable createDefaultWithoutCode(String defaultMessage) {
        return new YukonMessageSourceResolvable(null, new Object[]{}, defaultMessage);
    }
    
    public static MessageSourceResolvable createMultipleCodes(String... codes) {
        return new YukonMessageSourceResolvable(codes, new Object[]{});
    }

    public static MessageSourceResolvable createMultipleCodes(Iterable<String> asList) {
        return new YukonMessageSourceResolvable(toArray(asList, String.class));
    }
    
    public static MessageSourceResolvable createMultipleCodesWithDefault(Iterable<String> asList, String defaultMessage) {
        return new YukonMessageSourceResolvable(toArray(asList, String.class), defaultMessage);
    }

    public static MessageSourceResolvable createDefaultWithArguments(String code, String defaultMessage, Object... arguments) {
        return new YukonMessageSourceResolvable(new String[] { code }, arguments, defaultMessage);
    }

    public static MessageSourceResolvable createMultipleCodesWithArguments(Iterable<String> codes, Object... arguments) {
        return new YukonMessageSourceResolvable(toArray(codes, String.class), arguments);
    }
    
    public static MessageSourceResolvable createMultipleCodesWithArgumentList(Iterable<String> codes, Iterable<? extends Object> arguments) {
        return new YukonMessageSourceResolvable(toArray(codes, String.class), toArray(arguments, Object.class));
    }
    
    public static <T> T[] toArray(Iterable<? extends T> iterable, Class<T> type) {
        Collection<? extends T> collection = (iterable instanceof Collection)
        ? (Collection<? extends T>) iterable
                : Lists.newArrayList(iterable);
        T[] array = ObjectArrays.newArray(type, collection.size());
        return collection.toArray(array);
    }
    
    @Override
    public String toString() {
        if (getCodes() == null || getCodes().length == 0) {
            return getDefaultMessage() + "*";
        }
        if (getCodes().length == 1) {
            return getCodes()[0] +"!";
        }
        return getCodes()[0] + "+";
    }
}
