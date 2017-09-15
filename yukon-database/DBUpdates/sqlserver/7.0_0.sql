/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-17045 */
IF( (SELECT COUNT(*) 
     FROM   YukonListEntry 
     WHERE  EntryText = 'LCR-6700(RFN)') = 0 ) 
  INSERT INTO YukonListEntry 
  VALUES      ((SELECT MAX(EntryId) + 1 
                FROM   YukonListEntry 
                WHERE  EntryId < 10000), 
               1005, 
               0, 
               'LCR-6700(RFN)', 
               1337); 
/* End YUK-17045 */

/* Start YUK-17051 */
ALTER TABLE DISPLAY2WAYDATA DROP CONSTRAINT FK_Display2WayData_Display;
ALTER TABLE DISPLAYCOLUMNS DROP CONSTRAINT FK_DisplayColumns_Display;
ALTER TABLE TemplateDisplay DROP CONSTRAINT FK_TemplateDisplay_DISPLAY;
GO

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
GO
/* End YUK-17051 */

/* Start YUK-17096 */
DROP INDEX INDX_CCEventLog_PointId ON CCEventLog;
/* End YUK-17096 */

/* Start YUK-17113 */
CREATE TABLE DBUpdates (
   UpdateId             VARCHAR(50)          NOT NULL,
   Version              VARCHAR(10)           NULL,
   InstallDate          DATETIME             NULL,
   CONSTRAINT PK_DBUPDATES PRIMARY KEY (UpdateId)
);
GO
/* End YUK-17113 */

/* Start YUK-17066 */
UPDATE PU SET PU.DECIMALPLACES = 0 
FROM POINT P 
    JOIN YukonPAObject YPO ON P.PAObjectID = YPO.PAObjectID
	JOIN POINTUNIT PU ON PU.POINTID = P.POINTID 
WHERE YPO.Type IN ('RFW-201', 'RFW-205') AND P.POINTTYPE = 'Analog' 
    AND P.POINTOFFSET = 6 AND PU.DECIMALPLACES = 3;
GO
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
GO
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
CREATE TABLE SmartNotificationSub (
   SubscriptionId       NUMERIC              NOT NULL,
   UserId               NUMERIC              NOT NULL,
   Type                 VARCHAR(30)          NOT NULL,
   Media                VARCHAR(30)          NOT NULL,
   Frequency            VARCHAR(30)          NOT NULL,
   Verbosity            VARCHAR(30)          NOT NULL,
   Recipient            VARCHAR(100)         NOT NULL,
   CONSTRAINT PK_SmartNotificationSub PRIMARY KEY (SubscriptionId)
);

CREATE TABLE SmartNotificationSubParam (
   SubscriptionId       NUMERIC              NOT NULL,
   Name                 VARCHAR(30)          NOT NULL,
   Value                VARCHAR(100)         NOT NULL,
   CONSTRAINT PK_SmartNotificationSubParam PRIMARY KEY (SubscriptionId, Name, Value)
);
GO

ALTER TABLE SmartNotificationSub
   ADD CONSTRAINT FK_SmartNotifSub_YukonUser FOREIGN KEY (UserId)
      REFERENCES YukonUser (UserID)
         ON DELETE CASCADE;

ALTER TABLE SmartNotificationSubParam
   ADD CONSTRAINT FK_SmartNotifSP_SmartNotifS FOREIGN KEY (SubscriptionId)
      REFERENCES SmartNotificationSub (SubscriptionId)
         ON DELETE CASCADE;
GO

CREATE INDEX INDX_SmartNotifSub_UserId_Type ON SmartNotificationSub (
   UserId ASC,
   Type ASC
);
GO
/* End YUK-17126 */

/* Start YUK-17120 */
CREATE TABLE SmartNotificationEvent (
    EventId         NUMERIC         NOT NULL,
    Type            VARCHAR(30)     NOT NULL,
    Timestamp       DATETIME        NOT NULL,
    ProcessedTime   DATETIME        NULL,
    CONSTRAINT PK_SmartNotificationEvent PRIMARY KEY (EventId)
);

CREATE TABLE SmartNotificationEventParam (
    EventId          NUMERIC         NOT NULL,
    Name             VARCHAR(30)     NOT NULL,
    Value            VARCHAR(100)    NOT NULL,
    CONSTRAINT PK_SmartNotificationEventParam PRIMARY KEY (EventId, Name, Value)
);
GO

ALTER TABLE SmartNotificationEventParam
    ADD CONSTRAINT FK_SmartNotifEP_SmartNotifE FOREIGN KEY (EventId)
        REFERENCES SmartNotificationEvent (EventId)
            ON DELETE CASCADE;
GO
/* End YUK-17120 */

/* Start YUK-17209 */
/* @start-block */
DECLARE
    @newConfigCategoryId        NUMERIC,
    @newConfigCategoryItemId    NUMERIC,
    @newConfigDeviceType        NUMERIC

BEGIN
    SET @newConfigCategoryId =      (SELECT MAX(DeviceConfigCategoryId) + 1 FROM DeviceConfigCategory);
    SET @newConfigCategoryItemId =  (SELECT MAX(DeviceConfigCategoryItemId) + 1 FROM DeviceConfigCategoryItem);
    SET @newConfigDeviceType =      (SELECT MAX(DeviceConfigDeviceTypeId) + 1 FROM DeviceConfigDeviceTypes);

    INSERT INTO DeviceConfigCategory        VALUES (@newConfigCategoryId, 'cbcAttributeMapping', 'Default CBC Attribute Mapping', NULL);
    INSERT INTO DeviceConfigCategoryItem    VALUES (@newConfigCategoryItemId, @newConfigCategoryId, 'attributeMappings', '0');
    INSERT INTO DeviceConfigCategoryMap     VALUES (-1, @newConfigCategoryId); /* -1 is the ID for the default DNP Configuration */
    INSERT INTO DeviceConfigDeviceTypes     VALUES (@newConfigDeviceType, -1, 'CBC DNP Logical');
END;
/* @end-block */
/* End YUK-17209 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.0', '01-AUG-2017', 'Latest Update', 0, GETDATE());*/