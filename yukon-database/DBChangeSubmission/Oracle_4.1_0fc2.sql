/* 4.1_0fc2 changes.  These are changes to 4.1 that have been made since 4.1_0fc1*/
/* This script must be run manually using the SQL tool and not the DBToolsFrame tool. */

/* Start YUK-6139 */
/* @start-block */
DECLARE
    tbl_exist int;
BEGIN
    SELECT COUNT(*) INTO tbl_exist 
    FROM USER_TAB_COLUMNS
    WHERE table_name = 'CCOPERATIONLOGCACHE';
    
    IF tbl_exist > 0 THEN
		execute immediate 'DROP VIEW CCOperations_View';
        execute immediate 'DROP TABLE CCOperationLogCache';
    END IF;
END;
/
/* @end-block */

CREATE OR REPLACE VIEW CCOperationsASent_View
as
SELECT logid, pointid, datetime, text, feederid, subid, additionalinfo
FROM CCEventLog
WHERE text LIKE '%Close sent,%' OR text LIKE '%Open sent,%';

CREATE OR REPLACE VIEW CCOperationsBConfirmed_view
as
SELECT logid, pointid, datetime, text, kvarbefore, kvarafter, kvarchange
FROM CCEventLog
WHERE text LIKE 'Var: %';

CREATE OR REPLACE VIEW CCOperationsCOrphanedConf_view
as
SELECT EL.LogId AS OpId, MIN(el2.LogID) AS ConfId 
FROM CCOperationsASent_View EL 
JOIN CCOperationsBConfirmed_view EL2 ON EL2.PointId = EL.PointId AND EL.LogId < EL2.LogId 
LEFT JOIN (SELECT A.LogId AS AId, MIN(b.LogID) AS NextAId 
           FROM CCOperationsASent_View A 
           JOIN CCOperationsASent_View B ON A.PointId = B.PointId AND A.LogId < B.LogId 
           GROUP BY A.LogId) EL3 ON EL3.AId = EL.LogId 
WHERE EL3.NextAId IS NULL
GROUP BY EL.LogId;

CREATE OR REPLACE VIEW CCOperationsDSentAndValid_view
as
SELECT EL.LogId AS OpId, MIN(el2.LogID) AS ConfId 
FROM CCOperationsASent_View EL 
JOIN CCOperationsBConfirmed_view EL2 ON EL2.PointId = EL.PointId AND EL.LogId < EL2.LogId 
LEFT JOIN (SELECT A.LogId AS AId, MIN(b.LogID) AS NextAId 
           FROM CCOperationsASent_View A 
           JOIN CCOperationsASent_View B ON A.PointId = B.PointId AND A.LogId < B.LogId 
           GROUP BY A.LogId) EL3 ON EL3.AId = EL.LogId 
WHERE EL2.LogId < EL3.NextAId OR EL3.NextAId IS NULL
GROUP BY EL.LogId;

CREATE OR REPLACE VIEW CCOperationsESentAndAll_view
as
SELECT OP.LogId AS OId, MIN(aaa.confid) AS CId 
FROM CCOperationsASent_View OP
LEFT JOIN CCOperationsDSentAndValid_view AAA ON OP.LogId = AAA.OpId
GROUP BY OP.LogId;


CREATE TABLE CCOperationLogCache 
(OperationLogId NUMBER not null,
 ConfirmationLogId NUMBER null,
 constraint PK_CCOperationLogCache primary key (OperationLogId));

INSERT INTO CCOperationLogCache
SELECT OpId, ConfId 
FROM CCOperationsDSentAndValid_view
WHERE OpId NOT IN (SELECT OperationLogId 
                   FROM CCOperationLogCache);

CREATE OR REPLACE VIEW CCOperations_View
as 
SELECT YP3.PAObjectid AS CBCId, YP3.PAOName AS CBCName, YP.PAObjectid AS CapBankId, 
       YP.PAOName AS CapBankName, EL.DateTime AS OpTime, EL.Text AS Operation, 
       EL2.DateTime AS ConfTime, EL2.Text AS ConfStatus, YP1.PAOName AS FeederName, 
       YP1.PAObjectId AS FeederId, YP2.PAOName AS SubBusName, YP2.PAObjectId AS SubBusId, 
       YP5.PAOName AS SubstationName, YP5.PAObjectId AS SubstationId, 
       YP4.PAOName AS Region, YP4.PAObjectId AS AreaId, CB.BankSize, CB.ControllerType, 
       EL.AdditionalInfo AS IPAddress, CBC.SerialNumber AS SerialNum, DA.SlaveAddress, 
       EL2.KvarAfter, EL2.KvarChange, EL2.KvarBefore 
