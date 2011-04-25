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
INSERT INTO YukonRoleProperty VALUES (-1120, -2, 'Allow Designation Codes', 'false', 'Toggles on or off the regional (usually zip) code option for service companies.');

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
UPDATE YukonPAObject
SET Type = 'ZigBee Utility Pro'
WHERE Type = 'Zigbee Utility Pro';
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
/* @start-block */  
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
        DBMS_OUTPUT.PUT_LINE('YUK-9647 has been run in a previous update script, 5.2_7.sql.  Skipping this update script.');
                            
END;
/
/* @end-block */  
/* End YUK-9647 */

/* Start YUK-9723 */ 
DELETE FROM DeviceTapPagingSettings 
WHERE DeviceId IN (SELECT PAObjectId 
                    FROM YukonPAObject 
                    WHERE type='TNPP TERMINAL'); 
/* End YUK-9723 */ 

/* Start YUK-9734 */ 
UPDATE YukonPAObject SET Type = 'CCU-710A' WHERE LOWER(Type) = LOWER('CCU-710A'); 
UPDATE YukonPAObject SET Type = 'CCU-711' WHERE LOWER(Type) = LOWER('CCU-711'); 
UPDATE YukonPAObject SET Type = 'TCU-5000' WHERE LOWER(Type) = LOWER('TCU-5000'); 
UPDATE YukonPAObject SET Type = 'TCU-5500' WHERE LOWER(Type) = LOWER('TCU-5500'); 
UPDATE YukonPAObject SET Type = 'LCU-415' WHERE LOWER(Type) = LOWER('LCU-415'); 
UPDATE YukonPAObject SET Type = 'LCU-LG' WHERE LOWER(Type) = LOWER('LCU-LG'); 
UPDATE YukonPAObject SET Type = 'LCU-EASTRIVER' WHERE LOWER(Type) = LOWER('LCU-EASTRIVER'); 
UPDATE YukonPAObject SET Type = 'LCU-T3026' WHERE LOWER(Type) = LOWER('LCU-T3026'); 
UPDATE YukonPAObject SET Type = 'ALPHA POWER PLUS' WHERE LOWER(Type) = LOWER('ALPHA POWER PLUS'); 
UPDATE YukonPAObject SET Type = 'FULCRUM' WHERE LOWER(Type) = LOWER('FULCRUM'); 
UPDATE YukonPAObject SET Type = 'VECTRON' WHERE LOWER(Type) = LOWER('VECTRON'); 
UPDATE YukonPAObject SET Type = 'LANDIS-GYR S4' WHERE LOWER(Type) = LOWER('LANDIS-GYR S4'); 
UPDATE YukonPAObject SET Type = 'DAVIS WEATHER' WHERE LOWER(Type) = LOWER('DAVIS WEATHER'); 
UPDATE YukonPAObject SET Type = 'ALPHA A1' WHERE LOWER(Type) = LOWER('ALPHA A1'); 
UPDATE YukonPAObject SET Type = 'DR-87' WHERE LOWER(Type) = LOWER('DR-87'); 
UPDATE YukonPAObject SET Type = 'QUANTUM' WHERE LOWER(Type) = LOWER('QUANTUM'); 
UPDATE YukonPAObject SET Type = 'SIXNET' WHERE LOWER(Type) = LOWER('SIXNET'); 
UPDATE YukonPAObject SET Type = 'REPEATER 800' WHERE LOWER(Type) = LOWER('REPEATER 800'); 
UPDATE YukonPAObject SET Type = 'REPEATER 850' WHERE LOWER(Type) = LOWER('REPEATER 850'); 
UPDATE YukonPAObject SET Type = 'MCT-310' WHERE LOWER(Type) = LOWER('MCT-310'); 
UPDATE YukonPAObject SET Type = 'MCT-318' WHERE LOWER(Type) = LOWER('MCT-318'); 
UPDATE YukonPAObject SET Type = 'MCT-360' WHERE LOWER(Type) = LOWER('MCT-360'); 
UPDATE YukonPAObject SET Type = 'MCT-370' WHERE LOWER(Type) = LOWER('MCT-370'); 
UPDATE YukonPAObject SET Type = 'MCT-240' WHERE LOWER(Type) = LOWER('MCT-240'); 
UPDATE YukonPAObject SET Type = 'MCT-248' WHERE LOWER(Type) = LOWER('MCT-248'); 
UPDATE YukonPAObject SET Type = 'MCT-250' WHERE LOWER(Type) = LOWER('MCT-250'); 
UPDATE YukonPAObject SET Type = 'MCT-210' WHERE LOWER(Type) = LOWER('MCT-210'); 
UPDATE YukonPAObject SET Type = 'REPEATER' WHERE LOWER(Type) = LOWER('REPEATER'); 
UPDATE YukonPAObject SET Type = 'LMT-2' WHERE LOWER(Type) = LOWER('LMT-2'); 
UPDATE YukonPAObject SET Type = 'RTU-ILEX' WHERE LOWER(Type) = LOWER('RTU-ILEX'); 
UPDATE YukonPAObject SET Type = 'RTU-WELCO' WHERE LOWER(Type) = LOWER('RTU-WELCO'); 
UPDATE YukonPAObject SET Type = 'DCT-501' WHERE LOWER(Type) = LOWER('DCT-501'); 
UPDATE YukonPAObject SET Type = 'RTU-DNP' WHERE LOWER(Type) = LOWER('RTU-DNP'); 
UPDATE YukonPAObject SET Type = 'TAP TERMINAL' WHERE LOWER(Type) = LOWER('TAP TERMINAL'); 
UPDATE YukonPAObject SET Type = 'TNPP TERMINAL' WHERE LOWER(Type) = LOWER('TNPP TERMINAL'); 
UPDATE YukonPAObject SET Type = 'MCT-310ID' WHERE LOWER(Type) = LOWER('MCT-310ID'); 
UPDATE YukonPAObject SET Type = 'MCT-310IL' WHERE LOWER(Type) = LOWER('MCT-310IL'); 
UPDATE YukonPAObject SET Type = 'MCT-318L' WHERE LOWER(Type) = LOWER('MCT-318L'); 
UPDATE YukonPAObject SET Type = 'MCT-213' WHERE LOWER(Type) = LOWER('MCT-213'); 
UPDATE YukonPAObject SET Type = 'WCTP TERMINAL' WHERE LOWER(Type) = LOWER('WCTP TERMINAL'); 
UPDATE YukonPAObject SET Type = 'MCT-310CT' WHERE LOWER(Type) = LOWER('MCT-310CT'); 
UPDATE YukonPAObject SET Type = 'MCT-310IM' WHERE LOWER(Type) = LOWER('MCT-310IM'); 
UPDATE YukonPAObject SET Type = 'EMETCON GROUP' WHERE LOWER(Type) = LOWER('EMETCON GROUP'); 
UPDATE YukonPAObject SET Type = 'VERSACOM GROUP' WHERE LOWER(Type) = LOWER('VERSACOM GROUP'); 
UPDATE YukonPAObject SET Type = 'EXPRESSCOM GROUP' WHERE LOWER(Type) = LOWER('EXPRESSCOM GROUP'); 
UPDATE YukonPAObject SET Type = 'RIPPLE GROUP' WHERE LOWER(Type) = LOWER('RIPPLE GROUP'); 
UPDATE YukonPAObject SET Type = 'DIGI SEP GROUP' WHERE LOWER(Type) = LOWER('DIGI SEP GROUP'); 
UPDATE YukonPAObject SET Type = 'LM DIRECT PROGRAM' WHERE LOWER(Type) = LOWER('LM DIRECT PROGRAM'); 
UPDATE YukonPAObject SET Type = 'LM SEP PROGRAM' WHERE LOWER(Type) = LOWER('LM SEP PROGRAM'); 
UPDATE YukonPAObject SET Type = 'LM CURTAIL PROGRAM' WHERE LOWER(Type) = LOWER('LM CURTAIL PROGRAM'); 
UPDATE YukonPAObject SET Type = 'LM CONTROL AREA' WHERE LOWER(Type) = LOWER('LM CONTROL AREA'); 
UPDATE YukonPAObject SET Type = 'LM ENERGY EXCHANGE' WHERE LOWER(Type) = LOWER('LM ENERGY EXCHANGE'); 
UPDATE YukonPAObject SET Type = 'MACRO GROUP' WHERE LOWER(Type) = LOWER('MACRO GROUP'); 
UPDATE YukonPAObject SET Type = 'CAP BANK' WHERE LOWER(Type) = LOWER('CAP BANK'); 
UPDATE YukonPAObject SET Type = 'CBC Versacom' WHERE LOWER(Type) = LOWER('CBC Versacom'); 
UPDATE YukonPAObject SET Type = 'VIRTUAL SYSTEM' WHERE LOWER(Type) = LOWER('VIRTUAL SYSTEM'); 
UPDATE YukonPAObject SET Type = 'CBC FP-2800' WHERE LOWER(Type) = LOWER('CBC FP-2800'); 
UPDATE YukonPAObject SET Type = 'POINT GROUP' WHERE LOWER(Type) = LOWER('POINT GROUP'); 
UPDATE YukonPAObject SET Type = 'CBC 6510' WHERE LOWER(Type) = LOWER('CBC 6510'); 
UPDATE YukonPAObject SET Type = 'SYSTEM' WHERE LOWER(Type) = LOWER('SYSTEM'); 
UPDATE YukonPAObject SET Type = 'MCT Broadcast' WHERE LOWER(Type) = LOWER('MCT Broadcast'); 
UPDATE YukonPAObject SET Type = 'ION-7700' WHERE LOWER(Type) = LOWER('ION-7700'); 
UPDATE YukonPAObject SET Type = 'ION-8300' WHERE LOWER(Type) = LOWER('ION-8300'); 
UPDATE YukonPAObject SET Type = 'ION-7330' WHERE LOWER(Type) = LOWER('ION-7330'); 
UPDATE YukonPAObject SET Type = 'RTU-DART' WHERE LOWER(Type) = LOWER('RTU-DART'); 
UPDATE YukonPAObject SET Type = 'MCT-310IDL' WHERE LOWER(Type) = LOWER('MCT-310IDL'); 
UPDATE YukonPAObject SET Type = 'MCT GROUP' WHERE LOWER(Type) = LOWER('MCT GROUP'); 
UPDATE YukonPAObject SET Type = 'MCT-410IL' WHERE LOWER(Type) = LOWER('MCT-410IL'); 
UPDATE YukonPAObject SET Type = 'TRANSDATA MARK-V' WHERE LOWER(Type) = LOWER('TRANSDATA MARK-V'); 
UPDATE YukonPAObject SET Type = 'SA-305 Group' WHERE LOWER(Type) = LOWER('SA-305 Group'); 
UPDATE YukonPAObject SET Type = 'SA-205 Group' WHERE LOWER(Type) = LOWER('SA-205 Group'); 
UPDATE YukonPAObject SET Type = 'SA-Digital Group' WHERE LOWER(Type) = LOWER('SA-Digital Group'); 
UPDATE YukonPAObject SET Type = 'Golay Group' WHERE LOWER(Type) = LOWER('Golay Group'); 
UPDATE YukonPAObject SET Type = 'RTU-LMI' WHERE LOWER(Type) = LOWER('RTU-LMI'); 
UPDATE YukonPAObject SET Type = 'RTC' WHERE LOWER(Type) = LOWER('RTC'); 
UPDATE YukonPAObject SET Type = 'LMSCENARIO' WHERE LOWER(Type) = LOWER('LMSCENARIO'); 
UPDATE YukonPAObject SET Type = 'KV' WHERE LOWER(Type) = LOWER('KV'); 
UPDATE YukonPAObject SET Type = 'KV2' WHERE LOWER(Type) = LOWER('KV2'); 
UPDATE YukonPAObject SET Type = 'RTM' WHERE LOWER(Type) = LOWER('RTM'); 
UPDATE YukonPAObject SET Type = 'CBC Expresscom' WHERE LOWER(Type) = LOWER('CBC Expresscom'); 
UPDATE YukonPAObject SET Type = 'SENTINEL' WHERE LOWER(Type) = LOWER('SENTINEL'); 
UPDATE YukonPAObject SET Type = 'FOCUS' WHERE LOWER(Type) = LOWER('FOCUS'); 
UPDATE YukonPAObject SET Type = 'ALPHA A3' WHERE LOWER(Type) = LOWER('ALPHA A3'); 
UPDATE YukonPAObject SET Type = 'MCT-470' WHERE LOWER(Type) = LOWER('MCT-470'); 
UPDATE YukonPAObject SET Type = 'MCT-410CL' WHERE LOWER(Type) = LOWER('MCT-410CL'); 
UPDATE YukonPAObject SET Type = 'CBC 7010' WHERE LOWER(Type) = LOWER('CBC 7010'); 
UPDATE YukonPAObject SET Type = 'CBC 7020' WHERE LOWER(Type) = LOWER('CBC 7020'); 
UPDATE YukonPAObject SET Type = 'SNPP TERMINAL' WHERE LOWER(Type) = LOWER('SNPP TERMINAL'); 
UPDATE YukonPAObject SET Type = 'RTU-MODBUS' WHERE LOWER(Type) = LOWER('RTU-MODBUS'); 
UPDATE YukonPAObject SET Type = 'MCT-430A' WHERE LOWER(Type) = LOWER('MCT-430A'); 
UPDATE YukonPAObject SET Type = 'MCT-430S4' WHERE LOWER(Type) = LOWER('MCT-430S4'); 
UPDATE YukonPAObject SET Type = 'CBC 7022' WHERE LOWER(Type) = LOWER('CBC 7022'); 
UPDATE YukonPAObject SET Type = 'CBC 7023' WHERE LOWER(Type) = LOWER('CBC 7023'); 
UPDATE YukonPAObject SET Type = 'CBC 7024' WHERE LOWER(Type) = LOWER('CBC 7024'); 
UPDATE YukonPAObject SET Type = 'CBC 7011' WHERE LOWER(Type) = LOWER('CBC 7011'); 
UPDATE YukonPAObject SET Type = 'CBC 7012' WHERE LOWER(Type) = LOWER('CBC 7012'); 
UPDATE YukonPAObject SET Type = 'REPEATER 801' WHERE LOWER(Type) = LOWER('REPEATER 801'); 
UPDATE YukonPAObject SET Type = 'REPEATER 921' WHERE LOWER(Type) = LOWER('REPEATER 921'); 
UPDATE YukonPAObject SET Type = 'MCT-410FL' WHERE LOWER(Type) = LOWER('MCT-410FL'); 
UPDATE YukonPAObject SET Type = 'MCT-410GL' WHERE LOWER(Type) = LOWER('MCT-410GL'); 
UPDATE YukonPAObject SET Type = 'MCT-430SL' WHERE LOWER(Type) = LOWER('MCT-430SL'); 
UPDATE YukonPAObject SET Type = 'CCU-721' WHERE LOWER(Type) = LOWER('CCU-721'); 
UPDATE YukonPAObject SET Type = 'Simple' WHERE LOWER(Type) = LOWER('Simple'); 
UPDATE YukonPAObject SET Type = 'Script' WHERE LOWER(Type) = LOWER('Script'); 
UPDATE YukonPAObject SET Type = 'REPEATER 902' WHERE LOWER(Type) = LOWER('REPEATER 902'); 
UPDATE YukonPAObject SET Type = 'Faulted Circuit Indicator' WHERE LOWER(Type) = LOWER('Faulted Circuit Indicator'); 
UPDATE YukonPAObject SET Type = 'Capacitor Bank Neutral Monitor' WHERE LOWER(Type) = LOWER('Capacitor Bank Neutral Monitor'); 
UPDATE YukonPAObject SET Type = 'CBC DNP' WHERE LOWER(Type) = LOWER('CBC DNP'); 
UPDATE YukonPAObject SET Type = 'INTEGRATION GROUP' WHERE LOWER(Type) = LOWER('INTEGRATION GROUP'); 
UPDATE YukonPAObject SET Type = 'INTEGRATION' WHERE LOWER(Type) = LOWER('INTEGRATION'); 
UPDATE YukonPAObject SET Type = 'MCT-430A3' WHERE LOWER(Type) = LOWER('MCT-430A3'); 
UPDATE YukonPAObject SET Type = 'LCR-3102' WHERE LOWER(Type) = LOWER('LCR-3102'); 
UPDATE YukonPAObject SET Type = 'RDS TERMINAL' WHERE LOWER(Type) = LOWER('RDS TERMINAL'); 
UPDATE YukonPAObject SET Type = 'CCU' WHERE LOWER(Type) = LOWER('CCU'); 
UPDATE YukonPAObject SET Type = 'TCU' WHERE LOWER(Type) = LOWER('TCU'); 
UPDATE YukonPAObject SET Type = 'LCU' WHERE LOWER(Type) = LOWER('LCU'); 
UPDATE YukonPAObject SET Type = 'Macro' WHERE LOWER(Type) = LOWER('Macro'); 
UPDATE YukonPAObject SET Type = 'Versacom' WHERE LOWER(Type) = LOWER('Versacom'); 
UPDATE YukonPAObject SET Type = 'Tap Paging' WHERE LOWER(Type) = LOWER('Tap Paging'); 
UPDATE YukonPAObject SET Type = 'WCTP Terminal Route' WHERE LOWER(Type) = LOWER('WCTP Terminal Route'); 
UPDATE YukonPAObject SET Type = 'Series 5 LMI' WHERE LOWER(Type) = LOWER('Series 5 LMI'); 
UPDATE YukonPAObject SET Type = 'RTC Route' WHERE LOWER(Type) = LOWER('RTC Route'); 
UPDATE YukonPAObject SET Type = 'SNPP Terminal Route' WHERE LOWER(Type) = LOWER('SNPP Terminal Route'); 
UPDATE YukonPAObject SET Type = 'Integration Route' WHERE LOWER(Type) = LOWER('Integration Route'); 
UPDATE YukonPAObject SET Type = 'TNPP Terminal Route' WHERE LOWER(Type) = LOWER('TNPP Terminal Route'); 
UPDATE YukonPAObject SET Type = 'RDS Terminal Route' WHERE LOWER(Type) = LOWER('RDS Terminal Route'); 
UPDATE YukonPAObject SET Type = 'Local Direct' WHERE LOWER(Type) = LOWER('Local Direct'); 
UPDATE YukonPAObject SET Type = 'Local Serial Port' WHERE LOWER(Type) = LOWER('Local Serial Port'); 
UPDATE YukonPAObject SET Type = 'Local Radio' WHERE LOWER(Type) = LOWER('Local Radio'); 
UPDATE YukonPAObject SET Type = 'Local Dialup' WHERE LOWER(Type) = LOWER('Local Dialup'); 
UPDATE YukonPAObject SET Type = 'Terminal Server Direct' WHERE LOWER(Type) = LOWER('Terminal Server Direct'); 
UPDATE YukonPAObject SET Type = 'TCP' WHERE LOWER(Type) = LOWER('TCP'); 
UPDATE YukonPAObject SET Type = 'UDP' WHERE LOWER(Type) = LOWER('UDP'); 
UPDATE YukonPAObject SET Type = 'Terminal Server' WHERE LOWER(Type) = LOWER('Terminal Server'); 
UPDATE YukonPAObject SET Type = 'Terminal Server Radio' WHERE LOWER(Type) = LOWER('Terminal Server Radio'); 
UPDATE YukonPAObject SET Type = 'Terminal Server Dialup' WHERE LOWER(Type) = LOWER('Terminal Server Dialup'); 
UPDATE YukonPAObject SET Type = 'Local Dialback' WHERE LOWER(Type) = LOWER('Local Dialback'); 
UPDATE YukonPAObject SET Type = 'Dialout Pool' WHERE LOWER(Type) = LOWER('Dialout Pool'); 
UPDATE YukonPAObject SET Type = 'CCSUBBUS' WHERE LOWER(Type) = LOWER('CCSUBBUS'); 
UPDATE YukonPAObject SET Type = 'CCFEEDER' WHERE LOWER(Type) = LOWER('CCFEEDER'); 
UPDATE YukonPAObject SET Type = 'CCAREA' WHERE LOWER(Type) = LOWER('CCAREA'); 
UPDATE YukonPAObject SET Type = 'CCSPECIALAREA' WHERE LOWER(Type) = LOWER('CCSPECIALAREA'); 
UPDATE YukonPAObject SET Type = 'CCSUBSTATION' WHERE LOWER(Type) = LOWER('CCSUBSTATION'); 
UPDATE YukonPAObject SET Type = 'LTC' WHERE LOWER(Type) = LOWER('LTC'); 
UPDATE YukonPAObject SET Type = 'GO_REGULATOR' WHERE LOWER(Type) = LOWER('GO_REGULATOR'); 
UPDATE YukonPAObject SET Type = 'PO_REGULATOR' WHERE LOWER(Type) = LOWER('PO_REGULATOR'); 
UPDATE YukonPAObject SET Type = 'RFN-410fL' WHERE LOWER(Type) = LOWER('RFN-410fL'); 
UPDATE YukonPAObject SET Type = 'RFN-410fX' WHERE LOWER(Type) = LOWER('RFN-410fX'); 
UPDATE YukonPAObject SET Type = 'RFN-410fD' WHERE LOWER(Type) = LOWER('RFN-410fD'); 
UPDATE YukonPAObject SET Type = 'RFN-430A3' WHERE LOWER(Type) = LOWER('RFN-430A3'); 
UPDATE YukonPAObject SET Type = 'RFN-430KV' WHERE LOWER(Type) = LOWER('RFN-430KV'); 
UPDATE YukonPAObject SET Type = 'MCT-420FL' WHERE LOWER(Type) = LOWER('MCT-420FL'); 
UPDATE YukonPAObject SET Type = 'MCT-420FLD' WHERE LOWER(Type) = LOWER('MCT-420FLD'); 
UPDATE YukonPAObject SET Type = 'MCT-420CL' WHERE LOWER(Type) = LOWER('MCT-420CL'); 
UPDATE YukonPAObject SET Type = 'MCT-420CLD' WHERE LOWER(Type) = LOWER('MCT-420CLD'); 
UPDATE YukonPAObject SET Type = 'ZigBee Utility Pro' WHERE LOWER(Type) = LOWER('ZigBee Utility Pro'); 
UPDATE YukonPAObject SET Type = 'Digi Gateway' WHERE LOWER(Type) = LOWER('Digi Gateway'); 

