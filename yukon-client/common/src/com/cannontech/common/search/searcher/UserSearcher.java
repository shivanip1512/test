package com.cannontech.common.search.searcher;

import com.cannontech.common.search.criteria.YukonObjectCriteria;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.search.result.UltraLightYukonUser;


public interface UserSearcher extends Searcher<UltraLightYukonUser> {
    public SearchResults<UltraLightYukonUser> search(String queryString, YukonObjectCriteria criteria);
}
