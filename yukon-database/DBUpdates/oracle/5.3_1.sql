/******************************************/ 
/**** Oracle DBupdates                 ****/ 
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

ALTER TABLE ServiceCompanyDesignationCode 
    ADD CONSTRAINT FK_ServCompDesCode_ServComp FOREIGN KEY (ServiceCompanyId) 
        REFERENCES ServiceCompany (CompanyId) 
            ON DELETE CASCADE;
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

ALTER TABLE CCMonitorBankList
    ADD CONSTRAINT FK_CCMonBankList_CapBank FOREIGN KEY (BankId)
        REFERENCES CapBank (DeviceId)
            ON DELETE CASCADE;
ALTER TABLE CCMonitorBankList
    ADD CONSTRAINT FK_CCMonBankList_Point FOREIGN KEY (PointId)
        REFERENCES Point (PointId)
            ON DELETE CASCADE;
/* @error ignore-end */
/* End YUK-9646 */

/* Start YUK-9648 */
ALTER TABLE ZBDevice 
ADD MacAddress VARCHAR2(255); 

UPDATE ZBDevice
SET MacAddress = ' ';

ALTER TABLE ZBDevice 
MODIFY MacAddress VARCHAR2(255) NOT NULL; 

ALTER TABLE ZBDevice RENAME TO ZBEndPoint;

ALTER TABLE ZBEndPoint RENAME CONSTRAINT PK_ZBDevice to PK_ZBEndPoint;
ALTER INDEX PK_ZBDevice RENAME TO PK_ZBEndPoint;

ALTER TABLE ZBEndPoint RENAME CONSTRAINT FK_ZBDevice_Device to FK_ZBEndPoint_Device;
ALTER TABLE ZBGatewayToDeviceMapping RENAME CONSTRAINT FK_ZBGateDeviceMap_ZBDevice to FK_ZBGateDeviceMap_ZBEndPoint;
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

ALTER TABLE OptOutEvent
    MODIFY CustomerAccountId NUMBER NULL;

ALTER TABLE OptOutEvent 
    ADD CONSTRAINT FK_OptOutEvent_CustAcct FOREIGN KEY(CustomerAccountId) 
        REFERENCES CustomerAccount (AccountId) 
            ON DELETE SET NULL;
/* @error ignore-end */
/* End YUK-9705 */

/* Start YUK-9647 */
SET SERVEROUTPUT ON;  

DECLARE 
    E_TAB_NOT_EXISTS EXCEPTION;
    PRAGMA EXCEPTION_INIT(E_TAB_NOT_EXISTS, -942);
    
BEGIN
    --Test for DynamicPaoStatisticsHistory table existance
    EXECUTE IMMEDIATE 'SELECT COUNT(*) FROM DynamicPaoStatisticsHistory';
        EXECUTE IMMEDIATE 'ALTER TABLE DynamicPAOStatistics RENAME TO DynamicPAOStatisticsOld'; 
         
        EXECUTE IMMEDIATE 'ALTER TABLE DynamicPAOStatisticsOld 
                                DROP CONSTRAINT PK_DynamicPAOStatistics'; 
         
        /* @error ignore-begin */ 
        EXECUTE IMMEDIATE 'DROP INDEX PK_DynamicPAOStatistics'; 
        /* @error ignore-end */ 
             
        EXECUTE IMMEDIATE 'UPDATE DynamicPAOStatisticsOld 
        SET StatisticType = ''Monthly'' 
        WHERE StatisticType = ''LastMonth'''; 
         
        EXECUTE IMMEDIATE  
        'CREATE TABLE DynamicPAOStatistics (  
           DynamicPAOStatisticsId          NUMBER                 NOT NULL,  
           PAObjectId                      NUMBER                 NOT NULL,  
           StatisticType                   VARCHAR2(16)           NOT NULL,  
           StartDateTime                   DATE                  NOT NULL,  
           Requests                        NUMBER                 NOT NULL,  
           Attempts                        NUMBER                 NOT NULL,  
           Completions                     NUMBER                 NOT NULL,  
           CommErrors                      NUMBER                 NOT NULL,  
           ProtocolErrors                  NUMBER                 NOT NULL,  
           SystemErrors                    NUMBER                 NOT NULL,  
           CONSTRAINT PK_DynPAOStat PRIMARY KEY (DynamicPAOStatisticsId)  
        )'; 
         
        EXECUTE IMMEDIATE  
        'ALTER TABLE DynamicPaoStatistics  
            ADD CONSTRAINT FK_DynPAOStat_PAO FOREIGN KEY (PAObjectId)  
                REFERENCES YukonPAObject (PAObjectId)  
                    ON DELETE CASCADE';  
         
        EXECUTE IMMEDIATE  
        'CREATE UNIQUE INDEX Indx_DynPAOStat_PId_ST_SD_UNQ ON DynamicPAOStatistics ( 
           PAOBjectId ASC, 
           StatisticType ASC, 
           StartDateTime ASC 
        )'; 
         

        EXECUTE IMMEDIATE 'INSERT INTO DynamicPAOStatistics  
        SELECT ROW_NUMBER() OVER (ORDER BY StartDateTime, PAObjectId, StatisticType),  
                PAObjectId, StatisticType, StartDateTime, Requests, Attempts, Completions,  
                CommErrors, ProtocolErrors, SystemErrors  
        FROM DynamicPAOStatisticsOld  
        WHERE StatisticType IN (''Lifetime'', ''Monthly'', ''Daily'')'; 

      
                
        /* @start-block */  
        EXECUTE IMMEDIATE 'DECLARE  
            maxId number;  
        BEGIN  
            SELECT MAX(DynamicPaoStatisticsId) INTO maxId 
            FROM DynamicPaoStatistics;  
         
            INSERT INTO DynamicPAOStatistics  
            SELECT maxId + ROW_NUMBER() OVER (ORDER BY PAObjectId, DateOffset), PAObjectId,  
                    ''Daily'', TO_DATE(''1970-01-01'', ''yyyy-mm-dd'') + DateOffset, Requests, Attempts,  
                    Completions, CommErrors, ProtocolErrors, SystemErrors  
            FROM DynamicPAOStatisticsHistory;  
        END;'; 
        /* @end-block */  
        

        EXECUTE IMMEDIATE 'DROP TABLE DynamicPaoStatisticsOld CASCADE CONSTRAINTS';  
        EXECUTE IMMEDIATE 'DROP TABLE DynamicPaoStatisticsHistory CASCADE CONSTRAINTS'; 

    EXCEPTION
        WHEN E_TAB_NOT_EXISTS THEN
        DBMS_OUTPUT.PUT_LINE('DON’T RUN SQL AGAIN -> : Table has already been removed');
                            
END;
/
/* End YUK-9647 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
