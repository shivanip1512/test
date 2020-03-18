/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-21642 */
DROP INDEX INDX_DynRfnDevData_GatewayId ON DynamicRfnDeviceData;
GO

ALTER TABLE DynamicRfnDeviceData
ALTER COLUMN GatewayId NUMERIC NOT NULL;
GO

CREATE INDEX INDX_DynRfnDevData_GatewayId ON DynamicRfnDeviceData (
GatewayId ASC
)
GO

ALTER TABLE DynamicRfnDeviceData
ADD DescendantCount NUMERIC NULL;
GO

UPDATE DynamicRfnDeviceData
SET DescendantCount = -1;

ALTER TABLE DynamicRfnDeviceData
ALTER COLUMN DescendantCount NUMERIC NOT NULL;
GO

ALTER TABLE DynamicRfnDeviceData
ADD LastTransferTimeNew datetime NULL;
GO

UPDATE DynamicRfnDeviceData
SET LastTransferTimeNew = LastTransferTime;
GO

ALTER TABLE DynamicRfnDeviceData
ALTER COLUMN LastTransferTimeNew datetime NOT NULL;
GO

ALTER TABLE DynamicRfnDeviceData
DROP COLUMN LastTransferTime;
GO

EXEC sp_rename 'DynamicRfnDeviceData.LastTransferTimeNew', 'LastTransferTime', 'COLUMN';
GO

INSERT INTO DBUpdates VALUES ('YUK-21642', '7.5.0', GETDATE());
/* @end YUK-21642 */

/* @start YUK-20982 */
ALTER TABLE MSPInterface ADD UseVendorAuth CHAR(1);
GO
UPDATE MSPInterface SET UseVendorAuth = 'N' WHERE UseVendorAuth IS NULL;
GO
ALTER TABLE MSPInterface ALTER COLUMN UseVendorAuth CHAR(1) NOT NULL;
GO

ALTER TABLE MSPInterface ADD InUserName VARCHAR(64);
GO
UPDATE MSPInterface SET InUserName = ' ' WHERE InUserName IS NULL;
GO
ALTER TABLE MSPInterface ALTER COLUMN InUserName VARCHAR(64) NOT NULL;
GO

ALTER TABLE MSPInterface ADD InPassword VARCHAR(64);
GO
UPDATE MSPInterface SET InPassword = ' ' WHERE InPassword IS NULL;
GO
ALTER TABLE MSPInterface ALTER COLUMN InPassword VARCHAR(64) NOT NULL;
GO

ALTER TABLE MSPInterface ADD OutUserName VARCHAR(64);
GO
UPDATE MSPInterface SET OutUserName = ' ' WHERE OutUserName IS NULL;
GO
ALTER TABLE MSPInterface ALTER COLUMN OutUserName VARCHAR(64) NOT NULL;
GO

ALTER TABLE MSPInterface ADD OutPassword VARCHAR(64);
GO
UPDATE MSPInterface SET OutPassword = ' ' WHERE OutPassword IS NULL;
GO
ALTER TABLE MSPInterface ALTER COLUMN OutPassword VARCHAR(64) NOT NULL;
GO

ALTER TABLE MSPInterface ADD ValidateCertificate CHAR(1);
GO
UPDATE MSPInterface SET ValidateCertificate = '1' WHERE ValidateCertificate IS NULL;
GO
ALTER TABLE MSPInterface ALTER COLUMN ValidateCertificate CHAR(1) NOT NULL;
GO

INSERT INTO DBUpdates VALUES ('YUK-20982', '7.5.0', GETDATE());
/* @end YUK-20982 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.5', '15-NOV-2019', 'Latest Update', 0, GETDATE());*/