/*==============================================================*/
/* Database name:  YukonDatabase                                */
/* DBMS name:      CTI SqlServer 2000                           */
/* Created on:     5/19/2004 9:58:46 AM                         */
/*==============================================================*/


if exists (select 1
            from  sysobjects
           where  id = object_id('DISPLAY2WAYDATA_VIEW')
            and   type = 'V')
   drop view DISPLAY2WAYDATA_VIEW
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ExpressComAddress_View')
            and   type = 'V')
   drop view ExpressComAddress_View
go


if exists (select 1
            from  sysobjects
           where  id = object_id('FeederAddress_View')
            and   type = 'V')
   drop view FeederAddress_View
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
           where  id = object_id('GeoAddress_View')
            and   type = 'V')
   drop view GeoAddress_View
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
           where  id = object_id('LMProgram_View')
            and   type = 'V')
   drop view LMProgram_View
go


if exists (select 1
            from  sysobjects
           where  id = object_id('Peakpointhistory_View')
            and   type = 'V')
   drop view Peakpointhistory_View
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
           where  id = object_id('ProgramAddress_View')
            and   type = 'V')
   drop view ProgramAddress_View
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ServiceAddress_View')
            and   type = 'V')
   drop view ServiceAddress_View
go


if exists (select 1
            from  sysobjects
           where  id = object_id('SubstationAddress_View')
            and   type = 'V')
   drop view SubstationAddress_View
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ActivityLog')
            and   type = 'U')
   drop table ActivityLog
go


if exists (select 1
            from  sysobjects
           where  id = object_id('Address')
            and   type = 'U')
   drop table Address
go


if exists (select 1
            from  sysobjects
           where  id = object_id('AlarmCategory')
            and   type = 'U')
   drop table AlarmCategory
go


if exists (select 1
            from  sysobjects
           where  id = object_id('BaseLine')
            and   type = 'U')
   drop table BaseLine
go


if exists (select 1
            from  sysobjects
           where  id = object_id('BillingFileFormats')
            and   type = 'U')
   drop table BillingFileFormats
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CALCBASE')
            and   type = 'U')
   drop table CALCBASE
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
           where  id = object_id('CTIDatabase')
            and   type = 'U')
   drop table CTIDatabase
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CalcPointBaseline')
            and   type = 'U')
   drop table CalcPointBaseline
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CapControlFeeder')
            and   type = 'U')
   drop table CapControlFeeder
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CarrierRoute')
            and   type = 'U')
   drop table CarrierRoute
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CommErrorHistory')
            and   type = 'U')
   drop table CommErrorHistory
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CommPort')
            and   type = 'U')
   drop table CommPort
go


if exists (select 1
            from  sysobjects
           where  id = object_id('Contact')
            and   type = 'U')
   drop table Contact
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ContactNotification')
            and   type = 'U')
   drop table ContactNotification
go


if exists (select 1
            from  sysobjects
           where  id = object_id('Customer')
            and   type = 'U')
   drop table Customer
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerAdditionalContact')
            and   type = 'U')
   drop table CustomerAdditionalContact
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerBaseLinePoint')
            and   type = 'U')
   drop table CustomerBaseLinePoint
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerLoginSerialGroup')
            and   type = 'U')
   drop table CustomerLoginSerialGroup
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
           where  id = object_id('DEVICEDIALUPSETTINGS')
            and   type = 'U')
   drop table DEVICEDIALUPSETTINGS
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
           where  id = object_id('DEVICESCANRATE')
            and   type = 'U')
   drop table DEVICESCANRATE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DEVICETAPPAGINGSETTINGS')
            and   type = 'U')
   drop table DEVICETAPPAGINGSETTINGS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DISPLAY')
            and   type = 'U')
   drop table DISPLAY
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
           where  id = object_id('DeviceAddress')
            and   type = 'U')
   drop table DeviceAddress
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DeviceCBC')
            and   type = 'U')
   drop table DeviceCBC
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DeviceCustomerList')
            and   type = 'U')
   drop table DeviceCustomerList
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DeviceDirectCommSettings')
            and   type = 'U')
   drop table DeviceDirectCommSettings
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DeviceRTC')
            and   type = 'U')
   drop table DeviceRTC
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DeviceRoutes')
            and   type = 'U')
   drop table DeviceRoutes
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DeviceSeries5RTU')
            and   type = 'U')
   drop table DeviceSeries5RTU
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
           where  id = object_id('DynamicPAOStatistics')
            and   type = 'U')
   drop table DynamicPAOStatistics
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DynamicPointAlarming')
            and   type = 'U')
   drop table DynamicPointAlarming
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DynamicTags')
            and   type = 'U')
   drop table DynamicTags
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
            from  sysobjects
           where  id = object_id('FDRTRANSLATION')
            and   type = 'U')
   drop table FDRTRANSLATION
go


if exists (select 1
            from  sysobjects
           where  id = object_id('FDRTelegyrGroup')
            and   type = 'U')
   drop table FDRTelegyrGroup
go


if exists (select 1
            from  sysobjects
           where  id = object_id('GRAPHDATASERIES')
            and   type = 'U')
   drop table GRAPHDATASERIES
go


if exists (select 1
            from  sysobjects
           where  id = object_id('GRAPHDEFINITION')
            and   type = 'U')
   drop table GRAPHDEFINITION
go


if exists (select 1
            from  sysobjects
           where  id = object_id('GatewayEndDevice')
            and   type = 'U')
   drop table GatewayEndDevice
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
            from  sysobjects
           where  id = object_id('HolidaySchedule')
            and   type = 'U')
   drop table HolidaySchedule
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
            from  sysobjects
           where  id = object_id('LMControlArea')
            and   type = 'U')
   drop table LMControlArea
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMControlHistory')
            and   type = 'U')
   drop table LMControlHistory
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMControlScenarioProgram')
            and   type = 'U')
   drop table LMControlScenarioProgram
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMCurtailCustomerActivity')
            and   type = 'U')
   drop table LMCurtailCustomerActivity
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMCurtailProgramActivity')
            and   type = 'U')
   drop table LMCurtailProgramActivity
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMDirectCustomerList')
            and   type = 'U')
   drop table LMDirectCustomerList
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMDirectNotifGrpList')
            and   type = 'U')
   drop table LMDirectNotifGrpList
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
           where  id = object_id('LMGroup')
            and   type = 'U')
   drop table LMGroup
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMGroupEmetcon')
            and   type = 'U')
   drop table LMGroupEmetcon
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMGroupExpressCom')
            and   type = 'U')
   drop table LMGroupExpressCom
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMGroupExpressComAddress')
            and   type = 'U')
   drop table LMGroupExpressComAddress
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMGroupMCT')
            and   type = 'U')
   drop table LMGroupMCT
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
           where  id = object_id('LMGroupSA205105')
            and   type = 'U')
   drop table LMGroupSA205105
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMGroupSA305')
            and   type = 'U')
   drop table LMGroupSA305
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMGroupSASimple')
            and   type = 'U')
   drop table LMGroupSASimple
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMGroupVersacom')
            and   type = 'U')
   drop table LMGroupVersacom
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
           where  id = object_id('LMProgramConstraints')
            and   type = 'U')
   drop table LMProgramConstraints
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
           where  id = object_id('LMThermoStatGear')
            and   type = 'U')
   drop table LMThermoStatGear
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
           where  id = object_id('MCTBroadCastMapping')
            and   type = 'U')
   drop table MCTBroadCastMapping
go


if exists (select 1
            from  sysobjects
           where  id = object_id('MCTConfig')
            and   type = 'U')
   drop table MCTConfig
go


if exists (select 1
            from  sysobjects
           where  id = object_id('MCTConfigMapping')
            and   type = 'U')
   drop table MCTConfigMapping
go


if exists (select 1
            from  sysobjects
           where  id = object_id('NotificationDestination')
            and   type = 'U')
   drop table NotificationDestination
go


if exists (select 1
            from  sysobjects
           where  id = object_id('NotificationGroup')
            and   type = 'U')
   drop table NotificationGroup
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
           where  id = object_id('PAOExclusion')
            and   type = 'U')
   drop table PAOExclusion
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PAOowner')
            and   type = 'U')
   drop table PAOowner
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
           where  id = object_id('PORTTERMINALSERVER')
            and   type = 'U')
   drop table PORTTERMINALSERVER
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PointAlarming')
            and   type = 'U')
   drop table PointAlarming
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PortTiming')
            and   type = 'U')
   drop table PortTiming
go


if exists (select 1
            from  sysobjects
           where  id = object_id('RAWPOINTHISTORY')
            and   type = 'U')
   drop table RAWPOINTHISTORY
go


if exists (select 1
            from  sysobjects
           where  id = object_id('RepeaterRoute')
            and   type = 'U')
   drop table RepeaterRoute
go


if exists (select 1
            from  sysobjects
           where  id = object_id('Route')
            and   type = 'U')
   drop table Route
go


if exists (select 1
            from  sysobjects
           where  id = object_id('STATE')
            and   type = 'U')
   drop table STATE
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
           where  id = object_id('SeasonSchedule')
            and   type = 'U')
   drop table SeasonSchedule
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
           where  id = object_id('TagLog')
            and   type = 'U')
   drop table TagLog
go


if exists (select 1
            from  sysobjects
           where  id = object_id('Tags')
            and   type = 'U')
   drop table Tags
go


if exists (select 1
            from  sysobjects
           where  id = object_id('UNITMEASURE')
            and   type = 'U')
   drop table UNITMEASURE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('VersacomRoute')
            and   type = 'U')
   drop table VersacomRoute
go


if exists (select 1
            from  sysobjects
           where  id = object_id('YukonGroup')
            and   type = 'U')
   drop table YukonGroup
go


if exists (select 1
            from  sysobjects
           where  id = object_id('YukonGroupRole')
            and   type = 'U')
   drop table YukonGroupRole
go


if exists (select 1
            from  sysobjects
           where  id = object_id('YukonImage')
            and   type = 'U')
   drop table YukonImage
go


if exists (select 1
            from  sysobjects
           where  id = object_id('YukonListEntry')
            and   type = 'U')
   drop table YukonListEntry
go


if exists (select 1
            from  sysobjects
           where  id = object_id('YukonPAObject')
            and   type = 'U')
   drop table YukonPAObject
go


if exists (select 1
            from  sysobjects
           where  id = object_id('YukonRole')
            and   type = 'U')
   drop table YukonRole
go


if exists (select 1
            from  sysobjects
           where  id = object_id('YukonRoleProperty')
            and   type = 'U')
   drop table YukonRoleProperty
go


if exists (select 1
            from  sysobjects
           where  id = object_id('YukonSelectionList')
            and   type = 'U')
   drop table YukonSelectionList
go


if exists (select 1
            from  sysobjects
           where  id = object_id('YukonServices')
            and   type = 'U')
   drop table YukonServices
go


if exists (select 1
            from  sysobjects
           where  id = object_id('YukonUser')
            and   type = 'U')
   drop table YukonUser
go


if exists (select 1
            from  sysobjects
           where  id = object_id('YukonUserGroup')
            and   type = 'U')
   drop table YukonUserGroup
go


if exists (select 1
            from  sysobjects
           where  id = object_id('YukonUserRole')
            and   type = 'U')
   drop table YukonUserRole
go


if exists (select 1
            from  sysobjects
           where  id = object_id('YukonWebConfiguration')
            and   type = 'U')
   drop table YukonWebConfiguration
go


/*==============================================================*/
/* Table : ActivityLog                                          */
/*==============================================================*/
create table ActivityLog (
ActivityLogID        numeric              not null,
TimeStamp            datetime             not null,
UserID               numeric              null,
AccountID            numeric              null,
EnergyCompanyID      numeric              null,
CustomerID           numeric              null,
PaoID                numeric              null,
Action               varchar(80)          not null,
Description          varchar(120)         not null
)
go


alter table ActivityLog
   add constraint PK_ACTIVITYLOG primary key  (ActivityLogID)
go


/*==============================================================*/
/* Table : Address                                              */
/*==============================================================*/
create table Address (
AddressID            numeric              not null,
LocationAddress1     varchar(40)          not null,
LocationAddress2     varchar(40)          not null,
CityName             varchar(32)          not null,
StateCode            char(2)              not null,
ZipCode              varchar(12)          not null,
County               varchar(30)          not null
)
go


insert into address values ( 0, '(none)', '(none)', '(none)', 'MN', '(none)', '(none)' );

alter table Address
   add constraint PK_ADDRESS primary key  (AddressID)
go


/*==============================================================*/
/* Table : AlarmCategory                                        */
/*==============================================================*/
create table AlarmCategory (
AlarmCategoryID      numeric              not null,
CategoryName         varchar(40)          not null,
NotificationGroupID  numeric              not null
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

alter table AlarmCategory
   add constraint PK_ALARMCATEGORYID primary key  (AlarmCategoryID)
go


/*==============================================================*/
/* Table : BaseLine                                             */
/*==============================================================*/
create table BaseLine (
BaselineID           numeric              not null,
BaselineName         varchar(30)          not null,
DaysUsed             numeric              not null,
PercentWindow        numeric              not null,
CalcDays             numeric              not null,
ExcludedWeekDays     char(7)              not null,
HolidaysUsed         numeric              not null
)
go


insert into baseline values (1, 'Default Baseline', 30, 75, 5, 'YNNNNNY', 0);

alter table BaseLine
   add constraint PK_BASELINE primary key  (BaselineID)
go


/*==============================================================*/
/* Table : BillingFileFormats                                   */
/*==============================================================*/
create table BillingFileFormats (
FormatID             numeric              not null,
FormatType           varchar(30)          not null
)
go


insert into billingfileformats values( -11, 'MV_90 DATA Import');
insert into BillingFileFormats values(-1,'INVALID');
insert into BillingFileFormats values(0,'SEDC');
insert into BillingFileFormats values(1,'CADP');
insert into BillingFileFormats values(2,'CADPXL2');
insert into BillingFileFormats values(3,'WLT-40');
insert into BillingFileFormats values(4,'CTI-CSV');
insert into BillingFileFormats values(5,'OPU');
insert into BillingFileFormats values(6,'DAFRON');
insert into BillingFileFormats values(7,'NCDC');
insert into billingFileformats values (9, 'CTI2');
insert into billingfileformats values( 12, 'SEDC 5.4');
insert into billingfileformats values( 13, 'NISC-Turtle');
insert into billingfileformats values( 14, 'NISC-NCDC');


alter table BillingFileFormats
   add constraint PK_BILLINGFILEFORMATS primary key  (FormatID)
go


/*==============================================================*/
/* Table : CALCBASE                                             */
/*==============================================================*/
create table CALCBASE (
POINTID              numeric              not null,
UPDATETYPE           varchar(16)          not null,
PERIODICRATE         numeric              not null
)
go


alter table CALCBASE
   add constraint PK_CALCBASE primary key  (POINTID)
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
FUNCTIONNAME         varchar(20)          null
)
go


alter table CALCCOMPONENT
   add constraint PK_CALCCOMPONENT primary key  (PointID, COMPONENTORDER)
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
RecloseDelay         numeric              not null
)
go


alter table CAPBANK
   add constraint PK_CAPBANK primary key  (DEVICEID)
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
UpperBandwidth       float                not null,
CONTROLINTERVAL      numeric              not null,
MINRESPONSETIME      numeric              not null,
MINCONFIRMPERCENT    numeric              not null,
FAILUREPERCENT       numeric              not null,
DAYSOFWEEK           char(8)              not null,
MapLocationID        numeric              not null,
LowerBandwidth       float                not null,
ControlUnits         varchar(20)          not null,
ControlDelayTime     numeric              not null,
ControlSendRetries   numeric              not null
)
go


alter table CAPCONTROLSUBSTATIONBUS
   add constraint SYS_C0013476 primary key  (SubstationBusID)
go


/*==============================================================*/
/* Index: Indx_CSUBVPT                                          */
/*==============================================================*/
create   index Indx_CSUBVPT on CAPCONTROLSUBSTATIONBUS (
CurrentVarLoadPointID
)
go


/*==============================================================*/
/* Table : CCFeederBankList                                     */
/*==============================================================*/
create table CCFeederBankList (
FeederID             numeric              not null,
DeviceID             numeric              not null,
ControlOrder         numeric              not null
)
go


alter table CCFeederBankList
   add constraint PK_CCFEEDERBANKLIST primary key  (FeederID, DeviceID)
go


/*==============================================================*/
/* Table : CCFeederSubAssignment                                */
/*==============================================================*/
create table CCFeederSubAssignment (
SubStationBusID      numeric              not null,
FeederID             numeric              not null,
DisplayOrder         numeric              not null
)
go


alter table CCFeederSubAssignment
   add constraint PK_CCFEEDERSUBASSIGNMENT primary key  (SubStationBusID, FeederID)
go


/*==============================================================*/
/* Table : CICustomerBase                                       */
/*==============================================================*/
create table CICustomerBase (
CustomerID           numeric              not null,
MainAddressID        numeric              not null,
CustomerDemandLevel  float                not null,
CurtailmentAgreement varchar(100)         not null,
CurtailAmount        float                not null,
CompanyName          varchar(80)          not null
)
go


alter table CICustomerBase
   add constraint PK_CICUSTOMERBASE primary key  (CustomerID)
go


/*==============================================================*/
/* Table : COLUMNTYPE                                           */
/*==============================================================*/
create table COLUMNTYPE (
TYPENUM              numeric              not null,
NAME                 varchar(20)          not null
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
insert into columntype values (13, 'State');
insert into columntype values (14, 'PointImage' );
insert into columntype values (15, 'QualityCount' );

alter table COLUMNTYPE
   add constraint SYS_C0013414 primary key  (TYPENUM)
go


/*==============================================================*/
/* Table : CTIDatabase                                          */
/*==============================================================*/
create table CTIDatabase (
Version              varchar(6)           not null,
CTIEmployeeName      varchar(30)          not null,
DateApplied          datetime             null,
Notes                varchar(300)         null,
Build                numeric              not null
)
go


insert into CTIDatabase values('3.01', 'Ryan', '10-MAY-2004', 'Added many load control and protocol changes', 1);

alter table CTIDatabase
   add constraint PK_CTIDATABASE primary key  (Version)
go


/*==============================================================*/
/* Table : CalcPointBaseline                                    */
/*==============================================================*/
create table CalcPointBaseline (
PointID              numeric              not null,
BaselineID           numeric              not null
)
go


alter table CalcPointBaseline
   add constraint PK_CalcBsPt primary key  (PointID)
go


/*==============================================================*/
/* Table : CapControlFeeder                                     */
/*==============================================================*/
create table CapControlFeeder (
FeederID             numeric              not null,
PeakSetPoint         float                not null,
OffPeakSetPoint      float                not null,
UpperBandwidth       float                not null,
CurrentVarLoadPointID numeric              not null,
CurrentWattLoadPointID numeric              not null,
MapLocationID        numeric              not null,
LowerBandwidth       float                not null
)
go


alter table CapControlFeeder
   add constraint PK_CAPCONTROLFEEDER primary key  (FeederID)
go


/*==============================================================*/
/* Index: Indx_CPCNFDVARPT                                      */
/*==============================================================*/
create   index Indx_CPCNFDVARPT on CapControlFeeder (
CurrentVarLoadPointID
)
go


/*==============================================================*/
/* Table : CarrierRoute                                         */
/*==============================================================*/
create table CarrierRoute (
ROUTEID              numeric              not null,
BUSNUMBER            numeric              not null,
CCUFIXBITS           numeric              not null,
CCUVARIABLEBITS      numeric              not null,
UserLocked           char(1)              not null,
ResetRptSettings     char(1)              not null
)
go


alter table CarrierRoute
   add constraint PK_CARRIERROUTE primary key  (ROUTEID)
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
InMessage            varchar(160)         not null
)
go


alter table CommErrorHistory
   add constraint PK_COMMERRORHISTORY primary key  (CommErrorID)
go


/*==============================================================*/
/* Table : CommPort                                             */
/*==============================================================*/
create table CommPort (
PORTID               numeric              not null,
ALARMINHIBIT         varchar(1)           not null
     constraint SYS_C0013108 check ("ALARMINHIBIT" IS NOT NULL),
COMMONPROTOCOL       varchar(8)           not null
     constraint SYS_C0013109 check ("COMMONPROTOCOL" IS NOT NULL),
PERFORMTHRESHOLD     numeric              not null
     constraint SYS_C0013110 check ("PERFORMTHRESHOLD" IS NOT NULL),
PERFORMANCEALARM     varchar(1)           not null
     constraint SYS_C0013111 check ("PERFORMANCEALARM" IS NOT NULL),
SharedPortType       varchar(20)          not null,
SharedSocketNumber   numeric              not null
)
go


alter table CommPort
   add constraint SYS_C0013112 primary key  (PORTID)
go


/*==============================================================*/
/* Table : Contact                                              */
/*==============================================================*/
create table Contact (
ContactID            numeric              not null,
ContFirstName        varchar(20)          not null,
ContLastName         varchar(32)          not null,
LogInID              numeric              not null,
AddressID            numeric              not null
)
go


insert into contact values ( 0, '(none)', '(none)', -1, 0 );

alter table Contact
   add constraint PK_CONTACT primary key  (ContactID)
go


/*==============================================================*/
/* Table : ContactNotification                                  */
/*==============================================================*/
create table ContactNotification (
ContactNotifID       numeric              not null,
ContactID            numeric              not null,
NotificationCategoryID numeric              not null,
DisableFlag          char(1)              not null,
Notification         varchar(130)         not null
)
go


insert into ContactNotification values( 0, 0, 0, 'N', '(none)' );


alter table ContactNotification
   add constraint PK_CONTACTNOTIFICATION primary key  (ContactNotifID)
go


/*==============================================================*/
/* Table : Customer                                             */
/*==============================================================*/
create table Customer (
CustomerID           numeric              not null,
PrimaryContactID     numeric              not null,
CustomerTypeID       numeric              not null,
TimeZone             varchar(40)          not null
)
go


INSERT INTO Customer VALUES (-1,0,0,'(none)');

alter table Customer
   add constraint PK_CUSTOMER primary key  (CustomerID)
go


/*==============================================================*/
/* Table : CustomerAdditionalContact                            */
/*==============================================================*/
create table CustomerAdditionalContact (
CustomerID           numeric              not null,
ContactID            numeric              not null
)
go


alter table CustomerAdditionalContact
   add constraint PK_CUSTOMERADDITIONALCONTACT primary key  (ContactID, CustomerID)
go


/*==============================================================*/
/* Table : CustomerBaseLinePoint                                */
/*==============================================================*/
create table CustomerBaseLinePoint (
CustomerID           numeric              not null,
PointID              numeric              not null
)
go


alter table CustomerBaseLinePoint
   add constraint PK_CUSTOMERBASELINEPOINT primary key  (CustomerID, PointID)
go


/*==============================================================*/
/* Table : CustomerLoginSerialGroup                             */
/*==============================================================*/
create table CustomerLoginSerialGroup (
LoginID              numeric              not null,
LMGroupID            numeric              not null
)
go


alter table CustomerLoginSerialGroup
   add constraint PK_CUSTOMERLOGINSERIALGROUP primary key  (LoginID, LMGroupID)
go


/*==============================================================*/
/* Table : DEVICE                                               */
/*==============================================================*/
create table DEVICE (
DEVICEID             numeric              not null,
ALARMINHIBIT         varchar(1)           not null,
CONTROLINHIBIT       varchar(1)           not null
)
go


INSERT into device values (0, 'N', 'N');

alter table DEVICE
   add constraint PK_DEV_DEVICEID2 primary key  (DEVICEID)
go


/*==============================================================*/
/* Table : DEVICE2WAYFLAGS                                      */
/*==============================================================*/
create table DEVICE2WAYFLAGS (
DEVICEID             numeric              not null,
MONTHLYSTATS         varchar(1)           not null
     constraint SYS_C0013200 check ("MONTHLYSTATS" IS NOT NULL),
TWENTYFOURHOURSTATS  varchar(1)           not null
     constraint SYS_C0013201 check ("TWENTYFOURHOURSTATS" IS NOT NULL),
HOURLYSTATS          varchar(1)           not null
     constraint SYS_C0013202 check ("HOURLYSTATS" IS NOT NULL),
FAILUREALARM         varchar(1)           not null
     constraint SYS_C0013203 check ("FAILUREALARM" IS NOT NULL),
PERFORMANCETHRESHOLD numeric              not null
     constraint SYS_C0013204 check ("PERFORMANCETHRESHOLD" IS NOT NULL),
PERFORMANCEALARM     varchar(1)           not null
     constraint SYS_C0013205 check ("PERFORMANCEALARM" IS NOT NULL),
PERFORMANCETWENTYFOURALARM varchar(1)           not null
     constraint SYS_C0013206 check ("PERFORMANCETWENTYFOURALARM" IS NOT NULL)
)
go


alter table DEVICE2WAYFLAGS
   add constraint PK_DEVICE2WAYFLAGS primary key  (DEVICEID)
go


/*==============================================================*/
/* Table : DEVICECARRIERSETTINGS                                */
/*==============================================================*/
create table DEVICECARRIERSETTINGS (
DEVICEID             numeric              not null,
ADDRESS              numeric              not null
     constraint SYS_C0013215 check ("ADDRESS" IS NOT NULL)
)
go


alter table DEVICECARRIERSETTINGS
   add constraint PK_DEVICECARRIERSETTINGS primary key  (DEVICEID)
go


/*==============================================================*/
/* Table : DEVICEDIALUPSETTINGS                                 */
/*==============================================================*/
create table DEVICEDIALUPSETTINGS (
DEVICEID             numeric              not null,
PHONENUMBER          varchar(40)          not null
     constraint SYS_C0013189 check ("PHONENUMBER" IS NOT NULL),
MINCONNECTTIME       numeric              not null
     constraint SYS_C0013190 check ("MINCONNECTTIME" IS NOT NULL),
MAXCONNECTTIME       numeric              not null
     constraint SYS_C0013191 check ("MAXCONNECTTIME" IS NOT NULL),
LINESETTINGS         varchar(8)           not null
     constraint SYS_C0013192 check ("LINESETTINGS" IS NOT NULL),
BaudRate             numeric              not null
)
go


alter table DEVICEDIALUPSETTINGS
   add constraint PK_DEVICEDIALUPSETTINGS primary key  (DEVICEID)
go


/*==============================================================*/
/* Table : DEVICEIDLCREMOTE                                     */
/*==============================================================*/
create table DEVICEIDLCREMOTE (
DEVICEID             numeric              not null,
ADDRESS              numeric              not null
     constraint SYS_C0013239 check ("ADDRESS" IS NOT NULL),
POSTCOMMWAIT         numeric              not null
     constraint SYS_C0013240 check ("POSTCOMMWAIT" IS NOT NULL),
CCUAmpUseType        varchar(20)          not null
)
go


alter table DEVICEIDLCREMOTE
   add constraint PK_DEVICEIDLCREMOTE primary key  (DEVICEID)
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


alter table DEVICEIED
   add constraint PK_DEVICEIED primary key  (DEVICEID)
go


/*==============================================================*/
/* Table : DEVICELOADPROFILE                                    */
/*==============================================================*/
create table DEVICELOADPROFILE (
DEVICEID             numeric              not null,
LASTINTERVALDEMANDRATE numeric              not null,
LOADPROFILEDEMANDRATE numeric              not null,
LOADPROFILECOLLECTION varchar(4)           not null,
VoltageDmdInterval   numeric              not null,
VoltageDmdRate       numeric              not null
)
go


alter table DEVICELOADPROFILE
   add constraint PK_DEVICELOADPROFILE primary key  (DEVICEID)
go


/*==============================================================*/
/* Table : DEVICEMCTIEDPORT                                     */
/*==============================================================*/
create table DEVICEMCTIEDPORT (
DEVICEID             numeric              not null,
CONNECTEDIED         varchar(20)          not null
     constraint SYS_C0013247 check ("CONNECTEDIED" IS NOT NULL),
IEDSCANRATE          numeric              not null
     constraint SYS_C0013248 check ("IEDSCANRATE" IS NOT NULL),
DEFAULTDATACLASS     numeric              not null
     constraint SYS_C0013249 check ("DEFAULTDATACLASS" IS NOT NULL),
DEFAULTDATAOFFSET    numeric              not null
     constraint SYS_C0013250 check ("DEFAULTDATAOFFSET" IS NOT NULL),
PASSWORD             varchar(6)           not null
     constraint SYS_C0013251 check ("PASSWORD" IS NOT NULL),
REALTIMESCAN         varchar(1)           not null
     constraint SYS_C0013252 check ("REALTIMESCAN" IS NOT NULL)
)
go


alter table DEVICEMCTIEDPORT
   add constraint PK_DEVICEMCTIEDPORT primary key  (DEVICEID)
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


alter table DEVICEMETERGROUP
   add constraint PK_DEVICEMETERGROUP primary key  (DEVICEID)
go


/*==============================================================*/
/* Table : DEVICESCANRATE                                       */
/*==============================================================*/
create table DEVICESCANRATE (
DEVICEID             numeric              not null,
SCANTYPE             varchar(20)          not null
     constraint SYS_C0013195 check ("SCANTYPE" IS NOT NULL),
INTERVALRATE         numeric              not null
     constraint SYS_C0013196 check ("INTERVALRATE" IS NOT NULL),
SCANGROUP            numeric              not null
     constraint SYS_C0013197 check ("SCANGROUP" IS NOT NULL),
AlternateRate        numeric              not null
)
go


alter table DEVICESCANRATE
   add constraint PK_DEVICESCANRATE primary key  (DEVICEID, SCANTYPE)
go


/*==============================================================*/
/* Table : DEVICETAPPAGINGSETTINGS                              */
/*==============================================================*/
create table DEVICETAPPAGINGSETTINGS (
DEVICEID             numeric              not null,
PAGERNUMBER          varchar(20)          not null
)
go


alter table DEVICETAPPAGINGSETTINGS
   add constraint PK_DEVICETAPPAGINGSETTINGS primary key  (DEVICEID)
go


/*==============================================================*/
/* Table : DISPLAY                                              */
/*==============================================================*/
create table DISPLAY (
DISPLAYNUM           numeric              not null,
NAME                 varchar(40)          not null,
TYPE                 varchar(40)          not null,
TITLE                varchar(30)          null,
DESCRIPTION          varchar(200)         null
)
go


