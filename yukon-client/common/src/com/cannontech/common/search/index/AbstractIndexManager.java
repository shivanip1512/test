package com.cannontech.common.search.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.search.TopDocsCallbackHandler;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;

/**
 * Abstract class which manages index building and updating.
 */
@ManagedResource
public abstract class AbstractIndexManager implements IndexManager {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
    private static final String VERSION_PROPERTY = "version";
    private static final String DATABASE_PROPERTY = "database";
    private static final String DATABASE_USER_PROPERTY = "dbuser";
    private static final String DATE_CREATED_PROPERTY = "created";

    private ConfigurationSource configurationSource = null;

    // This number can have a large affect on memory usage and index
    // building speed. 1000 was initially determined to be a 'sweet'
    // spot for speed. This number can and may have to be changed in the
    // future because of memory restrictions and/or speed requirements.
    private int maxBufferedDocs = 200;

    protected JdbcOperations jdbcTemplate = null;

    // All indexes will be written to: {yukon home}/cache/{index name}/index
    private Version luceneVersion = Version.LUCENE_34;
    private Directory indexLocation = null;
    private File versionFile = null;
    private String version = null;
    private String database = null;
    private String databaseUsername = null;
    private Date dateCreated = null;
    private int recordCount = 0;
    private AtomicInteger count = new AtomicInteger(0);
    private AtomicInteger updatesProcessedCount = new AtomicInteger(0);
    private AtomicInteger updatesCommittedCount = new AtomicInteger(0);
    private AtomicInteger updateErrorCount = new AtomicInteger(0);

    private boolean isBuilding = false;
    private boolean buildIndex = false;
    private LinkedBlockingQueue<IndexUpdateInfo> updateQueue = null;
    private RuntimeException currentException = null;
    private Thread managerThread;
    private boolean shutdownNow = false;

    public AbstractIndexManager() {
    }

    public boolean isBuilding() {
        return this.isBuilding;
    }

    public String getDateCreated() {
        if (this.dateCreated == null) {
            return null;
        }
        return DATE_FORMAT.format(this.dateCreated);
    }

    public String getVersion() {
        return this.version;
    }
    
    public String getDatabase() {
        return this.database;
    }
    
    public String getDatabaseUsername() {
        return this.databaseUsername;
    }
    
    public int getRecordCount() {
        return this.recordCount;
    }

