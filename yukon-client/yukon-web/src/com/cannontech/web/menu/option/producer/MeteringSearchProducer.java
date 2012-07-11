package com.cannontech.web.menu.option.producer;

import com.cannontech.user.YukonUserContext;


public class MeteringSearchProducer implements SearchProducer {

    @Override
    public SearchFormData getSearchProducer(YukonUserContext userContext) {
        return new SearchFormData("/spring/meter/search", "quickSearch");
    }
}
