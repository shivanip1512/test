package com.cannontech.web.search.searcher;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;

import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.search.result.UltraLightCustomerAccount;
import com.cannontech.web.search.lucene.criteria.YukonObjectCriteria;

public class CustomerAccountLuceneSearcher extends AbstractLuceneSearcher<UltraLightCustomerAccount> implements CustomerAccountSearcher {
    
    @Override
    public UltraLightCustomerAccount buildResults(final Document doc) {
        final UltraLightCustomerAccount ultra = new UltraLightCustomerAccount() {
            public String getAccountNumber() {
                return doc.get("accountNumber");
            }
            public int getAccountId() {
                return Integer.parseInt(doc.get("accountId"));
            }
        };
        return ultra;
    }

    public SearchResults<UltraLightCustomerAccount> all(YukonObjectCriteria criteria, int start, int count) {
        try {
            final Query query = compileAndCombine(new MatchAllDocsQuery(), criteria);
            return doSearch(query, start, count);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
