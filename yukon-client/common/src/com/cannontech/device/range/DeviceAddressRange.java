package com.cannontech.device.range;

/**
 * Ranges entered by SNelson on 20090615.
 * Ranges can be found in http://portal.cannontech.com/sites/Ops/Production%20Documents/Other%20Address%20Numbers-%20Allocation%20doc/Product%20Address%20Numbers.xls
 */
import java.util.Map;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class DeviceAddressRange
{
	
	//IMPORTANT!  4194012 is the reserved broadcast group address. This should not be a valid address that
	// could be entered into the Physical Address field.  SN (via Fisher) 20090724
	
	// Use Integer values for default range.
	// The address field in DeviceCarrierSettings is an int (and should be positive)
	private static final RangeBase RANGE_DEFAULT = new RangeBase(0, Integer.MAX_VALUE, null);
	private static final Map<PaoType, RangeBase> paoTypeRange;
	
	static {
        Builder<PaoType, RangeBase> builder = ImmutableMap.builder();
        add(builder, PaoType.MCT470, 100000, 3997695);		// MCT470= 510,001-594,000
    	add(builder, PaoType.MCT430A, 100000, 3997695);		// MCT430= 620,000-620,199; 620,200-700,000
    	add(builder, PaoType.MCT430S4, 100000, 3997695);
    	add(builder, PaoType.MCT430SL, 100000, 3997695);
    	add(builder, PaoType.MCT430A3, 100000, 3997695);
    	add(builder, PaoType.MCT410CL, 0, 3997695);	   		// 310,000-313,555; 313,600-314,499; 314,500-499,999; 594,001-610,000; 810,001-1,000,000; 2,897,700-3,597,700 
    	add(builder, PaoType.MCT410IL, 0, 3997695);			// 700,001-799,999; 1,000,001-1,398,100; 1,398,102 - 2,796,201   
    	add(builder, PaoType.MCT410FL, 0, 3997695);			// 3,797,700-3,996,927
    	add(builder, PaoType.MCT410GL, 0, 3997695);
    	add(builder, PaoType.MCTBROADCAST, 1, 4096);
    	
    	add(builder, PaoType.REPEATER, 464, 4302);
    	add(builder, PaoType.REPEATER_921, 464, 4302);
    	add(builder, PaoType.REPEATER_902, 800450, 809900);
    	add(builder, PaoType.REPEATER_850, 560001, 590000);
    	
    	add(builder, PaoType.CCU710A, 0, 127);
    	add(builder, PaoType.CCU711, 0, 126);
    	add(builder, PaoType.CCU721, 0, 126);
    	add(builder, PaoType.RTUWELCO, 0, 127);
    	add(builder, PaoType.CAPBANKCONTROLLER, 0, 999999999);
    	add(builder, PaoType.CBC_FP_2800, 0, 999999999);
    	add(builder, PaoType.DNP_CBC_6510, 0, 999999999);
    	add(builder, PaoType.SERIES_5_LMI, 0, 127);
    	add(builder, PaoType.RTC, 0, 15);
    	add(builder, PaoType.RTM, 0, 15);
    	
    	paoTypeRange = builder.build();
	}

	private static void add(Builder<PaoType, RangeBase> builder, PaoType paoType, int lowerRange, int upperRange) {
   		builder.put(paoType, new RangeBase(lowerRange, upperRange, paoType));
	}
	
	public static RangeBase getRangeBase(PaoType paoType) {
		RangeBase rangeBase = paoTypeRange.get(paoType);
		if (rangeBase == null) {
			CTILogger.debug("No Range found for " + paoType + ". Using Default Range [" + RANGE_DEFAULT.getRangeDescription() + "].");
			return RANGE_DEFAULT; 
		}
		return rangeBase;
	}		

	/**
	 * @Deprecated - use getRangeBase(PaoType paoType) */
	public static RangeBase getRangeBase(int deviceType) {
		return getRangeBase(PaoType.getForId(deviceType));
	}
}