UPDATE YukonPAObject SET PAOClass = 'TRANSMITTER' WHERE LOWER(PAOClass) = LOWER('TRANSMITTER'); 
UPDATE YukonPAObject SET PAOClass = 'RTU' WHERE LOWER(PAOClass) = LOWER('RTU'); 
UPDATE YukonPAObject SET PAOClass = 'IED' WHERE LOWER(PAOClass) = LOWER('IED'); 
UPDATE YukonPAObject SET PAOClass = 'METER' WHERE LOWER(PAOClass) = LOWER('METER'); 
UPDATE YukonPAObject SET PAOClass = 'CARRIER' WHERE LOWER(PAOClass) = LOWER('CARRIER'); 
UPDATE YukonPAObject SET PAOClass = 'GROUP' WHERE LOWER(PAOClass) = LOWER('GROUP'); 
UPDATE YukonPAObject SET PAOClass = 'VIRTUAL' WHERE LOWER(PAOClass) = LOWER('VIRTUAL'); 
UPDATE YukonPAObject SET PAOClass = 'LOADMANAGEMENT' WHERE LOWER(PAOClass) = LOWER('LOADMANAGEMENT'); 
UPDATE YukonPAObject SET PAOClass = 'SYSTEM' WHERE LOWER(PAOClass) = LOWER('SYSTEM'); 
UPDATE YukonPAObject SET PAOClass = 'GRIDADVISOR' WHERE LOWER(PAOClass) = LOWER('GRIDADVISOR'); 
UPDATE YukonPAObject SET PAOClass = 'ROUTE' WHERE LOWER(PAOClass) = LOWER('ROUTE'); 
UPDATE YukonPAObject SET PAOClass = 'PORT' WHERE LOWER(PAOClass) = LOWER('PORT'); 
UPDATE YukonPAObject SET PAOClass = 'CUSTOMER' WHERE LOWER(PAOClass) = LOWER('CUSTOMER'); 
UPDATE YukonPAObject SET PAOClass = 'CAPCONTROL' WHERE LOWER(PAOClass) = LOWER('CAPCONTROL'); 
UPDATE YukonPAObject SET PAOClass = 'Schedule' WHERE LOWER(PAOClass) = LOWER('Schedule'); 
UPDATE YukonPAObject SET PAOClass = 'RFMESH' WHERE LOWER(PAOClass) = LOWER('RFMESH'); 

