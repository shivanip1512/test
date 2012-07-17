/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 

/* Start YUK-11144 */
DECLARE @newDevConfigId NUMERIC;
SELECT @newDevConfigId = MAX(DeviceConfigurationID)+1 FROM DEVICECONFIGURATION;

INSERT INTO DEVICECONFIGURATION VALUES (@newDevConfigId, 'Default DNP Configuration', 'DNP')

DECLARE @internalRetries NUMERIC;
DECLARE @localTimeInt NUMERIC;
DECLARE @enableTimeSyncs varchar(60);

/* @start-cparm YUKON_DNP_INTERNAL_RETRIES */
SELECT @internalRetries = 2;
/* @end-cparm */

/* @start-cparm YUKON_DNP_LOCALTIME */
SELECT @localTimeInt = 0;
/* @end-cparm */

/* @start-cparm YUKON_DNP_TIMESYNCS */
SELECT @enableTimeSyncs = 'false';
/* @end-cparm */

DECLARE @localTime varchar(60);

IF @localTimeInt = 0
    SET @localTime = 'false'
ELSE
    SET @localTime = 'true'

DECLARE @newItemId NUMERIC;
SELECT @newItemId = MAX(DeviceConfigurationItemId)+1 FROM DEVICECONFIGURATIONITEM;

INSERT INTO DEVICECONFIGURATIONITEM VALUES (@newItemId,   @newDevConfigId, 'Internal Retries', @internalRetries);
SELECT @newItemId = MAX(DeviceConfigurationItemId)+1 FROM DEVICECONFIGURATIONITEM;

INSERT INTO DEVICECONFIGURATIONITEM VALUES (@newItemId, @newDevConfigId, 'Omit Time Request', 'false');
SELECT @newItemId = MAX(DeviceConfigurationItemId)+1 FROM DEVICECONFIGURATIONITEM;

INSERT INTO DEVICECONFIGURATIONITEM VALUES (@newItemId, @newDevConfigId, 'Enable DNP Timesyncs', @enableTimeSyncs);
SELECT @newItemId = MAX(DeviceConfigurationItemId)+1 FROM DEVICECONFIGURATIONITEM;

INSERT INTO DEVICECONFIGURATIONITEM VALUES (@newItemId, @newDevConfigId, 'Local Time', @localTime);
SELECT @newItemId = MAX(DeviceConfigurationItemId)+1 FROM DEVICECONFIGURATIONITEM;

INSERT INTO DEVICECONFIGURATIONITEM VALUES (@newItemId, @newDevConfigId, 'Enable Unsolicited Messages', 'true');
SELECT @newItemId = MAX(DeviceConfigurationItemId)+1 FROM DEVICECONFIGURATIONITEM;

/*
 * Find all DNP devices in the database and assign them to the new device configuration.
 */

DECLARE @count NUMERIC = (SELECT COUNT(*) 
                          FROM YukonPAObject 
                          WHERE (Type LIKE 'CBC 702%' OR Type LIKE 'CBC 802%' OR TYPE LIKE '%DNP%'))
                          
DECLARE @deviceID NUMERIC = (SELECT MIN(PAObjectID) 
                             FROM YukonPAObject 
                             WHERE (Type LIKE 'CBC 702%' OR Type LIKE 'CBC 802%' OR TYPE LIKE '%DNP%'))

WHILE(@count > 0)
BEGIN
    INSERT INTO DEVICECONFIGURATIONDEVICEMAP VALUES (@deviceID, @newDevConfigId)
    SET @deviceID = (SELECT MIN(PAObjectID) 
                     FROM YukonPAObject 
                     WHERE (Type LIKE 'CBC 702%' OR Type LIKE 'CBC 802%' OR TYPE LIKE '%DNP%') AND
                            PAObjectID > @deviceID);
    SET @count -= 1;
END
/* End YUK-11144 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 