package com.cannontech.common.search.criteria;

import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.TermQuery;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;

public abstract class YukonPaoTagCriteriaHelper extends YukonObjectCriteriaHelper {
    private PaoDefinitionDao paoDefinitionDao;
    
    @PostConstruct
    public void initialize() {
        for (PaoTag paoTag : getPaoTags()) {
            Set<PaoType> paoTypes = paoDefinitionDao.getPaoTypesThatSupportTag(paoTag);
            for (PaoType paoType : paoTypes) {
                TermQuery termQuery = new TermQuery(new Term("type", paoType.getDbString()));
                query.add(termQuery, BooleanClause.Occur.SHOULD);
            }
        }
        
    }
    
    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }
    
    protected abstract Set<PaoTag> getPaoTags();
}
