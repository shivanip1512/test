/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 

/* Start YUK-9322 */
INSERT INTO YukonRoleProperty VALUES(-1118,-2,'Serial Number Validation','NUMERIC','Treat serial numbers as numeric or alpha-numberic. Possible values (NUMERIC, ALPHANUMERIC)');
/* End YUK-9322 */

/* Start YUK-9354 */
DELETE FROM InventoryConfigTask;

ALTER TABLE InventoryConfigTask
ADD UserId NUMERIC NOT NULL;

ALTER TABLE InventoryConfigTask 
    ADD CONSTRAINT FK_InvConfTask_YukonUser FOREIGN KEY (UserId) 
        REFERENCES YukonUser (UserId) 
            ON DELETE CASCADE; 
/* End YUK-9354 */

/* Start YUK-9467 */
UPDATE YukonRoleProperty 
SET DefaultValue = 'METER_NUMBER', 
    Description = 'Defines a Yukon Pao (Device) Name field alias. Valid values: METER_NUMBER, ACCOUNT_NUMBER, SERVICE_LOCATION, CUSTOMER_ID, GRID_LOCATION, POLE_NUMBER' 
WHERE RolePropertyId = -1600;
/* End YUK-9467 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
