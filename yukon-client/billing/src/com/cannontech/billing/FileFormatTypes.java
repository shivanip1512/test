package com.cannontech.billing;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.spring.YukonSpringHook;

/**
 * Insert the type's description here.
 * Creation date: (5/18/00 2:08:15 PM)
 * @author: 
 */
public final class FileFormatTypes {
    private static SimpleJdbcTemplate jdbcTemplate = YukonSpringHook.getBean("simpleJdbcTemplate", SimpleJdbcTemplate.class);
    
	// Constants representing types of file formats
	// ** THESE MUST MATCH THE DATABASE TABLE BILLINGFILEFORMATS **
	// ** IF YOU ADD TO THESE VALUES, YOU MUST ADD TO THE DATABASE
	// ** TABLE THE FORMATS THAT ARE VALID FOR EACH CUSTOMER.
	public static final int INVALID = -1;
	public static final int SEDC = 0;
	public static final int CADP = 1;
	public static final int CADPXL2 = 2;
	public static final int WLT_40 = 3;
	public static final int CTICSV = 4;
	public static final int OPU = 5;
	public static final int DAFFRON = 6;
	public static final int NCDC = 7;
//	public static final int CADPXL1 = 8; Not valid - not implemented
	public static final int CTIStandard2 = 9;
	public static final int MVRS = 10;
	public static final int MV_90 = 11;
	public static final int SEDC_5_4 = 12;
	public static final int NISC_TURTLE = 13;
	public static final int NISC_NCDC = 14;
	public static final int NCDC_HANDHELD = 15;
	public static final int NISC_TOU_KVARH = 16;
	public static final int SEDC_yyyyMMdd = 17;
	public static final int ATS = 18;
	public static final int NISC_TURTLE_NO_LIMIT_KWH = 19;
	public static final int IVUE_BI_T65 = 20;
	public static final int SIMPLE_TOU = 21;
	public static final int EXTENDED_TOU = 22;
    public static final int BIG_RIVERS_ELEC_COOP = 23;
  	public static final int EXTENDED_TOU_INCODE = 24;
  	public static final int ITRON_REGISTER_READINGS_EXPORT = 25;
  	public static final int BASIC_DEVICE_NAME = 26;

	public static final String SEDC_STRING = "SEDC";
	public static final String CADP_STRING = "CADP";
	public static final String CADPXL2_STRING = "CADPXL2";
	public static final String WLT_40_STRING = "WLT-40";
	public static final String CTICSV_STRING = "CTI-CSV";
	public static final String OPU_STRING = "OPU";
	public static final String DAFFRON_STRING = "DAFFRON";
	public static final String NCDC_STRING = "NCDC";
	public static final String CTI_STANDARD2_STRING = "CTI2";
	public static final String MVRS_STRING = "MVRS";
	public static final String SEDC_5_4_STRING = "SEDC 5.4";
	public static final String NISC_TURTLE_STRING = "NISC-Turtle";
	public static final String NISC_NCDC_STRING = "NISC-NCDC";
	public static final String NCDC_HANDHELD_STRING = "NCDC-Handheld";
	public static final String NISC_TOU_KVARH_STRING = "NISC TOU (kVarH)";
	public static final String SEDC_yyyyMMdd_STRING = "SEDC (yyyyMMdd)";
	public static final String ATS_STRING = "ATS";
	public static final String NISC_TURTLE_NO_LIMIT_KWH_STRING = "NISC-Turtle No Limit kWh";
	public static final String IVUE_BI_T65_STRING = "IVUE_BI_T65";
	public static final String SIMPLE_TOU_STRING = "SIMPLE_TOU";
	public static final String EXTENDED_TOU_STRING = "EXTENDED_TOU";
    public static final String BIG_RIVERS_STRING = "Big Rivers Elec Coop";
	public static final String EXTENDED_TOU_INCODE_STRING = "INCODE (Extended TOU)";
	public static final String ITRON_REGISTER_READINGS_EXPORT_STRING = "Itron Register Readings Export";
	public static final String BASIC_DEVICE_NAME_STRING = "Basic_DeviceName";
	
    private static final int[] defaultValidFormatIDs;
    private static final String[] defaultValidFormatTypes;
    
