/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

delete from yukonlistentry where entryid = 125;
go

alter table MspVendor add AppName varchar(64);
go
update MspVendor set AppName = '(none)';
go
alter table MspVendor alter column AppName varchar(64) not null;
go
alter table MspVendor add OutUserName varchar(64);
go
update MspVendor set OutUserName = '(none)';
go
alter table MspVendor alter column OutUserName varchar(64) not null;
go
alter table MspVendor add OutPassword varchar(64);
go
update MspVendor set OutPassword = '(none)';
go
alter table MspVendor alter column OutPassword varchar(64) not null;
go

delete YukonGroupRole where Roleid = -7;
delete YukonUserRole where RoleID = -7;
delete yukonroleproperty where RoleID = -7;
go

insert into YukonRoleProperty values(-1600,-7,'PaoName Alias','0','Defines a Yukon Pao (Device) Name field alias. Valid values(0-4): [0=Device Name, 1=Account Number, 2=Service Location, 3=Customer]');
insert into YukonRoleProperty values(-1601,-7,'Primary CIS Vendor','0','Defines the primary CIS vendor for CB interfaces.');
go
insert into YukonGroupRole values(-270,-1,-7,-1600,'0');

insert into point values( -110, 'System', 'Multispeak' , 0, 'Default', 0, 'N', 'N', 'S', 110 ,'None', 0);

/* @error ignore-begin */
create table Substation (
   SubstationID         numeric              not null,
   SubstationName       varchar(50)          null,
   LMRouteID              numeric              null
);
go

insert into Substation values (0,'(none)',0);
alter table Substation
   add constraint PK_SUBSTATION primary key  (SubstationID);
go
alter table Substation
   add constraint FK_Sub_Rt foreign key (RouteID)
      references Route (RouteID);
go
/* @error ignore-end */


sp_rename 'Substation.RouteID', 'LMRouteID', 'COLUMN';
go

/*==============================================================*/
/* Table: SubstationToRouteMapping                              */
/*==============================================================*/
create table SubstationToRouteMapping (
   SubstationID		numeric              not null,
   RouteID              numeric              not null,
   Ordering             numeric              not null
);
go

alter table SubstationToRouteMapping
   add constraint PK_SUB_Rte_Map primary key  (SubstationID, RouteID);
go

alter table SubstationToRouteMapping
   add constraint FK_Sub_Rte_Map_SubID foreign key (SubstationID)
      references Substation (SubstationID);
go

alter table SubstationToRouteMapping
   add constraint FK_Sub_Rte_Map_RteID foreign key (RouteID)
      references Route (RouteID);
go

alter table ImportData add BillGrp varchar(64);
go
update ImportData set BillGrp = '';
go
alter table ImportData alter column BillGrp varchar(64) not null;
go

alter table ImportData add SubstationName varchar(50);
go
update ImportData set SubstationName = '';
go
alter table ImportData alter column SubstationName varchar(50) not null;
go

alter table ImportFail add BillGrp varchar(64);
go
update ImportFail set BillGrp = '';
go
alter table ImportFail alter column BillGrp varchar(64) not null;
go

alter table ImportFail add SubstationName varchar(50);
go
update ImportFail set SubstationName = '';
go
alter table ImportFail alter column SubstationName varchar(50) not null;
go

alter table ImportFail add FailType varchar(64);
go
update ImportFail set FailType = '';
go
alter table ImportFail alter column FailType varchar(64) not null;
go

/*==============================================================*/
/* Table: ImportPendingComm                                     */
/*==============================================================*/
create table ImportPendingComm (
   DeviceID	       	   	numeric		    	 not null,
   Address             	varchar(64)          not null,
   Name	               	varchar(64)          not null,
   RouteName           	varchar(64)          not null,
   MeterNumber	       	varchar(64)	    	 not null,
   CollectionGrp       	varchar(64)          not null,
   AltGrp	       		varchar(64)          not null,
   TemplateName	       	varchar(64)          not null,
   BillGrp             	varchar(64)          not null, 
   SubstationName      	varchar(64)          not null
)
go
alter table ImportPendingComm
   add constraint PK_IMPPENDINGCOMM primary key  (DeviceID);
go

alter table ImportPendingComm
   add constraint FK_ImpPC_PAO foreign key (DeviceID)
      references YukonPAObject (PAObjectID);
go

insert into YukonRoleProperty values(-20011,-200,'Multispeak Setup','false','Controls access to configure the Multispeak Interfaces.');
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
