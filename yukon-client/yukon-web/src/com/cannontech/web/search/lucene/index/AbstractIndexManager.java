package com.cannontech.web.search.lucene.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.web.search.lucene.TopDocsCallbackHandler;
import com.cannontech.web.search.lucene.YukonObjectAnalyzer;

/**
 * Abstract class which manages index building and updating.
 */
@ManagedResource
public abstract class AbstractIndexManager implements IndexManager, DBChangeListener {
    private static final Logger log = YukonLogManager.getLogger(AbstractIndexManager.class);

    @Autowired private ConfigurationSource configurationSource;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
    private static final String DATE_CREATED_PROPERTY = "created";

    // This number can have a large affect on memory usage and index
    // building speed. 1000 was initially determined to be a 'sweet'
    // spot for speed. This number can and may have to be changed in the
    // future because of memory restrictions and/or speed requirements.
    private int maxBufferedDocs = 200;

    // All indexes will be written to: {yukon home}/cache/{index name}/index
    private Directory indexLocation;
    private File versionFile;
    private Date dateCreated;
    private int recordCount;
    private AtomicInteger count = new AtomicInteger(0);
    private AtomicInteger updatesProcessedCount = new AtomicInteger(0);
    private AtomicInteger updatesCommittedCount = new AtomicInteger(0);
    private AtomicInteger updateErrorCount = new AtomicInteger(0);

    
    public static final FieldType TYPE_NOT_STORED = new FieldType(StringField.TYPE_NOT_STORED);

    /** Indexed, tokenized, stored. */
    public static final FieldType TYPE_STORED = new FieldType(StringField.TYPE_STORED);

    static {

        TYPE_NOT_STORED.setOmitNorms(false);
        TYPE_STORED.setOmitNorms(false);
    };

    private boolean isBuilding;
    private boolean buildIndex;
    private LinkedBlockingQueue<IndexUpdateInfo> updateQueue;
    private RuntimeException currentException;
    private Thread managerThread;
    private boolean shutdownNow;

