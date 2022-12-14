package com.cannontech.common.gui.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

public class TextFieldDocument extends javax.swing.text.PlainDocument {
    private int maxCharCount = 0;
    public static int MAX_DEVICE_NAME_LENGTH = 60;
    public static int MAX_IED_PASSWORD_LENGTH = 20;
    public static int MAX_INITIALIZATION_STRING_LENGTH = 50;
    public static int MAX_MCT_PASSWORD_LENGTH = 6;
    public static int MAX_METER_NUMBER_LENGTH = 50;
    public static int MAX_PAGER_NUMBER_LENGTH = 20;
    public static int MAX_PASSWORD_LENGTH = 20;
    public static int MAX_PHONE_NUMBER_LENGTH = 40;
    public static int MAX_POINT_LIMIT_NAME_LENGTH = 14;
    public static int MAX_POINT_NAME_LENGTH = 60;
    public static int MAX_PORT_DESCRIPTION_LENGTH = 60;
    public static int MAX_PREFIX_NUMBER_LENGTH = 10;
    public static int MAX_ROUTE_NAME_LENGTH = 60;
    public static int MAX_STATE_GROUP_NAME_LENGTH = 60;
    public static int MAX_STATE_NAME_LENGTH = 32;
    public static int MAX_SUFFIX_NUMBER_LENGTH = 10;
    public static int MAX_CAP_BANK_ADDRESS_LENGTH = 40;
    public static int MAX_CAP_SUBBUS_NAME_LENGTH = 30;
    public static int MAX_CAP_GEO_NAME_LENGTH = 30;
    public static int MAX_SEASON_SCHEDULE_NAME_LENGTH = 60;
    public static int MAX_HOLIDAY_SCHEDULE_NAME_LENGTH = 40;
    public static int MAX_HOLIDAY_DAY_NAME_LENGTH = 20;
    public static int MAX_TOU_SCHEDULE_NAME_LENGTH = 30;
    public static int MAX_BASELINE_NAME_LENGTH = 30;
    public static int MAX_LOGIN_NAME_LENGTH = 50;

    // some simple int defined lengths
    public static int STRING_LENGTH_30 = 30;
    public static int STRING_LENGTH_40 = 40;
    public static int STRING_LENGTH_60 = 60;
    public static int STRING_LENGTH_80 = 80;
    public static int STRING_LENGTH_100 = 100;

    private char[] invalidChars = null;

    public static final char[] INVALID_CHARS_WINDOWS = { '*', '|', '\\', '/', ':', '"', '<', '>', '?' };

    public static final char[] INVALID_CHARS_LMCONSTRAINTS = { '\'', ',', '|' };

    public static final char[] INVALID_CHARS_TOURATEOFFSETS = { '\'', ',', '|', '"' };

    public TextFieldDocument() {
        super();
    }

    public TextFieldDocument(int maxLength) {
        super();

        maxCharCount = maxLength;
    }

    public TextFieldDocument(int maxLength, char[] nonvalidChars) {
        this(maxLength);

        invalidChars = nonvalidChars;
    }

    public boolean checkInputValue(String proposedValue) {
        if ((proposedValue.length() <= maxCharCount || maxCharCount < 0) && isValidString(proposedValue)) {
            return true;
        } else
            return false;
    }

    private boolean isValidString(String val) {
        
        if (invalidChars != null) {
            for (int i = 0; i < invalidChars.length; i++) {
                if (val.indexOf(invalidChars[i]) != -1)
                    return false;
            }
        }

        return true;
    }

    public void insertString(int offset, String string, AttributeSet attributes) throws BadLocationException {
        
        if (string == null)
            return;
        else {
            String newValue;
            int length = getLength();

            if (length == 0) {
                newValue = string;
            } else {
                String currentContent = getText(0, length);
                StringBuffer currentBuffer = new StringBuffer(currentContent);

                currentBuffer.insert(offset, string);
                newValue = currentBuffer.toString();
            }

            if (checkInputValue(newValue))
                super.insertString(offset, string, attributes);
        }
    }

    public void remove(int offset, int length) throws BadLocationException {
        super.remove(offset, length);
    }
}
