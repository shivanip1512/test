package com.cannontech.device.range;

/**
 * @author rneuharth
 * Sep 5, 2002 at 4:20:40 PM
 * 
 * A undefined generated comment
 */
import com.cannontech.database.data.pao.DeviceTypes;

public class DeviceAddressRange
{
	private static final RangeBase RANGE_DEFAULT = new RangeBase();
   
	private static final RangeBase RANGE_MCT470 = 
		new RangeBase( 100000, 2796201, DeviceTypes.MCT470);
	
    private static final RangeBase RANGE_MCT430A = 
        new RangeBase( 100000, 2796201, DeviceTypes.MCT430A);
    
    private static final RangeBase RANGE_MCT430S4 = 
        new RangeBase( 100000, 2796201, DeviceTypes.MCT430S4);

    private static final RangeBase RANGE_MCT430SL = 
        new RangeBase( 100000, 2796201, DeviceTypes.MCT430SL);
   	
	private static final RangeBase RANGE_MCT410CL = 
		new RangeBase( 0, 2796201, DeviceTypes.MCT410CL); 
   	
   	private static final RangeBase RANGE_MCT410IL = 
		new RangeBase( 1000000, 2796201, DeviceTypes.MCT410IL);   
   	
   	private static final RangeBase RANGE_MCT410FL = 
   	    new RangeBase( 0, 2796201, DeviceTypes.MCT410FL);
   	
   	private static final RangeBase RANGE_MCT410GL = 
   	    new RangeBase( 0, 2796201, DeviceTypes.MCT410GL);

   	private static final RangeBase RANGE_MCT_BROADCAST = 
        new RangeBase( 1, 4096, DeviceTypes.MCTBROADCAST);

   	private static final RangeBase RANGE_REPEATER900 = 
        new RangeBase( 464, 4302, DeviceTypes.REPEATER);

    private static final RangeBase RANGE_REPEATER921 = 
        new RangeBase( 464, 4302, DeviceTypes.REPEATER_921);

   	private static final RangeBase RANGE_REPEATER902 = 
   		new RangeBase( 800450, 809900, DeviceTypes.REPEATER_902);

   	private static final RangeBase RANGE_CCU711 = 
        new RangeBase( 0, 127, DeviceTypes.CCU711);
   	
   	private static final RangeBase RANGE_CCU721 = 
   	    new RangeBase( 0, 127, DeviceTypes.CCU721);

   	private static final RangeBase RANGE_RTU_WELCO = 
        new RangeBase( 0, 127, DeviceTypes.RTUWELCO);

   	private static final RangeBase RANGE_VERSACOM = 
        new RangeBase( 0, 999999999, DeviceTypes.EDITABLEVERSACOMSERIAL);
         
    private static final RangeBase RANGE_CAPBANKCONTROLLER = 
        new RangeBase( 0, 999999999, DeviceTypes.CAPBANKCONTROLLER);

    private static final RangeBase RANGE_CBC_FP_2800 = 
        new RangeBase( 0, 999999999, DeviceTypes.CBC_FP_2800);

    private static final RangeBase RANGE_DNP_CBC_6510 = 
        new RangeBase( 0, 999999999, DeviceTypes.DNP_CBC_6510);

   	private static final RangeBase RANGE_SERIES_5_LMI = 
		new RangeBase( 0, 127, DeviceTypes.SERIES_5_LMI);

   	private static final RangeBase RANGE_RTC = 
		new RangeBase( 0, 15, DeviceTypes.RTC);
   
   	private static final RangeBase RANGE_RTM = 
		new RangeBase( 0, 15, DeviceTypes.RTM);

	/**
	 * Constructor for DeviceAddressRange.
	 */
	private DeviceAddressRange()
	{
		super();      
	}
   
   	private static RangeBase getRangeBase( int deviceType_ )
   	{
   	    switch (deviceType_) {
            case DeviceTypes.MCT470:
                return RANGE_MCT470;
                
            case DeviceTypes.MCT430A:
                return RANGE_MCT430A;    
                
            case DeviceTypes.MCT430S4:
                return RANGE_MCT430S4;    
            
            case DeviceTypes.MCT430SL:
                return RANGE_MCT430SL;    
        
            case DeviceTypes.MCT410CL:
                return RANGE_MCT410CL;
		
            case DeviceTypes.MCT410IL:
                return RANGE_MCT410IL;
                
            case DeviceTypes.MCT410FL:
                return RANGE_MCT410FL;
                
            case DeviceTypes.MCT410GL:
                return RANGE_MCT410GL;
            
            case DeviceTypes.REPEATER:
                return RANGE_REPEATER900;
            
            case DeviceTypes.REPEATER_902:
                return RANGE_REPEATER902;
                
            case DeviceTypes.CCU711:
                return RANGE_CCU711;
                
            case DeviceTypes.CCU721:
                return RANGE_CCU721;
                
            case DeviceTypes.MCTBROADCAST:
                return RANGE_MCT_BROADCAST;

            case DeviceTypes.RTUWELCO:
                return RANGE_RTU_WELCO;
                
            case DeviceTypes.SERIES_5_LMI:
                return RANGE_SERIES_5_LMI;
                
            case DeviceTypes.RTC:
                return RANGE_RTC;
            
            case DeviceTypes.RTM:
                return RANGE_RTM;
                
            case DeviceTypes.CAPBANKCONTROLLER:
                return RANGE_CAPBANKCONTROLLER;
                
            case DeviceTypes.CBC_FP_2800:
                return RANGE_CBC_FP_2800;
            
            case DeviceTypes.DNP_CBC_6510:
                return RANGE_DNP_CBC_6510;

            case DeviceTypes.EDITABLEVERSACOMSERIAL:
                return RANGE_VERSACOM;
                
            default:
            return RANGE_DEFAULT;
        }
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