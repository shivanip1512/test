package com.cannontech.stars.honeywell.action;

/**
 * <p>Title: HoneywellApplianceToLMProgramMapping.java</p>
 * <p>Description: </p>
 * <p>Generation Time: Jul 31, 2002 1:19:29 PM</p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @author yao
 * @version 1.0
 */
public class HoneywellApplianceToLMProgramMapping {
	
	public static final String TABLE_NAME = "HoneywellApplianceToLMProgramMapping";
	
	/**
	 * Method getLMProgramID.
	 * @param hnwlAppID
	 * @return Integer
	 */
	public static Integer getLMProgramID(String hnwlAppID) {
        java.sql.Connection conn = null;
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;

        try {
            conn = com.cannontech.database.PoolManager.getInstance().getConnection(
                        com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
            if (conn == null) return null;

            StringBuffer sql = new StringBuffer();
            sql.append("SELECT * FROM ")
               .append(TABLE_NAME)
               .append(" WHERE HoneywellApplianceID = ?");

            pstmt = conn.prepareStatement( sql.toString() );
            pstmt.setString( 1, hnwlAppID );
            rset = pstmt.executeQuery();

            if (rset.next())
                return new Integer( rset.getInt("LMProgramID") );
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (conn != null) conn.close();
                if (pstmt != null) pstmt.close();
                if (rset != null) rset.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        
		return null;
	}

}
