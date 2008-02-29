package com.cannontech.web.editor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;

import com.cannontech.cbc.dao.CapbankDao;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.*;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.PoolManager;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.capcontrol.*;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.TwoWayDevice;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.point.CapBankMonitorPointParams;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.capcontrol.CCMonitorBankList;
import com.cannontech.database.db.capcontrol.CapBankAdditional;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.database.db.device.DeviceScanRate;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.util.CBCDBUtil;
import com.cannontech.web.util.CBCSelectionLists;
import com.cannontech.web.util.JSFComparators;

public class CapBankEditorForm extends DBEditorForm {

    // private static final long serialVersionUID = 5491225604682004562L;
    private List<CapBankMonitorPointParams> unassignedPoints = null;
    private List<CapBankMonitorPointParams> assignedPoints = null;
    private MultiDBPersistent monitorPointsVector = null;
    // this variable exists only to display relevant information about a cbc
    // in the cap bank editor. Right now it is not possible to
    // edit cbc from cap bank editor (which is how it should be since there is a
    // separate editor for CBC).
    // This object should be decoupled from CBController class which is where
    // editing occurs.
    // TODO move the setting of the control device to this menu as well
    private DeviceBase controller = null;
    // this cap bank that we are editing
    private CapBank capBank = null;
    private String[] DYNAMIC_TABLE_NAMES = { "DynamicCCMonitorBankHistory",
            "DynamicCCMonitorPointResponse" };
    private CapBankAdditional additionalInfo;
    
    private static PointDao pointDao = YukonSpringHook.getBean("pointDao",PointDao.class);
    private static PaoDao paoDao = YukonSpringHook.getBean("paoDao",PaoDao.class);
    
   //over-rides the drop-down values
    private boolean customSize = Boolean.FALSE;

    public CapBankEditorForm() {
        super();
    }

    protected void checkForErrors() throws IllegalArgumentException {
        List<CCMonitorBankList> monitorPointList = ((CapBank) getDbPersistent()).getCcMonitorBankList();
        for (Iterator<CCMonitorBankList> iter = monitorPointList.iterator(); iter.hasNext();) {
            CCMonitorBankList monitorPoint = iter.next();
            if (monitorPoint.getNINAvg().longValue() <= 0)
                throw new IllegalArgumentException("Adaptive Count value should be greater then 0.");
        }
        
    }

    public void update() {

        FacesMessage facesMessage = new FacesMessage();
        facesMessage.setDetail("Database UPDATE successful");
        List<CCMonitorBankList> points = new ArrayList<CCMonitorBankList>();
        for (Iterator<CapBankMonitorPointParams> iter = assignedPoints.iterator(); iter.hasNext();) {
            CapBankMonitorPointParams point = iter.next();
            CCMonitorBankList monitorPoint = new CCMonitorBankList(point);
            points.add(monitorPoint);
        }
        ((CapBank) getDbPersistent()).setCcMonitorBankList(points);

        try {
            checkForErrors();
            updateAddInfo();
            updateDBObject(getDbPersistent(), facesMessage);
            capBank = (CapBank) getDbPersistent();
            handleMonitorPointsForController(capBank.getCapBank()
                                                    .getControlDeviceID()
                                                    .intValue());
        } catch (IllegalArgumentException eae) {
            String errorString = eae.getMessage();
            facesMessage.setDetail(errorString);
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
        } catch (TransactionException e) {
            String errorString = e.getMessage();
            facesMessage.setDetail(errorString);
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
        } finally {
            FacesContext.getCurrentInstance().addMessage("cti_db_update",
                                                         facesMessage);
        }

    }

    private void updateAddInfo(){
        Connection connection = CBCDBUtil.getConnection();
        getAdditionalInfo().setDbConnection(connection);
        try {
            getAdditionalInfo().update();
        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        }
        CBCDBUtil.closeConnection(connection);
    }

