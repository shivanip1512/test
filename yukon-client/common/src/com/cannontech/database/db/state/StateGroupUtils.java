package com.cannontech.database.db.state;

import com.cannontech.common.YukonColorPalette;
import com.cannontech.database.data.state.GroupState;

/**
 * @author rneuharth Jul 25, 2002 at 5:08:09 PM
 * 
 * A undefined generated comment
 */
public final class StateGroupUtils {
    // ALWAYS UPDATE THIS VALUE TO THE MAX GROUPID WE USE IN HERE!!
    public static final int PREDEFINED_MAX_STATE_GROUPID = 3;

    // predefined uneditable stateGroups
    public static final int STATEGROUPID_CAPBANK = PREDEFINED_MAX_STATE_GROUPID;

    public static final int STATEGROUP_THREE_STATE_STATUS = -9;

    public static final int STATEGROUP_TWO_STATE_STATUS = 1;

    public static final int SYSTEM_STATEGROUPID = 0;
    
    public static final int STATEGROUP_TRUEFALSE = 4;

    public static final int STATEGROUP_MCT410DISC = -6;

    public static final int STATEGROUP_ALARM = -5;

    public static final int STATEGROUP_ANALOG = -1;

    public static final int STATEGROUP_TWO_STATE_ACTIVE = -8;
    
    public static final int STATEGROUP_COMMISSIONED_STATE = -13;
    
    public static final int STATEGROUP_LASTCONTROL_STATE = -17;

    public static final int STATEGROUP_IGNORED_CONTROL = -20;
    
    public static final int STATEGROUP_BECKWITH_REGULATOR_CONTROL_MODE = -32;
    
    public static final int STATEGROUP_EATON_REGULATOR_CONTROL_MODE = -31;


    //Default State of point
    public static final int DEFAULT_STATE = 0;

    // all StateGroup types
    public static final String GROUP_TYPE_STATUS = "Status";

    public static final String GROUP_TYPE_SYSTEM = "System";

    public static final String GROUP_TYPE_ANALOG = "Analog";

    /**
     * Constructor for StateGroupUtils.
     */
    private StateGroupUtils() {
        super();
    }

    public static GroupState buildAnalogStateGroup(GroupState gs) {
        
        String[] stateText = new String[] {
                "Normal",
                "Non-updated",
                "Rate of Change",
                "Limit Set 1",
                "Limit Set 2",
                "High Resonability",
                "Low Reasonability",
                "Low Limit 1",
                "Low Limit 2",
                "High Limit 1",
                "High Limit 2",
        };

        for (int i = 0; i < stateText.length; i++) {
            com.cannontech.database.data.state.State tempStateData = new com.cannontech.database.data.state.State();
            tempStateData.setState(new State(gs.getStateGroup().getStateGroupID(), Integer.valueOf(i), stateText[i],
                                             YukonColorPalette.getColor(i).getColorId(), YukonColorPalette.BLACK.getColorId()));
            gs.getStatesVector().add(tempStateData);
        }
        return gs;
    }
}