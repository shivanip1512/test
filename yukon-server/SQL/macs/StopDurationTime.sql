CREATE TABLE StopDurationTime (
                                 ScheduleID INTEGER REFERENCES MCSchedule NOT NULL,
                                 DurationTime   VARCHAR2(8) NOT NULL )
/

