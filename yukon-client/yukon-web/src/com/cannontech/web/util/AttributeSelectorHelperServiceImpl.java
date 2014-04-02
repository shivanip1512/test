package com.cannontech.web.util;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;

public class AttributeSelectorHelperServiceImpl implements AttributeSelectorHelperService {

    private AttributeService attributeService;
    
    public Set<Attribute> getAttributeSet(HttpServletRequest request, String selectorName, String backupParameterName) {
        
        if (selectorName == null) {
            selectorName = "attribute";
        }
        if (backupParameterName == null) {
            backupParameterName = "selectedAttributeStrs";
        }
        
        String[] attributeParametersArray = ServletRequestUtils.getStringParameters(request, selectorName);
        Set<Attribute> attributeSet = new HashSet<Attribute>();
        
        if (attributeParametersArray.length > 0) {
            for (String attrStr : attributeParametersArray) {
                attributeSet.add(attributeService.resolveAttributeName(attrStr));
            }
        } else {
            String selectedAttributeStrs = ServletRequestUtils.getStringParameter(request, backupParameterName, null);
            if (selectedAttributeStrs != null) {
                String[] selectedAttributeStrsArray = StringUtils.split(selectedAttributeStrs, ",");
                for (String attrStr : selectedAttributeStrsArray) {
                    attributeSet.add(attributeService.resolveAttributeName(attrStr));
                }
            }
        }
        return attributeSet;
    }
    
    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
}
