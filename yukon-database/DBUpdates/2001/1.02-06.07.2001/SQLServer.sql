/*==============================================================*/
/*--------------- BEGIN dbeditor.sql ---------------------------*/
/*==============================================================*/
/* extend the SystemLog tables USERNAME column's size */
alter table SystemLog alter column UserName VARCHAR(30);
/* extend the LMGroupVersacom tables RelayUsage column's size */
alter table LMGroupVersacom alter column RelayUsage CHAR(7);

/*==============================================================*/
/* Table : FDRInterface                                         */
/*==============================================================*/
create table FDRInterface (
InterfaceID          numeric              not null,
InterfaceName        varchar(30)          not null,
PossibleDirections   varchar(40)          not null,
constraint PK_FDRINTERFACE primary key  (InterfaceID)
)
go
insert into FDRInterface values ( 1, 'INET', 'Send,Receive' );
insert into FDRInterface values ( 2, 'ACS', 'Send,Receive' );
insert into FDRInterface values ( 3, 'VALMET', 'Send,Receive' );
insert into FDRInterface values ( 4, 'CYGNET', 'Receive' );


/*==============================================================*/
/* Table : FDRInterfaceOption                                   */
/*==============================================================*/
create table FDRInterfaceOption (
InterfaceID          numeric              not null,
OptionLabel          varchar(20)          not null,
OptionType           varchar(8)           not null,
OptionValues         varchar(150)         not null,
constraint PK_FDRINTERFACEOPTION primary key  (InterfaceID, OptionLabel)
)
go
insert into FDRInterfaceOption values(1, 'DEVICE', 'Text', '(none)' );
insert into FDRInterfaceOption values(1, 'POINT', 'Text', '(none)' );
insert into FDRInterfaceOption values(1, 'DESTINATION', 'Text', '(none)' );
insert into FDRInterfaceOption values(2, 'CATEGORY', 'Combo', 'PSEUDO,REAL' );
insert into FDRInterfaceOption values(2, 'REMOTE', 'Text', '(none)' );
insert into FDRInterfaceOption values(2, 'POINT', 'Text', '(none)' );
insert into FDRInterfaceOption values(3, 'POINT', 'Text', '(none)' );
insert into FDRInterfaceOption values(4, 'POINTID', 'Text', '(none)' );
alter table FDRInterfaceOption
   add constraint FK_FDRINTER_REFERENCE_FDRINTER foreign key (InterfaceID)
      references FDRInterface (InterfaceID)
go


/*==============================================================*/
/* Table : LMProgramDirectGear                                  */
/*==============================================================*/
/*==============================================================*/
/* ADDED A NEW COLUMN TO LMProgramDirectGear, WE WILL DROP IT THEN CREATE IT */
/*==============================================================*/
if exists (select 1
            from  sysobjects
           where  id = object_id('LMProgramDirectGear')
            and   type = 'U')
   drop table LMProgramDirectGear
go
create table LMProgramDirectGear (
DeviceID             numeric              not null,
GearName             varchar(30)          not null,
GearNumber           numeric              not null,
ControlMethod        varchar(30)          not null,
MethodRate           numeric              not null,
MethodPeriod         numeric              not null,
MethodRateCount      numeric              not null,
MethodCommand        varchar(40)          not null,
CycleRefreshRate     numeric              not null,
MethodStopType       varchar(20)          not null,
ChangeCondition      varchar(24)          not null,
ChangeDuration       numeric              not null,
ChangePriority       numeric              not null,
ChangeTriggerNumber  numeric              not null,
ChangeTriggerOffset  float                not null,
PercentReduction     numeric              not null,
constraint PK_LMPROGRAMDIRECTGEAR primary key  (DeviceID, GearNumber)
)
go
alter table LMProgramDirectGear
   add constraint FK_LMProgD_LMProgDGr foreign key (DeviceID)
      references LMProgramDirect (DeviceID)
go
/*==============================================================*/
/*--------------- END dbeditor.sql -----------------------------*/
/*==============================================================*/





/*==============================================================*/
/*--------------- BEGIN energyexchange.sql ---------------------*/
/*==============================================================*/
/*==============================================================*/
/* Table : LMProgramEnergyExchange                              */
/*==============================================================*/
create table LMProgramEnergyExchange (
DeviceID             numeric              not null,
MinNotifyTime        numeric              not null,
Heading              varchar(40)          not null,
MessageHeader        varchar(160)         not null,
MessageFooter        varchar(160)         not null,
CanceledMsg          varchar(80)          not null,
StoppedEarlyMsg      varchar(80)          not null,
constraint PK_LMPROGRAMENERGYEXCHANGE primary key  (DeviceID)
)
go
alter table LMProgramEnergyExchange
   add constraint FK_PrgEnExc_DevID foreign key (DeviceID)
      references DEVICE (DEVICEID)
go


/*==============================================================*/
/* Table : LMEnergyExchangeProgramOffer                         */
/*==============================================================*/
create table LMEnergyExchangeProgramOffer (
DeviceID             numeric              not null,
OfferID              numeric              not null,
RunStatus            varchar(20)          not null,
OfferDate            datetime             not null,
constraint PK_LMENERGYEXCHANGEPROGRAMOFFE primary key  (OfferID)
)
go
alter table LMEnergyExchangeProgramOffer
   add constraint FK_EnExOff_PrgEnEx foreign key (DeviceID)
      references LMProgramEnergyExchange (DeviceID)
