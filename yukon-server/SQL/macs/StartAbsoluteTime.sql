CREATE TABLE StartAbsoluteTime (
                                ScheduleID INTEGER REFERENCES MCSchedule NOT NULL,
                                StartTime VARCHAR2(8) NOT NULL)
/
