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

INSERT INTO DBUpdates VALUES ('YUK-21642', '7.4.1', SYSDATE);
/* @end YUK-21642 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.4', '10-FEB-2020', 'Latest Update', 1, SYSDATE);*/
