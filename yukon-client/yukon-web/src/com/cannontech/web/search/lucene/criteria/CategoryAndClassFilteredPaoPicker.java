package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import com.cannontech.common.pao.PaoType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.picker.PaoPicker;

public class CategoryAndClassFilteredPaoPicker extends PaoPicker {
    
    @Override
    public YukonObjectCriteria combineCriteria(YukonObjectCriteria criteria, YukonUserContext userContext,
            String extraArgs) {
        PaoType paoType = PaoType.valueOf(extraArgs);
        
        final BooleanQuery.Builder query = new BooleanQuery.Builder();
        query.setMinimumNumberShouldMatch(2);
        
        TermQuery paoCategoryTermQuery = new TermQuery(new Term("category", paoType.getPaoCategory().getDbString()));
        query.add(paoCategoryTermQuery, BooleanClause.Occur.SHOULD);
        
        TermQuery paoClassTermQuery = new TermQuery(new Term("paoclass", paoType.getPaoClass().getDbString()));
        query.add(paoClassTermQuery, BooleanClause.Occur.SHOULD);
        
        return new YukonObjectCriteria() {
            @Override
            public Query getCriteria() {
                return query.build();
            }
        };
    }
}
