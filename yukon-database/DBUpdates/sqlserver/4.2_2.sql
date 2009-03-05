/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

/* Start YUK-6943 */
DELETE FROM YukonUserRole 
WHERE RolePropertyId IN (-1018, -10003, -10900, -10901, -10902, -10910, -20200,
                         -20201, -20202, -30500, -30000, -30001, -30600, -30601, 
                         -30602, -30603, -40002, -40110, -40111, -40112, -40113, 
                         -40114, -40115, -40116, -40130, -40131, -40132, -40135, 
                         -40136, -40137, -40138, -40139, -40150, -40151, -40152, 
                         -40153, -40154, -40155, -40156, -40157, -40158, -40159, 
                         -40170, -40171, -40173, -40180, -40181, -40190, -40191, 
                         -40192, -40193, -40194, -40195, -40196, -40198);
GO
DELETE FROM YukonGroupRole 
WHERE RolePropertyId IN (-1018, -10003, -10900, -10901, -10902, -10910, -20200,
                         -20201, -20202, -30500, -30000, -30001, -30600, -30601, 
                         -30602, -30603, -40002, -40110, -40111, -40112, -40113, 
                         -40114, -40115, -40116, -40130, -40131, -40132, -40135, 
                         -40136, -40137, -40138, -40139, -40150, -40151, -40152, 
                         -40153, -40154, -40155, -40156, -40157, -40158, -40159, 
                         -40170, -40171, -40173, -40180, -40181, -40190, -40191, 
                         -40192, -40193, -40194, -40195, -40196, -40198);
GO
DELETE FROM YukonRoleProperty 
WHERE RolePropertyId IN (-1018, -10003, -10900, -10901, -10902, -10910, -20200,
                         -20201, -20202, -30500, -30000, -30001, -30600, -30601, 
                         -30602, -30603, -40002, -40110, -40111, -40112, -40113, 
                         -40114, -40115, -40116, -40130, -40131, -40132, -40135, 
                         -40136, -40137, -40138, -40139, -40150, -40151, -40152, 
                         -40153, -40154, -40155, -40156, -40157, -40158, -40159, 
                         -40170, -40171, -40173, -40180, -40181, -40190, -40191, 
                         -40192, -40193, -40194, -40195, -40196, -40198);
GO
DELETE FROM YukonUserRole 
WHERE RoleId IN (-305,-300,-306);
GO
DELETE FROM YukonGroupRole 
WHERE RoleId IN (-305,-300,-306);
GO
DELETE FROM YukonRoleProperty 
WHERE RoleId IN (-305,-300,-306);
GO
DELETE FROM YukonRole 
WHERE RoleId IN (-305,-300,-306);
GO
/* End YUK-6943 */

/* Start YUK-6841 */
INSERT INTO Command VALUES(-158, 'getvalue interval last', 'Last Interval kW', 'All Two Way LCR');
INSERT INTO Command VALUES(-159, 'getvalue runtime load 1 previous ?''12 , 24, or 36''', 'Runtime Load 1', 'All Two Way LCR');
INSERT INTO Command VALUES(-160, 'getvalue runtime load 2 previous ?''12 , 24, or 36''', 'Runtime Load 2', 'All Two Way LCR');
INSERT INTO Command VALUES(-161, 'getvalue runtime load 3 previous ?''12 , 24, or 36''', 'Runtime Load 3', 'All Two Way LCR');
INSERT INTO Command VALUES(-162, 'getvalue runtime load 4 previous ?''12 , 24, or 36''', 'Runtime Load 4', 'All Two Way LCR');
INSERT INTO Command VALUES(-163, 'getvalue shedtime relay 1 previous ?''12 , 24, or 36''', 'Relay 1 Shed Time', 'All Two Way LCR');
INSERT INTO Command VALUES(-164, 'getvalue shedtime relay 2 previous ?''12 , 24, or 36''', 'Relay 2 Shed Time', 'All Two Way LCR');
INSERT INTO Command VALUES(-165, 'getvalue shedtime relay 3 previous ?''12 , 24, or 36''', 'Relay 3 Shed Time', 'All Two Way LCR');
INSERT INTO Command VALUES(-166, 'getvalue shedtime relay 4 previous ?''12 , 24, or 36''', 'Relay 4 Shed Time', 'All Two Way LCR');
INSERT INTO Command VALUES(-167, 'getvalue propcount', 'Prop Count', 'All Two Way LCR');

