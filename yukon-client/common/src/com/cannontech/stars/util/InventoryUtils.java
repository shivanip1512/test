package com.cannontech.stars.util;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryCategory;
import com.cannontech.common.inventory.YukonInventory;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class InventoryUtils {
    
    private static final Function<YukonInventory, Integer> yukonInventoryToIds = new Function<YukonInventory, Integer>() {
        @Override
        public Integer apply(YukonInventory from) {
            return from.getInventoryIdentifier().getInventoryId();
        }
    };

	/**
     * @deprecated Use InventoryCategory enum and/or HardwareType enum instead.
     */
    @Deprecated
	public static int getInventoryCategoryID(int deviceTypeID, LiteStarsEnergyCompany energyCompany) {
        YukonListEntry entry = YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( deviceTypeID );
        SelectionListService selectionListService = YukonSpringHook.getBean(SelectionListService.class);
		
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
            entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_UTILITYPRO ||
            entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_UTILITYPRO_G2 ||
            entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_UTILITYPRO_G3 ||
            entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_6200_XCOM ||
            entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_6600_XCOM)
		{
			return selectionListService.getListEntry(energyCompany, YukonListEntryTypes.YUK_DEF_ID_INV_CAT_ONEWAYREC).getEntryID();
		}
		else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_3102 ||
		        entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_ZIGBEE_UTILITYPRO ||
		        entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_6200_ZIGBEE ||
		        entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_6600_ZIGBEE)
		{
			return selectionListService.getListEntry(energyCompany, YukonListEntryTypes.YUK_DEF_ID_INV_CAT_TWOWAYREC).getEntryID();
		}
		else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_NON_YUKON_METER)
		{
			return selectionListService.getListEntry(energyCompany, YukonListEntryTypes.YUK_DEF_ID_INV_CAT_MCT).getEntryID();
		}
		
		return CtiUtilities.NONE_ZERO_ID;
	}
	
    /**
     * @deprecated Use {@link HardwareType#isLmHardware} or {@link InventoryCategory#isLmHardware}  
     */
    @Deprecated
	public static boolean isLMHardware(int categoryID) {
		YukonListEntry entry = YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( categoryID );
		return (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_INV_CAT_ONEWAYREC ||
				entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_INV_CAT_TWOWAYREC);
	}
	
	public static boolean isMCT(int categoryID) {
		YukonListEntry entry = YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( categoryID );
		return (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_INV_CAT_MCT);
	}
	
	public static boolean is3102(int devTypeId) {
		
		int devTypeDefID = YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry(devTypeId).getYukonDefID(); 
		if (devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_3102) {
			return true;
		}
		
		return false;
	}

    public static Iterable<Integer> convertYukonInventoryToIds(Iterable<? extends YukonInventory> inventory) {
        return Iterables.transform(inventory, yukonInventoryToIds);
    }
}