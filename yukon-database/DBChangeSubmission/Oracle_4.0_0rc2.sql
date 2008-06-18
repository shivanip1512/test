/* 4.0_0rc2 changes.  These are changes to 4.0 that have been made since 4.0_0rc1*/
/* This script must be run manually using the SQL tool and not the DBToolsFrame tool. */

/* A database that was newly created in verion 3.5 AND already upgraded to 4.0rc1 CANNOT be upgraded to rc2 using 
 * this script.  Manual processing will be needed for upgrading a brand new 3.5 database, that has already been upgraded
 * to r.0rc1, to 4.0rc2. 
 */ 

/* There were no "new" customers deployed in 3.5 for an Oracle database, so the work needed to modify DeviceGroup id values
 * was not completed for Oracle (since it would be completely different syntax to do so).  Therefore, no NEW 3.5 databases can be upgraded to 
 * 4.0rc2.
 * Instead a manual procedure will be needed to review the id keys used in the Oracle database for the DeviceGroup tables.
 */


/* Start YUK-4744 */
update DynamicBillingFormat set footer = ' ' WHERE formatId = 31;
update DynamicBillingFormat set header = ' ', footer = ' ' where formatId = 21; 

update DynamicBillingField set fieldFormat = ' ' where id = 2;
update DynamicBillingField set fieldFormat = ' ' where id = 10;
/* End YUK-4744 */



/* Start YUK-5960 */ 
ALTER TABLE State DROP CONSTRAINT SYS_C0013342; 
ALTER TABLE Point DROP CONSTRAINT Ref_STATGRP_PT;

UPDATE StateGroup 
SET stateGroupId = -9 
WHERE stateGroupId = 2;

UPDATE Point 
SET stateGroupId = -9 
WHERE stateGroupId = 2; 

UPDATE State 
SET stateGroupId = -9 
WHERE stateGroupId = 2; 

ALTER TABLE Point 
    ADD CONSTRAINT Ref_STATGRP_PT FOREIGN KEY (stateGroupId) 
        REFERENCES StateGroup (stateGroupId); 

ALTER TABLE State 
    ADD CONSTRAINT SYS_C0013342 FOREIGN KEY (stateGroupId) 
        REFERENCES StateGroup (stateGroupId); 
/* End YUK-5960 */

/* Start YUK-6004 */
/* @start-block */
DECLARE
    tbl_exist int;
BEGIN
    SELECT COUNT(*) INTO tbl_exist 
    FROM USER_TAB_COLUMNS
    WHERE table_name = 'DYNAMICCCCAPBANK'
    AND column_name = 'TWOWAYCBCLASTCONTROL';
    
    IF tbl_exist = 0 THEN
        execute immediate 'ALTER TABLE DynamicCCCapBank 
                           ADD twoWayCBCLastControl NUMBER';
        execute immediate 'UPDATE DynamicCCCapBank 
                           SET twoWayCBCLastControl = 0 
                           WHERE twoWayCBCLastControl IS NULL';
        execute immediate 'ALTER TABLE DynamicCCCapBank 
                           MODIFY twoWayCBCLastControl NUMBER NOT NULL';
    END IF;
END;
/
/* @end-block */
/* End YUK-6004 */

/* Start YUK-6006 */
/* @start-block */
DECLARE
    tbl_exist int;
BEGIN
    SELECT COUNT(*) INTO tbl_exist 
    FROM USER_TAB_COLUMNS
    WHERE table_name = 'DYNAMICCCFEEDER'
    AND column_name = 'RETRYINDEX';
    
    IF tbl_exist = 0 THEN
        execute immediate 'ALTER TABLE DynamicCCFeeder 
                           ADD retryIndex NUMBER';
        execute immediate 'UPDATE DynamicCCFeeder 
                           SET retryIndex = 0 
                           WHERE retryIndex IS NULL';
        execute immediate 'ALTER TABLE DynamicCCFeeder 
                           MODIFY retryIndex NUMBER NOT NULL';
    END IF;
END;
/
/* @end-block */
/* End YUK-6006 */

/* Start YUK-5999*/
INSERT INTO YukonRoleProperty VALUES(-100013, -1000, 'Three Phase', 'false', 'display 3-phase data for sub bus');
/* End YUK-5999 */

/* Start YUK-5365 */
/* @error ignore-begin */
INSERT INTO YukonGroupRole VALUES (-779,-301,-900,-90004,'(none)'); 
INSERT INTO YukonRoleProperty VALUES (-1112,-2,'applicable_point_type_key',' ','The name of the set of CICustomerPointData TYPES that should be set for customers.'); 
/* @error ignore-end */
/* End YUK-5365 */