UPDATE YukonPAObject SET Category = 'DEVICE' WHERE LOWER(Category) = LOWER('DEVICE'); 
UPDATE YukonPAObject SET Category = 'ROUTE' WHERE LOWER(Category) = LOWER('ROUTE'); 
UPDATE YukonPAObject SET Category = 'PORT' WHERE LOWER(Category) = LOWER('PORT'); 
UPDATE YukonPAObject SET Category = 'CUSTOMER' WHERE LOWER(Category) = LOWER('CUSTOMER'); 
UPDATE YukonPAObject SET Category = 'CAPCONTROL' WHERE LOWER(Category) = LOWER('CAPCONTROL'); 
UPDATE YukonPAObject SET Category = 'LOADMANAGEMENT' WHERE LOWER(Category) = LOWER('LOADMANAGEMENT'); 
UPDATE YukonPAObject SET Category = 'Schedule' WHERE LOWER(Category) = LOWER('Schedule');
/* End YUK-9734 */

/* Start YUK-9724 */
CREATE TABLE DigiControlEventMapping  (
   EventId              NUMBER                          NOT NULL,
   StartTime            DATE                            NOT NULL,
   GroupId              NUMBER                          NOT NULL,
   LMControlHistoryId   NUMBER                          NULL,
   DeviceCount          NUMBER                          NULL,
   CONSTRAINT PK_DigiContEventMap PRIMARY KEY (EventId)
);

