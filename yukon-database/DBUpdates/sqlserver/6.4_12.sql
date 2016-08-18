/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-15352 */
UPDATE DynamicPointDispatch
SET Tags = CAST(dpd.Tags AS BIGINT) & CAST(0xFFFFFFEF AS BIGINT)
FROM DynamicPointDispatch dpd 
JOIN Point p ON p.PointId = dpd.PointId
JOIN YukonPAObject y ON y.PAObjectID = p.PAObjectID
WHERE y.DisableFlag = 'N' 
  AND CAST(dpd.Tags AS BIGINT) & 0x10 != 0;


UPDATE DynamicPointDispatch
SET Tags = CAST(dpd.Tags AS BIGINT) & CAST(0xFFFFFFFE AS BIGINT)
FROM DynamicPointDispatch dpd 
JOIN Point p ON p.PointId = dpd.PointId
JOIN YukonPAObject y ON y.PAObjectID = p.PAObjectID
WHERE p.ServiceFlag = 'N' 
  AND CAST(dpd.Tags AS BIGINT) & 0x01 != 0;
/* End YUK-15352 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('6.4', '18-AUG-2016', 'Latest Update', 12, GETDATE());