package com.cannontech.stars.xml;

import com.cannontech.stars.xml.serialize.*;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class StarsGetEnrollmentProgramsResponseFactory {

	public static StarsGetEnrollmentProgramsResponse getStarsGetEnrollmentProgramsResponse(Integer energyCompanyID) {
		try {
            StarsGetEnrollmentProgramsResponse response = new StarsGetEnrollmentProgramsResponse();
            Integer[] progIDs = getAllEnrLMProgramIDs( energyCompanyID );
            
            for (int i = 0; i < progIDs.length; i++) {
            	com.cannontech.database.data.stars.LMProgramWebPublishing webPub =
            			com.cannontech.database.data.stars.LMProgramWebPublishing.getLMProgramWebPublishing( progIDs[i] );
            	Integer categoryID = webPub.getApplianceCategory().getApplianceCategory().getApplianceCategoryID();
            	
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
            
            return response;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static Integer[] getAllEnrLMProgramIDs(Integer energyCompanyID) {
		String sql = "SELECT ItemID FROM ECToGenericMapping WHERE "
				   + "EnergyCompanyID = ? AND MappingCategory = 'LMPrograms'";
				   
        java.sql.Connection conn = null;
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;
        java.util.ArrayList progIDList = new java.util.ArrayList();

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
