package com.cannontech.common.util;

import java.math.BigDecimal;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.i18n.YukonMessageSourceResolvable;

/**
 * A list of internationalizable binary prefixes.  Currently this list only supports prefixes which
 * can be used with a single Java long value.
 */
public enum BinaryPrefix {
    kibi,
    mebi,
    gibi,
    tebi,
    pebi,
    exbi;

    public double convertValue(long input) {
        BigDecimal bigInput = new BigDecimal(input);
        return bigInput.divide(new BigDecimal(1024).pow(ordinal() + 1)).doubleValue();
    }

    public static MessageSourceResolvable getCompactRepresentation(long value) {
        BigDecimal convertedValue = new BigDecimal(value);
        BinaryPrefix prefixToUse = null;

        for (BinaryPrefix prefix : values()) {
            double prefixConvertedValue = prefix.convertValue(value);
            if (prefixConvertedValue < 1) {
                break;
            }
            convertedValue = new BigDecimal(prefixConvertedValue);
            prefixToUse = prefix;
        }

        String messageKey = "yukon.common.prefixedByteValue." +
            (prefixToUse == null ? "noPrefix" : prefixToUse.name());
        return YukonMessageSourceResolvable.createSingleCodeWithArguments(messageKey,
                                                                          convertedValue.doubleValue());
    }
}
