package com.cannontech.stars.dr.account.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.database.RowAndFieldMapper;
import com.cannontech.database.SqlUtils;
import com.cannontech.stars.dr.account.model.CallReport;

public class CallReportRowAndFieldMapper implements RowAndFieldMapper<CallReport> {

    public Number getPrimaryKey(CallReport callReport) {
        return callReport.getCallId();
    }
    
    public void setPrimaryKey(CallReport callReport, int value) {
    	callReport.setCallId(value);
    }
    
    public void extractValues(MapSqlParameterSource p, CallReport callReport) {
        p.addValue("CallNumber", callReport.getCallNumber());
        p.addValue("CallTypeId", callReport.getCallTypeId());
        p.addValue("DateTaken", callReport.getDateTaken());
        p.addValue("TakenBy", SqlUtils.convertStringToDbValue(callReport.getTakenBy()));
        p.addValue("Description", SqlUtils.convertStringToDbValue(callReport.getDescription()));
        p.addValue("AccountId", callReport.getAccountId());
    }
    
    public CallReport mapRow(ResultSet rs, int rowNum) throws SQLException {
    	CallReport callReport = new CallReport();
    	callReport.setCallId(rs.getInt("CallId"));
    	callReport.setCallNumber(rs.getString("CallNumber"));
    	callReport.setCallTypeId(rs.getInt("CallTypeId"));
    	callReport.setDateTaken(rs.getTimestamp("DateTaken"));
    	callReport.setTakenBy(SqlUtils.convertDbValueToString(rs, "TakenBy"));
    	callReport.setDescription(SqlUtils.convertDbValueToString(rs, "Description"));
    	callReport.setAccountId(rs.getInt("AccountId"));
        return callReport;
    }
}
