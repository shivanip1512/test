drop table LMProgramEnergyExchange cascade constraints
/
/*==============================================================*/
/* Table : LMProgramEnergyExchange                              */
/*==============================================================*/
create table LMProgramEnergyExchange (
   DeviceID             NUMBER                           not null,
   MinNotifyTime        NUMBER                           not null,
   Heading              VARCHAR2(40)                     not null,
   MessageHeader        VARCHAR2(160)                    not null,
   MessageFooter        VARCHAR2(160)                    not null,
   CanceledMessage      VARCHAR2(80)                     not null,
   StoppedEarlyMessage  VARCHAR2(80)                     not null,
   constraint PK_LMPROGRAMENERGYEXCHANGE primary key (DeviceID),
   constraint FK_PrgEnExc_DevID foreign key (DeviceID)
         references DEVICE (DEVICEID)
)
/


drop table LMEnergyExchangeProgramOffer cascade constraints
/
/*==============================================================*/
/* Table : LMEnergyExchangeProgramOffer                         */
/*==============================================================*/
create table LMEnergyExchangeProgramOffer (
   DeviceID             NUMBER                           not null,
   OfferID              NUMBER                           not null,
   RunStatus            VARCHAR2(20)                     not null,
   OfferDate            DATE                             not null,
   constraint PK_LMENERGYEXCHANGEPROGRAMOFFE primary key (OfferID),
   constraint FK_EnExOff_PrgEnEx foreign key (DeviceID)
         references LMProgramEnergyExchange (DeviceID)
)
/


drop table LMEnergyExchangeOfferRevision cascade constraints
/
/*==============================================================*/
/* Table : LMEnergyExchangeOfferRevision                        */
/*==============================================================*/
create table LMEnergyExchangeOfferRevision (
   OfferID              NUMBER                           not null,
   RevisionNumber       NUMBER                           not null,
   ActionDateTime       DATE                             not null,
   NotificationDateTime DATE                             not null,
   OfferExpirationDateTime DATE                             not null,
   AdditionalInfo       VARCHAR2(100)                    not null,
   constraint PK_LMENERGYEXCHANGEOFFERREVISI primary key (OfferID, RevisionNumber),
   constraint FK_EExOffR_ExPrOff foreign key (OfferID)
         references LMEnergyExchangeProgramOffer (OfferID)
)
/


drop table LMEnergyExchangeHourlyOffer cascade constraints
/
/*==============================================================*/
/* Table : LMEnergyExchangeHourlyOffer                          */
/*==============================================================*/

create table LMEnergyExchangeHourlyOffer (
   OfferID              NUMBER                           not null,
   RevisionNumber       NUMBER                           not null,
   Hour                 NUMBER                           not null,
   Price                NUMBER                           not null,
   AmountRequested      FLOAT                            not null,
   constraint PK_LMENERGYEXCHANGEHOURLYOFFER primary key (OfferID, RevisionNumber),
   constraint FK_ExHrOff_ExOffRv foreign key (OfferID, RevisionNumber)
         references LMEnergyExchangeOfferRevision (OfferID, RevisionNumber)
)
/


drop table LMEnergyExchangeCustomerList cascade constraints
/
/*==============================================================*/
/* Table : LMEnergyExchangeCustomerList                         */
/*==============================================================*/
create table LMEnergyExchangeCustomerList (
   DeviceID             NUMBER                           not null,
   LMCustomerDeviceID   NUMBER                           not null,
   CustomerOrder        NUMBER                           not null,
   constraint PK_LMENERGYEXCHANGECUSTOMERLIS primary key (DeviceID, LMCustomerDeviceID),
   constraint FK_ExCsLs_PrEx foreign key (DeviceID)
         references LMProgramEnergyExchange (DeviceID),
   constraint FK_ExCsLs_CstBs foreign key (LMCustomerDeviceID)
         references CICustomerBase (DeviceID)
)
/


drop table LMEnergyExchangeCustomerReply cascade constraints
/
/*==============================================================*/
/* Table : LMEnergyExchangeCustomerReply                        */
/*==============================================================*/
create table LMEnergyExchangeCustomerReply (
   CustomerID           NUMBER                           not null,
   OfferID              NUMBER                           not null,
   AcceptStatus         VARCHAR2(30)                     not null,
   AcceptDateTime       DATE                             not null,
   RevisionNumber       NUMBER                           not null,
   IPAddressOfAcceptUser VARCHAR2(15)                     not null,
   UserIDName           VARCHAR2(40)                     not null,
   NameOfAcceptPerson   VARCHAR2(40)                     not null,
   EnergyExchangeNotes  VARCHAR2(120)                    not null,
   constraint PK_LMENERGYEXCHANGECUSTOMERREP primary key (CustomerID, OfferID, RevisionNumber),
   constraint FK_ExCsRp_CstBs foreign key (CustomerID)
         references CICustomerBase (DeviceID),
   constraint FK_LMENERGY_REFEXCSTR_LMENERGY foreign key (OfferID, RevisionNumber)
         references LMEnergyExchangeOfferRevision (OfferID, RevisionNumber)
         on delete cascade
)
/


drop table LMEnergyExchangeHourlyCustomer cascade constraints
/
/*==============================================================*/
/* Table : LMEnergyExchangeHourlyCustomer                       */
/*==============================================================*/

create table LMEnergyExchangeHourlyCustomer (
   CustomerID           NUMBER                           not null,
   OfferID              NUMBER                           not null,
   RevisionNumber       NUMBER                           not null,
   Hour                 NUMBER                           not null,
   AmountCommitted      FLOAT                            not null,
   constraint PK_LMENERGYEXCHANGEHOURLYCUSTO primary key (CustomerID, OfferID, RevisionNumber, Hour),
   constraint FK_ExHrCs_ExCsRp foreign key (CustomerID, OfferID, RevisionNumber)
         references LMEnergyExchangeCustomerReply (CustomerID, OfferID, RevisionNumber)
         on delete cascade
)
/