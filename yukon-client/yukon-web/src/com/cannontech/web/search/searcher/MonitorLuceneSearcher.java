package com.cannontech.web.search.searcher;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;

import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.search.result.UltraLightMonitor;
import com.cannontech.web.search.lucene.criteria.YukonObjectCriteria;

public class MonitorLuceneSearcher extends AbstractLuceneSearcher<UltraLightMonitor> implements MonitorSearcher {

    @Override
    public UltraLightMonitor buildResults(Document doc) {
        
        int id = Integer.parseInt(doc.get("id"));
        int subId = Integer.parseInt(doc.get("subId"));
        final UltraLightMonitor monitor = new UltraLightMonitor() {

            @Override
            public String getMonitorName() {
                return doc.get("monitor");
            }

            @Override
            public int getId() {
                return id;
            }

            @Override
            public String getType() {
                return doc.get("type");
            }

            @Override
            public int getSubId() {
                return subId;
            }
            
        };
        
        return monitor;
    }

    @Override
    public SearchResults<UltraLightMonitor> all(YukonObjectCriteria criteria, int start, int count) {
        try {
            final Query query = compileAndCombine(new MatchAllDocsQuery(), criteria);
            return doSearch(query, start, count);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