INSERT INTO DeviceTypeCommand VALUES (-753, -158, 'LCR-3102', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-754, -159, 'LCR-3102', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-755, -160, 'LCR-3102', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-756, -161, 'LCR-3102', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-757, -162, 'LCR-3102', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-758, -163, 'LCR-3102', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-759, -164, 'LCR-3102', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-760, -165, 'LCR-3102', 8, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-761, -166, 'LCR-3102', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-762, -167, 'LCR-3102', 10, 'Y', -1); 

INSERT INTO YukonListEntry VALUES (1066,1005,-1,'LCR-3102',1315); 
/* End YUK-6841 */

/* Start YUK-6874 */
UPDATE Command 
SET Command = 'putconfig reset r1 r2 r3 cl' 
WHERE CommandId = -48; 
/* End YUK-6874 */

/* Start YUK-6862 */
INSERT INTO Command VALUES(-168, 'putvalue emetcon ied reset a3', 'Reset IED Demand.', 'MCT-430A3'); 

INSERT INTO DeviceTypeCommand VALUES(-763, -3, 'MCT-430A3', 1, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES(-764, -20, 'MCT-430A3', 2, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES(-765, -21, 'MCT-430A3', 3, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES(-766, -22, 'MCT-430A3', 4, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES(-767, -115, 'MCT-430A3', 5, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES(-768, -116, 'MCT-430A3', 6, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES(-769, -117, 'MCT-430A3', 7, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES(-770, -118, 'MCT-430A3', 8, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES(-771, -119, 'MCT-430A3', 9, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES(-772, -120, 'MCT-430A3', 10, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES(-773, -121, 'MCT-430A3', 11, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES(-774, -122, 'MCT-430A3', 12, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES(-775, -123, 'MCT-430A3', 13, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES(-776, -15, 'MCT-430A3', 14, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES(-777, -18, 'MCT-430A3', 15, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES(-778, -19, 'MCT-430A3', 16, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES(-779, -6, 'MCT-430A3', 17, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES(-780, -34, 'MCT-430A3', 18, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES(-781, -105, 'MCT-430A3', 19, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES(-782, -109, 'MCT-430A3', 20, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES(-783, -112, 'MCT-430A3', 21, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES(-784, -113, 'MCT-430A3', 22, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES(-785, -130, 'MCT-430A3', 23, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES(-786, -131, 'MCT-430A3', 24, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES(-787, -132, 'MCT-430A3', 25, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES(-788, -111, 'MCT-430A3', 26, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES(-789, -2, 'MCT-430A3', 27, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES(-790, -83, 'MCT-430A3', 28, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES(-791, -136, 'MCT-430A3', 29, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES(-792, -144, 'MCT-430A3', 30, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES(-793, -168, 'MCT-430A3', 31, 'Y', -1);
/* End YUK-6862 */

/* Start YUK-7084 */
UPDATE YukonRoleProperty 
SET KeyName = 'Inventory Checking', DefaultValue = 'true' 
WHERE RolePropertyId = -20153;

UPDATE YukonGroupRole 
SET Value = '(none)' 
WHERE Value != 'false' 
AND RolePropertyId = -20153;

UPDATE YukonUserRole 
SET Value = '(none)' 
WHERE Value != 'false' 
AND RolePropertyId = -20153; 
/* End YUK-7084 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/

INSERT INTO CTIDatabase VALUES('4.2', 'Matt K', '06-MAR-2009', 'Latest Update', 2);
