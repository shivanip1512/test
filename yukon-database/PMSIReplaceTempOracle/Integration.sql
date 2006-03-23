/*==============================================================*/
/* Table CRSToSAM_PTJ	                                        */
/*==============================================================*/
create table CRSToSAM_PTJ  (
  PTJID             NUMBER	NOT NULL, 
  PREMISENUMBER     NUMBER,
  DEBTORNUMBER      VARCHAR2(10 BYTE),
  PTJTYPE           VARCHAR2(5 BYTE),
  TIMESTAMP         DATE,
  CONSUMPTIONTYPE   VARCHAR2(5 BYTE),
  SERVUTILITYTYPE   CHAR(1 BYTE),
  NOTES             VARCHAR2(300 BYTE),
  STREETADDRESS1    VARCHAR2(40 BYTE),
  STREETADDRESS2    VARCHAR2(40 BYTE),
  CITYNAME          VARCHAR2(32 BYTE),
  STATECODE         VARCHAR2(2 BYTE),
  ZIPCODE           VARCHAR2(12 BYTE),
  FIRSTNAME         VARCHAR2(20 BYTE),
  LASTNAME          VARCHAR2(32 BYTE),
  HOMEPHONE         VARCHAR2(14 BYTE),
  WORKPHONE         VARCHAR2(14 BYTE),
  CRSCONTACTPHONE   VARCHAR2(14 BYTE),
  CRSLOGGEDUSER     VARCHAR2(12 BYTE),
  PRESENCEREQUIRED  CHAR(1 BYTE),
  AIRCONDITIONER    CHAR(1 BYTE),
  WATERHEATER       CHAR(1 BYTE),
  SERVICENUMBER     VARCHAR2(3 BYTE),
  METERNUMBER       VARCHAR2(14 BYTE)
);

alter table CRSToSAM_PTJ
   add constraint CRSTOSAM_PTJ primary key (PTJID);

/*==============================================================*/
/* Table: FailureCRSToSAM_PTJ	                                */
/*==============================================================*/
create table FailureCRSToSAM_PTJ  (
  PTJID             NUMBER	NOT NULL,
  PREMISENUMBER     NUMBER,
  DEBTORNUMBER      VARCHAR2(10 BYTE),
  PTJTYPE           VARCHAR2(5 BYTE),
  TIMESTAMP         DATE,
  CONSUMPTIONTYPE   VARCHAR2(5 BYTE),
  SERVUTILITYTYPE   CHAR(1 BYTE),
  NOTES             VARCHAR2(300 BYTE),
  STREETADDRESS1    VARCHAR2(40 BYTE),
  STREETADDRESS2    VARCHAR2(40 BYTE),
  CITYNAME          VARCHAR2(32 BYTE),
  STATECODE         VARCHAR2(2 BYTE),
  ZIPCODE           VARCHAR2(12 BYTE),
  FIRSTNAME         VARCHAR2(20 BYTE),
  LASTNAME          VARCHAR2(32 BYTE),
  HOMEPHONE         VARCHAR2(14 BYTE),
  WORKPHONE         VARCHAR2(14 BYTE),
  CRSCONTACTPHONE   VARCHAR2(14 BYTE),
  CRSLOGGEDUSER     VARCHAR2(12 BYTE),
  PRESENCEREQUIRED  CHAR(1 BYTE),
  AIRCONDITIONER    CHAR(1 BYTE),
  WATERHEATER       CHAR(1 BYTE),
  SERVICENUMBER     VARCHAR2(3 BYTE),
  METERNUMBER       VARCHAR2(14 BYTE),
  ERRORMSG          VARCHAR2(1024 BYTE),
  DATETIME          DATE
);

alter table FailureCRSToSAM_PTJ
   add constraint PK_FAILURECRSTOSAM_PTJ primary key (PTJID);


/*==============================================================*/
/* Table: SAMToCRS_PTJ	                                        */
/*==============================================================*/
create table SAMToCRS_PTJ  (
   PTJID		NUMBER				NOT NULL,
   PremiseNumber	NUMBER		                null,
   DebtorNumber		VARCHAR2(10)	                null,
   WorkOrderNumber	VARCHAR2(30)			null,
   StatusCode		VARCHAR2(2)			null,
   Timestamp		VARCHAR2(16)			null,
   STARSUserName	VARCHAR2(10)			null
);

alter table SAMToCRS_PTJ
   add constraint PK_SAMToCRS_PTJ primary key (PTJID);

