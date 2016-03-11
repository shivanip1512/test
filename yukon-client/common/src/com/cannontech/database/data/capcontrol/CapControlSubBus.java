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
        int altSubId = completeBus.getAltSubId();
        innerBus.setAltSubPAOId(altSubId != 0 ? altSubId : null);
        innerBus.setControlFlagBoolean(completeBus.getControlFlag());

        int varPoint = completeBus.getCurrentVarLoadPointId();
        innerBus.setCurrentVarLoadPointID(varPoint != 0 ? varPoint : null);
        int voltPoint = completeBus.getCurrentVoltLoadPointId();
        innerBus.setCurrentVoltLoadPointID(voltPoint != 0 ? voltPoint : null);
        int wattPoint = completeBus.getCurrentWattLoadPointId();
        innerBus.setCurrentWattLoadPointID(wattPoint != 0 ? wattPoint : null);
        int phaseB = completeBus.getPhaseB();
        innerBus.setPhaseB(phaseB != 0 ? phaseB : null);
        int phaseC = completeBus.getPhaseC();
        innerBus.setPhaseC(phaseC != 0 ? phaseC : null);

        int disableBus = completeBus.getDisableBusPointId();
        innerBus.setDisableBusPointId(disableBus != 0 ? disableBus : null);
        int voltReduction = completeBus.getVoltReductionPointId();
        innerBus.setVoltReductionPointId(voltReduction != 0 ? voltReduction : null);
        int switchPoint = completeBus.getSwitchPointId();
        innerBus.setSwitchPointID(switchPoint != 0 ? switchPoint : null);

        innerBus.setDualBusEnabledBoolean(completeBus.getDualBusEnabled());
        innerBus.setMapLocationID(completeBus.getMapLocationId());
        innerBus.setMultiMonitorControlBoolean(completeBus.getMultiMonitorControl());
        innerBus.setSubstationBusID(completeBus.getPaObjectId());
        innerBus.setUsePhaseDataBoolean(completeBus.getUsePhaseData());

        bus.setCapControlSubstationBus(innerBus);

        return bus;
    }

    public CompleteCapControlSubstationBus asCompletePao() {
        CompleteCapControlSubstationBus completeBus = new CompleteCapControlSubstationBus();

        CapControlSubstationBus innerBus = getCapControlSubstationBus();

        Integer altSubId = innerBus.getAltSubPAOId();
        completeBus.setAltSubId(altSubId != null ? altSubId : 0);
        completeBus.setControlFlag(innerBus.getControlFlagBoolean());

        Integer varPoint = innerBus.getCurrentVarLoadPointID();
        completeBus.setCurrentVarLoadPointId(varPoint != null ? varPoint : 0);
        Integer voltPoint = innerBus.getCurrentVoltLoadPointID();
        completeBus.setCurrentVoltLoadPointId(voltPoint != null ? voltPoint : 0);
        Integer wattPoint = innerBus.getCurrentWattLoadPointID();
        completeBus.setCurrentWattLoadPointId(wattPoint != null ? wattPoint : 0);
        Integer phaseB = innerBus.getPhaseB();
        completeBus.setPhaseB(phaseB != null ? phaseB : 0);
        Integer phaseC = innerBus.getPhaseC();
        completeBus.setPhaseC(phaseC != null ? phaseC : 0);

        Integer disableBus = innerBus.getDisableBusPointId();
        completeBus.setDisableBusPointId(disableBus != null ? disableBus : 0);
        Integer voltReduction = innerBus.getVoltReductionPointId();
        completeBus.setVoltReductionPointId(voltReduction != null ? voltReduction : 0);
        Integer switchPoint = innerBus.getSwitchPointID();
        completeBus.setSwitchPointId(switchPoint != null ? switchPoint : 0);

        completeBus.setDescription(getGeoAreaName());
        completeBus.setDisabled(isDisabled());
        completeBus.setDualBusEnabled(innerBus.isDualBusEnabledBoolean());
        completeBus.setMapLocationId(innerBus.getMapLocationID());
        completeBus.setMultiMonitorControl(innerBus.isMultiMonitorControlBoolean());
        completeBus.setPaoName(getName());
        completeBus.setStatistics(getPAOStatistics());
        completeBus.setUsePhaseData(innerBus.getUsePhaseDataBoolean());

        if (getPAObjectID() != null) {
            PaoIdentifier paoIdentifier = PaoIdentifier.of(getId(), getPaoType());
            completeBus.setPaoIdentifier(paoIdentifier);
        }

        return completeBus;
    }

}
