package com.cannontech.device.range;

import com.cannontech.database.data.pao.PAOGroups;


public class RangeBase implements IValidRange
{
   private long lowRange = Long.MIN_VALUE;
   private long upperRange = Long.MAX_VALUE;
   private long[] excludedValues = new long[0];
   private int deviceType = 0;
   private String rangeDescription = null;

	/**
	 * Constructor for RangeBase.
	 */
   protected RangeBase()
   {
      super();
   }

	public RangeBase( long lowRange_, long upperRange_, int deviceType_, String desc_, 
                      long[] excludedValues_ ) {
		super();
		lowRange = lowRange_;
		upperRange = upperRange_;
		deviceType = deviceType_;
		rangeDescription = desc_;
		excludedValues = excludedValues_;
	}

	public RangeBase( long lowRange_, long upperRange_, int deviceType_) {
      super();
      lowRange = lowRange_;
      upperRange = upperRange_;
      deviceType = deviceType_;
   }

   public RangeBase( long lowRange_, long upperRange_, int deviceType_, String desc_ )
   {
      super();
      lowRange = lowRange_;
      upperRange = upperRange_;
      deviceType = deviceType_;
      rangeDescription = desc_;
   }
   
   public String getRangeDescription()
   {
       if( rangeDescription == null) {
           rangeDescription = "Valid range for " + 
                               PAOGroups.getPAOTypeString(deviceType) + 
                               " addresses is " + lowRange +
                               " to " + upperRange;
       }
       return rangeDescription;
   }
   
   public void setExcludedValues( long[] values )
   {
      if( values == null )
         excludedValues = new long[0];

      excludedValues = values;
   }
   
   /**
    * @see com.cannontech.dbeditor.range.ValidRange#isValidRange(Number)
    */
   public boolean isValidRange(Number num)
   {
      if( num.longValue() >= lowRange && num.longValue() <= upperRange )
      {
         for( int i = 0; i < excludedValues.length; i++ )
            if( excludedValues[i] == num.longValue() )
                return false;
      
         return true;
      }
      
      return false;
   }
}
