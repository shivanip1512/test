/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/
alter table cceventlog add kvarBefore float;
update cceventlog set kvarBefore = 0.0;
alter table cceventlog modify kvarBefore float not null;

alter table cceventlog add kvarAfter float;
update cceventlog set kvarAfter = 0.0;
alter table cceventlog modify kvarAfter float not null;

alter table cceventlog add kvarChange float;
update cceventlog set kvarChange = 0.0;
alter table cceventlog modify kvarChange float not null;

alter table cceventlog add additionalInfo varchar2(20);
update cceventlog set additionalInfo = '(none)';
alter table cceventlog modify additionalInfo varchar2(20) not null;

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