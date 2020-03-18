/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* @start YUK-21642 */
DROP INDEX INDX_DynRfnDevData_GatewayId;
GO

ALTER TABLE DynamicRfnDeviceData
ALTER COLUMN GatewayId NUMBER NOT NULL;
GO

CREATE INDEX INDX_DynRfnDevData_GatewayId ON DynamicRfnDeviceData (
GatewayId ASC
)
GO

ALTER TABLE DynamicRfnDeviceData
ADD DescendantCount NUMBER NULL;
GO

UPDATE DynamicRfnDeviceData
SET DescendantCount = -1;

ALTER TABLE DynamicRfnDeviceData
ALTER COLUMN DescendantCount NUMBER NOT NULL;
GO

ALTER TABLE DynamicRfnDeviceData
ADD LastTransferTimeNew DATE NULL;
GO

UPDATE DynamicRfnDeviceData
SET LastTransferTimeNew = LastTransferTime;
GO

ALTER TABLE DynamicRfnDeviceData
ALTER COLUMN LastTransferTimeNew DATE NOT NULL;
GO

ALTER TABLE DynamicRfnDeviceData
DROP COLUMN LastTransferTime;
GO

ALTER TABLE DynamicRfnDeviceData
RENAME COLUMN LastTransferTimeNew
TO LastTransferTime;
GO

INSERT INTO DBUpdates VALUES ('YUK-21642', '7.5.0', SYSDATE);
/* @end YUK-21642 */

/* @start YUK-20982 */
ALTER TABLE MSPInterface ADD UseVendorAuth CHAR(1);
UPDATE MSPInterface SET UseVendorAuth = 'N' WHERE UseVendorAuth IS NULL;
ALTER TABLE MSPInterface MODIFY UseVendorAuth CHAR(1) NOT NULL;

ALTER TABLE MSPInterface ADD InUserName VARCHAR2(64);
UPDATE MSPInterface SET InUserName = ' ' WHERE InUserName IS NULL;
ALTER TABLE MSPInterface MODIFY InUserName VARCHAR2(64) NOT NULL;

ALTER TABLE MSPInterface ADD InPassword VARCHAR2(64);
UPDATE MSPInterface SET InPassword = ' ' WHERE InPassword IS NULL;
ALTER TABLE MSPInterface MODIFY InPassword VARCHAR2(64) NOT NULL;

ALTER TABLE MSPInterface ADD OutUserName VARCHAR2(64);
UPDATE MSPInterface SET OutUserName = ' ' WHERE OutUserName IS NULL;
ALTER TABLE MSPInterface MODIFY OutUserName VARCHAR2(64) NOT NULL;

ALTER TABLE MSPInterface ADD OutPassword VARCHAR2(64);
UPDATE MSPInterface SET OutPassword = ' ' WHERE OutPassword IS NULL;
ALTER TABLE MSPInterface MODIFY OutPassword VARCHAR2(64) NOT NULL;

ALTER TABLE MSPInterface ADD ValidateCertificate CHAR(1);
UPDATE MSPInterface SET ValidateCertificate = '1' WHERE ValidateCertificate IS NULL;
ALTER TABLE MSPInterface MODIFY ValidateCertificate CHAR(1) NOT NULL;

INSERT INTO DBUpdates VALUES ('YUK-20982', '7.5.0', SYSDATE);
/* @end YUK-20982 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.5', '10-FEB-2020', 'Latest Update', 0, SYSDATE);*/
