package com.cannontech.web.editor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;

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
import com.cannontech.cbc.validators.CapControlCreationModelValidator;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.CapControlDao;
import com.cannontech.core.dao.HolidayScheduleDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.SeasonScheduleDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.schedule.dao.PaoScheduleDao;
import com.cannontech.core.schedule.model.PaoSchedule;
import com.cannontech.database.TransactionException;
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
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.TwoWayDevice;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.CapControlTypes;
import com.cannontech.database.data.pao.DBEditorTypes;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.pao.PAOFactory;
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
import com.cannontech.database.db.capcontrol.TargetSettingType;
import com.cannontech.database.db.device.DeviceScanRate;
import com.cannontech.database.db.holiday.HolidaySchedule;
import com.cannontech.database.db.pao.PAOScheduleAssign;
import com.cannontech.database.db.season.SeasonSchedule;
import com.cannontech.database.model.Season;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.servlet.nav.CBCNavigationUtil;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.YukonUserContext;
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
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

public class CapControlForm extends DBEditorForm implements ICapControlModel {
    
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
    private TransactionTemplate transactionTemplate;
    
    Logger log = YukonLogManager.getLogger(CapControlForm.class);
    private ControlAlgorithm currentControlAlgorithm;
    private ControlMethod currentControlMethod;
    
