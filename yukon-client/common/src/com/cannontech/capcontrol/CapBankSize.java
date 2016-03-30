package com.cannontech.capcontrol;

import com.cannontech.common.i18n.DisplayableEnum;

public enum CapBankSize implements DisplayableEnum {
    ONE_HUNDRED(100), TWO_HUNDRED(200), THREE_HUNDRED(300), SIX_HUNDRED(600), NINE_HUNDRED(900), TWELVE_HUNDRED(1200), EIGHTEEN_HUNDRED(1800), TWENTYFOUR_HUNDRED(2400);
    
    private int displayValue;
    
    CapBankSize(int value) {
        this.displayValue = value;
    }
    
    public int getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(int value) {
        this.displayValue = value;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.capcontrol.capbank.bankSize." + name();
    }

}
