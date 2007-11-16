package com.cannontech.database.db.capcontrol;

import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.dispatch.message.DBChangeMsg;

public class CCStrategyTimeOfDay extends DBPersistent implements com.cannontech.database.db.CTIDbChange {

    public CCStrategyTimeOfDay() {
        super();
    }
    
    public CCStrategyTimeOfDay(Integer strategyId) {
        super();
        this.strategyId = strategyId;
    }

    public static final String SETTER_COLUMNS[] = { 
        "HourZero",
        "HourOne",
        "HourTwo",
        "HourThree",
        "HourFour",
        "HourFive",
        "HourSix",
        "HourSeven",
        "HourEight",
        "HourNine",
        "HourTen",
        "HourEleven",
        "HourTwelve",
        "HourThirteen",
        "HourFourteen",
        "HourFifteen",
        "HourSixteen",
        "HourSeventeen",
        "HourEighteen",
        "HourNineteen",
        "HourTwenty",
        "HourTwentyone",
        "HourTwentytwo",
        "HourTwentyThree"
    };
    public static final String CONSTRAINT_COLUMNS[] = { "StrategyID" };
    public static final String TABLE_NAME = "CCStrategyTimeOfDay";
    private Integer strategyId = -1;
    private Integer hourZero = 0;
    private Integer hourOne = 0;
    private Integer hourTwo = 0;
    private Integer hourThree = 0;
    private Integer hourFour = 0;
    private Integer hourFive = 0;
    private Integer hourSix = 0;
    private Integer hourSeven = 0;
    private Integer hourEight = 0;
    private Integer hourNine = 0;
    private Integer hourTen = 0;
    private Integer hourEleven = 0;
    private Integer hourTwelve = 0;
    private Integer hourThirteen = 0;
    private Integer hourFourteen = 0;
    private Integer hourFifteen = 0;
    private Integer hourSixteen = 0;
    private Integer hourSeventeen = 0;
    private Integer hourEighteen = 0;
    private Integer hourNineteen = 0;
    private Integer hourTwenty = 0;
    private Integer hourTwentyOne = 0;
    private Integer hourTwentyTwo = 0;
    private Integer hourTwentyThree = 0;
    
    public Integer getStrategyId() {
        return strategyId;
    }
    
    public void setStrategyId(Integer strategyId) {
        this.strategyId = strategyId;
    }
    
    public Integer getHourZero() {
        return hourZero;
    }
    
    public void setHourZero(Integer hourZero) {
        this.hourZero = hourZero;
    }
    
    public Integer getHourZeroOpen() {
        return 100 - hourZero;
    }
    
    public void setHourZeroOpen(Integer hourZero) {
    }
    
    public Integer getHourOne() {
        return hourOne;
    }
    
    public void setHourOne(Integer hourOne) {
        this.hourOne = hourOne;
    }
    
    public Integer getHourOneOpen() {
        return 100 - hourOne;
    }
    
    public void setHourOneOpen(Integer hourOne) {
    }
    
    public Integer getHourTwo() {
        return hourTwo;
    }
    
    public void setHourTwo(Integer hourTwo) {
        this.hourTwo = hourTwo;
    }
    
    public Integer getHourTwoOpen() {
        return 100 - hourTwo;
    }
    
    public void setHourTwoOpen(Integer hourTwo) {
    }

    public Integer getHourThree() {
        return hourThree;
    }
    
    public void setHourThree(Integer hourThree) {
        this.hourThree = hourThree;
    }
    
    public Integer getHourThreeOpen() {
        return 100 - hourThree;
    }
    
    public void setHourThreeOpen(Integer hourThree) {
    }
    
    public Integer getHourFour() {
        return hourFour;
    }
    
    public void setHourFour(Integer hourFour) {
        this.hourFour = hourFour;
    }
    
    public Integer getHourFourOpen() {
        return 100 - hourFour;
    }
    
    public void setHourFourOpen(Integer hourFour) {
    }
    
    public Integer getHourFive() {
        return hourFive;
    }
    
    public void setHourFive(Integer hourFive) {
        this.hourFive = hourFive;
    }
    
    public Integer getHourFiveOpen() {
        return 100 - hourFive;
    }
    
    public void setHourFiveOpen(Integer hourFive) {
    }
    
    public Integer getHourSix() {
        return hourSix;
    }
    
    public void setHourSix(Integer hourSix) {
        this.hourSix = hourSix;
    }
    
    public Integer getHourSixOpen() {
        return 100 - hourSix;
    }
    
    public void setHourSixOpen(Integer hourSix) {
    }
    
    public Integer getHourSeven() {
        return hourSeven;
    }
    
    public void setHourSeven(Integer hourSeven) {
        this.hourSeven = hourSeven;
    }
    
