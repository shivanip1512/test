CREATE TABLE MCSchedule (
                           ScheduleID     INTEGER PRIMARY KEY,
                           ScheduleName   VARCHAR2(20) NOT NULL,
                           CommandFile    VARCHAR2(48) NOT NULL,
                           CurrentState   VARCHAR2(12) NOT NULL,
                           StartType      VARCHAR2(20) NOT NULL,
                           StopType       VARCHAR2(20) NOT NULL,
                           LastRunTime    DATE NOT NULL,
                           LastRunStatus  VARCHAR2(12) NOT NULL );

CREATE TABLE StartAbsoluteTime (
                                ScheduleID INTEGER REFERENCES MCSchedule NOT NULL,
                                StartTime VARCHAR2(8) NOT NULL);

CREATE TABLE StartDateTime (
                              ScheduleID  INTEGER  REFERENCES MCSchedule NOT NULL,
                              Month       VARCHAR2(9) NOT NULL,
                              DayOfMonth  INTEGER NOT NULL,
                              StartTime   VARCHAR2(8) NOT NULL);


CREATE TABLE StartDeltaTime (
                                 ScheduleID INTEGER REFERENCES MCSchedule NOT NULL,
                                 DeltaTime   VARCHAR2(8) NOT NULL );

CREATE TABLE StartDOMTime (
                              ScheduleID  INTEGER  REFERENCES MCSchedule NOT NULL,
                              DayOfMonth  INTEGER NOT NULL,
                              StartTime   VARCHAR2(8) NOT NULL );

CREATE TABLE StartDOWTime (
                              ScheduleID  INTEGER  REFERENCES MCSchedule NOT NULL,
                              DayOfWeek   VARCHAR2(9) NOT NULL,
                              StartTime   VARCHAR2(8) NOT NULL);

CREATE TABLE StopAbsoluteTime (
                                 ScheduleID INTEGER REFERENCES MCSchedule NOT NULL,
                                 StopTime VARCHAR2(8) NOT NULL );

CREATE TABLE StopDurationTime (
                                 ScheduleID INTEGER REFERENCES MCSchedule NOT NULL,
                                 DurationTime   VARCHAR2(8) NOT NULL );
