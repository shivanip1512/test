/********************************************/
/* Oracle 9.2 DBupdates                     */
/*   Contains all 3.00 updates that         */
/*   occured after 3.0.13                   */
/********************************************/

/* @error ignore-remaining */
/*==============================================================*/
/* Add all the constraints that should be on the                */
/* database already. This is to ensure constraint existance     */
/*                                                              */
/*==============================================================*/

alter table ActivityLog
   add constraint PK_ACTIVITYLOG primary key (ActivityLogID);

alter table Address
   add constraint PK_ADDRESS primary key (AddressID);

alter table AlarmCategory
   add constraint PK_ALARMCATEGORYID primary key (AlarmCategoryID);

alter table BaseLine
   add constraint PK_BASELINE primary key (BaselineID);

alter table BillingFileFormats
   add constraint PK_BILLINGFILEFORMATS primary key (FormatID);

alter table CALCBASE
   add constraint PK_CALCBASE primary key (POINTID);

/*==============================================================*/
/* Index: Indx_ClcBaseUpdTyp                                    */
/*==============================================================*/
create index Indx_ClcBaseUpdTyp on CALCBASE (
   UPDATETYPE ASC
);

alter table CALCCOMPONENT
   add constraint PK_CALCCOMPONENT primary key (PointID, COMPONENTORDER);

/*==============================================================*/
/* Index: Indx_CalcCmpCmpType                                   */
/*==============================================================*/
create index Indx_CalcCmpCmpType on CALCCOMPONENT (
   COMPONENTTYPE ASC
);

alter table CAPBANK
   add constraint PK_CAPBANK primary key (DEVICEID);

alter table CAPCONTROLSUBSTATIONBUS
   add constraint SYS_C0013476 primary key (SubstationBusID);

/*==============================================================*/
/* Index: Indx_CSUBVPT                                          */
/*==============================================================*/
create index Indx_CSUBVPT on CAPCONTROLSUBSTATIONBUS (
   CurrentVarLoadPointID ASC
);

alter table CCFeederBankList
   add constraint PK_CCFEEDERBANKLIST primary key (FeederID, DeviceID);

alter table CCFeederSubAssignment
   add constraint PK_CCFEEDERSUBASSIGNMENT primary key (SubStationBusID, FeederID);

alter table CICustomerBase
   add constraint PK_CICUSTOMERBASE primary key (CustomerID);

alter table COLUMNTYPE
   add constraint SYS_C0013414 primary key (TYPENUM);

alter table CTIDatabase
   add constraint PK_CTIDATABASE primary key (Version, Build);

alter table CalcPointBaseline
   add constraint PK_CalcBsPt primary key (PointID);

alter table CapControlFeeder
   add constraint PK_CAPCONTROLFEEDER primary key (FeederID);

/*==============================================================*/
/* Index: Indx_CPCNFDVARPT                                      */
/*==============================================================*/
create index Indx_CPCNFDVARPT on CapControlFeeder (
   CurrentVarLoadPointID ASC
);

alter table CarrierRoute
   add constraint PK_CARRIERROUTE primary key (ROUTEID);

alter table CommErrorHistory
   add constraint PK_COMMERRORHISTORY primary key (CommErrorID);

alter table CommPort
   add constraint SYS_C0013112 primary key (PORTID);

alter table Contact
   add constraint PK_CONTACT primary key (ContactID);

alter table ContactNotification
   add constraint PK_CONTACTNOTIFICATION primary key (ContactNotifID);

alter table Customer
   add constraint PK_CUSTOMER primary key (CustomerID);

alter table CustomerAdditionalContact
   add constraint PK_CUSTOMERADDITIONALCONTACT primary key (ContactID, CustomerID);

alter table CustomerBaseLinePoint
   add constraint PK_CUSTOMERBASELINEPOINT primary key (CustomerID, PointID);

alter table CustomerLoginSerialGroup
   add constraint PK_CUSTOMERLOGINSERIALGROUP primary key (LoginID, LMGroupID);

alter table DEVICE
   add constraint PK_DEV_DEVICEID2 primary key (DEVICEID);

alter table DEVICE2WAYFLAGS
   add constraint PK_DEVICE2WAYFLAGS primary key (DEVICEID);

alter table DEVICECARRIERSETTINGS
   add constraint PK_DEVICECARRIERSETTINGS primary key (DEVICEID);

alter table DEVICEDIALUPSETTINGS
   add constraint PK_DEVICEDIALUPSETTINGS primary key (DEVICEID);

alter table DEVICEIDLCREMOTE
   add constraint PK_DEVICEIDLCREMOTE primary key (DEVICEID);

alter table DEVICEIED
   add constraint PK_DEVICEIED primary key (DEVICEID);

alter table DEVICELOADPROFILE
   add constraint PK_DEVICELOADPROFILE primary key (DEVICEID);

alter table DEVICEMCTIEDPORT
   add constraint PK_DEVICEMCTIEDPORT primary key (DEVICEID);

alter table DEVICEMETERGROUP
   add constraint PK_DEVICEMETERGROUP primary key (DEVICEID);

alter table DEVICESCANRATE
   add constraint PK_DEVICESCANRATE primary key (DEVICEID, SCANTYPE);

alter table DEVICETAPPAGINGSETTINGS
   add constraint PK_DEVICETAPPAGINGSETTINGS primary key (DEVICEID);

alter table DISPLAY
   add constraint SYS_C0013412 primary key (DISPLAYNUM);

/*==============================================================*/
/* Index: Indx_DISPLAYNAME                                      */
/*==============================================================*/
create unique index Indx_DISPLAYNAME on DISPLAY (
   NAME ASC
);

alter table DISPLAY2WAYDATA
   add constraint PK_DISPLAY2WAYDATA primary key (DISPLAYNUM, ORDERING);

alter table DISPLAYCOLUMNS
   add constraint PK_DISPLAYCOLUMNS primary key (DISPLAYNUM, TITLE);

alter table DYNAMICACCUMULATOR
   add constraint PK_DYNAMICACCUMULATOR primary key (POINTID);

alter table DYNAMICDEVICESCANDATA
   add constraint PK_DYNAMICDEVICESCANDATA primary key (DEVICEID);

alter table DYNAMICPOINTDISPATCH
   add constraint PK_DYNAMICPOINTDISPATCH primary key (POINTID);

alter table DateOfHoliday
   add constraint PK_DATEOFHOLIDAY primary key (HolidayScheduleID, HolidayName);

alter table DeviceCBC
   add constraint PK_DEVICECBC primary key (DEVICEID);

alter table DeviceCustomerList
   add constraint PK_DEVICECUSTOMERLIST primary key (DeviceID, CustomerID);

alter table DeviceDNP
   add constraint PK_DEVICEDNP primary key (DeviceID);

alter table DeviceDirectCommSettings
   add constraint PK_DEVICEDIRECTCOMMSETTINGS primary key (DEVICEID);

alter table DeviceMCT400Series
   add constraint PK_DEV400S primary key (DeviceID);

alter table DeviceRoutes
   add constraint PK_DEVICEROUTES primary key (DEVICEID, ROUTEID);

alter table DeviceWindow
   add constraint PK_DEVICEWINDOW primary key (DeviceID, Type);

alter table DynamicCCCapBank
   add constraint PK_DYNAMICCCCAPBANK primary key (CapBankID);

alter table DynamicCCFeeder
   add constraint PK_DYNAMICCCFEEDER primary key (FeederID);

alter table DynamicCCSubstationBus
   add constraint PK_DYNAMICCCSUBSTATIONBUS primary key (SubstationBusID);

alter table DynamicCalcHistorical
   add constraint PK_DYNAMICCALCHISTORICAL primary key (PointID);

alter table DynamicLMControlArea
   add constraint PK_DYNAMICLMCONTROLAREA primary key (DeviceID);

alter table DynamicLMControlAreaTrigger
   add constraint PK_DYNAMICLMCONTROLAREATRIGGER primary key (DeviceID, TriggerNumber);

alter table DynamicLMControlHistory
   add constraint PK_DYNLMCONTROLHISTORY primary key (PAObjectID);

