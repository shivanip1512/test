package com.cannontech.web.search.lucene.index.site;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

import com.cannontech.database.data.lite.LiteYukonUser;

public interface PageIndexBuilder {
    /**
     * Return the bit of the key that is unique to documents indexed by this instance of {@link DbPageIndexBuilder}.
     */
    String getPageKeyBase();

    /**
     * This method is called to get a negative query which can filter out documents the user is not allowed to view.
     * i.e. This query will be used in a {@link BooleanQuery} with a {@link Occur#MUST_NOT} to limit search results.
     * This is not the end of the story, however as {@link #isAllowedToView(Document, LiteYukonUser)} will be called
     * for all documents also.
     * 
     * This method should return null if no filtering is generally possible for this builder type.
     */
    Query userLimitingQuery(LiteYukonUser user);

    /**
     * This method should be called to check permissions on a given document before including it in a search list.
     * 
     * The caller is responsible for only calling this with documents applicable to this {@link PageIndexBuilder}.
     */
    boolean isAllowedToView(Document document, LiteYukonUser user);
}
