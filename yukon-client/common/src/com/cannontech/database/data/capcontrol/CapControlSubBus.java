package com.cannontech.database.data.capcontrol;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cannontech.capcontrol.service.ZoneService;
import com.cannontech.common.editor.EditorPanel;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.db.capcontrol.CCFeederSubAssignment;
import com.cannontech.database.db.capcontrol.CapControlSubstationBus;
import com.cannontech.spring.YukonSpringHook;

public class CapControlSubBus extends CapControlYukonPAOBase implements EditorPanel {

    public static final String ENABLE_OPSTATE = "subEnabled";
    public static final String DISABLE_OPSTATE = "subDisabled";
    public static final String ENABLE_OVUVSTATE = "subOVUVEnabled";
    public static final String DISABLE_OVUVSTATE = "subOVUVDisabled";
    private CapControlSubstationBus capControlSubstationBus = null;
    private List<CCFeederSubAssignment> ccFeederList = null;
    
    public CapControlSubBus() {
        super(PaoType.CAP_CONTROL_SUBBUS);
    }

    public CapControlSubBus(Integer subBusID) {
        this();
        setCapControlPAOID( subBusID );
    }

    @Override
    public void add() throws SQLException  {
        if( getCapControlPAOID() == null ) {
            PaoDao paoDao = YukonSpringHook.getBean(PaoDao.class);
            setCapControlPAOID(paoDao.getNextPaoId());
        }

        super.add();
        getCapControlSubstationBus().add();

        for (int i = 0; i < getChildList().size(); i++) {
            getChildList().get(i).add();
        }
    }

    @Override
    public void delete() throws SQLException {
        //remove all the associations of feeders to this SubstationBus
        CCFeederSubAssignment.deleteCCFeedersFromSubList(getCapControlPAOID(), null, getDbConnection());

        //Delete from all dynamic SubBus cap control tables here
        delete("DynamicCCSubstationBus", "SubstationBusID", getCapControlPAOID() );
        delete("ccsubstationsubbuslist", "SubstationBusID", getCapControlPAOID() );
        delete("CapControlComment", "paoID", getCapControlPAOID());
        //delete all the points that belog to this sub
        //there should be a constraint on pointid in point table
        deleteAllPoints();

        getCapControlSubstationBus().delete();

        super.delete();
    }
    
    private void deleteAllPoints() throws SQLException {
        List<LitePoint> litePointsByPaObjectId = YukonSpringHook.getBean(PointDao.class).getLitePointsByPaObjectId(getCapControlPAOID());
        for (LitePoint point : litePointsByPaObjectId) {
            PointBase pointPers = (PointBase) LiteFactory.convertLiteToDBPers(point);
            pointPers.setDbConnection(getDbConnection());
            pointPers.delete();
        }
    }

    public CapControlSubstationBus getCapControlSubstationBus() {
        if( capControlSubstationBus == null )
            capControlSubstationBus = new CapControlSubstationBus();

        return capControlSubstationBus;
    }
    
    @Override
    public List<CCFeederSubAssignment> getChildList() {
        if (ccFeederList == null)
            ccFeederList = new ArrayList<CCFeederSubAssignment>(16);

        return ccFeederList;
    }

    @Override
    public void retrieve() throws SQLException {
        super.retrieve();

        getCapControlSubstationBus().retrieve();

        ccFeederList = CCFeederSubAssignment.getCCFeedersOnSub(getPAObjectID(), getDbConnection() );
    }

    @Override
    public void setCapControlPAOID(Integer subBusID) {
        super.setPAObjectID( subBusID );
        getCapControlSubstationBus().setSubstationBusID( subBusID );

        for (int i = 0; i < getChildList().size(); i++)
            getChildList().get(i).setSubstationBusID( subBusID );
    }

    public void setCapControlSubstationBus(CapControlSubstationBus newCapControlSubstationBus) {
        capControlSubstationBus = newCapControlSubstationBus;
    }

    public void setCcFeederList(List<CCFeederSubAssignment> newCcFeederListVector) {
        ccFeederList = newCcFeederListVector;
    }

    @Override
    public void setDbConnection(Connection conn) {
        super.setDbConnection( conn );
        getCapControlSubstationBus().setDbConnection(conn);

        for (int i = 0; i < getChildList().size(); i++)
            getChildList().get(i).setDbConnection(conn);
    }

    @Override
    public void update() throws SQLException {
        super.update();

        getCapControlSubstationBus().update();

        CCFeederSubAssignment.deleteCCFeedersFromSubList(getCapControlPAOID(), null, getDbConnection());

        for( int i = 0; i < getChildList().size(); i++ ) {
            getChildList().get(i).add();
        }
        
        ZoneService zoneService = YukonSpringHook.getBean("zoneService",ZoneService.class);
        zoneService.handleSubstationBusUpdate(getCapControlPAOID());
    }

}
