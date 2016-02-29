package com.cannontech.web.search.lucene.index.site;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.search.lucene.index.PageType;
import com.cannontech.web.search.lucene.index.AbstractIndexManager.IndexUpdateInfo;
import com.cannontech.web.support.SiteMapHelper;
import com.cannontech.web.support.SiteMapPage;
import com.cannontech.web.support.SiteMapPage.PermissionLevel;

public class StaticPageIndexBuilder implements PageIndexBuilder {
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private SiteMapHelper siteMapHelper;

    @Override
    public String getPageKeyBase() {
        return "siteMap";
    }

    public Query getUserContextQuery(YukonUserContext userContext) {
        BooleanQuery.Builder userContextQuery = new BooleanQuery.Builder();
        userContextQuery.add(new TermQuery(new Term("theme", userContext.getThemeName())), Occur.MUST);
        userContextQuery.add(new TermQuery(new Term("locale", userContext.getLocale().toLanguageTag())), Occur.MUST);
        return userContextQuery.build();
    }

    public IndexUpdateInfo buildIndexUpdateInfo(YukonUserContext userContext) {
        List<Document> documents = new ArrayList<>();

        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        for (SiteMapPage siteMapPage : SiteMapPage.values()) {
            DocumentBuilder builder = new DocumentBuilder();
            builder.pageKey(getPageKeyBase() + ":" + siteMapPage.name());
            builder.pageType(PageType.SITE_MAP);
            MessageSourceResolvable resolvable = new YukonMessageSourceResolvable(siteMapPage.getFormatKey());
            String pageName = messageSourceAccessor.getMessage(resolvable);
            // Adding this to the pageArgs makes it searchable and usable for auto-complete searches.
            builder.pageArgs(pageName);
            builder.theme(userContext.getThemeName());
            builder.locale(userContext.getLocale());
            documents.add(builder.build());
        }

        // Don't really need the deleteQuery since we don't index if we already have them indexed but
        // it's here for completeness.
        Query deleteQuery = getUserContextQuery(userContext);
        IndexUpdateInfo indexUpdateInfo = new IndexUpdateInfo(documents, deleteQuery);

        return indexUpdateInfo;
    }

    @Override
    public Query userLimitingQuery(LiteYukonUser user) {
        return null;
    }

    @Override
    public boolean isAllowedToView(Document document, LiteYukonUser user) {
        String[] pageKeyParts = document.get("pageKey").split(":");
        SiteMapPage siteMapPage = SiteMapPage.valueOf(pageKeyParts[1]);
        PermissionLevel permission = siteMapHelper.getPermissionLevel(siteMapPage, user);
        return permission == PermissionLevel.ACCESS;
    }
}
