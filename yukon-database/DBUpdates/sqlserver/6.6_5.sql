/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-16411 */
INSERT INTO YukonListEntry VALUES ((SELECT MAX(EntryId)+1 FROM YukonListEntry WHERE EntryId < 10000), 1005, 0, 'ecobee3 Lite', 1336);
/* End YUK-16411 */

/* Start YUK-16537 */
UPDATE Point 
SET PointOffset = 321, PointName = 'Delivered kVAr'
WHERE PointType = 'Analog' 
AND PointOffset = 347
AND PointName = 'kVAr'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-430A3R')
);
/* End YUK-16537 */


/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.6', '20-MARCH-2017', 'Latest Update', 5, GETDATE());*/
