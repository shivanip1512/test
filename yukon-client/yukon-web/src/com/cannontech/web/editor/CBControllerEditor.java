package com.cannontech.web.editor;

import java.io.IOException;
import java.util.List;
import java.util.TreeSet;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;

import org.apache.myfaces.custom.tree2.HtmlTree;
import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeNodeBase;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.functions.DeviceFuncs;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.capcontrol.CapBankController702x;
import com.cannontech.database.data.capcontrol.ICapBankController;
import com.cannontech.database.data.device.TwoWayDevice;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOFactory;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.PointUtil;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.device.DeviceAddress;
import com.cannontech.database.db.device.DeviceScanRate;
import com.cannontech.servlet.nav.CBCNavigationUtil;
import com.cannontech.web.editor.point.PointForm;
import com.cannontech.web.exceptions.MultipleDevicesOnPortException;
import com.cannontech.web.exceptions.PortDoesntExistException;
import com.cannontech.web.exceptions.SameMasterSlaveCombinationException;
import com.cannontech.web.util.JSFParamUtil;
import com.cannontech.web.util.JSFTreeUtils;
import com.cannontech.yukon.cbc.CBCUtils;

/**
 * @author ryan
 *
 */
public class CBControllerEditor {

    private YukonPAObject deviceCBC = null;
    
    private String cbcControllerStatusMessage = null;
    private HtmlTree pointTree = null;
    private boolean editingController;

    /**
     * Accepts a paoID and creates the DBPersistent from it, and then retrieves the
     * data from the DB
     */
    public CBControllerEditor(int paoId) {
        super();

        LiteYukonPAObject litePAO = PAOFuncs.getLiteYukonPAO(paoId);
        deviceCBC = PAOFactory.createPAObject(litePAO);
        setPaoCBC(deviceCBC);
        //create points for device if need be
        //insert the points into DB
        //if there was anything such as DB exception or empty points
        //message status message will be set
        if (deviceCBC instanceof CapBankController702x) 
            {          
            showScanRate();
            }
        
        retrieveDB();
        //complain if the DB object is not null and it is not a Controller
        if (getPaoCBC() != null && !(getPaoCBC() instanceof ICapBankController))
            CTILogger.warn("The CapController editor only allows PAO ids that map to a Controller, paoID=" + paoId + " is not an instance of a ICapBankController");
    }



    /**
     * @return
     */
    public boolean isEditingIntegrity() {

        return isTwoWay() && ((TwoWayDevice) getPaoCBC()).getDeviceScanRateMap()
                                                         .containsKey(DeviceScanRate.TYPE_INTEGRITY);
    }

    /**
     * @return
     */
    public boolean isEditingException() {

        return isTwoWay() && ((TwoWayDevice) getPaoCBC()).getDeviceScanRateMap()
                                                         .containsKey(DeviceScanRate.TYPE_EXCEPTION);
    }

    /**
     * @return
     */
    public void setEditingIntegrity(boolean val) {
    }

    /**
     * @return
     */
    public void setEditingException(boolean val) {
    }

    /**
     * @return
     */
    public boolean isTwoWay() {
        return CBCUtils.isTwoWay(PAOGroups.getDeviceType(getPaoCBC().getPAOType()));
    }

    /**
     * @return
     */
    public YukonPAObject getPaoCBC() {

        return deviceCBC;
    }

    /**
     * @param deviceCBC
     */
    private void setPaoCBC(YukonPAObject deviceCBC) {
        this.deviceCBC = deviceCBC;
    }

    /**
     * Gets the full DB object from the database so we can operate on
     * the whole thing.
     */
    public void retrieveDB() {

        if (getPaoCBC() == null)
            return;

        try {
            setPaoCBC((YukonPAObject) Transaction.createTransaction(Transaction.RETRIEVE,
                                                                    getPaoCBC())
                                                 .execute());

        } catch (TransactionException te) {
            CTILogger.error("Unable to retrieve CBC db object", te);
            return;
        }

    }

    public TreeNode getPointList() {
        TreeNode rootData = new TreeNodeBase("root", "Points", false);
        TreeNode points = new TreeNodeBase("pointtype", "analog", false);
        TreeNode status = new TreeNodeBase("pointtype", "status", false);
        TreeNode accum = new TreeNodeBase("pointtype","accumulator", false);
        
        LitePoint[] tempArray = PAOFuncs.getLitePointsForPAObject(deviceCBC.getPAObjectID());

        TreeSet statusSet = new TreeSet();
        TreeSet analogSet = new TreeSet();
        TreeSet accumSet = new TreeSet();
        
        for (int i = 0; i < tempArray.length; i++) {
            LitePoint litePoint = tempArray[i];
            if (litePoint.getPointType() == PointTypes.ANALOG_POINT) {
                analogSet.add(litePoint);
            } else if (litePoint.getPointType() == PointTypes.STATUS_POINT) {
                statusSet.add(litePoint);
            } else if (litePoint.getPointType() == PointTypes.PULSE_ACCUMULATOR_POINT){
                accumSet.add(litePoint);
            }
        }

        points = JSFTreeUtils.createTreeFromPointList(analogSet, points);
        status = JSFTreeUtils.createTreeFromPointList(statusSet, status);
        accum = JSFTreeUtils.createTreeFromPointList(accumSet, accum);
        
        rootData.getChildren().add(status);
        rootData.getChildren().add(points);
        rootData.getChildren().add(accum);
        
        return rootData;
    }

    public String getSelectedPointFormatString() {
        return "Add Point";
    }

    public String getCbcControllerStatusMessage() {
        return cbcControllerStatusMessage;
    }

