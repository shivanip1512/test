/*==============================================================*/
/* Database name:  STARS                                        */
/* DBMS name:      CTI SqlServer 2000                           */
/* Created on:     8/12/2002 9:06:58 AM                         */
/*==============================================================*/


if exists (select 1
            from  sysobjects
           where  id = object_id('DISPLAY2WAYDATA_VIEW')
            and   type = 'V')
   drop view DISPLAY2WAYDATA_VIEW
go


if exists (select 1
            from  sysobjects
           where  id = object_id('FullEventLog_View')
            and   type = 'V')
   drop view FullEventLog_View
go


if exists (select 1
            from  sysobjects
           where  id = object_id('FullPointHistory_View')
            and   type = 'V')
   drop view FullPointHistory_View
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMCurtailCustomerActivity_View')
            and   type = 'V')
   drop view LMCurtailCustomerActivity_View
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMGroupMacroExpander_View')
            and   type = 'V')
   drop view LMGroupMacroExpander_View
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PointEventLog_View')
            and   type = 'V')
   drop view PointEventLog_View
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PointHistory_View')
            and   type = 'V')
   drop view PointHistory_View
go


if exists (select 1
            from  sysobjects
           where  id = object_id('AccountSite')
            and   type = 'U')
   drop table AccountSite
go


if exists (select 1
            from  sysobjects
           where  id = object_id('AirConditioner')
            and   type = 'U')
   drop table AirConditioner
go


if exists (select 1
            from  sysobjects
           where  id = object_id('AlarmCategory')
            and   type = 'U')
   drop table AlarmCategory
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ApplianceBase')
            and   type = 'U')
   drop table ApplianceBase
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ApplianceCategory')
            and   type = 'U')
   drop table ApplianceCategory
go


if exists (select 1
            from  sysobjects
           where  id = object_id('BillingFileFormats')
            and   type = 'U')
   drop table BillingFileFormats
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('CALCBASE')
            and   name  = 'Indx_ClcBaseUpdTyp'
            and   indid > 0
            and   indid < 255)
   drop index CALCBASE.Indx_ClcBaseUpdTyp
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CALCBASE')
            and   type = 'U')
   drop table CALCBASE
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('CALCCOMPONENT')
            and   name  = 'Indx_CalcCmpCmpType'
            and   indid > 0
            and   indid < 255)
   drop index CALCCOMPONENT.Indx_CalcCmpCmpType
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CALCCOMPONENT')
            and   type = 'U')
   drop table CALCCOMPONENT
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CAPBANK')
            and   type = 'U')
   drop table CAPBANK
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CAPCONTROLSUBSTATIONBUS')
            and   type = 'U')
   drop table CAPCONTROLSUBSTATIONBUS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CARRIERROUTE')
            and   type = 'U')
   drop table CARRIERROUTE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CCFeederBankList')
            and   type = 'U')
   drop table CCFeederBankList
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CCFeederSubAssignment')
            and   type = 'U')
   drop table CCFeederSubAssignment
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CICustContact')
            and   type = 'U')
   drop table CICustContact
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CICustomerBase')
            and   type = 'U')
   drop table CICustomerBase
go


if exists (select 1
            from  sysobjects
           where  id = object_id('COLUMNTYPE')
            and   type = 'U')
   drop table COLUMNTYPE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('COMMPORT')
            and   type = 'U')
   drop table COMMPORT
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CTIDatabase')
            and   type = 'U')
   drop table CTIDatabase
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CallReportBase')
            and   type = 'U')
   drop table CallReportBase
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CapControlFeeder')
            and   type = 'U')
   drop table CapControlFeeder
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CommErrorHistory')
            and   type = 'U')
   drop table CommErrorHistory
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CstBaseCstContactMap')
            and   type = 'U')
   drop table CstBaseCstContactMap
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerAccount')
            and   type = 'U')
   drop table CustomerAccount
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerAddress')
            and   type = 'U')
   drop table CustomerAddress
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerBase')
            and   type = 'U')
   drop table CustomerBase
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerBaseLine')
            and   type = 'U')
   drop table CustomerBaseLine
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerBaseLinePoint')
            and   type = 'U')
   drop table CustomerBaseLinePoint
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerContact')
            and   type = 'U')
   drop table CustomerContact
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerListEntry')
            and   type = 'U')
   drop table CustomerListEntry
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('CustomerLogin')
            and   name  = 'Indx_CstLogUsIDNm'
            and   indid > 0
            and   indid < 255)
   drop index CustomerLogin.Indx_CstLogUsIDNm
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('CustomerLogin')
            and   name  = 'Indx_CstLogPassword'
            and   indid > 0
            and   indid < 255)
   drop index CustomerLogin.Indx_CstLogPassword
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerLogin')
            and   type = 'U')
   drop table CustomerLogin
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerSelectionList')
            and   type = 'U')
   drop table CustomerSelectionList
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerWebConfiguration')
            and   type = 'U')
   drop table CustomerWebConfiguration
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerWebSettings')
            and   type = 'U')
   drop table CustomerWebSettings
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DEVICE')
            and   type = 'U')
   drop table DEVICE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DEVICE2WAYFLAGS')
            and   type = 'U')
   drop table DEVICE2WAYFLAGS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DEVICECARRIERSETTINGS')
            and   type = 'U')
   drop table DEVICECARRIERSETTINGS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DEVICECBC')
            and   type = 'U')
   drop table DEVICECBC
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DEVICEDIALUPSETTINGS')
            and   type = 'U')
   drop table DEVICEDIALUPSETTINGS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DEVICEDIRECTCOMMSETTINGS')
            and   type = 'U')
   drop table DEVICEDIRECTCOMMSETTINGS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DEVICEIDLCREMOTE')
            and   type = 'U')
   drop table DEVICEIDLCREMOTE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DEVICEIED')
            and   type = 'U')
   drop table DEVICEIED
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DEVICELOADPROFILE')
            and   type = 'U')
   drop table DEVICELOADPROFILE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DEVICEMCTIEDPORT')
            and   type = 'U')
   drop table DEVICEMCTIEDPORT
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DEVICEMETERGROUP')
            and   type = 'U')
   drop table DEVICEMETERGROUP
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DEVICEROUTES')
            and   type = 'U')
   drop table DEVICEROUTES
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DEVICESCANRATE')
            and   type = 'U')
   drop table DEVICESCANRATE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DEVICESTATISTICS')
            and   type = 'U')
   drop table DEVICESTATISTICS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DEVICETAPPAGINGSETTINGS')
            and   type = 'U')
   drop table DEVICETAPPAGINGSETTINGS
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('DISPLAY')
            and   name  = 'Indx_DISPLAYNAME'
            and   indid > 0
            and   indid < 255)
   drop index DISPLAY.Indx_DISPLAYNAME
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DISPLAY')
            and   type = 'U')
   drop table DISPLAY
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('DISPLAY2WAYDATA')
            and   name  = 'Index_DisNum'
            and   indid > 0
            and   indid < 255)
   drop index DISPLAY2WAYDATA.Index_DisNum
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DISPLAY2WAYDATA')
            and   type = 'U')
   drop table DISPLAY2WAYDATA
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DISPLAYCOLUMNS')
            and   type = 'U')
   drop table DISPLAYCOLUMNS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DYNAMICACCUMULATOR')
            and   type = 'U')
   drop table DYNAMICACCUMULATOR
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DYNAMICDEVICESCANDATA')
            and   type = 'U')
   drop table DYNAMICDEVICESCANDATA
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DYNAMICPOINTDISPATCH')
            and   type = 'U')
   drop table DYNAMICPOINTDISPATCH
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DateOfHoliday')
            and   type = 'U')
   drop table DateOfHoliday
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DeviceWindow')
            and   type = 'U')
   drop table DeviceWindow
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DynamicCCCapBank')
            and   type = 'U')
   drop table DynamicCCCapBank
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DynamicCCFeeder')
            and   type = 'U')
   drop table DynamicCCFeeder
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DynamicCCSubstationBus')
            and   type = 'U')
   drop table DynamicCCSubstationBus
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DynamicCalcHistorical')
            and   type = 'U')
   drop table DynamicCalcHistorical
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DynamicLMControlArea')
            and   type = 'U')
   drop table DynamicLMControlArea
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DynamicLMControlAreaTrigger')
            and   type = 'U')
   drop table DynamicLMControlAreaTrigger
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DynamicLMGroup')
            and   type = 'U')
   drop table DynamicLMGroup
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DynamicLMProgram')
            and   type = 'U')
   drop table DynamicLMProgram
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DynamicLMProgramDirect')
            and   type = 'U')
   drop table DynamicLMProgramDirect
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ECToAccountMapping')
            and   type = 'U')
   drop table ECToAccountMapping
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ECToCallReportMapping')
            and   type = 'U')
   drop table ECToCallReportMapping
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ECToCustomerBaseMapping')
            and   type = 'U')
   drop table ECToCustomerBaseMapping
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ECToGenericMapping')
            and   type = 'U')
   drop table ECToGenericMapping
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ECToInventoryMapping')
            and   type = 'U')
   drop table ECToInventoryMapping
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ECToLMCustomerEventMapping')
            and   type = 'U')
   drop table ECToLMCustomerEventMapping
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ECToWorkOrderMapping')
            and   type = 'U')
   drop table ECToWorkOrderMapping
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('EnergyCompany')
            and   name  = 'Indx_EnCmpName'
            and   indid > 0
            and   indid < 255)
   drop index EnergyCompany.Indx_EnCmpName
go


if exists (select 1
            from  sysobjects
           where  id = object_id('EnergyCompany')
            and   type = 'U')
   drop table EnergyCompany
go


if exists (select 1
            from  sysobjects
           where  id = object_id('EnergyCompanyCustomerList')
            and   type = 'U')
   drop table EnergyCompanyCustomerList
go


if exists (select 1
            from  sysobjects
           where  id = object_id('EnergyCompanyOperatorLoginList')
            and   type = 'U')
   drop table EnergyCompanyOperatorLoginList
go


if exists (select 1
            from  sysobjects
           where  id = object_id('FDRInterface')
            and   type = 'U')
   drop table FDRInterface
go


if exists (select 1
            from  sysobjects
           where  id = object_id('FDRInterfaceOption')
            and   type = 'U')
   drop table FDRInterfaceOption
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('FDRTRANSLATION')
            and   name  = 'Indx_FdrTransIntTyp'
            and   indid > 0
            and   indid < 255)
   drop index FDRTRANSLATION.Indx_FdrTransIntTyp
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('FDRTRANSLATION')
            and   name  = 'Indx_FdrTrnsIntTypDir'
            and   indid > 0
            and   indid < 255)
   drop index FDRTRANSLATION.Indx_FdrTrnsIntTypDir
go


if exists (select 1
            from  sysobjects
           where  id = object_id('FDRTRANSLATION')
            and   type = 'U')
   drop table FDRTRANSLATION
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('GRAPHDATASERIES')
            and   name  = 'Indx_GrpDSerPtID'
            and   indid > 0
            and   indid < 255)
   drop index GRAPHDATASERIES.Indx_GrpDSerPtID
go


if exists (select 1
            from  sysobjects
           where  id = object_id('GRAPHDATASERIES')
            and   type = 'U')
   drop table GRAPHDATASERIES
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('GRAPHDEFINITION')
            and   name  = 'Indx_GrNam'
            and   indid > 0
            and   indid < 255)
   drop index GRAPHDEFINITION.Indx_GrNam
go


if exists (select 1
            from  sysobjects
           where  id = object_id('GRAPHDEFINITION')
            and   type = 'U')
   drop table GRAPHDEFINITION
go


if exists (select 1
            from  sysobjects
           where  id = object_id('GenericMacro')
            and   type = 'U')
   drop table GenericMacro
go


if exists (select 1
            from  sysobjects
           where  id = object_id('GraphCustomerList')
            and   type = 'U')
   drop table GraphCustomerList
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('HolidaySchedule')
            and   name  = 'Indx_HolSchName'
            and   indid > 0
            and   indid < 255)
   drop index HolidaySchedule.Indx_HolSchName
go


if exists (select 1
            from  sysobjects
           where  id = object_id('HolidaySchedule')
            and   type = 'U')
   drop table HolidaySchedule
go


if exists (select 1
            from  sysobjects
           where  id = object_id('InventoryBase')
            and   type = 'U')
   drop table InventoryBase
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMCONTROLAREA')
            and   type = 'U')
   drop table LMCONTROLAREA
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMCONTROLAREAPROGRAM')
            and   type = 'U')
   drop table LMCONTROLAREAPROGRAM
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMCONTROLAREATRIGGER')
            and   type = 'U')
   drop table LMCONTROLAREATRIGGER
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('LMControlHistory')
            and   name  = 'Indx_Start'
            and   indid > 0
            and   indid < 255)
   drop index LMControlHistory.Indx_Start
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMControlHistory')
            and   type = 'U')
   drop table LMControlHistory
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('LMCurtailCustomerActivity')
            and   name  = 'Index_LMCrtCstActID'
            and   indid > 0
            and   indid < 255)
   drop index LMCurtailCustomerActivity.Index_LMCrtCstActID
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('LMCurtailCustomerActivity')
            and   name  = 'Index_LMCrtCstAckSt'
            and   indid > 0
            and   indid < 255)
   drop index LMCurtailCustomerActivity.Index_LMCrtCstAckSt
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMCurtailCustomerActivity')
            and   type = 'U')
   drop table LMCurtailCustomerActivity
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('LMCurtailProgramActivity')
            and   name  = 'Indx_LMCrtPrgActStTime'
            and   indid > 0
            and   indid < 255)
   drop index LMCurtailProgramActivity.Indx_LMCrtPrgActStTime
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMCurtailProgramActivity')
            and   type = 'U')
   drop table LMCurtailProgramActivity
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMCustomerEventBase')
            and   type = 'U')
   drop table LMCustomerEventBase
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMDirectCustomerList')
            and   type = 'U')
   drop table LMDirectCustomerList
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMDirectOperatorList')
            and   type = 'U')
   drop table LMDirectOperatorList
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMEnergyExchangeCustomerList')
            and   type = 'U')
   drop table LMEnergyExchangeCustomerList
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMEnergyExchangeCustomerReply')
            and   type = 'U')
   drop table LMEnergyExchangeCustomerReply
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMEnergyExchangeHourlyCustomer')
            and   type = 'U')
   drop table LMEnergyExchangeHourlyCustomer
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMEnergyExchangeHourlyOffer')
            and   type = 'U')
   drop table LMEnergyExchangeHourlyOffer
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMEnergyExchangeOfferRevision')
            and   type = 'U')
   drop table LMEnergyExchangeOfferRevision
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMEnergyExchangeProgramOffer')
            and   type = 'U')
   drop table LMEnergyExchangeProgramOffer
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMGROUPEMETCON')
            and   type = 'U')
   drop table LMGROUPEMETCON
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMGROUPVERSACOM')
            and   type = 'U')
   drop table LMGROUPVERSACOM
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMGroup')
            and   type = 'U')
   drop table LMGroup
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMGroupPoint')
            and   type = 'U')
   drop table LMGroupPoint
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMGroupRipple')
            and   type = 'U')
   drop table LMGroupRipple
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMHardwareActivity')
            and   type = 'U')
   drop table LMHardwareActivity
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMHardwareBase')
            and   type = 'U')
   drop table LMHardwareBase
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMHardwareConfiguration')
            and   type = 'U')
   drop table LMHardwareConfiguration
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMHardwareEvent')
            and   type = 'U')
   drop table LMHardwareEvent
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMMACSScheduleOperatorList')
            and   type = 'U')
   drop table LMMACSScheduleOperatorList
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMMacsScheduleCustomerList')
            and   type = 'U')
   drop table LMMacsScheduleCustomerList
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMPROGRAM')
            and   type = 'U')
   drop table LMPROGRAM
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMProgramControlWindow')
            and   type = 'U')
   drop table LMProgramControlWindow
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMProgramCurtailCustomerList')
            and   type = 'U')
   drop table LMProgramCurtailCustomerList
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMProgramCurtailment')
            and   type = 'U')
   drop table LMProgramCurtailment
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMProgramDirect')
            and   type = 'U')
   drop table LMProgramDirect
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMProgramDirectGear')
            and   type = 'U')
   drop table LMProgramDirectGear
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMProgramDirectGroup')
            and   type = 'U')
   drop table LMProgramDirectGroup
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMProgramEnergyExchange')
            and   type = 'U')
   drop table LMProgramEnergyExchange
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMProgramEvent')
            and   type = 'U')
   drop table LMProgramEvent
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMProgramWebPublishing')
            and   type = 'U')
   drop table LMProgramWebPublishing
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LOGIC')
            and   type = 'U')
   drop table LOGIC
go


if exists (select 1
            from  sysobjects
           where  id = object_id('MACROROUTE')
            and   type = 'U')
   drop table MACROROUTE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('MACSchedule')
            and   type = 'U')
   drop table MACSchedule
go


if exists (select 1
            from  sysobjects
           where  id = object_id('MACSimpleSchedule')
            and   type = 'U')
   drop table MACSimpleSchedule
go


if exists (select 1
            from  sysobjects
           where  id = object_id('NotificationDestination')
            and   type = 'U')
   drop table NotificationDestination
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('NotificationGroup')
            and   name  = 'Indx_NOTIFGRPNme'
            and   indid > 0
            and   indid < 255)
   drop index NotificationGroup.Indx_NOTIFGRPNme
go


if exists (select 1
            from  sysobjects
           where  id = object_id('NotificationGroup')
            and   type = 'U')
   drop table NotificationGroup
go


if exists (select 1
            from  sysobjects
           where  id = object_id('NotificationRecipient')
            and   type = 'U')
   drop table NotificationRecipient
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('OperatorLogin')
            and   name  = 'Index_OpLogNam'
            and   indid > 0
            and   indid < 255)
   drop index OperatorLogin.Index_OpLogNam
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('OperatorLogin')
            and   name  = 'Indx_OpLogPassword'
            and   indid > 0
            and   indid < 255)
   drop index OperatorLogin.Indx_OpLogPassword
go


if exists (select 1
            from  sysobjects
           where  id = object_id('OperatorLogin')
            and   type = 'U')
   drop table OperatorLogin
go


if exists (select 1
            from  sysobjects
           where  id = object_id('OperatorLoginGraphList')
            and   type = 'U')
   drop table OperatorLoginGraphList
go


if exists (select 1
            from  sysobjects
           where  id = object_id('OperatorSerialGroup')
            and   type = 'U')
   drop table OperatorSerialGroup
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PAOowner')
            and   type = 'U')
   drop table PAOowner
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('POINT')
            and   name  = 'Indx_PTNM_YUKPAOID'
            and   indid > 0
            and   indid < 255)
   drop index POINT.Indx_PTNM_YUKPAOID
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('POINT')
            and   name  = 'Indx_PointStGrpID'
            and   indid > 0
            and   indid < 255)
   drop index POINT.Indx_PointStGrpID
go


if exists (select 1
            from  sysobjects
           where  id = object_id('POINT')
            and   type = 'U')
   drop table POINT
go


if exists (select 1
            from  sysobjects
           where  id = object_id('POINTACCUMULATOR')
            and   type = 'U')
   drop table POINTACCUMULATOR
go


