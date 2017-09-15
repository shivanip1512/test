/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-17045 */
INSERT INTO YukonListEntry 
SELECT (SELECT MAX(EntryId) + 1 
        FROM   YukonListEntry 
        WHERE  EntryId < 10000), 
       1005, 
       0, 
       'LCR-6700(RFN)', 
       1337 
FROM   dual 
WHERE  NOT EXISTS (SELECT * 
                   FROM   YukonListEntry 
                   WHERE  EntryText = 'LCR-6700(RFN)'); 
/* End YUK-17045 */

/* Start YUK-17051 */
ALTER TABLE DISPLAY2WAYDATA DROP CONSTRAINT FK_Display2WayData_Display;
ALTER TABLE DISPLAYCOLUMNS DROP CONSTRAINT FK_DisplayColumns_Display;
ALTER TABLE TemplateDisplay DROP CONSTRAINT FK_TemplateDisplay_DISPLAY;

ALTER TABLE DISPLAY2WAYDATA
   ADD CONSTRAINT FK_Display2WayData_Display FOREIGN KEY (DISPLAYNUM)
      REFERENCES DISPLAY (DISPLAYNUM)
      ON DELETE CASCADE;

ALTER TABLE DISPLAYCOLUMNS
   ADD CONSTRAINT FK_DisplayColumns_Display FOREIGN KEY (DISPLAYNUM)
      REFERENCES DISPLAY (DISPLAYNUM)
      ON DELETE CASCADE;

ALTER TABLE TemplateDisplay
   ADD CONSTRAINT FK_TemplateDisplay_DISPLAY FOREIGN KEY (DisplayNum)
      REFERENCES DISPLAY (DISPLAYNUM)
      ON DELETE CASCADE;
/* End YUK-17051 */

/* Start YUK-17096 */
DROP INDEX INDX_CCEventLog_PointId;
/* End YUK-17096 */

/* Start YUK-17113 */
CREATE TABLE DBUpdates  (
   UpdateId             VARCHAR2(50)                    NOT NULL,
   Version              VARCHAR2(10),
   InstallDate          DATE,
   CONSTRAINT PK_DBUPDATES PRIMARY KEY (UpdateId)
);
/* End YUK-17113 */

/* Start YUK-17066 */
UPDATE 
(SELECT PU.decimalplaces FROM POINT P 
    JOIN YukonPAObject YPO ON P.PAObjectID = YPO.PAObjectID
    JOIN POINTUNIT PU ON PU.POINTID = P.POINTID 
WHERE YPO.Type IN ('RFW-201', 'RFW-205') AND P.POINTTYPE = 'Analog' 
    AND P.POINTOFFSET = 6 AND PU.DECIMALPLACES = 3) t 
SET t.decimalplaces = 0;
/* End YUK-17066 */

/* Start YUK-17159 */
UPDATE ExtraPaoPointAssignment
SET Attribute = 'VOLTAGE'
WHERE Attribute = 'VOLTAGE_Y';

UPDATE ExtraPaoPointAssignment
SET Attribute = 'SOURCE_VOLTAGE'
WHERE Attribute = 'VOLTAGE_X';

CREATE INDEX INDX_EPPA_PointId ON ExtraPaoPointAssignment (
    PointId ASC
);
/* End YUK-17159 */

/* Start YUK-17166 */
UPDATE YukonGroupRole 
SET Value = 'CREATE' 
WHERE RolePropertyID = -21200;

UPDATE YukonRoleProperty
SET 
    KeyName = 'MACS Scripts', 
    DefaultValue = 'UPDATE', 
    Description = 'Controls the ability to view, start/stop, enable/disable, edit, create, delete for MACS Script.'
WHERE RolePropertyID = -21200;
/* End YUK-17166 */

