/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

alter table DeviceTapPagingSettings add Sender varchar2(64);
update DeviceTapPagingSettings set Sender = 'yukonserver@cannontech.com';
alter table DeviceTapPagingSettings modify Sender not null;

alter table DeviceTapPagingSettings add SecurityCode varchar2(64);
update DeviceTapPagingSettings set SecurityCode = '(none)';
alter table DeviceTapPagingSettings modify SecurityCode not null;

alter table DeviceTapPagingSettings add POSTPath varchar2(64);
update DeviceTapPagingSettings set POSTPath = '/wctp';
alter table DeviceTapPagingSettings modify POSTPath not null;













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