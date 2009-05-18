/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

/* Start YUK-7315 */
INSERT INTO YukonRoleProperty VALUES(-1509,-6,'Rounding Mode','HALF_EVEN','Rounding Mode used when formatting value data in billing formats. Available placeholders: HALF_EVEN, CEILING, FLOOR, UP, DOWN, HALF_DOWN, HALF_UP');
INSERT INTO YukonGroupRole VALUES(-239,-1,-6,-1509,'(none)');

ALTER TABLE DynamicBillingField ADD RoundingMode VARCHAR(20);
GO
UPDATE DynamicBillingField SET RoundingMode = 'HALF_EVEN';
GO
ALTER TABLE DynamicBillingField ALTER COLUMN RoundingMode VARCHAR(20) NOT NULL;
GO
/* End YUK-7315 */

/* Start YUK-7257 */
/* @error ignore-begin */
DROP INDEX INDX_LMThermSeaEnt_SeaId;
CREATE INDEX INDX_LMThermSeaEntry_SeaId ON LMThermostatSeasonEntry (
   SeasonId ASC
);
/* @error ignore-end */
/* End YUK-7257 */

/* Start YUK-7452 */
INSERT INTO FDRInterfaceOption VALUES(28, 'Multiplier', 5, 'Text', '1.0');
/* End YUK-7452 */

/* Start YUK-6565 */
DROP VIEW CCOperationsASent_view;
GO
DROP VIEW CCOperationsBConfirmed_view;
GO
DROP VIEW CCOperations_View;
GO

UPDATE CCEventLog 
SET Text = 'Var: , Open' 
WHERE Value = 0 
AND Text = '';

UPDATE CCEventLog 
SET Text = 'Var: , Close' 
WHERE Value = 1 
AND Text = '';

UPDATE CCEventLog 
SET Text = 'Var: , OpenQuestionable' 
WHERE Value = 2 
AND Text = '';

UPDATE CCEventLog 
SET Text = 'Var: , CloseQuestionable' 
WHERE Value = 3 
AND Text = '';

UPDATE CCEventLog 
SET Text = 'Var: , OpenFail' 
WHERE Value = 4 
AND Text = '';

UPDATE CCEventLog 
SET Text = 'Var: , CloseFail' 
WHERE Value = 5 
AND Text = '';

UPDATE CCEventLog 
SET Text = 'Var: , Unknown' 
WHERE Value >= 8 
AND Text = '';
GO

UPDATE CCEventLog 
SET Text = 'Var: ' + Text 
WHERE Text LIKE 'Non%Questionable';
GO

UPDATE CCEventLog 
SET Text = 'Close Sent, CBC Local Change'
WHERE Text = 'ClosePending Sent, CBC Local Change' 
OR Text = 'CloseFail Sent, CBC Local Change'
OR Text = 'CloseQuestionable Sent, CBC Local Change';

UPDATE CCEventLog 
SET Text = 'Open Sent, CBC Local Change'
WHERE Text = 'OpenPending Sent, CBC Local Change' 
OR Text = 'OpenFail Sent, CBC Local Change'
OR Text = 'OpenQuestionable Sent, CBC Local Change';
GO

UPDATE CCEventLog 
SET EventType = 1 
WHERE (Text LIKE '%Close sent,%' 
       OR Text LIKE '%Open sent,%');

UPDATE CCEventLog 
SET EventType = 0 
WHERE Text LIKE 'Var: %';
GO

UPDATE CCEventLog 
SET EventType = 14 
WHERE EventType = 0 
AND (Text = 'OpenFail' 
     OR Text = 'CloseFail');
GO

UPDATE CCEventLog 
SET EventType = 14 
WHERE Text LIKE 'Resending%';

UPDATE CCEventLog 
SET EventType = 2 
WHERE Text LIKE 'Manual Scan%';

UPDATE CCEventLog 
SET EventType = 2 
WHERE Text LIKE 'CapBank%Manual State Change%';

UPDATE CCEventLog 
SET EventType = 2 
WHERE Text LIKE 'Manual Confirm%';
GO

CREATE VIEW CCOperationsASent_view
AS
SELECT LogId, ActionId, PointId, DateTime, Text, FeederId, SubId, AdditionalInfo, StationId, AreaId, SpAreaId
FROM CCEventLog
WHERE Text LIKE '%Close sent,%' 
OR Text LIKE '%Open sent,%';
GO

CREATE VIEW CCOperationsBConfirmed_view
AS
SELECT LogId, ActionId, PointId, DateTime, Text, KvarBefore, KvarAfter, KvarChange
FROM CCEventLog
WHERE Text LIKE 'Var: %';
GO

INSERT INTO CCOperationLogCache
SELECT OpId, ConfId
FROM CCOperationsDSentAndValid_view
WHERE OpId NOT IN (SELECT OperationLogId
                   FROM CCOperationLogCache);
