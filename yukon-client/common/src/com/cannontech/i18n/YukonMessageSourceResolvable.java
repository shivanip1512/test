package com.cannontech.i18n;

import java.util.Collection;
import java.util.List;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

/**
 * Yukon extension of DefaultMessageSourceResolvable with convenience
 * constructor
 */
public class YukonMessageSourceResolvable extends DefaultMessageSourceResolvable {
    private static final long serialVersionUID = 3775006143046764578L;

    public YukonMessageSourceResolvable(String code, String... args) {
        super(new String[] { code }, args);
    }

    public YukonMessageSourceResolvable(String code, Collection<String> args) {
        super(new String[] { code }, args.toArray(new String[args.size()]));
    }

    public static MessageSourceResolvable createDefault(String code, String defaultMessage) {
        return new DefaultMessageSourceResolvable(new String[] { code }, new Object[]{}, defaultMessage);
    }
    
    public static MessageSourceResolvable createMultipleCodes(String... codes) {
        return new DefaultMessageSourceResolvable(codes, new Object[]{});
    }

    public static MessageSourceResolvable createMultipleCodes(List<String> asList) {
        return new DefaultMessageSourceResolvable(asList.toArray(new String[asList.size()]));
    }
    
    public static MessageSourceResolvable createMultipleCodes(List<String> asList, String defaultMessage) {
        return new DefaultMessageSourceResolvable(asList.toArray(new String[asList.size()]), defaultMessage);
    }
    
}
