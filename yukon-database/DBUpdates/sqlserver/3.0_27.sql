/********************************************/
/* SQLServer 2000 DBupdates                 */
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
   add constraint PK_ACTIVITYLOG primary key  (ActivityLogID);
go


alter table Address
   add constraint PK_ADDRESS primary key  (AddressID);
go


alter table AlarmCategory
   add constraint PK_ALARMCATEGORYID primary key  (AlarmCategoryID);
go


alter table BaseLine
   add constraint PK_BASELINE primary key  (BaselineID);
go


alter table BillingFileFormats
   add constraint PK_BILLINGFILEFORMATS primary key  (FormatID);
go


alter table CALCBASE
   add constraint PK_CALCBASE primary key  (POINTID);
go


/*==============================================================*/
/* Index: Indx_ClcBaseUpdTyp                                    */
/*==============================================================*/
create   index Indx_ClcBaseUpdTyp on CALCBASE (
UPDATETYPE
);
go


alter table CALCCOMPONENT
   add constraint PK_CALCCOMPONENT primary key  (PointID, COMPONENTORDER);
go


/*==============================================================*/
/* Index: Indx_CalcCmpCmpType                                   */
/*==============================================================*/
create   index Indx_CalcCmpCmpType on CALCCOMPONENT (
COMPONENTTYPE
);
go


alter table CAPBANK
   add constraint PK_CAPBANK primary key  (DEVICEID);
go


alter table CAPCONTROLSUBSTATIONBUS
   add constraint SYS_C0013476 primary key  (SubstationBusID);
go


/*==============================================================*/
/* Index: Indx_CSUBVPT                                          */
/*==============================================================*/
create   index Indx_CSUBVPT on CAPCONTROLSUBSTATIONBUS (
CurrentVarLoadPointID
);
go


alter table CCFeederBankList
   add constraint PK_CCFEEDERBANKLIST primary key  (FeederID, DeviceID);
go


alter table CCFeederSubAssignment
   add constraint PK_CCFEEDERSUBASSIGNMENT primary key  (SubStationBusID, FeederID);
go


alter table CICustomerBase
   add constraint PK_CICUSTOMERBASE primary key  (CustomerID);
go


alter table COLUMNTYPE
   add constraint SYS_C0013414 primary key  (TYPENUM);
go


alter table CTIDatabase
   add constraint PK_CTIDATABASE primary key  (Version, Build);
go


alter table CalcPointBaseline
   add constraint PK_CalcBsPt primary key  (PointID);
go


alter table CapControlFeeder
   add constraint PK_CAPCONTROLFEEDER primary key  (FeederID);
go


/*==============================================================*/
/* Index: Indx_CPCNFDVARPT                                      */
/*==============================================================*/
create   index Indx_CPCNFDVARPT on CapControlFeeder (
CurrentVarLoadPointID
);
go


alter table CarrierRoute
   add constraint PK_CARRIERROUTE primary key  (ROUTEID);
go


alter table CommErrorHistory
   add constraint PK_COMMERRORHISTORY primary key  (CommErrorID);
go


alter table CommPort
   add constraint SYS_C0013112 primary key  (PORTID);
go


alter table Contact
   add constraint PK_CONTACT primary key  (ContactID);
go


alter table ContactNotification
   add constraint PK_CONTACTNOTIFICATION primary key  (ContactNotifID);
go


alter table Customer
   add constraint PK_CUSTOMER primary key  (CustomerID);
go


alter table CustomerAdditionalContact
   add constraint PK_CUSTOMERADDITIONALCONTACT primary key  (ContactID, CustomerID);
go


alter table CustomerBaseLinePoint
   add constraint PK_CUSTOMERBASELINEPOINT primary key  (CustomerID, PointID);
go


alter table CustomerLoginSerialGroup
   add constraint PK_CUSTOMERLOGINSERIALGROUP primary key  (LoginID, LMGroupID);
go


alter table DEVICE
   add constraint PK_DEV_DEVICEID2 primary key  (DEVICEID);
go


alter table DEVICE2WAYFLAGS
   add constraint PK_DEVICE2WAYFLAGS primary key  (DEVICEID);
go


alter table DEVICECARRIERSETTINGS
   add constraint PK_DEVICECARRIERSETTINGS primary key  (DEVICEID);
go


alter table DEVICEDIALUPSETTINGS
   add constraint PK_DEVICEDIALUPSETTINGS primary key  (DEVICEID);
go


alter table DEVICEIDLCREMOTE
   add constraint PK_DEVICEIDLCREMOTE primary key  (DEVICEID);
go


alter table DEVICEIED
   add constraint PK_DEVICEIED primary key  (DEVICEID);
go


alter table DEVICELOADPROFILE
   add constraint PK_DEVICELOADPROFILE primary key  (DEVICEID);
go


alter table DEVICEMCTIEDPORT
   add constraint PK_DEVICEMCTIEDPORT primary key  (DEVICEID);
go


alter table DEVICEMETERGROUP
   add constraint PK_DEVICEMETERGROUP primary key  (DEVICEID);
go


alter table DEVICESCANRATE
   add constraint PK_DEVICESCANRATE primary key  (DEVICEID, SCANTYPE);
go


alter table DEVICETAPPAGINGSETTINGS
   add constraint PK_DEVICETAPPAGINGSETTINGS primary key  (DEVICEID);
go


alter table DISPLAY
   add constraint SYS_C0013412 primary key  (DISPLAYNUM);
go


/*==============================================================*/
/* Index: Indx_DISPLAYNAME                                      */
/*==============================================================*/
create unique  index Indx_DISPLAYNAME on DISPLAY (
NAME
);
go


alter table DISPLAY2WAYDATA
   add constraint PK_DISPLAY2WAYDATA primary key  (DISPLAYNUM, ORDERING);
go


alter table DISPLAYCOLUMNS
   add constraint PK_DISPLAYCOLUMNS primary key  (DISPLAYNUM, TITLE);
go


alter table DYNAMICACCUMULATOR
   add constraint PK_DYNAMICACCUMULATOR primary key  (POINTID);
go


alter table DYNAMICDEVICESCANDATA
   add constraint PK_DYNAMICDEVICESCANDATA primary key  (DEVICEID);
go


alter table DYNAMICPOINTDISPATCH
   add constraint PK_DYNAMICPOINTDISPATCH primary key  (POINTID);
go


alter table DateOfHoliday
   add constraint PK_DATEOFHOLIDAY primary key  (HolidayScheduleID, HolidayName);
go


alter table DeviceCBC
   add constraint PK_DEVICECBC primary key  (DEVICEID);
go


alter table DeviceCustomerList
   add constraint PK_DEVICECUSTOMERLIST primary key  (DeviceID, CustomerID);
go


alter table DeviceDNP
   add constraint PK_DEVICEDNP primary key  (DeviceID);
go


alter table DeviceDirectCommSettings
   add constraint PK_DEVICEDIRECTCOMMSETTINGS primary key  (DEVICEID);
go


alter table DeviceMCT400Series
   add constraint PK_DEV400S primary key  (DeviceID);
go


alter table DeviceRoutes
   add constraint PK_DEVICEROUTES primary key  (DEVICEID, ROUTEID);
go


alter table DeviceWindow
   add constraint PK_DEVICEWINDOW primary key  (DeviceID, Type);
go


alter table DynamicCCCapBank
   add constraint PK_DYNAMICCCCAPBANK primary key  (CapBankID);
go


alter table DynamicCCFeeder
   add constraint PK_DYNAMICCCFEEDER primary key  (FeederID);
go


alter table DynamicCCSubstationBus
   add constraint PK_DYNAMICCCSUBSTATIONBUS primary key  (SubstationBusID);
go


alter table DynamicCalcHistorical
   add constraint PK_DYNAMICCALCHISTORICAL primary key  (PointID);
go


