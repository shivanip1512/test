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

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('9.2', '27-SEP-2021', 'Latest Update', 0, SYSDATE);