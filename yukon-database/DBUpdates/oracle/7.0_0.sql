/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-17045 */
INSERT INTO YukonListEntry 
SELECT (SELECT MAX(EntryId) + 1 
        FROM   YukonListEntry 
        WHERE  EntryId < 10000), 
       1005, 
       0, 
       'LCR-6700(RFN)', 
       1337 
FROM   dual 
WHERE  NOT EXISTS (SELECT * 
                   FROM   YukonListEntry 
                   WHERE  EntryText = 'LCR-6700(RFN)'); 
/* End YUK-17045 */

/* Start YUK-17051 */
ALTER TABLE DISPLAY2WAYDATA DROP CONSTRAINT FK_Display2WayData_Display;
ALTER TABLE DISPLAYCOLUMNS DROP CONSTRAINT FK_DisplayColumns_Display;
ALTER TABLE TemplateDisplay DROP CONSTRAINT FK_TemplateDisplay_DISPLAY;

ALTER TABLE DISPLAY2WAYDATA
   ADD CONSTRAINT FK_Display2WayData_Display FOREIGN KEY (DISPLAYNUM)
      REFERENCES DISPLAY (DISPLAYNUM)
      ON DELETE CASCADE;

ALTER TABLE DISPLAYCOLUMNS
   ADD CONSTRAINT FK_DisplayColumns_Display FOREIGN KEY (DISPLAYNUM)
      REFERENCES DISPLAY (DISPLAYNUM)
      ON DELETE CASCADE;

ALTER TABLE TemplateDisplay
   ADD CONSTRAINT FK_TemplateDisplay_DISPLAY FOREIGN KEY (DisplayNum)
      REFERENCES DISPLAY (DISPLAYNUM)
      ON DELETE CASCADE;
/* End YUK-17051 */

/* Start YUK-17096 */
DROP INDEX INDX_CCEventLog_PointId;
/* End YUK-17096 */

/* Start YUK-17113 */
CREATE TABLE DBUpdates  (
   UpdateId             VARCHAR2(50)                    NOT NULL,
   Version              VARCHAR2(10),
   InstallDate          DATE,
   CONSTRAINT PK_DBUPDATES PRIMARY KEY (UpdateId)
);
/* End YUK-17113 */

/* Start YUK-17066 */
UPDATE 
(SELECT PU.decimalplaces FROM POINT P 
    JOIN YukonPAObject YPO ON P.PAObjectID = YPO.PAObjectID
    JOIN POINTUNIT PU ON PU.POINTID = P.POINTID 
WHERE YPO.Type IN ('RFW-201', 'RFW-205') AND P.POINTTYPE = 'Analog' 
    AND P.POINTOFFSET = 6 AND PU.DECIMALPLACES = 3) t 
SET t.decimalplaces = 0;
/* End YUK-17066 */

/* Start YUK-17159 */
UPDATE ExtraPaoPointAssignment
SET Attribute = 'VOLTAGE'
WHERE Attribute = 'VOLTAGE_Y';

UPDATE ExtraPaoPointAssignment
SET Attribute = 'SOURCE_VOLTAGE'
WHERE Attribute = 'VOLTAGE_X';

CREATE INDEX INDX_EPPA_PointId ON ExtraPaoPointAssignment (
    PointId ASC
);
/* End YUK-17159 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.0', '01-AUG-2017', 'Latest Update', 0, SYSDATE);*/
