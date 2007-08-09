package com.cannontech.web.editor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.Validate;
import org.apache.myfaces.custom.tree2.HtmlTree;
import org.apache.myfaces.custom.tree2.TreeModel;
import org.apache.myfaces.custom.tree2.TreeModelBase;
import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeNodeBase;
import org.apache.myfaces.custom.tree2.TreeStateBase;

import com.cannontech.cbc.dao.SeasonScheduleDao;
import com.cannontech.cbc.exceptions.CBCExceptionMessages;
import com.cannontech.cbc.exceptions.FormWarningException;
import com.cannontech.cbc.exceptions.MultipleDevicesOnPortException;
import com.cannontech.cbc.exceptions.PAODoesntHaveNameException;
import com.cannontech.cbc.exceptions.PortDoesntExistException;
import com.cannontech.cbc.exceptions.SameMasterSlaveCombinationException;
import com.cannontech.cbc.exceptions.SerialNumberExistsException;
import com.cannontech.cbc.model.CBCCreationModel;
import com.cannontech.cbc.model.EditorDataModel;
import com.cannontech.cbc.model.ICBControllerModel;
import com.cannontech.cbc.model.ICapControlModel;
import com.cannontech.cbc.model.Season;
import com.cannontech.cbc.point.CBCPointFactory;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.StringUtils;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.capcontrol.CCYukonPAOFactory;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.capcontrol.CapBankController702x;
import com.cannontech.database.data.capcontrol.CapControlArea;
import com.cannontech.database.data.capcontrol.CapControlFeeder;
import com.cannontech.database.data.capcontrol.CapControlSubBus;
import com.cannontech.database.data.capcontrol.ICapBankController;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.TwoWayDevice;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.pao.CapControlTypes;
import com.cannontech.database.data.pao.DBEditorTypes;
import com.cannontech.database.data.pao.PAOFactory;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.database.data.point.StatusPoint;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.capcontrol.CCFeederBankList;
import com.cannontech.database.db.capcontrol.CCFeederSubAssignment;
import com.cannontech.database.db.capcontrol.CapBankAdditional;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.database.db.capcontrol.CapControlSubstationBus;
import com.cannontech.database.db.device.DeviceScanRate;
import com.cannontech.database.db.pao.PAOSchedule;
import com.cannontech.database.db.pao.PAOScheduleAssign;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;
import com.cannontech.database.db.season.SeasonSchedule;
import com.cannontech.servlet.nav.CBCNavigationUtil;
import com.cannontech.web.db.CBCDBObjCreator;
import com.cannontech.web.editor.model.CapControlStrategyModel;
import com.cannontech.web.editor.model.DataModelFactory;
import com.cannontech.web.editor.point.PointLists;
import com.cannontech.web.exceptions.AltBusNeedsSwitchPointException;
import com.cannontech.web.util.CBCDBUtil;
import com.cannontech.web.util.CBCSelectionLists;
import com.cannontech.web.util.JSFParamUtil;
import com.cannontech.web.util.JSFTreeUtils;
import com.cannontech.web.util.JSFUtil;
import com.cannontech.web.wizard.CBCWizardModel;
import com.cannontech.yukon.cbc.CBCUtils;

public class CapControlForm extends DBEditorForm implements ICapControlModel{
    private String paoDescLabel = "Description";
	private String childLabel = "Children";
	private boolean editingCBCStrategy = false;
	private boolean editingController = false;
	private int itemID = -1;
	// contains <Integer(stratID), CapControlStrategy>
	private HashMap<Integer, CapControlStrategy> cbcStrategiesMap = null;
	// contains LiteYukonPAObject
	private List unassignedBanks = null;
	// contains LiteYukonPAObject
	private List unassignedFeeders = null;
	// possible selection types for every wizard panel
	private CBCCreationModel wizData = null;
	// possible editor for the CBC a CapBank belongs to
	private ICBControllerModel cbControllerEditor = null;
	private LiteYukonPAObject[] unusedCCPAOs = null;
	// selectable items that appear in lists on the GUI
	private SelectItem[] kwkvarPaos = null;
	private SelectItem[] kwkvarPoints = null;
	private SelectItem[] cbcStrategies = null;
    private SelectItem[] cbcSchedules = null;
	// variables that hold sub bus info
	protected List subBusList = null;
    private Integer oldSubBus = null;
    //contains the offset variables
    private Map offsetMap = new HashMap();
	// Boolean to keep track of the disable dual subbus status
	// by default will be set to true
    private Boolean enableDualBus = Boolean.FALSE;
	private HtmlTree dualBusSwitchPointTree;
	private boolean isDualSubBusEdited;
    private boolean switchPointEnabled = true;
    private TreeNode varTreeData = null;
    private TreeNode wattTreeData = null;
    private TreeNode voltTreeData = null;
    private SelectItem[] controlMethods = null;
    private Map paoNameMap = null;
    private Map pointNameMap = null;
    private Map<Season, Integer> assignedStratMap = null;
    private EditorDataModel dataModel = null;
    private EditorDataModel currentStratModel = null;
    private SeasonScheduleDao seasonScheduleDao;
    private Integer scheduleId = -1;
    
    /**
	 * default constructor
	 */
	public CapControlForm() {
		super();
    }
    
    /**
     * Returns a Season object for each season of the current schedule.
     */
    public List<Season> getSeasonsForSchedule() {
        return seasonScheduleDao.getSeasonsForSchedule(getScheduleId());
    }
    
    /**
     * Returns the scheduleID of the current pao.
     */
    public Integer getScheduleId() {
        if(scheduleId == -1) {
            Integer paoId = ((YukonPAObject)getDbPersistent()).getPAObjectID();
            scheduleId = seasonScheduleDao.getScheduleForPao(paoId).getScheduleId();
        }
        return scheduleId;
    }
    
    public SelectItem[] getCbcStrategies() {
		if (cbcStrategies == null) {
			CapControlStrategy[] cbcDBStrats = CapControlStrategy.getAllCBCStrategies();
			cbcStrategies = new SelectItem[cbcDBStrats.length];
			for (int i = 0; i < cbcDBStrats.length; i++) {
				cbcStrategies[i] = new SelectItem(cbcDBStrats[i].getStrategyID(), cbcDBStrats[i].getStrategyName());
				getCbcStrategiesMap().put(cbcDBStrats[i].getStrategyID(),cbcDBStrats[i]);
			}
		}
		return cbcStrategies;
	}
    
    public SelectItem[] getCbcSchedules() {
        if (cbcSchedules== null) {
            SeasonSchedule[] cbcDBSchedules = SeasonSchedule.getAllCBCSchedules();
            cbcSchedules = new SelectItem[cbcDBSchedules.length];
            for (int i = 0; i < cbcDBSchedules.length; i++) {
                cbcSchedules[i] = new SelectItem(cbcDBSchedules[i].getScheduleId(), cbcDBSchedules[i].getScheduleName());
            }
        }
        return cbcSchedules;
    }
    
    public Map<Season, Integer> getAssignedStratMap(){
        if(assignedStratMap == null) {
            HashMap<Season, Integer> map = (HashMap<Season, Integer>) seasonScheduleDao.getSeasonStrategyAssignments(((YukonPAObject)getDbPersistent()).getPAObjectID());
            List<Season> seasons = seasonScheduleDao.getSeasonsForSchedule(getScheduleId());
            for(Season season : seasons) {
                if (!map.containsKey(season)) {
                    map.put(season, 0);
                }
            }
            assignedStratMap = map;
        }
        
        return assignedStratMap;
    }

	public HashMap<Integer, CapControlStrategy> getCbcStrategiesMap() {
		if (cbcStrategiesMap == null) {
			cbcStrategiesMap = new HashMap<Integer, CapControlStrategy>(32);
			// force us to init our map with the correct data
			getCbcStrategies();
		}
		return cbcStrategiesMap;
	}

	public SelectItem[] getKwkvarPaos() {
		if (kwkvarPaos == null) {
			PointLists pLists = new PointLists();
			LiteYukonPAObject[] lPaos = pLists.getPAOsByUofMPoints(PointUnits.CAP_CONTROL_VAR_UOMIDS);
			SelectItem[] temp = new SelectItem[lPaos.length];
			for (int i = 0; i < temp.length; i++) {
				temp[i] = new SelectItem(new Integer(lPaos[i].getLiteID()), lPaos[i].getPaoName());
            }
			// add the none PAObject to this list
			kwkvarPaos = new SelectItem[temp.length + 1];
			kwkvarPaos[0] = new SelectItem(new Integer(LiteYukonPAObject.LITEPAOBJECT_NONE.getLiteID()), LiteYukonPAObject.LITEPAOBJECT_NONE.getPaoName());
			System.arraycopy(temp, 0, kwkvarPaos, 1, temp.length);
		}
		return kwkvarPaos;
	}

	public SelectItem[] getKwkvarPoints() {
		if (kwkvarPoints == null) {
			kwkvarPoints = new SelectItem[1];
			kwkvarPoints[0] = new SelectItem(new Integer(LitePoint.NONE_LITE_PT.getLiteID()), LitePoint.NONE_LITE_PT.getPointName());
		}
		return kwkvarPoints;
	}

	public int getCurrentStrategyID() {
		int stratID = CtiUtilities.NONE_ZERO_ID;
//		if (getDbPersistent() instanceof CapControlFeeder) {
//			stratID = ((CapControlFeeder) getDbPersistent()).getCapControlFeeder().getStrategyID().intValue();
//        }else if (getDbPersistent() instanceof CapControlSubBus) {
//			stratID = ((CapControlSubBus) getDbPersistent()).getCapControlSubstationBus().getStrategyID().intValue();
//		}else if (getDbPersistent() instanceof CapControlArea) {
//            stratID = ((CapControlArea) getDbPersistent()).getCapControlArea().getStrategyID().intValue();
//        } else 
        if (getDbPersistent() instanceof CapControlStrategy) {
            stratID = ((CapControlStrategy)getDbPersistent()).getStrategyID();
        }
		return stratID;
	}
    
