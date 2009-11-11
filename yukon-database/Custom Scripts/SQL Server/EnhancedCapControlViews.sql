/* These queries were built to integrat between the Yukon database and Progress Energy Carolina's 
 * custom appliciation.  The requirement was to include extra fields to the normal cap control
 * views.
 * 
 * YUK-7973
 */

/* CCAllCapBankInventory View
 * This view is the same view as CCInventory_View except for it also contains the  
 * communication medium (CommMedium), the communication strength (CommStrength), and the
 * feeder identifier (FeederIdentifier).
 */
CREATE VIEW CCAllCapBankInventory_View AS 
SELECT YP4.PAOName AS Region, YP5.PAOName AS SubstationName, YP3.PAOName AS SubBusName, 
       YP2.PAOName AS FeederName, YP4.PAObjectId AS AreaId, YP5.PAObjectId AS SubId, 
       YP3.PAObjectId AS SubBusId, YP2.PAObjectId AS FdrId, YP.PAOName AS CBCName, 
       YP.PAObjectId AS CBCId, YP1.PAOName AS CapBankName, YP1.PAObjectId AS BankId, 
       CB.BankSize AS CapBankSize, FB.ControlOrder AS DisplayOrder, DCB.ControlStatus, 
       S.Text AS ControlStatusName, CB.SwitchManufacture AS SWMfgr, CB.TypeOfSwitch AS SWType, 
       CB.OperationalState AS OperationMethod, CB.ControllerType, DPI.Value AS IPAddress, 
       DA.SlaveAddress, CAPA.Latitude AS Lat, CAPA.Longitude AS Lon, CAPA.DriveDirections AS DriveDirection, 
       CB.MapLocationId AS OpCenter, CAPA.MaintenanceAreaId AS TA, FB.CloseOrder AS CloseSequence, 
       FB.TripOrder AS OpenSequence, DCB.LastStatusChangeTime AS LastOperationTime, 
       CAPA.LastInspVisit AS LastInspectionDate, CAPA.LastMaintVisit AS LastMaintenanceDate, 
       CAPA.MaintenanceReqPend, YP1.DisableFlag AS CapDisabled, CAPA.PotentialTransformer, 
       CAPA.OtherComments, CAPA.OpTeamComments, CAPA.PoleNumber, 
       DTWC.TotalOpCount AS OpsCounterSinceLastReset, DTWC.TotalOpCount AS OperationsCounterToday, 
       DTWC.UvOpCount AS UvOperationsCounter, DTWC.OvOpCount AS OvOperationsCounter, 
       DTWC.OvUvCountResetDate AS UvOvCounterResetDate, DTWC.LastOvUvDateTime, 
       CCS.MapLocationId + FDR.MapLocationId AS FeederIdentifier, 
       CASE WHEN (SUBSTRING(CAPA.CommMedium, 1, LEN(CB.ControllerType)) = CB.ControllerType AND 
                  LEN(CAPA.CommMedium) > LEN(CB.ControllerType)) THEN 
                  substring(CAPA.commMedium, len(CB.controllertype) + 1, LEN(capa.commMedium)) 
            ELSE 
                 CAPA.CommMedium 
            END CommMedium, CAPA.CommStrength 
FROM CapBank CB 
LEFT OUTER JOIN (SELECT PAObjectId, PAOName 
      FROM YukonPAObject 
      WHERE (Type LIKE '%CBC%')) YP ON YP.PAObjectId = CB.ControlDeviceId 
LEFT OUTER JOIN YukonPAObject YP1 ON YP1.PAObjectId = CB.DeviceId 
LEFT OUTER JOIN DynamicCCCapBank DCB ON DCB.CapBankId = YP1.PAObjectId 
LEFT OUTER JOIN State S ON S.StateGroupId = 3 AND DCB.ControlStatus = S.RawState 
LEFT OUTER JOIN State SL ON SL.StateGroupId = 3 AND DCB.TwoWayCBCState = SL.RawState 
LEFT OUTER JOIN CCFeederBankList FB ON FB.DeviceId = CB.DeviceId 
LEFT OUTER JOIN YukonPAObject YP2 ON YP2.PAObjectId = FB.FeederId 
LEFT OUTER JOIN CCFeederSubAssignment SF ON FB.FeederId = SF.FeederId 
LEFT OUTER JOIN YukonPAObject YP3 ON YP3.PAObjectId = SF.SubStationBusId 
LEFT OUTER JOIN CCSubstationSubbusList SSL ON SSL.SubstationBusId = YP3.PAObjectId 
LEFT OUTER JOIN YukonPAObject YP5 ON YP5.PAObjectId = SSL.SubstationId 
LEFT OUTER JOIN CCSubAreaAssignment SA ON SA.SubstationBusId = SSL.SubstationId 
LEFT OUTER JOIN YukonPAObject YP4 ON YP4.PAObjectId = SA.AreaId 
LEFT OUTER JOIN DeviceDirectCommSettings DDCS ON DDCS.DeviceId = CB.ControlDeviceId 
LEFT OUTER JOIN DeviceAddress DA ON DA.DeviceId = YP.PAObjectId 
LEFT OUTER JOIN (SELECT EntryId, PAObjectId, Owner, InfoKey, Value, UpdateTime 
                 FROM DynamicPAOInfo 
                 WHERE (InfoKey LIKE '%udp ip%')) DPI ON DPI.PAObjectId = YP.PAObjectId 
