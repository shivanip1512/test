/* SQL Server */
/*This script was created to address the thermostat schedule issues introduced in 5.2 and 5.3.
These queries will be part of the standard scripts for all versions after 5.3.6. These scripts
clean up schedules that have too many entries and have entries of the wrong type. See YUK-10534
for more information.*/

/*This entire script now assumes that 5-2 schedules are not allowed*/

/* Start YUK-10534 */
/*Script deletes all entries >4 per scheduleid AND time of week*/
WITH OrderedEntries as
(
   SELECT row_number() over (partition by atse.AcctThermostatScheduleId, atse.TimeOfWeek order by AcctThermostatScheduleEntryId) RowNumber, atse.AcctThermostatScheduleEntryId, ats.AccountId
      FROM AcctThermostatScheduleEntry atse 
         JOIN AcctThermostatSchedule ats ON atse.AcctThermostatScheduleId = ats.AcctThermostatScheduleId
)
delete FROM AcctThermostatScheduleEntry 
WHERE AcctThermostatScheduleEntry.AcctThermostatScheduleEntryId in (SELECT AcctThermostatScheduleEntryId 
                                                                       FROM OrderedEntries 
                                                                       WHERE AccountId != 0 
                                                                          AND RowNumber > 4);

/*This adds saturday/sunday entries if there are only weekend entries. This must not be run on a system that uses 5-2 schedules*/
/* @start-block */
DECLARE @v_schedule_id NUMERIC;
DECLARE @v_starttime NUMERIC;
DECLARE @v_cooltemp FLOAT;
DECLARE @v_heattemp FLOAT;
DECLARE @v_last_schedule_entry NUMERIC;

DECLARE c_weekend_entries CURSOR STATIC FOR (SELECT AcctThermostatScheduleId, StartTime, CoolTemp, HeatTemp
                                             FROM AcctThermostatScheduleEntry 
                                             WHERE TimeOfWeek = 'WEEKEND' 
                                                AND AcctThermostatScheduleId not in (SELECT AcctThermostatScheduleId 
                                                                                     FROM AcctThermostatScheduleEntry 
                                                                                     WHERE TimeOfWeek = 'SUNDAY'
                                                                                     GROUP BY AcctThermostatScheduleId));

SET @v_last_schedule_entry = (SELECT MAX(AcctThermostatScheduleEntryId) 
FROM AcctThermostatScheduleEntry);

OPEN c_weekend_entries;
FETCH next FROM c_weekend_entries INTO @v_schedule_id, @v_starttime, @v_cooltemp, @v_heattemp;


WHILE (@@fetch_status = 0)
BEGIN
  SET @v_last_schedule_entry = @v_last_schedule_entry + 1; 
  INSERT INTO AcctThermostatScheduleEntry VALUES(@v_last_schedule_entry, @v_schedule_id, @v_starttime, 'SUNDAY', @v_cooltemp, @v_heattemp);
  SET @v_last_schedule_entry = @v_last_schedule_entry + 1; 
  INSERT INTO AcctThermostatScheduleEntry VALUES(@v_last_schedule_entry, @v_schedule_id, @v_starttime, 'SATURDAY', @v_cooltemp, @v_heattemp);

  FETCH next FROM c_weekend_entries INTO @v_schedule_id, @v_starttime, @v_cooltemp, @v_heattemp;
END
   
CLOSE c_weekend_entries;
deallocate c_weekend_entries;
/* @end-block */
go

UPDATE AcctThermostatSchedule
SET ScheduleMode = NULL
WHERE ScheduleMode = 'WEEKDAY_WEEKEND';
go

UPDATE SequenceNumber
SET LastValue = (SELECT MAX(AcctThermostatScheduleEntryId)
                 FROM AcctThermostatScheduleEntry)
WHERE SequenceName = 'AcctThermostatScheduleEntry';
go

/*Note each of these must be run in sequence*/
/*UPDATE the schedule type to all if saturday AND sunday AND weekday are equal*/
UPDATE AcctThermostatSchedule 
SET AcctThermostatSchedule.ScheduleMode = 'ALL' 
WHERE AcctThermostatSchedule.ScheduleMode IS NULL 
   AND AcctThermostatSchedule.AccountId != 0 
   AND AcctThermostatScheduleId in (/* This returns all schedules WHERE saturday = sunday = weekday*/
                                    SELECT AcctThermostatScheduleId 
                                    FROM (SELECT AcctThermostatScheduleId 
                                          FROM AcctThermostatScheduleEntry
                                          WHERE AcctThermostatScheduleEntry.TimeOfWeek IN ('SATURDAY', 'SUNDAY', 'WEEKDAY')
                                          GROUP BY AcctThermostatScheduleId, StartTime, CoolTemp, HeatTemp ) subquery
                                    GROUP BY acctThermostatScheduleId
                                    HAVING count(AcctThermostatScheduleId) = 4);
go