    static {
        defaultValidFormatIDs = new int[] {
                SEDC, 
                CADP,
                CADPXL2, 
                CTICSV, 
                OPU, 
                DAFFRON, 
                NCDC,
                CTIStandard2, 
                SEDC_5_4,
                NISC_TURTLE,
                NISC_NCDC,
                NCDC_HANDHELD,
                ATS,
                IVUE_BI_T65,
                SIMPLE_TOU,
                EXTENDED_TOU
        };
        
        defaultValidFormatTypes = new String[]{
                SEDC_STRING, 
                CADP_STRING, 
                CADPXL2_STRING, 
                CTICSV_STRING, 
                OPU_STRING, 
                DAFFRON_STRING, 
                NCDC_STRING, 
                CTI_STANDARD2_STRING,
                SEDC_5_4_STRING,
                NISC_TURTLE_STRING,
                NISC_NCDC_STRING,
                NCDC_HANDHELD_STRING,
                ATS_STRING,
                IVUE_BI_T65_STRING,
                SIMPLE_TOU_STRING,
                EXTENDED_TOU_STRING
        };
    }
    /**
     * This method was created in VisualAge.
     * @return int
     * @param typeStr java.lang.String
     */
    public final static int getFormatID(String typeStr) {
        final String sql = "SELECT FORMATID FROM BillingFileFormats WHERE FORMATTYPE = ?";
        try {
            int formatId = jdbcTemplate.queryForInt(sql, typeStr);
            return formatId;
        } catch (DataAccessException e) {
            throw new DataRetrievalFailureException("FileFormatTypes::getFormatID(String) - Unrecognized type: " + typeStr);
        }
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (5/18/00 2:34:54 PM)
     */
    public final static String getFormatType(int typeEnum) {
        final String sql = "SELECT FORMATTYPE FROM BillingFileFormats WHERE FORMATID = ?";
        try {
            String formatType = jdbcTemplate.queryForObject(sql, String.class, typeEnum);
            return formatType;
        } catch (DataAccessException e) {
            throw new DataRetrievalFailureException("FileFormatTypes::getFormatType(int) - received unknown type: " + typeEnum );
        }
    }

    /**
     * @return Returns the validFormatIds.
     */
    public static int[] getValidFormatIDs() {
        final String sql = "SELECT FORMATID FROM BillingFileFormats WHERE FORMATID >= 0";
        try {
            List<Integer> list = jdbcTemplate.query(sql, new ParameterizedRowMapper<Integer>() {
                public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getInt("FORMATID");
                }
            });
            
            if (list.size() > 0) {
                int[] formatIds = new int[list.size()];
                for (int x = 0; x < list.size(); x++) {
                    formatIds[x] = list.get(x);
                }
                return formatIds;
            }    
        } catch (DataAccessException e) {
            return defaultValidFormatIDs;
        }
        return defaultValidFormatIDs;
    }

	public static String[] getValidFormatTypes() {
        final String sql = "SELECT FORMATTYPE FROM BillingFileFormats WHERE FORMATID >= 0";
        try {
            List<String> list = jdbcTemplate.query(sql, new ParameterizedRowMapper<String>(){
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getString("FORMATTYPE");
                }
            });
            
            if (list.size() > 0) {
                return list.toArray(new String[list.size()]);
            }
        } catch (DataAccessException e) {
            return defaultValidFormatTypes;
        }
        return defaultValidFormatTypes;
	}
    
    public static Map<Integer,String> getValidFormats() {
        final String sql = "SELECT FORMATID,FORMATTYPE FROM BillingFileFormats WHERE FORMATID >= 0";
        final Map<Integer,String> resultMap = new HashMap<Integer,String>();
        try {
            jdbcTemplate.getJdbcOperations().query(sql, new RowCallbackHandler() {
                public void processRow(ResultSet rs) throws SQLException {
                    resultMap.put(rs.getInt("FORMATID"), rs.getString("FORMATTYPE"));
                }
            });
        } catch (DataAccessException e) {
            for (int x = 0; x < defaultValidFormatIDs.length; x++) {
                resultMap.put(defaultValidFormatIDs[x], defaultValidFormatTypes[x]);
            }
        }
        return resultMap;
    }
    
    public static void setSimpleJdbcTemplate(final SimpleJdbcTemplate jdbcTemplate) {
        FileFormatTypes.jdbcTemplate = jdbcTemplate;
    }
}
