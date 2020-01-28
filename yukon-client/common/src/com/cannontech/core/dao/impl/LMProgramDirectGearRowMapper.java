package com.cannontech.core.dao.impl;

import java.sql.SQLException;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;

public class LMProgramDirectGearRowMapper extends AbstractRowMapperWithBaseQuery<LMProgramDirectGear> {

    private final static String tableName = "LMProgramDirectGear";

    @Override
    public SqlFragmentSource getBaseQuery() {
        SqlStatementBuilder retVal = new SqlStatementBuilder();
        retVal.append("SELECT * FROM").append(tableName);
        return retVal;
    }

    @Override
    public LMProgramDirectGear mapRow(YukonResultSet rs) throws SQLException {
        LMProgramDirectGear gear = new LMProgramDirectGear();

        gear.setChangeCondition(rs.getString("ChangeCondition"));
        gear.setChangeDuration(rs.getInt("ChangeDuration"));
        gear.setChangePriority(rs.getInt("ChangePriority"));
        gear.setChangeTriggerNumber(rs.getInt("ChangeTriggerNumber"));
        gear.setChangeTriggerOffset(rs.getDouble("ChangeTriggerOffset"));
        gear.setControlMethod(rs.getEnum("ControlMethod", GearControlMethod.class));
        gear.setCycleRefreshRate(rs.getInt("CycleRefreshRate"));
        gear.setGearName(rs.getString("GearName"));
        gear.setGearNumber(rs.getInt("GearNumber"));
        gear.setGroupSelectionMethod(rs.getString("GroupSelectionMethod"));
        gear.setKwReduction(rs.getDouble("kwReduction"));
        gear.setMethodOptionMax(rs.getInt("MethodOptionMax"));
        gear.setMethodOptionType(rs.getString("MethodOptionType"));
        gear.setMethodPeriod(rs.getInt("MethodPeriod"));
        gear.setMethodRate(rs.getInt("MethodRate"));
        gear.setMethodRateCount(rs.getInt("MethodRateCount"));
        gear.setMethodStopType(rs.getString("MethodStopType"));
        gear.setRampInInterval(rs.getInt("RampInInterval"));
        gear.setPercentReduction(rs.getInt("PercentReduction"));
        gear.setRampInPercent(rs.getInt("RampInPercent"));
        gear.setRampOutInterval(rs.getInt("RampOutInterval"));
        gear.setRampOutPercent(rs.getInt("RampOutPercent"));
        gear.setGearId(rs.getInt("GearId"));
        gear.setProgramId(rs.getInt("DeviceID"));
        gear.setFrontRampOption(rs.getString("FrontRampOption"));
        gear.setBackRampOption(rs.getString("BackRampOption"));
        gear.setStopCommandRepeat(rs.getInt("StopCommandRepeat"));

        return gear;
    }

}
