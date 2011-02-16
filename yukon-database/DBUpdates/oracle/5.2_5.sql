/******************************************/ 
/**** Oracle DBupdates                 ****/ 
/******************************************/ 

/* Start YUK-9322 */
INSERT INTO YukonRoleProperty VALUES(-1118,-2,'Serial Number Validation','NUMERIC','Treat serial numbers as numeric or alpha-numberic. Possible values (NUMERIC, ALPHANUMERIC)');
/* End YUK-9322 */

/* Start YUK-9354 */
DELETE FROM InventoryConfigTask; 

ALTER TABLE InventoryConfigTask
ADD UserId NUMBER NOT NULL; 

ALTER TABLE InventoryConfigTask 
    ADD CONSTRAINT FK_InvConfTask_YukonUser FOREIGN KEY (UserId) 
        REFERENCES YukonUser (UserId) 
            ON DELETE CASCADE; 
/* End YUK-9354 */

/* Start YUK-9467 */
UPDATE YukonRoleProperty 
SET DefaultValue = 'METER_NUMBER', 
    Description = 'Defines a Yukon Pao (Device) Name field alias. Valid values: METER_NUMBER, ACCOUNT_NUMBER, SERVICE_LOCATION, CUSTOMER_ID, GRID_LOCATION, POLE_NUMBER' 
WHERE RolePropertyId = -1600;
/* End YUK-9467 */

/* Start YUK-9471 */
CREATE TABLE ThermostatEventHistory(
      EventId           NUMBER           NOT NULL,
      EventType         VARCHAR2(64)       NOT NULL,
      UserName          VARCHAR2(64)       NOT NULL,
      EventTime         DATE          NOT NULL,
      ThermostatId      NUMBER           NOT NULL,
      ManualTemp        NUMBER           NULL,
      ManualMode        VARCHAR2(64)       NULL,
      ManualFan         VARCHAR2(64)       NULL,
      ManualHold        CHAR(1)           NULL,
      ScheduleId        NUMBER           NULL,
      ScheduleMode      VARCHAR2(64)       NULL,
      CONSTRAINT PK_ThermEventHist PRIMARY KEY (EventId)
);

ALTER TABLE ThermostatEventHistory
      ADD CONSTRAINT FK_ThermEventHist_InvBase FOREIGN KEY (ThermostatId)
            REFERENCES InventoryBase (InventoryId)
                  ON DELETE CASCADE;

ALTER TABLE ThermostatEventHistory
   ADD CONSTRAINT FK_ThermEventHist_AcctThermSch FOREIGN KEY (ScheduleId)
      REFERENCES AcctThermostatSchedule (AcctThermostatScheduleId)
         ON DELETE SET NULL;
/* End YUK-9471 */

/* Start YUK-9475 */
UPDATE ValidationMonitor 
SET ReadingError = .1000001 
where ReadingError = .1;
/* End YUK-9475 */

/* Start YUK-8691 */
UPDATE AcctThermostatScheduleEntry
SET CoolTemp = 72 
WHERE CoolTemp <= 0;

/* @start-block */
DECLARE
    i NUMBER;
BEGIN
    SELECT MAX(CoolTemp) INTO i FROM AcctThermostatScheduleEntry;
WHILE (i > 100)
    LOOP
        UPDATE AcctThermostatScheduleEntry
        SET CoolTemp = ((CoolTemp - 32)/1.8)
        WHERE CoolTemp > 100;
        
		SELECT MAX(CoolTemp) INTO i FROM AcctThermostatScheduleEntry;
	END LOOP;
END;
/* @end-block */

/* @start-block */
DECLARE
    i NUMBER;
BEGIN
    SELECT MIN(CoolTemp) INTO i FROM AcctThermostatScheduleEntry;
WHILE (i < 38)
    LOOP
        UPDATE AcctThermostatScheduleEntry
        SET CoolTemp = ((CoolTemp * 1.8) + 32)
        WHERE CoolTemp < 38;
        
		SELECT MIN(CoolTemp) INTO i FROM AcctThermostatScheduleEntry;
	END LOOP;
END;
/* @end-block */

UPDATE AcctThermostatScheduleEntry
SET HeatTemp = 72
WHERE HeatTemp <= 0;

/* @start-block */
DECLARE
    i NUMBER;
BEGIN
    SELECT MAX(HeatTemp) INTO i FROM AcctThermostatScheduleEntry;
WHILE (i > 100)
    LOOP
        UPDATE AcctThermostatScheduleEntry
        SET HeatTemp = ((HeatTemp - 32)/1.8)
        WHERE HeatTemp > 100;
        
		SELECT MAX(HeatTemp) INTO i FROM AcctThermostatScheduleEntry;
	END LOOP;
END;
/* @end-block */

/* @start-block */
DECLARE
    i NUMBER;
BEGIN
    SELECT MIN(HeatTemp) INTO i FROM AcctThermostatScheduleEntry;
WHILE (i < 38)
    LOOP
        UPDATE AcctThermostatScheduleEntry
        SET HeatTemp = ((HeatTemp * 1.8) + 32)
        WHERE HeatTemp < 38;
        
		SELECT MIN(HeatTemp) INTO i FROM AcctThermostatScheduleEntry;
	END LOOP;
END;
/* @end-block */
/* End YUK-8691 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
INSERT INTO CTIDatabase VALUES ('5.2', 'Matt K', '10-JAN-2011', 'Latest Update', 5);