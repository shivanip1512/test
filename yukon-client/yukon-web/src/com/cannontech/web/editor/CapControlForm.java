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
import java.util.TreeSet;
import java.util.Vector;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.Validate;
import org.apache.myfaces.custom.tree2.HtmlTree;
import org.apache.myfaces.custom.tree2.TreeModel;
import org.apache.myfaces.custom.tree2.TreeModelBase;
import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeNodeBase;
import org.apache.myfaces.custom.tree2.TreeStateBase;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.dao.*;
import com.cannontech.cbc.exceptions.CBCExceptionMessages;
import com.cannontech.cbc.exceptions.FormWarningException;
import com.cannontech.cbc.exceptions.MultipleDevicesOnPortException;
import com.cannontech.cbc.exceptions.PAODoesntHaveNameException;
import com.cannontech.cbc.exceptions.PortDoesntExistException;
import com.cannontech.cbc.exceptions.SameMasterSlaveCombinationException;
import com.cannontech.cbc.exceptions.SerialNumberExistsException;
import com.cannontech.cbc.model.EditorDataModel;
import com.cannontech.cbc.model.ICBControllerModel;
import com.cannontech.cbc.model.ICapControlModel;
import com.cannontech.cbc.point.CBCPointFactory;
import com.cannontech.cbc.service.CapControlCreationModel;
import com.cannontech.cbc.util.CBCUtils;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.StringUtils;
import com.cannontech.core.dao.*;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.capcontrol.*;
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
import com.cannontech.database.db.capcontrol.CCStrategyTimeOfDaySet;
import com.cannontech.database.db.capcontrol.CCSubstationSubBusList;
import com.cannontech.database.db.capcontrol.CapBankAdditional;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.database.db.capcontrol.CapControlSubstationBus;
import com.cannontech.database.db.device.DeviceScanRate;
import com.cannontech.database.db.holiday.HolidaySchedule;
import com.cannontech.database.db.pao.PAOSchedule;
import com.cannontech.database.db.pao.PAOScheduleAssign;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;
import com.cannontech.database.db.season.SeasonSchedule;
import com.cannontech.database.model.Season;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.servlet.nav.CBCNavigationUtil;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.db.CBCDBObjCreator;
import com.cannontech.web.editor.data.CBCSpecialAreaData;
import com.cannontech.web.editor.model.CBCSpecialAreaDataModel;
import com.cannontech.web.editor.model.CapControlStrategyModel;
import com.cannontech.web.editor.model.DataModelFactory;
import com.cannontech.web.editor.point.PointLists;
import com.cannontech.web.exceptions.AltBusNeedsSwitchPointException;
import com.cannontech.web.navigation.CtiNavObject;
import com.cannontech.web.util.CBCDBUtil;
import com.cannontech.web.util.CBCSelectionLists;
import com.cannontech.web.util.JSFParamUtil;
import com.cannontech.web.util.JSFTreeUtils;
import com.cannontech.web.util.JSFUtil;
import com.cannontech.web.wizard.CBCWizardModel;
import com.cannontech.yukon.cbc.SubStation;

public class CapControlForm extends DBEditorForm implements ICapControlModel{
    private int specialAreaTab = -1;
    private int areaTab = -1;
    private int substationTab = -1;
    private int subBusTab = -1;
    private int feederTab = -1;
    private int capbankTab = -1;
    private String paoDescLabel = "Description";
	private String childLabel = "Children";
	private boolean editingCBCStrategy = false;
	private boolean editingController = false;
	private int itemID = -1;
	private HashMap<Integer, CapControlStrategy> cbcStrategiesMap = null;
	private HashMap<Integer, CapControlStrategy> cbcHolidayStrategiesMap = null;
	private List<LiteYukonPAObject> unassignedBanks = null;
	private List<LiteYukonPAObject> unassignedFeeders = null;
	private List<LiteYukonPAObject> unassignedSubBuses = null;
	// possible selection types for every wizard panel
	private CapControlCreationModel wizData = null;
	// possible editor for the CBC a CapBank belongs to
	private ICBControllerModel cbControllerEditor = null;
	private PointTreeForm pointTreeForm = null;
	private LiteYukonPAObject[] unusedCCPAOs = null;
	// selectable items that appear in lists on the GUI
	private SelectItem[] kwkvarPaos = null;
	private SelectItem[] kwkvarPoints = null;
	private SelectItem[] cbcStrategies = null;
	private SelectItem[] cbcHolidayStrategies = null;
    private SelectItem[] cbcSchedules = null;
    private SelectItem[] cbcHolidaySchedules = null;
	// variables that hold sub bus info
	protected List<LiteYukonPAObject> subBusList = null;
    private Integer oldSubBus = null;
    //contains the offset variables
    private Map<String, String> offsetMap = new HashMap<String, String>();
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
    private Map<Integer, String> paoNameMap = null;
    private Map<Integer, String> pointNameMap = null;
    private Map<Season, Integer> assignedStratMap = null;
    private EditorDataModel dataModel = null;
    private EditorDataModel currentStratModel = null;
    private SeasonScheduleDao seasonScheduleDao;
    private HolidayScheduleDao holidayScheduleDao;
    private Integer scheduleId = -1000;
    private Integer holidayScheduleId = -1000;
    private Integer holidayStrategyId = -1000;
    private CCStrategyTimeOfDaySet strategyTimeOfDay = null;
    
