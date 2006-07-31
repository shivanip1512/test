package com.cannontech.common.search;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexModifier;
import org.apache.lucene.index.Term;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.cannontech.clientutils.CTILogger;

public class PointDeviceLuceneUpdater extends PointDeviceLuceneIndexer {
    
    private final class LuceneResultSetIndexer implements ResultSetExtractor {
        private final IndexModifier modifier;
        
        private LuceneResultSetIndexer(IndexModifier modifier) {
            super();
            this.modifier = modifier;
        }
        
        public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
            int batchSize = 10;
            int sleepTime = 500;
            
            List<Document> docBacklog = new ArrayList<Document>(batchSize);
            List<Term> termBacklog = new ArrayList<Term>(batchSize);
            try {
                long start = System.currentTimeMillis();
                while (rs.next()) {
                    Document doc = createDocument(rs);
                    docBacklog.add(doc);
                    Term term = new Term("pointid", Integer.toString(rs.getInt("pointid")));
                    termBacklog.add(term);
                    
                    if (docBacklog.size() == batchSize) {
                        // consider opening and closing IndexModifier instead of using sync block
                        // delete old documents
                        for (Term aTerm : termBacklog) {
                            modifier.deleteDocuments(aTerm);
                        }
                        // add back new documents
                        for (Document aDoc : docBacklog) {
                            modifier.addDocument(aDoc);
                        }
                        long elapsed = System.currentTimeMillis() - start;
                        System.out.println("updated " + batchSize + " in " + elapsed + "ms");
                        termBacklog.clear();
                        docBacklog.clear();
                        Thread.sleep(sleepTime);
                        start = System.currentTimeMillis();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to modify index", e);
            } catch (InterruptedException e) {
                CTILogger.info("Received InterruptedException, exiting loop");
            }
            return null;
        }
    }
    
    public void updateWholeIndex() {
        IndexModifier indexModifier = null;
        try {
            indexModifier = new IndexModifier(indexLocation, new PointDeviceAnalyzer(), true);
            final AtomicInteger count = new AtomicInteger(0);
            
            String query = getDocumentQuery();
            ResultSetExtractor rse = new LuceneResultSetIndexer(indexModifier);
            // consider retreiving batches from database (simple
            // impl would be based on pointid ranges) so that
            // result set isn't held open for so long
            jdbcTemplate.query(query, rse );
        } catch (Exception e) {
            CTILogger.error(e);
        } finally {
            try {
                indexModifier.close();
            } catch (IOException e) {
                CTILogger.error(e);
            }
        }
    }
    
    
    
    private void startBackgroundThread() {
        Thread background = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        updateWholeIndex();
                    } catch (Exception e) {
                        CTILogger.error("Background index updater did not complete normally, sleeping.", e);
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e1) {
                            break;
                        }
                    }
                }
                CTILogger.warn("Background index updater is exiting.");
            }
        });
        background.start();
    }
    
    
}
