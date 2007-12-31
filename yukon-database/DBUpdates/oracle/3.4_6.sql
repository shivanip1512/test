/******************************************/
/**** Oracle DBupdates   		       ****/
/******************************************/

/* @error ignore-begin */
insert into yukonroleproperty values (-100011,-1000, 'Daily/Max Operation Count', 'true', 'is Daily/Max Operation stat displayed');
insert into yukonroleproperty values (-100012,-1000, 'Substation Last Update Timestamp', 'true', 'is last update timstamp shown for substations');
insert into yukonroleproperty values (-100106,-1001, 'Feeder Last Update Timestamp', 'true', 'is last update timstamp shown for feeders');
insert into yukonroleproperty values (-100203,-1002, 'CapBank Last Update Timestamp', 'true', 'is last update timstamp shown for capbanks');
insert into yukonroleproperty values (-100105,-1001, 'Target', 'true', 'is target stat displayed');

ALTER TABLE DeviceReadJobLog DROP CONSTRAINT FK_DEVICERE_FK_DRJOBL_MACSCHED;
/* @error ignore-end */

update yukonroleproperty set DefaultValue = 'false' where rolepropertyid = -100008;
update yukonroleproperty set DefaultValue = 'false' where rolepropertyid = -100007;

insert into billingfileformats values(-26, 'SIMPLE_TOU_DeviceName');

create table dynamicccarea ( AreaID numeric not null, additionalflags varchar2(20) not null );

alter table dynamicccarea
   add constraint FK_ccarea_Dynccarea foreign key (areaID)
      references Capcontrolarea (areaID);

insert into dynamicccarea (areaid, additionalflags) select areaid, 'NNNNNNNNNNNNNNNNNNNN' from capcontrolarea; 

/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.4', 'David', '16-Aug-2007', 'Latest Update', 6 );