alter table DynamicLMGroup
   add constraint PK_DYNAMICLMGROUP primary key (DeviceID, LMProgramID);

alter table DynamicLMProgram
   add constraint PK_DYNAMICLMPROGRAM primary key (DeviceID);

alter table DynamicLMProgramDirect
   add constraint PK_DYNAMICLMPROGRAMDIRECT primary key (DeviceID);

alter table DynamicPAOStatistics
   add constraint PK_DYNAMICPAOSTATISTICS primary key (PAOBjectID, StatisticType);

alter table DynamicPointAlarming
   add constraint PK_DYNAMICPOINTALARMING primary key (PointID, AlarmCondition);

alter table DynamicTags
   add constraint PK_DYNAMICTAGS primary key (InstanceID);

alter table EnergyCompany
   add constraint PK_ENERGYCOMPANY primary key (EnergyCompanyID);

/*==============================================================*/
/* Index: Indx_EnCmpName                                        */
/*==============================================================*/
create unique index Indx_EnCmpName on EnergyCompany (
   Name ASC
);

alter table EnergyCompanyCustomerList
   add constraint PK_ENERGYCOMPANYCUSTOMERLIST primary key (EnergyCompanyID, CustomerID);

alter table EnergyCompanyOperatorLoginList
   add constraint PK_ENERGYCOMPANYOPERATORLOGINL primary key (EnergyCompanyID, OperatorLoginID);

alter table FDRInterface
   add constraint PK_FDRINTERFACE primary key (InterfaceID);

alter table FDRInterfaceOption
   add constraint PK_FDRINTERFACEOPTION primary key (InterfaceID, Ordering);

alter table FDRTRANSLATION
   add constraint PK_FDRTrans primary key (POINTID, InterfaceType, TRANSLATION);

/*==============================================================*/
/* Index: Indx_FdrTransIntTyp                                   */
/*==============================================================*/
create index Indx_FdrTransIntTyp on FDRTRANSLATION (
   InterfaceType ASC
);

/*==============================================================*/
/* Index: Indx_FdrTrnsIntTypDir                                 */
/*==============================================================*/
create index Indx_FdrTrnsIntTypDir on FDRTRANSLATION (
   DIRECTIONTYPE ASC,
   InterfaceType ASC
);

alter table FDRTelegyrGroup
   add constraint PK_FDRTELEGYRGROUP primary key (GroupID);

alter table GRAPHDATASERIES
   add constraint SYS_GrphDserID primary key (GRAPHDATASERIESID);

/*==============================================================*/
/* Index: Indx_GrpDSerPtID                                      */
/*==============================================================*/
create index Indx_GrpDSerPtID on GRAPHDATASERIES (
   POINTID ASC
);

alter table GRAPHDEFINITION
   add constraint SYS_C0015109 primary key (GRAPHDEFINITIONID);

alter table GRAPHDEFINITION
   add constraint AK_GRNMUQ_GRAPHDEF unique (NAME);

alter table GatewayEndDevice
   add constraint PK_GATEWAYENDDEVICE primary key (SerialNumber, HardwareType, DataType);

alter table GenericMacro
   add constraint PK_GENERICMACRO primary key (OwnerID, ChildOrder, MacroType);

alter table GraphCustomerList
   add constraint PK_GRAPHCUSTOMERLIST primary key (GraphDefinitionID, CustomerID);

alter table HolidaySchedule
   add constraint PK_HOLIDAYSCHEDULE primary key (HolidayScheduleID);

/*==============================================================*/
/* Index: Indx_HolSchName                                       */
/*==============================================================*/
create unique index Indx_HolSchName on HolidaySchedule (
   HolidayScheduleName ASC
);

alter table LMCONTROLAREAPROGRAM
   add constraint PK_LMCONTROLAREAPROGRAM primary key (DEVICEID, LMPROGRAMDEVICEID);

alter table LMCONTROLAREATRIGGER
   add constraint PK_LMCONTROLAREATRIGGER primary key (DEVICEID, TRIGGERNUMBER);

alter table LMControlArea
   add constraint PK_LMCONTROLAREA primary key (DEVICEID);

alter table LMControlHistory
   add constraint PK_LMCONTROLHISTORY primary key (LMCtrlHistID);

alter table LMCurtailCustomerActivity
   add constraint PK_LMCURTAILCUSTOMERACTIVITY primary key (CustomerID, CurtailReferenceID);

/*==============================================================*/
/* Index: Index_LMCrtCstActID                                   */
/*==============================================================*/
create index Index_LMCrtCstActID on LMCurtailCustomerActivity (
   CustomerID ASC
);

/*==============================================================*/
/* Index: Index_LMCrtCstAckSt                                   */
/*==============================================================*/
create index Index_LMCrtCstAckSt on LMCurtailCustomerActivity (
   AcknowledgeStatus ASC
);

alter table LMCurtailProgramActivity
   add constraint PK_LMCURTAILPROGRAMACTIVITY primary key (CurtailReferenceID);

/*==============================================================*/
/* Index: Indx_LMCrtPrgActStTime                                */
/*==============================================================*/
create index Indx_LMCrtPrgActStTime on LMCurtailProgramActivity (
   CurtailmentStartTime ASC
);

alter table LMDirectCustomerList
   add constraint PK_LMDIRECTCUSTOMERLIST primary key (ProgramID, CustomerID);

alter table LMDirectOperatorList
   add constraint PK_LMDIRECTOPERATORLIST primary key (ProgramID, OperatorLoginID);

alter table LMEnergyExchangeCustomerList
   add constraint PK_LMENERGYEXCHANGECUSTOMERLIS primary key (ProgramID, CustomerID);

alter table LMEnergyExchangeCustomerReply
   add constraint PK_LMENERGYEXCHANGECUSTOMERREP primary key (CustomerID, OfferID, RevisionNumber);

alter table LMEnergyExchangeHourlyCustomer
   add constraint PK_LMENERGYEXCHANGEHOURLYCUSTO primary key (CustomerID, OfferID, RevisionNumber, Hour);

alter table LMEnergyExchangeHourlyOffer
   add constraint PK_LMENERGYEXCHANGEHOURLYOFFER primary key (OfferID, RevisionNumber, Hour);

alter table LMEnergyExchangeOfferRevision
   add constraint PK_LMENERGYEXCHANGEOFFERREVISI primary key (OfferID, RevisionNumber);

alter table LMEnergyExchangeProgramOffer
   add constraint PK_LMENERGYEXCHANGEPROGRAMOFFE primary key (OfferID);

alter table LMGroup
   add constraint PK_LMGROUP primary key (DeviceID);

alter table LMGroupEmetcon
   add constraint PK_LMGROUPEMETCON primary key (DEVICEID);

alter table LMGroupExpressCom
   add constraint PK_LMGROUPEXPRESSCOM primary key (LMGroupID);

alter table LMGroupExpressComAddress
   add constraint PK_LMGROUPEXPRESSCOMADDRESS primary key (AddressID);

alter table LMGroupMCT
   add constraint PK_LMGrpMCTPK primary key (DeviceID);

alter table LMGroupPoint
   add constraint PK_LMGROUPPOINT primary key (DEVICEID);

alter table LMGroupRipple
   add constraint PK_LMGROUPRIPPLE primary key (DeviceID);

alter table LMGroupVersacom
   add constraint PK_LMGROUPVERSACOM primary key (DEVICEID);

alter table LMMACSScheduleOperatorList
   add constraint PK_LMMACSSCHEDULEOPERATORLIST primary key (ScheduleID, OperatorLoginID);

alter table LMMacsScheduleCustomerList
   add constraint PK_LMMACSSCHEDULECUSTOMERLIST primary key (ScheduleID, LMCustomerDeviceID);

alter table LMPROGRAM
   add constraint PK_LMPROGRAM primary key (DEVICEID);

alter table LMProgramControlWindow
   add constraint PK_LMPROGRAMCONTROLWINDOW primary key (DeviceID, WindowNumber);

alter table LMProgramCurtailCustomerList
   add constraint PK_LMPROGRAMCURTAILCUSTOMERLIS primary key (CustomerID, ProgramID);