alter table DynamicLMControlArea
   add constraint PK_DYNAMICLMCONTROLAREA primary key  (DeviceID);
go


alter table DynamicLMControlAreaTrigger
   add constraint PK_DYNAMICLMCONTROLAREATRIGGER primary key  (DeviceID, TriggerNumber);
go


alter table DynamicLMControlHistory
   add constraint PK_DYNLMCONTROLHISTORY primary key  (PAObjectID);
go


alter table DynamicLMGroup
   add constraint PK_DYNAMICLMGROUP primary key  (DeviceID, LMProgramID);
go


alter table DynamicLMProgram
   add constraint PK_DYNAMICLMPROGRAM primary key  (DeviceID);
go


alter table DynamicLMProgramDirect
   add constraint PK_DYNAMICLMPROGRAMDIRECT primary key  (DeviceID);
go


alter table DynamicPAOStatistics
   add constraint PK_DYNAMICPAOSTATISTICS primary key  (PAOBjectID, StatisticType);
go


alter table DynamicPointAlarming
   add constraint PK_DYNAMICPOINTALARMING primary key  (PointID, AlarmCondition);
go


alter table DynamicTags
   add constraint PK_DYNAMICTAGS primary key  (InstanceID);
go


alter table EnergyCompany
   add constraint PK_ENERGYCOMPANY primary key  (EnergyCompanyID);
go


/*==============================================================*/
/* Index: Indx_EnCmpName                                        */
/*==============================================================*/
create unique  index Indx_EnCmpName on EnergyCompany (
Name
);
go


alter table EnergyCompanyCustomerList
   add constraint PK_ENERGYCOMPANYCUSTOMERLIST primary key  (EnergyCompanyID, CustomerID);
go


alter table EnergyCompanyOperatorLoginList
   add constraint PK_ENERGYCOMPANYOPERATORLOGINL primary key  (EnergyCompanyID, OperatorLoginID);
go


alter table FDRInterface
   add constraint PK_FDRINTERFACE primary key  (InterfaceID);
go


alter table FDRInterfaceOption
   add constraint PK_FDRINTERFACEOPTION primary key  (InterfaceID, Ordering);
go


alter table FDRTRANSLATION
   add constraint PK_FDRTrans primary key  (POINTID, InterfaceType, TRANSLATION);
go


/*==============================================================*/
/* Index: Indx_FdrTransIntTyp                                   */
/*==============================================================*/
create   index Indx_FdrTransIntTyp on FDRTRANSLATION (
InterfaceType
);
go


/*==============================================================*/
/* Index: Indx_FdrTrnsIntTypDir                                 */
/*==============================================================*/
create   index Indx_FdrTrnsIntTypDir on FDRTRANSLATION (
DIRECTIONTYPE,
InterfaceType
);
go


alter table FDRTelegyrGroup
   add constraint PK_FDRTELEGYRGROUP primary key  (GroupID);
go


alter table GRAPHDATASERIES
   add constraint SYS_GrphDserID primary key  (GRAPHDATASERIESID);
go


/*==============================================================*/
/* Index: Indx_GrpDSerPtID                                      */
/*==============================================================*/
create   index Indx_GrpDSerPtID on GRAPHDATASERIES (
POINTID
);
go


alter table GRAPHDEFINITION
   add constraint SYS_C0015109 primary key  (GRAPHDEFINITIONID);
go


alter table GRAPHDEFINITION
   add constraint AK_GRNMUQ_GRAPHDEF unique  (NAME);
go


alter table GatewayEndDevice
   add constraint PK_GATEWAYENDDEVICE primary key  (SerialNumber, HardwareType, DataType);
go


alter table GenericMacro
   add constraint PK_GENERICMACRO primary key  (OwnerID, ChildOrder, MacroType);
go


alter table GraphCustomerList
   add constraint PK_GRAPHCUSTOMERLIST primary key  (GraphDefinitionID, CustomerID);
go


alter table HolidaySchedule
   add constraint PK_HOLIDAYSCHEDULE primary key  (HolidayScheduleID);
go


/*==============================================================*/
/* Index: Indx_HolSchName                                       */
/*==============================================================*/
create unique  index Indx_HolSchName on HolidaySchedule (
HolidayScheduleName
);
go


alter table LMCONTROLAREAPROGRAM
   add constraint PK_LMCONTROLAREAPROGRAM primary key  (DEVICEID, LMPROGRAMDEVICEID);
go


alter table LMCONTROLAREATRIGGER
   add constraint PK_LMCONTROLAREATRIGGER primary key  (DEVICEID, TRIGGERNUMBER);
go


alter table LMControlArea
   add constraint PK_LMCONTROLAREA primary key  (DEVICEID);
go


alter table LMControlHistory
   add constraint PK_LMCONTROLHISTORY primary key  (LMCtrlHistID);
go


alter table LMCurtailCustomerActivity
   add constraint PK_LMCURTAILCUSTOMERACTIVITY primary key  (CustomerID, CurtailReferenceID);
go


/*==============================================================*/
/* Index: Index_LMCrtCstActID                                   */
/*==============================================================*/
create   index Index_LMCrtCstActID on LMCurtailCustomerActivity (
CustomerID
);
go


/*==============================================================*/
/* Index: Index_LMCrtCstAckSt                                   */
/*==============================================================*/
create   index Index_LMCrtCstAckSt on LMCurtailCustomerActivity (
AcknowledgeStatus
);
go


alter table LMCurtailProgramActivity
   add constraint PK_LMCURTAILPROGRAMACTIVITY primary key  (CurtailReferenceID);
go


/*==============================================================*/
/* Index: Indx_LMCrtPrgActStTime                                */
/*==============================================================*/
create   index Indx_LMCrtPrgActStTime on LMCurtailProgramActivity (
CurtailmentStartTime
);
go


alter table LMDirectCustomerList
   add constraint PK_LMDIRECTCUSTOMERLIST primary key  (ProgramID, CustomerID);
go


alter table LMDirectOperatorList
   add constraint PK_LMDIRECTOPERATORLIST primary key  (ProgramID, OperatorLoginID);
go


alter table LMEnergyExchangeCustomerList
   add constraint PK_LMENERGYEXCHANGECUSTOMERLIS primary key  (ProgramID, CustomerID);
go


alter table LMEnergyExchangeCustomerReply
   add constraint PK_LMENERGYEXCHANGECUSTOMERREP primary key  (CustomerID, OfferID, RevisionNumber);
go


alter table LMEnergyExchangeHourlyCustomer
   add constraint PK_LMENERGYEXCHANGEHOURLYCUSTO primary key  (CustomerID, OfferID, RevisionNumber, Hour);
go


alter table LMEnergyExchangeHourlyOffer
   add constraint PK_LMENERGYEXCHANGEHOURLYOFFER primary key  (OfferID, RevisionNumber, Hour);
go


alter table LMEnergyExchangeOfferRevision
   add constraint PK_LMENERGYEXCHANGEOFFERREVISI primary key  (OfferID, RevisionNumber);
go


alter table LMEnergyExchangeProgramOffer
   add constraint PK_LMENERGYEXCHANGEPROGRAMOFFE primary key  (OfferID);
go


alter table LMGroup
   add constraint PK_LMGROUP primary key  (DeviceID);
go


alter table LMGroupEmetcon
   add constraint PK_LMGROUPEMETCON primary key  (DEVICEID);
go


alter table LMGroupExpressCom
   add constraint PK_LMGROUPEXPRESSCOM primary key  (LMGroupID);
go


alter table LMGroupExpressComAddress
   add constraint PK_LMGROUPEXPRESSCOMADDRESS primary key  (AddressID);
go


alter table LMGroupMCT
   add constraint PK_LMGrpMCTPK primary key  (DeviceID);
go


alter table LMGroupPoint
   add constraint PK_LMGROUPPOINT primary key  (DEVICEID);