    public EditorDataModel getCurrentStratModel () {
        if (currentStratModel == null){
            CapControlStrategy strat = getCbcStrategiesMap().get(getCurrentStrategyID());
            currentStratModel = new CapControlStrategyModel (strat);
        }
        ((CapControlStrategyModel) currentStratModel).updateModel();
        return currentStratModel;
    }
    
    public void newStrategySelected (ValueChangeEvent vce) {
        resetCurrentStratModel();
    }
    
    public void newScheduleSelected (ValueChangeEvent vce) {
        assignedStratMap = null;
    }

	public TreeNode getVarTreeData() {
		if (varTreeData == null){
			varTreeData = new TreeNodeBase("root", "Var Points", false);
			Integer [] types = { PointTypes.ANALOG_POINT, PointTypes.CALCULATED_POINT, PointTypes.DEMAND_ACCUMULATOR_POINT, PointTypes.PULSE_ACCUMULATOR_POINT};
			List points = DaoFactory.getPointDao().getLitePointsBy(types, PointUnits.CAP_CONTROL_VAR_UOMIDS, null,null,null);
			JSFTreeUtils.createPAOTreeFromPointList (points, varTreeData, JSFParamUtil.getYukonUser());
		}	
		return varTreeData;
	}

	public TreeNode getWattTreeData() {
		if (wattTreeData == null){
			wattTreeData =  new TreeNodeBase("root", "Watt Points", false);
            Integer [] types = { PointTypes.ANALOG_POINT, PointTypes.CALCULATED_POINT, PointTypes.DEMAND_ACCUMULATOR_POINT, PointTypes.PULSE_ACCUMULATOR_POINT};
            List points = DaoFactory.getPointDao().getLitePointsBy(types, PointUnits.CAP_CONTROL_WATTS_UOMIDS, null,null,null);
			JSFTreeUtils.createPAOTreeFromPointList (points, wattTreeData, JSFParamUtil.getYukonUser());
		}	
		return wattTreeData;
	}

	public TreeNode getVoltTreeData() {
		if (voltTreeData == null){
			voltTreeData = new TreeNodeBase("root", "Volt Points", false);
			Integer [] types = { PointTypes.ANALOG_POINT, PointTypes.CALCULATED_POINT, PointTypes.DEMAND_ACCUMULATOR_POINT, PointTypes.PULSE_ACCUMULATOR_POINT};
			List points = DaoFactory.getPointDao().getLitePointsBy(types, PointUnits.CAP_CONTROL_VOLTS_UOMIDS, null,null,null);
			JSFTreeUtils.createPAOTreeFromPointList (points, voltTreeData, JSFParamUtil.getYukonUser());
		}	
		return voltTreeData;
	}
	
	private void  resetUOFMTreeData() {
		voltTreeData = null;
		wattTreeData = null;
		varTreeData = null;
	}

	/**
	 * Returns all the unused PAOs for a control point in the system. Only do
	 * this once with each instance of this class
	 */
	private LiteYukonPAObject[] getAllUnusedCCPAOs() {
		if (unusedCCPAOs == null) {
			unusedCCPAOs = DaoFactory.getPaoDao().getAllUnusedCCPAOs(((CapBank) getDbPersistent()).getCapBank().getControlDeviceID());
        }
		return unusedCCPAOs;
	}

	@SuppressWarnings("unchecked")
    public TreeNode getCapBankPoints() {
		TreeNode rootNode = new TreeNodeBase("root", "Devices With Status Points", false);
		if (!(getDbPersistent() instanceof CapBank)) {
			return rootNode;
        }
		LiteYukonPAObject[] tempArr = getAllUnusedCCPAOs();
		List tempList = new ArrayList ();
        for (int i = 0; i < tempArr.length; i++) {
            LiteYukonPAObject object = tempArr[i];
            if (object != null) {
                tempList.add(object);
            }
        }
        LiteYukonPAObject[] lPaos = (LiteYukonPAObject[])tempList.toArray(new LiteYukonPAObject[tempList.size()]);
        Vector typeList = new Vector(32);
		Arrays.sort(lPaos, LiteComparators.litePaoTypeComparator);
		int currType = Integer.MIN_VALUE;
		TreeNodeBase paoTypeNode = null;
		TreeNodeBase[] paoNodes = new TreeNodeBase[lPaos.length];
		for (int i = 0; i < lPaos.length; i++) {
			// only show CapControl speficic PAOs
			if (!PAOGroups.isCapControl(lPaos[i])) {
				continue;
            }
			if (currType != lPaos[i].getType()) {
				paoTypeNode = new TreeNodeBase( "paoTypes", "Type: " + PAOGroups.getPAOTypeString(lPaos[i].getType()), PAOGroups.getPAOTypeString(lPaos[i].getType()), false);
				typeList.add(paoTypeNode);
			}
			paoNodes[i] = new TreeNodeBase("paos", lPaos[i].getPaoName(), String.valueOf(lPaos[i].getYukonID()), false);
			List lPoints = DaoFactory.getPointDao().getLitePointsByPaObjectId(lPaos[i].getYukonID());
			for (int j = 0; j < lPoints.size(); j++) {
				// status points are only allowed in this list
				if ( ((LitePoint)lPoints.get(j)).getPointType() == PointTypes.STATUS_POINT) {
                    TreeNode node = new TreeNodeBase("points", ((LitePoint) lPoints.get(j)).getPointName(), String.valueOf(((LitePoint) lPoints.get(j)).getPointID()), true);
					paoNodes[i].getChildren().add(node);
                }
			}
			// only show PAObjects that have 1 or more points
			if (paoNodes[i].getChildren().size() > 0) {
				paoTypeNode.getChildren().add(paoNodes[i]);
            }
			currType = lPaos[i].getType();
		}
		// this list will be a fixed size with a controlled max value
		// java.util.Collections.sort( typeList, DummyTreeNode.comparator);
		// only show types that have 1 or more points
		for (int i = 0; i < typeList.size(); i++) {
			paoTypeNode = (TreeNodeBase) typeList.get(i);	
			for (int j=0; j < paoTypeNode.getChildCount(); j++) {
				TreeNodeBase pao = (TreeNodeBase) paoTypeNode.getChildren().get(j);
				if (pao.getChildCount() > 100) {
					TreeNode newRoot = new TreeNodeBase("root", "temp root", false);
					newRoot.getChildren().add(pao);
					newRoot = JSFTreeUtils.splitTree(newRoot, 100, "sublevels");
					TreeNodeBase newPAO = (TreeNodeBase) newRoot.getChildren().get(0);
					paoTypeNode.getChildren().set(j, newPAO);					
				}
			}
			if (paoTypeNode.getChildren().size() > 0) {
				rootNode.getChildren().add(paoTypeNode);
            }
		}
		return rootNode;
	}

	public void varPtTeeClick(ActionEvent ae) {
		String val = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("currentVarPointId");
		if (val == null) {
			return;
        }
		if (getDbPersistent() instanceof CapControlFeeder) {
			((CapControlFeeder) getDbPersistent()).getCapControlFeeder().setCurrentVarLoadPointID(new Integer(val));
        } else if (getDbPersistent() instanceof CapControlSubBus) {
			((CapControlSubBus) getDbPersistent()).getCapControlSubstationBus().setCurrentVarLoadPointID(new Integer(val));
            ((CapControlSubBus) getDbPersistent()).getCapControlSubstationBus().setPhaseB(new Integer(val));
            ((CapControlSubBus) getDbPersistent()).getCapControlSubstationBus().setPhaseC(new Integer(val));
        }
	}

	public void twoWayPtsTeeClick(ActionEvent ae) {
		String val = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("ptID");
		if (val == null) {
			return;
        }
		if (getDbPersistent() instanceof CapControlSubBus) {
			((CapControlSubBus) getDbPersistent()).getCapControlSubstationBus().setSwitchPointID(Integer.valueOf(val));
			if (Integer.valueOf(val).intValue() == 0) { 
				setSwitchPointEnabled(false);
            } else {
				setSwitchPointEnabled(true);
            }
		}
		setDualSubBusEdited(true);
	}

	public void selectedTwoWayPointClick(ActionEvent ae) {
	    //for some reason that works better then doin them separately
        selectedAltSubBusClick(ae);
    }

	public void selectedAltSubBusClick(ActionEvent ae){
	    resetCurrentDivOffset();
	    getSelectedTwoWayPointsFormatString();
        resetCurrentAltSubDivOffset(); 
        getSelectedSubBusFormatString();
    }
	
	public void subBusPAOsClick(ActionEvent ae) {
        String val = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("ptID");
        if (val == null) {
            return;
        }
        if (getDbPersistent() instanceof CapControlSubBus) {
            // save the old value
            setOldSubBus(((CapControlSubBus) getDbPersistent()).getCapControlSubstationBus().getSubstationBusID());
            // set the new value
            ((CapControlSubBus) getDbPersistent()).getCapControlSubstationBus().setAltSubPAOId(Integer.valueOf(val));
        }
        //The user fiddled around ...
        setDualSubBusEdited(true);
    }
    
    public Integer getOldSubBus() {
        return oldSubBus;
    }
    
    public void setOldSubBus(Integer oldSubBus) {
        this.oldSubBus = oldSubBus;
    }