    @PostConstruct
    public void init() {
        maxBufferedDocs = configurationSource.getInteger("WEB_INDEX_MANAGER_MAX_BUFFERED_DOCS", maxBufferedDocs);
        int queueSize = configurationSource.getInteger("WEB_INDEX_MANAGER_QUEUE_SIZE", 10000);
        updateQueue = new LinkedBlockingQueue<IndexUpdateInfo>(queueSize);

        // Using SimpleFSDirectory isn't the speediest form of Directories, but seems to be the most
        // stable of the implementations.  Once they fix some of the other options we should look at switching.
        File indexFileLocation = new File(CtiUtilities.getYukonBase() + "/cache/" + getIndexName() + "/index");
        try {
            indexLocation = new SimpleFSDirectory(indexFileLocation.toPath());
            // Read in the information from the version file (if it exists)
            versionFile = new File(indexFileLocation, "version.txt");

            try (InputStream iStream = new FileInputStream(versionFile)) {
                Properties properties = new Properties();
                properties.load(iStream);

                String dateString = properties.getProperty(DATE_CREATED_PROPERTY);
                if (dateString != null) {
                    try {
                        dateCreated = DATE_FORMAT.parse(dateString);
                    } catch (ParseException e) {
                        log.error(e);
                    }
                }
            } catch (FileNotFoundException e) {
                // do nothing - no version.txt
            }
        } catch (IOException e) {
            log.error("Exception reading " + getIndexName() + " lucene index directory", e);
        }

        // Start the index manager thread
        managerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                processBuild(false);
                processUpdates();
            }
        }, getIndexName() + "IndexManager");

        managerThread.start();
    }

    @Override
    public boolean isBuilding() {
        return isBuilding;
    }

    @Override
    public String getDateCreated() {
        if (dateCreated == null) {
            return null;
        }
        return DATE_FORMAT.format(dateCreated);
    }

    public int getRecordCount() {
        return recordCount;
    }

    /**
     * Method to get the analyzer for a specific index.
     * @return Index specific analyzer
     */
    protected Analyzer getAnalyzer() {
        return new YukonObjectAnalyzer();
    }

    @Override
    public float getPercentDone() {
        checkAndRebuildIndex();

        if (recordCount == 0) {
            return recordCount;
        }

        return count.floatValue() / recordCount * 100;
    }
    /**
     * Method to process a DBChangeMsg
     * @param dbChangeType 
     * @param id - Id of db change msg object
     * @param database - Database of the db change msg
     * @param category - Category of the db change msg
     * @param type - Type of the db change msg object
     * @return Index update info for the dbchange or null if the change should
     *         not be processed for this index
     */
    abstract protected IndexUpdateInfo processDBChange(DbChangeType dbChangeType, int id, int database, String category);

    @Override
    public void dbChangeReceived(DBChangeMsg dbChange) {
        if (log.isDebugEnabled()) {
            log.debug("processDBChange(" + dbChange + ")");
        }

        try {
            IndexUpdateInfo info = processDBChange(dbChange.getDbChangeType(), dbChange.getId(), dbChange.getDatabase(),
                dbChange.getCategory());

            if (info != null) {
                updateQueue.put(info);
            }
        } catch (RuntimeException e) {
            if (log.isDebugEnabled()) {
                log.error("Caught exception handling db change for " + getIndexName() + ": ", e);
            } else {
                log.error("Caught exception handling db change for " + getIndexName() + ": " + e);
            }
            updateErrorCount.getAndIncrement();
            currentException = e;
        } catch (InterruptedException e) {
            log.error("Caught exception handling db change for " + getIndexName() + ": " + e);
        }
    }

    @Override
    public synchronized void rebuildIndex() {
        if (!isBuilding && !managerThread.isInterrupted()) {
            buildIndex = true;
            currentException = null;
            managerThread.interrupt();
        }
    }

    @Required
    public void setAsyncDynamicDataSource(AsyncDynamicDataSource dataSource) {
        dataSource.addDBChangeListener(this);
    }

    private class SearchTemplateImpl implements SearchTemplate {
        @Override
        public <R> R doCallBackSearch(Query query, TopDocsCallbackHandler<R> handler, final int maxResults)
                throws IOException {
            // Make sure there are currently no issues with the index
            checkAndRebuildIndex();

            // Make sure the index is not currently being built
            if (isBuilding) {
                throw new IndexBeingBuiltException("The index is currently being built. Please try again later.");
            }

            // Make sure we don't search while someone is updating the index
            try (IndexReader indexReader = DirectoryReader.open(indexLocation)) {
                IndexSearcher indexSearcher = new IndexSearcher(indexReader);
                TopDocs topDocs = indexSearcher.search(query, maxResults);
                return handler.processHits(topDocs, indexSearcher);
            }
        }

        @Override
        public <R> R doCallBackSearch(Query query, TopDocsCallbackHandler<R> handler) throws IOException {
            return doCallBackSearch(query, handler, Integer.MAX_VALUE);
        }
    }

    @Override
    public SearchTemplate getSearchTemplate() {
        return new SearchTemplateImpl();
    }

    @Override
    @PreDestroy
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
                IndexUpdateInfo info = updateQueue.take();

                // Make sure we don't update while someone is searching or building the index

                IndexWriter indexModifier = null;
                try {
                    indexModifier = new IndexWriter(indexLocation, getIndexWriterConfig());

                    while (info != null) {
                        processSingleInfoWithWriter(indexModifier, info);

                        // while we have the writer open, let's grab some more entries after taking a short nap
                        Thread.sleep(5);
                        info = updateQueue.poll();
                    }
                } catch (IOException e) {
                    currentException = new RuntimeException("There was a problem updating the " + getIndexName()
                        + " index", e);
                    updateErrorCount.getAndIncrement();
                    log.error("Caught IOException exception while processing update, index is probably out of sync",
                        currentException);
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
                    log.info("Shutting down " + getIndexName() + " indexing thread");
                    break;
                } else if (buildIndex && !isBuilding()) {
                    // Build the index
                    buildIndex = false;
                    processBuild(true);
                } else {
                    // Interrupted for a reason other than a rebuild - stop
                    // updating
                    break;
                }
            } catch (Throwable e) {
                updateErrorCount.getAndIncrement();
                log.error("Caught unknown exception while processing updates, index is probably out of sync", e);
            }
        }
    }

    private void processSingleInfoWithWriter(IndexWriter writer, IndexUpdateInfo info)
            throws CorruptIndexException, IOException {
        if (info.deleteTerm != null) {
            writer.deleteDocuments(info.deleteTerm);
        }
        if (info.deleteQuery != null) {
            writer.deleteDocuments(info.deleteQuery);
        }

        List<Document> docList = info.docList;
        if (docList != null) {
            for (Document doc : docList) {
                writer.addDocument(doc);
            }
        }
        updatesProcessedCount.getAndIncrement();
    }

    /**
     * Overriding classes must implement this to return a document count.  This is called when re-indexing to help
     * with the progress bar.
     */
    abstract protected int calculateDocumentCount();

    /**
     * Build the Lucene documents and add them to the given IndexWriter, incrementing the counter as you go.  This
     * is called when re-indexing and should rebuild every document.
     */
    abstract protected void buildDocuments(IndexWriter indexWriter, AtomicInteger counter);

    /**
     * Helper method to build the index
     * @param forceRebuild Set this to true if you want to force a rebuild, use false to just ensure
     *            the index has been built.
     */
    private void processBuild(boolean forceRebuild) {
        if (!forceRebuild) {
            boolean indexExists;
            try {
                indexExists = DirectoryReader.indexExists(indexLocation);
            } catch (IOException e) {
                indexExists = false;
            }

            if (indexExists) {
                return;
            }
        }

        isBuilding = true;

        // Set the dateCreated to the time the index building started
        dateCreated = new Date();

        // Make sure we don't build while someone is searching or updating the index
        // Create the index
        IndexWriter indexWriter = null;
        try {
            log.info("Building " + getIndexName() + " index.");

            // Get a new index writer
            indexWriter = new IndexWriter(indexLocation,  getIndexWriterConfig().setOpenMode(OpenMode.CREATE));

            // Get the total # of records to be written into the index
            recordCount = calculateDocumentCount();

            buildDocuments(indexWriter, count);

            // Reset the current document count and clear any exceptions
            count.set(0);

            log.info(getIndexName() + " index has been built.");
            currentException = null;

        } catch (IOException e) {
            currentException = new RuntimeException(e);
            throw currentException;
        } catch (RuntimeException e) {
            currentException = e;
            throw currentException;
        } finally {
            try {
                if (indexWriter != null) {
                    indexWriter.close();
                }
            } catch (IOException e) {
                log.error(e);
            }
        }

        // Create the version file
        OutputStream oStream = null;
        try {
            versionFile.delete();
            versionFile.createNewFile();
            oStream = new FileOutputStream(versionFile);

            Properties properties = new Properties();

            Date date = new Date();

            properties.setProperty(DATE_CREATED_PROPERTY, DATE_FORMAT.format(date));
            properties.store(oStream, null);

            dateCreated = date;
        } catch (IOException e) {
            log.error("Exception creating " + getIndexName() + " index version file", e);
        } finally {
            try {
                if (oStream != null) {
                    oStream.close();
                }
            } catch (IOException e) {
                // Do nothing - tried to close;
            }
        }

        isBuilding = false;
    }

    /**
     * Helper method to check for a current exception and rebuild index
     */
    private void checkAndRebuildIndex() {
        File indexFileLocation = new File(CtiUtilities.getYukonBase() + "/cache/" + getIndexName());
        if (currentException != null || !indexFileLocation.exists()) {
            shutdown();
            if (currentException != null) {
                boolean isDeleted = FileUtils.deleteQuietly(indexFileLocation);
                if (isDeleted == false) {
                    log.warn("This index may be corrupted because of a previous exception: ", currentException);
                    return;
                }
            }
            log.info("Rebuilding indexes for ", getIndexName());
            processBuild(true);
        }
    }

    /**
     * Helper class which contains information for an index update
     */
    public static final class IndexUpdateInfo {
        private final List<Document> docList;
        private final Term deleteTerm;
        private final Query deleteQuery;

        /**
         * @param docs - List of documents to be written into the index
         * @param deleteTerm - Term to be used to remove any deleted or old documents from
         *            the index before inserting updated documents.
         */
        public IndexUpdateInfo(List<Document> docList, Term deleteTerm) {
            this.docList = docList;
            this.deleteTerm = deleteTerm;
            this.deleteQuery = null;
        }

        /**
         * @param docs - List of documents to be written into the index
         * @param deleteQuery - query to be used to remove any deleted or old documents from
         *            the index before inserting updated documents.
         */
        public IndexUpdateInfo(List<Document> docList, Query deleteQuery) {
            this.docList = docList;
            this.deleteTerm = null;
            this.deleteQuery = deleteQuery;
        }
    }

    /**
     * Can be used by base classes to process a set of updates immediately.  This is for indexing site map
     * pages just before doing a search.  There are so few of them, we can afford to do this.
     */
    protected synchronized void indexImmediately(IndexUpdateInfo indexUpdateInfo) {
        try (IndexWriter indexWriter = new IndexWriter(indexLocation, getIndexWriterConfig())) {
            processSingleInfoWithWriter(indexWriter, indexUpdateInfo);
        } catch (IOException e) {
            throw new RuntimeException("Failed to index immediately.", e);
        }
    }

    protected IndexWriterConfig getIndexWriterConfig() {
        IndexWriterConfig writerConfig = new IndexWriterConfig(getAnalyzer());
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
}
