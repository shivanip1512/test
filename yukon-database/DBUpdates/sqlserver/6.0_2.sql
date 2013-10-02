/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-12536*/
INSERT INTO YukonRoleProperty VALUES (-90044,-900,'Asset Availability','false','Controls access to view Asset Availability for Scenarios, Control Areas, Programs, and Load Groups.');
/* End YUK-12536*/

/* Start YUK-12484*/
INSERT INTO YukonRoleProperty VALUES(-20020,-200,'Network Manager Access','false','Controls access to Network Manager.');
/* End YUK-12484*/

/* Start YUK-12305 */
ALTER TABLE JobProperty
ALTER COLUMN Value VARCHAR(4000) NOT NULL;
/* End YUK-12305 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.0', '1-OCT-2013', 'Latest Update', 2, GETDATE());*/