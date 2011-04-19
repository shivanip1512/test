/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 

/* Start YUK-9557 */
UPDATE CapControlStrategy 
SET ControlUnits = 'MULTI_VOLT_VAR' 
WHERE ControlUnits = 'Multi Volt Var'; 
/* End YUK-9557 */

/* Start YUK-9565 */
UPDATE State 
SET ForegroundColor = 3 
WHERE StateGroupId = -14 
  AND RawState = 1; 
UPDATE State 
SET ForegroundColor = 1 
WHERE StateGroupId = -14 
  AND RawState = 2;
/* End YUK-9565 */

/* Start YUK-9575 */
ALTER TABLE ServiceCompanyDesignationCode 
    DROP CONSTRAINT FK_SRVCODSGNTNCODES_SRVCO;
GO

ALTER TABLE ServiceCompanyDesignationCode 
    ADD CONSTRAINT FK_ServCompDesCode_ServComp FOREIGN KEY (ServiceCompanyId) 
        REFERENCES ServiceCompany (CompanyId) 
            ON DELETE CASCADE;
GO
/* End YUK-9575 */

/* Start YUK-9603 */
INSERT INTO YukonRoleProperty VALUES (-1120, -2, 'Allow Designation Codes', 'false', 'Toggles on or off the regional (usually zip) code option for service companies.') 

DELETE FROM YukonUserRole 
WHERE RolePropertyId = -20008; 
DELETE FROM YukonGroupRole 
WHERE RolePropertyId = -20008; 
DELETE FROM YukonRoleProperty 
WHERE RolePropertyId = -20008;
/* End YUK-9603 */

/* Start YUK-9595 */
INSERT INTO Command VALUES (-187, 'getconfig ied dnp address', 'Read DNP address configuration', 'MCT-470');
INSERT INTO Command VALUES (-188, 'putconfig emetcon ied dnp address master ?''Master address'' outstation ?''Outstation address''', 'Write DNP address configuration', 'MCT-470');

INSERT INTO DeviceTypeCommand VALUES (-814, -187, 'MCT-470', 34, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-815, -188, 'MCT-470', 35, 'Y', -1);
/* End YUK-9595 */

/* Start YUK-9646 */
/* @error ignore-begin */
ALTER TABLE CCMonitorBankList
    DROP CONSTRAINT FK_CCMONBNKLIST_BNKID;
ALTER TABLE CCMonitorBankList
    DROP CONSTRAINT FK_CCMONBNKLST_PTID;
GO

ALTER TABLE CCMonitorBankList
    ADD CONSTRAINT FK_CCMonBankList_CapBank FOREIGN KEY (BankId)
        REFERENCES CapBank (DeviceId)
            ON DELETE CASCADE;
ALTER TABLE CCMonitorBankList
    ADD CONSTRAINT FK_CCMonBankList_Point FOREIGN KEY (PointId)
        REFERENCES Point (PointId)
            ON DELETE CASCADE;
GO
/* @error ignore-end */
/* End YUK-9646 */

/* Start YUK-9648 */
ALTER TABLE ZBDevice 
ADD MacAddress VARCHAR(255); 
GO

UPDATE ZBDevice
SET MacAddress = ' ';
GO

ALTER TABLE ZBDevice 
ALTER COLUMN MacAddress VARCHAR(255) NOT NULL; 
GO

EXEC SP_RENAME 'ZBDevice', 'ZBEndPoint';
EXEC SP_RENAME 'PK_ZBDevice', 'PK_ZBEndPoint', 'OBJECT';
EXEC SP_RENAME 'FK_ZBDevice_Device', 'FK_ZBEndPoint_Device', 'OBJECT';
EXEC SP_RENAME 'FK_ZBGateDeviceMap_ZBDevice', 'FK_ZBGateDeviceMap_ZBEndPoint', 'OBJECT';
/* End YUK-9648 */

/* Start YUK-9669 */
UPDATE State 
SET Text = 'Decommissioned' 
WHERE StateGroupId = -13 
  AND RawState = 1;
/* End YUK-9669 */

/* Start YUK-9676 */
INSERT INTO YukonServices VALUES (16, 'SepControlMessageListener', 'classpath:com/cannontech/services/sepControlMessageListener/sepControlMessageListenerContext.xml', 'ServiceManager'); 
/* End YUK-9676 */

