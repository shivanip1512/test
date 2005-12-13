package com.cannontech.web.taglib;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class StandardPageTEI extends TagExtraInfo {
    public boolean isValid(TagData tagData) {
        boolean htmlGood = false;
        String htmlLevel = tagData.getAttributeString("htmlLevel");
        if (htmlLevel == null) {
            htmlGood = true;
        } else {
            htmlGood =  ArrayUtils.contains(StandardPageTag.ALLOWED_HTML_LEVELS, htmlLevel);
        }
        
        boolean skinGood = false;
        String skin = tagData.getAttributeString("skin");
        if (skin == null) {
            skinGood = true;
        } else {
            skinGood = ArrayUtils.contains(StandardPageTag.ALLOWED_SKIN_VALUES, skin);
        }
        
        boolean moduleGood = false;
        String module = tagData.getAttributeString("module");
        moduleGood = !StringUtils.isEmpty(module);
            
        return htmlGood && skinGood && moduleGood;
    }

}
