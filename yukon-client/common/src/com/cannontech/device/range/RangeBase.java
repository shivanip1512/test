package com.cannontech.device.range;

/**
 * @author rneuharth
 * Sep 5, 2002 at 4:18:31 PM
 * 
 * A undefined generated comment
 */
public class RangeBase implements IValidRange
{
   private long lowRange = Long.MIN_VALUE;
   private long upperRange = Long.MAX_VALUE;
   private long[] excludedValues = new long[0];
   private String rangeDescription = null;

	/**
	 * Constructor for RangeBase.
	 */
   protected RangeBase()
   {
      super();
   }

	public RangeBase( long lowRange_, long upperRange_, String desc_, 
                      long[] excludedValues_ )   
	{
		super();
      lowRange = lowRange_;
      upperRange = upperRange_;
      rangeDescription = desc_;
      excludedValues = excludedValues_;
	}

   public RangeBase( long lowRange_, long upperRange_, String desc_ )
   {
      super();
      lowRange = lowRange_;
      upperRange = upperRange_;
      rangeDescription = desc_;
   }

   public String getRangeDescription()
   {
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

   /**
    * @see com.cannontech.dbeditor.range.ValidRange#isValidRange(String)
    */
   public boolean isValidRange(String txt)
   {
      return false;
   }   
   
}
