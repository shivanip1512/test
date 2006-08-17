package com.cannontech.web.editor;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.TreeSet;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;

import org.apache.myfaces.custom.tree2.HtmlTree;
import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeNodeBase;

import com.cannontech.cbc.web.CBCSessionInfo;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.capcontrol.CapBankController702x;
import com.cannontech.database.data.capcontrol.ICapBankController;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.TwoWayDevice;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOFactory;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.PointUtil;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.capcontrol.DeviceCBC;
import com.cannontech.database.db.device.DeviceAddress;
import com.cannontech.database.db.device.DeviceScanRate;
import com.cannontech.servlet.nav.CBCNavigationUtil;
import com.cannontech.web.exceptions.MultipleDevicesOnPortException;
import com.cannontech.web.exceptions.PortDoesntExistException;
import com.cannontech.web.exceptions.SameMasterSlaveCombinationException;
import com.cannontech.web.exceptions.SerialNumberExistsException;
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
    private boolean editingController  = false;
    private TreeNode pointList = null;
    private long serialNumber = 0;
    private int deviceType = 0;
    
    

    
    
    private final String pointTreeName = "cbcPointTree";

    /**
     * Accepts a paoID and creates the DBPersistent from it, and then retrieves the
     * data from the DB
     */
    public CBControllerEditor(int paoId) {
        super();

        LiteYukonPAObject litePAO = DaoFactory.getPaoDao().getLiteYukonPAO(paoId);
        deviceCBC = PAOFactory.createPAObject(litePAO);
        setPaoCBC(deviceCBC);
        //create points for device if need be
        //insert the points into DB
        //if there was anything such as DB exception or empty points
        //message status message will be set
        retrieveDB();
        //complain if the DB object is not null and it is not a Controller
        if (getPaoCBC() != null) {
        	if (!(getPaoCBC() instanceof ICapBankController)) {
        		CTILogger.warn("The CapController editor only allows PAO ids that map to a Controller, paoID=" + paoId + " is not an instance of a ICapBankController");
        	}
        else {
        	
        	if (deviceCBC instanceof CapBankController702x) {          
        		
        		setSerialNumber ( ((CapBankController702x)deviceCBC).getSerialNumber().longValue());
            
        	}
        	
        	if (deviceCBC instanceof CapBankController) {
        		setSerialNumber ( ((CapBankController)deviceCBC).getDeviceCBC().getSerialNumber().longValue());           
        	}
//        	setDeviceType(PAOGroups.getDeviceType(deviceCBC.getPAOType()));
        	this.deviceType = PAOGroups.getDeviceType(deviceCBC.getPAOType());
        
        	}
        }
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
        if (getPaoCBC() != null)
        	return CBCUtils.isTwoWay(PAOGroups.getDeviceType(getPaoCBC().getPAOType()));
        return false;
    }
    
    public boolean isOneWay() {
        if (getPaoCBC() != null) {
        	if ( !isTwoWay() && (getPaoCBC() instanceof ICapBankController) )
        		return true;
        }
        return false;
        
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
    public void setPaoCBC(YukonPAObject deviceCBC) {
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
        if (pointList == null) {
	    	pointList = new TreeNodeBase("root", "Points", false);
	        TreeNode points = new TreeNodeBase("pointtype", "analog", false);
	        TreeNode status = new TreeNodeBase("pointtype", "status", false);
	        TreeNode accum = new TreeNodeBase("pointtype","accumulator", false);
	        if (deviceCBC != null) {
                int deviceId = deviceCBC.getPAObjectID();
                List<LitePoint> litePoints = DaoFactory.getPointDao().getLitePointsByPaObjectId(deviceId);
		
		        TreeSet statusSet = new TreeSet();
		        TreeSet analogSet = new TreeSet();
		        TreeSet accumSet = new TreeSet();

                for (LitePoint litePoint : litePoints) {
		            int pointType = litePoint.getPointType();
					if (pointType == PointTypes.ANALOG_POINT || pointType == PointTypes.CALCULATED_POINT) {
		                analogSet.add(litePoint);
		            } else if (pointType == PointTypes.STATUS_POINT || pointType == PointTypes.CALCULATED_STATUS_POINT) {
		                statusSet.add(litePoint);
		            } else if (pointType == PointTypes.PULSE_ACCUMULATOR_POINT){
		                accumSet.add(litePoint);
		            }
		        }
		
		        points = JSFTreeUtils.createTreeFromPointList(analogSet, points);
		        status = JSFTreeUtils.createTreeFromPointList(statusSet, status);
		        accum = JSFTreeUtils.createTreeFromPointList(accumSet, accum);
		        
		        pointList.getChildren().add(status);
		        pointList.getChildren().add(points);
		        pointList.getChildren().add(accum);
		        
		        //make sure we are will be grouping the nodes that have a lot of points attached to them
		        JSFTreeUtils.splitTree(pointList, 100, "sublevels");
	        }
	    }
        //restore any previous states of tree
        restoreState(getPointTree());
       
		return pointList;
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
            MultipleDevicesOnPortException, SameMasterSlaveCombinationException, SQLException, SerialNumberExistsException {
        
    	//error handling when serial number exists
    	if (getPaoCBC() instanceof ICapBankController) {
        	
        	handleSerialNumber();        		
        }
    	
    	/*        a.  Show an error and don’t save the update if the user tries to put 
         the same master/slave address combination for a different device on the same communication port
         b.  Show a warning if the user uses the same master/slave address for a 
         device on a different communication port*/
        if (getPaoCBC() instanceof CapBankController702x) {

            DeviceAddress currentDeviceAddress = ((CapBankController702x) getPaoCBC()).getDeviceAddress();
            Integer commPortId = ((CapBankController702x) getPaoCBC()).getDeviceDirectCommSettings()
                                                                      .getPortID();


            List devicesWithSameAddress = DaoFactory.getDeviceDao().getDevicesByDeviceAddress(currentDeviceAddress.getMasterAddress(),
                                                                                currentDeviceAddress.getSlaveAddress());
            List devicesByPort = DaoFactory.getDeviceDao().getDevicesByPort(commPortId.intValue());         
            //remove the current device from the list 
            devicesByPort.remove(getPaoCBC().getPAObjectID());
            devicesWithSameAddress.remove(getPaoCBC().getPAObjectID());
            
            
            if (commPortId.intValue() <= 0) {
                throw new PortDoesntExistException();
            }
            
            //check to see if the master slave combination is the same
            if (devicesWithSameAddress.size() > 0) {
                for (int i = 0; i < devicesWithSameAddress.size(); i++) {

                        Integer paoId = (Integer)devicesWithSameAddress.get(i);
                        if (devicesByPort.contains(paoId)) {
                            LiteYukonPAObject litePAO = DaoFactory.getPaoDao().getLiteYukonPAO(paoId.intValue());
                            throw new MultipleDevicesOnPortException(litePAO.getPaoName());
                            
                        }


                }
                LiteYukonPAObject litePAO = DaoFactory.getPaoDao().getLiteYukonPAO(((Integer)devicesWithSameAddress.get(0)).intValue());
                throw new SameMasterSlaveCombinationException(litePAO.getPaoName());
            }
        }
    }



	/**
	 * checks to see if the serial number on the form is unique
	 * @throws SQLException
	 * @throws SerialNumberExistsException
	 */
	private void handleSerialNumber() throws SQLException, SerialNumberExistsException {
			String[] paos = null;	
			//find out if the serial number is unique
			if (deviceCBC != null) {
				if (deviceCBC instanceof CapBankController) {
					CapBankController controller = (CapBankController) deviceCBC;
					paos = DeviceCBC.isSerialNumberUnique(getSerialNumber(), controller.getDeviceCBC().getDeviceID());
				}
				else if (deviceCBC instanceof CapBankController702x) {
					CapBankController702x controller = (CapBankController702x) deviceCBC;
					paos = DeviceCBC.isSerialNumberUnique(getSerialNumber(), controller.getDevice().getDeviceID());
				}
				//if serial was unique then paos would be empty
				//throw an exception to the calling class to indicate
				if (paos != null && paos.length > 0) {
					String paosWithSameSerialNumber = "";
					for (int i = 0; i < paos.length; i++) {
						if (i == paos.length - 1) 
							paosWithSameSerialNumber += paos[i] + ".";		
						else
							paosWithSameSerialNumber += paos[i] + ", ";
					}
					throw new SerialNumberExistsException (paosWithSameSerialNumber);
				 }
				//if got to the point then we can set the serial number because it is unique
				if (getPaoCBC() instanceof CapBankController) {
					CapBankController cbc = (CapBankController) getPaoCBC();				
					cbc.getDeviceCBC().setSerialNumber(new Integer ( (String.valueOf (getSerialNumber()))));
				}	
				else if (getPaoCBC() instanceof CapBankController702x) {
					CapBankController702x cbc = (CapBankController702x) getPaoCBC();
					cbc.getDeviceCBC().setSerialNumber(new Integer ( (String.valueOf (getSerialNumber()))));
				}
			}
	}



	/**
	 * @throws SQLException
	 * @throws SerialNumberExistsException 
	 * @throws SerialNumberExistsException
	 */
	private void isSerialNumberUnique() throws SQLException, SerialNumberExistsException {
		String[] paos = null;
		
		if (deviceCBC instanceof CapBankController) {
			CapBankController controller = (CapBankController) deviceCBC;
			paos = DeviceCBC.isSerialNumberUnique(getSerialNumber(), controller.getDeviceCBC().getDeviceID());
		}
		else if (deviceCBC instanceof CapBankController702x) {
			CapBankController702x controller = (CapBankController702x) deviceCBC;
			paos = DeviceCBC.isSerialNumberUnique(getSerialNumber(), controller.getDevice().getDeviceID());
		}
		
		if (paos != null && paos.length > 0) {
			String paosWithSameSerialNumber = "";
			for (int i = 0; i < paos.length; i++) {
				if (i == paos.length - 1) 
					paosWithSameSerialNumber += paos[i] + ".";		
				else
					paosWithSameSerialNumber += paos[i] + ", ";
			}
			throw new SerialNumberExistsException (paosWithSameSerialNumber);
		 }
		
	}
    

    public void pointClick (ActionEvent ae){
        FacesMessage fm = new FacesMessage();
        //save the state of the point tree before changing the page
        saveState(getPointTree());
        try {
            //make sure the point form will have the pao id
            //of the cbc 
            String red = "pointBase.jsf?parentId=" + getPaoCBC().getPAObjectID().toString() + "&itemid=";
            String val = JSFParamUtil.getJSFReqParam("ptID");
            String location = red + val;            
            //bookmark the current page
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            new CBCNavigationUtil().bookmarkLocation(location, session);
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
        //save the state of the point tree before changing the page
        saveState(getPointTree());        
        try {
            String val = JSFParamUtil.getJSFReqParam("parentId");
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            String location = "pointWizBase.jsf?parentId=" + val;
            new CBCNavigationUtil().bookmarkLocation(location,session);            
            FacesContext.getCurrentInstance().getExternalContext().redirect(location);
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



	public void setPointList(TreeNode pointList) {
		this.pointList = pointList;
	}



	public long getSerialNumber() {
		return serialNumber;
	}



	public void setSerialNumber(long serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	public void resetSerialNumber () {  	
    	if (deviceCBC != null) {
			if (deviceCBC instanceof CapBankController702x) {          
	    		
	    		setSerialNumber ( ((CapBankController702x)deviceCBC).getSerialNumber().longValue());
	        
	    	}
	    	
	    	if (deviceCBC instanceof CapBankController) {
	    		setSerialNumber ( ((CapBankController)deviceCBC).getDeviceCBC().getSerialNumber().longValue());           
	    	}
    	}
    }
	
	private void saveState (HtmlTree tree) {
        Object treeState = (Object) tree.saveState(FacesContext.getCurrentInstance());
        CBCSessionInfo cbcSession = (CBCSessionInfo) JSFParamUtil.getJSFVar("cbcSession");
		cbcSession.setTreeState(pointTreeName, treeState);
	}

	private void restoreState (HtmlTree tree) {
        if (tree != null) {
    		CBCSessionInfo cbcSession = (CBCSessionInfo) JSFParamUtil.getJSFVar("cbcSession");    		
			if (cbcSession.getTreeState(pointTreeName) != null )
				this.pointTree.restoreState(FacesContext.getCurrentInstance(), cbcSession.getTreeState(pointTreeName));
        }
	}



	public boolean isDevice702X() {
		if (getPaoCBC() != null) {
			int deviceType = PAOGroups.getDeviceType(getPaoCBC().getPAOType());
			if (DeviceTypesFuncs.isCapBankController (deviceType) && 
					DeviceTypesFuncs.cbcHasPort(deviceType))
				return true;
		}
		return false;		
	}

	public boolean isDevice701X() {
		if (getPaoCBC() != null) {
			int deviceType = PAOGroups.getDeviceType(getPaoCBC().getPAOType());
			if (DeviceTypesFuncs.isCapBankController (deviceType) && 
					(! DeviceTypesFuncs.cbcHasPort(deviceType) ) )
				return true;
		}
		return false;		
	}



	public int getDeviceType() {
		return deviceType;
	}



	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;

		}

}
