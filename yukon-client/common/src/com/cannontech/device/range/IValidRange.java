package com.cannontech.device.range;

/**
 * @author rneuharth
 * Sep 5, 2002 at 4:16:55 PM
 * 
 * A undefined generated comment
 */
public interface IValidRange
{

   boolean isValidRange( Number num );
   
   boolean isValidRange( String txt );
   
   String getRangeDescription();
}