	public void wattPtTeeClick(ActionEvent ae) {
		String val = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("ptID");
		if (val == null) {
			return;
        }
		if (getDbPersistent() instanceof CapControlFeeder) {
			((CapControlFeeder) getDbPersistent()).getCapControlFeeder().setCurrentWattLoadPointID(new Integer(val));
        }
		else if (getDbPersistent() instanceof CapControlSubBus) {
			((CapControlSubBus) getDbPersistent()).getCapControlSubstationBus().setCurrentWattLoadPointID(new Integer(val));
        }
	}

	public void voltPtTeeClick(ActionEvent ae) {
		String val = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("ptID");
		if (val == null) {
			return;
        }
		if (getDbPersistent() instanceof CapControlFeeder) {
			((CapControlFeeder) getDbPersistent()).getCapControlFeeder().setCurrentVoltLoadPointID(new Integer(val));
        }
		else if (getDbPersistent() instanceof CapControlSubBus) {
			((CapControlSubBus) getDbPersistent()).getCapControlSubstationBus().setCurrentVoltLoadPointID(new Integer(val));
        }
	}

	public void initItem(int id, int type) {

		DBPersistent dbObj = null;
        
		switch (type) {

    		case DBEditorTypes.EDITOR_CAPCONTROL:
    			dbObj = PAOFactory.createPAObject(id);
    			break;
    
    		case DBEditorTypes.EDITOR_SCHEDULE:
    			dbObj = new PAOSchedule();
    			((PAOSchedule) dbObj).setScheduleID(new Integer(id));
    			break;
                
            case DBEditorTypes.EDITOR_STRATEGY:
                dbObj = new CapControlStrategy();
                ((CapControlStrategy)dbObj).setStrategyID(new Integer(id));
                break;
		}
		setDbPersistent(dbObj);        
		initItem();
	}

	public void initWizard(int paoType) {
		((CBCWizardModel) getWizData()).setWizPaoType(paoType);
		initPanels(paoType);
	}

	/**
	 * Initialize our current DBPersistent object from the databse
	 */
	protected void initItem() {
		if (retrieveDBPersistent() == null) {
			return;
        }
        scheduleId = -1;
		// decide what editor type should be used
		if (getDbPersistent() instanceof YukonPAObject) {
			itemID = ((YukonPAObject) getDbPersistent()).getPAObjectID().intValue();
           if (getDbPersistent() instanceof CapBankController || getDbPersistent() instanceof CapBankController702x) {
               setEditingController( true );
           }
           else {
               setEditingController(false);
           }
           initDataModel(getDbPersistent());
           initPanels(PAOGroups.getPAOType(((YukonPAObject) getDbPersistent()).getPAOCategory(), ((YukonPAObject) getDbPersistent()).getPAOType()));
        } else if (getDbPersistent() instanceof PointBase) {
			itemID = ((PointBase) getDbPersistent()).getPoint().getPointID().intValue();
			initPanels(PointTypes.getType(((PointBase) getDbPersistent()).getPoint().getPointType()));
		} else if (getDbPersistent() instanceof PAOSchedule) {
			itemID = ((PAOSchedule) getDbPersistent()).getScheduleID().intValue();
            initPanels(DBEditorTypes.PAO_SCHEDULE);
		}
        else if (getDbPersistent() instanceof CapControlStrategy) {
            itemID = ((CapControlStrategy)getDbPersistent()).getStrategyID().intValue();
            initPanels(DBEditorTypes.EDITOR_STRATEGY);
        }
		//restore the value of the dual bus from DB
		resetDualBusEnabled();
        if (!(getDbPersistent() instanceof CapControlStrategy)) {
            editingCBCStrategy = false;
		    resetCurrentStratModel();
        }
        initEditorPanels();
	}

    private void resetCurrentStratModel() {
        currentStratModel = null;
    }

    //initiatiates data model for our specific object
	private void initDataModel(DBPersistent dbPersistent) {
        if ((dbPersistent instanceof CapControlArea) || (dbPersistent instanceof CapControlSubBus) || (dbPersistent instanceof CapControlFeeder)) {
            dataModel = DataModelFactory.createModel(dbPersistent);
        }
    }

	public void resetForm() {
		resetStrategies();
		resetCBCEditor();
		resetCurrentDivOffset();
		resetCurrentAltSubDivOffset();
		resetUOFMTreeData();
        resetCapBankEditor();
		isDualSubBusEdited = false;
		editingCBCStrategy = false;
		unassignedBanks = null;
		unassignedFeeders = null;
		unusedCCPAOs = null;
		kwkvarPaos = null;
		kwkvarPoints = null;
		editingController = false;
        assignedStratMap = null;
        scheduleId = -1;

        initItem();
	}

	private void resetCapBankEditor() {
	    if (getDbPersistent() instanceof CapBank) {
	        JSFUtil.resetForm("capBankEditor");
        }
    }

    /**
     * Function that restores the setting of the dual bus ctl
     */
	private void resetDualBusEnabled() {
		if (getDbPersistent() instanceof CapControlSubBus) {
			String dualBusEn = ((CapControlSubBus) getDbPersistent()).getCapControlSubstationBus().getDualBusEnabled();
			Boolean val = (dualBusEn.equalsIgnoreCase("Y") ? Boolean.TRUE : Boolean.FALSE);
			this.setEnableDualBus(val);
		}
	}

	/**
	 * Reset CBC Strategy data, forcing them to be reInited
	 */
	private void resetStrategies() {
//		cbcStrategiesMap = null;
		cbcStrategies = null;
	}

	/**
	 * Reset the CBC device data, forcing them to be reInited
	 */
	private void resetCBCEditor() {
		//setCBControllerEditor(null);
        setEditingController(false);
        getCBControllerEditor().retrieveDB();        
        getCBControllerEditor().resetSerialNumber();
	}

	/**
	 * All possible panels for this editor go here. Set visible panels and
	 * labels based on type of object. DO NOT access the DB persitent object in
	 * this method since it may be null in the case of a Wizard.
	 */
	@SuppressWarnings("unchecked")
    private void initPanels(int paoType) {

		// all panels that are always displayed
		getVisibleTabs().put("General", Boolean.TRUE);

		// all type specifc panels
		getVisibleTabs().put("GeneralPAO", Boolean.TRUE);
        getVisibleTabs().put("CBCArea", Boolean.FALSE);
		getVisibleTabs().put("BaseCapControl", Boolean.FALSE);
		getVisibleTabs().put("CBCSubstation", Boolean.FALSE);
		getVisibleTabs().put("CBCFeeder", Boolean.FALSE);
		getVisibleTabs().put("CBCCapBank", Boolean.FALSE);
		getVisibleTabs().put("CBCType", Boolean.FALSE);
		getVisibleTabs().put("CBCController", Boolean.FALSE);
		getVisibleTabs().put("GeneralSchedule", Boolean.FALSE);
		getVisibleTabs().put("CBCSchedule", Boolean.FALSE);
        getVisibleTabs().put("CBAddInfo", Boolean.FALSE);
        getVisibleTabs().put("CBCStrategy", Boolean.FALSE);

		//here you go ... this is the new era. we decide to create an area
        //We can't call area an area because area already exists in our code.
        //We will call that capcontrolarea
        //Don't confuse area (description of the sub in the yukonpaobject table) with an actual
        //capcontrolarea object
        switch (paoType) {

            case PAOGroups.CAP_CONTROL_AREA:
                setEditorTitle("Substation Area");
                setPaoDescLabel("Substation Area");
                setChildLabel("Substations");
                getVisibleTabs().put("CBCArea", Boolean.TRUE);
                break;
    
            case PAOGroups.CAP_CONTROL_SUBBUS:
    			setEditorTitle("Substation Bus");
    			setPaoDescLabel("Geographical Name");
    			setChildLabel("Feeders");
    			getVisibleTabs().put("CBCSubstation", Boolean.TRUE);
                break;
    
    		case PAOGroups.CAP_CONTROL_FEEDER:
    			setEditorTitle("Feeder");
    			getVisibleTabs().put("CBCFeeder", Boolean.TRUE);
    			setPaoDescLabel(null);
    			setChildLabel("CapBanks");
    			break;
    
    		case PAOGroups.CAPBANK:
    			setEditorTitle("Capacitor Bank");
    			setPaoDescLabel("Street Location");
    			getVisibleTabs().put("CBCCapBank", Boolean.TRUE);
                LiteYukonUser user = JSFUtil.getYukonUser();
                if (user != null) {
                    boolean showCapBankAddInfo = CBCUtils.isCBAdditionalInfoAllowed(user);
                    getVisibleTabs().put ("CBAddInfo", Boolean.TRUE && showCapBankAddInfo);
                }
    			break;
    
    		case PAOGroups.CAPBANKCONTROLLER:
    		case PAOGroups.CBC_FP_2800:
    		case PAOGroups.DNP_CBC_6510:
    		case PAOGroups.CBC_EXPRESSCOM:
    		case PAOGroups.CBC_7010:
            case PAOGroups.CBC_7011:
            case PAOGroups.CBC_7012:
            case PAOGroups.CBC_7020:
            case PAOGroups.CBC_7022:
            case PAOGroups.CBC_7023:
            case PAOGroups.CBC_7024:                         
    			setEditorTitle("CBC");
    			setPaoDescLabel(null);
    			getVisibleTabs().put("CBCType", Boolean.TRUE);
    			getVisibleTabs().put("CBCController", Boolean.TRUE);
    			break;
    
    		case DBEditorTypes.PAO_SCHEDULE:
    			setEditorTitle("Schedule");
    			getVisibleTabs().put("GeneralPAO", Boolean.FALSE);
    			getVisibleTabs().put("GeneralSchedule", Boolean.TRUE);
    			getVisibleTabs().put("CBCSchedule", Boolean.TRUE);
    			break;
                
            case DBEditorTypes.EDITOR_STRATEGY:
                setEditorTitle("Strategy");
                getVisibleTabs().put("GeneralPAO", Boolean.FALSE);
                getVisibleTabs().put("CBCStrategy", Boolean.TRUE);
                break;
    
    		case PointTypes.ANALOG_POINT:
    			break;
    
            case PointTypes.STATUS_POINT:
                break;
    
            default:
    			throw new IllegalArgumentException("Unknown PAO type given, PAO type = " + paoType);
		}
	}

