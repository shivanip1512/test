package com.cannontech.web.taglib;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;

public class RolePropertyTEI extends TagExtraInfo {
    public boolean isValid(TagData tagData) {
        boolean propertySet = tagData.getAttribute("property") != null;
        boolean propertyIdSet = tagData.getAttribute("propertyid") != null;
        return  propertySet != propertyIdSet;
    }

}
