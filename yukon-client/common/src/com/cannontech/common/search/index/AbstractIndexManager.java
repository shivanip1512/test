package com.cannontech.common.search.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexModifier;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.message.dispatch.message.DBChangeMsg;

/**
 * Abstract class which manages index creation and update.
 */
public abstract class AbstractIndexManager implements IndexManager {

    private static final String VERSION_PROPERTY = "version";
    private static final String DATE_CREATED_PROPERTY = "created";

    // This number can have a large affect on memory usage and index
    // building speed. 1000 was initially determined to be a 'sweet'
    // spot for speed. This number can and may have to be changed in the
    // future because of memory restrictions and/or speed requirements.
    private static final int MAX_BUFFERED_DOCS = 1000;

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

    protected JdbcOperations jdbcTemplate = null;

    // All indexes will be written to: {yukon home}/cache/{index name}/index
    private File indexLocation = null;
    private File versionFile = null;

    private String version = null;
    private Date dateCreated = null;

    private boolean isBuilding = false;

    private int recordCount = 0;
    private AtomicInteger count = new AtomicInteger(0);

    private RuntimeException currentException = null;

    public AbstractIndexManager() {
        this.initialize();
    }

    public boolean isBuilding() {
        return this.isBuilding;
    }

    public void setBuilding(boolean isBuilding) {
        this.isBuilding = isBuilding;
    }

    public Date getDateCreated() {
        return this.dateCreated;
    }

    public String getFormattedDateCreated() {
        if (this.dateCreated == null) {
            return null;
        }
        return DATE_FORMAT.format(this.dateCreated);
    }

    public String getVersion() {
        return this.version;
    }

    public File getIndexLocation() {
        return this.indexLocation;
    }

    public int getRecordCount() {
        return this.recordCount;
    }

    public void getCurrentRecord() {
        this.count.get();
    }

