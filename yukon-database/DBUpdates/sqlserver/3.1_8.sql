/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

insert into YukonRoleProperty values(-20302,-203,'3 Tier Direct Control','false','Allows access to the 3-tier load management web interface');
insert into yukongrouprole values (-777,-301,-203,-20302,'(none)');
insert into YukonGroupRole values (-1277,-2,-203,-20302,'(none)');
insert into YukonUserRole values (-777,-1,-203,-20302,'(none)');

create table DynamicPAOInfo (
   EntryID              numeric              not null,
   PAObjectID           numeric              not null,
   Owner                varchar(64)          not null,
   Info                 varchar(128)         not null,
   Value                varchar(128)         not null
);
go
alter table DynamicPAOInfo
   add constraint PK_DYNPAOINFO primary key  (EntryID);
go
alter table DynamicPAOInfo
   add constraint AK_DYNPAO_OWNKYUQ unique  (EntryID, PAObjectID, Owner);
go
alter table DynamicPAOInfo
   add constraint FK_DynPAOInfo_YukPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID);
go

INSERT INTO DEVICETYPECOMMAND VALUES (-361, -11, 'MCT-410IL', 11, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-362, -12, 'MCT-410IL', 12, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-363, -13, 'MCT-410IL', 13, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-364, -1, 'MCT-410CL', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-365, -81, 'MCT-410CL', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-366, -3, 'MCT-410CL', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-367, -6, 'MCT-410CL', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-368, -34, 'MCT-410CL', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-369, -82, 'MCT-410CL', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-370, -18, 'MCT-410CL', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-371, -19, 'MCT-410CL', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-372, -83, 'MCT-410CL', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-373, -84, 'MCT-410CL', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-374, -11, 'MCT-410CL', 11, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-375, -12, 'MCT-410CL', 12, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-376, -13, 'MCT-410CL', 13, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-377, -30, 'MCT-248', 14, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-378, -31, 'MCT-248', 15, 'Y');







/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.1', 'Ryan', '5-JUN-2005', 'Manual version insert done', 8);