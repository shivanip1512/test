/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-14940 */
UPDATE DeviceConfigCategory
SET CategoryType= 'lcdConfiguration'
WHERE CategoryType = 'meterParameters';
/* End YUK-14940 */

/* Start YUK-14987 */
/* @error ignore-begin */
INSERT INTO StateGroup VALUES(-20, 'Ignored Control', 'Status');

INSERT INTO State VALUES(-20, 0, 'Manual', 1, 6, 0);
INSERT INTO State VALUES(-20, 1, 'SCADA Override', 2, 6, 0);
INSERT INTO State VALUES(-20, 2, 'Fault Current', 3, 6, 0);
INSERT INTO State VALUES(-20, 3, 'Emergency Voltage', 4, 6, 0);
INSERT INTO State VALUES(-20, 4, 'Time ONOFF', 5, 6, 0);
INSERT INTO State VALUES(-20, 5, 'OVUV Control', 7, 6, 0);
INSERT INTO State VALUES(-20, 6, 'VAR', 8, 6, 0);
INSERT INTO State VALUES(-20, 7, 'Va', 9, 6, 0);
INSERT INTO State VALUES(-20, 8, 'Vb', 10, 6, 0);
INSERT INTO State VALUES(-20, 9, 'Vc', 1, 6, 0);
INSERT INTO State VALUES(-20, 10, 'Ia', 2, 6, 0);
INSERT INTO State VALUES(-20, 11, 'Ib', 3, 6, 0);
INSERT INTO State VALUES(-20, 12, 'Ic', 4, 6, 0);
INSERT INTO State VALUES(-20, 13, 'Temp', 5, 6, 0);
INSERT INTO State VALUES(-20, 15, 'Time', 7, 6, 0);
INSERT INTO State VALUES(-20, 17, 'Bad Active Relay', 8, 6, 0);
INSERT INTO State VALUES(-20, 18, 'NC Lockout', 9, 6, 0);
INSERT INTO State VALUES(-20, 20, 'Auto Mode', 10, 6, 0);
INSERT INTO State VALUES(-20, 21, 'Reclose Block', 1, 6, 0);
/* @error ignore-end */
/* End YUK-14987 */

/* Start YUK-15030 */
INSERT INTO JobProperty
    SELECT (SELECT ISNULL(MAX(JobPropertyId), 0) FROM JobProperty) + ROW_NUMBER() OVER (ORDER BY JobId), T.* 
    FROM (
        SELECT JobId, 'daysOffset' as Name, 0 as Value
        FROM Job j
        WHERE BeanName = 'scheduledWaterLeakFileExportJobDefinition' and Disabled != 'D'
          AND NOT EXISTS (SELECT 1 FROM JobProperty WHERE JobId = j.JobId AND Name = 'daysOffset')
    ) T;
/* End YUK-15030 */

/* Start YUK-15019 */
sp_rename 'FK_CAPCONTR_REFERENCE_YUKONPAO', 'FK_CCArea_YukonPAO', 'OBJECT';
GO
sp_rename 'FK_CAPCONTR_YUKONPAO2', 'FK_CCSpecArea_YukonPAO', 'OBJECT';
GO
sp_rename 'FK_CAPCONTR_REF_YUKONPA2', 'FK_CCSubstation_YukonPAO', 'OBJECT';
GO

sp_rename 'FK_CapContrSubBus_Point_Switch', 'FK_CCSubBus_Point_Switch', 'OBJECT';
GO
sp_rename 'FK_CapContrSubBus_Point_VarPt', 'FK_CCSubBus_Point_VarLoad', 'OBJECT';
GO
sp_rename 'FK_CapContrSubBus_Point_VoltPt', 'FK_CCSubBus_Point_VoltLoad', 'OBJECT';
GO
sp_rename 'FK_CapContrSubBus_Point_WattPt', 'FK_CCSubBus_Point_WattLoad', 'OBJECT';
GO

sp_rename 'FK_CAPCONTR_VARPTID', 'FK_CCFeeder_Point_VarLoad', 'OBJECT';
GO
sp_rename 'FK_CAPCONTR_VOLTPTID', 'FK_CCFeeder_Point_VoltLoad', 'OBJECT';
GO
sp_rename 'FK_CAPCONTR_WATTPTID', 'FK_CCFeeder_Point_WattLoad', 'OBJECT';
GO
sp_rename 'FK_PAObj_CCFeed', 'FK_CCFeeder_YukonPAO', 'OBJECT';
GO

ALTER TABLE CapControlArea
   ADD CONSTRAINT FK_CCArea_Point_VoltReduction FOREIGN KEY (VoltReductionPointID)
      REFERENCES Point (PointId);

ALTER TABLE CapControlSpecialArea
   ADD CONSTRAINT FK_CCSpecArea_Point_VoltReduc FOREIGN KEY (VoltReductionPointID)
      REFERENCES Point (PointId);

ALTER TABLE CapControlSubstation
   ADD CONSTRAINT FK_CCSub_Point_VoltReduct FOREIGN KEY (VoltReductionPointId)
      REFERENCES Point (PointId);

ALTER TABLE CapControlSubstationBus
   ADD CONSTRAINT FK_CCSubBus_Point_DisableBus FOREIGN KEY (DisableBusPointId)
      REFERENCES Point (PointId);

ALTER TABLE CapControlSubstationBus
   ADD CONSTRAINT FK_CCSubBus_Point_PhaseB FOREIGN KEY (PhaseB)
      REFERENCES Point (PointId);

ALTER TABLE CapControlSubstationBus
   ADD CONSTRAINT FK_CCSubBus_Point_PhaseC FOREIGN KEY (PhaseC)
      REFERENCES Point (PointId);

ALTER TABLE CapControlSubstationBus
   ADD CONSTRAINT FK_CCSubBus_Point_VoltReduct FOREIGN KEY (VoltReductionPointId)
      REFERENCES Point (PointId);

ALTER TABLE CapControlFeeder
   ADD CONSTRAINT FK_CCFeeder_Point_PhaseB FOREIGN KEY (PhaseB)
      REFERENCES Point (PointId);

ALTER TABLE CapControlFeeder
   ADD CONSTRAINT FK_CCFeeder_Point_PhaseC FOREIGN KEY (PhaseC)
      REFERENCES Point (PointId);
GO
/* End YUK-15019 */

/* Start YUK-15052 */
INSERT INTO Device
SELECT PaobjectId, 'N', 'N'
FROM YukonPAObject
WHERE Type = 'WEATHER LOCATION'
  AND PAObjectID NOT IN (SELECT DeviceID FROM Device);
/* End YUK-15052 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.5', '31-JAN-2016', 'Latest Update', 1, GETDATE());*/