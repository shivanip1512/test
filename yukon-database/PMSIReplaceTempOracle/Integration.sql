/*==============================================================*/
/* Table CRSToSAM_PTJ	                                        */
/*==============================================================*/
create table CRSToSAM_PTJ  (
   PTJID		NUMBER				null,
   PremiseNumber        NUMBER		                null,
   DebtorNumber         VARCHAR2(10)			null,
   PTJType		VARCHAR2(3)			null,
   Timestamp		DATE				null,
   ConsumptionType	VARCHAR2(5)			null,
   ServUtilityType	CHAR()				null,
   Notes		VARCHAR2(300)			null,
   StreetAddress	VARCHAR2(60)			null,
   CityName		VARCHAR2(32)			null,
   StateCode		VARCHAR2(2)			null,
   ZipCode		VARCHAR2(12)			null,
   FirstName		VARCHAR2(20)			null,
   LastName		VARCHAR2(32)			null,
   HomePhone		VARCHAR2(14)			null,
   WorkPhone		VARCHAR2(14)			null,
   CRSContactPhone	VARCHAR2(14)			null,
   CRSLoggedUser	VARCHAR2(12)			null,
   PresenceRequired	CHAR()				null,
   AirConditioner	CHAR()				null,
   WaterHeater		CHAR()				null,
   ServiceNumber	VARCHAR2(3)			null,
   MeterNumber	        VARCHAR2(12)			null,
   MeterMfg		VARCHAR2(2)			null,  
);

alter table CRSToSAM_PTJ
   add constraint CRSTOSAM_PTJ primary key (PTJID);

/*==============================================================*/
/* Table: FailureCRSToSAM_PTJ	                                */
/*==============================================================*/
create table FailureCRSToSAM_PTJ  (
   PTJID		NUMBER				null,
   PremiseNumber        NUMBER		                null,
   DebtorNumber         VARCHAR2(10)			null,
   PTJType		VARCHAR2(3)			null,
   Timestamp		DATE				null,
   ConsumptionType	VARCHAR2(5)			null,
   ServUtilityType	CHAR()				null,
   Notes		VARCHAR2(300)			null,
   StreetAddress	VARCHAR2(60)			null,
   CityName		VARCHAR2(32)			null,
   StateCode		VARCHAR2(2)			null,
   ZipCode		VARCHAR2(12)			null,
   FirstName		VARCHAR2(20)			null,
   LastName		VARCHAR2(32)			null,
   HomePhone		VARCHAR2(14)			null,
   WorkPhone		VARCHAR2(14)			null,
   CRSContactPhone	VARCHAR2(14)			null,
   CRSLoggedUser	VARCHAR2(12)			null,
   PresenceRequired	CHAR()				null,
   AirConditioner	CHAR()				null,
   WaterHeater		CHAR()				null,
   ServiceNumber	VARCHAR2(3)			null,
   MeterNumber	        VARCHAR2(12)			null,
   MeterMfg		VARCHAR2(2)			null,
   ErrorMsg		VARCHAR2(1024)			null,
   DateTime		DATETIME			null	   
);

alter table FailureCRSToSAM_PTJ
   add constraint PK_FAILURECRSTOSAM_PTJ primary key (PTJID);


/*==============================================================*/
/* Table: SAMToCRS_PTJ	                                        */
/*==============================================================*/
create table SAMToCRS_PTJ  (
   PTJID		NUMBER				null,
   PremiseNumber	NUMBER		                null,
   DebtorNumber		VARCHAR2(10)	                null,
   WorkOrderNumber	VARCHAR2(30)			null,
   StatusCode		VARCHAR2(2)			null,
   Timestamp		VARCHAR2(16)			null,
   STARSUserName	VARCHAR2(10)			null,
);

alter table SAMToCRS_PTJ
   add constraint PK_SAMToCRS_PTJ primary key (PTJID);

