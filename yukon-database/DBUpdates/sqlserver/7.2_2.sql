/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-19773 */
ALTER TABLE LMGroupItronMapping
ADD ItronEventId NUMERIC NULL;

INSERT INTO DBUpdates VALUES ('YUK-19773', '7.2.2', GETDATE());
/* @end YUK-19773 */

/* @start YUK-19963 */
CREATE INDEX INDX_CRE_StartDesc_ExecContId ON CommandRequestExec (
    StartTime DESC,
    CommandRequestExecContextId ASC
);

INSERT INTO DBUpdates VALUES ('YUK-19963', '7.2.2', GETDATE());
/* @end YUK-19963 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('7.2', '20-MAY-2019', 'Latest Update', 2, GETDATE());
