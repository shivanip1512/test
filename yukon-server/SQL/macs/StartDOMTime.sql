CREATE TABLE StartDOMTime (
                              ScheduleID  INTEGER  REFERENCES MCSchedule NOT NULL,
                              DayOfMonth  INTEGER NOT NULL,
                              StartTime   VARCHAR2(8) NOT NULL )
/
