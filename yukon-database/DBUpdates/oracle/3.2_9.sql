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
