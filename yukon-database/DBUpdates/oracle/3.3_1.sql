/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/
alter table DeviceMeterGroup modify CollectionGroup VARCHAR2(50) not null;

delete MSPInterface where vendorid = 1 and interface = 'CB_MR';
delete MSPInterface where vendorid = 1 and interface = 'EA_MR';
delete MSPInterface where vendorid = 1 and interface = 'OA_OD';
delete MSPInterface where vendorid = 1 and interface = 'CB_CD';

/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
/* __YUKON_VERSION__ */