package com.cannontech.web.search.lucene.index;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Term;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.google.common.collect.ImmutableList;

/**
 * Class which manages point device lucene index creation and update.
 */
public class PointDeviceIndexManager extends SimpleIndexManager {
    @Override
    public String getIndexName() {
        return "point";
    }

    @Override
    protected SqlStatementBuilder getDocumentQuery() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append(getQueryGuts());
        sql.append(getOrderBy());
        return sql;
    }

    @Override
    protected SqlStatementBuilder getDocumentCountQuery() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select count(*)");
        sql.append(getQueryGuts());
        return sql;
    }
    
    private SqlStatementBuilder getQueryGuts() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("from point p");
        sql.append("join yukonpaobject ypo on (ypo.paobjectid = p.paobjectid)");
        sql.append("left join pointunit pu on p.pointid = pu.pointid");
        sql.append("where ypo.category").in(ImmutableList.of(PaoCategory.DEVICE, PaoCategory.CAPCONTROL, PaoCategory.LOADMANAGEMENT));
        return sql;
    }
    
    private SqlStatementBuilder getOrderBy() {
        return new SqlStatementBuilder("order by p.pointname, p.pointid");
    }

    @Override
    protected Document createDocument(YukonResultSet rs) throws SQLException {

        Document doc = new Document();

        String pointName = rs.getString("pointname");
        String paoName = rs.getString("paoname");
        int uomidInt = rs.getInt("uomid");
        if (rs.wasNull()) {
            uomidInt = UnitOfMeasure.INVALID.getId();
        }
        // additional fields for point types and point states
        int stateGrpID = rs.getInt("stategroupid");
        String pointType = rs.getString("pointtype");
        String paoType = rs.getString("type");
        int pointOffset = rs.getInt("pointoffset");
        
        String uomid = Integer.toString(uomidInt);
        String pointid = Integer.toString(rs.getInt("pointid"));
        String deviceid = Integer.toString(rs.getInt("paobjectid"));
        String stateGroupID = Integer.toString(stateGrpID);
        String all = pointName + " " + paoName + " " + pointid + " " + deviceid;
        String category = rs.getString("category");
        doc.add(new TextField("point", pointName, Field.Store.YES));
        doc.add(new TextField("device", paoName, Field.Store.YES));
        doc.add(new TextField("all", all, Field.Store.NO));

        doc.add(new Field("uomid", uomid, TYPE_NOT_STORED));
        doc.add(new Field("pointid", pointid, TYPE_STORED));
        doc.add(new Field("deviceid", deviceid, TYPE_STORED));

        doc.add(new Field("pointtype", pointType, TYPE_STORED));
        doc.add(new Field("stategroupid", stateGroupID, TYPE_NOT_STORED));
        doc.add(new Field("paotype", paoType, TYPE_NOT_STORED));
        doc.add(new Field("pointName", pointName, TYPE_NOT_STORED));
        doc.add(new Field("pointoffset", Integer.toString(pointOffset), TYPE_NOT_STORED));
        doc.add(new Field("category", category, TYPE_STORED));

        return doc;
    }

    @Override
    protected IndexUpdateInfo processDBChange(DbChangeType dbChangeType, int id, int database, String category) {
        if (database == DBChangeMsg.CHANGE_PAO_DB && (PaoCategory.DEVICE.getDbString().equalsIgnoreCase(category) 
                || PaoCategory.CAPCONTROL.getDbString().equalsIgnoreCase(category))) {
            // Device change msg
            
            if(id == 0) {
                // Bulk dbchange msg - rebuild index;
                this.rebuildIndex();
                return null;
            }
            
            return this.processDeviceChange(dbChangeType, id);
        } else if (database == DBChangeMsg.CHANGE_POINT_DB
                && DBChangeMsg.CAT_POINT.equals(category)) {
            // Point change msg
            return this.processPointChange(dbChangeType, id);
        }

        // Return null if no update is to be done
        return null;
    }

    /**
     * Helper method to process a device change
     * @param deviceId - Id of changed device
     * @return Index update info for the device change
     */
    private IndexUpdateInfo processDeviceChange(DbChangeType dbChangeType, int deviceId) {

        Term term = new Term("deviceid", Integer.toString(deviceId));
        if (dbChangeType == DbChangeType.DELETE) {
            // no need to hit the database to create an empty
            // list of documents for that deviceId
            // note: deleting a device will NOT currently generate point delete messages
            return new IndexUpdateInfo(null, term);
        }
        //Device ADD messages may be the only message we get that there were points added.  
        // Must processes the ADD Device message as if it were and ADD for all Points on the Device. 

        List<Document> docList = new ArrayList<Document>();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append(getQueryGuts());
        sql.append("AND p.paobjectid").eq(deviceId);
        sql.append(getOrderBy());

        docList = this.jdbcTemplate.query(sql, new DocumentMapper());
        return new IndexUpdateInfo(docList, term);
    }

    /**
     * Helper method to process a point change
     * @param pointId - Id of changed point
     * @return Index update info for the point change
     */
    private IndexUpdateInfo processPointChange(DbChangeType dbChangeType, int pointId) {

        Term term = new Term("pointid", Integer.toString(pointId));
        if (dbChangeType == DbChangeType.DELETE) {
            // no need to hit the database to create an empty
            // list of documents for that pointId
            return new IndexUpdateInfo(null, term);
        }
        List<Document> docList = new ArrayList<Document>();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append(getQueryGuts());
        sql.append(" AND p.pointid").eq(pointId);
        sql.append(getOrderBy());

        docList = this.jdbcTemplate.query(sql, new DocumentMapper());
        return new IndexUpdateInfo(docList, term);
    }

}
