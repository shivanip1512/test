/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-17045 */
IF( (SELECT COUNT(*) 
     FROM   YukonListEntry 
     WHERE  EntryText = 'LCR-6700(RFN)') = 0 ) 
  INSERT INTO YukonListEntry 
  VALUES      ((SELECT MAX(EntryId) + 1 
                FROM   YukonListEntry 
                WHERE  EntryId < 10000), 
               1005, 
               0, 
               'LCR-6700(RFN)', 
               1337); 
/* End YUK-17045 */

/* Start YUK-17051 */
ALTER TABLE DISPLAY2WAYDATA DROP CONSTRAINT FK_Display2WayData_Display;
ALTER TABLE DISPLAYCOLUMNS DROP CONSTRAINT FK_DisplayColumns_Display;
ALTER TABLE TemplateDisplay DROP CONSTRAINT FK_TemplateDisplay_DISPLAY;
GO

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
GO
/* End YUK-17051 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.0', '01-AUG-2017', 'Latest Update', 0, GETDATE());*/