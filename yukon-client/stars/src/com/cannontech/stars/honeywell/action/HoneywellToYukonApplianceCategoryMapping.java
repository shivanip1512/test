package com.cannontech.stars.honeywell.action;

/**
 * <p>Title: HoneywellToYukonApplianceCategoryMapping.java</p>
 * <p>Description: </p>
 * <p>Generation Time: Jul 31, 2002 1:46:41 PM</p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @author yao
 * @version 1.0
 */
public class HoneywellToYukonApplianceCategoryMapping {

	public static final String TABLE_NAME = "HoneywellToYukonApplianceCategoryMapping";

	/**
	 * Method getYukonApplianceCategory.
	 * @param hnwlAppType
	 * @return String
	 */
	public static String getYukonApplianceCategory(String hnwlAppType) {
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
               .append(" WHERE HoneywellApplianceCategory = ?");

            pstmt = conn.prepareStatement( sql.toString() );
            pstmt.setString( 1, hnwlAppType );
            rset = pstmt.executeQuery();

            if (rset.next())
                return rset.getString("YukonApplianceCategory");
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
