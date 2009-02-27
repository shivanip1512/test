/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

/* Start YUK-6863 */
DELETE FROM YukonGroupRole
WHERE GroupId IN (-1, -2, -303, -100)
AND RoleId = -400;
/* End YUK-6863 */

/* Start YUK-6878 */
INSERT INTO YukonRoleProperty 
VALUES(-1603,-7,'Msp LM Interface Mapping Setup','false','Controls access to setup the MultiSpeak LM interface mappings.');
/* End YUK-6878 */

/* Start YUK-6518 */
UPDATE YukonRoleProperty
SET DefaultValue = 'true'
WHERE RolePropertyId = -20015
OR RolePropertyId = -20016;
/* End YUK-6518 */

/* Start YUK-6897 */
/* @start-block */
DECLARE @jobIdValue INT; 
SET @jobIdValue = CAST((SELECT CASE  
                            WHEN (MAX(JobId) IS NULL)
                                THEN 1 
                            ELSE 
                                MAX(JobId)+1 
                            END 
                        FROM JOB) as INT);

INSERT INTO Job
VALUES (@jobIdValue, 'optOutSchedulerJob', 'N', -1, 'en_US', ' ', ' ');
INSERT INTO JobScheduledRepeating
VALUES (@jobIdValue, '0 0/5 * * * ?');
/* @end-block */
/* End YUK-6897 */

/* Start YUK-6933 */
ALTER TABLE OptOutEvent 
    ADD CONSTRAINT FK_OptOutEvent_InvBase FOREIGN KEY(InventoryId) 
        REFERENCES InventoryBase (InventoryId) 
            ON DELETE CASCADE;

ALTER TABLE OptOutEvent 
    ADD CONSTRAINT FK_OptOutEvent_CustAcct FOREIGN KEY(CustomerAccountId) 
        REFERENCES CustomerAccount (AccountId) 
            ON DELETE CASCADE;
/* End YUK-6933 */

/* Start YUK-6942 */
UPDATE MspInterface
SET Interface = 'MR_Server', Endpoint = 'MR_ServerSoap'
WHERE VendorId = 1
AND Interface = 'MR_CB';

UPDATE MspInterface
SET Interface = 'OD_Server', Endpoint = 'OD_ServerSoap'
WHERE VendorId = 1
AND Interface = 'OD_OA';

UPDATE MspInterface
SET Interface = 'CD_Server', Endpoint = 'CD_ServerSoap'
WHERE VendorId = 1
AND Interface = 'CD_CB';

DELETE MspInterface 
WHERE VendorId = 1 
AND Interface = 'MR_EA'; 
/* End YUK-6942 */

/* Start YUK-6966 */
ALTER TABLE OptOutEventLog
   DROP CONSTRAINT FK_OptOutEvent_OptOutEventLog;
/* End YUK-6966 */

/* Start YUK-6993 */
INSERT INTO YukonRoleProperty VALUES(-20164,-201,'Enroll Multiple Programs per Category','false','Enables you to enroll in multiple programs within an appliance category.');
/* End YUK-6993 */

/* Start YUK-6999 */
INSERT INTO YukonRoleProperty VALUES(-20017,-200,'Initialize Energy Company Setup','false','Controls if Energy Company needs to be initialized. Property should not be changed manually, but rather will be controlled by the system.');
/* End YUK-6999 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/

INSERT INTO CTIDatabase VALUES('4.2', 'Matt K', '19-FEB-2009', 'Latest Update', 1);