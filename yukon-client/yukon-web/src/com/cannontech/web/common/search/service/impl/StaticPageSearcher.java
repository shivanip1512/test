package com.cannontech.web.common.search.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.search.YukonObjectAnalyzer;
import com.cannontech.common.search.YukonObjectSearchAnalyzer;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.search.result.Page;
import com.cannontech.web.common.search.service.PageSearcher;
import com.cannontech.web.support.SiteMapHelper;
import com.cannontech.web.support.SiteMapPage;
import com.cannontech.web.support.SiteMapPage.PermissionLevel;

@Service
public class StaticPageSearcher implements PageSearcher {
    private final Logger log = YukonLogManager.getLogger(StaticPageSearcher.class);

    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private SiteMapHelper siteMapHelper;

    protected final static Analyzer analyzer = new YukonObjectSearchAnalyzer();

    private final static class IndexKey {
        final String themeName;
        final Locale locale;

        public IndexKey(String themeName, Locale locale) {
            this.themeName = themeName;
            this.locale = locale;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((locale == null) ? 0 : locale.hashCode());
            result = prime * result + ((themeName == null) ? 0 : themeName.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            IndexKey other = (IndexKey) obj;
            if (locale == null) {
                if (other.locale != null)
                    return false;
            } else if (!locale.equals(other.locale))
                return false;
            if (themeName == null) {
                if (other.themeName != null)
                    return false;
            } else if (!themeName.equals(other.themeName))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "theme [" + themeName + "], locale " + locale;
        }
    }

    private Map<IndexKey, Directory> indexes = new HashMap<>();

    private synchronized Directory getIndex(YukonUserContext userContext) {
        IndexKey indexKey = new IndexKey(userContext.getThemeName(), userContext.getLocale());
        Directory directory = indexes.get(indexKey);
        if (directory != null) {
            log.debug("found index for " + indexKey);
            return directory;
        }

        log.debug("building index for " + indexKey);

        directory = new RAMDirectory();
        indexes.put(indexKey, directory);

        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_34, new YukonObjectAnalyzer());

        try (IndexWriter indexWriter = new IndexWriter(directory, config)) {
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            for (SiteMapPage siteMapPage : SiteMapPage.values()) {
                Document document = new Document();
                MessageSourceResolvable resolvable = new YukonMessageSourceResolvable(siteMapPage.getFormatKey());
                String pageName = messageSourceAccessor.getMessage(resolvable);
                document.add(new Field("pageName", pageName, Field.Store.YES, Field.Index.ANALYZED));
                document.add(new Field("page", siteMapPage.name(), Field.Store.YES, Field.Index.NOT_ANALYZED));
                if (log.isTraceEnabled()) {
                    log.trace("indexed " + siteMapPage + " as " + pageName);
                }
                indexWriter.addDocument(document);
            }
        } catch (IOException exception) {
            throw new RuntimeException("error creating index for userContext " + userContext, exception);
        }

        return directory;
    }

    @Override
    public SearchResults<Page> search(String searchString, int count, YukonUserContext userContext) {
        Directory directory = getIndex(userContext);
        List<Page> list = new ArrayList<>();
        try (IndexReader reader = IndexReader.open(directory); IndexSearcher searcher = new IndexSearcher(reader) ){
            QueryParser parser = new QueryParser(Version.LUCENE_34, "pageName", analyzer);
            parser.setDefaultOperator(Operator.AND);
            Query query = parser.parse(searchString);
            TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document document = searcher.doc(scoreDoc.doc);
                SiteMapPage siteMapPage = SiteMapPage.valueOf(document.get("page"));
                PermissionLevel permission = siteMapHelper.getPermissionLevel(siteMapPage, userContext.getYukonUser());
                if (log.isTraceEnabled()) {
                    log.trace("found page " + siteMapPage + " with permission " + permission);
                }
                if (permission == PermissionLevel.ACCESS) {
                    list.add(new Page(siteMapPage));
                }
            }
        } catch (IOException exception) {
            throw new RuntimeException("error searching index for userContext " + userContext, exception);
        } catch (ParseException exception) {
            // TODO:
            throw new RuntimeException("unable to parse Lucene expression", exception);
        }

        SearchResults<Page> results = new SearchResults<>();
        results.setResultList(list);
        results.setBounds(0, count, list.size());

        return results;
    }

    @Override
    public Set<String> autocomplete(String searchString, int count, YukonUserContext userContext) {
        Directory directory = getIndex(userContext);
        Set<String> results = new HashSet<>();
        try (IndexReader reader = IndexReader.open(directory); IndexSearcher searcher = new IndexSearcher(reader) ) {
            QueryParser parser = new QueryParser(Version.LUCENE_34, "pageName", analyzer);
            Query query = parser.parse(searchString);
            TopDocs topDocs = searcher.search(query, count);
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document document = searcher.doc(scoreDoc.doc);
                SiteMapPage siteMapPage = SiteMapPage.valueOf(document.get("page"));
                PermissionLevel permission = siteMapHelper.getPermissionLevel(siteMapPage, userContext.getYukonUser());
                String pageName = document.get("pageName");
                if (log.isTraceEnabled()) {
                    log.trace("found page " + siteMapPage + " with permission " + permission);
                }
                if (permission == PermissionLevel.ACCESS) {
                    results.add(pageName);
                }
            }
        } catch (IOException exception) {
            throw new RuntimeException("error searching index for userContext " + userContext, exception);
        } catch (ParseException exception) {
            // TODO:
            throw new RuntimeException("unable to parse Lucene expression", exception);
        }

        return results;
    }
}
