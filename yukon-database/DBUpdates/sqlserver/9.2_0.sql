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
    
INSERT INTO DBUpdates VALUES ('YUK-24891', '9.1.0', GETDATE());

/* @end YUK-24891 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT CTIDatabase VALUES ('9.2', '27-SEP-2021', 'Latest Update', 0, GETDATE());
