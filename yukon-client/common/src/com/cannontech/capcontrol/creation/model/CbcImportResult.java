package com.cannontech.capcontrol.creation.model;

import org.jdom.Element;
import org.jdom.Namespace;

import com.cannontech.i18n.YukonMessageSourceResolvable;

public interface CbcImportResult {
    public Element getResponseElement(Namespace ns);
    
    public YukonMessageSourceResolvable getResolvable();
    
    public CbcImportResultType getResultType();
}
