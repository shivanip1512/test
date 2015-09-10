package com.cannontech.database.data.capcontrol;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cannontech.capcontrol.service.ZoneService;
import com.cannontech.common.editor.EditorPanel;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.model.CompleteCapControlSubstationBus;
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

    public Integer getId() {
        return getCapControlPAOID();
    }

    public void setId(Integer id) {
        setCapControlPAOID(id);
    }

    public String getName() {
        return getPAOName();
    }

    public void setName(String name) {
        setPAOName(name);
    }

    /**
     * Note: Does not set up the {@link #setCcFeederList(List)} or {@link #setSchedules(List)}
     * which are not part of the {@link CompleteCapControlSubstationBus}
     *
     */
    public static CapControlSubBus of(CompleteCapControlSubstationBus completeBus) {

        CapControlSubBus bus = new CapControlSubBus();

        bus.setDisabled(completeBus.isDisabled());
        bus.setId(completeBus.getPaObjectId());
        bus.setName(completeBus.getPaoName());
        bus.setGeoAreaName(completeBus.getDescription());
        bus.setPAOStatistics(completeBus.getStatistics());

        CapControlSubstationBus innerBus = new CapControlSubstationBus();
        innerBus.setAltSubPAOId(completeBus.getAltSubId());
        innerBus.setControlFlagBoolean(completeBus.getControlFlag());
        innerBus.setCurrentVarLoadPointID(completeBus.getCurrentVarLoadPointId());
        innerBus.setCurrentVoltLoadPointID(completeBus.getCurrentVoltLoadPointId());
        innerBus.setCurrentWattLoadPointID(completeBus.getCurrentWattLoadPointId());
        innerBus.setDisableBusPointId(completeBus.getDisableBusPointId());
        innerBus.setDualBusEnabledBoolean(completeBus.getDualBusEnabled());
        innerBus.setMapLocationID(completeBus.getMapLocationId());
        innerBus.setMultiMonitorControlBoolean(completeBus.getMultiMonitorControl());
        innerBus.setPhaseB(completeBus.getPhaseB());
        innerBus.setPhaseC(completeBus.getPhaseC());
        innerBus.setSubstationBusID(completeBus.getPaObjectId());
        innerBus.setSwitchPointID(completeBus.getSwitchPointId());
        innerBus.setUsePhaseDataBoolean(completeBus.getUsePhaseData());
        innerBus.setVoltReductionPointId(completeBus.getVoltReductionPointId());

        bus.setCapControlSubstationBus(innerBus);

        return bus;
    }

    public CompleteCapControlSubstationBus asCompletePao() {
        CompleteCapControlSubstationBus completeBus = new CompleteCapControlSubstationBus();

        CapControlSubstationBus innerBus = getCapControlSubstationBus();

        completeBus.setAltSubId(innerBus.getAltSubPAOId());
        completeBus.setControlFlag(innerBus.getControlFlagBoolean());
        completeBus.setCurrentVarLoadPointId(innerBus.getCurrentVarLoadPointID());
        completeBus.setCurrentVoltLoadPointId(innerBus.getVoltReductionPointId());
        completeBus.setCurrentWattLoadPointId(innerBus.getCurrentWattLoadPointID());
        completeBus.setDescription(getGeoAreaName());
        completeBus.setDisableBusPointId(innerBus.getDisableBusPointId());
        completeBus.setDisabled(isDisabled());
        completeBus.setDualBusEnabled(innerBus.isDualBusEnabledBoolean());
        completeBus.setMapLocationId(innerBus.getMapLocationID());
        completeBus.setMultiMonitorControl(innerBus.isMultiMonitorControlBoolean());
        completeBus.setPaoName(getName());
        completeBus.setPhaseB(innerBus.getPhaseB());
        completeBus.setPhaseC(innerBus.getPhaseC());
        completeBus.setStatistics(getPAOStatistics());
        completeBus.setSwitchPointId(innerBus.getSwitchPointID());
        completeBus.setUsePhaseData(innerBus.getUsePhaseDataBoolean());
        completeBus.setVoltReductionPointId(innerBus.getVoltReductionPointId());

        if (getPAObjectID() != null) {
            PaoIdentifier paoIdentifier = PaoIdentifier.of(getId(), getPaoType());
            completeBus.setPaoIdentifier(paoIdentifier);
        }

        return completeBus;
    }

}
