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

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
