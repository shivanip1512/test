/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

alter table DeviceTapPagingSettings add Sender varchar(64);
go
update DeviceTapPagingSettings set Sender = 'yukonserver@cannontech.com';
go
alter table DeviceTapPagingSettings alter column Sender varchar(64) not null;
go

alter table DeviceTapPagingSettings add SecurityCode varchar(64);
go
update DeviceTapPagingSettings set SecurityCode = '(none)';
go
alter table DeviceTapPagingSettings alter column SecurityCode varchar(64) not null;
go

alter table DeviceTapPagingSettings add POSTPath varchar(64);
go
update DeviceTapPagingSettings set POSTPath = '/wctp';
go
alter table DeviceTapPagingSettings alter column POSTPath varchar(64) not null;
go















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