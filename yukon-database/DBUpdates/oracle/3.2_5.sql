/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/


/* @error ignore */
insert into YukonRoleProperty values(-10811,-108, 'inbound_voice_home_url', '/voice/inboundOptOut.jsp', 'Home URL for inbound voice logins');

insert into billingfileformats values(20, 'IVUE_BI_T65');

/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.2', 'DBMaintainer', '15-MAY-2006', 'Manual version insert done', 5 );