alter table LMProgramCurtailment
   add constraint PK_LMPROGRAMCURTAILMENT primary key (DeviceID);

alter table LMProgramDirect
   add constraint PK_LMPROGRAMDIRECT primary key (DeviceID);

alter table LMProgramDirectGear
   add constraint PK_LMPROGRAMDIRECTGEAR primary key (GearID);

alter table LMProgramDirectGear
   add constraint AK_AKEY_LMPRGDIRG_LMPROGRA unique (DeviceID, GearNumber);

alter table LMProgramDirectGroup
   add constraint PK_LMPROGRAMDIRECTGROUP primary key (DeviceID, GroupOrder);

alter table LMProgramEnergyExchange
   add constraint PK_LMPROGRAMENERGYEXCHANGE primary key (DeviceID);

alter table LMThermoStatGear
   add constraint PK_LMTHERMOSTATGEAR primary key (GearID);

alter table LOGIC
   add constraint SYS_C0013445 primary key (LOGICID);

alter table MACROROUTE
   add constraint PK_MACROROUTE primary key (ROUTEID, ROUTEORDER);

alter table MACSchedule
   add constraint PK_MACSCHEDULE primary key (ScheduleID);

alter table MACSimpleSchedule
   add constraint PK_MACSIMPLESCHEDULE primary key (ScheduleID);

alter table MCTBroadCastMapping
   add constraint PK_MCTBROADCASTMAPPING primary key (MCTBroadCastID, MctID);

alter table MCTConfig
   add constraint PK_MCTCONFIG primary key (ConfigID);

alter table MCTConfigMapping
   add constraint PK_MCTCONFIGMAPPING primary key (MctID, ConfigID);

alter table NotificationDestination
   add constraint PKey_NotDestID primary key (NotificationGroupID, DestinationOrder);

alter table NotificationGroup
   add constraint PK_NOTIFICATIONGROUP primary key (NotificationGroupID);

/*==============================================================*/
/* Index: Indx_NOTIFGRPNme                                      */
/*==============================================================*/
create unique index Indx_NOTIFGRPNme on NotificationGroup (
   GroupName ASC
);

alter table OperatorLoginGraphList
   add constraint PK_OPERATORLOGINGRAPHLIST primary key (OperatorLoginID, GraphDefinitionID);

alter table OperatorSerialGroup
   add constraint PK_OpSerGrp primary key (LoginID);

alter table PAOExclusion
   add constraint PK_PAOEXCLUSION primary key (ExclusionID);

/*==============================================================*/
/* Index: Indx_PAOExclus                                        */
/*==============================================================*/
create unique index Indx_PAOExclus on PAOExclusion (
   PaoID ASC,
   ExcludedPaoID ASC
);

alter table PAOowner
   add constraint PK_PAOOWNER primary key (OwnerID, ChildID);

alter table POINT
   add constraint Key_PT_PTID primary key (POINTID);

alter table POINT
   add constraint AK_KEY_PTNM_YUKPAOID unique (POINTNAME, PAObjectID);

/*==============================================================*/
/* Index: Indx_PointStGrpID                                     */
/*==============================================================*/
create index Indx_PointStGrpID on POINT (
   STATEGROUPID ASC
);

alter table POINTACCUMULATOR
   add constraint PK_POINTACCUMULATOR primary key (POINTID);

alter table POINTANALOG
   add constraint PK_POINTANALOG primary key (POINTID);

alter table POINTLIMITS
   add constraint PK_POINTLIMITS primary key (POINTID, LIMITNUMBER);

alter table POINTSTATUS
   add constraint PK_PtStatus primary key (POINTID);

alter table POINTUNIT
   add constraint PK_POINTUNITID primary key (POINTID);

alter table PORTDIALUPMODEM
   add constraint PK_PORTDIALUPMODEM primary key (PORTID);

alter table PORTLOCALSERIAL
   add constraint PK_PORTLOCALSERIAL primary key (PORTID);

alter table PORTRADIOSETTINGS
   add constraint PK_PORTRADIOSETTINGS primary key (PORTID);

alter table PORTSETTINGS
   add constraint PK_PORTSETTINGS primary key (PORTID);

alter table PORTTERMINALSERVER
   add constraint PK_PORTTERMINALSERVER primary key (PORTID);

alter table PointAlarming
   add constraint PK_POINTALARMING primary key (PointID);

alter table PortStatistics
   add constraint PK_PORTSTATISTICS primary key (PORTID, STATISTICTYPE);

alter table PortTiming
   add constraint PK_PORTTIMING primary key (PORTID);

alter table RAWPOINTHISTORY
   add constraint SYS_C0013322 primary key (CHANGEID);

/*==============================================================*/
/* Index: Index_PointID                                         */
/*==============================================================*/
create index Index_PointID on RAWPOINTHISTORY (
   POINTID ASC
);

/*==============================================================*/
/* Index: Indx_TimeStamp                                        */
/*==============================================================*/
create index Indx_TimeStamp on RAWPOINTHISTORY (
   TIMESTAMP ASC
);

alter table RepeaterRoute
   add constraint PK_REPEATERROUTE primary key (ROUTEID, DEVICEID);

alter table Route
   add constraint SYS_RoutePK primary key (RouteID);

/*==============================================================*/
/* Index: Indx_RouteDevID                                       */
/*==============================================================*/
create unique index Indx_RouteDevID on Route (
   DeviceID DESC,
   RouteID ASC
);

alter table STATE
   add constraint PK_STATE primary key (STATEGROUPID, RAWSTATE);

/*==============================================================*/
/* Index: Indx_StateRaw                                         */
/*==============================================================*/
create index Indx_StateRaw on STATE (
   RAWSTATE ASC
);

alter table STATEGROUP
   add constraint SYS_C0013128 primary key (STATEGROUPID);

/*==============================================================*/
/* Index: Indx_STATEGRP_Nme                                     */
/*==============================================================*/
create unique index Indx_STATEGRP_Nme on STATEGROUP (
   NAME DESC
);

alter table SYSTEMLOG
   add constraint SYS_C0013407 primary key (LOGID);

/*==============================================================*/
/* Index: Indx_SYSLG_PtId                                       */
/*==============================================================*/
create index Indx_SYSLG_PtId on SYSTEMLOG (
   POINTID ASC
);

/*==============================================================*/
/* Index: Indx_SYSLG_Date                                       */
/*==============================================================*/
create index Indx_SYSLG_Date on SYSTEMLOG (
   DATETIME ASC
);

alter table SeasonSchedule
   add constraint PK_SEASONSCHEDULE primary key (ScheduleID);

alter table TEMPLATE
   add constraint SYS_C0013425 primary key (TEMPLATENUM);

alter table TEMPLATECOLUMNS
   add constraint PK_TEMPLATECOLUMNS primary key (TEMPLATENUM, TITLE);

alter table TOUDay
   add constraint PK_TOUDAY primary key (TOUDayID);

alter table TOUDayMapping
   add constraint PK_TOUDAYMAPPING primary key (TOUScheduleID, TOUDayOffset);

alter table TOUDayRateSwitches
   add constraint PK_TOURATESWITCH primary key (TOURateSwitchID);

alter table TOUSchedule
   add constraint PK_TOUSCHEDULE primary key (TOUScheduleID);

alter table TagLog
   add constraint PK_TAGLOG primary key (LogID);

alter table Tags
   add constraint PK_TAGS primary key (TagID);

alter table UNITMEASURE
   add constraint SYS_C0013344 primary key (UOMID);

alter table VersacomRoute
   add constraint PK_VERSACOMROUTE primary key (ROUTEID);

alter table YukonGroup
   add constraint PK_YUKONGROUP primary key (GroupID);

alter table YukonGroupRole
   add constraint PK_YUKONGRPROLE primary key (GroupRoleID);

alter table YukonImage
   add constraint PK_YUKONIMAGE primary key (ImageID);

alter table YukonListEntry
   add constraint PK_YUKONLISTENTRY primary key (EntryID);

/*==============================================================*/
/* Index: Indx_YkLstDefID                                       */
/*==============================================================*/
create index Indx_YkLstDefID on YukonListEntry (
   YukonDefinitionID ASC
);

