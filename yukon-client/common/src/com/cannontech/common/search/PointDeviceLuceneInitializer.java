package com.cannontech.common.search;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.cannontech.clientutils.CTILogger;

public class PointDeviceLuceneInitializer extends PointDeviceLuceneIndexer {
    public PointDeviceLuceneInitializer() {
        super();
    }
    
    public void createInitialIndex() {
        IndexWriter indexWriter = null;
        try {
            indexWriter = new IndexWriter(indexLocation.getAbsolutePath(), new PointDeviceAnalyzer(), true);
            indexWriter.setMaxBufferedDocs(1000);
            long start = System.currentTimeMillis();
            // to create new index, close searcher first
            // now we can recreate searcher
            
            AtomicInteger count = new AtomicInteger(0);
            
            String query = getDocumentQuery();
            RowCallbackHandler rch = new LuceneRowIndexer(indexWriter, start, count);
            jdbcTemplate.query(query, rch );
            indexWriter.optimize();
            long elapsed = System.currentTimeMillis() - start;
            System.out.println(count + " in " + elapsed + "ms");
        } catch (Exception e) {
            CTILogger.error(e);
        } finally {
            try {
                indexWriter.close();
            } catch (IOException e) {
                CTILogger.error(e);
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        PointDeviceLuceneInitializer pds = new PointDeviceLuceneInitializer();
        DriverManagerDataSource ds = 
            new DriverManagerDataSource("net.sourceforge.jtds.jdbc.Driver", 
                                        "jdbc:jtds:sqlserver://mn1db02:1433;APPNAME=yukon-client;TDS=8.0", 
                                        "tom_splains", "tom_splains");
//        DriverManagerDataSource ds = 
//            new DriverManagerDataSource("oracle.jdbc.OracleDriver", 
//                                        "jdbc:oracle:thin:@mn1db02:1521:xcel", 
//                                        "isoc_tom", "isoc_tom");
        JdbcTemplate template = new JdbcTemplate(ds);
        pds.setJdbcTemplate(template);
        pds.setIndexLocation(new File("c:/dev/lucene"));
        pds.createInitialIndex();
    }
    
    private final class LuceneRowIndexer implements RowCallbackHandler {
        private final IndexWriter writer;
        private final long start;
        private final AtomicInteger count;

        private LuceneRowIndexer(IndexWriter writer, long start, AtomicInteger count) {
            super();
            this.writer = writer;
            this.start = start;
            this.count = count;
        }

        public void processRow(ResultSet rs) throws SQLException {
            Document doc = createDocument(rs);
            
            try {
                writer.addDocument(doc);
            } catch (IOException e) {
                CTILogger.error(e);
            }
            int i = count.incrementAndGet();
            long elapsed = System.currentTimeMillis() - start;
            if (i % 1000 == 0) {
                System.out.println(" completed " + i + " in " + elapsed + "ms");
//                try {
//                    writer.optimize();
//                } catch (IOException e) {
//                    CTILogger.error(e);
//                }
            }
        }
    }
}
