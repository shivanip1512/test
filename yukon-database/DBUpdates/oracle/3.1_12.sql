/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

/* @error ignore */
alter table YukonImage modify ImageValue long raw;

/* @error ignore */
insert into YukonServices values( -6, 'Price_Server', 'com.cannontech.jmx.services.DynamicPriceServer', '(none)', '(none)' );

insert into command values(-98, 'putconfig emetcon disconnect', 'Upload Disconnect Address', 'MCT-410IL');
insert into command values(-99, 'getconfig disconnect', 'Read Disconnect Address/Status', 'MCT-410IL');

INSERT INTO DEVICETYPECOMMAND VALUES (-379, -98, 'MCT-410IL', 14, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-380, -99, 'MCT-410IL', 15, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-381, -98, 'MCT-410CL', 14, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-382, -99, 'MCT-410CL', 15, 'Y');






/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.1', 'Ryan', '15-AUG-2005', 'Manual version insert done', 12);