/*UPDATE the schedule type to weekday_sat_sun if saturday AND sunday are not equal*/
UPDATE AcctThermostatSchedule 
SET AcctThermostatSchedule.ScheduleMode = 'WEEKDAY_SAT_SUN' 
WHERE AcctThermostatSchedule.ScheduleMode IS NULL 
   AND AcctThermostatSchedule.AccountId != 0;
go
/* End YUK-10534 */

/* Repeated FROM the 5.3.6 upgrade script */
/* Start YUK-10440 */
DELETE
FROM AcctThermostatScheduleEntry 
WHERE AcctThermostatScheduleEntryId IN
(SELECT atse.AcctThermostatScheduleEntryId
FROM AcctThermostatSchedule ats
JOIN AcctThermostatScheduleEntry atse ON (ats.AcctThermostatScheduleId = atse.AcctThermostatScheduleId)
WHERE ats.AcctThermostatScheduleId NOT IN (SELECT AcctThermostatScheduleId FROM ECToAcctThermostatSchedule)
      AND (((ThermostatType = 'UTILITY_PRO'             AND ScheduleMode NOT IN ('ALL', 'WEEKDAY_SAT_SUN', 'WEEKDAY_WEEKEND')) OR
            (ThermostatType = 'COMMERCIAL_EXPRESSSTAT'  AND ScheduleMode NOT IN ('ALL', 'WEEKDAY_SAT_SUN')) OR
            (ThermostatType = 'HEAT_PUMP_EXPRESSSTAT'   AND ScheduleMode NOT IN ('ALL', 'WEEKDAY_SAT_SUN')) OR
            (ThermostatType = 'RESIDENTIAL_EXPRESSSTAT' AND ScheduleMode NOT IN ('ALL', 'WEEKDAY_SAT_SUN')) OR
            (ThermostatType IN ('UTILITY_PRO_G2', 'UTILITY_PRO_G3', 'UTILITY_PRO_ZIGBEE') 
                      AND ScheduleMode NOT IN ('ALL', 'WEEKDAY_SAT_SUN', 'WEEKDAY_WEEKEND', 'SEVEN_DAY')))
      OR   ((ats.ThermostatType = 'UTILITY_PRO'             AND ats.ScheduleMode = 'ALL'             AND atse.TimeOfWeek NOT IN ('WEEKDAY'))
         OR (ats.ThermostatType = 'UTILITY_PRO'             AND ats.ScheduleMode = 'WEEKDAY_SAT_SUN' AND atse.TimeOfWeek NOT IN ('WEEKDAY', 'SATURDAY', 'SUNDAY'))
         OR (ats.ThermostatType = 'UTILITY_PRO'             AND ats.ScheduleMode = 'WEEKDAY_WEEKEND' AND atse.TimeOfWeek NOT IN ('WEEKDAY', 'WEEKEND')))
      OR   ((ats.ThermostatType = 'COMMERCIAL_EXPRESSSTAT'  AND ats.ScheduleMode = 'ALL'             AND atse.TimeOfWeek NOT IN ('WEEKDAY'))
         OR (ats.ThermostatType = 'COMMERCIAL_EXPRESSSTAT'  AND ats.ScheduleMode = 'WEEKDAY_SAT_SUN' AND atse.TimeOfWeek NOT IN ('WEEKDAY', 'SATURDAY', 'SUNDAY')))
      OR   ((ats.ThermostatType = 'HEAT_PUMP_EXPRESSSTAT'   AND ats.ScheduleMode = 'ALL'             AND atse.TimeOfWeek NOT IN ('WEEKDAY'))
         OR (ats.ThermostatType = 'HEAT_PUMP_EXPRESSSTAT'   AND ats.ScheduleMode = 'WEEKDAY_SAT_SUN' AND atse.TimeOfWeek NOT IN ('WEEKDAY', 'SATURDAY', 'SUNDAY')))
      OR   ((ats.ThermostatType = 'RESIDENTIAL_EXPRESSSTAT' AND ats.ScheduleMode = 'ALL'             AND atse.TimeOfWeek NOT IN ('WEEKDAY'))
         OR (ats.ThermostatType = 'RESIDENTIAL_EXPRESSSTAT' AND ats.ScheduleMode = 'WEEKDAY_SAT_SUN' AND atse.TimeOfWeek NOT IN ('WEEKDAY', 'SATURDAY', 'SUNDAY')))
      OR   ((ats.ThermostatType IN ('UTILITY_PRO_G2', 'UTILITY_PRO_G3', 'UTILITY_PRO_ZIGBEE'))
             AND((ats.ScheduleMode = 'ALL'             AND atse.TimeOfWeek NOT IN ('WEEKDAY'))
              OR (ats.ScheduleMode = 'WEEKDAY_SAT_SUN' AND atse.TimeOfWeek NOT IN ('WEEKDAY', 'SATURDAY', 'SUNDAY'))
              OR (ats.ScheduleMode = 'WEEKDAY_WEEKEND' AND atse.TimeOfWeek NOT IN ('WEEKDAY', 'WEEKEND'))
              OR (ats.ScheduleMode = 'SEVEN_DAY'       AND atse.TimeOfWeek NOT IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'))))));
/* End YUK-10440 */