alter table YukonPAObject
   add constraint PK_YUKONPAOBJECT primary key (PAObjectID);

/*==============================================================*/
/* Index: Indx_PAO                                              */
/*==============================================================*/
create unique index Indx_PAO on YukonPAObject (
   Category ASC,
   PAOName ASC,
   PAOClass ASC,
   Type ASC
);

alter table YukonRole
   add constraint PK_YUKONROLE primary key (RoleID);

/*==============================================================*/
/* Index: Indx_YukRol_Nm                                        */
/*==============================================================*/
create index Indx_YukRol_Nm on YukonRole (
   RoleName ASC
);

alter table YukonRoleProperty
   add constraint PK_YUKONROLEPROPERTY primary key (RolePropertyID);

alter table YukonSelectionList
   add constraint PK_YUKONSELECTIONLIST primary key (ListID);

alter table YukonServices
   add constraint PK_YUKSER primary key (ServiceID);

alter table YukonUser
   add constraint PK_YUKONUSER primary key (UserID);

/*==============================================================*/
/* Index: Indx_YkUsIDNm                                         */
/*==============================================================*/
create unique index Indx_YkUsIDNm on YukonUser (
   UserName ASC
);

alter table YukonUserGroup
   add constraint PK_YUKONUSERGROUP primary key (UserID, GroupID);

alter table YukonUserRole
   add constraint PK_YKONUSRROLE primary key (UserRoleID);

alter table YukonWebConfiguration
   add constraint PK_YUKONWEBCONFIGURATION primary key (ConfigurationID);

alter table AlarmCategory
   add constraint FK_ALRMCAT_NOTIFGRP foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID);

alter table CALCBASE
   add constraint SYS_C0013434 foreign key (POINTID)
      references POINT (POINTID);

alter table CALCCOMPONENT
   add constraint FK_ClcCmp_ClcBs foreign key (PointID)
      references CALCBASE (POINTID);

alter table CALCCOMPONENT
   add constraint FK_ClcCmp_Pt foreign key (COMPONENTPOINTID)
      references POINT (POINTID);

alter table CAPBANK
   add constraint SYS_C0013453 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

alter table CAPBANK
   add constraint SYS_C0013454 foreign key (CONTROLPOINTID)
      references POINT (POINTID);

alter table CAPBANK
   add constraint SYS_C0013455 foreign key (CONTROLDEVICEID)
      references DEVICE (DEVICEID);

alter table CAPCONTROLSUBSTATIONBUS
   add constraint FK_CpSbBus_YPao foreign key (SubstationBusID)
      references YukonPAObject (PAObjectID);

alter table CAPCONTROLSUBSTATIONBUS
   add constraint SYS_C0013478 foreign key (CurrentWattLoadPointID)
      references POINT (POINTID);

alter table CAPCONTROLSUBSTATIONBUS
   add constraint SYS_C0013479 foreign key (CurrentVarLoadPointID)
      references POINT (POINTID);

alter table CCFeederBankList
   add constraint FK_CB_CCFeedLst foreign key (DeviceID)
      references CAPBANK (DEVICEID);

alter table CCFeederBankList
   add constraint FK_CCFeed_CCBnk foreign key (FeederID)
      references CapControlFeeder (FeederID);

alter table CCFeederSubAssignment
   add constraint FK_CCFeed_CCFass foreign key (FeederID)
      references CapControlFeeder (FeederID);

alter table CCFeederSubAssignment
   add constraint FK_CCSub_CCFeed foreign key (SubStationBusID)
      references CAPCONTROLSUBSTATIONBUS (SubstationBusID);

alter table CICustomerBase
   add constraint FK_CICstBas_CstAddrs foreign key (MainAddressID)
      references Address (AddressID);

alter table CICustomerBase
   add constraint FK_CstCI_Cst foreign key (CustomerID)
      references Customer (CustomerID);

alter table CalcPointBaseline
   add constraint FK_CLCBS_BASL foreign key (BaselineID)
      references BaseLine (BaselineID);

alter table CalcPointBaseline
   add constraint FK_ClcPtBs_ClcBs foreign key (PointID)
      references CALCBASE (POINTID);

alter table CapControlFeeder
   add constraint FK_PAObj_CCFeed foreign key (FeederID)
      references YukonPAObject (PAObjectID);

alter table CarrierRoute
   add constraint SYS_C0013264 foreign key (ROUTEID)
      references Route (RouteID);

alter table CommErrorHistory
   add constraint FK_ComErrHis_YPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID);

alter table CommPort
   add constraint FK_COMMPORT_REF_COMPO_YUKONPAO foreign key (PORTID)
      references YukonPAObject (PAObjectID);

alter table Contact
   add constraint FK_RefCstLg_CustCont foreign key (LogInID)
      references YukonUser (UserID);

alter table Contact
   add constraint FK_CONTACT_REF_CNT_A_ADDRESS foreign key (AddressID)
      references Address (AddressID);

alter table ContactNotification
   add constraint FK_CntNot_YkLs foreign key (NotificationCategoryID)
      references YukonListEntry (EntryID);

alter table ContactNotification
   add constraint FK_Cnt_CntNot foreign key (ContactID)
      references Contact (ContactID);

alter table Customer
   add constraint FK_Cst_Cnt foreign key (PrimaryContactID)
      references Contact (ContactID);

alter table CustomerAdditionalContact
   add constraint FK_CstCont_CICstCont foreign key (ContactID)
      references Contact (ContactID);

alter table CustomerAdditionalContact
   add constraint FK_Cust_CustAddCnt foreign key (CustomerID)
      references Customer (CustomerID);

alter table CustomerBaseLinePoint
   add constraint FK_CstBseLn_CICust foreign key (CustomerID)
      references CICustomerBase (CustomerID);

alter table CustomerBaseLinePoint
   add constraint FK_CstBseLn_ClcBse foreign key (PointID)
      references CALCBASE (POINTID);

alter table CustomerLoginSerialGroup
   add constraint FK_CsLgSG_CsL foreign key (LoginID)
      references YukonUser (UserID);

alter table CustomerLoginSerialGroup
   add constraint FK_CsLgSG_LMG foreign key (LMGroupID)
      references LMGroup (DeviceID);

alter table DEVICE
   add constraint FK_Dev_YukPAO foreign key (DEVICEID)
      references YukonPAObject (PAObjectID);

alter table DEVICE2WAYFLAGS
   add constraint SYS_C0013208 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

alter table DEVICECARRIERSETTINGS
   add constraint SYS_C0013216 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

alter table DEVICEDIALUPSETTINGS
   add constraint SYS_C0013193 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

alter table DEVICEIDLCREMOTE
   add constraint SYS_C0013241 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

alter table DEVICEIED
   add constraint SYS_C0013245 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

alter table DEVICELOADPROFILE
   add constraint SYS_C0013234 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

alter table DEVICEMCTIEDPORT
   add constraint SYS_C0013253 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

alter table DEVICEMETERGROUP
   add constraint SYS_C0013213 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

alter table DEVICESCANRATE
   add constraint SYS_C0013198 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

alter table DEVICETAPPAGINGSETTINGS
   add constraint SYS_C0013237 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

alter table DISPLAY2WAYDATA
   add constraint FK_DISPLAY2W_REF_POINT foreign key (POINTID)
      references POINT (POINTID);

alter table DISPLAY2WAYDATA
   add constraint SYS_C0013422 foreign key (DISPLAYNUM)
      references DISPLAY (DISPLAYNUM);

alter table DISPLAYCOLUMNS
   add constraint SYS_C0013418 foreign key (DISPLAYNUM)
      references DISPLAY (DISPLAYNUM);

alter table DISPLAYCOLUMNS
   add constraint SYS_C0013419 foreign key (TYPENUM)
      references COLUMNTYPE (TYPENUM);

alter table DYNAMICACCUMULATOR
   add constraint SYS_C0015129 foreign key (POINTID)
      references POINT (POINTID);

alter table DYNAMICDEVICESCANDATA
   add constraint SYS_C0015139 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

alter table DYNAMICPOINTDISPATCH
   add constraint SYS_C0013331 foreign key (POINTID)
      references POINT (POINTID);