	public DBPersistent getPAOBase() {
        return getDbPersistent();
     }

	public void kwkvarPaosChanged(ValueChangeEvent ev) {
		if (ev == null || ev.getNewValue() == null) {
			return;
        }
		PointLists pLists = new PointLists();
		LitePoint[] lPoints = pLists.getPointsByUofMPAOs(((Integer) ev.getNewValue()).intValue(), PointUnits.CAP_CONTROL_VAR_UOMIDS);
		SelectItem[] temp = new SelectItem[lPoints.length];
		for (int i = 0; i < lPoints.length; i++) {
			temp[i] = new SelectItem(new Integer(lPoints[i].getLiteID()), lPoints[i].getPointName());
        }
		// add the none LitePoint to this list
		kwkvarPoints = new SelectItem[temp.length + 1];
		kwkvarPoints[0] = new SelectItem(new Integer(LitePoint.NONE_LITE_PT.getLiteID()), LitePoint.NONE_LITE_PT.getPointName());
		System.arraycopy(temp, 0, kwkvarPoints, 1, temp.length);
	}

    public void clearfaces() {
        FacesMessage facesMessage = new FacesMessage();
        facesMessage.setDetail("");   
        FacesContext currentInstance = FacesContext.getCurrentInstance();
        currentInstance.addMessage("cti_db_update", facesMessage);
    }
    
	public void update() throws SQLException {
		// this message will be filled in by the super class
		FacesMessage facesMsg = new FacesMessage();
        Connection connection = null;
		try {
			// update the CBCStrategy object if we are editing it
			if (isEditingCBCStrategy() && (! (getDbPersistent() instanceof CapControlStrategy))) {
                CapControlStrategy currentStrategy = ((CapControlStrategyModel) getCurrentStratModel()).getStrategy();
                updateDBObject(currentStrategy, facesMsg);
				// clear out the memory of any list of Strategies
				resetCurrentStratModel();
                resetStrategies();
                setEditingCBCStrategy(false);
			}
			// update the CBC object if we are editing it
			String successfulUpdateMsg = CBCExceptionMessages.DB_UPDATE_SUCCESS;
			facesMsg.setDetail(successfulUpdateMsg);
			if (isEditingController()) {
                try {
                    checkForErrors();
                }
                catch (FormWarningException e) {
                    String errorString = e.getMessage();
                    facesMsg.setDetail(errorString);
                    facesMsg.setSeverity(FacesMessage.SEVERITY_WARN);
                } 
                //if the editing occured outside CBCEditor i.e Cap Bank Editor then update DB with the changes made
                DBPersistent dbPers = getCBControllerEditor().getPaoCBC();
               //creates 2 db change messages - so comment this out updateDBObject(dbPers, facesMsg); if the editing did occur in the CBC Editor then
                //make sure the object is not overwritten later
                if ((getDbPersistent() instanceof CapBankController702x) || (getDbPersistent() instanceof CapBankController)) {
                    YukonPAObject thisObj = (YukonPAObject)getDbPersistent();
                    DeviceBase deviceBase = (DeviceBase)dbPers;
                    deviceBase.setPAOName(thisObj.getPAOName());
                    deviceBase.setDisabled(thisObj.isDisabled());
                    setDbPersistent(dbPers);
                }
        		if (getDbPersistent() instanceof ICapBankController) {
        			setDbPersistent( (YukonPAObject) DeviceTypesFuncs.changeCBCType(PAOGroups.getPAOTypeString( getCBControllerEditor().getDeviceType() ) , (ICapBankController)getDbPersistent()));
    			}
            }
			if ( getDbPersistent() instanceof CapControlSubBus) {
                if (isDualSubBusEdited()) {
                    if (!checkIfDualBusHasValidPoint() && getEnableDualBus().booleanValue()) {
                        // save the old value
                        // reset the model bean to the old value
                        if (getOldSubBus() != null) {
                            ((CapControlSubBus) getDbPersistent()).getCapControlSubstationBus().setAltSubPAOId(getOldSubBus());
                            setOldSubBus(null);
                        }
                        // inform the user they need to have a point picked
                        throw new AltBusNeedsSwitchPointException();
                    }
                }
            }
            if (getDataModel() != null) {
                getDataModel().updateDataModel();    
            }
            if (getDbPersistent().getDbConnection() == null) {
                connection = CBCDBUtil.getConnection();
                getDbPersistent().setDbConnection(connection);
            }
            DBPersistent dbPers = getDbPersistent();
            if (getDbPersistent() instanceof CapControlStrategy) {
                dbPers = ((CapControlStrategyModel) getCurrentStratModel()).getStrategy();
                setEditingCBCStrategy(false);
            }
            updateDBObject(dbPers, facesMsg);
            int paoId = ((YukonPAObject)getDbPersistent()).getPAObjectID();
            seasonScheduleDao.saveSeasonStrategyAssigment(paoId, getAssignedStratMap(), getScheduleId());
		} catch (TransactionException te) {
            String errorString = te.getMessage();
            facesMsg.setDetail(errorString);
            facesMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
		} finally {
			FacesContext.getCurrentInstance().addMessage("cti_db_update", facesMsg);
            CBCDBUtil.closeConnection(connection);
        }
	}

    private boolean checkIfDualBusHasValidPoint() {
        // Integer altSubPAOId;
        Integer switchPointId;
        // get the sub bus currently selected
        if (getDbPersistent() instanceof CapControlSubBus) {
            switchPointId = ((CapControlSubBus) getDbPersistent()).getCapControlSubstationBus().getSwitchPointID();
            if (switchPointId.intValue() > 0) {
            	return true;
            }
        }
        return false;
    }

	private void updateDualBusEnabled() {
		String dualBusCtl = (getEnableDualBus().booleanValue()) ? "Y" : "N";
		((CapControlSubBus) getDbPersistent()).getCapControlSubstationBus().setDualBusEnabled(dualBusCtl);
	}

	/**
	 * Creates extra points or any other supporting object for the given parent
	 * based on the paoType
	 * @param dbObj 
	 * @throws PAODoesntHaveNameException 
	 */
	private void createPostItems(int paoType, int parentID, final FacesMessage facesMsg, DBPersistent dbObj) throws TransactionException {
        SmartMultiDBPersistent smartMulti = new SmartMultiDBPersistent();
		// store the objects we add to the DB
		CBCDBObjCreator cbObjCreator = new CBCDBObjCreator(getWizData());
        if (dbObj instanceof CapBank) {
            createCapBankAdditional(dbObj, facesMsg);
        }
        if  (paoType == CapControlTypes.CAP_CONTROL_FEEDER || paoType == CapControlTypes.CAP_CONTROL_SUBBUS) {
            smartMulti = CBCPointFactory.createPointsForPAO(dbObj);
        } else {
            smartMulti = cbObjCreator.createChildItems(paoType, new Integer(parentID));
        }
		addDBObject(smartMulti, facesMsg);
	}

	/**
	 * Creates extra supporting object(s) for the given parent based on the
	 * paoType
	 * @throws PAODoesntHaveNameException 
	 */
	private void createPreItems(int paoType, DBPersistent dbObj, final FacesMessage facesMsg) throws TransactionException, PAODoesntHaveNameException {
		// store the objects we add to the DB
		CBCDBObjCreator cbObjCreator = new CBCDBObjCreator(getWizData());
        SmartMultiDBPersistent smartMulti = new SmartMultiDBPersistent();
		//work around for a when pao is a sub bus. need when creating points
        if (paoType != PAOGroups.INVALID) {
            smartMulti = cbObjCreator.createParentItems(paoType);
        }
		//make sure we are inserting the right object
		try {			
			Validate.notNull(smartMulti.getOwnerDBPersistent());
			errorCheckOnCreate(smartMulti.getOwnerDBPersistent(), cbObjCreator.getCbcWizardModel().getNestedWizard());
		} catch (IllegalArgumentException nullArg) {
		    //don't do anything since we just want to avoid exception thrown to the user
		} catch (PAODoesntHaveNameException e) {
			throw e;
		}
		addDBObject(smartMulti, facesMsg);
		if  (dbObj instanceof CapBank) {
		    // set the parent to use the newly created supporting items
    		if (((CBCWizardModel) getWizData()).isCreateNested()) {
    			// set the CapBanks ControlDeviceID to be the ID of the CBC we just created
    			YukonPAObject pao = ((YukonPAObject) smartMulti.getOwnerDBPersistent());
                ((CapBank) dbObj).getCapBank().setControlDeviceID(pao.getPAObjectID());
                StatusPoint statusPt;
                if (pao instanceof CapBankController702x) {
                       MultiDBPersistent pointVector = CBCPointFactory.createPointsForCBCDevice(pao);           
                       CBControllerEditor.insertPointsIntoDB(pointVector);  
                       statusPt = (StatusPoint) MultiDBPersistent.getFirstObjectOfType(StatusPoint.class, pointVector);
                } else {
                    //create addtional info find the first status point in our CBC and assign its ID to our CapBank for control purposes
                    statusPt = (StatusPoint) SmartMultiDBPersistent.getFirstObjectOfType(StatusPoint.class, smartMulti);
                }
    			((CapBank) dbObj).getCapBank().setControlPointID(statusPt.getPoint().getPointID());
    		}
        }
    }

