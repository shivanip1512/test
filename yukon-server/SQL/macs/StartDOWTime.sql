CREATE TABLE StartDOWTime (
                              ScheduleID  INTEGER  REFERENCES MCSchedule NOT NULL,
                              DayOfWeek   VARCHAR2(9) NOT NULL,
                              StartTime   VARCHAR2(8) NOT NULL)
/