alter table DateOfHoliday
   add constraint FK_HolSchID foreign key (HolidayScheduleID)
      references HolidaySchedule (HolidayScheduleID);

alter table DeviceCBC
   add constraint SYS_C0013459 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

alter table DeviceCBC
   add constraint SYS_C0013460 foreign key (ROUTEID)
      references Route (RouteID);

alter table DeviceCustomerList
   add constraint FK_DvStLsCst foreign key (CustomerID)
      references Customer (CustomerID);

alter table DeviceCustomerList
   add constraint FK_DvStLsDev foreign key (DeviceID)
      references DEVICE (DEVICEID);

alter table DeviceDNP
   add constraint FK_Dev_DevDNP foreign key (DeviceID)
      references DEVICE (DEVICEID);

alter table DeviceDirectCommSettings
   add constraint SYS_C0013186 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

alter table DeviceDirectCommSettings
   add constraint SYS_C0013187 foreign key (PORTID)
      references CommPort (PORTID);

alter table DeviceMCT400Series
   add constraint FK_Dev4_DevC foreign key (DeviceID)
      references DEVICECARRIERSETTINGS (DEVICEID);

alter table DeviceMCT400Series
   add constraint FK_Dev4_TOU foreign key (TOUScheduleID)
      references TOUSchedule (TOUScheduleID);

alter table DeviceRoutes
   add constraint SYS_C0013219 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

alter table DeviceRoutes
   add constraint SYS_C0013220 foreign key (ROUTEID)
      references Route (RouteID);

alter table DeviceWindow
   add constraint FK_DevScWin_Dev foreign key (DeviceID)
      references DEVICE (DEVICEID);

alter table DynamicCCCapBank
   add constraint FK_CpBnk_DynCpBnk foreign key (CapBankID)
      references CAPBANK (DEVICEID);

alter table DynamicCCFeeder
   add constraint FK_CCFeed_DyFeed foreign key (FeederID)
      references CapControlFeeder (FeederID);

alter table DynamicCCSubstationBus
   add constraint FK_CCSubBs_DySubBs foreign key (SubstationBusID)
      references CAPCONTROLSUBSTATIONBUS (SubstationBusID);

alter table DynamicCalcHistorical
   add constraint FK_DynClc_ClcB foreign key (PointID)
      references CALCBASE (POINTID);

alter table DynamicLMControlArea
   add constraint FK_LMCntlAr_DynLMCntAr foreign key (DeviceID)
      references LMControlArea (DEVICEID);

alter table DynamicLMControlAreaTrigger
   add constraint FK_LMCntArTr_DyLMCnArTr foreign key (DeviceID, TriggerNumber)
      references LMCONTROLAREATRIGGER (DEVICEID, TRIGGERNUMBER);

alter table DynamicLMControlHistory
   add constraint FK_DYNLMCNT_PAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID);

alter table DynamicLMGroup
   add constraint FK_LMGrp_DynLmGrp foreign key (DeviceID)
      references LMGroup (DeviceID);

alter table DynamicLMGroup
   add constraint FK_DyLmGr_LmPrDGr foreign key (LMProgramID)
      references LMProgramDirect (DeviceID);

alter table DynamicLMProgram
   add constraint FK_LMProg_DynLMPrg foreign key (DeviceID)
      references LMPROGRAM (DEVICEID);

alter table DynamicLMProgramDirect
   add constraint FK_DYNAMICL_LMPROGDIR_LMPROGRA foreign key (DeviceID)
      references LMProgramDirect (DeviceID);

alter table DynamicPAOStatistics
   add constraint FK_PASt_YkPA foreign key (PAOBjectID)
      references YukonPAObject (PAObjectID);

alter table DynamicPointAlarming
   add constraint FK_DynPtAl_Pt foreign key (PointID)
      references POINT (POINTID);

alter table DynamicPointAlarming
   add constraint FKf_DynPtAl_SysL foreign key (LogID)
      references SYSTEMLOG (LOGID);

alter table DynamicTags
   add constraint FK_DynTgs_Pt foreign key (PointID)
      references POINT (POINTID);

alter table DynamicTags
   add constraint FK_DYNAMICT_REF_DYNTG_TAGS foreign key (TagID)
      references Tags (TagID);

alter table EnergyCompany
   add constraint FK_EnCm_Cnt foreign key (PrimaryContactID)
      references Contact (ContactID);

alter table EnergyCompany
   add constraint FK_EngCmp_YkUs foreign key (UserID)
      references YukonUser (UserID);

alter table EnergyCompanyCustomerList
   add constraint FK_CICstBsEnCmpCsLs foreign key (CustomerID)
      references CICustomerBase (CustomerID);

alter table EnergyCompanyCustomerList
   add constraint FK_EnCmpEnCmpCsLs foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID);

alter table EnergyCompanyOperatorLoginList
   add constraint FK_EnCmpEnCmpOpLs foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID);

alter table EnergyCompanyOperatorLoginList
   add constraint FK_OpLgEnCmpOpLs foreign key (OperatorLoginID)
      references YukonUser (UserID);

alter table FDRInterfaceOption
   add constraint FK_FDRINTER_REFERENCE_FDRINTER foreign key (InterfaceID)
      references FDRInterface (InterfaceID);

alter table FDRTRANSLATION
   add constraint SYS_C0015066 foreign key (POINTID)
      references POINT (POINTID);

alter table GRAPHDATASERIES
   add constraint GrphDSeri_GrphDefID foreign key (GRAPHDEFINITIONID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID);

alter table GRAPHDATASERIES
   add constraint GrphDSeris_ptID foreign key (POINTID)
      references POINT (POINTID);

alter table GraphCustomerList
   add constraint FK_GRAPHCUS_REFGRPHCU_GRAPHDEF foreign key (GraphDefinitionID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID);

alter table GraphCustomerList
   add constraint FK_GrphCstLst_Cst foreign key (CustomerID)
      references Customer (CustomerID);

alter table LMCONTROLAREAPROGRAM
   add constraint FK_LMCntlArea_LMCntlArProg foreign key (DEVICEID)
      references LMControlArea (DEVICEID);

alter table LMCONTROLAREAPROGRAM
   add constraint FK_LMPrg_LMCntlArProg foreign key (LMPROGRAMDEVICEID)
      references LMPROGRAM (DEVICEID);

alter table LMCONTROLAREATRIGGER
   add constraint FK_LMCntlArea_LMCntlArTrig foreign key (DEVICEID)
      references LMControlArea (DEVICEID);

alter table LMCONTROLAREATRIGGER
   add constraint FK_Point_LMCntlArTrig foreign key (POINTID)
      references POINT (POINTID);

alter table LMCONTROLAREATRIGGER
   add constraint FK_Point_LMCtrlArTrigPk foreign key (PEAKPOINTID)
      references POINT (POINTID);

alter table LMControlArea
   add constraint FK_LmCntAr_YukPAO foreign key (DEVICEID)
      references YukonPAObject (PAObjectID);

alter table LMControlHistory
   add constraint FK_LmCtrlHis_YPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID);

alter table LMCurtailCustomerActivity
   add constraint FK_CICBas_LMCrtCstAct foreign key (CustomerID)
      references CICustomerBase (CustomerID);

alter table LMCurtailCustomerActivity
   add constraint FK_LMCURTAI_REFLMCST__LMCURTAI foreign key (CurtailReferenceID)
      references LMCurtailProgramActivity (CurtailReferenceID);

alter table LMCurtailProgramActivity
   add constraint FK_LMPrgCrt_LMCrlPAct foreign key (DeviceID)
      references LMProgramCurtailment (DeviceID);

alter table LMDirectCustomerList
   add constraint FK_CICstB_LMPrDi foreign key (CustomerID)
      references CICustomerBase (CustomerID);

alter table LMDirectCustomerList
   add constraint FK_LMDIRECT_REFLMPDIR_LMPROGRA foreign key (ProgramID)
      references LMProgramDirect (DeviceID);

alter table LMDirectOperatorList
   add constraint FK_LMDirOpLs_LMPrD foreign key (ProgramID)
      references LMProgramDirect (DeviceID);