/* Start YUK-17126 */
CREATE TABLE SmartNotificationSub  (
   SubscriptionId       NUMBER                          NOT NULL,
   UserId               NUMBER                          NOT NULL,
   Type                 VARCHAR2(30)                    NOT NULL,
   Media                VARCHAR2(30)                    NOT NULL,
   Frequency            VARCHAR2(30)                    NOT NULL,
   Verbosity            VARCHAR2(30)                    NOT NULL,
   Recipient            VARCHAR2(100)                   NOT NULL,
   CONSTRAINT PK_SmartNotificationSub PRIMARY KEY (SubscriptionId)
);

CREATE TABLE SmartNotificationSubParam  (
   SubscriptionId       NUMBER                          NOT NULL,
   Name                 VARCHAR2(30)                    NOT NULL,
   Value                VARCHAR2(100)                   NOT NULL,
   CONSTRAINT PK_SmartNotificationSubParam PRIMARY KEY (SubscriptionId, Name, Value)
);

ALTER TABLE SmartNotificationSub
   ADD CONSTRAINT FK_SmartNotifSub_YukonUser FOREIGN KEY (UserId)
      REFERENCES YukonUser (UserID)
      ON DELETE CASCADE;

ALTER TABLE SmartNotificationSubParam
   ADD CONSTRAINT FK_SmartNotifSP_SmartNotifS FOREIGN KEY (SubscriptionId)
      REFERENCES SmartNotificationSub (SubscriptionId)
      ON DELETE CASCADE;

CREATE INDEX INDX_SmartNotifSub_UserId_Type ON SmartNotificationSub (
   UserId ASC,
   Type ASC
);
/* End YUK-17126 */

/* Start YUK-17120 */
CREATE TABLE SmartNotificationEvent  (
    EventId         NUMBER              NOT NULL,
    Type            VARCHAR2(30)        NOT NULL,
    Timestamp       DATE                NOT NULL,
    ProcessedTime   DATE,
    CONSTRAINT PK_SmartNotificationEvent PRIMARY KEY (EventId)
);

CREATE TABLE SmartNotificationEventParam  (
    EventId         NUMBER              NOT NULL,
    Name            VARCHAR2(30)        NOT NULL,
    Value           VARCHAR2(100)       NOT NULL,
    CONSTRAINT PK_SmartNotificationEventParam PRIMARY KEY (EventId, Name, Value)
);      

ALTER TABLE SmartNotificationEventParam
    ADD CONSTRAINT FK_SmartNotifEP_SmartNotifE FOREIGN KEY (EventId)
        REFERENCES SmartNotificationEvent (EventId)
        ON DELETE CASCADE;
/* End YUK-17120 */

/* Start YUK-17209 */
/* @start-block */
DECLARE
    v_newConfigCategoryId       NUMBER;
    v_newConfigCategoryItemId   NUMBER;
    v_newConfigDeviceType       NUMBER;

BEGIN
    SELECT MAX(DeviceConfigCategoryId) + 1      INTO v_newConfigCategoryId      FROM DeviceConfigCategory;
    SELECT MAX(DeviceConfigCategoryItemId) + 1  INTO v_newConfigCategoryItemId  FROM DeviceConfigCategoryItem;
    SELECT MAX(DeviceConfigDeviceTypeId) + 1    INTO v_newConfigDeviceType      FROM DeviceConfigDeviceTypes;

    INSERT INTO DeviceConfigCategory        VALUES (v_newConfigCategoryId, 'cbcAttributeMapping', 'Default CBC Attribute Mapping', NULL);
    INSERT INTO DeviceConfigCategoryItem    VALUES (v_newConfigCategoryItemId, v_newConfigCategoryId, 'attributeMappings', '0');
    INSERT INTO DeviceConfigCategoryMap     VALUES (-1, v_newConfigCategoryId); /* -1 is the ID for the default DNP Configuration */
    INSERT INTO DeviceConfigDeviceTypes     VALUES (v_newConfigDeviceType, -1, 'CBC DNP Logical');
END;
/
/* @end-block */
/* End YUK-17209 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.0', '01-AUG-2017', 'Latest Update', 0, SYSDATE);*/