    private static CapControlCache cache = (CapControlCache)YukonSpringHook.getBean("capControlCache");
    private static CapbankDao capbankDao = YukonSpringHook.getBean("capbankDao",CapbankDao.class);
    private static FeederDao feederDao = YukonSpringHook.getBean("feederDao",FeederDao.class);
    private static SubstationBusDao substationBusDao = YukonSpringHook.getBean("substationBusDao", SubstationBusDao.class);
    private static PointDao pointDao = YukonSpringHook.getBean("pointDao",PointDao.class);
    private static PaoDao paoDao = YukonSpringHook.getBean("paoDao",PaoDao.class);
    private static RolePropertyDao rolePropertyDao = YukonSpringHook.getBean("rolePropertyDao",RolePropertyDao.class);
    
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
        return seasonScheduleDao.getUserFriendlySeasonsForSchedule(getScheduleId());
    }
    
    /**
     * Returns the scheduleID of the current pao.
     */
    public Integer getScheduleId() {
        if(scheduleId < -1) {
            Integer paoId = ((YukonPAObject)getDbPersistent()).getPAObjectID();
            scheduleId = seasonScheduleDao.getScheduleForPao(paoId).getScheduleId();
        }
        return scheduleId;
    }
    
    /**
     * Returns the scheduleID of the current pao.
     */
    public Integer getHolidayScheduleId() {
        if(holidayScheduleId < -1) {
            Integer paoId = ((YukonPAObject)getDbPersistent()).getPAObjectID();
            holidayScheduleId = holidayScheduleDao.getScheduleForPao(paoId).getHolidayScheduleId();
        }
        return holidayScheduleId;
    }
    
    /**
     * Returns the scheduleID of the current pao.
     */
    public Integer getHolidayStrategyId() {
        if(holidayStrategyId < -1) {
            Integer paoId = ((YukonPAObject)getDbPersistent()).getPAObjectID();
            holidayStrategyId = holidayScheduleDao.getStrategyForPao(paoId);
        }
        return holidayStrategyId;
    }
    
    public void setHolidayStrategyId(Integer holidayStrategyId) {
        this.holidayStrategyId = holidayStrategyId;
    }
    
    public SelectItem[] getCbcStrategies() {
		if (cbcStrategies == null) {
			List<CapControlStrategy> cbcDBStrats = CapControlStrategy.getAllCBCStrategies();
			cbcStrategies = new SelectItem[cbcDBStrats.size()];
			for (int i = 0; i < cbcDBStrats.size(); i++) {
				cbcStrategies[i] = new SelectItem(cbcDBStrats.get(i).getStrategyID(), cbcDBStrats.get(i).getStrategyName());
				getCbcStrategiesMap().put(cbcDBStrats.get(i).getStrategyID(),cbcDBStrats.get(i));
			}
		}
		return cbcStrategies;
	}
    
    public SelectItem[] getCbcHolidayStrategies() {
        if (cbcHolidayStrategies == null) {
            List<CapControlStrategy> cbcDBStrats = CapControlStrategy.getAllCBCStrategies();
            cbcHolidayStrategies = new SelectItem[cbcDBStrats.size()];
            for (int i = 0; i < cbcDBStrats.size(); i++) {
                cbcHolidayStrategies[i] = new SelectItem(cbcDBStrats.get(i).getStrategyID(), cbcDBStrats.get(i).getStrategyName());
                getCbcStrategiesMap().put(cbcDBStrats.get(i).getStrategyID(),cbcDBStrats.get(i));
            }
        }
        return cbcHolidayStrategies;
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
    
    public SelectItem[] getCbcHolidaySchedules() {
        if (cbcHolidaySchedules== null) {
            HolidaySchedule[] cbcDBSchedules = holidayScheduleDao.getAllHolidaySchedules();
            cbcHolidaySchedules = new SelectItem[cbcDBSchedules.length];
            for (int i = 0; i < cbcDBSchedules.length; i++) {
                cbcHolidaySchedules[i] = new SelectItem(cbcDBSchedules[i].getHolidayScheduleId(), cbcDBSchedules[i].getHolidayScheduleName());
            }
        }
        return cbcHolidaySchedules;
    }
    
    public Map<Season, Integer> getAssignedStratMap(){
        if(assignedStratMap == null) {
            HashMap<Season, Integer> map = (HashMap<Season, Integer>) seasonScheduleDao.getUserFriendlySeasonStrategyAssignments(((YukonPAObject)getDbPersistent()).getPAObjectID());
            List<Season> seasons = seasonScheduleDao.getUserFriendlySeasonsForSchedule(getScheduleId());
            for(Season season : seasons) {
                if (!map.containsKey(season)) {
                    map.put(season, 0);
                }
            }
            assignedStratMap = map;
        }
        
        return assignedStratMap;
    }
    
    public HashMap<Integer, String> getStrategyNameMap(){
        HashMap<Integer, String> map = new HashMap<Integer, String>();
        List<CapControlStrategy> strats = CapControlStrategy.getAllCBCStrategies();
        for(CapControlStrategy strat : strats) {
            map.put(strat.getStrategyID(), strat.getStrategyName());
        }
        return map;
    }
    
	public HashMap<Integer, CapControlStrategy> getCbcStrategiesMap() {
		if (cbcStrategiesMap == null) {
			cbcStrategiesMap = new HashMap<Integer, CapControlStrategy>(32);
			// force us to init our map with the correct data
			getCbcStrategies();
		}
		return cbcStrategiesMap;
	}
	
	public HashMap<Integer, CapControlStrategy> getCbcHolidayStrategiesMap() {
        if (cbcHolidayStrategiesMap == null) {
            cbcHolidayStrategiesMap = new HashMap<Integer, CapControlStrategy>(32);
            // force us to init our map with the correct data
            getCbcHolidayStrategies();
        }
        return cbcHolidayStrategiesMap;
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
        if (getDbPersistent() instanceof CapControlStrategy) {
            stratID = ((CapControlStrategy)getDbPersistent()).getStrategyID();
        }
		return stratID;
	}
    
    public EditorDataModel getCurrentStratModel() {
        if (currentStratModel == null){
            CapControlStrategy strat = getCbcStrategiesMap().get(getCurrentStrategyID());
            currentStratModel = new CapControlStrategyModel (strat);
        }
        ((CapControlStrategyModel) currentStratModel).updateModel();
        return currentStratModel;
    }
    
    public String getCurrentStratName() {
        return getCbcStrategiesMap().get(getCurrentStrategyID()).getStrategyName();
    }
    
    public void newStrategySelected (ValueChangeEvent vce) {
        resetCurrentStratModel();
    }
    
    public void newScheduleSelected (ValueChangeEvent vce) {
        assignedStratMap = null;
    }
    
    public void newHolidayScheduleSelected (ValueChangeEvent vce) {
    }

	public TreeNode getVarTreeData() {
		if (varTreeData == null){
			varTreeData = new TreeNodeBase("root", "Var Points", false);
			Integer [] types = { PointTypes.ANALOG_POINT, PointTypes.CALCULATED_POINT, PointTypes.DEMAND_ACCUMULATOR_POINT, PointTypes.PULSE_ACCUMULATOR_POINT};
			List<LitePoint> points = pointDao.getLitePointsBy(types, PointUnits.CAP_CONTROL_VAR_UOMIDS, null,null,null);
			JSFTreeUtils.createPAOTreeFromPointList (points, varTreeData, JSFParamUtil.getYukonUser());
		}	
		return varTreeData;
	}

	public TreeNode getWattTreeData() {
		if (wattTreeData == null){
			wattTreeData =  new TreeNodeBase("root", "Watt Points", false);
            Integer [] types = { PointTypes.ANALOG_POINT, PointTypes.CALCULATED_POINT, PointTypes.DEMAND_ACCUMULATOR_POINT, PointTypes.PULSE_ACCUMULATOR_POINT};
            List<LitePoint> points = pointDao.getLitePointsBy(types, PointUnits.CAP_CONTROL_WATTS_UOMIDS, null,null,null);
			JSFTreeUtils.createPAOTreeFromPointList (points, wattTreeData, JSFParamUtil.getYukonUser());
		}	
		return wattTreeData;
	}

	public TreeNode getVoltTreeData() {
		if (voltTreeData == null){
			voltTreeData = new TreeNodeBase("root", "Volt Points", false);
			Integer [] types = { PointTypes.ANALOG_POINT, PointTypes.CALCULATED_POINT, PointTypes.DEMAND_ACCUMULATOR_POINT, PointTypes.PULSE_ACCUMULATOR_POINT};
			List<LitePoint> points = pointDao.getLitePointsBy(types, PointUnits.CAP_CONTROL_VOLTS_UOMIDS, null,null,null);
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
			unusedCCPAOs = paoDao.getAllUnusedCCPAOs(((CapBank) getDbPersistent()).getCapBank().getControlDeviceID());
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
			List lPoints = pointDao.getLitePointsByPaObjectId(lPaos[i].getYukonID());
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
		String val = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("ptId");
		if (val == null) {
			return;
        }
		if (getDbPersistent() instanceof CapControlFeeder) {
			((CapControlFeeder) getDbPersistent()).getCapControlFeeder().setCurrentVarLoadPointID(new Integer(val));
			((CapControlFeeder) getDbPersistent()).getCapControlFeeder().setPhaseB(new Integer(val));
			((CapControlFeeder) getDbPersistent()).getCapControlFeeder().setPhaseC(new Integer(val));
        } else if (getDbPersistent() instanceof CapControlSubBus) {
			((CapControlSubBus) getDbPersistent()).getCapControlSubstationBus().setCurrentVarLoadPointID(new Integer(val));
            ((CapControlSubBus) getDbPersistent()).getCapControlSubstationBus().setPhaseB(new Integer(val));
            ((CapControlSubBus) getDbPersistent()).getCapControlSubstationBus().setPhaseC(new Integer(val));
        }
	}
	
	public void areaNoVoltReductionPointClicked(ActionEvent ae) {
        String val = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("ptId");
        if (val == null) {
            return;
        }
        ((CapControlArea) getDbPersistent()).getCapControlArea().setVoltReductionPointId(new Integer(val));
    }
	
	public void substationBusNoVoltReductionPointClicked(ActionEvent ae) {
        String val = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("ptId");
        if (val == null) {
            return;
        }
        CapControlSubstationBus sub = ((CapControlSubBus) getPAOBase()).getCapControlSubstationBus();
        sub.setVoltReductionPointId(new Integer(val));
    }
	
	public void substationNoVoltReductionPointClicked(ActionEvent ae) {
        String val = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("ptId");
        if (val == null) {
            return;
        }
        ((CapControlSubstation) getDbPersistent()).getCapControlSubstation().setVoltReductionPointId(new Integer(val));
    }
	
	public void specialAreaNoVoltReductionPointClicked(ActionEvent ae) {
        String val = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("ptId");
        if (val == null) {
            return;
        }
        ((CapControlSpecialArea) getDbPersistent()).getCapControlSpecialArea().setVoltReductionPointId(new Integer(val));
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
		String val = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("ptId");
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
		String val = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("ptId");
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
	
	public static void setupFacesNavigation() {
		/* Start hack */
		
		//This is needed because this was handled in the CBCSerlvet before entering faces pages.
		//Since the servlet bypass, this static method will need to be called entering any faces page.
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
		CtiNavObject navObject = (CtiNavObject) session.getAttribute("CtiNavObject");
		if (navObject.getModuleExitPage() == null || "".equalsIgnoreCase(navObject.getModuleExitPage())) {
			navObject.setModuleExitPage(navObject.getCurrentPage());
			//Hack to preserve an address that will normally fall off our 2 page history.
			navObject.setPreservedAddress(navObject.getPreviousPage());
			
			HttpServletRequest request = (HttpServletRequest)context.getExternalContext().getRequest();

	        String url = request.getRequestURL().toString();
	        String urlParams = request.getQueryString();
	        String navUrl = url + ((urlParams != null) ? "?" + urlParams : "");
			
			navObject.setNavigation(navUrl);			
			navObject.clearHistory();
		}
		/* End hack */
	}
	
	public void initItem(int id, int type) {
	    resetForm();
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
	    ((CBCWizardModel) getWizData()).reset();
		((CBCWizardModel) getWizData()).setWizPaoType(paoType);
        if(paoType == CapControlTypes.CAP_CONTROL_SPECIAL_AREA) {
            ((CBCWizardModel) getWizData()).setDisabled(true);
        }
		initPanels(paoType);
	}

	/**
	 * Initialize our current DBPersistent object from the databse
	 */
	protected void initItem() {
		if (retrieveDBPersistent() == null) {
			return;
        }
        scheduleId = -1000;
		// decide what editor type should be used
		if (getDbPersistent() instanceof YukonPAObject) {
			itemID = ((YukonPAObject) getDbPersistent()).getPAObjectID().intValue();
           if (getDbPersistent() instanceof CapBankController || getDbPersistent() instanceof CapBankController702x || getDbPersistent() instanceof CapBankControllerDNP) {
               setEditingController( true );
           }
           else {
               setEditingController(false);
           }
           if(getDbPersistent() instanceof CapBank) {
               CapBankEditorForm editor = (CapBankEditorForm) JSFParamUtil.getJSFVar("capBankEditor");
               editor.resetForm();
               editor.init(getPAOBase());
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
            CapControlStrategy strat = (CapControlStrategy)getDbPersistent();
            itemID = strat.getStrategyID().intValue();
            editingCBCStrategy = true;
            initPanels(DBEditorTypes.EDITOR_STRATEGY);
            if(strat.getControlMethod().equalsIgnoreCase(CapControlStrategy.CNTRL_TIME_OF_DAY)) {
                CCStrategyTimeOfDaySet tod = getStrategyTimeOfDay();
                tod.setStrategyId(strat.getStrategyID());
                Connection connection = CBCDBUtil.getConnection();
                try {
                    tod.retrieve(connection);
                }
                catch( SQLException sql ) {
                    CTILogger.error("Unable to retrieve CCStrategyTimeofDay Object", sql );
                }
                finally {
                    getDbPersistent().setDbConnection( null );
                    try {
                    if( connection != null ) connection.close();
                    } catch( java.sql.SQLException e2 ) { }         
                }
            }
        }
		//restore the value of the dual bus from DB
		resetDualBusEnabled();
        if (!(getDbPersistent() instanceof CapControlStrategy)) {
            editingCBCStrategy = false;
		    resetCurrentStratModel();
        }
        initEditorPanels();
        getPointTreeForm().init(itemID);
	}

    private void resetCurrentStratModel() {
        currentStratModel = null;
    }

    //initiatiates data model for our specific object
	private void initDataModel(DBPersistent dbPersistent) {
        if ((dbPersistent instanceof CapControlArea) 
                || (dbPersistent instanceof CapControlSpecialArea) 
                || (dbPersistent instanceof CapControlSubBus)
                || (dbPersistent instanceof CapControlSubstation)
                || (dbPersistent instanceof CapControlFeeder)) {
            dataModel = DataModelFactory.createModel(dbPersistent);
        }
    }

	public void resetForm() {
		resetStrategies();
		resetCBCEditor();
		resetPointTreeForm();
		resetCurrentDivOffset();
		resetCurrentAltSubDivOffset();
		resetUOFMTreeData();
		isDualSubBusEdited = false;
		editingCBCStrategy = false;
		unassignedBanks = null;
		unassignedFeeders = null;
		unassignedSubBuses = null;
		unusedCCPAOs = null;
		kwkvarPaos = null;
		kwkvarPoints = null;
		editingController = false;
        assignedStratMap = null;
        scheduleId = -1000;

        initItem();
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
		cbcStrategies = null;
	}

	/**
	 * Reset the CBC device data, forcing them to be reInited
	 */
	private void resetCBCEditor() {
        setEditingController(false);
        setCBControllerEditor(null);
        getCBControllerEditor().retrieveDB();        
        getCBControllerEditor().resetSerialNumber();
	}
	
	/**
     * Reset the the point tree form
     */
    private void resetPointTreeForm() {
        getPointTreeForm().retrieveDB();        
    }

	/**
	 * All possible panels for this editor go here. Set visible panels and
	 * labels based on type of object. DO NOT access the DB persitent object in
	 * this method since it may be null in the case of a Wizard.
	 */
    private void initPanels(int paoType) {

		// all panels that are always displayed
		getVisibleTabs().put("General", Boolean.TRUE);

		// all type specifc panels
		getVisibleTabs().put("GeneralPAO", Boolean.TRUE);
        getVisibleTabs().put("CBCArea", Boolean.FALSE);
        getVisibleTabs().put("CBCSpecialArea", Boolean.FALSE);
		getVisibleTabs().put("BaseCapControl", Boolean.FALSE);
		getVisibleTabs().put("CBCSubstation", Boolean.FALSE);
		getVisibleTabs().put("CBCSubstationBus", Boolean.FALSE);
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
                setEditorTitle("Area");
                setPaoDescLabel("Area Location");
                setChildLabel("Substations");
                getVisibleTabs().put("CBCArea", Boolean.TRUE);
                break;
                
            case PAOGroups.CAP_CONTROL_SPECIAL_AREA:
                setEditorTitle("Special Area");
                setPaoDescLabel("Special Area Location");
                setChildLabel("Substations");
                getVisibleTabs().put("CBCSpecialArea", Boolean.TRUE);
                break;

            case PAOGroups.CAP_CONTROL_SUBSTATION:
    			setEditorTitle("Substation");
    			setPaoDescLabel("Geographical Name");
    			setChildLabel("Substation Buses");
    			getVisibleTabs().put("CBCSubstation", Boolean.TRUE);
                break;
                
            case PAOGroups.CAP_CONTROL_SUBBUS:
    			setEditorTitle("Substation Bus");
    			setPaoDescLabel("Geographical Name");
    			setChildLabel("Feeders");
    			getVisibleTabs().put("CBCSubstationBus", Boolean.TRUE);
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
            case PAOGroups.CBC_DNP:
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
    
    public boolean checkStrategy(CapControlStrategy strat) {
        CapControlStrategyModel stratModel = (CapControlStrategyModel)getCurrentStratModel();
        boolean integratedControl = stratModel.getIntegrateFlag();
        if(integratedControl) {
            int integrationSeconds = strat.getIntegratePeriod();
            int analysisSeconds = strat.getControlInterval();
            if(integrationSeconds > analysisSeconds) {
                return false;
            }
        }
        return true;
    }
    
	public void update() throws SQLException {
	    if(!isEditingAuthorized()) {
	        throw new NotAuthorizedException("User " + JSFParamUtil.getYukonUser() + " is not authorized to edit this object.");
	    }
		FacesMessage facesMsg = new FacesMessage();
        facesMsg.setDetail(CBCExceptionMessages.DB_UPDATE_SUCCESS);
        Connection connection = getDbPersistent().getDbConnection();
		try {
			// update the CBCStrategy object if we are editing it
			if (isEditingCBCStrategy() && (! (getDbPersistent() instanceof CapControlStrategy))) {
                CapControlStrategy currentStrategy = ((CapControlStrategyModel) getCurrentStratModel()).getStrategy();
                boolean ok = checkStrategy(currentStrategy);
                if(ok) {
                    updateDBObject(currentStrategy, facesMsg);
                    if(currentStrategy.getControlMethod().equalsIgnoreCase(CapControlStrategy.CNTRL_TIME_OF_DAY)) {
                        getStrategyTimeOfDay().setStrategyId(currentStrategy.getStrategyID());
                        getStrategyTimeOfDay().add(connection);
                    }
    				// clear out the memory of any list of Strategies
    				resetCurrentStratModel();
                    resetStrategies();
                    setEditingCBCStrategy(false);
                }else {
                    facesMsg.setDetail("ERROR: When using Integrate Control, the analysis interval must be greater than or equal to the integration interval.");
                    facesMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                }
			}
			// update the CBC object if we are editing it
			if (isEditingController()) {
                try {
                    checkForErrors();
                }
                catch (FormWarningException e) {
                    String errorString = e.getMessage();
                    CTILogger.error(errorString, e);
                    facesMsg.setDetail(errorString);
                    facesMsg.setSeverity(FacesMessage.SEVERITY_WARN);
                }
                //if the editing occured outside CBCEditor i.e Cap Bank Editor then update DB with the changes made
                DBPersistent dbPers = getCBControllerEditor().getPaoCBC();
               //creates 2 db change messages - so comment this out updateDBObject(dbPers, facesMsg); if the editing did occur in the CBC Editor then
                //make sure the object is not overwritten later
                if ((getDbPersistent() instanceof CapBankController702x)|| (getDbPersistent() instanceof CapBankControllerDNP) || (getDbPersistent() instanceof CapBankController)) {
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
			
			boolean dataModelOK = true;
			List<String> duplicates = new ArrayList<String>();
			
			DBPersistent dbPers = getDbPersistent();
            if (getDataModel() != null) {
                if(dbPers instanceof CapControlSpecialArea) {
                    duplicates = checkEnableSpecialArea((CapControlSpecialArea)dbPers);
                    if(!duplicates.isEmpty()) {
                        dataModelOK = false;
                    }else {
                        getDataModel().updateDataModel();
                    }
                }else {
                    getDataModel().updateDataModel();
                }
            }
            
            connection = CBCDBUtil.getConnection();
            dbPers.setDbConnection(connection);
            
            if (dbPers instanceof CapControlStrategy) {
                CapControlStrategy strategy = ((CapControlStrategyModel) getCurrentStratModel()).getStrategy();
                boolean ok = checkStrategy(strategy);
                if(ok) {
                    setEditingCBCStrategy(false);
                    updateDBObject(strategy, facesMsg);
                    if(strategy.getControlMethod().equalsIgnoreCase(CapControlStrategy.CNTRL_TIME_OF_DAY)) {
                        CCStrategyTimeOfDaySet ccStrategyTimeOfDay = getStrategyTimeOfDay();
                        if(CapControlStrategy.todExists(ccStrategyTimeOfDay.getStrategyId())) {
                            ccStrategyTimeOfDay.update(connection);
                        }else {
                            getStrategyTimeOfDay().setStrategyId(strategy.getStrategyID());
                            getStrategyTimeOfDay().add(connection);
                        }
                    }else {
                        CapControlStrategy.deleteTod(((CapControlStrategy)getDbPersistent()).getStrategyID());
                        strategyTimeOfDay = new CCStrategyTimeOfDaySet();
                    }
                }else {
                	facesMsg.setDetail("ERROR: When using Integrate Control, the analysis interval must be greater than or equal to the integration interval.");
                    facesMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                }
            }else if(dbPers instanceof CapControlSpecialArea) {
                
                if(dataModelOK) {
                    updateDBObject(dbPers, facesMsg);
                } else {
                    String dups = StringUtils.toStringList(duplicates);
                    facesMsg.setDetail("Error: Some of the substations assigned are already assigned to another enabled Special Area: " + dups );
                    facesMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                }
                
            }else {
                updateDBObject(dbPers, facesMsg);
            }
            
            if(getDbPersistent() instanceof CapControlSubBus 
            	|| getDbPersistent() instanceof CapControlArea
            	|| getDbPersistent() instanceof CapControlSpecialArea
            	|| getDbPersistent() instanceof CapControlFeeder){
                
                if(dataModelOK) {
                	int paoId = ((YukonPAObject)getDbPersistent()).getPAObjectID();
                	seasonScheduleDao.saveSeasonStrategyAssigment(paoId, getAssignedStratMap(), getScheduleId());
                	holidayScheduleDao.saveHolidayScheduleStrategyAssigment(paoId, getHolidayScheduleId(), getHolidayStrategyId());
                }
            }
            pointNameMap = null;
            paoNameMap = null;
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
    
    /**
     * Method to check whether a special area should be enabled.
     * A substation can only be in one ENABLED special area at a time.
     * @param area
     * @return
     */
    private List<String> checkEnableSpecialArea(CapControlSpecialArea area){
        List<String> duplicates = new ArrayList<String>();
        
        if(!area.isDisabled()) {  // only check the newly assigned subs list if they are trying to enable this special area
            List<CBCSpecialAreaData> assignedAreas = ((CBCSpecialAreaDataModel)getDataModel()).getAssigned();
            
            for(CBCSpecialAreaData data: assignedAreas) {
                try {
                    SubStation substation = cache.getSubstation(data.getSubID());
                    if(substation.getSpecialAreaEnabled() && substation.getSpecialAreaId().intValue() != area.getPAObjectID().intValue()) {
                        duplicates.add(cache.getCBCSpecialArea(substation.getSpecialAreaId()).getCcName() + ": " + substation.getCcName());
                    }
                } catch(NotFoundException nfe) {
                    // if it's not in the cache then it's an orphan and there are no duplicates.
                    continue;
                }
            }
        }
        return duplicates;
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
	private void createPostItems(int paoType, int paoId, final FacesMessage facesMsg, DBPersistent dbObj) throws TransactionException {
        SmartMultiDBPersistent smartMulti = new SmartMultiDBPersistent();
		// store the objects we add to the DB
		CBCDBObjCreator cbObjCreator = new CBCDBObjCreator(getWizData());
        if (dbObj instanceof CapBank) {
            createCapBankAdditional(dbObj, facesMsg);
        }else if(dbObj instanceof CapControlSubBus 
            	|| dbObj instanceof CapControlArea
            	|| dbObj instanceof CapControlSpecialArea
            	|| dbObj instanceof CapControlFeeder) {
            seasonScheduleDao.saveDefaultSeasonStrategyAssigment(paoId);
            holidayScheduleDao.saveDefaultHolidayScheduleStrategyAssigment(paoId);
        }
        if  (paoType == CapControlTypes.CAP_CONTROL_FEEDER || paoType == CapControlTypes.CAP_CONTROL_SUBBUS) {
            smartMulti = CBCPointFactory.createPointsForPAO(paoId,dbObj.getDbConnection());
        } else {
            smartMulti = cbObjCreator.createChildItems(paoType, new Integer(paoId));
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
                if ( pao instanceof CapBankController702x || pao instanceof CapBankControllerDNP ) {
                   MultiDBPersistent pointVector = CBCPointFactory.createPointsForCBCDevice(pao);           
                   CBControllerEditor.insertPointsIntoDB(pointVector);
                   statusPt = (StatusPoint) MultiDBPersistent.getFirstObjectOfType(StatusPoint.class, pointVector);
                }else {
                    //create additional info find the first status point in our CBC and assign its ID to our CapBank for control purposes
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
            CapControlStrategy strategy = null;
			int editorType = PAOGroups.INVALID;
			// todo: do this in a better way later
			if (paoType == DBEditorTypes.PAO_SCHEDULE) {
				dbObj = new PAOSchedule();
				((PAOSchedule) dbObj).setDisabled(((CBCWizardModel) getWizData()).getDisabled().booleanValue());
				((PAOSchedule) dbObj).setScheduleName(((CBCWizardModel) getWizData()).getName());
				addDBObject(dbObj, facesMsg);
				itemID = ((PAOSchedule) dbObj).getScheduleID().intValue();
				editorType = DBEditorTypes.EDITOR_SCHEDULE;
			} else if(paoType == DBEditorTypes.EDITOR_STRATEGY) {
                strategy = CCYukonPAOFactory.createCapControlStrategy();
                Integer newID = CapControlStrategy.getNextStrategyID();
                strategy.setStrategyID(newID);
                strategy.setStrategyName(((CBCWizardModel) getWizData()).getName());
                addDBObject(strategy, facesMsg);
                itemID = strategy.getStrategyID();
                editorType = DBEditorTypes.EDITOR_STRATEGY;
            }else {
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
			
            //create points for the CBC702x or CBC DNP device   
            if (dbObj instanceof CapBankController702x || dbObj instanceof CapBankControllerDNP){
                DBPersistent pointVector = CBCPointFactory.createPointsForCBCDevice((YukonPAObject)dbObj);
                CBControllerEditor.insertPointsIntoDB(pointVector);  
            }

            if(!(paoType == DBEditorTypes.EDITOR_STRATEGY)) {
                generateDBChangeMsg(dbObj, DBChangeMsg.CHANGE_TYPE_UPDATE);
            }else {
                generateDBChangeMsg(strategy, DBChangeMsg.CHANGE_TYPE_UPDATE);
            }
            
            // redirect to this form as the editor for this new DB object
            if (facesContext != null){
                HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
                String url = "/editor/cbcBase.jsf?type="+editorType+"&itemid="+itemID;
                CBCNavigationUtil.redirect(url, session);
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
	
	public PointTreeForm getPointTreeForm() {
        if (pointTreeForm == null) {
            int paoId = itemID;         
            pointTreeForm = new PointTreeForm(paoId);
        }
        return pointTreeForm;
    }
	
	public void setPointTreeForm(PointTreeForm pointTreeForm) {
	    this.pointTreeForm = pointTreeForm;
    }

	public ICBControllerModel getCBControllerEditor() {
		if (cbControllerEditor == null) {
            int paoId = itemID;			
            if ((getDbPersistent() instanceof CapBankController702x) || (getDbPersistent() instanceof CapBankController) || (getDbPersistent() instanceof CapBankControllerDNP)){
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
		Integer newId = CapControlStrategy.getNextStrategyID();
		ccStrat.setStrategyID(newId);
		ccStrat.setStrategyName("Strat #" + newId + " (New)");
		// this message will be filled in by the super class
		FacesMessage facesMsg = new FacesMessage();
		try {
			addDBObject(ccStrat, facesMsg);
			// clear out the memory of the any list of Strategies
			resetStrategies();
			setEditingCBCStrategy(true);
			facesMsg.setDetail("CapControl Strategy add was SUCCESSFUL");
			cbcStrategiesMap.put(newId, ccStrat);
			JSFUtil.redirect("/editor/cbcBase.jsf?type=" + DBEditorTypes.EDITOR_STRATEGY + "&itemid=" + newId);
		} catch (TransactionException te) {
			// do nothing since the appropriate actions was taken in the super
		} finally {
			FacesContext.getCurrentInstance().addMessage("cti_db_add", facesMsg);
		}
	}

	public boolean deleteStrategy() {
	    boolean success = true;
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
			int[] paos = CapControlStrategy.getAllPAOSUsingSeasonStrategy(stratID, itemID);
			if (paos.length <= 0) {
				// update the current PAOBase object just in case it uses the strategy we are deleting
				updateDBObject(getDbPersistent(), null);
				deleteDBObject(getCbcStrategiesMap().get(new Integer(stratID)), facesMsg);
				// clear out the memory of the any list of Strategies
				resetStrategies();
				facesMsg.setDetail("CapControl Strategy delete was SUCCESSFUL");
				success = true;
			} else {
				StringBuffer items = new StringBuffer("");
				for (int i = 0; i < paos.length; i++) {
					items.append(paoDao.getYukonPAOName(paos[i]) + ",");
                }
				facesMsg.setDetail("Unable to delete the Strategy since the following items use it: " + items.deleteCharAt(items.length() - 1));
				facesMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
				success = false;
			}
		} catch (TransactionException te) {
			facesMsg.setDetail("Unable to delete the Strategy: " + te.getMessage());
            facesMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            success = false;
		} finally {
		    FacesContext context = FacesContext.getCurrentInstance(); 
			if(context != null) {
			    context.addMessage("cti_db_delete", facesMsg);
			}
		}
		return success;
	}

	public String getPAODescLabel() {
		return paoDescLabel;
	}

	public String getParent() {
		int parentID = CtiUtilities.NONE_ZERO_ID;
		if (getDbPersistent() instanceof CapControlFeeder) {
		    try{
		        parentID = feederDao.getParentSubBusID(itemID);
		    }catch( EmptyResultDataAccessException e ){
		        //do nothing
		    }
		} else if (getDbPersistent() instanceof CapBank) {
		    try{
		        parentID = capbankDao.getParentFeederId(itemID);
            }catch( EmptyResultDataAccessException e ){
                //do nothing
            }
        } else if (getDbPersistent() instanceof CapControlSubBus) {
            try{
                parentID = cache.getSubBus(itemID).getParentID();
            }catch( NotFoundException e ){
                //do nothing
                parentID = 0;
            }
        } else if (getDbPersistent() instanceof CapControlSubstation) {
            try{
                parentID = cache.getSubstation(itemID).getParentID();
            }catch( NotFoundException e ){
                //do nothing
                parentID = 0;
            }
        }
		if (parentID == CtiUtilities.NONE_ZERO_ID) {
			return CtiUtilities.STRING_NONE;
        } else {
            try {
    			LiteYukonPAObject parentPAO = paoDao.getLiteYukonPAO(parentID);
    			return parentPAO.getPaoName() + "   (" + PAOGroups.getPAOTypeString(parentPAO.getType()) + ",  id: " + parentPAO.getLiteID() + ")";
            }catch (NotFoundException nfe) {
                return CtiUtilities.STRING_NONE;
            }
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
					if (elemID == unassignedBanks.get(i).getLiteID()) {
						// Add the mapping for the given CapBank id to this Feeder
						CapControlFeeder currFdr = (CapControlFeeder) getDbPersistent();
                        float order = maxDispOrderOnList (currFdr.getChildList()) + 1;
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
					if (elemID == unassignedFeeders.get(i).getLiteID()) {
						// Add the mapping for the given Feeders id to this Sub
						CapControlSubBus currSub = (CapControlSubBus) getDbPersistent();
                        //NOTE: casting maxDispOrderOnList. if we change this to float later need to remove cast.
                        CCFeederSubAssignment sa = new CCFeederSubAssignment(elemID,itemID,(int)maxDispOrderOnList (currSub.getChildList())+ 1); 
						currSub.getChildList().add(sa);
						unassignedFeeders.remove(i);
						break;
					}
				}
			}
		} else if ( "SubstationBus".equalsIgnoreCase(swapType)) {
            if (unassignedSubBuses != null) {
                for (int i = 0; i < unassignedSubBuses.size(); i++) {
                    if (elemID == unassignedSubBuses.get(i).getLiteID()) {
                        CapControlSubstation currSub = (CapControlSubstation) getDbPersistent();
                        CCSubstationSubBusList sa = new CCSubstationSubBusList(itemID,elemID,(int)maxDispOrderOnList (currSub.getChildList())+ 1); 
                        currSub.getChildList().add(sa);
                        unassignedSubBuses.remove(i);
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
        List<CCFeederBankList> childList = currFdr.getChildList();
        for (Iterator iter = childList.iterator(); iter.hasNext();) {
            CCFeederBankList assign = (CCFeederBankList) iter.next();
            assign.setTripOrder(maxDispOrderOnList(childList) + 1 - assign.getControlOrder());
        }
    }

    private void reorderList(List<? extends DBPersistent> childList) {
		for (int i = 0; i < childList.size(); i++) {
			Object element = childList.get(i);
			if (element instanceof CCFeederSubAssignment) {
				CCFeederSubAssignment feeder = (CCFeederSubAssignment) element;
				feeder.setDisplayOrder(new Integer ( i + 1));
			}else if (element instanceof CCSubstationSubBusList) {
			    CCSubstationSubBusList subBus = (CCSubstationSubBusList) element;
			    subBus.setDisplayOrder(new Integer ( i + 1));
            } else if (element instanceof CCFeederBankList) {
				CCFeederBankList capBank = (CCFeederBankList) element;
				capBank.setControlOrder(new Float ( i  + 1));
                capBank.setCloseOrder(new Float ( i + 1));
                capBank.setTripOrder(new Float ( childList.size() - i));
			} else {
				return;
            }
		}		
	}
    
    private void reorderBankList(List<CCFeederBankList> childList, float removeDispOrder, float removeCloseOrder, float removeTripOrder) {
    	if (childList.size() > 0 && removeDispOrder >= 1  && removeCloseOrder >= 1 && removeTripOrder >= 1){
    		CCFeederBankList capBank = childList.get(0);
    		float prevAdjControlOrder = 0;
    		float prevAdjCloseOrder = 0;
    		float prevAdjTripOrder = 0;
    		boolean tripOrderDesc = false;
    		if (capBank.getTripOrder() == childList.size()){
    			tripOrderDesc = true;
    			prevAdjTripOrder = childList.size();
    		} 
    		for (int i = 0; i < childList.size(); i++) {
    			capBank = childList.get(i);

    			if(capBank.getControlOrder() > removeDispOrder && (capBank.getControlOrder() - 1) > prevAdjControlOrder){
    				capBank.setControlOrder(new Float ( capBank.getControlOrder() - 1));
    			}
    			if(capBank.getCloseOrder() > removeCloseOrder && (capBank.getCloseOrder() - 1) > prevAdjCloseOrder){
    				capBank.setCloseOrder(new Float ( capBank.getCloseOrder() - 1));
    			}
    			if ( (capBank.getTripOrder() > removeTripOrder && (!tripOrderDesc && (capBank.getTripOrder() - 1) > prevAdjTripOrder)) || 
       			     ((capBank.getTripOrder()-1) >= removeTripOrder && (tripOrderDesc && (capBank.getTripOrder() - 1) < prevAdjTripOrder))) {
       					capBank.setTripOrder(new Float ( capBank.getTripOrder() - 1));
           		} 
    			prevAdjControlOrder = capBank.getControlOrder();
				prevAdjCloseOrder = capBank.getCloseOrder();
				prevAdjTripOrder = capBank.getTripOrder();

    		}
		} else {
    		reorderList(childList);
    	}
    		
	}

    //Warning: instanceof CCFeederSubAssignment is putting an int into a float.
	@SuppressWarnings("unchecked")
    private float maxDispOrderOnList(List<? extends DBPersistent> childList) {
		float max = 0;
		for (Iterator iter = childList.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (element instanceof CCFeederSubAssignment) {
				CCFeederSubAssignment feeder = (CCFeederSubAssignment) element;
				if (feeder.getDisplayOrder().intValue() > max) {
					max = feeder.getDisplayOrder().intValue();
				}
			} else if (element instanceof CCFeederBankList) {
				CCFeederBankList capBank = (CCFeederBankList) element;
				if (capBank.getControlOrder().floatValue() > max) {
					max = capBank.getControlOrder().floatValue();
				}
			} else if (element instanceof CCSubstationSubBusList) {
                CCSubstationSubBusList subBus = (CCSubstationSubBusList) element;
                if (subBus.getDisplayOrder().floatValue() > max) {
                    max = subBus.getDisplayOrder().floatValue();
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
			CapControlFeeder currFdr = (CapControlFeeder) getDbPersistent();
			for (int i = 0; i < currFdr.getChildList().size(); i++) {
				CCFeederBankList listItem = currFdr.getChildList().get(i);
				if (elemID == listItem.getDeviceID().intValue()) {
					// remove the mapping for the given CapBank id to this Feeder
					float dispO = listItem.getControlOrder();
					float closeO = listItem.getCloseOrder();
					float tripO = listItem.getTripOrder();
					
					currFdr.getChildList().remove(i);
					unassignedBanks.add(paoDao.getLiteYukonPAO(elemID));
					// keep our order
					Collections.sort(unassignedBanks, LiteComparators.liteStringComparator);
					reorderBankList (currFdr.getChildList(), dispO, closeO, tripO);
					break;
				}
			}
		} else if ("Feeder".equalsIgnoreCase(swapType)) {
			CapControlSubBus currSub = (CapControlSubBus) getDbPersistent();
			for (int i = 0; i < currSub.getChildList().size(); i++) {
				CCFeederSubAssignment listItem = currSub.getChildList().get(i);
				if (elemID == listItem.getFeederID().intValue()) {
					// remove the mapping for the given Feeder id to this SubBus
					currSub.getChildList().remove(i);
					unassignedFeeders.add(paoDao.getLiteYukonPAO(elemID));
					// keep our order
					Collections.sort(unassignedFeeders, LiteComparators.liteStringComparator);
					reorderList (currSub.getChildList());
					break;
				}
			}
		} else if ("SubstationBus".equalsIgnoreCase(swapType)) {
            CapControlSubstation currSub = (CapControlSubstation) getDbPersistent();
            for (int i = 0; i < currSub.getChildList().size(); i++) {
                CCSubstationSubBusList listItem = currSub.getChildList().get(i);
                if (elemID == listItem.getSubstationBusID().intValue()) {
                    // remove the mapping for the given Feeder id to this SubBus
                    currSub.getChildList().remove(i);
                    unassignedSubBuses.add(paoDao.getLiteYukonPAO(elemID));
                    // keep our order
                    Collections.sort(unassignedSubBuses, LiteComparators.liteStringComparator);
                    reorderList (currSub.getChildList());
                    break;
                }
            }
        }
	}

    public void initEditorPanels() {
		if (getDbPersistent() instanceof CapControlFeeder) {
			com.cannontech.database.db.capcontrol.CapControlFeeder capControlFeeder = ((CapControlFeeder) getDbPersistent()).getCapControlFeeder();
			int fdrVarPtID = capControlFeeder.getCurrentVarLoadPointID().intValue();
			LitePoint litePoint = pointDao.getLitePoint(fdrVarPtID);
			if (litePoint != null) {
				int paobjectID = litePoint.getPaobjectID();
				kwkvarPaosChanged(new ValueChangeEvent(DUMMY_UI, null, new Integer(paobjectID)));
			}
		} else if (getDbPersistent() instanceof CapControlSubBus) {
			int varPtID = ((CapControlSubBus) getDbPersistent()).getCapControlSubstationBus().getCurrentVarLoadPointID().intValue();
			LitePoint litePoint = pointDao.getLitePoint(varPtID);
			if (litePoint != null) {
				if (varPtID > CtiUtilities.NONE_ZERO_ID) {
					kwkvarPaosChanged(new ValueChangeEvent(DUMMY_UI, null, new Integer(litePoint.getPaobjectID())));
                }
			}
		}

		List<Integer> unassignedBankIDs = capbankDao.getUnassignedCapBankIds();
        unassignedBanks = new ArrayList<LiteYukonPAObject>(unassignedBankIDs.size());
		for (Integer i : unassignedBankIDs ) {
			LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(i);
			if (liteYukonPAO != null) {
				unassignedBanks.add(liteYukonPAO);
            }
		}
		Collections.sort(unassignedBanks, LiteComparators.liteStringComparator);
		
		List<Integer> unassignedFeederIDs = feederDao.getUnassignedFeederIds();
        unassignedFeeders = new ArrayList<LiteYukonPAObject>(unassignedFeederIDs.size());
        for ( Integer i : unassignedFeederIDs ) {
			unassignedFeeders.add(paoDao.getLiteYukonPAO(i));
		}
		Collections.sort(unassignedFeeders, LiteComparators.liteStringComparator);
				
        List<Integer> allUnassignedBuses = substationBusDao.getAllUnassignedBuses();
        unassignedSubBuses = new ArrayList<LiteYukonPAObject>(allUnassignedBuses.size());
        for (Integer i : allUnassignedBuses) {
			unassignedSubBuses.add( paoDao.getLiteYukonPAO(i));
		}
		Collections.sort(unassignedSubBuses, LiteComparators.liteStringComparator);
	}

	public void setPaoDescLabel(String string) {
		paoDescLabel = string;
	}
	
	/*
	 * Remembers if the edit checkbox is checked on the startegy editor.
	 */
	public boolean isEditingCBCStrategy() {
		return editingCBCStrategy;
	}
	
	/*
	 * Tells us whether or not the pao we are editing is a strategy.
	 */
	public boolean isEditingAStrategy() {
        if(getDbPersistent() instanceof CapControlStrategy) {
            return true;
        }
        return false;
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

	public List<LiteYukonPAObject> getUnassignedBanks() {
		return unassignedBanks;
	}

	public List<LiteYukonPAObject> getUnassignedFeeders() {
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
    		LitePoint litePoint = pointDao.getLitePoint(controlPointId);
    		if (litePoint != null) {
    			int paoID = litePoint.getPaobjectID();
    			if (paoID != CtiUtilities.NONE_ZERO_ID) {
    				return DeviceTypesFuncs.isCapBankController(paoDao.getLiteYukonPAO(paoID).getType());
                }
    		}
    	} else if (getDbPersistent() instanceof YukonPAObject){
            // support for the TwoWay devices
            int paoID = ((YukonPAObject)getDbPersistent()).getPAObjectID().intValue();
            return DeviceTypesFuncs.isCapBankController(paoDao.getLiteYukonPAO(paoID));
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

	public CapControlCreationModel getWizData() {
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

    public LiteYukonPAObject[] getSubBusList() {
		if (subBusList == null) {
			subBusList = DaoFactory.getCBCDao().getAllSubsForUser (JSFParamUtil.getYukonUser());
		}
		return subBusList.toArray(new LiteYukonPAObject[subBusList.size()]);
	}

	public TreeNode getSwitchPointList() {
	    TreeNode rootData = new TreeNodeBase("root","Switch Points", false);
        TreeSet<LitePoint> points = PointLists.getAllTwoStateStatusPoints();
        rootData = JSFTreeUtils.createPAOTreeFromPointList(points, rootData, JSFParamUtil.getYukonUser());
        return rootData;
    }
    
    public String getSelectedSubBusFormatString() {
        
		String retString = new String();
		Integer subPAOid = null;
        
		if (getDbPersistent() instanceof CapControlSubBus) {
			subPAOid = ((CapControlSubBus) getDbPersistent()).getCapControlSubstationBus().getAltSubPAOId();
			// get the pao name
			LiteYukonPAObject liteDevice = paoDao.getLiteYukonPAO(subPAOid.intValue());
			retString = new String(liteDevice.getPaoName());
		}
		// Every time the page loads we need to keep track of the
		// currently selected value. We index the current value
		// from the total list
		if (this.subBusList != null && subPAOid != null) {
			for (int i = 0; i < this.subBusList.size(); i++) {
				LiteYukonPAObject device = this.subBusList.get(i);
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
		List<TreeNodeBase> paos = tn.getChildren();
      
		for (int i = 0; i < paos.size(); i++) {
			TreeNodeBase pao = paos.get(i);
			String paoName = pao.getDescription();
			List<TreeNodeBase> points = pao.getChildren();
			for (int j = 0; j < points.size(); j++) {
				TreeNodeBase point = points.get(j);
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
    
    private void resetCurrentDivOffset() {
        offsetMap.put("currentTwoWayPointDivOffset","0");
	}

    private void resetCurrentAltSubDivOffset ()  {
		offsetMap.put("currentAltSubDivOffset","0");
	}
	
	public boolean isDualSubBusEdited() {
		return isDualSubBusEdited;
	}

	public void setDualSubBusEdited(boolean isDualSubBusEdited) {
		this.isDualSubBusEdited = isDualSubBusEdited;
	}

    public Map<String, String> getOffsetMap() {
        return offsetMap;
    }

    public void setOffsetMap(Map<String, String> offsetMap) {
        this.offsetMap = offsetMap;
    }

    public int getSelectedPanelIndex() {
        if (isEditingController()) {
           if (getDbPersistent() instanceof CapBankController || getDbPersistent() instanceof CapBankController702x || getDbPersistent() instanceof CapBankControllerDNP) {
               return CBCSelectionLists.CapBankControllerSetup;
           }
        } else if (getDbPersistent() instanceof CapControlArea) {
            return areaTab > -1 ? areaTab : CBCSelectionLists.CapControlAreaSetup;
        } else if (getDbPersistent() instanceof CapControlSpecialArea) {
            return specialAreaTab > -1 ? specialAreaTab : CBCSelectionLists.CapControlSpecialAreaSetup;
        } else if (getDbPersistent() instanceof CapBank) {
            return capbankTab > -1 ? capbankTab : CBCSelectionLists.CapBankSetup;
        } else if (getDbPersistent() instanceof CapControlSubBus) {
            return subBusTab > -1 ? subBusTab : CBCSelectionLists.CapControlSubBusSetup;
        } else if (getDbPersistent() instanceof CapControlSubstation) {
            return substationTab > -1 ? substationTab : CBCSelectionLists.CapControlSubstationSetup;
        } else if (getDbPersistent() instanceof CapControlFeeder) {
            return feederTab > -1 ? feederTab : CBCSelectionLists.CapControlFeederSetup;
        }else if (getDbPersistent() instanceof CapControlStrategy) {
            return CBCSelectionLists.CapControlStrategyEditor;
        }
        return CBCSelectionLists.General;
    }
    
    @SuppressWarnings("unchecked")
    public void setSpecialAreaTab(ActionEvent e) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<Integer, String> paramMap = context.getExternalContext().getRequestParameterMap();
        int tabId = Integer.parseInt( paramMap.get("tabId") );
        specialAreaTab = tabId;
    }
    
    @SuppressWarnings("unchecked")
    public void setAreaTab(ActionEvent e) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<Integer, String> paramMap = context.getExternalContext().getRequestParameterMap();
        int tabId = Integer.parseInt( paramMap.get("tabId") );
        areaTab = tabId;
    }
    
    @SuppressWarnings("unchecked")
    public void setSubstationTab(ActionEvent e) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<Integer, String> paramMap = context.getExternalContext().getRequestParameterMap();
        int tabId = Integer.parseInt( paramMap.get("tabId") );
        substationTab = tabId;
    }
    
    @SuppressWarnings("unchecked")
    public void setSubBusTab(ActionEvent e) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<Integer, String> paramMap = context.getExternalContext().getRequestParameterMap();
        int tabId = Integer.parseInt( paramMap.get("tabId") );
        subBusTab = tabId;
    }
    
    @SuppressWarnings("unchecked")
    public void setFeederTab(ActionEvent e) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<Integer, String> paramMap = context.getExternalContext().getRequestParameterMap();
        int tabId = Integer.parseInt( paramMap.get("tabId") );
        feederTab = tabId;
    }
    
    @SuppressWarnings("unchecked")
    public void setCapbankTab(ActionEvent e) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<Integer, String> paramMap = context.getExternalContext().getRequestParameterMap();
        int tabId = Integer.parseInt( paramMap.get("tabId") );
        capbankTab = tabId;
    }
    
    @SuppressWarnings("unchecked")
    public void setTab(ActionEvent e) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<Integer, String> paramMap = context.getExternalContext().getRequestParameterMap();
        int tabId = Integer.parseInt( paramMap.get("tabId") );
        if (getDbPersistent() instanceof CapControlSpecialArea) {
            specialAreaTab = tabId;
        }
        if (getDbPersistent() instanceof CapControlArea) {
            areaTab = tabId;
        }
        if (getDbPersistent() instanceof CapControlSubstation) {
            substationTab= tabId;
        }
        if (getDbPersistent() instanceof CapControlSubBus) {
            subBusTab = tabId;
        }
        if (getDbPersistent() instanceof CapControlFeeder) {
            feederTab = tabId;
        }
        if (getDbPersistent() instanceof CapBank) {
            capbankTab = tabId;
        }
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
	        } else if (getDbPersistent() instanceof CapControlStrategy) {
	            retStr = ((CapControlStrategy)getDbPersistent()).getStrategyName();
	        }
    	}
        return retStr;
    }
    
    @SuppressWarnings("unchecked")
    public LitePoint[] getCapBankPointList() {
        List temp = pointDao.getLitePointsByPaObjectId(((YukonPAObject)getDbPersistent()).getPAObjectID().intValue());
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
            if (varPoint != PointTypes.SYS_PID_SYSTEM) {
                pointNameMap.put(varPoint, pointDao.getPointName(varPoint));
            }
            int wattPoint = getControlPoint (PointUnits.UOMID_KW);
            if (wattPoint != PointTypes.SYS_PID_SYSTEM) {
                pointNameMap.put(wattPoint, pointDao.getPointName(wattPoint));      
            }
            int voltPoint = getControlPoint (PointUnits.UOMID_KVOLTS);
            if (voltPoint != PointTypes.SYS_PID_SYSTEM) {
                pointNameMap.put(voltPoint, pointDao.getPointName(voltPoint));
            }
            if (getDbPersistent() instanceof CapControlSubBus) {
                CapControlSubBus sub = (CapControlSubBus) getDbPersistent();
                CapControlSubstationBus subBus = sub.getCapControlSubstationBus();
                int switchPoint = subBus.getSwitchPointID();
                int phaseBPoint = subBus.getPhaseB();
                int phaseCPoint = subBus.getPhaseC();
                int voltReductionPoint = subBus.getVoltReductionPointId();
                pointNameMap.put(switchPoint, pointDao.getPointName(switchPoint));
                pointNameMap.put(phaseBPoint, pointDao.getPointName(phaseBPoint));
                pointNameMap.put(phaseCPoint, pointDao.getPointName(phaseCPoint));
                pointNameMap.put(voltReductionPoint, pointDao.getPointName(voltReductionPoint));
            }else if(getDbPersistent() instanceof CapControlFeeder) {
                CapControlFeeder feeder = (CapControlFeeder) getDbPersistent();
                com.cannontech.database.db.capcontrol.CapControlFeeder feederthinger = feeder.getCapControlFeeder();
                int phaseBPoint = feederthinger.getPhaseB();
                int phaseCPoint = feederthinger.getPhaseC();
                pointNameMap.put(phaseBPoint, pointDao.getPointName(phaseBPoint));
                pointNameMap.put(phaseCPoint, pointDao.getPointName(phaseCPoint));
            }else if(getDbPersistent() instanceof CapControlArea) {
                CapControlArea area = (CapControlArea) getDbPersistent();
                com.cannontech.database.db.capcontrol.CapControlArea areaThinger = area.getCapControlArea();
                int voltReductionPointId = areaThinger.getVoltReductionPointId();
                pointNameMap.put(voltReductionPointId, pointDao.getPointName(voltReductionPointId));
            }else if(getDbPersistent() instanceof CapControlSpecialArea) {
                CapControlSpecialArea area = (CapControlSpecialArea) getDbPersistent();
                com.cannontech.database.db.capcontrol.CapControlSpecialArea areaThinger = area.getCapControlSpecialArea();
                int voltReductionPointId = areaThinger.getVoltReductionPointId();
                pointNameMap.put(voltReductionPointId, pointDao.getPointName(voltReductionPointId));
            }else if(getDbPersistent() instanceof CapControlSubstation) {
                CapControlSubstation sub = (CapControlSubstation) getDbPersistent();
                com.cannontech.database.db.capcontrol.CapControlSubstation subThinger = sub.getCapControlSubstation();
                int voltReductionPointId = subThinger.getVoltReductionPointId();
                pointNameMap.put(voltReductionPointId, pointDao.getPointName(voltReductionPointId));
            }
            pointNameMap.put(0, "(none)");
        }
        return pointNameMap;
     }
     
    public void setPointNameMap (Map<Integer, String> m) {
         pointNameMap =  m;
     }

    public Map<Integer, String> getPaoNameMap () {
        if(paoNameMap == null) {
            paoNameMap = new HashMap<Integer, String>();
            int varPoint = getControlPoint (PointUnits.UOMID_KVAR);
            int wattPoint = getControlPoint(PointUnits.UOMID_KW);
            int voltPoint = getControlPoint(PointUnits.UOMID_KVOLTS);
            if(varPoint != PointTypes.SYS_PID_SYSTEM) {
                paoNameMap.put(varPoint, paoDao.getYukonPAOName(pointDao.getLitePoint(varPoint).getPaobjectID()));
            }
            if(wattPoint != PointTypes.SYS_PID_SYSTEM) {
                paoNameMap.put(wattPoint, paoDao.getYukonPAOName(pointDao.getLitePoint(wattPoint).getPaobjectID()));
            }
            if(voltPoint != PointTypes.SYS_PID_SYSTEM) {
                paoNameMap.put(voltPoint, paoDao.getYukonPAOName(pointDao.getLitePoint(voltPoint).getPaobjectID()));
            }
            if(getDbPersistent() instanceof CapControlSubBus) {
                CapControlSubstationBus subBus = ((CapControlSubBus) getPAOBase()).getCapControlSubstationBus();
                int phaseBPoint = subBus.getPhaseB();
                int phaseCPoint = subBus.getPhaseC();
                int voltReductionPointId = subBus.getVoltReductionPointId();
                int switchPoint = subBus.getSwitchPointID();
                paoNameMap.put(switchPoint, paoDao.getYukonPAOName(pointDao.getLitePoint(switchPoint).getPaobjectID()));
                paoNameMap.put(phaseBPoint, paoDao.getYukonPAOName(pointDao.getLitePoint(phaseBPoint).getPaobjectID()));
                paoNameMap.put(phaseCPoint, paoDao.getYukonPAOName(pointDao.getLitePoint(phaseCPoint).getPaobjectID()));
                paoNameMap.put(voltReductionPointId, paoDao.getYukonPAOName(pointDao.getLitePoint(voltReductionPointId).getPaobjectID()));
            }else if(getDbPersistent() instanceof CapControlFeeder) {
                CapControlFeeder feeder = (CapControlFeeder) getDbPersistent();
                com.cannontech.database.db.capcontrol.CapControlFeeder feederthinger = feeder.getCapControlFeeder();
                int phaseBPoint = feederthinger.getPhaseB();
                int phaseCPoint = feederthinger.getPhaseC();
                paoNameMap.put(phaseBPoint, paoDao.getYukonPAOName(pointDao.getLitePoint(phaseBPoint).getPaobjectID()));
                paoNameMap.put(phaseCPoint, paoDao.getYukonPAOName(pointDao.getLitePoint(phaseCPoint).getPaobjectID()));
            }else if(getDbPersistent() instanceof CapControlArea) {
                CapControlArea area = (CapControlArea) getDbPersistent();
                com.cannontech.database.db.capcontrol.CapControlArea areaThinger = area.getCapControlArea();
                int voltReductionPointId = areaThinger.getVoltReductionPointId();
                paoNameMap.put(voltReductionPointId, paoDao.getYukonPAOName(pointDao.getLitePoint(voltReductionPointId).getPaobjectID()));
            }else if(getDbPersistent() instanceof CapControlSpecialArea) {
                CapControlSpecialArea area = (CapControlSpecialArea) getDbPersistent();
                com.cannontech.database.db.capcontrol.CapControlSpecialArea areaThinger = area.getCapControlSpecialArea();
                int voltReductionPointId = areaThinger.getVoltReductionPointId();
                paoNameMap.put(voltReductionPointId, paoDao.getYukonPAOName(pointDao.getLitePoint(voltReductionPointId).getPaobjectID()));
            }else if(getDbPersistent() instanceof CapControlSubstation) {
                CapControlSubstation sub = (CapControlSubstation) getDbPersistent();
                com.cannontech.database.db.capcontrol.CapControlSubstation subThinger = sub.getCapControlSubstation();
                int voltReductionPointId = subThinger.getVoltReductionPointId();
                paoNameMap.put(voltReductionPointId, paoDao.getYukonPAOName(pointDao.getLitePoint(voltReductionPointId).getPaobjectID()));
            }
            paoNameMap.put(0, "(none)");
        }
        return paoNameMap;
     }
     
    public void setPaoNameMap (Map<Integer, String> m) {
         paoNameMap = m;
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
        List<CapControlStrategy> allCBCStrategies = CapControlStrategy.getAllCBCStrategies();
        List<CapControlStrategy> allStratList = new ArrayList<CapControlStrategy>();
        for (int i = 0; i < allCBCStrategies.size(); i++) {
            CapControlStrategy strategy = allCBCStrategies.get(i);
            if (strategy.getStrategyID().intValue() > 0) {
                allStratList.add(strategy);
            }
        }
        return allStratList;
    }
    
    @SuppressWarnings("unchecked")
    public void editCBCStrat( ActionEvent ev ) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<Integer, String> paramMap = context.getExternalContext().getRequestParameterMap();
        int elemID = Integer.parseInt( paramMap.get("stratID") );
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        CtiNavObject navObject = (CtiNavObject) session.getAttribute("CtiNavObject");
        HttpServletRequest req = (HttpServletRequest)context.getExternalContext().getRequest();
        String currentPage = req.getRequestURL().toString();
        navObject.setPreviousPage(currentPage);
        navObject.getHistory().push(currentPage);
        String location = "/editor/cbcBase.jsf?type=5&itemid="+elemID;
        navObject.setCurrentPage(location);
        try {
            context.getExternalContext().redirect(location);
        } catch (IOException e) {
            CTILogger.error(e);
        }
        FacesContext.getCurrentInstance().responseComplete();
    }
    
    @SuppressWarnings("unchecked")
    public void deleteStrat( ActionEvent ev ) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<Integer, String> paramMap = context.getExternalContext().getRequestParameterMap();
        int elemID = Integer.parseInt( paramMap.get("stratID") );
        initItem(elemID, DBEditorTypes.EDITOR_STRATEGY);
        deleteStrategy();
    }
    
    public boolean isTimeOfDay() {
        String strat = getCbcStrategiesMap().get(getCurrentStrategyID()).getControlMethod();
        if( strat.equalsIgnoreCase(CapControlStrategy.CNTRL_TIME_OF_DAY)){
            return true;
        }
        return false;
    }
    
    public void resetTabIndex() {
        specialAreaTab = -1;
        areaTab = -1;
        substationTab = -1;
        subBusTab = -1;
        feederTab = -1;
        capbankTab = -1;
    }
    
    /**
     * Returns true if the user has the CapControl Settings > Database Editing role property.
     * @return
     */
    public boolean isEditingAuthorized() {
        return rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, JSFParamUtil.getYukonUser());
    }
    
    public CCStrategyTimeOfDaySet getStrategyTimeOfDay() {
        if(strategyTimeOfDay == null) {
            strategyTimeOfDay = new CCStrategyTimeOfDaySet(getCurrentStrategyID());
        }
        return strategyTimeOfDay;
    }
    
    public void setStrategyTimeOfDay(CCStrategyTimeOfDaySet strategyTimeOfDay) {
        this.strategyTimeOfDay = strategyTimeOfDay;
    }

    public void setScheduleId(Integer id) {
        this.scheduleId = id;
    }
    
    public void setHolidayScheduleId(Integer id) {
        this.holidayScheduleId = id;
    }
    
    public void setSeasonScheduleDao(SeasonScheduleDao seasonScheduleDao) {
        this.seasonScheduleDao = seasonScheduleDao;
    }
    
    public void setHolidayScheduleDao(HolidayScheduleDao holidayScheduleDao) {
        this.holidayScheduleDao = holidayScheduleDao;
    }

	public List<LiteYukonPAObject> getUnassignedSubBuses() {
		return unassignedSubBuses;
	}

	public void setUnassignedSubBuses(List<LiteYukonPAObject> unassignedSubBuses) {
		this.unassignedSubBuses = unassignedSubBuses;
	}
	
}