CREATE TABLE StopAbsoluteTime (
                                 ScheduleID INTEGER REFERENCES MCSchedule NOT NULL,
                                 StopTime VARCHAR2(8) NOT NULL )
/
