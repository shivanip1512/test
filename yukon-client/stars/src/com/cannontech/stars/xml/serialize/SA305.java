package com.cannontech.stars.xml.serialize;

public class SA305 {
    private int utility;
    private boolean hasUtility;
    private int group;
    private boolean hasGroup;
    private int division;
    private boolean hasDivision;
    private int substation;
    private boolean hasSubstation;
    private int rateFamily;
    private boolean hasRateFamily;
    private int rateMember;
    private boolean hasRateMember;
    private int rateHierarchy;
    private boolean hasRateHierarchy;

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
		return 16*this.rateFamily + this.rateMember;
	}
	
    public int getSubstation() {
        return this.substation;
    }

    public int getUtility() {
        return this.utility;
    }

    public boolean hasDivision() {
        return this.hasDivision;
    }

    public boolean hasGroup() {
        return this.hasGroup;
    }

    public boolean hasRateFamily() {
        return this.hasRateFamily;
    }

    public boolean hasRateHierarchy() {
        return this.hasRateHierarchy;
    }

    public boolean hasRateMember() {
        return this.hasRateMember;
    }

    public boolean hasSubstation() {
        return this.hasSubstation;
    }

    public boolean hasUtility() {
        return this.hasUtility;
    }

    public void setDivision(int division) {
        this.division = division;
        this.hasDivision = true;
    }

    public void setGroup(int group) {
        this.group = group;
        this.hasGroup = true;
    }

    public void setRateFamily(int rateFamily) {
        this.rateFamily = rateFamily;
        this.hasRateFamily = true;
    }

    public void setRateHierarchy(int rateHierarchy) {
        this.rateHierarchy = rateHierarchy;
        this.hasRateHierarchy = true;
    }

    public void setRateMember(int rateMember) {
        this.rateMember = rateMember;
        this.hasRateMember = true;
    }

    public void setSubstation(int substation) {
        this.substation = substation;
        this.hasSubstation = true;
    }

    public void setUtility(int utility) {
        this.utility = utility;
        this.hasUtility = true;
    }

}
