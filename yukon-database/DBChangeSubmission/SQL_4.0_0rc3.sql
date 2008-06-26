/* 4.0_0rc2 changes.  These are changes to 4.0 that have been made since 4.0_0rc1*/
/* This script must be run manually using the SQL tool and not the DBToolsFrame tool. */

/* A database that was newly created in verion 3.5 AND already upgraded to 4.0rc1 CANNOT be upgraded to rc2 using 
 * this script.  Manual processing will be needed for upgrading a brand new 3.5 database, that has already been upgraded
 * to r.0rc1, to 4.0rc2. 
 */ 

/* The following block is only included to be run on a database that is being upgrade from version 4.0fc5 or less.
 * If the database has already been upgraded to rc1 , then do not run the following block.
 * Please ask Stacey (x7720) if you have questions on whether this block should be run or not.  
 */
/* START SPECIAL BLOCK */
/* Start YUK-6107 */
if exists (select 1 
            from sysobjects 
           where id = object_id('CCOperations_View') 
            and type = 'V') 
   drop view CCOperations_View 
go 

create view CCOperations_View as 
SELECT YP3.PAOName AS CBCName, YP.PAOName AS CapBankName, YP.PAObjectId AS CapBankId, 
       EL.DateTime AS OpTime, EL.Text AS Operation, EL2.DateTime AS ConfTime, EL2.Text AS ConfStatus, 
       YP1.PAOName AS FeederName, YP1.PAObjectId AS FeederId, YP2.PAOName AS SubBusName, 
       YP2.PAObjectId AS SubBusId, YP5.PAOName AS SubstationName, YP5.PAObjectId AS SubstationId, 
       YP4.PAOName AS Region, YP4.PAObjectId AS AreaId, CB.BankSize, CB.ControllerType, 
       EL.AdditionalInfo AS IPAddress, CBC.SerialNumber AS SerialNum, DA.SlaveAddress, 
       EL2.KvarAfter, EL2.KvarChange, EL2.KvarBefore 
FROM (SELECT OP.LogId AS OId, MIN(aaa.confid) AS CId 
      FROM (SELECT LogId, PointId 
            FROM CCEventLog 
            WHERE Text LIKE '%Close sent%' OR Text LIKE '%Open sent%') OP 
      LEFT OUTER JOIN (SELECT EL.LogId AS OpId, MIN(el2.LogID) AS ConfId 
                       FROM CCEventLog EL 
                       INNER JOIN CCEventLog EL2 ON EL2.PointId = EL.PointId AND EL.LogId < EL2.LogId 
                       LEFT OUTER JOIN (SELECT A.LogId AS AId, MIN(b.LogID) AS NextAId 
                                        FROM CCEventLog A 
                                        INNER JOIN CCEventLog B ON A.PointId = B.PointId AND A.LogId < B.LogId 
                                        WHERE (A.Text LIKE '%Close sent,%' OR A.Text LIKE '%Open sent,%') 
                                        AND (B.Text LIKE '%Close sent,%' OR B.Text LIKE '%Open sent,%') 
                                        GROUP BY A.LogId) EL3 ON EL3.AId = EL.LogId 
                       WHERE (EL.Text LIKE '%Close sent,%' OR EL.Text LIKE '%Open sent,%') 
                       AND (EL2.Text LIKE 'Var: %') AND (EL2.LogId < EL3.NextAId) 
                       OR (EL.Text LIKE '%Close sent,%' OR EL.Text LIKE '%Open sent,%') 
                       AND (EL2.Text LIKE 'Var: %') AND (EL3.NextAId IS NULL) 
                       GROUP BY EL.LogId) AAA ON OP.LogId = AAA.OpId 
      GROUP BY OP.LogId) OpConf 
      INNER JOIN CCEventLog EL ON EL.LogId = OpConf.OId 
      LEFT OUTER JOIN CCEventLog EL2 ON EL2.LogId = OpConf.CId 
      INNER JOIN Point ON Point.PointId = EL.PointId 
      INNER JOIN DynamicCCCapBank ON DynamicCCCapBank.CapBankId = Point.PAObjectId 
      INNER JOIN YukonPAObject YP ON YP.PAObjectId = DynamicCCCapBank.CapBankId 
      INNER JOIN YukonPAObject YP1 ON YP1.PAObjectId = EL.FeederId 
      INNER JOIN YukonPAObject YP2 ON YP2.PAObjectId = EL.SubId 
      INNER JOIN CapBank CB ON CB.DeviceId = DynamicCCCapBank.CapBankId 
      LEFT OUTER JOIN DeviceDirectCommSettings DDCS ON DDCS.DeviceId = CB.ControlDeviceId 
      LEFT OUTER JOIN DeviceAddress DA ON DA.DeviceId = CB.ControlDeviceId 
      INNER JOIN YukonPAObject YP3 ON YP3.PAObjectId = CB.ControlDeviceId 
      LEFT OUTER JOIN DeviceCBC CBC ON CBC.DeviceId = CB.ControlDeviceId 
      LEFT OUTER JOIN (SELECT EntryId, PAObjectId, Owner, InfoKey, Value, UpdateTime 
                       FROM DynamicPAOInfo 
                       WHERE (InfoKey LIKE '%udp ip%')) P ON P.PAObjectId = CB.ControlDeviceId 
      LEFT OUTER JOIN CCSubstationSubbusList SSL ON SSL.SubstationBusId = EL.SubId 
      LEFT OUTER JOIN YukonPAObject YP5 ON YP5.PAObjectId = SSL.SubstationBusId 
      LEFT OUTER JOIN CCSubAreaAssignment CSA ON CSA.SubstationBusId = SSL.SubstationId 
      LEFT OUTER JOIN YukonPAObject YP4 ON YP4.PAObjectId = CSA.AreaId; 
go 
/* End YUK-6107 */
