package com.cannontech.database.data.lite;

/**
 * This is a "Role Group"
 */
public class LiteYukonGroup extends LiteBase {
    private String groupName;
    private String groupDescription;

    public LiteYukonGroup() {
        initialize(0, null);
    }

    public LiteYukonGroup(int id) {
        initialize(id, null);
    }

    public LiteYukonGroup(int id, String name) {
        initialize(id, name);
    }

    private void initialize(int id, String groupName) {
        setLiteType(LiteTypes.YUKON_GROUP);
        setLiteID(id);
        setGroupName(groupName);
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getGroupID() {
        return getLiteID();
    }

    public void setGroupID(int groupID) {
        setLiteID(groupID);
    }

    @Override
    public String toString() {
        return getGroupName();
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String string) {
        groupDescription = string;
    }
}