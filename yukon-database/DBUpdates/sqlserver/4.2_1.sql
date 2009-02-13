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

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/

/* __YUKON_VERSION__ */