LEFT OUTER JOIN DeviceCBC CBC ON CBC.DeviceId = CB.ControlDeviceId 
LEFT OUTER JOIN CapBankAdditional CAPA ON CAPA.DeviceId = CB.DeviceId 
LEFT OUTER JOIN DynamicCCTwoWayCBC DTWC ON CB.ControlDeviceId = DTWC.DeviceId 
LEFT OUTER JOIN CapControlFeeder FDR ON FDR.FeederId = YP2.PAObjectId 
LEFT OUTER JOIN CapControlSubstation CCS on CCS.SubstationId = YP5.PAObjectId
GO

/* EnhancedCCOperationsASent View
 * This view is the same view as CCOperationsASent_View except for it also contains the  
 * switch command (SWCommand) and the operation reason (OPReason).
 */
CREATE VIEW EnhancedCCOperationsASent_View AS 
SELECT LogId, PointId, ActionId, DateTime, Text, FeederId, SubId, AdditionalInfo, 
CASE WHEN Text LIKE '%Open%' THEN 'OPEN' 
     WHEN Text LIKE '%Close%' THEN 'CLOSE' 
     ELSE 'UNKNOWN' END AS SWCommand,
CASE WHEN Text LIKE 'Manual%' THEN 'MAN' 
     WHEN Text LIKE '%TimeOfDay%' THEN 'SCH' 
     WHEN Text LIKE '%LikeDay%' THEN 'BVAR' 
     WHEN Text LIKE '%CYC%' THEN 'CYC' 
     WHEN Text LIKE '%REF%' THEN 'REF' 
     ELSE 'VAR' END  AS OPReason 
FROM CCEventLog 
WHERE EventType = 1 
AND ActionId > -1
GO

/* EnhancedCCOperations View
 * This view is the same view as CCInventory_View except for it also contains the 
 * communication strength (CommStrength), the switch command (SWCommand), the operation reason (OPReason), 
 * and the confirmation status (ConfirmationStatus).
 */
CREATE VIEW EnhancedCCOperations_View AS 
SELECT YP3.PAObjectId AS CBCId, YP3.PAOName AS CBCName, YP.PAObjectId AS CapBankId, YP.PAOName AS CapBankName, 
       CCOAS.PointId, CCOAS.LogId AS OpLogId, CCOAS.ActionId, CCOAS.DateTime AS OpTime, CCOAS.Text AS Operation, 
       CCOBC.LogId AS ConfLogId, CCOBC.ActionId AS ActionId2, CCOBC.DateTime AS ConfTime, CCOBC.Text AS ConfStatus, 
       YP1.PAOName AS FeederName, YP1.PAObjectId AS FeederId, YP2.PAOName AS SubBusName, YP2.PAObjectId AS SubBusId, 
       YP5.PAOName AS SubstationName, YP5.PAObjectId AS SubstationId, YP4.PAOName AS Region, YP4.PAObjectId AS AreaId, 
       CB.BankSize, CB.ControllerType, CBA.CommStrength, CCOAS.AdditionalInfo AS IPAddress, CBC.SerialNumber AS SerialNum, DA.SlaveAddress, 
       CCOBC.KvarAfter, CCOBC.KvarChange, CCOBC.KvarBefore, CCOAS.SWCommand, CCOAS.OPReason, CCOBC.CapBankStateInfo AS ConfirmationStatus
