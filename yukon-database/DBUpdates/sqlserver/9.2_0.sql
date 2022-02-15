/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-24891 */

UPDATE p
SET p.PointName = 'Minimum Power Factor Frozen'
FROM POINT p 
    JOIN YukonPAObject y ON p.PAObjectID=y.PAObjectID
WHERE p.PointType = 'Analog' 
    AND p.PointOffset = 495
    AND p.PointName = 'Previous Minimum Power Factor'
    AND y.Type IN ('RFN530S4X', 'RFN530S4ERX', 'RFN530S4ERXR');
    
INSERT INTO DBUpdates VALUES ('YUK-24891', '9.2.0', GETDATE());

/* @end YUK-24891 */

/* @start YUK-25103 */
UPDATE Theme
SET Name = 'Default'
WHERE ThemeId = -1;

UPDATE ThemeProperty
SET Value = '#007bc1'
WHERE ThemeId = -1
AND Property = 'BUTTON_COLOR';

UPDATE ThemeProperty
SET Value = '#007bc1'
WHERE ThemeId = -1
AND Property = 'BUTTON_COLOR_BORDER';

UPDATE ThemeProperty
SET Value = '#4da3d4'
WHERE ThemeId = -1
AND Property = 'BUTTON_COLOR_HOVER';

UPDATE ThemeProperty
SET Value = '#007bc1'
WHERE ThemeId = -1
AND Property = 'PRIMARY_COLOR';

UPDATE ThemeProperty
SET Value = '#007bc1'
WHERE ThemeId = -1
AND Property = 'VISITED_COLOR';

UPDATE ThemeProperty
SET Value = '#727e84'
WHERE ThemeId = -1
AND Property = 'PAGE_BACKGROUND';

UPDATE ThemeProperty
SET Value = '#727e84'
WHERE ThemeId = -1
AND Property = 'PAGE_BACKGROUND_SHADOW';

UPDATE ThemeProperty
SET Value = '#727e84'
WHERE ThemeId = -1
AND Property = 'LOGIN_FONT_SHADOW';

INSERT INTO DBUpdates VALUES ('YUK-25103', '9.2.0', GETDATE());

/* @end YUK-25103 */

/* @start YUK-25214 */
INSERT INTO YukonListEntry VALUES ((SELECT MAX(EntryId)+1 FROM YukonListEntry WHERE EntryId < 10000), 1005, 0, 'LCR-6200S', 1344);

INSERT INTO DBUpdates VALUES ('YUK-25214', '9.2.0', GETDATE());
/* @end YUK-25214 */

/* @start YUK-25507 */
UPDATE LMGroupExpressCom 
SET ProtocolPriority = 0 
WHERE ProtocolPriority = 4;

INSERT INTO DBUpdates VALUES ('YUK-25507', '9.2.0', GETDATE());
/* @end YUK-25507 */

/* @start YUK-25508 */
/* @start-warning checkControlPriority Yukon has updated Control priority for Expresscom Group & RFN Expresscom Group load groups in YUK-25507. */
SELECT DISTINCT(ypo.PAOName) FROM 
    YukonPAObject ypo INNER JOIN EventLog eventLog ON eventLog.String1 = ypo.PAOName JOIN 
    LMGroupExpressCom lmGroup ON lmGroup.LMGroupID = ypo.PAObjectID WHERE
    eventLog.EventType IN('dr.setup.loadGroup.loadGroupCreated', 'dr.setup.loadGroup.loadGroupUpdated') AND
    eventLog.String2 IN('LM_GROUP_EXPRESSCOMM', 'LM_GROUP_RFN_EXPRESSCOMM')
    AND lmGroup.ProtocolPriority != '3';
   
/* @end-warning checkControlPriority */

INSERT INTO DBUpdates VALUES ('YUK-25508', '9.2.0', GETDATE());
/* @end YUK-25508 */

/* @start YUK-24293 */
UPDATE CALCCOMPONENT 
SET COMPONENTTYPE='Operation' 
WHERE CONVERT(VARBINARY, COMPONENTTYPE)=CONVERT(VARBINARY, 'OPERATION');

INSERT INTO CALCCOMPONENT (
    PointID, 
    COMPONENTORDER, 
    COMPONENTTYPE, 
    COMPONENTPOINTID, 
    OPERATION, 
    CONSTANT, 
    FUNCTIONNAME)
SELECT 
    OutageCalcPoint.POINTID, 
    3, 
    'Operation', 
    RestoreBlink.POINTID, 
    '+', 
    0, 
    '(none)'
