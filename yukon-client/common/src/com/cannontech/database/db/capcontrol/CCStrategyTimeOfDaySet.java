package com.cannontech.database.db.capcontrol;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CCStrategyTimeOfDaySet {
    
    private Integer strategyId = -1;
    private CCStrategyTimeOfDay hourZero = new CCStrategyTimeOfDay();
    private CCStrategyTimeOfDay hourOne = new CCStrategyTimeOfDay();
    private CCStrategyTimeOfDay hourTwo = new CCStrategyTimeOfDay();
    private CCStrategyTimeOfDay hourThree = new CCStrategyTimeOfDay();
    private CCStrategyTimeOfDay hourFour = new CCStrategyTimeOfDay();
    private CCStrategyTimeOfDay hourFive = new CCStrategyTimeOfDay();
    private CCStrategyTimeOfDay hourSix = new CCStrategyTimeOfDay();
    private CCStrategyTimeOfDay hourSeven = new CCStrategyTimeOfDay();
    private CCStrategyTimeOfDay hourEight = new CCStrategyTimeOfDay();
    private CCStrategyTimeOfDay hourNine = new CCStrategyTimeOfDay();
    private CCStrategyTimeOfDay hourTen = new CCStrategyTimeOfDay();
    private CCStrategyTimeOfDay hourEleven = new CCStrategyTimeOfDay();
    private CCStrategyTimeOfDay hourTwelve = new CCStrategyTimeOfDay();
    private CCStrategyTimeOfDay hourThirteen = new CCStrategyTimeOfDay();
    private CCStrategyTimeOfDay hourFourteen = new CCStrategyTimeOfDay();
    private CCStrategyTimeOfDay hourFifteen = new CCStrategyTimeOfDay();
    private CCStrategyTimeOfDay hourSixteen = new CCStrategyTimeOfDay();
    private CCStrategyTimeOfDay hourSeventeen = new CCStrategyTimeOfDay();
    private CCStrategyTimeOfDay hourEighteen = new CCStrategyTimeOfDay();
    private CCStrategyTimeOfDay hourNineteen = new CCStrategyTimeOfDay();
    private CCStrategyTimeOfDay hourTwenty = new CCStrategyTimeOfDay();
    private CCStrategyTimeOfDay hourTwentyOne = new CCStrategyTimeOfDay();
    private CCStrategyTimeOfDay hourTwentyTwo = new CCStrategyTimeOfDay();
    private CCStrategyTimeOfDay hourTwentyThree = new CCStrategyTimeOfDay();
    private CCStrategyTimeOfDay[] array = {
        hourZero, hourOne, hourTwo, hourThree, hourFour, hourFive, hourSix, 
        hourSeven, hourEight, hourNine, hourTen, hourEleven, hourTwelve, 
        hourThirteen, hourFourteen, hourFifteen, hourSixteen, hourSeventeen, 
        hourEighteen, hourNineteen, hourTwenty, hourTwentyOne, hourTwentyTwo, 
        hourTwentyThree
    };
    private List<CCStrategyTimeOfDay> list = new ArrayList<CCStrategyTimeOfDay>(Arrays.asList(array)); 

    public CCStrategyTimeOfDaySet() {
        super();
        setTimes();
    }
    
    public CCStrategyTimeOfDaySet(Integer strategyId) {
        super();
        this.strategyId = strategyId;
        setStrategyIds(strategyId);
        setTimes();
    }
    
    public List<CCStrategyTimeOfDay> getList(){
        return list;
    }
    
    public void setStrategyIds(Integer strategyId) {
        for(CCStrategyTimeOfDay tod : list) {
            tod.setStrategyId(strategyId);
        }
    }
    
    public void delete(Connection conn) throws java.sql.SQLException {
        for(CCStrategyTimeOfDay tod : list) {
            tod.setDbConnection(conn);
            tod.delete();
        }
    }
    
    public void retrieve(Connection conn) throws java.sql.SQLException {
        for(CCStrategyTimeOfDay tod : list) {
            tod.setDbConnection(conn);
            tod.retrieve();
        }
    }
    
    public void add(Connection conn) throws java.sql.SQLException {
        for(CCStrategyTimeOfDay tod : list) {
            tod.setDbConnection(conn);
            tod.add();
        }
    }
    
    public void update(Connection conn) throws java.sql.SQLException {
        for(CCStrategyTimeOfDay tod : list) {
            tod.setDbConnection(conn);
            tod.update();
        }
    }
    
    public void setTimes() {
        getHourZero().setStartTimeSeconds(0);
        getHourOne().setStartTimeSeconds(3600);
        getHourTwo().setStartTimeSeconds(7200);
        getHourThree().setStartTimeSeconds(10800);
        getHourFour().setStartTimeSeconds(14400);
        getHourFive().setStartTimeSeconds(18000);
        getHourSix().setStartTimeSeconds(21600);
        getHourSeven().setStartTimeSeconds(25200);
        getHourEight().setStartTimeSeconds(28800);
        getHourNine().setStartTimeSeconds(32400);
        getHourTen().setStartTimeSeconds(36000);
        getHourEleven().setStartTimeSeconds(39600);
        getHourTwelve().setStartTimeSeconds(43200);
        getHourThirteen().setStartTimeSeconds(46800);
        getHourFourteen().setStartTimeSeconds(50400);
        getHourFifteen().setStartTimeSeconds(54000);
        getHourSixteen().setStartTimeSeconds(57600);
        getHourSeventeen().setStartTimeSeconds(61200);
        getHourEighteen().setStartTimeSeconds(64800);
        getHourNineteen().setStartTimeSeconds(68400);
        getHourTwenty().setStartTimeSeconds(72000);
        getHourTwentyOne().setStartTimeSeconds(75600);
        getHourTwentyTwo().setStartTimeSeconds(79200);
        getHourTwentyThree().setStartTimeSeconds(82800);
    }
    
    public Integer getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(Integer strategyId) {
        this.strategyId = strategyId;
        setStrategyIds(strategyId);
    }

    public CCStrategyTimeOfDay getHourZero() {
        return hourZero;
    }

    public void setHourZero(CCStrategyTimeOfDay hourZero) {
        this.hourZero = hourZero;
    }

    public CCStrategyTimeOfDay getHourOne() {
        return hourOne;
    }

    public void setHourOne(CCStrategyTimeOfDay hourOne) {
        this.hourOne = hourOne;
    }

    public CCStrategyTimeOfDay getHourTwo() {
        return hourTwo;
    }

    public void setHourTwo(CCStrategyTimeOfDay hourTwo) {
        this.hourTwo = hourTwo;
    }

    public CCStrategyTimeOfDay getHourThree() {
        return hourThree;
    }
    
    public void setHourThree(CCStrategyTimeOfDay hourThree) {
        this.hourThree = hourThree;
    }

    public CCStrategyTimeOfDay getHourFour() {
        return hourFour;
    }

    public void setHourFour(CCStrategyTimeOfDay hourFour) {
        this.hourFour = hourFour;
    }

    public CCStrategyTimeOfDay getHourFive() {
        return hourFive;
    }

    public void setHourFive(CCStrategyTimeOfDay hourFive) {
        this.hourFive = hourFive;
    }

    public CCStrategyTimeOfDay getHourSix() {
        return hourSix;
    }

    public void setHourSix(CCStrategyTimeOfDay hourSix) {
        this.hourSix = hourSix;
    }

    public CCStrategyTimeOfDay getHourSeven() {
        return hourSeven;
    }

    public void setHourSeven(CCStrategyTimeOfDay hourSeven) {
        this.hourSeven = hourSeven;
    }

    public CCStrategyTimeOfDay getHourEight() {
        return hourEight;
    }

    public void setHourEight(CCStrategyTimeOfDay hourEight) {
        this.hourEight = hourEight;
    }

    public CCStrategyTimeOfDay getHourNine() {
        return hourNine;
    }

    public void setHourNine(CCStrategyTimeOfDay hourNine) {
        this.hourNine = hourNine;
    }

    public CCStrategyTimeOfDay getHourTen() {
        return hourTen;
    }

    public void setHourTen(CCStrategyTimeOfDay hourTen) {
        this.hourTen = hourTen;
    }

    public CCStrategyTimeOfDay getHourEleven() {
        return hourEleven;
    }

    public void setHourEleven(CCStrategyTimeOfDay hourEleven) {
        this.hourEleven = hourEleven;
    }

    public CCStrategyTimeOfDay getHourTwelve() {
        return hourTwelve;
    }

    public void setHourTwelve(CCStrategyTimeOfDay hourTwelve) {
        this.hourTwelve = hourTwelve;
    }

    public CCStrategyTimeOfDay getHourThirteen() {
        return hourThirteen;
    }

    public void setHourThirteen(CCStrategyTimeOfDay hourThirteen) {
        this.hourThirteen = hourThirteen;
    }

    public CCStrategyTimeOfDay getHourFourteen() {
        return hourFourteen;
    }

    public void setHourFourteen(CCStrategyTimeOfDay hourFourteen) {
        this.hourFourteen = hourFourteen;
    }

    public CCStrategyTimeOfDay getHourFifteen() {
        return hourFifteen;
    }

    public void setHourFifteen(CCStrategyTimeOfDay hourFifteen) {
        this.hourFifteen = hourFifteen;
    }

    public CCStrategyTimeOfDay getHourSixteen() {
        return hourSixteen;
    }

    public void setHourSixteen(CCStrategyTimeOfDay hourSixteen) {
        this.hourSixteen = hourSixteen;
    }

    public CCStrategyTimeOfDay getHourSeventeen() {
        return hourSeventeen;
    }

    public void setHourSeventeen(CCStrategyTimeOfDay hourSeventeen) {
        this.hourSeventeen = hourSeventeen;
    }

    public CCStrategyTimeOfDay getHourEighteen() {
        return hourEighteen;
    }

    public void setHourEighteen(CCStrategyTimeOfDay hourEighteen) {
        this.hourEighteen = hourEighteen;
    }

    public CCStrategyTimeOfDay getHourNineteen() {
        return hourNineteen;
    }

    public void setHourNineteen(CCStrategyTimeOfDay hourNineteen) {
        this.hourNineteen = hourNineteen;
    }

    public CCStrategyTimeOfDay getHourTwenty() {
        return hourTwenty;
    }

    public void setHourTwenty(CCStrategyTimeOfDay hourTwenty) {
        this.hourTwenty = hourTwenty;
    }

    public CCStrategyTimeOfDay getHourTwentyOne() {
        return hourTwentyOne;
    }

    public void setHourTwentyOne(CCStrategyTimeOfDay hourTwentyOne) {
        this.hourTwentyOne = hourTwentyOne;
    }

    public CCStrategyTimeOfDay getHourTwentyTwo() {
        return hourTwentyTwo;
    }

    public void setHourTwentyTwo(CCStrategyTimeOfDay hourTwentyTwo) {
        this.hourTwentyTwo = hourTwentyTwo;
    }

    public CCStrategyTimeOfDay getHourTwentyThree() {
        return hourTwentyThree;
    }

    public void setHourTwentyThree(CCStrategyTimeOfDay hourTwentyThree) {
        this.hourTwentyThree = hourTwentyThree;
    }
    
}
