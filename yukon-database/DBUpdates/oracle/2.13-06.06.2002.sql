/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.13', 'Ryan', '6-JUN-2002', 'Added CustomerLoginSerialGroup table and data for the telegyr interface, added PAOStatistics Table and ExpressCommm Support');


/******************************************************************************/
/* START DATABASEEDITOR UPDATES                                               */
/******************************************************************************/

create table CustomerLoginSerialGroup  (
   LoginID              NUMBER                           not null,
   LMGroupID            NUMBER                           not null,
   constraint PK_CUSTOMERLOGINSERIALGROUP primary key (LoginID, LMGroupID),
   constraint FK_CsLgSG_CsL foreign key (LoginID)
         references CustomerLogin (LogInID),
   constraint FK_CsLgSG_LMG foreign key (LMGroupID)
         references LMGroup (DeviceID)
);
insert into FDRInterface values (11,'TELEGYR','Receive,Receive for control','f');
insert into FDRInterfaceOption values(11, 'Point', 1, 'Text', '(none)' );
insert into FDRInterfaceOption values(11, 'Group', 2, 'Query', 'select GroupName from FDRTelegyrGroup');


create table FDRTelegyrGroup  (
   GroupID              NUMBER                           not null,
   GroupName            VARCHAR2(40)                     not null,
   CollectionInterval   NUMBER                           not null,
   GroupType            VARCHAR2(20)                     not null,
   constraint PK_FDRTELEGYRGROUP primary key (GroupID)
);



DELETE FROM DeviceStatistics;
DELETE FROM PortStatistics;
DROP TABLE DeviceStatistics;
DROP TABLE PortStatistics;

create table DynamicPAOStatistics  (
   PAOBjectID           NUMBER                           not null,
   StatisticType        VARCHAR2(16)                     not null,
   Requests             NUMBER                           not null,
   Completions          NUMBER                           not null,
   Attempts             NUMBER                           not null,
   CommErrors           NUMBER                           not null,
   ProtocolErrors       NUMBER                           not null,
   SystemErrors         NUMBER                           not null,
   StartDateTime        DATE                             not null,
   StopDateTime         DATE                             not null,
   constraint PK_DYNAMICPAOSTATISTICS primary key (PAOBjectID,StatisticType),
   constraint FK_PASt_YkPA foreign key (PAOBjectID)
         references YukonPAObject (PAObjectID)
);


create table LMGroupExpressCommAddress  (
   AddressID            NUMBER                           not null,
   AddressType          VARCHAR2(20)                     not null,
   Address              NUMBER                           not null,
   AddressName          VARCHAR2(30)                     not null,
   constraint PK_LMGROUPEXPRESSCOMMADDRESS primary key (AddressID)
);
insert into LMGroupExpressCommAddress values( 0, '(none)', 0, '(none)' );


create table LMGroupExpressComm  (
   LMGroupID            NUMBER                           not null,
   RouteID              NUMBER                           not null,
   SerialNumber         VARCHAR2(10)                     not null,
   ServiceProviderID    NUMBER                           not null,
   GeoID                NUMBER                           not null,
   SubstationID         NUMBER                           not null,
   FeederID             NUMBER                           not null,
   ZipCodeAddress       NUMBER                           not null,
   UDAddress            NUMBER                           not null,
   ProgramID            NUMBER                           not null,
   SplinterAddress      NUMBER                           not null,
   AddressUsage         VARCHAR2(10)                     not null,
   RelayUsage           CHAR(15)                         not null,
   constraint PK_LMGROUPEXPRESSCOMM primary key (LMGroupID),
   constraint FK_ExCad_LMExCm foreign key (FeederID)
         references LMGroupExpressCommAddress (AddressID),
   constraint FK_ExCG_LMExCm foreign key (GeoID)
         references LMGroupExpressCommAddress (AddressID),
   constraint FK_ExCSp_LMExCm foreign key (ServiceProviderID)
         references LMGroupExpressCommAddress (AddressID),
   constraint FK_ExCP_LMExCm foreign key (ProgramID)
         references LMGroupExpressCommAddress (AddressID),
   constraint FK_ExCSb_LMExCm foreign key (SubstationID)
         references LMGroupExpressCommAddress (AddressID),
   constraint FK_LGrEx_LMG foreign key (LMGroupID)
         references LMGroup (DeviceID),
   constraint FK_LGrEx_Rt foreign key (RouteID)
         references Route (RouteID)
);

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
