package com.cannontech.stars.web.action;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteCustomerResidence;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.ConstructionMaterial;
import com.cannontech.stars.xml.serialize.DecadeBuilt;
import com.cannontech.stars.xml.serialize.GeneralCondition;
import com.cannontech.stars.xml.serialize.InsulationDepth;
import com.cannontech.stars.xml.serialize.MainCoolingSystem;
import com.cannontech.stars.xml.serialize.MainFuelType;
import com.cannontech.stars.xml.serialize.MainHeatingSystem;
import com.cannontech.stars.xml.serialize.NumberOfOccupants;
import com.cannontech.stars.xml.serialize.OwnershipType;
import com.cannontech.stars.xml.serialize.ResidenceType;
import com.cannontech.stars.xml.serialize.SquareFeet;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsResidenceInformation;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.serialize.StarsUpdateResidenceInformation;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class UpdateResidenceInfoAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
        try {
        	StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
        	if (user == null) return null;
        	
        	Hashtable selectionLists = (Hashtable) session.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );
        	StarsUpdateResidenceInformation updateResInfo = new StarsUpdateResidenceInformation();
        	
        	updateResInfo.setResidenceType(
        		(ResidenceType) StarsFactory.newStarsCustListEntry(
        			ServletUtils.getStarsCustListEntryByID(
        				selectionLists,
        				YukonSelectionListDefs.YUK_LIST_NAME_RESIDENCE_TYPE,
        				Integer.parseInt(req.getParameter("ResidenceType"))
        			),
        			ResidenceType.class
        		)
        	);
        	updateResInfo.setConstructionMaterial(
        		(ConstructionMaterial) StarsFactory.newStarsCustListEntry(
        			ServletUtils.getStarsCustListEntryByID(
        				selectionLists,
        				YukonSelectionListDefs.YUK_LIST_NAME_CONSTRUCTION_MATERIAL,
        				Integer.parseInt(req.getParameter("ConstMaterial"))
        			),
        			ConstructionMaterial.class
        		)
        	);
        	updateResInfo.setDecadeBuilt(
        		(DecadeBuilt) StarsFactory.newStarsCustListEntry(
        			ServletUtils.getStarsCustListEntryByID(
        				selectionLists,
        				YukonSelectionListDefs.YUK_LIST_NAME_DECADE_BUILT,
        				Integer.parseInt(req.getParameter("DecadeBuilt"))
        			),
        			DecadeBuilt.class
        		)
        	);
        	updateResInfo.setSquareFeet(
        		(SquareFeet) StarsFactory.newStarsCustListEntry(
        			ServletUtils.getStarsCustListEntryByID(
        				selectionLists,
        				YukonSelectionListDefs.YUK_LIST_NAME_SQUARE_FEET,
        				Integer.parseInt(req.getParameter("SquareFeet"))
        			),
        			SquareFeet.class
        		)
        	);
        	updateResInfo.setInsulationDepth(
        		(InsulationDepth) StarsFactory.newStarsCustListEntry(
        			ServletUtils.getStarsCustListEntryByID(
        				selectionLists,
        				YukonSelectionListDefs.YUK_LIST_NAME_INSULATION_DEPTH,
        				Integer.parseInt(req.getParameter("InsulationDepth"))
        			),
        			InsulationDepth.class
        		)
        	);
        	updateResInfo.setGeneralCondition(
        		(GeneralCondition) StarsFactory.newStarsCustListEntry(
        			ServletUtils.getStarsCustListEntryByID(
        				selectionLists,
        				YukonSelectionListDefs.YUK_LIST_NAME_GENERAL_CONDITION,
        				Integer.parseInt(req.getParameter("GeneralCondition"))
        			),
        			GeneralCondition.class
        		)
        	);
        	updateResInfo.setMainCoolingSystem(
        		(MainCoolingSystem) StarsFactory.newStarsCustListEntry(
        			ServletUtils.getStarsCustListEntryByID(
        				selectionLists,
        				YukonSelectionListDefs.YUK_LIST_NAME_COOLING_SYSTEM,
        				Integer.parseInt(req.getParameter("CoolingSystem"))
        			),
        			MainCoolingSystem.class
        		)
        	);
        	updateResInfo.setMainHeatingSystem(
        		(MainHeatingSystem) StarsFactory.newStarsCustListEntry(
        			ServletUtils.getStarsCustListEntryByID(
        				selectionLists,
        				YukonSelectionListDefs.YUK_LIST_NAME_HEATING_SYSTEM,
        				Integer.parseInt(req.getParameter("HeatingSystem"))
        			),
        			MainHeatingSystem.class
        		)
        	);
        	updateResInfo.setNumberOfOccupants(
        		(NumberOfOccupants) StarsFactory.newStarsCustListEntry(
        			ServletUtils.getStarsCustListEntryByID(
        				selectionLists,
        				YukonSelectionListDefs.YUK_LIST_NAME_NUM_OF_OCCUPANTS,
        				Integer.parseInt(req.getParameter("OccupantNum"))
        			),
        			NumberOfOccupants.class
        		)
        	);
        	updateResInfo.setOwnershipType(
        		(OwnershipType) StarsFactory.newStarsCustListEntry(
        			ServletUtils.getStarsCustListEntryByID(
        				selectionLists,
        				YukonSelectionListDefs.YUK_LIST_NAME_OWNERSHIP_TYPE,
        				Integer.parseInt(req.getParameter("OwnershipType"))
        			),
        			OwnershipType.class
        		)
        	);
        	updateResInfo.setMainFuelType(
        		(MainFuelType) StarsFactory.newStarsCustListEntry(
        			ServletUtils.getStarsCustListEntryByID(
        				selectionLists,
        				YukonSelectionListDefs.YUK_LIST_NAME_FUEL_TYPE,
        				Integer.parseInt(req.getParameter("FuelType"))
        			),
        			MainFuelType.class
        		)
        	);
        	updateResInfo.setNotes( req.getParameter("Notes").replaceAll(System.getProperty("line.separator"), "<br>") );
        	
        	StarsOperation operation = new StarsOperation();
        	operation.setStarsUpdateResidenceInformation( updateResInfo );
        	
            return SOAPUtil.buildSOAPMessage( operation );
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Invalid request parameters" );
        }

		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#process(SOAPMessage, HttpSession)
	 */
	public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
        StarsOperation respOper = new StarsOperation();
        
        try {
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            
            LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation)
					session.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
            StarsUpdateResidenceInformation updateResInfo = reqOper.getStarsUpdateResidenceInformation();
            
            updateResidenceInformation( updateResInfo, liteAcctInfo );
            
            StarsSuccess success = new StarsSuccess();
            success.setDescription( "Residential information updated successfully" );
            
            respOper.setStarsSuccess( success );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot create/update residential information") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            catch (Exception e2) {
            	CTILogger.error( e2.getMessage(), e2 );
            }
        }

		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#parse(SOAPMessage, SOAPMessage, HttpSession)
	 */
	public int parse(SOAPMessage reqMsg, SOAPMessage respMsg, HttpSession session) {
        try {
            StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( respMsg );

			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
				return failure.getStatusCode();
			}
			
			StarsSuccess success = operation.getStarsSuccess();
			if (success == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					session.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			
			StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
			accountInfo.setStarsResidenceInformation( (StarsResidenceInformation)
					StarsFactory.newStarsCustResidence(reqOper.getStarsUpdateResidenceInformation(), StarsResidenceInformation.class) );
			
            return 0;
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	public static void updateResidenceInformation(StarsUpdateResidenceInformation updateResInfo, LiteStarsCustAccountInformation liteAcctInfo)
		throws CommandExecutionException
	{
		com.cannontech.database.db.stars.customer.CustomerResidence custRes =
				new com.cannontech.database.db.stars.customer.CustomerResidence();
        
		custRes.setAccountSiteID( new Integer(liteAcctInfo.getAccountSite().getAccountSiteID()) );
		custRes.setResidenceTypeID( new Integer(updateResInfo.getResidenceType().getEntryID()) );
		custRes.setConstructionMaterialID( new Integer(updateResInfo.getConstructionMaterial().getEntryID()) );
		custRes.setDecadeBuiltID( new Integer(updateResInfo.getDecadeBuilt().getEntryID()) );
		custRes.setSquareFeetID( new Integer(updateResInfo.getSquareFeet().getEntryID()) );
		custRes.setInsulationDepthID( new Integer(updateResInfo.getInsulationDepth().getEntryID()) );
		custRes.setGeneralConditionID( new Integer(updateResInfo.getGeneralCondition().getEntryID()) );
		custRes.setMainCoolingSystemID( new Integer(updateResInfo.getMainCoolingSystem().getEntryID()) );
		custRes.setMainHeatingSystemID( new Integer(updateResInfo.getMainHeatingSystem().getEntryID()) );
		custRes.setNumberOfOccupantsID( new Integer(updateResInfo.getNumberOfOccupants().getEntryID()) );
		custRes.setOwnershipTypeID( new Integer(updateResInfo.getOwnershipType().getEntryID()) );
		custRes.setMainFuelTypeID( new Integer(updateResInfo.getMainFuelType().getEntryID()) );
		custRes.setNotes( updateResInfo.getNotes() );
        
		if (liteAcctInfo.getCustomerResidence() == null) {
			Transaction.createTransaction(Transaction.INSERT, custRes).execute();
			liteAcctInfo.setCustomerResidence( (LiteCustomerResidence) StarsLiteFactory.createLite(custRes) );
		}
		else {
			Transaction.createTransaction(Transaction.UPDATE, custRes).execute();
			StarsLiteFactory.setLiteCustomerResidence( liteAcctInfo.getCustomerResidence(), custRes );
		}
	}

}