FROM 
    POINT OutageCalcPoint 
        JOIN YukonPAObject y 
            ON y.PAObjectID=OutageCalcPoint.PAObjectID
        JOIN CALCBASE cb
            ON cb.POINTID=OutageCalcPoint.POINTID
        JOIN CALCCOMPONENT cc
            ON cc.PointID=cb.POINTID
        JOIN CALCCOMPONENT cc1
            ON cc1.PointID=cb.POINTID
                and cc1.COMPONENTORDER=1
                and cc1.COMPONENTTYPE='Operation'
                and cc1.OPERATION='+'
        JOIN POINT ccp1
            ON ccp1.POINTID=cc1.COMPONENTPOINTID
                and ccp1.POINTTYPE='Analog'
                and ccp1.POINTOFFSET='20'
        JOIN CALCCOMPONENT cc2
            ON cc2.PointID=cb.POINTID
                and cc2.COMPONENTORDER=2
                and cc2.COMPONENTTYPE='Operation'
                and cc2.OPERATION='+'
        JOIN POINT ccp2
            ON ccp2.POINTID=cc2.COMPONENTPOINTID
                and ccp2.POINTTYPE='Analog'
                and ccp2.POINTOFFSET='22'
        JOIN POINT RestoreBlink
            ON RestoreBlink.PAObjectID=y.PAObjectID
                and RestoreBlink.POINTTYPE='Analog'
                and RestoreBlink.POINTOFFSET='21'
WHERE
    OutageCalcPoint.POINTTYPE='CalcAnalog'
        AND OutageCalcPoint.POINTOFFSET='0'
        AND y.Type in (
            'RFN-410fL', 'RFN-410fX', 'RFN-410fD',
            'RFN-420fL', 'RFN-420fX', 'RFN-420fD', 'RFN-420fRX', 'RFN-420fRD',
            'RFN-410cL',
            'RFN-420cL', 'RFN-420cD',
            'WRL-420cL', 'WRL-420cD',
            'RFN-430A3D', 'RFN-430A3T', 'RFN-430A3K', 'RFN-430A3R',
            'RFN-430KV',
            'RFN-430SL0', 'RFN-430SL1', 'RFN-430SL2', 'RFN-430SL3', 'RFN-430SL4',
            'RFN-440-2131TD', 'RFN-440-2132TD', 'RFN-440-2133TD',
            'RFN-510fL',
            'RFN-520fAX', 'RFN-520fRX', 'RFN-520fAXD', 'RFN-520fRXD',
            'RFN-520fAXe', 'RFN-520fRXe', 'RFN-520fAXeD', 'RFN-520fRXeD',
            'RFN-530fAX', 'RFN-530fRX', 'RFN-530fAXe', 'RFN-530fRXe',
            'RFN-530S4x',
            'RFN-530S4eAX', 'RFN-530S4eAXR', 'RFN-530S4eRX', 'RFN-530S4eRXR')
GROUP BY 
    OutageCalcPoint.POINTID,
    RestoreBlink.POINTID
HAVING
    COUNT(cc.PointID) = 2;

INSERT INTO DBUpdates VALUES ('YUK-24293', '9.2.0', GETDATE());
/* @end YUK-24293 */

/* @start YUK-25509 */
UPDATE LMGroupExpressCom 
SET ProtocolPriority = 3 
WHERE (SELECT COUNT(*) FROM LMGroupExpressCom) = (SELECT COUNT(*) FROM LMGroupExpressCom WHERE ProtocolPriority = 1);

INSERT INTO DBUpdates VALUES ('YUK-25509', '9.2.0', GETDATE());
/* @end YUK-25509 */

/* @start YUK-25562 */
INSERT INTO UnitMeasure VALUES ( 60,'CCF', 0, 'Centum Cubic Feet', '(none)');

INSERT INTO DBUpdates VALUES ('YUK-25562', '9.2.0', GETDATE());
/* @end YUK-25562 */

/* @start YUK-25792 */
DROP INDEX INDX_EventType ON EventLog;
GO

CREATE INDEX INDX_EventLog_EvntTime_EvntLogId_EvntType ON EventLog (
EventTime DESC,
EventLogId DESC,
EventType ASC
);
GO

INSERT INTO DBUpdates VALUES ('YUK-25792', '9.2.0', GETDATE());
/* @end YUK-25792 */

/* @start YUK-25754 */
ALTER TABLE DeviceDataMonitor 
ADD NotifyOnAlarmOnly CHAR(1) NULL;
GO

UPDATE DeviceDataMonitor 
SET NotifyOnAlarmOnly = 0; -- disable
GO

ALTER TABLE DeviceDataMonitor 
ALTER COLUMN NotifyOnAlarmOnly CHAR(1) NOT NULL;
GO

ALTER TABLE StatusPointMonitorProcessor 
ADD NotifyOnAlarmOnly CHAR(1);
GO

UPDATE StatusPointMonitorProcessor 
SET NotifyOnAlarmOnly = 0; -- disable
GO

ALTER TABLE StatusPointMonitorProcessor 
ALTER COLUMN NotifyOnAlarmOnly CHAR(1) NOT NULL;
GO

INSERT INTO DBUpdates VALUES ('YUK-25754', '9.2.0', GETDATE());
/* @end YUK-25754 */

/* @start YUK-25874 */
/* @error ignore-begin */
DROP INDEX INDX_EventLog_EvntTime_EvntLogID ON EventLog;
GO
/* @error ignore-end */

INSERT INTO DBUpdates VALUES ('YUK-25874', '9.2.0', GETDATE());
/ @error ignore-end */
/* @end YUK-25874 */

/***********************************************************************************/
/* VERSION INFO                                                                    */
/* Inserted when update script is run, stays commented out until the release build */
/***********************************************************************************/
/* INSERT INTO CTIDatabase VALUES ('9.2', '18-OCT-2021', 'Latest Update', 0, GETDATE()); */
