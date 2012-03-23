package com.cannontech.selenium.solvents.common.Pickers;

import com.cannontech.selenium.solvents.common.PopupMenuSolvent;

public class PickerFactory { 
    
    public enum PickerType {
        Embedded,
        MultiSelect,
        SingleSelect,
    }
    
    public static PopupMenuSolvent createPopupMenuSolvent(String pickerBaseString, PickerType pickerType) {
        
        if (pickerType.equals(PickerType.Embedded)) {
            return new EmbeddedPopupMenuSolvent("menuId="+pickerBaseString);
        } else if (pickerType.equals(PickerType.MultiSelect)) {
            return new MultiSelectPopupMenuSolvent("menuId="+pickerBaseString);
        } else  if (pickerType.equals(PickerType.SingleSelect)) {
            return new SingleSelectPopupMenuSolvent("menuId="+pickerBaseString);
        }
        
        throw new IllegalArgumentException();
    }
}