package com.cannontech.common.search;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.springframework.jdbc.core.JdbcTemplate;

public class PointDeviceLuceneIndexer {

    protected File indexLocation;
    protected JdbcTemplate jdbcTemplate;

    public PointDeviceLuceneIndexer() {
        super();
    }

    protected String getDocumentQuery() {
        String query = "select * " + 
            "from point " + 
            "  join yukonpaobject on (yukonpaobject.paobjectid = point.paobjectid) " + 
            "  join device on (deviceid = yukonpaobject.paobjectid) " + 
            "  left join pointunit on point.pointid = pointunit.pointid " +
            "where category = 'DEVICE'";
        return query;
    }

    protected Document createDocument(ResultSet rs) throws SQLException {
        Document doc = new Document();
        String pointName = rs.getString("pointname");
        String paoName = rs.getString("paoname");
        String uomid = Integer.toString(rs.getInt("uomid"));
        String pointid = Integer.toString(rs.getInt("pointid"));
        String deviceid = Integer.toString(rs.getInt("paobjectid"));
        String all = pointName + " " + paoName + " " + pointid + " " + deviceid;
        doc.add(new Field("point", pointName, Field.Store.YES, Field.Index.TOKENIZED));
        doc.add(new Field("device", paoName, Field.Store.YES, Field.Index.TOKENIZED));
        doc.add(new Field("all", all, Field.Store.YES, Field.Index.TOKENIZED));
        
        doc.add(new Field("uomid", uomid, Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("pointid", pointid, Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("deviceid", deviceid, Field.Store.YES, Field.Index.UN_TOKENIZED));
        return doc;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setIndexLocation(File indexLocation) {
        this.indexLocation = indexLocation;
    }

}
