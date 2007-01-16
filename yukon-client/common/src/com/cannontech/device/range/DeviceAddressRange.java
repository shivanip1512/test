package com.cannontech.device.range;

/**
 * @author rneuharth
 * Sep 5, 2002 at 4:20:40 PM
 * 
 * A undefined generated comment
 */
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.pao.DeviceTypes;

public class DeviceAddressRange
{
	private static final RangeBase RANGE_DEFAULT = new RangeBase();
   
	private static final RangeBase RANGE_MCT470 = 
		new RangeBase( 100000, 2796201, "Valid range for MCT470 addresses is 100000 to 2796201");
		// and can not be 1398101" );
    private static final RangeBase RANGE_MCT430A = 
        new RangeBase( 100000, 2796201, "Valid range for MCT430A addresses is 100000 to 2796201");
        // and can not be 1398101" );
    
    private static final RangeBase RANGE_MCT430S = 
        new RangeBase( 100000, 2796201, "Valid range for MCT430S addresses is 100000 to 2796201");
        // and can not be 1398101" );

    private static final RangeBase RANGE_MCT430IN = 
        new RangeBase( 100000, 2796201, "Valid range for MCT430IN addresses is 100000 to 2796201");
   	
	private static final RangeBase RANGE_MCT410CL = 
		new RangeBase( 0, 2796201, "Valid range for MCT410CL addresses is 0 to 2796201");
		// and can not be 1398101" ); 
   	
   	private static final RangeBase RANGE_MCT410IL = 
		new RangeBase( 1000000, 2796201, "Valid range for MCT410IL addresses is 1000000 to 2796201");
		// and can not be 1398101" );   
   	
   	private static final RangeBase RANGE_MCT410FL = 
   	    new RangeBase( 0, 2796201, "Valid range for MCT410FL addresses is 0 to 2796201");
   	
   	private static final RangeBase RANGE_MCT410GL = 
   	    new RangeBase( 0, 2796201, "Valid range for MCT410GL addresses is 0 to 2796201");
   
   	private static final RangeBase RANGE_MCT = 
    	new RangeBase( 0, 2796201, "Valid range for MCT addresses is 0 to 2796201");
        // and can not be 1398101" );

   	private static final RangeBase RANGE_MCT_BROADCAST = 
        new RangeBase( 1, 4096, "Valid range for MCT Broadcast addresses is 1 to 4096" );

   	private static final RangeBase RANGE_REPEATER900 = 
        new RangeBase( 464, 4302, "Valid range for " + DeviceTypes.STRING_REPEATER[0] + " addresses is 464 to 4302" );

   	private static final RangeBase RANGE_CCU711 = 
        new RangeBase( 0, 127, "Valid range for " + DeviceTypes.STRING_CCU_711[0] + " addresses is 0 to 127" );
   	
   	private static final RangeBase RANGE_CCU721 = 
   	    new RangeBase( 0, 127, "Valid range for " + DeviceTypes.STRING_CCU_721[0] + " addresses is 0 to 127" );

   	private static final RangeBase RANGE_RTU_WELCO = 
        new RangeBase( 0, 127, "Valid range for " + DeviceTypes.STRING_RTU_WELCO[0] + " addresses is 0 to 127" );

   	private static final RangeBase RANGE_VERSACOM = 
        new RangeBase( 0, 999999999, "Valid range for " + DeviceTypes.STRING_VERSACOM_SERIAL_NUMBER[0] + " is 0 to 999999999" );
         
   	private static final RangeBase RANGE_SERIES_5_LMI = 
		new RangeBase( 0, 127, "Valid range for " + DeviceTypes.STRING_SERIES_5_LMI[0] + " addresses is 0 to 127" );

   	private static final RangeBase RANGE_RTC = 
		new RangeBase( 0, 15, "Valid range for " + DeviceTypes.STRING_RTC[0] + " addresses is 0 to 15" );
   
   	private static final RangeBase RANGE_RTM = 
		new RangeBase( 0, 15, "Valid range for " + DeviceTypes.STRING_RTM[0] + " addresses is 0 to 15" );

   	//build any extra params into the RangeBase appropriate intsances
   	static
   	{
   		//NOT USED ANYMORE AS OF 12-23-2002
      	//long[] excludedValues = { 1398101 }; 
      	//RANGE_MCT.setExcludedValues( excludedValues );            
   	}

	/**
	 * Constructor for DeviceAddressRange.
	 */
	private DeviceAddressRange()
	{
		super();      
	}
   
   	private static RangeBase getRangeBase( int deviceType_ )
   	{
   		if( deviceType_ == DeviceTypesFuncs.MCT470)
		{
	   		return RANGE_MCT470;	
		}
        
        if(deviceType_ == DeviceTypesFuncs.MCT430EL)
        {
            return RANGE_MCT430A;    
        }
        
        if(deviceType_ == DeviceTypesFuncs.MCT430LG)
        {
            return RANGE_MCT430S;    
        }
        
        if(deviceType_ == DeviceTypesFuncs.MCT430IN)
        {
            return RANGE_MCT430IN;    
        }

		if( deviceType_ == DeviceTypesFuncs.MCT410CL )
		{
			return RANGE_MCT410CL;
		}
      	if( deviceType_ == DeviceTypesFuncs.MCT410IL )
      	{
        	return RANGE_MCT410IL;
      	}
      	if( deviceType_ == DeviceTypesFuncs.MCT410FL )
      	{
      	    return RANGE_MCT410FL;
      	}
      	if( deviceType_ == DeviceTypesFuncs.MCT410GL )
      	{
      	    return RANGE_MCT410GL;
      	}
      	else if( com.cannontech.database.data.device.DeviceTypesFuncs.isMCT(deviceType_) )
      	{
         	return RANGE_MCT;
      	}
      	else if( DeviceTypes.REPEATER == deviceType_ )
      	{
         	return RANGE_REPEATER900;
      	}
      	else if( DeviceTypes.CCU711 == deviceType_ )
      	{
        	return RANGE_CCU711;
      	}
      	else if( DeviceTypes.CCU721 == deviceType_ )
      	{
      	    return RANGE_CCU721;
      	}
      	else if( DeviceTypes.MCTBROADCAST == deviceType_ )
      	{
         	return RANGE_MCT_BROADCAST;
      	}
      	else if( DeviceTypes.RTUWELCO == deviceType_ )
      	{
         	return RANGE_RTU_WELCO;
      	}
      	else if( DeviceTypes.SERIES_5_LMI == deviceType_ )
	  	{
	  	 	return RANGE_SERIES_5_LMI;
	  	}
	  	else if( DeviceTypes.RTC == deviceType_ )
	  	{
	     	return RANGE_RTC;
	  	}
	  	else if( DeviceTypes.RTM == deviceType_ )
	  	{
	  	 	return RANGE_RTM;
	  	}
      	else if( DeviceTypes.CAPBANKCONTROLLER == deviceType_
               || DeviceTypes.CBC_FP_2800 == deviceType_
               || DeviceTypes.DNP_CBC_6510 == deviceType_ )
      	{
         	return RANGE_VERSACOM;
      	}
      	else
         	return RANGE_DEFAULT;
   	}
   
   	public synchronized static String getRangeMessage( int deviceType_ )
   	{
      	return getRangeBase( deviceType_ ).getRangeDescription();
   	}
   
   	public synchronized static boolean isValidRange( int deviceType_, long value_ )
   	{
      	return getRangeBase( deviceType_ ).isValidRange( new Long(value_) );
   	}
   
}