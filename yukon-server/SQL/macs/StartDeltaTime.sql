CREATE TABLE StartDeltaTime (
                                 ScheduleID INTEGER REFERENCES MCSchedule NOT NULL,
                                 DeltaTime   VARCHAR2(8) NOT NULL )
/