/*==============================================================*/
/* Table: CRSToSAM_PremiseMeterChange                           */
/*==============================================================*/
create table CRSToSAM_PremiseMeterChange  (   
   ChangeID		NUMBER				null,
   PremiseNumber	NUMBER		                null,
   NewDebtorNumber	VARCHAR2(10)			null,
   TransID		VARCHAR2(4)			null,
   StreetAddress	VARCHAR2(60)			null,
   CityName		VARCHAR2(32)			null,
   StateCode		VARCHAR2(2)			null,
   ZipCode		VARCHAR2(12)			null,
   FirstName		VARCHAR2(20)			null,
   LastName		VARCHAR2(32)			null,
   HomePhone		VARCHAR2(14)			null,
   WorkPhone		VARCHAR2(14)			null,
   OldMeterNumber	VARCHAR2(12)			null,
   OldMeterMfg		VARCHAR2(2)			null,
   NewMeterNumber	VARCHAR2(12)			null,
   NewMeterMfg		VARCHAR2(2)			null
);

alter table CRSToSAM_PremiseMeterChange
   add constraint PK_CRSTOSAMPRMMTRCHG primary key (ChangeID);

/*==============================================================*/
/* Table: FailureCRSToSAM_PremiseMeterChange                    */
/*==============================================================*/
create table FailureCRSToSAM_PremiseMeterChange  (   
   ChangeID		NUMBER				null,
   PremiseNumber	NUMBER		                null,
   NewDebtorNumber	VARCHAR2(10)			null,
   TransID		VARCHAR2(4)			null,
   StreetAddress	VARCHAR2(60)			null,
   CityName		VARCHAR2(32)			null,
   StateCode		VARCHAR2(2)			null,
   ZipCode		VARCHAR2(12)			null,
   FirstName		VARCHAR2(20)			null,
   LastName		VARCHAR2(32)			null,
   HomePhone		VARCHAR2(14)			null,
   WorkPhone		VARCHAR2(14)			null,
   OldMeterNumber	VARCHAR2(12)			null,
   OldMeterMfg		VARCHAR2(2)			null,
   NewMeterNumber	VARCHAR2(12)			null,
   NewMeterMfg		VARCHAR2(2)			null,
   ErrorMsg		VARCHAR2(1024)			null,
   DateTime		DATETIME			null	
);

alter table FailureCRSToSAM_PremiseMeterChange
   add constraint PK_FAILCRSTOSAMPRMMTRCHG primary key (ChangeID);


/*==============================================================*/
/* Table: CRSToSAM_PTJAdditionalMeterInstalls                   */
/*==============================================================*/
create table CRSToSAM_PTJAdditionalMeterInstalls  ( 
   PTJID		NUMBER				null,
   MeterNumber	        VARCHAR2(12)			null,
   MeterMfg		VARCHAR2(2)			null,
);	

alter table FailureCRSToSAM_PremiseMeterChange
   add constraint PK_CRSTOSAMPTJADDMTRINSTLLS primary key (PTJID, MeterNumber);

alter table MeterHardwareBase
   add constraint FK_PTJADDMTRINSTLLS_REF_CSTLS_CRSTOSAMPTJ foreign key (PTJID)
      references CRSToSAM_PTJ (PTJID)


/*==============================================================*/
/* Table: SAM_MassSwitchChangeout                 		*/
/*==============================================================*/
create table SAM_MassSwitchChangeout (
   PremiseNumber	NUMBER				null,
   OldSerialNumber	VARCHAR2(30)			null,
   NewSerialNumber	VARCHAR2(30)			null,
   ServiceCompanyID	NUMBER				null,
   InstallDate		DATETIME			null,
   ApplianceTypeID	NUMBER				null
);

alter table SAM_MassSwitchChangeout
   add constraint PK_MASSSWITCHCHANGEOUT primary key (PremiseNumber, OldSerialNumber);


/*==============================================================*/
/* Table: FailureSAM_MassSwitchChangeout                 	*/
/*==============================================================*/
create table FailureSAM_MassSwitchChangeout (
   PremiseNumber	NUMBER				null,
   OldSerialNumber	VARCHAR2(30)			null,
   NewSerialNumber	VARCHAR2(30)			null,
   ServiceCompanyID	NUMBER				null,
   InstallDate		DATETIME			null,
   ApplianceTypeID	NUMBER				null,
   ErrorMsg		VARCHAR2(1024)			null,
   DateTime		DATETIME			null	
);

alter table SAM_MassSwitchChangeout
   add constraint PK_FAILSWITCHCHANGEOUT primary key (PremiseNumber, OldSerialNumber);