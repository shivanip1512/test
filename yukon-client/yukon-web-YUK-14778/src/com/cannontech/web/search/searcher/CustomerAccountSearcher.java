package com.cannontech.web.search.searcher;

import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.search.result.UltraLightCustomerAccount;
import com.cannontech.web.search.lucene.criteria.YukonObjectCriteria;


public interface CustomerAccountSearcher extends Searcher<UltraLightCustomerAccount> {
    public SearchResults<UltraLightCustomerAccount> search(String queryString, YukonObjectCriteria criteria);
}