if exists (select 1
            from  sysobjects
           where  id = object_id('POINTANALOG')
            and   type = 'U')
   drop table POINTANALOG
go


if exists (select 1
            from  sysobjects
           where  id = object_id('POINTLIMITS')
            and   type = 'U')
   drop table POINTLIMITS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('POINTSTATUS')
            and   type = 'U')
   drop table POINTSTATUS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('POINTUNIT')
            and   type = 'U')
   drop table POINTUNIT
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PORTDIALUPMODEM')
            and   type = 'U')
   drop table PORTDIALUPMODEM
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PORTLOCALSERIAL')
            and   type = 'U')
   drop table PORTLOCALSERIAL
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PORTRADIOSETTINGS')
            and   type = 'U')
   drop table PORTRADIOSETTINGS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PORTSETTINGS')
            and   type = 'U')
   drop table PORTSETTINGS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PORTSTATISTICS')
            and   type = 'U')
   drop table PORTSTATISTICS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PORTTERMINALSERVER')
            and   type = 'U')
   drop table PORTTERMINALSERVER
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PORTTIMING')
            and   type = 'U')
   drop table PORTTIMING
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PointAlarming')
            and   type = 'U')
   drop table PointAlarming
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('RAWPOINTHISTORY')
            and   name  = 'Index_PointID'
            and   indid > 0
            and   indid < 255)
   drop index RAWPOINTHISTORY.Index_PointID
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('RAWPOINTHISTORY')
            and   name  = 'Indx_TimeStamp'
            and   indid > 0
            and   indid < 255)
   drop index RAWPOINTHISTORY.Indx_TimeStamp
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('RAWPOINTHISTORY')
            and   name  = 'Indx_RwPtHisPtIDTst'
            and   indid > 0
            and   indid < 255)
   drop index RAWPOINTHISTORY.Indx_RwPtHisPtIDTst
go


if exists (select 1
            from  sysobjects
           where  id = object_id('RAWPOINTHISTORY')
            and   type = 'U')
   drop table RAWPOINTHISTORY
go


if exists (select 1
            from  sysobjects
           where  id = object_id('REPEATERROUTE')
            and   type = 'U')
   drop table REPEATERROUTE
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('ROUTE')
            and   name  = 'Indx_RouteDevID'
            and   indid > 0
            and   indid < 255)
   drop index ROUTE.Indx_RouteDevID
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ROUTE')
            and   type = 'U')
   drop table ROUTE
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('STATE')
            and   name  = 'Indx_StateRaw'
            and   indid > 0
            and   indid < 255)
   drop index STATE.Indx_StateRaw
go


if exists (select 1
            from  sysobjects
           where  id = object_id('STATE')
            and   type = 'U')
   drop table STATE
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('STATEGROUP')
            and   name  = 'Indx_STATEGRP_Nme'
            and   indid > 0
            and   indid < 255)
   drop index STATEGROUP.Indx_STATEGRP_Nme
go


if exists (select 1
            from  sysobjects
           where  id = object_id('STATEGROUP')
            and   type = 'U')
   drop table STATEGROUP
go


if exists (select 1
            from  sysobjects
           where  id = object_id('SYSTEMLOG')
            and   type = 'U')
   drop table SYSTEMLOG
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ServiceCompany')
            and   type = 'U')
   drop table ServiceCompany
go


if exists (select 1
            from  sysobjects
           where  id = object_id('SiteInformation')
            and   type = 'U')
   drop table SiteInformation
go


if exists (select 1
            from  sysobjects
           where  id = object_id('Substation')
            and   type = 'U')
   drop table Substation
go


if exists (select 1
            from  sysobjects
           where  id = object_id('TEMPLATE')
            and   type = 'U')
   drop table TEMPLATE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('TEMPLATECOLUMNS')
            and   type = 'U')
   drop table TEMPLATECOLUMNS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('UNITMEASURE')
            and   type = 'U')
   drop table UNITMEASURE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('VERSACOMROUTE')
            and   type = 'U')
   drop table VERSACOMROUTE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('WorkOrderBase')
            and   type = 'U')
   drop table WorkOrderBase
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('YukonPAObject')
            and   name  = 'Indx_PAO'
            and   indid > 0
            and   indid < 255)
   drop index YukonPAObject.Indx_PAO
go


if exists (select 1
            from  sysobjects
           where  id = object_id('YukonPAObject')
            and   type = 'U')
   drop table YukonPAObject
go


/*==============================================================*/
/* Table : AccountSite                                          */
/*==============================================================*/
create table AccountSite (
AccountSiteID        numeric              not null,
SiteInformationID    numeric              null,
SiteNumber           varchar(40)          not null,
StreetAddressID      numeric              not null,
PropertyNotes        varchar(200)         null,
constraint PK_ACCOUNTSITE primary key  (AccountSiteID)
)
go


/*==============================================================*/
/* Table : AirConditioner                                       */
/*==============================================================*/
create table AirConditioner (
ApplianceID          numeric              not null,
TonageID             numeric              null,
TypeID               numeric              null,
constraint PK_AIRCONDITIONER primary key  (ApplianceID)
)
go


/*==============================================================*/
/* Table : AlarmCategory                                        */
/*==============================================================*/
create table AlarmCategory (
AlarmCategoryID      numeric              not null,
CategoryName         varchar(40)          not null,
NotificationGroupID  numeric              not null,
constraint PK_ALARMCATEGORYID primary key  (AlarmCategoryID)
)
go


insert into AlarmCategory values(1,'(none)',1);
insert into AlarmCategory values(2,'Category 1',1);
insert into AlarmCategory values(3,'Category 2',1);
insert into AlarmCategory values(4,'Category 3',1);
insert into AlarmCategory values(5,'Category 4',1);
insert into AlarmCategory values(6,'Category 5',1);
insert into AlarmCategory values(7,'Category 6',1);
insert into AlarmCategory values(8,'Category 7',1);
insert into AlarmCategory values(9,'Category 8',1);
insert into AlarmCategory values(10,'Category 9',1);
insert into AlarmCategory values(11,'Category 10',1);


insert into AlarmCategory values(12,'Category 11',1);
insert into AlarmCategory values(13,'Category 12',1);
insert into AlarmCategory values(14,'Category 13',1);
insert into AlarmCategory values(15,'Category 14',1);
insert into AlarmCategory values(16,'Category 15',1);
insert into AlarmCategory values(17,'Category 16',1);
insert into AlarmCategory values(18,'Category 17',1);
insert into AlarmCategory values(19,'Category 18',1);
insert into AlarmCategory values(20,'Category 19',1);
insert into AlarmCategory values(21,'Category 20',1);
insert into AlarmCategory values(22,'Category 21',1);
insert into AlarmCategory values(23,'Category 22',1);
insert into AlarmCategory values(24,'Category 23',1);
insert into AlarmCategory values(25,'Category 24',1);
insert into AlarmCategory values(26,'Category 25',1);
insert into AlarmCategory values(27,'Category 26',1);
insert into AlarmCategory values(28,'Category 27',1);
insert into AlarmCategory values(29,'Category 28',1);
insert into AlarmCategory values(30,'Category 29',1);
insert into AlarmCategory values(31,'Category 30',1);
insert into AlarmCategory values(32,'Category 31',1);

/*==============================================================*/
/* Table : ApplianceBase                                        */
/*==============================================================*/
create table ApplianceBase (
ApplianceID          numeric              not null,
AccountID            numeric              not null,
ApplianceCategoryID  numeric              not null,
LMProgramID          numeric              null,
Notes                varchar(100)         null,
constraint PK_APPLIANCEBASE primary key  (ApplianceID)
)
go


/*==============================================================*/
/* Table : ApplianceCategory                                    */
/*==============================================================*/
create table ApplianceCategory (
ApplianceCategoryID  numeric              not null,
CategoryID           numeric              null,
WebConfigurationID   numeric              null,
constraint PK_APPLIANCECATEGORY primary key  (ApplianceCategoryID)
)
go


/*==============================================================*/
/* Table : BillingFileFormats                                   */
/*==============================================================*/
create table BillingFileFormats (
FormatID             numeric              not null,
FormatType           varchar(30)          not null
)
go


/*==============================================================*/
/* Table : CALCBASE                                             */
/*==============================================================*/
create table CALCBASE (
POINTID              numeric              not null,
UPDATETYPE           varchar(16)          not null,
PERIODICRATE         numeric              not null,
constraint PK_CALCBASE primary key  (POINTID)
)
go


/*==============================================================*/
/* Index: Indx_ClcBaseUpdTyp                                    */
/*==============================================================*/
create   index Indx_ClcBaseUpdTyp on CALCBASE (
UPDATETYPE
)
go


/*==============================================================*/
/* Table : CALCCOMPONENT                                        */
/*==============================================================*/
create table CALCCOMPONENT (
PointID              numeric              not null,
COMPONENTORDER       numeric              not null,
COMPONENTTYPE        varchar(10)          not null,
COMPONENTPOINTID     numeric              not null,
OPERATION            varchar(10)          null,
CONSTANT             float                not null,
FUNCTIONNAME         varchar(20)          null,
constraint PK_CALCCOMPONENT primary key  (PointID, COMPONENTORDER)
)
go


/*==============================================================*/
/* Index: Indx_CalcCmpCmpType                                   */
/*==============================================================*/
create   index Indx_CalcCmpCmpType on CALCCOMPONENT (
COMPONENTTYPE
)
go


/*==============================================================*/
/* Table : CAPBANK                                              */
/*==============================================================*/
create table CAPBANK (
DEVICEID             numeric              not null,
OPERATIONALSTATE     varchar(8)           not null,
ControllerType       varchar(20)          not null,
CONTROLDEVICEID      numeric              not null,
CONTROLPOINTID       numeric              not null,
BANKSIZE             numeric              not null,
TypeOfSwitch         varchar(20)          not null,
SwitchManufacture    varchar(20)          not null,
MapLocationID        numeric              not null,
constraint PK_CAPBANK primary key  (DEVICEID)
)
go


/*==============================================================*/
/* Table : CAPCONTROLSUBSTATIONBUS                              */
/*==============================================================*/
create table CAPCONTROLSUBSTATIONBUS (
SubstationBusID      numeric              not null,
ControlMethod        varchar(20)          not null,
MAXDAILYOPERATION    numeric              not null,
MaxOperationDisableFlag char(1)              not null,
PEAKSETPOINT         float                not null,
OFFPEAKSETPOINT      float                not null,
PEAKSTARTTIME        numeric              not null,
PEAKSTOPTIME         numeric              not null,
CurrentVarLoadPointID numeric              not null,
CurrentWattLoadPointID numeric              not null,
BANDWIDTH            numeric              not null,
CONTROLINTERVAL      numeric              not null,
MINRESPONSETIME      numeric              not null,
MINCONFIRMPERCENT    numeric              not null,
FAILUREPERCENT       numeric              not null,
DAYSOFWEEK           char(8)              not null,
MapLocationID        numeric              not null,
constraint SYS_C0013476 primary key  (SubstationBusID)
)
go


/*==============================================================*/
/* Table : CARRIERROUTE                                         */
/*==============================================================*/
create table CARRIERROUTE (
ROUTEID              numeric              not null,
BUSNUMBER            numeric              not null,
CCUFIXBITS           numeric              not null,
CCUVARIABLEBITS      numeric              not null,
UserLocked           char(1)              not null,
ResetRptSettings     char(1)              not null
)
go


/*==============================================================*/
/* Table : CCFeederBankList                                     */
/*==============================================================*/
create table CCFeederBankList (
FeederID             numeric              not null,
DeviceID             numeric              not null,
ControlOrder         numeric              not null,
constraint PK_CCFEEDERBANKLIST primary key  (FeederID, DeviceID)
)
go


/*==============================================================*/
/* Table : CCFeederSubAssignment                                */
/*==============================================================*/
create table CCFeederSubAssignment (
SubStationBusID      numeric              not null,
FeederID             numeric              not null,
DisplayOrder         numeric              not null,
constraint PK_CCFEEDERSUBASSIGNMENT primary key  (SubStationBusID, FeederID)
)
go


/*==============================================================*/
/* Table : CICustContact                                        */
/*==============================================================*/
create table CICustContact (
DeviceID             numeric              not null,
ContactID            numeric              not null,
constraint PK_CICUSTCONTACT primary key  (ContactID, DeviceID)
)
go


/*==============================================================*/
/* Table : CICustomerBase                                       */
/*==============================================================*/
create table CICustomerBase (
DeviceID             numeric              not null,
AddressID            numeric              not null,
MainPhoneNumber      varchar(18)          not null,
MainFaxNumber        varchar(18)          not null,
CustFPL              float                not null,
PrimeContactID       numeric              not null,
CustTimeZone         varchar(6)           not null,
CurtailmentAgreement varchar(100)         not null,
CurtailAmount        float                not null,
constraint PK_CICUSTOMERBASE primary key  (DeviceID)
)
go


/*==============================================================*/
/* Table : COLUMNTYPE                                           */
/*==============================================================*/
create table COLUMNTYPE (
TYPENUM              numeric              not null,
NAME                 varchar(20)          not null,
constraint SYS_C0013414 primary key  (TYPENUM)
)
go


insert into columntype values (1, 'PointID');
insert into columntype values (2, 'PointName');
insert into columntype values (3, 'PointType');
insert into columntype values (4, 'PointState');
insert into columntype values (5, 'DeviceName');
insert into columntype values (6, 'DeviceType');
insert into columntype values (7, 'DeviceCurrentState');
insert into columntype values (8, 'DeviceID');
insert into columntype values (9, 'PointValue');
insert into columntype values (10, 'PointQuality');
insert into columntype values (11, 'PointTimeStamp');
insert into columntype values (12, 'UofM');

/*==============================================================*/
/* Table : COMMPORT                                             */
/*==============================================================*/
create table COMMPORT (
PORTID               numeric              not null,
ALARMINHIBIT         varchar(1)           not null,
COMMONPROTOCOL       varchar(8)           not null,
PERFORMTHRESHOLD     numeric              not null,
PERFORMANCEALARM     varchar(1)           not null,
SharedPortType       varchar(20)          not null,
SharedSocketNumber   numeric              not null,
constraint SYS_C0013112 primary key  (PORTID)
)
go


/*==============================================================*/
/* Table : CTIDatabase                                          */
/*==============================================================*/
create table CTIDatabase (
Version              varchar(6)           not null,
CTIEmployeeName      varchar(30)          not null,
DateApplied          datetime             null,
Notes                varchar(300)         null,
constraint PK_CTIDATABASE primary key  (Version)
)
go


insert into CTIDatabase values( '1.01', '(none)',  null, 'The initial database creation script created this.  The DateApplied is (null) because I can not find a way to set it in PowerBuilder!' );

insert into CTIDatabase values('1.02', 'Ryan', '06-JUN-01', 'Added EnergyExhange and FDRInterface tables, also made some columns larger.');

insert into CTIDatabase values('1.03', 'Ryan', '12-JUL-01', 'Remove the historical viewer from TDC, made device names unique, created some new tables for EnergyExchange');

insert into CTIDatabase values('1.04', 'Ryan', '28-JUL-01', 'Changed the structure of FDRINTERFACE and LMPROGRAMENERGYEXCHANGE.  Added the Ripple group to LM and some other tables.');

insert into CTIDatabase values('1.05', 'Ryan', '17-AUG-01', 'Readded the LMGroupRipple table and created the GenericMacro table. Change some data in the TDC tables.');

insert into CTIDatabase values('1.06', 'Ryan', '15-SEP-01', 'Added the sharing columns to the CommPort table and the Alternate Scan rate.');

insert into CTIDatabase values('2.00', 'Ryan', '6-NOV-01', 'Upgrade to Yukon 2.X tables.');

insert into CTIDatabase values('2.01', 'Ryan', '14-DEC-01', 'Added CommandTimeOut to PointStatus and some other minor changes.');

insert into CTIDatabase values('2.02', 'Ryan', '11-JAN-2002', 'Updated a TDC table to allow the new version of LoadManagement Client to work.');

insert into CTIDatabase values('2.03', 'Ryan', '15-JAN-2002', 'Changed the PK_MACROROUTE constraint on the MacroRoute table to use the RouteID and the RouteOrder.');

insert into CTIDatabase values('2.04', 'Ryan', '18-JAN-2002', 'Added the table CustomerBaseLinePoint and deleted EExchange from the display table.');

insert into CTIDatabase values('2.05', 'Ryan', '08-FEB-2002', 'Added DynamicCalcHistoracle table for the CalcHistorical App');

insert into CTIDatabase values('2.06', 'Ryan', '14-FEB-2002', 'Removed a column from the LMProgramDirect table and put it in the LMProgramDirectGear table');

insert into CTIDatabase values('2.07', 'Ryan', '8-MAR-2002', 'Added 2 new FDR Interfaces and remove LMPrograms and LMControlAreas from the device table');

insert into CTIDatabase values('2.08', 'Ryan', '14-MAR-2002', 'Added a LMGroupPoint, remove RecordControlHistory flag from LMGroup and added the DSM2IMPORT interface');

insert into CTIDatabase values('2.09', 'Ryan', '22-MAR-2002', 'Added a column to the LMControlHistory table');

insert into CTIDatabase values('2.10', 'Ryan', '05-APR-2002', 'Added 2 columns to the LMProgramDirectGear table');

insert into CTIDatabase values('2.11', 'Ryan', '12-APR-2002', 'Added the LMDirectOperatorList table');
insert into CTIDatabase values('2.12', 'Ryan', '15-MAY-2002', 'Added some new UOM, modified DyanmicLMControlArea and added BillingsFormat table');


/*==============================================================*/
/* Table : CallReportBase                                       */
/*==============================================================*/
create table CallReportBase (
CallID               numeric              not null,
CallNumber           varchar(20)          null,
CallTypeID           numeric              null,
DateTaken            datetime             null,
Description          varchar(300)         null,
AccountID            numeric              null,
CustomerID           numeric              null,
constraint PK_CALLREPORTBASE primary key  (CallID)
)
go


/*==============================================================*/
/* Table : CapControlFeeder                                     */
/*==============================================================*/
create table CapControlFeeder (
FeederID             numeric              not null,
PeakSetPoint         float                not null,
OffPeakSetPoint      float                not null,
Bandwidth            numeric              not null,
CurrentVarLoadPointID numeric              not null,
CurrentWattLoadPointID numeric              not null,
MapLocationID        numeric              not null,
constraint PK_CAPCONTROLFEEDER primary key  (FeederID)
)
go


/*==============================================================*/
/* Table : CommErrorHistory                                     */
/*==============================================================*/
create table CommErrorHistory (
CommErrorID          numeric              not null,
PAObjectID           numeric              not null,
DateTime             datetime             not null,
SOE_Tag              numeric              not null,
ErrorType            numeric              not null,
ErrorNumber          numeric              not null,
Command              varchar(50)          not null,
OutMessage           varchar(160)         not null,
InMessage            varchar(160)         not null,
constraint PK_COMMERRORHISTORY primary key  (CommErrorID)
)
go


/*==============================================================*/
/* Table : CstBaseCstContactMap                                 */
/*==============================================================*/
create table CstBaseCstContactMap (
CustomerID           numeric              not null,
CustomerContactID    numeric              not null,
constraint PK_CSTBASECSTCONTACTMAP primary key  (CustomerID, CustomerContactID)
)
go


