package com.cannontech.database.db.state;

/**
 * @author rneuharth
 * Jul 25, 2002 at 5:08:09 PM
 * 
 * A undefined generated comment
 */
public final class StateGroupUtils
{
   // ALWAYS UPDATE THIS VALUE TO THE MAX GROUPID WE USE IN HERE!!
   public static final int PREDEFINED_MAX_STATE_GROUPID = 3;
 
  
   // predefined uneditable stateGroups
   public static final int STATEGROUPID_CAPBANK = PREDEFINED_MAX_STATE_GROUPID;
   public static final int STATEGROUP_THREE_STATE_STATUS = 2;
   public static final int STATEGROUP_TWO_STATE_STATUS = 1;

   public static final int SYSTEM_STATEGROUPID = 0;
   
   public static final int STATEGROUP_MCT410DISC = -6;
   public static final int STATEGROUP_ALARM = -5;
   public static final int STATEGROUP_CALCULATED = -3;
   public static final int STATEGROUP_ACCUMULATOR = -2;
   public static final int STATEGROUP_ANALOG = -1;

   //all StateGroup types
   public static final String GROUP_TYPE_STATUS = "Status";
   public static final String GROUP_TYPE_SYSTEM = "System";
   public static final String GROUP_TYPE_ANALOG = "Analog";
   
   
	/**
	 * Constructor for StateGroupUtils.
	 */
	private StateGroupUtils()
	{
		super();
	}

}