alter table LMDirectOperatorList
   add constraint FK_OpLg_LMDOpLs foreign key (OperatorLoginID)
      references YukonUser (UserID);

alter table LMEnergyExchangeCustomerList
   add constraint FK_ExCsLs_CstBs foreign key (CustomerID)
      references CICustomerBase (CustomerID);

alter table LMEnergyExchangeCustomerList
   add constraint FK_ExCsLs_PrEx foreign key (ProgramID)
      references LMProgramEnergyExchange (DeviceID);

alter table LMEnergyExchangeCustomerReply
   add constraint FK_ExCsRp_CstBs foreign key (CustomerID)
      references CICustomerBase (CustomerID);

alter table LMEnergyExchangeCustomerReply
   add constraint FK_LMENERGY_REFEXCSTR_LMENERGY foreign key (OfferID, RevisionNumber)
      references LMEnergyExchangeOfferRevision (OfferID, RevisionNumber)
      on delete cascade;

alter table LMEnergyExchangeHourlyCustomer
   add constraint FK_ExHrCs_ExCsRp foreign key (CustomerID, OfferID, RevisionNumber)
      references LMEnergyExchangeCustomerReply (CustomerID, OfferID, RevisionNumber)
      on delete cascade;

alter table LMEnergyExchangeHourlyOffer
   add constraint FK_ExHrOff_ExOffRv foreign key (OfferID, RevisionNumber)
      references LMEnergyExchangeOfferRevision (OfferID, RevisionNumber);

alter table LMEnergyExchangeOfferRevision
   add constraint FK_EExOffR_ExPrOff foreign key (OfferID)
      references LMEnergyExchangeProgramOffer (OfferID);

alter table LMEnergyExchangeProgramOffer
   add constraint FK_EnExOff_PrgEnEx foreign key (DeviceID)
      references LMProgramEnergyExchange (DeviceID);

alter table LMGroup
   add constraint FK_Device_LMGrpBase2 foreign key (DeviceID)
      references DEVICE (DEVICEID);

alter table LMGroupEmetcon
   add constraint SYS_C0013356 foreign key (DEVICEID)
      references LMGroup (DeviceID);

alter table LMGroupEmetcon
   add constraint SYS_C0013357 foreign key (ROUTEID)
      references Route (RouteID);

alter table LMGroupExpressCom
   add constraint FK_ExCG_LMExCm foreign key (GeoID)
      references LMGroupExpressComAddress (AddressID);

alter table LMGroupExpressCom
   add constraint FK_ExCP_LMExCm foreign key (ProgramID)
      references LMGroupExpressComAddress (AddressID);

alter table LMGroupExpressCom
   add constraint FK_ExCSb_LMExCm foreign key (SubstationID)
      references LMGroupExpressComAddress (AddressID);

alter table LMGroupExpressCom
   add constraint FK_ExCSp_LMExCm foreign key (ServiceProviderID)
      references LMGroupExpressComAddress (AddressID);

alter table LMGroupExpressCom
   add constraint FK_ExCad_LMExCm foreign key (FeederID)
      references LMGroupExpressComAddress (AddressID);

alter table LMGroupExpressCom
   add constraint FK_LGrEx_LMG foreign key (LMGroupID)
      references LMGroup (DeviceID);

alter table LMGroupExpressCom
   add constraint FK_LGrEx_Rt foreign key (RouteID)
      references Route (RouteID);

alter table LMGroupMCT
   add constraint FK_LMGrMC_Grp foreign key (DeviceID)
      references LMGroup (DeviceID);

alter table LMGroupMCT
   add constraint FK_LMGrMC_Rt foreign key (RouteID)
      references Route (RouteID);

alter table LMGroupMCT
   add constraint FK_LMGrMC_YkP foreign key (MCTDeviceID)
      references YukonPAObject (PAObjectID);

alter table LMGroupPoint
   add constraint FK_LMGrpPt_Dev foreign key (DeviceIDUsage)
      references DEVICE (DEVICEID);

alter table LMGroupPoint
   add constraint FK_LMGrpPt_LMGrp foreign key (DEVICEID)
      references LMGroup (DeviceID);

alter table LMGroupPoint
   add constraint FK_LMGrpPt_Pt foreign key (PointIDUsage)
      references POINT (POINTID);

alter table LMGroupRipple
   add constraint FK_LmGr_LmGrpRip foreign key (DeviceID)
      references LMGroup (DeviceID);

alter table LMGroupRipple
   add constraint FK_LmGrpRip_Rout foreign key (RouteID)
      references Route (RouteID);

alter table LMGroupVersacom
   add constraint FK_LMGrp_LMGrpVers foreign key (DEVICEID)
      references LMGroup (DeviceID);

alter table LMGroupVersacom
   add constraint SYS_C0013367 foreign key (ROUTEID)
      references Route (RouteID);

alter table LMMACSScheduleOperatorList
   add constraint FK_MCSchLMMcSchOpLs foreign key (ScheduleID)
      references MACSchedule (ScheduleID);

alter table LMMACSScheduleOperatorList
   add constraint FK_OpLgLMMcSchOpLs foreign key (OperatorLoginID)
      references YukonUser (UserID);

alter table LMMacsScheduleCustomerList
   add constraint FK_McSchCstLst_MCSched foreign key (ScheduleID)
      references MACSchedule (ScheduleID);

alter table LMMacsScheduleCustomerList
   add constraint FK_McsSchdCusLst_CICBs foreign key (LMCustomerDeviceID)
      references CICustomerBase (CustomerID);

alter table LMPROGRAM
   add constraint FK_HlSc_LmPr foreign key (HolidayScheduleID)
      references HolidaySchedule (HolidayScheduleID);

alter table LMPROGRAM
   add constraint FK_LmProg_YukPAO foreign key (DEVICEID)
      references YukonPAObject (PAObjectID);

alter table LMPROGRAM
   add constraint FK_SesSch_LmPr foreign key (SeasonScheduleID)
      references SeasonSchedule (ScheduleID);

alter table LMProgramControlWindow
   add constraint FK_LMPrg_LMPrgCntWind foreign key (DeviceID)
      references LMPROGRAM (DEVICEID);

alter table LMProgramCurtailCustomerList
   add constraint FK_CICstBase_LMProgCList foreign key (CustomerID)
      references CICustomerBase (CustomerID);

alter table LMProgramCurtailCustomerList
   add constraint FK_LMPrgCrt_LMPrCstLst foreign key (ProgramID)
      references LMProgramCurtailment (DeviceID)
      on delete cascade;

alter table LMProgramCurtailment
   add constraint FK_LMPrg_LMPrgCurt foreign key (DeviceID)
      references LMPROGRAM (DEVICEID);

alter table LMProgramDirect
   add constraint FK_LMPrg_LMPrgDirect foreign key (DeviceID)
      references LMPROGRAM (DEVICEID);

alter table LMProgramDirectGear
   add constraint FK_LMProgD_LMProgDGr foreign key (DeviceID)
      references LMProgramDirect (DeviceID);

alter table LMProgramDirectGroup
   add constraint FK_LMGrp_LMPrgDGrp foreign key (LMGroupDeviceID)
      references LMGroup (DeviceID);

alter table LMProgramDirectGroup
   add constraint FK_LMPrgD_LMPrgDGrp foreign key (DeviceID)
      references LMProgramDirect (DeviceID);

alter table LMProgramEnergyExchange
   add constraint FK_LmPrg_LmPrEEx foreign key (DeviceID)
      references LMPROGRAM (DEVICEID);

alter table LMThermoStatGear
   add constraint FK_ThrmStG_PrDiGe foreign key (GearID)
      references LMProgramDirectGear (GearID);

alter table MACROROUTE
   add constraint SYS_C0013274 foreign key (ROUTEID)
      references Route (RouteID);

alter table MACROROUTE
   add constraint SYS_C0013275 foreign key (SINGLEROUTEID)
      references Route (RouteID);

alter table MACSchedule
   add constraint FK_SchdID_PAOID foreign key (ScheduleID)
      references YukonPAObject (PAObjectID);

