package com.cannontech.web.util;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.common.pao.attribute.model.Attribute;

public interface AttributeSelectorHelperService {

    /**
     * Used to parse out attributes selected from the Attribute Selector (attributeSelector.tag). If none are found it will check for them 
     * in the request as a comma-separated list of attribute values.
     * @param request
     * @param selectorName "fieldName" given to the attibuteSelector.tag attribute. Pass null to use default name of "attribute".
     * @param backupParameterName name of parameter to search for the possible comma-separated list of attributes. Pass null to use default name of "selectedAttributeStrs".
     * @return
     */
    public Set<Attribute> getAttributeSet(HttpServletRequest request, String selectorName, String backupParameterName);
}