/*==============================================================*/
/* Table : CustomerAccount                                      */
/*==============================================================*/
create table CustomerAccount (
AccountID            numeric              not null,
AccountSiteID        numeric              null,
AccountNumber        varchar(40)          null,
CustomerID           numeric              not null,
BillingAddressID     numeric              null,
AccountNotes         varchar(200)         null,
constraint PK_CUSTOMERACCOUNT primary key  (AccountID)
)
go


/*==============================================================*/
/* Table : CustomerAddress                                      */
/*==============================================================*/
create table CustomerAddress (
AddressID            numeric              not null,
LocationAddress1     varchar(40)          not null,
LocationAddress2     varchar(40)          not null,
CityName             varchar(32)          not null,
StateCode            char(2)              not null,
ZipCode              varchar(12)          not null,
constraint PK_CUSTOMERADDRESS primary key  (AddressID)
)
go


/*==============================================================*/
/* Table : CustomerBase                                         */
/*==============================================================*/
create table CustomerBase (
CustomerID           numeric              not null,
PrimaryContactID     numeric              not null,
CustomerType         varchar(30)          not null,
TimeZone             varchar(30)          null,
PAObjectID           numeric              null,
constraint PK_CUSTOMERBASE primary key  (CustomerID)
)
go


/*==============================================================*/
/* Table : CustomerBaseLine                                     */
/*==============================================================*/
create table CustomerBaseLine (
CustomerID           numeric              not null,
DaysUsed             numeric              not null,
PercentWindow        numeric              not null,
CalcDays             numeric              not null,
ExcludedWeekDays     char(7)              not null,
HolidaysUsed         numeric              not null,
constraint PK_CUSTOMERBASELINE primary key  (CustomerID)
)
go


/*==============================================================*/
/* Table : CustomerBaseLinePoint                                */
/*==============================================================*/
create table CustomerBaseLinePoint (
CustomerID           numeric              not null,
PointID              numeric              not null,
constraint PK_CUSTOMERBASELINEPOINT primary key  (CustomerID, PointID)
)
go


/*==============================================================*/
/* Table : CustomerContact                                      */
/*==============================================================*/
create table CustomerContact (
ContactID            numeric              not null,
ContFirstName        varchar(20)          not null,
ContLastName         varchar(32)          not null,
ContPhone1           varchar(14)          not null,
ContPhone2           varchar(14)          not null,
LocationID           numeric              not null,
LogInID              numeric              not null,
constraint PK_CUSTOMERCONTACT primary key  (ContactID)
)
go


insert into CustomerContact(contactID, contFirstName, contLastName, contPhone1, contPhone2, locationID,loginID)
values (-1,'(none)','(none)','(none)','(none)',0,-1)

/*==============================================================*/
/* Table : CustomerListEntry                                    */
/*==============================================================*/
create table CustomerListEntry (
EntryID              numeric              not null,
ListID               numeric              not null,
EntryOrder           numeric              null,
EntryText            varchar(50)          null,
YukonDefinition      varchar(20)          null,
constraint PK_CUSTOMERLISTENTRY primary key  (EntryID)
)
go


/*==============================================================*/
/* Table : CustomerLogin                                        */
/*==============================================================*/
create table CustomerLogin (
LogInID              numeric              not null,
UserName             varchar(20)          not null,
Password             varchar(20)          not null,
LoginType            varchar(25)          not null,
LoginCount           numeric              not null,
LastLogin            datetime             not null,
Status               varchar(20)          not null,
constraint PK_CUSTOMERLOGIN primary key  (LogInID)
)
go


insert into CustomerLogin(LogInID,UserName,Password,LoginType,LoginCount,LastLogin,Status)
values (-1,'(none)','(none)','(none)',0,'01-JAN-1990', 'Disabled');

/*==============================================================*/
/* Index: Indx_CstLogUsIDNm                                     */
/*==============================================================*/
create unique  index Indx_CstLogUsIDNm on CustomerLogin (
UserName
)
go


/*==============================================================*/
/* Index: Indx_CstLogPassword                                   */
/*==============================================================*/
create   index Indx_CstLogPassword on CustomerLogin (
Password
)
go


/*==============================================================*/
/* Table : CustomerSelectionList                                */
/*==============================================================*/
create table CustomerSelectionList (
ListID               numeric              not null,
Ordering             varchar(1)           null,
SelectionLabel       varchar(30)          null,
WhereIsList          varchar(100)         null,
ListName             varchar(40)          null,
UserUpdateAvailable  varchar(1)           null,
constraint PK_CUSTOMERSELECTIONLIST primary key  (ListID)
)
go


/*==============================================================*/
/* Table : CustomerWebConfiguration                             */
/*==============================================================*/
create table CustomerWebConfiguration (
ConfigurationID      numeric              not null,
LogoLocation         varchar(100)         null,
Description          varchar(200)         null,
AlternateDisplayName varchar(50)          null,
URL                  varchar(100)         null,
constraint PK_CUSTOMERWEBCONFIGURATION primary key  (ConfigurationID)
)
go


/*==============================================================*/
/* Table : CustomerWebSettings                                  */
/*==============================================================*/
create table CustomerWebSettings (
CustomerID           numeric              not null,
DatabaseAlias        varchar(40)          not null,
Logo                 varchar(60)          not null,
HomeURL              varchar(60)          not null,
constraint PK_CUSTOMERWEBSETTINGS primary key  (CustomerID)
)
go


/*==============================================================*/
/* Table : DEVICE                                               */
/*==============================================================*/
create table DEVICE (
DEVICEID             numeric              not null,
ALARMINHIBIT         varchar(1)           not null,
CONTROLINHIBIT       varchar(1)           not null,
constraint PK_DEV_DEVICEID primary key  (DEVICEID)
)
go


INSERT into device values (0, 'N', 'N');

/*==============================================================*/
/* Table : DEVICE2WAYFLAGS                                      */
/*==============================================================*/
create table DEVICE2WAYFLAGS (
DEVICEID             numeric              not null,
MONTHLYSTATS         varchar(1)           not null,
TWENTYFOURHOURSTATS  varchar(1)           not null,
HOURLYSTATS          varchar(1)           not null,
FAILUREALARM         varchar(1)           not null,
PERFORMANCETHRESHOLD numeric              not null,
PERFORMANCEALARM     varchar(1)           not null,
PERFORMANCETWENTYFOURALARM varchar(1)           not null
)
go


/*==============================================================*/
/* Table : DEVICECARRIERSETTINGS                                */
/*==============================================================*/
create table DEVICECARRIERSETTINGS (
DEVICEID             numeric              not null,
ADDRESS              numeric              not null
)
go


/*==============================================================*/
/* Table : DEVICECBC                                            */
/*==============================================================*/
create table DEVICECBC (
DEVICEID             numeric              not null,
SERIALNUMBER         numeric              not null,
ROUTEID              numeric              not null
)
go


/*==============================================================*/
/* Table : DEVICEDIALUPSETTINGS                                 */
/*==============================================================*/
create table DEVICEDIALUPSETTINGS (
DEVICEID             numeric              not null,
PHONENUMBER          varchar(40)          not null,
MINCONNECTTIME       numeric              not null,
MAXCONNECTTIME       numeric              not null,
LINESETTINGS         varchar(8)           not null,
BaudRate             numeric              not null
)
go


/*==============================================================*/
/* Table : DEVICEDIRECTCOMMSETTINGS                             */
/*==============================================================*/
create table DEVICEDIRECTCOMMSETTINGS (
DEVICEID             numeric              not null,
PORTID               numeric              not null
)
go


/*==============================================================*/
/* Table : DEVICEIDLCREMOTE                                     */
/*==============================================================*/
create table DEVICEIDLCREMOTE (
DEVICEID             numeric              not null,
ADDRESS              numeric              not null,
POSTCOMMWAIT         numeric              not null,
CCUAmpUseType        varchar(20)          not null
)
go


/*==============================================================*/
/* Table : DEVICEIED                                            */
/*==============================================================*/
create table DEVICEIED (
DEVICEID             numeric              not null,
PASSWORD             varchar(20)          not null,
SLAVEADDRESS         varchar(20)          not null
)
go


/*==============================================================*/
/* Table : DEVICELOADPROFILE                                    */
/*==============================================================*/
create table DEVICELOADPROFILE (
DEVICEID             numeric              not null,
LASTINTERVALDEMANDRATE numeric              not null,
LOADPROFILEDEMANDRATE numeric              not null,
LOADPROFILECOLLECTION varchar(4)           not null
)
go


/*==============================================================*/
/* Table : DEVICEMCTIEDPORT                                     */
/*==============================================================*/
create table DEVICEMCTIEDPORT (
DEVICEID             numeric              not null,
CONNECTEDIED         varchar(20)          not null,
IEDSCANRATE          numeric              not null,
DEFAULTDATACLASS     numeric              not null,
DEFAULTDATAOFFSET    numeric              not null,
PASSWORD             varchar(6)           not null,
REALTIMESCAN         varchar(1)           not null
)
go


/*==============================================================*/
/* Table : DEVICEMETERGROUP                                     */
/*==============================================================*/
create table DEVICEMETERGROUP (
DEVICEID             numeric              not null,
CollectionGroup      varchar(20)          not null,
TestCollectionGroup  varchar(20)          not null,
METERNUMBER          varchar(15)          not null,
BillingGroup         varchar(20)          not null
)
go


/*==============================================================*/
/* Table : DEVICEROUTES                                         */
/*==============================================================*/
create table DEVICEROUTES (
DEVICEID             numeric              not null,
ROUTEID              numeric              not null
)
go


/*==============================================================*/
/* Table : DEVICESCANRATE                                       */
/*==============================================================*/
create table DEVICESCANRATE (
DEVICEID             numeric              not null,
SCANTYPE             varchar(20)          not null,
INTERVALRATE         numeric              not null,
SCANGROUP            numeric              not null,
AlternateRate        numeric              not null,
constraint PK_DEVICESCANRATE primary key  (DEVICEID, SCANTYPE)
)
go


/*==============================================================*/
/* Table : DEVICESTATISTICS                                     */
/*==============================================================*/
create table DEVICESTATISTICS (
DEVICEID             numeric              not null,
STATISTICTYPE        varchar(16)          not null,
ATTEMPTS             numeric              not null,
COMLINEERRORS        numeric              not null,
SYSTEMERRORS         numeric              not null,
DLCERRORS            numeric              not null,
STARTDATETIME        datetime             not null,
STOPDATETIME         datetime             not null
)
go


/*==============================================================*/
/* Table : DEVICETAPPAGINGSETTINGS                              */
/*==============================================================*/
create table DEVICETAPPAGINGSETTINGS (
DEVICEID             numeric              not null,
PAGERNUMBER          varchar(20)          not null
)
go


/*==============================================================*/
/* Table : DISPLAY                                              */
/*==============================================================*/
create table DISPLAY (
DISPLAYNUM           numeric              not null,
NAME                 varchar(40)          not null,
TYPE                 varchar(40)          not null,
TITLE                varchar(30)          null,
DESCRIPTION          varchar(200)         null,
constraint SYS_C0013412 primary key  (DISPLAYNUM)
)
go


insert into display values(-1, 'All Categories', 'Scheduler Client', 'Metering And Control Scheduler', 'com.cannontech.macs.gui.Scheduler');
/**insert into display values(-2, 'All Areas', 'Cap Control Client', 'Cap Control', 'com.cannontech.cbc.gui.StrategyReceiver');**/
/**insert into display values(-3, 'All Control Areas', 'Load Management Client', 'Load Management', 'com.cannontech.loadcontrol.gui.LoadControlMainPanel');**/

insert into display values(99, 'Your Custom Display', 'Custom Displays', 'Edit This Display', 'This display is is used to show what a user created display looks like. You may edit this display to fit your own needs.');
insert into display values(1, 'Event Viewer', 'Alarms and Events', 'Current Event Viewer', 'This display will recieve current events as they happen in the system.');

/*********************************************************************************************************
This use to be for the Historical Data View,but that view was deleted and no longer used as of 7-11-2001
*********************************************************************************************************
insert into display values(2, 'Historical Viewer', 'Alarms and Events', 'Historical Event Viewer', 'This display will allow the user to select a range of dates and show the events that occured.');
********************************************************************************************************/

insert into display values(3, 'Raw Point Viewer', 'Alarms and Events', 'Current Raw Point Viewer', 'This display will recieve current raw point updates as they happen in the system.');
insert into display values(4, 'All Alarms', 'Alarms and Events', 'Global Alarm Viewer', 'This display will recieve all alarm events as they happen in the system.');
insert into display values(5, 'Priority 1 Alarms', 'Alarms and Events', 'Priority 1 Alarm Viewer', 'This display will recieve all priority 1 alarm events as they happen in the system.');
insert into display values(6, 'Priority 2 Alarms', 'Alarms and Events', 'Priority 2 Alarm Viewer', 'This display will recieve all priority 2 alarm events as they happen in the system.');
insert into display values(7, 'Priority 3 Alarms', 'Alarms and Events', 'Priority 3 Alarm Viewer', 'This display will recieve all priority 3 alarm events as they happen in the system.');
insert into display values(8, 'Priority 4 Alarms', 'Alarms and Events', 'Priority 4 Alarm Viewer', 'This display will recieve all priority 4 alarm events as they happen in the system.');
insert into display values(9, 'Priority 5 Alarms', 'Alarms and Events', 'Priority 5 Alarm Viewer', 'This display will recieve all priority 5 alarm events as they happen in the system.');
insert into display values(10, 'Priority 6 Alarms', 'Alarms and Events', 'Priority 6 Alarm Viewer', 'This display will recieve all priority 6 alarm events as they happen in the system.');
insert into display values(11, 'Priority 7 Alarms', 'Alarms and Events', 'Priority 7 Alarm Viewer', 'This display will recieve all priority 7 alarm events as they happen in the system.');
insert into display values(12, 'Priority 8 Alarms', 'Alarms and Events', 'Priority 8 Alarm Viewer', 'This display will recieve all priority 8 alarm events as they happen in the system.');
insert into display values(13, 'Priority 9 Alarms', 'Alarms and Events', 'Priority 9 Alarm Viewer', 'This display will recieve all priority 9 alarm events as they happen in the system.');

/*==============================================================*/
/* Index: Indx_DISPLAYNAME                                      */
/*==============================================================*/
create unique  index Indx_DISPLAYNAME on DISPLAY (
NAME
)
go


/*==============================================================*/
/* Table : DISPLAY2WAYDATA                                      */
/*==============================================================*/
create table DISPLAY2WAYDATA (
DISPLAYNUM           numeric              not null,
ORDERING             numeric              not null,
POINTID              numeric              not null,
constraint PK_DISPLAY2WAYDATA primary key  (DISPLAYNUM, POINTID)
)
go


/*==============================================================*/
/* Index: Index_DisNum                                          */
/*==============================================================*/
create   index Index_DisNum on DISPLAY2WAYDATA (
DISPLAYNUM
)
go


/*==============================================================*/
/* Table : DISPLAYCOLUMNS                                       */
/*==============================================================*/
create table DISPLAYCOLUMNS (
DISPLAYNUM           numeric              not null,
TITLE                varchar(50)          not null,
TYPENUM              numeric              not null,
ORDERING             numeric              not null,
WIDTH                numeric              not null,
constraint PK_DISPLAYCOLUMNS primary key  (DISPLAYNUM, TITLE)
)
go


insert into displaycolumns values(99, 'Device Name', 5, 1, 90 );
insert into displaycolumns values(99, 'Point Name', 2, 2, 90 );
insert into displaycolumns values(99, 'Value', 9, 3, 90 );
insert into displaycolumns values(99, 'Quality', 10, 4, 70 );
insert into displaycolumns values(99, 'Time Stamp', 11, 1, 140 );

insert into displaycolumns values(1, 'Time Stamp', 11, 1, 60 );
insert into displaycolumns values(1, 'Device Name', 5, 2, 70 );
insert into displaycolumns values(1, 'Point Name', 2, 3, 70 );
insert into displaycolumns values(1, 'Text Message', 12, 4, 180 );
insert into displaycolumns values(1, 'Additional Info', 10, 5, 180 );
insert into displaycolumns values(1, 'User Name', 8, 6, 35 );

/*********************************************************************************************************
This use to be for the Historical Data View,but that view was deleted and no longer used as of 7-11-2001
*********************************************************************************************************
insert into displaycolumns values(2, 'Time Stamp', 11, 1, 60 );
insert into displaycolumns values(2, 'Device Name', 5, 2, 70 );
insert into displaycolumns values(2, 'Point Name', 2, 3, 70 );
insert into displaycolumns values(2, 'Text Message', 12, 4, 180 );
insert into displaycolumns values(2, 'Additional Info', 10, 5, 180 );
insert into displaycolumns values(2, 'User Name', 8, 6, 35 );
*********************************************************************************************************/