FROM EnhancedCCOperationsASent_View CCOAS 
JOIN CCOperationsBConfirmed_View CCOBC ON CCOBC.ActionId = CCOAS.ActionId 
AND CCOBC.PointId = CCOAS.PointId 
AND CCOAS.ActionId >= 0 
AND CCOBC.ActionId >= 0 
JOIN Point ON Point.PointId = CCOAS.PointId 
JOIN DynamicCCCapBank ON DynamicCCCapBank.CapBankId = Point.PAObjectId 
JOIN YukonPAObject YP ON YP.PAObjectId = DynamicCCCapBank.CapBankId 
JOIN YukonPAObject YP1 ON YP1.PAObjectId = CCOAS.FeederId 
JOIN YukonPAObject YP2 ON YP2.PAObjectId = CCOAS.SubId 
JOIN CapBank CB ON CB.DeviceId = DynamicCCCapBank.CapBankId 
LEFT JOIN DeviceDirectCommSettings DDCS ON DDCS.DeviceId = CB.ControlDeviceId 
LEFT JOIN DeviceAddress DA ON DA.DeviceId = CB.ControlDeviceId 
JOIN YukonPAObject YP3 ON YP3.PAObjectId = CB.ControlDeviceId 
LEFT JOIN DeviceCBC CBC ON CBC.DeviceId = CB.ControlDeviceId 
LEFT JOIN (SELECT EntryId, PAObjectId, Owner, InfoKey, Value, UpdateTime 
           FROM DynamicPAOInfo 
           WHERE (InfoKey LIKE '%udp ip%')) P ON P.PAObjectId = CB.ControlDeviceId 
LEFT JOIN CCSubstationSubbusList SSL ON SSL.SubstationBusId = CCOAS.SubId 
LEFT JOIN YukonPAObject YP5 ON YP5.PAObjectId = SSL.SubstationBusId 
LEFT JOIN CCSubAreaAssignment CSA ON CSA.SubstationBusId = SSL.SubstationId 
LEFT JOIN YukonPAObject YP4 ON YP4.PAObjectId = CSA.AreaId 
LEFT JOIN CapBankAdditional CBA ON CBA.DeviceID = Point.PAObjectID
GO
  
/* EnhancedCCInventory View
 * This view is the same view as CCInventory_View except for it also contains the  
 * feeder identifier (FeederIdentifier).
 */
CREATE VIEW EnhancedCCInventory_View AS 
SELECT YP4.PAOName AS Region, YP5.PAOName AS SubstationName, YP3.PAOName AS SubBusName, 
       YP2.PAOName AS FeederName, YP4.PAObjectId AS AreaId, YP5.PAObjectId AS SubId, 
       YP3.PAObjectId AS SubBusId, YP2.PAObjectId AS FdrId, YP.PAOName AS CBCName, 
       YP.PAObjectId AS CBCId, YP1.PAOName AS CapBankName, YP1.PAObjectId AS BankId, 
       CB.BankSize AS CapBankSize, FB.ControlOrder AS DisplayOrder, DCB.ControlStatus, 
       S.Text AS ControlStatusName, CB.SwitchManufacture AS SWMfgr, CB.TypeOfSwitch AS SWType, 
       CB.OperationalState AS OperationMethod, CB.ControllerType, DPI.Value AS IPAddress, 
       DA.SlaveAddress, CAPA.Latitude AS Lat, CAPA.Longitude AS Lon, CAPA.DriveDirections AS DriveDirection, 
       CB.MapLocationId AS OpCenter, CAPA.MaintenanceAreaId AS TA, FB.CloseOrder AS CloseSequence, 
       FB.TripOrder AS OpenSequence, DCB.LastStatusChangeTime AS LastOperationTime, 
       CAPA.LastInspVisit AS LastInspectionDate, CAPA.LastMaintVisit AS LastMaintenanceDate, 
       CAPA.MaintenanceReqPend, YP1.DisableFlag AS CapDisabled, CAPA.PotentialTransformer, 
       CAPA.OtherComments, CAPA.OpTeamComments, CAPA.PoleNumber, 
       DTWC.TotalOpCount AS OpsCounterSinceLastReset, DTWC.TotalOpCount AS OperationsCounterToday, 
       DTWC.UvOpCount AS UvOperationsCounter, DTWC.OvOpCount AS OvOperationsCounter, 
       DTWC.OvUvCountResetDate AS UvOvCounterResetDate, DTWC.LastOvUvDateTime, 
       FDR.MapLocationId AS FeederIdentifier 
FROM (SELECT PAObjectId, PAOName 
      FROM YukonPAObject 
      WHERE (Type LIKE '%CBC%')) YP 