go


alter table LMGroupRipple
   add constraint PK_LMGROUPRIPPLE primary key  (DeviceID);
go


alter table LMGroupVersacom
   add constraint PK_LMGROUPVERSACOM primary key  (DEVICEID);
go


alter table LMMACSScheduleOperatorList
   add constraint PK_LMMACSSCHEDULEOPERATORLIST primary key  (ScheduleID, OperatorLoginID);
go


alter table LMMacsScheduleCustomerList
   add constraint PK_LMMACSSCHEDULECUSTOMERLIST primary key  (ScheduleID, LMCustomerDeviceID);
go


alter table LMPROGRAM
   add constraint PK_LMPROGRAM primary key  (DEVICEID);
go


alter table LMProgramControlWindow
   add constraint PK_LMPROGRAMCONTROLWINDOW primary key  (DeviceID, WindowNumber);
go


alter table LMProgramCurtailCustomerList
   add constraint PK_LMPROGRAMCURTAILCUSTOMERLIS primary key  (CustomerID, ProgramID);
go


alter table LMProgramCurtailment
   add constraint PK_LMPROGRAMCURTAILMENT primary key  (DeviceID);
go


alter table LMProgramDirect
   add constraint PK_LMPROGRAMDIRECT primary key  (DeviceID);
go


alter table LMProgramDirectGear
   add constraint PK_LMPROGRAMDIRECTGEAR primary key  (GearID);
go


alter table LMProgramDirectGear
   add constraint AK_AKEY_LMPRGDIRG_LMPROGRA unique  (DeviceID, GearNumber);
go


alter table LMProgramDirectGroup
   add constraint PK_LMPROGRAMDIRECTGROUP primary key  (DeviceID, GroupOrder);
go


alter table LMProgramEnergyExchange
   add constraint PK_LMPROGRAMENERGYEXCHANGE primary key  (DeviceID);
go


alter table LMThermoStatGear
   add constraint PK_LMTHERMOSTATGEAR primary key  (GearID);
go


alter table LOGIC
   add constraint SYS_C0013445 primary key  (LOGICID);
go


alter table MACROROUTE
   add constraint PK_MACROROUTE primary key  (ROUTEID, ROUTEORDER);
go


alter table MACSchedule
   add constraint PK_MACSCHEDULE primary key  (ScheduleID);
go


alter table MACSimpleSchedule
   add constraint PK_MACSIMPLESCHEDULE primary key  (ScheduleID);
go


alter table MCTBroadCastMapping
   add constraint PK_MCTBROADCASTMAPPING primary key  (MCTBroadCastID, MctID);
go


alter table MCTConfig
   add constraint PK_MCTCONFIG primary key  (ConfigID);
go


alter table MCTConfigMapping
   add constraint PK_MCTCONFIGMAPPING primary key  (MctID, ConfigID);
go


alter table NotificationDestination
   add constraint PKey_NotDestID primary key  (NotificationGroupID, DestinationOrder);
go


alter table NotificationGroup
   add constraint PK_NOTIFICATIONGROUP primary key  (NotificationGroupID);
go


/*==============================================================*/
/* Index: Indx_NOTIFGRPNme                                      */
/*==============================================================*/
create unique  index Indx_NOTIFGRPNme on NotificationGroup (
GroupName
);
go


alter table OperatorLoginGraphList
   add constraint PK_OPERATORLOGINGRAPHLIST primary key  (OperatorLoginID, GraphDefinitionID);
go


alter table OperatorSerialGroup
   add constraint PK_OpSerGrp primary key  (LoginID);
go


alter table PAOExclusion
   add constraint PK_PAOEXCLUSION primary key  (ExclusionID);
go


/*==============================================================*/
/* Index: Indx_PAOExclus                                        */
/*==============================================================*/
create unique  index Indx_PAOExclus on PAOExclusion (
PaoID,
ExcludedPaoID
);
go


alter table PAOowner
   add constraint PK_PAOOWNER primary key  (OwnerID, ChildID);
go


alter table POINT
   add constraint Key_PT_PTID primary key  (POINTID);
go


alter table POINT
   add constraint AK_KEY_PTNM_YUKPAOID unique  (POINTNAME, PAObjectID);
go


/*==============================================================*/
/* Index: Indx_PointStGrpID                                     */
/*==============================================================*/
create   index Indx_PointStGrpID on POINT (
STATEGROUPID
);
go


alter table POINTACCUMULATOR
   add constraint PK_POINTACCUMULATOR primary key  (POINTID);
go


alter table POINTANALOG
   add constraint PK_POINTANALOG primary key  (POINTID);
go


alter table POINTLIMITS
   add constraint PK_POINTLIMITS primary key  (POINTID, LIMITNUMBER);
go


alter table POINTSTATUS
   add constraint PK_PtStatus primary key  (POINTID);
go


alter table POINTUNIT
   add constraint PK_POINTUNITID primary key  (POINTID);
go


alter table PORTDIALUPMODEM
   add constraint PK_PORTDIALUPMODEM primary key  (PORTID);
go


alter table PORTLOCALSERIAL
   add constraint PK_PORTLOCALSERIAL primary key  (PORTID);
go


alter table PORTRADIOSETTINGS
   add constraint PK_PORTRADIOSETTINGS primary key  (PORTID);
go


alter table PORTSETTINGS
   add constraint PK_PORTSETTINGS primary key  (PORTID);
go


alter table PORTTERMINALSERVER
   add constraint PK_PORTTERMINALSERVER primary key  (PORTID);
go


alter table PointAlarming
   add constraint PK_POINTALARMING primary key  (PointID);
go


alter table PortStatistics
   add constraint PK_PORTSTATISTICS primary key  (PORTID, STATISTICTYPE);
go


alter table PortTiming
   add constraint PK_PORTTIMING primary key  (PORTID);
go


alter table RAWPOINTHISTORY
   add constraint SYS_C0013322 primary key  (CHANGEID);
go


/*==============================================================*/
/* Index: Index_PointID                                         */
/*==============================================================*/
create   index Index_PointID on RAWPOINTHISTORY (
POINTID
);
go


/*==============================================================*/
/* Index: Indx_TimeStamp                                        */
/*==============================================================*/
create   index Indx_TimeStamp on RAWPOINTHISTORY (
TIMESTAMP
);
go


alter table RepeaterRoute
   add constraint PK_REPEATERROUTE primary key  (ROUTEID, DEVICEID);
go


alter table Route
   add constraint SYS_RoutePK primary key  (RouteID);
go


/*==============================================================*/
/* Index: Indx_RouteDevID                                       */
/*==============================================================*/
create unique  index Indx_RouteDevID on Route (
DeviceID,
RouteID
);
go


alter table STATE
   add constraint PK_STATE primary key  (STATEGROUPID, RAWSTATE);
go


/*==============================================================*/
/* Index: Indx_StateRaw                                         */
/*==============================================================*/
create   index Indx_StateRaw on STATE (
RAWSTATE
);
go


alter table STATEGROUP
   add constraint SYS_C0013128 primary key  (STATEGROUPID);
go


/*==============================================================*/
/* Index: Indx_STATEGRP_Nme                                     */
/*==============================================================*/
create unique  index Indx_STATEGRP_Nme on STATEGROUP (
NAME
);
go


alter table SYSTEMLOG
   add constraint SYS_C0013407 primary key  (LOGID);
go


/*==============================================================*/
/* Index: Indx_SYSLG_PtId                                       */
/*==============================================================*/
create   index Indx_SYSLG_PtId on SYSTEMLOG (
POINTID
);
go


/*==============================================================*/
/* Index: Indx_SYSLG_Date                                       */
/*==============================================================*/
create   index Indx_SYSLG_Date on SYSTEMLOG (
DATETIME
);
go


alter table SeasonSchedule
   add constraint PK_SEASONSCHEDULE primary key  (ScheduleID);