insert into display values(-4, 'Yukon Server', 'Static Displays', 'Yukon Servers', 'com.cannontech.tdc.windows.WinServicePanel');
insert into display values(-1, 'All Categories', 'Scheduler Client', 'Metering And Control Scheduler', 'com.cannontech.macs.gui.Scheduler');

/**insert into display values(-2, 'All Areas', 'Cap Control Client', 'Cap Control', 'com.cannontech.cbc.gui.StrategyReceiver');**/
/**insert into display values(-3, 'All Control Areas', 'Load Management Client', 'Load Management', 'com.cannontech.loadcontrol.gui.LoadControlMainPanel');**/
/**insert into display values(2, 'Historical Viewer', 'Alarms and Events', 'Historical Event Viewer', 'This display will allow the user to select a range of dates and show the events that occured.');**/
/**insert into display values(3, 'Raw Point Viewer', 'Alarms and Events', 'Current Raw Point Viewer', 'This display will recieve current raw point updates as they happen in the system.');**/


insert into display values(1, 'Event Viewer', 'Alarms and Events', 'Current Event Viewer', 'This display will recieve current events as they happen in the system.');
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
insert into display values(14, 'Priority 10 Alarms', 'Alarms and Events', 'Priority 10 Alarm Viewer', 'This display will recieve all priority 10 alarm events as they happen in the system.');
insert into display values(15, 'Priority 11 Alarms', 'Alarms and Events', 'Priority 11 Alarm Viewer', 'This display will recieve all priority 11 alarm events as they happen in the system.');
insert into display values(16, 'Priority 12 Alarms', 'Alarms and Events', 'Priority 12 Alarm Viewer', 'This display will recieve all priority 12 alarm events as they happen in the system.');
insert into display values(17, 'Priority 13 Alarms', 'Alarms and Events', 'Priority 13 Alarm Viewer', 'This display will recieve all priority 13 alarm events as they happen in the system.');
insert into display values(18, 'Priority 14 Alarms', 'Alarms and Events', 'Priority 14 Alarm Viewer', 'This display will recieve all priority 14 alarm events as they happen in the system.');
insert into display values(19, 'Priority 15 Alarms', 'Alarms and Events', 'Priority 15 Alarm Viewer', 'This display will recieve all priority 15 alarm events as they happen in the system.');
insert into display values(20, 'Priority 16 Alarms', 'Alarms and Events', 'Priority 16 Alarm Viewer', 'This display will recieve all priority 16 alarm events as they happen in the system.');
insert into display values(21, 'Priority 17 Alarms', 'Alarms and Events', 'Priority 17 Alarm Viewer', 'This display will recieve all priority 17 alarm events as they happen in the system.');
insert into display values(22, 'Priority 18 Alarms', 'Alarms and Events', 'Priority 18 Alarm Viewer', 'This display will recieve all priority 18 alarm events as they happen in the system.');
insert into display values(23, 'Priority 19 Alarms', 'Alarms and Events', 'Priority 19 Alarm Viewer', 'This display will recieve all priority 19 alarm events as they happen in the system.');
insert into display values(24, 'Priority 20 Alarms', 'Alarms and Events', 'Priority 20 Alarm Viewer', 'This display will recieve all priority 20 alarm events as they happen in the system.');
insert into display values(25, 'Priority 21 Alarms', 'Alarms and Events', 'Priority 21 Alarm Viewer', 'This display will recieve all priority 21 alarm events as they happen in the system.');
insert into display values(26, 'Priority 22 Alarms', 'Alarms and Events', 'Priority 22 Alarm Viewer', 'This display will recieve all priority 22 alarm events as they happen in the system.');
insert into display values(27, 'Priority 23 Alarms', 'Alarms and Events', 'Priority 23 Alarm Viewer', 'This display will recieve all priority 23 alarm events as they happen in the system.');
insert into display values(28, 'Priority 24 Alarms', 'Alarms and Events', 'Priority 24 Alarm Viewer', 'This display will recieve all priority 24 alarm events as they happen in the system.');
insert into display values(29, 'Priority 25 Alarms', 'Alarms and Events', 'Priority 25 Alarm Viewer', 'This display will recieve all priority 25 alarm events as they happen in the system.');
insert into display values(30, 'Priority 26 Alarms', 'Alarms and Events', 'Priority 26 Alarm Viewer', 'This display will recieve all priority 26 alarm events as they happen in the system.');
insert into display values(31, 'Priority 27 Alarms', 'Alarms and Events', 'Priority 27 Alarm Viewer', 'This display will recieve all priority 27 alarm events as they happen in the system.');
insert into display values(32, 'Priority 28 Alarms', 'Alarms and Events', 'Priority 28 Alarm Viewer', 'This display will recieve all priority 28 alarm events as they happen in the system.');
insert into display values(33, 'Priority 29 Alarms', 'Alarms and Events', 'Priority 29 Alarm Viewer', 'This display will recieve all priority 29 alarm events as they happen in the system.');
insert into display values(34, 'Priority 30 Alarms', 'Alarms and Events', 'Priority 30 Alarm Viewer', 'This display will recieve all priority 30 alarm events as they happen in the system.');
insert into display values(35, 'Priority 31 Alarms', 'Alarms and Events', 'Priority 31 Alarm Viewer', 'This display will recieve all priority 31 alarm events as they happen in the system.');

insert into display values(50, 'SOE Log', 'Alarms and Events', 'SOE Log Viewer', 'This display shows all the SOE events in the SOE log table for a given day.');
insert into display values(51, 'TAG Log', 'Alarms and Events', 'TAG Log Viewer', 'This display shows all the TAG events in the TAG log table for a given day.');

insert into display values(99, 'Your Custom Display', 'Custom Displays', 'Edit This Display', 'This display is is used to show what a user created display looks like. You may edit this display to fit your own needs.');


alter table DISPLAY
   add constraint SYS_C0013412 primary key  (DISPLAYNUM)
go


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
POINTID              numeric              not null
)
go


alter table DISPLAY2WAYDATA
   add constraint PK_DISPLAY2WAYDATA primary key  (DISPLAYNUM, ORDERING)
go


