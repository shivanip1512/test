package com.cannontech.web.editor;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;

import org.apache.myfaces.custom.tree2.HtmlTree;
import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeNodeBase;

import com.cannontech.cbc.exceptions.MultipleDevicesOnPortException;
import com.cannontech.cbc.exceptions.PortDoesntExistException;
import com.cannontech.cbc.exceptions.SameMasterSlaveCombinationException;
import com.cannontech.cbc.exceptions.SerialNumberExistsException;
import com.cannontech.cbc.model.ICBControllerModel;
import com.cannontech.cbc.util.CapControlUtils;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.ConfigurationBase;
import com.cannontech.common.device.config.model.DNPConfiguration;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.capcontrol.CapBankController702x;
import com.cannontech.database.data.capcontrol.CapBankControllerDNP;
import com.cannontech.database.data.capcontrol.ICapBankController;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.RemoteBase;
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
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.capcontrol.CCSessionInfo;
import com.cannontech.web.util.JSFParamUtil;
import com.cannontech.web.util.JSFTreeUtils;

public class CBControllerEditor implements ICBControllerModel {

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
        init(paoId);
    }
    
    private void init(int paoId){
        LiteYukonPAObject litePAO = DaoFactory.getPaoDao().getLiteYukonPAO(paoId);
        deviceCBC = PAOFactory.createPAObject(litePAO);
        setPaoCBC(deviceCBC);
        retrieveDB();
        if (getPaoCBC() != null) {
            if (!(getPaoCBC() instanceof ICapBankController)) {
                CTILogger.warn("The CapController editor only allows PAO ids that map to a Controller, paoID=" + paoId + " is not an instance of a ICapBankController");
            } else {
                if (deviceCBC instanceof CapBankController702x) {
                    setSerialNumber(((CapBankController702x) deviceCBC).getSerialNumber().longValue());
                }
                if (deviceCBC instanceof CapBankControllerDNP) {
                    setSerialNumber(((CapBankControllerDNP) deviceCBC).getSerialNumber()
                                                                      .longValue());
                }
                if (deviceCBC instanceof CapBankController) {
                    setSerialNumber(((CapBankController) deviceCBC).getDeviceCBC().getSerialNumber().longValue());
                }
                this.deviceType = PaoType.getForDbString(deviceCBC.getPAOType()).getDeviceTypeId();
            }
        }
    }

    public CBControllerEditor() {}

    public boolean isEditingIntegrity() {
        return isTwoWay() && ((TwoWayDevice) getPaoCBC()).getDeviceScanRateMap().containsKey(DeviceScanRate.TYPE_INTEGRITY);
    }

    public boolean isEditingException() {

        return isTwoWay() && ((TwoWayDevice) getPaoCBC()).getDeviceScanRateMap().containsKey(DeviceScanRate.TYPE_EXCEPTION);
    }

    public void setEditingIntegrity(boolean val) {
    }

    public void setEditingException(boolean val) {
    }

    public boolean isTwoWay() {
        if (getPaoCBC() != null) {
            int type = PaoType.getForDbString(getPaoCBC().getPAOType()).getDeviceTypeId();
        	return CapControlUtils.isTwoWay(type);
        }
        return false;
    }
    
    public boolean isOneWay() {
        if (getPaoCBC() != null) {
        	if ( !isTwoWay() && (getPaoCBC() instanceof ICapBankController) ) {
        		return true;
        	}
        }
        return false;
    }

    public YukonPAObject getPaoCBC() {
        return deviceCBC;
    }

    public void setPaoCBC(YukonPAObject deviceCBC) {
        this.deviceCBC = deviceCBC;
    }

    public void retrieveDB() {
        if (getPaoCBC() == null) {
            return;
        }
        
        try {
            setPaoCBC(Transaction.createTransaction(Transaction.RETRIEVE, getPaoCBC()).execute());
        } catch (TransactionException te) {
            CTILogger.error("Unable to retrieve CBC db object", te);
            return;
        }
    }

    @SuppressWarnings("unchecked")
    public TreeNode getPointList() {
        if (pointList == null) {
	    	pointList = new TreeNodeBase("root", "Points", false);
	        TreeNode analog = new TreeNodeBase("pointtype", "Analog", false);
	        TreeNode status = new TreeNodeBase("pointtype", "Status", false);
	        TreeNode accum = new TreeNodeBase("pointtype","Accumulator", false);
	        if (deviceCBC != null) {
                int deviceId = deviceCBC.getPAObjectID();
                List<LitePoint> litePoints = DaoFactory.getPointDao().getLitePointsByPaObjectId(deviceId);
		
		        Comparator<LitePoint> pointOffsetComparator = 
		            new Comparator<LitePoint>(){
		                public int compare(LitePoint point1, LitePoint point2){
		                    Integer point1Offset = point1.getPointOffset();
		                    Integer point2Offset = point2.getPointOffset();
		                    return point1Offset.compareTo(point2Offset);
		                }
		            };
                
		        //using the comparator to sort by point offset instead of default sort by LiteID
                TreeSet<LitePoint> statusSet = new TreeSet<LitePoint>(pointOffsetComparator);
		        TreeSet<LitePoint> analogSet = new TreeSet<LitePoint>(pointOffsetComparator);
		        TreeSet<LitePoint> accumSet = new TreeSet<LitePoint>(pointOffsetComparator);

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
                
		        analog = JSFTreeUtils.createTreeFromPointList(analogSet, analog);
		        status = JSFTreeUtils.createTreeFromPointList(statusSet, status);
		        accum = JSFTreeUtils.createTreeFromPointList(accumSet, accum);
		        
		        pointList.getChildren().add(status);
		        pointList.getChildren().add(analog);
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
    	
    	/*
    	 * A.  Show an error and don’t save the update if the user tries to put 
         *      the same master/slave address combination for a different device on the same communication port
         * B.  Show a warning if the user uses the same master/slave address for a 
         *      device on a different communication port
    	 */
        if (getPaoCBC() instanceof CapBankController702x || getPaoCBC() instanceof CapBankControllerDNP ) {
            DeviceAddress currentDeviceAddress;
            Integer commPortId;
            
            if( getPaoCBC() instanceof CapBankController702x ){
                currentDeviceAddress= ((CapBankController702x) getPaoCBC()).getDeviceAddress();
                commPortId = ((CapBankController702x) getPaoCBC()).getDeviceDirectCommSettings().getPortID();
            }else {
                currentDeviceAddress = ((CapBankControllerDNP) getPaoCBC()).getDeviceAddress();
                commPortId = ((CapBankControllerDNP) getPaoCBC()).getDeviceDirectCommSettings().getPortID();
            }
            
            DeviceDao deviceDao = DaoFactory.getDeviceDao();
            
            List devicesWithSameAddress = deviceDao.getDevicesByDeviceAddress(currentDeviceAddress.getMasterAddress(), currentDeviceAddress.getSlaveAddress());
            List devicesByPort = deviceDao.getDevicesByPort(commPortId.intValue());         
            //remove the current device from the list 
            devicesByPort.remove(getPaoCBC().getPAObjectID());
            devicesWithSameAddress.remove(getPaoCBC().getPAObjectID());
            
            if (commPortId.intValue() <= 0) {
                throw new PortDoesntExistException();
            }
            
            //check to see if the master slave combination is the same
            if (devicesWithSameAddress.size() > 0) {
                for (int i = 0; i < devicesWithSameAddress.size(); i++) {
                    Integer paoId = (Integer) devicesWithSameAddress.get(i);
                    if (devicesByPort.contains(paoId)) {
                        LiteYukonPAObject litePAO = DaoFactory.getPaoDao().getLiteYukonPAO(paoId.intValue());
                        throw new MultipleDevicesOnPortException(litePAO.getPaoName());
                    }
                }
                LiteYukonPAObject litePAO = DaoFactory.getPaoDao().getLiteYukonPAO(((Integer) devicesWithSameAddress.get(0)).intValue());
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
			else if (deviceCBC instanceof CapBankControllerDNP) {
                CapBankControllerDNP controller = (CapBankControllerDNP) deviceCBC;
                paos = DeviceCBC.isSerialNumberUnique(getSerialNumber(), controller.getDevice().getDeviceID());
            }
			//if serial was unique then paos would be empty
			//throw an exception to the calling class to indicate
			if (paos != null && paos.length > 0) {
				String paosWithSameSerialNumber = "";
				for (int i = 0; i < paos.length; i++) {
					if (i == paos.length - 1) {
						paosWithSameSerialNumber += paos[i] + ".";
					} else {
						paosWithSameSerialNumber += paos[i] + ", ";
					}
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
			else if (getPaoCBC() instanceof CapBankControllerDNP) {
			    CapBankControllerDNP cbc = (CapBankControllerDNP) getPaoCBC();
                cbc.getDeviceCBC().setSerialNumber(new Integer ( (String.valueOf (getSerialNumber()))));
            }
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
            CBCNavigationUtil.bookmarkLocationAndRedirect(location, session);
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
            CBCNavigationUtil.bookmarkLocationAndRedirect(location,session);            
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
    
    public void deletePointClick (ActionEvent ae){
        FacesMessage fm = new FacesMessage();
        //save the state of the point tree before changing the page
        saveState(getPointTree());        
        try {
            Integer paoID = getPaoCBC().getPAObjectID();
            List<LitePoint> points = DaoFactory.getPointDao().getLitePointsByPaObjectId(paoID.intValue());
            String attribVal  = "";
            for (LitePoint point : points) {
                attribVal += "value=" + point.getLiteID() + "&";
            }
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            String location = "deleteBasePoint.jsf?" + attribVal;
            CBCNavigationUtil.bookmarkLocationAndRedirect(location,session);            
            FacesContext.getCurrentInstance().getExternalContext().redirect(location);
            FacesContext.getCurrentInstance().responseComplete();
        } 
        catch (IOException e) {
            fm.setDetail("ERROR - Couldn't redirect. CBControllerEditor:deletePointClick. " + e.getMessage());
        }
        finally{
            if(fm.getDetail() != null) {
                FacesContext.getCurrentInstance().addMessage("delete_point_click", fm);
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
             if (fcsMessage.getDetail() != null) {
                 FacesContext.getCurrentInstance().addMessage("cti_db_add", fcsMessage);                
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
            if (deviceCBC instanceof CapBankControllerDNP) {          
                setSerialNumber ( ((CapBankControllerDNP)deviceCBC).getSerialNumber().longValue());
            }
	    	if (deviceCBC instanceof CapBankController) {
	    		setSerialNumber ( ((CapBankController)deviceCBC).getDeviceCBC().getSerialNumber().longValue());           
	    	}
    	}
    }
	
	private void saveState (HtmlTree tree) {
        if (tree != null) {
            Object treeState = tree.saveState(FacesContext.getCurrentInstance());
            CCSessionInfo ccSession = (CCSessionInfo) JSFParamUtil.getJSFVar("ccSession");
    		ccSession.setTreeState(pointTreeName, treeState);
        }
    }

	private void restoreState (HtmlTree tree) {
        if (tree != null) {
    		CCSessionInfo ccSession = (CCSessionInfo) JSFParamUtil.getJSFVar("ccSession");    		
			if (ccSession.getTreeState(pointTreeName) != null )
				this.pointTree.restoreState(FacesContext.getCurrentInstance(), ccSession.getTreeState(pointTreeName));
        }
	}

	public boolean isDevice702X() {
		if (getPaoCBC() != null) {
			int deviceType = PaoType.getForDbString(getPaoCBC().getPAOType()).getDeviceTypeId();
			return DeviceTypesFuncs.isCapBankController702X(deviceType);
		}
		return false;		
	}
	
	public boolean isDevice802X() {
	    if(getPaoCBC() != null) {
	        PaoType paoType = PaoType.getForDbString(getPaoCBC().getPAOType());
	        return CapControlUtils.isCapBankController802X(paoType);
	    }
	    return false;
	}

	public boolean isDeviceDNP() {
        if (getPaoCBC() != null) {
            PaoType paoType = PaoType.getForDbString(getPaoCBC().getPAOType());
            return paoType == PaoType.CBC_DNP;
        }
        return false;       
	}
	
	public boolean isDevice701X() {
		if (getPaoCBC() != null) {
			PaoType paoType = PaoType.getForDbString(getPaoCBC().getPAOType());
			return (paoType == PaoType.CBC_7010 || 
			        paoType == PaoType.CBC_7011 || 
			        paoType == PaoType.CBC_7012);
		}
		return false;		
	}

	public int getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
    }
	
	public boolean isTcpPort() {
        String cbcTypeStr = getPaoCBC().getPAOType();
        PaoType deviceType = PaoType.getForDbString(cbcTypeStr);
        
        if (PAOGroups.isTcpPortEligible(deviceType) && deviceType.isCbc()) {
            return isTcpPort((RemoteBase)getPaoCBC());
        }
        
        return false;
	}
	
	public boolean isTcpPort(RemoteBase cbc) {
	    int portId = cbc.getDeviceDirectCommSettings().getPortID();
	    return DeviceTypesFuncs.isTcpPort(portId);
	}
	
	@Override
	public DNPConfiguration getDnpConfiguration() {
	    DeviceConfigurationDao configurationDao = 
	            YukonSpringHook.getBean("deviceConfigurationDao", DeviceConfigurationDao.class);

	    PaoIdentifier identifier = 
	            new PaoIdentifier(deviceCBC.getPAObjectID(), 
	                              PaoType.getForDbString(deviceCBC.getPAOType()));
	    
	    ConfigurationBase config = configurationDao.findConfigurationForDevice(new SimpleDevice(identifier));
	    return (DNPConfiguration) configurationDao.getConfiguration(config.getId());
	}
}
