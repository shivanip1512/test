package com.cannontech.analysis.data.device.capcontrol;

public class MaxDailyOpsData {

    private String region = null;
    private String subName = null;
    private String feeder = null;
    private String cbc = null;
    private String cap = null;
    private Integer currentWeek = null;
    private Integer prevWeek1 = null;
    private Integer prevWeek2 = null;
    private Integer prevWeek3 = null;
    private Integer prevWeek4 = null;
    private Integer prevWeek5 = null;
    private Integer prevWeek6 = null;
    
    public MaxDailyOpsData() {
        super();
    }
    
    public MaxDailyOpsData(String region_, String subName_, String feeder_, String cbc_, String cap_, Integer currentWeek_, Integer prevWeek1_, Integer prevWeek2_, Integer prevWeek3_, Integer prevWeek4_, Integer prevWeek5_, Integer prevWeek6_) {
        region = region_;
        subName = subName_;
        feeder = feeder_;
        cbc = cbc_;
        cap = cap_;
        currentWeek = currentWeek_;
        prevWeek1 = prevWeek1_;
        prevWeek2 = prevWeek2_;
        prevWeek3 = prevWeek3_;
        prevWeek4 = prevWeek4_;
        prevWeek5 = prevWeek5_;
        prevWeek6 = prevWeek6_;
    }
    
    public String getRegion() {
        return region;
    }
    
    public String getSubName() {
        return subName;
    }
    
    public String getFeeder() {
        return feeder;
    }
    
    public String getCBC() {
        return cbc;
    }
    
    public String getCap() {
        return cap;
    }
    
    public Integer getCurrentWeek() {
        return currentWeek;
    }
    
    public Integer getPrevWeek1() {
        return prevWeek1;
    }
    
    public Integer getPrevWeek2() {
        return prevWeek2;
    }
    
    public Integer getPrevWeek3() {
        return prevWeek3;
    }
    
    public Integer getPrevWeek4() {
        return prevWeek4;
    }
    
    public Integer getPrevWeek5() {
        return prevWeek5;
    }
    
    public Integer getPrevWeek6() {
        return prevWeek6;
    }
    
}
