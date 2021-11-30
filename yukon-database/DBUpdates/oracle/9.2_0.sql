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

/***********************************************************************************/
/* VERSION INFO                                                                    */
/* Inserted when update script is run, stays commented out until the release build */
/***********************************************************************************/
/* INSERT INTO CTIDatabase VALUES ('9.2', '18-OCT-2021', 'Latest Update', 0, SYSDATE); */