    public DeviceConfiguration getDefaultDnpConfiguration() {
        DeviceConfiguration configuration = deviceConfigurationDao.getDefaultDNPConfiguration();
        return configuration;
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
				cbcStrategies.add(new SelectItem(strategy.getId(), strategy.getName()));
				getCbcStrategiesMap().put(strategy.getId(), strategy);
			}
		}
		return cbcStrategies;
	}
    
    public List<SelectItem> getCbcHolidayStrategies() {
        List<SelectItem> cbcHolidayStrategies;
        List<CapControlStrategy> cbcDBStrats = strategyDao.getAllStrategies();
        cbcHolidayStrategies = Lists.newArrayList();
        for (CapControlStrategy strategy : cbcDBStrats) {
            cbcHolidayStrategies.add(new SelectItem(strategy.getId(), strategy.getName()));
            getCbcStrategiesMap().put(strategy.getId(), strategy);
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
            map.put(strat.getId(), strat.getName());
        }
        return map;
    }
    
	@Override
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

	@Override
    public int getCurrentStrategyID() {
		int stratID = CtiUtilities.NONE_ZERO_ID;
        if (getDbPersistent() instanceof CapControlStrategy) {
            stratID = ((CapControlStrategy)getDbPersistent()).getId();
        }
		return stratID;
	}
    
    public String getCurrentStratName() {
        return getStrategy().getName();
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
    
    @Override
    public Integer getOldSubBus() {
        return oldSubBus;
    }
    
    @Override
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
	
	@Override
    public void initItem(int id, int type) {
	    clearBeanState();
		DBPersistent dbObj = null;
        
		editorType = type;
		
		switch (type) {

    		case DBEditorTypes.EDITOR_CAPCONTROL:
    			dbObj = PAOFactory.createPAObject(id);
    			break;
    
            case DBEditorTypes.EDITOR_STRATEGY:
                dbObj = new CapControlStrategy();
                ((CapControlStrategy)dbObj).setId(new Integer(id));
                break;
		}
		setDbPersistent(dbObj);
		initItem();
	}

	@Override
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
           initPanels(pao.getPaoType().getDeviceTypeId());
           
        } else if (getDbPersistent() instanceof PointBase) {
            
            PointBase point = (PointBase) getDbPersistent();
			itemId = point.getPoint().getPointID().intValue();
			initPanels(PointTypes.getType(point.getPoint().getPointType()));
			
		} else if (getDbPersistent() instanceof CapControlStrategy) {
		    
            CapControlStrategy strat = (CapControlStrategy)getDbPersistent();
            itemId = strat.getId().intValue();
            editingCBCStrategy = true;
            currentControlAlgorithm = strat.getAlgorithm();
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
			setEnableDualBus(val);
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
		getVisibleTabs().put("CBCSubstation", Boolean.FALSE);
		getVisibleTabs().put("CBCSubstationBus", Boolean.FALSE);
		getVisibleTabs().put("CBCFeeder", Boolean.FALSE);
		getVisibleTabs().put("CBCCapBank", Boolean.FALSE);
		getVisibleTabs().put("CBCType", Boolean.FALSE);
		getVisibleTabs().put("CBCController", Boolean.FALSE);
		getVisibleTabs().put("GeneralSchedule", Boolean.FALSE);
        getVisibleTabs().put("CBAddInfo", Boolean.FALSE);
        getVisibleTabs().put("CBCStrategy", Boolean.FALSE);

        switch (paoType) {

            case CapControlTypes.CAP_CONTROL_AREA:
                setEditorTitle("Area");
                setPaoDescLabel("Area Location");
                setChildLabel("Substations");
                getVisibleTabs().put("CBCArea", Boolean.TRUE);
                break;
                
            case CapControlTypes.CAP_CONTROL_SPECIAL_AREA:
                setEditorTitle("Special Area");
                setPaoDescLabel("Special Area Location");
                setChildLabel("Substations");
                getVisibleTabs().put("CBCSpecialArea", Boolean.TRUE);
                break;

            case CapControlTypes.CAP_CONTROL_SUBSTATION:
    			setEditorTitle("Substation");
    			setPaoDescLabel("Geographical Name");
    			setChildLabel("Substation Buses");
    			getVisibleTabs().put("CBCSubstation", Boolean.TRUE);
                break;
                
            case CapControlTypes.CAP_CONTROL_SUBBUS:
    			setEditorTitle("Substation Bus");
    			setPaoDescLabel("Geographical Name");
    			setChildLabel("Feeders");
    			getVisibleTabs().put("CBCSubstationBus", Boolean.TRUE);
                break;
    
    		case CapControlTypes.CAP_CONTROL_FEEDER:
    			setEditorTitle("Feeder");
    			getVisibleTabs().put("CBCFeeder", Boolean.TRUE);
    			setPaoDescLabel(null);
    			setChildLabel("CapBanks");
    			break;
    
    		case DeviceTypes.CAPBANK:
    			setEditorTitle("Capacitor Bank");
    			setPaoDescLabel("Street Location");
    			getVisibleTabs().put("CBCCapBank", Boolean.TRUE);
                LiteYukonUser user = JSFUtil.getYukonUser();
                if (user != null) {
                    boolean showCapBankAddInfo = CapControlUtils.isCBAdditionalInfoAllowed(user);
                    getVisibleTabs().put ("CBAddInfo", showCapBankAddInfo);
                }
    			break;
    
    		case DeviceTypes.CAPBANKCONTROLLER:
    		case DeviceTypes.CBC_FP_2800:
    		case DeviceTypes.DNP_CBC_6510:
    		case DeviceTypes.CBC_EXPRESSCOM:
    		case DeviceTypes.CBC_7010:
            case DeviceTypes.CBC_7011:
            case DeviceTypes.CBC_7012:
            case DeviceTypes.CBC_7020:
            case DeviceTypes.CBC_7022:
            case DeviceTypes.CBC_7023:
            case DeviceTypes.CBC_7024: 
            case DeviceTypes.CBC_8020:
            case DeviceTypes.CBC_8024:
            case DeviceTypes.CBC_DNP:
    			setEditorTitle("CBC");
    			setPaoDescLabel(null);
    			getVisibleTabs().put("CBCType", Boolean.TRUE);
    			getVisibleTabs().put("CBCController", Boolean.TRUE);
    			break;
    
            case CapControlTypes.CAP_CONTROL_STRATEGY:
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

	@Override
    public DBPersistent getPAOBase() {
        return getDbPersistent();
     }

    @Override
    public void clearfaces() {
        FacesMessage facesMessage = new FacesMessage();
        facesMessage.setDetail("");   
        FacesContext currentInstance = FacesContext.getCurrentInstance();
        currentInstance.addMessage("cti_db_update", facesMessage);
    }
    
    public boolean checkStrategy(CapControlStrategy strat) {
        if(strat.isIntegrateFlag()) {
            int integrationSeconds = strat.getIntegratePeriod();
            int analysisSeconds = strat.getControlInterval();
            if(integrationSeconds > analysisSeconds) {
                return false;
            }
        }
        return true;
    }
    
	@Override
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
        			setDbPersistent( (YukonPAObject) DeviceTypesFuncs.changeCBCType(PaoType.getForId(getCBControllerEditor().getDeviceType()) , (ICapBankController)getDbPersistent()));
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
                    String dups = com.cannontech.common.util.StringUtils.toStringList(duplicates);
                    facesMsg.setDetail("Error: Some of the substations assigned are already assigned to another enabled Special Area: " + dups );
                    facesMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                }
                
            }else {
                updateDBObject(dbPers, facesMsg);
            }
            
            if (dbPers instanceof CapControlSubBus ||
                dbPers instanceof CapControlArea ||
                dbPers instanceof CapControlSpecialArea ||
                dbPers instanceof CapControlFeeder) {
                
                if(getHolidayScheduleId() != -1 && getHolidayStrategyId() < 1){
                    //if a holiday schedule is selected, a strategy must also be selected
                    facesMsg.setDetail("Holiday Strategy: strategy not selected.");
                    facesMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                } else if(dataModelOK) {
                	int paoId = ((YukonPAObject)dbPers).getPAObjectID();
                	seasonScheduleDao.saveSeasonStrategyAssigment(paoId, getAssignedStratMap(), getScheduleId());
                	holidayScheduleDao.saveHolidayScheduleStrategyAssigment(paoId, getHolidayScheduleId(), getHolidayStrategyId());
                }
            }
            pointNameMap = null;
            paoNameMap = null;

            if (dbPers instanceof CapControlSubstation) {
                for (LiteYukonPAObject bus : unassignedSubBuses) {
                    capControlCache.handleDeleteItem(bus.getLiteID());
                }
            }
            if (dbPers instanceof CapControlSubstationBus) {
                for (LiteYukonPAObject feeder : unassignedFeeders) {
                    capControlCache.handleDeleteItem(feeder.getLiteID());
                }
            }
            if (dbPers instanceof CapControlFeeder) {
                for (LiteYukonPAObject bank : unassignedBanks) {
                    capControlCache.handleDeleteItem(bank.getLiteID());
                }
            }
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
                    if(substation.getSpecialAreaEnabled() && substation.getSpecialAreaId() != area.getPAObjectID()) {
                        duplicates.add(capControlCache.getSpecialArea(substation.getSpecialAreaId()).getCcName() + ": " + substation.getCcName());
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
        if (type == CapControlTypes.CAP_CONTROL_STRATEGY) {
            return DBEditorTypes.EDITOR_STRATEGY;
        }else {
            return DBEditorTypes.EDITOR_CAPCONTROL;
        }
    }

    /**
     * Creates a cap control object or strategy.
     * @Return String the url to go when done.
     */
    @Override
    public String create() {
        FacesMessage facesMsg = new FacesMessage();
        FacesContext facesContext = FacesContext.getCurrentInstance();

        final CBCWizardModel wizard = (CBCWizardModel) getWizData();

        DataBinder binder = new DataBinder(wizard);
        CapControlCreationModelValidator validator = new CapControlCreationModelValidator(strategyDao, paoDao);
        binder.setValidator(validator);
        binder.validate();
        BindingResult bindingResult = binder.getBindingResult();

        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                facesMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                facesMsg.setDetail(error.getCode());
                facesContext.addMessage("cti_db_add", facesMsg);
            }
            return StringUtils.EMPTY;
        }

        final String name = wizard.getName();
        final int type = wizard.getSelectedType();
        final boolean disabled = wizard.getDisabled();
        final int portId = wizard.getPortID();
        final boolean isCapBankAndNested = (type == CapControlTypes.CAP_CONTROL_CAPBANK && wizard.isCreateNested());

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                // If this is a Strategy it is NOT a Pao, handle
                // accordingly.
                if (type == CapControlTypes.CAP_CONTROL_STRATEGY) {
                    itemId = strategyDao.add(name);
                    return;
                }

                // Must be a Pao
                PaoType paoType = PaoType.getForId(type);
                boolean isController = CapControlUtils
                        .checkControllerByType(paoType);

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
        ExternalContext externalcontext=facesContext.getExternalContext();
        CBCNavigationUtil.redirect(externalcontext.getRequestContextPath()+url, session);

        /* Does the actually redirection to the editor url */
        JSFUtil.redirect(url);

        facesContext.addMessage("cti_db_add", facesMsg);

        return StringUtils.EMPTY;
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

	@Override
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

	@Override
    public void setCBControllerEditor(ICBControllerModel cbCntrlEditor) {
		cbControllerEditor = cbCntrlEditor;
	}

	@Override
    public boolean isEditingController() {
		return editingController;
	}

	@Override
    public void setEditingController(boolean val) {
		editingController = val;
	}

	@Override
    public String getPAODescLabel() {
		return paoDescLabel;
	}

	@Override
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
    	    PaoIdentifier capbank = capbankDao.findCapBankByCbc(itemId);
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

    @Override
    public void treeSwapAddAction() {
		FacesContext context = FacesContext.getCurrentInstance();
		Map paramMap = context.getExternalContext().getRequestParameterMap();
		String swapType = (String) paramMap.get("swapType");
		int elemID = Integer.parseInt((String) paramMap.get("id"));
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
    private void updateTripOrder(CapControlFeeder currFdr) {
        List<CCFeederBankList> childList = currFdr.getChildList();
        for (CCFeederBankList assign : childList) {
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
    private float maxDispOrderOnList(List<? extends DBPersistent> childList) {
		float max = 0;
		for (DBPersistent element : childList) {
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

	@Override
    public void treeSwapRemoveAction() {
		FacesContext context = FacesContext.getCurrentInstance();
		Map paramMap = context.getExternalContext().getRequestParameterMap();
		String swapType = (String) paramMap.get("swapType");
		int elemID = Integer.parseInt((String) paramMap.get("id"));
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

    @Override
    public void initEditorPanels() {
        unassignedBanks = capbankDao.getUnassignedCapBanks();
		Collections.sort(unassignedBanks, LiteComparators.liteStringComparator);
		
        unassignedFeeders = feederDao.getUnassignedFeeders();
		Collections.sort(unassignedFeeders, LiteComparators.liteStringComparator);
				
        unassignedSubBuses = substationBusDao.getUnassignedBuses();
		Collections.sort(unassignedSubBuses, LiteComparators.liteStringComparator);
	}

	@Override
    public void setPaoDescLabel(String string) {
		paoDescLabel = string;
	}
	
	/*
	 * Remembers if the edit checkbox is checked on the startegy editor.
	 */
	@Override
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

	@Override
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

	@Override
    public List<LiteYukonPAObject> getUnassignedBanks() {
		return unassignedBanks;
	}

	@Override
    public List<LiteYukonPAObject> getUnassignedFeeders() {
		return unassignedFeeders;
	}

	@Override
    public String getChildLabel() {
		return childLabel;
	}

	@Override
    public void setChildLabel(String string) {
		childLabel = string;
	}

	@Override
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

	@Override
    public boolean isBankControlPtVisible() {
		if (getDbPersistent() instanceof CapBank) {
			return !CapBank.FIXED_OPSTATE.equals(((CapBank) getDbPersistent()).getCapBank().getOperationalState());
		} else {
			return false;
        }
	}

    @Override
    public String addSchedule() {
		if (!(getDbPersistent() instanceof CapControlSubBus)) {
			return null;
        }
		CapControlSubBus subBus = (CapControlSubBus) getDbPersistent();
		if (subBus.getSchedules().size() < PAOScheduleAssign.MAX_SHEDULES_PER_PAO) {
			PAOScheduleAssign paoSched = new PAOScheduleAssign();
			paoSched.setPaoID(subBus.getPAObjectID());
			subBus.getSchedules().add(paoSched);
		}
		return null;
	}

	@Override
    public CapControlCreationModel getWizData() {
		if (wizData == null) {
			wizData = new CBCWizardModel();
        }
		return wizData;
	}

	@Override
    public boolean isVoltageControl() {
		if (getCurrentStrategyID() != CtiUtilities.NONE_ZERO_ID) {
			CapControlStrategy strat = getCbcStrategiesMap().get(new Integer(getCurrentStrategyID()));
			return strat != null && ControlAlgorithm.VOLTS.getDisplayName().equals(strat.getAlgorithm());
		} else {
			return false;
        }
	}

    @Override
    public LiteYukonPAObject[] getSubBusList() {
		if (subBusList == null) {
			subBusList = YukonSpringHook.getBean(CapControlDao.class).getAllSubsForUser (JSFParamUtil.getYukonUser());
		}
		return subBusList.toArray(new LiteYukonPAObject[subBusList.size()]);
	}

    @Override
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

    @Override
    public Boolean getEnableDualBus() {
        return enableDualBus;
    }

    @Override
    public void setEnableDualBus(Boolean enableDualBus) {
        this.enableDualBus = enableDualBus;
        updateDualBusEnabled();         
        setDualSubBusEdited(true);
    }
    
	@Override
    public boolean isDualSubBusEdited() {
		return isDualSubBusEdited;
	}

	@Override
    public void setDualSubBusEdited(boolean isDualSubBusEdited) {
		this.isDualSubBusEdited = isDualSubBusEdited;
	}

    @Override
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
    
    @Override
    protected void checkForErrors() throws PortDoesntExistException, MultipleDevicesOnPortException, SameMasterSlaveCombinationException, SerialNumberExistsException, SQLException { 
        if (getDbPersistent() != null){
			getCBControllerEditor().checkForErrors();
		}
    }
    
    @Override
    public String getPaoName() {
    	String retStr = "";
    	if (getDbPersistent() != null) {
	    	if (getDbPersistent() instanceof YukonPAObject) {
	        	retStr = ((YukonPAObject)getDbPersistent()).getPAOName();
	        } else if (getDbPersistent() instanceof CapControlStrategy) {
	            retStr = ((CapControlStrategy)getDbPersistent()).getName();
	        }
    	}
        return retStr;
    }
    
    @Override
    public LitePoint[] getCapBankPointList() {
        List<LitePoint> temp = pointDao.getLitePointsByPaObjectId(((YukonPAObject)getDbPersistent()).getPAObjectID().intValue());
        return temp.toArray(new LitePoint [temp.size()]);
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
	    for(ControlMethod controlMethod : ControlMethod.values()) {
	        controlMethods.add(new SelectItem(controlMethod, controlMethod.getDisplayName()));
	    }
	    return controlMethods.toArray(new SelectItem[0]);
	    
	}
    
    @Override
    public Map<Integer, String> getPointNameMap () {

        pointNameMap = new HashMap<>();
        List<Integer> pointIds = new ArrayList<Integer>();
        
        pointIds.add(getControlPoint(UnitOfMeasure.KVAR));
        pointIds.add(getControlPoint(UnitOfMeasure.KW));
        pointIds.add(getControlPoint(UnitOfMeasure.KVOLTS));

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

    @Override
    public Map<Integer, String> getPaoNameMap () {
        
        paoNameMap = new HashMap<Integer, String>();
        List<Integer> pointIds = new ArrayList<Integer>();
        
        pointIds.add(getControlPoint(UnitOfMeasure.KVAR));
        pointIds.add(getControlPoint(UnitOfMeasure.KW));
        pointIds.add(getControlPoint(UnitOfMeasure.KVOLTS));
        
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
     
    private int getControlPoint(UnitOfMeasure uom) {
        int pointID = 0;
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
                default:
                    log.warn("Invalid control point Unit of Measure for Sub Bus: " + uom);
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
                default:
                    log.warn("Invalid control point Unit of Measure for Feeder: " + uom);
            }
        }
        return pointID;
    }

    public void paoClick(ActionEvent ae){
        FacesMessage fm = new FacesMessage();
        try {
            //go to the next page
            int itemId = Integer.valueOf((String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("paoID"));
            IDatabaseCache dbCache = YukonSpringHook.getBean(IDatabaseCache.class);
            LiteYukonPAObject pao = dbCache.getAllPaosMap().get(itemId);

            String location;
            if (pao != null && pao.getPaoType().isRegulator()) {
                location = "/capcontrol/regulators/" + itemId;
            } else {
                location = "/editor/cbcBase.jsf?type=" + DBEditorTypes.EDITOR_CAPCONTROL + "&itemid=" + itemId;
            }

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

    @Override
    public EditorDataModel getDataModel() {
       if (dataModel == null) {
           if (getDbPersistent() != null) {
               initDataModel(getDbPersistent());
           }
       }
        return dataModel;
    }

    @Override
    public void setDataModel(EditorDataModel dataModel) {
        this.dataModel = dataModel;
    }
    
    @Override
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
    
    //TODO Remove after deleting JSF strategyEditor.jsp
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
        scheduleId = id;
    }
    
    public void setHolidayScheduleId(Integer id) {
        holidayScheduleId = id;
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
        return getStrategy().isIntegrateFlag();
    }

    public void setIntegrateFlag(Boolean integrateFlag) {
        getStrategy().setIntegrateFlag(integrateFlag);
    }

    public Integer getIntegratePeriod() {
        return getStrategy().getIntegratePeriod();
    }

    public void setIntegratePeriod(Integer integratePeriod) {
        getStrategy().setIntegratePeriod(integratePeriod);
    }
    public Boolean getLikeDayFallBack() {
        return getStrategy().isLikeDayFallBack();
    }

    public void setLikeDayFallBack(Boolean fallBackFlag) {
        getStrategy().setLikeDayFallBack(fallBackFlag);
    }
    
    public void algorithmChanged(ValueChangeEvent e) {
        ControlAlgorithm newControlAlgorithm = ControlAlgorithm.valueOf(e.getNewValue().toString());
        currentControlAlgorithm = newControlAlgorithm;
        Map<TargetSettingType, PeakTargetSetting> newTargetSettings = StrategyPeakSettingsHelper.getSettingDefaults(currentControlAlgorithm);
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
            getStrategy().setAlgorithm(currentControlAlgorithm);
            
            /* Set new target settings */
            Map<TargetSettingType, PeakTargetSetting> newTargetSettings = StrategyPeakSettingsHelper.getSettingDefaults(currentControlAlgorithm); 
            getStrategy().setTargetSettings(newTargetSettings);
            
            /* Set new control method */
            getStrategy().setControlMethod(currentControlMethod);
        }
    }
    
    public String getPeakHeader() {
        return isTimeOfDay() ? "Close" : "Peak";
    }
    
    public String getOffPeakHeader() {
        return isTimeOfDay() ? "Close" : "Off Peak";
    }

    //for subBusSchedule.jsp
    public List<SelectItem> getPaoScheduleSelectItems() {

        List<PaoSchedule> schedules = paoScheduleDao.getAll();
        List<SelectItem> selectItems = new ArrayList<>();

        for(PaoSchedule schedule : schedules) {
            selectItems.add(new SelectItem(schedule.getId(), schedule.getName()));
        }

        return selectItems;
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
    
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
}