GO

/* @start-block */
SELECT YP3.PAObjectid AS CBCId, YP3.PAOName AS CBCName, YP.PAObjectid AS CapBankId, 
       YP.PAOName AS CapBankName, CCOAS.PointId, CCOAS.LogId AS OpLogId, CCOAS.ActionId, CCOAS.DateTime AS OpTime, CCOAS.Text AS Operation, 
       CCOBC.LogId AS ConfLogId, CCOBC.ActionId as ActionId2, CCOBC.DateTime AS ConfTime, CCOBC.Text AS ConfStatus, YP1.PAOName AS FeederName, 
       YP1.PAObjectId AS FeederId, YP2.PAOName AS SubBusName, YP2.PAObjectId AS SubBusId, 
       YP5.PAOName AS SubstationName, YP5.PAObjectId AS SubstationId, 
       YP4.PAOName AS Region, YP4.PAObjectId AS AreaId, CB.BankSize, CB.ControllerType, 
       CCOAS.AdditionalInfo AS IPAddress, CBC.SerialNumber AS SerialNum, DA.SlaveAddress, 
       CCOBC.KvarAfter, CCOBC.KvarChange, CCOBC.KvarBefore
INTO #originalCCoperationsView 
FROM CCOperationsASent_view  CCOAS
JOIN CCOperationLogCache OpConf ON CCOAS.LogId = OpConf.OperationLogId        
LEFT JOIN CCOperationsBConfirmed_view CCOBC ON CCOBC.LogId = OpConf.ConfirmationLogId 
LEFT JOIN CCOperationsCOrphanedConf_View Orphs ON CCOAS.LogId = Orphs.OpId       
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
LEFT JOIN YukonPAObject YP5 ON YP5.PAObjectId =  SSL.SubstationBusId        
LEFT JOIN CCSubAreaAssignment CSA ON CSA.SubstationBusId = SSL.SubstationId        
LEFT JOIN YukonPAObject YP4 ON YP4.PAObjectId = CSA.AreaId;

UPDATE CCEventLog SET ActionId = -1;
GO

SELECT * 
INTO #originalCCOperationsViewOrderd 
FROM #originalCCoperationsView
ORDER BY PointId, OpLogId, ConfLogId;
GO

