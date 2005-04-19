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

insert into FDRInterface values (22, 'LIVEDATA','Receive', 'f' );

alter table FDRINTERFACEOPTION modify column OPTIONVALUES varchar2(256);

insert into FDRInterfaceOption values(22, 'Address', 1, 'Text', '(none)' );
insert into FDRInterfaceOption values(22, 'Data Type', 2, 'Combo', 'Data_RealExtended,Data_DiscreteExtended,Data_StateExtended,Data_RealQ,Data_DiscreteQ,Data_State,Data_Discrete,Data_Real,Data_RealQTimeTag,Data_StateQTimeTag,Data_DiscreteQTimeTag' );












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