package com.cannontech.web.search.searcher;

import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.search.result.UltraLightYukonUser;
import com.cannontech.web.search.lucene.criteria.YukonObjectCriteria;


public interface UserSearcher extends Searcher<UltraLightYukonUser> {
    public SearchResults<UltraLightYukonUser> search(String queryString, YukonObjectCriteria criteria);
}