go


alter table TEMPLATE
   add constraint SYS_C0013425 primary key  (TEMPLATENUM);
go


alter table TEMPLATECOLUMNS
   add constraint PK_TEMPLATECOLUMNS primary key  (TEMPLATENUM, TITLE);
go


alter table TOUDay
   add constraint PK_TOUDAY primary key  (TOUDayID);
go


alter table TOUDayMapping
   add constraint PK_TOUDAYMAPPING primary key  (TOUScheduleID, TOUDayOffset);
go


alter table TOUDayRateSwitches
   add constraint PK_TOURATESWITCH primary key  (TOURateSwitchID);
go


alter table TOUSchedule
   add constraint PK_TOUSCHEDULE primary key  (TOUScheduleID);
go


alter table TagLog
   add constraint PK_TAGLOG primary key  (LogID);
go


alter table Tags
   add constraint PK_TAGS primary key  (TagID);
go


alter table UNITMEASURE
   add constraint SYS_C0013344 primary key  (UOMID);
go


alter table VersacomRoute
   add constraint PK_VERSACOMROUTE primary key  (ROUTEID);
go


alter table YukonGroup
   add constraint PK_YUKONGROUP primary key  (GroupID);
go


alter table YukonGroupRole
   add constraint PK_YUKONGRPROLE primary key  (GroupRoleID);
go


alter table YukonImage
   add constraint PK_YUKONIMAGE primary key  (ImageID);
go


alter table YukonListEntry
   add constraint PK_YUKONLISTENTRY primary key  (EntryID);
go


/*==============================================================*/
/* Index: Indx_YkLstDefID                                       */
/*==============================================================*/
create   index Indx_YkLstDefID on YukonListEntry (
YukonDefinitionID
);
go


alter table YukonPAObject
   add constraint PK_YUKONPAOBJECT primary key  (PAObjectID);
go


/*==============================================================*/
/* Index: Indx_PAO                                              */
/*==============================================================*/
create unique  index Indx_PAO on YukonPAObject (
Category,
PAOName,
PAOClass,
Type
);
go


alter table YukonRole
   add constraint PK_YUKONROLE primary key  (RoleID);
go


/*==============================================================*/
/* Index: Indx_YukRol_Nm                                        */
/*==============================================================*/
create   index Indx_YukRol_Nm on YukonRole (
RoleName
);
go


alter table YukonRoleProperty
   add constraint PK_YUKONROLEPROPERTY primary key  (RolePropertyID);
go


alter table YukonSelectionList
   add constraint PK_YUKONSELECTIONLIST primary key  (ListID);
go


alter table YukonServices
   add constraint PK_YUKSER primary key  (ServiceID);
go


alter table YukonUser
   add constraint PK_YUKONUSER primary key  (UserID);
go


/*==============================================================*/
/* Index: Indx_YkUsIDNm                                         */
/*==============================================================*/
create unique  index Indx_YkUsIDNm on YukonUser (
UserName
);
go


alter table YukonUserGroup
   add constraint PK_YUKONUSERGROUP primary key  (UserID, GroupID);
go


alter table YukonUserRole
   add constraint PK_YKONUSRROLE primary key  (UserRoleID);
go


alter table YukonWebConfiguration
   add constraint PK_YUKONWEBCONFIGURATION primary key  (ConfigurationID);
go


alter table AlarmCategory
   add constraint FK_ALRMCAT_NOTIFGRP foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID);
go


alter table CALCBASE
   add constraint SYS_C0013434 foreign key (POINTID)
      references POINT (POINTID);
go


alter table CALCCOMPONENT
   add constraint FK_ClcCmp_ClcBs foreign key (PointID)
      references CALCBASE (POINTID);
go


alter table CALCCOMPONENT
   add constraint FK_ClcCmp_Pt foreign key (COMPONENTPOINTID)
      references POINT (POINTID);
go


alter table CAPBANK
   add constraint SYS_C0013453 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table CAPBANK
   add constraint SYS_C0013454 foreign key (CONTROLPOINTID)
      references POINT (POINTID);
go


alter table CAPBANK
   add constraint SYS_C0013455 foreign key (CONTROLDEVICEID)
      references DEVICE (DEVICEID);
go


alter table CAPCONTROLSUBSTATIONBUS
   add constraint FK_CpSbBus_YPao foreign key (SubstationBusID)
      references YukonPAObject (PAObjectID);
go


alter table CAPCONTROLSUBSTATIONBUS
   add constraint SYS_C0013478 foreign key (CurrentWattLoadPointID)
      references POINT (POINTID);
go


alter table CAPCONTROLSUBSTATIONBUS
   add constraint SYS_C0013479 foreign key (CurrentVarLoadPointID)
      references POINT (POINTID);
go


alter table CCFeederBankList
   add constraint FK_CB_CCFeedLst foreign key (DeviceID)
      references CAPBANK (DEVICEID);
go


alter table CCFeederBankList
   add constraint FK_CCFeed_CCBnk foreign key (FeederID)
      references CapControlFeeder (FeederID);
go


alter table CCFeederSubAssignment
   add constraint FK_CCFeed_CCFass foreign key (FeederID)
      references CapControlFeeder (FeederID);
go


alter table CCFeederSubAssignment
   add constraint FK_CCSub_CCFeed foreign key (SubStationBusID)
      references CAPCONTROLSUBSTATIONBUS (SubstationBusID);
go


alter table CICustomerBase
   add constraint FK_CICstBas_CstAddrs foreign key (MainAddressID)
      references Address (AddressID);
go


alter table CICustomerBase
   add constraint FK_CstCI_Cst foreign key (CustomerID)
      references Customer (CustomerID);
go


alter table CalcPointBaseline
   add constraint FK_CLCBS_BASL foreign key (BaselineID)
      references BaseLine (BaselineID);
go


alter table CalcPointBaseline
   add constraint FK_ClcPtBs_ClcBs foreign key (PointID)
      references CALCBASE (POINTID);
go


alter table CapControlFeeder
   add constraint FK_PAObj_CCFeed foreign key (FeederID)
      references YukonPAObject (PAObjectID);
go


alter table CarrierRoute
   add constraint SYS_C0013264 foreign key (ROUTEID)
      references Route (RouteID);
go


alter table CommErrorHistory
   add constraint FK_ComErrHis_YPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID);
go


alter table CommPort
   add constraint FK_COMMPORT_REF_COMPO_YUKONPAO foreign key (PORTID)
      references YukonPAObject (PAObjectID);
go


alter table Contact
   add constraint FK_RefCstLg_CustCont foreign key (LogInID)
      references YukonUser (UserID);
go


alter table Contact
   add constraint FK_CONTACT_REF_CNT_A_ADDRESS foreign key (AddressID)
      references Address (AddressID);
go


alter table ContactNotification
   add constraint FK_CntNot_YkLs foreign key (NotificationCategoryID)
      references YukonListEntry (EntryID);
go


alter table ContactNotification
   add constraint FK_Cnt_CntNot foreign key (ContactID)
      references Contact (ContactID);
go


alter table Customer
   add constraint FK_Cst_Cnt foreign key (PrimaryContactID)
      references Contact (ContactID);
go


alter table CustomerAdditionalContact
   add constraint FK_CstCont_CICstCont foreign key (ContactID)
      references Contact (ContactID);
go


alter table CustomerAdditionalContact
   add constraint FK_Cust_CustAddCnt foreign key (CustomerID)
      references Customer (CustomerID);
go


alter table CustomerBaseLinePoint
   add constraint FK_CstBseLn_CICust foreign key (CustomerID)
      references CICustomerBase (CustomerID);
go


alter table CustomerBaseLinePoint
   add constraint FK_CstBseLn_ClcBse foreign key (PointID)
      references CALCBASE (POINTID);
go


