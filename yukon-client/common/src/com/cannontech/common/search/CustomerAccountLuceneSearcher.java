package com.cannontech.common.search;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

public class CustomerAccountLuceneSearcher extends AbstractLuceneSearcher<UltraLightCustomerAccount> implements CustomerAccountSearcher {
    private static final Sort sort = new Sort(new String[]{"accountNumber",  "energyCompanyId"});
    
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

    public SearchResult<UltraLightCustomerAccount> all(YukonObjectCriteria criteria, int start, int count) {
        try {
            final Query query = compileAndCombine(new MatchAllDocsQuery(), criteria);
            return doSearch(query, sort, start, count);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
