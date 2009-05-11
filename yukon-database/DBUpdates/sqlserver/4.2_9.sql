/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

/* Start YUK-7315 */
INSERT INTO YukonRoleProperty VALUES(-1509,-6,'Rounding Mode','HALF_EVEN','Rounding Mode used when formatting value data in billing formats. Available placeholders: HALF_EVEN, CEILING, FLOOR, UP, DOWN, HALF_DOWN, HALF_UP');
INSERT INTO YukonGroupRole VALUES(-239,-1,-6,-1509,'(none)');

ALTER TABLE DynamicBillingField ADD RoundingMode VARCHAR(20);
GO
UPDATE DynamicBillingField SET RoundingMode = 'HALF_EVEN';
GO
ALTER TABLE DynamicBillingField ALTER COLUMN RoundingMode VARCHAR(20) NOT NULL;
GO
/* End YUK-7315 */

/* Start YUK-7257 */
/* @error ignore-begin */
DROP INDEX INDX_LMThermSeaEnt_SeaId;
CREATE INDEX INDX_LMThermSeaEntry_SeaId ON LMThermostatSeasonEntry (
   SeasonId ASC
);
/* @error ignore-end */
/* End YUK-7257 */

/* Start YUK-7452 */
INSERT INTO FDRInterfaceOption VALUES(28, 'Multiplier', 5, 'Text', '1.0');

UPDATE FDRTranslation 
SET Translation = SUBSTRING(Translation, 1, (LEN(Translation) - LEN('POINTTYPE:Analog;'))) + 'Multiplier:1.0;POINTTYPE:Analog;'
WHERE InterfaceType = 'DNPSLAVE'
AND Translation LIKE '%Type:Analog;%';

UPDATE FDRTranslation 
SET Translation = SUBSTRING(Translation, 1, (LEN(Translation) - LEN('POINTTYPE:PulseAccumulator;'))) + 'Multiplier:1.0;POINTTYPE:PulseAccumulator;'
WHERE InterfaceType = 'DNPSLAVE' 
AND Translation LIKE '%Type:PulseAccumulator;%';

UPDATE FDRTranslation 
SET Translation = SUBSTRING(Translation, 1, (LEN(Translation) - LEN('POINTTYPE:Status;'))) + 'Multiplier:1.0;POINTTYPE:Status;'
WHERE InterfaceType = 'DNPSLAVE'
AND Translation LIKE '%Type:Status;%';
/* End YUK-7452 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
