package com.cannontech.database.data.device.lm;

/**
 * Insert the type's description here.
 * Creation date: (2/18/2002 9:38:37 AM)
 * @author: 
 */
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.PAOGroups;

public final class LMFactory {
/**
 * LMFactory constructor comment.
 */
private LMFactory() {
	super();
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.data.pao.YukonPAObject
 * @param lmType int
 */
public final static com.cannontech.database.data.pao.YukonPAObject createLoadManagement(int lmType)
{

	com.cannontech.database.data.pao.YukonPAObject retLm = null;

	switch( lmType )
	{
		case PAOGroups.LM_GROUP_EMETCON:
			retLm = new LMGroupEmetcon();
			retLm.setPAOCategory( com.cannontech.database.data.pao.PAOGroups.STRING_CAT_DEVICE );
			retLm.setPAOClass( DeviceClasses.STRING_CLASS_GROUP );
			break;
		case PAOGroups.LM_GROUP_VERSACOM:
			retLm = new LMGroupVersacom();
			retLm.setPAOCategory( com.cannontech.database.data.pao.PAOGroups.STRING_CAT_DEVICE );
			retLm.setPAOClass(DeviceClasses.STRING_CLASS_GROUP);
			break;
		case PAOGroups.LM_GROUP_EXPRESSCOMM:
			retLm = new LMGroupExpressCom();
			retLm.setPAOCategory( com.cannontech.database.data.pao.PAOGroups.STRING_CAT_DEVICE );
			retLm.setPAOClass(DeviceClasses.STRING_CLASS_GROUP);
			break;			
		case PAOGroups.LM_GROUP_POINT:
			retLm = new LMGroupPoint();
			retLm.setPAOCategory( com.cannontech.database.data.pao.PAOGroups.STRING_CAT_DEVICE );
			retLm.setPAOClass(DeviceClasses.STRING_CLASS_GROUP);
			break;
		case PAOGroups.LM_GROUP_RIPPLE:
			retLm = new LMGroupRipple();
			retLm.setPAOCategory( com.cannontech.database.data.pao.PAOGroups.STRING_CAT_DEVICE );
			retLm.setPAOClass(DeviceClasses.STRING_CLASS_GROUP);
			break;
		case PAOGroups.LM_GROUP_MCT:
			retLm = new LMGroupMCT();
			retLm.setPAOCategory( com.cannontech.database.data.pao.PAOGroups.STRING_CAT_DEVICE );
			retLm.setPAOClass(DeviceClasses.STRING_CLASS_GROUP);
			break;
		case PAOGroups.LM_GROUP_SA305:
			retLm = new LMGroupSA305();
			retLm.setPAOCategory( com.cannontech.database.data.pao.PAOGroups.STRING_CAT_DEVICE );
			retLm.setPAOClass(DeviceClasses.STRING_CLASS_GROUP);
		
		case PAOGroups.LM_GROUP_SA205:
			retLm = new LMGroupSA205();
			retLm.setPAOCategory( com.cannontech.database.data.pao.PAOGroups.STRING_CAT_DEVICE );
			retLm.setPAOClass(DeviceClasses.STRING_CLASS_GROUP);
		
		case PAOGroups.LM_GROUP_SADIGITAL:
			retLm = new LMGroupSADigital();
			retLm.setPAOCategory( com.cannontech.database.data.pao.PAOGroups.STRING_CAT_DEVICE );
			retLm.setPAOClass(DeviceClasses.STRING_CLASS_GROUP);
		
		case PAOGroups.LM_GROUP_GOLAY:
			retLm = new LMGroupGolay();
			retLm.setPAOCategory( com.cannontech.database.data.pao.PAOGroups.STRING_CAT_DEVICE );
			retLm.setPAOClass(DeviceClasses.STRING_CLASS_GROUP);
			
		case PAOGroups.MACRO_GROUP:
			retLm = new MacroGroup();
			retLm.setPAOCategory( com.cannontech.database.data.pao.PAOGroups.STRING_CAT_DEVICE );
			retLm.setPAOClass(DeviceClasses.STRING_CLASS_GROUP);
			break;
		case PAOGroups.LM_DIRECT_PROGRAM:
			retLm = new LMProgramDirect();			
			retLm.setPAOClass(DeviceClasses.STRING_CLASS_LOADMANAGER);
			break;
		case PAOGroups.LM_CURTAIL_PROGRAM:
			retLm = new LMProgramCurtailment();
			retLm.setPAOClass(DeviceClasses.STRING_CLASS_LOADMANAGER);
			break;
		case PAOGroups.LM_ENERGY_EXCHANGE_PROGRAM:
			retLm = new LMProgramEnergyExchange();
			retLm.setPAOClass(DeviceClasses.STRING_CLASS_LOADMANAGER);
			break;
		case PAOGroups.LM_CONTROL_AREA:
			retLm = new LMControlArea();
			retLm.setPAOClass(DeviceClasses.STRING_CLASS_LOADMANAGER);
			break;
	}

	//Set a couple reasonable defaults
	if( retLm.getPAOCategory() == null )
		retLm.setPAOCategory( PAOGroups.STRING_CAT_LOADMANAGEMENT );

	if( retLm instanceof com.cannontech.database.data.device.DeviceBase )
	{
		((com.cannontech.database.data.device.DeviceBase)retLm).setDisableFlag( new Character('N') );
		((com.cannontech.database.data.device.DeviceBase)retLm).getDevice().setAlarmInhibit(new Character('N') );
		((com.cannontech.database.data.device.DeviceBase)retLm).getDevice().setControlInhibit(new Character('N') );
	}
		
	return retLm;
}
}
