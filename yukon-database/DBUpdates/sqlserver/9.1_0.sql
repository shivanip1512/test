/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-23591 */
ALTER TABLE ControlEvent
ADD ExternalEventId varchar(36);
GO

UPDATE ControlEvent
SET ExternalEventId = ControlEventId
WHERE ProgramId IN
    (SELECT DISTINCT PAObjectId from YukonPAObject
     WHERE Type IN ('HONEYWELL PROGRAM', 'ITRON PROGRAM'));

INSERT INTO DBUpdates VALUES ('YUK-23591', '9.1.0', GETDATE());
/* @end YUK-23591 */

/* @start YUK-23949 */
INSERT INTO DeviceConfigCategoryItem
SELECT ROW_NUMBER() OVER (ORDER BY DeviceConfigCategoryID) 
           + (SELECT ISNULL(MAX(DeviceConfigCategoryItemID), 1) FROM DeviceConfigCategoryItem),
       DeviceConfigCategoryID,
       'installOrientation',
       'FORWARD'
FROM DeviceConfigCategory 
WHERE CategoryType = 'regulatorCategory';

INSERT INTO DBUpdates VALUES ('YUK-23949', '9.1.0', GETDATE());
/* @end YUK-23949 */

/* @start YUK-23951 */
INSERT INTO StateGroup VALUES(-31, 'Eaton Regulator Control Mode', 'Status');
INSERT INTO State VALUES(-31, 0, 'Locked Forward', 0, 6, 0);
INSERT INTO State VALUES(-31, 1, 'Locked Reverse', 1, 6, 0);
INSERT INTO State VALUES(-31, 2, 'Reverse Idle', 2, 6, 0);
INSERT INTO State VALUES(-31, 3, 'Bidirectional', 3, 6, 0);
INSERT INTO State VALUES(-31, 4, 'Neutral Idle', 4, 6, 0);
INSERT INTO State VALUES(-31, 5, 'Cogeneration', 5, 6, 0);
INSERT INTO State VALUES(-31, 6, 'Reactive Bidirectional', 7, 6, 0);
INSERT INTO State VALUES(-31, 7, 'Bias Bidirectional', 8, 6, 0);
INSERT INTO State VALUES(-31, 8, 'Bias Cogeneration', 9, 6, 0);
INSERT INTO State VALUES(-31, 9, 'Reverse Cogeneration', 10, 6, 0);

INSERT INTO StateGroup VALUES(-32, 'Beckwith Regulator Control Mode', 'Status');
INSERT INTO State VALUES(-32, 0, 'Reverse Block', 0, 6, 0);
INSERT INTO State VALUES(-32, 1, 'Regulate Reverse', 1, 6, 0);
INSERT INTO State VALUES(-32, 2, 'Regulator Forward', 2, 6, 0);
INSERT INTO State VALUES(-32, 3, 'Return to Neutral', 3, 6, 0);
INSERT INTO State VALUES(-32, 4, 'Regulate in Reverse', 4, 6, 0);
INSERT INTO State VALUES(-32, 5, 'Distributed Generation', 5, 6, 0);
INSERT INTO State VALUES(-32, 7, 'Auto Determination', 7, 6, 0);

INSERT INTO DBUpdates VALUES ('YUK-23951', '9.1.0', GETDATE());
/* @end YUK-23951 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/* INSERT INTO CTIDatabase VALUES ('9.1', '09-SEP-2020', 'Latest Update', 0, GETDATE()); */