    public Integer getHourSevenOpen() {
        return 100 - hourSeven;
    }
    
    public void setHourSevenOpen(Integer hourSeven) {
    }
    
    public Integer getHourEight() {
        return hourEight;
    }
    
    public void setHourEight(Integer hourEight) {
        this.hourEight = hourEight;
    }
    
    public Integer getHourEightOpen() {
        return 100 - hourEight;
    }
    
    public void setHourEightOpen(Integer hourEight) {
    }
    
    public Integer getHourNine() {
        return hourNine;
    }
    
    public void setHourNine(Integer hourNine) {
        this.hourNine = hourNine;
    }
    
    public Integer getHourNineOpen() {
        return 100 - hourNine;
    }
    
    public void setHourNineOpen(Integer hourNine) {
    }
    
    public Integer getHourTen() {
        return hourTen;
    }
    
    public void setHourTen(Integer hourTen) {
        this.hourTen = hourTen;
    }
    
    public Integer getHourTenOpen() {
        return 100 - hourTen;
    }
    
    public void setHourTenOpen(Integer hourTen) {
    }
    
    public Integer getHourEleven() {
        return hourEleven;
    }
    
    public void setHourEleven(Integer hourEleven) {
        this.hourEleven = hourEleven;
    }
    
    public Integer getHourElevenOpen() {
        return 100 - hourEleven;
    }
    
    public void setHourElevenOpen(Integer hourEleven) {
    }
    
    public Integer getHourTwelve() {
        return hourTwelve;
    }
    
    public void setHourTwelve(Integer hourTwelve) {
        this.hourTwelve = hourTwelve;
    }
    
    public Integer getHourTwelveOpen() {
        return 100 - hourTwelve;
    }
    
    public void setHourTwelveOpen(Integer hourTwelve) {
    }
    
    public Integer getHourThirteen() {
        return hourThirteen;
    }
    
    public void setHourThirteen(Integer hourThirteen) {
        this.hourThirteen = hourThirteen;
    }
    
    public Integer getHourThirteenOpen() {
        return 100 - hourThirteen;
    }
    
    public void setHourThirteenOpen(Integer hourThirteen) {
    }
    
    public Integer getHourFourteen() {
        return hourFourteen;
    }
    
    public void setHourFourteen(Integer hourFourteen) {
        this.hourFourteen = hourFourteen;
    }
    
    public Integer getHourFourteenOpen() {
        return 100 - hourFourteen;
    }
    
    public void setHourFourteenOpen(Integer hourFourteen) {
    }
    
    public Integer getHourFifteen() {
        return hourFifteen;
    }
    
    public void setHourFifteen(Integer hourFifteen) {
        this.hourFifteen = hourFifteen;
    }
    
    public Integer getHourFifteenOpen() {
        return 100 - hourFifteen;
    }
    
    public void setHourFifteenOpen(Integer hourFifteen) {
    }
    
    public Integer getHourSixteen() {
        return hourSixteen;
    }
    
    public void setHourSixteen(Integer hourSixteen) {
        this.hourSixteen = hourSixteen;
    }
    
    public Integer getHourSixteenOpen() {
        return 100 - hourSixteen;
    }
    
    public void setHourSixteenOpen(Integer hourSixteen) {
    }
    
    public Integer getHourSeventeen() {
        return hourSeventeen;
    }
    
    public void setHourSeventeen(Integer hourSeventeen) {
        this.hourSeventeen = hourSeventeen;
    }
    
    public Integer getHourSeventeenOpen() {
        return 100 - hourSeventeen;
    }
    
    public void setHourSeventeenOpen(Integer hourSeventeen) {
    }
    
    public Integer getHourEighteen() {
        return hourEighteen;
    }
    
    public void setHourEighteen(Integer hourEighteen) {
        this.hourEighteen = hourEighteen;
    }
    
    public Integer getHourEighteenOpen() {
        return 100 - hourEighteen;
    }
    
    public void setHourEighteenOpen(Integer hourEighteen) {
    }
    
    public Integer getHourNineteen() {
        return hourNineteen;
    }
    
    public void setHourNineteen(Integer hourNineteen) {
        this.hourNineteen = hourNineteen;
    }
    
    public Integer getHourNineteenOpen() {
        return 100 - hourNineteen;
    }
    
    public void setHourNineteenOpen(Integer hourNineteen) {
    }
    
    public Integer getHourTwenty() {
        return hourTwenty;
    }
    
    public void setHourTwenty(Integer hourTwenty) {
        this.hourTwenty = hourTwenty;
    }
    
    public Integer getHourTwentyOpen() {
        return 100 - hourTwenty;
    }
    
    public void setHourTwentyOpen(Integer hourTwenty) {
    }
    
    public Integer getHourTwentyOne() {
        return hourTwentyOne;
    }
    
