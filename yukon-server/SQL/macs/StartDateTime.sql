CREATE TABLE StartDateTime (
                              ScheduleID  INTEGER  REFERENCES MCSchedule NOT NULL,
                              Month       VARCHAR2(9) NOT NULL,
                              DayOfMonth  INTEGER NOT NULL,
                              StartTime   VARCHAR2(8) NOT NULL)
/
