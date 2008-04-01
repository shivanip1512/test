package com.cannontech.i18n;

import java.util.Collection;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

/**
 * Yukon extension of DefaultMessageSourceResolvable with convenience
 * constructor
 */
public class YukonMessageSourceResolvable extends DefaultMessageSourceResolvable {
    private static final long serialVersionUID = 3775006143046764578L;
    private String code;

    public YukonMessageSourceResolvable(String code, String... args) {
        super(new String[] { code }, args);
        this.code = code;
    }

    public YukonMessageSourceResolvable(String code, Collection<String> args) {
        super(new String[] { code }, args.toArray(new String[args.size()]));
        this.code = code;
    }
    
    public static MessageSourceResolvable createDefault(String defaultMessage) {
        return new DefaultMessageSourceResolvable(new String[0], defaultMessage);
    }
    
    @Override
    public String getCode() {
        return this.code;
    }

}
