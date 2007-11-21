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
        "percentclose",
    };
    public static final String CONSTRAINT_COLUMNS[] = { "strategyId", "startTimeSeconds" };
    public static final String TABLE_NAME = "CCStrategyTimeOfDay";
    private Integer strategyId = -1;
    private Integer startTimeSeconds = 0;
    private Integer percentClose = 0;
    
    public Integer getStrategyId() {
        return strategyId;
    }
    
    public void setStrategyId(Integer strategyId) {
        this.strategyId = strategyId;
    }
    
    public void setStartTimeSeconds(Integer startTimeSeconds) {
        this.startTimeSeconds = startTimeSeconds;
    }
    
    public Integer getStartTimeSeconds() {
        return startTimeSeconds;
    }
    
    public void setPercentClose(Integer percentClose) {
        this.percentClose = percentClose;
    }
    
    public Integer getPercentClose() {
        return percentClose;
    }
    
    public Integer getPercentOpen() {
        return 100 - percentClose;
    }
    
    public void setPercentOpen() {
    }
    
    /**
     * add method.
     */
    public void add() throws java.sql.SQLException {
        Object[] addValues = {
            getStrategyId(), getStartTimeSeconds(), getPercentClose()
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
        Object constraintValues[] = { getStrategyId(), getStartTimeSeconds() };
        Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    
        if( results.length == SETTER_COLUMNS.length ) {
            setPercentClose( (Integer) results[0] );
        } else {
            throw new IncorrectResultSizeDataAccessException(SETTER_COLUMNS.length, results.length);
        }
    }

    /**
     * update method.
     */
    public void update() throws java.sql.SQLException {
        Object setValues[]= { getPercentClose() };
        Object constraintValues[] = { getStrategyId(), getStartTimeSeconds() };
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
