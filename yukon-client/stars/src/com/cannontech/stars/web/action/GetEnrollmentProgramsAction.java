package com.cannontech.stars.web.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.stars.web.StarsOperator;
import com.cannontech.stars.xml.StarsWebConfigFactory;
import com.cannontech.stars.xml.serialize.StarsApplianceCategory;
import com.cannontech.stars.xml.serialize.StarsEnrollmentLMProgram;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsGetEnrollmentPrograms;
import com.cannontech.stars.xml.serialize.StarsGetEnrollmentProgramsResponse;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

/**
 * <p>Title: GetEnrollmentProgramsAction.java</p>
 * <p>Description: </p>
 * <p>Generation Time: Aug 29, 2002 4:29:02 PM</p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @author yao
 * @version 1.0
 */
public class GetEnrollmentProgramsAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsGetEnrollmentPrograms getEnrProgs = new StarsGetEnrollmentPrograms();
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsGetEnrollmentPrograms( getEnrProgs );
			
			return SOAPUtil.buildSOAPMessage( operation );
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#process(SOAPMessage, HttpSession)
	 */
	public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
        try {
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            StarsOperation respOper = new StarsOperation();
            
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
            if (operator == null) {
            	StarsFailure failure = new StarsFailure();
            	failure.setStatusCode( StarsConstants.FAILURE_CODE_SESSION_INVALID );
            	failure.setDescription( "Session invalidated, please login again" );
            	respOper.setStarsFailure( failure );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            /*
             * Now I'm using "small category" in StarsApplianceCategory, i.e. each StarsApplianceCategory
             * is a row instance in the ApplianceCategory table. One reason is that the WebConfigurationID
             * is defined in this table.
             * If we decide to use "large category" (category defined in the CustomerListEntry table),
             * the following code need to be changed a little bit.
             */
            StarsGetEnrollmentProgramsResponse response = new StarsGetEnrollmentProgramsResponse();
            Integer[] progIDs = getAllEnrLMProgramIDs( new Integer((int) operator.getEnergyCompanyID()) );
            
            for (int i = 0; i < progIDs.length; i++) {
            	com.cannontech.database.data.stars.LMProgramWebPublishing webPub =
            			com.cannontech.database.data.stars.LMProgramWebPublishing.getLMProgramWebPublishing( progIDs[i] );
            	Integer categoryID = webPub.getApplianceCategory().getApplianceCategory().getCategoryID();
            	
            	StarsApplianceCategory appCategory = null;
            	for (int j = 0; j < response.getStarsApplianceCategoryCount(); j++) {
            		if (response.getStarsApplianceCategory(j).getApplianceCategoryID() == categoryID.intValue()) {
            			appCategory = response.getStarsApplianceCategory(j);
            			break;
            		}
            	}
            	
            	if (appCategory == null) {
            		appCategory = new StarsApplianceCategory();
            		response.addStarsApplianceCategory( appCategory );
            	
	            	appCategory.setApplianceCategoryID( categoryID.intValue() );
	            	appCategory.setCategoryName( webPub.getApplianceCategory().getApplianceCategory().getDescription() );
	            	appCategory.setStarsWebConfig(
	            			StarsWebConfigFactory.newStarsWebConfig(webPub.getApplianceCategory().getWebConfiguration()) );
            	}
            	
            	StarsEnrollmentLMProgram program = new StarsEnrollmentLMProgram();
            	program.setProgramID( webPub.getLMProgram().getPAObjectID().intValue() );
            	program.setProgramName( webPub.getLMProgram().getPAOName() );
            	program.setStarsWebConfig(
            			StarsWebConfigFactory.newStarsWebConfig(webPub.getWebConfiguration()) );
            	appCategory.addStarsEnrollmentLMProgram( program );
            }
            
            respOper.setStarsGetEnrollmentProgramsResponse( response );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
        	e.printStackTrace();
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
			if (failure != null) return failure.getStatusCode();
			
            StarsGetEnrollmentProgramsResponse programs = operation.getStarsGetEnrollmentProgramsResponse();
            if (programs == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;

			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			if (operator != null)
	            operator.setAttribute("ENROLLMENT_PROGRAMS", programs);
	        else
	        	session.setAttribute("ENROLLMENT_PROGRAMS", programs);
            
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	private Integer[] getAllEnrLMProgramIDs(Integer energyCompanyID) {
		String sql = "SELECT ItemID FROM ECToGenericMapping WHERE "
				   + "EnergyCompanyID = ? AND MappingCategory = 'LMPrograms'";
				   
        java.sql.Connection conn = null;
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;
        ArrayList progIDList = new ArrayList();

        try {
            conn = com.cannontech.database.PoolManager.getInstance().getConnection(
                        com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
            if (conn == null) return null;
            
            pstmt = conn.prepareStatement( sql );
            pstmt.setInt( 1, energyCompanyID.intValue() );
            rset = pstmt.executeQuery();
            
            while (rset.next()) {
            	progIDList.add( new Integer(rset.getInt("ItemID")) );
            }
            
            Integer[] progIDs = new Integer[ progIDList.size() ];
            progIDList.toArray( progIDs );
            return progIDs;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (conn != null) conn.close();
                if( pstmt != null ) pstmt.close();
                if (rset != null) rset.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
	}

}
