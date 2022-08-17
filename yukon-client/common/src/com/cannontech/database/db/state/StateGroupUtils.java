package com.cannontech.database.db.state;

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
        com.cannontech.database.data.state.State tempStateData = new com.cannontech.database.data.state.State();

        tempStateData = new com.cannontech.database.data.state.State();

        tempStateData.setState(new com.cannontech.database.db.state.State(gs
                .getStateGroup().getStateGroupID(), new Integer(0), "Normal",
                new Integer(0), new Integer(
                        com.cannontech.common.gui.util.Colors.BLACK_ID)));

        gs.getStatesVector().add(tempStateData);

        tempStateData = new com.cannontech.database.data.state.State();

        tempStateData.setState(new com.cannontech.database.db.state.State(gs
                .getStateGroup().getStateGroupID(), new Integer(1),
                "Non-updated", new Integer(1), new Integer(
                        com.cannontech.common.gui.util.Colors.BLACK_ID)));

        gs.getStatesVector().add(tempStateData);

        tempStateData = new com.cannontech.database.data.state.State();

        tempStateData.setState(new com.cannontech.database.db.state.State(gs
                .getStateGroup().getStateGroupID(), new Integer(2),
                "Rate of Change", new Integer(2), new Integer(
                        com.cannontech.common.gui.util.Colors.BLACK_ID)));

        gs.getStatesVector().add(tempStateData);

        tempStateData = new com.cannontech.database.data.state.State();

        tempStateData.setState(new com.cannontech.database.db.state.State(gs
                .getStateGroup().getStateGroupID(), new Integer(3),
                "Limit Set 1", new Integer(3), new Integer(
                        com.cannontech.common.gui.util.Colors.BLACK_ID)));

        gs.getStatesVector().add(tempStateData);

        tempStateData = new com.cannontech.database.data.state.State();

        tempStateData.setState(new com.cannontech.database.db.state.State(gs
                .getStateGroup().getStateGroupID(), new Integer(4),
                "Limit Set 2", new Integer(4), new Integer(
                        com.cannontech.common.gui.util.Colors.BLACK_ID)));

        gs.getStatesVector().add(tempStateData);

        tempStateData = new com.cannontech.database.data.state.State();

        tempStateData.setState(new com.cannontech.database.db.state.State(gs
                .getStateGroup().getStateGroupID(), new Integer(5),
                "High Resonability", new Integer(5), new Integer(
                        com.cannontech.common.gui.util.Colors.BLACK_ID)));

        gs.getStatesVector().add(tempStateData);

        tempStateData = new com.cannontech.database.data.state.State();

        tempStateData.setState(new com.cannontech.database.db.state.State(gs
                .getStateGroup().getStateGroupID(), new Integer(6),
                "Low Reasonability", new Integer(6), new Integer(
                        com.cannontech.common.gui.util.Colors.BLACK_ID)));

        gs.getStatesVector().add(tempStateData);

        tempStateData = new com.cannontech.database.data.state.State();

        tempStateData.setState(new com.cannontech.database.db.state.State(gs
                .getStateGroup().getStateGroupID(), new Integer(7),
                "Low Limit 1", new Integer(7), new Integer(
                        com.cannontech.common.gui.util.Colors.BLACK_ID)));

        gs.getStatesVector().add(tempStateData);

        tempStateData = new com.cannontech.database.data.state.State();

        tempStateData.setState(new com.cannontech.database.db.state.State(gs
                .getStateGroup().getStateGroupID(), new Integer(8),
                "Low Limit 2", new Integer(8), new Integer(
                        com.cannontech.common.gui.util.Colors.BLACK_ID)));

        gs.getStatesVector().add(tempStateData);

        tempStateData = new com.cannontech.database.data.state.State();

        tempStateData.setState(new com.cannontech.database.db.state.State(gs
                .getStateGroup().getStateGroupID(), new Integer(9),
                "High Limit 1", new Integer(9), new Integer(
                        com.cannontech.common.gui.util.Colors.BLACK_ID)));

        gs.getStatesVector().add(tempStateData);

        tempStateData = new com.cannontech.database.data.state.State();

        tempStateData.setState(new com.cannontech.database.db.state.State(gs
                .getStateGroup().getStateGroupID(), new Integer(10),
                "High Limit 2", new Integer(10), new Integer(
                        com.cannontech.common.gui.util.Colors.BLACK_ID)));

        gs.getStatesVector().add(tempStateData);

        return gs;
    }

}
