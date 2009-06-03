/******************************************/
/**** Oracle DBupdates                 ****/
/******************************************/

/* Start YUK-7122, YUK-7548 */
ALTER TABLE CCurtEEParticipantWindow DROP CONSTRAINT FK_CCRTEEPRTWN_CCRTEEPRIWN;
ALTER TABLE CCurtEEPricingWindow DROP CONSTRAINT FK_CCURTEEPRWIN_CCURTEEPR; 

ALTER TABLE CCurtEEPricingWindow RENAME TO CCurtEEPricingWindowTemp;
ALTER TABLE CCurtEEPricingWindowTemp DROP CONSTRAINT PK_CCURTEEPRICINGWINDOW;
DROP INDEX INDX_CCURTEEPRWIN;
/* @error ignore-begin */
DROP INDEX PK_CCURTEEPRICINGWINDOW;
/* @error ignore-end */

CREATE TABLE CCurtEEPricingWindow  (
   CCurtEEPricingWindowID NUMBER                        not null,
   EnergyPrice          NUMBER(19,6)                    not null,
   Offset               NUMBER                          not null,
   CCurtEEPricingID     NUMBER                          not null,
   constraint PK_CCURTEEPRICINGWINDOW primary key (CCurtEEPricingWindowID)
);

CREATE UNIQUE INDEX INDX_CCURTEEPRWIN on CCurtEEPricingWindow (
   Offset ASC,
   CCurtEEPricingID ASC
);

INSERT INTO CCurtEEPricingWindow
SELECT CCurtEEPricingWindowId, EnergyPrice, Offset, CCurtEEPricingID
FROM CCurtEEPricingWindowTemp;

ALTER TABLE CCurtEEParticipantWindow
   ADD CONSTRAINT FK_CCRTEEPRTWN_CCRTEEPRIWN FOREIGN KEY (CCurtEEPricingWindowId)
      REFERENCES CCurtEEPricingWindow (CCurtEEPricingWindowId);

ALTER TABLE CCurtEEPricingWindow
   ADD CONSTRAINT FK_CCURTEEPRWIN_CCURTEEPR FOREIGN KEY (CCurtEEPricingId)
      REFERENCES CCurtEEPricing (CCurtEEPricingId);

DROP TABLE CCurtEEPricingWindowTemp;
/* End YUK-7122, YUK-7548 */

/* Start YUK-7167 */
INSERT INTO YukonRoleProperty VALUES(-80004,-800,'IVR URL Dialer Template','http://127.0.0.1:9998/VoiceXML.start?tokenid=yukon-{MESSAGE_TYPE}&numbertodial={PHONE_NUMBER}','The URL used to initiate a call, see documentation for allowed variables'); 
INSERT INTO YukonRoleProperty VALUES(-80005,-800,'IVR URL Dialer Success Matcher','success','A Java Regular Expression that will be matched against the output of the URL to determine if the call was successfull'); 

DELETE FROM YukonUserRole 
WHERE RolePropertyId IN (-1400, -80002, -1401);
DELETE FROM YukonGroupRole 
WHERE RolePropertyId IN (-1400, -80002, -1401);
DELETE FROM YukonRoleProperty 
WHERE RolePropertyId IN (-1400, -80002, -1401);
/* End YUK-7167 */

/* Start YUK-7164 */
INSERT INTO YukonRoleProperty VALUES(-10318,-103,'Enable Web Commander', 'true', 'Controls access to web commander applications');
INSERT INTO YukonRoleProperty VALUES(-10319,-103,'Enable Client Commander', 'true', 'Controls access to client commander application'); 
/* End YUK-7164 */

/* Start YUK-7184 */
DELETE FROM YukonUserRole 
WHERE RolePropertyId IN (-100007, -100008, -100200, -100204);
DELETE FROM YukonGroupRole 
WHERE RolePropertyId IN (-100007, -100008, -100200, -100204);
DELETE FROM YukonRoleProperty 
WHERE RolePropertyId IN (-100007, -100008, -100200, -100204);

UPDATE YukonRoleProperty 
SET KeyName = 'Daily/Max Operation Count', Description = 'is Daily/Max Operation stat displayed'
WHERE RolePropertyId = -100103; 
/* End YUK-7184 */

/* Start YUK-7174 */
/* This delete can take some time. */
DELETE RawPointHistory 
WHERE PointId NOT IN (SELECT DISTINCT PointId 
                      FROM Point); 
/* End YUK-7174 */

/* Start YUK-7092 */
UPDATE Command
SET Command = 'getvalue daily read detail channel 1 ?''MM/DD/YYYY''', Label = 'Read Daily kWh, Peak Demand, Min/Max Voltage (Channel 1).'
WHERE CommandId = -156;
UPDATE Command
SET Command = 'getvalue daily read channel 1 ?''MM/DD/YYYY'' ?''MM/DD/YYYY''', Label = 'Read Daily kWh for date range (Channel 1).'
WHERE CommandId = -157;
INSERT INTO Command VALUES(-169, 'getvalue daily read ?''MM/DD/YYYY''', 'Read Daily kWh, Peak kW, and Outages (Channel 1 only, up to 8 days ago)', 'MCT-410IL');
INSERT INTO Command VALUES(-170, 'getvalue daily read detail channel ?''Channel 2|3'' ?''MM/DD/YYYY''', 'Read Daily kWh, Peak Demand, Outages (Channel 2 or 3).', 'MCT-410IL');

INSERT INTO DeviceTypeCommand VALUES (-794, -169, 'MCT-410CL', 40, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-795, -169, 'MCT-410FL', 40, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-796, -169, 'MCT-410GL', 40, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-797, -169, 'MCT-410IL', 40, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-798, -170, 'MCT-410CL', 41, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-799, -170, 'MCT-410FL', 41, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-800, -170, 'MCT-410GL', 41, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-801, -170, 'MCT-410IL', 41, 'Y', -1);
/* End YUK-7092 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/

INSERT INTO CTIDatabase VALUES('4.2', 'Matt K', '26-MAR-2009', 'Latest Update', 3);
