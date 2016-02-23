package com.cannontech.database.data.capcontrol;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cannontech.capcontrol.service.ZoneService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.db.capcontrol.CCFeederBankList;
import com.cannontech.database.db.capcontrol.CCFeederSubAssignment;
import com.cannontech.spring.YukonSpringHook;

/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class CapControlFeeder extends CapControlYukonPAOBase implements com.cannontech.common.editor.EditorPanel {
    public static final String ENABLE_OPSTATE = "feederEnabled";
    public static final String DISABLE_OPSTATE = "feederDisabled";
    public static final String ENABLE_OVUVSTATE = "feederOVUVEnabled";
    public static final String DISABLE_OVUVSTATE = "feederOVUVDisabled";
    private com.cannontech.database.db.capcontrol.CapControlFeeder capControlFeeder =
        new com.cannontech.database.db.capcontrol.CapControlFeeder();
    private List<CCFeederBankList> ccBankListVector;
    private boolean updateBankList = true;

    public Integer getId() {
        return capControlFeeder.getFeederID();
    }

    public void setId(Integer id) {
        setPAObjectID(id);
        capControlFeeder.setFeederID(id);
    }

    public void setName(String name) {
        setPAOName(name);
    }

    public String getName() {
        return getPAOName();
    }

    public CapControlFeeder() {
        super(PaoType.CAP_CONTROL_FEEDER);
    }

    public CapControlFeeder(Integer feedID) {
        this();
        setCapControlPAOID( feedID );
    }
    
    @Override
    public void add() throws SQLException {
        if (getPAObjectID() == null) {
            PaoDao paoDao = YukonSpringHook.getBean(PaoDao.class);   
            setCapControlPAOID(paoDao.getNextPaoId());   
        }

        super.add();

        getCapControlFeeder().add();

        for (int i = 0; i < getChildList().size(); i++) {
            getChildList().get(i).add();
        }
    }

    @Override
    public void delete() throws SQLException {
        ZoneService zoneService = YukonSpringHook.getBean("zoneService", ZoneService.class);
        //Must happen before CCFeederBankList.deleteCapBanksFromFeederList call
        zoneService.unassignBanksByFeeder(getCapControlPAOID());

        //remove all the associations of CapBanks to this Feeder
        CCFeederBankList.deleteCapBanksFromFeederList(getCapControlPAOID(), null, getDbConnection());
        
        //remove all the associations of this Feeder to any SubBus
        CCFeederSubAssignment.deleteCCFeedersFromSubList(null, getCapControlPAOID(), getDbConnection());

        //Delete from all dynamic Feeder cap control tables here
        delete("DynamicCCFeeder", "FeederID", getCapControlPAOID() );
        delete("CapControlComment", "paoID", getCapControlPAOID());
        deleteAllPoints();

        getCapControlFeeder().delete();

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

    public com.cannontech.database.db.capcontrol.CapControlFeeder getCapControlFeeder() {
        if (capControlFeeder == null)
            capControlFeeder = new com.cannontech.database.db.capcontrol.CapControlFeeder();

        return capControlFeeder;
    }

    @Override
    public List<CCFeederBankList> getChildList() {
        if( ccBankListVector == null )
            ccBankListVector = new ArrayList<CCFeederBankList>(16);

        return ccBankListVector;
    }

    @Override
    public void retrieve() throws SQLException {
        super.retrieve();

        getCapControlFeeder().retrieve();

        ccBankListVector = CCFeederBankList.getCapBanksOnFeederList(getCapControlPAOID(), getDbConnection() );
    }

    public void setCapControlFeeder(com.cannontech.database.db.capcontrol.CapControlFeeder newCapControlFeeder) {
        capControlFeeder = newCapControlFeeder;
    }

    @Override
    public void setCapControlPAOID(Integer feedID) {
        super.setPAObjectID( feedID );
        getCapControlFeeder().setFeederID( feedID );

        for (int i = 0; i < getChildList().size(); i++)
            getChildList().get(i).setFeederID( feedID );
    }
    
    public void setCcBankList(List<CCFeederBankList> newCcBankListVector) {
        ccBankListVector = newCcBankListVector;
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection( conn );
        getCapControlFeeder().setDbConnection(conn);

        for (int i = 0; i < getChildList().size(); i++)
            getChildList().get(i).setDbConnection(conn);
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();

        getCapControlFeeder().update();
        
        if(isUpdateBankList()){
            CCFeederBankList.deleteCapBanksFromFeederList(getCapControlPAOID(), null, getDbConnection());
    
            for( int i = 0; i < getChildList().size(); i++ ) {
                getChildList().get(i).add();
            }
        }
        
        ZoneService zoneService = YukonSpringHook.getBean("zoneService", ZoneService.class);
        zoneService.handleFeederUpdate(getCapControlPAOID());
    }

    public boolean isUpdateBankList() {
        return updateBankList;
    }

    public void setUpdateBankList(boolean updateBankList) {
        this.updateBankList = updateBankList;
    }

}
