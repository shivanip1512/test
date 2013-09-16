package com.cannontech.common.search.searcher;

import com.cannontech.common.search.criteria.YukonObjectCriteria;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.search.result.UltraLightCustomerAccount;


public interface CustomerAccountSearcher extends Searcher<UltraLightCustomerAccount> {
    public SearchResults<UltraLightCustomerAccount> search(String queryString, YukonObjectCriteria criteria);
}