/*==============================================================*/
/* Table: CRSToSAM_PremiseMeterChange                           */
/*==============================================================*/
create table CRSToSAM_PremiseMeterChange  (   
  CHANGEID         NUMBER		NOT NULL,
  PREMISENUMBER    NUMBER,
  NEWDEBTORNUMBER  VARCHAR2(10 BYTE),
  TRANSID          VARCHAR2(4 BYTE),
  STREETADDRESS1   VARCHAR2(40 BYTE),
  STREETADDRESS2   VARCHAR2(40 BYTE),
  CITYNAME         VARCHAR2(32 BYTE),
  STATECODE        VARCHAR2(2 BYTE),
  ZIPCODE          VARCHAR2(12 BYTE),
  FIRSTNAME        VARCHAR2(20 BYTE),
  LASTNAME         VARCHAR2(32 BYTE),
  HOMEPHONE        VARCHAR2(14 BYTE),
  WORKPHONE        VARCHAR2(14 BYTE),
  OLDMETERNUMBER   VARCHAR2(14 BYTE),
  NEWMETERNUMBER   VARCHAR2(14 BYTE)
);

alter table CRSToSAM_PremiseMeterChange
   add constraint PK_CRSTOSAMPRMMTRCHG primary key (ChangeID);

/*==============================================================*/
/* Table: FailureCRSToSAM_PremMeterChg                          */
/*==============================================================*/ 
  CREATE TABLE FAILURECRSTOSAM_PREMMETERCHG
(
  CHANGEID         NUMBER		NOT NULL,
  PREMISENUMBER    NUMBER,
  NEWDEBTORNUMBER  VARCHAR2(10 BYTE),
  TRANSID          VARCHAR2(4 BYTE),
  STREETADDRESS1   VARCHAR2(40 BYTE),
  STREETADDRESS2   VARCHAR2(40 BYTE),
  CITYNAME         VARCHAR2(32 BYTE),
  STATECODE        VARCHAR2(2 BYTE),
  ZIPCODE          VARCHAR2(12 BYTE),
  FIRSTNAME        VARCHAR2(20 BYTE),
  LASTNAME         VARCHAR2(32 BYTE),
  HOMEPHONE        VARCHAR2(14 BYTE),
  WORKPHONE        VARCHAR2(14 BYTE),
  OLDMETERNUMBER   VARCHAR2(14 BYTE),
  NEWMETERNUMBER   VARCHAR2(14 BYTE),
  ERRORMSG         VARCHAR2(1024 BYTE),
  DATETIME         DATE
);

alter table FailureCRSToSAM_PremMeterChg
   add constraint PK_FAILCRSTOSAMPRMMTRCHG primary key (ChangeID);


/*==============================================================*/
/* Table: CRSToSAM_PTJAdditionalMeters                           */
/*==============================================================*/
create table CRSToSAM_PTJAdditionalMeters    ( 
   PTJID		NUMBER				NOT NULL,
   MeterNumber	        VARCHAR2(14)			null
);	

alter table CRSToSAM_PTJAdditionalMeters
   add constraint PK_CRSTOSAMPTJADDMTRS primary key (PTJID, MeterNumber);

alter table CRSToSAM_PTJAdditionalMeters
   add constraint FK_PTJADDMTRS_CRSTOSAMPTJ foreign key (PTJID)
      references CRSToSAM_PTJ (PTJID)

/*==============================================================*/
/* Table: SwitchReplacement                                     */
/*==============================================================*/
create table SwitchReplacement(
   ReplacementID		NUMBER							not null,
   SerialNumber			VARCHAR2(10)                    not null,
   WOType				VARCHAR2(20)                    not null,
   DeviceType           VARCHAR2(30)                    not null,
   UserName				VARCHAR2(30)					not null
);

alter table SwitchReplacement 
   add constraint PK_SwitchReplace primary key (ReplacementID);

/*==============================================================*/
/* Table: Failure_SwitchReplacement                             */
/*==============================================================*/
create table Failure_SwitchReplacement(
   ReplacementID		NUMBER							not null,
   SerialNumber			VARCHAR2(10)                    not null,
   WOType				VARCHAR2(20)                    not null,
   DeviceType           VARCHAR2(30)                    not null,
   UserName				VARCHAR2(30)					not null,
   ErrorMsg				VARCHAR2(1024)					not null,
   DateTime             DATE                            not null
);

alter table Failure_SwitchReplacement 
   add constraint PK_Fail_SwitchReplace primary key (ReplacementID);

/*No longer used since we moved it out of JMX
insert into YukonServices values( -7, 'CRS_Integration', 'com.cannontech.jmx.services.DynamicCRSIntegrator', '(none)', '(none)' );

update YukonServices set ServiceID = 7 where ServiceID = -7;   */