	//  create addtional info
    private void createCapBankAdditional(DBPersistent dbObj, final FacesMessage facesMsg) throws TransactionException {
        CapBankAdditional additionalInfo = new CapBankAdditional();
        additionalInfo.setDeviceID(((CapBank)dbObj).getDevice().getDeviceID());
        addDBObject(additionalInfo, facesMsg);
    }

	public String create() {
		// creates the DB object
		FacesMessage facesMsg = new FacesMessage();
        FacesContext facesContext = FacesContext.getCurrentInstance();
		try {
			// if there is a secondaryType set, use that value to creat the PAO
			int paoType = ((CBCWizardModel) getWizData()).getSelectedType();
			DBPersistent dbObj = null;
			int editorType = PAOGroups.INVALID;
			// todo: do this in a better way later
			if (paoType == DBEditorTypes.PAO_SCHEDULE) {
				dbObj = new PAOSchedule();
				((PAOSchedule) dbObj).setDisabled(((CBCWizardModel) getWizData()).getDisabled().booleanValue());
				((PAOSchedule) dbObj).setScheduleName(((CBCWizardModel) getWizData()).getName());
				addDBObject(dbObj, facesMsg);
				itemID = ((PAOSchedule) dbObj).getScheduleID().intValue();
				editorType = DBEditorTypes.EDITOR_SCHEDULE;
			} else {
				dbObj = CCYukonPAOFactory.createCapControlPAO(paoType);
				((YukonPAObject) dbObj).setDisabled(((CBCWizardModel) getWizData()).getDisabled().booleanValue());
				// for CBCs that have a portID with it
				if (DeviceTypesFuncs.cbcHasPort(paoType)) {
                    ICapBankController capBankController = (ICapBankController) dbObj;
                    capBankController.setCommID(getWizData().getPortID());
                }
				createPreItems(paoType, dbObj, facesMsg);
				//make sure we configured the object correctly before we insert it into DB
				errorCheckOnCreate(dbObj, (CBCWizardModel) getWizData());
				addDBObject(dbObj, facesMsg);
				itemID = ((YukonPAObject) dbObj).getPAObjectID().intValue();
				editorType = DBEditorTypes.EDITOR_CAPCONTROL;
			}
			// creates any extra db objects if need be
			createPostItems(paoType, itemID, facesMsg, dbObj);
			facesMsg.setDetail("Database add was SUCCESSFUL");
			// init this form with the newly created DB object wich should be in the cache
			initItem(itemID, editorType);
            //create points for the CBC702x device   
            if (dbObj instanceof CapBankController702x){
                DBPersistent pointVector = CBCPointFactory.createPointsForCBCDevice((YukonPAObject)dbObj);
                CBControllerEditor.insertPointsIntoDB(pointVector);  
            }
            // redirect to this form as the editor for this new DB object
            if (facesContext != null){
                HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
                String url = "/editor/cbcBase.jsf?type="+editorType+"&itemid="+itemID;
                CBCNavigationUtil.bookmarkLocationAndRedirect(url, session);
            }
            return "cbcEditor";
		} 
		catch (PAODoesntHaveNameException noNameE){
			facesMsg.setDetail(noNameE.getMessage());
			facesMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
		}
		catch (TransactionException te) {
			// do nothing since the appropriate actions was taken in the super
		} finally {
            if (facesContext != null){
                facesContext.addMessage("cti_db_add", facesMsg);
            }
        }
		return ""; // go nowhere since this action failed
	}

	/**
	 * @param dbObj
	 * @param wizData 
	 * @throws PAODoesntHaveNameException
	 */
	private void errorCheckOnCreate(DBPersistent dbObj, CBCWizardModel wizData) throws PAODoesntHaveNameException {
		if (!((CBCWizardModel) getWizData()).getName().equalsIgnoreCase("")){
			((YukonPAObject) dbObj).setPAOName(wizData.getName());			
		}
		else {		
			throw new PAODoesntHaveNameException();
		}
	}

	@SuppressWarnings("unchecked")
    public void showScanRate(ValueChangeEvent ev) {
		if (ev == null || ev.getNewValue() == null) {
			return;
        }
		Boolean isChecked = (Boolean) ev.getNewValue();
		// find out if this device is TwoWay (used for 2 way CBCs)
        if (isControllerCBC() && getCBControllerEditor().isTwoWay()) {
			TwoWayDevice twoWayDev = (TwoWayDevice) getCBControllerEditor().getPaoCBC();
			String type = ev.getComponent().getId().equalsIgnoreCase("scanIntegrityChk") ? DeviceScanRate.TYPE_INTEGRITY
				: (ev.getComponent().getId().equalsIgnoreCase("scanExceptionChk") ? DeviceScanRate.TYPE_EXCEPTION : "");
			if (isChecked.booleanValue()) {
				twoWayDev.getDeviceScanRateMap().put(type, new DeviceScanRate(getCBControllerEditor().getPaoCBC().getPAObjectID(), type));
			} else {
				twoWayDev.getDeviceScanRateMap().remove(type);
			}
		}
	}

	public ICBControllerModel getCBControllerEditor() {
		if (cbControllerEditor == null) {
            int paoId = itemID;			
            if ((getDbPersistent() instanceof CapBankController702x) || (getDbPersistent() instanceof CapBankController)){
                setEditingController(true);
                cbControllerEditor = new CBControllerEditor(paoId);
            }else{
                cbControllerEditor = new CBControllerEditor();
            }
        }
		return cbControllerEditor;
	}

	public void setCBControllerEditor(ICBControllerModel cbCntrlEditor) {
		cbControllerEditor = cbCntrlEditor;
	}

	public boolean isEditingController() {
		return editingController;
	}

	public void setEditingController(boolean val) {
		editingController = val;
	}

	public void createStrategy() {
        currentStratModel = null;
        CapControlStrategy ccStrat = CCYukonPAOFactory.createCapControlStrategy();
		Integer newID = CapControlStrategy.getNextStrategyID();
		ccStrat.setStrategyID(newID);
		ccStrat.setStrategyName("Strat #" + newID + " (New)");
		// this message will be filled in by the super class
		FacesMessage facesMsg = new FacesMessage();
		try {
			addDBObject(ccStrat, facesMsg);
			// clear out the memory of the any list of Strategies
			resetStrategies();
			setEditingCBCStrategy(true);
			facesMsg.setDetail("CapControl Strategy add was SUCCESSFUL");
		} catch (TransactionException te) {
			// do nothing since the appropriate actions was taken in the super
		} finally {
			FacesContext.getCurrentInstance().addMessage("cti_db_add", facesMsg);
		}
	}

	public void deleteStrategy() {
		// this message will be filled in by the super class
		FacesMessage facesMsg = new FacesMessage();
		try {
			// cancel any editing of the Strategy we may have been doing
			setEditingCBCStrategy(false);
			int stratID = CtiUtilities.NONE_ZERO_ID;
            if (getDbPersistent() instanceof CapControlStrategy) {
                stratID = ((CapControlStrategy)getDbPersistent()).getStrategyID().intValue();
            }
			// decide if we need to do any special handling of this transaction based on what other PAOs use this Strategy
			int[] paos = CapControlStrategy.getAllPAOSUsingStrategy(stratID, itemID);
			if (paos.length <= 0) {
				// update the current PAOBase object just in case it uses the strategy we are deleting
				updateDBObject(getDbPersistent(), null);
				deleteDBObject(getCbcStrategiesMap().get(new Integer(stratID)), facesMsg);
				// clear out the memory of the any list of Strategies
				resetStrategies();
				facesMsg.setDetail("CapControl Strategy delete was SUCCESSFUL");
			} else {
				StringBuffer items = new StringBuffer("");
				for (int i = 0; i < paos.length; i++) {
					items.append(DaoFactory.getPaoDao().getYukonPAOName(paos[i]) + ",");
                }
				facesMsg.setDetail("Unable to delete the Strategy since the following items use it: " + items.deleteCharAt(items.length() - 1));
				facesMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
			}
		} catch (TransactionException te) {
			facesMsg.setDetail("Unable to delete the Strategy: " + te.getMessage());
            facesMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
		} finally {
			FacesContext.getCurrentInstance().addMessage("cti_db_delete", facesMsg);
		}
	}

	public String getPAODescLabel() {
		return paoDescLabel;
	}

	public String getParent() {
		int parentID = CtiUtilities.NONE_ZERO_ID;
		if (getDbPersistent() instanceof CapControlFeeder) {
			parentID = com.cannontech.database.db.capcontrol.CapControlFeeder.getParentSubBusID(itemID);
        } else if (getDbPersistent() instanceof CapBank) {
			parentID = com.cannontech.database.db.capcontrol.CapBank.getParentFeederID(itemID);
        }
		if (parentID == CtiUtilities.NONE_ZERO_ID) {
			return CtiUtilities.STRING_NONE;
        } else {
			LiteYukonPAObject parentPAO = DaoFactory.getPaoDao().getLiteYukonPAO(parentID);
			return parentPAO.getPaoName() + "   (" + PAOGroups.getPAOTypeString(parentPAO.getType()) + ",  id: " + parentPAO.getLiteID() + ")";
		}
	}

