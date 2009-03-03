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
GO
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

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
