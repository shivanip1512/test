/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-24891 */

UPDATE Point 
SET PointName = 'Minimum Power Factor Frozen'
WHERE PointType = 'Analog' 
AND PointOffset = 495
AND PointName = 'Previous Minimum Power Factor'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN530S4X', 'RFN530S4ERX', 'RFN530S4ERXR')
);

INSERT INTO DBUpdates VALUES ('YUK-24891', '9.2.0', SYSDATE);
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

INSERT INTO DBUpdates VALUES ('YUK-25103', '9.2.0', SYSDATE);
/* @end YUK-25103 */

/* @start YUK-25214 */
INSERT INTO YukonListEntry VALUES ((SELECT MAX(EntryId)+1 FROM YukonListEntry WHERE EntryId < 10000), 1005, 0, 'LCR-6200S', 1344);

INSERT INTO DBUpdates VALUES ('YUK-25214', '9.2.0', SYSDATE);
/* @end YUK-25214 */

/* @start YUK-25507 */
UPDATE LMGroupExpressCom 
SET ProtocolPriority = 0 
WHERE ProtocolPriority = 4;

INSERT INTO DBUpdates VALUES ('YUK-25507', '9.2.0', SYSDATE);
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

INSERT INTO DBUpdates VALUES ('YUK-25508', '9.2.0', SYSDATE);
/* @end YUK-25508 */

/* @start YUK-24293 */
UPDATE CALCCOMPONENT 
SET COMPONENTTYPE='Operation' 
WHERE COMPONENTTYPE='OPERATION';

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

INSERT INTO DBUpdates VALUES ('YUK-24293', '9.2.0', SYSDATE);
/* @end YUK-24293 */

/* @start YUK-25509 */
UPDATE LMGroupExpressCom 
SET ProtocolPriority = 3 
WHERE (SELECT COUNT(*) FROM LMGroupExpressCom) = (SELECT COUNT(*) FROM LMGroupExpressCom WHERE ProtocolPriority = 1);

INSERT INTO DBUpdates VALUES ('YUK-25509', '9.2.0', SYSDATE);
/* @end YUK-25509 */

/* @start YUK-25562 */
INSERT INTO UnitMeasure VALUES ( 60,'CCF', 0, 'Centum Cubic Feet', '(none)');

INSERT INTO DBUpdates VALUES ('YUK-25562', '9.2.0', SYSDATE);
/* @end YUK-25562 */

/* @start YUK-25742 */
DELETE FROM POINTANALOG WHERE POINTID = (SELECT POINTID FROM POINT where POINTNAME = 'RFN Meter Reading Archive Requests Pushed');

DELETE FROM POINTUNIT WHERE POINTID = (SELECT POINTID FROM POINT where POINTNAME = 'RFN Meter Reading Archive Requests Pushed');

DELETE FROM POINTALARMING WHERE POINTID = (SELECT POINTID FROM POINT where POINTNAME = 'RFN Meter Reading Archive Requests Pushed');

DELETE FROM POINT WHERE POINTNAME = 'RFN Meter Reading Archive Requests Pushed';

UPDATE POINT SET POINTNAME = 'RFN Meter Reading Archive Point Data Generated Count' WHERE POINTNAME = 'Rfn Meter Reading Archive Requests Pt Data Generated Count';

INSERT INTO DBUpdates VALUES ('YUK-25742', '9.2.0', SYSDATE);
/* @end YUK-25742 */

/* @start YUK-25614 */

/* @error ignore-begin */
/* This will almost definitely error, but it's a necessary safety check */
TRUNCATE TABLE t_OutageCalcValuesTemp;
DROP TABLE t_OutageCalcValuesTemp;
/* @error ignore-end */
CREATE GLOBAL TEMPORARY TABLE t_OutageCalcValuesTemp
(
   POINTID              NUMBER                          not null,
   TIMESTAMP            DATE                            not null,
   QUALITY              NUMBER                          not null,
   VALUE                FLOAT                           not null,
) ON COMMIT PRESERVE ROWS;

/* Calculate the new values into the temp table */
INSERT INTO t_OutageCalcValuesTemp
SELECT
    outTotal.POINTID,
    SYSDATE,
    CASE
/* 5 = Normal quality */
        WHEN (dpdOutBlink.QUALITY=5 AND dpdOutCount.QUALITY=5 AND dpdResBlink.QUALITY=5) THEN 5
/* 18 = Estimated quality */
        ELSE 18
    END,
    dpdOutCount.VALUE + dpdResBlink.VALUE + NVL(dpdOutBlink.VALUE, 0)