    /**
     * 
     */
    private void resetAdvancedTab() {
        handleMonitorPointsForController(capBank.getCapBank()
                                                .getControlDeviceID()
                                                .intValue());
        assignedPoints = pointDao.getCapBankMonitorPoints(capBank);
        unassignedPoints = null;
        getUnassignedPoints();
        Collections.sort(assignedPoints, JSFComparators.monitorPointDisplayOrderComparator);
    }

    public List<CapBankMonitorPointParams> getAssignedPoints() {
        if (assignedPoints == null)
            assignedPoints = new ArrayList<CapBankMonitorPointParams>();
        return assignedPoints;
    }

    public List<CapBankMonitorPointParams> getUnassignedPoints() {
        if (unassignedPoints == null) {
            unassignedPoints = new ArrayList<CapBankMonitorPointParams>();
            CapBank capBank = ((CapBank) getDbPersistent());
            int controlDeviceId = capBank.getCapBank()
                                         .getControlDeviceID()
                                         .intValue();
            if (controlDeviceId > 0) {
                List<LitePoint> allPoints = pointDao.getLitePointsByPaObjectId(controlDeviceId);
                for (int i = 0; i < allPoints.size(); i++) {
                    LitePoint point = allPoints.get(i);
                    if (point.getUofmID() == PointUnits.UOMID_VOLTS) {
                        CapBankMonitorPointParams monitorPoint = new CapBankMonitorPointParams(point);
                        // set the cap bank id to replace the CBC if with Cap
                        // Bank Id
                        monitorPoint.setCapBankId(capBank.getCapBank()
                                                         .getDeviceID()
                                                         .intValue());
                        // set the feeder limits by default
                        setDefaultFeederLimits(capBank, monitorPoint);

                        if (!isPointAssigned(monitorPoint)) {
                            unassignedPoints.add(monitorPoint);
                        }
                    }
                }
            }
            Collections.sort(unassignedPoints, JSFComparators.monitorPointComparator);
        }
        return unassignedPoints;
    }

