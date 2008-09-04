/* 4.1_0fc2 changes.  These are changes to 4.1 that have been made since 4.1_0fc1*/
/* This script must be run manually using the SQL tool and not the DBToolsFrame tool. */

/* A database that was newly created in verion 4.0 AND already upgraded to 4.1fc1 CANNOT be upgraded to fc2 using 
 * this script.  Manual processing will be needed for upgrading a brand new 4.0 database, that has already been upgraded
 * to 4.1fc1, to 4.1fc2. 
 */ 

/* Start YUK-6139 */
if exists (select 1 
           from sysobjects 
           where id = object_id('CCOperations_View') 
           and type = 'V') 
   drop view CCOperations_View 
go 

if exists (select 1 
           from sysobjects 
           where id = object_id('CCOperationsESentAndAll_view') 
           and type = 'V') 
   drop view CCOperationsESentAndAll_view 
go 

if exists (select 1 
           from sysobjects 
           where id = object_id('CCOperationsDSentAndValid_view') 
           and type = 'V') 
   drop view CCOperationsDSentAndValid_view 
go 

if exists (select 1 
           from sysobjects 
           where id = object_id('CCOperationsCOrphanedConf_view') 
           and type = 'V') 
   drop view CCOperationsCOrphanedConf_view 
go 

if exists (select 1 
           from sysobjects 
           where id = object_id('CCOperationsBConfirmed_view') 
           and type = 'V') 
   drop view CCOperationsBConfirmed_view 
go 

if exists (select 1 
           from sysobjects 
           where id = object_id('CCOperationsASent_View') 
           and type = 'V') 
   drop view CCOperationsASent_View 
go 

if exists (select 1
            from  sysobjects
           where  id = object_id('CCOperationLogCache')
            and   type = 'U')
   drop table CCOperationLogCache;
go

CREATE VIEW CCOperationsASent_View
as
SELECT logid, pointid, datetime, text, feederid, subid, additionalinfo
FROM CCEventLog
WHERE text LIKE '%Close sent,%' OR text LIKE '%Open sent,%';
go

CREATE VIEW CCOperationsBConfirmed_view
as
SELECT logid, pointid, datetime, text, kvarbefore, kvarafter, kvarchange
FROM CCEventLog
WHERE text LIKE 'Var: %';
go

CREATE VIEW CCOperationsCOrphanedConf_view
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
go

CREATE VIEW CCOperationsDSentAndValid_view
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
go 

CREATE VIEW CCOperationsESentAndAll_view
as
SELECT OP.LogId AS OId, MIN(aaa.confid) AS CId 
FROM CCOperationsASent_View OP
LEFT JOIN CCOperationsDSentAndValid_view AAA ON OP.LogId = AAA.OpId
GROUP BY OP.LogId;
go

CREATE TABLE CCOperationLogCache 
(OperationLogId numeric(18,0) not null,
 ConfirmationLogId numeric(18,0) null,
 constraint PK_CCOperationLogCache primary key (OperationLogId));
go

INSERT INTO CCOperationLogCache
SELECT OpId, ConfId 
FROM CCOperationsDSentAndValid_view
WHERE OpId NOT IN (SELECT OperationLogId 
                   FROM CCOperationLogCache);
go

/* @start-block */
CREATE VIEW CCOperations_View
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
go
/* @end-block */
/* End YUK-6139 */

/* Start YUK-6268 */
UPDATE YukonUserRole 
SET value = 'DEVICE_NAME' 
WHERE rolePropertyId = -1700 
AND value != '(none)';
go

UPDATE YukonGroupRole 
SET value = 'DEVICE_NAME' 
WHERE rolePropertyId = -1700 
AND value != '(none)';
go

UPDATE YukonRoleProperty 
SET defaultValue = 'DEVICE_NAME', description = 'Defines the format for displaying devices. Available placeholders: DEVICE_NAME, METER_NUMBER, ID, ADDRESS' 
WHERE rolePropertyId = -1700;
go
/* End YUK-6268 */

/* Start YUK-6393 */
EXEC sp_rename 'BaseLine.holidaysUsed', 'HolidayScheduleId', 'COLUMN'; 
go 

alter table BaseLine 
   add constraint FK_BASELINE_HOLIDAYS foreign key (HolidayScheduleId) 
      references HolidaySchedule (HolidayScheduleID) 
go 

alter table MACSchedule 
   add constraint FK_MACSCHED_HOLIDAYS foreign key (HolidayScheduleID) 
      references HolidaySchedule (HolidayScheduleID) 
go 
/* End YUK-6393 */

/* Start YUK-6395 */
CREATE UNIQUE INDEX Indx_YukonGroup_groupName_UNQ ON YukonGroup (
GroupName ASC
)
go
/* End YUK-6395 */

/* Start YUK-6412 */
Update YukonListEntry
SET EntryText = 'UtilityPro'
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

