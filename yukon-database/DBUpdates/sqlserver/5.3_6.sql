/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 

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

/* Start YUK-10441 */
/* Warn about any default schedules that have problems similar to YUK-10440.
 *     Note: These are not deleted by default because of the heightened 
 *           sensitivity of default schedule values.*/              
/* @error warn-once */
/* @start-block */
IF 0 < (SELECT COUNT(ats.AcctThermostatScheduleId)
		FROM AcctThermostatSchedule ats
		JOIN AcctThermostatScheduleEntry atse ON (ats.AcctThermostatScheduleId = atse.AcctThermostatScheduleId)
		JOIN ECToAcctThermostatSchedule ec2ats ON (ats.AcctThermostatScheduleId = ec2ats.AcctThermostatScheduleId)
		WHERE /* Either the thermostat has an invalid ScheduleMode given its ThermostatType */
		           ((ThermostatType = 'UTILITY_PRO'             AND ScheduleMode NOT IN ('ALL', 'WEEKDAY_SAT_SUN', 'WEEKDAY_WEEKEND')) OR
		            (ThermostatType = 'COMMERCIAL_EXPRESSSTAT'  AND ScheduleMode NOT IN ('ALL', 'WEEKDAY_SAT_SUN')) OR
		            (ThermostatType = 'HEAT_PUMP_EXPRESSSTAT'   AND ScheduleMode NOT IN ('ALL', 'WEEKDAY_SAT_SUN')) OR
		            (ThermostatType = 'RESIDENTIAL_EXPRESSSTAT' AND ScheduleMode NOT IN ('ALL', 'WEEKDAY_SAT_SUN')) OR
		            (ThermostatType IN ('UTILITY_PRO_G2', 'UTILITY_PRO_G3', 'UTILITY_PRO_ZIGBEE') 
		                      AND ScheduleMode NOT IN ('ALL', 'WEEKDAY_SAT_SUN', 'WEEKDAY_WEEKEND', 'SEVEN_DAY')))
		      /* Or the schedule has invalid timeOfWeek entries in AcctThermostatScheduleEntry */
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
		              OR (ats.ScheduleMode = 'SEVEN_DAY'       AND atse.TimeOfWeek NOT IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY')))))
RAISERROR('Your thermostat default schedules contain errors. To correct this, please go to your EC default schedules & double-check them.  There will be at least one default schedule that has invalid data (too many entries).  For any schedules that have incorrect entries, choose a different mode and save.  Then edit the schedule again to restore the original mode (the entries will then be gone).  For more information see YUK-10441.', 16, 1);		              
/* @end-block */
/* End YUK-10441 */
              
/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
INSERT INTO CTIDatabase VALUES ('5.3', 'Garrett D', '21-NOV-2011', 'Latest Update', 6 );
