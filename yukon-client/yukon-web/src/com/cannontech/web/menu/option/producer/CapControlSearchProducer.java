package com.cannontech.web.menu.option.producer;

import com.cannontech.user.YukonUserContext;


public class CapControlSearchProducer implements SearchProducer {

    @Override
    public SearchFormData getSearchProducer(YukonUserContext userContext) {
        return new SearchFormData("/capcontrol/search/searchResults", "cbc_lastSearch");
    }

}