alter table CustomerLoginSerialGroup
   add constraint FK_CsLgSG_CsL foreign key (LoginID)
      references YukonUser (UserID);
go


alter table CustomerLoginSerialGroup
   add constraint FK_CsLgSG_LMG foreign key (LMGroupID)
      references LMGroup (DeviceID);
go


alter table DEVICE
   add constraint FK_Dev_YukPAO foreign key (DEVICEID)
      references YukonPAObject (PAObjectID);
go


alter table DEVICE2WAYFLAGS
   add constraint SYS_C0013208 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table DEVICECARRIERSETTINGS
   add constraint SYS_C0013216 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table DEVICEDIALUPSETTINGS
   add constraint SYS_C0013193 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table DEVICEIDLCREMOTE
   add constraint SYS_C0013241 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table DEVICEIED
   add constraint SYS_C0013245 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table DEVICELOADPROFILE
   add constraint SYS_C0013234 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table DEVICEMCTIEDPORT
   add constraint SYS_C0013253 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table DEVICEMETERGROUP
   add constraint SYS_C0013213 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table DEVICESCANRATE
   add constraint SYS_C0013198 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table DEVICETAPPAGINGSETTINGS
   add constraint SYS_C0013237 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table DISPLAY2WAYDATA
   add constraint FK_DISPLAY2W_REF_POINT foreign key (POINTID)
      references POINT (POINTID);
go


alter table DISPLAY2WAYDATA
   add constraint SYS_C0013422 foreign key (DISPLAYNUM)
      references DISPLAY (DISPLAYNUM);
go


alter table DISPLAYCOLUMNS
   add constraint SYS_C0013418 foreign key (DISPLAYNUM)
      references DISPLAY (DISPLAYNUM);
go


alter table DISPLAYCOLUMNS
   add constraint SYS_C0013419 foreign key (TYPENUM)
      references COLUMNTYPE (TYPENUM);
go


alter table DYNAMICACCUMULATOR
   add constraint SYS_C0015129 foreign key (POINTID)
      references POINT (POINTID);
go


alter table DYNAMICDEVICESCANDATA
   add constraint SYS_C0015139 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table DYNAMICPOINTDISPATCH
   add constraint SYS_C0013331 foreign key (POINTID)
      references POINT (POINTID);
go


alter table DateOfHoliday
   add constraint FK_HolSchID foreign key (HolidayScheduleID)
      references HolidaySchedule (HolidayScheduleID);
go


alter table DeviceCBC
   add constraint SYS_C0013459 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table DeviceCBC
   add constraint SYS_C0013460 foreign key (ROUTEID)
      references Route (RouteID);
go


alter table DeviceCustomerList
   add constraint FK_DvStLsCst foreign key (CustomerID)
      references Customer (CustomerID);
go


alter table DeviceCustomerList
   add constraint FK_DvStLsDev foreign key (DeviceID)
      references DEVICE (DEVICEID);
go


alter table DeviceDNP
   add constraint FK_Dev_DevDNP foreign key (DeviceID)
      references DEVICE (DEVICEID);
go


alter table DeviceDirectCommSettings
   add constraint SYS_C0013186 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table DeviceDirectCommSettings
   add constraint SYS_C0013187 foreign key (PORTID)
      references CommPort (PORTID);
go


alter table DeviceMCT400Series
   add constraint FK_Dev4_DevC foreign key (DeviceID)
      references DEVICECARRIERSETTINGS (DEVICEID);
go


alter table DeviceMCT400Series
   add constraint FK_Dev4_TOU foreign key (TOUScheduleID)
      references TOUSchedule (TOUScheduleID);
go


alter table DeviceRoutes
   add constraint SYS_C0013219 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table DeviceRoutes
   add constraint SYS_C0013220 foreign key (ROUTEID)
      references Route (RouteID);
go


alter table DeviceWindow
   add constraint FK_DevScWin_Dev foreign key (DeviceID)
      references DEVICE (DEVICEID);
go


alter table DynamicCCCapBank
   add constraint FK_CpBnk_DynCpBnk foreign key (CapBankID)
      references CAPBANK (DEVICEID);
go


alter table DynamicCCFeeder
   add constraint FK_CCFeed_DyFeed foreign key (FeederID)
      references CapControlFeeder (FeederID);
go


alter table DynamicCCSubstationBus
   add constraint FK_CCSubBs_DySubBs foreign key (SubstationBusID)
      references CAPCONTROLSUBSTATIONBUS (SubstationBusID);
go


alter table DynamicCalcHistorical
   add constraint FK_DynClc_ClcB foreign key (PointID)
      references CALCBASE (POINTID);
go


alter table DynamicLMControlArea
   add constraint FK_LMCntlAr_DynLMCntAr foreign key (DeviceID)
      references LMControlArea (DEVICEID);
go


alter table DynamicLMControlAreaTrigger
   add constraint FK_LMCntArTr_DyLMCnArTr foreign key (DeviceID, TriggerNumber)
      references LMCONTROLAREATRIGGER (DEVICEID, TRIGGERNUMBER);
go


alter table DynamicLMControlHistory
   add constraint FK_DYNLMCNT_PAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID);
go


alter table DynamicLMGroup
   add constraint FK_LMGrp_DynLmGrp foreign key (DeviceID)
      references LMGroup (DeviceID);
go


alter table DynamicLMGroup
   add constraint FK_DyLmGr_LmPrDGr foreign key (LMProgramID)
      references LMProgramDirect (DeviceID);
go


alter table DynamicLMProgram
   add constraint FK_LMProg_DynLMPrg foreign key (DeviceID)
      references LMPROGRAM (DEVICEID);
go


alter table DynamicLMProgramDirect
   add constraint FK_DYNAMICL_LMPROGDIR_LMPROGRA foreign key (DeviceID)
      references LMProgramDirect (DeviceID);
go


alter table DynamicPAOStatistics
   add constraint FK_PASt_YkPA foreign key (PAOBjectID)
      references YukonPAObject (PAObjectID);
go


alter table DynamicPointAlarming
   add constraint FK_DynPtAl_Pt foreign key (PointID)
      references POINT (POINTID);
go


alter table DynamicPointAlarming
   add constraint FKf_DynPtAl_SysL foreign key (LogID)
      references SYSTEMLOG (LOGID);
go


alter table DynamicTags
   add constraint FK_DynTgs_Pt foreign key (PointID)
      references POINT (POINTID);
go


alter table DynamicTags
   add constraint FK_DYNAMICT_REF_DYNTG_TAGS foreign key (TagID)
      references Tags (TagID);
go


alter table EnergyCompany
   add constraint FK_EnCm_Cnt foreign key (PrimaryContactID)
      references Contact (ContactID);
go


alter table EnergyCompany
   add constraint FK_EngCmp_YkUs foreign key (UserID)
      references YukonUser (UserID);
go


alter table EnergyCompanyCustomerList
   add constraint FK_CICstBsEnCmpCsLs foreign key (CustomerID)
      references CICustomerBase (CustomerID);
go


alter table EnergyCompanyCustomerList
   add constraint FK_EnCmpEnCmpCsLs foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID);
go


alter table EnergyCompanyOperatorLoginList
   add constraint FK_EnCmpEnCmpOpLs foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID);
go


alter table EnergyCompanyOperatorLoginList
   add constraint FK_OpLgEnCmpOpLs foreign key (OperatorLoginID)
      references YukonUser (UserID);
go


alter table FDRInterfaceOption
   add constraint FK_FDRINTER_REFERENCE_FDRINTER foreign key (InterfaceID)
      references FDRInterface (InterfaceID);
go


alter table FDRTRANSLATION
   add constraint SYS_C0015066 foreign key (POINTID)
      references POINT (POINTID);
go


alter table GRAPHDATASERIES
   add constraint GrphDSeri_GrphDefID foreign key (GRAPHDEFINITIONID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID);
