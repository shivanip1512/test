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
   
   public final static String STRING_BANK_OPEN = "Open";
   public final static String STRING_BANK_CLOSE = "Close";
   public final static String STRING_BANK_OPEN_QUESTIONABLE = "OpenQuestionable";
   public final static String STRING_BANK_CLOSEQUESTIONABLE = "CloseQuestionable";
   public final static String STRING_BANK_OPEN_FAIL = "OpenFail";
   public final static String STRING_BANK_CLOSE_FAIL = "CloseFail";
   public final static String STRING_BANK_OPEN_PENDING = "OpenPending";
   public final static String STRING_BANK_CLOSE_PENDING = "ClosePending";
   
   public final static int CAPBANKSTATUS_STATEGROUP_ID = 3;
   public final static int ONELINE_SUBSTATE_STATEGROUP_ID = 6;
   public final static int ONELINE_VERIFY_STATEGROUP_ID = 7;
   
   public final static String CAPBANKSTATUS_STATEGROUP_NAME = "CapBankStatus";
   public final static String ONELINE_SUBSTATE_STATEGROUP_NAME = "1LNSUBSTATE";
   public final static String ONELINE_VERIFY_STATEGROUP_NAME = "1LNVERIFY";
   
}