LEFT OUTER JOIN CapBank CB ON YP.PAObjectId = CB.ControlDeviceId 
LEFT OUTER JOIN YukonPAObject YP1 ON YP1.PAObjectId = CB.DeviceId 
LEFT OUTER JOIN DynamicCCCapBank DCB ON DCB.CapBankId = YP1.PAObjectId 
LEFT OUTER JOIN State S ON S.StateGroupId = 3 AND DCB.ControlStatus = S.RawState 
LEFT OUTER JOIN State SL ON SL.StateGroupId = 3 AND DCB.TwoWayCBCState = SL.RawState 
LEFT OUTER JOIN CCFeederBankList FB ON FB.DeviceId = CB.DeviceId 
LEFT OUTER JOIN YukonPAObject YP2 ON YP2.PAObjectId = FB.FeederId 
LEFT OUTER JOIN CCFeederSubAssignment SF ON FB.FeederId = SF.FeederId 
LEFT OUTER JOIN YukonPAObject YP3 ON YP3.PAObjectId = SF.SubStationBusId 
LEFT OUTER JOIN CCSubstationSubbusList SSL ON SSL.SubstationBusId = YP3.PAObjectId 
LEFT OUTER JOIN YukonPAObject YP5 ON YP5.PAObjectId = SSL.SubstationId 
LEFT OUTER JOIN CCSubAreaAssignment SA ON SA.SubstationBusId = SSL.SubstationId 
LEFT OUTER JOIN YukonPAObject YP4 ON YP4.PAObjectId = SA.AreaId 
LEFT OUTER JOIN DeviceDirectCommSettings DDCS ON DDCS.DeviceId = CB.ControlDeviceId 
LEFT OUTER JOIN DeviceAddress DA ON DA.DeviceId = YP.PAObjectId 
LEFT OUTER JOIN (SELECT EntryId, PAObjectId, Owner, InfoKey, Value, UpdateTime 
                 FROM DynamicPAOInfo 
                 WHERE (InfoKey LIKE '%udp ip%')) DPI ON DPI.PAObjectId = YP.PAObjectId 
LEFT OUTER JOIN DeviceCBC CBC ON CBC.DeviceId = CB.ControlDeviceId 
LEFT OUTER JOIN CapBankAdditional CAPA ON CAPA.DeviceId = CB.DeviceId 
LEFT OUTER JOIN DynamicCCTwoWayCBC DTWC ON CB.ControlDeviceId = DTWC.DeviceId 
LEFT OUTER JOIN CapControlFeeder FDR ON FDR.FeederId = YP2.PAObjectId
GO

/* EnhancedCCCapInventory View
 * This view is the same view as CCCapInventory_View except for it also contains the  
 * feeder identifier (FeederIdentifier).
 */
CREATE VIEW EnhancedCCCapInventory_View AS 
SELECT YP4.PAOName AS Region, CB.MapLocationId AS OpCenter, YP5.PAOName AS SubstationName, 
       YP3.PAOName AS SubbusName, YP2.PAOName AS FeederName, YP1.PAOName AS CapBankName, 
       CB.BankSize, CAPA.Latitude AS Lat, CAPA.Longitude AS LON, 
       CAPA.DriveDirections AS DriveDirection, CB.OperationalState AS OperationMethod, 
       CB.SwitchManufacture AS SWMfgr, CB.TypeOfSwitch AS SWType, YP.PAOName AS CBCName, 
       DPI.Value AS IPAddress, DA.SlaveAddress, CAPA.MaintenanceAreaId AS TA, 
       CAPA.CapBankConfig, CAPA.CommMedium, CAPA.CommStrength, 
       FDR.MapLocationId AS FeederIdentifier 
FROM CapBank CB 
INNER JOIN YukonPAObject YP1 ON YP1.PAObjectId = CB.DeviceId 
LEFT OUTER JOIN YukonPAObject YP ON CB.ControlDeviceId = YP.PAObjectId AND CB.ControlDeviceId > 0 
LEFT OUTER JOIN CCFeederBankList FB ON FB.DeviceId = CB.DeviceId 
LEFT OUTER JOIN YukonPAObject YP2 ON YP2.PAObjectId = FB.FeederId 
LEFT OUTER JOIN CCFeederSubAssignment SF ON FB.FeederId = SF.FeederId 
LEFT OUTER JOIN YukonPAObject YP3 ON YP3.PAObjectId = SF.SubStationBusId 
LEFT OUTER JOIN CCSubStationSubbusList SS ON SS.SubstationBusId = YP3.PAObjectId 
LEFT OUTER JOIN YukonPAObject YP5 ON YP5.PAObjectId = SS.SubStationId 
LEFT OUTER JOIN CCSubAreaAssignment SA ON SS.SubstationId = SA.SubstationBusId 
LEFT OUTER JOIN YukonPAObject YP4 ON YP4.PAObjectId = SA.AreaId 
LEFT OUTER JOIN DeviceAddress DA ON DA.DeviceId = CB.ControlDeviceId 
LEFT OUTER JOIN (SELECT EntryId, PAObjectId, Owner, InfoKey, Value, UpdateTime 
                 FROM DynamicPAOInfo 
                 WHERE (InfoKey LIKE '%udp ip%')) DPI ON DPI.PAObjectId = YP.PAObjectId
LEFT OUTER JOIN CapBankAdditional CAPA ON CAPA.DeviceId = CB.DeviceId 
LEFT OUTER JOIN CapControlFeeder FDR ON FDR.FeederId = YP2.PAObjectId 
GO