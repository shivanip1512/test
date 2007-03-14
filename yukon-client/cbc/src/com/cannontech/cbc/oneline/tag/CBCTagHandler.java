package com.cannontech.cbc.oneline.tag;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.cannontech.cbc.point.CBCPointFactory;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.capcontrol.CapControlFeeder;
import com.cannontech.database.data.capcontrol.CapControlSubBus;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteTag;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.point.TAGLog;
import com.cannontech.message.dispatch.ClientConnection;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.tags.Tag;
import com.cannontech.tags.TagManager;

public class CBCTagHandler {

    // manages tags for capcontrol
    private YukonPAObject pao;
    private LitePoint point;
    // private OnelineTags tags;
    private String tagName;

    private TagManager tagManager;

    private List<LitePoint> points;
    private String userName;

   
    public CBCTagHandler(ClientConnection conn) {
        tagManager = new TagManager(conn);

    }

    public void init(DBPersistent dbPers, String tagStateName, String uname) {
        pao = (YukonPAObject) dbPers;
        OnelineTags tags = null;
        boolean updatePAO = false;
        point = CBCPointFactory.getTagPoint(pao.getPAObjectID());
        if (dbPers instanceof CapBank) {
            tags = new OnelineTags(OnelineTags.TAGTYPE_CAP);
            if (CapBank.isOpstate(tagStateName)) {
                ((CapBank) dbPers).getCapBank()
                                  .setOperationalState(tagStateName);
                updatePAO = true;
            }
        } else if (pao instanceof CapControlFeeder) {
            tags = new OnelineTags(OnelineTags.TAGTYPE_FDR);

        } else if (pao instanceof CapControlSubBus) {
            tags = new OnelineTags(OnelineTags.TAGTYPE_SUB);
        }
        if (tags != null)
            tagName = tags.getTagName(tagStateName);
        if (updatePAO)
            updatePAO();
        userName = uname;
    }

    private void updatePAO() {
        Connection connection = PoolManager.getInstance()
                                           .getConnection(CtiUtilities.getDatabaseAlias());
        pao.setDbConnection(connection);
        try {
            pao.update();
            DaoFactory.getDbPersistentDao()
                      .performDBChange(pao, DBChangeMsg.CHANGE_TYPE_UPDATE);
        } catch (SQLException e) {
            CTILogger.error(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    CTILogger.error(e);
                }
            }
        }
    }

    public LitePoint getPoint() {
        return point;
    }

    public boolean process(String tagDesc, String reason) {
        try {
            if (reason == null)
                reason = "";

            Integer tagID = getTagID();

            HashSet<Tag> tags = (HashSet<Tag>) tagManager.getTags(point.getPointID());
            boolean tagFound = false;
            for (Tag tag : tags) {
                if (tagName.equalsIgnoreCase(tag.getTaggedForStr())) {
                    tag.setReferenceStr(getReferenceString(tagDesc));
                    tag.setDescriptionStr("(none)");
                    tagManager.removeTag(tag, userName);
                    tagFound = true;
                }
            }
            if (!tagFound)
                createTag(tagDesc, reason, tagID);
        } catch (Exception e1) {
            CTILogger.error(e1);

        }
        return true;
    }

    private void createTag(String tagDesc, String reason, Integer tagID)
            throws Exception {

        String refString = getReferenceString(tagDesc);
        tagManager.createTag(point.getPointID(),
                             tagID,
                             userName,
                             reason,
                             refString,
                             tagName);

    }

    private String getReferenceString(String tagDesc) {
        Integer objectID = pao.getPAObjectID();
        String type = pao.getPAOType();
        String refString = type + ":" + objectID + ":" + tagDesc;
        return refString;
    }

    private Integer getTagID() {
        LiteTag lt = DaoFactory.getTagDao().getLiteTag(tagName);
        return new Integer(lt.getLiteID());
    }

    public static String getReason(String tagName, Integer paoID) {

        LitePoint tagPoint = null;
        String reason = "";
        tagPoint = CBCPointFactory.getTagPoint(paoID);

        if (tagPoint != null) {
            List<TAGLog> tagLog = TagManager.getInstance()
                                            .getTagLog(tagPoint.getPointID());

            Date latestTime = null;
            TAGLog latestLog = null;

            for (TAGLog log : tagLog) {
                if (log.getForStr().equalsIgnoreCase(tagName)) {
                    if (latestTime == null) {
                        latestTime = log.getTagTime();
                        latestLog = log;
                    } else {
                        if (latestTime.before(log.getTagTime())) {
                            latestTime = log.getTagTime();
                            latestLog = log;
                        }
                    }
                }
            }

            if (latestLog != null) {

                reason = latestLog.getDescription();
            }

        }
        return reason;

    }

    public YukonPAObject getPao() {
        return pao;
    }

    public void setPao(YukonPAObject pao) {
        this.pao = pao;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }

    public List<LitePoint> getPoints() {
        return points;
    }

    public void setPoints(List<LitePoint> points) {
        this.points = points;
    }
}
