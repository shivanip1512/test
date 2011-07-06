package com.cannontech.common.search;


public interface CustomerAccountSearcher extends Searcher<UltraLightCustomerAccount> {
    public SearchResult<UltraLightCustomerAccount> search(String queryString, YukonObjectCriteria criteria);
}
