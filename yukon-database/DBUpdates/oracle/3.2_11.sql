/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

delete from yukonlistentry where entryid = 125;

alter table MspVendor add AppName varchar2(64);
update MspVendor set AppName = '(none)';
alter table MspVendor modify AppName varchar2(64) not null;

alter table MspVendor add OutUserName varchar2(64);
update MspVendor set OutUserName = '(none)';
alter table MspVendor modify OutUserName varchar2(64) not null;

alter table MspVendor add OutPassword varchar2(64);
update MspVendor set OutPassword = '(none)';
alter table MspVendor modify OutPassword varchar2(64) not null;

delete YukonGroupRole where Roleid = -7;
delete YukonUserRole where RoleID = -7;
delete yukonroleproperty where RoleID = -7;

insert into YukonRoleProperty values(-1600,-7,'PaoName Alias','0','Defines a Yukon Pao (Device) Name field alias. Valid values(0-4): [0=Device Name, 1=Account Number, 2=Service Location, 3=Customer]');
insert into YukonRoleProperty values(-1601,-7,'Primary CIS Vendor','0','Defines the primary CIS vendor for CB interfaces.');
insert into YukonGroupRole values(-270,-1,-7,-1600,'0');
insert into point values( -110, 'System', 'Multispeak' , 0, 'Default', 0, 'N', 'N', 'S', 110 ,'None', 0);

/* @error ignore-begin */
create table Substation (
   SubstationID        	number              not null,
   SubstationName       varchar2(50)        null,
   LMRouteID            number              null
);

insert into Substation values (0,'(none)',0);
alter table Substation
   add constraint PK_SUBSTATION primary key  (SubstationID);

alter table Substation
   add constraint FK_Sub_Rt foreign key (LMRouteID)
      references Route (RouteID);

/* @error ignore-end */

/* @error ignore-begin */
alter table Substation rename column RouteID to LMRouteID;
/* @error ignore-end */

/*==============================================================*/
/* Table: SubstationToRouteMapping                              */
/*==============================================================*/
create table SubstationToRouteMapping (
   SubstationID			number              not null,
   RouteID              number              not null,
   Ordering             number              not null
);

alter table SubstationToRouteMapping
   add constraint PK_SUB_Rte_Map primary key  (SubstationID, RouteID);

alter table SubstationToRouteMapping
   add constraint FK_Sub_Rte_Map_SubID foreign key (SubstationID)
      references Substation (SubstationID);

alter table SubstationToRouteMapping
   add constraint FK_Sub_Rte_Map_RteID foreign key (RouteID)
      references Route (RouteID);

alter table ImportData add BillGrp varchar2(64);
update ImportData set BillGrp = ' ';
alter table ImportData modify BillGrp varchar2(64) not null;

alter table ImportData add SubstationName varchar2(50);
update ImportData set SubstationName = ' ';
alter table ImportData modify SubstationName varchar2(50) not null;

alter table ImportFail add BillGrp varchar2(64);
update ImportFail set BillGrp = ' ';
alter table ImportFail modify BillGrp varchar2(64) not null;

alter table ImportFail add SubstationName varchar2(50);
update ImportFail set SubstationName = ' ';
alter table ImportFail modify SubstationName varchar2(50) not null;

alter table ImportFail add FailType varchar2(64);
update ImportFail set FailType = ' ';
alter table ImportFail modify FailType varchar2(64) not null;

/*==============================================================*/
/* Table: ImportPendingComm                                     */
/*==============================================================*/
create table ImportPendingComm (
   DeviceID	       	   	number		    	  not null,
   Address             	varchar2(64)          not null,
   Name	               	varchar2(64)          not null,
   RouteName           	varchar2(64)          not null,
   MeterNumber	       	varchar2(64)	   	  not null,
   CollectionGrp       	varchar2(64)          not null,
   AltGrp	       		varchar2(64)          not null,
   TemplateName	       	varchar2(64)          not null,
   BillGrp             	varchar2(64)          not null, 
   SubstationName      	varchar2(64)          not null
);

alter table ImportPendingComm
   add constraint PK_IMPPENDINGCOMM primary key  (DeviceID);

alter table ImportPendingComm
   add constraint FK_ImpPC_PAO foreign key (DeviceID)
      references YukonPAObject (PAObjectID);

insert into YukonRoleProperty values(-20011,-200,'Multispeak Setup','false','Controls access to configure the Multispeak Interfaces.');

/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.2', 'Jon', '11-Dec-2006', 'Latest Update', 11 );
