CREATE TABLE MCSchedule (
                           ScheduleID     INTEGER PRIMARY KEY,
                           ScheduleName   VARCHAR2(200) NOT NULL,
                           CommandFile    VARCHAR2(100) NOT NULL,
                           CurrentState   VARCHAR2(12) NOT NULL,
                           StartType      VARCHAR2(20) NOT NULL,
                           StopType       VARCHAR2(20) NOT NULL,
                           LastRunTime    DATE NOT NULL,
                           LastRunStatus  VARCHAR2(12) NOT NULL )
/
