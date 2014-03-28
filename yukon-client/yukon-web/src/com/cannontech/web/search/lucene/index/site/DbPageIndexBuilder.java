package com.cannontech.web.search.lucene.index.site;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.search.lucene.index.AbstractIndexManager.IndexUpdateInfo;
import com.cannontech.web.search.lucene.index.SiteSearchIndexManager;

public abstract class DbPageIndexBuilder implements PageIndexBuilder {
    private static final Logger log = YukonLogManager.getLogger(DbPageIndexBuilder.class);

    @Autowired protected YukonJdbcTemplate jdbcTemplate;

    protected final String pageKeyBase;

    // database and category of applicable DB change messages.  category can be null.
    protected final int database;
    protected final String category;

    /**
     * The "select ..." bit of the query.  This is used when rebuilding the index as well as when reading a single
     * item for a database update.
     */
    protected abstract SqlFragmentSource getBaseQuery();

    /**
     * The tables to query from.  This should start with "from" and might include joins.  Like baseQuery, this is used
     * when rebuilding the entire index as well as when reading a single item for an update.  It is also used to build
     * the "count" query when rebuilding the index (used to generate an accurate progress bar).
     */
    protected abstract SqlFragmentSource getQueryTables();

    /**
     * An optional where class that should be added to a query if filtering is needed for a complete rebuild of the
     * index.  This can be used, for example, to filter out the rows we tend to have with "0" ids.  This should _not_
     * include the keyword "where" or "and".  (e.g. "inventoryId <> 0")
     */
    protected abstract SqlFragmentSource getAllWhereClause();

    private final YukonRowMapper<Document> documentRowMapper = new YukonRowMapper<Document>() {
        @Override
        public Document mapRow(YukonResultSet rs) throws SQLException {
            return createDocument(rs);
        }};

    protected DbPageIndexBuilder(String pageKeyBase, int database, String category) {
        this.pageKeyBase = pageKeyBase;
        this.database = database;
        this.category = category;
    }

    @Override
    public String getPageKeyBase() {
        return pageKeyBase;
    }

    /**
     * The lucene index uses the value created by this method as a primary key.  Since meters may have the same
     * ids as accounts or inventory, the returned value should include other data to make it unique.
     * 
     * See more information the documentation for the {@link SiteSearchIndexManager} class.
     */
    protected final String createPageKey(int id) {
        return pageKeyBase + ":" + id;
    }

    /**
     * This is implemented in each base class to teach the index how to build a Lucene document from a database row.
     * This will be called for each item when re-indexing as well as when updating a single item.
     */
    protected abstract Document createDocument(YukonResultSet rs) throws SQLException;

    /**
     * Calculate the total number of documents this builder will generate if
     * {@link #buildDocuments(IndexWriter, AtomicInteger, YukonUserContext)} were to be
     * called right now.
     */
    public final int calculateDocumentCount() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select count(*)");
        sql.append(getQueryTables());
        int count = jdbcTemplate.queryForInt(sql);
        log.trace(pageKeyBase + " has " + count + " documents");
        return count;
    }

    /**
     * Build the documents. This method is expected to take a long time and to be run in a separate
     * thread. Classes implementing this method should increment counter as each document is added
     * to the indexWriter.
     */
    public final void buildDocuments(final IndexWriter indexWriter, final AtomicInteger counter) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(getBaseQuery());
        sql.append(getQueryTables());
        SqlFragmentSource allWhereClause = getAllWhereClause();
        if (allWhereClause != null) {
            sql.append("where").append(allWhereClause);
        }

        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                Document document = createDocument(rs);
                if (log.isTraceEnabled()) {
                    log.trace("processing document " + document.get("pageKey") + " (count: " + counter.get() + ")");
                }

                try {
                    indexWriter.addDocument(document);
                } catch (IOException e) {
                    log.error("Exception adding " + pageKeyBase + " document to site index", e);
                    throw new RuntimeException("Exception adding document to site index", e);
                }

                counter.incrementAndGet();
            }
        });
    }

    /**
     * Subclasses need to override this to handle DBChanges.  They should handle them by returning a where
     * clause to be used in grabbing the updated object from the database if if the database change message is
     * pertinent.  Otherwise, they should return null.
     */
    protected abstract SqlFragmentSource getWhereClauseForDbChange(int database, String category, int id);

    public IndexUpdateInfo processDBChange(DbChangeType dbChangeType, int id, int database, String category) {
        if (database != this.database || this.category != null && !this.category.equalsIgnoreCase(category)) {
            // This database change isn't applicable to this index builder.
            if (log.isTraceEnabled()) {
                log.trace("ignoring DB change not applicable to page index builder " + pageKeyBase + "(dbChangeType="
                    + dbChangeType + ", id=" + id +", database=" + database + ", category=" + category);
            }

            return null;
        }

        if (log.isTraceEnabled()) {
            log.trace("handling DB change in page index builder " + pageKeyBase + "(dbChangeType="
                + dbChangeType + ", id=" + id +", database=" + database + ", category=" + category);
        }

        Term deleteTerm = new Term("pageKey", createPageKey(id));

        if (dbChangeType == DbChangeType.DELETE) {
            // No new or updated documents if it's a delete...just need the deleteTerm.
            List<Document> documents = Collections.emptyList();
            return new IndexUpdateInfo(documents, deleteTerm);
        }

        SqlFragmentSource whereClause = getWhereClauseForDbChange(database, category, id);
        if (whereClause != null) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append(getBaseQuery());
            sql.append(getQueryTables());
            sql.append("where").append(whereClause);
            List<Document> documents = jdbcTemplate.query(sql, documentRowMapper);
            return new IndexUpdateInfo(documents, deleteTerm);
        }

        return null;
    }
}
