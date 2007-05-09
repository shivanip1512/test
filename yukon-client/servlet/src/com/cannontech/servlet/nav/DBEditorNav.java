package com.cannontech.servlet.nav;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.pao.DBEditorTypes;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointTypes;

/**
 * @author ryan
 *
 */
public class DBEditorNav implements DBEditorTypes {

	/**
	 * 
	 */
	public DBEditorNav() {
		super();
	}

	/**
	 * Retuns the wizard URL for the given editorType.
	 */
	public static String getWizardURL( int wizardType ) {
		
		switch(wizardType) {

			case PAOGroups.CAP_CONTROL_SUBBUS:
			case PAOGroups.CAP_CONTROL_FEEDER:
			case PAOGroups.CAPBANK:
			case PAOGroups.CAPBANKCONTROLLER:
			case DBEditorTypes.PAO_SCHEDULE:
				return "/editor/cbcWizBase.jsf?type=" + wizardType;


			case PointTypes.ANALOG_POINT:
				return "/editor/pointWizBase.jsf";


			default:
				CTILogger.info("Uknown WizardType ("+wizardType+"), redirecting to same page");
				return "";
		}
	}

	/**
	 * Retuns the editor URL for the  given editorType. Add all editor types
	 * and their starting panel here.
	 */
	public static String getEditorURL( String editorType ) {
		
		if( DBEditorNav.EDTYPE_POINT.equals(editorType) ) {
			return "/editor/pointBase.jsf";
		}		
		else if( DBEditorNav.EDTYPE_CAPCONTROL.equals(editorType) ) {
			return "/editor/cbcBase.jsf?type=" + DBEditorNav.EDITOR_CAPCONTROL;
		}
		else if( DBEditorNav.EDTYPE_SCHEDULE.equals(editorType) ) {
			return "/editor/cbcBase.jsf?type=" + DBEditorNav.EDITOR_SCHEDULE;			
		}		
		else if( DBEditorNav.EDTYPE_LIST_SCHEDULE.equals(editorType) ) {
			return "/editor/paoSchedListBase.jsf";
		}
		else {
			CTILogger.warn("Uknown EditorType ("+editorType+"), redirecting to same page");
			return "";
		}

	}

	/**
	 * Retuns the delete URL for the  given deleteType. Add all other deleteTypes
	 * and their starting panel here.
	 */
	public static String getDeleteURL( String deleteType ) {
		
		if( DBEditorNav.EDTYPE_CAPCONTROL.equals(deleteType) ) {
			return "/editor/deleteBasePAO.jsf";
		}		
		else if( DBEditorNav.EDTYPE_POINT.equals(deleteType) ) {
			return "/editor/deleteBasePoint.jsf";
		}
		else {
			CTILogger.warn("Uknown DeleteType ("+deleteType+"), redirecting to same page");
			return "";
		}

	}

	public static String getCopyURL(String modType) {
		if( DBEditorNav.EDTYPE_CAPCONTROL.equals(modType) ) {
			return "/editor/copyBase.jsf";
		}		
		else if( DBEditorNav.EDTYPE_POINT.equals(modType) ) {
			return "/editor/copyBase.jsf";
		}
		else {
			CTILogger.warn("Uknown CopyType ("+modType+"), redirecting to same page");
			return "";
		}
	}
}
