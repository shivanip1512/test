/*
 * Created on Jan 3, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsSearchDao;


/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class InventoryUtils {

	public static final int SA205_UNUSED_ADDR = 3909;
	
	public static final int HW_CONFIG_TYPE_EXPRESSCOM = 1;
	public static final int HW_CONFIG_TYPE_VERSACOM = 2;
	public static final int HW_CONFIG_TYPE_SA205 = 3;
	public static final int HW_CONFIG_TYPE_SA305 = 4;
	public static final int HW_CONFIG_TYPE_SA_SIMPLE = 5;
	
	public static int getInventoryCategoryID(int deviceTypeID, LiteStarsEnergyCompany energyCompany) {
		YukonListEntry entry = DaoFactory.getYukonListDao().getYukonListEntry( deviceTypeID );
		
		if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_5000_XCOM ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_5000_VCOM ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_4000 ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_3000 ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_2000 ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_COMM_EXPRESSSTAT ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA205 ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA305 ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA_SIMPLE ||
            entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT_HEATPUMP ||
            entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_UTILITYPRO) 
		{
			return energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_INV_CAT_ONEWAYREC).getEntryID();
		}
		else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_ENERGYPRO || entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_3102)
		{
			return energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_INV_CAT_TWOWAYREC).getEntryID();
		}
		else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT)
		{
			return energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_INV_CAT_MCT).getEntryID();
		}
		
		return CtiUtilities.NONE_ZERO_ID;
	}
	
	public static boolean isLMHardware(int categoryID) {
		YukonListEntry entry = DaoFactory.getYukonListDao().getYukonListEntry( categoryID );
		return (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_INV_CAT_ONEWAYREC ||
				entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_INV_CAT_TWOWAYREC);
	}

	public static boolean isNonYukonMeter(int categoryID) {
		YukonListEntry entry = DaoFactory.getYukonListDao().getYukonListEntry( categoryID );
		return (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_INV_CAT_NON_YUKON_METER);
	}
	
	public static boolean isMCT(int categoryID) {
		YukonListEntry entry = DaoFactory.getYukonListDao().getYukonListEntry( categoryID );
		return (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_INV_CAT_MCT);
	}
	
	public static boolean isValidHardwareSerialNumber(String serialNum) {
        boolean valid = false;
        //restrict the hardware serialNumber to 18-digit numeric value (long)
        if (!StringUtils.isBlank(serialNum) && StringUtils.isNumeric(serialNum) && serialNum.length() <= 18) {
            valid = true;
        }
        return valid;
    }
	
	public static int getHardwareConfigType(int devTypeID) {
		int devTypeDefID = DaoFactory.getYukonListDao().getYukonListEntry( devTypeID ).getYukonDefID();
		if (devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_5000_XCOM
			|| devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT
			|| devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_COMM_EXPRESSSTAT
			|| devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_ENERGYPRO
			|| devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_UTILITYPRO
            || devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT_HEATPUMP
            || devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_3102)
			return HW_CONFIG_TYPE_EXPRESSCOM;
		else if (devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_5000_VCOM
			|| devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_4000
			|| devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_3000
			|| devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_2000)
			return HW_CONFIG_TYPE_VERSACOM;
		else if (devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA205)
			return HW_CONFIG_TYPE_SA205;
		else if (devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA305)
			return HW_CONFIG_TYPE_SA305;
		else if (devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA_SIMPLE)
			return HW_CONFIG_TYPE_SA_SIMPLE;
		
		return 0;
	}
	
	public static boolean supportServiceInOut(int devTypeID) {
		int hwConfigType = getHardwareConfigType( devTypeID );
		return hwConfigType == HW_CONFIG_TYPE_EXPRESSCOM
			|| hwConfigType == HW_CONFIG_TYPE_VERSACOM;
	}
	
	public static boolean supportConfiguration(int devTypeID) {
		int devTypeDefID = DaoFactory.getYukonListDao().getYukonListEntry( devTypeID ).getYukonDefID();
		return (devTypeDefID != YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA_SIMPLE);
	}
	
	public static boolean isAdditionalProtocol(int devTypeDefID) {
		return (devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA205
			|| devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA305
			|| devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA_SIMPLE);
	}
	
	public static List<LiteStarsLMHardware> getLMHardwareInRange(LiteStarsEnergyCompany energyCompany, int devTypeDefID, Integer snFrom, Integer snTo) {
		
		StarsSearchDao starsSearchDao = 
			YukonSpringHook.getBean("starsSearchDao", StarsSearchDao.class);
		
		// If either end of the range is null - there is no limit on that end
		if(snFrom == null) {
			snFrom = Integer.MIN_VALUE;
		}
		if(snTo == null) {
			snTo = Integer.MAX_VALUE;
		}
		
		List<LiteStarsLMHardware> hwList = starsSearchDao.searchLMHardwareBySerialNumberRange(
														snFrom, 
														snTo, 
														devTypeDefID, 
														Collections.singletonList(energyCompany));
		
		return hwList;
	}
    
    public static boolean isSerialWithPossibleCharsGreaterThan(String serial, String minimumVal)
    {
        Long serialToCompare = null;
        try 
        {
            serialToCompare = Long.valueOf( serial );
        }
        catch (NumberFormatException e) {}
        
        Long requiredMin = null;
        try 
        {
            requiredMin = Long.valueOf( minimumVal );
        }
        catch (NumberFormatException e) {}
        
        if (serialToCompare != null && requiredMin != null) 
        {
            int result = serialToCompare.compareTo( requiredMin );
            if(result >= 0) 
                return true;
            return false;
        }
        else if(requiredMin == null && serialToCompare == null)
        {
            int result = serial.compareTo(minimumVal);
            if(result >= 0)
                return true;
            return false;
        }
        else
            return false;
       
    }
    
    public static boolean isSerialWithPossibleCharsLessThan(String serial, String maximumVal)
    {
        Long serialToCompare = null;
        try 
        {
            serialToCompare = Long.valueOf( serial );
        }
        catch (NumberFormatException e) {}
        
        Long requiredMax = null;
        try 
        {
            requiredMax = Long.valueOf( maximumVal );
        }
        catch (NumberFormatException e) {}
        
        if (serialToCompare != null && requiredMax != null) 
        {
            int result = serialToCompare.compareTo( requiredMax );
            if(result <= 0) 
                return true;
            return false;
        }
        else if(requiredMax == null && serialToCompare == null)
        {
            int result = serial.compareTo( maximumVal );
            if(result <= 0)
                return true;
            return false;
        }
        else
            return false;
       
    }
    
    public static Integer returnIntegerIfPossible(String specificString)
    {
        Integer convert = new Integer(-999999);
        try 
        {
            convert = Integer.valueOf( specificString );
        }
        catch (NumberFormatException e) {}
        
        return convert;
    }
    
    //TODO: need to start putting these in an appropriate services/DAO structure
    public static Integer getYukonLoadGroupIDFromSTARSProgramID(int progID) {
        String sql = "select distinct LMGroupDeviceID from LMProgramDirectGroup ldg, LMProgramWebPublishing lwp " +
                "where ldg.DeviceID = lwp.DeviceID AND lwp.ProgramID = " + progID;
        
        Integer groupID = new Integer(0);
        
        try {
            JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
            groupID = jdbcOps.queryForInt(sql);
        } 
        catch (IncorrectResultSizeDataAccessException e) {
            return groupID;
        }
        
        return groupID;
    }

}