CREATE TABLE ZBControlEvent  (
   ZBControlEventId     NUMBER                          NOT NULL,
   EventId              NUMBER                          NOT NULL,
   EventTime            DATE                            NOT NULL,
   DeviceId             NUMBER                          NOT NULL,
   Action               VARCHAR2(255)                   NOT NULL,
   CONSTRAINT PK_ZBContEvent PRIMARY KEY (ZBControlEventId)
);

ALTER TABLE DigiControlEventMapping
    ADD CONSTRAINT FK_DigiContEventMap_LMContHist FOREIGN KEY (LMControlHistoryId)
        REFERENCES LMControlHistory (LMCtrlHistID);

ALTER TABLE DigiControlEventMapping
    ADD CONSTRAINT FK_DigiContEventMap_LMGroup FOREIGN KEY (GroupId)
        REFERENCES LMGroup (DeviceId)
            ON DELETE CASCADE;
        
ALTER TABLE ZBControlEvent
    ADD CONSTRAINT FK_ZBContEvent_DigiContEventMa FOREIGN KEY (EventId)
        REFERENCES DigiControlEventMapping (EventId)
            ON DELETE CASCADE;

ALTER TABLE ZBControlEvent
    ADD CONSTRAINT FK_ZBContEvent_ZBEndPoint FOREIGN KEY (DeviceId)
        REFERENCES ZBEndPoint (DeviceId)
            ON DELETE CASCADE;