DECLARE @pointId NUMERIC;
DECLARE @oplogid NUMERIC;
DECLARE @conflogid NUMERIC;
DECLARE @currentPointId NUMERIC;
DECLARE @actionId NUMERIC;
DECLARE pointid_curs CURSOR FOR (SELECT PointId, OpLogId, ConfLogId
							     FROM #originalCCoperationsViewOrderd );

OPEN pointid_curs;
FETCH pointid_curs INTO @pointId, @oplogid, @conflogid;
SET @currentPointId = -1;
WHILE (@@fetch_status = 0)
	BEGIN
	    IF (@currentPointId != @pointId)
			BEGIN
				SET @actionId = 0;
				SET @currentPointId = @pointId;
			END
		UPDATE CCEventLog SET actionid = @actionId where logid = @oplogid;
		UPDATE CCEventLog SET actionid = @actionId where logid = @conflogid;
		SET @actionId = @actionId + 1;
		FETCH pointid_curs INTO @pointId, @oplogid, @conflogid;
	END
CLOSE pointid_curs;
DEALLOCATE pointid_curs;
/* @end-block */

DROP VIEW CCOperationsASent_View;
DROP VIEW CCOperationsBConfirmed_View;
GO

/* @error ignore-begin */
/* We want to use schema binding if possible.  This will give us a performance boost. */
SET NUMERIC_ROUNDABORT OFF;
SET ANSI_PADDING, ANSI_WARNINGS, CONCAT_NULL_YIELDS_NULL, ARITHABORT ON;
SET QUOTED_IDENTIFIER, ANSI_NULLS ON;
GO
CREATE VIEW [dbo].[CCOperationsASent_View] with schemabinding
AS
SELECT LogId, PointId, ActionId, DateTime, Text, FeederId, SubId, AdditionalInfo
FROM dbo.CCEventLog
WHERE EventType = 1
AND ActionId > -1;
GO

SET NUMERIC_ROUNDABORT OFF;
SET ANSI_PADDING, ANSI_WARNINGS, CONCAT_NULL_YIELDS_NULL, ARITHABORT ON;
SET QUOTED_IDENTIFIER, ANSI_NULLS ON;
GO
CREATE VIEW [dbo].[CCOperationsBConfirmed_View] with schemabinding
AS
SELECT LogId, PointId, ActionId, DateTime, Text, KvarBefore, KvarAfter, KvarChange, CapBankStateInfo
FROM dbo.CCEventLog
WHERE EventType = 0
AND ActionId > -1;
GO

CREATE VIEW CCOperationsASent_View
AS
SELECT LogId, PointId, ActionId, DateTime, Text, FeederId, SubId, AdditionalInfo
FROM CCEventLog
WHERE EventType = 1
AND ActionId > -1;
GO

CREATE VIEW CCOperationsBConfirmed_View
AS
SELECT LogId, PointId, ActionId, DateTime, Text, KvarBefore, KvarAfter, KvarChange, CapBankStateInfo
FROM CCEventLog
WHERE EventType = 0
AND ActionId > -1;
GO

CREATE UNIQUE CLUSTERED INDEX INDX_CCOperASent_LogId ON CCOperationsASent_View (
	LogId ASC
);
CREATE UNIQUE CLUSTERED INDEX INDX_CCOperBConf_LogId ON CCOperationsBConfirmed_View (
	LogId ASC
);
CREATE INDEX INDX_CCOperASent_PointId ON CCOperationsASent_View (
	PointId ASC
);
CREATE INDEX INDX_CCOperBConf_PointId ON CCOperationsBConfirmed_View (
	PointId ASC
);
CREATE INDEX INDX_CCOperASent_ActId ON CCOperationsASent_View (
	ActionId ASC
);
CREATE INDEX INDX_CCOperBConf_ActId ON CCOperationsBConfirmed_View (
	ActionId ASC
);
CREATE INDEX INDX_CCOperASent_FeedId ON CCOperationsASent_View (
	FeederId ASC
);
CREATE INDEX INDX_CCOperASent_SubId ON CCOperationsASent_View (
	SubId ASC
);
GO
/* @error ignore-end */

CREATE INDEX INDX_CCEventLog_PointId_ActId ON CCEventLog (
	PointId ASC, 
	ActionId ASC
);
CREATE INDEX INDX_CCEventLog_ActId ON CCEventLog (
	ActionId ASC
);
CREATE INDEX INDX_CCEventLog_PointId ON CCEventLog (
	PointId ASC
);
CREATE INDEX INDX_CCEventLog_FeedId ON CCEventLog (
	FeederId ASC
);
CREATE INDEX INDX_CCEventLog_SubId ON CCEventLog (
	SubId ASC
);
GO

DELETE FROM CCEventLog
WHERE PointId IS NULL;
GO

ALTER TABLE CCEventlog 
    ADD CONSTRAINT FK_CCEventLog_Point FOREIGN KEY(PointId) 
        REFERENCES Point (PointId)
            ON DELETE CASCADE;
GO

CREATE VIEW CCOperations_View
AS
SELECT YP3.PAObjectId AS CBCId, YP3.PAOName AS CBCName, YP.PAObjectId AS CapBankId, YP.PAOName AS CapBankName, 
       CCOAS.PointId, CCOAS.LogId AS OpLogId, CCOAS.ActionId, CCOAS.DateTime AS OpTime, CCOAS.Text AS Operation, 
       CCOBC.LogId AS ConfLogId, CCOBC.ActionId AS ActionId2, CCOBC.DateTime AS ConfTime, CCOBC.Text AS ConfStatus, 
       YP1.PAOName AS FeederName, YP1.PAObjectId AS FeederId, YP2.PAOName AS SubBusName, YP2.PAObjectId AS SubBusId, 
       YP5.PAOName AS SubstationName, YP5.PAObjectId AS SubstationId, YP4.PAOName AS Region, YP4.PAObjectId AS AreaId,
       CB.BankSize, CB.ControllerType, CCOAS.AdditionalInfo AS IPAddress, CBC.SerialNumber AS SerialNum, DA.SlaveAddress, 
       CCOBC.KvarAfter, CCOBC.KvarChange, CCOBC.KvarBefore
FROM CCOperationsASent_View CCOAS
JOIN CCOperationsBConfirmed_view CCOBC ON CCOBC.ActionId = CCOAS.ActionId 
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
LEFT JOIN YukonPAObject YP5 ON YP5.PAObjectId =  SSL.SubstationBusId        
LEFT JOIN CCSubAreaAssignment CSA ON CSA.SubstationBusId = SSL.SubstationId        
LEFT JOIN YukonPAObject YP4 ON YP4.PAObjectId = CSA.AreaId;
/* End YUK-6565 */

/* Start YUK-7477 */
DELETE FROM FDRInterfaceOption WHERE InterfaceId = 20;

INSERT INTO FDRInterfaceOption Values(20, 'Coop Id', 1, 'Text','(none)');
INSERT INTO FDRInterfaceOption Values(20, 'Filename', 2, 'Text','(none)');
/* End YUK-7477 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
INSERT INTO CTIDatabase VALUES('4.2', 'Matt K', '15-MAY-2009', 'Latest Update', 9);
