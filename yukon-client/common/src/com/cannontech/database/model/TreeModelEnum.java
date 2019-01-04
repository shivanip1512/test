package com.cannontech.database.model;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.ExceptionHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * This type was created in VisualAge.
 */

public enum TreeModelEnum 
{
	DEVICE (DeviceTreeModel.class,"Device"),
	IED (IEDTreeModel.class, "IED"),
	MCT (MCTTreeModel.class, "MCT"),
	METER (MeterTreeModel.class, "Meter"),
	PORT (CommChannelTreeModel.class, "Comm Channel        "),
	ROUTE (RouteTreeModel.class,"Route"),
	RTU (RTUTreeModel.class, "RTU"),
	TRANSMITTER (TransmitterTreeModel.class, "Transmitter"),
	LMGROUPS (LMGroupsModel.class, "Load Group"),
	LMGROUPEMETCON (LMGroupEmetconModel.class, "EMETCON Group"),
	LMGROUPVERSACOM (LMGroupVersacomModel.class, "VERSACOM Group"),
	LMGROUPEXPRESSCOM (LMGroupExpresscomModel.class, "EXPRESSCOM Group"),
	LMGROUPSA305 (LMGroupSA305Model.class, "SA-305 Group"),
	LMGROUPSA205 (LMGroupSA205Model.class, "SA-205 Group"),
	LMGROUPSADIGITAL (LMGroupSADigitalModel.class, "SA-Digital Group"),
	LMGROUPDIGISEP (LMGroupDigiSepModel.class, "Digi SEP Group"),
	LMGROUPECOBEE (LMGroupEcobeeModel.class, "ecobee Group"),
	LMGROUPHONEYWELL (LMGroupHoneywellModel.class, "Honeywell Group"),
	LMGROUPITRON(LMGroupItronModel.class, "Itron Group"),
    LMGROUPNEST(LMGroupNestModel.class, "Nest Group"),
	LMGROUPMACRO (LMGroupMacroModel.class, "Macro Group"),
	GOLAY (LMGroupGolayModel.class, "Golay Group"),
	LMPROGRAM (LMProgramModel.class, "Load Program"),
	LMCONTROLAREA (LMControlAreaModel.class, "Control Area"),
	LMSCENARIO (LMScenarioModel.class, "Scenario"),
	LMCONSTRAINT (LMConstraintModel.class, "Constraint"),
	STATEGROUP (StateGroupTreeModel.class, "State Group"),
	CAPBANK (CapBankModel.class,"Cap Bank"),
	CAPBANKCONTROLLER (CapBankControllerModel.class, "Cap Bank Controller"),
	CAPCONTROLSTRATEGY (CapControlSubBusModel.class, "Substation Bus"),
	CAPCONTROLFEEDER (CapControlFeederModel.class, "Cap Feeders"),
	NOTIFICATION_GROUP (NotificationGroupTreeModel.class, "Notification Group"),
	CONTACT (ContactTreeModel.class, "Contacts"),
	ALARM_STATES (AlarmCategoriesTreeModel.class, "Alarm Categories"),
	CICUSTOMER (CICustomerTreeModel.class, "CI Customers"),
	DEVICE_CUSTOMPOINTS (DeviceTree_CustomPointsModel.class, "Device - CustomPoints"),	// //overridden value in class.toString()
	HOLIDAY_SCHEDULE (HolidayScheduleModel.class, "Holiday Schedule"),
	DEVICE_METERNUMBER (DeviceMeterGroupModel.class, "Meter Number"),
	MCTBROADCAST (MCTBroadcastGroupTreeModel.class, "MCT Broadcast"),
	USERS (LoginTreeModel.class, "User"),
	ROLE_GROUPS (LoginGroupTreeModel.class, "Role Group"),
	USER_GROUPS (UserGroupTreeModel.class, "User Group"),
	BASELINE (BaselineModel.class, "Baseline"),
	TWOWAYCONFIG (ConfigModel.class, "MCT Config"),
	TAG (TagModel.class, "Tag"),
	DEVICE_CHECKBOX (DeviceCheckBoxTreeModel.class, "Device"),
	EDITABLE_LCR_SERIAL (EditableLCRSerialModel.class, "LCR Serial #"),
	EDITABLE_VERSACOM_SERIAL (EditableVersacomModel.class, "Versacom Serial"),
	EDITABLE_EXPRESSCOM_SERIAL (EditableExpresscomModel.class, "Expresscom Serial"),
	EDITABLE_SA305_SERIAL (EditableSA305Model.class, "DCU-305 Serial"),
	EDITABLE_SA205_SERIAL (EditableSA205Model.class, "DCU-205 Serial"),
	SEASON (SeasonModel.class, "Season Schedule"),
	TOUSCHEDULE (TOUScheduleModel.class, "TOU Schedule"),
	RECEIVERS (ReceiverTreeModel.class, "Receivers"),
	SYSTEM_DEVICE (SystemDeviceModel.class, "System Points"),
	RFMESH (RFNTreeModel.class, "RF Mesh"),
	BY_TYPE (TDCDeviceTreeModel.class, "All Types"),
	;

	private TreeModelEnum(Class<? extends LiteBaseTreeModel> treeModelClass, String treeModelName) {
		this.treeModelClass = treeModelClass;
		this.treeModelName = treeModelName;
	}

	private Class<? extends LiteBaseTreeModel> treeModelClass;
	private String treeModelName;

    private final static ImmutableMap<Class<? extends LiteBaseTreeModel>, TreeModelEnum> lookupByClass;
    
    static {
        Builder<Class<? extends LiteBaseTreeModel>, TreeModelEnum> classBuilder = ImmutableMap.builder();
        for (TreeModelEnum model : values()) {
			classBuilder.put(model.getTreeModelClass(), model);
		}
        lookupByClass = classBuilder.build();
    }
    
    /**
     * Looks up the Model based on its Class type.
     * @param class
     * @return
     * @throws IllegalArgumentException - if no match
     */
    public static TreeModelEnum getForClass(Class<? extends LiteBaseTreeModel> modelClass) throws IllegalArgumentException {
        TreeModelEnum model = lookupByClass.get(modelClass);
        Validate.notNull(model, modelClass.toString());
        return model;
    }
    
	public Class<? extends LiteBaseTreeModel> getTreeModelClass() {
		return treeModelClass;
	}

	public String getTreeModelName() {
		return treeModelName;
	}

	/**
	 * Returns a new dbTreeModel instance for model
	 * @param model
	 * @return
	 */
	public static LiteBaseTreeModel create(TreeModelEnum model) {
	    try {
	        LiteBaseTreeModel returnVal = model.getTreeModelClass().newInstance();
	        return returnVal;
	    } catch (Exception e) {
	        CTILogger.error("could not instantiate LiteBaseTreeModel for " + model, e);
	        throw ExceptionHelper.wrapIfNeeded(e);
	    }
	}
	
    private static final Set<Class<? extends LiteBaseTreeModel>> editableSerialClasses = new HashSet<>();

    static {
        editableSerialClasses.add(EditableSA205Model.class);
        editableSerialClasses.add(EditableSA305Model.class);
        editableSerialClasses.add(EditableVersacomModel.class);
        editableSerialClasses.add(EditableExpresscomModel.class);
        editableSerialClasses.add(EditableLCRSerialModel.class);
    }

	public static boolean isEditableSerial(Class<? extends LiteBaseTreeModel> model)
	{
	    return editableSerialClasses.contains(model);
	}
}