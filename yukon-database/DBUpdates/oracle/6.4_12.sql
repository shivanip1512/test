/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-15352 */
UPDATE DynamicPointDispatch
SET Tags = BITAND(CAST(Tags AS NUMBER),  TO_NUMBER('FFFFFFEF', 'xxxxxxxx'))
WHERE PointId IN (
    SELECT dpd.PointId
    FROM DynamicPointDispatch dpd
    JOIN Point p ON p.PointId = dpd.PointId
    JOIN YukonPAObject y ON y.PAObjectID = p.PAObjectID
    WHERE y.DisableFlag = 'N' 
      AND BITAND(CAST(dpd.Tags AS NUMBER), TO_NUMBER('10', 'xx')) != 0);

UPDATE DynamicPointDispatch
SET Tags = BITAND(CAST(Tags AS NUMBER), TO_NUMBER('FFFFFFFE', 'xxxxxxxx'))
WHERE PointId IN (
    SELECT dpd.PointId
    FROM DynamicPointDispatch dpd
    JOIN Point p ON p.PointId = dpd.PointId
    JOIN YukonPAObject y ON y.PAObjectID = p.PAObjectID
    WHERE p.ServiceFlag = 'N'
      AND BITAND(CAST(dpd.Tags AS NUMBER), 1) != 0);
/* End YUK-15352 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.4', '30-JUN-2016', 'Latest Update', 12, SYSDATE);*/