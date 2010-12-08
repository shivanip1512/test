package com.cannontech.web.picker.v2;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;

import com.cannontech.common.search.SearchResult;
import com.cannontech.common.search.Searcher;
import com.cannontech.common.search.YukonObjectCriteria;
import com.cannontech.user.YukonUserContext;

public abstract class LucenePicker<T> extends BasePicker<T> {
    protected YukonObjectCriteria criteria = null;
    protected Searcher<T> searcher;

    public YukonObjectCriteria getCriteria() {
        return criteria;
    }


    @Override
    public SearchResult<T> search(String ss, int start, int count,
            String extraArgs, YukonUserContext userContext) {
        SearchResult<T> hits;
        
        YukonObjectCriteria combinedCriteria = combineCriteria(criteria, extraArgs);
		if (StringUtils.isBlank(ss)) {
            hits = searcher.all(combinedCriteria, start, count);
        } else {
            hits = searcher.search(ss, combinedCriteria, start , count);
        }
        return hits;
    }

    @Override
    public SearchResult<T> search(Iterable<Integer> initialIds,
            String extraArgs, YukonUserContext userContext) {
        YukonObjectCriteria combinedCriteria = combineCriteria(criteria, initialIds);
        SearchResult<T> hits = searcher.all(combinedCriteria, 0, Integer.MAX_VALUE);
        return hits;
    }

    public YukonObjectCriteria combineCriteria(YukonObjectCriteria baseCriteria, String extraArgs) {
    	return baseCriteria;
    }

    public YukonObjectCriteria combineCriteria(YukonObjectCriteria baseCriteria,
            Iterable<Integer> initialIds) {
        final BooleanQuery query = new BooleanQuery(false);

        if (baseCriteria != null) {
            query.add(baseCriteria.getCriteria(), Occur.MUST);
        }

        final BooleanQuery idQuery = new BooleanQuery(false);

        for (Integer id : initialIds) {
            TermQuery termQuery = new TermQuery(new Term(getLuceneIdFieldName(), id.toString()));
            idQuery.add(termQuery, BooleanClause.Occur.SHOULD);
        }

        query.add(idQuery, BooleanClause.Occur.MUST);

        return new YukonObjectCriteria() {
            @Override
            public Query getCriteria() {
                return query;
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
