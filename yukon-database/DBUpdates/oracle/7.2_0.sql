/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* @start YUK-18039 */
ALTER TABLE InfrastructureWarnings
ADD Timestamp DATE;

UPDATE InfrastructureWarnings
SET Timestamp = '01-JAN-1990';

ALTER TABLE InfrastructureWarnings
MODIFY (Timestamp DATE NOT NULL);

INSERT INTO DBUpdates VALUES ('YUK-18039', '7.2.0', SYSDATE);
/* @end YUK-18039 */

/* @start YUK-18744 */
UPDATE SmartNotificationEventParam
SET Name = 'Status', Value = 'STOPPED'
WHERE Name = 'Argument0';

UPDATE SmartNotificationEventParam
SET Value = 'YUKON_WEB_APPLICATION_SERVICE'
WHERE Value = 'WEB_SERVER_SERVICE_STATUS'
AND Name = 'WarningType';

UPDATE SmartNotificationEventParam
SET Value = 'YUKON_SERVICE_MANAGER'
WHERE Value = 'SERVICE_MANAGER_SERVICE_STATUS'
AND Name = 'WarningType';

UPDATE SmartNotificationEventParam
SET Value = 'YUKON_NOTIFICATION_SERVER'
WHERE Value = 'NOTIFICATION_SERVER_SERVICE_STATUS'
AND Name = 'WarningType';

UPDATE SmartNotificationEventParam
SET Value = 'YUKON_MESSAGE_BROKER'
WHERE Value = 'YUKON_MESSAGE_BROKER_SERVICE_STATUS'
AND Name = 'WarningType';

UPDATE SmartNotificationEventParam
SET Value = 'YUKON_DISPATCH_SERVICE'
WHERE Value = 'DISPATCH_SERVICE_STATUS'
AND Name = 'WarningType';

UPDATE SmartNotificationEventParam
SET Value = 'YUKON_CAP_CONTROL_SERVICE'
WHERE Value = 'CAPCONTROL_SERVICE_STATUS'
AND Name = 'WarningType';

UPDATE SmartNotificationEventParam
SET Value = 'YUKON_LOAD_MANAGEMENT_SERVICE'
WHERE Value = 'LOADMANAGEMENT_SERVICE_STATUS'
AND Name = 'WarningType';

UPDATE SmartNotificationEventParam
SET Value = 'YUKON_PORT_CONTROL_SERVICE'
WHERE Value = 'PORTER_SERVICE_STATUS'
AND Name = 'WarningType';

UPDATE SmartNotificationEventParam
SET Value = 'YUKON_MAC_SCHEDULER_SERVICE'
WHERE Value = 'MACS_SERVICE_STATUS'
AND Name = 'WarningType';

UPDATE SmartNotificationEventParam
SET Value = 'YUKON_FOREIGN_DATA_SERVICE'
WHERE Value = 'FDR_SERVICE_STATUS'
AND Name = 'WarningType';

UPDATE SmartNotificationEventParam
SET Value = 'YUKON_CALC_LOGIC_SERVICE'
WHERE Value = 'CALC_SERVICE_STATUS'
AND Name = 'WarningType';

UPDATE SmartNotificationEventParam
SET Value = 'YUKON_REAL_TIME_SCAN_SERVICE'
WHERE Value = 'SCANNER_SERVICE_STATUS'
AND Name = 'WarningType';

UPDATE SmartNotificationEventParam
SET Value = 'YUKON_NETWORK_MANAGER'
WHERE Value = 'NETWORK_MANAGER_STATUS'
AND Name = 'WarningType';

INSERT INTO DBUpdates VALUES ('YUK-18744', '7.2.0', SYSDATE);
/* @end YUK-18744 */

/* @start YUK-18870 */
CREATE TABLE LMNestLoadShapingGear  (
    GearId               NUMBER                          NOT NULL,
    PreparationOption    VARCHAR2(20)                    NOT NULL,
    PeakOption           VARCHAR2(20)                    NOT NULL,
    PostPeakOption       VARCHAR2(20)                    NOT NULL,
    CONSTRAINT PK_LMNestLoadShapingGear PRIMARY KEY (GearId)
);

ALTER TABLE LMNestLoadShapingGear
    ADD CONSTRAINT FK_NLSGear_LMProgramDirectGear FOREIGN KEY (GearId)
        REFERENCES LMProgramDirectGear (GearID)
            ON DELETE CASCADE;

INSERT INTO DBUpdates VALUES ('YUK-18870', '7.2.0', SYSDATE);
/* @start YUK-18870 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.2', '13-AUG-2018', 'Latest Update', 0, SYSDATE);*/
