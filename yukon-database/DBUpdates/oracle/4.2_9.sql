/******************************************/
/**** Oracle DBupdates                 ****/
/******************************************/

/* Start YUK-7315 */
INSERT INTO YukonRoleProperty VALUES(-1509,-6,'Rounding Mode','HALF_EVEN','Rounding Mode used when formatting value data in billing formats. Available placeholders: HALF_EVEN, CEILING, FLOOR, UP, DOWN, HALF_DOWN, HALF_UP');
INSERT INTO YukonGroupRole VALUES(-239,-1,-6,-1509,'(none)');

ALTER TABLE DynamicBillingField ADD RoundingMode VARCHAR2(20);
UPDATE DynamicBillingField SET RoundingMode = 'HALF_EVEN';
ALTER TABLE DynamicBillingField MODIFY RoundingMode VARCHAR2(20) NOT NULL;
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
UPDATE CCEventLog 
SET Text = 'Var: , Open' 
WHERE Value = 0 
AND Text = '';

UPDATE CCEventLog 
SET Text = 'Var: , Close' 
WHERE Value = 1 
AND text = '';

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

UPDATE CCEventLog 
SET EventType = 1 
WHERE (Text LIKE '%Close Sent,%' OR 
       Text LIKE '%Open Sent,%');

UPDATE CCEventLog 
SET EventType = 0 
WHERE Text LIKE 'Var: %';

UPDATE CCEventLog 
SET EventType = 14 
WHERE EventType = 0 
AND (Text = 'OpenFail' OR 
     Text = 'CloseFail');

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

UPDATE CCEventLog 
SET Text = CONCAT('Var: ',Text) 
WHERE Text LIKE 'Non%Questionable';

UPDATE CCEventLog 
SET Text = 'Close sent, CBC Local Change' 
WHERE Text = 'ClosePending sent, CBC Local Change' 
OR Text = 'CloseFail sent, CBC Local Change' 
OR Text = 'CloseQuestionable sent, CBC Local Change';

UPDATE CCEventLog 
SET Text = 'Open sent, CBC Local Change' 
WHERE Text = 'OpenPending sent, CBC Local Change' 
OR Text = 'OpenFail sent, CBC Local Change' 
OR Text = 'OpenQuestionable sent, CBC Local Change';

INSERT INTO CCOperationLogCache
SELECT OpId, ConfId
FROM CCOperationsDSentAndValid_view
WHERE OpId NOT IN (SELECT OperationLogId
                   FROM CCOperationLogCache);

CREATE OR REPLACE VIEW CCOperationsASent_View 
AS
SELECT LogId, ActionId, PointId, Datetime, Text, FeederId, SubId, AdditionalInfo, StationId, AreaId, SPAreaId
FROM CCEventLog
WHERE text LIKE '%Close Sent,%' 
OR Text LIKE '%Open Sent,%';

CREATE OR REPLACE VIEW CCOperationsBConfirmed_view
AS
SELECT LogId, ActionId, PointId, DateTime, Text, KvarBefore, KvarAfter, KvarChange
FROM CCEventLog
WHERE Text LIKE 'Var: %';

CREATE OR REPLACE VIEW CCOperationsCOrphanedConf_view
AS
SELECT CCOAS.LogId AS OpId, MIN(CCOBC.LogId) AS ConfId 
FROM CCOperationsASent_View CCOAS 
JOIN CCOperationsBConfirmed_view CCOBC ON CCOBC.PointId = CCOAS.PointId AND CCOAS.LogId < CCOBC.LogId 
LEFT JOIN (SELECT A.LogId AS AId, MIN(B.LogID) AS NextAId 
           FROM CCOperationsASent_View A 
           JOIN CCOperationsASent_View B ON A.PointId = B.PointId AND A.LogId < B.LogId 
           GROUP BY A.LogId) EL3 ON EL3.AId = CCOAS.LogId 
WHERE EL3.NextAId IS NULL
GROUP BY CCOAS.LogId;

CREATE OR REPLACE VIEW CCOperationsDSentAndValid_view
as
SELECT CCOAS.LogId AS OpId, MIN(CCOBC.LogID) AS ConfId 
FROM CCOperationsASent_View CCOAS 
JOIN CCOperationsBConfirmed_view CCOBC ON CCOBC.PointId = CCOAS.PointId AND CCOAS.LogId < CCOBC.LogId 
LEFT JOIN (SELECT A.LogId AS AId, MIN(b.LogID) AS NextAId 
           FROM CCOperationsASent_View A 
           JOIN CCOperationsASent_View B ON A.PointId = B.PointId AND A.LogId < B.LogId 
           GROUP BY A.LogId) EL3 ON EL3.AId = CCOAS.LogId 
WHERE CCOBC.LogId < EL3.NextAId OR EL3.NextAId IS NULL
GROUP BY CCOAS.LogId;

