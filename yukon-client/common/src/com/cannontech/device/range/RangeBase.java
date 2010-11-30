package com.cannontech.device.range;

import java.util.Arrays;

import com.cannontech.common.pao.PaoType;


public class RangeBase implements IValidRange
{
   private long lowRange = Long.MIN_VALUE;
   private long upperRange = Long.MAX_VALUE;
   private long[] excludedValues = new long[0];
   private PaoType paoType;
   private String rangeDescription = null;

	public RangeBase( long lowRange_, long upperRange_, PaoType paoType, String desc_, 
                      long[] excludedValues_ ) {
		super();
		lowRange = lowRange_;
		upperRange = upperRange_;
		this.paoType = paoType;
		rangeDescription = desc_;
		excludedValues = excludedValues_;
	}

	public RangeBase( long lowRange_, long upperRange_, PaoType paoType) {
      super();
      lowRange = lowRange_;
      upperRange = upperRange_;
      this.paoType = paoType;
   }

   public RangeBase( long lowRange_, long upperRange_, PaoType paoType, String desc_ )
   {
      super();
      lowRange = lowRange_;
      upperRange = upperRange_;
      this.paoType = paoType;
      rangeDescription = desc_;
   }
   
   public String getRangeDescription()
   {
       if( rangeDescription == null) {
           rangeDescription = "PaoType:" + paoType +
           						"  Valid Range:" + lowRange + " to " + upperRange +
           						"  Excluded: " + Arrays.toString(excludedValues);
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
   
   public long getLowRange() {
	   return lowRange;
   }
   
   public long getUpperRange() {
	   return upperRange;
   }
}
