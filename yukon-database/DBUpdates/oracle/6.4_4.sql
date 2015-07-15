/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-14427 */
DELETE FROM ExtraPaoPointAssignment 
WHERE Attribute = 'KEEP_ALIVE_TIMER';
/* End YUK-14427 */

/* Start YUK-14377 */
DELETE FROM State 
WHERE StateGroupId = -17 
  AND RawState IN (16, 17, 18, 19, 20, 21);

UPDATE State
SET Text = 'Remote'
WHERE StateGroupId = -17
  AND RawState = 14;
/* End YUK-14377 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.4', '31-JUL-2015', 'Latest Update', 4, SYSDATE);*/