    public IndexSearcher getIndexSearcher() {

        // Make sure there are currently no issues with the index
        this.checkForException();

        // Make sure the index is not currently being built
        if (this.isBuilding) {
            throw new RuntimeException("The index is currently being built. Please try again later.");
        }

        try {
            return new IndexSearcher(this.indexLocation.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setAsyncDynamicDataSource(AsyncDynamicDataSource dataSource) {
        dataSource.addDBChangeListener(this);
    }

    /**
     * Method to get the current version of the index
     * @return Index version
     */
    abstract protected int getIndexVersion();

    /**
     * Method to get the analyzer for a specific index.
     * @return Index specific analyzer
     */
    abstract protected Analyzer getAnalyzer();

    /**
     * Method to get the query used to build documents for a specific index.
     * <br />
     * <br />
     * ****NOTE: if you change the query in this method, you must increment the
     * version number returned by getIndexVersion() for the specific index
     * <br />
     * @return Index specific document query
     */
    abstract protected String getDocumentQuery();

    /**
     * Method to get the number of records that will be in the index. This query
     * should be identical to the getDocumentQuery, the only change being this
     * query should return a count of records and not the actual records.
     * <code>Select count(*) from .... vs Select * from ....</code> <br />
     * <br />
     * ****NOTE: if you change the query in this method, you must increment the
     * version number returned by getIndexVersion() for the specific index
     * <br />
     * @return Index specific document count query
     */
    abstract protected String getDocumentCountQuery();

    /**
     * Method to process a result set row into a document for a specific index.
     * <br />
     * <br />
     * ****NOTE: if you change the way a document is created or change the
     * fields in the document, you must increment the version number returned by
     * getIndexVersion() for the specific index <br />
     * @param rs - Result set to build the document from
     * @return Index specific document
     */
    abstract protected Document createDocument(ResultSet rs) throws SQLException;

    /**
     * Method to process a DBChangeMsg
     * @param id - Id of db change msg object
     * @param database - Database of the db change msg
     * @param category - Category of the db change msg
     * @param type - Type of the db change msg object
     */
    abstract protected void processDBChange(int id, int database, String category, String type);

    public float getPercentDone() {

        this.checkForException();

        if (this.recordCount == 0) {
            return this.recordCount;
        }

        float curr = this.count.floatValue();
        float total = this.recordCount;

        return (curr / total) * 100;

    }

    public synchronized void createIndex(boolean overwrite) {

        if (!overwrite) {
            boolean indexExists = true;
            try {
                IndexSearcher indexSearcher = new IndexSearcher(indexLocation.getAbsolutePath());
                indexSearcher.close();
            } catch (IOException e) {
                indexExists = false;
            }

            if (indexExists && this.isCurrentVersion()) {
                return;
            }
        }
        
        this.version = null;
        // Set the dateCreated to the time the index building started
        this.dateCreated = new Date();

        // Create the index
        IndexWriter indexWriter = null;
        try {
            CTILogger.info("Building " + this.getIndexName() + " index.");

            // Get a new index writer
            indexWriter = new IndexWriter(indexLocation.getAbsolutePath(), getAnalyzer(), true);
            indexWriter.setMaxBufferedDocs(MAX_BUFFERED_DOCS);

            // Get the total # of records to be written into the index
            String sql = getDocumentCountQuery();
            recordCount = jdbcTemplate.queryForInt(sql);

            sql = getDocumentQuery();

            RowCallbackHandler rch = new LuceneRowIndexer(indexWriter, count);
            jdbcTemplate.query(sql, rch);
            indexWriter.optimize();

            // Reset the current document count and clear any exceptions
            count.set(0);
            this.currentException = null;
            CTILogger.info(this.getIndexName() + " index has been built.");

        } catch (IOException e) {
            this.currentException = new RuntimeException(e);
            throw this.currentException;
        } catch (RuntimeException e) {
            this.currentException = e;
            throw this.currentException;
        } finally {
            isBuilding = false;
            try {
                if (indexWriter != null) {
                    indexWriter.close();
                }
            } catch (IOException e) {
                CTILogger.error(e);
            }
        }

        // Create the version file
        OutputStream oStream = null;
        try {
            versionFile.delete();
            versionFile.createNewFile();
            oStream = new FileOutputStream(versionFile);

            Properties properties = new Properties();

            String newVersion = String.valueOf(getIndexVersion());
            Date date = new Date();

            properties.setProperty(VERSION_PROPERTY, newVersion);
            properties.setProperty(DATE_CREATED_PROPERTY, DATE_FORMAT.format(date));
            properties.store(oStream, null);

            version = newVersion;
            dateCreated = date;

        } catch (IOException e) {
            CTILogger.error("Exception creating " + this.getIndexName() + " index version file", e);
        } finally {
            try {
                if (oStream != null) {
                    oStream.close();
                }
            } catch (IOException e) {
                // Do nothing - tried to close;
            }
        }

    }

    public void dbChangeReceived(DBChangeMsg dbChange) {

        this.processDBChange(dbChange.getId(),
                             dbChange.getDatabase(),
                             dbChange.getCategory(),
                             dbChange.getObjectType());

    }

    /**
     * Method to update the index
     * @param docs - List of documents to be written into the index
     * @param term - Term to be used to remove any outdated documents from the
     *            index before inserting new documents
     */
    protected void updateIndex(List<Document> docs, Term term) {

        IndexModifier indexModifier = null;
        try {
            indexModifier = new IndexModifier(this.indexLocation, this.getAnalyzer(), false);
            indexModifier.deleteDocuments(term);

            if (docs.size() != 0) {
                for (Document doc : docs) {
                    indexModifier.addDocument(doc);
                }
            }

        } catch (IOException e) {
            this.currentException = new RuntimeException("There was a problem updating the "
                    + this.getIndexName() + " index", e);
        } finally {
            try {
                if (indexModifier != null) {
                    indexModifier.close();
                }
            } catch (IOException e) {
                // Do nothing - tried to close
            }
        }
    }

    /**
     * Helper method to check for a current exception and throw if there is one
     */
    private void checkForException() {
        if (this.currentException != null) {
            throw this.currentException;
        }
    }

    /**
     * Helper method to initialize the index manager
     */
    private void initialize() {

        this.indexLocation = new File(CtiUtilities.getYukonBase() + "/cache/" + this.getIndexName()
                + "/index");

        // Read in the information from the version file (if it exists)
        this.versionFile = new File(this.indexLocation, "version.txt");
        try {
            InputStream iStream = new FileInputStream(this.versionFile);
            Properties properties = new Properties();
            properties.load(iStream);

            this.version = properties.getProperty(VERSION_PROPERTY);
            String dateString = properties.getProperty(DATE_CREATED_PROPERTY);
            if (dateString != null) {
                try {
                    this.dateCreated = DATE_FORMAT.parse(dateString);
                } catch (ParseException e) {
                    CTILogger.error(e);
                }
            }
        } catch (FileNotFoundException e) {
            // do nothing - no version.txt
        } catch (IOException e) {
            CTILogger.error("Exception reading " + this.getIndexName() + " index version file", e);
        }
    }

    /**
     * Helper method to determine if the index version is the same as the index
     * code version
     * @return True if the versions match
     */
    private boolean isCurrentVersion() {
        return String.valueOf(this.getIndexVersion()).equals(this.version);
    }

    /**
     * Helper class which is used to process a result set into documents and
     * write each document into the index.
     */
    private final class LuceneRowIndexer implements RowCallbackHandler {

        private final IndexWriter writer;
        private final AtomicInteger count;

        private LuceneRowIndexer(IndexWriter writer, AtomicInteger count) {
            super();
            this.writer = writer;
            this.count = count;
        }

        public void processRow(ResultSet rs) throws SQLException {

            Document doc = createDocument(rs);

            try {
                writer.addDocument(doc);
            } catch (IOException e) {
                throw new RuntimeException("Exception adding document to " + getIndexName()
                        + " index", e);
            }

            count.incrementAndGet();
        }
    }

    /**
     * Mapping class to process a result set row into a Document
     */
    protected class DocumentMapper implements RowMapper {

        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return createDocument(rs);
        }
    }

}
