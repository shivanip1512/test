package com.cannontech.web.picker;

import java.sql.SQLException;
import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonResultSet;

public class AvailableProgramRowMapper extends AbstractRowMapperWithBaseQuery<LMDto> {

    public AvailableProgramRowMapper() {

    }

    @Override
    public SqlFragmentSource getBaseQuery() {

        SqlStatementBuilder retVal = new SqlStatementBuilder();
        retVal.append("SELECT pao.PAOName, pao.PAObjectID FROM LMControlAreaProgram program, YukonPAObject pao WHERE program.LMPROGRAMDEVICEID = pao.PAObjectID");
        retVal.append("AND Type").in(PaoType.getDirectLMProgramTypes());
        return retVal;
    }

    @Override
    public LMDto mapRow(YukonResultSet rs) throws SQLException {
        return new LMDto(rs.getInt("PAObjectID"), rs.getString("PAOName"));
    }
}
