package com.cannontech.amr.csr.service.impl;

import java.util.List;

import com.cannontech.amr.csr.dao.CsrSearchDao;
import com.cannontech.amr.csr.model.FilterBy;
import com.cannontech.amr.csr.model.OrderBy;
import com.cannontech.amr.csr.model.SearchPao;
import com.cannontech.amr.csr.service.CsrService;
import com.cannontech.common.search.SearchResult;

public class CsrServiceImpl implements CsrService {

    private CsrSearchDao searchDao = null;

    public void setCsrSearchDao(CsrSearchDao csrSearchDao) {
        this.searchDao = csrSearchDao;
    }

    public SearchResult<SearchPao> search(List<FilterBy> filterByList, OrderBy orderBy, int start,
            int count) {

        return searchDao.search(filterByList, orderBy, start, count);
    }

}
