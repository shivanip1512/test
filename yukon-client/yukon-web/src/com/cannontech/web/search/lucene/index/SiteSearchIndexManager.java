package com.cannontech.web.search.lucene.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.search.result.Page;
import com.cannontech.web.search.lucene.TopDocsCallbackHandler;
import com.cannontech.web.search.lucene.index.site.DbPageIndexBuilder;
import com.cannontech.web.search.lucene.index.site.PageIndexBuilder;
import com.cannontech.web.search.lucene.index.site.StaticPageIndexBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * Index for site-wide search.  This indexes fields.
 * 
 * Searchable:
 *     NOTES:
 *         All searchable fields end up as page or summary arguments
 *         page and summary arguments are used for auto-complete
 *     Static Pages:
 *         page name (localized) and (probably and but maybe or) page title (localized)
 *         theme (this guy can't be combined)
 *         locale (this guy can't be combined)
 *         energyCompanyId (this guy can't be combined)
 *     Meters:
 *         deviceName
 *         meterNumber
 *     Accounts:
 *         primary contact
 *             name
 *             phone number
 *             address?
 *         account number
 *     Cap Control Objects
 *         PAO name
 *     DR Objects
 *         PAO name
 *     Inventory
 *         serial number
 *         meter number
 *         device label?
 *         display name?
 *         alternate tracking number?
 * 
 * Document:
 *     pageKey - Unique key to identify the row (for deletion mainly).  See descriptions below for value.
 *     pageType (modern, siteMap or legacy)
 *     a) for SiteMapPage pages:
 *         pageKey is "sm:" + siteMapPage enum value
 *     b) for normal pages:
 *         pageKey is searcherKey [+ ":" + id]
 *         // maybe consider not storing path...it's kinda redundant
 *         module
 *         pageName (raw)
 *         path
 *         page arguments
 *         extra summary arguments (includes page arguments)
 */
public class SiteSearchIndexManager extends AbstractIndexManager {
    private static final Logger log = YukonLogManager.getLogger(SiteSearchIndexManager.class);

    @Autowired private List<DbPageIndexBuilder> indexBuilders;
    @Autowired private StaticPageIndexBuilder staticPageIndexBuilder;

    private Map<String, PageIndexBuilder> indexBuildersByBasePageKey;

    @Override
    @PostConstruct
    public void init() {
        super.init();
        Builder<String, PageIndexBuilder> builder = ImmutableMap.builder();
        for (DbPageIndexBuilder indexBuilder : indexBuilders) {
            builder.put(indexBuilder.getPageKeyBase(), indexBuilder);
        }
        builder.put(staticPageIndexBuilder.getPageKeyBase(), staticPageIndexBuilder);
        indexBuildersByBasePageKey = builder.build();
    }

    @Override
    public String getIndexName() {
        return "siteSearch";
    }

    @Override
    protected int calculateDocumentCount() {
        int totalCount = 0;

        for (DbPageIndexBuilder indexBuilder : indexBuilders) {
            totalCount += indexBuilder.calculateDocumentCount();
        }

        return totalCount;
    }

    @Override
    protected void buildDocuments(IndexWriter indexWriter, AtomicInteger counter) {
        for (DbPageIndexBuilder indexBuilder : indexBuilders) {
            log.debug("building site search index documents for " + indexBuilder.getPageKeyBase());
            indexBuilder.buildDocuments(indexWriter, counter);
        }
    }

    /**
     * This special function makes sure static pages are indexed and should be called for this search instead
     * of {@link #getSearchTemplate()}.  This method will block until this indexing is complete but it
     * needs to be very quick.  Since there are a very small number of static pages, it is very quick.
     */
    public SearchTemplate getSearchTemplate(YukonUserContext userContext) {
        log.trace("getSearchTemplate - enter");

        // First, make sure static pages have been indexed for this locale.
        SearchTemplate searchTemplate = super.getSearchTemplate();
        TopDocsCallbackHandler<Boolean> handler = new TopDocsCallbackHandler<Boolean>() {
            @Override
            public Boolean processHits(TopDocs topDocs, IndexSearcher indexSearcher) throws IOException {
                return topDocs.totalHits == 0;
            }};
        Query query = staticPageIndexBuilder.getUserContextQuery(userContext);
        boolean needToIndex = true;
        try {
            needToIndex = searchTemplate.doCallBackSearch(query, handler);
        } catch (IOException ioException) {
            throw new IllegalStateException("unexpected error querying site search index", ioException);
        }

        if (needToIndex) {
            if (log.isDebugEnabled()) {
                log.debug("indexing static pages for theme " + userContext.getThemeName() + " and locale "
                    + userContext.getLocale());
            }

            IndexUpdateInfo indexUpdateInfo = staticPageIndexBuilder.buildIndexUpdateInfo(userContext);
            indexImmediately(indexUpdateInfo);
        } else if (log.isDebugEnabled()) {
            log.debug("static pages for theme " + userContext.getThemeName() + " and locale "
                + userContext.getLocale() + " have already been indexed");
        }

        return searchTemplate;
    }

    @Override
    public SearchTemplate getSearchTemplate() {
        throw new UnsupportedOperationException("Use getSearchTemplate(YukonUserContext) for site searches");
    }

    @Override
    protected IndexUpdateInfo processDBChange(DbChangeType dbChangeType, int id, int database, String category) {
        for (DbPageIndexBuilder indexBuilder : indexBuilders) {
            IndexUpdateInfo indexUpdateInfo = indexBuilder.processDBChange(dbChangeType, id, database, category);
            // We're assuming only one handler will handle a given change.
            if (indexUpdateInfo != null) {
                return indexUpdateInfo;
            }
        }

        return null;
    }

    /**
     * Return a list of queries that will help exclude results we know the user won't have permissions for.
     * It is not expected (or possible) for these query to eliminate all results for which the user does not
     * have permissions so for some implementations of {@link DbPageIndexBuilder} and for
     * {@link StaticPageIndexBuilder}, further checking will need to be done after the documents have been read.
     * 
     * The returned queries should be used negatively in a BooleanQuery to eliminate results.
     */
    public List<Query> getPermissionQueries(LiteYukonUser user) {
        List<Query> permissionQueries = new ArrayList<>();

        for (DbPageIndexBuilder indexBuilder : indexBuilders) {
            Query userLimitingQuery = indexBuilder.userLimitingQuery(user);
            if (userLimitingQuery != null) {
                permissionQueries.add(userLimitingQuery);
            }
        }

        return permissionQueries;
    }

    public boolean isAllowedToView(Document document, LiteYukonUser user) {
        String pageKey = document.get("pageKey");
        String basePageKey = pageKey.substring(0, pageKey.lastIndexOf(':'));
        PageIndexBuilder indexBuilder = indexBuildersByBasePageKey.get(basePageKey);
        return indexBuilder.isAllowedToView(document, user);
    }

    /**
     * Convert the document into a "page" model object for use in the UI.  This will delegate to the appropriate
     * {@link PageIndexBuilder} to actually create the document.  This will return null if the user is not allowed
     * to view the page.
     */
    public Page buildPageFromDocument(Document document, LiteYukonUser user) {
        
        String pageKey = document.get("pageKey");
        String basePageKey = pageKey.substring(0, pageKey.lastIndexOf(':'));
        PageIndexBuilder indexBuilder = indexBuildersByBasePageKey.get(basePageKey);
        PageType pageType = PageType.valueOf(document.get("pageType"));
        
        return indexBuilder.isAllowedToView(document, user) ? pageType.buildPage(document) : null;
    }
}
