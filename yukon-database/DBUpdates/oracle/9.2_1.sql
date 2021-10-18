/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-25311 */
DELETE FROM CTIDatabase
WHERE Version = 9.2
AND Build = 0;

INSERT INTO DBUpdates VALUES ('YUK-25311', '9.2.0', SYSDATE);
/* @end YUK-25311 */

/***********************************************************************************/
/* VERSION INFO                                                                    */
/* Inserted when update script is run, stays commented out until the release build */
/* https://confluence-prod.tcc.etn.com/display/EEST/Writing+Update+Scripts         */
/* https://confluence-prod.tcc.etn.com/display/EEST/Yukon+Build+Database+Tasks     */
/***********************************************************************************/
/* INSERT INTO CTIDatabase VALUES ('9.2', '18-OCT-2021', 'Latest Update', 1, SYSDATE); */