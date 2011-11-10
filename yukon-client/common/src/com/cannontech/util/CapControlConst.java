package com.cannontech.util;

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
   public final static int BANK_TIME_OF_DAY = 8;
   
   public final static int CAPBANKSTATUS_STATEGROUP_ID = 3;
   public final static int ONELINE_SUBSTATE_STATEGROUP_ID = 6;
   public final static int ONELINE_VERIFY_STATEGROUP_ID = 7;
   
   public final static String CAPBANKSTATUS_STATEGROUP_NAME = "CapBankStatus";
   public final static String ONELINE_SUBSTATE_STATEGROUP_NAME = "1LNSUBSTATE";
   public final static String ONELINE_VERIFY_STATEGROUP_NAME = "1LNVERIFY";
   
   public final static int CC_NORMAL_QUAL = 0;
   public final static int CC_PARTIAL_QUAL = 1;
   public final static int CC_SIGNIFICANT_QUAL = 2;
   public final static int CC_ABNORMAL_QUAL = 3;
   public final static int CC_FAIL_QUAL = 4;
   public final static int CC_COMMFAIL_QUAL = 5;
   public final static int CC_NO_CONTROL_QUAL = 6;
   public final static int CC_UNSOLICITED_QUAL = 7;
   
}