go


alter table GRAPHDATASERIES
   add constraint GrphDSeris_ptID foreign key (POINTID)
      references POINT (POINTID);
go


alter table GraphCustomerList
   add constraint FK_GRAPHCUS_REFGRPHCU_GRAPHDEF foreign key (GraphDefinitionID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID);
go


alter table GraphCustomerList
   add constraint FK_GrphCstLst_Cst foreign key (CustomerID)
      references Customer (CustomerID);
go


alter table LMCONTROLAREAPROGRAM
   add constraint FK_LMCntlArea_LMCntlArProg foreign key (DEVICEID)
      references LMControlArea (DEVICEID);
go


alter table LMCONTROLAREAPROGRAM
   add constraint FK_LMPrg_LMCntlArProg foreign key (LMPROGRAMDEVICEID)
      references LMPROGRAM (DEVICEID);
go


alter table LMCONTROLAREATRIGGER
   add constraint FK_LMCntlArea_LMCntlArTrig foreign key (DEVICEID)
      references LMControlArea (DEVICEID);
go


alter table LMCONTROLAREATRIGGER
   add constraint FK_Point_LMCntlArTrig foreign key (POINTID)
      references POINT (POINTID);
go


alter table LMCONTROLAREATRIGGER
   add constraint FK_Point_LMCtrlArTrigPk foreign key (PEAKPOINTID)
      references POINT (POINTID);
go


alter table LMControlArea
   add constraint FK_LmCntAr_YukPAO foreign key (DEVICEID)
      references YukonPAObject (PAObjectID);
go


alter table LMControlHistory
   add constraint FK_LmCtrlHis_YPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID);
go


alter table LMCurtailCustomerActivity
   add constraint FK_CICBas_LMCrtCstAct foreign key (CustomerID)
      references CICustomerBase (CustomerID);
go


alter table LMCurtailCustomerActivity
   add constraint FK_LMCURTAI_REFLMCST__LMCURTAI foreign key (CurtailReferenceID)
      references LMCurtailProgramActivity (CurtailReferenceID);
go


alter table LMCurtailProgramActivity
   add constraint FK_LMPrgCrt_LMCrlPAct foreign key (DeviceID)
      references LMProgramCurtailment (DeviceID);
go


alter table LMDirectCustomerList
   add constraint FK_CICstB_LMPrDi foreign key (CustomerID)
      references CICustomerBase (CustomerID);
go


alter table LMDirectCustomerList
   add constraint FK_LMDIRECT_REFLMPDIR_LMPROGRA foreign key (ProgramID)
      references LMProgramDirect (DeviceID);
go


alter table LMDirectOperatorList
   add constraint FK_LMDirOpLs_LMPrD foreign key (ProgramID)
      references LMProgramDirect (DeviceID);
go


alter table LMDirectOperatorList
   add constraint FK_OpLg_LMDOpLs foreign key (OperatorLoginID)
      references YukonUser (UserID);
go


alter table LMEnergyExchangeCustomerList
   add constraint FK_ExCsLs_CstBs foreign key (CustomerID)
      references CICustomerBase (CustomerID);
go


alter table LMEnergyExchangeCustomerList
   add constraint FK_ExCsLs_PrEx foreign key (ProgramID)
      references LMProgramEnergyExchange (DeviceID);
go


alter table LMEnergyExchangeCustomerReply
   add constraint FK_ExCsRp_CstBs foreign key (CustomerID)
      references CICustomerBase (CustomerID);
go


alter table LMEnergyExchangeCustomerReply
   add constraint FK_LMENERGY_REFEXCSTR_LMENERGY foreign key (OfferID, RevisionNumber)
      references LMEnergyExchangeOfferRevision (OfferID, RevisionNumber)
      on delete cascade
go


alter table LMEnergyExchangeHourlyCustomer
   add constraint FK_ExHrCs_ExCsRp foreign key (CustomerID, OfferID, RevisionNumber)
      references LMEnergyExchangeCustomerReply (CustomerID, OfferID, RevisionNumber)
      on delete cascade
go


alter table LMEnergyExchangeHourlyOffer
   add constraint FK_ExHrOff_ExOffRv foreign key (OfferID, RevisionNumber)
      references LMEnergyExchangeOfferRevision (OfferID, RevisionNumber);
go


alter table LMEnergyExchangeOfferRevision
   add constraint FK_EExOffR_ExPrOff foreign key (OfferID)
      references LMEnergyExchangeProgramOffer (OfferID);
go


alter table LMEnergyExchangeProgramOffer
   add constraint FK_EnExOff_PrgEnEx foreign key (DeviceID)
      references LMProgramEnergyExchange (DeviceID);
go


alter table LMGroup
   add constraint FK_Device_LMGrpBase2 foreign key (DeviceID)
      references DEVICE (DEVICEID);
go


alter table LMGroupEmetcon
   add constraint SYS_C0013356 foreign key (DEVICEID)
      references LMGroup (DeviceID);
go


alter table LMGroupEmetcon
   add constraint SYS_C0013357 foreign key (ROUTEID)
      references Route (RouteID);
go


alter table LMGroupExpressCom
   add constraint FK_ExCG_LMExCm foreign key (GeoID)
      references LMGroupExpressComAddress (AddressID);
go


alter table LMGroupExpressCom
   add constraint FK_ExCP_LMExCm foreign key (ProgramID)
      references LMGroupExpressComAddress (AddressID);
go


alter table LMGroupExpressCom
   add constraint FK_ExCSb_LMExCm foreign key (SubstationID)
      references LMGroupExpressComAddress (AddressID);
go


alter table LMGroupExpressCom
   add constraint FK_ExCSp_LMExCm foreign key (ServiceProviderID)
      references LMGroupExpressComAddress (AddressID);
go


alter table LMGroupExpressCom
   add constraint FK_ExCad_LMExCm foreign key (FeederID)
      references LMGroupExpressComAddress (AddressID);
go


alter table LMGroupExpressCom
   add constraint FK_LGrEx_LMG foreign key (LMGroupID)
      references LMGroup (DeviceID);
go


alter table LMGroupExpressCom
   add constraint FK_LGrEx_Rt foreign key (RouteID)
      references Route (RouteID);
go


alter table LMGroupMCT
   add constraint FK_LMGrMC_Grp foreign key (DeviceID)
      references LMGroup (DeviceID);
go


alter table LMGroupMCT
   add constraint FK_LMGrMC_Rt foreign key (RouteID)
      references Route (RouteID);
go


alter table LMGroupMCT
   add constraint FK_LMGrMC_YkP foreign key (MCTDeviceID)
      references YukonPAObject (PAObjectID);
go


alter table LMGroupPoint
   add constraint FK_LMGrpPt_Dev foreign key (DeviceIDUsage)
      references DEVICE (DEVICEID);
go


alter table LMGroupPoint
   add constraint FK_LMGrpPt_LMGrp foreign key (DEVICEID)
      references LMGroup (DeviceID);
go


alter table LMGroupPoint
   add constraint FK_LMGrpPt_Pt foreign key (PointIDUsage)
      references POINT (POINTID);
go


alter table LMGroupRipple
   add constraint FK_LmGr_LmGrpRip foreign key (DeviceID)
      references LMGroup (DeviceID);
go


alter table LMGroupRipple
   add constraint FK_LmGrpRip_Rout foreign key (RouteID)
      references Route (RouteID);
go


alter table LMGroupVersacom
   add constraint FK_LMGrp_LMGrpVers foreign key (DEVICEID)
      references LMGroup (DeviceID);
go


alter table LMGroupVersacom
   add constraint SYS_C0013367 foreign key (ROUTEID)
      references Route (RouteID);
go


alter table LMMACSScheduleOperatorList
   add constraint FK_MCSchLMMcSchOpLs foreign key (ScheduleID)
      references MACSchedule (ScheduleID);
