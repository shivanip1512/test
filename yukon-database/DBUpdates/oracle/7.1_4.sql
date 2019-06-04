/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* @start YUK-19667 */
UPDATE POINT
SET POINTOFFSET = 387
WHERE POINTOFFSET = 362
AND POINTTYPE = 'Analog'
AND POINTNAME = 'Net kW';

UPDATE POINT
SET POINTOFFSET = 388
WHERE POINTOFFSET = 363
AND POINTTYPE = 'Analog'
AND POINTNAME = 'kVAr (Quadrants 1 3)';

INSERT INTO DBUpdates VALUES ('YUK-19667', '7.1.4', SYSDATE);
/* @end YUK-19667 */

/* @start YUK-19963 */
CREATE INDEX INDX_CRE_StartDesc_ExecContId ON CommandRequestExec (
    StartTime DESC,
    CommandRequestExecContextId ASC
);

INSERT INTO DBUpdates VALUES ('YUK-19963', '7.1.4', SYSDATE);
/* @end YUK-19963 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('7.1', '4-JUN-2019', 'Latest Update', 4, SYSDATE);