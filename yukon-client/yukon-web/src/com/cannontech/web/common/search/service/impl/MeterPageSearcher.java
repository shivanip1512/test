package com.cannontech.web.common.search.service.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.TopDocsCallbackHandler;
import com.cannontech.common.search.index.PaoIndexManager;
import com.cannontech.common.search.index.SearchTemplate;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.userpage.model.UserPage;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.search.result.Page;
import com.cannontech.web.common.search.service.PageSearcher;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class MeterPageSearcher implements PageSearcher {
    @Autowired private PaoIndexManager indexManager;

    @Override
    public SearchResults<Page> search(String searchString, final int count, YukonUserContext userContext) {
        BooleanQuery searchQuery = new BooleanQuery(false);
        searchQuery.add(new TermQuery(new Term("deviceName", searchString)), Occur.SHOULD);
        searchQuery.add(new TermQuery(new Term("meterNumber", searchString)), Occur.SHOULD);

        BooleanQuery query = new BooleanQuery(false);
        query.add(new TermQuery(new Term("isMeter", "true")), Occur.MUST);
        query.add(searchQuery, Occur.MUST);

        SearchTemplate searchTemplate = indexManager.getSearchTemplate();
        TopDocsCallbackHandler<SearchResults<Page>> handler = new TopDocsCallbackHandler<SearchResults<Page>>() {
            @Override
            public SearchResults<Page> processHits(TopDocs topDocs, IndexSearcher indexSearcher) throws IOException {
                int stop = Math.min(count, topDocs.totalHits);
                List<Page> list = Lists.newArrayListWithCapacity(stop);

                for (int index = 0; index < stop; ++index) {
                    int docId = topDocs.scoreDocs[index].doc;
                    Document document = indexSearcher.doc(docId);
                    String deviceName = document.get("deviceName");
                    @SuppressWarnings("deprecation")
                    PaoType paoType = PaoType.getForDbString(document.get("type"));
                    int paoId = Integer.parseInt(document.get("paoid"));
                    String meterNumber = document.get("meterNumber");

                    String module = "amr";
                    String path = "/meter/home?deviceId=" + paoId;
                    String pageName = "meterDetail.electric";
                    if (paoType.isWaterMeter()) {
                        path = "/meter/water/home?deviceId=" + paoId;
                        pageName = "meterDetail.water";
                    }

                    List<String> arguments = ImmutableList.of(deviceName);
                    UserPage userPage = new UserPage(0, path, false, module, pageName, arguments, null, null);

                    Page page = new Page(userPage, new Object[] { deviceName, meterNumber });
                    list.add(page);
                }

                SearchResults<Page> results = new SearchResults<>();
                results.setResultList(list);
                results.setBounds(0, count, topDocs.totalHits);

                return results;
            }
        };

        try {
            SearchResults<Page> results = searchTemplate.doCallBackSearch(query, handler);
            return results;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Set<String> autocomplete(String searchString, final int count, final String columnToMatch)
            throws IOException {
        BooleanQuery query = new BooleanQuery(false);
        query.add(new TermQuery(new Term("isMeter", "true")), Occur.MUST);
        query.add(new TermQuery(new Term(columnToMatch, searchString)), Occur.MUST);

        SearchTemplate searchTemplate = indexManager.getSearchTemplate();
        TopDocsCallbackHandler<Set<String>> handler = new TopDocsCallbackHandler<Set<String>>() {
            @Override
            public Set<String> processHits(TopDocs topDocs, IndexSearcher indexSearcher) throws IOException {
                int stop = Math.min(count, topDocs.totalHits);
                Set<String> matches = new HashSet<>();

                for (int index = 0; index < stop; ++index) {
                    int docId = topDocs.scoreDocs[index].doc;
                    Document document = indexSearcher.doc(docId);
                    String match = document.get(columnToMatch);
                    matches.add(match);
                }

                return matches;
            }
        };
        return searchTemplate.doCallBackSearch(query, handler);
    }

    @Override
    public Set<String> autocomplete(String searchString, int count, YukonUserContext userContext) {
        try {
            Set<String> matches = new HashSet<>();
            matches.addAll(autocomplete(searchString, count, "deviceName"));
            matches.addAll(autocomplete(searchString, count, "meterNumber"));
            return matches;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
