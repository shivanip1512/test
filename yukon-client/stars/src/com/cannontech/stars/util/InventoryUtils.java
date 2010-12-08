package com.cannontech.stars.util;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.inventory.HardwareConfigType;
import com.cannontech.common.inventory.YukonInventory;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class InventoryUtils {
    
    private static final Function<YukonInventory, Integer> yukonInventoryToIds = new Function<YukonInventory, Integer>() {
        @Override
        public Integer apply(YukonInventory from) {
            return from.getInventoryIdentifier().getInventoryId();
        }
    };

	public static final int SA205_UNUSED_ADDR = 3909;
	
	public static final int HW_CONFIG_TYPE_EXPRESSCOM = HardwareConfigType.EXPRESSCOM.getHardwareConfigTypeId();
	public static final int HW_CONFIG_TYPE_VERSACOM = HardwareConfigType.VERSACOM.getHardwareConfigTypeId();
	public static final int HW_CONFIG_TYPE_SA205 = HardwareConfigType.SA205.getHardwareConfigTypeId();
	public static final int HW_CONFIG_TYPE_SA305 = HardwareConfigType.SA305.getHardwareConfigTypeId();
	public static final int HW_CONFIG_TYPE_SA_SIMPLE = HardwareConfigType.SA_SIMPLE.getHardwareConfigTypeId();
	
	public static int getInventoryCategoryID(int deviceTypeID, LiteStarsEnergyCompany energyCompany) {
		YukonListEntry entry = DaoFactory.getYukonListDao().getYukonListEntry( deviceTypeID );
		
		if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_5000_XCOM ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_5000_VCOM ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_4000 ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_3000 ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_2000 ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_1000 ||
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
	
	public static boolean isTwoWayLcr(int devTypeId) {
		
		int devTypeDefID = DaoFactory.getYukonListDao().getYukonListEntry(devTypeId).getYukonDefID(); 
		if (devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_3102) {
			return true;
		}
		
		return false;
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
	
	public static boolean isAdditionalProtocol(int devTypeDefID) {
		return (devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA205
			|| devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA305
			|| devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA_SIMPLE);
	}
	
	public static List<LiteStarsLMHardware> getLMHardwareInRange(LiteStarsEnergyCompany energyCompany, int devTypeDefID, long snFrom, long snTo)
	    throws PersistenceException {
		
		StarsSearchDao starsSearchDao = 
			YukonSpringHook.getBean("starsSearchDao", StarsSearchDao.class);
		
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
    
    public static Integer getYukonLoadGroupIDFromSTARSProgramID(int progID) {
        Integer groupID = new Integer(0);
        ProgramDao starsProgramDao = YukonSpringHook.getBean("starsProgramDao",
                                                        ProgramDao.class);
        List<Integer> loadGroupList = starsProgramDao.getGroupIdsByProgramId(progID);
        if (loadGroupList.size() > 0) {
            groupID = loadGroupList.get(0);
        }
        return groupID;
    }

    public static boolean supportConfiguration(int devTypeID) {
        int devTypeDefID = DaoFactory.getYukonListDao().getYukonListEntry( devTypeID ).getYukonDefID();
        return (devTypeDefID != YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA_SIMPLE);
    }
    
    public static Iterable<Integer> convertYukonInventoryToIds(Iterable<? extends YukonInventory> inventory) {
        return Iterables.transform(inventory, yukonInventoryToIds);
    }
}