alter table MACSimpleSchedule
   add constraint FK_MACSIMPLE_MACSCHED_ID foreign key (ScheduleID)
      references MACSchedule (ScheduleID);

alter table MCTBroadCastMapping
   add constraint FK_MCTB_MAPDEV foreign key (MCTBroadCastID)
      references DEVICE (DEVICEID);

alter table MCTBroadCastMapping
   add constraint FK_MCTB_MAPMCT foreign key (MctID)
      references DEVICE (DEVICEID);

alter table MCTConfigMapping
   add constraint FK_McCfgM_Dev foreign key (MctID)
      references DEVICE (DEVICEID);

alter table MCTConfigMapping
   add constraint FK_McCfgM_McCfg foreign key (ConfigID)
      references MCTConfig (ConfigID);

alter table NotificationDestination
   add constraint FK_NotifDest_NotifGrp foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID);

alter table NotificationDestination
   add constraint FK_CntNt_NtDst foreign key (RecipientID)
      references ContactNotification (ContactNotifID);

alter table OperatorLoginGraphList
   add constraint FK_OpLgOpLgGrLs foreign key (GraphDefinitionID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID);

alter table OperatorLoginGraphList
   add constraint FK_OpLgOpLgGrLs2 foreign key (OperatorLoginID)
      references YukonUser (UserID);

alter table OperatorSerialGroup
   add constraint FK_OpSGrp_LmGrp foreign key (LMGroupID)
      references LMGroup (DeviceID);

alter table OperatorSerialGroup
   add constraint FK_OpSGrp_OpLg foreign key (LoginID)
      references YukonUser (UserID);

alter table PAOExclusion
   add constraint FK_PAOEx_Pt foreign key (PointID)
      references POINT (POINTID);

alter table PAOExclusion
   add constraint FK_PAOEx_YkPAO foreign key (PaoID)
      references YukonPAObject (PAObjectID);

alter table PAOExclusion
   add constraint FK_PAOEXCLU_REF_PAOEX_YUKONPAO foreign key (ExcludedPaoID)
      references YukonPAObject (PAObjectID);

alter table PAOowner
   add constraint FK_YukPAO_PAOOwn foreign key (ChildID)
      references YukonPAObject (PAObjectID);

alter table PAOowner
   add constraint FK_YukPAO_PAOid foreign key (OwnerID)
      references YukonPAObject (PAObjectID);

alter table POINT
   add constraint FK_Pt_YukPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID);

alter table POINT
   add constraint Ref_STATGRP_PT foreign key (STATEGROUPID)
      references STATEGROUP (STATEGROUPID);

alter table POINTACCUMULATOR
   add constraint SYS_C0013317 foreign key (POINTID)
      references POINT (POINTID);

alter table POINTANALOG
   add constraint SYS_C0013300 foreign key (POINTID)
      references POINT (POINTID);

alter table POINTLIMITS
   add constraint SYS_C0013289 foreign key (POINTID)
      references POINT (POINTID);

alter table POINTSTATUS
   add constraint Ref_ptstatus_pt foreign key (POINTID)
      references POINT (POINTID);

alter table POINTUNIT
   add constraint FK_PtUnit_UoM foreign key (UOMID)
      references UNITMEASURE (UOMID);

alter table POINTUNIT
   add constraint Ref_ptunit_point foreign key (POINTID)
      references POINT (POINTID);

alter table PORTDIALUPMODEM
   add constraint SYS_C0013175 foreign key (PORTID)
      references CommPort (PORTID);

alter table PORTLOCALSERIAL
   add constraint SYS_C0013147 foreign key (PORTID)
      references CommPort (PORTID);

alter table PORTRADIOSETTINGS
   add constraint SYS_C0013169 foreign key (PORTID)
      references CommPort (PORTID);

alter table PORTSETTINGS
   add constraint SYS_C0013156 foreign key (PORTID)
      references CommPort (PORTID);

alter table PORTTERMINALSERVER
   add constraint SYS_C0013151 foreign key (PORTID)
      references CommPort (PORTID);

alter table PointAlarming
   add constraint FK_POINTALAARM_POINT_POINTID foreign key (PointID)
      references POINT (POINTID);

alter table PointAlarming
   add constraint FK_POINTALARMING foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID);

alter table PointAlarming
   add constraint FK_CntNt_PtAl foreign key (RecipientID)
      references ContactNotification (ContactNotifID);

alter table PortStatistics
   add constraint SYS_C0013183 foreign key (PORTID)
      references CommPort (PORTID);

alter table PortTiming
   add constraint SYS_C0013163 foreign key (PORTID)
      references CommPort (PORTID);

alter table RAWPOINTHISTORY
   add constraint FK_RawPt_Point foreign key (POINTID)
      references POINT (POINTID);

alter table RepeaterRoute
   add constraint SYS_C0013269 foreign key (ROUTEID)
      references Route (RouteID);

alter table RepeaterRoute
   add constraint SYS_C0013270 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

alter table Route
   add constraint FK_Route_DevID foreign key (DeviceID)
      references DEVICE (DEVICEID);

alter table Route
   add constraint FK_Route_YukPAO foreign key (RouteID)
      references YukonPAObject (PAObjectID);

alter table STATE
   add constraint FK_YkIm_St foreign key (ImageID)
      references YukonImage (ImageID);

alter table STATE
   add constraint SYS_C0013342 foreign key (STATEGROUPID)
      references STATEGROUP (STATEGROUPID);

alter table SYSTEMLOG
   add constraint SYS_C0013408 foreign key (POINTID)
      references POINT (POINTID);

alter table TEMPLATECOLUMNS
   add constraint SYS_C0013429 foreign key (TEMPLATENUM)
      references TEMPLATE (TEMPLATENUM);

alter table TEMPLATECOLUMNS
   add constraint SYS_C0013430 foreign key (TYPENUM)
      references COLUMNTYPE (TYPENUM);

alter table TOUDayMapping
   add constraint FK_TOUd_TOUSc foreign key (TOUScheduleID)
      references TOUSchedule (TOUScheduleID);

alter table TOUDayMapping
   add constraint FK_TOUm_TOUd foreign key (TOUDayID)
      references TOUDay (TOUDayID);

alter table TOUDayRateSwitches
   add constraint FK_TOUdRS_TOUd foreign key (TOUDayID)
      references TOUDay (TOUDayID);

alter table TagLog
   add constraint FK_TagLg_Pt foreign key (PointID)
      references POINT (POINTID);

alter table TagLog
   add constraint FK_TagLg_Tgs foreign key (TagID)
      references Tags (TagID);

alter table VersacomRoute
   add constraint FK_VERSACOM_ROUTE_VER_ROUTE foreign key (ROUTEID)
      references Route (RouteID);

alter table YukonGroupRole
   add constraint FK_YkGrRl_YkGrp foreign key (GroupID)
      references YukonGroup (GroupID);

alter table YukonGroupRole
   add constraint FK_YkGrRl_YkRle foreign key (RoleID)
      references YukonRole (RoleID);

alter table YukonGroupRole
   add constraint FK_YkGrpR_YkRlPr foreign key (RolePropertyID)
      references YukonRoleProperty (RolePropertyID);

alter table YukonListEntry
   add constraint FK_LstEnty_SelLst foreign key (ListID)
      references YukonSelectionList (ListID);

alter table YukonRoleProperty
   add constraint FK_YkRlPrp_YkRle foreign key (RoleID)
      references YukonRole (RoleID);

alter table YukonUserGroup
   add constraint FK_YkUsGr_YkGr foreign key (GroupID)
      references YukonGroup (GroupID);

alter table YukonUserGroup
   add constraint FK_YUKONUSE_REF_YKUSG_YUKONUSE foreign key (UserID)
      references YukonUser (UserID);

alter table YukonUserRole
   add constraint FK_YkUsRl_RlPrp foreign key (RolePropertyID)
      references YukonRoleProperty (RolePropertyID);
alter table YukonUserRole
   add constraint FK_YkUsRl_YkRol foreign key (RoleID)
      references YukonRole (RoleID);
alter table YukonUserRole
   add constraint FK_YkUsRlr_YkUsr foreign key (UserID)
      references YukonUser (UserID);












