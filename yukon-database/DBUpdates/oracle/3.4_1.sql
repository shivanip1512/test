/******************************************/
/**** Oracle DBupdates   		       ****/
/******************************************/
alter table FDRTranslation modify Translation varchar2(200);

alter table dynamicccsubstationbus add iVControlTot float;
update dynamicccsubstationbus set iVControlTot = 0;
alter table dynamicccsubstationbus modify iVControlTot float not null;

alter table dynamicccsubstationbus add iVCount number;
update dynamicccsubstationbus set iVCount = 0;
alter table dynamicccsubstationbus modify iVCount number not null;

alter table dynamicccsubstationbus add iWControlTot float;
update dynamicccsubstationbus set iWControlTot = 0;
alter table dynamicccsubstationbus modify iWControlTot float not null;

alter table dynamicccsubstationbus add iWCount number;
update dynamicccsubstationbus set iWCount = 0;
alter table dynamicccsubstationbus modify iWCount number not null;

alter table dynamicccfeeder add iVControlTot float;
update dynamicccfeeder set iVControlTot = 0;
alter table dynamicccfeeder modify iVControlTot float not null;

alter table dynamicccfeeder add iVCount number;
update dynamicccfeeder set iVCount = 0;
alter table dynamicccfeeder modify iVCount number not null;

alter table dynamicccfeeder add iWControlTot float;
update dynamicccfeeder set iWControlTot = 0;
alter table dynamicccfeeder modify iWControlTot float not null;

alter table dynamicccfeeder add iWCount number;
update dynamicccfeeder set iWCount = 0;
alter table dynamicccfeeder modify iWCount number not null;

/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.4', 'Jon', '24-Apr-2007', 'Latest Update', 1 );