	@SuppressWarnings("unchecked")
    public void treeSwapAddAction() {
		FacesContext context = FacesContext.getCurrentInstance();
		Map paramMap = context.getExternalContext().getRequestParameterMap();
		String swapType = (String) paramMap.get("swapType");
		int elemID = new Integer((String) paramMap.get("id")).intValue();
		if ("CapBank".equalsIgnoreCase(swapType)) {
			// a table that swaps CapBanks, must be for a Feeder object
			if (unassignedBanks != null) {
				for (int i = 0; i < unassignedBanks.size(); i++) {
					if (elemID == ((LiteYukonPAObject) unassignedBanks.get(i)).getLiteID()) {
						// Add the mapping for the given CapBank id to this Feeder
						CapControlFeeder currFdr = (CapControlFeeder) getDbPersistent();
                        int order = maxDispOrderOnList (currFdr.getChildList()) + 1;
                        CCFeederBankList bankList = new CCFeederBankList(itemID,elemID,order,order,order);
                        currFdr.getChildList().add(bankList);
                        updateTripOrder (currFdr);
						unassignedBanks.remove(i);
						break;
					}
				}
			}
		} else if ("Feeder".equalsIgnoreCase(swapType)) {
			// a table that swaps Feeders, must be for a SubBus object
			if (unassignedFeeders != null) {
				for (int i = 0; i < unassignedFeeders.size(); i++) {
					if (elemID == ((LiteYukonPAObject) unassignedFeeders.get(i)).getLiteID()) {
						// Add the mapping for the given Feeders id to this Sub
						CapControlSubBus currSub = (CapControlSubBus) getDbPersistent();
                        CCFeederSubAssignment sa = new CCFeederSubAssignment(elemID,itemID,maxDispOrderOnList (currSub.getChildList())+ 1); 
						currSub.getChildList().add(sa);
						unassignedFeeders.remove(i);
						break;
					}
				}
			}
		}
	}

    /**
     *iterates through the bank list and reverses the trip order for the cap 
	 */
    @SuppressWarnings("unchecked")
    private void updateTripOrder(CapControlFeeder currFdr) {
        ArrayList<CCFeederBankList> childList = currFdr.getChildList();
        for (Iterator iter = childList.iterator(); iter.hasNext();) {
            CCFeederBankList assign = (CCFeederBankList) iter.next();
            assign.setTripOrder(childList.size() + 1 - assign.getControlOrder());
        }
    }

    private void reorderList(ArrayList childList) {
		for (int i = 0; i < childList.size(); i++) {
			Object element = childList.get(i);
			if (element instanceof CCFeederSubAssignment) {
				CCFeederSubAssignment feeder = (CCFeederSubAssignment) element;
				feeder.setDisplayOrder(new Integer ( i + 1));
			} else if (element instanceof CCFeederBankList) {
				CCFeederBankList capBank = (CCFeederBankList) element;
				capBank.setControlOrder(new Integer ( i  + 1));
                capBank.setCloseOrder(new Integer ( i + 1));
                capBank.setTripOrder(new Integer ( i + 1));
			} else {
				return;
            }
		}		
	}

	private int maxDispOrderOnList(ArrayList childList) {
		int max = 0;
		for (Iterator iter = childList.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (element instanceof CCFeederSubAssignment) {
				CCFeederSubAssignment feeder = (CCFeederSubAssignment) element;
				if (feeder.getDisplayOrder().intValue() > max) {
					max = feeder.getDisplayOrder().intValue();
				}
			} else if (element instanceof CCFeederBankList) {
				CCFeederBankList capBank = (CCFeederBankList) element;
				if (capBank.getControlOrder().intValue() > max) {
					max = capBank.getControlOrder().intValue();
				}
			} else {
				return 0;
            }
		}	
		return max;
	}

