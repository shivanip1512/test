package com.cannontech.web.editor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.capcontrol.ControlAlgorithm;
import com.cannontech.capcontrol.ControlMethod;
import com.cannontech.capcontrol.creation.service.CapControlCreationService;
import com.cannontech.capcontrol.dao.CapbankControllerDao;
import com.cannontech.capcontrol.dao.CapbankDao;
import com.cannontech.capcontrol.dao.FeederDao;
import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.capcontrol.dao.SubstationBusDao;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.exceptions.CBCExceptionMessages;
import com.cannontech.cbc.exceptions.FormWarningException;
import com.cannontech.cbc.exceptions.MultipleDevicesOnPortException;
import com.cannontech.cbc.exceptions.PortDoesntExistException;
import com.cannontech.cbc.exceptions.SameMasterSlaveCombinationException;
import com.cannontech.cbc.exceptions.SerialNumberExistsException;
import com.cannontech.cbc.model.EditorDataModel;
import com.cannontech.cbc.model.ICBControllerModel;
import com.cannontech.cbc.model.ICapControlModel;
import com.cannontech.cbc.service.CapControlCreationModel;
import com.cannontech.cbc.util.CapControlUtils;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.ConfigurationBase;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.StringUtils;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.HolidayScheduleDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PaoScheduleDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.SeasonScheduleDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.capcontrol.CCYukonPAOFactory;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.capcontrol.CapBankController702x;
import com.cannontech.database.data.capcontrol.CapBankControllerDNP;
import com.cannontech.database.data.capcontrol.CapControlArea;
import com.cannontech.database.data.capcontrol.CapControlFeeder;
import com.cannontech.database.data.capcontrol.CapControlSpecialArea;
import com.cannontech.database.data.capcontrol.CapControlSubBus;
import com.cannontech.database.data.capcontrol.CapControlSubstation;
import com.cannontech.database.data.capcontrol.ICapBankController;
import com.cannontech.database.data.capcontrol.VoltageRegulator;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.TwoWayDevice;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.CapControlTypes;
import com.cannontech.database.data.pao.DBEditorTypes;
import com.cannontech.database.data.pao.PAOFactory;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.capcontrol.CCFeederBankList;
import com.cannontech.database.db.capcontrol.CCFeederSubAssignment;
import com.cannontech.database.db.capcontrol.CCSubstationSubBusList;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.database.db.capcontrol.CapControlSubstationBus;
import com.cannontech.database.db.capcontrol.PeakTargetSetting;
import com.cannontech.database.db.capcontrol.StrategyPeakSettingsHelper;
import com.cannontech.database.db.device.DeviceScanRate;
import com.cannontech.database.db.holiday.HolidaySchedule;
import com.cannontech.database.db.pao.PAOSchedule;
import com.cannontech.database.db.pao.PAOScheduleAssign;
import com.cannontech.database.db.season.SeasonSchedule;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.database.model.Season;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.servlet.nav.CBCNavigationUtil;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.editor.data.CBCSpecialAreaData;
import com.cannontech.web.editor.model.CBCSpecialAreaDataModel;
import com.cannontech.web.editor.model.DataModelFactory;
import com.cannontech.web.exceptions.AltBusNeedsSwitchPointException;
import com.cannontech.web.navigation.CtiNavObject;
import com.cannontech.web.util.CBCDBUtil;
import com.cannontech.web.util.CBCSelectionLists;
import com.cannontech.web.util.JSFParamUtil;
import com.cannontech.web.util.JSFUtil;
import com.cannontech.web.wizard.CBCWizardModel;
import com.google.common.collect.Lists;

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
	private boolean hasEditingRole = false;
	private int itemId = -1;
	private int editorType = -1;
	private HashMap<Integer, CapControlStrategy> cbcStrategiesMap;
	private HashMap<Integer, CapControlStrategy> cbcHolidayStrategiesMap;
	private List<LiteYukonPAObject> unassignedBanks;
	private List<LiteYukonPAObject> unassignedFeeders;
	private List<LiteYukonPAObject> unassignedSubBuses;
	private CapControlCreationModel wizData;
	private ICBControllerModel cbControllerEditor;
	private PointTreeForm pointTreeForm;
	private List<SelectItem> cbcStrategies;
	protected List<LiteYukonPAObject> subBusList;
    private Integer oldSubBus;
    private Boolean enableDualBus = Boolean.FALSE;
	private boolean isDualSubBusEdited;
    private Map<Integer, String> paoNameMap;
    private Map<Integer, String> pointNameMap;
    private Map<Season, Integer> assignedStratMap;
    private EditorDataModel dataModel;
    private SeasonScheduleDao seasonScheduleDao;
    private HolidayScheduleDao holidayScheduleDao;
    private Integer scheduleId = -1000;
    private Integer holidayScheduleId = -1000;
    private Integer holidayStrategyId = -1000;
    private CapControlCreationService capControlCreationService;
    private CapbankControllerDao capbankControllerDao;
    private CapControlCache capControlCache;
    private DeviceConfigurationDao deviceConfigurationDao;
    private StrategyDao strategyDao;
    private PaoScheduleDao paoScheduleDao;
    private CapbankDao capbankDao;
    private FeederDao feederDao;
    private SubstationBusDao substationBusDao;
    private PointDao pointDao;
    private PaoDao paoDao;
    private RolePropertyDao rolePropertyDao;
    private CBCSelectionLists selectionLists;
    private NextValueHelper nextValueHelper;
    private TransactionTemplate transactionTemplate;
    
    Logger log = YukonLogManager.getLogger(CapControlForm.class);
    private ControlAlgorithm currentControlAlgorithm;
    private ControlMethod currentControlMethod;
    
    public ConfigurationBase getDefaultDnpConfiguration() {
        ConfigurationBase configurationBase = deviceConfigurationDao.getDefaultDNPConfiguration();
        return configurationBase;
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
        if(holidayStrategyId == null || holidayStrategyId < -1) {
            Integer paoId = ((YukonPAObject)getDbPersistent()).getPAObjectID();
            holidayStrategyId = holidayScheduleDao.getStrategyForPao(paoId);
        }
        return holidayStrategyId;
    }
    
    public void setHolidayStrategyId(Integer holidayStrategyId) {
        this.holidayStrategyId = holidayStrategyId;
    }
    
    public List<SelectItem> getCbcStrategies() {
		if (cbcStrategies == null) {
			List<CapControlStrategy> cbcDBStrats = strategyDao.getAllStrategies();
			cbcStrategies = Lists.newArrayList();
			cbcStrategies.add(new SelectItem(-1, "(none)"));
			for (CapControlStrategy strategy : cbcDBStrats) {
				cbcStrategies.add(new SelectItem(strategy.getStrategyID(), strategy.getStrategyName()));
				getCbcStrategiesMap().put(strategy.getStrategyID(), strategy);
			}
		}
		return cbcStrategies;
	}
    
    public List<SelectItem> getCbcHolidayStrategies() {
        List<SelectItem> cbcHolidayStrategies;
        List<CapControlStrategy> cbcDBStrats = strategyDao.getAllStrategies();
        cbcHolidayStrategies = Lists.newArrayList();
        for (CapControlStrategy strategy : cbcDBStrats) {
            cbcHolidayStrategies.add(new SelectItem(strategy.getStrategyID(), strategy.getStrategyName()));
            getCbcStrategiesMap().put(strategy.getStrategyID(), strategy);
        }
        return cbcHolidayStrategies;
    }
    
    public List<SelectItem> getCbcSchedules() {
        List<SelectItem> cbcSchedules;
        SeasonSchedule[] cbcDBSchedules = SeasonSchedule.getAllCBCSchedules();
        cbcSchedules = Lists.newArrayList();
        for (SeasonSchedule schedule : cbcDBSchedules) {
            cbcSchedules.add(new SelectItem(schedule.getScheduleId(), schedule.getScheduleName()));
        }
        return cbcSchedules;
    }
    
    public List<SelectItem> getCbcHolidaySchedules() {
        List<SelectItem> cbcHolidaySchedules;
        List<HolidaySchedule> cbcDBSchedules = holidayScheduleDao.getAllHolidaySchedules();
        cbcHolidaySchedules = Lists.newArrayList();
        for (HolidaySchedule schedule : cbcDBSchedules) {
            cbcHolidaySchedules.add(new SelectItem(schedule.getHolidayScheduleId(), schedule.getHolidayScheduleName()));
        }
        return cbcHolidaySchedules;
    }
    
    public Map<Season, Integer> getAssignedStratMap(){
        if(assignedStratMap == null) {
            HashMap<Season, Integer> map = (HashMap<Season, Integer>) seasonScheduleDao.getUserFriendlySeasonStrategyAssignments(((YukonPAObject)getDbPersistent()).getPAObjectID());
            List<Season> seasons = seasonScheduleDao.getUserFriendlySeasonsForSchedule(getScheduleId());
            for(Season season : seasons) {
                if (!map.containsKey(season)) {
                    map.put(season, -1);
                }
            }
            assignedStratMap = map;
        }
        
        return assignedStratMap;
    }
    
    public HashMap<Integer, String> getStrategyNameMap(){
        HashMap<Integer, String> map = new HashMap<Integer, String>();
        List<CapControlStrategy> strats = strategyDao.getAllStrategies();
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

	public int getCurrentStrategyID() {
		int stratID = CtiUtilities.NONE_ZERO_ID;
        if (getDbPersistent() instanceof CapControlStrategy) {
            stratID = ((CapControlStrategy)getDbPersistent()).getStrategyID();
        }
		return stratID;
	}
    
    public String getCurrentStratName() {
        return getStrategy().getStrategyName();
    }
    
    public void newStrategySelected (ValueChangeEvent vce) {
    }
    
    public void newScheduleSelected (ValueChangeEvent vce) {
        assignedStratMap = null;
    }
    
    public void newHolidayScheduleSelected (ValueChangeEvent vce) {
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
	
	public void substationBusNoDisablePointClicked(ActionEvent ae) {
        String val = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("disablePtId");
        if (val == null) {
            return;
        }
        CapControlSubstationBus sub = ((CapControlSubBus) getPAOBase()).getCapControlSubstationBus();
        sub.setDisableBusPointId(new Integer(val));
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
		}
		setDualSubBusEdited(true);
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
	    clearBeanState();
		DBPersistent dbObj = null;
        
		editorType = type;
		
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
	 * Initialize our current DBPersistent object from the database
	 */
	protected void initItem() {
		if (retrieveDBPersistent() == null) {
			return;
        }

        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest)context.getRequest();
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        hasEditingRole = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, userContext.getYukonUser());

        scheduleId = -1000;
        holidayScheduleId = -1000;
        holidayStrategyId = -1000;
		// decide what editor type should be used
		if (getDbPersistent() instanceof YukonPAObject) {
			YukonPAObject pao = (YukonPAObject) getDbPersistent();
		    itemId = pao.getPAObjectID().intValue();
           
			if (getDbPersistent() instanceof CapBankController 
                   || getDbPersistent() instanceof CapBankController702x 
                   || getDbPersistent() instanceof CapBankControllerDNP) {
               setEditingController( true );
           } else {
               setEditingController(false);
           }
           
           if(getDbPersistent() instanceof CapBank) {
               CapBankEditorForm editor = (CapBankEditorForm) JSFParamUtil.getJSFVar("capBankEditor");
               editor.resetForm();
               editor.init(getPAOBase());
           }
           
           initDataModel(pao);
           initPanels(PAOGroups.getPAOType(pao.getPAOCategory(), pao.getPAOType()));
           
        } else if (getDbPersistent() instanceof PointBase) {
            
            PointBase point = (PointBase) getDbPersistent();
			itemId = point.getPoint().getPointID().intValue();
			initPanels(PointTypes.getType(point.getPoint().getPointType()));
			
		} else if (getDbPersistent() instanceof PAOSchedule) {
		    
		    PAOSchedule schedule = (PAOSchedule) getDbPersistent();
			itemId = schedule.getScheduleID().intValue();
            initPanels(CapControlTypes.CAP_CONTROL_SCHEDULE);
            
		} else if (getDbPersistent() instanceof CapControlStrategy) {
		    
            CapControlStrategy strat = (CapControlStrategy)getDbPersistent();
            itemId = strat.getStrategyID().intValue();
            editingCBCStrategy = true;
            currentControlAlgorithm = strat.getControlUnits();
            currentControlMethod = strat.getControlMethod();
            initPanels(CapControlTypes.CAP_CONTROL_STRATEGY);
        }
		
		/* Restore the value of the dual bus from DB */
		resetDualBusEnabled();
        if (!(getDbPersistent() instanceof CapControlStrategy)) {
            editingCBCStrategy = false;
        }
        initEditorPanels();
        getPointTreeForm().init(itemId);
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
	
	/**
	 * Resets the bean's state holding member fields.
	 */
	private void clearBeanState() {
	    assignedStratMap = null;
        cbControllerEditor = null;
        cbcStrategies = null;
        cbcStrategiesMap = null;
        editingCBCStrategy = false;
        editingController = false;
        isDualSubBusEdited = false;
        pointTreeForm = null; 
        scheduleId = -1000;
        hasEditingRole = false;
        holidayScheduleId = -1000;
        holidayStrategyId = -1000;
        unassignedBanks = null;
        unassignedFeeders = null;
        unassignedSubBuses = null;
	}

	/**
	 * Resets bean objects and flags.
	 */
	@Override
    public void resetForm() {
	    clearBeanState();
	    initItem(getItemId(), getEditorType());
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
        getVisibleTabs().put("Regulator", Boolean.FALSE);

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
                    boolean showCapBankAddInfo = CapControlUtils.isCBAdditionalInfoAllowed(user);
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
            case PAOGroups.CBC_8020:
            case PAOGroups.CBC_8024:
            case PAOGroups.CBC_DNP:
    			setEditorTitle("CBC");
    			setPaoDescLabel(null);
    			getVisibleTabs().put("CBCType", Boolean.TRUE);
    			getVisibleTabs().put("CBCController", Boolean.TRUE);
    			break;
    
    		case CapControlTypes.CAP_CONTROL_SCHEDULE:
    			setEditorTitle("Schedule");
    			getVisibleTabs().put("GeneralPAO", Boolean.FALSE);
    			getVisibleTabs().put("GeneralSchedule", Boolean.TRUE);
    			getVisibleTabs().put("CBCSchedule", Boolean.TRUE);
    			break;
                
            case CapControlTypes.CAP_CONTROL_STRATEGY:
                setEditorTitle("Strategy");
                getVisibleTabs().put("GeneralPAO", Boolean.FALSE);
                getVisibleTabs().put("CBCStrategy", Boolean.TRUE);
                break;
            
            case CapControlTypes.CAP_CONTROL_LTC:
            case CapControlTypes.GANG_OPERATED_REGULATOR:
            case CapControlTypes.PHASE_OPERATED_REGULATOR:
                setEditorTitle("Regulator");
                setPaoDescLabel("Description");
                getVisibleTabs().put("Regulator", Boolean.TRUE);
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
	
	public VoltageRegulator getRegulatorBase(){
	    return (VoltageRegulator) getDbPersistent();
	}

    public void clearfaces() {
        FacesMessage facesMessage = new FacesMessage();
        facesMessage.setDetail("");   
        FacesContext currentInstance = FacesContext.getCurrentInstance();
        currentInstance.addMessage("cti_db_update", facesMessage);
    }
    
    public boolean checkStrategy(CapControlStrategy strat) {
        boolean integratedControl = strat.getIntegrateFlag().equalsIgnoreCase("Y");
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
                CapControlStrategy currentStrategy = getStrategy();
                boolean ok = checkStrategy(currentStrategy);
                if(ok) {
                    updateDBObject(currentStrategy, facesMsg);
    				cbcStrategies = null;
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
                    log.error(errorString, e);
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
                CapControlStrategy strategy = getStrategy();
                boolean ok = checkStrategy(strategy);
                if(ok) {
                    setEditingCBCStrategy(false);
                    updateDBObject(strategy, facesMsg);
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
                
                if(getHolidayScheduleId() != -1 && getHolidayStrategyId() < 1){
                    //if a holiday schedule is selected, a strategy must also be selected
                    facesMsg.setDetail("Holiday Strategy: strategy not selected.");
                    facesMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                } else if(dataModelOK) {
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
                    SubStation substation = capControlCache.getSubstation(data.getSubID());
                    if(substation.getSpecialAreaEnabled() && substation.getSpecialAreaId().intValue() != area.getPAObjectID().intValue()) {
                        duplicates.add(capControlCache.getCBCSpecialArea(substation.getSpecialAreaId()).getCcName() + ": " + substation.getCcName());
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
     * Returns the JSF page id to start editing from.
     * @param type
     * @return
     */
    private int getEditorType(int type) {
        if (type == CapControlTypes.CAP_CONTROL_SCHEDULE) {
            return DBEditorTypes.EDITOR_SCHEDULE;
        }else if (type == CapControlTypes.CAP_CONTROL_STRATEGY) {
            return DBEditorTypes.EDITOR_STRATEGY;
        }else {
            return DBEditorTypes.EDITOR_CAPCONTROL;
        }
    }

    /**
     * Creates a cap control object, strategy, or schedule.
     * @Return String the url to go when done.
     */
    public String create() {
        FacesMessage facesMsg = new FacesMessage();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        
        final CBCWizardModel wizard = (CBCWizardModel) getWizData();
        final String name = wizard.getName();
        if(org.apache.commons.lang.StringUtils.isBlank(name)) {
            facesMsg.setDetail("A name must be specified for this object.");
            facesMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            facesContext.addMessage("cti_db_add", facesMsg);
            return "";
        }
        final int type = wizard.getSelectedType();
        final boolean disabled = wizard.getDisabled();
        final int portId = wizard.getPortID();
        final boolean isCapBankAndNested = (type == CapControlTypes.CAP_CONTROL_CAPBANK && wizard.isCreateNested());
        
        try {
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    //If this is a Schedule or Strategy it is NOT a Pao, handle accordingly.
                    if (type == CapControlTypes.CAP_CONTROL_SCHEDULE) {
                        itemId = paoScheduleDao.add(name, disabled);
                        return;
                    } else if (type == CapControlTypes.CAP_CONTROL_STRATEGY) {
                        itemId = strategyDao.add(name);
                        return;
                    }
                    
                    //Must be a Pao
                    PaoType paoType = PaoType.getForId(type);
                    boolean isController = CapControlUtils.checkControllerByType(paoType);
                    
                    if (isCapBankAndNested) {
                        /* Create the cbc, then the cap bank, then assign cbc to cap bank */
                        CBCWizardModel cbcWizard = wizard.getNestedWizard();
                        PaoType cbcType = PaoType.getForId(cbcWizard.getSelectedType());
                        boolean cbcDisabled = cbcWizard.getDisabled();
                        String cbcName = cbcWizard.getName();
                        int cbcPortId = cbcWizard.getPortID();
                        PaoIdentifier controller = capControlCreationService.createCbc(cbcType, cbcName, cbcDisabled, cbcPortId, getDefaultDnpConfiguration());
                        PaoIdentifier capbank = capControlCreationService.createCapControlObject(paoType, name, disabled);
                        int controllerId = controller.getPaoId();
                        int tempItemId = capbank.getPaoId();
                        capbankControllerDao.assignController(tempItemId, controllerId);
                        itemId = tempItemId;
                    } else if (isController) {
                        PaoType cbcType = PaoType.getForId(wizard.getSelectedType());
                	    PaoIdentifier item = capControlCreationService.createCbc(cbcType, name, disabled, portId, getDefaultDnpConfiguration());
                	    itemId = item.getPaoId();
                    } else {
                        PaoIdentifier item = capControlCreationService.createCapControlObject(paoType, name, disabled);
                        itemId = item.getPaoId();
                    }
                }
            });
            
            /* Redirect to the editor after creation */
            facesMsg.setDetail("Database add was SUCCESSFUL");
            String url = "/editor/cbcBase.jsf?type=" + getEditorType(type) + "&itemid=" + itemId;
            
            /* Sets the navigation history so the return buttons work */
            HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
            CBCNavigationUtil.redirect(url, session);
            
            /* Does the actually redirection to the editor url */
            JSFUtil.redirect(url);
            
        }catch (DataIntegrityViolationException e) { //TODO do something smarter with this
            facesMsg.setDetail(e.getMessage());
            facesMsg.setSeverity(FacesMessage.SEVERITY_ERROR);

            if (isCapBankAndNested) {
                String cbcName = wizard.getNestedWizard().getName();
                if (name.equalsIgnoreCase(cbcName)) {
                    facesMsg.setDetail("ERROR - Cannot create new Capacitor Bank and CBC with the same name. " + e.getMessage());
                }
            }

            return "";
        } catch (Exception e) {
            facesMsg.setDetail(e.getCause().getMessage());
            facesMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            return "";
        } finally {
            facesContext.addMessage("cti_db_add", facesMsg);
        }
        
        return "";
	}

    public void showScanRate(ValueChangeEvent ev) {
		if (ev == null || ev.getNewValue() == null) {
			return;
        }
		Boolean isChecked = (Boolean) ev.getNewValue();
        if (isControllerCBC() && getCBControllerEditor().isTwoWay()) {
            TwoWayDevice twoWayDev = (TwoWayDevice) getCBControllerEditor().getPaoCBC();
    		String type = ev.getComponent().getId().equalsIgnoreCase("scanIntegrityChk") ? DeviceScanRate.TYPE_INTEGRITY
    			: (ev.getComponent().getId().equalsIgnoreCase("scanExceptionChk") ? DeviceScanRate.TYPE_EXCEPTION : "");
    		if (isChecked.booleanValue()) {
    			twoWayDev.getDeviceScanRateMap().put(type, new DeviceScanRate(twoWayDev.getPAObjectID(), type));
    		} else {
    			twoWayDev.getDeviceScanRateMap().remove(type);
    		}
        }
	}
    
	public PointTreeForm getPointTreeForm() {
        if (pointTreeForm == null) {
            int paoId = itemId;         
            pointTreeForm = new PointTreeForm(paoId);
        }
        return pointTreeForm;
    }
	
	public void setPointTreeForm(PointTreeForm pointTreeForm) {
	    this.pointTreeForm = pointTreeForm;
    }

	public ICBControllerModel getCBControllerEditor() {
		if (cbControllerEditor == null) {
            int paoId = itemId;			
            if ((getDbPersistent() instanceof CapBankController702x) || (getDbPersistent() instanceof CapBankController) || (getDbPersistent() instanceof CapBankControllerDNP)){
                setEditingController(true);
                cbControllerEditor = new CBControllerEditor(paoId);
                cbControllerEditor.retrieveDB();
                cbControllerEditor.resetSerialNumber();
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
        CapControlStrategy ccStrat = CCYukonPAOFactory.createCapControlStrategy();
		Integer newId = nextValueHelper.getNextValue(CapControlStrategy.TABLE_NAME);
		ccStrat.setStrategyID(newId);
		ccStrat.setStrategyName("Strat #" + newId + " (New)");
		// this message will be filled in by the super class
		FacesMessage facesMsg = new FacesMessage();
		try {
			addDBObject(ccStrat, facesMsg);
			// clear out the memory of the any list of Strategies
			cbcStrategies = null;
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

	public void deleteStrategy() {
	    FacesContext context = FacesContext.getCurrentInstance(); 
		FacesMessage facesMsg = new FacesMessage();
		int stratID = CtiUtilities.NONE_ZERO_ID;
		try {
			// cancel any editing of the Strategy we may have been doing
			setEditingCBCStrategy(false);
            if (getDbPersistent() instanceof CapControlStrategy) {
                stratID = ((CapControlStrategy)getDbPersistent()).getStrategyID().intValue();
            }
			// decide if we need to do any special handling of this transaction based on what other PAOs use this Strategy
            
			List<String> otherPaosUsingStrategy = strategyDao.getAllOtherPaoNamesUsingStrategyAssignment(stratID, itemId);
			if (otherPaosUsingStrategy.isEmpty()) {
				// update the current PAOBase object just in case it uses the strategy we are deleting
				updateDBObject(getDbPersistent(), null);
				deleteDBObject(getCbcStrategiesMap().get(new Integer(stratID)), facesMsg);
				// clear out the memory of the any list of Strategies
				cbcStrategies = null;
				facesMsg.setDetail("CapControl Strategy delete was SUCCESSFUL");
				HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
				CtiNavObject navObject = (CtiNavObject) session.getAttribute(ServletUtil.NAVIGATE); 
				String moduleExitPage = navObject.getModuleExitPage();
                JSFUtil.redirect(moduleExitPage);
			} else {
				if(otherPaosUsingStrategy.size() > 4) {
				    facesMsg.setDetail("Strategy used by: " + otherPaosUsingStrategy.get(0) 
				                       + ", " +otherPaosUsingStrategy.get(1)
				                       + ", " +otherPaosUsingStrategy.get(2)
				                       + ", " +otherPaosUsingStrategy.get(3) + " ..." + Integer.toString(otherPaosUsingStrategy.size() - 4) + " more.");
				} else {
				    facesMsg.setDetail("Strategy used by: " + otherPaosUsingStrategy);
				}
				facesMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
			}
		} catch (TransactionException te) {
			facesMsg.setDetail("Unable to delete the Strategy: " + te.getMessage());
            facesMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
		} finally {
		    context.addMessage("cti_db_delete", facesMsg);
		}
	}

	public String getPAODescLabel() {
		return paoDescLabel;
	}

	public String getParent() {
		int parentID = CtiUtilities.NONE_ZERO_ID;
		if (getDbPersistent() instanceof CapControlFeeder) {
		    try{
		        parentID = feederDao.getParentSubBusID(itemId);
		    }catch( EmptyResultDataAccessException e ){
		        //do nothing
		    }
		} else if (getDbPersistent() instanceof CapBank) {
		    try{
		        parentID = capbankDao.getParentFeederIdentifier(itemId).getPaoId();
            }catch( EmptyResultDataAccessException e ){
                //do nothing
            }
        } else if (getDbPersistent() instanceof CapControlSubBus) {
            try{
                parentID = capControlCache.getSubBus(itemId).getParentID();
            }catch( NotFoundException e ){
                //do nothing
                parentID = 0;
            }
        } else if (getDbPersistent() instanceof CapControlSubstation) {
            try{
                parentID = capControlCache.getSubstation(itemId).getParentID();
            }catch( NotFoundException e ){
                //do nothing
                parentID = 0;
            }
        } else if (getDbPersistent() instanceof ICapBankController) {
    	    PaoIdentifier capbank = capbankDao.findCapBankIdByCBC(itemId);
    	    parentID = (capbank != null) ? capbank.getPaoId() : 0;
        }
		if (parentID == CtiUtilities.NONE_ZERO_ID) {
			return CtiUtilities.STRING_NONE;
        } else {
            try {
    			LiteYukonPAObject parentPAO = paoDao.getLiteYukonPAO(parentID);
    			return parentPAO.getPaoName() + "   (" + parentPAO.getPaoType().getDbString() + ",  id: " + parentPAO.getLiteID() + ")";
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
                        CCFeederBankList bankList = new CCFeederBankList(itemId,elemID,order,order,order);
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
                        CCFeederSubAssignment sa = new CCFeederSubAssignment(elemID,itemId,(int)maxDispOrderOnList (currSub.getChildList())+ 1); 
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
                        CCSubstationSubBusList sa = new CCSubstationSubBusList(itemId,elemID,(int)maxDispOrderOnList (currSub.getChildList())+ 1); 
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
    	if (childList.size() > 0 && removeDispOrder >= 1  && removeCloseOrder >= 1 && removeTripOrder >= 1) {
    		Collections.sort(childList, CapControlUtils.BANK_TRIP_ORDER_COMPARATOR);
    		for (int i = 0; i < childList.size(); i++) {
    			CCFeederBankList capBank = childList.get(i);
    			capBank.setTripOrder(new Float(i + 1.0));
    		}
    		
    		Collections.sort(childList, CapControlUtils.BANK_CLOSE_ORDER_COMPARATOR);
    		for (int i = 0; i < childList.size(); i++) {
    			CCFeederBankList capBank = childList.get(i);
    			capBank.setCloseOrder(new Float(i + 1.0));
    		}
    		
    		Collections.sort(childList, CapControlUtils.BANK_DISPLAY_ORDER_COMPARATOR);
    		for (int i = 0; i < childList.size(); i++) {
    			CCFeederBankList capBank = childList.get(i);
    			capBank.setControlOrder(new Float(i + 1.0));
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

	public boolean isHasEditingRole() {
        return hasEditingRole;
    }

    public void setHasEditingRole(boolean hasEditingRole) {
        this.hasEditingRole = hasEditingRole;
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
    				return paoDao.getLiteYukonPAO(paoID).getPaoType().isCbc();
                }
    		}
    	} else if (getDbPersistent() instanceof YukonPAObject){
            // support for the TwoWay devices
            int paoID = ((YukonPAObject)getDbPersistent()).getPAObjectID().intValue();
            return paoDao.getLiteYukonPAO(paoID).getPaoType().isCbc();
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
			return strat != null && ControlAlgorithm.VOLTS.getDisplayName().equals(strat.getControlUnits());
		} else {
			return false;
        }
	}

    public LiteYukonPAObject[] getSubBusList() {
		if (subBusList == null) {
			subBusList = DaoFactory.getCapControlDao().getAllSubsForUser (JSFParamUtil.getYukonUser());
		}
		return subBusList.toArray(new LiteYukonPAObject[subBusList.size()]);
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
		return retString;
	}

    public Boolean getEnableDualBus() {
        return enableDualBus;
    }

    public void setEnableDualBus(Boolean enableDualBus) {
        this.enableDualBus = enableDualBus;
        updateDualBusEnabled();         
        setDualSubBusEdited(true);
    }
    
	public boolean isDualSubBusEdited() {
		return isDualSubBusEdited;
	}

	public void setDualSubBusEdited(boolean isDualSubBusEdited) {
		this.isDualSubBusEdited = isDualSubBusEdited;
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

	//delegate to this class because generic class doesn't have to know about business rules	
	public SelectItem[] getControlMethods () {
	    List<SelectItem> controlMethods = Lists.newArrayList();
	    for(ControlMethod controlMethod : ControlMethod.valuesForDisplay()) {
	        controlMethods.add(new SelectItem(controlMethod, controlMethod.getDisplayName()));
	    }
	    return controlMethods.toArray(new SelectItem[0]);
	    
	}
    
    @SuppressWarnings("unchecked")
    public Map getPointNameMap () {

        pointNameMap = new HashMap();
        List<Integer> pointIds = new ArrayList<Integer>();
        
        pointIds.add(getControlPoint(UnitOfMeasure.KVAR.getId()));
        pointIds.add(getControlPoint(UnitOfMeasure.KW.getId()));
        pointIds.add(getControlPoint(UnitOfMeasure.KVOLTS.getId()));

        if (getDbPersistent() instanceof CapControlSubBus) {
            CapControlSubBus sub = (CapControlSubBus) getDbPersistent();
            CapControlSubstationBus subBus = sub.getCapControlSubstationBus();
            pointIds.add(subBus.getSwitchPointID());
            pointIds.add(subBus.getPhaseB());
            pointIds.add(subBus.getPhaseC());
            pointIds.add(subBus.getVoltReductionPointId());
            pointIds.add(subBus.getDisableBusPointId());            
        }else if(getDbPersistent() instanceof CapControlFeeder) {
            CapControlFeeder feeder = (CapControlFeeder) getDbPersistent();
            pointIds.add(feeder.getCapControlFeeder().getPhaseB());
            pointIds.add(feeder.getCapControlFeeder().getPhaseC());
        }else if(getDbPersistent() instanceof CapControlArea) {
            CapControlArea area = (CapControlArea) getDbPersistent();
            pointIds.add(area.getCapControlArea().getVoltReductionPointId());
        }else if(getDbPersistent() instanceof CapControlSpecialArea) {
            CapControlSpecialArea area = (CapControlSpecialArea) getDbPersistent();
            pointIds.add(area.getCapControlSpecialArea().getVoltReductionPointId());
        }else if(getDbPersistent() instanceof CapControlSubstation) {
            CapControlSubstation sub = (CapControlSubstation) getDbPersistent();
            pointIds.add(sub.getCapControlSubstation().getVoltReductionPointId());
        }
        
        for (Integer pointId : pointIds) {
            setPointNameInMap(pointNameMap,pointId);
        }
        pointNameMap.put(0, "(none)");

        return pointNameMap;
     }
    
    private void setPointNameInMap(Map<Integer,String> map, int pointId) {
        map.put(pointId, pointDao.getPointName(pointId));
    }

    public Map<Integer, String> getPaoNameMap () {
        
        paoNameMap = new HashMap<Integer, String>();
        List<Integer> pointIds = new ArrayList<Integer>();
        
        pointIds.add(getControlPoint(UnitOfMeasure.KVAR.getId()));
        pointIds.add(getControlPoint(UnitOfMeasure.KW.getId()));
        pointIds.add(getControlPoint(UnitOfMeasure.KVOLTS.getId()));
        
        if(getDbPersistent() instanceof CapControlSubBus) {
            CapControlSubstationBus subBus = ((CapControlSubBus) getPAOBase()).getCapControlSubstationBus();
            pointIds.add(subBus.getPhaseB());
            pointIds.add(subBus.getPhaseC());
            pointIds.add(subBus.getVoltReductionPointId());
            pointIds.add(subBus.getDisableBusPointId());
            pointIds.add(subBus.getSwitchPointID());
        } else if(getDbPersistent() instanceof CapControlFeeder) {
            CapControlFeeder feeder = (CapControlFeeder) getDbPersistent();
            pointIds.add(feeder.getCapControlFeeder().getPhaseB());
            pointIds.add(feeder.getCapControlFeeder().getPhaseC());
        } else if(getDbPersistent() instanceof CapControlArea) {
            CapControlArea area = (CapControlArea) getDbPersistent();
            pointIds.add(area.getCapControlArea().getVoltReductionPointId());
        } else if(getDbPersistent() instanceof CapControlSpecialArea) {
            CapControlSpecialArea area = (CapControlSpecialArea) getDbPersistent();
            pointIds.add(area.getCapControlSpecialArea().getVoltReductionPointId());
        } else if(getDbPersistent() instanceof CapControlSubstation) {
            CapControlSubstation sub = (CapControlSubstation) getDbPersistent();
            pointIds.add(sub.getCapControlSubstation().getVoltReductionPointId());
        }
        
        for( Integer pointId : pointIds) {
            setPaoNameInMap(paoNameMap,pointId);
        }
        //Make 0 a (none)
        paoNameMap.put(0, "(none)");

        return paoNameMap;
     }

    private void setPaoNameInMap(Map<Integer,String> map, int pointId) {
        map.put(pointId, paoDao.getYukonPAOName(pointDao.getLitePoint(pointId).getPaobjectID()));
    }
     
    private int getControlPoint(int uomid) {
        int pointID = 0;
        UnitOfMeasure uom = UnitOfMeasure.getForId(uomid);
        if (getPAOBase() instanceof CapControlSubBus) {
            CapControlSubstationBus sub = ((CapControlSubBus) getPAOBase()).getCapControlSubstationBus();
            switch (uom) {
                case KVAR:
                    pointID = sub.getCurrentVarLoadPointID();
                    break;                        
                case KW:
                    pointID = sub.getCurrentWattLoadPointID();
                    break;
                case KVOLTS: 
                    pointID = sub.getCurrentVoltLoadPointID();
                    break;
            }
        }
        if (getPAOBase() instanceof CapControlFeeder) {
            com.cannontech.database.db.capcontrol.CapControlFeeder feeder = ((CapControlFeeder) getPAOBase()).getCapControlFeeder();
            switch (uom) {
                case KVAR:
                    pointID = feeder.getCurrentVarLoadPointID();
                    break;                        
                case KW:
                    pointID = feeder.getCurrentWattLoadPointID();
                    break;
                case KVOLTS: 
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
        return strategyDao.getAllStrategies();
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
            log.error(e);
        }
        FacesContext.getCurrentInstance().responseComplete();
    }
    
    public boolean isTimeOfDay() {
        boolean timeOfDay = getStrategy().isTimeOfDay();
        return timeOfDay;
    }
    
    public void resetTabIndex() {
        specialAreaTab = -1;
        areaTab = -1;
        substationTab = -1;
        subBusTab = -1;
        feederTab = -1;
        capbankTab = -1;
    }
    
    public List<SelectItem> getControlAlgorithims() {
        ControlMethod currentMethod = getStrategy().getControlMethod();
        Set<ControlAlgorithm> supportedAlgorithms = currentMethod.getSupportedAlgorithms();
        List<SelectItem> items = Lists.newArrayList();
        for(ControlAlgorithm algorithm : supportedAlgorithms) {
            items.add(new SelectItem(algorithm, algorithm.getDisplayName()));
        }
        return items;
    }
    
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
    
    public int getItemId() {
        return itemId;
    }
    
    public void setEditorType(int editorType) {
        this.editorType = editorType;
    }
    
    public int getEditorType() {
        return editorType;
    }
    
    /**
     * Returns true if the user has the CapControl Settings > Database Editing role property.
     * @return
     */
    public boolean isEditingAuthorized() {
        return rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, JSFParamUtil.getYukonUser());
    }
    
    public void setScheduleId(Integer id) {
        this.scheduleId = id;
    }
    
    public void setHolidayScheduleId(Integer id) {
        this.holidayScheduleId = id;
    }
    
	public List<LiteYukonPAObject> getUnassignedSubBuses() {
		return unassignedSubBuses;
	}

	public void setUnassignedSubBuses(List<LiteYukonPAObject> unassignedSubBuses) {
		this.unassignedSubBuses = unassignedSubBuses;
	}
	
    public CapControlStrategy getStrategy() {
        return getCbcStrategiesMap().get(getCurrentStrategyID());
    }
    
    public Boolean getIntegrateFlag() {
        return getStrategy().getIntegrateFlag().equalsIgnoreCase("Y");
    }

    public void setIntegrateFlag(Boolean integrateFlag) {
        getStrategy().setIntegrateFlag((integrateFlag) ? "Y" : "N");
    }

    public Integer getIntegratePeriod() {
        return getStrategy().getIntegratePeriod();
    }

    public void setIntegratePeriod(Integer integratePeriod) {
        getStrategy().setIntegratePeriod(integratePeriod);
    }
    public Boolean getLikeDayFallBack() {
        return getStrategy().getLikeDayFallBack().equalsIgnoreCase("Y");
    }

    public void setLikeDayFallBack(Boolean fallBackFlag) {
        getStrategy().setLikeDayFallBack((fallBackFlag) ? "Y" : "N");
    }
    
    public void controlUnitsChanged(ValueChangeEvent e) {
        ControlAlgorithm newControlAlgorithm = ControlAlgorithm.valueOf(e.getNewValue().toString());
        currentControlAlgorithm = newControlAlgorithm;
        List<PeakTargetSetting> newTargetSettings = determineCorrectDefaultTargetSettings(currentControlMethod, currentControlAlgorithm); 
        getStrategy().setTargetSettings(newTargetSettings);
    }
    
    public void controlMethodChanged(ValueChangeEvent e) {
        PhaseId phaseId = e.getPhaseId();
        //queue the event to the update phase - after the model is updated, so our changes won't 
        //be overwritten, but before the view is built, so the UI sees the changes.
        if(phaseId.equals(PhaseId.ANY_PHASE)) {
            e.setPhaseId(PhaseId.UPDATE_MODEL_VALUES);
            e.queue();
        } else if(phaseId.equals(PhaseId.UPDATE_MODEL_VALUES)) {
            ControlMethod newMethod = ControlMethod.valueOf(e.getNewValue().toString());
            currentControlMethod = newMethod;
            
            /* Set new control algorithm */
            currentControlAlgorithm = currentControlMethod.getDefaultAlgorithm();
            getStrategy().setControlUnits(currentControlAlgorithm);
            
            /* Set new target settings */
            List<PeakTargetSetting> newTargetSettings = determineCorrectDefaultTargetSettings(currentControlMethod, currentControlAlgorithm); 
            getStrategy().setTargetSettings(newTargetSettings);
            
            /* Set new control method */
            getStrategy().setControlMethod(currentControlMethod);
        }
    }
    
    private static List<PeakTargetSetting> determineCorrectDefaultTargetSettings(ControlMethod method, ControlAlgorithm algorithm) {
        List<PeakTargetSetting> defaultTargetSettings;
        if (method == ControlMethod.TIME_OF_DAY) {
            defaultTargetSettings = StrategyPeakSettingsHelper.getSettingDefaults(ControlAlgorithm.TIME_OF_DAY);
        } else {
            defaultTargetSettings = StrategyPeakSettingsHelper.getSettingDefaults(algorithm);
        }
        
        return defaultTargetSettings;
    }
    
    public String getPeakHeader() {
        return isTimeOfDay() ? "Close" : "Peak";
    }
    
    public String getOffPeakHeader() {
        return isTimeOfDay() ? "Close" : "Off Peak";
    }
    
    public void setSeasonScheduleDao(SeasonScheduleDao seasonScheduleDao) {
        this.seasonScheduleDao = seasonScheduleDao;
    }
    
    public void setHolidayScheduleDao(HolidayScheduleDao holidayScheduleDao) {
        this.holidayScheduleDao = holidayScheduleDao;
    }
    
    public void setCapControlCreationService(CapControlCreationService capControlCreationService) {
        this.capControlCreationService = capControlCreationService;
    }
    
    public void setCapbankControllerDao(CapbankControllerDao capbankControllerDao) {
        this.capbankControllerDao = capbankControllerDao;
    }

    public void setSelectionLists(CBCSelectionLists selectionLists) {
        this.selectionLists = selectionLists;
    }

    public CBCSelectionLists getSelectionLists() {
        return selectionLists;
    }
    
    public void setStrategyDao (StrategyDao strategyDao) {
        this.strategyDao = strategyDao;
    }
    
    public void setPaoScheduleDao(PaoScheduleDao paoScheduleDao) {
    	this.paoScheduleDao = paoScheduleDao;
    }
    
    public void setCapControlCache(CapControlCache capControlCache) {
        this.capControlCache = capControlCache;
    }
    
    public void setCapbankDao(CapbankDao capbankDao) {
        this.capbankDao = capbankDao;
    }
    
    public void setDeviceConfigurationDao(DeviceConfigurationDao deviceConfigurationDao) {
        this.deviceConfigurationDao = deviceConfigurationDao;
    }
    
    public void setFeederDao(FeederDao feederDao) {
        this.feederDao = feederDao;
    }
    
    public void setSubstationBusDao(SubstationBusDao substationBusDao) {
        this.substationBusDao = substationBusDao;
    }
    
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
    
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
}