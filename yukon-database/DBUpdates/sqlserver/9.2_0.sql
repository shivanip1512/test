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

/***********************************************************************************/
/* VERSION INFO                                                                    */
/* Inserted when update script is run, stays commented out until the release build */
/***********************************************************************************/
/* INSERT INTO CTIDatabase VALUES ('9.2', '18-OCT-2021', 'Latest Update', 0, GETDATE()); */
