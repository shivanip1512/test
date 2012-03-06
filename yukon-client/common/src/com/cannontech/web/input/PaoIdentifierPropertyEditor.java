package com.cannontech.web.input;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;


public class PaoIdentifierPropertyEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if(StringUtils.isNotBlank(text)) {
            String paoTypeString = text.substring(0, text.indexOf(":"));
            PaoType paoType = PaoType.valueOf(paoTypeString);
            int paoId = Integer.valueOf(text.substring(text.indexOf(":") + 1));
            PaoIdentifier paoIdentifier = new PaoIdentifier(paoId, paoType);
            setValue(paoIdentifier);
        }
    }
    
    @Override
    public String getAsText() {
        PaoIdentifier paoIdentifier = (PaoIdentifier) getValue();
        String returnVal = paoIdentifier.getPaoType().name() + ":" + paoIdentifier.getPaoId();
        return paoIdentifier == null ? "" : returnVal;
    }

}