    public void setHourTwentyOne(Integer hourTwentyOne) {
        this.hourTwentyOne = hourTwentyOne;
    }
    
    public Integer getHourTwentyOneOpen() {
        return 100 - hourTwentyOne;
    }
    
    public void setHourTwentyOneOpen(Integer hourTwentyOne) {
    }
    
    public Integer getHourTwentyTwo() {
        return hourTwentyTwo;
    }
    
    public void setHourTwentyTwo(Integer hourTwentyTwo) {
        this.hourTwentyTwo = hourTwentyTwo;
    }
    
    public Integer getHourTwentyTwoOpen() {
        return 100 - hourTwentyTwo;
    }
    
    public void setHourTwentyTwoOpen(Integer hourTwentyTwo) {
    }
    
    public Integer getHourTwentyThree() {
        return hourTwentyThree;
    }
    
    public void setHourTwentyThree(Integer hourTwentyThree) {
        this.hourTwentyThree = hourTwentyThree;
    }
    
    public Integer getHourTwentyThreeOpen() {
        return 100 - hourTwentyThree;
    }
    
    public void setHourTwentyThreeOpen(Integer hourTwentyThree) {
    }
    
    /**
     * add method.
     */
    public void add() throws java.sql.SQLException {
        Object[] addValues = {
            getStrategyId(), getHourZero(), getHourOne(), getHourTwo(), getHourThree(),
            getHourFour(), getHourFive(), getHourSix(), getHourSeven(),
            getHourEight(), getHourNine(), getHourTen(), getHourEleven(),
            getHourTwelve(), getHourThirteen(), getHourFourteen(),
            getHourFifteen(), getHourSixteen(), getHourSeventeen(), 
            getHourEighteen(), getHourNineteen(), getHourTwenty(),
            getHourTwentyOne(), getHourTwentyTwo(), getHourTwentyThree()
        };
        add( TABLE_NAME, addValues );
    }
    
    /**
     * delete method.
     */
    public void delete() throws java.sql.SQLException {
        delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getStrategyId() );   
    }

    /**
     * retrieve method.
     */
    public void retrieve() throws java.sql.SQLException {
        Object constraintValues[] = { getStrategyId() };
        Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    
        if( results.length == SETTER_COLUMNS.length ) {
            setHourZero( (Integer) results[0] );
            setHourOne( (Integer) results[1] );
            setHourTwo( (Integer) results[2] );
            setHourThree( (Integer) results[3] );
            setHourFour( (Integer) results[4] );
            setHourFive( (Integer) results[5] );
            setHourSix( (Integer) results[6] );
            setHourSeven( (Integer) results[7] );
            setHourEight( (Integer) results[8] );
            setHourNine( (Integer) results[9] );
            setHourTen( (Integer) results[10] );
            setHourEleven( (Integer) results[11] );
            setHourTwelve( (Integer) results[12] );
            setHourThirteen( (Integer) results[13] );
            setHourFourteen( (Integer) results[14] );
            setHourFifteen( (Integer) results[15] );
            setHourSixteen( (Integer) results[16] );
            setHourSeventeen( (Integer) results[17] );
            setHourEighteen( (Integer) results[18] );
            setHourNineteen( (Integer) results[19] );
            setHourTwenty( (Integer) results[20] );
            setHourTwentyOne( (Integer) results[21] );
            setHourTwentyTwo( (Integer) results[22] );
            setHourTwentyThree( (Integer) results[23] );
        } else {
            throw new IncorrectResultSizeDataAccessException(SETTER_COLUMNS.length, results.length);
        }
    }

    /**
     * update method.
     */
    public void update() throws java.sql.SQLException {
        Object setValues[]= { 
                getHourZero(), getHourOne(), getHourTwo(), getHourThree(),
                getHourFour(), getHourFive(), getHourSix(), getHourSeven(),
                getHourEight(), getHourNine(), getHourTen(), getHourEleven(),
                getHourTwelve(), getHourThirteen(), getHourFourteen(),
                getHourFifteen(), getHourSixteen(), getHourSeventeen(), 
                getHourEighteen(), getHourNineteen(), getHourTwenty(),
                getHourTwentyOne(), getHourTwentyTwo(), getHourTwentyThree()
        };
        Object constraintValues[] = { getStrategyId() };
        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public DBChangeMsg[] getDBChangeMsgs( int typeOfChange ) {
        DBChangeMsg[] dbChange = new DBChangeMsg[1];

        //add the basic change method
        dbChange[0] = new DBChangeMsg(
                        getStrategyId().intValue(),
                        DBChangeMsg.CHANGE_CBC_STRATEGY_DB,
                        DBChangeMsg.CAT_CBC_STRATEGY,
                        typeOfChange );

        return dbChange;
    }
}