UPDATE YukonServices 
SET ServiceName = 'SepMessageListener', 
    ServiceClass = 'classpath:com/cannontech/services/sepMessageListener/sepMessageListenerContext.xml' 
WHERE ServiceId = 16;
/* End YUK-9724 */

/* Start YUK-8711 */
CREATE INDEX Indx_AcctSite_SiteNum ON AccountSite (
    SiteNumber ASC
);

CREATE INDEX Indx_Add_LocAdd ON Address (
    LocationAddress1 ASC
);

CREATE INDEX Indx_CICustBase_CompName ON CICustomerBase (
    CompanyName ASC
);

CREATE INDEX Indx_ContNot_Not ON ContactNotification (
    Notification ASC
);

CREATE INDEX Indx_Cust_AltTrackNum ON Customer (
    AltTrackNum ASC
);

CREATE INDEX Indx_LMHardBase_ManSerNum ON LMHardwareBase (
    ManufacturerSerialNumber ASC
);

/* Add Functional Based Indexes */
CREATE INDEX INDX_AcctSite_SiteNum_FB ON AccountSite(
    UPPER(SiteNumber)
);

CREATE INDEX INDX_Add_LocAdd_FB ON Address(
    UPPER(LocationAddress1)
);

CREATE INDEX INDX_CICustBase_CompName_FB ON CICustomerBase(
    UPPER(CompanyName)
);

CREATE INDEX INDX_Cont_CLastName_CFirstN_FB ON Contact(
    UPPER(ContLastName),
    UPPER(ContFirstName)
);

CREATE INDEX INDX_ContNot_Not_FB ON ContactNotification(
    UPPER(Notification)
);

CREATE INDEX INDX_Cust_AltTrackNum_FB ON Customer(
    UPPER(AltTrackNum)
);
/* End YUK-8711 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
INSERT INTO CTIDatabase VALUES ('5.3', 'Matt K', '24-APR-2011', 'Latest Update', 1);
