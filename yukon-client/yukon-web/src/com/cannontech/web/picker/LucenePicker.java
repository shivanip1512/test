package com.cannontech.web.picker;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import com.cannontech.common.search.result.SearchResults;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.search.lucene.criteria.YukonObjectCriteria;
import com.cannontech.web.search.lucene.criteria.YukonObjectCriteriaHelper;
import com.cannontech.web.search.searcher.Searcher;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public abstract class LucenePicker<T> extends BasePicker<T> {
    
    protected YukonObjectCriteria criteria = null;
    protected Searcher<T> searcher;
    
    @JsonDeserialize(as = YukonObjectCriteriaHelper.class)
    public YukonObjectCriteria getCriteria() {
        return criteria;
    }
    
    @Override
    public SearchResults<T> search(String ss, int start, int count,
            String extraArgs, YukonUserContext userContext) {
        SearchResults<T> hits;
        
        YukonObjectCriteria combinedCriteria = combineCriteria(criteria, userContext, extraArgs);
        if (StringUtils.isBlank(ss)) {
            hits = searcher.all(combinedCriteria, start, count);
        } else {
            hits = searcher.search(ss, combinedCriteria, start , count);
        }
        return hits;
    }
    
    @Override
    public SearchResults<T> search(Collection<Integer> initialIds, String extraArgs, YukonUserContext userContext) {
        YukonObjectCriteria combinedCriteria = combineCriteria(criteria, initialIds);
        SearchResults<T> hits = searcher.all(combinedCriteria, 0, initialIds.size());
        return hits;
    }
    
    /**
     * Override this method if you need to have dynamic criteria based on the logged in user or any extra
     * arguments.
     * 
     * @param baseCriteria The base criteria. This should always be accounted for in the returned criteria.
     *        The default implementation simply returns it.
     * @param userContext The {@link YukonUserContext} for the logged in user.
     * @param extraArgs Any extra arguments passed to the picker by the JSP tag.
     */
    public YukonObjectCriteria combineCriteria(YukonObjectCriteria baseCriteria, YukonUserContext userContext,
            String extraArgs) {
        return baseCriteria;
    }
    
    public YukonObjectCriteria combineCriteria(YukonObjectCriteria baseCriteria,
            Iterable<Integer> initialIds) {
        
        final BooleanQuery.Builder query = new BooleanQuery.Builder().setDisableCoord(false);
        if (baseCriteria != null) {
            query.add(baseCriteria.getCriteria(), Occur.MUST);
        }
        
        final BooleanQuery.Builder idQuery = new BooleanQuery.Builder().setDisableCoord(false);
        
        for (Integer id : initialIds) {
            TermQuery termQuery = new TermQuery(new Term(getLuceneIdFieldName(), id.toString()));
            idQuery.add(termQuery, BooleanClause.Occur.SHOULD);
        }
        
        query.add(idQuery.build(), BooleanClause.Occur.MUST);
        
        return new YukonObjectCriteria() {
            @Override
            public Query getCriteria() {
                return query.build();
            }
        }; 
    }
    
    /**
     * Subclasses need to override this method if the id field name is not the
     * same as the database field.
     */
    protected String getLuceneIdFieldName() {
        return getIdFieldName();
    }
    
    public void setCriteria(YukonObjectCriteria criteria) {
        this.criteria = criteria;
    }
    
    public void setSearcher(Searcher<T> searcher) {
        this.searcher = searcher;
    }
    
}