FROM 
    POINT outTotal 
        JOIN YukonPAObject y 
            ON y.PAObjectID=outTotal.PAObjectID
        JOIN CALCBASE cb
            ON cb.POINTID=outTotal.POINTID
        JOIN CALCCOMPONENT cc1 ON cc1.PointID=cb.POINTID and cc1.COMPONENTORDER=1 and cc1.COMPONENTTYPE='Operation' and cc1.OPERATION='+'
        JOIN CALCCOMPONENT cc2 ON cc2.PointID=cb.POINTID and cc2.COMPONENTORDER=2 and cc2.COMPONENTTYPE='Operation' and cc2.OPERATION='+'
        JOIN CALCCOMPONENT cc3 ON cc3.PointID=cb.POINTID and cc3.COMPONENTORDER=3 and cc3.COMPONENTTYPE='Operation' and cc3.OPERATION='+'
        JOIN POINT outBlink ON outBlink.POINTID=cc1.COMPONENTPOINTID and outBlink.POINTTYPE='Analog' and outBlink.POINTOFFSET='20'
        JOIN POINT outCount ON outCount.POINTID=cc2.COMPONENTPOINTID and outCount.POINTTYPE='Analog' and outCount.POINTOFFSET='22'
        JOIN POINT resBlink ON resBlink.POINTID=cc3.COMPONENTPOINTID and resBlink.POINTTYPE='Analog' and resBlink.POINTOFFSET='21'
        JOIN DYNAMICPOINTDISPATCH dpdOutTotal on outTotal.POINTID=dpdOutTotal.POINTID
        JOIN DYNAMICPOINTDISPATCH dpdOutCount on outCount.POINTID=dpdOutCount.POINTID
        JOIN DYNAMICPOINTDISPATCH dpdResBlink on resBlink.POINTID=dpdResBlink.POINTID
        LEFT JOIN DYNAMICPOINTDISPATCH dpdOutBlink on outBlink.POINTID=dpdOutBlink.POINTID
WHERE
    outTotal.POINTTYPE='CalcAnalog'
        AND outTotal.POINTOFFSET='0'
        AND (SELECT COUNT(PointID) FROM CALCCOMPONENT WHERE PointID=outTotal.POINTID)=3
        AND dpdOutTotal.QUALITY != 0
        AND dpdOutCount.QUALITY != 0
        AND dpdResBlink.QUALITY != 0
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
            'RFN-530S4eAX', 'RFN-530S4eAXR', 'RFN-530S4eRX', 'RFN-530S4eRXR');

/* Update DynamicPointDispatch with the new value */
UPDATE 
    dpd
SET
    dpd.TIMESTAMP = 
        t.TIMESTAMP,
    dpd.QUALITY =
        t.QUALITY,
    dpd.VALUE =
        t.VALUE, 
    dpd.TAGS =
        0, 
    dpd.NEXTARCHIVE =
        '2038-01-15 00:00:00', 
    dpd.millis =
        0
FROM
    DYNAMICPOINTDISPATCH dpd
        JOIN t_OutageCalcValuesTemp t on dpd.POINTID=t.POINTID;

/* @start-block */
DECLARE
    v_maxChangeId NUMBER;
BEGIN
    SELECT NVL(MAX(CHANGEID), 0) INTO v_maxChangeId FROM RAWPOINTHISTORY;

/* Insert the new values into RPH */
    INSERT INTO RAWPOINTHISTORY (
        CHANGEID,
        POINTID, 
        TIMESTAMP, 
        QUALITY, 
        VALUE, 
        millis)
    SELECT 
        @maxChangeId + ROW_NUMBER() OVER (ORDER BY POINTID),
        POINTID, 
        TIMESTAMP,
        QUALITY,
        VALUE,
        0
    FROM 
        t_OutageCalcValuesTemp;

END;
/* @end-block */

/* Clean up */
TRUNCATE TABLE t_OutageCalcValuesTemp;
DROP TABLE t_OutageCalcValuesTemp;

INSERT INTO DBUpdates VALUES ('YUK-25614', '9.2.0', SYSDATE);
/* @end YUK-25614 */

/***********************************************************************************/
/* VERSION INFO                                                                    */
/* Inserted when update script is run, stays commented out until the release build */
/***********************************************************************************/
/* INSERT INTO CTIDatabase VALUES ('9.2', '18-OCT-2021', 'Latest Update', 0, SYSDATE); */