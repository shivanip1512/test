package com.cannontech.web.taglib;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;

import org.apache.commons.lang.ArrayUtils;

public class OutputDoctypeTEI extends TagExtraInfo {
    public boolean isValid(TagData tagData) {
        String htmlLevel = tagData.getAttributeString("htmlLevel");
        return ArrayUtils.contains(StandardPageTag.ALLOWED_HTML_LEVELS, htmlLevel);
    }

}
