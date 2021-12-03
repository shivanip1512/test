/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-25507 */
UPDATE LMGroupExpressCom 
SET ProtocolPriority = 0 
WHERE ProtocolPriority = 4;

INSERT INTO DBUpdates VALUES ('YUK-25507', '9.1.2', SYSDATE);
/* @end YUK-25507 */


/***********************************************************************************/
/* VERSION INFO                                                                    */
/* Inserted when update script is run, stays commented out until the release build */
/***********************************************************************************/
/* INSERT INTO CTIDatabase VALUES ('9.1', '01-DEC-2021', 'Latest Update', 2, SYSDATE); */