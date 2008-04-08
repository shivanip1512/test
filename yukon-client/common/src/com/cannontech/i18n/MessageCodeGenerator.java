package com.cannontech.i18n;

public final class MessageCodeGenerator {

    public static final String generateCode(final String prefix, String input) {
        if (prefix.endsWith(".")) {
            throw new IllegalArgumentException("Invalid prefix format ending with . " + prefix);
        }
        
        final StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        sb.append(".");
        sb.append(MessageCodeGenerator.generateCode(input));

        String output = sb.toString();
        return output;
    }
    
    public static final String generateCode(final String input) {
        String output = input.replaceAll("[\\.|\\\"|\\s+|&|<]", "");
        if ("".equals(output)) throw new UnsupportedOperationException("Unable to generate key: " + input);
        return output;
    }
    
}
