package com.cannontech.cbc.oneline;

import com.cannontech.database.data.lite.LiteYukonUser;

public class OneLineParams {

    private int height;
    private int width;
    private boolean isSingleFeeder = false;
    private String redirectURL;
    private LiteYukonUser user;

    public OneLineParams(int h, int w, boolean singleFdr) {
        height = h;
        width = w;
        isSingleFeeder = singleFdr;
    }

    public double getFeederHorzLineLength() {
        if (isSingleFeeder) {
            double feederHorzLineStop = getFeederHorzLineStop();
            double feederHorzLineStart = getFeederHorzLineStart();
            return feederHorzLineStop - feederHorzLineStart;
        }
        return width / 1.333;
    }

    public double getFeederHorzLineStart() {
        return (width - (width / 1.333)) / 2;
    }

    public double getFeederHorzLineStop() {
        if (isSingleFeeder) {
            return getHalfAcross();
        }
        return (width - getFeederHorzLineLength()) / 2 + getFeederHorzLineLength();
    }

    public double getHalfAcross() {
        return width / 2;
    }

    public double getLabelTextHorzOffset() {
        return width / 51.2;
    }

    public double getLabelTextVertOffset() {
        return height / 53.333;
    }

    public double getSubLineLength() {
        return height / 5.333;
    }

    public double getSubLineLevel() {
        return height / 26.666 + height / 5.333;
    }

    public double getSubLineStart() {
        return height / 26.666;
    }

    public double getValueTextHorzOffset() {
        return width / 51.2 + width / 14.63;
    }

    public double getFeederVertLineLength() {
        return height / 1.013 - getSubLineStart() - getSubLineLevel();
    }

    public double getFeederVertLineStart() {
        return getSubLineLevel();
    }

    public double getFeederVertLineStop() {
        return getFeederVertLineStart() + getFeederVertLineLength();
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    

    public String getRedirectURL() {
        return redirectURL;
    }

    public void setRedirectURL(String redirectURL) {
        if (redirectURL != null)
            this.redirectURL = redirectURL;
    }

    public void setUser(LiteYukonUser u) {
        user = u;
    }

    public LiteYukonUser getUser() {
        return user;
    }

}
