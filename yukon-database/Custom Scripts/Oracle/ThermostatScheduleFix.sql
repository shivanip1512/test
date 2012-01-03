/* ORACLE */
/*This script was created to address the thermostat schedule issues introduced in 5.2 and 5.3.
These queries will be part of the standard scripts for all versions after 5.3.6. These scripts
clean up schedules that have too many entries and have entries of the wrong type. See YUK-10534
for more information.*/

/*This entire script now assumes that 5-2 schedules are not allowed*/

/* Start YUK-10534 */
/*This deletes all entries >4 per scheduleid and time of week*/
DELETE FROM AcctThermostatScheduleEntry WHERE AcctThermostatScheduleEntryid in (
   WITH OrderedEntries as
   (
      SELECT row_number() over (PARTITION BY ATSE.AcctThermostatScheduleId, ATSE.TimeOfWeek ORDER BY ATSE.RowId) RowNumber, ATSE.AcctThermostatScheduleEntryID, ATS.AccountId
         FROM AcctThermostatScheduleEntry ATSE
           JOIN AcctThermostatSchedule ATS ON ATSE.AcctThermostatScheduleId = ATS.AcctThermostatScheduleId
   )
   SELECT AcctThermostatScheduleEntryId 
   FROM OrderedEntries 
   WHERE AccountId != 0 
       AND RowNumber > 4
);
COMMIT;


/*This adds saturday/sunday entries if there are only weekend entries. This must not be run on a system that uses 5-2 schedules*/
/* @start-block */
DECLARE
v_schedule_id NUMBER;
v_starttime NUMBER;
v_cooltemp FLOAT;
v_heattemp FLOAT;
v_last_schedule_entry NUMBER;

CURSOR c_weekend_entries IS (SELECT AcctThermostatScheduleId, StartTime, CoolTemp, HeatTemp
                             FROM AcctThermostatScheduleEntry 
                             WHERE TimeOfWeek = 'WEEKEND' 
                                AND AcctThermostatScheduleId NOT IN (SELECT AcctThermostatScheduleId 
                                                                     FROM AcctThermostatScheduleEntry 
                                                                     WHERE TimeOfWeek = 'SUNDAY'
                                                                     GROUP BY AcctThermostatScheduleId));

BEGIN
   SELECT MAX(AcctThermostatScheduleEntryId) INTO v_last_schedule_entry 
   FROM AcctThermostatScheduleEntry;
   
   OPEN c_weekend_entries;
   FETCH c_weekend_entries INTO v_schedule_id, v_starttime, v_cooltemp, v_heattemp;

   WHILE(c_weekend_entries%found)
   LOOP
      v_last_schedule_entry := v_last_schedule_entry + 1; 
      INSERT INTO AcctThermostatScheduleEntry values(v_last_schedule_entry, v_schedule_id, v_starttime, 'SUNDAY', v_cooltemp, v_heattemp);
      v_last_schedule_entry := v_last_schedule_entry + 1; 
      INSERT INTO AcctThermostatScheduleEntry values(v_last_schedule_entry, v_schedule_id, v_starttime, 'SATURDAY', v_cooltemp, v_heattemp);

      FETCH c_weekend_entries INTO v_schedule_id, v_starttime, v_cooltemp, v_heattemp;
   END LOOP;
   
   CLOSE c_weekend_entries;
END;
/
/* @end-block */
COMMIT;

UPDATE AcctThermostatSchedule 
SET ScheduleMode = NULL 
WHERE ScheduleMode = 'WEEKDAY_WEEKEND';

UPDATE SequenceNUMBER 
SET LastValue = (SELECT MAX(AcctThermostatScheduleEntryId) 
                 FROM AcctThermostatScheduleEntry)
WHERE SequenceName = 'AcctThermostatScheduleEntry';

/*Note each of these must be run in sequence*/
/*Update the schedule type to all if saturday and sunday and weekday are equal*/
UPDATE AcctThermostatSchedule 
SET AcctThermostatSchedule.ScheduleMode = 'ALL' 
WHERE AcctThermostatSchedule.ScheduleMode IS NULL 
    AND AcctThermostatSchedule.AccountId != 0 
    AND AcctThermostatScheduleId IN (/* This returns all schedules WHERE saturday = sunday = weekday*/
                                     SELECT AcctThermostatScheduleId 
                                     FROM (SELECT AcctThermostatScheduleId 
                                           FROM AcctThermostatScheduleEntry
                                           WHERE AcctThermostatScheduleEntry.TimeOfWeek IN ('SATURDAY', 'SUNDAY', 'WEEKDAY')
                                           GROUP BY AcctThermostatScheduleId, StartTime, CoolTemp, HeatTemp)
                                     GROUP BY AcctThermostatScheduleId
                                     HAVING COUNT(AcctThermostatScheduleId) = 4);

/*Update the schedule type to weekday_sat_sun if saturday and sunday are not equal*/
UPDATE AcctThermostatSchedule 
SET AcctThermostatSchedule.ScheduleMode = 'WEEKDAY_SAT_SUN'
WHERE AcctThermostatSchedule.ScheduleMode IS NULL
   AND AcctThermostatSchedule.AccountId != 0;
/* End YUK-10534 */
COMMIT;


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
COMMIT;