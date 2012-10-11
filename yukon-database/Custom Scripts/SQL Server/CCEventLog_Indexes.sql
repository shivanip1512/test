/*
 * Please see YUK-11531 for more information regarding this custom script an.
 *
 * These indexes are intended to improve performance of Cap Control reports.
 *
 * The new indexes provide better coverage for some of the more intensive queries 
 * used by the Cap Control reports and should significantly speed up the running
 * time of these reports.
  */

CREATE NONCLUSTERED INDEX INDX_CCEventLog_EventType_Time_Action ON CCEventLog
(
    EventType ASC,
    DateTime ASC,
    ActionId ASC
)
INCLUDE 
(   
    PointID,
    SubID,
    FeederID
);
GO

CREATE NONCLUSTERED INDEX INDX_CCEventLog_EventType_Action ON CCEventLog
(
    EventType ASC,
    ActionId ASC
)
INCLUDE 
(   
    PointID,
    Text
);
GO

CREATE NONCLUSTERED INDEX INDX_CCEventLog_EventType ON CCEventLog
(
    EventType ASC
)
INCLUDE 
(
    LogID,
    PointID,
    DateTime,
    SubID,
    FeederID,
    SeqID,
    Value,
    Text
);
GO

ALTER TABLE CCEventLog
DROP CONSTRAINT PK_CCEventLog;
GO

ALTER TABLE CCEventLog
ADD CONSTRAINT PK_CCEventLog PRIMARY KEY NONCLUSTERED (LogId);
GO

CREATE CLUSTERED INDEX INDX_CCEventLog_DateTime_Clustered ON CCEventLog
(
    DateTime ASC
);
GO


