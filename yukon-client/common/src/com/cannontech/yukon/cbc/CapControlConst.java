package com.cannontech.yukon.cbc;

/**
 * @author rneuharth
 * Jul 9, 2002 at 2:26:06 PM
 * 
 * A undefined generated comment
 */
public interface CapControlConst
{
   public static final float PF_INVALID_VALUE = -1000000.0f;

   //cap bank states   
   public final static int BANK_OPEN = 0;
   public final static int BANK_CLOSE = 1;
   public final static int BANK_OPEN_QUESTIONABLE = 2;
   public final static int BANK_CLOSE_QUESTIONABLE = 3;
   public final static int BANK_OPEN_FAIL = 4;
   public final static int BANK_CLOSE_FAIL = 5;
   public final static int BANK_OPEN_PENDING = 6;
   public final static int BANK_CLOSE_PENDING = 7;
   
}
