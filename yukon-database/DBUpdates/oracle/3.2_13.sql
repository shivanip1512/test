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

insert into YukonRoleProperty values(-10813, -108,'Show flip command', 'false', 'Show flip command for Cap Banks with 7010 type controller');

/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.2', 'Jon', '06-Feb-2007', 'Latest Update', 13 );