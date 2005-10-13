package com.cannontech.web.editor;

import com.cannontech.clientutils.CTILogger;
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
	 * 
	 */
	public static String getEditorURL( int editorType ) {
		
		switch(editorType) {
			case DBEditorNav.EDITOR_POINT:
				return "/editor/pointBase.jsf";

			case DBEditorNav.EDITOR_CAPCONTROL:
				return "/editor/cbcBase.jsf?type=" + DBEditorNav.EDITOR_CAPCONTROL;

			case DBEditorNav.EDITOR_SCHEDULE:
				return "/editor/cbcBase.jsf?type=" + DBEditorNav.EDITOR_SCHEDULE;
			
			default:
				CTILogger.info("Uknown EditorType ("+editorType+"), redirecting to same page");
				return "";
		}

	}


	/**
	 * Retuns the editor URL for the  given litetype
	 * 
	 * @param liteType: int type from the Lites
	 * @param itemID:  the primary key identifier
	 * @param itemType: specific type within the given litetype 
	 * 			(Status Point, CCU711 Transmitter)
	 */
	public static String getEditorURL( int liteType, int itemID ) {
		
		return getEditorURL(liteType) +
			"?itemid=" + itemID;
			//"&itemtype=" + itemType;
	}

	/**
	 * Retuns the delete URL for the  given deleteType. Add all other deleteTypes
	 * and their starting panel here.
	 */
	public static String getDeleteURL( int deleteType ) {
		
		switch( deleteType ) {
			case DBEditorNav.DELETE_PAO:
				return "/editor/deleteBase.jsf";

			case DBEditorNav.DELETE_POINT:
				return "";
			
			default:
				CTILogger.info("Uknown DeleteType ("+deleteType+"), redirecting to same page");
				return "";
		}

	}
}
