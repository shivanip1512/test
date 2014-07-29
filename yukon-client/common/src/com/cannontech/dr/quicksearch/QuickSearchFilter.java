package com.cannontech.dr.quicksearch;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

/**
 * DisplayablePao filter for DR quick search
 */
public class QuickSearchFilter implements UiFilter<DisplayablePao> {

    private String searchText = null;
    
    public QuickSearchFilter(String searchText) {
        this.searchText = searchText;
    }
    
    @Override
    public Iterable<PostProcessingFilter<DisplayablePao>> getPostProcessingFilters() {
        return null;
    }

    @Override
    public Iterable<SqlFilter> getSqlFilters() {
        List<SqlFilter> retVal = new ArrayList<SqlFilter>(1);
        retVal.add(new SqlFilter(){
            
            @Override
            public SqlFragmentSource getWhereClauseFragment() {
                SqlStatementBuilder retVal = new SqlStatementBuilder("LOWER(paoName) like LOWER(");
                retVal.appendArgument("%" + searchText + "%");
                retVal.append(")");
                return retVal;
            }});
        
        return retVal;
    }

}
