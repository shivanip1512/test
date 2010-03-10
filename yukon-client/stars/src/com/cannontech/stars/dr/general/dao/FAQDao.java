package com.cannontech.stars.dr.general.dao;

import java.util.Map;

import com.cannontech.user.YukonUserContext;


public interface FAQDao {

    public Map<String, Map<String, String>> getQuestionMap(YukonUserContext yukonUserContext);
    
}
