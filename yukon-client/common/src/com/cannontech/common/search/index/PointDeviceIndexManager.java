package com.cannontech.common.search.index;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;

import com.cannontech.common.search.PointDeviceAnalyzer;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.message.dispatch.message.DBChangeMsg;

/**
 * Class which manages point device lucene index creation and update.
 */
public class PointDeviceIndexManager extends AbstractIndexManager {

    public String getIndexName() {
        return "pointPicker";
    }

    protected int getIndexVersion() {
        return 1;
    }

    protected Analyzer getAnalyzer() {
        return new PointDeviceAnalyzer();
    }

    protected String getDocumentQuery() {
        String query = "select *                                                        "
                + " from                                                                "
                + "     point                                                           "
                + " join                                                                "
                + "     yukonpaobject on (yukonpaobject.paobjectid = point.paobjectid)  "
                + " join                                                                "
                + "     device on (deviceid = yukonpaobject.paobjectid)                 "
                + " left join                                                           "
                + "     pointunit on point.pointid = pointunit.pointid                  "
                + " where                                                               "
                + "     category = 'DEVICE'                                             ";
        return query;
    }

    protected String getDocumentCountQuery() {
        String query = "select count(*)                                             "
                + " from                                                                "
                + "     point                                                           "
                + " join                                                                "
                + "     yukonpaobject on (yukonpaobject.paobjectid = point.paobjectid)  "
                + " join                                                                "
                + "     device on (deviceid = yukonpaobject.paobjectid)                 "
                + " left join                                                           "
                + "     pointunit on point.pointid = pointunit.pointid                  "
                + " where                                                               "
                + "     category = 'DEVICE'";
        return query;
    }

    protected Document createDocument(ResultSet rs) throws SQLException {

        Document doc = new Document();

        String pointName = rs.getString("pointname");
        String paoName = rs.getString("paoname");
        int uomidInt = rs.getInt("uomid");
        // additional fields for point types and point states
        int stateGrpID = rs.getInt("stategroupid");
        String pointType = rs.getString("pointtype");
        String paoType = rs.getString("type");
        if (rs.wasNull()) {
            uomidInt = PointUnits.UOMID_INVALID;
        }
        String uomid = Integer.toString(uomidInt);
        String pointid = Integer.toString(rs.getInt("pointid"));
        String deviceid = Integer.toString(rs.getInt("paobjectid"));
        String stateGroupID = Integer.toString(stateGrpID);
        String all = pointName + " " + paoName + " " + pointid + " " + deviceid;
        doc.add(new Field("point", pointName, Field.Store.YES, Field.Index.TOKENIZED));
        doc.add(new Field("device", paoName, Field.Store.YES, Field.Index.TOKENIZED));
        doc.add(new Field("all", all, Field.Store.YES, Field.Index.TOKENIZED));

        doc.add(new Field("uomid", uomid, Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("pointid", pointid, Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("deviceid", deviceid, Field.Store.YES, Field.Index.UN_TOKENIZED));

        doc.add(new Field("pointtype", pointType, Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("stategroupid", stateGroupID, Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("paotype", paoType, Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("pointName", pointName, Field.Store.NO, Field.Index.UN_TOKENIZED));

        return doc;
    }

    protected void processDBChange(int id, int database, String category, String type) {
        if (database == DBChangeMsg.CHANGE_PAO_DB && "DEVICE".equalsIgnoreCase(category)) {
            // Device change msg
            this.processDeviceChange(id);
        } else if (database == DBChangeMsg.CHANGE_POINT_DB
                && DBChangeMsg.CAT_POINT.equals(category)) {
            // Point change msg
            this.processPointChange(id);
        }
    }

    /**
     * Helper method to process a device change
     * @param deviceId - Id of changed device
     */
    @SuppressWarnings("unchecked")
    private void processDeviceChange(int deviceId) {

        Term term = new Term("deviceid", Integer.toString(deviceId));
        List<Document> docList = new ArrayList<Document>();

        StringBuffer sql = new StringBuffer(this.getDocumentQuery());
        sql.append(" AND device.deviceid = ?");

        docList = this.jdbcTemplate.query(sql.toString(),
                                          new Object[] { deviceId },
                                          new DocumentMapper());
        this.updateIndex(docList, term);

    }

    /**
     * Helper method to process a point change
     * @param pointId - Id of changed point
     */
    @SuppressWarnings("unchecked")
    private void processPointChange(int pointId) {

        Term term = new Term("pointid", Integer.toString(pointId));
        List<Document> docList = new ArrayList<Document>();

        StringBuffer sql = new StringBuffer(this.getDocumentQuery());
        sql.append(" AND point.pointid = ?");

        docList = this.jdbcTemplate.query(sql.toString(),
                                          new Object[] { pointId },
                                          new DocumentMapper());
        this.updateIndex(docList, term);

    }

}
