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
    AND lmGroup.ProtocolPriority = '3';
   
/* @end-warning checkControlPriority */

INSERT INTO DBUpdates VALUES ('YUK-25508', '9.2.0', SYSDATE);
/* @end YUK-25508 */

/***********************************************************************************/
/* VERSION INFO                                                                    */
/* Inserted when update script is run, stays commented out until the release build */
/***********************************************************************************/
/* INSERT INTO CTIDatabase VALUES ('9.2', '18-OCT-2021', 'Latest Update', 0, SYSDATE); */