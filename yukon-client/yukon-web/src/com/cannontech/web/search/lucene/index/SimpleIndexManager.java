package com.cannontech.web.search.lucene.index;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.clientutils.YukonLogManager;

/**
 * Abstract class which manages index building and updating indexes based on a single database query.
 */
public abstract class SimpleIndexManager extends AbstractIndexManager {
    private static final Logger log = YukonLogManager.getLogger(SimpleIndexManager.class);

    protected JdbcOperations jdbcTemplate = null;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Method to get the query used to build documents for a specific index.
     * <br />
     * <br />
     * @return Index specific document query
     */
    abstract protected String getDocumentQuery();

    @Override
    protected void buildDocuments(IndexWriter indexWriter, AtomicInteger counter) {
        RowCallbackHandler rch = new LuceneRowIndexer(indexWriter, counter);
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
    abstract protected String getDocumentCountQuery();

    @Override
    protected int calculateDocumentCount() {
        String sql = getDocumentCountQuery();
        int recordCount = jdbcTemplate.queryForInt(sql);
        return recordCount;
    }

    /**
     * Method to process a result set row into a document for a specific index.
     * <br />
     * <br />
     * @param rs - Result set to build the document from
     * @return Index specific document
     */
    abstract protected Document createDocument(ResultSet rs) throws SQLException;

    /**
     * Helper class which is used to process a result set into documents and
     * write each document into the index.
     */
    private final class LuceneRowIndexer implements RowCallbackHandler {
        private final IndexWriter writer;
        private final AtomicInteger count;

        private LuceneRowIndexer(IndexWriter writer, AtomicInteger count) {
            this.writer = writer;
            this.count = count;
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
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
    protected class DocumentMapper implements RowMapper<Document> {
        @Override
        public Document mapRow(ResultSet rs, int rowNum) throws SQLException {
            return createDocument(rs);
        }
    }
}