CREATE TABLE
TempOrigCCOpView
AS
SELECT YP3.PAObjectId AS CBCId, YP3.PAOName AS CBCName, YP.PAObjectId AS CapBankId, YP.PAOName AS CapBankName, 
    CCOAS.PointId, CCOAS.LogId AS OpLogid, CCOAS.ActionId, CCOAS.DateTime AS OpTime, CCOAS.Text AS Operation, CCOBC.LogId AS ConfLogId, 
    CCOBC.ActionId AS ActionId2, CCOBC.DateTime AS ConfTime, CCOBC.Text AS ConfStatus, YP1.PAOName AS FeederName, 
    YP1.PAObjectId AS FeederId, YP2.PAOName AS SubBusName, YP2.PAObjectId AS SubBusId, 
    YP5.PAOName AS SubstationName, YP5.PAObjectId AS SubstationId, YP4.PAOName AS Region, YP4.PAObjectId AS AreaId, 
    CB.BankSize, CB.ControllerType, CCOAS.AdditionalInfo AS IPAddress, CBC.SerialNumber AS SerialNum, DA.SlaveAddress, 
    CCOBC.KvarAfter, CCOBC.KvarChange, CCOBC.KvarBefore
FROM CCOperationsASent_View CCOAS
JOIN CCOperationLogCache OpConf ON CCOAS.LogId = OpConf.OperationLogId        
LEFT JOIN CCOperationsBConfirmed_view CCOBC ON CCOBC.LogId = OpConf.ConfirmationLogId 
LEFT JOIN CCOperationsCOrphanedConf_view Orphs ON CCOAS.logid = Orphs.opid       
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

CREATE TABLE TempOrderedOpView 
AS
SELECT *
FROM TempOrigCCOpView
ORDER BY PointId, OpLogId, ConfLogId;

/* @start-block */
DECLARE
    v_pointId   NUMBER(6) :=1;
    v_oplogid    number(6) := 1;
    v_conflogid     number(6) := 1;
    v_currentPointId     number(6) := 1;
    v_actionId  number(6) := 1;
CURSOR pointid_curs is SELECT PointId, OpLogId, ConfLogId FROM TempOrderedOpView ;
BEGIN
SELECT -1 INTO v_currentPointId FROM Dual;
OPEN pointid_curs;
LOOP
    FETCH pointid_curs into v_pointId, v_oplogid, v_conflogid;
    EXIT WHEN pointid_curs%NOTFOUND;
        IF (v_currentPointId != v_pointId)
        THEN
            v_actionId := 0;
            v_currentPointId := v_pointId;
        END IF;
        UPDATE CCEventLog SET ActionId = v_actionId where LogId = v_oplogid;
        UPDATE CCEventLog SET ActionId = v_actionId where LogId = v_conflogid;
        v_actionId := v_actionId + 1;
    END LOOP;
CLOSE pointid_curs;
END;
/
/* @end-block */

CREATE OR REPLACE VIEW CCOperationsASent_View
AS
SELECT LogId, PointId, ActionId, DateTime, Text, FeederId, SubId, AdditionalInfo
FROM CCEventLog
WHERE EventType = 1
AND ActionId > -1;

CREATE OR REPLACE VIEW CCOperationsBConfirmed_View
AS
SELECT LogId, PointId, ActionId, DateTime, Text, KvarBefore, KvarAfter, KvarChange, CapBankStateInfo
FROM  CCEventLog
WHERE EventType = 0
AND ActionId > -1;

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

ALTER TABLE CCEventlog 
    ADD CONSTRAINT FK_CCEventLog_Point FOREIGN KEY(PointId) 
        REFERENCES Point (PointId);

CREATE OR REPLACE VIEW CCOperations_View
AS
SELECT YP3.PAObjectid AS CBCId, YP3.PAOName AS CBCName, YP.PAObjectid AS CapBankId, YP.PAOName AS CapBankName, 
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
AND CCOBC.Actionid >= 0
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
           WHERE (InfoKey LIKE'%udp ip%')) P ON P.PAObjectId = CB.ControlDeviceId        
LEFT JOIN CCSubstationSubbusList SSL ON SSL.SubstationBusId = CCOAS.SubId         
LEFT JOIN YukonPAObject YP5 ON YP5.PAObjectId =  SSL.SubstationBusId        
LEFT JOIN CCSubAreaAssignment CSA ON CSA.SubstationBusId = SSL.SubstationId        
LEFT JOIN YukonPAObject YP4 ON YP4.PAObjectId = CSA.AreaId;

DROP TABLE TempOrigCCOpView;
DROP TABLE TempOrderedOpView;
/* End YUK-6565 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
INSERT INTO CTIDatabase VALUES('4.2', 'Matt K', '13-MAY-2009', 'Latest Update', 9);