    public void getCurrentRecord() {
        this.count.get();
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
     * @param dbChangeType 
     * @param id - Id of db change msg object
     * @param database - Database of the db change msg
     * @param i 
     * @param category - Category of the db change msg
     * @param type - Type of the db change msg object
     * @return Index update info for the dbchange or null if the change should
     *         not be processed for this index
     */
    abstract protected IndexUpdateInfo processDBChange(DbChangeType dbChangeType, int id, int database, String category,
            String type);

    public float getPercentDone() {

        this.checkForException();

        if (this.recordCount == 0) {
            return this.recordCount;
        }

        float curr = this.count.floatValue();
        float total = this.recordCount;

        return (curr / total) * 100;

    }

    public void dbChangeReceived(DBChangeMsg dbChange) {

        try {
            IndexUpdateInfo info = this.processDBChange(dbChange.getDbChangeType(),
                                                        dbChange.getId(),
                                                        dbChange.getDatabase(),
                                                        dbChange.getCategory(),
                                                        dbChange.getObjectType());

            if (info != null) {
                boolean success = this.updateQueue.offer(info);
                if (!success) {
                    CTILogger.warn("Unable to insert IndexUpdateInfo onto work queue (it is full), index will be out of sync");
                    updateErrorCount.getAndIncrement();
                }
            }
        } catch (RuntimeException e) {
            CTILogger.warn("Caught exception handling db change for " + this.getIndexName() + ": " + e);
            updateErrorCount.getAndIncrement();
            this.currentException = e;
        }

    }

    public synchronized void rebuildIndex() {

        if (!this.isBuilding() && !this.managerThread.isInterrupted()) {
            this.buildIndex = true;
            this.currentException = null;
            this.managerThread.interrupt();
        }
    }

    /**
     * Helper method to initialize the index manager
     */
    public void initialize() {
        maxBufferedDocs = configurationSource.getInteger("WEB_INDEX_MANAGER_MAX_BUFFERED_DOCS", maxBufferedDocs);
        int queueSize = configurationSource.getInteger("WEB_INDEX_MANAGER_QUEUE_SIZE", 1000);
        updateQueue = new LinkedBlockingQueue<IndexUpdateInfo>(queueSize);

        // Using SimpleFSDirectory isn't the speediest form of Directories, but seems to be the most
        // stable of the implementations.  Once they fix some of the other options we should look at switching.
        File indexFileLocation = new File(CtiUtilities.getYukonBase() + "/cache/" + this.getIndexName() + "/index");
        try {
            this.indexLocation = new SimpleFSDirectory(indexFileLocation);
            // Read in the information from the version file (if it exists)
            this.versionFile = new File(indexFileLocation, "version.txt");

            try {
                InputStream iStream = new FileInputStream(this.versionFile);
                Properties properties = new Properties();
                properties.load(iStream);
    
                this.version = properties.getProperty(VERSION_PROPERTY);
                this.database = properties.getProperty(DATABASE_PROPERTY);
                this.databaseUsername = properties.getProperty(DATABASE_USER_PROPERTY);
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
            }
        } catch (IOException e) {
            CTILogger.error("Exception reading " + this.getIndexName() + " lucene index directory", e);
        }

        
        // If index is locked, must have shutdown improperly last time - rebuild
        boolean indexLocked = false;
        try {
            if(IndexWriter.isLocked(indexLocation)){
                IndexWriter.unlock(indexLocation);
                indexLocked = true;
            }
            
        } catch (IOException e) {
            // ignore - must be no index
        }
        final boolean rebuild = indexLocked;

        // Start the index manager thread
        managerThread = new Thread(new Runnable() {
            public void run() {
                processBuild(rebuild);
                processUpdates();
            }
        }, getIndexName() + "IndexManager");

        managerThread.start();

    }
    
    public SearchTemplate getSearchTemplate(){
        return new SearchTemplate(){
            public <R> R doCallBackSearch(Query query, Sort sort, TopDocsCallbackHandler<R> handler) throws IOException {
                
                // Make sure there are currently no issues with the index
                checkForException();

                // Make sure the index is not currently being built
                if (isBuilding) {
                    throw new RuntimeException("The index is currently being built. Please try again later.");
                }
                
                // Make sure we don't search while someone is updating the index
                final IndexSearcher indexSearcher = new IndexSearcher(indexLocation);
                final Sort aSort = (sort == null) ? new Sort() : sort;
                try {
                    TopDocs topDocs = indexSearcher.search(query, Integer.MAX_VALUE, aSort);
                    return handler.processHits(topDocs, indexSearcher);
                } finally {
                    indexSearcher.close();
                }
            }};
    }

    public void shutdown() {
        shutdownNow = true;
        managerThread.interrupt();
    }

    /**
     * Helper method to process updates to the index
     */
    private void processUpdates() {

        // Loop forever - updating the index
        while (true) {
            try {
                IndexUpdateInfo info = this.updateQueue.take();

                // Make sure we don't update while someone is searching or building the index

                IndexWriter indexModifier = null;
                try {
                    indexModifier = new IndexWriter(indexLocation, getIndexWriterConfig());

                    while (info != null) {
                        processSingleInfoWithWriter(indexModifier, info);

                        // while we have the writer open, let's grab some more entries after taking a short nap
                        Thread.sleep(5);
                        info = this.updateQueue.poll();
                    }

                } catch (IOException e) {
                    this.currentException = new RuntimeException("There was a problem updating the "
                                                                 + this.getIndexName()
                                                                 + " index",
                                                                 e);
                    updateErrorCount.getAndIncrement();
                    CTILogger.error("Caught IOException exception while processing update, index is probably out of sync", this.currentException);
                } finally {
                    try {
                        if (indexModifier != null) {
                            indexModifier.close();
                            updatesCommittedCount.getAndIncrement();
                        }
                    } catch (IOException e) {
                        // Do nothing - tried to close
                    }
                }

            } catch (InterruptedException e) {
                if (shutdownNow) {
                    CTILogger.info("Shutting down " + getIndexName() + " indexing thread");
                    break;
                } else if (this.buildIndex && !this.isBuilding()) {
                    // Build the index
                    this.buildIndex = false;
                    this.processBuild(true);

                } else {
                    // Interrupted for a reason other than a rebuild - stop
                    // updating
                    break;
                }
            } catch (Throwable e) {
                updateErrorCount.getAndIncrement();
                CTILogger.warn("Caught unknown exception while processing updates, index is probably out of sync", e);
            }
        }
    }

    public void processSingleInfoWithWriter(IndexWriter writer, IndexUpdateInfo info) throws CorruptIndexException, IOException {
        writer.deleteDocuments(info.getDeleteTerm());

        List<Document> docList = info.getDocList();
        if (docList != null) {
            for (Document doc : docList) {
                writer.addDocument(doc);
            }
        }
        updatesProcessedCount.getAndIncrement();
    }

    /**
     * Helper method to build the index
     */
    private void processBuild(boolean overwrite) {

        if (!overwrite) {
            boolean indexExists = true;
            try {
                IndexSearcher indexSearcher = new IndexSearcher(indexLocation);
                indexSearcher.close();
            } catch (IOException e) {
                indexExists = false;
            }

            if (indexExists && this.isCurrentVersion() && this.isCurrentDatabase() && this.isCurrentDatabaseUser()) {
                return;
            }
        }

        this.isBuilding = true;

        this.version = null;
        this.database = null;
        this.databaseUsername = null;
        // Set the dateCreated to the time the index building started
        this.dateCreated = new Date();


        // Make sure we don't build while someone is searching or updating the index
        // Create the index
        IndexWriter indexWriter = null;
        try {
            CTILogger.info("Building " + this.getIndexName() + " index.");

            // Get a new index writer
            indexWriter = new IndexWriter(indexLocation,  getIndexWriterConfig().setOpenMode(OpenMode.CREATE));

            // Get the total # of records to be written into the index
            String sql = getDocumentCountQuery();
            recordCount = jdbcTemplate.queryForInt(sql);

            sql = getDocumentQuery();

            RowCallbackHandler rch = new LuceneRowIndexer(indexWriter, count);
            jdbcTemplate.query(sql, rch);
            indexWriter.optimize();

            // Reset the current document count and clear any exceptions
            count.set(0);

            CTILogger.info(this.getIndexName() + " index has been built.");
            this.currentException = null;

        } catch (IOException e) {
            this.currentException = new RuntimeException(e);
            throw this.currentException;
        } catch (RuntimeException e) {
            this.currentException = e;
            throw this.currentException;
        } finally {
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
            databaseUsername = getCurrentDbUser();
            database = getCurrentDb();

            properties.setProperty(VERSION_PROPERTY, newVersion);
            properties.setProperty(DATABASE_PROPERTY, database);
            properties.setProperty(DATABASE_USER_PROPERTY, databaseUsername);
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

        this.isBuilding = false;

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
     * Helper method to determine if the index version is the same as the index
     * code version
     * @return True if the versions match
     */
    private boolean isCurrentVersion() {
        return String.valueOf(this.getIndexVersion()).equals(this.version);
    }

    /**
     * Helper method to determine if the index database is the same as the
     * current database
     * @return True if the databases match
     */
    private boolean isCurrentDatabase() {
        return getCurrentDb().equals(this.database);
    }
    
    /**
     * Helper method to determine if the index database user is the same as
     * the current database user
     * @return True if the database users match
     */
    private boolean isCurrentDatabaseUser() {
        return getCurrentDbUser().equals(this.databaseUsername);
    }
    
    /**
     * Helper method to get the URL of the current database
     */
    private String getCurrentDb() {
        String currentDb = (String) jdbcTemplate.execute(new ConnectionCallback() {
            public Object doInConnection(Connection con) throws SQLException, DataAccessException {
                return con.getMetaData().getURL();
            }
        });
        
        return currentDb;
    }
    
    /**
     * Helper method to get the user connecting to the current database.
     */
    private String getCurrentDbUser() {
        String currentDbUser = (String) jdbcTemplate.execute(new ConnectionCallback() {
            public Object doInConnection(Connection con) throws SQLException, DataAccessException {
                return con.getMetaData().getUserName();
            }
        });
        
        return currentDbUser;
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
    protected class DocumentMapper implements RowMapper<Document> {

        public Document mapRow(ResultSet rs, int rowNum) throws SQLException {
            return createDocument(rs);
        }
    }

    /**
     * Helper class which contains information for an index update
     */
    protected class IndexUpdateInfo {
        private List<Document> docList = null;
        private Term deleteTerm = null;

        /**
         * @param docs - List of documents to be written into the index
         * @param term - Term to be used to remove any outdated documents from
         *            the index before inserting new documents
         */
        public IndexUpdateInfo(List<Document> docList, Term deleteTerm) {
            this.docList = docList;
            this.deleteTerm = deleteTerm;
        }

        public Term getDeleteTerm() {
            return deleteTerm;
        }

        public void setDeleteTerm(Term deleteTerm) {
            this.deleteTerm = deleteTerm;
        }

        public List<Document> getDocList() {
            return docList;
        }

        public void setDocList(List<Document> docList) {
            this.docList = docList;
        }

    }
    
    protected IndexWriterConfig getIndexWriterConfig() {
        IndexWriterConfig writerConfig = new IndexWriterConfig(luceneVersion, getAnalyzer());
        writerConfig.setMaxBufferedDocs(maxBufferedDocs);
        return writerConfig;
    }

    @ManagedAttribute
    public int getUpdateQueueSize() {
        return updateQueue.size();
    }

    @ManagedAttribute
    public AtomicInteger getUpdatesProcessedCount() {
        return updatesProcessedCount;
    }

    @ManagedAttribute
    public AtomicInteger getUpdateErrorCount() {
        return updateErrorCount;
    }

    @ManagedAttribute
    public AtomicInteger getUpdatesCommittedCount() {
        return updatesCommittedCount;
    }

    @Autowired
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }

}