FROM CCOperationsASent_View EL
JOIN CCOperationLogCache OpConf ON EL.LogId = OpConf.OperationLogId        
LEFT JOIN CCOperationsBConfirmed_view EL2 ON EL2.LogId = OpConf.ConfirmationLogId 
LEFT JOIN CCOperationsCOrphanedConf_view Orphs ON EL.logid = Orphs.opid       
JOIN Point ON Point.PointId = EL.PointId        
JOIN DynamicCCCapBank ON DynamicCCCapBank.CapBankId = Point.PAObjectId        
JOIN YukonPAObject YP ON YP.PAObjectId = DynamicCCCapBank.CapBankId        
JOIN YukonPAObject YP1 ON YP1.PAObjectId = EL.FeederId        
JOIN YukonPAObject YP2 ON YP2.PAObjectId = EL.SubId        
JOIN CapBank CB ON CB.DeviceId = DynamicCCCapBank.CapBankId        
LEFT JOIN DeviceDirectCommSettings DDCS ON DDCS.DeviceId = CB.ControlDeviceId        
LEFT JOIN DeviceAddress DA ON DA.DeviceId = CB.ControlDeviceId        
JOIN YukonPAObject YP3 ON YP3.PAObjectId = CB.ControlDeviceId        
LEFT JOIN DeviceCBC CBC ON CBC.DeviceId = CB.ControlDeviceId        
LEFT JOIN (SELECT EntryId, PAObjectId, Owner, InfoKey, Value, UpdateTime                        
           FROM DynamicPAOInfo                         
           WHERE (InfoKey LIKE '%udp ip%')) P ON P.PAObjectId = CB.ControlDeviceId        
LEFT JOIN CCSubstationSubbusList SSL ON SSL.SubstationBusId = EL.SubId         
LEFT JOIN YukonPAObject YP5 ON YP5.PAObjectId =  SSL.SubstationBusId        
LEFT JOIN CCSubAreaAssignment CSA ON CSA.SubstationBusId = SSL.SubstationId        
LEFT JOIN YukonPAObject YP4 ON YP4.PAObjectId = CSA.AreaId; 
/* End YUK-6139 */

/* Start YUK-6268 */
UPDATE YukonUserRole 
SET value = 'DEVICE_NAME' 
WHERE rolePropertyId = -1700 
AND value != '(none)'; 

UPDATE YukonGroupRole 
SET value = 'DEVICE_NAME' 
WHERE rolePropertyId = -1700 
AND value != '(none)'; 

UPDATE YukonRoleProperty 
SET defaultValue = 'DEVICE_NAME', description = 'Defines the format for displaying devices. Available placeholders: DEVICE_NAME, METER_NUMBER, ID, ADDRESS' 
WHERE rolePropertyId = -1700; 
/* End YUK-6268 */

/* Start YUK-6393 */
ALTER TABLE Baseline
RENAME COLUMN holidaysused TO holidayScheduleId;

alter table BaseLine
   add constraint FK_BASELINE_HOLIDAYS foreign key (HolidayScheduleId)
      references HolidaySchedule (HolidayScheduleID);

alter table MACSchedule
   add constraint FK_MACSCHED_HOLIDAYS foreign key (HolidayScheduleID)
      references HolidaySchedule (HolidayScheduleID);
/* End YUK-6393 */

/* Start YUK-6395 */
CREATE UNIQUE INDEX Indx_YukonGroup_groupName_UNQ ON YukonGroup (
   GroupName ASC
);
/* End YUK-6395 */

/* Start YUK-6412 */
Update YukonListEntry
SET EntryText = 'UtilityPRO'
WHERE EntryText = 'ExpressStat Utility Pro';
/* End YUK-6412 */

/* Start YUK-6351 */
UPDATE PointUnit
SET PointUnit.UOMID = 8
WHERE PointUnit.UOMID = 55;

DELETE FROM UnitMeasure
WHERE UOMID = 55;
/* End YUK-6351 */

/* Start YUK-6409 */
DELETE FROM YukonUserRole 
WHERE RolePropertyId = -20105;

DELETE FROM YukonGroupRole 
WHERE RolePropertyId = -20105;

