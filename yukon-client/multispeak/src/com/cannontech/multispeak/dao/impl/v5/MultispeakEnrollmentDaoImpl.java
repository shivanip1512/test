package com.cannontech.multispeak.dao.impl.v5;

import java.sql.SQLException;
import java.util.List;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.multispeak.dao.v5.MultispeakEnrollmentDao;
import com.cannontech.stars.dr.jms.message.DrJmsMessageType;
import com.cannontech.stars.dr.jms.message.EnrollmentJmsMessage;

public class MultispeakEnrollmentDaoImpl implements MultispeakEnrollmentDao {

    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;

    @Override
    public List<EnrollmentJmsMessage> getEnrollmentMessagesToSend() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT lmhg.InventoryID, lmhg.Relay, lmhg.GroupEnrollStart, lmhg.GroupEnrollStop, lmhg.ProgramId, lmhg.lmGroupId, lmhg.AccountID");
        sql.append("FROM LMHardwareControlGroup lmhg");
        sql.append("JOIN");
        sql.append("    (SELECT  InventoryID , Relay , MAX(GroupEnrollStart) AS GroupEnrollStart");
        sql.append("    FROM LMHardwareControlGroup GROUP BY inventoryid, Relay) inq");
        sql.append("ON lmhg.InventoryID = inq.InventoryID");
        sql.append("    AND lmhg.Relay = inq.Relay");
        sql.append("    AND lmhg.GroupEnrollStart = inq.GroupEnrollStart");
        sql.append("JOIN LMProgramWebPublishing lmwp ON lmwp.ProgramID = lmhg.ProgramId");
        sql.append("JOIN YukonPAObject ypo ON ypo.PAObjectID = lmwp.DeviceID");
        sql.append("JOIN yukonPAObject lgPao on lgPao.paobjectId = lmhg.lmGroupId");
        sql.append("WHERE lmhg.Type").eq_k(1);
        return yukonJdbcTemplate.query(sql, EnrollmentMessageRowMapper);
    }
    
    private final static YukonRowMapper<EnrollmentJmsMessage> EnrollmentMessageRowMapper =
            new YukonRowMapper<EnrollmentJmsMessage>() {
                @Override
                public EnrollmentJmsMessage mapRow(YukonResultSet rs) throws SQLException {
                    int inventoryId = rs.getInt("InventoryID");
                    int relay = rs.getInt("Relay");
                    int programId = rs.getInt("ProgramId");
                    int loadGroupId = rs.getInt("lmGroupId");
                    int accountId = rs.getInt("AccountID");
                    Instant enrollmentStartTime = rs.getInstant("GroupEnrollStart");
                    Instant enrollmentStopTime = rs.getInstant("GroupEnrollStop");
                    EnrollmentJmsMessage enrollmentMessage = new EnrollmentJmsMessage(inventoryId, accountId, loadGroupId, relay,
                             programId, enrollmentStartTime, enrollmentStopTime);
                    if(enrollmentStopTime != null) {
                        enrollmentMessage.setMessageType(DrJmsMessageType.UNENROLLMENT);
                    } else {
                        enrollmentMessage.setMessageType(DrJmsMessageType.ENROLLMENT);
                    }
                    return enrollmentMessage;
                }
            };
}
