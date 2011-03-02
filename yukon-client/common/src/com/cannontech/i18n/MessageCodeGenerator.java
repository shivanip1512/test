package com.cannontech.i18n;

import org.apache.commons.lang.Validate;

public final class MessageCodeGenerator {

    public static final String generateCode(final String prefix, String input) {
        if (prefix.endsWith(".")) {
            throw new IllegalArgumentException("Invalid prefix format ending with . " + prefix);
        }
        Validate.notEmpty(input, "input String must not be empty or null");
        
        final StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        sb.append(".");
        sb.append(MessageCodeGenerator.generateCode(input));

        String output = sb.toString();
        return output;
    }
    
    public static final String generateCode(final String input) {
        /* This pattern must match generateMessageCode in yukonGeneral.js */
        String output = input.replaceAll("\\W+", "");
        if ("".equals(output)) throw new UnsupportedOperationException("Unable to generate key: " + input);
        return output;
    }
    
}