go


alter table LMMACSScheduleOperatorList
   add constraint FK_OpLgLMMcSchOpLs foreign key (OperatorLoginID)
      references YukonUser (UserID);
go


alter table LMMacsScheduleCustomerList
   add constraint FK_McSchCstLst_MCSched foreign key (ScheduleID)
      references MACSchedule (ScheduleID);
go


alter table LMMacsScheduleCustomerList
   add constraint FK_McsSchdCusLst_CICBs foreign key (LMCustomerDeviceID)
      references CICustomerBase (CustomerID);
go


alter table LMPROGRAM
   add constraint FK_HlSc_LmPr foreign key (HolidayScheduleID)
      references HolidaySchedule (HolidayScheduleID);
go


alter table LMPROGRAM
   add constraint FK_LmProg_YukPAO foreign key (DEVICEID)
      references YukonPAObject (PAObjectID);
go


alter table LMPROGRAM
   add constraint FK_SesSch_LmPr foreign key (SeasonScheduleID)
      references SeasonSchedule (ScheduleID);
go


alter table LMProgramControlWindow
   add constraint FK_LMPrg_LMPrgCntWind foreign key (DeviceID)
      references LMPROGRAM (DEVICEID);
go


alter table LMProgramCurtailCustomerList
   add constraint FK_CICstBase_LMProgCList foreign key (CustomerID)
      references CICustomerBase (CustomerID);
go


alter table LMProgramCurtailCustomerList
   add constraint FK_LMPrgCrt_LMPrCstLst foreign key (ProgramID)
      references LMProgramCurtailment (DeviceID)
      on delete cascade
go


alter table LMProgramCurtailment
   add constraint FK_LMPrg_LMPrgCurt foreign key (DeviceID)
      references LMPROGRAM (DEVICEID);
go


alter table LMProgramDirect
   add constraint FK_LMPrg_LMPrgDirect foreign key (DeviceID)
      references LMPROGRAM (DEVICEID);
go


alter table LMProgramDirectGear
   add constraint FK_LMProgD_LMProgDGr foreign key (DeviceID)
      references LMProgramDirect (DeviceID);
go


alter table LMProgramDirectGroup
   add constraint FK_LMGrp_LMPrgDGrp foreign key (LMGroupDeviceID)
      references LMGroup (DeviceID);
go


alter table LMProgramDirectGroup
   add constraint FK_LMPrgD_LMPrgDGrp foreign key (DeviceID)
      references LMProgramDirect (DeviceID);
go


alter table LMProgramEnergyExchange
   add constraint FK_LmPrg_LmPrEEx foreign key (DeviceID)
      references LMPROGRAM (DEVICEID);
go


alter table LMThermoStatGear
   add constraint FK_ThrmStG_PrDiGe foreign key (GearID)
      references LMProgramDirectGear (GearID);
go


alter table MACROROUTE
   add constraint SYS_C0013274 foreign key (ROUTEID)
      references Route (RouteID);
go


alter table MACROROUTE
   add constraint SYS_C0013275 foreign key (SINGLEROUTEID)
      references Route (RouteID);
go


alter table MACSchedule
   add constraint FK_SchdID_PAOID foreign key (ScheduleID)
      references YukonPAObject (PAObjectID);
go


alter table MACSimpleSchedule
   add constraint FK_MACSIMPLE_MACSCHED_ID foreign key (ScheduleID)
      references MACSchedule (ScheduleID);
go


alter table MCTBroadCastMapping
   add constraint FK_MCTB_MAPDEV foreign key (MCTBroadCastID)
      references DEVICE (DEVICEID);
go


alter table MCTBroadCastMapping
   add constraint FK_MCTB_MAPMCT foreign key (MctID)
      references DEVICE (DEVICEID);
go


alter table MCTConfigMapping
   add constraint FK_McCfgM_Dev foreign key (MctID)
      references DEVICE (DEVICEID);
go


alter table MCTConfigMapping
   add constraint FK_McCfgM_McCfg foreign key (ConfigID)
      references MCTConfig (ConfigID);
go


alter table NotificationDestination
   add constraint FK_NotifDest_NotifGrp foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID);
go


alter table NotificationDestination
   add constraint FK_CntNt_NtDst foreign key (RecipientID)
      references ContactNotification (ContactNotifID);
go


alter table OperatorLoginGraphList
   add constraint FK_OpLgOpLgGrLs foreign key (GraphDefinitionID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID);
go


alter table OperatorLoginGraphList
   add constraint FK_OpLgOpLgGrLs2 foreign key (OperatorLoginID)
      references YukonUser (UserID);
go


alter table OperatorSerialGroup
   add constraint FK_OpSGrp_LmGrp foreign key (LMGroupID)
      references LMGroup (DeviceID);
go


alter table OperatorSerialGroup
   add constraint FK_OpSGrp_OpLg foreign key (LoginID)
      references YukonUser (UserID);
go


alter table PAOExclusion
   add constraint FK_PAOEx_Pt foreign key (PointID)
      references POINT (POINTID);
go


alter table PAOExclusion
   add constraint FK_PAOEx_YkPAO foreign key (PaoID)
      references YukonPAObject (PAObjectID);
go


alter table PAOExclusion
   add constraint FK_PAOEXCLU_REF_PAOEX_YUKONPAO foreign key (ExcludedPaoID)
      references YukonPAObject (PAObjectID);
go


alter table PAOowner
   add constraint FK_YukPAO_PAOOwn foreign key (ChildID)
      references YukonPAObject (PAObjectID);
go


alter table PAOowner
   add constraint FK_YukPAO_PAOid foreign key (OwnerID)
      references YukonPAObject (PAObjectID);
go


alter table POINT
   add constraint FK_Pt_YukPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID);
go


alter table POINT
   add constraint Ref_STATGRP_PT foreign key (STATEGROUPID)
      references STATEGROUP (STATEGROUPID);
go


alter table POINTACCUMULATOR
   add constraint SYS_C0013317 foreign key (POINTID)
      references POINT (POINTID);
go


alter table POINTANALOG
   add constraint SYS_C0013300 foreign key (POINTID)
      references POINT (POINTID);
go


alter table POINTLIMITS
   add constraint SYS_C0013289 foreign key (POINTID)
      references POINT (POINTID);
go


alter table POINTSTATUS
   add constraint Ref_ptstatus_pt foreign key (POINTID)
      references POINT (POINTID);
go


alter table POINTUNIT
   add constraint FK_PtUnit_UoM foreign key (UOMID)
      references UNITMEASURE (UOMID);
go


alter table POINTUNIT
   add constraint Ref_ptunit_point foreign key (POINTID)
      references POINT (POINTID);
go


alter table PORTDIALUPMODEM
   add constraint SYS_C0013175 foreign key (PORTID)
      references CommPort (PORTID);
go


alter table PORTLOCALSERIAL
   add constraint SYS_C0013147 foreign key (PORTID)
      references CommPort (PORTID);
go


alter table PORTRADIOSETTINGS
   add constraint SYS_C0013169 foreign key (PORTID)
      references CommPort (PORTID);
go


alter table PORTSETTINGS
   add constraint SYS_C0013156 foreign key (PORTID)
      references CommPort (PORTID);
go


alter table PORTTERMINALSERVER
   add constraint SYS_C0013151 foreign key (PORTID)
      references CommPort (PORTID);
go


alter table PointAlarming
   add constraint FK_POINTALAARM_POINT_POINTID foreign key (PointID)
      references POINT (POINTID);
go


alter table PointAlarming
   add constraint FK_POINTALARMING foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID);
go


alter table PointAlarming
   add constraint FK_CntNt_PtAl foreign key (RecipientID)
      references ContactNotification (ContactNotifID);
go


alter table PortStatistics
   add constraint SYS_C0013183 foreign key (PORTID)
      references CommPort (PORTID);
