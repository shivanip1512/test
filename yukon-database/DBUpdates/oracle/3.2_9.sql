/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

alter table DCItemType add Description VARCHAR2(320);
update DCItemType set Description='(none)';
alter table DCItemType modify Description VARCHAR2(320) not null;

/* @error ignore */
alter table DynamicCCFeeder rename column LastCurrentVarUpdate to LastCurrentVarUpdateTime;

insert into YukonListEntry values( 11, 1, 0, 'IVR Login', 3 );

alter table PointUnit add DecimalDigits NUMBER;
update PointUnit set DecimalDigits = 0;
alter table PointUnit modify DecimalDigits NUMBER not null;

/* @error ignore */
alter table DynamicPointAlarming drop constraint FKf_DynPtAl_SysL;

alter table DeviceMeterGroup modify CollectionGroup VARCHAR2(50) not null;
alter table DeviceMeterGroup modify TestCollectionGroup VARCHAR2(50) not null;
alter table DeviceMeterGroup modify BillingGroup VARCHAR2(50) not null;

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
