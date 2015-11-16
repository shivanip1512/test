package com.cannontech.web.search.lucene.index;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;

/**
 * Abstract class which manages index building and updating indexes based on a single database query.
 */
public abstract class SimpleIndexManager extends AbstractIndexManager {
    private static final Logger log = YukonLogManager.getLogger(SimpleIndexManager.class);
    
    @Autowired protected YukonJdbcTemplate jdbcTemplate;
    /**
     * Method to get the query used to build documents for a specific index.
     * <br />
     * <br />
     * @return Index specific document query
     */
    abstract protected SqlStatementBuilder getDocumentQuery();

    @Override
    protected void buildDocuments(IndexWriter indexWriter, AtomicInteger counter) {
        YukonRowCallbackHandler rch = new LuceneRowIndexer(indexWriter, counter);
        jdbcTemplate.query(getDocumentQuery(), rch);
    }

    /**
     * Method to get the number of records that will be in the index. This query
     * should be identical to the getDocumentQuery, the only change being this
     * query should return a count of records and not the actual records.
     * <code>Select count(*) from .... vs Select * from ....</code> <br />
     * <br />
     * @return Index specific document count query
     */
    abstract protected SqlStatementBuilder getDocumentCountQuery();

    @Override
    protected int calculateDocumentCount() {
        int recordCount = jdbcTemplate.queryForInt(getDocumentCountQuery());
        return recordCount;
    }

    /**
     * Method to process a result set row into a document for a specific index.
     * <br />
     * <br />
     * @param rs - Result set to build the document from
     * @return Index specific document
     */
    abstract protected Document createDocument(YukonResultSet rs) throws SQLException;

    /**
     * Helper class which is used to process a result set into documents and
     * write each document into the index.
     */
    private final class LuceneRowIndexer implements YukonRowCallbackHandler {
        private final IndexWriter writer;
        private final AtomicInteger count;

        private LuceneRowIndexer(IndexWriter writer, AtomicInteger count) {
            this.writer = writer;
            this.count = count;
        }

        @Override
        public void processRow(YukonResultSet rs) throws SQLException {
            Document doc = createDocument(rs);

            try {
                writer.addDocument(doc);
            } catch (IOException e) {
                log.error("Exception adding document to " + getIndexName() + " index", e);
                throw new RuntimeException("Exception adding document to " + getIndexName() + " index", e);
            }

            count.incrementAndGet();
        }
    }

    /**
     * Mapping class to process a result set row into a Document
     */
    protected class DocumentMapper implements YukonRowMapper<Document> {

        @Override
        public Document mapRow(YukonResultSet rs) throws SQLException {
            return createDocument(rs);
        }
    }
}