go


alter table PortTiming
   add constraint SYS_C0013163 foreign key (PORTID)
      references CommPort (PORTID);
go


alter table RAWPOINTHISTORY
   add constraint FK_RawPt_Point foreign key (POINTID)
      references POINT (POINTID);
go


alter table RepeaterRoute
   add constraint SYS_C0013269 foreign key (ROUTEID)
      references Route (RouteID);
go


alter table RepeaterRoute
   add constraint SYS_C0013270 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table Route
   add constraint FK_Route_DevID foreign key (DeviceID)
      references DEVICE (DEVICEID);
go


alter table Route
   add constraint FK_Route_YukPAO foreign key (RouteID)
      references YukonPAObject (PAObjectID);
go


alter table STATE
   add constraint FK_YkIm_St foreign key (ImageID)
      references YukonImage (ImageID);
go


alter table STATE
   add constraint SYS_C0013342 foreign key (STATEGROUPID)
      references STATEGROUP (STATEGROUPID);
go


alter table SYSTEMLOG
   add constraint SYS_C0013408 foreign key (POINTID)
      references POINT (POINTID);
go


alter table TEMPLATECOLUMNS
   add constraint SYS_C0013429 foreign key (TEMPLATENUM)
      references TEMPLATE (TEMPLATENUM);
go


alter table TEMPLATECOLUMNS
   add constraint SYS_C0013430 foreign key (TYPENUM)
      references COLUMNTYPE (TYPENUM);
go


alter table TOUDayMapping
   add constraint FK_TOUd_TOUSc foreign key (TOUScheduleID)
      references TOUSchedule (TOUScheduleID);
go


alter table TOUDayMapping
   add constraint FK_TOUm_TOUd foreign key (TOUDayID)
      references TOUDay (TOUDayID);
go


alter table TOUDayRateSwitches
   add constraint FK_TOUdRS_TOUd foreign key (TOUDayID)
      references TOUDay (TOUDayID);
go


alter table TagLog
   add constraint FK_TagLg_Pt foreign key (PointID)
      references POINT (POINTID);
go


alter table TagLog
   add constraint FK_TagLg_Tgs foreign key (TagID)
      references Tags (TagID);
go


alter table VersacomRoute
   add constraint FK_VERSACOM_ROUTE_VER_ROUTE foreign key (ROUTEID)
      references Route (RouteID);
go


alter table YukonGroupRole
   add constraint FK_YkGrRl_YkGrp foreign key (GroupID)
      references YukonGroup (GroupID);
go


alter table YukonGroupRole
   add constraint FK_YkGrRl_YkRle foreign key (RoleID)
      references YukonRole (RoleID);
go


alter table YukonGroupRole
   add constraint FK_YkGrpR_YkRlPr foreign key (RolePropertyID)
      references YukonRoleProperty (RolePropertyID);
go


alter table YukonListEntry
   add constraint FK_LstEnty_SelLst foreign key (ListID)
      references YukonSelectionList (ListID);
go


alter table YukonRoleProperty
   add constraint FK_YkRlPrp_YkRle foreign key (RoleID)
      references YukonRole (RoleID);
go


alter table YukonUserGroup
   add constraint FK_YkUsGr_YkGr foreign key (GroupID)
      references YukonGroup (GroupID);
go


alter table YukonUserGroup
   add constraint FK_YUKONUSE_REF_YKUSG_YUKONUSE foreign key (UserID)
      references YukonUser (UserID);
go


alter table YukonUserRole
   add constraint FK_YkUsRl_RlPrp foreign key (RolePropertyID)
      references YukonRoleProperty (RolePropertyID);
go


alter table YukonUserRole
   add constraint FK_YkUsRl_YkRol foreign key (RoleID)
      references YukonRole (RoleID);
go


alter table YukonUserRole
   add constraint FK_YkUsRlr_YkUsr foreign key (UserID)
      references YukonUser (UserID);
go


drop table TOUDeviceMapping;
drop table TOUDayMapping;
drop table TOURateOffset;
drop table TOUDay;
drop table TOUSchedule;
go


create table DynamicLMControlHistory (
PAObjectID           numeric              not null,
LMCtrlHistID         numeric              not null,
StartDateTime        datetime             not null,
SOE_Tag              numeric              not null,
ControlDuration      numeric              not null,
ControlType          varchar(32)          not null,
CurrentDailyTime     numeric              not null,
CurrentMonthlyTime   numeric              not null,
CurrentSeasonalTime  numeric              not null,
CurrentAnnualTime    numeric              not null,
ActiveRestore        char(1)              not null,
ReductionValue       float                not null,
StopDateTime         datetime             not null
);
go
alter table DynamicLMControlHistory
   add constraint PK_DYNLMCONTROLHISTORY primary key  (PAObjectID);
go
alter table DynamicLMControlHistory
   add constraint FK_DYNLMCNT_PAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID);
go


create table TOUSchedule (
TOUScheduleID        numeric              not null,
TOUScheduleName      varchar(32)          not null,
TOUDefaultRate       varchar(4)           not null
);
go
alter table TOUSchedule
   add constraint PK_TOUSCHEDULE primary key  (TOUScheduleID);
go

create table TOUDay (
TOUDayID             numeric              not null,
TOUDayName           varchar(32)          not null
);
go
alter table TOUDay
   add constraint PK_TOUDAY primary key  (TOUDayID);
go

create table TOUDayRateSwitches (
TOURateSwitchID	     numeric		  not null,
SwitchRate           varchar(4)           not null,
SwitchOffset         numeric              not null,
TOUDayID             numeric              not null
);
go
alter table TOUDayRateSwitches
   add constraint PK_TOURATESWITCH primary key  (TOURateSwitchID);
go
alter table TOUDayRateSwitches
   add constraint FK_TOUdRS_TOUd foreign key (TOUDayID)
      references TOUDay (TOUDayID);
go
create unique index Indx_TOUSWITCHRATE_PK on TOUDayRateSwitches (
TOUDayID, SwitchOffset
);
go

create table TOUDayMapping (
TOUScheduleID        numeric              not null,
TOUDayID             numeric              not null,
TOUDayOffset         numeric              not null
);
go
alter table TOUDayMapping
   add constraint PK_TOUDAYMAPPING primary key  (TOUScheduleID, TOUDayOffset);
go
alter table TOUDayMapping
   add constraint FK_TOUd_TOUSc foreign key (TOUScheduleID)
      references TOUSchedule (TOUScheduleID);
go
alter table TOUDayMapping
   add constraint FK_TOUm_TOUd foreign key (TOUDayID)
      references TOUDay (TOUDayID);
go


alter table MCTConfig add DisplayDigits numeric;
go
update MCTConfig set DisplayDigits = 0;
go
alter table MCTConfig alter column DisplayDigits numeric not null;
go

create table DeviceMCT400Series (
DeviceID             numeric              not null,
DisconnectAddress    numeric              not null,
TOUScheduleID        numeric              not null
);
go
alter table DeviceMCT400Series
   add constraint PK_DEV400S primary key  (DeviceID);
go
alter table DeviceMCT400Series
   add constraint FK_Dev4_DevC foreign key (DeviceID)
      references DEVICECARRIERSETTINGS (DEVICEID);
go
alter table DeviceMCT400Series
   add constraint FK_Dev4_TOU foreign key (TOUScheduleID)
      references TOUSchedule (TOUScheduleID);
go

update yukonpaobject set type = 'MCT-410IL' where type like '%410%';

alter table ctidatabase drop constraint PK_CTIDATABASE;
go
alter table ctidatabase add constraint PK_CTIDATABASE primary key  (Version, Build);
go

insert into TOUSchedule values (0, '(none)', 0);
go






/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/



/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('3.00', 'Ryan', '22-APR-2004', 'Manual version insert done', 27);
go