DELETE FROM YukonRoleProperty 
WHERE RolePropertyId = -20105;
/* End YUK-6409 */

/* Start YUK-6289 */
/* Device Actions role */ 
INSERT INTO YukonGroupRole VALUES (-1150, -2, -213, -21300, '(none)'); 
INSERT INTO YukonGroupRole VALUES (-1151, -2, -213, -21301, '(none)'); 
INSERT INTO YukonGroupRole VALUES (-1152, -2, -213, -21302, '(none)'); 
INSERT INTO YukonGroupRole VALUES (-1153, -2, -213, -21303, '(none)'); 
INSERT INTO YukonGroupRole VALUES (-1154, -2, -213, -21304, '(none)'); 
INSERT INTO YukonGroupRole VALUES (-1155, -2, -213, -21305, '(none)'); 
INSERT INTO YukonGroupRole VALUES (-1156, -2, -213, -21306, '(none)'); 
INSERT INTO YukonGroupRole VALUES (-1157, -2, -213, -21307, '(none)');
/* End YUK-6289 */

/* Start YUK-6426 */
/* Versacom */ 
INSERT INTO Command VALUES(-145, 'putconfig vcom lcrmode ?''Enter e|v (for Emetcon or Versacom)''', 'Set LCR3100 Versacom mode', 'VersacomSerial'); 
INSERT INTO Command VALUES(-146, 'putconfig vcom silver ?''Enter a value 1-60''', 'Set LCR3100 Versacom Silver Addressing', 'VersacomSerial'); 
INSERT INTO Command VALUES(-147, 'putconfig vcom gold ?''Enter a value 1-4''', 'Set LCR3100 Versacom Gold Addressing', 'VersacomSerial'); 

/* ExpressCom */ 
INSERT INTO Command VALUES(-148, 'putconfig xcom lcrmode ?''Enter Ex|Em|V|G (For example: ExEmVG or ExV)''', 'Set LCR3100 Expresscom mode', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-149, 'putconfig xcom silver ?''Enter a value 1-60''', 'Set LCR3100 Expresscom Silver Addressing', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-150, 'putconfig xcom gold ?''Enter a value 1-4''', 'Set LCR3100 Expresscom Gold Addressing', 'ExpresscomSerial'); 

INSERT INTO Command VALUES(-151, 'putconfig xcom lcrmode Ex', 'Set LCR3100 Expresscom mode', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-152, 'putconfig xcom lcrmode V', 'Set LCR3100 Versacom mode', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-153, 'putconfig xcom lcrmode Em', 'Set LCR3100 Emetcon mode', 'ExpresscomSerial'); 

INSERT INTO DEVICETYPECOMMAND VALUES (-728, -148, 'ExpresscomSerial', 25, 'N', -1); 
INSERT INTO DEVICETYPECOMMAND VALUES (-729, -149, 'ExpresscomSerial', 26, 'Y', -1); 
INSERT INTO DEVICETYPECOMMAND VALUES (-730, -150, 'ExpresscomSerial', 27, 'Y', -1); 
INSERT INTO DEVICETYPECOMMAND VALUES (-731, -151, 'ExpresscomSerial', 28, 'Y', -1); 
INSERT INTO DEVICETYPECOMMAND VALUES (-732, -152, 'ExpresscomSerial', 29, 'Y', -1); 
INSERT INTO DEVICETYPECOMMAND VALUES (-733, -153, 'ExpresscomSerial', 30, 'Y', -1); 

INSERT INTO DEVICETYPECOMMAND VALUES (-734, -145, 'VersacomSerial', 24, 'Y', -1); 
INSERT INTO DEVICETYPECOMMAND VALUES (-735, -146, 'VersacomSerial', 25, 'Y', -1); 
INSERT INTO DEVICETYPECOMMAND VALUES (-736, -147, 'VersacomSerial', 26, 'Y', -1); 
/* End YUK-6426 */

/* Start YUK-6441 */
DELETE BillingFileFormats 
WHERE formatId < 0 
AND formatType IN (SELECT DISTINCT formatType 
                   FROM BillingFileFormats 
                   WHERE formatId >= 0); 

create unique index Indx_BillFile_FormType_UNQ on BillingFileFormats ( 
FormatType ASC 
);
/* End YUK-6441 */

/* Start YUK-6450 */
INSERT INTO BillingFileFormats VALUES( 33, 'NISC Interval Readings', 1); 
/* End YUK-6450 */
