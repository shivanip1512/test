/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* @start YUK-21642 */
ALTER TABLE DynamicRfnDeviceData
ALTER COLUMN GatewayId NUMBER NOT NULL;
GO

ALTER TABLE DynamicRfnDeviceData
DROP COLUMN LastTransferTime;
GO

ALTER TABLE DynamicRfnDeviceData
ADD DescendantCount NUMBER NULL;
GO

ALTER TABLE DynamicRfnDeviceData
ADD LastTransferTime DATE NULL;
GO

UPDATE DynamicRfnDeviceData
SET DescendantCount = -1;

UPDATE DynamicRfnDeviceData
SET LastTransferTime = SYSDATE;
GO

ALTER TABLE DynamicRfnDeviceData
ALTER COLUMN DescendantCount NUMBER NOT NULL;

ALTER TABLE DynamicRfnDeviceData
ALTER COLUMN LastTransferTime DATE NOT NULL;
GO

INSERT INTO DBUpdates VALUES ('YUK-21642', '7.5.0', SYSDATE);
/* @end YUK-21642 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.5', '10-FEB-2020', 'Latest Update', 0, SYSDATE);*/