insert into displaycolumns values(3, 'Time Stamp', 11, 1, 70 );
insert into displaycolumns values(3, 'Device Name', 5, 2, 60 );
insert into displaycolumns values(3, 'Point Name', 2, 3, 60 );
insert into displaycolumns values(3, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(3, 'Additional Info', 10, 5, 200 );
insert into displaycolumns values(3, 'User Name', 8, 6, 35 );

insert into displaycolumns values(4, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(4, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(4, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(4, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(4, 'User Name', 8, 5, 50 );

insert into displaycolumns values(5, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(5, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(5, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(5, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(5, 'User Name', 8, 5, 50 );
insert into displaycolumns values(6, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(6, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(6, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(6, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(6, 'User Name', 8, 5, 50 );
insert into displaycolumns values(7, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(7, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(7, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(7, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(7, 'User Name', 8, 5, 50 );
insert into displaycolumns values(8, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(8, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(8, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(8, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(8, 'User Name', 8, 5, 50 );
insert into displaycolumns values(9, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(9, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(9, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(9, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(9, 'User Name', 8, 5, 50 );
insert into displaycolumns values(10, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(10, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(10, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(10, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(10, 'User Name', 8, 5, 50 );
insert into displaycolumns values(11, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(11, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(11, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(11, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(11, 'User Name', 8, 5, 50 );
insert into displaycolumns values(12, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(12, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(12, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(12, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(12, 'User Name', 8, 5, 50 );
insert into displaycolumns values(13, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(13, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(13, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(13, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(13, 'User Name', 8, 5, 50 );

/*==============================================================*/
/* Table : DYNAMICACCUMULATOR                                   */
/*==============================================================*/
create table DYNAMICACCUMULATOR (
POINTID              numeric              null,
PREVIOUSPULSES       numeric              not null,
PRESENTPULSES        numeric              not null
)
go


/*==============================================================*/
/* Table : DYNAMICDEVICESCANDATA                                */
/*==============================================================*/
create table DYNAMICDEVICESCANDATA (
DEVICEID             numeric              null,
LASTFREEZETIME       datetime             not null,
PREVFREEZETIME       datetime             not null,
LASTLPTIME           datetime             not null,
LASTFREEZENUMBER     numeric              not null,
PREVFREEZENUMBER     numeric              not null,
NEXTSCAN0            datetime             not null,
NEXTSCAN1            datetime             not null,
NEXTSCAN2            datetime             not null,
NEXTSCAN3            datetime             not null
)
go


/*==============================================================*/
/* Table : DYNAMICPOINTDISPATCH                                 */
/*==============================================================*/
create table DYNAMICPOINTDISPATCH (
POINTID              numeric              not null,
TIMESTAMP            datetime             not null,
QUALITY              numeric              not null,
VALUE                float                not null,
TAGS                 numeric              not null,
NEXTARCHIVE          datetime             not null,
STALECOUNT           numeric              not null
)
go


/*==============================================================*/
/* Table : DateOfHoliday                                        */
/*==============================================================*/
create table DateOfHoliday (
HolidayScheduleID    numeric              not null,
HolidayName          varchar(20)          not null,
HolidayMonth         numeric              not null,
HolidayDay           numeric              not null,
HolidayYear          numeric              not null,
constraint PK_DATEOFHOLIDAY primary key  (HolidayScheduleID, HolidayName)
)
go


/*==============================================================*/
/* Table : DeviceWindow                                         */
/*==============================================================*/
create table DeviceWindow (
DeviceID             numeric              not null,
Type                 varchar(20)          not null,
WinOpen              numeric              not null,
WinClose             numeric              not null,
AlternateOpen        numeric              not null,
AlternateClose       numeric              not null,
constraint PK_DEVICEWINDOW primary key  (DeviceID, Type)
)
go


/*==============================================================*/
/* Table : DynamicCCCapBank                                     */
/*==============================================================*/
create table DynamicCCCapBank (
CapBankID            numeric              not null,
ControlStatus        numeric              not null,
CurrentDailyOperations numeric              not null,
LastStatusChangeTime datetime             not null,
TagsControlStatus    numeric              not null,
CTITimeStamp         datetime             not null,
constraint PK_DYNAMICCCCAPBANK primary key  (CapBankID)
)
go


/*==============================================================*/
/* Table : DynamicCCFeeder                                      */
/*==============================================================*/
create table DynamicCCFeeder (
FeederID             numeric              not null,
CurrentVarPointValue float                not null,
CurrentWattPointValue float                not null,
NewPointDataReceivedFlag char(1)              not null,
LastCurrentVarUpdateTime datetime             not null,
EstimatedVarPointValue float                not null,
CurrentDailyOperations numeric              not null,
RecentlyControlledFlag char(1)              not null,
LastOperationTime    datetime             not null,
VarValueBeforeControl float                not null,
LastCapBankDeviceID  numeric              not null,
BusOptimizedVarCategory numeric              not null,
BusOptimizedVarOffset float                not null,
CTITimeStamp         datetime             not null,
constraint PK_DYNAMICCCFEEDER primary key  (FeederID)
)
go


/*==============================================================*/
/* Table : DynamicCCSubstationBus                               */
/*==============================================================*/
create table DynamicCCSubstationBus (
SubstationBusID      numeric              not null,
CurrentVarPointValue float                not null,
CurrentWattPointValue float                not null,
NextCheckTime        datetime             not null,
NewPointDataReceivedFlag char(1)              not null,
BusUpdatedFlag       char(1)              not null,
LastCurrentVarUpdateTime datetime             not null,
EstimatedVarPointValue float                not null,
CurrentDailyOperations numeric              not null,
PeakTimeFlag         char(1)              not null,
RecentlyControlledFlag char(1)              not null,
LastOperationTime    datetime             not null,
VarValueBeforeControl float                not null,
LastFeederPAOid      numeric              not null,
LastFeederPosition   numeric              not null,
CTITimeStamp         datetime             not null,
constraint PK_DYNAMICCCSUBSTATIONBUS primary key  (SubstationBusID)
)
go


/*==============================================================*/
/* Table : DynamicCalcHistorical                                */
/*==============================================================*/
create table DynamicCalcHistorical (
PointID              numeric              not null,
LastUpdate           datetime             not null,
constraint PK_DYNAMICCALCHISTORICAL primary key  (PointID)
)
go


/*==============================================================*/
/* Table : DynamicLMControlArea                                 */
/*==============================================================*/
create table DynamicLMControlArea (
DeviceID             numeric              not null,
NextCheckTime        datetime             not null,
NewPointDataReceivedFlag char(1)              not null,
UpdatedFlag          char(1)              not null,
ControlAreaState     numeric              not null,
CurrentPriority      numeric              not null,
TimeStamp            datetime             not null,
CurrentDailyStartTime numeric              not null,
CurrentDailyStopTime numeric              not null,
constraint PK_DYNAMICLMCONTROLAREA primary key  (DeviceID)
)
go


/*==============================================================*/
/* Table : DynamicLMControlAreaTrigger                          */
/*==============================================================*/
create table DynamicLMControlAreaTrigger (
DeviceID             numeric              not null,
TriggerNumber        numeric              not null,
PointValue           float                not null,
LastPointValueTimeStamp datetime             not null,
PeakPointValue       float                not null,
LastPeakPointValueTimeStamp datetime             not null,
constraint PK_DYNAMICLMCONTROLAREATRIGGER primary key  (DeviceID, TriggerNumber)
)
go


/*==============================================================*/
/* Table : DynamicLMGroup                                       */
/*==============================================================*/
create table DynamicLMGroup (
DeviceID             numeric              not null,
GroupControlState    numeric              not null,
CurrentHoursDaily    numeric              not null,
CurrentHoursMonthly  numeric              not null,
CurrentHoursSeasonal numeric              not null,
CurrentHoursAnnually numeric              not null,
LastControlSent      datetime             not null,
TimeStamp            datetime             not null,
constraint PK_DYNAMICLMGROUP primary key  (DeviceID)
)
go


/*==============================================================*/
/* Table : DynamicLMProgram                                     */
/*==============================================================*/
create table DynamicLMProgram (
DeviceID             numeric              not null,
ProgramState         numeric              not null,
ReductionTotal       float                not null,
StartedControlling   datetime             not null,
LastControlSent      datetime             not null,
ManualControlReceivedFlag char(1)              not null,
TimeStamp            datetime             not null,
constraint PK_DYNAMICLMPROGRAM primary key  (DeviceID)
)
go


/*==============================================================*/
/* Table : DynamicLMProgramDirect                               */
/*==============================================================*/
create table DynamicLMProgramDirect (
DeviceID             numeric              not null,
CurrentGearNumber    numeric              not null,
LastGroupControlled  numeric              not null,
StartTime            datetime             not null,
StopTime             datetime             not null,
TimeStamp            datetime             not null,
constraint PK_DYNAMICLMPROGRAMDIRECT primary key  (DeviceID)
)
go


/*==============================================================*/
/* Table : ECToAccountMapping                                   */
/*==============================================================*/
create table ECToAccountMapping (
EnergyCompanyID      numeric              not null,
AccountID            numeric              not null,
constraint PK_ECTOACCOUNTMAPPING primary key  (EnergyCompanyID, AccountID)
)
go


/*==============================================================*/
/* Table : ECToCallReportMapping                                */
/*==============================================================*/
create table ECToCallReportMapping (
EnergyCompanyID      numeric              not null,
CallReportID         numeric              not null,
constraint PK_ECTOCALLREPORTMAPPING primary key  (EnergyCompanyID, CallReportID)
)
go


/*==============================================================*/
/* Table : ECToCustomerBaseMapping                              */
/*==============================================================*/
create table ECToCustomerBaseMapping (
EnergyCompanyID      numeric              not null,
CustomerID           numeric              not null,
constraint PK_ECTOCUSTOMERBASEMAPPING primary key  (EnergyCompanyID, CustomerID)
)
go


/*==============================================================*/
/* Table : ECToGenericMapping                                   */
/*==============================================================*/
create table ECToGenericMapping (
EnergyCompanyID      numeric              not null,
ItemID               numeric              not null,
MappingCategory      varchar(40)          not null,
constraint PK_ECTOGENERICMAPPING primary key  (EnergyCompanyID, ItemID, MappingCategory)
)
go


/*==============================================================*/
/* Table : ECToInventoryMapping                                 */
/*==============================================================*/
create table ECToInventoryMapping (
EnergyCompanyID      numeric              not null,
InventoryID          numeric              not null,
constraint PK_ECTOINVENTORYMAPPING primary key  (EnergyCompanyID, InventoryID)
)
go


/*==============================================================*/
/* Table : ECToLMCustomerEventMapping                           */
/*==============================================================*/
create table ECToLMCustomerEventMapping (
EnergyCompanyID      numeric              not null,
EventID              numeric              not null,
constraint PK_ECTOLMCUSTOMEREVENTMAPPING primary key  (EnergyCompanyID, EventID)
)
go


/*==============================================================*/
/* Table : ECToWorkOrderMapping                                 */
/*==============================================================*/
create table ECToWorkOrderMapping (
EnergyCompanyID      numeric              not null,
WorkOrderID          numeric              not null,
constraint PK_ECTOWORKORDERMAPPING primary key  (EnergyCompanyID, WorkOrderID)
)
go


/*==============================================================*/
/* Table : EnergyCompany                                        */
/*==============================================================*/
create table EnergyCompany (
EnergyCompanyID      numeric              not null,
Name                 varchar(60)          not null,
RouteID              numeric              not null,
constraint PK_ENERGYCOMPANY primary key  (EnergyCompanyID)
)
go


/*==============================================================*/
/* Index: Indx_EnCmpName                                        */
/*==============================================================*/
create unique  index Indx_EnCmpName on EnergyCompany (
Name
)
go


/*==============================================================*/
/* Table : EnergyCompanyCustomerList                            */
/*==============================================================*/
create table EnergyCompanyCustomerList (
EnergyCompanyID      numeric              null,
CustomerID           numeric              null
)
go


/*==============================================================*/
/* Table : EnergyCompanyOperatorLoginList                       */
/*==============================================================*/
create table EnergyCompanyOperatorLoginList (
EnergyCompanyID      numeric              null,
OperatorLoginID      numeric              null
)
go


/*==============================================================*/
/* Table : FDRInterface                                         */
/*==============================================================*/
create table FDRInterface (
InterfaceID          numeric              not null,
InterfaceName        varchar(30)          not null,
PossibleDirections   varchar(100)         not null,
hasDestination       char(1)              not null,
constraint PK_FDRINTERFACE primary key  (InterfaceID)
)
go


insert into FDRInterface values ( 1, 'INET', 'Send,Send for control,Receive,Receive for control', 't' );
insert into FDRInterface values ( 2, 'ACS', 'Send,Send for control,Receive,Receive for control', 'f' );
insert into FDRInterface values ( 3, 'VALMET', 'Send,Send for control,Receive,Receive for control', 'f' );
insert into FDRInterface values ( 4, 'CYGNET', 'Send,Send for control,Receive,Receive for control', 'f' );
insert into FDRInterface values ( 5, 'STEC', 'Receive', 'f' );
insert into FDRInterface values ( 6, 'RCCS', 'Send,Send for control,Receive,Receive for control', 't' );
insert into FDRInterface values ( 7, 'TRISTATE', 'Receive', 'f' );
insert into FDRInterface values ( 8, 'RDEX', 'Send,Send for control,Receive,Receive for control', 't' );
insert into FDRInterface values (9,'SYSTEM','Link Status','f');
insert into FDRInterface values (10,'DSM2IMPORT','Receive,Receive for control','f');


/*==============================================================*/
/* Table : FDRInterfaceOption                                   */
/*==============================================================*/
create table FDRInterfaceOption (
InterfaceID          numeric              not null,
OptionLabel          varchar(20)          not null,
Ordering             numeric              not null,
OptionType           varchar(8)           not null,
OptionValues         varchar(150)         not null,
constraint PK_FDRINTERFACEOPTION primary key  (InterfaceID, Ordering)
)
go


insert into FDRInterfaceOption values(1, 'Device', 1, 'Text', '(none)' );
insert into FDRInterfaceOption values(1, 'Point', 2, 'Text', '(none)' );
insert into FDRInterfaceOption values(1, 'Destination/Source', 3, 'Text', '(none)' );
insert into FDRInterfaceOption values(2, 'Category', 1, 'Combo', 'PSEUDO,REAL' );
insert into FDRInterfaceOption values(2, 'Remote', 2, 'Text', '(none)' );
insert into FDRInterfaceOption values(2, 'Point', 3, 'Text', '(none)' );
insert into FDRInterfaceOption values(3, 'Point', 1, 'Text', '(none)' );
insert into FDRInterfaceOption values(4, 'PointID', 1, 'Text', '(none)' );
insert into FDRInterfaceOption values(5, 'Point', 1, 'Combo', 'SYSTEM LOAD,STEC LOAD' );
insert into FDRInterfaceOption values(6, 'Device', 1, 'Text', '(none)' );
insert into FDRInterfaceOption values(6, 'Point', 2, 'Text', '(none)' );
insert into FDRInterfaceOption values(6, 'Destination/Source', 3, 'Text', '(none)' );
insert into FDRInterfaceOption values(7, 'Point', 1, 'Combo', 'SYSTEM LOAD,30 MINUTE AVG' );
insert into FDRInterfaceOption values(8, 'Translation', 1, 'Text', '(none)' );
insert into FDRInterfaceOption values(8, 'Destination/Source', 2, 'Text', '(none)' );
insert into FDRInterfaceOption values(9,'Client',1,'Text','(none)');
insert into FDRInterfaceOption values(10,'Point',1,'Text','(none)');

/*==============================================================*/
/* Table : FDRTRANSLATION                                       */
/*==============================================================*/
create table FDRTRANSLATION (
POINTID              numeric              not null,
DIRECTIONTYPE        varchar(20)          not null,
InterfaceType        varchar(20)          not null,
DESTINATION          varchar(20)          not null,
TRANSLATION          varchar(100)         not null
)
go


/*==============================================================*/
/* Index: Indx_FdrTransIntTyp                                   */
/*==============================================================*/
create   index Indx_FdrTransIntTyp on FDRTRANSLATION (
InterfaceType
)
go


/*==============================================================*/
/* Index: Indx_FdrTrnsIntTypDir                                 */
/*==============================================================*/
create   index Indx_FdrTrnsIntTypDir on FDRTRANSLATION (
DIRECTIONTYPE,
InterfaceType
)
go


/*==============================================================*/
/* Table : GRAPHDATASERIES                                      */
/*==============================================================*/
create table GRAPHDATASERIES (
GRAPHDATASERIESID    numeric              not null,
GRAPHDEFINITIONID    numeric              not null,
POINTID              numeric              not null,
Label                varchar(40)          not null,
Axis                 char(1)              not null,
Color                numeric              not null,
Type                 varchar(12)          not null,
constraint SYS_GrphDserID primary key  (GRAPHDATASERIESID)
)
go


/*==============================================================*/
/* Index: Indx_GrpDSerPtID                                      */
/*==============================================================*/
create   index Indx_GrpDSerPtID on GRAPHDATASERIES (
POINTID
)
go


/*==============================================================*/
/* Table : GRAPHDEFINITION                                      */
/*==============================================================*/
create table GRAPHDEFINITION (
GRAPHDEFINITIONID    numeric              not null,
NAME                 varchar(40)          not null,
AutoScaleTimeAxis    char(1)              not null,
AutoScaleLeftAxis    char(1)              not null,
AutoScaleRightAxis   char(1)              not null,
STARTDATE            datetime             not null,
STOPDATE             datetime             not null,
LeftMin              float                not null,
LeftMax              float                not null,
RightMin             float                not null,
RightMax             float                not null,
Type                 char(1)              not null,
constraint SYS_C0015109 primary key  (GRAPHDEFINITIONID)
)
go


/*==============================================================*/
/* Index: Indx_GrNam                                            */
/*==============================================================*/
create unique  index Indx_GrNam on GRAPHDEFINITION (
NAME
)
go


/*==============================================================*/
/* Table : GenericMacro                                         */
/*==============================================================*/
create table GenericMacro (
OwnerID              numeric              not null,
ChildID              numeric              not null,
ChildOrder           numeric              not null,
MacroType            varchar(20)          not null,
constraint PK_GENERICMACRO primary key  (OwnerID, ChildOrder, MacroType)
)
go


/*==============================================================*/
/* Table : GraphCustomerList                                    */
/*==============================================================*/
create table GraphCustomerList (
GraphDefinitionID    numeric              not null,
CustomerID           numeric              not null,
CustomerOrder        numeric              not null,
constraint PK_GRAPHCUSTOMERLIST primary key  (GraphDefinitionID, CustomerID)
)
go


/*==============================================================*/
/* Table : HolidaySchedule                                      */
/*==============================================================*/
create table HolidaySchedule (
HolidayScheduleID    numeric              not null,
HolidayScheduleName  varchar(40)          not null,
constraint PK_HOLIDAYSCHEDULE primary key  (HolidayScheduleID)
)
go


insert into HolidaySchedule values( 0, 'Empty Holiday Schedule' );

/*==============================================================*/
/* Index: Indx_HolSchName                                       */
/*==============================================================*/
create unique  index Indx_HolSchName on HolidaySchedule (
HolidayScheduleName
)
go


/*==============================================================*/
/* Table : InventoryBase                                        */
/*==============================================================*/
create table InventoryBase (
InventoryID          numeric              not null,
AccountID            numeric              null,
InstallationCompanyID numeric              null,
CategoryID           numeric              not null,
ReceiveDate          datetime             null,
InstallDate          datetime             null,
RemoveDate           datetime             null,
AlternateTrackingNumber varchar(40)          null,
VoltageID            numeric              null,
Notes                varchar(100)         null,
DeviceID             numeric              null,
constraint PK_INVENTORYBASE primary key  (InventoryID)
)
go


/*==============================================================*/
/* Table : LMCONTROLAREA                                        */
/*==============================================================*/
create table LMCONTROLAREA (
DEVICEID             numeric              not null,
DEFOPERATIONALSTATE  varchar(20)          not null,
CONTROLINTERVAL      numeric              not null,
MINRESPONSETIME      numeric              not null,
DEFDAILYSTARTTIME    numeric              not null,
DEFDAILYSTOPTIME     numeric              not null,
REQUIREALLTRIGGERSACTIVEFLAG varchar(1)           not null,
constraint PK_LMCONTROLAREA primary key  (DEVICEID)
)
go


/*==============================================================*/
/* Table : LMCONTROLAREAPROGRAM                                 */
/*==============================================================*/
create table LMCONTROLAREAPROGRAM (
DEVICEID             numeric              not null,
LMPROGRAMDEVICEID    numeric              not null,
USERORDER            numeric              not null,
STOPORDER            numeric              not null,
DEFAULTPRIORITY      numeric              not null
)
go


/*==============================================================*/
/* Table : LMCONTROLAREATRIGGER                                 */
/*==============================================================*/
create table LMCONTROLAREATRIGGER (
DEVICEID             numeric              not null,
TRIGGERNUMBER        numeric              not null,
TRIGGERTYPE          varchar(20)          not null,
POINTID              numeric              not null,
NORMALSTATE          numeric              not null,
THRESHOLD            float                not null,
PROJECTIONTYPE       varchar(14)          not null,
PROJECTIONPOINTS     numeric              not null,
PROJECTAHEADDURATION numeric              not null,
THRESHOLDKICKPERCENT numeric              not null,
MINRESTOREOFFSET     float                not null,
PEAKPOINTID          numeric              not null,
constraint PK_LMCONTROLAREATRIGGER primary key  (DEVICEID, TRIGGERNUMBER)
)
go


/*==============================================================*/
/* Table : LMControlHistory                                     */
/*==============================================================*/
create table LMControlHistory (
LMCtrlHistID         numeric              not null,
PAObjectID           numeric              not null,
StartDateTime        datetime             not null,
SOE_Tag              numeric              not null,
ControlDuration      numeric              not null,
ControlType          varchar(20)          not null,
CurrentDailyTime     numeric              not null,
CurrentMonthlyTime   numeric              not null,
CurrentSeasonalTime  numeric              not null,
CurrentAnnualTime    numeric              not null,
ActiveRestore        char(1)              not null,
ReductionValue       float                not null,
StopDateTime         datetime             not null,
constraint PK_LMCONTROLHISTORY primary key  (LMCtrlHistID)
)
go


/*==============================================================*/
/* Index: Indx_Start                                            */
/*==============================================================*/
create   index Indx_Start on LMControlHistory (
StartDateTime
)
go


/*==============================================================*/
/* Table : LMCurtailCustomerActivity                            */
/*==============================================================*/
create table LMCurtailCustomerActivity (
CustomerID           numeric              not null,
CurtailReferenceID   numeric              not null,
AcknowledgeStatus    varchar(30)          not null,
AckDateTime          datetime             not null,
IPAddressOfAckUser   varchar(15)          not null,
UserIDName           varchar(40)          not null,
NameOfAckPerson      varchar(40)          not null,
CurtailmentNotes     varchar(120)         not null,
CurrentPDL           float                not null,
AckLateFlag          char(1)              not null
)
go


/*==============================================================*/
/* Index: Index_LMCrtCstActID                                   */
/*==============================================================*/
create   index Index_LMCrtCstActID on LMCurtailCustomerActivity (
CustomerID
)
go


/*==============================================================*/
/* Index: Index_LMCrtCstAckSt                                   */
/*==============================================================*/
create   index Index_LMCrtCstAckSt on LMCurtailCustomerActivity (
AcknowledgeStatus
)
go


/*==============================================================*/
/* Table : LMCurtailProgramActivity                             */
/*==============================================================*/
create table LMCurtailProgramActivity (
DeviceID             numeric              not null,
CurtailReferenceID   numeric              not null,
ActionDateTime       datetime             not null,
NotificationDateTime datetime             not null,
CurtailmentStartTime datetime             not null,
CurtailmentStopTime  datetime             not null,
RunStatus            varchar(20)          not null,
AdditionalInfo       varchar(100)         not null,
constraint PK_LMCURTAILPROGRAMACTIVITY primary key  (CurtailReferenceID)
)
go


/*==============================================================*/
/* Index: Indx_LMCrtPrgActStTime                                */
/*==============================================================*/
create   index Indx_LMCrtPrgActStTime on LMCurtailProgramActivity (
CurtailmentStartTime
)
go


/*==============================================================*/
/* Table : LMCustomerEventBase                                  */
/*==============================================================*/
create table LMCustomerEventBase (
EventID              numeric              not null,
EventTypeID          numeric              not null,
ActionID             numeric              not null,
EventDateTime        datetime             null,
Notes                varchar(100)         null,
AuthorizedBy         varchar(40)          null,
constraint PK_LMCUSTOMEREVENTBASE primary key  (EventID)
)
go


/*==============================================================*/
/* Table : LMDirectCustomerList                                 */
/*==============================================================*/
create table LMDirectCustomerList (
ProgramID            numeric              not null,
CustomerID           numeric              not null,
constraint PK_LMDIRECTCUSTOMERLIST primary key  (ProgramID, CustomerID)
)
go


/*==============================================================*/
/* Table : LMDirectOperatorList                                 */
/*==============================================================*/
create table LMDirectOperatorList (
ProgramID            numeric              not null,
OperatorLoginID      numeric              not null,
constraint PK_LMDIRECTOPERATORLIST primary key  (ProgramID, OperatorLoginID)
)
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


/*==============================================================*/
/* Table : LMGROUPEMETCON                                       */
/*==============================================================*/
create table LMGROUPEMETCON (
DEVICEID             numeric              not null,
GOLDADDRESS          numeric              not null,
SILVERADDRESS        numeric              not null,
ADDRESSUSAGE         char(1)              not null,
RELAYUSAGE           char(1)              not null,
ROUTEID              numeric              not null
)
go


/*==============================================================*/
/* Table : LMGROUPVERSACOM                                      */
/*==============================================================*/
create table LMGROUPVERSACOM (
DEVICEID             numeric              not null,
ROUTEID              numeric              not null,
UTILITYADDRESS       numeric              not null,
SECTIONADDRESS       numeric              not null,
CLASSADDRESS         numeric              not null,
DIVISIONADDRESS      numeric              not null,
ADDRESSUSAGE         char(4)              not null,
RELAYUSAGE           char(7)              not null,
SerialAddress        varchar(15)          not null
)
go


/*==============================================================*/
/* Table : LMGroup                                              */
/*==============================================================*/
create table LMGroup (
DeviceID             numeric              not null,
KWCapacity           float                not null,
constraint PK_LMGROUP primary key  (DeviceID)
)
go


/*==============================================================*/
/* Table : LMGroupPoint                                         */
/*==============================================================*/
create table LMGroupPoint (
DEVICEID             numeric              not null,
DeviceIDUsage        numeric              not null,
PointIDUsage         numeric              not null,
StartControlRawState numeric              not null,
constraint PK_LMGROUPPOINT primary key  (DEVICEID)
)
go


/*==============================================================*/
/* Table : LMGroupRipple                                        */
/*==============================================================*/
create table LMGroupRipple (
DeviceID             numeric              not null,
RouteID              numeric              not null,
ShedTime             numeric              not null,
ControlValue         char(50)             not null,
RestoreValue         char(50)             not null,
constraint PK_LMGROUPRIPPLE primary key  (DeviceID)
)
go


/*==============================================================*/
/* Table : LMHardwareActivity                                   */
/*==============================================================*/
create table LMHardwareActivity (
EventID              numeric              not null,
InventoryID          numeric              null,
EventDateTime        datetime             null,
Notes                varchar(100)         null,
ActionID             numeric              null,
constraint PK_LMHARDWAREACTIVITY primary key  (EventID)
)
go


/*==============================================================*/
/* Table : LMHardwareBase                                       */
/*==============================================================*/
create table LMHardwareBase (
InventoryID          numeric              not null,
ManufacturerSerialNumber varchar(30)          null,
LMHardwareTypeID     numeric              not null,
constraint PK_LMHARDWAREBASE primary key  (InventoryID)
)
go


/*==============================================================*/
/* Table : LMHardwareConfiguration                              */
/*==============================================================*/
create table LMHardwareConfiguration (
InventoryID          numeric              not null,
ApplianceID          numeric              not null,
AddressingGroupID    numeric              null,
constraint PK_LMHARDWARECONFIGURATION primary key  (InventoryID, ApplianceID)
)
go


/*==============================================================*/
/* Table : LMHardwareEvent                                      */
/*==============================================================*/
create table LMHardwareEvent (
EventID              numeric              not null,
InventoryID          numeric              not null,
constraint PK_LMHARDWAREEVENT primary key  (EventID)
)
go


/*==============================================================*/
/* Table : LMMACSScheduleOperatorList                           */
/*==============================================================*/
create table LMMACSScheduleOperatorList (
ScheduleID           numeric              null,
OperatorLoginID      numeric              null
)
go


/*==============================================================*/
/* Table : LMMacsScheduleCustomerList                           */
/*==============================================================*/
create table LMMacsScheduleCustomerList (
ScheduleID           numeric              not null,
LMCustomerDeviceID   numeric              not null,
CustomerOrder        numeric              not null,
constraint PK_LMMACSSCHEDULECUSTOMERLIST primary key  (ScheduleID, LMCustomerDeviceID)
)
go


/*==============================================================*/
/* Table : LMPROGRAM                                            */
/*==============================================================*/
create table LMPROGRAM (
DEVICEID             numeric              not null,
CONTROLTYPE          varchar(20)          not null,
AVAILABLESEASONS     varchar(4)           not null,
AVAILABLEWEEKDAYS    varchar(8)           not null,
MAXHOURSDAILY        numeric              not null,
MAXHOURSMONTHLY      numeric              not null,
MAXHOURSSEASONAL     numeric              not null,
MAXHOURSANNUALLY     numeric              not null,
MINACTIVATETIME      numeric              not null,
MINRESTARTTIME       numeric              not null,
constraint PK_LMPROGRAM primary key  (DEVICEID)
)
go


/*==============================================================*/
/* Table : LMProgramControlWindow                               */
/*==============================================================*/
create table LMProgramControlWindow (
DeviceID             numeric              not null,
WindowNumber         numeric              not null,
AvailableStartTime   numeric              not null,
AvailableStopTime    numeric              not null
)
go


/*==============================================================*/
/* Table : LMProgramCurtailCustomerList                         */
/*==============================================================*/
create table LMProgramCurtailCustomerList (
DeviceID             numeric              not null,
LMCustomerDeviceID   numeric              not null,
CustomerOrder        numeric              not null,
RequireAck           char(1)              not null,
constraint PK_LMPROGRAMCURTAILCUSTOMERLIS primary key  (LMCustomerDeviceID, DeviceID)
)
go


/*==============================================================*/
/* Table : LMProgramCurtailment                                 */
/*==============================================================*/
create table LMProgramCurtailment (
DeviceID             numeric              not null,
MinNotifyTime        numeric              not null,
Heading              varchar(40)          not null,
MessageHeader        varchar(160)         not null,
MessageFooter        varchar(160)         not null,
AckTimeLimit         numeric              not null,
CanceledMsg          varchar(80)          not null default 'THIS CURTAILMENT HAS BEEN CANCELED, PLEASE DISREGARD.',
StoppedEarlyMsg      varchar(80)          not null default 'THIS CURTAILMENT HAS STOPPED EARLY, YOU MAY RESUME NORMAL OPERATIONS.',
constraint PK_LMPROGRAMCURTAILMENT primary key  (DeviceID)
)
go


/*==============================================================*/
/* Table : LMProgramDirect                                      */
/*==============================================================*/
create table LMProgramDirect (
DeviceID             numeric              not null,
constraint PK_LMPROGRAMDIRECT primary key  (DeviceID)
)
go


/*==============================================================*/
/* Table : LMProgramDirectGear                                  */
/*==============================================================*/
create table LMProgramDirectGear (
DeviceID             numeric              not null,
GearName             varchar(30)          not null,
GearNumber           numeric              not null,
ControlMethod        varchar(30)          not null,
MethodRate           numeric              not null,
MethodPeriod         numeric              not null,
MethodRateCount      numeric              not null,
CycleRefreshRate     numeric              not null,
MethodStopType       varchar(20)          not null,
ChangeCondition      varchar(24)          not null,
ChangeDuration       numeric              not null,
ChangePriority       numeric              not null,
ChangeTriggerNumber  numeric              not null,
ChangeTriggerOffset  float                not null,
PercentReduction     numeric              not null,
GroupSelectionMethod varchar(30)          not null,
MethodOptionType     varchar(30)          not null,
MethodOptionMax      numeric              not null,
constraint PK_LMPROGRAMDIRECTGEAR primary key  (DeviceID, GearNumber)
)
go


/*==============================================================*/
/* Table : LMProgramDirectGroup                                 */
/*==============================================================*/
create table LMProgramDirectGroup (
DeviceID             numeric              not null,
LMGroupDeviceID      numeric              not null,
GroupOrder           numeric              not null
)
go


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


/*==============================================================*/
/* Table : LMProgramEvent                                       */
/*==============================================================*/
create table LMProgramEvent (
EventID              numeric              not null,
AccountID            numeric              not null,
LMProgramID          numeric              not null,
constraint PK_LMPROGRAMEVENT primary key  (EventID)
)
go


/*==============================================================*/
/* Table : LMProgramWebPublishing                               */
/*==============================================================*/
create table LMProgramWebPublishing (
ApplianceCategoryID  numeric              not null,
LMProgramID          numeric              not null,
WebsettingsID        numeric              null,
constraint PK_LMPROGRAMWEBPUBLISHING primary key  (ApplianceCategoryID, LMProgramID)
)
go


/*==============================================================*/
/* Table : LOGIC                                                */
/*==============================================================*/
create table LOGIC (
LOGICID              numeric              not null,
LOGICNAME            varchar(20)          not null,
PERIODICRATE         numeric              not null,
STATEFLAG            varchar(10)          not null,
SCRIPTNAME           varchar(20)          not null,
constraint SYS_C0013445 primary key  (LOGICID)
)
go


/*==============================================================*/
/* Table : MACROROUTE                                           */
/*==============================================================*/
create table MACROROUTE (
ROUTEID              numeric              not null,
SINGLEROUTEID        numeric              not null,
ROUTEORDER           numeric              not null,
constraint PK_MACROROUTE primary key  (ROUTEID, ROUTEORDER)
)
go


/*==============================================================*/
/* Table : MACSchedule                                          */
/*==============================================================*/
create table MACSchedule (
ScheduleID           numeric              not null,
CategoryName         varchar(50)          not null,
HolidayScheduleID    numeric              null,
CommandFile          varchar(180)         null,
CurrentState         varchar(12)          not null,
StartPolicy          varchar(20)          not null,
StopPolicy           varchar(20)          not null,
LastRunTime          datetime             null,
LastRunStatus        varchar(12)          null,
StartDay             numeric              null,
StartMonth           numeric              null,
StartYear            numeric              null,
StartTime            varchar(8)           null,
StopTime             varchar(8)           null,
ValidWeekDays        char(8)              null,
Duration             numeric              null,
ManualStartTime      datetime             null,
ManualStopTime       datetime             null,
constraint PK_MACSCHEDULE primary key  (ScheduleID)
)
go


/*==============================================================*/
/* Table : MACSimpleSchedule                                    */
/*==============================================================*/
create table MACSimpleSchedule (
ScheduleID           numeric              not null,
TargetSelect         varchar(40)          null,
StartCommand         varchar(120)         null,
StopCommand          varchar(120)         null,
RepeatInterval       numeric              null,
constraint PK_MACSIMPLESCHEDULE primary key  (ScheduleID)
)
go


/*==============================================================*/
/* Table : NotificationDestination                              */
/*==============================================================*/
create table NotificationDestination (
DestinationOrder     numeric              not null,
NotificationGroupID  numeric              not null,
RecipientID          numeric              not null,
constraint PKey_NotDestID primary key  (NotificationGroupID, DestinationOrder)
)
go


/*==============================================================*/
/* Table : NotificationGroup                                    */
/*==============================================================*/
create table NotificationGroup (
NotificationGroupID  numeric              not null,
GroupName            varchar(40)          not null,
EmailSubject         varchar(60)          not null,
EmailFromAddress     varchar(100)         not null,
EmailMessage         varchar(160)         not null,
NumericPagerMessage  varchar(14)          not null,
DisableFlag          char(1)              not null,
constraint PK_NOTIFICATIONGROUP primary key  (NotificationGroupID)
)
go


insert into notificationgroup values(1,'(none)','(none)','(none)','(none)','(none)','N');

/*==============================================================*/
/* Index: Indx_NOTIFGRPNme                                      */
/*==============================================================*/
create unique  index Indx_NOTIFGRPNme on NotificationGroup (
GroupName
)
go


/*==============================================================*/
/* Table : NotificationRecipient                                */
/*==============================================================*/
create table NotificationRecipient (
RecipientID          numeric              not null,
RecipientName        varchar(30)          not null,
EmailAddress         varchar(100)         not null,
EmailSendType        numeric              not null,
PagerNumber          varchar(20)          not null,
DisableFlag          char(1)              not null,
RecipientType        varchar(20)          not null,
constraint PKey_GrpRecID primary key  (RecipientID)
)
go


insert into NotificationRecipient values(0,'(none)','(none)',1,'(none)','N', 'EMAIL');

/*==============================================================*/
/* Table : OperatorLogin                                        */
/*==============================================================*/
create table OperatorLogin (
LoginID              numeric              not null,
Username             varchar(30)          not null,
Password             varchar(20)          null,
LoginType            varchar(20)          not null,
LoginCount           numeric              not null,
LastLogin            datetime             not null,
Status               varchar(20)          not null,
constraint PK_OPERATORLOGIN primary key  (LoginID)
)
go


/*==============================================================*/
/* Index: Index_OpLogNam                                        */
/*==============================================================*/
create unique  index Index_OpLogNam on OperatorLogin (
Username
)
go


/*==============================================================*/
/* Index: Indx_OpLogPassword                                    */
/*==============================================================*/
create   index Indx_OpLogPassword on OperatorLogin (
Password
)
go


/*==============================================================*/
/* Table : OperatorLoginGraphList                               */
/*==============================================================*/
create table OperatorLoginGraphList (
OperatorLoginID      numeric              null,
GraphDefinitionID    numeric              null
)
go


/*==============================================================*/
/* Table : OperatorSerialGroup                                  */
/*==============================================================*/
create table OperatorSerialGroup (
LoginID              numeric              not null,
LMGroupID            numeric              not null,
constraint PK_OpSerGrp primary key  (LoginID)
)
go


/*==============================================================*/
/* Table : PAOowner                                             */
/*==============================================================*/
create table PAOowner (
OwnerID              numeric              not null,
ChildID              numeric              not null,
constraint PK_PAOOWNER primary key  (OwnerID, ChildID)
)
go


/*==============================================================*/
/* Table : POINT                                                */
/*==============================================================*/
create table POINT (
POINTID              numeric              not null,
POINTTYPE            varchar(20)          not null,
POINTNAME            varchar(60)          not null,
PAObjectID           numeric              not null,
LOGICALGROUP         varchar(14)          not null,
STATEGROUPID         numeric              not null,
SERVICEFLAG          varchar(1)           not null,
ALARMINHIBIT         varchar(1)           not null,
PSEUDOFLAG           varchar(1)           not null,
POINTOFFSET          numeric              not null,
ARCHIVETYPE          varchar(12)          not null,
ARCHIVEINTERVAL      numeric              not null,
constraint Key_PT_PTID primary key  (POINTID)
)
go


INSERT into point  values (0,   'System', 'System Point', 0, 'Default', 0, 'N', 'N', 'S', 0  ,'None', 0);
INSERT into point  values (-1,  'System', 'Porter', 0, 'Default', 0, 'N', 'N', 'S', 1  ,'None', 0);
INSERT into point  values (-2,  'System', 'Scanner', 0, 'Default', 0, 'N', 'N', 'S', 2  ,'None', 0);
INSERT into point  values (-3,  'System', 'Dispatch', 0, 'Default', 0, 'N', 'N', 'S', 3  ,'None', 0);
INSERT into point  values (-4,  'System', 'Macs', 0, 'Default', 0, 'N', 'N', 'S', 4  ,'None', 0);
INSERT into point  values (-5,  'System', 'Cap Control', 0, 'Default', 0, 'N', 'N', 'S', 5  ,'None', 0);
INSERT into point  values (-10, 'System', 'Load Management' , 0, 'Default', 0, 'N', 'N', 'S', 10 ,'None', 0);

/*==============================================================*/
/* Index: Indx_PTNM_YUKPAOID                                    */
/*==============================================================*/
create unique  index Indx_PTNM_YUKPAOID on POINT (
POINTNAME,
PAObjectID
)
go


/*==============================================================*/
/* Index: Indx_PointStGrpID                                     */
/*==============================================================*/
create   index Indx_PointStGrpID on POINT (
STATEGROUPID
)
go


/*==============================================================*/
/* Table : POINTACCUMULATOR                                     */
/*==============================================================*/
create table POINTACCUMULATOR (
POINTID              numeric              not null,
MULTIPLIER           float                not null,
DATAOFFSET           float                not null
)
go


/*==============================================================*/
/* Table : POINTANALOG                                          */
/*==============================================================*/
create table POINTANALOG (
POINTID              numeric              not null,
DEADBAND             float                not null,
TRANSDUCERTYPE       varchar(14)          not null,
MULTIPLIER           float                not null,
DATAOFFSET           float                not null,
constraint PK_POINTANALOG primary key  (POINTID)
)
go


/*==============================================================*/
/* Table : POINTLIMITS                                          */
/*==============================================================*/
create table POINTLIMITS (
POINTID              numeric              not null,
LIMITNUMBER          numeric              not null,
HIGHLIMIT            float                not null,
LOWLIMIT             float                not null,
LIMITDURATION        numeric              not null
)
go


/*==============================================================*/
/* Table : POINTSTATUS                                          */
/*==============================================================*/
create table POINTSTATUS (
POINTID              numeric              not null,
INITIALSTATE         numeric              not null,
CONTROLTYPE          varchar(12)          not null,
CONTROLINHIBIT       varchar(1)           not null,
ControlOffset        numeric              not null,
CloseTime1           numeric              not null,
CloseTime2           numeric              not null,
StateZeroControl     varchar(100)         not null,
StateOneControl      varchar(100)         not null,
CommandTimeOut       numeric              not null,
constraint PK_PtStatus primary key  (POINTID)
)
go


/*==============================================================*/
/* Table : POINTUNIT                                            */
/*==============================================================*/
create table POINTUNIT (
POINTID              numeric              not null,
UOMID                numeric              not null,
DECIMALPLACES        numeric              not null,
HighReasonabilityLimit float                not null,
LowReasonabilityLimit float                not null,
constraint PK_POINTUNITID primary key  (POINTID)
)
go


/*==============================================================*/
/* Table : PORTDIALUPMODEM                                      */
/*==============================================================*/
create table PORTDIALUPMODEM (
PORTID               numeric              not null,
MODEMTYPE            varchar(30)          not null,
INITIALIZATIONSTRING varchar(50)          not null,
PREFIXNUMBER         varchar(10)          not null,
SUFFIXNUMBER         varchar(10)          not null
)
go


/*==============================================================*/
/* Table : PORTLOCALSERIAL                                      */
/*==============================================================*/
create table PORTLOCALSERIAL (
PORTID               numeric              not null,
PHYSICALPORT         varchar(8)           not null
)
go


/*==============================================================*/
/* Table : PORTRADIOSETTINGS                                    */
/*==============================================================*/
create table PORTRADIOSETTINGS (
PORTID               numeric              not null,
RTSTOTXWAITSAMED     numeric              not null,
RTSTOTXWAITDIFFD     numeric              not null,
RADIOMASTERTAIL      numeric              not null,
REVERSERTS           numeric              not null
)
go


/*==============================================================*/
/* Table : PORTSETTINGS                                         */
/*==============================================================*/
create table PORTSETTINGS (
PORTID               numeric              not null,
BAUDRATE             numeric              not null,
CDWAIT               numeric              not null,
LINESETTINGS         varchar(8)           not null
)
go


/*==============================================================*/
/* Table : PORTSTATISTICS                                       */
/*==============================================================*/
create table PORTSTATISTICS (
PORTID               numeric              not null,
STATISTICTYPE        numeric              not null,
ATTEMPTS             numeric              not null,
DATAERRORS           numeric              not null,
SYSTEMERRORS         numeric              not null,
STARTDATETIME        datetime             not null,
STOPDATETIME         datetime             not null,
constraint PK_PORTSTATISTICS primary key  (PORTID, STATISTICTYPE)
)
go


/*==============================================================*/
/* Table : PORTTERMINALSERVER                                   */
/*==============================================================*/
create table PORTTERMINALSERVER (
PORTID               numeric              not null,
IPADDRESS            varchar(16)          not null,
SOCKETPORTNUMBER     numeric              not null
)
go


/*==============================================================*/
/* Table : PORTTIMING                                           */
/*==============================================================*/
create table PORTTIMING (
PORTID               numeric              not null,
PRETXWAIT            numeric              not null,
RTSTOTXWAIT          numeric              not null,
POSTTXWAIT           numeric              not null,
RECEIVEDATAWAIT      numeric              not null,
EXTRATIMEOUT         numeric              not null
)
go


/*==============================================================*/
/* Table : PointAlarming                                        */
/*==============================================================*/
create table PointAlarming (
PointID              numeric              not null,
AlarmStates          varchar(32)          not null,
ExcludeNotifyStates  varchar(32)          not null,
NotifyOnAcknowledge  char(1)              not null,
NotificationGroupID  numeric              not null,
RecipientID          numeric              not null,
constraint PK_POINTALARMING primary key  (PointID)
)
go


insert into pointalarming(pointid, alarmstates, excludenotifystates, notifyonacknowledge, notificationgroupid, recipientid)
	select pointid,
	'',
	'NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN',
	'N',
	1, 0  from point;

/*==============================================================*/
/* Table : RAWPOINTHISTORY                                      */
/*==============================================================*/
create table RAWPOINTHISTORY (
CHANGEID             numeric              not null,
POINTID              numeric              not null,
TIMESTAMP            datetime             not null,
QUALITY              numeric              not null,
VALUE                float                not null,
constraint SYS_C0013322 primary key  (CHANGEID)
)
go


/*==============================================================*/
/* Index: Index_PointID                                         */
/*==============================================================*/
create   index Index_PointID on RAWPOINTHISTORY (
POINTID
)
go


/*==============================================================*/
/* Index: Indx_TimeStamp                                        */
/*==============================================================*/
create   index Indx_TimeStamp on RAWPOINTHISTORY (
TIMESTAMP
)
go


/*==============================================================*/
/* Index: Indx_RwPtHisPtIDTst                                   */
/*==============================================================*/
create   index Indx_RwPtHisPtIDTst on RAWPOINTHISTORY (
POINTID,
TIMESTAMP
)
go


/*==============================================================*/
/* Table : REPEATERROUTE                                        */
/*==============================================================*/
create table REPEATERROUTE (
ROUTEID              numeric              not null,
DEVICEID             numeric              not null,
VARIABLEBITS         numeric              not null,
REPEATERORDER        numeric              not null
)
go


/*==============================================================*/
/* Table : ROUTE                                                */
/*==============================================================*/
create table ROUTE (
ROUTEID              numeric              not null,
DeviceID             numeric              not null,
DefaultRoute         char(1)              not null,
constraint SYS_RoutePK primary key  (ROUTEID)
)
go


/*==============================================================*/
/* Index: Indx_RouteDevID                                       */
/*==============================================================*/
create unique  index Indx_RouteDevID on ROUTE (
DeviceID,
ROUTEID
)
go


/*==============================================================*/
/* Table : STATE                                                */
/*==============================================================*/
create table STATE (
STATEGROUPID         numeric              not null,
RAWSTATE             numeric              not null,
TEXT                 varchar(20)          not null,
FOREGROUNDCOLOR      numeric              not null,
BACKGROUNDCOLOR      numeric              not null,
constraint PK_STATE primary key  (STATEGROUPID, RAWSTATE)
)
go


INSERT INTO State VALUES ( -1, 0, 'AnalogText', 0, 6 );
INSERT INTO State VALUES ( -2, 0, 'AccumulatorText', 0, 6 );
INSERT INTO State VALUES ( -3, 0, 'CalculatedText', 0, 6 );
INSERT INTO State VALUES ( 0, 0, 'SystemText', 0, 6 );
INSERT INTO State VALUES ( 1, -1, 'Any', 2, 6 );
INSERT INTO State VALUES ( 1, 0, 'Open', 0, 6 );
INSERT INTO State VALUES ( 1, 1, 'Closed', 1, 6 );
INSERT INTO State VALUES ( 2, -1, 'Any', 2, 6 );
INSERT INTO State VALUES ( 2, 0, 'Open', 0, 6 );
INSERT INTO State VALUES ( 2, 1, 'Closed', 1, 6 );
INSERT INTO State VALUES ( 2, 2, 'Unknown', 2, 6 );
INSERT INTO State VALUES ( 3, -1, 'Any', 2, 6 );
INSERT INTO State VALUES ( 3, 0, 'Open', 0, 6 );
INSERT INTO State VALUES ( 3, 1, 'Close', 1, 6 );
INSERT INTO State VALUES ( 3, 2, 'OpenQuestionable', 2, 6 );
INSERT INTO State VALUES ( 3, 3, 'CloseQuestionable', 3, 6 );
INSERT INTO State VALUES ( 3, 4, 'OpenFail', 4, 6 );
INSERT INTO State VALUES ( 3, 5, 'CloseFail', 5, 6 );
INSERT INTO State VALUES ( 3, 6, 'OpenPending', 7, 6 );
INSERT INTO State VALUES ( 3, 7, 'ClosePending', 8, 6 );
INSERT INTO State VALUES(-5, 0, 'Events', 2, 6);
INSERT INTO State VALUES(-5, 1, 'Priority 1', 1, 6);
INSERT INTO State VALUES(-5, 2, 'Priority 2', 4, 6);
INSERT INTO State VALUES(-5, 3, 'Priority 3', 0, 6);
INSERT INTO State VALUES(-5, 4, 'Priority 4', 7, 6);
INSERT INTO State VALUES(-5, 5, 'Priority 5', 8, 6);
INSERT INTO State VALUES(-5, 6, 'Priority 6', 5, 6);
INSERT INTO State VALUES(-5, 7, 'Priority 7', 3, 6);
INSERT INTO State VALUES(-5, 8, 'Priority 8', 2, 6);
INSERT INTO State VALUES(-5, 9, 'Priority 9', 6, 6);
INSERT INTO State VALUES(-5, 10, 'Priority 10', 9, 6);

/*==============================================================*/
/* Index: Indx_StateRaw                                         */
/*==============================================================*/
create   index Indx_StateRaw on STATE (
RAWSTATE
)
go


/*==============================================================*/
/* Table : STATEGROUP                                           */
/*==============================================================*/
create table STATEGROUP (
STATEGROUPID         numeric              not null,
NAME                 varchar(20)          not null,
constraint SYS_C0013128 primary key  (STATEGROUPID)
)
go


INSERT INTO StateGroup VALUES ( -1, 'DefaultAnalog' );
INSERT INTO StateGroup VALUES ( -2, 'DefaultAccumulator' );
INSERT INTO StateGroup VALUES ( -3, 'DefaultCalculated' );
INSERT INTO StateGroup VALUES (-5, 'Event Priority');
INSERT INTO StateGroup VALUES ( 0, 'SystemState' );
INSERT INTO StateGroup VALUES ( 1, 'TwoStateStatus' );
INSERT INTO StateGroup VALUES ( 2, 'ThreeStateStatus' );
INSERT INTO StateGroup VALUES ( 3, 'CapBankStatus' );

/*==============================================================*/
/* Index: Indx_STATEGRP_Nme                                     */
/*==============================================================*/
create unique  index Indx_STATEGRP_Nme on STATEGROUP (
NAME
)
go


/*==============================================================*/
/* Table : SYSTEMLOG                                            */
/*==============================================================*/
create table SYSTEMLOG (
LOGID                numeric              not null,
POINTID              numeric              not null,
DATETIME             datetime             not null,
SOE_TAG              numeric              not null,
TYPE                 numeric              not null,
PRIORITY             numeric              not null,
ACTION               varchar(60)          null,
DESCRIPTION          varchar(120)         null,
USERNAME             varchar(30)          null,
constraint SYS_C0013407 primary key  (LOGID)
)
go


/*==============================================================*/
/* Table : ServiceCompany                                       */
/*==============================================================*/
create table ServiceCompany (
CompanyID            numeric              not null,
CompanyName          varchar(40)          null,
AddressID            numeric              null,
MainPhoneNumber      varchar(14)          null,
MainFaxNumber        varchar(14)          null,
PrimaryContactID     numeric              null,
HIType               varchar(40)          null,
constraint PK_SERVICECOMPANY primary key  (CompanyID)
)
go


/*==============================================================*/
/* Table : SiteInformation                                      */
/*==============================================================*/
create table SiteInformation (
SiteID               numeric              not null,
Feeder               varchar(20)          null,
Pole                 varchar(20)          null,
TransformerSize      varchar(20)          null,
ServiceVoltage       varchar(20)          null,
SubstationID         numeric              null,
constraint PK_SITEINFORMATION primary key  (SiteID)
)
go


/*==============================================================*/
/* Table : Substation                                           */
/*==============================================================*/
create table Substation (
SubstationID         numeric              not null,
SubstationName       varchar(50)          null,
RouteID              numeric              null,
constraint PK_SUBSTATION primary key  (SubstationID)
)
go


/*==============================================================*/
/* Table : TEMPLATE                                             */
/*==============================================================*/
create table TEMPLATE (
TEMPLATENUM          numeric              not null,
NAME                 varchar(40)          not null,
DESCRIPTION          varchar(200)         null,
constraint SYS_C0013425 primary key  (TEMPLATENUM)
)
go


insert into template values( 1, 'Standard', 'First Standard Cannon Template');
insert into template values( 2, 'Standard - No PtName', 'Second Standard Cannon  Template');
insert into template values( 3, 'Standard - No DevName', 'Third Standard Cannon  Template');


/*==============================================================*/
/* Table : TEMPLATECOLUMNS                                      */
/*==============================================================*/
create table TEMPLATECOLUMNS (
TEMPLATENUM          numeric              not null,
TITLE                varchar(50)          null,
TYPENUM              numeric              not null,
ORDERING             numeric              not null,
WIDTH                numeric              not null
)
go


insert into templatecolumns values( 1, 'Device Name', 5, 1, 85 );
insert into templatecolumns values( 1, 'Point Name', 2, 2, 85 );
insert into templatecolumns values( 1, 'Value', 9, 3, 85 );
insert into templatecolumns values( 1, 'Quality', 10, 4, 80 );
insert into templatecolumns values( 1, 'Time', 11, 5, 135 );
insert into templatecolumns values( 2, 'Device Name', 5, 1, 85 );
insert into templatecolumns values( 2, 'Value', 9, 2, 85 );
insert into templatecolumns values( 2, 'Quality', 10, 3, 80 );
insert into templatecolumns values( 2, 'Time', 11, 4, 135 );
insert into templatecolumns values( 3, 'Point Name', 2, 1, 85 );
insert into templatecolumns values( 3, 'Value', 9, 2, 85 );
insert into templatecolumns values( 3, 'Quality', 10, 3, 80 );
insert into templatecolumns values( 3, 'Time', 11, 4, 135 );


/*==============================================================*/
/* Table : UNITMEASURE                                          */
/*==============================================================*/
create table UNITMEASURE (
UOMID                numeric              not null,
UOMName              varchar(8)           not null,
CalcType             numeric              not null,
LongName             varchar(40)          not null,
Formula              varchar(80)          not null,
constraint SYS_C0013344 primary key  (UOMID)
)
go


INSERT INTO UnitMeasure VALUES ( 0,'KW', 0,'KW','(none)' );
INSERT INTO UnitMeasure VALUES ( 1,'KWH', 0,'KWH','usage' );
INSERT INTO UnitMeasure VALUES ( 2,'KVA', 0,'KVA','(none)' );
INSERT INTO UnitMeasure VALUES ( 3,'KVAR', 0,'KVAR','(none)' );
INSERT INTO UnitMeasure VALUES ( 4,'KVAH', 0,'KVAH','usage' );
INSERT INTO UnitMeasure VALUES ( 5,'KVARH', 0,'KVARH','usage' );
INSERT INTO UnitMeasure VALUES ( 6,'KVolts', 0,'KVolts','(none)' );
INSERT INTO UnitMeasure VALUES ( 7,'KQ', 0,'KQ','(none)' );
INSERT INTO UnitMeasure VALUES ( 8,'Amps', 0,'Amps','(none)' );
INSERT INTO UnitMeasure VALUES ( 9,'Counts', 0,'Counts','(none)' );
INSERT INTO UnitMeasure VALUES ( 10,'Degrees', 0,'Degrees','(none)' );
INSERT INTO UnitMeasure VALUES ( 11,'Dollars', 0,'Dollars','(none)' );
INSERT INTO UnitMeasure VALUES ( 12,'$', 0,'Dollar Char','(none)' );
INSERT INTO UnitMeasure VALUES ( 13,'Feet', 0,'Feet','(none)' );
INSERT INTO UnitMeasure VALUES ( 14,'Gallons', 0,'Gallons','(none)' );
INSERT INTO UnitMeasure VALUES ( 15,'Gal/PM', 0,'Gal/PM','(none)' );
INSERT INTO UnitMeasure VALUES ( 16,'GAS-CFT', 0,'GAS-CFT','(none)' );
INSERT INTO UnitMeasure VALUES ( 17,'Hours', 0,'Hours','(none)' );
INSERT INTO UnitMeasure VALUES ( 18,'Level', 0,'Level','(none)' );
INSERT INTO UnitMeasure VALUES ( 19,'Minutes', 0,'Minutes','(none)' );
INSERT INTO UnitMeasure VALUES ( 20,'MW', 0,'MW','(none)' );
INSERT INTO UnitMeasure VALUES ( 21,'MWH', 0,'MWH','usage' );
INSERT INTO UnitMeasure VALUES ( 22,'MVA', 0,'MVA','(none)' );
INSERT INTO UnitMeasure VALUES ( 23,'MVAR', 0,'MVAR','(none)' );
INSERT INTO UnitMeasure VALUES ( 24,'MVAH', 0,'MVAH','usage' );
INSERT INTO UnitMeasure VALUES ( 25,'MVARH', 0,'MVARH','usage' );
INSERT INTO UnitMeasure VALUES ( 26,'Ops.', 0,'Ops','(none)' );
INSERT INTO UnitMeasure VALUES ( 27,'PF', 0,'PF','(none)' );
INSERT INTO UnitMeasure VALUES ( 28,'Percent', 0,'Percent','(none)' );
INSERT INTO UnitMeasure VALUES ( 29,'%', 0,'Percent Char','(none)' );
INSERT INTO UnitMeasure VALUES ( 30,'PSI', 0,'PSI','(none)' );
INSERT INTO UnitMeasure VALUES ( 31,'Seconds', 0,'Seconds','(none)' );
INSERT INTO UnitMeasure VALUES ( 32,'Temp-F', 0,'Temp-F','(none)' );
INSERT INTO UnitMeasure VALUES ( 33,'Temp-C', 0,'Temp-C','(none)' );
INSERT INTO UnitMeasure VALUES ( 34,'Vars', 0,'Vars','(none)' );
INSERT INTO UnitMeasure VALUES ( 35,'Volts', 0,'Volts','(none)' );
INSERT INTO UnitMeasure VALUES ( 36,'VoltAmps', 0,'VoltAmps','(none)' );
INSERT INTO UnitMeasure VALUES ( 37,'VA', 0,'VA','(none)' );
INSERT INTO UnitMeasure VALUES ( 38,'Watr-CFT', 0,'Watr-CFT','(none)' );
INSERT INTO UnitMeasure VALUES ( 39,'Watts', 0,'Watts','(none)' );
INSERT INTO UnitMeasure VALUES ( 40,'Hz', 0,'Hertz','(none)' );
INSERT INTO UnitMeasure VALUES ( 41,'Volts', 1,'Volts from V2H','(none)' );
INSERT INTO UnitMeasure VALUES ( 42,'Amps', 1,'Amps from A2H','(none)' );

/*==============================================================*/
/* Table : VERSACOMROUTE                                        */
/*==============================================================*/
create table VERSACOMROUTE (
ROUTEID              numeric              null,
UTILITYID            numeric              not null,
SECTIONADDRESS       numeric              not null,
CLASSADDRESS         numeric              not null,
DIVISIONADDRESS      numeric              not null,
BUSNUMBER            numeric              not null,
AMPCARDSET           numeric              not null
)
go


/*==============================================================*/
/* Table : WorkOrderBase                                        */
/*==============================================================*/
create table WorkOrderBase (
OrderID              numeric              not null,
OrderNumber          varchar(20)          null,
WorkTypeID           numeric              not null,
CurrentStateID       numeric              not null,
CustomerID           numeric              not null,
SiteID               numeric              not null,
ServiceCompanyID     numeric              null,
DateReported         datetime             null,
Description          varchar(200)         null,
DateScheduled        datetime             null,
DateCompleted        datetime             null,
ActionTaken          varchar(200)         null,
constraint PK_WORKORDERBASE primary key  (OrderID)
)
go


/*==============================================================*/
/* Table : YukonPAObject                                        */
/*==============================================================*/
create table YukonPAObject (
PAObjectID           numeric              not null,
Category             varchar(20)          not null,
PAOClass             varchar(20)          not null,
PAOName              varchar(60)          not null,
Type                 varchar(30)          not null,
Description          varchar(60)          not null,
DisableFlag          char(1)              not null,
constraint PK_YUKONPAOBJECT primary key  (PAObjectID)
)
go


INSERT into YukonPAObject values (0, 'DEVICE', 'System', 'System Device', 'System', 'Reserved System Device', 'N');

/*==============================================================*/
/* Index: Indx_PAO                                              */
/*==============================================================*/
create unique  index Indx_PAO on YukonPAObject (
Category,
PAOName,
PAOClass,
Type
)
go


/*==============================================================*/
/* View: DISPLAY2WAYDATA_VIEW                                   */
/*==============================================================*/
create view DISPLAY2WAYDATA_VIEW (POINTID, POINTNAME , POINTTYPE , POINTSTATE , DEVICENAME, DEVICETYPE, DEVICECURRENTSTATE, DEVICEID, POINTVALUE, POINTQUALITY, POINTTIMESTAMP, UofM) as
select POINTID, POINTNAME, POINTTYPE, SERVICEFLAG, YukonPAObject.PAOName, YukonPAObject.Type, YukonPAObject.Description, YukonPAObject.PAObjectID, '**DYNAMIC**', '**DYNAMIC**', '**DYNAMIC**', (select uomname from pointunit,unitmeasure where pointunit.pointid=point.pointid and pointunit.uomid=unitmeasure.uomid)
from YukonPAObject, POINT
where YukonPAObject.PAObjectID = POINT.PAObjectID
go


/*==============================================================*/
/* View: FullEventLog_View                                      */
/*==============================================================*/
create view FullEventLog_View (EventID, PointID, EventTimeStamp, EventSequence, EventType, EventAlarmID, DeviceName, PointName, EventDescription, AdditionalInfo, EventUserName) as
select s.LOGID, s.POINTID, s.DATETIME, s.SOE_TAG, s.TYPE, s.PRIORITY, y.PAOName, p.POINTNAME, s.DESCRIPTION, s.ACTION, s.USERNAME
from YukonPAObject y, POINT p, SYSTEMLOG s
where s.POINTID = p.POINTID and p.PAObjectID = y.PAObjectID
go


/*==============================================================*/
/* View: FullPointHistory_View                                  */
/*==============================================================*/
create view FullPointHistory_View (PointID, DeviceName, PointName, DataValue, DataTimeStamp, DataQuality) as
select r.POINTID, y.PAOName, p.POINTNAME, r.VALUE, r.TIMESTAMP, r.QUALITY
from YukonPAObject y, POINT p, RAWPOINTHISTORY r
where r.POINTID = p.POINTID and p.PAObjectID = y.PAObjectID
go


/*==============================================================*/
/* View: LMCurtailCustomerActivity_View                         */
/*==============================================================*/
create view LMCurtailCustomerActivity_View  as
select cust.CustomerID, prog.CurtailmentStartTime, prog.CurtailReferenceID, prog.CurtailmentStopTime, cust.AcknowledgeStatus, cust.AckDateTime, cust.NameOfAckPerson, cust.AckLateFlag
from LMCurtailProgramActivity prog, LMCurtailCustomerActivity cust
where prog.CurtailReferenceID = cust.CurtailReferenceID
go


/*==============================================================*/
/* View: LMGroupMacroExpander_View                              */
/*==============================================================*/
create view LMGroupMacroExpander_View  as
select distinct PAObjectID, Category, PAOClass, PAOName, Type, Description, DisableFlag, 
ALARMINHIBIT, CONTROLINHIBIT, KWCapacity, dg.DeviceID, 
LMGroupDeviceID, GroupOrder, OwnerID, ChildID, ChildOrder
from YukonPAObject y, DEVICE d, LMGroup g,
LMProgramDirectGroup dg, GenericMacro m
where y.PAObjectID = d.DEVICEID 
and d.DEVICEID = g.DeviceID
and dg.lmgroupdeviceid *= m.ownerid
go


/*==============================================================*/
/* View: PointEventLog_View                                     */
/*==============================================================*/
create view PointEventLog_View (EventID, PointID, EventTimeStamp, EventSequence, EventType, EventAlarmID, PointName, EventDescription, AdditionalInfo, EventUserName) as
select s.LOGID, s.POINTID, s.DATETIME, s.SOE_TAG, s.TYPE, s.PRIORITY, p.POINTNAME, s.DESCRIPTION, s.ACTION, s.USERNAME
from POINT p, SYSTEMLOG s
where s.POINTID = p.POINTID
go


/*==============================================================*/
/* View: PointHistory_View                                      */
/*==============================================================*/
create view PointHistory_View (PointID, PointName, DataValue, DataTimeStamp, DataQuality) as
select r.POINTID, p.POINTNAME, r.VALUE, r.TIMESTAMP, r.QUALITY
from POINT p, RAWPOINTHISTORY r
where r.POINTID = p.POINTID
go


alter table AlarmCategory
   add constraint FK_ALRMCAT_NOTIFGRP foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID)
go


alter table InventoryBase
   add constraint FK_CUS_CSTA_CUS3 foreign key (AccountID)
      references CustomerAccount (AccountID)
go


alter table CustomerAccount
   add constraint FK_CUS_CSTA_CUS2 foreign key (AccountSiteID)
      references AccountSite (AccountSiteID)
go


alter table ApplianceBase
   add constraint FK_CUS_CSTA_CUS4 foreign key (AccountID)
      references CustomerAccount (AccountID)
go


alter table LMHardwareConfiguration
   add constraint FK_LMH_CSTL_CUS2 foreign key (ApplianceID)
      references ApplianceBase (ApplianceID)
go


alter table ApplianceBase
   add constraint FK_APP_CSTL_APP foreign key (ApplianceCategoryID)
      references ApplianceCategory (ApplianceCategoryID)
go


alter table AccountSite
   add constraint FK_CUS_CSTS_CUS2 foreign key (SiteInformationID)
      references SiteInformation (SiteID)
go


alter table NotificationDestination
   add constraint FK_DESTID_RECID foreign key (RecipientID)
      references NotificationRecipient (RecipientID)
go


alter table InventoryBase
   add constraint FK_CUS_HRDI_HAR2 foreign key (InstallationCompanyID)
      references ServiceCompany (CompanyID)
go


alter table LMGROUPEMETCON
   add constraint SYS_C0013356 foreign key (DEVICEID)
      references LMGroup (DeviceID)
go


alter table LMProgramControlWindow
   add constraint FK_LMPrg_LMPrgCntWind foreign key (DeviceID)
      references LMPROGRAM (DEVICEID)
go


alter table DynamicLMProgramDirect
   add constraint FK_DYN_LMPR_LMP foreign key (DeviceID)
      references LMProgramDirect (DeviceID)
go


alter table LMHardwareConfiguration
   add constraint FK_LMH_LMHR_LMH foreign key (InventoryID)
      references LMHardwareBase (InventoryID)
go


alter table MACSimpleSchedule
   add constraint FK_MACSIMPLE_MACSCHED_ID foreign key (ScheduleID)
      references MACSchedule (ScheduleID)
go


alter table NotificationDestination
   add constraint FK_NotifDest_NotifGrp foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID)
go


alter table PointAlarming
   add constraint FK_POI_POIN_NOT foreign key (RecipientID)
      references NotificationRecipient (RecipientID)
go


alter table PointAlarming
   add constraint FK_POINTALAARM_POINT_POINTID foreign key (PointID)
      references POINT (POINTID)
go


alter table PointAlarming
   add constraint FK_POINTALARMING foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID)
go


alter table LMDirectCustomerList
   add constraint FK_CICstB_LMPrDi foreign key (CustomerID)
      references CICustomerBase (DeviceID)
go


alter table LMCurtailCustomerActivity
   add constraint FK_CICBas_LMCrtCstAct foreign key (CustomerID)
      references CICustomerBase (DeviceID)
go


alter table LMProgramCurtailCustomerList
   add constraint FK_CICstBase_LMProgCList foreign key (LMCustomerDeviceID)
      references CICustomerBase (DeviceID)
go


alter table CICustContact
   add constraint FK_CICstBase_CICstCont foreign key (DeviceID)
      references CICustomerBase (DeviceID)
go


alter table CICustomerBase
   add constraint FK_CICstBas_CstAddrs foreign key (AddressID)
      references CustomerAddress (AddressID)
go


alter table CALCCOMPONENT
   add constraint FK_ClcCmp_ClcBs foreign key (PointID)
      references CALCBASE (POINTID)
go


alter table CICustContact
   add constraint FK_CstCont_CICstCont foreign key (ContactID)
      references CustomerContact (ContactID)
go


alter table CustomerContact
   add constraint FK_CstCont_GrpRecip foreign key (LocationID)
      references NotificationRecipient (RecipientID)
go


alter table CustomerWebSettings
   add constraint FK_CustWebSet_CICstBse foreign key (CustomerID)
      references CICustomerBase (DeviceID)
go


alter table CustomerContact
   add constraint FK_RefCstLg_CustCont foreign key (LogInID)
      references CustomerLogin (LogInID)
go


alter table LMGroup
   add constraint FK_Device_LMGrpBase foreign key (DeviceID)
      references DEVICE (DEVICEID)
go


alter table LMEnergyExchangeOfferRevision
   add constraint FK_EExOffR_ExPrOff foreign key (OfferID)
      references LMEnergyExchangeProgramOffer (OfferID)
go


alter table LMEnergyExchangeProgramOffer
   add constraint FK_EnExOff_PrgEnEx foreign key (DeviceID)
      references LMProgramEnergyExchange (DeviceID)
go


alter table LMEnergyExchangeCustomerList
   add constraint FK_ExCsLs_CstBs foreign key (LMCustomerDeviceID)
      references CICustomerBase (DeviceID)
go


alter table LMEnergyExchangeCustomerList
   add constraint FK_ExCsLs_PrEx foreign key (DeviceID)
      references LMProgramEnergyExchange (DeviceID)
go


alter table LMEnergyExchangeCustomerReply
   add constraint FK_ExCsRp_CstBs foreign key (CustomerID)
      references CICustomerBase (DeviceID)
go


alter table LMEnergyExchangeCustomerReply
   add constraint FK_LME_REFE_LME foreign key (OfferID, RevisionNumber)
      references LMEnergyExchangeOfferRevision (OfferID, RevisionNumber)
go


alter table LMEnergyExchangeHourlyCustomer
   add constraint FK_ExHrCs_ExCsRp foreign key (CustomerID, OfferID, RevisionNumber)
      references LMEnergyExchangeCustomerReply (CustomerID, OfferID, RevisionNumber)
go


alter table LMEnergyExchangeHourlyOffer
   add constraint FK_ExHrOff_ExOffRv foreign key (OfferID, RevisionNumber)
      references LMEnergyExchangeOfferRevision (OfferID, RevisionNumber)
go


alter table FDRInterfaceOption
   add constraint FK_FDRINTER_REFERENCE_FDRINTER foreign key (InterfaceID)
      references FDRInterface (InterfaceID)
go


alter table GraphCustomerList
   add constraint FK_GRA_REFG_CIC foreign key (CustomerID)
      references CICustomerBase (DeviceID)
go


alter table GraphCustomerList
   add constraint FK_GRA_REFG_GRA foreign key (GraphDefinitionID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID)
go


alter table DynamicLMControlAreaTrigger
   add constraint FK_LMCntArTr_DyLMCnArTr foreign key (DeviceID, TriggerNumber)
      references LMCONTROLAREATRIGGER (DEVICEID, TRIGGERNUMBER)
go


alter table DynamicLMControlArea
   add constraint FK_LMCntlAr_DynLMCntAr foreign key (DeviceID)
      references LMCONTROLAREA (DEVICEID)
go


alter table LMCONTROLAREATRIGGER
   add constraint FK_LMCntlArea_LMCntlArTrig foreign key (DEVICEID)
      references LMCONTROLAREA (DEVICEID)
go


alter table LMCurtailCustomerActivity
   add constraint FK_LMC_REFL_LMC foreign key (CurtailReferenceID)
      references LMCurtailProgramActivity (CurtailReferenceID)
go


alter table LMCONTROLAREAPROGRAM
   add constraint FK_LMCntlArea_LMCntlArProg foreign key (DEVICEID)
      references LMCONTROLAREA (DEVICEID)
go


alter table LMProgramDirectGroup
   add constraint FK_LMGrp_LMPrgDGrp foreign key (LMGroupDeviceID)
      references LMGroup (DeviceID)
go


alter table LMGROUPVERSACOM
   add constraint FK_LMGrp_LMGrpVers foreign key (DEVICEID)
      references LMGroup (DeviceID)
go


alter table DynamicLMGroup
   add constraint FK_LMGrp_DynLmGrp foreign key (DeviceID)
      references LMGroup (DeviceID)
go


alter table LMDirectCustomerList
   add constraint FK_LMD_REFL_LMP foreign key (ProgramID)
      references LMProgramDirect (DeviceID)
go


alter table LMCurtailProgramActivity
   add constraint FK_LMPrgCrt_LMCrlPAct foreign key (DeviceID)
      references LMProgramCurtailment (DeviceID)
go


alter table LMProgramCurtailCustomerList
   add constraint FK_LMPrgCrt_LMPrCstLst foreign key (DeviceID)
      references LMProgramCurtailment (DeviceID)
go


alter table LMProgramDirectGear
   add constraint FK_LMProgD_LMProgDGr foreign key (DeviceID)
      references LMProgramDirect (DeviceID)
go


alter table LMProgramDirectGroup
   add constraint FK_LMPrgD_LMPrgDGrp foreign key (DeviceID)
      references LMProgramDirect (DeviceID)
go


alter table LMCONTROLAREAPROGRAM
   add constraint FK_LMPrg_LMCntlArProg foreign key (LMPROGRAMDEVICEID)
      references LMPROGRAM (DEVICEID)
go


alter table DynamicLMProgram
   add constraint FK_LMProg_DynLMPrg foreign key (DeviceID)
      references LMPROGRAM (DEVICEID)
go


alter table LMProgramCurtailment
   add constraint FK_LMPrg_LMPrgCurt foreign key (DeviceID)
      references LMPROGRAM (DEVICEID)
go


alter table LMProgramDirect
   add constraint FK_LMPrg_LMPrgDirect foreign key (DeviceID)
      references LMPROGRAM (DEVICEID)
go


alter table LMGroupRipple
   add constraint FK_LmGr_LmGrpRip foreign key (DeviceID)
      references LMGroup (DeviceID)
go


alter table LMGroupRipple
   add constraint FK_LmGrpRip_Rout foreign key (RouteID)
      references ROUTE (ROUTEID)
go


alter table LMMacsScheduleCustomerList
   add constraint FK_McSchCstLst_MCSched foreign key (ScheduleID)
      references MACSchedule (ScheduleID)
go


alter table LMMacsScheduleCustomerList
   add constraint FK_McsSchdCusLst_CICBs foreign key (LMCustomerDeviceID)
      references CICustomerBase (DeviceID)
go


alter table LMCONTROLAREATRIGGER
   add constraint FK_Point_LMCntlArTrig foreign key (POINTID)
      references POINT (POINTID)
go


alter table LMCONTROLAREATRIGGER
   add constraint FK_Point_LMCtrlArTrigPk foreign key (PEAKPOINTID)
      references POINT (POINTID)
go


alter table AccountSite
   add constraint FK_AccS_CstAd foreign key (StreetAddressID)
      references CustomerAddress (AddressID)
go


alter table WorkOrderBase
   add constraint FK_AccS_WkB foreign key (SiteID)
      references AccountSite (AccountSiteID)
go


alter table ApplianceBase
   add constraint FK_AppBs_LMPr foreign key (LMProgramID)
      references LMPROGRAM (DEVICEID)
go


alter table CCFeederBankList
   add constraint FK_CB_CCFeedLst foreign key (DeviceID)
      references CAPBANK (DEVICEID)
go


alter table CCFeederBankList
   add constraint FK_CCFeed_CCBnk foreign key (FeederID)
      references CapControlFeeder (FeederID)
go


alter table CCFeederSubAssignment
   add constraint FK_CCFeed_CCFass foreign key (FeederID)
      references CapControlFeeder (FeederID)
go


alter table DynamicCCFeeder
   add constraint FK_CCFeed_DyFeed foreign key (FeederID)
      references CapControlFeeder (FeederID)
go


alter table DynamicCCSubstationBus
   add constraint FK_CCSubBs_DySubBs foreign key (SubstationBusID)
      references CAPCONTROLSUBSTATIONBUS (SubstationBusID)
go


alter table CCFeederSubAssignment
   add constraint FK_CCSub_CCFeed foreign key (SubStationBusID)
      references CAPCONTROLSUBSTATIONBUS (SubstationBusID)
go


alter table EnergyCompanyCustomerList
   add constraint FK_CICstBsEnCmpCsLs foreign key (CustomerID)
      references CICustomerBase (DeviceID)
go


alter table CustomerBaseLine
   add constraint FK_CICst_CstBsLne foreign key (CustomerID)
      references CICustomerBase (DeviceID)
go


alter table CommErrorHistory
   add constraint FK_ComErrHis_YPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID)
go


alter table COMMPORT
   add constraint FK_COM_REF__YUK foreign key (PORTID)
      references YukonPAObject (PAObjectID)
go


alter table DynamicCCCapBank
   add constraint FK_CpBnk_DynCpBnk foreign key (CapBankID)
      references CAPBANK (DEVICEID)
go


alter table CAPCONTROLSUBSTATIONBUS
   add constraint FK_CpSbBus_YPao foreign key (SubstationBusID)
      references YukonPAObject (PAObjectID)
go


alter table AirConditioner
   add constraint FK_CsLsE_Ac foreign key (TonageID)
      references CustomerListEntry (EntryID)
go


alter table AirConditioner
   add constraint FK_CsLsE_Ac_ty foreign key (TypeID)
      references CustomerListEntry (EntryID)
go


alter table LMCustomerEventBase
   add constraint FK_CsLsE_LCstE foreign key (EventTypeID)
      references CustomerListEntry (EntryID)
go


alter table LMCustomerEventBase
   add constraint FK_CsLsE_LCstE_a foreign key (ActionID)
      references CustomerListEntry (EntryID)
go


alter table WorkOrderBase
   add constraint FK_CsLsE_WkB foreign key (WorkTypeID)
      references CustomerListEntry (EntryID)
go


alter table WorkOrderBase
   add constraint FK_CsLsE_WkB_c foreign key (CurrentStateID)
      references CustomerListEntry (EntryID)
go


alter table ApplianceCategory
   add constraint FK_CsWC_ApCt foreign key (WebConfigurationID)
      references CustomerWebConfiguration (ConfigurationID)
go


alter table CallReportBase
   add constraint FK_CstAc_ClRpB foreign key (AccountID)
      references CustomerAccount (AccountID)
go


alter table LMProgramEvent
   add constraint FK_CstAc_LMPrEv foreign key (AccountID)
      references CustomerAccount (AccountID)
go


alter table ServiceCompany
   add constraint FK_CstAdd_SrC foreign key (AddressID)
      references CustomerAddress (AddressID)
go


alter table CallReportBase
   add constraint FK_CstB_CllR foreign key (CustomerID)
      references CustomerBase (CustomerID)
go


alter table WorkOrderBase
   add constraint FK_CstB_WkB foreign key (CustomerID)
      references CustomerBase (CustomerID)
go


alter table CustomerAccount
   add constraint FK_CstBs_CstAcc foreign key (CustomerID)
      references CustomerBase (CustomerID)
go


alter table CustomerBase
   add constraint FK_CstBs_CstCnt foreign key (PrimaryContactID)
      references CustomerContact (ContactID)
go


alter table CstBaseCstContactMap
   add constraint FK_CstBs_CstMp foreign key (CustomerID)
      references CustomerBase (CustomerID)
go


alter table ECToCustomerBaseMapping
   add constraint FK_CstBs_ECstM foreign key (CustomerID)
      references CustomerBase (CustomerID)
go


alter table CustomerBaseLinePoint
   add constraint FK_CstBseLn_CICust foreign key (CustomerID)
      references CICustomerBase (DeviceID)
go


alter table CustomerBaseLinePoint
   add constraint FK_CstBseLn_ClcBse foreign key (PointID)
      references CALCBASE (POINTID)
go


alter table CstBaseCstContactMap
   add constraint FK_CstCnt_Csmap foreign key (CustomerContactID)
      references CustomerContact (ContactID)
go


alter table ServiceCompany
   add constraint FK_CstCnt_SrvC foreign key (PrimaryContactID)
      references CustomerContact (ContactID)
go


alter table CallReportBase
   add constraint FK_CstELs_ClRB foreign key (CallTypeID)
      references CustomerListEntry (EntryID)
go


alter table ApplianceCategory
   add constraint FK_CstLs_ApCt foreign key (CategoryID)
      references CustomerListEntry (EntryID)
go


alter table InventoryBase
   add constraint FK_INV_REF__CUS foreign key (CategoryID)
      references CustomerListEntry (EntryID)
go


alter table LMHardwareBase
   add constraint FK_LMH_REF__CUS foreign key (LMHardwareTypeID)
      references CustomerListEntry (EntryID)
go


alter table DeviceWindow
   add constraint FK_DevScWin_Dev foreign key (DeviceID)
      references DEVICE (DEVICEID)
go


alter table InventoryBase
   add constraint FK_Dev_InvB foreign key (DeviceID)
      references DEVICE (DEVICEID)
go


alter table DEVICE
   add constraint FK_Dev_YukPAO foreign key (DEVICEID)
      references YukonPAObject (PAObjectID)
go


alter table DynamicCalcHistorical
   add constraint FK_DynClc_ClcB foreign key (PointID)
      references CALCBASE (POINTID)
go


alter table ECToAccountMapping
   add constraint FK_ECTAcc_CstAcc foreign key (AccountID)
      references CustomerAccount (AccountID)
go


alter table ECToAccountMapping
   add constraint FK_ECTAcc_Enc foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID)
go


alter table ECToGenericMapping
   add constraint FK_ECTGn_Enc foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID)
go


alter table ECToInventoryMapping
   add constraint FK_ECTInv_Enc2 foreign key (InventoryID)
      references InventoryBase (InventoryID)
go


alter table ECToInventoryMapping
   add constraint FK_ECTInv_Enc foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID)
go


alter table ECToCallReportMapping
   add constraint FK_ECTSrv_Call foreign key (CallReportID)
      references CallReportBase (CallID)
go


alter table ECToCallReportMapping
   add constraint FK_ECTSrv_Enc foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID)
go


alter table ECToWorkOrderMapping
   add constraint FK_ECTWrk_Enc2 foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID)
go


alter table ECToWorkOrderMapping
   add constraint FK_ECTWrk_Enc foreign key (WorkOrderID)
      references WorkOrderBase (OrderID)
go


alter table ECToCustomerBaseMapping
   add constraint FK_ECmp_ECstM foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID)
go


alter table ECToLMCustomerEventMapping
   add constraint FK_EnCm_ECLmCs foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID)
go


alter table EnergyCompanyCustomerList
   add constraint FK_EnCmpEnCmpCsLs foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID)
go


alter table EnergyCompanyOperatorLoginList
   add constraint FK_EnCmpEnCmpOpLs foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID)
go


alter table EnergyCompany
   add constraint FK_EnCmpRt foreign key (RouteID)
      references ROUTE (ROUTEID)
go


alter table OperatorLoginGraphList
   add constraint FK_OpLgOpLgGrLs foreign key (GraphDefinitionID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID)
go


alter table DateOfHoliday
   add constraint FK_HolSchID foreign key (HolidayScheduleID)
      references HolidaySchedule (HolidayScheduleID)
go


alter table LMHardwareActivity
   add constraint FK_Inv_LMHrdAc foreign key (InventoryID)
      references InventoryBase (InventoryID)
go


alter table LMHardwareEvent
   add constraint FK_IvB_LMHrEv foreign key (InventoryID)
      references InventoryBase (InventoryID)
go


alter table ECToLMCustomerEventMapping
   add constraint FK_LCsEv_ECLmCs foreign key (EventID)
      references LMCustomerEventBase (EventID)
go


alter table LMDirectOperatorList
   add constraint FK_LMDirOpLs_LMPrD foreign key (ProgramID)
      references LMProgramDirect (DeviceID)
go


alter table LMGroupPoint
   add constraint FK_LMGrpPt_Dev foreign key (DeviceIDUsage)
      references DEVICE (DEVICEID)
go


alter table LMGroupPoint
   add constraint FK_LMGrpPt_LMGrp foreign key (DEVICEID)
      references LMGroup (DeviceID)
go


alter table LMGroupPoint
   add constraint FK_LMGrpPt_Pt foreign key (PointIDUsage)
      references POINT (POINTID)
go


alter table LMHardwareConfiguration
   add constraint FK_LMHrd_LMGr foreign key (AddressingGroupID)
      references LMGroup (DeviceID)
go


alter table LMProgramWebPublishing
   add constraint FK_LMPrWPb_CsWC foreign key (WebsettingsID)
      references CustomerWebConfiguration (ConfigurationID)
go


alter table LMProgramEvent
   add constraint FK_LMPrg_LMPrEv foreign key (LMProgramID)
      references LMPROGRAM (DEVICEID)
go


alter table LMProgramWebPublishing
   add constraint FK_LMprApp_App foreign key (ApplianceCategoryID)
      references ApplianceCategory (ApplianceCategoryID)
go


alter table LMProgramWebPublishing
   add constraint FK_LMprApp_LMPrg foreign key (LMProgramID)
      references LMPROGRAM (DEVICEID)
go


alter table LMCONTROLAREA
   add constraint FK_LmCntAr_YukPAO foreign key (DEVICEID)
      references YukonPAObject (PAObjectID)
go


alter table LMControlHistory
   add constraint FK_LmCtrlHis_YPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID)
go


alter table LMProgramEnergyExchange
   add constraint FK_LmPrg_LmPrEEx foreign key (DeviceID)
      references LMPROGRAM (DEVICEID)
go


alter table LMPROGRAM
   add constraint FK_LmProg_YukPAO foreign key (DEVICEID)
      references YukonPAObject (PAObjectID)
go


alter table LMMACSScheduleOperatorList
   add constraint FK_MCSchLMMcSchOpLs foreign key (ScheduleID)
      references MACSchedule (ScheduleID)
go


alter table EnergyCompanyOperatorLoginList
   add constraint FK_OpLgEnCmpOpLs foreign key (OperatorLoginID)
      references OperatorLogin (LoginID)
go


alter table LMMACSScheduleOperatorList
   add constraint FK_OpLgLMMcSchOpLs foreign key (OperatorLoginID)
      references OperatorLogin (LoginID)
go


alter table OperatorLoginGraphList
   add constraint FK_OpLgOpLgGrLs2 foreign key (OperatorLoginID)
      references OperatorLogin (LoginID)
go


alter table LMDirectOperatorList
   add constraint FK_OpLg_LMDOpLs foreign key (OperatorLoginID)
      references OperatorLogin (LoginID)
go


alter table OperatorSerialGroup
   add constraint FK_OpSGrp_LmGrp foreign key (LMGroupID)
      references LMGroup (DeviceID)
go


alter table OperatorLogin
   add constraint FK_OpSGrp_OpLg foreign key (LoginID)
      references OperatorSerialGroup (LoginID)
go


alter table CapControlFeeder
   add constraint FK_PAObj_CCFeed foreign key (FeederID)
      references YukonPAObject (PAObjectID)
go


alter table POINTUNIT
   add constraint FK_PtUnit_UoM foreign key (UOMID)
      references UNITMEASURE (UOMID)
go


alter table POINT
   add constraint FK_Pt_YukPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID)
go


alter table RAWPOINTHISTORY
   add constraint FK_RawPt_Point foreign key (POINTID)
      references POINT (POINTID)
go


alter table ROUTE
   add constraint FK_Route_DevID foreign key (DeviceID)
      references DEVICE (DEVICEID)
go


alter table ROUTE
   add constraint FK_Route_YukPAO foreign key (ROUTEID)
      references YukonPAObject (PAObjectID)
go


alter table POINT
   add constraint Ref_STATGRP_PT foreign key (STATEGROUPID)
      references STATEGROUP (STATEGROUPID)
go


alter table MACSchedule
   add constraint FK_SchdID_PAOID foreign key (ScheduleID)
      references YukonPAObject (PAObjectID)
go


alter table SiteInformation
   add constraint FK_Sub_Si foreign key (SubstationID)
      references Substation (SubstationID)
go


alter table PAOowner
   add constraint FK_YukPAO_PAOOwn foreign key (ChildID)
      references YukonPAObject (PAObjectID)
go


alter table PAOowner
   add constraint FK_YukPAO_PAOid foreign key (OwnerID)
      references YukonPAObject (PAObjectID)
go


alter table CICustomerBase
   add constraint FK_YukPA_CICust foreign key (DeviceID)
      references YukonPAObject (PAObjectID)
go


alter table POINTSTATUS
   add constraint Ref_ptstatus_pt foreign key (POINTID)
      references POINT (POINTID)
go


alter table POINTUNIT
   add constraint Ref_ptunit_point foreign key (POINTID)
      references POINT (POINTID)
go


alter table CALCBASE
   add constraint SYS_C0013434 foreign key (POINTID)
      references POINT (POINTID)
go


alter table DEVICE2WAYFLAGS
   add constraint SYS_C0013208 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table DEVICECARRIERSETTINGS
   add constraint SYS_C0013216 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table DEVICECBC
   add constraint SYS_C0013459 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table DEVICECBC
   add constraint SYS_C0013460 foreign key (ROUTEID)
      references ROUTE (ROUTEID)
go


alter table DEVICEDIALUPSETTINGS
   add constraint SYS_C0013193 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table DEVICEDIRECTCOMMSETTINGS
   add constraint SYS_C0013186 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table DEVICEDIRECTCOMMSETTINGS
   add constraint SYS_C0013187 foreign key (PORTID)
      references COMMPORT (PORTID)
go


alter table DISPLAY2WAYDATA
   add constraint FK_DISPLAY2W_REF_POINT foreign key (POINTID)
      references POINT (POINTID)
go


alter table DEVICEIDLCREMOTE
   add constraint SYS_C0013241 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table DEVICEIED
   add constraint SYS_C0013245 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table DEVICELOADPROFILE
   add constraint SYS_C0013234 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table DEVICEMCTIEDPORT
   add constraint SYS_C0013253 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table DEVICEMETERGROUP
   add constraint SYS_C0013213 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table DEVICEROUTES
   add constraint SYS_C0013219 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table DEVICEROUTES
   add constraint SYS_C0013220 foreign key (ROUTEID)
      references ROUTE (ROUTEID)
go


alter table DEVICESCANRATE
   add constraint SYS_C0013198 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table DEVICESTATISTICS
   add constraint SYS_C0013229 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table DEVICETAPPAGINGSETTINGS
   add constraint SYS_C0013237 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table CAPBANK
   add constraint SYS_C0013453 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table DISPLAY2WAYDATA
   add constraint SYS_C0013422 foreign key (DISPLAYNUM)
      references DISPLAY (DISPLAYNUM)
go


alter table DISPLAYCOLUMNS
   add constraint SYS_C0013418 foreign key (DISPLAYNUM)
      references DISPLAY (DISPLAYNUM)
go


alter table DISPLAYCOLUMNS
   add constraint SYS_C0013419 foreign key (TYPENUM)
      references COLUMNTYPE (TYPENUM)
go


alter table DYNAMICACCUMULATOR
   add constraint SYS_C0015129 foreign key (POINTID)
      references POINT (POINTID)
go


alter table DYNAMICDEVICESCANDATA
   add constraint SYS_C0015139 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table FDRTRANSLATION
   add constraint SYS_C0015066 foreign key (POINTID)
      references POINT (POINTID)
go


alter table GRAPHDATASERIES
   add constraint GrphDSeri_GrphDefID foreign key (GRAPHDEFINITIONID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID)
go


alter table GRAPHDATASERIES
   add constraint GrphDSeris_ptID foreign key (POINTID)
      references POINT (POINTID)
go


alter table CAPBANK
   add constraint SYS_C0013454 foreign key (CONTROLPOINTID)
      references POINT (POINTID)
go


alter table LMGROUPEMETCON
   add constraint SYS_C0013357 foreign key (ROUTEID)
      references ROUTE (ROUTEID)
go


alter table LMGROUPVERSACOM
   add constraint SYS_C0013367 foreign key (ROUTEID)
      references ROUTE (ROUTEID)
go


alter table MACROROUTE
   add constraint SYS_C0013274 foreign key (ROUTEID)
      references ROUTE (ROUTEID)
go


alter table MACROROUTE
   add constraint SYS_C0013275 foreign key (SINGLEROUTEID)
      references ROUTE (ROUTEID)
go


alter table CAPBANK
   add constraint SYS_C0013455 foreign key (CONTROLDEVICEID)
      references DEVICE (DEVICEID)
go


alter table POINTACCUMULATOR
   add constraint SYS_C0013317 foreign key (POINTID)
      references POINT (POINTID)
go


alter table POINTANALOG
   add constraint SYS_C0013300 foreign key (POINTID)
      references POINT (POINTID)
go


alter table DYNAMICPOINTDISPATCH
   add constraint SYS_C0013331 foreign key (POINTID)
      references POINT (POINTID)
go


alter table POINTLIMITS
   add constraint SYS_C0013289 foreign key (POINTID)
      references POINT (POINTID)
go


alter table PORTDIALUPMODEM
   add constraint SYS_C0013175 foreign key (PORTID)
      references COMMPORT (PORTID)
go


alter table PORTLOCALSERIAL
   add constraint SYS_C0013147 foreign key (PORTID)
      references COMMPORT (PORTID)
go


alter table CAPCONTROLSUBSTATIONBUS
   add constraint SYS_C0013478 foreign key (CurrentWattLoadPointID)
      references POINT (POINTID)
go


alter table PORTRADIOSETTINGS
   add constraint SYS_C0013169 foreign key (PORTID)
      references COMMPORT (PORTID)
go


alter table PORTSETTINGS
   add constraint SYS_C0013156 foreign key (PORTID)
      references COMMPORT (PORTID)
go


alter table PORTSTATISTICS
   add constraint SYS_C0013183 foreign key (PORTID)
      references COMMPORT (PORTID)
go


alter table PORTTERMINALSERVER
   add constraint SYS_C0013151 foreign key (PORTID)
      references COMMPORT (PORTID)
go


alter table PORTTIMING
   add constraint SYS_C0013163 foreign key (PORTID)
      references COMMPORT (PORTID)
go


alter table REPEATERROUTE
   add constraint SYS_C0013269 foreign key (ROUTEID)
      references ROUTE (ROUTEID)
go


alter table REPEATERROUTE
   add constraint SYS_C0013270 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table CAPCONTROLSUBSTATIONBUS
   add constraint SYS_C0013479 foreign key (CurrentVarLoadPointID)
      references POINT (POINTID)
go


alter table STATE
   add constraint SYS_C0013342 foreign key (STATEGROUPID)
      references STATEGROUP (STATEGROUPID)
go


alter table SYSTEMLOG
   add constraint SYS_C0013408 foreign key (POINTID)
      references POINT (POINTID)
go


alter table TEMPLATECOLUMNS
   add constraint SYS_C0013429 foreign key (TEMPLATENUM)
      references TEMPLATE (TEMPLATENUM)
go


alter table TEMPLATECOLUMNS
   add constraint SYS_C0013430 foreign key (TYPENUM)
      references COLUMNTYPE (TYPENUM)
go


alter table CARRIERROUTE
   add constraint SYS_C0013264 foreign key (ROUTEID)
      references ROUTE (ROUTEID)
go


alter table VERSACOMROUTE
   add constraint FK_VER_ROUT_ROU foreign key (ROUTEID)
      references ROUTE (ROUTEID)
go


alter table LMHardwareBase
   add constraint FK_LMH_ISA__INV foreign key (InventoryID)
      references InventoryBase (InventoryID)
go


alter table AirConditioner
   add constraint FK_AIR_ISA__APP foreign key (ApplianceID)
      references ApplianceBase (ApplianceID)
go


