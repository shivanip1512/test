/* Remove the Historical Event Viewer */
delete from displaycolumns where displaynum=2;
delete from display where displaynum=2;

alter table dynamiccapcontrolstrategy add LastPointUpdate DATE;
UPDATE dynamiccapcontrolstrategy SET LastPointUpdate = '01-JAN-1990';
alter table dynamiccapcontrolstrategy MODIFY LastPointUpdate NOT NULL;
/* SQLServer */
/* alter table dynamiccapcontrolstrategy add LastPointUpdate DATETIME not null DEFAULT '01-JAN-1990'; */

alter table lmgroupversacomserial modify relayusage CHAR(7);
/* SQLServer */
/* alter table lmgroupversacomserial alter column relayusage CHAR(7); */


/*==============================================================*/
/* Table : EnergyCompany                                        */
/*==============================================================*/
create table EnergyCompany  (
   EnergyCompanyID      NUMBER                           not null,
   Name                 VARCHAR2(60)                     not null,
   RouteID              NUMBER                           not null,
   constraint PK_ENERGYCOMPANY primary key (EnergyCompanyID),
   constraint FK_EnCmpRt foreign key (RouteID)
         references ROUTE (ROUTEID)
);
/*==============================================================*/
/* Index: EnCmpName                                             */
/*==============================================================*/
create unique index EnCmpName on EnergyCompany (
   Name ASC
);


/*==============================================================*/
/* Table : OperatorLogin                                        */
/*==============================================================*/
create table OperatorLogin  (
   LoginID              NUMBER                           not null,
   Username             VARCHAR2(30)                     not null,
   Password             VARCHAR2(20),
   LoginType            VARCHAR2(20)                     not null,
   LoginCount           NUMBER                           not null,
   LastLogin            DATE                             not null,
   Status               VARCHAR2(20)                     not null,
   constraint PK_OPERATORLOGIN primary key (LoginID)
);
/*==============================================================*/
/* Index: Index_OpLogNam                                        */
/*==============================================================*/
create unique index Index_OpLogNam on OperatorLogin (
   Username ASC
);


/*==============================================================*/
/* Table : LMMACSScheduleOperatorList                           */
/*==============================================================*/
create table LMMACSScheduleOperatorList  (
   ScheduleID           NUMBER,
   OperatorLoginID      NUMBER,
   constraint FK_OpLgLMMcSchOpLs foreign key (OperatorLoginID)
         references OperatorLogin (LoginID),
   constraint FK_MCSchLMMcSchOpLs foreign key (ScheduleID)
         references MACSchedule (ScheduleID)
);


/*==============================================================*/
/* Table : EnergyCompanyCustomerList                            */
/*==============================================================*/
create table EnergyCompanyCustomerList  (
   EnergyCompanyID      NUMBER,
   CustomerID           NUMBER,
   constraint FK_EnCmpEnCmpCsLs foreign key (EnergyCompanyID)
         references EnergyCompany (EnergyCompanyID),
   constraint FK_CICstBsEnCmpCsLs foreign key (CustomerID)
         references CICustomerBase (DeviceID)
);


/*==============================================================*/
/* Table : OperatorLoginGraphList                               */
/*==============================================================*/
create table OperatorLoginGraphList  (
   OperatorLoginID      NUMBER,
   GraphDefinitionID    NUMBER,
   constraint FK_OpLgOpLgGrLs foreign key (OperatorLoginID)
         references OperatorLogin (LoginID),
   constraint FK_OpLgOpLgGrLs foreign key (GraphDefinitionID)
         references GRAPHDEFINITION (GRAPHDEFINITIONID)
);
