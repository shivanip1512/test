/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* @start YUK-21642 */
DROP INDEX INDX_DynRfnDevData_GatewayId;

ALTER TABLE DynamicRfnDeviceData
MODIFY GatewayId NUMBER NOT NULL;

CREATE INDEX INDX_DynRfnDevData_GatewayId ON DynamicRfnDeviceData (
GatewayId ASC
);

ALTER TABLE DynamicRfnDeviceData
ADD DescendantCount NUMBER NULL;

UPDATE DynamicRfnDeviceData
SET DescendantCount = -1;

ALTER TABLE DynamicRfnDeviceData
MODIFY DescendantCount NUMBER NOT NULL;

ALTER TABLE DynamicRfnDeviceData
ADD LastTransferTimeNew DATE NULL;

UPDATE DynamicRfnDeviceData
SET LastTransferTimeNew = LastTransferTime;

ALTER TABLE DynamicRfnDeviceData
MODIFY LastTransferTimeNew DATE NOT NULL;

ALTER TABLE DynamicRfnDeviceData
DROP COLUMN LastTransferTime;

ALTER TABLE DynamicRfnDeviceData
RENAME COLUMN LastTransferTimeNew
TO LastTransferTime;

INSERT INTO DBUpdates VALUES ('YUK-21642', '7.4.1', SYSDATE);
/* @end YUK-21642 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.4', '10-FEB-2020', 'Latest Update', 1, SYSDATE);*/