/* Start YUK-9702 */
UPDATE YukonPaobject
SET PaoType = 'ZigBee Utility Pro'
WHERE PaoType = 'Zigbee Utility Pro';
/* End YUK-9702 */

/* Start YUK-9705 */
/* @error ignore-begin */
ALTER TABLE OptOutEvent DROP CONSTRAINT FK_OptOutEvent_CustAcct;
GO

ALTER TABLE OptOutEvent
    ALTER COLUMN CustomerAccountId NUMERIC NULL;
GO

ALTER TABLE OptOutEvent 
    ADD CONSTRAINT FK_OptOutEvent_CustAcct FOREIGN KEY(CustomerAccountId) 
        REFERENCES CustomerAccount (AccountId) 
            ON DELETE SET NULL;
/* @error ignore-end */
/* End YUK-9705 */

/* Start YUK-9647 */
IF EXISTS (SELECT 1 
           FROM sysobjects 
           WHERE xtype='u' AND name='DynamicPAOStatisticsHistory')
BEGIN
    EXEC SP_RENAME 'DynamicPAOStatistics', 'DynamicPAOStatisticsOld';

    ALTER TABLE DynamicPAOStatisticsOld
        DROP CONSTRAINT PK_DynamicPAOStatistics;
    
    UPDATE DynamicPAOStatisticsOld
    SET StatisticType='Monthly'
    WHERE StatisticType='LastMonth';
    
    CREATE TABLE DynamicPAOStatistics ( 
       DynamicPAOStatisticsId   NUMERIC          NOT NULL, 
       PAObjectId               NUMERIC          NOT NULL, 
       StatisticType            VARCHAR(16)      NOT NULL, 
       StartDateTime            DATETIME         NOT NULL, 
       Requests                 NUMERIC          NOT NULL, 
       Attempts                 NUMERIC          NOT NULL, 
       Completions              NUMERIC          NOT NULL, 
       CommErrors               NUMERIC          NOT NULL, 
       ProtocolErrors           NUMERIC          NOT NULL, 
       SystemErrors             NUMERIC          NOT NULL, 
       CONSTRAINT PK_DynPAOStat PRIMARY KEY (DynamicPAOStatisticsId) 
    );
    
    ALTER TABLE DynamicPaoStatistics 
        ADD CONSTRAINT FK_DynPAOStat_PAO FOREIGN KEY (PAObjectId) 
            REFERENCES YukonPAObject (PAObjectId) 
                ON DELETE CASCADE;
    
    CREATE UNIQUE INDEX Indx_DynPAOStat_PId_ST_SD_UNQ ON DynamicPAOStatistics (
       PAOBjectId ASC,
       StatisticType ASC,
       StartDateTime ASC
    );
    
    INSERT INTO DynamicPaoStatistics 
    SELECT ROW_NUMBER() OVER (ORDER BY StartDateTime, PAObjectId, StatisticType), 
            PAObjectId, statistictype, startdatetime, requests, attempts, completions, 
            commerrors, protocolerrors, systemerrors 
    FROM DynamicPAOStatisticsOld 
    WHERE StatisticType IN ('Lifetime', 'Monthly', 'Daily');
    
    /* @start-block */
    DECLARE 
        @maxId NUMERIC; 
    BEGIN
        SELECT @maxId = MAX(DynamicPaoStatisticsId)
        FROM DynamicPaoStatistics; 

        INSERT INTO DynamicPAOStatistics 
        SELECT @maxId + ROW_NUMBER() OVER (ORDER BY PAObjectId, DateOffset), PAObjectId, 
                'Daily', DATEADD(Day, DateOffset, '1970-01-01') Requests, Attempts, 
                Completions, CommErrors, ProtocolErrors, SystemErrors 
        FROM DynamicPAOStatisticsHistory; 
    END
    /* @end-block */ 
    
    DROP TABLE DynamicPAOStatisticsOld; 
    DROP TABLE DynamicPAOStatisticsHistory;
END
ELSE
BEGIN
    SELECT 'YUK-9647 has been run in a previous update scirpt.  Skipping this update scripte.';
END 
/* End YUK-9647 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