go


/*==============================================================*/
/* Table : LMEnergyExchangeOfferRevision                        */
/*==============================================================*/
create table LMEnergyExchangeOfferRevision (
OfferID              numeric              not null,
RevisionNumber       numeric              not null,
ActionDateTime       datetime             not null,
NotificationDateTime datetime             not null,
OfferExpirationDateTime datetime             not null,
AdditionalInfo       varchar(100)         not null,
constraint PK_LMENERGYEXCHANGEOFFERREVISI primary key  (OfferID, RevisionNumber)
)
go
alter table LMEnergyExchangeOfferRevision
   add constraint FK_EExOffR_ExPrOff foreign key (OfferID)
      references LMEnergyExchangeProgramOffer (OfferID)
go


/*==============================================================*/
/* Table : LMEnergyExchangeHourlyOffer                          */
/*==============================================================*/
create table LMEnergyExchangeHourlyOffer (
OfferID              numeric              not null,
RevisionNumber       numeric              not null,
Hour                 numeric              not null,
Price                numeric              not null,
AmountRequested      float                not null,
constraint PK_LMENERGYEXCHANGEHOURLYOFFER primary key  (OfferID, RevisionNumber, Hour)
)
go
alter table LMEnergyExchangeHourlyOffer
   add constraint FK_ExHrOff_ExOffRv foreign key (OfferID, RevisionNumber)
      references LMEnergyExchangeOfferRevision (OfferID, RevisionNumber)
go


/*==============================================================*/
/* Table : LMEnergyExchangeCustomerList                         */
/*==============================================================*/
create table LMEnergyExchangeCustomerList (
DeviceID             numeric              not null,
LMCustomerDeviceID   numeric              not null,
CustomerOrder        numeric              not null,
constraint PK_LMENERGYEXCHANGECUSTOMERLIS primary key  (DeviceID, LMCustomerDeviceID)
)
go
alter table LMEnergyExchangeCustomerList
   add constraint FK_ExCsLs_CstBs foreign key (LMCustomerDeviceID)
      references CICustomerBase (DeviceID)
go
alter table LMEnergyExchangeCustomerList
   add constraint FK_ExCsLs_PrEx foreign key (DeviceID)
      references LMProgramEnergyExchange (DeviceID)
go


/*==============================================================*/
/* Table : LMEnergyExchangeCustomerReply                        */
/*==============================================================*/
create table LMEnergyExchangeCustomerReply (
CustomerID           numeric              not null,
OfferID              numeric              not null,
AcceptStatus         varchar(30)          not null,
AcceptDateTime       datetime             not null,
RevisionNumber       numeric              not null,
IPAddressOfAcceptUser varchar(15)          not null,
UserIDName           varchar(40)          not null,
NameOfAcceptPerson   varchar(40)          not null,
EnergyExchangeNotes  varchar(120)         not null,
constraint PK_LMENERGYEXCHANGECUSTOMERREP primary key  (CustomerID, OfferID, RevisionNumber)
)
go
alter table LMEnergyExchangeCustomerReply
   add constraint FK_ExCsRp_CstBs foreign key (CustomerID)
      references CICustomerBase (DeviceID)
go
alter table LMEnergyExchangeCustomerReply
   add constraint FK_LMENERGY_REFEXCSTR_LMENERGY foreign key (OfferID, RevisionNumber)
      references LMEnergyExchangeOfferRevision (OfferID, RevisionNumber)
go


/*==============================================================*/
/* Table : LMEnergyExchangeHourlyCustomer                       */
/*==============================================================*/
create table LMEnergyExchangeHourlyCustomer (
CustomerID           numeric              not null,
OfferID              numeric              not null,
RevisionNumber       numeric              not null,
Hour                 numeric              not null,
AmountCommitted      float                not null,
constraint PK_LMENERGYEXCHANGEHOURLYCUSTO primary key  (CustomerID, OfferID, RevisionNumber, Hour)
)
go
alter table LMEnergyExchangeHourlyCustomer
   add constraint FK_ExHrCs_ExCsRp foreign key (CustomerID, OfferID, RevisionNumber)
      references LMEnergyExchangeCustomerReply (CustomerID, OfferID, RevisionNumber)
go
/*==============================================================*/
/*--------------- END energyexchange.sql -----------------------*/
/*==============================================================*/





/*==============================================================*/
/*--------------- END Version.sql ------------------------------*/
/*==============================================================*/
/*insert into CTIDatabase values( '1.01', '(none)',  null, 'The initial database creation script created this.  The DateApplies is (null) because I can not find a way to set it in PowerBuilder!' );*/
insert into CTIDatabase values('1.02', 'Ryan Neuharth', '06-JUN-01', 'Added EnergyExhange and FDRInterface tables, also made some columns larger.');
/*==============================================================*/
/*--------------- END Version.sql ------------------------------*/
/*==============================================================*/
