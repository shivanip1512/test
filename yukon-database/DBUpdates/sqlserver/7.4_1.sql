/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-21642 */
ALTER TABLE DynamicRfnDeviceData
ALTER COLUMN GatewayId NUMERIC NOT NULL;
GO

ALTER TABLE DynamicRfnDeviceData
DROP COLUMN LastTransferTime;
GO

ALTER TABLE DynamicRfnDeviceData
ADD DescendantCount NUMERIC NULL;
GO

ALTER TABLE DynamicRfnDeviceData
ADD LastTransferTime datetime NULL;
GO

UPDATE DynamicRfnDeviceData
SET DescendantCount = -1;

UPDATE DynamicRfnDeviceData
SET LastTransferTime = GETDATE();
go

ALTER TABLE DynamicRfnDeviceData
ALTER COLUMN DescendantCount NUMERIC NOT NULL;

ALTER TABLE DynamicRfnDeviceData
ALTER COLUMN LastTransferTime datetime NOT NULL;
GO

INSERT INTO DBUpdates VALUES ('YUK-21642', '7.4.1', GETDATE());
/* @end YUK-21642 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.4', '15-NOV-2019', 'Latest Update', 1, GETDATE());*/