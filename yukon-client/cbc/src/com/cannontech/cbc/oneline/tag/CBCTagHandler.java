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
import com.cannontech.database.data.lite.*;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.point.TAGLog;
import com.cannontech.message.dispatch.ClientConnection;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.tags.Tag;
import com.cannontech.tags.TagManager;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.cbc.web.CapControlCache;

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
            boolean tagDisable = false;
            if( tagDesc.lastIndexOf("Disable") != -1 )
                tagDisable = true;
            
            for (Tag tag : tags) {
                if (tagName.equalsIgnoreCase(tag.getTaggedForStr()))
                {
                    removeTag(tag);
                }                   
            }
            if( tagDisable )
            {
                createTag(tagDesc, reason, tagID);
            }
        } catch (Exception e1) {
            CTILogger.error(e1);
        }
        return true;
    }
    
    private void removeTag( Tag tag ) throws Exception
    {
        CapControlCache cc = (CapControlCache)YukonSpringHook.getBean("cbcCache");
        Feeder[] fList = null;
        CapBankDevice[] cList = null;
        
        if( tag == null)
            throw new Exception("Null Pointer, Tag was not removed from dynamictags.");
        
        if( tag.getTaggedForStr().lastIndexOf("OVUV") == -1)
        {
            tagManager.removeTag(tag, userName);
        }else
        {
            //if this is a OVUV, need to go down the tree removing tags for objects below it.
            int id = pao.getPAObjectID();

            if(pao instanceof CapControlSubBus )
            {
                fList = cc.getFeedersBySub(id);
               //if substation, remove it and feeders, and capbanks.
            }else if( pao instanceof CapControlFeeder)
            {
                //if feeder, remove it and find all capbanks
                cList = cc.getCapBanksByFeeder(id);
            }else if( pao instanceof CapBank)
            {
                tagManager.removeTag(tag, userName);
            }
        }
        if( cList != null )
        {
            tagManager.removeTag(tag, userName);
            for( CapBankDevice cd : cList )
            {
                HashSet<Tag> temp = (HashSet<Tag>)tagManager.getTags( CBCPointFactory.getTagPoint(cd.getCcId()).getPointID() );
                for( Tag t : temp )
                    tagManager.removeTag(t, userName);
            }
        }
        if( fList != null ){
            tagManager.removeTag(tag, userName);
            for( Feeder f : fList )
            {
                HashSet<Tag> tempC = (HashSet<Tag>)tagManager.getTags( CBCPointFactory.getTagPoint(f.getCcId()).getPointID() );
                //have a list of feeder tags
                if( tempC.size() > 0 )
                {
                    int id = f.getCcId();
                    CapBankDevice[] bankList = cc.getCapBanksByFeeder(id);
                    for( CapBankDevice cd : bankList )
                    {
                        HashSet<Tag> temp = (HashSet<Tag>)tagManager.getTags( CBCPointFactory.getTagPoint(cd.getCcId()).getPointID() );
                        for( Tag t2 : temp )
                            tagManager.removeTag(t2, userName);
                    }
                }
                for( Tag t : tempC ){
                    tagManager.removeTag(t, userName);
                }

                //else No capbanks to search for.
            }
        }        
    }
    
    private void createTag(String tagDesc, String reason, Integer tagID) throws Exception 
    {
        CapControlCache cc = (CapControlCache)YukonSpringHook.getBean("cbcCache");
        Feeder[] fList = null;
        CapBankDevice[] cList = null;
        
        String refString = getReferenceString(tagDesc);
        //if this is just a disable/enable  just add it.
        if( tagDesc.lastIndexOf("OVUV") == -1)
        {
            tagManager.createTag(point.getPointID(),tagID,userName,reason,refString,tagName);
        }else
        {
            //if this is a OVUV, need to go down the tree adding tags for objects below it.  
            //if this is a OVUV, need to go down the tree removing tags for objects below it.

            int id = pao.getPAObjectID();

            if(pao instanceof CapControlSubBus )
            {
                fList = cc.getFeedersBySub(id);
               //if substation, remove it and feeders, and capbanks.
            }else if( pao instanceof CapControlFeeder)
            {
                //if feeder, remove it and find all capbanks
                cList = cc.getCapBanksByFeeder(id);
            }else if( pao instanceof CapBank)
            {
                tagManager.createTag(point.getPointID(),tagID,userName,reason,refString,tagName);  
            }
            if( cList != null )
            {
                tagManager.createTag(point.getPointID(),tagID,userName,reason,refString,tagName);                
                for( CapBankDevice cd : cList )
                {
                    LitePoint lp = CBCPointFactory.getTagPoint(cd.getCcId());
                    tagManager.createTag(lp.getPointID(),tagID,userName,reason,getReferenceString(cd.getCcId(), tagDesc),tagName);
                }
            }
            if( fList != null )
            {
                tagManager.createTag(point.getPointID(),tagID,userName,reason,refString,tagName);
                for( Feeder f : fList )
                {
                    LitePoint lp = CBCPointFactory.getTagPoint(f.getCcId());
                    tagManager.createTag(lp.getPointID(),tagID,userName,reason,getReferenceString(f.getCcId(), tagDesc),tagName);
                    cList = cc.getCapBanksByFeeder(f.getCcId());
                    for( CapBankDevice cd : cList )
                    {
                        LitePoint lp2 = CBCPointFactory.getTagPoint(cd.getCcId());
                        tagManager.createTag(lp2.getPointID(),tagID,userName,reason,getReferenceString(cd.getCcId(), tagDesc),tagName);
                    }
                }
                //else No capbanks to search for.
            }    
        }
    }

    private String getReferenceString(String tagDesc) {
        Integer objectID = pao.getPAObjectID();
        String type = pao.getPAOType();
        String refString = type + ":" + objectID + ":" + tagDesc;
        return refString;
    }
    
    private String getReferenceString( int paoID, String tagDesc )
    {
        LiteYukonPAObject liteYukonPAO = DaoFactory.getPaoDao().getLiteYukonPAO(paoID);
        DBPersistent dbPers = LiteFactory.convertLiteToDBPers(liteYukonPAO);
        Connection connection = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
        dbPers.setDbConnection(connection);
        try {
            dbPers.retrieve();
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
        YukonPAObject obj = (YukonPAObject) dbPers;
        String refString = obj.getPAOType() + ":" + obj.getPAObjectID() + ":" + tagDesc;
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