drop table TOUDeviceMapping;
drop table TOUDayMapping;
drop table TOURateOffset;
drop table TOUDay;
drop table TOUSchedule;


create table DynamicLMControlHistory  (
   PAObjectID           NUMBER                           not null,
   LMCtrlHistID         NUMBER                           not null,
   StartDateTime        DATE                             not null,
   SOE_Tag              NUMBER                           not null,
   ControlDuration      NUMBER                           not null,
   ControlType          VARCHAR2(32)                     not null,
   CurrentDailyTime     NUMBER                           not null,
   CurrentMonthlyTime   NUMBER                           not null,
   CurrentSeasonalTime  NUMBER                           not null,
   CurrentAnnualTime    NUMBER                           not null,
   ActiveRestore        CHAR(1)                          not null,
   ReductionValue       FLOAT                            not null,
   StopDateTime         DATE                             not null
);
alter table DynamicLMControlHistory
   add constraint PK_DYNLMCONTROLHISTORY primary key (PAObjectID);
alter table DynamicLMControlHistory
   add constraint FK_DYNLMCNT_PAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID);


create table TOUSchedule  (
   TOUScheduleID        NUMBER                           not null,
   TOUScheduleName      VARCHAR2(32)                     not null,
   TOUDefaultRate       VARCHAR2(4)                      not null
);
alter table TOUSchedule
   add constraint PK_TOUSCHEDULE primary key (TOUScheduleID);

create table TOUDay  (
   TOUDayID             NUMBER                           not null,
   TOUDayName           VARCHAR2(32)                     not null
);
alter table TOUDay
   add constraint PK_TOUDAY primary key (TOUDayID);

create table TOUDayRateSwitches (
TOURateSwitchID	     NUMBER                           not null,
SwitchRate           varchar2(4)                      not null,
SwitchOffset         NUMBER                           not null,
TOUDayID             NUMBER                           not null
);
alter table TOUDayRateSwitches
   add constraint PK_TOURATESWITCH primary key  (TOURateSwitchID);
alter table TOUDayRateSwitches
   add constraint FK_TOUdRS_TOUd foreign key (TOUDayID)
      references TOUDay (TOUDayID);
create unique index Indx_TOUSWITCHRATE_PK on TOUDayRateSwitches (
TOUDayID, SwitchOffset
);

create table TOUDayMapping  (
   TOUScheduleID        NUMBER                           not null,
   TOUDayID             NUMBER                           not null,
   TOUDayOffset         NUMBER                           not null
);
alter table TOUDayMapping
   add constraint PK_TOUDAYMAPPING primary key (TOUScheduleID, TOUDayID);
alter table TOUDayMapping
   add constraint FK_TOUd_TOUSc foreign key (TOUScheduleID)
      references TOUSchedule (TOUScheduleID);
alter table TOUDayMapping
   add constraint FK_TOUm_TOUd foreign key (TOUDayID)
      references TOUDay (TOUDayID);

alter table MCTConfig add DisplayDigits number;
update MCTConfig set DisplayDigits = 0;
alter table MCTConfig modify DisplayDigits not null;

create table DeviceMCT400Series  (
   DeviceID             NUMBER                           not null,
   DisconnectAddress    NUMBER                           not null,
   TOUScheduleID        NUMBER                           not null
);
alter table DeviceMCT400Series
   add constraint PK_DEV400S primary key (DeviceID);
alter table DeviceMCT400Series
   add constraint FK_Dev4_Dev foreign key (DeviceID)
      references DEVICECARRIERSETTINGS (DEVICEID);
alter table DeviceMCT400Series
   add constraint FK_Dev4_TOU foreign key (TOUScheduleID)
      references TOUSchedule (TOUScheduleID);

update yukonpaobject set type = 'MCT-410IL' where type like '%410%';

alter table ctidatabase drop constraint PK_CTIDATABASE;
alter table ctidatabase add constraint PK_CTIDATABASE primary key  (Version, Build);

insert into TOUSchedule values (0, '(none)', 0);

insert into YukonListEntry values (1001,1001,0,'Program',1001);
insert into YukonListEntry values (1002,1001,0,'Hardware',1002);
insert into YukonListEntry values (1003,1001,0,'ThermostatManual',1003);
insert into YukonListEntry values (1011,1002,0,'Signup',1101);
insert into YukonListEntry values (1012,1002,0,'Activation Pending',1102);
insert into YukonListEntry values (1013,1002,0,'Activation Completed',1103);
insert into YukonListEntry values (1014,1002,0,'Termination',1104);
insert into YukonListEntry values (1015,1002,0,'Temp Opt Out',1105);
insert into YukonListEntry values (1016,1002,0,'Future Activation',1106);
insert into YukonListEntry values (1017,1002,0,'Install',1107);
insert into YukonListEntry values (1018,1002,0,'Configure',1108);
insert into YukonListEntry values (1019,1002,0,'Programming',1109);
insert into YukonListEntry values (1020,1002,0,'Manual Option',1110);
insert into YukonListEntry values (1021,1002,0,'Uninstall',1111);
insert into YukonListEntry values (1031,1003,0,'OneWayReceiver',1201);
insert into YukonListEntry values (1032,1003,0,'TwoWayReceiver',1202);
insert into YukonListEntry values (1033,1003,0,'MCT',1203);
insert into YukonListEntry values (1041,1004,0,' ',0);
insert into YukonListEntry values (1042,1004,0,'120/120',0);
insert into YukonListEntry values (1051,1005,0,'LCR-5000',1302);
insert into YukonListEntry values (1052,1005,0,'LCR-4000',1302);
insert into YukonListEntry values (1053,1005,0,'LCR-3000',1302);
insert into YukonListEntry values (1054,1005,0,'LCR-2000',1302);
insert into YukonListEntry values (1055,1005,0,'LCR-1000',1302);
insert into YukonListEntry values (1056,1005,-1,'ExpressStat',1301);
insert into YukonListEntry values (1057,1005,-1,'EnergyPro',3100);
insert into YukonListEntry values (1058,1005,-1,'MCT',1303);

insert into YukonSelectionList values (1001,'A','(none)','Not visible, list defines the event ids','LMCustomerEvent','N');
insert into YukonSelectionList values (1002,'A','(none)','Not visible, defines possible event actions','LMCustomerAction','N');
insert into YukonSelectionList values (1003,'A','(none)','Not visible, defines inventory device category','InventoryCategory','N');
insert into YukonSelectionList values (1004,'A','(none)','Device voltage selection','DeviceVoltage','Y');
insert into YukonSelectionList values (1005,'A','(none)','Device type selection','DeviceType','Y');
insert into YukonSelectionList values (1006,'N','(none)','Hardware status selection','DeviceStatus','N');
insert into YukonSelectionList values (1007,'A','(none)','Appliance category','ApplianceCategory','N');
insert into YukonSelectionList values (1008,'A','(none)','Call type selection','CallType','Y');
insert into YukonSelectionList values (1009,'A','(none)','Service type selection','ServiceType','Y');
insert into YukonSelectionList values (1010,'N','(none)','Service request status','ServiceStatus','N');
insert into YukonSelectionList values (1011,'N','(none)','Search by selection','SearchBy','N');
insert into YukonSelectionList values (1012,'A','(none)','Appliance manufacturer selection','Manufacturer','Y');
insert into YukonSelectionList values (1013,'A','(none)','Appliance location selection','ApplianceLocation','Y');
insert into YukonSelectionList values (1014,'N','(none)','Chance of control selection','ChanceOfControl','Y');
insert into YukonSelectionList values (1015,'N','(none)','Thermostat settings time of week selection','TimeOfWeek','N');
insert into YukonSelectionList values (1016,'N','(none)','Question type selection','QuestionType','N');
insert into YukonSelectionList values (1017,'N','(none)','Answer type selection','AnswerType','N');
insert into YukonSelectionList values (1018,'N','(none)','Thermostat mode selection','ThermostatMode','N');
insert into YukonSelectionList values (1019,'N','(none)','Thermostat fan state selection','ThermostatFanState','N');





/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('3.00', 'Ryan', '22-APR-2004', 'Many changes to a major version jump', 27);
