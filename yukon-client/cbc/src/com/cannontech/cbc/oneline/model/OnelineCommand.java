package com.cannontech.cbc.oneline.model;

public class OnelineCommand {

    private String name = null;
    private int id = -1;
    private boolean isDynamic = false;
    private String stateGrpName;
    private boolean updateColor = false;
    private boolean updateText = true;

    public OnelineCommand(String n, int i) {
        name = n;
        id = i;
    }

    public OnelineCommand(String n, int i, boolean b, String groupName) {
        this(n, i);
        isDynamic = b;
        stateGrpName = groupName;
    }

    public OnelineCommand() {
    }

    public OnelineCommand(String n, int i, boolean b, String groupName,
            boolean updClr) {
        this(n, i, b, groupName);
        updateColor = updClr;
    }

    public OnelineCommand(String n, int i, boolean b, String groupName,
            boolean updClr, boolean updTxt) {
        this(n, i, b, groupName, updClr);
        updateText = updTxt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDynamic() {
        return isDynamic;
    }

    public void setDynamic(boolean isDynamic) {
        this.isDynamic = isDynamic;
    }

    public String getStateGrpName() {
        return stateGrpName;
    }

    public void setStateGrpName(String stateGrpName) {
        this.stateGrpName = stateGrpName;
    }

    public boolean isUpdateColor() {
        return updateColor;
    }

    public boolean isUpdateText() {
        return updateText;
    }

}
