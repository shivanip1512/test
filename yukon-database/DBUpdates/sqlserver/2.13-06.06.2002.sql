/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.13', 'Ryan', '6-JUN-2002', 'Added CustomerLoginSerialGroup table and data for the telegyr interface, added PAOStatistics Table & ExpressCommm Support');


/******************************************************************************/
/* START DATABASEEDITOR UPDATES                                               */
/******************************************************************************/

create table CustomerLoginSerialGroup (
LoginID              numeric                  not null,
LMGroupID            numeric                  not null,
constraint PK_CUSTOMERLOGINSERIALGROUP primary key  (LoginID, LMGroupID)
);
go
alter table CustomerLoginSerialGroup
   add constraint FK_CsLgSG_CsL foreign key (LoginID)
      references CustomerLogin (LogInID);
alter table CustomerLoginSerialGroup
   add constraint FK_CsLgSG_LMG foreign key (LMGroupID)
      references LMGroup (DeviceID);
go


insert into FDRInterface values (11,'TELEGYR','Receive,Receive for control','f');
go
insert into FDRInterfaceOption values(11, 'Point', 1, 'Text', '(none)' );
insert into FDRInterfaceOption values(11, 'Group', 2, 'Query', 'select GroupName from FDRTelegyrGroup');
   

create table FDRTelegyrGroup (
GroupID              numeric              not null,
GroupName            varchar(40)          not null,
CollectionInterval   numeric              not null,
GroupType            varchar(20)          not null,
constraint PK_FDRTELEGYRGROUP primary key  (GroupID)
);
go


DELETE FROM DeviceStatistics;
DELETE FROM PortStatistics;
go
DROP TABLE DeviceStatistics;
DROP TABLE PortStatistics;
go

create table DynamicPAOStatistics (
PAOBjectID           numeric              not null,
StatisticType        varchar(16)          not null,
Requests             numeric              not null,
Completions          numeric              not null,
Attempts             numeric              not null,
CommErrors           numeric              not null,
ProtocolErrors       numeric              not null,
SystemErrors         numeric              not null,
StartDateTime        datetime             not null,
StopDateTime         datetime             not null,
constraint PK_DYNAMICPAOSTATISTICS primary key  (PAOBjectID,StatisticType)
)
go
alter table DynamicPAOStatistics
   add constraint FK_PASt_YkPA foreign key (PAOBjectID)
      references YukonPAObject (PAObjectID)
go

create table LMGroupExpressCommAddress (
AddressID            numeric              not null,
AddressType          varchar(20)          not null,
Address              numeric              not null,
AddressName          varchar(30)          not null,
constraint PK_LMGROUPEXPRESSCOMMADDRESS primary key  (AddressID)
)
go
insert into LMGroupExpressCommAddress values( 0, '(none)', 0, '(none)' );


create table LMGroupExpressComm (
LMGroupID            numeric              not null,
RouteID              numeric              not null,
SerialNumber         varchar(10)          not null,
ServiceProviderID    numeric              not null,
GeoID                numeric              not null,
SubstationID         numeric              not null,
FeederID             numeric              not null,
ZipCodeAddress       numeric              not null,
UDAddress            numeric              not null,
ProgramID            numeric              not null,
SplinterAddress      numeric              not null,
AddressUsage         varchar(10)          not null,
RelayUsage           char(15)             not null,
constraint PK_LMGROUPEXPRESSCOMM primary key  (LMGroupID)
)
go
alter table LMGroupExpressComm
   add constraint FK_ExCad_LMExCm foreign key (FeederID)
      references LMGroupExpressCommAddress (AddressID)
go
alter table LMGroupExpressComm
   add constraint FK_ExCG_LMExCm foreign key (GeoID)
      references LMGroupExpressCommAddress (AddressID)
go
alter table LMGroupExpressComm
   add constraint FK_ExCSp_LMExCm foreign key (ServiceProviderID)
      references LMGroupExpressCommAddress (AddressID)
go
alter table LMGroupExpressComm
   add constraint FK_ExCP_LMExCm foreign key (ProgramID)
      references LMGroupExpressCommAddress (AddressID)
go
alter table LMGroupExpressComm
   add constraint FK_ExCSb_LMExCm foreign key (SubstationID)
      references LMGroupExpressCommAddress (AddressID)
go
alter table LMGroupExpressComm
   add constraint FK_LGrEx_LMG foreign key (LMGroupID)
      references LMGroup (DeviceID)
go
alter table LMGroupExpressComm
   add constraint FK_LGrEx_Rt foreign key (RouteID)
      references Route (RouteID)
go

/******************************************************************************/
/* END DATABASEEDITOR UPDATES                                                 */
/******************************************************************************/




/******************************************************************************/
/* START TDC UPDATES                                                 */
/******************************************************************************/
/******************************************************************************/
/* END TDC UPDATES                                                 */
/******************************************************************************/




/******************************************************************************/
/* START GRAPH UPDATES                                                 */
/******************************************************************************/

/******************************************************************************/
/* END GRAPH UPDATES                                                 */
/******************************************************************************/