	@SuppressWarnings("unchecked")
    public void treeSwapRemoveAction() {
		FacesContext context = FacesContext.getCurrentInstance();
		Map paramMap = context.getExternalContext().getRequestParameterMap();
		String swapType = (String) paramMap.get("swapType");
		int elemID = new Integer((String) paramMap.get("id")).intValue();
		if ("CapBank".equalsIgnoreCase(swapType)) {
			// a table that swaps CapBanks, must be for a Feeder object
			CapControlFeeder currFdr = (CapControlFeeder) getDbPersistent();
			for (int i = 0; i < currFdr.getChildList().size(); i++) {
				CCFeederBankList listItem = (CCFeederBankList) currFdr.getChildList().get(i);
				if (elemID == listItem.getDeviceID().intValue()) {
					// remove the mapping for the given CapBank id to this Feeder
					currFdr.getChildList().remove(i);
					unassignedBanks.add(DaoFactory.getPaoDao().getLiteYukonPAO(elemID));
					// keep our order
					Collections.sort(unassignedBanks, LiteComparators.liteStringComparator);
					reorderList (currFdr.getChildList());
					break;
				}
			}
		} else if ("Feeder".equalsIgnoreCase(swapType)) {
			// a table that swaps Feeders, must be for a SubBus object
			CapControlSubBus currSub = (CapControlSubBus) getDbPersistent();
			for (int i = 0; i < currSub.getChildList().size(); i++) {
				CCFeederSubAssignment listItem = (CCFeederSubAssignment) currSub.getChildList().get(i);
				if (elemID == listItem.getFeederID().intValue()) {
					// remove the mapping for the given Feeder id to this SubBus
					currSub.getChildList().remove(i);
					unassignedFeeders.add(DaoFactory.getPaoDao().getLiteYukonPAO(elemID));
					// keep our order
					Collections.sort(unassignedFeeders, LiteComparators.liteStringComparator);
					reorderList (currSub.getChildList());
					break;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
    public void initEditorPanels() {
		if (getDbPersistent() instanceof CapControlFeeder) {
			com.cannontech.database.db.capcontrol.CapControlFeeder capControlFeeder = ((CapControlFeeder) getDbPersistent()).getCapControlFeeder();
			int fdrVarPtID = capControlFeeder.getCurrentVarLoadPointID().intValue();
			LitePoint litePoint = DaoFactory.getPointDao().getLitePoint(fdrVarPtID);
			if (litePoint != null) {
				int paobjectID = litePoint.getPaobjectID();
				kwkvarPaosChanged(new ValueChangeEvent(DUMMY_UI, null, new Integer(paobjectID)));
			}
		} else if (getDbPersistent() instanceof CapControlSubBus) {
			int varPtID = ((CapControlSubBus) getDbPersistent()).getCapControlSubstationBus().getCurrentVarLoadPointID().intValue();
			LitePoint litePoint = DaoFactory.getPointDao().getLitePoint(varPtID);
			if (litePoint != null) {
				if (varPtID > CtiUtilities.NONE_ZERO_ID) {
					kwkvarPaosChanged(new ValueChangeEvent(DUMMY_UI, null, new Integer(litePoint.getPaobjectID())));
                }
			}
		}

		unassignedBanks = new Vector(16);
		int[] unassignedBankIDs = com.cannontech.database.db.capcontrol.CapBank.getUnassignedCapBankIDs();

		for (int i = 0; i < unassignedBankIDs.length; i++) {
			LiteYukonPAObject liteYukonPAO = DaoFactory.getPaoDao().getLiteYukonPAO(unassignedBankIDs[i]);
			if (liteYukonPAO != null) {
				unassignedBanks.add(liteYukonPAO);
            }
		}
		Collections.sort(unassignedBanks, LiteComparators.liteStringComparator);
		unassignedFeeders = new Vector(16);
		int[] unassignedFeederIDs = com.cannontech.database.db.capcontrol.CapControlFeeder.getUnassignedFeederIDs();
		for (int i = 0; i < unassignedFeederIDs.length; i++) {
			unassignedFeeders.add(DaoFactory.getPaoDao().getLiteYukonPAO(unassignedFeederIDs[i]));
		}
		Collections.sort(unassignedFeeders, LiteComparators.liteStringComparator);
	}

	public void setPaoDescLabel(String string) {
		paoDescLabel = string;
	}

	public boolean isEditingCBCStrategy() {
		return editingCBCStrategy;
	}

	public void setEditingCBCStrategy(boolean b) {
		editingCBCStrategy = b;
	}

	public SelectItem[] getTimeInterval() {
		return CBCSelectionLists.TIME_INTERVAL;
	}

	public SelectItem[] getScheduleRepeatTime() {
		return CBCSelectionLists.getTimeSubList(300);
	}

	public List getUnassignedBanks() {
		return unassignedBanks;
	}

	public List getUnassignedFeeders() {
		return unassignedFeeders;
	}

	public String getChildLabel() {
		return childLabel;
	}

	public void setChildLabel(String string) {
		childLabel = string;
	}

	public void setStratDaysOfWeek(String[] newDaysOfWeek) {

		CapControlStrategy strat = getCbcStrategiesMap().get(new Integer(getCurrentStrategyID()));

		if (strat == null || newDaysOfWeek == null) {
			return;
        }
		StringBuffer buff = new StringBuffer("NNNNNNNN");
		for (int i = 0; i < newDaysOfWeek.length; i++) {
			buff.setCharAt(Integer.parseInt(newDaysOfWeek[i]), 'Y');
		}
		strat.setDaysOfWeek(buff.toString());
	}

	@SuppressWarnings("unchecked")
    public String[] getStratDaysOfWeek() {

		CapControlStrategy strat = getCbcStrategiesMap().get(new Integer(getCurrentStrategyID()));

		if (strat == null) {
			return new String[0];
        }
		Vector retList = new Vector(8);
		for (int i = 0; i < strat.getDaysOfWeek().length(); i++) {
			if (strat.getDaysOfWeek().charAt(i) == 'Y') {
				retList.add(String.valueOf(i));
            }
		}

		String[] strArray = new String[retList.size()];
		return (String[]) retList.toArray(strArray);
	}

	public boolean isControllerCBC() {
    	if (getDbPersistent() instanceof CapBank) {
    		int controlPointId = ((CapBank) getDbPersistent()).getCapBank().getControlPointID().intValue();
    		LitePoint litePoint = DaoFactory.getPointDao().getLitePoint(controlPointId);
    		if (litePoint != null) {
    			int paoID = litePoint.getPaobjectID();
    			if (paoID != CtiUtilities.NONE_ZERO_ID) {
    				return DeviceTypesFuncs.isCapBankController(DaoFactory.getPaoDao().getLiteYukonPAO(paoID).getType());
                }
    		}
    	} else if (getDbPersistent() instanceof YukonPAObject){
            // support for the TwoWay devices
            int paoID = ((YukonPAObject)getDbPersistent()).getPAObjectID().intValue();
            return DeviceTypesFuncs.isCapBankController(DaoFactory.getPaoDao().getLiteYukonPAO(paoID));
       }
       return false;
	}

	public boolean isBankControlPtVisible() {
		if (getDbPersistent() instanceof CapBank) {
			return !CapBank.FIXED_OPSTATE.equals(((CapBank) getDbPersistent()).getCapBank().getOperationalState());
		} else {
			return false;
        }
	}

	@SuppressWarnings("unchecked")
    public void addSchedule() {
		if (!(getDbPersistent() instanceof CapControlSubBus)) {
			return;
        }
		CapControlSubBus subBus = (CapControlSubBus) getDbPersistent();
		if (subBus.getSchedules().size() < PAOScheduleAssign.MAX_SHEDULES_PER_PAO) {
			PAOScheduleAssign paoSched = new PAOScheduleAssign();
			paoSched.setPaoID(subBus.getPAObjectID());
			subBus.getSchedules().add(paoSched);
		}
	}

	public CBCCreationModel getWizData() {
		if (wizData == null) {
			wizData = new CBCWizardModel();
        }
		return wizData;
	}

	public boolean isVoltageControl() {
		if (getCurrentStrategyID() != CtiUtilities.NONE_ZERO_ID) {
			CapControlStrategy strat = getCbcStrategiesMap().get(new Integer(getCurrentStrategyID()));
			return strat != null && CalcComponentTypes.LABEL_VOLTS.equals(strat.getControlUnits());
		} else {
			return false;
        }
	}

	@SuppressWarnings("unchecked")
    public LiteYukonPAObject[] getSubBusList() {
		if (subBusList == null) {
			//subBusList = PAOFuncs.getAllCapControlSubBuses();			
			subBusList = DaoFactory.getCBCDao().getAllSubsForUser (JSFParamUtil.getYukonUser());
		}
		return (LiteYukonPAObject[]) subBusList.toArray(new LiteYukonPAObject[subBusList.size()]);
	}

	public TreeNode getSwitchPointList() {
	    TreeNode rootData = new TreeNodeBase("root","Switch Points", false);
        Set points = PointLists.getAllTwoStateStatusPoints();
        rootData = JSFTreeUtils.createPAOTreeFromPointList(points, rootData, JSFParamUtil.getYukonUser());
        return rootData;
    }
    
	@SuppressWarnings("unchecked")
    public String getSelectedSubBusFormatString() {
        
		String retString = new String();
		Integer subPAOid = null;
        
		if (getDbPersistent() instanceof CapControlSubBus) {
			subPAOid = ((CapControlSubBus) getDbPersistent()).getCapControlSubstationBus().getAltSubPAOId();
			// get the pao name
			LiteYukonPAObject liteDevice = DaoFactory.getPaoDao().getLiteYukonPAO(subPAOid.intValue());
			retString = new String(liteDevice.getPaoName());
		}
		// Every time the page loads we need to keep track of the
		// currently selected value. We index the current value
		// from the total list
		if (this.subBusList != null && subPAOid != null) {
			for (int i = 0; i < this.subBusList.size(); i++) {
				LiteYukonPAObject device = (LiteYukonPAObject) 
				this.subBusList.get(i);
				if (device.getLiteID() == subPAOid.intValue()) {
				    offsetMap.put("selectedSubBus", "" + i);
                }
            }
		}
		return retString;
	}

    @SuppressWarnings("unchecked")
    public String getSelectedTwoWayPointsFormatString() {
		String retString = new String(" ");
		Integer pointId = null;

		//variables to hold state info to expand the nodes
		TreeModelBase model = (TreeModelBase) getTreeModelData();
		TreeStateBase state = (TreeStateBase) model.getTreeState();

		if (getDbPersistent() instanceof CapControlSubBus) {
			pointId = ((CapControlSubBus) getDbPersistent()).getCapControlSubstationBus().getSwitchPointID();
		}
		TreeNode tn = this.getSwitchPointList();
		List paos = tn.getChildren();
      
		for (int i = 0; i < paos.size(); i++) {
			TreeNodeBase pao = (TreeNodeBase) paos.get(i);
			String paoName = pao.getDescription();
			List points = pao.getChildren();
			for (int j = 0; j < points.size(); j++) {
				TreeNodeBase point = (TreeNodeBase) points.get(j);
				if (pointId.equals(new Integer(point.getIdentifier()))) {
					retString = retString + paoName + "/" + point.getDescription();
					//code to expand the selected node
                    //currently TreeModelBase class accepts colon delimited
                    //string as the node path. 0 would be the root and the
                    //rest of the nodes would be realtive to the root such as
                    //0:1: 0:0 would be identify 2 leaf nodes in the binary tree
					String nodeId = "0:" + i;
					state.expandPath(model.getPathInformation(nodeId));
					model.setTreeState(state);
					
                    //set the index of the switch point selected
					offsetMap.put("selectedSwitchPoint", "" + i);
                }
			}
		}
		return retString;
	}

	//binding to the tree model. used to expand selected node 
    private TreeModel getTreeModelData() {
		TreeModelBase tree = (TreeModelBase) getDualBusSwitchPointTree().getDataModel();
		return tree;
	}

	public HtmlTree getDualBusSwitchPointTree() {
		return dualBusSwitchPointTree;
	}

	public void setDualBusSwitchPointTree(HtmlTree tree) {
		this.dualBusSwitchPointTree = tree;
	}

    public Boolean getEnableDualBus() {
        return enableDualBus;
    }

    public void setEnableDualBus(Boolean enableDualBus) {
        this.enableDualBus = enableDualBus;
        updateDualBusEnabled();         
        setDualSubBusEdited(true);
    }
    
	@SuppressWarnings("unchecked")
    private void resetCurrentDivOffset() {
        offsetMap.put("currentTwoWayPointDivOffset","0");
	}

	@SuppressWarnings("unchecked")
    private void resetCurrentAltSubDivOffset ()  {
		offsetMap.put("currentAltSubDivOffset","0");
	}
	
	public boolean isDualSubBusEdited() {
		return isDualSubBusEdited;
	}

	public void setDualSubBusEdited(boolean isDualSubBusEdited) {
		this.isDualSubBusEdited = isDualSubBusEdited;
	}

    public Map getOffsetMap() {
        return offsetMap;
    }

    public void setOffsetMap(Map offsetMap) {
        this.offsetMap = offsetMap;
    }

    public int getSelectedPanelIndex() {
        if (isEditingController()) {
           if (getDbPersistent() instanceof CapBankController || getDbPersistent() instanceof CapBankController702x) {
               return CBCSelectionLists.CapBankControllerSetup;
           }
        } else if (getDbPersistent() instanceof CapBank) {
            return CBCSelectionLists.CapBankSetup;    
        } else if (getDbPersistent() instanceof CapControlSubBus) {
            return CBCSelectionLists.CapControlSubBusSetup;
        } else if (getDbPersistent() instanceof CapControlFeeder) {
           	return CBCSelectionLists.CapControlFeederCapBank;
        }else if (getDbPersistent() instanceof CapControlStrategy) {
            return CBCSelectionLists.CapControlStrategyEditor;
        }
        return CBCSelectionLists.General;
    }
         
    protected void checkForErrors() throws PortDoesntExistException, MultipleDevicesOnPortException, SameMasterSlaveCombinationException, SerialNumberExistsException, SQLException { 
        if (getDbPersistent() != null){
			getCBControllerEditor().checkForErrors();
		}
    }
    
    public String getPaoName() {
    	String retStr = "";
    	if (getDbPersistent() != null) {
	    	if (getDbPersistent() instanceof YukonPAObject) {
	        	retStr = ((YukonPAObject)getDbPersistent()).getPAOName();
	        } else if (getDbPersistent() instanceof PAOSchedule) {
	        	if (getDbPersistent() != null) {
	            	retStr = ((PAOSchedule)getDbPersistent()).getScheduleName();
	        	}
	        }
    	}
        return retStr;
    }
    
    @SuppressWarnings("unchecked")
    public LitePoint[] getCapBankPointList() {
        List temp = DaoFactory.getPointDao().getLitePointsByPaObjectId(((YukonPAObject)getDbPersistent()).getPAObjectID().intValue());
        return (LitePoint[])temp.toArray(new LitePoint [temp.size()]);        
    }
    
    public void capBankPointClick (ActionEvent ae){
        FacesMessage fm = new FacesMessage();
        try {
            //make sure the point form will have the pao id
            //of the cbc 
            String red = "pointBase.jsf?parentId=" + ((YukonPAObject)getDbPersistent()).getPAObjectID().toString() + "&itemid=";
            String val = JSFParamUtil.getJSFReqParam("ptID");
            String location = red + val;
            //bookmark the current page
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            CBCNavigationUtil.bookmarkLocationAndRedirect(location, session);
            FacesContext.getCurrentInstance().getExternalContext().redirect(location);
            FacesContext.getCurrentInstance().responseComplete();
        } 
        catch (IOException e) {
            fm.setDetail("ERROR - Couldn't redirect. CBControllerEditor:pointClick. " + e.getMessage());
        } catch (Exception e) {
            //add some code to handle null session exception
        }
        finally{
            if(fm.getDetail() != null) {
                FacesContext.getCurrentInstance().addMessage("point_click", fm);
            }
        }
    }

	public boolean isSwitchPointEnabled() {
		return switchPointEnabled;
	}

	public void setSwitchPointEnabled(boolean switchPointEnabled) {
		this.switchPointEnabled = switchPointEnabled;
	}
	
	//delegate to this class because generic class doesn't have to know about business rules	
	public SelectItem[]  getControlMethods () {
    	String algorithm = getCbcStrategiesMap().get( new Integer ( getCurrentStrategyID() )).getControlUnits();
    	if (algorithm.equalsIgnoreCase(CalcComponentTypes.LABEL_MULTI_VOLT)) {
    		controlMethods = new SelectItem [2];
    		controlMethods[0] = new SelectItem(CapControlStrategy.CNTRL_INDIVIDUAL_FEEDER, StringUtils.addCharBetweenWords( ' ', CapControlStrategy.CNTRL_INDIVIDUAL_FEEDER));
    		controlMethods[1] = new SelectItem(CapControlStrategy.CNTRL_SUBSTATION_BUS, StringUtils.addCharBetweenWords( ' ', CapControlStrategy.CNTRL_SUBSTATION_BUS));
    	} else {
    		controlMethods = new CBCSelectionLists().getCbcControlMethods();
        }
    	return controlMethods;
	}
    
    @SuppressWarnings("unchecked")
    public Map getPointNameMap () {
        if(pointNameMap == null) {
            pointNameMap = new HashMap();
            int varPoint = getControlPoint (PointUnits.UOMID_KVAR);
            int voltPoint = getControlPoint (PointUnits.UOMID_KVOLTS);
            int wattPoint = getControlPoint (PointUnits.UOMID_KW);
            int switchPointID = PointTypes.SYS_PID_SYSTEM;
            pointNameMap.put(switchPointID, "(none)");
            if (getDbPersistent() instanceof CapControlSubBus) {
                CapControlSubBus sub = (CapControlSubBus) getDbPersistent();
                CapControlSubstationBus subBus = sub.getCapControlSubstationBus();
                switchPointID = subBus.getSwitchPointID();
                
                int phaseBPoint = subBus.getPhaseB();
                int phaseCPoint = subBus.getPhaseC();
                pointNameMap.put(phaseBPoint, DaoFactory.getPointDao().getPointName(phaseBPoint));
                pointNameMap.put(phaseCPoint, DaoFactory.getPointDao().getPointName(phaseCPoint));
            }
            pointNameMap.put(varPoint, DaoFactory.getPointDao().getPointName(varPoint));
            pointNameMap.put(wattPoint, DaoFactory.getPointDao().getPointName(wattPoint));            
            pointNameMap.put(voltPoint, DaoFactory.getPointDao().getPointName(voltPoint));
            pointNameMap.put(0, DaoFactory.getPointDao().getPointName(0));
            if (switchPointID != PointTypes.SYS_PID_SYSTEM) {
                pointNameMap.put(switchPointID, DaoFactory.getPointDao().getPointName(switchPointID));
            }
        }
        return pointNameMap;
     }
     
    @SuppressWarnings("cast")
    public void setPointNameMap (Map m) {
         pointNameMap = (HashMap) m;
     }

    @SuppressWarnings("unchecked")
    public Map getPaoNameMap () {
        if(paoNameMap == null) {
            paoNameMap = new HashMap();
            int varPoint = getControlPoint (PointUnits.UOMID_KVAR);
            int wattPoint = getControlPoint(PointUnits.UOMID_KW);
            int voltPoint = getControlPoint(PointUnits.UOMID_KVOLTS);
            
            paoNameMap.put(varPoint, DaoFactory.getPaoDao().getYukonPAOName(DaoFactory.getPointDao().getLitePoint(varPoint).getPaobjectID()));
            paoNameMap.put(wattPoint, DaoFactory.getPaoDao().getYukonPAOName(DaoFactory.getPointDao().getLitePoint(wattPoint).getPaobjectID()));
            paoNameMap.put(voltPoint, DaoFactory.getPaoDao().getYukonPAOName(DaoFactory.getPointDao().getLitePoint(voltPoint).getPaobjectID()));
            paoNameMap.put(0, DaoFactory.getPaoDao().getYukonPAOName(DaoFactory.getPointDao().getLitePoint(0).getPaobjectID()));
            if(getDbPersistent() instanceof CapControlSubBus) {
                CapControlSubstationBus sub = ((CapControlSubBus) getPAOBase()).getCapControlSubstationBus();
                int phaseBPoint = sub.getPhaseB();
                int phaseCPoint = sub.getPhaseC();
                paoNameMap.put(phaseBPoint, DaoFactory.getPaoDao().getYukonPAOName(DaoFactory.getPointDao().getLitePoint(varPoint).getPaobjectID()));
                paoNameMap.put(phaseCPoint, DaoFactory.getPaoDao().getYukonPAOName(DaoFactory.getPointDao().getLitePoint(wattPoint).getPaobjectID()));
            }
        }
        return paoNameMap;
     }
     
    @SuppressWarnings("cast")
    public void setPaoNameMap (Map m) {
         paoNameMap = (HashMap) m;
     }

    private int getControlPoint(int uomid) {
        int pointID = 0;
        if (getPAOBase() instanceof CapControlSubBus) {
            CapControlSubstationBus sub = ((CapControlSubBus) getPAOBase()).getCapControlSubstationBus();
            switch (uomid) {
                case PointUnits.UOMID_KVAR:
                    pointID = sub.getCurrentVarLoadPointID();
                    break;                        
                case PointUnits.UOMID_KW:
                    pointID = sub.getCurrentWattLoadPointID();
                    break;
                case PointUnits.UOMID_KVOLTS: 
                    pointID = sub.getCurrentVoltLoadPointID();
                    break;
            }
        }
        if (getPAOBase() instanceof CapControlFeeder) {
            com.cannontech.database.db.capcontrol.CapControlFeeder feeder = ((CapControlFeeder) getPAOBase()).getCapControlFeeder();
            switch (uomid) {
                case PointUnits.UOMID_KVAR:
                    pointID = feeder.getCurrentVarLoadPointID();
                    break;                        
                case PointUnits.UOMID_KW:
                    pointID = feeder.getCurrentWattLoadPointID();
                    break;
                case PointUnits.UOMID_KVOLTS: 
                    pointID = feeder.getCurrentVoltLoadPointID();
                    break;
            }
        }
        return pointID;
    }

    public void paoClick(ActionEvent ae){
        FacesMessage fm = new FacesMessage();
        try {
            //go to the next page
            String path = "/editor/cbcBase.jsf";
            String itemId = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("paoID");
            String type = "" + DBEditorTypes.EDITOR_CAPCONTROL;
            String query = "?type=" + type + "&" + "itemid=" + itemId;
            String location = path + query;                                       
            //bookmark the current page
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            CBCNavigationUtil.bookmarkLocationAndRedirect(location,session);
            FacesContext.getCurrentInstance().getExternalContext().redirect(location);            
            FacesContext.getCurrentInstance().responseComplete();            
        } catch (IOException e) {
            fm.setDetail("ERROR - Couldn't redirect. PointForm:paoClick. " + e.getMessage());
        } catch (Exception e) {
            //code to handle null session
        }
        finally{
            if(fm.getDetail() != null) {
                FacesContext.getCurrentInstance().addMessage("pao_click", fm);
            }
        }
    }
    
    public void usePhaseDataClicked (ValueChangeEvent vce) {
        
    }

    public EditorDataModel getDataModel() {
       if (dataModel == null) {
           if (getDbPersistent() != null) {
               initDataModel(getDbPersistent());
           }
       }
        return dataModel;
    }

    public void setDataModel(EditorDataModel dataModel) {
        this.dataModel = dataModel;
    }
    
    public List<CapControlStrategy> getAllCBCStrats() {
        CapControlStrategy[] allCBCStrategies = CapControlStrategy.getAllCBCStrategies();
        List<CapControlStrategy> allStratList = new ArrayList<CapControlStrategy>();
        for (int i = 0; i < allCBCStrategies.length; i++) {
            CapControlStrategy strategy = allCBCStrategies[i];
            if (strategy.getStrategyID().intValue() > 0) {
                allStratList.add(strategy);
            }
        }
        return allStratList;
    }
    
    public void editCBCStrat( ActionEvent ev ) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map paramMap = context.getExternalContext().getRequestParameterMap();
        int elemID = Integer.parseInt( (String)paramMap.get("stratID") );
        try {
            String location = "/editor/cbcBase.jsf?type=5&itemid="+elemID;
            context.getExternalContext().redirect(location);
        } catch (IOException e) {
            CTILogger.error(e);
        }            
        FacesContext.getCurrentInstance().responseComplete();            
    }
    
    public void deleteStrat( ActionEvent ev ) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map paramMap = context.getExternalContext().getRequestParameterMap();
        int elemID = Integer.parseInt( (String)paramMap.get("stratID") );
        initItem(elemID, DBEditorTypes.EDITOR_STRATEGY);
        deleteStrategy();
    }

    public void setScheduleId(Integer id) {
        this.scheduleId = id;
    }
    
    public void setSeasonScheduleDao(SeasonScheduleDao seasonScheduleDao) {
        this.seasonScheduleDao = seasonScheduleDao;
    }

}