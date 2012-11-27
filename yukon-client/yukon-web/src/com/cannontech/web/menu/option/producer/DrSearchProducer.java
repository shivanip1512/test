package com.cannontech.web.menu.option.producer;

import com.cannontech.user.YukonUserContext;

public class DrSearchProducer implements SearchProducer {
    
    public SearchFormData getSearchProducer(YukonUserContext userContext) {
        return new SearchFormData("/dr/search", "name");
    }
}

