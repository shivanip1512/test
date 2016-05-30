package com.cannontech.capcontrol.creation.model;

import org.jdom2.Element;
import org.jdom2.Namespace;

import com.cannontech.common.i18n.Displayable;
import com.cannontech.i18n.YukonMessageSourceResolvable;

public interface CbcImportResult extends Displayable {
    public Element getResponseElement(Namespace ns);
    
    public YukonMessageSourceResolvable getMessage();
    
    public CbcImportResultType getResultType();
}
