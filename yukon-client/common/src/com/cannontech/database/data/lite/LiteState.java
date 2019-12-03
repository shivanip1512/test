package com.cannontech.database.data.lite;

public class LiteState extends LiteBase {

    private String stateText;
    private int fgColor;
    private int bgColor;
    private int imageID;

    public LiteState(int rawState, String stateText, int fgColor, int bgColor, int imageId) {
        super();
        setLiteID(rawState);
        this.stateText = stateText;
        setLiteType(LiteTypes.STATE);
        this.fgColor = fgColor;
        this.bgColor = bgColor;
        this.imageID = imageId;
    }

    public int getStateRawState() {
        return getLiteID();
    }
    
    public void setStateRawState(int newValue) {
        setLiteID(newValue);
    }

    public String getStateText() {
        return stateText;
    }

    public void setStateText(String stateText) {
        this.stateText = stateText;
    }

    public int getFgColor() {
        return fgColor;
    }

    public void setFgColor(int fgColor) {
        this.fgColor = fgColor;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    public String toString() {
        return stateText;
    }

    /**
     * Returns true in case the raw state is either 0 or 1 in case of load group point
     */
    public boolean isValidRawState() {
        return (getStateRawState() == 0 || getStateRawState() == 1);
    }

}