    public void setCbcControllerStatusMessage(String msgStr) {
        this.cbcControllerStatusMessage = msgStr;
    }

    public HtmlTree getPointTree() {

        return pointTree;
    }

    public void setPointTree(HtmlTree pointTree) {

        this.pointTree = pointTree;
    }

    public boolean isEditingController() {
        return editingController;
    }

    public void setEditingController(boolean editingController) {
        this.editingController = editingController;
    }

    public void checkForErrors() throws PortDoesntExistException,
            MultipleDevicesOnPortException, SameMasterSlaveCombinationException {
        /*        a.  Show an error and don�t save the update if the user tries to put 
         the same master/slave address combination for a different device on the same communication port
         b.  Show a warning if the user uses the same master/slave address for a 
         device on a different communication port*/
        if (getPaoCBC() instanceof CapBankController702x) {

            DeviceAddress currentDeviceAddress = ((CapBankController702x) getPaoCBC()).getDeviceAddress();
            Integer commPortId = ((CapBankController702x) getPaoCBC()).getDeviceDirectCommSettings()
                                                                      .getPortID();


            List devicesWithSameAddress = DeviceFuncs.getDevicesByDeviceAddress(currentDeviceAddress.getMasterAddress(),
                                                                                currentDeviceAddress.getSlaveAddress());
            List devicesByPort = DeviceFuncs.getDevicesByPort(commPortId.intValue());
            //remove the current device from the list 
            devicesByPort.remove(getPaoCBC().getPAObjectID());
            devicesWithSameAddress.remove(getPaoCBC().getPAObjectID());
            
            
            if (!(!devicesByPort.isEmpty())) {
                throw new PortDoesntExistException();
            }
            
            //check to see if the master slave combination is the same
            if (devicesWithSameAddress.size() > 0) {
                for (int i = 0; i < devicesWithSameAddress.size(); i++) {

                        Integer paoId = (Integer)devicesWithSameAddress.get(i);
                        if (devicesByPort.contains(paoId)) {
                            LiteYukonPAObject litePAO = PAOFuncs.getLiteYukonPAO(paoId.intValue());
                            throw new MultipleDevicesOnPortException(litePAO.getPaoName());
                            
                        }


                }
                LiteYukonPAObject litePAO = PAOFuncs.getLiteYukonPAO((Integer)devicesWithSameAddress.get(0));
                throw new SameMasterSlaveCombinationException(litePAO.getPaoName());
            }
        }
    }
    
    public void showScanRate() {
        // find out if this device is TwoWay (used for 2 way CBCs)
        if (CBCUtils.isTwoWay(PAOGroups.getDeviceType(getPaoCBC().getPAOType()))) 
        {
            
            TwoWayDevice twoWayDev = (TwoWayDevice)getPaoCBC();
            twoWayDev.getDeviceScanRateMap().put(
                        DeviceScanRate.TYPE_INTEGRITY,
                        new DeviceScanRate(getPaoCBC()
                                .getPAObjectID(), DeviceScanRate.TYPE_INTEGRITY));
            
        
            twoWayDev.getDeviceScanRateMap().put(
                                                 DeviceScanRate.TYPE_EXCEPTION,
                                                 new DeviceScanRate(getPaoCBC()
                                                         .getPAObjectID(), DeviceScanRate.TYPE_EXCEPTION));
                                     
                                 
        }
    }

    public void pointClick (ActionEvent ae){
        FacesMessage fm = new FacesMessage();
        try {
            //make sure the point form will have the pao id
            //of the cbc 
            String red = "pointBase.jsf?parentId=" + getPaoCBC().getPAObjectID().toString() + "&itemid=";
            String val = JSFParamUtil.getJSFReqParam("ptID");
            String location = red + val;            
            //bookmark the current page
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            CBCNavigationUtil.bookmarkLocation(location, session);
            //go to the next page
            FacesContext.getCurrentInstance().getExternalContext().redirect(location);
            FacesContext.getCurrentInstance().responseComplete();
        } 
        catch (IOException e) {
            fm.setDetail("ERROR - Couldn't redirect. CBControllerEditor:pointClick. " + e.getMessage());
        } catch (Exception e) {
            //some code to handle null session 
        }
        finally{
            if(fm.getDetail() != null) {
                FacesContext.getCurrentInstance().addMessage("point_click", fm);
            }
        }
    }
    
    public void addPointClick (ActionEvent ae){
        FacesMessage fm = new FacesMessage();
        try {
            String val = JSFParamUtil.getJSFReqParam("parentId");
            FacesContext.getCurrentInstance().getExternalContext().redirect("pointWizBase.jsf?parentId=" + val);
            FacesContext.getCurrentInstance().responseComplete();
        } 
        catch (IOException e) {
            fm.setDetail("ERROR - Couldn't redirect. CBControllerEditor:addPointClick. " + e.getMessage());
        }
        finally{
            if(fm.getDetail() != null) {
                FacesContext.getCurrentInstance().addMessage("add_point_click", fm);
            }
        }
    }
   
    public static void insertPointsIntoDB(DBPersistent pointVector) {
        FacesMessage fcsMessage = new FacesMessage();
         try {
         
             PointUtil.insertIntoDB(pointVector);
         } 
         catch (TransactionException e) {
        
             fcsMessage.setDetail("ERROR creating points for CBC:CBCControlEditor" + e.getMessage());
         }
         
         finally {
    
             if (fcsMessage.getDetail() != null)
             {
                 FacesContext.getCurrentInstance()
                 .addMessage("cti_db_add", fcsMessage);                
             }
          }
         
     }
    
}
