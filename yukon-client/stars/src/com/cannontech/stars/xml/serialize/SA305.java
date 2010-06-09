package com.cannontech.stars.xml.serialize;

public class SA305 {
    private int utility;
    private int group;
    private int division;
    private int substation;
    private int rateFamily;
    private int rateMember;
    private int rateHierarchy;

    public SA305() {
        
    }

    public int getDivision() {
        return this.division;
    }

    public int getGroup() {
        return this.group;
    }

    public int getRateFamily() {
        return this.rateFamily;
    } 

    public int getRateHierarchy() {
        return this.rateHierarchy;
    }

    public int getRateMember() {
        return this.rateMember;
    }

	public int getRateRate() {
	    // There is Javascript code doing this also in hardwareAddressingInfo.tag.
        return 16 * rateFamily + rateMember;
	}

	public void setRateRate(int rate) {
        rateFamily = rate / 16;
        rateMember = rate % 16;
	}

    public int getSubstation() {
        return this.substation;
    }

    public int getUtility() {
        return this.utility;
    }

    public void setDivision(int division) {
        this.division = division;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public void setRateFamily(int rateFamily) {
        this.rateFamily = rateFamily;
    }

    public void setRateHierarchy(int rateHierarchy) {
        this.rateHierarchy = rateHierarchy;
    }

    public void setRateMember(int rateMember) {
        this.rateMember = rateMember;
    }

    public void setSubstation(int substation) {
        this.substation = substation;
    }

    public void setUtility(int utility) {
        this.utility = utility;
    }
}