    private void setDefaultFeederLimits(CapBank capBank,
            CapBankMonitorPointParams monitorPoint) {
        
        CapbankDao dao = YukonSpringHook.getBean("capbankDao", CapbankDao.class);
        int fdrId = 0;
        try{
            fdrId = dao.getParentFeederId(capBank.getPAObjectID().intValue());
        }
        catch( EmptyResultDataAccessException e)
        {
            CTILogger.warn("Feeder " + capBank.getPAObjectID().intValue() + " not found.");
        }
        
        if (fdrId != 0) {
            LiteYukonPAObject liteFeeder = paoDao.getLiteYukonPAO(fdrId);
            CapControlFeeder feeder = (CapControlFeeder) LiteFactory.convertLiteToDBPers(liteFeeder);
            Connection conn = PoolManager.getInstance()
                                         .getConnection(CtiUtilities.getDatabaseAlias());
            try {

                feeder.setDbConnection(conn);
                feeder.retrieve();
            } catch (SQLException e) {

            }
            int stratId = 0;
//            int stratId = feeder.getCapControlFeeder()
//                                .getStrategyID()
//                                .intValue();

            CapControlStrategy strategy = new CapControlStrategy();
            strategy.setStrategyID(new Integer(stratId));

            try {
                strategy.setDbConnection(conn);
                strategy.retrieve();

                monitorPoint.setLowerBandwidth(strategy.getPeakLag()
                                                       .floatValue());
                monitorPoint.setUpperBandwidth(strategy.getPeakLead()
                                                       .floatValue());

            } catch (SQLException e) {
                CTILogger.info("CapBankEditorForm -- setDefaultFeederLimits" + e.getMessage());
            } finally {
                feeder.setDbConnection(null);
                strategy.setDbConnection(null);

                try {
                    if (conn != null)
                        conn.close();
                } catch (java.sql.SQLException e2) {
                    CTILogger.info("CapBankEditorForm -- setDefaultFeederLimits" + e2.getMessage());

                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void treeSwapRemoveAction() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map paramMap = context.getExternalContext().getRequestParameterMap();

        String swapType = (String) paramMap.get("swapType");
        int elemId = new Integer((String) paramMap.get("id")).intValue();

        if ("CapBankPoint".equalsIgnoreCase(swapType)) {
            if (unassignedPoints != null) {
                for (Iterator<CapBankMonitorPointParams> iter = getAssignedPoints().iterator(); iter.hasNext();) {
                    CapBankMonitorPointParams monitorPoint = iter.next();
                    if (monitorPoint.getPointId() == elemId) {
                        getAssignedPoints().remove(monitorPoint);
                        getUnassignedPoints().add(monitorPoint);
                        break;
                    }
                }
                Collections.sort(unassignedPoints, JSFComparators.monitorPointComparator);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void treeSwapAddAction() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map paramMap = context.getExternalContext().getRequestParameterMap();

        String swapType = (String) paramMap.get("swapType");
        int elemId = new Integer((String) paramMap.get("id")).intValue();
        if (unassignedPoints != null) {
            if ("CapBankPoint".equalsIgnoreCase(swapType)) {
                for (Iterator<CapBankMonitorPointParams> iter = getUnassignedPoints().iterator(); iter.hasNext();) {
                    CapBankMonitorPointParams monitorPoint = iter.next();
                    monitorPoint.setDisplayOrder(getAssignedPoints().size() + 1);
                    if (monitorPoint.getPointId() == elemId) {
                        getUnassignedPoints().remove(monitorPoint);
                        getAssignedPoints().add(monitorPoint);
                        break;
                    }
                }
                Collections.sort(unassignedPoints, JSFComparators.monitorPointComparator);
                Collections.sort(assignedPoints, JSFComparators.monitorPointDisplayOrderComparator);
            }
        }
    }

    // hook into capcontrol form
    public void init(DBPersistent dbPersistent) {
        if (getDbPersistent() == null) {
            setDbPersistent(dbPersistent);
            if (dbPersistent instanceof CapBank) {
                capBank = (CapBank) getDbPersistent();
                initController(capBank);
                initAdditionalInfo();
            }
        }
    }

    private void initAdditionalInfo() {
        additionalInfo = new CapBankAdditional();
        Connection connection = PoolManager.getInstance()
                                           .getConnection(CtiUtilities.getDatabaseAlias());
        additionalInfo.setDbConnection(connection);
        additionalInfo.setDeviceID(capBank.getPAObjectID());
        try {
            additionalInfo.retrieve();
        } catch (SQLException e) {
            CTILogger.error(e);
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                CTILogger.error(e);
            }
        }
        initCustomSize();
    }

    private void initCustomSize() {
        customSize = true;
        Integer bankSize = capBank.getCapBank().getBankSize();
        CBCSelectionLists lists = new CBCSelectionLists();
        SelectItem[] addCapBankSizes = lists.getAddCapBankSizes();
        for (int i = 0; i < addCapBankSizes.length; i++) {
            SelectItem item = addCapBankSizes[i];
            if (item.getValue().equals(bankSize))
                customSize = false;
            
        }
    }

    /**
     * @param capBank
     */
    private void initController(CapBank capBank) {
        int controllerID = resetController(capBank);
        // need to make sure that controller is synched with assigned points
        // make sure that all the points that are defined in assigned and
        // unassigned lists
        // are the points available for the given controller
        if (controller instanceof CapBankController) {
            handleMonitorPointsForController(controllerID);
        } else if (controller instanceof CapBankController702x) {
            assignedPoints = pointDao.getCapBankMonitorPoints(capBank);
            unassignedPoints = null;
            getUnassignedPoints();
            Collections.sort(assignedPoints, JSFComparators.monitorPointDisplayOrderComparator);
        } else if (controller instanceof CapBankControllerDNP) {
            assignedPoints = pointDao.getCapBankMonitorPoints(capBank);
            unassignedPoints = null;
            getUnassignedPoints();
            Collections.sort(assignedPoints, JSFComparators.monitorPointDisplayOrderComparator);
        } 
    }

    /**
     * @param capBank
     * @return
     */
    private int resetController(CapBank capBank) {
        int controllerID = capBank.getCapBank().getControlDeviceID().intValue();
        LiteYukonPAObject liteController = paoDao.getLiteYukonPAO(controllerID);
        if (liteController == null)
            liteController = paoDao.getLiteYukonPAO(0);
        DBPersistent temp = LiteFactory.convertLiteToDBPersAndRetrieve(liteController);
        if (temp instanceof DeviceBase) {
            controller = (DeviceBase) temp;
        }
        return controllerID;
    }

    /**
     * @param controllerID
     */
    private void handleMonitorPointsForController(int controllerID) {
        handleAllPointsOnList(getAssignedPoints(), controllerID, true);
        if ((getAssignedPoints() == null) || (getAssignedPoints().size() == 0))
            deleteAllDynamic(getUnassignedPoints());
        handleAllPointsOnList(getUnassignedPoints(), controllerID, false);
    }

    private void deleteAllDynamic(List<CapBankMonitorPointParams> points) {

        for (int i = 0; i < DYNAMIC_TABLE_NAMES.length; i++) {
            String table = DYNAMIC_TABLE_NAMES[i];
            for (Iterator<CapBankMonitorPointParams> iter = points.iterator(); iter.hasNext();) {
                CapBankMonitorPointParams point = iter.next();
                deletePointFromTable(table, point.getPointId());
            }
        }
    }

    private void deletePointFromTable(String table, int pointId) {
        String sqlStmt = "DELETE FROM " + table + " WHERE pointId = ?";
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        yukonTemplate.update(sqlStmt, new Integer[] { new Integer(pointId) });
    }

    private void handleAllPointsOnList(List<CapBankMonitorPointParams> points, int controllerID,
            boolean delDynamic) {
        List<LitePoint> controllerPoints = pointDao.getLitePointsByPaObjectId(controllerID);
        List<CapBankMonitorPointParams> pointsToRemove = new ArrayList<CapBankMonitorPointParams>(10);
        if (controllerPoints != null && points != null) {
            for (Iterator<CapBankMonitorPointParams> iter = points.iterator(); iter.hasNext();) {
                CapBankMonitorPointParams point = iter.next();
                if (!pointBelongsToController(controllerPoints,
                                              point.getPointId()))
                    pointsToRemove.add(point);
            }
            if (pointsToRemove != null) {
                if (delDynamic)
                    deleteAllDynamic(pointsToRemove);
                points.removeAll(pointsToRemove);

            }
        }
    }

    private boolean pointBelongsToController(List<LitePoint> controllerPoints, int pointID) {
        for (int i = 0; i < controllerPoints.size(); i++) {
            LitePoint point = controllerPoints.get(i);
            if (point.getLiteID() == pointID)
                return true;
        }
        return false;
    }

    public MultiDBPersistent getMonitorPointsVector() {
        if (monitorPointsVector == null) {
            monitorPointsVector = new MultiDBPersistent();
        }
        return monitorPointsVector;
    }

    /**
     * checks to see if the point is on the assigned list and populates
     * unassigned list
     * @param monitorPoint
     */
    private boolean isPointAssigned(CapBankMonitorPointParams monitorPoint) {
        for (Iterator<CapBankMonitorPointParams> iter = getAssignedPoints().iterator(); iter.hasNext();) {
            CapBankMonitorPointParams assignedPoint = iter.next();
            if (assignedPoint.getPointId() == monitorPoint.getPointId()) {
                return true;
            }
        }
        return false;
    }

    public DeviceBase getController() {
        return controller;
    }

    public boolean isTwoWayController() {
        if (controller != null)
            return (controller instanceof TwoWayDevice);
        return false;
    }

    public boolean isOneWayController() {
        if (controller != null) {
            if (!isTwoWayController() && (controller instanceof CapBankController))

                return true;
        }
        return false;

    }

    public CapBank getCapBank() {
        return capBank;
    }

    public boolean isEditingIntegrity() {

        return isTwoWayController() && ((TwoWayDevice) controller).getDeviceScanRateMap()
                                                                  .containsKey(DeviceScanRate.TYPE_INTEGRITY);
    }

    /**
     * @return
     */
    public boolean isEditingException() {

        return isTwoWayController() && ((TwoWayDevice) controller).getDeviceScanRateMap()
                                                                  .containsKey(DeviceScanRate.TYPE_EXCEPTION);
    }

    /**
     * Event fired when the the CapBank control point selection has changed
     * @throws IOException
     */
    public void capBankTeeClick(ActionEvent ae) throws IOException {

        String val = (String) FacesContext.getCurrentInstance()
                                          .getExternalContext()
                                          .getRequestParameterMap()
                                          .get("ptID");
        if (val == null)
            return;
        if (getDbPersistent() instanceof CapBank) {
            capBank.getCapBank().setControlPointID(new Integer(val));
            int controlPointId = capBank.getCapBank()
                                        .getControlPointID()
                                        .intValue();
            LitePoint litePoint = pointDao.getLitePoint(controlPointId);
            if (litePoint != null) {
                int paoId = litePoint.getPaobjectID();
                Integer ctlPointid = new Integer(paoId);
                capBank.getCapBank().setControlDeviceID(ctlPointid);
            }
        }
        resetController(capBank);
        if (controller instanceof CapBankController)
            handleMonitorPointsForController(capBank.getCapBank()
                                                    .getControlDeviceID()
                                                    .intValue());
        else
            resetAdvancedTab();
    }

    public String getCtlPaoName() {
        if (capBank != null) {
            Integer controlDeviceID = capBank.getCapBank().getControlDeviceID();
            return paoDao.getLiteYukonPAO(controlDeviceID).getPaoName();
        } else
            return "";
    }

    public String getCtlPointName() {
        try {
            Integer pointID = capBank.getCapBank().getControlPointID();
            if (pointID != null && pointID.intValue() > 0)
                return pointDao.getLitePoint(pointID).getPointName();
        } catch (NullPointerException npe) {
            CTILogger.info(npe.getMessage());
        }
        return "";
    }

    public Integer getCtlPaoID() {
        Integer controlDeviceID;
        try {
            controlDeviceID = capBank.getCapBank().getControlDeviceID();
        } catch (NullPointerException npe) {
            controlDeviceID = 0;
        }
        return controlDeviceID;
    }

    public Integer getCtlPointID() {
        Integer controlPointID;
        try {
            controlPointID = capBank.getCapBank().getControlPointID();
        } catch (NullPointerException npe) {
            controlPointID = 0;
        }
        return controlPointID;
    }

    public void ctlPointChanged(ValueChangeEvent vce) {
        Integer val = (Integer) vce.getNewValue();
        if (val == null)
            return;
        if (getDbPersistent() instanceof CapBank) {
            capBank.getCapBank().setControlPointID(new Integer(val));
            int controlPointId = capBank.getCapBank()
                                        .getControlPointID()
                                        .intValue();
            LitePoint litePoint = pointDao.getLitePoint(controlPointId);
            if (litePoint != null) {
                int paoId = litePoint.getPaobjectID();
                Integer ctlPointid = new Integer(paoId);
                capBank.getCapBank().setControlDeviceID(ctlPointid);
            }
        }
        resetController(capBank);
        if (controller instanceof CapBankController)
            handleMonitorPointsForController(capBank.getCapBank()
                                                    .getControlDeviceID()
                                                    .intValue());
        else
            resetAdvancedTab();

    }

    public CapBankAdditional getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(CapBankAdditional additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public void resetForm() {
        initAdditionalInfo();
    }

    public boolean isCustomSize() {
        return customSize;
    }

    public void setCustomSize(boolean customSize) {
        this.customSize = customSize;
    }


}