/*==============================================================*/
/* Table : DISPLAYCOLUMNS                                       */
/*==============================================================*/
create table DISPLAYCOLUMNS (
DISPLAYNUM           numeric              not null,
TITLE                varchar(50)          not null,
TYPENUM              numeric              not null,
ORDERING             numeric              not null,
WIDTH                numeric              not null
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
This use to be for the Historical Data View and RawPointHistory view,  they have been deleted and no longer used as of 7-11-2001
*********************************************************************************************************
insert into displaycolumns values(2, 'Time Stamp', 11, 1, 60 );
insert into displaycolumns values(2, 'Device Name', 5, 2, 70 );
insert into displaycolumns values(2, 'Point Name', 2, 3, 70 );
insert into displaycolumns values(2, 'Text Message', 12, 4, 180 );
insert into displaycolumns values(2, 'Additional Info', 10, 5, 180 );
insert into displaycolumns values(2, 'User Name', 8, 6, 35 );
insert into displaycolumns values(3, 'Time Stamp', 11, 1, 70 );
insert into displaycolumns values(3, 'Device Name', 5, 2, 60 );
insert into displaycolumns values(3, 'Point Name', 2, 3, 60 );
insert into displaycolumns values(3, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(3, 'Additional Info', 10, 5, 200 );
insert into displaycolumns values(3, 'User Name', 8, 6, 35 );
*********************************************************************************************************/


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
insert into displaycolumns values(14, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(14, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(14, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(14, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(14, 'User Name', 8, 5, 50 );

insert into displaycolumns values(15, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(15, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(15, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(15, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(15, 'User Name', 8, 5, 50 );

insert into displaycolumns values(16, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(16, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(16, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(16, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(16, 'User Name', 8, 5, 50 );

insert into displaycolumns values(17, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(17, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(17, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(17, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(17, 'User Name', 8, 5, 50 );

insert into displaycolumns values(18, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(18, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(18, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(18, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(18, 'User Name', 8, 5, 50 );

insert into displaycolumns values(19, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(19, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(19, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(19, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(19, 'User Name', 8, 5, 50 );

insert into displaycolumns values(20, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(20, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(20, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(20, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(20, 'User Name', 8, 5, 50 );

insert into displaycolumns values(21, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(21, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(21, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(21, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(21, 'User Name', 8, 5, 50 );

insert into displaycolumns values(22, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(22, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(22, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(22, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(22, 'User Name', 8, 5, 50 );

insert into displaycolumns values(23, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(23, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(23, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(23, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(23, 'User Name', 8, 5, 50 );

insert into displaycolumns values(24, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(24, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(24, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(24, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(24, 'User Name', 8, 5, 50 );

insert into displaycolumns values(25, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(25, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(25, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(25, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(25, 'User Name', 8, 5, 50 );

insert into displaycolumns values(26, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(26, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(26, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(26, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(26, 'User Name', 8, 5, 50 );

insert into displaycolumns values(27, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(27, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(27, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(27, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(27, 'User Name', 8, 5, 50 );

insert into displaycolumns values(28, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(28, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(28, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(28, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(28, 'User Name', 8, 5, 50 );

insert into displaycolumns values(29, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(29, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(29, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(29, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(29, 'User Name', 8, 5, 50 );

insert into displaycolumns values(30, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(30, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(30, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(30, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(30, 'User Name', 8, 5, 50 );

insert into displaycolumns values(31, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(31, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(31, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(31, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(31, 'User Name', 8, 5, 50 );

insert into displaycolumns values(32, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(32, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(32, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(32, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(32, 'User Name', 8, 5, 50 );

insert into displaycolumns values(33, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(33, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(33, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(33, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(33, 'User Name', 8, 5, 50 );

insert into displaycolumns values(34, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(34, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(34, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(34, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(34, 'User Name', 8, 5, 50 );

insert into displaycolumns values(35, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(35, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(35, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(35, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(35, 'User Name', 8, 5, 50 );

insert into displaycolumns values(50, 'Device Name', 5, 1, 70 );
insert into displaycolumns values(50, 'Point Name', 2, 2, 70 );
insert into displaycolumns values(50, 'Time Stamp', 11, 3, 60 );
insert into displaycolumns values(50, 'Description', 12, 4, 180 );
insert into displaycolumns values(50, 'Additional Info', 10, 5, 180 );
insert into displaycolumns values(51, 'Device Name', 5, 1, 70 );
insert into displaycolumns values(51, 'Point Name', 2, 2, 70 );
insert into displaycolumns values(51, 'Time Stamp', 11, 3, 60 );
insert into displaycolumns values(51, 'Description', 12, 4, 180 );
insert into displaycolumns values(51, 'Additional Info', 10, 5, 180 );
insert into displaycolumns values(51, 'User Name', 8, 6, 40 );
insert into displaycolumns values(51, 'Tag', 13, 7, 60 );

alter table DISPLAYCOLUMNS
   add constraint PK_DISPLAYCOLUMNS primary key  (DISPLAYNUM, TITLE)
go


/*==============================================================*/
/* Table : DYNAMICACCUMULATOR                                   */
/*==============================================================*/
create table DYNAMICACCUMULATOR (
POINTID              numeric              not null,
PREVIOUSPULSES       numeric              not null,
PRESENTPULSES        numeric              not null
)
go


alter table DYNAMICACCUMULATOR
   add constraint PK_DYNAMICACCUMULATOR primary key  (POINTID)
go


/*==============================================================*/
/* Table : DYNAMICDEVICESCANDATA                                */
/*==============================================================*/
create table DYNAMICDEVICESCANDATA (
DEVICEID             numeric              not null,
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


alter table DYNAMICDEVICESCANDATA
   add constraint PK_DYNAMICDEVICESCANDATA primary key  (DEVICEID)
go


/*==============================================================*/
/* Table : DYNAMICPOINTDISPATCH                                 */
/*==============================================================*/
create table DYNAMICPOINTDISPATCH (
POINTID              numeric              not null,
TIMESTAMP            datetime             not null
     constraint SYS_C0013325 check ("TIMESTAMP" IS NOT NULL),
QUALITY              numeric              not null
     constraint SYS_C0013326 check ("QUALITY" IS NOT NULL),
VALUE                float                not null
     constraint SYS_C0013327 check ("VALUE" IS NOT NULL),
TAGS                 numeric              not null
     constraint SYS_C0013328 check ("TAGS" IS NOT NULL),
NEXTARCHIVE          datetime             not null
     constraint SYS_C0013329 check ("NEXTARCHIVE" IS NOT NULL),
STALECOUNT           numeric              not null
     constraint SYS_C0013330 check ("STALECOUNT" IS NOT NULL),
LastAlarmLogID       numeric              not null,
millis               smallint             not null
)
go


alter table DYNAMICPOINTDISPATCH
   add constraint PK_DYNAMICPOINTDISPATCH primary key  (POINTID)
go


/*==============================================================*/
/* Table : DateOfHoliday                                        */
/*==============================================================*/
create table DateOfHoliday (
HolidayScheduleID    numeric              not null,
HolidayName          varchar(20)          not null,
HolidayMonth         numeric              not null,
HolidayDay           numeric              not null,
HolidayYear          numeric              not null
)
go


alter table DateOfHoliday
   add constraint PK_DATEOFHOLIDAY primary key  (HolidayScheduleID, HolidayName)
go


/*==============================================================*/
/* Table : DeviceAddress                                        */
/*==============================================================*/
create table DeviceAddress (
DeviceID             numeric              not null,
MasterAddress        numeric              not null,
SlaveAddress         numeric              not null,
PostCommWait         numeric              not null
)
go


alter table DeviceAddress
   add constraint PK_DEVICEADDRESS primary key  (DeviceID)
go


/*==============================================================*/
/* Table : DeviceCBC                                            */
/*==============================================================*/
create table DeviceCBC (
DEVICEID             numeric              not null,
SERIALNUMBER         numeric              not null,
ROUTEID              numeric              not null
)
go


alter table DeviceCBC
   add constraint PK_DEVICECBC primary key  (DEVICEID)
go


/*==============================================================*/
/* Table : DeviceCustomerList                                   */
/*==============================================================*/
create table DeviceCustomerList (
CustomerID           numeric              not null,
DeviceID             numeric              not null
)
go


alter table DeviceCustomerList
   add constraint PK_DEVICECUSTOMERLIST primary key  (DeviceID, CustomerID)
go


/*==============================================================*/
/* Table : DeviceDirectCommSettings                             */
/*==============================================================*/
create table DeviceDirectCommSettings (
DEVICEID             numeric              not null,
PORTID               numeric              not null
)
go


alter table DeviceDirectCommSettings
   add constraint PK_DEVICEDIRECTCOMMSETTINGS primary key  (DEVICEID)
go


/*==============================================================*/
/* Table : DeviceRTC                                            */
/*==============================================================*/
create table DeviceRTC (
DeviceID             numeric              not null,
RTCAddress           numeric              not null,
Response             varchar(1)           not null,
LBTMode              numeric              not null,
DisableVerifies      varchar(1)           not null
)
go


alter table DeviceRTC
   add constraint PK_DEVICERTC primary key  (DeviceID)
go


/*==============================================================*/
/* Table : DeviceRoutes                                         */
/*==============================================================*/
create table DeviceRoutes (
DEVICEID             numeric              not null,
ROUTEID              numeric              not null
)
go


alter table DeviceRoutes
   add constraint PK_DEVICEROUTES primary key  (DEVICEID, ROUTEID)
go


/*==============================================================*/
/* Table : DeviceSeries5RTU                                     */
/*==============================================================*/
create table DeviceSeries5RTU (
DeviceID             numeric              not null,
TickTime             numeric              not null,
TransmitOffset       numeric              not null,
SaveHistory          char(1)              not null,
PowerValueHighLimit  numeric              not null,
PowerValueLowLimit   numeric              not null,
PowerValueMultiplier float                not null,
PowerValueOffset     float                not null,
StartCode            numeric              not null,
StopCode             numeric              not null,
Retries              numeric              not null
)
go


alter table DeviceSeries5RTU
   add constraint PK_DEVICESERIES5RTU primary key  (DeviceID)
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
AlternateClose       numeric              not null
)
go


alter table DeviceWindow
   add constraint PK_DEVICEWINDOW primary key  (DeviceID, Type)
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
OriginalFeederID     numeric              not null,
OriginalSwitchingOrder numeric              not null
)
go


alter table DynamicCCCapBank
   add constraint PK_DYNAMICCCCAPBANK primary key  (CapBankID)
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
PowerFactorValue     float                not null,
KvarSolution         float                not null,
EstimatedPFValue     float                not null,
CurrentVarPointQuality numeric              not null,
WaiveControlFlag     char(1)              not null
)
go


alter table DynamicCCFeeder
   add constraint PK_DYNAMICCCFEEDER primary key  (FeederID)
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
PowerFactorValue     float                not null,
KvarSolution         float                not null,
EstimatedPFValue     float                not null,
CurrentVarPointQuality numeric              not null,
WaiveControlFlag     char(1)              not null
)
go


alter table DynamicCCSubstationBus
   add constraint PK_DYNAMICCCSUBSTATIONBUS primary key  (SubstationBusID)
go


/*==============================================================*/
/* Table : DynamicCalcHistorical                                */
/*==============================================================*/
create table DynamicCalcHistorical (
PointID              numeric              not null,
LastUpdate           datetime             not null
)
go


alter table DynamicCalcHistorical
   add constraint PK_DYNAMICCALCHISTORICAL primary key  (PointID)
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
CurrentDailyStopTime numeric              not null
)
go


alter table DynamicLMControlArea
   add constraint PK_DYNAMICLMCONTROLAREA primary key  (DeviceID)
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
LastPeakPointValueTimeStamp datetime             not null
)
go


alter table DynamicLMControlAreaTrigger
   add constraint PK_DYNAMICLMCONTROLAREATRIGGER primary key  (DeviceID, TriggerNumber)
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
LMProgramID          numeric              not null,
ControlStartTime     datetime             not null,
ControlCompleteTime  datetime             not null
)
go


alter table DynamicLMGroup
   add constraint PK_DYNAMICLMGROUP primary key  (DeviceID, LMProgramID)
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
TimeStamp            datetime             not null
)
go


alter table DynamicLMProgram
   add constraint PK_DYNAMICLMPROGRAM primary key  (DeviceID)
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
DailyOps             numeric              not null,
NotifyTime           datetime             not null
)
go


alter table DynamicLMProgramDirect
   add constraint PK_DYNAMICLMPROGRAMDIRECT primary key  (DeviceID)
go


/*==============================================================*/
/* Table : DynamicPAOStatistics                                 */
/*==============================================================*/
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
StopDateTime         datetime             not null
)
go


alter table DynamicPAOStatistics
   add constraint PK_DYNAMICPAOSTATISTICS primary key  (PAOBjectID, StatisticType)
go


/*==============================================================*/
/* Table : DynamicPointAlarming                                 */
/*==============================================================*/
create table DynamicPointAlarming (
PointID              numeric              not null,
AlarmCondition       numeric              not null,
CategoryID           numeric              not null,
AlarmTime            datetime             not null,
Action               varchar(60)          not null,
Description          varchar(120)         not null,
Tags                 numeric              not null,
LogID                numeric              not null,
SOE_TAG              numeric              not null,
Type                 numeric              not null,
UserName             varchar(30)          not null
)
go


alter table DynamicPointAlarming
   add constraint PK_DYNAMICPOINTALARMING primary key  (PointID, AlarmCondition)
go


/*==============================================================*/
/* Table : DynamicTags                                          */
/*==============================================================*/
create table DynamicTags (
InstanceID           numeric              not null,
PointID              numeric              not null,
TagID                numeric              not null,
UserName             varchar(60)          not null,
Action               varchar(20)          not null,
Description          varchar(120)         not null,
TagTime              datetime             not null,
RefStr               varchar(60)          not null,
ForStr               varchar(60)          not null
)
go


alter table DynamicTags
   add constraint PK_DYNAMICTAGS primary key  (InstanceID)
go


/*==============================================================*/
/* Table : EnergyCompany                                        */
/*==============================================================*/
create table EnergyCompany (
EnergyCompanyID      numeric              not null,
Name                 varchar(60)          not null,
PrimaryContactID     numeric              not null,
UserID               numeric              not null
)
go


insert into EnergyCompany VALUES (-1,'Default Energy Company',0,-100);

alter table EnergyCompany
   add constraint PK_ENERGYCOMPANY primary key  (EnergyCompanyID)
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
EnergyCompanyID      numeric              not null,
CustomerID           numeric              not null
)
go


alter table EnergyCompanyCustomerList
   add constraint PK_ENERGYCOMPANYCUSTOMERLIST primary key  (EnergyCompanyID, CustomerID)
go


/*==============================================================*/
/* Table : EnergyCompanyOperatorLoginList                       */
/*==============================================================*/
create table EnergyCompanyOperatorLoginList (
EnergyCompanyID      numeric              not null,
OperatorLoginID      numeric              not null
)
go


INSERT INTO EnergyCompanyOperatorLoginList VALUES (-1,-100);

alter table EnergyCompanyOperatorLoginList
   add constraint PK_ENERGYCOMPANYOPERATORLOGINL primary key  (EnergyCompanyID, OperatorLoginID)
go


/*==============================================================*/
/* Table : FDRInterface                                         */
/*==============================================================*/
create table FDRInterface (
InterfaceID          numeric              not null,
InterfaceName        varchar(30)          not null,
PossibleDirections   varchar(100)         not null,
hasDestination       char(1)              not null
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
insert into FDRInterface values (11,'TELEGYR','Receive,Receive for control','f');
insert into FDRInterface values (12,'TEXTIMPORT','Receive,Receive for control','f');
insert into FDRInterface values (13,'TEXTEXPORT','Send','f');

insert into fdrinterface values(16,'LODESTAR_STD','Receive','f');
insert into fdrinterface values(17,'LODESTAR_ENH','Receive','f');
insert into fdrinterface values (18, 'DSM2FILEIN', 'Receive,Receive for control', 'f');

alter table FDRInterface
   add constraint PK_FDRINTERFACE primary key  (InterfaceID)
go


/*==============================================================*/
/* Table : FDRInterfaceOption                                   */
/*==============================================================*/
create table FDRInterfaceOption (
InterfaceID          numeric              not null,
OptionLabel          varchar(20)          not null,
Ordering             numeric              not null,
OptionType           varchar(8)           not null,
OptionValues         varchar(150)         not null
)
go


insert into FDRInterfaceOption values(1, 'Device', 1, 'Text', '(none)' );
insert into FDRInterfaceOption values(1, 'Point', 2, 'Text', '(none)' );
insert into FDRInterfaceOption values(1, 'Destination/Source', 3, 'Text', '(none)' );
insert into FDRInterfaceOption values(2, 'Category', 1, 'Combo', 'PSEUDO,REAL,CALCULATED' );
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
insert into FDRInterfaceOption values(11, 'Point', 1, 'Text', '(none)' );
insert into FDRInterfaceOption values(11, 'Group', 2, 'Query', 'select GroupName from FDRTelegyrGroup' );
insert into FDRInterfaceOption values(12,'Point ID',1,'Text','(none)');
insert into FDRInterfaceOption values(13,'Point ID',1,'Text','(none)');

insert into fdrinterfaceoption values (16,'Customer',1,'Text','(none)');
insert into fdrinterfaceoption values (16,'Channel',2,'Text','(none)');
insert into fdrinterfaceoption values (16,'DrivePath',3,'Text','(none)');
insert into fdrinterfaceoption values (16,'Filename',4,'Text','(none)');
insert into fdrinterfaceoption values (17,'Customer',1,'Text','(none)');
insert into fdrinterfaceoption values (17,'Channel',2,'Text','(none)');
insert into fdrinterfaceoption values (17,'DrivePath',3,'Text','(none)');
insert into fdrinterfaceoption values (17,'Filename',4,'Text','(none)');
insert into fdrinterfaceoption values(18, 'Option Number', 1, 'Combo', '1');
insert into fdrinterfaceoption values(18, 'Point ID', 2, 'Text', '(none)');


alter table FDRInterfaceOption
   add constraint PK_FDRINTERFACEOPTION primary key  (InterfaceID, Ordering)
go


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


alter table FDRTRANSLATION
   add constraint PK_FDRTrans primary key  (POINTID, InterfaceType, TRANSLATION)
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
/* Table : FDRTelegyrGroup                                      */
/*==============================================================*/
create table FDRTelegyrGroup (
GroupID              numeric              not null,
GroupName            varchar(40)          not null,
CollectionInterval   numeric              not null,
GroupType            varchar(20)          not null
)
go


alter table FDRTelegyrGroup
   add constraint PK_FDRTELEGYRGROUP primary key  (GroupID)
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
Type                 numeric              not null,
Multiplier           float                not null,
Renderer             smallint             not null,
MoreData             varchar(100)         not null
)
go


alter table GRAPHDATASERIES
   add constraint SYS_GrphDserID primary key  (GRAPHDATASERIESID)
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
Type                 char(1)              not null
)
go


alter table GRAPHDEFINITION
   add constraint SYS_C0015109 primary key  (GRAPHDEFINITIONID)
go


alter table GRAPHDEFINITION
   add constraint AK_GRNMUQ_GRAPHDEF unique  (NAME)
go


/*==============================================================*/
/* Table : GatewayEndDevice                                     */
/*==============================================================*/
create table GatewayEndDevice (
SerialNumber         varchar(30)          not null,
HardwareType         numeric              not null,
DataType             numeric              not null,
DataValue            varchar(100)         null
)
go


alter table GatewayEndDevice
   add constraint PK_GATEWAYENDDEVICE primary key  (SerialNumber, HardwareType, DataType)
go


/*==============================================================*/
/* Table : GenericMacro                                         */
/*==============================================================*/
create table GenericMacro (
OwnerID              numeric              not null,
ChildID              numeric              not null,
ChildOrder           numeric              not null,
MacroType            varchar(20)          not null
)
go


alter table GenericMacro
   add constraint PK_GENERICMACRO primary key  (OwnerID, ChildOrder, MacroType)
go


/*==============================================================*/
/* Table : GraphCustomerList                                    */
/*==============================================================*/
create table GraphCustomerList (
GraphDefinitionID    numeric              not null,
CustomerID           numeric              not null,
CustomerOrder        numeric              not null
)
go


alter table GraphCustomerList
   add constraint PK_GRAPHCUSTOMERLIST primary key  (GraphDefinitionID, CustomerID)
go


/*==============================================================*/
/* Table : HolidaySchedule                                      */
/*==============================================================*/
create table HolidaySchedule (
HolidayScheduleID    numeric              not null,
HolidayScheduleName  varchar(40)          not null
)
go


insert into HolidaySchedule values( 0, 'Empty Holiday Schedule' );

alter table HolidaySchedule
   add constraint PK_HOLIDAYSCHEDULE primary key  (HolidayScheduleID)
go


/*==============================================================*/
/* Index: Indx_HolSchName                                       */
/*==============================================================*/
create unique  index Indx_HolSchName on HolidaySchedule (
HolidayScheduleName
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


alter table LMCONTROLAREAPROGRAM
   add constraint PK_LMCONTROLAREAPROGRAM primary key  (DEVICEID, LMPROGRAMDEVICEID)
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
PEAKPOINTID          numeric              not null
)
go


alter table LMCONTROLAREATRIGGER
   add constraint PK_LMCONTROLAREATRIGGER primary key  (DEVICEID, TRIGGERNUMBER)
go


/*==============================================================*/
/* Table : LMControlArea                                        */
/*==============================================================*/
create table LMControlArea (
DEVICEID             numeric              not null,
DEFOPERATIONALSTATE  varchar(20)          not null,
CONTROLINTERVAL      numeric              not null,
MINRESPONSETIME      numeric              not null,
DEFDAILYSTARTTIME    numeric              not null,
DEFDAILYSTOPTIME     numeric              not null,
REQUIREALLTRIGGERSACTIVEFLAG varchar(1)           not null
)
go


alter table LMControlArea
   add constraint PK_LMCONTROLAREA primary key  (DEVICEID)
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
StopDateTime         datetime             not null
)
go


alter table LMControlHistory
   add constraint PK_LMCONTROLHISTORY primary key  (LMCtrlHistID)
go


/*==============================================================*/
/* Index: Indx_Start                                            */
/*==============================================================*/
create   index Indx_Start on LMControlHistory (
StartDateTime
)
go


/*==============================================================*/
/* Table : LMControlScenarioProgram                             */
/*==============================================================*/
create table LMControlScenarioProgram (
ScenarioID           numeric              not null,
ProgramID            numeric              not null,
StartDelay           numeric              not null,
StopOffset           numeric              not null,
StartGear            numeric              not null
)
go


alter table LMControlScenarioProgram
   add constraint PK_LMCONTROLSCENARIOPROGRAM primary key  (ScenarioID, ProgramID)
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


alter table LMCurtailCustomerActivity
   add constraint PK_LMCURTAILCUSTOMERACTIVITY primary key  (CustomerID, CurtailReferenceID)
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
AdditionalInfo       varchar(100)         not null
)
go


alter table LMCurtailProgramActivity
   add constraint PK_LMCURTAILPROGRAMACTIVITY primary key  (CurtailReferenceID)
go


/*==============================================================*/
/* Index: Indx_LMCrtPrgActStTime                                */
/*==============================================================*/
create   index Indx_LMCrtPrgActStTime on LMCurtailProgramActivity (
CurtailmentStartTime
)
go


/*==============================================================*/
/* Table : LMDirectCustomerList                                 */
/*==============================================================*/
create table LMDirectCustomerList (
ProgramID            numeric              not null,
CustomerID           numeric              not null
)
go


alter table LMDirectCustomerList
   add constraint PK_LMDIRECTCUSTOMERLIST primary key  (ProgramID, CustomerID)
go


/*==============================================================*/
/* Table : LMDirectNotifGrpList                                 */
/*==============================================================*/
create table LMDirectNotifGrpList (
ProgramID            numeric              not null,
NotificationGrpID    numeric              not null
)
go


alter table LMDirectNotifGrpList
   add constraint PK_LMDIRECTNOTIFGRPLIST primary key  (ProgramID, NotificationGrpID)
go


/*==============================================================*/
/* Table : LMDirectOperatorList                                 */
/*==============================================================*/
create table LMDirectOperatorList (
ProgramID            numeric              not null,
OperatorLoginID      numeric              not null
)
go


alter table LMDirectOperatorList
   add constraint PK_LMDIRECTOPERATORLIST primary key  (ProgramID, OperatorLoginID)
go


/*==============================================================*/
/* Table : LMEnergyExchangeCustomerList                         */
/*==============================================================*/
create table LMEnergyExchangeCustomerList (
ProgramID            numeric              not null,
CustomerID           numeric              not null,
CustomerOrder        numeric              not null
)
go


alter table LMEnergyExchangeCustomerList
   add constraint PK_LMENERGYEXCHANGECUSTOMERLIS primary key  (ProgramID, CustomerID)
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
EnergyExchangeNotes  varchar(120)         not null
)
go


alter table LMEnergyExchangeCustomerReply
   add constraint PK_LMENERGYEXCHANGECUSTOMERREP primary key  (CustomerID, OfferID, RevisionNumber)
go


/*==============================================================*/
/* Table : LMEnergyExchangeHourlyCustomer                       */
/*==============================================================*/
create table LMEnergyExchangeHourlyCustomer (
CustomerID           numeric              not null,
OfferID              numeric              not null,
RevisionNumber       numeric              not null,
Hour                 numeric              not null,
AmountCommitted      float                not null
)
go


alter table LMEnergyExchangeHourlyCustomer
   add constraint PK_LMENERGYEXCHANGEHOURLYCUSTO primary key  (CustomerID, OfferID, RevisionNumber, Hour)
go


/*==============================================================*/
/* Table : LMEnergyExchangeHourlyOffer                          */
/*==============================================================*/
create table LMEnergyExchangeHourlyOffer (
OfferID              numeric              not null,
RevisionNumber       numeric              not null,
Hour                 numeric              not null,
Price                numeric              not null,
AmountRequested      float                not null
)
go


alter table LMEnergyExchangeHourlyOffer
   add constraint PK_LMENERGYEXCHANGEHOURLYOFFER primary key  (OfferID, RevisionNumber, Hour)
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
AdditionalInfo       varchar(100)         not null
)
go


alter table LMEnergyExchangeOfferRevision
   add constraint PK_LMENERGYEXCHANGEOFFERREVISI primary key  (OfferID, RevisionNumber)
go


/*==============================================================*/
/* Table : LMEnergyExchangeProgramOffer                         */
/*==============================================================*/
create table LMEnergyExchangeProgramOffer (
DeviceID             numeric              not null,
OfferID              numeric              not null,
RunStatus            varchar(20)          not null,
OfferDate            datetime             not null
)
go


alter table LMEnergyExchangeProgramOffer
   add constraint PK_LMENERGYEXCHANGEPROGRAMOFFE primary key  (OfferID)
go


/*==============================================================*/
/* Table : LMGroup                                              */
/*==============================================================*/
create table LMGroup (
DeviceID             numeric              not null,
KWCapacity           float                not null
)
go


alter table LMGroup
   add constraint PK_LMGROUP primary key  (DeviceID)
go


/*==============================================================*/
/* Table : LMGroupEmetcon                                       */
/*==============================================================*/
create table LMGroupEmetcon (
DEVICEID             numeric              not null,
GOLDADDRESS          numeric              not null
     constraint SYS_C13351 check ("GOLDADDRESS" IS NOT NULL),
SILVERADDRESS        numeric              not null
     constraint SYS_C13352 check ("SILVERADDRESS" IS NOT NULL),
ADDRESSUSAGE         char(1)              not null
     constraint SYS_C0013353 check ("ADDRESSUSAGE" IS NOT NULL),
RELAYUSAGE           char(1)              not null
     constraint SYS_C0013354 check ("RELAYUSAGE" IS NOT NULL),
ROUTEID              numeric              not null
)
go


alter table LMGroupEmetcon
   add constraint PK_LMGROUPEMETCON primary key  (DEVICEID)
go


/*==============================================================*/
/* Table : LMGroupExpressCom                                    */
/*==============================================================*/
create table LMGroupExpressCom (
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
RelayUsage           char(15)             not null
)
go


alter table LMGroupExpressCom
   add constraint PK_LMGROUPEXPRESSCOM primary key  (LMGroupID)
go


/*==============================================================*/
/* Table : LMGroupExpressComAddress                             */
/*==============================================================*/
create table LMGroupExpressComAddress (
AddressID            numeric              not null,
AddressType          varchar(20)          not null,
Address              numeric              not null,
AddressName          varchar(30)          not null
)
go


insert into LMGroupExpressComAddress values( 0, '(none)', 0, '(none)' );

alter table LMGroupExpressComAddress
   add constraint PK_LMGROUPEXPRESSCOMADDRESS primary key  (AddressID)
go


/*==============================================================*/
/* Table : LMGroupMCT                                           */
/*==============================================================*/
create table LMGroupMCT (
DeviceID             numeric              not null,
MCTAddress           numeric              not null,
MCTLevel             char(1)              not null,
RelayUsage           char(7)              not null,
RouteID              numeric              not null,
MCTDeviceID          numeric              not null
)
go


alter table LMGroupMCT
   add constraint PK_LMGrpMCTPK primary key  (DeviceID)
go


/*==============================================================*/
/* Table : LMGroupPoint                                         */
/*==============================================================*/
create table LMGroupPoint (
DEVICEID             numeric              not null,
DeviceIDUsage        numeric              not null,
PointIDUsage         numeric              not null,
StartControlRawState numeric              not null
)
go


alter table LMGroupPoint
   add constraint PK_LMGROUPPOINT primary key  (DEVICEID)
go


/*==============================================================*/
/* Table : LMGroupRipple                                        */
/*==============================================================*/
create table LMGroupRipple (
DeviceID             numeric              not null,
RouteID              numeric              not null,
ShedTime             numeric              not null,
ControlValue         char(50)             not null,
RestoreValue         char(50)             not null
)
go


alter table LMGroupRipple
   add constraint PK_LMGROUPRIPPLE primary key  (DeviceID)
go


/*==============================================================*/
/* Table : LMGroupSA205105                                      */
/*==============================================================*/
create table LMGroupSA205105 (
GroupID              numeric              not null,
RouteID              numeric              not null,
OperationalAddress   numeric              not null,
LoadNumber           varchar(64)          not null
)
go


alter table LMGroupSA205105
   add constraint PK_LMGROUPSA205105 primary key  (GroupID)
go


/*==============================================================*/
/* Table : LMGroupSA305                                         */
/*==============================================================*/
create table LMGroupSA305 (
GroupID              numeric              not null,
RouteID              numeric              not null,
AddressUsage         varchar(8)           not null,
UtilityAddress       numeric              not null,
GroupAddress         numeric              not null,
DivisionAddress      numeric              not null,
SubstationAddress    numeric              not null,
IndividualAddress    varchar(16)          not null,
RateFamily           numeric              not null,
RateMember           numeric              not null,
RateHierarchy        numeric              not null,
LoadNumber           varchar(8)           not null
)
go


alter table LMGroupSA305
   add constraint PK_LMGROUPSA305 primary key  (GroupID)
go


/*==============================================================*/
/* Table : LMGroupSASimple                                      */
/*==============================================================*/
create table LMGroupSASimple (
GroupID              numeric              not null,
RouteID              numeric              not null,
OperationalAddress   varchar(8)           not null,
NominalTimeout       numeric              not null,
MarkIndex            numeric              not null,
SpaceIndex           numeric              not null
)
go


alter table LMGroupSASimple
   add constraint PK_LMGROUPSASIMPLE primary key  (GroupID)
go


/*==============================================================*/
/* Table : LMGroupVersacom                                      */
/*==============================================================*/
create table LMGroupVersacom (
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


alter table LMGroupVersacom
   add constraint PK_LMGROUPVERSACOM primary key  (DEVICEID)
go


/*==============================================================*/
/* Table : LMMACSScheduleOperatorList                           */
/*==============================================================*/
create table LMMACSScheduleOperatorList (
ScheduleID           numeric              not null,
OperatorLoginID      numeric              not null
)
go


alter table LMMACSScheduleOperatorList
   add constraint PK_LMMACSSCHEDULEOPERATORLIST primary key  (ScheduleID, OperatorLoginID)
go


/*==============================================================*/
/* Table : LMMacsScheduleCustomerList                           */
/*==============================================================*/
create table LMMacsScheduleCustomerList (
ScheduleID           numeric              not null,
LMCustomerDeviceID   numeric              not null,
CustomerOrder        numeric              not null
)
go


alter table LMMacsScheduleCustomerList
   add constraint PK_LMMACSSCHEDULECUSTOMERLIST primary key  (ScheduleID, LMCustomerDeviceID)
go


/*==============================================================*/
/* Table : LMPROGRAM                                            */
/*==============================================================*/
create table LMPROGRAM (
DeviceID             numeric              not null,
ControlType          varchar(20)          not null,
ConstraintID         numeric              not null
)
go


insert into LMProgram values(0, 'Automatic', 0);

alter table LMPROGRAM
   add constraint PK_LMPROGRAM primary key  (DeviceID)
go


/*==============================================================*/
/* Table : LMProgramConstraints                                 */
/*==============================================================*/
create table LMProgramConstraints (
ConstraintID         numeric              not null,
ConstraintName       varchar(60)          not null,
AvailableSeasons     varchar(4)           not null,
AvailableWeekDays    varchar(8)           not null,
MaxHoursDaily        numeric              not null,
MaxHoursMonthly      numeric              not null,
MaxHoursSeasonal     numeric              not null,
MaxHoursAnnually     numeric              not null,
MinActivateTime      numeric              not null,
MinRestartTime       numeric              not null,
MaxDailyOps          numeric              not null,
MaxActivateTime      numeric              not null,
HolidayScheduleID    numeric              not null,
SeasonScheduleID     numeric              not null
)
go


insert into LMProgramConstraints values (0, 'Default Constraint', 'YYYY', 'YYYYYYYN', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

alter table LMProgramConstraints
   add constraint PK_PRGCONSTR primary key  (ConstraintID)
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


alter table LMProgramControlWindow
   add constraint PK_LMPROGRAMCONTROLWINDOW primary key  (DeviceID, WindowNumber)
go


/*==============================================================*/
/* Table : LMProgramCurtailCustomerList                         */
/*==============================================================*/
create table LMProgramCurtailCustomerList (
ProgramID            numeric              not null,
CustomerID           numeric              not null,
CustomerOrder        numeric              not null,
RequireAck           char(1)              not null
)
go


alter table LMProgramCurtailCustomerList
   add constraint PK_LMPROGRAMCURTAILCUSTOMERLIS primary key  (CustomerID, ProgramID)
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
CanceledMsg          varchar(80)          not null,
StoppedEarlyMsg      varchar(80)          not null
)
go


alter table LMProgramCurtailment
   add constraint PK_LMPROGRAMCURTAILMENT primary key  (DeviceID)
go


/*==============================================================*/
/* Table : LMProgramDirect                                      */
/*==============================================================*/
create table LMProgramDirect (
DeviceID             numeric              not null,
NotifyOffset         numeric              not null,
Heading              varchar(40)          not null,
MessageHeader        varchar(160)         not null,
MessageFooter        varchar(160)         not null
)
go


alter table LMProgramDirect
   add constraint PK_LMPROGRAMDIRECT primary key  (DeviceID)
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
GearID               numeric              not null,
RampInInterval       numeric              not null,
RampInPercent        numeric              not null,
RampOutInterval      numeric              not null,
RampOutPercent       numeric              not null
)
go


alter table LMProgramDirectGear
   add constraint PK_LMPROGRAMDIRECTGEAR primary key  (GearID)
go


alter table LMProgramDirectGear
   add constraint AK_AKEY_LMPRGDIRG_LMPROGRA unique  (DeviceID, GearNumber)
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


alter table LMProgramDirectGroup
   add constraint PK_LMPROGRAMDIRECTGROUP primary key  (DeviceID, GroupOrder)
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
StoppedEarlyMsg      varchar(80)          not null
)
go


alter table LMProgramEnergyExchange
   add constraint PK_LMPROGRAMENERGYEXCHANGE primary key  (DeviceID)
go


/*==============================================================*/
/* Table : LMThermoStatGear                                     */
/*==============================================================*/
create table LMThermoStatGear (
GearID               numeric              not null,
Settings             varchar(10)          not null,
MinValue             numeric              not null,
MaxValue             numeric              not null,
ValueB               numeric              not null,
ValueD               numeric              not null,
ValueF               numeric              not null,
Random               numeric              not null,
ValueTa              numeric              not null,
ValueTb              numeric              not null,
ValueTc              numeric              not null,
ValueTd              numeric              not null,
ValueTe              numeric              not null,
ValueTf              numeric              not null
)
go


alter table LMThermoStatGear
   add constraint PK_LMTHERMOSTATGEAR primary key  (GearID)
go


/*==============================================================*/
/* Table : LOGIC                                                */
/*==============================================================*/
create table LOGIC (
LOGICID              numeric              not null,
LOGICNAME            varchar(20)          not null
     constraint SYS_C0013441 check ("LOGICNAME" IS NOT NULL),
PERIODICRATE         numeric              not null
     constraint SYS_C0013442 check ("PERIODICRATE" IS NOT NULL),
STATEFLAG            varchar(10)          not null
     constraint SYS_C0013443 check ("STATEFLAG" IS NOT NULL),
SCRIPTNAME           varchar(20)          not null
     constraint SYS_C0013444 check ("SCRIPTNAME" IS NOT NULL)
)
go


alter table LOGIC
   add constraint SYS_C0013445 primary key  (LOGICID)
go


/*==============================================================*/
/* Table : MACROROUTE                                           */
/*==============================================================*/
create table MACROROUTE (
ROUTEID              numeric              not null,
SINGLEROUTEID        numeric              not null,
ROUTEORDER           numeric              not null
     constraint SYS_C0013273 check ("ROUTEORDER" IS NOT NULL)
)
go


alter table MACROROUTE
   add constraint PK_MACROROUTE primary key  (ROUTEID, ROUTEORDER)
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
ManualStopTime       datetime             null
)
go


alter table MACSchedule
   add constraint PK_MACSCHEDULE primary key  (ScheduleID)
go


/*==============================================================*/
/* Table : MACSimpleSchedule                                    */
/*==============================================================*/
create table MACSimpleSchedule (
ScheduleID           numeric              not null,
TargetSelect         varchar(40)          null,
StartCommand         varchar(120)         null,
StopCommand          varchar(120)         null,
RepeatInterval       numeric              null
)
go


alter table MACSimpleSchedule
   add constraint PK_MACSIMPLESCHEDULE primary key  (ScheduleID)
go


/*==============================================================*/
/* Table : MCTBroadCastMapping                                  */
/*==============================================================*/
create table MCTBroadCastMapping (
MCTBroadCastID       numeric              not null,
MctID                numeric              not null,
Ordering             numeric              not null
)
go


alter table MCTBroadCastMapping
   add constraint PK_MCTBROADCASTMAPPING primary key  (MCTBroadCastID, MctID)
go


/*==============================================================*/
/* Table : MCTConfig                                            */
/*==============================================================*/
create table MCTConfig (
ConfigID             numeric              not null,
ConfigName           varchar(30)          not null,
ConfigType           numeric              not null,
ConfigMode           varchar(30)          not null,
MCTWire1             numeric              not null,
Ke1                  float                not null,
MCTWire2             numeric              not null,
Ke2                  float                not null,
MCTWire3             numeric              not null,
Ke3                  float                not null
)
go


alter table MCTConfig
   add constraint PK_MCTCONFIG primary key  (ConfigID)
go


/*==============================================================*/
/* Table : MCTConfigMapping                                     */
/*==============================================================*/
create table MCTConfigMapping (
MctID                numeric              not null,
ConfigID             numeric              not null
)
go


alter table MCTConfigMapping
   add constraint PK_MCTCONFIGMAPPING primary key  (MctID, ConfigID)
go


/*==============================================================*/
/* Table : NotificationDestination                              */
/*==============================================================*/
create table NotificationDestination (
DestinationOrder     numeric              not null,
NotificationGroupID  numeric              not null,
RecipientID          numeric              not null
)
go


alter table NotificationDestination
   add constraint PKey_NotDestID primary key  (NotificationGroupID, DestinationOrder)
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
DisableFlag          char(1)              not null
)
go


insert into notificationgroup values(1,'(none)','(none)','(none)','(none)','(none)','N');

alter table NotificationGroup
   add constraint PK_NOTIFICATIONGROUP primary key  (NotificationGroupID)
go


/*==============================================================*/
/* Index: Indx_NOTIFGRPNme                                      */
/*==============================================================*/
create unique  index Indx_NOTIFGRPNme on NotificationGroup (
GroupName
)
go


/*==============================================================*/
/* Table : OperatorLoginGraphList                               */
/*==============================================================*/
create table OperatorLoginGraphList (
OperatorLoginID      numeric              not null,
GraphDefinitionID    numeric              not null
)
go


alter table OperatorLoginGraphList
   add constraint PK_OPERATORLOGINGRAPHLIST primary key  (OperatorLoginID, GraphDefinitionID)
go


/*==============================================================*/
/* Table : OperatorSerialGroup                                  */
/*==============================================================*/
create table OperatorSerialGroup (
LoginID              numeric              not null,
LMGroupID            numeric              not null
)
go


alter table OperatorSerialGroup
   add constraint PK_OpSerGrp primary key  (LoginID)
go


/*==============================================================*/
/* Table : PAOExclusion                                         */
/*==============================================================*/
create table PAOExclusion (
ExclusionID          numeric              not null,
PaoID                numeric              not null,
ExcludedPaoID        numeric              not null,
PointID              numeric              not null,
Value                numeric              not null,
FunctionID           numeric              not null,
FuncName             varchar(100)         not null,
FuncRequeue          numeric              not null,
FuncParams           varchar(200)         not null
)
go


alter table PAOExclusion
   add constraint PK_PAOEXCLUSION primary key  (ExclusionID)
go


/*==============================================================*/
/* Index: Indx_PAOExclus                                        */
/*==============================================================*/
create unique  index Indx_PAOExclus on PAOExclusion (
PaoID,
ExcludedPaoID
)
go


/*==============================================================*/
/* Table : PAOowner                                             */
/*==============================================================*/
create table PAOowner (
OwnerID              numeric              not null,
ChildID              numeric              not null
)
go


alter table PAOowner
   add constraint PK_PAOOWNER primary key  (OwnerID, ChildID)
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
ARCHIVEINTERVAL      numeric              not null
)
go


INSERT into point  values (0,   'System', 'System Point', 0, 'Default', 0, 'N', 'N', 'S', 0  ,'None', 0);
INSERT into point  values (-1,  'System', 'Porter', 0, 'Default', 0, 'N', 'N', 'S', 1  ,'None', 0);
INSERT into point  values (-2,  'System', 'Scanner', 0, 'Default', 0, 'N', 'N', 'S', 2  ,'None', 0);
INSERT into point  values (-3,  'System', 'Dispatch', 0, 'Default', 0, 'N', 'N', 'S', 3  ,'None', 0);
INSERT into point  values (-4,  'System', 'Macs', 0, 'Default', 0, 'N', 'N', 'S', 4  ,'None', 0);
INSERT into point  values (-5,  'System', 'Cap Control', 0, 'Default', 0, 'N', 'N', 'S', 5  ,'None', 0);
INSERT into point  values (-10, 'System', 'Load Management' , 0, 'Default', 0, 'N', 'N', 'S', 10 ,'None', 0);
INSERT into point  values (-100, 'System', 'Threshold' , 0, 'Default', 0, 'N', 'N', 'S', 10 ,'None', 0);

alter table POINT
   add constraint Key_PT_PTID primary key  (POINTID)
go


alter table POINT
   add constraint AK_KEY_PTNM_YUKPAOID unique  (POINTNAME, PAObjectID)
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


alter table POINTACCUMULATOR
   add constraint PK_POINTACCUMULATOR primary key  (POINTID)
go


/*==============================================================*/
/* Table : POINTANALOG                                          */
/*==============================================================*/
create table POINTANALOG (
POINTID              numeric              not null,
DEADBAND             float                not null,
TRANSDUCERTYPE       varchar(14)          not null,
MULTIPLIER           float                not null,
DATAOFFSET           float                not null
)
go


alter table POINTANALOG
   add constraint PK_POINTANALOG primary key  (POINTID)
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


alter table POINTLIMITS
   add constraint PK_POINTLIMITS primary key  (POINTID, LIMITNUMBER)
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
CommandTimeOut       numeric              not null
)
go


alter table POINTSTATUS
   add constraint PK_PtStatus primary key  (POINTID)
go


/*==============================================================*/
/* Table : POINTUNIT                                            */
/*==============================================================*/
create table POINTUNIT (
POINTID              numeric              not null,
UOMID                numeric              not null,
DECIMALPLACES        numeric              not null,
HighReasonabilityLimit float                not null,
LowReasonabilityLimit float                not null
)
go


alter table POINTUNIT
   add constraint PK_POINTUNITID primary key  (POINTID)
go


/*==============================================================*/
/* Table : PORTDIALUPMODEM                                      */
/*==============================================================*/
create table PORTDIALUPMODEM (
PORTID               numeric              not null,
MODEMTYPE            varchar(30)          not null
     constraint SYS_C13171 check ("MODEMTYPE" IS NOT NULL),
INITIALIZATIONSTRING varchar(50)          not null
     constraint SYS_C13172 check ("INITIALIZATIONSTRING" IS NOT NULL),
PREFIXNUMBER         varchar(10)          not null
     constraint SYS_C0013173 check ("PREFIXNUMBER" IS NOT NULL),
SUFFIXNUMBER         varchar(10)          not null
     constraint SYS_C0013174 check ("SUFFIXNUMBER" IS NOT NULL)
)
go


alter table PORTDIALUPMODEM
   add constraint PK_PORTDIALUPMODEM primary key  (PORTID)
go


/*==============================================================*/
/* Table : PORTLOCALSERIAL                                      */
/*==============================================================*/
create table PORTLOCALSERIAL (
PORTID               numeric              not null,
PHYSICALPORT         varchar(8)           not null
     constraint SYS_C0013146 check ("PHYSICALPORT" IS NOT NULL)
)
go


alter table PORTLOCALSERIAL
   add constraint PK_PORTLOCALSERIAL primary key  (PORTID)
go


/*==============================================================*/
/* Table : PORTRADIOSETTINGS                                    */
/*==============================================================*/
create table PORTRADIOSETTINGS (
PORTID               numeric              not null,
RTSTOTXWAITSAMED     numeric              not null
     constraint SYS_C0013165 check ("RTSTOTXWAITSAMED" IS NOT NULL),
RTSTOTXWAITDIFFD     numeric              not null
     constraint SYS_C0013166 check ("RTSTOTXWAITDIFFD" IS NOT NULL),
RADIOMASTERTAIL      numeric              not null
     constraint SYS_C0013167 check ("RADIOMASTERTAIL" IS NOT NULL),
REVERSERTS           numeric              not null
     constraint SYS_C0013168 check ("REVERSERTS" IS NOT NULL)
)
go


alter table PORTRADIOSETTINGS
   add constraint PK_PORTRADIOSETTINGS primary key  (PORTID)
go


/*==============================================================*/
/* Table : PORTSETTINGS                                         */
/*==============================================================*/
create table PORTSETTINGS (
PORTID               numeric              not null,
BAUDRATE             numeric              not null
     constraint SYS_C0013153 check ("BAUDRATE" IS NOT NULL),
CDWAIT               numeric              not null
     constraint SYS_C0013154 check ("CDWAIT" IS NOT NULL),
LINESETTINGS         varchar(8)           not null
     constraint SYS_C0013155 check ("LINESETTINGS" IS NOT NULL)
)
go


alter table PORTSETTINGS
   add constraint PK_PORTSETTINGS primary key  (PORTID)
go


/*==============================================================*/
/* Table : PORTTERMINALSERVER                                   */
/*==============================================================*/
create table PORTTERMINALSERVER (
PORTID               numeric              not null,
IPADDRESS            varchar(64)          not null
     constraint SYS_C0013149 check ("IPADDRESS" IS NOT NULL),
SOCKETPORTNUMBER     numeric              not null
     constraint SYS_C0013150 check ("SOCKETPORTNUMBER" IS NOT NULL)
)
go


alter table PORTTERMINALSERVER
   add constraint PK_PORTTERMINALSERVER primary key  (PORTID)
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
RecipientID          numeric              not null
)
go


insert into pointalarming(pointid, alarmstates, excludenotifystates, notifyonacknowledge, notificationgroupid, recipientid)
	select pointid,
	'',
	'NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN',
	'N',
	1, 0  from point;

alter table PointAlarming
   add constraint PK_POINTALARMING primary key  (PointID)
go


/*==============================================================*/
/* Table : PortTiming                                           */
/*==============================================================*/
create table PortTiming (
PORTID               numeric              not null,
PRETXWAIT            numeric              not null
     constraint SYS_C0013158 check ("PRETXWAIT" IS NOT NULL),
RTSTOTXWAIT          numeric              not null
     constraint SYS_C0013159 check ("RTSTOTXWAIT" IS NOT NULL),
POSTTXWAIT           numeric              not null
     constraint SYS_C0013160 check ("POSTTXWAIT" IS NOT NULL),
RECEIVEDATAWAIT      numeric              not null
     constraint SYS_C0013161 check ("RECEIVEDATAWAIT" IS NOT NULL),
EXTRATIMEOUT         numeric              not null
     constraint SYS_C0013162 check ("EXTRATIMEOUT" IS NOT NULL)
)
go


alter table PortTiming
   add constraint PK_PORTTIMING primary key  (PORTID)
go


/*==============================================================*/
/* Table : RAWPOINTHISTORY                                      */
/*==============================================================*/
create table RAWPOINTHISTORY (
CHANGEID             numeric              not null,
POINTID              numeric              not null,
TIMESTAMP            datetime             not null,
QUALITY              numeric              not null,
VALUE                float                not null,
millis               smallint             not null
)
go


alter table RAWPOINTHISTORY
   add constraint SYS_C0013322 primary key  (CHANGEID)
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
/* Table : RepeaterRoute                                        */
/*==============================================================*/
create table RepeaterRoute (
ROUTEID              numeric              not null,
DEVICEID             numeric              not null,
VARIABLEBITS         numeric              not null
     constraint SYS_C0013267 check ("VARIABLEBITS" IS NOT NULL),
REPEATERORDER        numeric              not null
     constraint SYS_C0013268 check ("REPEATERORDER" IS NOT NULL)
)
go


alter table RepeaterRoute
   add constraint PK_REPEATERROUTE primary key  (ROUTEID, DEVICEID)
go


/*==============================================================*/
/* Table : Route                                                */
/*==============================================================*/
create table Route (
RouteID              numeric              not null,
DeviceID             numeric              not null,
DefaultRoute         char(1)              not null
)
go


INSERT INTO Route VALUES (0,0,'N');

alter table Route
   add constraint SYS_RoutePK primary key  (RouteID)
go


/*==============================================================*/
/* Index: Indx_RouteDevID                                       */
/*==============================================================*/
create unique  index Indx_RouteDevID on Route (
DeviceID,
RouteID
)
go


/*==============================================================*/
/* Table : STATE                                                */
/*==============================================================*/
create table STATE (
STATEGROUPID         numeric              not null,
RAWSTATE             numeric              not null
     constraint SYS_C0013338 check ("RAWSTATE" IS NOT NULL),
TEXT                 varchar(20)          not null
     constraint SYS_C0013339 check ("TEXT" IS NOT NULL),
FOREGROUNDCOLOR      numeric              not null
     constraint SYS_C0013340 check ("FOREGROUNDCOLOR" IS NOT NULL),
BACKGROUNDCOLOR      numeric              not null
     constraint SYS_C0013341 check ("BACKGROUNDCOLOR" IS NOT NULL),
ImageID              numeric              not null
)
go


INSERT INTO State VALUES ( -1, 0, 'AnalogText', 0, 6 , 0);
INSERT INTO State VALUES ( -2, 0, 'AccumulatorText', 0, 6 , 0);
INSERT INTO State VALUES ( -3, 0, 'CalculatedText', 0, 6 , 0);
INSERT INTO State VALUES ( 0, 0, 'SystemText', 0, 6 , 0);
INSERT INTO State VALUES ( 1, -1, 'Any', 2, 6 , 0);
INSERT INTO State VALUES ( 1, 0, 'Open', 0, 6 , 0);
INSERT INTO State VALUES ( 1, 1, 'Closed', 1, 6 , 0);
INSERT INTO State VALUES ( 2, -1, 'Any', 2, 6 , 0);
INSERT INTO State VALUES ( 2, 0, 'Open', 0, 6 , 0);
INSERT INTO State VALUES ( 2, 1, 'Closed', 1, 6 , 0);
INSERT INTO State VALUES ( 2, 2, 'Unknown', 2, 6 , 0);
INSERT INTO State VALUES ( 3, -1, 'Any', 2, 6 , 0);
INSERT INTO State VALUES ( 3, 0, 'Open', 0, 6 , 0);
INSERT INTO State VALUES ( 3, 1, 'Close', 1, 6 , 0);
INSERT INTO State VALUES ( 3, 2, 'OpenQuestionable', 2, 6 , 0);
INSERT INTO State VALUES ( 3, 3, 'CloseQuestionable', 3, 6 , 0);
INSERT INTO State VALUES ( 3, 4, 'OpenFail', 4, 6 , 0);
INSERT INTO State VALUES ( 3, 5, 'CloseFail', 5, 6 , 0);
INSERT INTO State VALUES ( 3, 6, 'OpenPending', 7, 6 , 0);
INSERT INTO State VALUES ( 3, 7, 'ClosePending', 8, 6 , 0);
INSERT INTO State VALUES(-5, 0, 'Events', 2, 6, 0);
INSERT INTO State VALUES(-5, 1, 'Priority 1', 1, 6, 0);
INSERT INTO State VALUES(-5, 2, 'Priority 2', 4, 6, 0);
INSERT INTO State VALUES(-5, 3, 'Priority 3', 0, 6, 0);
INSERT INTO State VALUES(-5, 4, 'Priority 4', 7, 6, 0);
INSERT INTO State VALUES(-5, 5, 'Priority 5', 8, 6, 0);
INSERT INTO State VALUES(-5, 6, 'Priority 6', 5, 6, 0);
INSERT INTO State VALUES(-5, 7, 'Priority 7', 3, 6, 0);
INSERT INTO State VALUES(-5, 8, 'Priority 8', 2, 6, 0);
INSERT INTO State VALUES(-5, 9, 'Priority 9', 10, 6, 0);
INSERT INTO State VALUES(-5, 10, 'Priority 10', 9, 6, 0);

alter table STATE
   add constraint PK_STATE primary key  (STATEGROUPID, RAWSTATE)
go


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
NAME                 varchar(20)          not null
     constraint SYS_C0013127 check ("NAME" IS NOT NULL),
GroupType            varchar(20)          not null
)
go


INSERT INTO StateGroup VALUES ( -1, 'DefaultAnalog', 'Analog' );
INSERT INTO StateGroup VALUES ( -2, 'DefaultAccumulator', 'Accumulator' );
INSERT INTO StateGroup VALUES ( -3, 'DefaultCalculated', 'Calculated' );
INSERT INTO StateGroup VALUES (-5, 'Event Priority', 'System' );
INSERT INTO StateGroup VALUES ( 0, 'SystemState', 'System' );
INSERT INTO StateGroup VALUES ( 1, 'TwoStateStatus', 'Status' );
INSERT INTO StateGroup VALUES ( 2, 'ThreeStateStatus', 'Status' );
INSERT INTO StateGroup VALUES ( 3, 'CapBankStatus', 'Status' );

alter table STATEGROUP
   add constraint SYS_C0013128 primary key  (STATEGROUPID)
go


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
DATETIME             datetime             not null
     constraint SYS_C0013403 check ("DATETIME" IS NOT NULL),
SOE_TAG              numeric              not null
     constraint SYS_C0013404 check ("SOE_TAG" IS NOT NULL),
TYPE                 numeric              not null
     constraint SYS_C0013405 check ("TYPE" IS NOT NULL),
PRIORITY             numeric              not null
     constraint SYS_C0013406 check ("PRIORITY" IS NOT NULL),
ACTION               varchar(60)          null,
DESCRIPTION          varchar(120)         null,
USERNAME             varchar(30)          null,
millis               smallint             not null
)
go


alter table SYSTEMLOG
   add constraint SYS_C0013407 primary key  (LOGID)
go


/*==============================================================*/
/* Index: Indx_SYSLG_PtId                                       */
/*==============================================================*/
create   index Indx_SYSLG_PtId on SYSTEMLOG (
POINTID
)
go


/*==============================================================*/
/* Index: Indx_SYSLG_Date                                       */
/*==============================================================*/
create   index Indx_SYSLG_Date on SYSTEMLOG (
DATETIME
)
go


/*==============================================================*/
/* Table : SeasonSchedule                                       */
/*==============================================================*/
create table SeasonSchedule (
ScheduleID           numeric              not null,
ScheduleName         varchar(40)          not null,
SpringMonth          numeric              not null,
SpringDay            numeric              not null,
SummerMonth          numeric              not null,
SummerDay            numeric              not null,
FallMonth            numeric              not null,
FallDay              numeric              not null,
WinterMonth          numeric              not null,
WinterDay            numeric              not null
)
go


/* There should be one default season schedule, months range 0-11, days range
*  1-31.  It is implied that the start of Spring signals the end of Winter, start of
*  Fall signals the end of Summer, etc.
*/
insert into SeasonSchedule values(0,'Default Season Schedule',3,15,5,1,8,15,11,1);

alter table SeasonSchedule
   add constraint PK_SEASONSCHEDULE primary key  (ScheduleID)
go


/*==============================================================*/
/* Table : TEMPLATE                                             */
/*==============================================================*/
create table TEMPLATE (
TEMPLATENUM          numeric              not null,
NAME                 varchar(40)          not null,
DESCRIPTION          varchar(200)         null
)
go


insert into template values( 1, 'Standard', 'First Standard Cannon Template');
insert into template values( 2, 'Standard - No PtName', 'Second Standard Cannon  Template');
insert into template values( 3, 'Standard - No DevName', 'Third Standard Cannon  Template');


alter table TEMPLATE
   add constraint SYS_C0013425 primary key  (TEMPLATENUM)
go


/*==============================================================*/
/* Table : TEMPLATECOLUMNS                                      */
/*==============================================================*/
create table TEMPLATECOLUMNS (
TEMPLATENUM          numeric              not null,
TITLE                varchar(50)          not null,
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
insert into templatecolumns values( 1, 'State', 13, 6, 80 );

insert into templatecolumns values( 2, 'Device Name', 5, 1, 85 );
insert into templatecolumns values( 2, 'Value', 9, 2, 85 );
insert into templatecolumns values( 2, 'Quality', 10, 3, 80 );
insert into templatecolumns values( 2, 'Time', 11, 4, 135 );
insert into templatecolumns values( 2, 'State', 13, 5, 80 );

insert into templatecolumns values( 3, 'Point Name', 2, 1, 85 );
insert into templatecolumns values( 3, 'Value', 9, 2, 85 );
insert into templatecolumns values( 3, 'Quality', 10, 3, 80 );
insert into templatecolumns values( 3, 'Time', 11, 4, 135 );
insert into templatecolumns values( 3, 'State', 13, 5, 80 );


alter table TEMPLATECOLUMNS
   add constraint PK_TEMPLATECOLUMNS primary key  (TEMPLATENUM, TITLE)
go


/*==============================================================*/
/* Table : TagLog                                               */
/*==============================================================*/
create table TagLog (
LogID                numeric              not null,
InstanceID           numeric              not null,
PointID              numeric              not null,
TagID                numeric              not null,
UserName             varchar(60)          not null,
Action               varchar(20)          not null,
Description          varchar(120)         not null,
TagTime              datetime             not null,
RefStr               varchar(60)          not null,
ForStr               varchar(60)          not null
)
go


alter table TagLog
   add constraint PK_TAGLOG primary key  (LogID)
go


/*==============================================================*/
/* Table : Tags                                                 */
/*==============================================================*/
create table Tags (
TagID                numeric              not null,
TagName              varchar(60)          not null,
TagLevel             numeric              not null,
Inhibit              char(1)              not null,
ColorID              numeric              not null,
ImageID              numeric              not null
)
go


insert into tags values(-1, 'Out Of Service', 1, 'Y', 1, 0);
insert into tags values(-2, 'Info', 1, 'N', 6, 0);

alter table Tags
   add constraint PK_TAGS primary key  (TagID)
go


/*==============================================================*/
/* Table : UNITMEASURE                                          */
/*==============================================================*/
create table UNITMEASURE (
UOMID                numeric              not null,
UOMName              varchar(8)           not null,
CalcType             numeric              not null,
LongName             varchar(40)          not null,
Formula              varchar(80)          not null
)
go


INSERT INTO UnitMeasure VALUES ( 0,'kW', 0,'kW','(none)' );
INSERT INTO UnitMeasure VALUES ( 1,'kWH', 0,'kWH','usage' );
INSERT INTO UnitMeasure VALUES ( 2,'kVA', 0,'kVA','(none)' );
INSERT INTO UnitMeasure VALUES ( 3,'kVAr', 0,'kVAr','(none)' );
INSERT INTO UnitMeasure VALUES ( 4,'kVAh', 0,'kVAh','usage' );
INSERT INTO UnitMeasure VALUES ( 5,'kVArh', 0,'kVArh','usage' );
INSERT INTO UnitMeasure VALUES ( 6,'kVolts', 0,'kVolts','(none)' );
INSERT INTO UnitMeasure VALUES ( 7,'kQ', 0,'kQ','(none)' );
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
INSERT INTO UnitMeasure VALUES ( 21,'MWh', 0,'MWh','usage' );
INSERT INTO UnitMeasure VALUES ( 22,'MVA', 0,'MVA','(none)' );
INSERT INTO UnitMeasure VALUES ( 23,'MVAr', 0,'MVAr','(none)' );
INSERT INTO UnitMeasure VALUES ( 24,'MVAh', 0,'MVAh','usage' );
INSERT INTO UnitMeasure VALUES ( 25,'MVArh', 0,'MVArh','usage' );
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
INSERT INTO UnitMeasure VALUES ( 43,'Tap', 0,'LTC Tap Position','(none)' );
INSERT INTO UnitMeasure VALUES ( 44,'Miles', 0,'Miles','(none)' );
INSERT INTO UnitMeasure VALUES ( 45,'Ms', 0,'Milliseconds','(none)' );
INSERT INTO UnitMeasure VALUES( 46,'PPM',0,'Parts Per Million','(none)');
INSERT INTO UnitMeasure VALUES( 47,'MPH',0,'Miles Per Hour','(none)');
INSERT INTO UnitMeasure VALUES( 48,'Inches',0,'Inches','(none)');
INSERT INTO UnitMeasure VALUES( 49,'KPH',0,'Kilometers Per Hour','(none)');
INSERT INTO UnitMeasure VALUES( 50,'Milibars',0,'Milibars','(none)');
INSERT INTO UnitMeasure VALUES( 51,'km/h',0,'Kilometers Per Hour','(none)');
INSERT INTO UnitMeasure VALUES( 52,'m/s',0,'Meters Per Second','(none)');
INSERT INTO UnitMeasure VALUES( 53,'KV', 0,'KVolts','(none)' );
INSERT INTO UnitMeasure VALUES( 54,'UNDEF', 0,'Undefined','(none)' );
INSERT INTO UnitMeasure VALUES( 55,'A', 0,'Amps (A)','(none)' );

alter table UNITMEASURE
   add constraint SYS_C0013344 primary key  (UOMID)
go


/*==============================================================*/
/* Table : VersacomRoute                                        */
/*==============================================================*/
create table VersacomRoute (
ROUTEID              numeric              not null,
UTILITYID            numeric              not null
     constraint SYS_C0013276 check ("UTILITYID" IS NOT NULL),
SECTIONADDRESS       numeric              not null
     constraint SYS_C0013277 check ("SECTIONADDRESS" IS NOT NULL),
CLASSADDRESS         numeric              not null
     constraint SYS_C0013278 check ("CLASSADDRESS" IS NOT NULL),
DIVISIONADDRESS      numeric              not null
     constraint SYS_C0013279 check ("DIVISIONADDRESS" IS NOT NULL),
BUSNUMBER            numeric              not null
     constraint SYS_C0013280 check ("BUSNUMBER" IS NOT NULL),
AMPCARDSET           numeric              not null
     constraint SYS_C0013281 check ("AMPCARDSET" IS NOT NULL)
)
go


alter table VersacomRoute
   add constraint PK_VERSACOMROUTE primary key  (ROUTEID)
go


/*==============================================================*/
/* Table : YukonGroup                                           */
/*==============================================================*/
create table YukonGroup (
GroupID              numeric              not null,
GroupName            varchar(120)         not null,
GroupDescription     varchar(200)         not null
)
go


insert into YukonGroup values(-1,'Yukon Grp','The default system user group that allows limited user interaction.');
insert into YukonGroup values(-2,'System Administrator Grp','A set of roles that allow administrative access to the system.');
insert into YukonGroup values(-100,'Operators Grp', 'The default group of yukon operators');
insert into yukongroup values(-200,'Esub Users Grp', 'The default group of esubstation users');
insert into yukongroup values(-201,'Esub Operators Grp', 'The default group of esubstation operators');
insert into yukongroup values (-301,'Web Client Operators Grp','The default group of web client operators');
insert into yukongroup values (-300,'Residential Customers Grp','The default group of residential customers');
insert into yukongroup values (-302, 'Web Client Customers Grp', 'The default group of web client customers');

alter table YukonGroup
   add constraint PK_YUKONGROUP primary key  (GroupID)
go


/*==============================================================*/
/* Table : YukonGroupRole                                       */
/*==============================================================*/
create table YukonGroupRole (
GroupRoleID          numeric              not null,
GroupID              numeric              not null,
RoleID               numeric              not null,
RolePropertyID       numeric              not null,
Value                varchar(1000)        not null
)
go


/* Assign the default Yukon role to the default Yukon group */
insert into YukonGroupRole values(-1,-1,-1,-1000,'(none)');
insert into YukonGroupRole values(-2,-1,-1,-1001,'(none)');
insert into YukonGroupRole values(-3,-1,-1,-1002,'(none)');
insert into YukonGroupRole values(-4,-1,-1,-1003,'(none)');
insert into YukonGroupRole values(-5,-1,-1,-1004,'(none)');
insert into YukonGroupRole values(-6,-1,-1,-1005,'(none)');
insert into YukonGroupRole values(-7,-1,-1,-1006,'(none)');
insert into YukonGroupRole values(-8,-1,-1,-1007,'(none)');
insert into YukonGroupRole values(-9,-1,-1,-1008,'(none)');
insert into YukonGroupRole values(-10,-1,-1,-1009,'(none)');
insert into YukonGroupRole values(-11,-1,-1,-1010,'(none)');
insert into YukonGroupRole values(-12,-1,-1,-1011,'(none)');
insert into YukonGroupRole values(-13,-1,-1,-1012,'(none)');
insert into YukonGroupRole values(-14,-1,-1,-1013,'(none)');
insert into YukonGroupRole values(-15,-1,-1,-1014,'CannonLogo.gif');

/* Logging default Yukon group */
insert into YukonGroupRole values(-50,-1,-3,-1200,'(none)');
insert into YukonGroupRole values(-51,-1,-3,-1201,'(none)');
insert into YukonGroupRole values(-52,-1,-3,-1202,'(none)');
insert into YukonGroupRole values(-53,-1,-3,-1203,'(none)');
insert into YukonGroupRole values(-54,-1,-3,-1204,'(none)');
insert into YukonGroupRole values(-55,-1,-3,-1205,'(none)');
insert into YukonGroupRole values(-56,-1,-3,-1206,'(none)');
insert into YukonGroupRole values(-57,-1,-3,-1207,'(none)');
insert into YukonGroupRole values(-58,-1,-3,-1208,'(none)');
insert into YukonGroupRole values(-59,-1,-3,-1209,'(none)');
insert into YukonGroupRole values(-60,-1,-3,-1210,'(none)');
insert into YukonGroupRole values(-61,-1,-3,-1211,'(none)');
insert into YukonGroupRole values(-62,-1,-3,-1212,'(none)');
insert into YukonGroupRole values(-63,-1,-3,-1213,'(none)');
insert into YukonGroupRole values(-64,-1,-3,-1214,'(none)');
insert into YukonGroupRole values(-65,-1,-3,-1215,'(none)');
insert into YukonGroupRole values(-66,-1,-3,-1216,'(none)');


/* Calc Historical for Yukon Gorup */
insert into YukonGroupRole values(-190,-1,-104,-10400,'(none)');
insert into YukonGroupRole values(-191,-1,-104,-10401,'(none)');
insert into YukonGroupRole values(-192,-1,-104,-10402,'(none)');

/* Web Graph for Yukon Gorup */
insert into YukonGroupRole values(-210,-1,-105,-10500,'(none)');
insert into YukonGroupRole values(-211,-1,-105,-10501,'(none)');

/* Billing for Yukon Gorup */
insert into YukonGroupRole values(-230,-1,-106,-10600,'(none)');
insert into YukonGroupRole values(-231,-1,-106,-10601,'(none)');

/* Assign roles to the default operator group to allow them to use all the main rich Yukon applications */
/* Database Editor */
insert into YukonGroupRole values(-100,-100,-100,-10000,'(none)');
insert into YukonGroupRole values(-101,-100,-100,-10001,'(none)');
insert into YukonGroupRole values(-102,-100,-100,-10002,'(none)');
insert into YukonGroupRole values(-103,-100,-100,-10003,'(none)');
insert into YukonGroupRole values(-104,-100,-100,-10004,'(none)');
insert into YukonGroupRole values(-105,-100,-100,-10005,'(none)');
insert into YukonGroupRole values(-107,-100,-100,-10007,'(none)');
insert into YukonGroupRole values(-108,-100,-100,-10008,'(none)');
insert into YukonGroupRole values(-109,-100,-100,-10009,'(none)');


/* TDC */
insert into YukonGroupRole values(-120,-100,-101,-10100,'(none)');
insert into YukonGroupRole values(-121,-100,-101,-10101,'(none)');
insert into YukonGroupRole values(-122,-100,-101,-10102,'(none)');
insert into YukonGroupRole values(-123,-100,-101,-10103,'(none)');
insert into YukonGroupRole values(-124,-100,-101,-10104,'(none)');
insert into YukonGroupRole values(-125,-100,-101,-10105,'(none)');
insert into YukonGroupRole values(-126,-100,-101,-10106,'(none)');
insert into YukonGroupRole values(-127,-100,-101,-10107,'(none)');
insert into YukonGroupRole values(-128,-100,-101,-10108,'(none)');
insert into YukonGroupRole values(-129,-100,-101,-10109,'(none)');

/* Trending */
insert into YukonGroupRole values(-150,-100,-102,-10200,'(none)');

/* Commander */
insert into YukonGroupRole values(-170,-100,-103,-10300,'(none)');
insert into YukonGroupRole values(-171,-100,-103,-10301,'true');
insert into YukonGroupRole values(-172,-100,-103,-10302,'true');
insert into YukonGroupRole values(-173,-100,-103,-10303,'false');
insert into YukonGroupRole values(-174,-100,-103,-10304,'false');

/* Esubstation Editor */
insert into YukonGroupRole values(-250,-100,-107,-10700,'(none)');

/* Assign roles to the default Esub Users */
insert into YukonGroupRole values(-300,-200,-206,-20600,'(none)');
insert into YukonGroupRole values(-301,-200,-206,-20601,'(none)');
insert into YukonGroupRole values(-302,-200,-206,-20602,'(none)');

/* Assign roles to the default Esub Operators */
insert into YukonGroupRole values(-350,-201,-206,-20600,'(none)');
insert into YukonGroupRole values(-351,-201,-206,-20601,'true');
insert into YukonGroupRole values(-352,-201,-206,-20602,'false');

/* Web Client Customers Web Client role */
insert into yukongrouprole values (-400, -302, -108, -10800, '/user/CILC/user_trending.jsp');
insert into yukongrouprole values (-402, -302, -108, -10802, '(none)');
insert into yukongrouprole values (-403, -302, -108, -10803, '(none)');
insert into yukongrouprole values (-404, -302, -108, -10804, '(none)');
insert into yukongrouprole values (-405, -302, -108, -10805, '(none)');
insert into yukongrouprole values (-406, -302, -108, -10806, '(none)');

/* Web Client Customers Direct Load Control role */
insert into yukongrouprole values (-407, -302, -300, -30000, '(none)');
insert into yukongrouprole values (-408, -302, -300, -30001, 'true');

/* Web Client Customers Curtailment role */
insert into yukongrouprole values (-409, -302, -301, -30100, '(none)');
insert into yukongrouprole values (-410, -302, -301, -30101, '(none)');

/* Web Client Customers Energy Buyback role */
insert into yukongrouprole values (-411, -302, -302, -30200, '(none)');
insert into yukongrouprole values (-412, -302, -302, -30200, '(none)');

/* Web Client Customers Commercial Metering role */
insert into yukongrouprole values (-413, -302, -304, -30400, '(none)');
insert into yukongrouprole values (-414, -302, -304, -30401, 'true');

/* Web Client Customers Administrator role */
insert into yukongrouprole values (-415, -302, -305, -30500, 'true');
insert into YukonGroupRole values (-416, -302, -304, -30402, '(none)');
insert into YukonGroupRole values (-417, -302, -304, -30403, '(none)');

insert into yukongrouprole values (-500,-300,-108,-10800,'/user/ConsumerStat/stat/General.jsp');
insert into yukongrouprole values (-502,-300,-108,-10802,'(none)');
insert into yukongrouprole values (-503,-300,-108,-10803,'(none)');
insert into yukongrouprole values (-504,-300,-108,-10804,'(none)');
insert into yukongrouprole values (-505,-300,-108, -10805,'yukon/DemoHeaderCES.gif');
insert into yukongrouprole values (-506,-300,-108,-10806,'(none)');
insert into yukongrouprole values (-507,-300,-108,-10807,'(none)');
insert into yukongrouprole values (-508,-300,-108,-10808,'(none)');

insert into yukongrouprole values (-520,-300,-400,-40000,'true');
insert into yukongrouprole values (-521,-300,-400,-40001,'true');
insert into yukongrouprole values (-522,-300,-400,-40002,'false');
insert into yukongrouprole values (-523,-300,-400,-40003,'true');
insert into yukongrouprole values (-524,-300,-400,-40004,'true');
insert into yukongrouprole values (-525,-300,-400,-40005,'true');
insert into yukongrouprole values (-526,-300,-400,-40006,'true');
insert into yukongrouprole values (-527,-300,-400,-40007,'true');
insert into yukongrouprole values (-528,-300,-400,-40008,'true');
insert into yukongrouprole values (-529,-300,-400,-40009,'true');
insert into yukongrouprole values (-530,-300,-400,-40010,'true');

insert into yukongrouprole values (-550,-300,-400,-40050,'false');
insert into yukongrouprole values (-551,-300,-400,-40051,'false');
insert into yukongrouprole values (-552,-300,-400,-40052,'false');
insert into yukongrouprole values (-554,-300,-400,-40054,'false');
insert into yukongrouprole values (-555,-300,-400,-40055,'(none)');

insert into yukongrouprole values (-600,-300,-400,-40100,'(none)');
insert into yukongrouprole values (-601,-300,-400,-40101,'(none)');
insert into yukongrouprole values (-602,-300,-400,-40102,'(none)');
insert into yukongrouprole values (-610,-300,-400,-40110,'(none)');
insert into yukongrouprole values (-611,-300,-400,-40111,'(none)');
insert into yukongrouprole values (-612,-300,-400,-40112,'(none)');
insert into yukongrouprole values (-613,-300,-400,-40113,'(none)');
insert into yukongrouprole values (-614,-300,-400,-40114,'(none)');
insert into yukongrouprole values (-615,-300,-400,-40115,'(none)');
insert into yukongrouprole values (-616,-300,-400,-40116,'(none)');
insert into yukongrouprole values (-617,-300,-400,-40117,'(none)');
insert into yukongrouprole values (-630,-300,-400,-40130,'(none)');
insert into yukongrouprole values (-631,-300,-400,-40131,'(none)');
insert into yukongrouprole values (-632,-300,-400,-40132,'(none)');
insert into yukongrouprole values (-633,-300,-400,-40133,'(none)');
insert into yukongrouprole values (-634,-300,-400,-40134,'(none)');
insert into yukongrouprole values (-650,-300,-400,-40150,'(none)');
insert into yukongrouprole values (-651,-300,-400,-40151,'(none)');
insert into yukongrouprole values (-652,-300,-400,-40152,'(none)');
insert into yukongrouprole values (-653,-300,-400,-40153,'(none)');
insert into yukongrouprole values (-654,-300,-400,-40154,'(none)');
insert into yukongrouprole values (-655,-300,-400,-40155,'(none)');
insert into yukongrouprole values (-656,-300,-400,-40156,'(none)');
insert into yukongrouprole values (-657,-300,-400,-40157,'(none)');
insert into yukongrouprole values (-658,-300,-400,-40158,'(none)');
insert into yukongrouprole values (-670,-300,-400,-40170,'(none)');
insert into yukongrouprole values (-671,-300,-400,-40171,'(none)');
insert into yukongrouprole values (-672,-300,-400,-40172,'(none)');
insert into yukongrouprole values (-673,-300,-400,-40173,'(none)');
insert into yukongrouprole values (-680,-300,-400,-40180,'(none)');
insert into yukongrouprole values (-681,-300,-400,-40181,'(none)');

insert into yukongrouprole values (-700,-301,-108,-10800,'/operator/Operations.jsp');
insert into yukongrouprole values (-702,-301,-108,-10802,'(none)');
insert into yukongrouprole values (-703,-301,-108,-10803,'(none)');
insert into yukongrouprole values (-704,-301,-108,-10804,'(none)');
insert into yukongrouprole values (-705,-301,-108,-10805,'(none)');
insert into yukongrouprole values (-706,-301,-108,-10806,'(none)');
insert into yukongrouprole values (-707,-301,-108,-10807,'(none)');
insert into yukongrouprole values (-708,-301,-108,-10808,'(none)');

insert into yukongrouprole values (-720,-301,-201,-20100,'true');
insert into yukongrouprole values (-721,-301,-201,-20101,'true');
insert into yukongrouprole values (-722,-301,-201,-20102,'true');
insert into yukongrouprole values (-723,-301,-201,-20103,'true');
insert into yukongrouprole values (-724,-301,-201,-20104,'false');
insert into yukongrouprole values (-725,-301,-201,-20105,'false');
insert into yukongrouprole values (-726,-301,-201,-20106,'true');
insert into yukongrouprole values (-727,-301,-201,-20107,'true');
insert into yukongrouprole values (-728,-301,-201,-20108,'true');
insert into yukongrouprole values (-729,-301,-201,-20109,'true');
insert into yukongrouprole values (-730,-301,-201,-20110,'true');
insert into yukongrouprole values (-731,-301,-201,-20111,'true');
insert into yukongrouprole values (-732,-301,-201,-20112,'true');
insert into yukongrouprole values (-733,-301,-201,-20113,'true');
insert into yukongrouprole values (-734,-301,-201,-20114,'true');
insert into yukongrouprole values (-735,-301,-201,-20115,'true');
insert into yukongrouprole values (-736,-301,-201,-20116,'true');
insert into yukongrouprole values (-737,-301,-201,-20117,'true');
insert into YukonGroupRole values (-738,-301,-201,-20118,'true');

insert into yukongrouprole values (-750,-301,-201,-20150,'true');
insert into yukongrouprole values (-751,-301,-201,-20151,'true');
insert into yukongrouprole values (-752,-301,-201,-20152,'(none)');
insert into yukongrouprole values (-753,-301,-201,-20153,'(none)');
insert into yukongrouprole values (-754,-301,-201,-20154,'false');
insert into yukongrouprole values (-755,-301,-201,-20155,'true');
insert into yukongrouprole values (-756,-301,-201,-20156,'true');
insert into yukongrouprole values (-757,-301,-201,-20157,'(none)');

insert into yukongrouprole values (-765,-301,-210,-21000,'(none)');
insert into yukongrouprole values (-766,-301,-210,-21001,'(none)');

insert into yukongrouprole values (-770,-301,-202,-20200,'(none)');
insert into yukongrouprole values (-775,-301,-203,-20300,'(none)');
insert into yukongrouprole values (-776,-301,-203,-20301,'(none)');
insert into yukongrouprole values (-780,-301,-204,-20400,'(none)');
insert into yukongrouprole values (-785,-301,-205,-20500,'(none)');
insert into yukongrouprole values (-790,-301,-207,-20700,'(none)');
insert into yukongrouprole values (-791,-301,-209,-20900,'(none)');
insert into yukongrouprole values (-792,-301,-209,-20901,'(none)');
insert into yukongrouprole values (-793,-301,-209,-20902,'(none)');
insert into yukongrouprole values (-794,-301,-209,-20903,'(none)');
insert into yukongrouprole values (-795,-301,-209,-20904,'(none)');

insert into yukongrouprole values (-800,-301,-201,-20800,'(none)');
insert into yukongrouprole values (-801,-301,-201,-20801,'(none)');
insert into yukongrouprole values (-810,-301,-201,-20810,'(none)');
insert into yukongrouprole values (-813,-301,-201,-20813,'(none)');
insert into yukongrouprole values (-814,-301,-201,-20814,'(none)');
insert into yukongrouprole values (-815,-301,-201,-20815,'(none)');
insert into yukongrouprole values (-816,-301,-201,-20816,'(none)');
insert into yukongrouprole values (-819,-301,-201,-20819,'(none)');
insert into yukongrouprole values (-820,-301,-201,-20820,'(none)');
insert into yukongrouprole values (-830,-301,-201,-20830,'(none)');
insert into yukongrouprole values (-831,-301,-201,-20831,'(none)');
insert into yukongrouprole values (-832,-301,-201,-20832,'(none)');
insert into yukongrouprole values (-833,-301,-201,-20833,'(none)');
insert into yukongrouprole values (-834,-301,-201,-20834,'(none)');
insert into yukongrouprole values (-850,-301,-201,-20850,'(none)');
insert into yukongrouprole values (-851,-301,-201,-20851,'(none)');
insert into yukongrouprole values (-852,-301,-201,-20852,'(none)');
insert into yukongrouprole values (-853,-301,-201,-20853,'(none)');
insert into yukongrouprole values (-854,-301,-201,-20854,'(none)');
insert into yukongrouprole values (-855,-301,-201,-20855,'(none)');
insert into yukongrouprole values (-856,-301,-201,-20856,'(none)');
insert into yukongrouprole values (-870,-301,-201,-20870,'(none)');


/* Add the user-control properties to the Web Client Customers group */
insert into yukongrouprole values ( -985, -302, -306, -30600, '(none)');
insert into yukongrouprole values ( -986, -302, -306, -30601, 'true');
insert into yukongrouprole values ( -987, -302, -306, -30602, 'true');
insert into yukongrouprole values ( -988, -302, -306, -30603, 'true');


/* START the Admin role Group */
insert into YukonGroupRole values(-1000,-2,-100,-10000,'(none)');
insert into YukonGroupRole values(-1001,-2,-100,-10001,'(none)');
insert into YukonGroupRole values(-1002,-2,-100,-10002,'(none)');
insert into YukonGroupRole values(-1003,-2,-100,-10003,'(none)');
insert into YukonGroupRole values(-1004,-2,-100,-10004,'(none)');
insert into YukonGroupRole values(-1005,-2,-100,-10005,'(none)');
insert into YukonGroupRole values(-1007,-2,-100,-10007,'(none)');
insert into YukonGroupRole values(-1008,-2,-100,-10008,'(none)');
insert into YukonGroupRole values(-1009,-2,-100,-10009,'(none)');


insert into YukonGroupRole values(-1020,-2,-101,-10100,'(none)');
insert into YukonGroupRole values(-1021,-2,-101,-10101,'(none)');
insert into YukonGroupRole values(-1022,-2,-101,-10102,'(none)');
insert into YukonGroupRole values(-1023,-2,-101,-10103,'(none)');
insert into YukonGroupRole values(-1024,-2,-101,-10104,'(none)');
insert into YukonGroupRole values(-1025,-2,-101,-10105,'(none)');
insert into YukonGroupRole values(-1026,-2,-101,-10106,'(none)');
insert into YukonGroupRole values(-1027,-2,-101,-10107,'(none)');
insert into YukonGroupRole values(-1028,-2,-101,-10108,'(none)');
insert into YukonGroupRole values(-1029,-2,-101,-10109,'(none)');
insert into YukonGroupRole values(-1030,-2,-101,-10111,'(none)');

insert into YukonGroupRole values(-1050,-2,-102,-10200,'(none)');

insert into YukonGroupRole values(-1070,-2,-103,-10300,'(none)');
insert into YukonGroupRole values(-1071,-2,-103,-10301,'true');
insert into YukonGroupRole values(-1072,-2,-103,-10302,'true');
insert into YukonGroupRole values(-1073,-2,-103,-10303,'false');
insert into YukonGroupRole values(-1074,-2,-103,-10304,'false');

insert into YukonGroupRole values(-1080,-2,-107,-10700,'(none)');
insert into YukonGroupRole values(-1081,-2,-206,-20600,'(none)');
insert into YukonGroupRole values(-1082,-2,-206,-20601,'(none)');
insert into YukonGroupRole values(-1083,-2,-206,-20602,'(none)');
insert into YukonGroupRole values(-1084,-2,-206,-20600,'(none)');
insert into YukonGroupRole values(-1085,-2,-206,-20601,'true');
insert into YukonGroupRole values(-1086,-2,-206,-20602,'false');

insert into YukonGroupRole values (-1090,-2, -108, -10800, '/user/CILC/user_trending.jsp');
insert into YukonGroupRole values (-1091,-2, -108, -10802, '(none)');
insert into YukonGroupRole values (-1092,-2, -108, -10803, '(none)');
insert into YukonGroupRole values (-1093,-2, -108, -10804, '(none)');
insert into YukonGroupRole values (-1094,-2, -108, -10805, '(none)');
insert into YukonGroupRole values (-1095,-2, -108, -10806, '(none)');
insert into YukonGroupRole values (-1096,-2, -108, -10807, '(none)');
insert into YukonGroupRole values (-1097,-2, -108, -10808, '(none)');

insert into YukonGroupRole values (-1100,-2, -300, -30000, '(none)');
insert into YukonGroupRole values (-1101,-2, -300, -30001, 'true');

insert into YukonGroupRole values (-1110,-2, -301, -30100, '(none)');
insert into YukonGroupRole values (-1111,-2, -301, -30101, '(none)');

insert into YukonGroupRole values (-1120,-2, -302, -30200, '(none)');
insert into YukonGroupRole values (-1121,-2, -302, -30200, '(none)');

insert into YukonGroupRole values (-1130,-2, -304, -30400, '(none)');
insert into YukonGroupRole values (-1131,-2, -304, -30401, 'true');
insert into YukonGroupRole values (-1132,-2, -304, -30402, '(none)');
insert into YukonGroupRole values (-1133,-2, -304, -30403, '(none)');

insert into YukonGroupRole values (-1140,-2, -305, -30500, 'true');
insert into YukonGroupRole values (-1141,-2,-400,-40000,'(none)');
insert into YukonGroupRole values (-1142,-2,-400,-40001,'(none)');
insert into YukonGroupRole values (-1143,-2,-400,-40002,'(none)');
insert into YukonGroupRole values (-1144,-2,-400,-40003,'(none)');
insert into YukonGroupRole values (-1145,-2,-400,-40004,'(none)');
insert into YukonGroupRole values (-1146,-2,-400,-40005,'(none)');
insert into YukonGroupRole values (-1147,-2,-400,-40006,'(none)');
insert into YukonGroupRole values (-1148,-2,-400,-40007,'(none)');
insert into YukonGroupRole values (-1149,-2,-400,-40008,'(none)');
insert into YukonGroupRole values (-1150,-2,-400,-40009,'(none)');
insert into YukonGroupRole values (-1151,-2,-400,-40010,'(none)');
insert into YukonGroupRole values (-1152,-2,-400,-40050,'(none)');
insert into YukonGroupRole values (-1153,-2,-400,-40051,'(none)');
insert into YukonGroupRole values (-1154,-2,-400,-40052,'(none)');
insert into YukonGroupRole values (-1155,-2,-400,-40054,'(none)');
insert into YukonGroupRole values (-1156,-2,-400,-40055,'(none)');

insert into YukonGroupRole values (-1157,-2,-400,-40100,'(none)');
insert into YukonGroupRole values (-1158,-2,-400,-40101,'(none)');
insert into YukonGroupRole values (-1159,-2,-400,-40110,'(none)');
insert into YukonGroupRole values (-1160,-2,-400,-40111,'(none)');
insert into YukonGroupRole values (-1161,-2,-400,-40112,'(none)');
insert into YukonGroupRole values (-1162,-2,-400,-40113,'(none)');
insert into YukonGroupRole values (-1163,-2,-400,-40114,'(none)');
insert into YukonGroupRole values (-1164,-2,-400,-40115,'(none)');
insert into YukonGroupRole values (-1165,-2,-400,-40116,'(none)');
insert into YukonGroupRole values (-1166,-2,-400,-40117,'(none)');
insert into YukonGroupRole values (-1167,-2,-400,-40130,'(none)');
insert into YukonGroupRole values (-1168,-2,-400,-40131,'(none)');
insert into YukonGroupRole values (-1169,-2,-400,-40132,'(none)');
insert into YukonGroupRole values (-1170,-2,-400,-40133,'(none)');
insert into YukonGroupRole values (-1171,-2,-400,-40134,'(none)');
insert into YukonGroupRole values (-1172,-2,-400,-40150,'(none)');
insert into YukonGroupRole values (-1173,-2,-400,-40151,'(none)');
insert into YukonGroupRole values (-1174,-2,-400,-40152,'(none)');
insert into YukonGroupRole values (-1175,-2,-400,-40153,'(none)');
insert into YukonGroupRole values (-1176,-2,-400,-40154,'(none)');
insert into YukonGroupRole values (-1177,-2,-400,-40155,'(none)');
insert into YukonGroupRole values (-1178,-2,-400,-40156,'(none)');
insert into YukonGroupRole values (-1179,-2,-400,-40157,'(none)');
insert into YukonGroupRole values (-1180,-2,-400,-40158,'(none)');
insert into YukonGroupRole values (-1181,-2,-400,-40170,'(none)');
insert into YukonGroupRole values (-1182,-2,-400,-40171,'(none)');
insert into YukonGroupRole values (-1183,-2,-400,-40172,'(none)');
insert into YukonGroupRole values (-1184,-2,-400,-40173,'(none)');
insert into YukonGroupRole values (-1185,-2,-400,-40180,'(none)');
insert into YukonGroupRole values (-1186,-2,-400,-40181,'(none)');

insert into YukonGroupRole values (-1187,-2,-201,-20100,'(none)');
insert into YukonGroupRole values (-1188,-2,-201,-20101,'(none)');
insert into YukonGroupRole values (-1189,-2,-201,-20102,'(none)');
insert into YukonGroupRole values (-1190,-2,-201,-20103,'(none)');
insert into YukonGroupRole values (-1191,-2,-201,-20104,'(none)');
insert into YukonGroupRole values (-1192,-2,-201,-20105,'(none)');
insert into YukonGroupRole values (-1193,-2,-201,-20106,'(none)');
insert into YukonGroupRole values (-1194,-2,-201,-20107,'(none)');
insert into YukonGroupRole values (-1195,-2,-201,-20108,'(none)');
insert into YukonGroupRole values (-1196,-2,-201,-20109,'(none)');
insert into YukonGroupRole values (-1197,-2,-201,-20110,'(none)');
insert into YukonGroupRole values (-1198,-2,-201,-20111,'(none)');
insert into YukonGroupRole values (-1199,-2,-201,-20112,'(none)');
insert into YukonGroupRole values (-1200,-2,-201,-20113,'(none)');
insert into YukonGroupRole values (-1201,-2,-201,-20114,'(none)');
insert into YukonGroupRole values (-1202,-2,-201,-20115,'(none)');
insert into YukonGroupRole values (-1203,-2,-201,-20116,'(none)');
insert into YukonGroupRole values (-1204,-2,-201,-20117,'(none)');
insert into YukonGroupRole values (-1205,-2,-201,-20118,'true');

insert into YukonGroupRole values (-1250,-2,-201,-20150,'(none)');
insert into YukonGroupRole values (-1251,-2,-201,-20151,'(none)');
insert into YukonGroupRole values (-1252,-2,-201,-20152,'(none)');
insert into YukonGroupRole values (-1253,-2,-201,-20153,'(none)');
insert into YukonGroupRole values (-1254,-2,-201,-20154,'(none)');
insert into YukonGroupRole values (-1255,-2,-201,-20155,'(none)');
insert into YukonGroupRole values (-1256,-2,-201,-20156,'(none)');
insert into YukonGroupRole values (-1257,-2,-201,-20157,'(none)');

insert into YukonGroupRole values (-1265,-2,-210,-21000,'(none)');
insert into YukonGroupRole values (-1266,-2,-210,-21001,'(none)');

insert into YukonGroupRole values (-1270,-2,-202,-20200,'(none)');
insert into YukonGroupRole values (-1275,-2,-203,-20300,'(none)');
insert into YukonGroupRole values (-1276,-2,-203,-20301,'(none)');
insert into YukonGroupRole values (-1280,-2,-204,-20400,'(none)');
insert into YukonGroupRole values (-1285,-2,-205,-20500,'(none)');
insert into YukonGroupRole values (-1290,-2,-207,-20700,'(none)');
insert into YukonGroupRole values (-1291,-2,-209,-20900,'(none)');
insert into YukonGroupRole values (-1292,-2,-209,-20901,'(none)');
insert into YukonGroupRole values (-1293,-2,-209,-20902,'(none)');
insert into YukonGroupRole values (-1294,-2,-209,-20903,'(none)');
insert into YukonGroupRole values (-1295,-2,-209,-20904,'(none)');

insert into YukonGroupRole values (-1300,-2,-201,-20800,'(none)');
insert into YukonGroupRole values (-1301,-2,-201,-20801,'(none)');
insert into YukonGroupRole values (-1310,-2,-201,-20810,'(none)');
insert into YukonGroupRole values (-1313,-2,-201,-20813,'(none)');
insert into YukonGroupRole values (-1314,-2,-201,-20814,'(none)');
insert into YukonGroupRole values (-1315,-2,-201,-20815,'(none)');
insert into YukonGroupRole values (-1316,-2,-201,-20816,'(none)');
insert into YukonGroupRole values (-1319,-2,-201,-20819,'(none)');
insert into YukonGroupRole values (-1320,-2,-201,-20820,'(none)');
insert into YukonGroupRole values (-1330,-2,-201,-20830,'(none)');
insert into YukonGroupRole values (-1331,-2,-201,-20831,'(none)');
insert into YukonGroupRole values (-1332,-2,-201,-20832,'(none)');
insert into YukonGroupRole values (-1333,-2,-201,-20833,'(none)');
insert into YukonGroupRole values (-1334,-2,-201,-20834,'(none)');
insert into YukonGroupRole values (-1350,-2,-201,-20850,'(none)');
insert into YukonGroupRole values (-1351,-2,-201,-20851,'(none)');
insert into YukonGroupRole values (-1352,-2,-201,-20852,'(none)');
insert into YukonGroupRole values (-1353,-2,-201,-20853,'(none)');
insert into YukonGroupRole values (-1354,-2,-201,-20854,'(none)');
insert into YukonGroupRole values (-1355,-2,-201,-20855,'(none)');
insert into YukonGroupRole values (-1356,-2,-201,-20856,'(none)');
insert into YukonGroupRole values (-1370,-2,-201,-20870,'(none)');
/* END the Admin role Group */


alter table YukonGroupRole
   add constraint PK_YUKONGRPROLE primary key  (GroupRoleID)
go


/*==============================================================*/
/* Table : YukonImage                                           */
/*==============================================================*/
create table YukonImage (
ImageID              numeric              not null,
ImageCategory        varchar(20)          null,
ImageName            varchar(80)          null,
ImageValue           image                null
)
go


insert into YukonImage values( 0, '(none)', '(none)', null );

alter table YukonImage
   add constraint PK_YUKONIMAGE primary key  (ImageID)
go


/*==============================================================*/
/* Table : YukonListEntry                                       */
/*==============================================================*/
create table YukonListEntry (
EntryID              numeric              not null,
ListID               numeric              not null,
EntryOrder           numeric              not null,
EntryText            varchar(50)          not null,
YukonDefinitionID    numeric              not null
)
go


insert into YukonListEntry values( 0, 0, 0, '(none)', 0 );
insert into YukonListEntry values( 1, 1, 0, 'Email', 1 );
insert into YukonListEntry values( 2, 1, 0, 'Phone Number', 2 );
insert into YukonListEntry values( 3, 1, 0, 'Pager Number', 2 );
insert into YukonListEntry values( 4, 1, 0, 'Fax Number', 2 );
insert into YukonListEntry values( 5, 1, 0, 'Home Phone', 2 );
insert into YukonListEntry values( 6, 1, 0, 'Work Phone', 2 );


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
insert into YukonListEntry values (1059,1005,-1,'Commercial ExpressStat',1304);
insert into YukonListEntry values (1071,1006,0,'Available',1701);
insert into YukonListEntry values (1072,1006,0,'Temp Unavail',1702);
insert into YukonListEntry values (1073,1006,0,'Unavailable',1703);
insert into YukonListEntry values (1081,1007,0,'(Default)',1400);
insert into YukonListEntry values (1082,1007,0,'Air Conditioner',1401);
insert into YukonListEntry values (1083,1007,0,'Water Heater',1402);
insert into YukonListEntry values (1084,1007,0,'Storage Heat',1403);
insert into YukonListEntry values (1085,1007,0,'Heat Pump',1404);
insert into YukonListEntry values (1086,1007,0,'Dual Fuel',1405);
insert into YukonListEntry values (1087,1007,0,'Generator',1406);
insert into YukonListEntry values (1088,1007,0,'Grain Dryer',1407);
insert into YukonListEntry values (1089,1007,0,'Irrigation',1408);
insert into YukonListEntry values (1101,1008,0,'General',0);
insert into YukonListEntry values (1102,1008,0,'Credit',0);
insert into YukonListEntry values (1111,1009,0,'Service Call',0);
insert into YukonListEntry values (1112,1009,0,'Install',0);
insert into YukonListEntry values (1121,1010,0,'Pending',1501);
insert into YukonListEntry values (1122,1010,0,'Scheduled',1502);
insert into YukonListEntry values (1123,1010,0,'Completed',1503);
insert into YukonListEntry values (1124,1010,0,'Cancelled',1504);
insert into YukonListEntry values (1131,1011,0,'Acct #',1601);
insert into YukonListEntry values (1132,1011,0,'Phone #',1602);
insert into YukonListEntry values (1133,1011,0,'Last name',1603);
insert into YukonListEntry values (1134,1011,0,'Serial #',1604);
insert into YukonListEntry values (1135,1011,0,'Map #',1605);
insert into YukonListEntry values (1141,1012,0,'(Unknown)',1801);
insert into YukonListEntry values (1142,1012,0,'Century',0);
insert into YukonListEntry values (1143,1012,0,'Universal',0);
insert into YukonListEntry values (1151,1013,0,'(Unknown)',1901);
insert into YukonListEntry values (1152,1013,0,'Basement',0);
insert into YukonListEntry values (1153,1013,0,'North Side',0);
insert into YukonListEntry values (1161,1014,0,'Likely',0);
insert into YukonListEntry values (1162,1014,0,'Unlikely',0);
insert into YukonListEntry values (1171,1015,0,'Weekday',2101);
insert into YukonListEntry values (1172,1015,0,'Weekend',2102);
insert into YukonListEntry values (1173,1015,0,'Saturday',2103);
insert into YukonListEntry values (1174,1015,0,'Sunday',2104);
insert into YukonListEntry values (1175,1015,0,'Monday',2105);
insert into YukonListEntry values (1176,1015,0,'Tuesday',2106);
insert into YukonListEntry values (1177,1015,0,'Wednesday',2107);
insert into YukonListEntry values (1178,1015,0,'Thursday',2108);
insert into YukonListEntry values (1179,1015,0,'Friday',2109);
insert into YukonListEntry values (1191,1016,0,'Signup',2201);
insert into YukonListEntry values (1192,1016,0,'Exit',2202);
insert into YukonListEntry values (1201,1017,0,'Selection',2301);
insert into YukonListEntry values (1202,1017,0,'Free Form',2302);
insert into YukonListEntry values (1211,1018,0,'(Default)',2401);
insert into YukonListEntry values (1212,1018,0,'Cool',2402);
insert into YukonListEntry values (1213,1018,0,'Heat',2403);
insert into YukonListEntry values (1214,1018,0,'Off',2404);
insert into YukonListEntry values (1215,1018,0,'Auto',2405);
insert into YukonListEntry values (1216,1018,0,'Emergency Heat',2406);
insert into YukonListEntry values (1221,1019,0,'(Default)',2501);
insert into YukonListEntry values (1222,1019,0,'Auto',2502);
insert into YukonListEntry values (1223,1019,0,'On',2503);
insert into YukonListEntry values (1231,1020,1,'PROGRAMS',0);
insert into YukonListEntry values (1232,1020,2,'THERMOSTAT CONTROL',0);
insert into YukonListEntry values (1233,1020,3,'SAVINGS',0);
insert into YukonListEntry values (1241,1049,1,'Tomorrow',2601);
insert into YukonListEntry values (1242,1049,2,'Today',2602);
insert into YukonListEntry values (1243,1049,99,'Repeat Last',2699);

insert into YukonListEntry values (1251,1050,0,'Last Updated Time',3201);
insert into YukonListEntry values (1252,1050,0,'Setpoint',3202);
insert into YukonListEntry values (1253,1050,0,'Fan',3203);
insert into YukonListEntry values (1254,1050,0,'System',3204);
insert into YukonListEntry values (1255,1050,0,'Room,Unit',3205);
insert into YukonListEntry values (1256,1050,0,'Outdoor',3234);
insert into YukonListEntry values (1257,1050,0,'Filter Remaining,Filter Restart',3236);
insert into YukonListEntry values (1258,1050,0,'Lower CoolSetpoint Limit,Upper HeatSetpoint Limit',3237);
insert into YukonListEntry values (1259,1050,0,'Information String',3299);
insert into YukonListEntry values (1260,1050,0,'Cool Runtime,Heat Runtime',3238);
insert into YukonListEntry values (1261,1050,0,'Battery',3239);

insert into YukonListEntry values (1301,1051,0,'Serial #',2701);
insert into YukonListEntry values (1302,1051,0,'Acct #',2702);
insert into YukonListEntry values (1303,1051,0,'Phone #',2703);
insert into YukonListEntry values (1304,1051,0,'Last name',2704);
insert into YukonListEntry values (1305,1051,0,'Order #',2705);
insert into YukonListEntry values (1311,1052,0,'Serial #',2801);
insert into YukonListEntry values (1312,1052,0,'Install date',2802);
insert into YukonListEntry values (1321,1053,0,'Device type',2901);
insert into YukonListEntry values (1322,1053,0,'Service company',2902);
insert into YukonListEntry values (1323,1053,0,'Location',2903);
insert into YukonListEntry values (1324,1053,0,'Configuration',2904);
insert into YukonListEntry values (1325,1053,0,'Device status',2905);
insert into YukonListEntry values (1331,1054,0,'Order #',3301);
insert into YukonListEntry values (1332,1054,0,'Acct #',3302);
insert into YukonListEntry values (1333,1054,0,'Phone #',3303);
insert into YukonListEntry values (1334,1054,0,'Last Name',3304);
insert into YukonListEntry values (1335,1054,0,'Serial #',3305);
insert into YukonListEntry values (1341,1055,0,'Order #',3401);
insert into YukonListEntry values (1342,1055,0,'Date/Time',3402);
insert into YukonListEntry values (1352,1056,0,'Service Type',3502);
insert into YukonListEntry values (1353,1056,0,'Service Company',3503);

insert into YukonListEntry values (1400,1032,0,' ',0);
insert into YukonListEntry values (1401,1032,0,'2',0);
insert into YukonListEntry values (1402,1032,0,'2.5',0);
insert into YukonListEntry values (1403,1032,0,'3',0);
insert into YukonListEntry values (1404,1032,0,'3.5',0);
insert into YukonListEntry values (1405,1032,0,'4',0);
insert into YukonListEntry values (1406,1032,0,'4.5',0);
insert into YukonListEntry values (1407,1032,0,'5',0);
insert into YukonListEntry values (1410,1033,0,' ',0);
insert into YukonListEntry values (1411,1033,0,'Central Air',0);
insert into YukonListEntry values (1420,1034,0,' ',0);
insert into YukonListEntry values (1421,1034,0,'30',0);
insert into YukonListEntry values (1422,1034,0,'40',0);
insert into YukonListEntry values (1423,1034,0,'52',0);
insert into YukonListEntry values (1424,1034,0,'80',0);
insert into YukonListEntry values (1425,1034,0,'120',0);
insert into YukonListEntry values (1430,1035,0,' ',0);
insert into YukonListEntry values (1431,1035,0,'Propane',0);
insert into YukonListEntry values (1432,1035,0,'Electric',0);
insert into YukonListEntry values (1433,1035,0,'Natural Gas',0);
insert into YukonListEntry values (1434,1035,0,'Oil',0);
insert into YukonListEntry values (1440,1036,0,' ',0);
insert into YukonListEntry values (1441,1036,0,'Automatic',0);
insert into YukonListEntry values (1442,1036,0,'Manual',0);
insert into YukonListEntry values (1450,1037,0,' ',0);
insert into YukonListEntry values (1451,1037,0,'Propane',0);
insert into YukonListEntry values (1452,1037,0,'Electric',0);
insert into YukonListEntry values (1453,1037,0,'Natural Gas',0);
insert into YukonListEntry values (1454,1037,0,'Oil',0);
insert into YukonListEntry values (1460,1038,0,' ',0);
insert into YukonListEntry values (1461,1038,0,'Batch Dry',0);
insert into YukonListEntry values (1462,1038,0,'Aerate',0);
insert into YukonListEntry values (1463,1038,0,'Low Temp Air',0);
insert into YukonListEntry values (1470,1039,0,' ',0);
insert into YukonListEntry values (1471,1039,0,'0-2500 Bushels',0);
insert into YukonListEntry values (1472,1039,0,'2500-5000 Bushels',0);
insert into YukonListEntry values (1473,1039,0,'5000-7500 Bushels',0);
insert into YukonListEntry values (1474,1039,0,'7500-10000 Bushels',0);
insert into YukonListEntry values (1475,1039,0,'10000-12500 Bushels',0);
insert into YukonListEntry values (1476,1039,0,'12500-15000 Bushels',0);
insert into YukonListEntry values (1477,1039,0,'15000-17500 Bushels',0);
insert into YukonListEntry values (1478,1039,0,'17500-20000 Bushels',0);
insert into YukonListEntry values (1479,1039,0,'20000-25000 Bushels',0);
insert into YukonListEntry values (1480,1039,0,'25000-30000 Bushels',0);
insert into YukonListEntry values (1481,1039,0,'30000+ Bushels',0);
insert into YukonListEntry values (1490,1040,0,' ',0);
insert into YukonListEntry values (1491,1040,0,'Electric',0);
insert into YukonListEntry values (1500,1041,0,' ',0);
insert into YukonListEntry values (1501,1041,0,'5',0);
insert into YukonListEntry values (1502,1041,0,'10',0);
insert into YukonListEntry values (1503,1041,0,'15',0);
insert into YukonListEntry values (1504,1041,0,'20',0);
insert into YukonListEntry values (1505,1041,0,'25',0);
insert into YukonListEntry values (1506,1041,0,'30',0);
insert into YukonListEntry values (1507,1041,0,'35',0);
insert into YukonListEntry values (1508,1041,0,'40',0);
insert into YukonListEntry values (1509,1041,0,'45',0);
insert into YukonListEntry values (1510,1041,0,'50',0);
insert into YukonListEntry values (1511,1041,0,'60',0);
insert into YukonListEntry values (1512,1041,0,'70',0);
insert into YukonListEntry values (1513,1041,0,'80',0);
insert into YukonListEntry values (1514,1041,0,'100',0);
insert into YukonListEntry values (1515,1041,0,'150',0);
insert into YukonListEntry values (1520,1042,0,' ',0);
insert into YukonListEntry values (1521,1042,0,'Propane',0);
insert into YukonListEntry values (1522,1042,0,'Electric',0);
insert into YukonListEntry values (1523,1042,0,'Natural Gas',0);
insert into YukonListEntry values (1530,1043,0,' ',0);
insert into YukonListEntry values (1531,1043,0,'Ceramic Brick',0);
insert into YukonListEntry values (1532,1043,0,'Concrete Slab',0);
insert into YukonListEntry values (1533,1043,0,'Water',0);
insert into YukonListEntry values (1534,1043,0,'Phase-Change Comp',0);
insert into YukonListEntry values (1535,1043,0,'Pond',0);
insert into YukonListEntry values (1536,1043,0,'Rock Box',0);
insert into YukonListEntry values (1540,1044,0,' ',0);
insert into YukonListEntry values (1541,1044,0,'Air Source',0);
insert into YukonListEntry values (1542,1044,0,'Water Heater Closed',0);
insert into YukonListEntry values (1543,1044,0,'Water Heater Open',0);
insert into YukonListEntry values (1544,1044,0,'Water Heater Direct',0);
insert into YukonListEntry values (1545,1044,0,'Dual-Fuel (Gas)',0);
insert into YukonListEntry values (1550,1045,0,' ',0);
insert into YukonListEntry values (1551,1045,0,'Propane',0);
insert into YukonListEntry values (1552,1045,0,'Electric',0);
insert into YukonListEntry values (1553,1045,0,'Natural Gas',0);
insert into YukonListEntry values (1554,1045,0,'Oil',0);
insert into YukonListEntry values (1560,1046,0,' ',0);
insert into YukonListEntry values (1561,1046,0,'Pivot',0);
insert into YukonListEntry values (1562,1046,0,'Gated Pipe',0);
insert into YukonListEntry values (1563,1046,0,'Pivot/Power Only',0);
insert into YukonListEntry values (1564,1046,0,'Reuse Pit Only',0);
insert into YukonListEntry values (1565,1046,0,'Ditch W/Syphon Tube',0);
insert into YukonListEntry values (1566,1046,0,'Flood',0);
insert into YukonListEntry values (1570,1047,0,' ',0);
insert into YukonListEntry values (1571,1047,0,'Heavy Loam',0);
insert into YukonListEntry values (1572,1047,0,'Loam',0);
insert into YukonListEntry values (1573,1047,0,'Medium',0);
insert into YukonListEntry values (1574,1047,0,'Sandy',0);
insert into YukonListEntry values (1580,1059,0,' ',0);
insert into YukonListEntry values (1581,1059,0,'5',0);
insert into YukonListEntry values (1582,1059,0,'10',0);
insert into YukonListEntry values (1583,1059,0,'15',0);
insert into YukonListEntry values (1584,1059,0,'20',0);
insert into YukonListEntry values (1585,1059,0,'25',0);
insert into YukonListEntry values (1586,1059,0,'30',0);
insert into YukonListEntry values (1587,1059,0,'35',0);
insert into YukonListEntry values (1588,1059,0,'40',0);
insert into YukonListEntry values (1589,1059,0,'45',0);
insert into YukonListEntry values (1590,1059,0,'50',0);
insert into YukonListEntry values (1591,1059,0,'60',0);
insert into YukonListEntry values (1592,1059,0,'70',0);
insert into YukonListEntry values (1593,1059,0,'80',0);
insert into YukonListEntry values (1594,1059,0,'100',0);
insert into YukonListEntry values (1595,1059,0,'150',0);
insert into YukonListEntry values (1596,1059,0,'200',0);
insert into YukonListEntry values (1597,1059,0,'250',0);
insert into YukonListEntry values (1598,1059,0,'300',0);
insert into YukonListEntry values (1600,1060,0,' ',0);
insert into YukonListEntry values (1601,1060,0,'Propane',0);
insert into YukonListEntry values (1602,1060,0,'Electric',0);
insert into YukonListEntry values (1603,1060,0,'Natural Gas',0);
insert into YukonListEntry values (1604,1060,0,'Oil',0);
insert into YukonListEntry values (1610,1061,0,' ',0);
insert into YukonListEntry values (1611,1061,0,'At Road',0);
insert into YukonListEntry values (1612,1061,0,'At Pump',0);
insert into YukonListEntry values (1613,1061,0,'At Pivot',0);
insert into YukonListEntry values (1620,1062,0,' ',0);
insert into YukonListEntry values (1621,1062,0,'120 (PT)',0);
insert into YukonListEntry values (1622,1062,0,'240',0);
insert into YukonListEntry values (1623,1062,0,'277/480',0);
insert into YukonListEntry values (1624,1062,0,'480',0);
insert into YukonListEntry values (1630,1063,0,' ',0);
insert into YukonListEntry values (1631,1063,0,'Basement',0);
insert into YukonListEntry values (1632,1063,0,'Crawl Space',0);
insert into YukonListEntry values (1633,1063,0,'Main Floor Closet',0);
insert into YukonListEntry values (1634,1063,0,'Second Floor Closet',0);
insert into YukonListEntry values (1635,1063,0,'Under Counter',0);
insert into YukonListEntry values (1636,1063,0,'Attic',0);
insert into YukonListEntry values (1640,1064,0,' ',0);
insert into YukonListEntry values (1641,1064,0,'2',0);
insert into YukonListEntry values (1642,1064,0,'2.5',0);
insert into YukonListEntry values (1643,1064,0,'3',0);
insert into YukonListEntry values (1644,1064,0,'3.5',0);
insert into YukonListEntry values (1645,1064,0,'4',0);
insert into YukonListEntry values (1646,1064,0,'4.5',0);
insert into YukonListEntry values (1647,1064,0,'5',0);

insert into YukonListEntry values (1700,1021,0,' ',0);
insert into YukonListEntry values (1701,1021,0,'One Story Unfinished Basement',0);
insert into YukonListEntry values (1702,1021,0,'One Story Finished Basement',0);
insert into YukonListEntry values (1703,1021,0,'Two Story',0);
insert into YukonListEntry values (1704,1021,0,'Manufactured Home',0);
insert into YukonListEntry values (1705,1021,0,'Apartment',0);
insert into YukonListEntry values (1706,1021,0,'Duplex',0);
insert into YukonListEntry values (1707,1021,0,'Townhome',0);
insert into YukonListEntry values (1710,1022,0,' ',0);
insert into YukonListEntry values (1711,1022,0,'Frame',0);
insert into YukonListEntry values (1712,1022,0,'Brick',0);
insert into YukonListEntry values (1720,1023,0,' ',0);
insert into YukonListEntry values (1721,1023,0,'pre-1900',0);
insert into YukonListEntry values (1722,1023,0,'1910',0);
insert into YukonListEntry values (1723,1023,0,'1920',0);
insert into YukonListEntry values (1724,1023,0,'1930',0);
insert into YukonListEntry values (1725,1023,0,'1940',0);
insert into YukonListEntry values (1726,1023,0,'1950',0);
insert into YukonListEntry values (1727,1023,0,'1960',0);
insert into YukonListEntry values (1728,1023,0,'1970',0);
insert into YukonListEntry values (1729,1023,0,'1980',0);
insert into YukonListEntry values (1730,1023,0,'1990',0);
insert into YukonListEntry values (1731,1023,0,'2000',0);
insert into YukonListEntry values (1740,1024,0,' ',0);
insert into YukonListEntry values (1741,1024,0,'Less Than 1000',0);
insert into YukonListEntry values (1742,1024,0,'1000-1499',0);
insert into YukonListEntry values (1743,1024,0,'1500-1999',0);
insert into YukonListEntry values (1744,1024,0,'2000-2499',0);
insert into YukonListEntry values (1745,1024,0,'2500-2999',0);
insert into YukonListEntry values (1746,1024,0,'3000-3499',0);
insert into YukonListEntry values (1747,1024,0,'3500-3999',0);
insert into YukonListEntry values (1748,1024,0,'4000+',0);
insert into YukonListEntry values (1751,1025,0,'Unknown',0);
insert into YukonListEntry values (1752,1025,0,'Poor (0-3)"',0);
insert into YukonListEntry values (1753,1025,0,'Fair (3-5)"',0);
insert into YukonListEntry values (1754,1025,0,'Average (6-8)"',0);
insert into YukonListEntry values (1755,1025,0,'Good (9-11)"',0);
insert into YukonListEntry values (1756,1025,0,'Excellant (12+)"',0);
insert into YukonListEntry values (1760,1026,0,' ',0);
insert into YukonListEntry values (1761,1026,0,'Poor',0);
insert into YukonListEntry values (1762,1026,0,'Fair',0);
insert into YukonListEntry values (1763,1026,0,'Good',0);
insert into YukonListEntry values (1764,1026,0,'Excellent',0);
insert into YukonListEntry values (1770,1027,0,' ',0);
insert into YukonListEntry values (1771,1027,0,'None',0);
insert into YukonListEntry values (1772,1027,0,'Central Air',0);
insert into YukonListEntry values (1773,1027,0,'GSHP',0);
insert into YukonListEntry values (1774,1027,0,'ASHP',0);
insert into YukonListEntry values (1775,1027,0,'Window Unit',0);
insert into YukonListEntry values (1776,1027,0,'Gas ASHP',0);
insert into YukonListEntry values (1777,1027,0,'Attic Fan',0);
insert into YukonListEntry values (1780,1028,0,' ',0);
insert into YukonListEntry values (1781,1028,0,'Electric Forced Air',0);
insert into YukonListEntry values (1782,1028,0,'GSHP',0);
insert into YukonListEntry values (1783,1028,0,'ASHP',0);
insert into YukonListEntry values (1784,1028,0,'Electric Baseboard',0);
insert into YukonListEntry values (1785,1028,0,'Ceiling Fan',0);
insert into YukonListEntry values (1786,1028,0,'ETS',0);
insert into YukonListEntry values (1787,1028,0,'Gas Forced Air',0);
insert into YukonListEntry values (1788,1028,0,'Gas Wall Unit',0);
insert into YukonListEntry values (1790,1029,0,' ',0);
insert into YukonListEntry values (1791,1029,0,'1 - 2',0);
insert into YukonListEntry values (1792,1029,0,'3 - 4',0);
insert into YukonListEntry values (1793,1029,0,'5 - 6',0);
insert into YukonListEntry values (1794,1029,0,'7 - 8',0);
insert into YukonListEntry values (1795,1029,0,'9+',0);
insert into YukonListEntry values (1800,1030,0,' ',0);
insert into YukonListEntry values (1801,1030,0,'Own',0);
insert into YukonListEntry values (1802,1030,0,'Rent',0);
insert into YukonListEntry values (1810,1031,0,' ',0);
insert into YukonListEntry values (1811,1031,0,'Propane',0);
insert into YukonListEntry values (1812,1031,0,'Electric',0);
insert into YukonListEntry values (1813,1031,0,'Natural Gas',0);
insert into YukonListEntry values (1814,1031,0,'Oil',0);

insert into YukonListEntry values (2000,0,0,'Customer List Entry Base',0);

alter table YukonListEntry
   add constraint PK_YUKONLISTENTRY primary key  (EntryID)
go


/*==============================================================*/
/* Index: Indx_YkLstDefID                                       */
/*==============================================================*/
create   index Indx_YkLstDefID on YukonListEntry (
YukonDefinitionID
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
PAOStatistics        varchar(10)          not null
)
go


INSERT into YukonPAObject values (0, 'DEVICE', 'System', 'System Device', 'System', 'Reserved System Device', 'N', '-----');

alter table YukonPAObject
   add constraint PK_YUKONPAOBJECT primary key  (PAObjectID)
go


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
/* Table : YukonRole                                            */
/*==============================================================*/
create table YukonRole (
RoleID               numeric              not null,
RoleName             varchar(120)         not null,
Category             varchar(60)          not null,
RoleDescription      varchar(200)         not null
)
go


/* Default role for all users - yukon category */
insert into YukonRole values(-1,'Yukon','Yukon','Default Yukon role. Edit this role from the Yukon SetUp page.');
insert into YukonRole values(-3,'Logging','Yukon','Settings for how Yukon logs output. Edit this role from the Yukon SetUp page.');
insert into YukonRole values(-4,'Radius Login','Yukon','Settings for using RADIUS server to login instead of standard yukon login.');
insert into YukonRole values(-104,'Calc Historical','Yukon','Calc Historical. Edit this role from the Yukon SetUp page.');
insert into YukonRole values(-105,'Web Graph','Yukon','Web Graph. Edit this role from the Yukon SetUp page.');
insert into YukonRole values(-106,'Billing','Yukon','Billing. Edit this role from the Yukon SetUp page.');

/* Application specific roles */
insert into YukonRole values(-100,'Database Editor','Application','Access to the Yukon Database Editor application');
insert into YukonRole values(-101,'Tabular Display Console','Application','Access to the Yukon Tabular Display Console application');
insert into YukonRole values(-102,'Trending','Application','Access to the Yukon Trending application');
insert into YukonRole values(-103,'Commander','Application','Access to the Yukon Commander application');

insert into YukonRole values(-107,'Esubstation Editor','Application','Access to the Esubstation Drawing Editor application');
insert into YukonRole values(-108,'Web Client','Application','Access to the Yukon web application');

/* Web client operator roles */
insert into YukonRole values(-200,'Administrator','Operator','Access to Yukon administration');
insert into YukonRole values(-201,'Consumer Info','Operator','Operator access to consumer account information');
insert into YukonRole values(-202,'Commercial Metering','Operator','Operator access to commerical metering');
insert into YukonRole values(-203,'Direct Loadcontrol','Operator','Operator  access to direct loadcontrol');
insert into YukonRole values(-204,'Direct Curtailment','Operator','Operator access to direct curtailment');
insert into YukonRole values(-205,'Energy Buyback','Operator','Operator access to energy buyback');

/* Operator roles */
insert into YukonRole values(-206,'Esubstation Drawings','Operator','Operator access to esubstation drawings');
insert into YukonRole values(-207,'Odds For Control','Operator','Operator access to odds for control');
insert into YukonRole values(-2,'Energy Company','Operator','Energy company role');

/* Inventory Role */
insert into YukonRole values (-209,'Inventory','Operator','Operator Access to hardware inventory');

/* operator work order management role */
insert into YukonRole values (-210,'Work Order','Operator','Operator Access to work order management');

/* CI customer roles */
insert into YukonRole values(-300,'Direct Loadcontrol','CICustomer','Customer access to commercial/industrial customer direct loadcontrol');
insert into YukonRole values(-301,'Curtailment','CICustomer','Customer access to commercial/industrial customer direct curtailment');
insert into YukonRole values(-302,'Energy Buyback','CICustomer','Customer access to commercial/industrial customer energy buyback');
insert into YukonRole values(-304,'Commercial Metering','CICustomer','Customer access to commercial metering');
insert into YukonRole values(-305,'Administrator','CICustomer','Administrator privilages.');
insert into YukonRole values(-306, 'User Control', 'CICustomer', 'Customer access to user control operations.');


/* Consumer roles */
insert into YukonRole values(-400,'Residential Customer','Consumer','Access to residential customer information');

alter table YukonRole
   add constraint PK_YUKONROLE primary key  (RoleID)
go


/*==============================================================*/
/* Index: Indx_YukRol_Nm                                        */
/*==============================================================*/
create   index Indx_YukRol_Nm on YukonRole (
RoleName
)
go


/*==============================================================*/
/* Table : YukonRoleProperty                                    */
/*==============================================================*/
create table YukonRoleProperty (
RolePropertyID       numeric              not null,
RoleID               numeric              not null,
KeyName              varchar(100)         not null,
DefaultValue         varchar(1000)        not null,
Description          varchar(1000)        not null
)
go


/* Yukon Role */
insert into YukonRoleProperty values(-1000,-1,'dispatch_machine','127.0.0.1','Name or IP address of the Yukon Dispatch Service');
insert into YukonRoleProperty values(-1001,-1,'dispatch_port','1510','TCP/IP port of the Yukon Dispatch Service');
insert into YukonRoleProperty values(-1002,-1,'porter_machine','127.0.0.1','Name or IP address of the Yukon Port Control Service');
insert into YukonRoleProperty values(-1003,-1,'porter_port','1540','TCP/IP port of the Yukon Port Control Service');
insert into YukonRoleProperty values(-1004,-1,'macs_machine','127.0.0.1','Name or IP address of the Yukon Metering and Control Scheduler Service');
insert into YukonRoleProperty values(-1005,-1,'macs_port','1900','TCP/IP port of the Yukon Metering and Control Scheduler Service');
insert into YukonRoleProperty values(-1006,-1,'cap_control_machine','127.0.0.1','Name or IP address of the Yukon Capacitor Control Service');
insert into YukonRoleProperty values(-1007,-1,'cap_control_port','1910','TCP/IP port of the Yukon Capacitor Control Service');
insert into YukonRoleProperty values(-1008,-1,'loadcontrol_machine','127.0.0.1','Name or IP Address of the Yukon Load Management Service');
insert into YukonRoleProperty values(-1009,-1,'loadcontrol_port','1920','TCP/IP port of the Yukon Load Management Service');
insert into YukonRoleProperty values(-1010,-1,'smtp_host','127.0.0.1','Name or IP address of the mail server');
insert into YukonRoleProperty values(-1011,-1,'mail_from_address','yukon@cannontech.com','Name of the FROM email address the mail server will use');
insert into YukonRoleProperty values(-1012,-1,'print_insert_sql','(none)','File name of where to print all SQL insert statements');
insert into YukonRoleProperty values(-1013,-1,'stars_soap_server','(none)','Where the soap server is running, the default value is the local host');
insert into YukonRoleProperty values(-1014,-1,'web_logo','CannonLogo.gif','The logo that is used for the yukon web applications');

insert into YukonRoleProperty values(-1300,-4,'radius_server_address','(none)','Radius server machine address');
insert into YukonRoleProperty values(-1301,-4,'radius_auth_port','1812','Radius authentication port.');
insert into YukonRoleProperty values(-1302,-4,'radius_acct_port','1813','Radius accounting port.');
insert into YukonRoleProperty values(-1303,-4,'radius_secret_key','(none)','Radius clients secret key value, defined by the radius server.');
insert into YukonRoleProperty values(-1304,-4,'radius_auth_method','PAP','Radius authentication method. Possible values are PAP, [chap, others to follow soon]');

/* Database Editor Role */
insert into YukonRoleProperty values(-10000,-100,'point_id_edit','false','Controls whether point ids can be edited');
insert into YukonRoleProperty values(-10001,-100,'dbeditor_core','true','Controls whether the Core menu item in the View menu is displayed');
insert into YukonRoleProperty values(-10002,-100,'dbeditor_lm','true','Controls whether the Loadmanagement menu item in the View menu is displayed');
insert into YukonRoleProperty values(-10003,-100,'dbeditor_cap_control','true','Controls whether the Cap Control menu item in the View menu is displayed');
insert into YukonRoleProperty values(-10004,-100,'dbeditor_system','true','Controls whether the System menu item in the View menu is displayed');
insert into YukonRoleProperty values(-10005,-100,'utility_id_range','1-254','<description>');
insert into YukonRoleProperty values(-10007,-100,'dbeditor_trans_exclusion','false','Allows the editor panel for the mutual exclusion of transmissions to be shown');
insert into YukonRoleProperty values(-10008,-100,'permit_login_edit','true','Closes off all access to logins and login groups for non-administrators in the dbeditor');
insert into YukonRoleProperty values(-10009,-100,'allow_user_roles','false','Allows the editor panel individual user roles to be shown');


/* Energy Company Role Properties */
insert into YukonRoleProperty values(-1100,-2,'admin_email_address','info@cannontech.com','(Deprecated) Use this value as the admin email address only if the energy company''s primary contact doesn''t have an email address.');
insert into YukonRoleProperty values(-1101,-2,'optout_notification_recipients','override@cannontech.com','Recipients of the opt out notification email');
insert into YukonRoleProperty values(-1102,-2,'default_time_zone','CST','Default time zone of the energy company');
insert into YukonRoleProperty values(-1103,-2,'switch_command_file','c:/yukon/switch_command/default_switch.txt','Location of the file to temporarily store the switch commands');
insert into YukonRoleProperty values(-1104,-2,'optout_command_file','c:/yukon/switch_command/default_optout.txt','Location of the file to temporarily store the opt out commands');
insert into YukonRoleProperty values(-1105,-2,'customer_group_ids','-300','Group IDs of all the residential customer logins');
insert into YukonRoleProperty values(-1106,-2,'operator_group_ids','-301','Group IDs of all the web client operator logins');

/* Yukon Logging Role Properties */
insert into YukonRoleProperty values(-1200,-3,'dbeditor_log_level','INFO','Logging level for DBEditor functionality. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-1201,-3,'database_log_level','INFO','Logging level for the Database. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-1202,-3,'tdc_log_level','INFO','Logging level for TDC functionality. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-1203,-3,'commander_log_level','INFO','Logging level for Yukon Commander functionality. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-1204,-3,'billing_log_level','INFO','Logging level for Billing functionality. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-1205,-3,'calchist_log_level','INFO','Logging level for Calc Historical functionality. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-1206,-3,'cap_control_log_level','INFO','Logging level for Cap Control functionality. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-1207,-3,'esub_log_level','INFO','Logging level for Esubstation functionality. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-1208,-3,'export_log_level','INFO','Logging level for Export functionality. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-1209,-3,'load_control_log_level','INFO','Logging level for Load Control functionality. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-1210,-3,'macs_log_level','INFO','Logging level for MACS functionality. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-1211,-3,'notification_log_level','INFO','Logging level for Notification functionality. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-1212,-3,'reporting_log_level','INFO','Logging level for Reporting functionality. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-1213,-3,'trending_log_level','INFO','Logging level for Trending functionality. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-1214,-3,'stars_log_level','INFO','Logging level for STARS functionality. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-1215,-3,'general_log_level','INFO','Logging level for all functionality that is not otherwise defined. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-1216,-3,'log_to_file','false','Tells all logging that it needs to go to a file');



/* TDC Role */
insert into YukonRoleProperty values(-10100,-101,'loadcontrol_edit','00000000','<description>');
insert into YukonRoleProperty values(-10101,-101,'macs_edit','00000CCC','<description>');
insert into YukonRoleProperty values(-10102,-101,'tdc_express','ludicrous_speed','<description>');
insert into YukonRoleProperty values(-10103,-101,'tdc_max_rows','500','<description>');
insert into YukonRoleProperty values(-10104,-101,'tdc_rights','00000000','<description>');
insert into YukonRoleProperty values(-10105,-101,'CAP_CONTROL_INTERFACE','amfm','<description>');
insert into YukonRoleProperty values(-10106,-101,'cbc_creation_name','CBC %PAOName%','<description>');
insert into YukonRoleProperty values(-10107,-101,'tdc_alarm_count','3','<description>');
insert into YukonRoleProperty values(-10108,-101,'decimal_places','2','<description>');
insert into YukonRoleProperty values(-10109,-101,'pfactor_decimal_places','1','<description>');
insert into YukonRoleProperty values(-10111,-101,'lc_reduction_col','true','Tells TDC to show the LoadControl reduction column or not');


/* Trending Role */
insert into YukonRoleProperty values(-10200,-102,'graph_edit_graphdefinition','true','<description>');

/* Commander Role Properties */ 
insert into YukonRoleProperty values(-10300,-103,'msg_priority','14','Tells commander what the outbound priority of messages are (low)1 - 14(high)');
insert into YukonRoleProperty values(-10301,-103,'Versacom Serial','true','Show a Versacom Serial Number SortBy display');
insert into YukonRoleProperty values(-10302,-103,'Expresscom Serial','true','Show an Expresscom Serial Number SortBy display');
insert into YukonRoleProperty values(-10303,-103,'DCU SA203 Serial','false','Show a DCU SA205 Serial Number SortBy display');
insert into YukonRoleProperty values(-10304,-103,'DCU SA305 Serial','false','Show a DCU SA305 Serial Number SortBy display');

/* Calc Historical Role Properties */
insert into YukonRoleProperty values(-10400,-104,'interval','900','<description>');
insert into YukonRoleProperty values(-10401,-104,'baseline_calctime','4','<description>');
insert into YukonRoleProperty values(-10402,-104,'daysprevioustocollect','30','<description>');

/* Web Graph Role Properties */
insert into YukonRoleProperty values(-10500,-105,'home_directory','c:\yukon\client\webgraphs','<description>');
insert into YukonRoleProperty values(-10501,-105,'run_interval','900','<description>');

/* Billing Role Properties */
insert into YukonRoleProperty values(-10600,-106,'wiz_activate','false','<description>');
insert into YukonRoleProperty values(-10601,-106,'input_file','c:\yukon\client\bin\BillingIn.txt','<description>');

/* Esubstation Editor Role Properties */
insert into YukonRoleProperty values(-10700,-107,'default','false','The default esub editor property');

/* Web Client Role Properties */
insert into YukonRoleProperty values(-10800,-108,'home_url','/default.jsp','The url to take the user immediately after logging into the Yukon web applicatoin');
insert into YukonRoleProperty values(-10802,-108,'style_sheet','yukon/CannonStyle.css','The web client cascading style sheet.');
insert into YukonRoleProperty values(-10803,-108,'nav_bullet_selected','yukon/Bullet.gif','The bullet used when an item in the nav is selected.');
insert into YukonRoleProperty values(-10804,-108,'nav_bullet_expand','yukon/BulletExpand.gif','The bullet used when an item in the nav can be expanded to show submenu.');
insert into YukonRoleProperty values(-10805,-108,'header_logo','yukon/DemoHeader.gif','The main header logo');
insert into YukonRoleProperty values(-10806, -108,'log_in_url','/login.jsp','The url where the user login from. It is used as the url to send the users to when they log off.');
insert into YukonRoleProperty values(-10807,-108,'nav_connector_bottom','yukon/BottomConnector.gif','The connector icon in the nav used for showing the hardware tree structure, in front of the last hardware under each category');
insert into YukonRoleProperty values(-10808,-108,'nav_connector_middle','yukon/MidConnector.gif','The connector icon in the nav used for showing the hardware tree structure, in front of every hardware except the last one under each category');

/* Operator Consumer Info Role Properties */
insert into YukonRoleProperty values(-20100,-201,'Not Implemented','false','Controls whether to show the features not implemented yet (not recommended)');
insert into YukonRoleProperty values(-20101,-201,'Account General','true','Controls whether to show the general account information');
insert into YukonRoleProperty values(-20102,-201,'Account Residence','false','Controls whether to show the customer residence information');
insert into YukonRoleProperty values(-20103,-201,'Account Call Tracking','false','Controls whether to enable the call tracking feature');
insert into YukonRoleProperty values(-20104,-201,'Metering Interval Data','false','Controls whether to show the metering interval data');
insert into YukonRoleProperty values(-20105,-201,'Metering Usage','false','Controls whether to show the metering time of use');
insert into YukonRoleProperty values(-20106,-201,'Programs Control History','true','Controls whether to show the program control history');
insert into YukonRoleProperty values(-20107,-201,'Programs Enrollment','true','Controls whether to enable the program enrollment feature');
insert into YukonRoleProperty values(-20108,-201,'Programs Opt Out','true','Controls whether to enable the program opt out/reenable feature');
insert into YukonRoleProperty values(-20109,-201,'Appliances','true','Controls whether to show the appliance information');
insert into YukonRoleProperty values(-20110,-201,'Appliances Create','true','Controls whether to enable the appliance creation feature');
insert into YukonRoleProperty values(-20111,-201,'Hardwares','true','Controls whether to show the hardware information');
insert into YukonRoleProperty values(-20112,-201,'Hardwares Create','true','Controls whether to enable the hardware creation feature');
insert into YukonRoleProperty values(-20113,-201,'Hardwares Thermostat','true','Controls whether to enable the thermostat programming feature');
insert into YukonRoleProperty values(-20114,-201,'Work Orders','false','Controls whether to enable the service request feature');
insert into YukonRoleProperty values(-20115,-201,'Admin Change Login','true','Controls whether to enable the changing customer login feature');
insert into YukonRoleProperty values(-20116,-201,'Admin FAQ','false','Controls whether to show customer FAQs');
insert into YukonRoleProperty values(-20117,-201,'Thermostats All','false','Controls whether to allow programming multiple thermostats at one time');
insert into YukonRoleProperty values(-20118,-201,'Create Trend','false','Controls whether to allow new trends to assigned to the customer');

/* Operator Consumer Info Role Properties */
insert into YukonRoleProperty values(-20150,-201,'Super Operator','false','Used for some testing functions (not recommended)');
insert into YukonRoleProperty values(-20151,-201,'New Account Wizard','true','Controls whether to enable the new account wizard');
insert into YukonRoleProperty values(-20152,-201,'Import Customer Account','(none)','Controls whether to enable the customer account importing feature');
insert into YukonRoleProperty values(-20153,-201,'Inventory Checking','true','Controls when to perform inventory checking while creating or updating hardware information');
insert into YukonRoleProperty values(-20154,-201,'Automatic Configuration','false','Controls whether to automatically send out config command when creating hardware or changing program enrollment');
insert into YukonRoleProperty values(-20155,-201,'Order Number Auto Generation','false','Controls whether the order number is automatically generated or entered by user');
insert into YukonRoleProperty values(-20156,-201,'Call Number Auto Generation','false','Controls whether the call number is automatically generated or entered by user');
insert into YukonRoleProperty values(-20157,-201,'Opt Out Rules','(none)','Defines the rules for opting out.');

/* Operator Administrator Role Properties */
insert into YukonRoleProperty values(-20000,-200,'Config Energy Company','false','Controls whether to allow configuring the energy company');
insert into YukonRoleProperty values(-20001,-200,'Create Energy Company','false','Controls whether to allow creating a new energy company');
insert into YukonRoleProperty values(-20002,-200,'Delete Energy Company','false','Controls whether to allow deleting the energy company');
insert into YukonRoleProperty values(-20003,-200,'Manage Members','false','Controls whether to allow managing the energy company''s members');

/* Operator Commercial Metering Role Properties*/
insert into YukonRoleProperty values(-20200,-202,'Trending Disclaimer',' ','The disclaimer that appears with trends');

/* Operator Direct Loadcontrol Role Properties */
insert into YukonRoleProperty values(-20300,-203,'Direct Loadcontrol Label','Direct Control','The operator specific name for direct loadcontrol');
insert into YukonRoleProperty values(-20301,-203,'Individual Switch','true','Controls access to operator individual switch control');

/* Operator Direct Curtailment Role Properties */
insert into YukonRoleProperty values(-20400,-204,'Direct Curtailment Label','Notification','The operator specific name for direct curtailment');

/* Operator Energy Exchange Role Properties */
insert into YukonRoleProperty values(-20500,-205,'Energy Buyback Label','Energy Buyback','The operator specific name for Energy Buyback');

/* Operator Esubstation Drawings Role Properties */
insert into YukonRoleProperty values(-20600,-206,'View Drawings','true','Controls viewing of Esubstations drawings');
insert into YukonRoleProperty values(-20601,-206,'Edit Limits','false','Controls editing of point limits');
insert into YukonRoleProperty values(-20602,-206,'Control','false','Controls control from Esubstation drawings');

/* Odds For Control Role Properties */
insert into YukonRoleProperty values(-20700,-207,'Odds For Control Label','Odds for Control','The operator specific name for odds for control');

/* Operator Consumer Info Role Properties Part II */
insert into YukonRoleProperty values(-20800,-201,'Link FAQ','(none)','The customized FAQ link');
insert into YukonRoleProperty values(-20801,-201,'Link Thermostat Instructions','(none)','The customized thermostat instructions link');
insert into YukonRoleProperty values(-20810,-201,'Text Control','control','Term for control');
insert into YukonRoleProperty values(-20813,-201,'Text Opt Out Noun','opt out','Noun form of the term for opt out');
insert into YukonRoleProperty values(-20814,-201,'Text Opt Out Verb','opt out of','Verbical form of the term for opt out');
insert into YukonRoleProperty values(-20815,-201,'Text Opt Out Past','opted out','Past form of the term for opt out');
insert into YukonRoleProperty values(-20816,-201,'Text Reenable','reenable','Term for reenable');
insert into YukonRoleProperty values(-20819,-201,'Text Odds for Control','odds for control','Text for odds for control');
insert into YukonRoleProperty values(-20820,-201,'Text Recommended Settings','Recommended Settings','Text of the Recommended Settings button on the thermostat schedule page');
insert into YukonRoleProperty values(-20830,-201,'Label Programs Control History','Control History','Text of the programs control history link');
insert into YukonRoleProperty values(-20831,-201,'Label Programs Enrollment','Enrollment','Text of the programs enrollment link');
insert into YukonRoleProperty values(-20832,-201,'Label Programs Opt Out','Opt Out','Text of the programs opt out link');
insert into YukonRoleProperty values(-20833,-201,'Label Thermostat Schedule','Schedule','Text of the thermostat schedule link');
insert into YukonRoleProperty values(-20834,-201,'Label Thermostat Manual','Manual','Text of the thermostat manual link');
insert into YukonRoleProperty values(-20850,-201,'Title Programs Control History','PROGRAMS - CONTROL HISTORY','Title of the programs control history page');
insert into YukonRoleProperty values(-20851,-201,'Title Program Control History','PROGRAM - CONTROL HISTORY','Title of the control history page of a particular program');
insert into YukonRoleProperty values(-20852,-201,'Title Program Control Summary','PROGRAM - CONTROL SUMMARY','Title of the control summary page of a particular program');
insert into YukonRoleProperty values(-20853,-201,'Title Programs Enrollment','PROGRAMS - ENROLLMENT','Title of the programs enrollment page');
insert into YukonRoleProperty values(-20854,-201,'Title Programs Opt Out','PROGRAMS - OPT OUT','Title of the programs opt out page');
insert into YukonRoleProperty values(-20855,-201,'Title Thermostat Schedule','THERMOSTAT - SCHEDULE','Title of the thermostat schedule page');
insert into YukonRoleProperty values(-20856,-201,'Title Thermostat Manual','THERMOSTAT - MANUAL','Title of the thermostat manual page');
insert into YukonRoleProperty values(-20870,-201,'Description Opt Out','If you would like to temporarily opt out of all programs, select the time frame from the drop-down box below, then select Submit.','Description on the programs opt out page');

/* Operator Hardware Inventory Role Properties */
insert into YukonRoleProperty values(-20900,-209,'Show All Inventory','true','Controls whether to allow showing all inventory');
insert into YukonRoleProperty values(-20901,-209,'Add SN Range','true','Controls whether to allow adding hardwares by serial number range');
insert into YukonRoleProperty values(-20902,-209,'Update SN Range','true','Controls whether to allow updating hardwares by serial number range');
insert into YukonRoleProperty values(-20903,-209,'Config SN Range','true','Controls whether to allow configuring hardwares by serial number range');
insert into YukonRoleProperty values(-20904,-209,'Delete SN Range','true','Controls whether to allow deleting hardwares by serial number range');

/* operator work order management role properties */
insert into YukonRoleProperty values(-21000,-210,'Show All Work Orders','true','Controls whether to allow showing all work orders');
insert into YukonRoleProperty values(-21001,-210,'Create Work Order','true','Controls whether to allow creating new work orders');

/* CICustomer Direct Loadcontrol Role Properties */
insert into YukonRoleProperty values(-30000,-300,'Direct Loadcontrol Label','Direct Control','The customer specific name for direct loadcontrol');
insert into YukonRoleProperty values(-30001,-300,'Individual Switch','false','Controls access to customer individual switch control');

/* CICustomer Curtailment Role Properties */
insert into YukonRoleProperty values(-30100,-301,'Direct Curtailment Label','Notification','The customer specific name for direct curtailment');
insert into YukonRoleProperty values(-30101,-301,'Direct Curtailment Provider','<curtailment provider>','This customers direct curtailment provider');

/* CICustomer Energy Buyback Role Properties */
insert into YukonRoleProperty values(-30200,-302,'Energy Buyback Label','Energy Buyback','The customer specific name for Energy Buyback');
insert into YukonRoleProperty values(-30201,-302,'Energy Buyback Phone Number','1-800-555-5555','The phone number to call if the customer has questions');

/* CICustomer Commercial Metering Role Properties */
insert into YukonRoleProperty values(-30400,-304,'Trending Disclaimer',' ','The disclaimer that appears with trends');
insert into yukonroleproperty values(-30401, -304, 'Trending Get Data Now Button', 'false', 'Controls access to retrieve meter data on demand');
insert into yukonroleproperty values(-30402, -304, 'Minimum Scan Frequency', '15', 'Minimum duration (in minutes) between get data now events');
insert into yukonroleproperty values(-30403, -304, 'Maximum Daily Scans', '2', 'Maximum number of get data now scans available daily');

/* CICustomer Administrator Role */
insert into yukonroleproperty values(-30500, -305, 'Contact Information Editable', 'false', 'Contact information is editable by the customer');

/* Add the CICustomer user-control properties */
insert into yukonroleproperty values(-30600, -306, 'User Control Label', 'User-Control', 'The customer specific name for user control');
insert into yukonroleproperty values(-30601, -306, 'Auto Control', 'true', 'Controls access to auto control.');
insert into yukonroleproperty values(-30602, -306, 'Time Based Control', 'true', 'Controls access to time based control');
insert into yukonroleproperty values(-30603, -306, 'Switch Command Control', 'true', 'Controls acces to switch commands');

/* Residential Customer Role Properties */
insert into YukonRoleProperty values(-40000,-400,'Not Implemented','false','Controls whether to show the features not implemented yet (not recommended)');
insert into YukonRoleProperty values(-40001,-400,'Account General','true','Controls whether to show the general account information');
insert into YukonRoleProperty values(-40002,-400,'Metering Usage','false','Controls whether to show the metering time of use');
insert into YukonRoleProperty values(-40003,-400,'Programs Control History','true','Controls whether to show the program control history');
insert into YukonRoleProperty values(-40004,-400,'Programs Enrollment','true','Controls whether to enable the program enrollment feature');
insert into YukonRoleProperty values(-40005,-400,'Programs Opt Out','true','Controls whether to enable the program opt out/reenable feature');
insert into YukonRoleProperty values(-40006,-400,'Hardwares Thermostat','true','Controls whether to enable the thermostat programming feature');
insert into YukonRoleProperty values(-40007,-400,'Questions Utility','true','Controls whether to show the contact information of the energy company');
insert into YukonRoleProperty values(-40008,-400,'Questions FAQ','true','Controls whether to show customer FAQs');
insert into YukonRoleProperty values(-40009,-400,'Admin Change Login','true','Controls whether to allow customers to change their own login');
insert into YukonRoleProperty values(-40010,-400,'Thermostats All','false','Controls whether to allow programming multiple thermostats at one time');
insert into YukonRoleProperty values(-40050,-400,'Notification on General Page','false','Controls whether to show the notification email box on the general page (useful only when the programs enrollment feature is not selected)');
insert into YukonRoleProperty values(-40051,-400,'Hide Opt Out Box','false','Controls whether to show the opt out box on the programs opt out page');
insert into YukonRoleProperty values(-40052,-400,'Automatic Configuration','false','Controls whether to automatically send out config command when changing program enrollment');
insert into YukonRoleProperty values(-40054,-400,'Disable Program Signup','false','Controls whether to prevent the customers from enrolling in or out of the programs');
insert into YukonRoleProperty values(-40055,-400,'Opt Out Rules','(none)','Defines the rules for opting out.');

insert into YukonRoleProperty values(-40100,-400,'Link FAQ','(none)','The customized FAQ link');
insert into YukonRoleProperty values(-40101,-400,'Link Utility Email','(none)','(Deprecated) Use the "Description Utility" role property to handle the customized utility email.');
insert into YukonRoleProperty values(-40102,-400,'Link Thermostat Instructions','(none)','The customized thermostat instructions link');
insert into YukonRoleProperty values(-40110,-400,'Text Control','control','Term for control');
insert into YukonRoleProperty values(-40111,-400,'Text Controlled','controlled','Past form of the term for control');
insert into YukonRoleProperty values(-40112,-400,'Text Controlling','controlling','Present form of the term for control');
insert into YukonRoleProperty values(-40113,-400,'Text Opt Out Noun','opt out','Noun form of the term for opt out');
insert into YukonRoleProperty values(-40114,-400,'Text Opt Out Verb','opt out of','Verbical form of the term for opt out');
insert into YukonRoleProperty values(-40115,-400,'Text Opt Out Past','opted out','Past form of the term for opt out');
insert into YukonRoleProperty values(-40116,-400,'Text Odds for Control','odds for control','Text for odds for control');
insert into YukonRoleProperty values(-40117,-400,'Text Recommended Settings','Recommended Settings','Text of the Recommended Settings button on the thermostat schedule page');
insert into YukonRoleProperty values(-40130,-400,'Label Programs Control History','Control History','Text of the programs control history link');
insert into YukonRoleProperty values(-40131,-400,'Label Programs Enrollment','Enrollment','Text of the programs enrollment link');
insert into YukonRoleProperty values(-40132,-400,'Label Programs Opt Out','Opt Out','Text of the programs opt out link');
insert into YukonRoleProperty values(-40133,-400,'Label Thermostat Schedule','Schedule','Text of the thermostat schedule link');
insert into YukonRoleProperty values(-40134,-400,'Label Thermostat Manual','Manual','Text of the thermostat manual link');
insert into YukonRoleProperty values(-40150,-400,'Title General','WELCOME TO ENERGY COMPANY SERVICES!','Title of the general page');
insert into YukonRoleProperty values(-40151,-400,'Title Programs Control History','PROGRAMS - CONTROL HISTORY','Title of the programs control history page');
insert into YukonRoleProperty values(-40152,-400,'Title Program Control History','PROGRAM - CONTROL HISTORY','Title of the control history page of a particular program');
insert into YukonRoleProperty values(-40153,-400,'Title Program Control Summary','PROGRAM - CONTROL SUMMARY','Title of the control summary page of a particular program');
insert into YukonRoleProperty values(-40154,-400,'Title Programs Enrollment','PROGRAMS - ENROLLMENT','Title of the programs enrollment page');
insert into YukonRoleProperty values(-40155,-400,'Title Programs Opt Out','PROGRAMS - OPT OUT','Title of the programs opt out page');
insert into YukonRoleProperty values(-40156,-400,'Title Utility','QUESTIONS - UTILITY','Title of the utility page');

insert into YukonRoleProperty values(-40157,-400,'Title Thermostat Schedule','THERMOSTAT - SCHEDULE','Title of the thermostat schedule page');
insert into YukonRoleProperty values(-40158,-400,'Title Thermostat Manual','THERMOSTAT - MANUAL','Title of the thermostat manual page');
insert into YukonRoleProperty values(-40170,-400,'Description General','Thank you for participating in our Consumer Energy Services programs. By participating, you have helped to optimize our delivery of energy, stabilize rates, and reduce energy costs. Best of all, you are saving energy dollars!<br><br>This site is designed to help manage your programs on-line from anywhere with access to a Web browser.','Description on the general page');
insert into YukonRoleProperty values(-40171,-400,'Description Opt Out','If you would like to temporarily opt out of all programs, select the time frame from the drop-down box below, then select Submit.','Description on the programs opt out page');
insert into YukonRoleProperty values(-40172,-400,'Description Enrollment','Select the check boxes and corresponding radio button of the programs you would like to be enrolled in.','Description on the program enrollment page');
insert into YukonRoleProperty values(-40173,-400,'Description Utility',' <<COMPANY_ADDRESS>><br><<PHONE_NUMBER>><<FAX_NUMBER>><<EMAIL>>','Description on the contact us page. The special fields will be replaced by real information when displayed on the web.');

insert into YukonRoleProperty values(-40180,-400,'Image Corner','yukon/Mom.jpg','Image at the upper-left corner');
insert into YukonRoleProperty values(-40181,-400,'Image General','yukon/Family.jpg','Image on the general page');

alter table YukonRoleProperty
   add constraint PK_YUKONROLEPROPERTY primary key  (RolePropertyID)
go


/*==============================================================*/
/* Table : YukonSelectionList                                   */
/*==============================================================*/
create table YukonSelectionList (
ListID               numeric              not null,
Ordering             varchar(1)           not null,
SelectionLabel       varchar(30)          not null,
WhereIsList          varchar(100)         not null,
ListName             varchar(40)          not null,
UserUpdateAvailable  varchar(1)           not null
)
go


insert into YukonSelectionList values( 0, 'N', '(none)', '(none)', '(none)', 'N' );
insert into YukonSelectionList values( 1, 'A', 'Contact', 'DBEditor contact type list', 'ContactType', 'N' );


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
insert into YukonSelectionList values (1020,'O','(none)','Customer FAQ groups','CustomerFAQGroup','N');
insert into YukonSelectionList values (1021,'N','(none)','Residence type selection','ResidenceType','Y');
insert into YukonSelectionList values (1022,'N','(none)','Construction material selection','ConstructionMaterial','Y');
insert into YukonSelectionList values (1023,'N','(none)','Decade built selection','DecadeBuilt','Y');
insert into YukonSelectionList values (1024,'N','(none)','Square feet selection','SquareFeet','Y');
insert into YukonSelectionList values (1025,'N','(none)','Insulation depth selection','InsulationDepth','Y');
insert into YukonSelectionList values (1026,'N','(none)','General condition selection','GeneralCondition','Y');
insert into YukonSelectionList values (1027,'N','(none)','Main cooling system selection','CoolingSystem','Y');
insert into YukonSelectionList values (1028,'N','(none)','Main heating system selection','HeatingSystem','Y');
insert into YukonSelectionList values (1029,'N','(none)','Number of occupants selection','NumberOfOccupants','Y');
insert into YukonSelectionList values (1030,'N','(none)','Ownership type selection','OwnershipType','Y');
insert into YukonSelectionList values (1031,'N','(none)','Main fuel type selection','FuelType','Y');
insert into YukonSelectionList values (1032,'N','(none)','AC Tonnage selection','ACTonnage','Y');
insert into YukonSelectionList values (1033,'N','(none)','AC type selection','ACType','Y');
insert into YukonSelectionList values (1034,'N','(none)','Water heater number of gallons selection','WHNumberOfGallons','Y');
insert into YukonSelectionList values (1035,'N','(none)','Water heater energy source selection','WHEnergySource','Y');
insert into YukonSelectionList values (1036,'N','(none)','Dual fuel switch over type selection','DFSwitchOverType','Y');
insert into YukonSelectionList values (1037,'N','(none)','Dual fuel secondary source selection','DFSecondarySource','Y');
insert into YukonSelectionList values (1038,'N','(none)','Grain dryer type selection','GrainDryerType','Y');
insert into YukonSelectionList values (1039,'N','(none)','Grain dryer bin size selection','GDBinSize','Y');
insert into YukonSelectionList values (1040,'N','(none)','Grain dryer blower energy source selection','GDEnergySource','Y');
insert into YukonSelectionList values (1041,'N','(none)','Grain dryer blower horse power selection','GDHorsePower','Y');
insert into YukonSelectionList values (1042,'N','(none)','Grain dryer blower heat source selection','GDHeatSource','Y');
insert into YukonSelectionList values (1043,'N','(none)','Storage heat type selection','StorageHeatType','Y');
insert into YukonSelectionList values (1044,'N','(none)','Heat pump type selection','HeatPumpType','Y');
insert into YukonSelectionList values (1045,'N','(none)','Heat pump standby source selection','HPStandbySource','Y');
insert into YukonSelectionList values (1046,'N','(none)','Irrigation type selection','IrrigationType','Y');
insert into YukonSelectionList values (1047,'N','(none)','Irrigation soil type selection','IRRSoilType','Y');
insert into YukonSelectionList values (1048,'N','(none)','Device location selection','DeviceLocation','N');
insert into YukonSelectionList values (1049,'O','(none)','Opt out period selection','OptOutPeriod','Y');
insert into YukonSelectionList values (1050,'N','(none)','Gateway end device data description','GatewayEndDeviceDataDesc','N');
insert into YukonSelectionList values (1051,'N','(none)','Hardware Inventory search by selection','InvSearchBy','N');
insert into YukonSelectionList values (1052,'N','(none)','Hardware Inventory sort by selection','InvSortBy','N');
insert into YukonSelectionList values (1053,'N','(none)','Hardware Inventory filter by selection','InvFilterBy','N');
insert into YukonSelectionList values (1054,'N','(none)','Service order search by selection','SOSearchBy','N');
insert into YukonSelectionList values (1055,'N','(none)','Service order sort by selection','SOSortBy','N');
insert into YukonSelectionList values (1056,'N','(none)','Service order filter by selection','SOFilterBy','N');
insert into YukonSelectionList values (1057,'N','(none)','Generator transfer switch type selection','GENTransferSwitchType','Y');
insert into YukonSelectionList values (1058,'N','(none)','Generator transfer switch manufacturer selection','GENTransferSwitchMfg','Y');
insert into YukonSelectionList values (1059,'N','(none)','Irrigation horse power selection','IRRHorsePower','Y');
insert into YukonSelectionList values (1060,'N','(none)','Irrigation energy source selection','IRREnergySource','Y');
insert into YukonSelectionList values (1061,'N','(none)','Irrigation meter location selection','IRRMeterLocation','Y');
insert into YukonSelectionList values (1062,'N','(none)','Irrigation meter voltage selection','IRRMeterVoltage','Y');
insert into YukonSelectionList values (1063,'N','(none)','Water heater location selection','WHLocation','Y');
insert into YukonSelectionList values (1064,'N','(none)','Heat pump size selection','HeatPumpSize','Y');
insert into YukonSelectionList values (2000,'N','(none)','Customer Selection Base','(none)','N');

alter table YukonSelectionList
   add constraint PK_YUKONSELECTIONLIST primary key  (ListID)
go


/*==============================================================*/
/* Table : YukonServices                                        */
/*==============================================================*/
create table YukonServices (
ServiceID            numeric              not null,
ServiceName          varchar(60)          not null,
ServiceClass         varchar(100)         not null,
ParamNames           varchar(300)         not null,
ParamValues          varchar(300)         not null
)
go


insert into YukonServices values( 1, 'Notification_Server', 'com.cannontech.jmx.services.DynamicNotifcationServer', '(none)', '(none)' );
/* insert into YukonServices values( 2, 'WebGraph', 'com.cannontech.jmx.services.DynamicWebGraph', '(none)', '(none)' ); */
/* insert into YukonServices values( 3, 'Calc_Historical', 'com.cannontech.jmx.services.DynamicCalcHist', '(none)', '(none)' ); */

alter table YukonServices
   add constraint PK_YUKSER primary key  (ServiceID)
go


/*==============================================================*/
/* Table : YukonUser                                            */
/*==============================================================*/
create table YukonUser (
UserID               numeric              not null,
UserName             varchar(64)          not null,
Password             varchar(64)          not null,
LoginCount           numeric              not null,
LastLogin            datetime             not null,
Status               varchar(20)          not null
)
go


insert into YukonUser values(-1,'admin','admin',0,'01-JAN-00','Enabled');
insert into YukonUser values(-2,'yukon','yukon',0,'01-JAN-00','Enabled');
insert into YukonUser values(-100,'DefaultCTI','$cti_default',0,'01-JAN-00','Enabled');
insert into YukonUser values(-9999,'(none)','(none)',0,'01-JAN-00','Disabled');

alter table YukonUser
   add constraint PK_YUKONUSER primary key  (UserID)
go


/*==============================================================*/
/* Index: Indx_YkUsIDNm                                         */
/*==============================================================*/
create unique  index Indx_YkUsIDNm on YukonUser (
UserName
)
go


/*==============================================================*/
/* Table : YukonUserGroup                                       */
/*==============================================================*/
create table YukonUserGroup (
UserID               numeric              not null,
GroupID              numeric              not null
)
go


insert into YukonUserGroup values(-1,-1);
insert into YukonUserGroup values(-2,-1);
insert into YukonUserGroup values(-2,-2);

alter table YukonUserGroup
   add constraint PK_YUKONUSERGROUP primary key  (UserID, GroupID)
go


/*==============================================================*/
/* Table : YukonUserRole                                        */
/*==============================================================*/
create table YukonUserRole (
UserRoleID           numeric              not null,
UserID               numeric              not null,
RoleID               numeric              not null,
RolePropertyID       numeric              not null,
Value                varchar(1000)        not null
)
go


/* Database Editor */
insert into YukonUserRole values(-100,-1,-100,-10000,'(none)');
insert into YukonUserRole values(-101,-1,-100,-10001,'(none)');
insert into YukonUserRole values(-102,-1,-100,-10002,'(none)');
insert into YukonUserRole values(-103,-1,-100,-10003,'(none)');
insert into YukonUserRole values(-104,-1,-100,-10004,'(none)');
insert into YukonUserRole values(-105,-1,-100,-10005,'(none)');
insert into YukonUserRole values(-107,-1,-100,-10007,'(none)');
insert into YukonUserRole values(-108,-1,-100,-10008,'(none)');
insert into YukonUserRole values(-109,-1,-100,-10009,'(none)');

/* TDC */
insert into YukonUserRole values(-120,-1,-101,-10100,'(none)');
insert into YukonUserRole values(-121,-1,-101,-10101,'(none)');
insert into YukonUserRole values(-122,-1,-101,-10102,'(none)');
insert into YukonUserRole values(-123,-1,-101,-10103,'(none)');
insert into YukonUserRole values(-124,-1,-101,-10104,'(none)');
insert into YukonUserRole values(-125,-1,-101,-10105,'(none)');
insert into YukonUserRole values(-126,-1,-101,-10106,'(none)');
insert into YukonUserRole values(-127,-1,-101,-10107,'(none)');
insert into YukonUserRole values(-128,-1,-101,-10108,'(none)');
insert into YukonUserRole values(-129,-1,-101,-10109,'(none)');
insert into YukonUserRole values(-130,-1,-101,-10111,'(none)');


/* Trending */
insert into YukonUserRole values(-150,-1,-102,-10200,'(none)');

/* Commander */
insert into YukonUserRole values(-170,-1,-103,-10300,'(none)');
insert into YukonUserRole values(-171,-1,-103,-10301,'true');
insert into YukonUserRole values(-172,-1,-103,-10302,'true');
insert into YukonUserRole values(-173,-1,-103,-10303,'false');
insert into YukonUserRole values(-174,-1,-103,-10304,'false');

/* Esubstation Editor */
insert into YukonUserRole values(-250,-1,-107,-10700,'(none)');

insert into YukonUserRole values(-300,-1,-206,-20600,'(none)');
insert into YukonUserRole values(-301,-1,-206,-20601,'(none)');
insert into YukonUserRole values(-302,-1,-206,-20602,'(none)');

insert into YukonUserRole values(-350,-1,-206,-20600,'(none)');
insert into YukonUserRole values(-351,-1,-206,-20601,'true');
insert into YukonUserRole values(-352,-1,-206,-20602,'false');

/* Web Client Customers Web Client role */
insert into YukonUserRole values (-400, -1, -108, -10800, '/user/CILC/user_trending.jsp');
insert into YukonUserRole values (-402, -1, -108, -10802, '(none)');
insert into YukonUserRole values (-403, -1, -108, -10803, '(none)');
insert into YukonUserRole values (-404, -1, -108, -10804, '(none)');
insert into YukonUserRole values (-405, -1, -108, -10805, '(none)');
insert into YukonUserRole values (-406, -1, -108, -10806, '(none)');
insert into YukonUserRole values (-407, -1, -108, -10807, '(none)');
insert into YukonUserRole values (-408, -1, -108, -10808, '(none)');

/* Web Client Customers Direct Load Control role */
insert into YukonUserRole values (-410, -1, -300, -30000, '(none)');
insert into YukonUserRole values (-411, -1, -300, -30001, 'true');

/* Web Client Customers Curtailment role */
insert into YukonUserRole values (-420, -1, -301, -30100, '(none)');
insert into YukonUserRole values (-421, -1, -301, -30101, '(none)');

/* Web Client Customers Energy Buyback role */
insert into YukonUserRole values (-430, -1, -302, -30200, '(none)');
insert into YukonUserRole values (-431, -1, -302, -30200, '(none)');

/* Web Client Customers Commercial Metering role */
insert into YukonUserRole values (-440, -1, -304, -30400, '(none)');
insert into YukonUserRole values (-441, -1, -304, -30401, 'true');
insert into YukonUserRole values (-442, -1, -304, -30402, '(none)'); 
insert into YukonUserRole values (-443, -1, -304, -30403, '(none)');

/* Web Client Customers Administrator role */
insert into YukonUserRole values (-450, -1, -305, -30500, 'true');

insert into YukonUserRole values (-520,-1,-400,-40000,'(none)');
insert into YukonUserRole values (-521,-1,-400,-40001,'(none)');
insert into YukonUserRole values (-522,-1,-400,-40002,'(none)');
insert into YukonUserRole values (-523,-1,-400,-40003,'(none)');
insert into YukonUserRole values (-524,-1,-400,-40004,'(none)');
insert into YukonUserRole values (-525,-1,-400,-40005,'(none)');
insert into YukonUserRole values (-526,-1,-400,-40006,'(none)');
insert into YukonUserRole values (-527,-1,-400,-40007,'(none)');
insert into YukonUserRole values (-528,-1,-400,-40008,'(none)');
insert into YukonUserRole values (-529,-1,-400,-40009,'(none)');
insert into YukonUserRole values (-530,-1,-400,-40010,'(none)');

insert into YukonUserRole values (-550,-1,-400,-40050,'(none)');
insert into YukonUserRole values (-551,-1,-400,-40051,'(none)');
insert into YukonUserRole values (-552,-1,-400,-40052,'(none)');
insert into YukonUserRole values (-554,-1,-400,-40054,'(none)');
insert into YukonUserRole values (-555,-1,-400,-40055,'(none)');

insert into YukonUserRole values (-600,-1,-400,-40100,'(none)');
insert into YukonUserRole values (-601,-1,-400,-40101,'(none)');
insert into YukonUserRole values (-610,-1,-400,-40110,'(none)');
insert into YukonUserRole values (-611,-1,-400,-40111,'(none)');
insert into YukonUserRole values (-612,-1,-400,-40112,'(none)');
insert into YukonUserRole values (-613,-1,-400,-40113,'(none)');
insert into YukonUserRole values (-614,-1,-400,-40114,'(none)');
insert into YukonUserRole values (-615,-1,-400,-40115,'(none)');
insert into YukonUserRole values (-616,-1,-400,-40116,'(none)');
insert into YukonUserRole values (-617,-1,-400,-40117,'(none)');
insert into YukonUserRole values (-630,-1,-400,-40130,'(none)');
insert into YukonUserRole values (-631,-1,-400,-40131,'(none)');
insert into YukonUserRole values (-632,-1,-400,-40132,'(none)');
insert into YukonUserRole values (-633,-1,-400,-40133,'(none)');
insert into YukonUserRole values (-634,-1,-400,-40134,'(none)');
insert into YukonUserRole values (-650,-1,-400,-40150,'(none)');
insert into YukonUserRole values (-651,-1,-400,-40151,'(none)');
insert into YukonUserRole values (-652,-1,-400,-40152,'(none)');
insert into YukonUserRole values (-653,-1,-400,-40153,'(none)');
insert into YukonUserRole values (-654,-1,-400,-40154,'(none)');
insert into YukonUserRole values (-655,-1,-400,-40155,'(none)');
insert into YukonUserRole values (-656,-1,-400,-40156,'(none)');
insert into YukonUserRole values (-657,-1,-400,-40157,'(none)');
insert into YukonUserRole values (-658,-1,-400,-40158,'(none)');
insert into YukonUserRole values (-670,-1,-400,-40170,'(none)');
insert into YukonUserRole values (-671,-1,-400,-40171,'(none)');
insert into YukonUserRole values (-672,-1,-400,-40172,'(none)');
insert into YukonUserRole values (-673,-1,-400,-40173,'(none)');
insert into YukonUserRole values (-680,-1,-400,-40180,'(none)');
insert into YukonUserRole values (-681,-1,-400,-40181,'(none)');

insert into YukonUserRole values (-720,-1,-201,-20100,'(none)');
insert into YukonUserRole values (-721,-1,-201,-20101,'(none)');
insert into YukonUserRole values (-722,-1,-201,-20102,'(none)');
insert into YukonUserRole values (-723,-1,-201,-20103,'(none)');
insert into YukonUserRole values (-724,-1,-201,-20104,'(none)');
insert into YukonUserRole values (-725,-1,-201,-20105,'(none)');
insert into YukonUserRole values (-726,-1,-201,-20106,'(none)');
insert into YukonUserRole values (-727,-1,-201,-20107,'(none)');
insert into YukonUserRole values (-728,-1,-201,-20108,'(none)');
insert into YukonUserRole values (-729,-1,-201,-20109,'(none)');
insert into YukonUserRole values (-730,-1,-201,-20110,'(none)');
insert into YukonUserRole values (-731,-1,-201,-20111,'(none)');
insert into YukonUserRole values (-732,-1,-201,-20112,'(none)');
insert into YukonUserRole values (-733,-1,-201,-20113,'(none)');
insert into YukonUserRole values (-734,-1,-201,-20114,'(none)');
insert into YukonUserRole values (-735,-1,-201,-20115,'(none)');
insert into YukonUserRole values (-736,-1,-201,-20116,'(none)');
insert into YukonUserRole values (-737,-1,-201,-20117,'(none)');
insert into YukonUserRole values (-738,-1,-201,-20118,'true');

insert into YukonUserRole values (-750,-1,-201,-20150,'(none)');
insert into YukonUserRole values (-751,-1,-201,-20151,'(none)');
insert into YukonUserRole values (-752,-1,-201,-20152,'(none)');
insert into YukonUserRole values (-753,-1,-201,-20153,'(none)');
insert into YukonUserRole values (-754,-1,-201,-20154,'(none)');
insert into YukonUserRole values (-755,-1,-201,-20155,'(none)');
insert into YukonUserRole values (-756,-1,-201,-20156,'(none)');
insert into YukonUserRole values (-757,-1,-201,-20157,'(none)');

insert into YukonUserRole values (-765,-1,-210,-21000,'(none)');
insert into YukonUserRole values (-766,-1,-210,-21001,'(none)');

insert into YukonUserRole values (-770,-1,-202,-20200,'(none)');
insert into YukonUserRole values (-775,-1,-203,-20300,'(none)');
insert into YukonUserRole values (-776,-1,-203,-20301,'(none)');
insert into YukonUserRole values (-780,-1,-204,-20400,'(none)');
insert into YukonUserRole values (-785,-1,-205,-20500,'(none)');
insert into YukonUserRole values (-790,-1,-207,-20700,'(none)');
insert into YukonUserRole values (-791,-1,-209,-20900,'(none)');
insert into YukonUserRole values (-792,-1,-209,-20901,'(none)');
insert into YukonUserRole values (-793,-1,-209,-20902,'(none)');
insert into YukonUserRole values (-794,-1,-209,-20903,'(none)');
insert into YukonUserRole values (-795,-1,-209,-20904,'(none)');

insert into YukonUserRole values (-800,-1,-201,-20800,'(none)');
insert into YukonUserRole values (-810,-1,-201,-20810,'(none)');
insert into YukonUserRole values (-813,-1,-201,-20813,'(none)');
insert into YukonUserRole values (-814,-1,-201,-20814,'(none)');
insert into YukonUserRole values (-815,-1,-201,-20815,'(none)');
insert into YukonUserRole values (-816,-1,-201,-20816,'(none)');
insert into YukonUserRole values (-819,-1,-201,-20819,'(none)');
insert into YukonUserRole values (-820,-1,-201,-20820,'(none)');
insert into YukonUserRole values (-830,-1,-201,-20830,'(none)');
insert into YukonUserRole values (-831,-1,-201,-20831,'(none)');
insert into YukonUserRole values (-832,-1,-201,-20832,'(none)');
insert into YukonUserRole values (-833,-1,-201,-20833,'(none)');
insert into YukonUserRole values (-834,-1,-201,-20834,'(none)');
insert into YukonUserRole values (-850,-1,-201,-20850,'(none)');
insert into YukonUserRole values (-851,-1,-201,-20851,'(none)');
insert into YukonUserRole values (-852,-1,-201,-20852,'(none)');
insert into YukonUserRole values (-853,-1,-201,-20853,'(none)');
insert into YukonUserRole values (-854,-1,-201,-20854,'(none)');
insert into YukonUserRole values (-855,-1,-201,-20855,'(none)');
insert into YukonUserRole values (-856,-1,-201,-20856,'(none)');
insert into YukonUserRole values (-870,-1,-201,-20870,'(none)');

insert into YukonUserRole values (-1000, -100, -108, -10800, '/operator/Operations.jsp');
insert into YukonUserRole values (-1002, -100, -108, -10802, '(none)');
insert into YukonUserRole values (-1003, -100, -108, -10803, '(none)');
insert into YukonUserRole values (-1004, -100, -108, -10804, '(none)');
insert into YukonUserRole values (-1005, -100, -108, -10805, '(none)');
insert into YukonUserRole values (-1006, -100, -108, -10806, '(none)');
insert into YukonUserRole values (-1010, -100, -200, -20000, '(none)');
insert into YukonUserRole values (-1011, -100, -200, -20001, 'true');
insert into YukonUserRole values (-1012, -100, -200, -20002, '(none)');
insert into YukonUserRole values (-1013, -100, -200, -20003, '(none)');

alter table YukonUserRole
   add constraint PK_YKONUSRROLE primary key  (UserRoleID)
go


/*==============================================================*/
/* Table : YukonWebConfiguration                                */
/*==============================================================*/
create table YukonWebConfiguration (
ConfigurationID      numeric              not null,
LogoLocation         varchar(100)         null,
Description          varchar(500)         null,
AlternateDisplayName varchar(100)         null,
URL                  varchar(100)         null
)
go


INSERT INTO YukonWebConfiguration VALUES (-1,'Summer.gif','Default Summer Settings','Cooling','Cool');
INSERT INTO YukonWebConfiguration VALUES (-2,'Winter.gif','Default Winter Settings','Heating','Heat');
insert into YukonWebConfiguration values(0,'(none)','(none)','(none)','(none)');

alter table YukonWebConfiguration
   add constraint PK_YUKONWEBCONFIGURATION primary key  (ConfigurationID)
go


/*==============================================================*/
/* View: DISPLAY2WAYDATA_VIEW                                   */
/*==============================================================*/
go
create view DISPLAY2WAYDATA_VIEW (POINTID, POINTNAME , POINTTYPE , POINTSTATE , DEVICENAME, DEVICETYPE, DEVICECURRENTSTATE, DEVICEID, POINTVALUE, POINTQUALITY, POINTTIMESTAMP, UofM, TAGS) as
select POINTID, POINTNAME, POINTTYPE, SERVICEFLAG, YukonPAObject.PAOName, YukonPAObject.Type, YukonPAObject.Description, YukonPAObject.PAObjectID, '**DYNAMIC**', '**DYNAMIC**', '**DYNAMIC**', (select uomname from pointunit,unitmeasure where pointunit.pointid=point.pointid and pointunit.uomid=unitmeasure.uomid), '**DYNAMIC**'
from YukonPAObject, POINT
where YukonPAObject.PAObjectID = POINT.PAObjectID
go


/*==============================================================*/
/* View: ExpressComAddress_View                                 */
/*==============================================================*/
go
create view ExpressComAddress_View  as
select x.LMGroupID, x.RouteID, x.SerialNumber, s.Address as serviceaddress,
g.Address as geoaddress, b.Address as substationaddress, f.Address as feederaddress,
x.ZipCodeAddress, x.UDAddress, p.Address as programaddress, x.SplinterAddress, x.AddressUsage, x.RelayUsage
from LMGroupExpressCom x, LMGroupExpressComAddress s, 
LMGroupExpressComAddress g, LMGroupExpressComAddress b, LMGroupExpressComAddress f,
LMGroupExpressComAddress p
where ( x.ServiceProviderID = s.AddressID and ( s.AddressType = 'SERVICE' or s.AddressID = 0 ) )
and ( x.FeederID = f.AddressID and ( f.AddressType = 'FEEDER' or f.AddressID = 0 ) )
and ( x.GeoID = g.AddressID and ( g.AddressType = 'GEO' or g.AddressID = 0 ) )
and ( x.ProgramID = p.AddressID and ( p.AddressType = 'PROGRAM' or p.AddressID = 0 ) )
and ( x.SubstationID = b.AddressID and ( b.AddressType = 'SUBSTATION' or b.AddressID = 0 ) )
go


/*==============================================================*/
/* View: FeederAddress_View                                     */
/*==============================================================*/
go
create view FeederAddress_View  as
select x.LMGroupID, a.Address as FeederAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.FeederID = a.AddressID and ( a.AddressType = 'FEEDER' or a.AddressID = 0 ) )
go


/*==============================================================*/
/* View: FullEventLog_View                                      */
/*==============================================================*/
go
create view FullEventLog_View (EventID, PointID, EventTimeStamp, EventSequence, EventType, EventAlarmID, DeviceName, PointName, EventDescription, AdditionalInfo, EventUserName) as
select s.LOGID, s.POINTID, s.DATETIME, s.SOE_TAG, s.TYPE, s.PRIORITY, y.PAOName, p.POINTNAME, s.DESCRIPTION, s.ACTION, s.USERNAME
from YukonPAObject y, POINT p, SYSTEMLOG s
where s.POINTID = p.POINTID and p.PAObjectID = y.PAObjectID
go


/*==============================================================*/
/* View: FullPointHistory_View                                  */
/*==============================================================*/
go
create view FullPointHistory_View (PointID, DeviceName, PointName, DataValue, DataTimeStamp, DataQuality) as
select r.POINTID, y.PAOName, p.POINTNAME, r.VALUE, r.TIMESTAMP, r.QUALITY
from YukonPAObject y, POINT p, RAWPOINTHISTORY r
where r.POINTID = p.POINTID and p.PAObjectID = y.PAObjectID
go


/*==============================================================*/
/* View: GeoAddress_View                                        */
/*==============================================================*/
go
create view GeoAddress_View  as
select x.LMGroupID, a.Address as GeoAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.GeoID = a.AddressID and ( a.AddressType = 'GEO' or a.AddressID = 0 ) )
go


/*==============================================================*/
/* View: LMCurtailCustomerActivity_View                         */
/*==============================================================*/
go
create view LMCurtailCustomerActivity_View  as
select cust.CustomerID, prog.CurtailmentStartTime, prog.CurtailReferenceID, prog.CurtailmentStopTime, cust.AcknowledgeStatus, cust.AckDateTime, cust.NameOfAckPerson, cust.AckLateFlag
from LMCurtailProgramActivity prog, LMCurtailCustomerActivity cust
where prog.CurtailReferenceID = cust.CurtailReferenceID
go


/*==============================================================*/
/* View: LMGroupMacroExpander_View                              */
/*==============================================================*/
go
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
/* View: LMProgram_View                                         */
/*==============================================================*/
go
create view LMProgram_View (DeviceID, ControlType, ConstraintID , ConstraintName , AvailableSeasons , AvailableWeekDays , MaxHoursDaily , MaxHoursMonthly , MaxHoursSeasonal , MaxHoursAnnually , MinActivateTime , MinRestartTime , MaxDailyOps , MaxActivateTime , HolidayScheduleID , SeasonScheduleID ) as
select t.DeviceID, t.ControlType, u.ConstraintID, u.ConstraintName, u.AvailableSeasons, u.AvailableWeekDays, u.MaxHoursDaily, u.MaxHoursMonthly, u.MaxHoursSeasonal, u.MaxHoursAnnually, u.MinActivateTime, u.MinRestartTime, u.MaxDailyOps, u.MaxActivateTime, u.HolidayScheduleID, u.SeasonScheduleID
from LMPROGRAM t, LMProgramConstraints u
where u.ConstraintID = t.ConstraintID
go


/*==============================================================*/
/* View: Peakpointhistory_View                                  */
/*==============================================================*/
go
create view Peakpointhistory_View  as
select rph1.POINTID pointid, rph1.VALUE value, min(rph1.timestamp) timestamp
from RAWPOINTHISTORY rph1
where value in ( select max ( value ) from rawpointhistory rph2 where rph1.pointid = rph2.pointid )
group by pointid, value
go


/*==============================================================*/
/* View: PointEventLog_View                                     */
/*==============================================================*/
go
create view PointEventLog_View (EventID, PointID, EventTimeStamp, EventSequence, EventType, EventAlarmID, PointName, EventDescription, AdditionalInfo, EventUserName) as
select s.LOGID, s.POINTID, s.DATETIME, s.SOE_TAG, s.TYPE, s.PRIORITY, p.POINTNAME, s.DESCRIPTION, s.ACTION, s.USERNAME
from POINT p, SYSTEMLOG s
where s.POINTID = p.POINTID
go


/*==============================================================*/
/* View: PointHistory_View                                      */
/*==============================================================*/
go
create view PointHistory_View (PointID, PointName, DataValue, DataTimeStamp, DataQuality) as
select r.POINTID, p.POINTNAME, r.VALUE, r.TIMESTAMP, r.QUALITY
from POINT p, RAWPOINTHISTORY r
where r.POINTID = p.POINTID
go


/*==============================================================*/
/* View: ProgramAddress_View                                    */
/*==============================================================*/
go
create view ProgramAddress_View  as
select x.LMGroupID, a.Address as ProgramAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.ProgramID = a.AddressID and ( a.AddressType = 'PROGRAM' or a.AddressID = 0 ) )
go


/*==============================================================*/
/* View: ServiceAddress_View                                    */
/*==============================================================*/
go
create view ServiceAddress_View  as
select x.LMGroupID, a.Address as ServiceAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.ServiceProviderID = a.AddressID and ( a.AddressType = 'SERVICE' or a.AddressID = 0 ) )
go


/*==============================================================*/
/* View: SubstationAddress_View                                 */
/*==============================================================*/
go
create view SubstationAddress_View  as
select x.LMGroupID, a.Address as SubstationAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.SubstationID = a.AddressID and ( a.AddressType = 'SUBSTATION' or a.AddressID = 0 ) )
go


alter table AlarmCategory
   add constraint FK_ALRMCAT_NOTIFGRP foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID)
go


alter table LMGroupEmetcon
   add constraint SYS_C0013356 foreign key (DEVICEID)
      references LMGroup (DeviceID)
go


alter table LMProgramControlWindow
   add constraint FK_LMPrg_LMPrgCntWind foreign key (DeviceID)
      references LMPROGRAM (DeviceID)
go


alter table DynamicLMProgramDirect
   add constraint FK_DYN_LMPR_LMP foreign key (DeviceID)
      references LMProgramDirect (DeviceID)
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
   add constraint FK_POINTALAARM_POINT_POINTID foreign key (PointID)
      references POINT (POINTID)
go


alter table PointAlarming
   add constraint FK_POINTALARMING foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID)
go


alter table LMDirectCustomerList
   add constraint FK_CICstB_LMPrDi foreign key (CustomerID)
      references CICustomerBase (CustomerID)
go


alter table LMCurtailCustomerActivity
   add constraint FK_CICBas_LMCrtCstAct foreign key (CustomerID)
      references CICustomerBase (CustomerID)
go


alter table LMProgramCurtailCustomerList
   add constraint FK_CICstBase_LMProgCList foreign key (CustomerID)
      references CICustomerBase (CustomerID)
go


alter table CICustomerBase
   add constraint FK_CICstBas_CstAddrs foreign key (MainAddressID)
      references Address (AddressID)
go


alter table CALCCOMPONENT
   add constraint FK_ClcCmp_ClcBs foreign key (PointID)
      references CALCBASE (POINTID)
go


alter table CALCCOMPONENT
   add constraint FK_ClcCmp_Pt foreign key (COMPONENTPOINTID)
      references POINT (POINTID)
go


alter table CustomerAdditionalContact
   add constraint FK_CstCont_CICstCont foreign key (ContactID)
      references Contact (ContactID)
go


alter table Contact
   add constraint FK_RefCstLg_CustCont foreign key (LogInID)
      references YukonUser (UserID)
go


alter table LMGroup
   add constraint FK_Device_LMGrpBase2 foreign key (DeviceID)
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
   add constraint FK_ExCsLs_CstBs foreign key (CustomerID)
      references CICustomerBase (CustomerID)
go


alter table LMEnergyExchangeCustomerList
   add constraint FK_ExCsLs_PrEx foreign key (ProgramID)
      references LMProgramEnergyExchange (DeviceID)
go


alter table LMEnergyExchangeCustomerReply
   add constraint FK_ExCsRp_CstBs foreign key (CustomerID)
      references CICustomerBase (CustomerID)
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
   add constraint FK_GRA_REFG_GRA foreign key (GraphDefinitionID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID)
go


alter table DynamicLMControlAreaTrigger
   add constraint FK_LMCntArTr_DyLMCnArTr foreign key (DeviceID, TriggerNumber)
      references LMCONTROLAREATRIGGER (DEVICEID, TRIGGERNUMBER)
go


alter table DynamicLMControlArea
   add constraint FK_LMCntlAr_DynLMCntAr foreign key (DeviceID)
      references LMControlArea (DEVICEID)
go


alter table LMCONTROLAREATRIGGER
   add constraint FK_LMCntlArea_LMCntlArTrig foreign key (DEVICEID)
      references LMControlArea (DEVICEID)
go


alter table LMCurtailCustomerActivity
   add constraint FK_LMC_REFL_LMC foreign key (CurtailReferenceID)
      references LMCurtailProgramActivity (CurtailReferenceID)
go


alter table LMCONTROLAREAPROGRAM
   add constraint FK_LMCntlArea_LMCntlArProg foreign key (DEVICEID)
      references LMControlArea (DEVICEID)
go


alter table LMProgramDirectGroup
   add constraint FK_LMGrp_LMPrgDGrp foreign key (LMGroupDeviceID)
      references LMGroup (DeviceID)
go


alter table LMGroupVersacom
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
   add constraint FK_LMPrgCrt_LMPrCstLst foreign key (ProgramID)
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
      references LMPROGRAM (DeviceID)
go


alter table DynamicLMProgram
   add constraint FK_LMProg_DynLMPrg foreign key (DeviceID)
      references LMPROGRAM (DeviceID)
go


alter table LMProgramCurtailment
   add constraint FK_LMPrg_LMPrgCurt foreign key (DeviceID)
      references LMPROGRAM (DEVICEID)
go


alter table LMProgramDirect
   add constraint FK_LMPrg_LMPrgDirect foreign key (DeviceID)
      references LMPROGRAM (DeviceID)
go


alter table LMGroupRipple
   add constraint FK_LmGr_LmGrpRip foreign key (DeviceID)
      references LMGroup (DeviceID)
go


alter table LMGroupRipple
   add constraint FK_LmGrpRip_Rout foreign key (RouteID)
      references Route (RouteID)
go


alter table LMMacsScheduleCustomerList
   add constraint FK_McSchCstLst_MCSched foreign key (ScheduleID)
      references MACSchedule (ScheduleID)
go


alter table LMMacsScheduleCustomerList
   add constraint FK_McsSchdCusLst_CICBs foreign key (LMCustomerDeviceID)
      references CICustomerBase (CustomerID)
go


alter table LMCONTROLAREATRIGGER
   add constraint FK_Point_LMCntlArTrig foreign key (POINTID)
      references POINT (POINTID)
go


alter table LMCONTROLAREATRIGGER
   add constraint FK_Point_LMCtrlArTrigPk foreign key (PEAKPOINTID)
      references POINT (POINTID)
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
      references CICustomerBase (CustomerID)
go


alter table CalcPointBaseline
   add constraint FK_CLCBS_BASL foreign key (BaselineID)
      references BaseLine (BaselineID)
go


alter table CalcPointBaseline
   add constraint FK_ClcPtBs_ClcBs foreign key (PointID)
      references CALCBASE (POINTID)
go


alter table ContactNotification
   add constraint FK_CntNot_YkLs foreign key (NotificationCategoryID)
      references YukonListEntry (EntryID)
go


alter table NotificationDestination
   add constraint FK_CntNt_NtDst foreign key (RecipientID)
      references ContactNotification (ContactNotifID)
go


alter table PointAlarming
   add constraint FK_CntNt_PtAl foreign key (RecipientID)
      references ContactNotification (ContactNotifID)
go


alter table Contact
   add constraint FK_CON_REF__ADD foreign key (AddressID)
      references Address (AddressID)
go


alter table ContactNotification
   add constraint FK_Cnt_CntNot foreign key (ContactID)
      references Contact (ContactID)
go


alter table CommErrorHistory
   add constraint FK_ComErrHis_YPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID)
go


alter table CommPort
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


alter table CustomerLoginSerialGroup
   add constraint FK_CsLgSG_CsL foreign key (LoginID)
      references YukonUser (UserID)
go


alter table CustomerLoginSerialGroup
   add constraint FK_CsLgSG_LMG foreign key (LMGroupID)
      references LMGroup (DeviceID)
go


alter table CustomerBaseLinePoint
   add constraint FK_CstBseLn_CICust foreign key (CustomerID)
      references CICustomerBase (CustomerID)
go


alter table CustomerBaseLinePoint
   add constraint FK_CstBseLn_ClcBse foreign key (PointID)
      references CALCBASE (POINTID)
go


alter table CICustomerBase
   add constraint FK_CstCI_Cst foreign key (CustomerID)
      references Customer (CustomerID)
go


alter table Customer
   add constraint FK_Cst_Cnt foreign key (PrimaryContactID)
      references Contact (ContactID)
go


alter table CustomerAdditionalContact
   add constraint FK_Cust_CustAddCnt foreign key (CustomerID)
      references Customer (CustomerID)
go


alter table DeviceWindow
   add constraint FK_DevScWin_Dev foreign key (DeviceID)
      references DEVICE (DEVICEID)
go


alter table DeviceAddress
   add constraint FK_Dev_DevDNP foreign key (DeviceID)
      references DEVICE (DEVICEID)
go


alter table DeviceRTC
   add constraint FK_Dev_DevRTC foreign key (DeviceID)
      references DEVICE (DEVICEID)
go


alter table DEVICE
   add constraint FK_Dev_YukPAO foreign key (DEVICEID)
      references YukonPAObject (PAObjectID)
go


alter table DeviceSeries5RTU
   add constraint FK_DvS5r_Dv2w foreign key (DeviceID)
      references DEVICE2WAYFLAGS (DEVICEID)
go


alter table DeviceCustomerList
   add constraint FK_DvStLsCst foreign key (CustomerID)
      references Customer (CustomerID)
go


alter table DeviceCustomerList
   add constraint FK_DvStLsDev foreign key (DeviceID)
      references DEVICE (DEVICEID)
go


alter table DynamicLMGroup
   add constraint FK_DyLmGr_LmPrDGr foreign key (LMProgramID)
      references LMProgramDirect (DeviceID)
go


alter table DynamicCalcHistorical
   add constraint FK_DynClc_ClcB foreign key (PointID)
      references CALCBASE (POINTID)
go


alter table DynamicPointAlarming
   add constraint FK_DynPtAl_Pt foreign key (PointID)
      references POINT (POINTID)
go


alter table DynamicPointAlarming
   add constraint FKf_DynPtAl_SysL foreign key (LogID)
      references SYSTEMLOG (LOGID)
go


alter table DynamicTags
   add constraint FK_DynTgs_Pt foreign key (PointID)
      references POINT (POINTID)
go


alter table DynamicTags
   add constraint FK_DYN_REF__TAG foreign key (TagID)
      references Tags (TagID)
go


alter table EnergyCompany
   add constraint FK_EnCm_Cnt foreign key (PrimaryContactID)
      references Contact (ContactID)
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
   add constraint FK_EngCmp_YkUs foreign key (UserID)
      references YukonUser (UserID)
go


alter table LMGroupExpressCom
   add constraint FK_ExCG_LMExCm foreign key (GeoID)
      references LMGroupExpressComAddress (AddressID)
go


alter table LMGroupExpressCom
   add constraint FK_ExCP_LMExCm foreign key (ProgramID)
      references LMGroupExpressComAddress (AddressID)
go


alter table LMGroupExpressCom
   add constraint FK_ExCSb_LMExCm foreign key (SubstationID)
      references LMGroupExpressComAddress (AddressID)
go


alter table LMGroupExpressCom
   add constraint FK_ExCSp_LMExCm foreign key (ServiceProviderID)
      references LMGroupExpressComAddress (AddressID)
go


alter table LMGroupExpressCom
   add constraint FK_ExCad_LMExCm foreign key (FeederID)
      references LMGroupExpressComAddress (AddressID)
go


alter table OperatorLoginGraphList
   add constraint FK_OpLgOpLgGrLs foreign key (GraphDefinitionID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID)
go


alter table GraphCustomerList
   add constraint FK_GrphCstLst_Cst foreign key (CustomerID)
      references Customer (CustomerID)
go


alter table LMProgramConstraints
   add constraint FK_HlSc_LmPrC foreign key (HolidayScheduleID)
      references HolidaySchedule (HolidayScheduleID)
go


alter table DateOfHoliday
   add constraint FK_HolSchID foreign key (HolidayScheduleID)
      references HolidaySchedule (HolidayScheduleID)
go


alter table LMGroupExpressCom
   add constraint FK_LGrEx_LMG foreign key (LMGroupID)
      references LMGroup (DeviceID)
go


alter table LMGroupExpressCom
   add constraint FK_LGrEx_Rt foreign key (RouteID)
      references Route (RouteID)
go


alter table LMGroupSA205105
   add constraint FK_LGrS205_LmG foreign key (GroupID)
      references LMGroup (DeviceID)
go


alter table LMGroupSA205105
   add constraint FK_LGrS205_Rt foreign key (RouteID)
      references Route (RouteID)
go


alter table LMGroupSA305
   add constraint FK_LGrS305_LmGrp foreign key (GroupID)
      references LMGroup (DeviceID)
go


alter table LMGroupSA305
   add constraint FK_LGrS305_Rt foreign key (RouteID)
      references Route (RouteID)
go


alter table LMDirectNotifGrpList
   add constraint FK_LMDi_DNGrpL foreign key (ProgramID)
      references LMProgramDirect (DeviceID)
go


alter table LMDirectOperatorList
   add constraint FK_LMDirOpLs_LMPrD foreign key (ProgramID)
      references LMProgramDirect (DeviceID)
go


alter table LMGroupMCT
   add constraint FK_LMGrMC_Grp foreign key (DeviceID)
      references LMGroup (DeviceID)
go


alter table LMGroupMCT
   add constraint FK_LMGrMC_Rt foreign key (RouteID)
      references Route (RouteID)
go


alter table LMGroupMCT
   add constraint FK_LMGrMC_YkP foreign key (MCTDeviceID)
      references YukonPAObject (PAObjectID)
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


alter table LMPROGRAM
   add constraint FK_LMPr_PrgCon foreign key (ConstraintID)
      references LMProgramConstraints (ConstraintID)
go


alter table LMControlScenarioProgram
   add constraint FK_LmCScP_YkPA foreign key (ScenarioID)
      references YukonPAObject (PAObjectID)
go


alter table LMControlArea
   add constraint FK_LmCntAr_YukPAO foreign key (DEVICEID)
      references YukonPAObject (PAObjectID)
go


alter table LMControlHistory
   add constraint FK_LmCtrlHis_YPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID)
go


alter table LMGroupSASimple
   add constraint FK_LmGrSa_LmG foreign key (GroupID)
      references LMGroup (DeviceID)
go


alter table LMGroupSASimple
   add constraint FK_LmGrSa_Rt foreign key (RouteID)
      references Route (RouteID)
go


alter table LMProgramEnergyExchange
   add constraint FK_LmPrg_LmPrEEx foreign key (DeviceID)
      references LMPROGRAM (DeviceID)
go


alter table LMPROGRAM
   add constraint FK_LmProg_YukPAO foreign key (DeviceID)
      references YukonPAObject (PAObjectID)
go


alter table LMControlScenarioProgram
   add constraint FK_LMC_REF__LMP foreign key (ProgramID)
      references LMPROGRAM (DeviceID)
go


alter table YukonListEntry
   add constraint FK_LstEnty_SelLst foreign key (ListID)
      references YukonSelectionList (ListID)
go


alter table LMMACSScheduleOperatorList
   add constraint FK_MCSchLMMcSchOpLs foreign key (ScheduleID)
      references MACSchedule (ScheduleID)
go


alter table MCTBroadCastMapping
   add constraint FK_MCTB_MAPDEV foreign key (MCTBroadCastID)
      references DEVICE (DEVICEID)
go


alter table MCTBroadCastMapping
   add constraint FK_MCTB_MAPMCT foreign key (MctID)
      references DEVICE (DEVICEID)
go


alter table MCTConfigMapping
   add constraint FK_McCfgM_Dev foreign key (MctID)
      references DEVICE (DEVICEID)
go


alter table MCTConfigMapping
   add constraint FK_McCfgM_McCfg foreign key (ConfigID)
      references MCTConfig (ConfigID)
go


alter table LMDirectNotifGrpList
   add constraint FK_NtGr_DNGrpL foreign key (NotificationGrpID)
      references NotificationGroup (NotificationGroupID)
go


alter table EnergyCompanyOperatorLoginList
   add constraint FK_OpLgEnCmpOpLs foreign key (OperatorLoginID)
      references YukonUser (UserID)
go


alter table LMMACSScheduleOperatorList
   add constraint FK_OpLgLMMcSchOpLs foreign key (OperatorLoginID)
      references YukonUser (UserID)
go


alter table OperatorLoginGraphList
   add constraint FK_OpLgOpLgGrLs2 foreign key (OperatorLoginID)
      references YukonUser (UserID)
go


alter table LMDirectOperatorList
   add constraint FK_OpLg_LMDOpLs foreign key (OperatorLoginID)
      references YukonUser (UserID)
go


alter table OperatorSerialGroup
   add constraint FK_OpSGrp_LmGrp foreign key (LMGroupID)
      references LMGroup (DeviceID)
go


alter table OperatorSerialGroup
   add constraint FK_OpSGrp_OpLg foreign key (LoginID)
      references YukonUser (UserID)
go


alter table PAOExclusion
   add constraint FK_PAOEx_Pt foreign key (PointID)
      references POINT (POINTID)
go


alter table PAOExclusion
   add constraint FK_PAOEx_YkPAO foreign key (PaoID)
      references YukonPAObject (PAObjectID)
go


alter table PAOExclusion
   add constraint FK_PAO_REF__YUK foreign key (ExcludedPaoID)
      references YukonPAObject (PAObjectID)
go


alter table CapControlFeeder
   add constraint FK_PAObj_CCFeed foreign key (FeederID)
      references YukonPAObject (PAObjectID)
go


alter table DynamicPAOStatistics
   add constraint FK_PASt_YkPA foreign key (PAOBjectID)
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


alter table Route
   add constraint FK_Route_DevID foreign key (DeviceID)
      references DEVICE (DEVICEID)
go


alter table Route
   add constraint FK_Route_YukPAO foreign key (RouteID)
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


alter table LMProgramConstraints
   add constraint FK_SesSch_LmPrC foreign key (SeasonScheduleID)
      references SeasonSchedule (ScheduleID)
go


alter table TagLog
   add constraint FK_TagLg_Pt foreign key (PointID)
      references POINT (POINTID)
go


alter table TagLog
   add constraint FK_TagLg_Tgs foreign key (TagID)
      references Tags (TagID)
go


alter table LMThermoStatGear
   add constraint FK_ThrmStG_PrDiGe foreign key (GearID)
      references LMProgramDirectGear (GearID)
go


alter table YukonGroupRole
   add constraint FK_YkGrRl_YkGrp foreign key (GroupID)
      references YukonGroup (GroupID)
go


alter table YukonGroupRole
   add constraint FK_YkGrRl_YkRle foreign key (RoleID)
      references YukonRole (RoleID)
go


alter table YukonGroupRole
   add constraint FK_YkGrpR_YkRlPr foreign key (RolePropertyID)
      references YukonRoleProperty (RolePropertyID)
go


alter table STATE
   add constraint FK_YkIm_St foreign key (ImageID)
      references YukonImage (ImageID)
go


alter table YukonRoleProperty
   add constraint FK_YkRlPrp_YkRle foreign key (RoleID)
      references YukonRole (RoleID)
go


alter table YukonUserGroup
   add constraint FK_YkUsGr_YkGr foreign key (GroupID)
      references YukonGroup (GroupID)
go


alter table YukonUserGroup
   add constraint FK_YUK_REF__YUK foreign key (UserID)
      references YukonUser (UserID)
go


alter table YukonUserRole
   add constraint FK_YkUsRl_RlPrp foreign key (RolePropertyID)
      references YukonRoleProperty (RolePropertyID)
go


alter table YukonUserRole
   add constraint FK_YkUsRl_YkRol foreign key (RoleID)
      references YukonRole (RoleID)
go


alter table YukonUserRole
   add constraint FK_YkUsRlr_YkUsr foreign key (UserID)
      references YukonUser (UserID)
go


alter table PAOowner
   add constraint FK_YukPAO_PAOOwn foreign key (ChildID)
      references YukonPAObject (PAObjectID)
go


alter table PAOowner
   add constraint FK_YukPAO_PAOid foreign key (OwnerID)
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


alter table DeviceCBC
   add constraint SYS_C0013459 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table DeviceCBC
   add constraint SYS_C0013460 foreign key (ROUTEID)
      references Route (RouteID)
go


alter table DEVICEDIALUPSETTINGS
   add constraint SYS_C0013193 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table DeviceDirectCommSettings
   add constraint SYS_C0013186 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table DeviceDirectCommSettings
   add constraint SYS_C0013187 foreign key (PORTID)
      references CommPort (PORTID)
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


alter table DeviceRoutes
   add constraint SYS_C0013219 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table DeviceRoutes
   add constraint SYS_C0013220 foreign key (ROUTEID)
      references Route (RouteID)
go


alter table DEVICESCANRATE
   add constraint SYS_C0013198 foreign key (DEVICEID)
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


alter table LMGroupEmetcon
   add constraint SYS_C0013357 foreign key (ROUTEID)
      references Route (RouteID)
go


alter table LMGroupVersacom
   add constraint SYS_C0013367 foreign key (ROUTEID)
      references Route (RouteID)
go


alter table MACROROUTE
   add constraint SYS_C0013274 foreign key (ROUTEID)
      references Route (RouteID)
go


alter table MACROROUTE
   add constraint SYS_C0013275 foreign key (SINGLEROUTEID)
      references Route (RouteID)
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
      references CommPort (PORTID)
go


alter table PORTLOCALSERIAL
   add constraint SYS_C0013147 foreign key (PORTID)
      references CommPort (PORTID)
go


alter table CAPCONTROLSUBSTATIONBUS
   add constraint SYS_C0013478 foreign key (CurrentWattLoadPointID)
      references POINT (POINTID)
go


alter table PORTRADIOSETTINGS
   add constraint SYS_C0013169 foreign key (PORTID)
      references CommPort (PORTID)
go


alter table PORTSETTINGS
   add constraint SYS_C0013156 foreign key (PORTID)
      references CommPort (PORTID)
go


alter table PORTTERMINALSERVER
   add constraint SYS_C0013151 foreign key (PORTID)
      references CommPort (PORTID)
go


alter table PortTiming
   add constraint SYS_C0013163 foreign key (PORTID)
      references CommPort (PORTID)
go


alter table RepeaterRoute
   add constraint SYS_C0013269 foreign key (ROUTEID)
      references Route (RouteID)
go


alter table RepeaterRoute
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


alter table CarrierRoute
   add constraint SYS_C0013264 foreign key (ROUTEID)
      references Route (RouteID)
go


alter table VersacomRoute
   add constraint FK_VER_ROUT_ROU foreign key (ROUTEID)
      references Route (RouteID)
go


