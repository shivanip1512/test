/*==============================================================*/
/* Database name:  YukonDatabase                                */
/* DBMS name:      Microsoft SQL Server 2000                    */
/* Created on:     12/30/2004 12:59:04 PM                       */
/*==============================================================*/


if exists (select 1
            from  sysobjects
           where  id = object_id('SubstationAddress_View')
            and   type = 'V')
   drop view SubstationAddress_View
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ServiceAddress_View')
            and   type = 'V')
   drop view ServiceAddress_View
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ProgramAddress_View')
            and   type = 'V')
   drop view ProgramAddress_View
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PointHistory_View')
            and   type = 'V')
   drop view PointHistory_View
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PointEventLog_View')
            and   type = 'V')
   drop view PointEventLog_View
go


if exists (select 1
            from  sysobjects
           where  id = object_id('Peakpointhistory_View')
            and   type = 'V')
   drop view Peakpointhistory_View
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMProgram_View')
            and   type = 'V')
   drop view LMProgram_View
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMCurtailCustomerActivity_View')
            and   type = 'V')
   drop view LMCurtailCustomerActivity_View
go


if exists (select 1
            from  sysobjects
           where  id = object_id('GeoAddress_View')
            and   type = 'V')
   drop view GeoAddress_View
go


if exists (select 1
            from  sysobjects
           where  id = object_id('FullPointHistory_View')
            and   type = 'V')
   drop view FullPointHistory_View
go


if exists (select 1
            from  sysobjects
           where  id = object_id('FullEventLog_View')
            and   type = 'V')
   drop view FullEventLog_View
go


if exists (select 1
            from  sysobjects
           where  id = object_id('FeederAddress_View')
            and   type = 'V')
   drop view FeederAddress_View
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ExpressComAddress_View')
            and   type = 'V')
   drop view ExpressComAddress_View
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DISPLAY2WAYDATA_VIEW')
            and   type = 'V')
   drop view DISPLAY2WAYDATA_VIEW
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
            from  sysindexes
           where  id    = object_id('CALCCOMPONENT')
            and   name  = 'Indx_CalcCmpCmpType'
            and   indid > 0
            and   indid < 255)
   drop index CALCCOMPONENT.Indx_CalcCmpCmpType
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('CAPCONTROLSUBSTATIONBUS')
            and   name  = 'Indx_CSUBVPT'
            and   indid > 0
            and   indid < 255)
   drop index CAPCONTROLSUBSTATIONBUS.Indx_CSUBVPT
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('CapControlFeeder')
            and   name  = 'Indx_CPCNFDVARPT'
            and   indid > 0
            and   indid < 255)
   drop index CapControlFeeder.Indx_CPCNFDVARPT
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('Contact')
            and   name  = 'Indx_ContLstName'
            and   indid > 0
            and   indid < 255)
   drop index Contact.Indx_ContLstName
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
            from  sysindexes
           where  id    = object_id('DynamicVerification')
            and   name  = 'Index_DYNVER_CS'
            and   indid > 0
            and   indid < 255)
   drop index DynamicVerification.Index_DYNVER_CS
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('DynamicVerification')
            and   name  = 'Indx_DYNV_TIME'
            and   indid > 0
            and   indid < 255)
   drop index DynamicVerification.Indx_DYNV_TIME
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
            from  sysindexes
           where  id    = object_id('GRAPHDATASERIES')
            and   name  = 'Indx_GrpDSerPtID'
            and   indid > 0
            and   indid < 255)
   drop index GRAPHDATASERIES.Indx_GrpDSerPtID
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
            from  sysindexes
           where  id    = object_id('LMControlHistory')
            and   name  = 'Indx_Start'
            and   indid > 0
            and   indid < 255)
   drop index LMControlHistory.Indx_Start
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
            from  sysindexes
           where  id    = object_id('LMCurtailCustomerActivity')
            and   name  = 'Index_LMCrtCstActID'
            and   indid > 0
            and   indid < 255)
   drop index LMCurtailCustomerActivity.Index_LMCrtCstActID
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
            from  sysindexes
           where  id    = object_id('NotificationGroup')
            and   name  = 'Indx_NOTIFGRPNme'
            and   indid > 0
            and   indid < 255)
   drop index NotificationGroup.Indx_NOTIFGRPNme
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('PAOExclusion')
            and   name  = 'Indx_PAOExclus'
            and   indid > 0
            and   indid < 255)
   drop index PAOExclusion.Indx_PAOExclus
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
           where  id    = object_id('Route')
            and   name  = 'Indx_RouteDevID'
            and   indid > 0
            and   indid < 255)
   drop index Route.Indx_RouteDevID
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
            from  sysindexes
           where  id    = object_id('STATEGROUP')
            and   name  = 'Indx_STATEGRP_Nme'
            and   indid > 0
            and   indid < 255)
   drop index STATEGROUP.Indx_STATEGRP_Nme
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('SYSTEMLOG')
            and   name  = 'Indx_SYSLG_Date'
            and   indid > 0
            and   indid < 255)
   drop index SYSTEMLOG.Indx_SYSLG_Date
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('SYSTEMLOG')
            and   name  = 'Indx_SYSLG_PtId'
            and   indid > 0
            and   indid < 255)
   drop index SYSTEMLOG.Indx_SYSLG_PtId
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('YukonListEntry')
            and   name  = 'Indx_YkLstDefID'
            and   indid > 0
            and   indid < 255)
   drop index YukonListEntry.Indx_YkLstDefID
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
            from  sysindexes
           where  id    = object_id('YukonRole')
            and   name  = 'Indx_YukRol_Nm'
            and   indid > 0
            and   indid < 255)
   drop index YukonRole.Indx_YukRol_Nm
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('YukonUser')
            and   name  = 'Indx_YkUsIDNm'
            and   indid > 0
            and   indid < 255)
   drop index YukonUser.Indx_YkUsIDNm
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
           where  id = object_id('Command')
            and   type = 'U')
   drop table Command
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
           where  id = object_id('DateOfSeason')
            and   type = 'U')
   drop table DateOfSeason
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
           where  id = object_id('DeviceMCT400Series')
            and   type = 'U')
   drop table DeviceMCT400Series
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
           where  id = object_id('DeviceTypeCommand')
            and   type = 'U')
   drop table DeviceTypeCommand
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DeviceVerification')
            and   type = 'U')
   drop table DeviceVerification
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
           where  id = object_id('DynamicLMControlHistory')
            and   type = 'U')
   drop table DynamicLMControlHistory
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
           where  id = object_id('DynamicVerification')
            and   type = 'U')
   drop table DynamicVerification
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
           where  id = object_id('TOUDay')
            and   type = 'U')
   drop table TOUDay
go


if exists (select 1
            from  sysobjects
           where  id = object_id('TOUDayMapping')
            and   type = 'U')
   drop table TOUDayMapping
go


if exists (select 1
            from  sysobjects
           where  id = object_id('TOUDayRateSwitches')
            and   type = 'U')
   drop table TOUDayRateSwitches
go


if exists (select 1
            from  sysobjects
           where  id = object_id('TOUSchedule')
            and   type = 'U')
   drop table TOUSchedule
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
           where  id = object_id('UserPAOowner')
            and   type = 'U')
   drop table UserPAOowner
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
/* Table: ActivityLog                                           */
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
/* Table: Address                                               */
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
/* Table: AlarmCategory                                         */
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
/* Table: BaseLine                                              */
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
/* Table: BillingFileFormats                                    */
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
/* Table: CALCBASE                                              */
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
/* Table: CALCCOMPONENT                                         */
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
/* Table: CAPBANK                                               */
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
/* Table: CAPCONTROLSUBSTATIONBUS                               */
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
/* Table: CCFeederBankList                                      */
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
/* Table: CCFeederSubAssignment                                 */
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
/* Table: CICustomerBase                                        */
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
/* Table: COLUMNTYPE                                            */
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
/* Table: CTIDatabase                                           */
/*==============================================================*/
create table CTIDatabase (
   Version              varchar(6)           not null,
   CTIEmployeeName      varchar(30)          not null,
   DateApplied          datetime             null,
   Notes                varchar(300)         null,
   Build                numeric              not null
)
go


/* __YUKON_VERSION__ */
alter table CTIDatabase
   add constraint PK_CTIDATABASE primary key  (Version, Build)
go


/*==============================================================*/
/* Table: CalcPointBaseline                                     */
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
/* Table: CapControlFeeder                                      */
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
/* Table: CarrierRoute                                          */
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
/* Table: CommErrorHistory                                      */
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
/* Table: CommPort                                              */
/*==============================================================*/
create table CommPort (
   PORTID               numeric              not null,
   ALARMINHIBIT         varchar(1)           not null,
   COMMONPROTOCOL       varchar(8)           not null,
   PERFORMTHRESHOLD     numeric              not null,
   PERFORMANCEALARM     varchar(1)           not null,
   SharedPortType       varchar(20)          not null,
   SharedSocketNumber   numeric              not null
)
go


alter table CommPort
   add constraint SYS_C0013112 primary key  (PORTID)
go


/*==============================================================*/
/* Table: Command                                               */
/*==============================================================*/
create table Command (
   CommandID            numeric              not null,
   Command              varchar(256)         not null,
   Label                varchar(256)         not null,
   Category             varchar(32)          not null
)
go


/* N-A */
insert into command values(-0, 'Not Available Yet', 'Not Available Yet', 'DEVICE');

/* MCT-BASE */
insert into command values(-1, 'getvalue kWh', 'Read Energy', 'All MCTs');
insert into command values(-2, 'getvalue demand', 'Read Current Demand', 'All MCTs');
insert into command values(-3, 'getconfig model', 'Read Options', 'All MCTs');
insert into command values(-4, 'putvalue kyz 1 reset', 'Clear kWh Reading', 'All MCTs');
insert into command values(-5, 'getvalue powerfail', 'Read Powerfail', 'All MCTs');
insert into command values(-6, 'getstatus internal', 'Read General Info', 'All MCTs'); 
insert into command values(-7, 'getconfig mult kyz 1', 'Read MPKH ()', 'All MCTs');
insert into command values(-8, 'putconfig emetcon multiplier kyz1 ?Multiplier(x.xxx)', 'Write MPKH ()', 'All MCTs');
insert into command values(-9, 'getconfig interval ?LP/LI', 'Read Demand Interval (LP or LI)', 'All MCTs');
insert into command values(-10, 'putconfig emetcon interval ?LP/LI', 'Write Demand Interval (LP or LI)', 'All MCTs');
/* MCT-213, 310ID */
insert into command values(-11, 'getstatus disconnect', 'Read Disconnect Status', 'All Disconnect Meters');
insert into command values(-12, 'control disconnect', 'Disconnect Meter', 'All Disconnect Meters');
insert into command values(-13, 'control connect', 'Connect Meter', 'All Disconnect Meters');
/* MCT-250, 318, 318L, 360, 370 */
insert into command values(-14, 'getstatus external', 'Read Status Points', 'All Status Input');
/* LS-BASE */
insert into command values(-15, 'getstatus LP', 'Read LS Info', 'All LP Meters');
insert into command values(-16, 'Not Available Yet', 'Read LS Intervals 1 Thru 6', 'All LP Meters');
insert into command values(-17, 'Not Available Yet', 'Read  6 LS Intervals Starting at ?', 'All LP Meters');
insert into command values(-18, 'getconfig time', 'Read Date/Time', 'All LP Meters');
insert into command values(-19, 'getconfig time sync', 'Read Last TimeSync', 'All LP Meters');
/* IED-BASE */
insert into command values(-20, 'getvalue ied demand', 'Read IED Last Interval Demands', 'All IED Meters');
insert into command values(-21, 'getvalue ied kwh', 'Read IED KWH/KW', 'All IED Meters');
insert into command values(-22, 'getconfig ied time', 'Read IED Date/Time', 'All IED Meters');
insert into command values(-23, 'Not Available', 'Read IED TOU Rate [A,B,C,or D]', 'All IED Meters');
insert into command values(-24, 'Not Available', 'Read IED Reset Count', 'All IED Meters');
insert into command values(-25, 'getconfig ied scan', 'Read IED Scan Info', 'All IED Meters');
insert into command values(-26, 'putvalue ied reset', 'Reset IED Demand', 'All IED Meters');
/* LoadGroup-BASE */
insert into command values(-27, 'control shed 5m', 'Shed Group', 'All Load Group');
insert into command values(-28, 'control restore', 'Restore Group', 'All Load Group');
/* Alpha-BASE */
insert into command values(-29, 'scan integrity', 'Force Scan', 'All Alpha Meters');
/* CBC-BASE */
insert into command values(-30, 'control open', 'OPEN Cap Bank', 'All CBCs');
insert into command values(-31, 'control close', 'CLOSE Cap Bank', 'All CBCs');
insert into command values(-32, 'putstatus ovuv disable', 'Disable OVUV', 'All CBCs');
insert into command values(-33, 'putstatus ovuv enable', 'Enable OVUV', 'All CBCs');
/* CCU-BASE, RTU-BASE, LCU-BASE, TCU-BASE */
insert into command values(-34, 'ping', 'Ping', 'All Ping-able');
insert into command values(-35, 'loop', '1 Loopback', 'All Ping-able');
insert into command values(-36, 'loop 5', '5 Loopbacks', 'All Ping-able');
/* CCU-711 */
insert into command values(-37, 'putstatus reset', 'CCU Reset', 'CCU-711');
/* Emetcon Group */
insert into command values(-38, 'control shed 7.m', 'Shed 7.5min', 'Emetcon Group');
insert into command values(-39, 'control shed 15m', 'Shed 15-min', 'Emetcon Group');
insert into command values(-40, 'control shed 30m', 'Shed 30-min', 'Emetcon Group');
insert into command values(-41, 'control shed 60m', 'Shed 60-min', 'Emetcon Group');
insert into command values(-42, 'control shed ?''NumMins''m', 'Shed x-min', 'Emetcon Group');
insert into command values(-43, 'control restore', 'Restore', 'Emetcon Group');
/* Versacom Group */
insert into command values(-44, 'control cycle 50 period 30 count 4', 'Cycle 50% / 30min', 'Versacom Group');
insert into command values(-45, 'control cycle terminate', 'Terminate Cycle', 'Versacom Group');
insert into command values(-46, 'putconfig service out', 'Set to Out-Of-Service', 'Versacom Group');
insert into command values(-47, 'putconfig service in', 'Set to In-Service', 'Versacom Group');
insert into command values(-48, 'putstatus reset r1 r2 r3 cl', 'Reset All Counters', 'Versacom Group');
insert into command values(-49, 'putconfig led yyy', 'Configure LEDS (report, test, load)', 'Versacom Group');
/* TCU-BASE*/
/* insert into command values(-50, 'loop', '1 TCU Loop', 'All TCUs'); */
/* insert into command values(-51, 'loop 5', '5 TCU Loops', 'All TCUs'); */
/* Repeater-BASE */
insert into command values(-52, 'getconfig role 1', 'Read Roles', 'All Repeaters');
insert into command values(-53, 'getconfig emetcon install', 'Download All Roles', 'All Repeaters');
insert into command values(-54, 'loop locate', 'Locate Device', 'All Repeaters');
/* ION-BASE */
insert into command values(-55, 'scan general', 'Scan Status Points', 'All ION Meters');
insert into command values(-56, 'scan integrity', 'Scan Power Meter and Status', 'All ION Meters');
insert into command values(-57, 'getstatus eventlog', 'Retrieve Event Log', 'All ION Meters');
/* IEDAlpha-BASE */
insert into command values(-58, 'putconfig emetcon ied class 72 1', 'Set Current Period (Alpha)', 'DEVICE');
insert into command values(-59, 'putconfig emetcon ied class 72 2', 'Set Previous Period (Alpha)', 'DEVICE');
/* EDGEKV-BASE */
insert into command values(-60, 'getvalue ied demand', 'Read IED Volts', 'DEVICE');
insert into command values(-61, 'putconfig emetcon ied class 0 0', 'Set Current Period (GEKV)', 'DEVICE');
insert into command values(-62, 'putconfig emetcon ied class 0 1', 'Set Previous Period (GEKV)', 'DEVICE');
/* ExpresscomSerial */
insert into command values(-63, 'putconfig xcom raw 0x30 0x00 0x02 0x58', 'Cold Load Pickup (load, time x 0.5sec)', 'ExpresscomSerial');
insert into command values(-64, 'putstatus xcom prop inc', 'Increment Prop Counter', 'ExpresscomSerial');
insert into command values(-65, 'putconfig xcom raw 0x05 0x00', 'Reset CopCount', 'ExpresscomSerial');
insert into command values(-66, 'putconfig xcom main 0x01 0x40', 'Clear Prop Counter', 'ExpresscomSerial');
insert into command values(-67, 'putconfig xcom main 0x01 0x80', 'Clear Comm Loss COunter', 'ExpresscomSerial');
insert into command values(-68, 'putconfig xcom serv out temp 24', 'Temp Out-Of-Service (hours)', 'ExpresscomSerial');
insert into command values(-69, 'putconfig xcom serv out temp', 'In Service', 'ExpresscomSerial');
/* LCRSerial */
insert into command values(-70, 'control shed 5m relay 1', 'Shed 5-min Relay 1', 'LCRSerial');
insert into command values(-71, 'control restore relay 1', 'Restore Relay 1', 'LCRSerial');
insert into command values(-72, 'control cycle 50 period 30 relay 1', 'Cycle 50 Period-30 Relay 1', 'LCRSerial');
insert into command values(-73, 'control cycle terminate relay 1', 'Terminate Cycle Relay 1', 'LCRSerial');
insert into command values(-74, 'putconfig template ''?LoadGroup''', 'Install Versacom Addressing', 'LCRSerial');
insert into command values(-75, 'putconfig cycle r1 50', 'Install Cycle Count', 'LCRSerial');
insert into command values(-76, 'putconfig cold_load r1 10', 'Install Versacom Cold Load (relay, minutes)', 'LCRSerial');
insert into command values(-77, 'putconfig raw 0x3a ff', 'Emetcon Cold Load (ON -ff / OFF -00', 'LCRSerial');
insert into command values(-78, 'putconfig xcom raw 40 ?''Weighting (1-8)''', 'Store a TrueCycle Day', 'LCRSerial');
insert into command values(-79, 'putconfig raw 36 1', 'Set Versacom Mode', 'LCRSerial');
insert into command values(-80, 'putconfig raw 35 0', 'Set Emetcon Mode', 'LCRSerial');
/* MCT410IL */
insert into command values(-81, 'getvalue demand', 'Read KW Demand, Current Voltage, Blink Count', 'MCT-410IL');
insert into command values(-82, 'getvalue voltage', 'Read Min / Max Voltage', 'MCT-410IL');
insert into command values(-83, 'putconfig emetcon timesync', 'Write Time/Date to Meter', 'MCT-410IL');
insert into command values(-84, 'getvalue peak', 'Read Current Peak', 'MCT-410IL');
alter table Command
   add constraint PK_COMMAND primary key  (CommandID)
go


/*==============================================================*/
/* Table: Contact                                               */
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
/* Index: Indx_ContLstName                                      */
/*==============================================================*/
create   index Indx_ContLstName on Contact (
ContLastName
)
go


/*==============================================================*/
/* Table: ContactNotification                                   */
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
/* Table: Customer                                              */
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
/* Table: CustomerAdditionalContact                             */
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
/* Table: CustomerBaseLinePoint                                 */
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
/* Table: CustomerLoginSerialGroup                              */
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
/* Table: DEVICE                                                */
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
/* Table: DEVICE2WAYFLAGS                                       */
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


alter table DEVICE2WAYFLAGS
   add constraint PK_DEVICE2WAYFLAGS primary key  (DEVICEID)
go


/*==============================================================*/
/* Table: DEVICECARRIERSETTINGS                                 */
/*==============================================================*/
create table DEVICECARRIERSETTINGS (
   DEVICEID             numeric              not null,
   ADDRESS              numeric              not null
)
go


alter table DEVICECARRIERSETTINGS
   add constraint PK_DEVICECARRIERSETTINGS primary key  (DEVICEID)
go


/*==============================================================*/
/* Table: DEVICEDIALUPSETTINGS                                  */
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


alter table DEVICEDIALUPSETTINGS
   add constraint PK_DEVICEDIALUPSETTINGS primary key  (DEVICEID)
go


/*==============================================================*/
/* Table: DEVICEIDLCREMOTE                                      */
/*==============================================================*/
create table DEVICEIDLCREMOTE (
   DEVICEID             numeric              not null,
   ADDRESS              numeric              not null,
   POSTCOMMWAIT         numeric              not null,
   CCUAmpUseType        varchar(20)          not null
)
go


alter table DEVICEIDLCREMOTE
   add constraint PK_DEVICEIDLCREMOTE primary key  (DEVICEID)
go


/*==============================================================*/
/* Table: DEVICEIED                                             */
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
/* Table: DEVICELOADPROFILE                                     */
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
/* Table: DEVICEMCTIEDPORT                                      */
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


alter table DEVICEMCTIEDPORT
   add constraint PK_DEVICEMCTIEDPORT primary key  (DEVICEID)
go


/*==============================================================*/
/* Table: DEVICEMETERGROUP                                      */
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
/* Table: DEVICESCANRATE                                        */
/*==============================================================*/
create table DEVICESCANRATE (
   DEVICEID             numeric              not null,
   SCANTYPE             varchar(20)          not null,
   INTERVALRATE         numeric              not null,
   SCANGROUP            numeric              not null,
   AlternateRate        numeric              not null
)
go


alter table DEVICESCANRATE
   add constraint PK_DEVICESCANRATE primary key  (DEVICEID, SCANTYPE)
go


/*==============================================================*/
/* Table: DEVICETAPPAGINGSETTINGS                               */
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
/* Table: DISPLAY                                               */
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
/* Table: DISPLAY2WAYDATA                                       */
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
/* Table: DISPLAYCOLUMNS                                        */
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
insert into displaycolumns values(1, 'Additional Info', 7, 5, 180 );
insert into displaycolumns values(1, 'User Name', 8, 6, 35 );

/*********************************************************************************************************
This use to be for the Historical Data View and RawPointHistory view,  they have been deleted and no longer used as of 7-11-2001
*********************************************************************************************************
insert into displaycolumns values(2, 'Time Stamp', 11, 1, 60 );
insert into displaycolumns values(2, 'Device Name', 5, 2, 70 );
insert into displaycolumns values(2, 'Point Name', 2, 3, 70 );
insert into displaycolumns values(2, 'Text Message', 12, 4, 180 );
insert into displaycolumns values(2, 'Additional Info', 7, 5, 180 );
insert into displaycolumns values(2, 'User Name', 8, 6, 35 );
insert into displaycolumns values(3, 'Time Stamp', 11, 1, 70 );
insert into displaycolumns values(3, 'Device Name', 5, 2, 60 );
insert into displaycolumns values(3, 'Point Name', 2, 3, 60 );
insert into displaycolumns values(3, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(3, 'Additional Info', 7, 5, 200 );
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
insert into displaycolumns values(50, 'Additional Info', 7, 5, 180 );
insert into displaycolumns values(51, 'Device Name', 5, 1, 70 );
insert into displaycolumns values(51, 'Point Name', 2, 2, 70 );
insert into displaycolumns values(51, 'Time Stamp', 11, 3, 60 );
insert into displaycolumns values(51, 'Description', 12, 4, 180 );
insert into displaycolumns values(51, 'Additional Info', 7, 5, 180 );
insert into displaycolumns values(51, 'User Name', 8, 6, 40 );
insert into displaycolumns values(51, 'Tag', 13, 7, 60 );
alter table DISPLAYCOLUMNS
   add constraint PK_DISPLAYCOLUMNS primary key  (DISPLAYNUM, TITLE)
go


/*==============================================================*/
/* Table: DYNAMICACCUMULATOR                                    */
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
/* Table: DYNAMICDEVICESCANDATA                                 */
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
/* Table: DYNAMICPOINTDISPATCH                                  */
/*==============================================================*/
create table DYNAMICPOINTDISPATCH (
   POINTID              numeric              not null,
   TIMESTAMP            datetime             not null,
   QUALITY              numeric              not null,
   VALUE                float                not null,
   TAGS                 numeric              not null,
   NEXTARCHIVE          datetime             not null,
   STALECOUNT           numeric              not null,
   LastAlarmLogID       numeric              not null,
   millis               smallint             not null
)
go


alter table DYNAMICPOINTDISPATCH
   add constraint PK_DYNAMICPOINTDISPATCH primary key  (POINTID)
go


/*==============================================================*/
/* Table: DateOfHoliday                                         */
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
/* Table: DateOfSeason                                          */
/*==============================================================*/
create table DateOfSeason (
   SeasonScheduleID     numeric              not null,
   SeasonName           varchar(20)          not null,
   SeasonStartMonth     numeric              not null,
   SeasonStartDay       numeric              not null,
   SeasonEndMonth       numeric              not null,
   SeasonEndDay         numeric              not null
)
go


alter table DateOfSeason
   add constraint PK_DATEOFSEASON primary key  (SeasonScheduleID, SeasonName)
go


/*==============================================================*/
/* Table: DeviceAddress                                         */
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
/* Table: DeviceCBC                                             */
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
/* Table: DeviceCustomerList                                    */
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
/* Table: DeviceDirectCommSettings                              */
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
/* Table: DeviceMCT400Series                                    */
/*==============================================================*/
create table DeviceMCT400Series (
   DeviceID             numeric              not null,
   DisconnectAddress    numeric              not null,
   TOUScheduleID        numeric              not null
)
go


alter table DeviceMCT400Series
   add constraint PK_DEV400S primary key  (DeviceID)
go


/*==============================================================*/
/* Table: DeviceRTC                                             */
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
/* Table: DeviceRoutes                                          */
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
/* Table: DeviceSeries5RTU                                      */
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
/* Table: DeviceTypeCommand                                     */
/*==============================================================*/
create table DeviceTypeCommand (
   DeviceCommandID      numeric              not null,
   CommandID            numeric              not null,
   DeviceType           varchar(32)          not null,
   DisplayOrder         numeric              not null,
   VisibleFlag          char(1)              not null
)
go


INSERT INTO DEVICETYPECOMMAND VALUES (0, 0, 'System', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-1, 0, 'CICustomer', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-2, 0, 'Davis Weather', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-3, 0, 'Fulcrum', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-4, 0, 'Landis-Gyr S4', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-5, 0, 'MCT Broadcast', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-6, 0, 'Sixnet', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-7, 0, 'Tap Terminal', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-8, 0, 'VersacomSerial', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-9, 0, 'SA205Serial', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-10, 0, 'SA305Serial', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-11, 0, 'CollectionGroup', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-12, 0, 'TestCollectionGroup', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-13, -1, 'LMT-2', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-14, -2, 'LMT-2', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-15, -3, 'LMT-2', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-16, -4, 'LMT-2', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-17, -5, 'LMT-2', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-18, -6, 'LMT-2', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-19, -7, 'LMT-2', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-20, -8, 'LMT-2', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-21, -9, 'LMT-2', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-22, -10, 'LMT-2', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-23, -27, 'Macro Group', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-24, -28, 'Macro Group', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-25, -29, 'Alpha A1', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-26, -29, 'Alpha Power Plus', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-27, -30, 'CBC 6510', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-28, -31, 'CBC 6510', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-29, -32, 'CBC 6510', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-30, -33, 'CBC 6510', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-31, -30, 'CBC FP-2800', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-32, -31, 'CBC FP-2800', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-33, -32, 'CBC FP-2800', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-34, -33, 'CBC FP-2800', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-35, -30, 'CBC Versacom', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-36, -31, 'CBC Versacom', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-37, -32, 'CBC Versacom', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-38, -33, 'CBC Versacom', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-39, -30, 'Cap Bank', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-40, -31, 'Cap Bank', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-41, -32, 'Cap Bank', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-42, -33, 'Cap Bank', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-43, -34, 'CCU-710A', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-44, -35, 'CCU-710A', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-45, -36, 'CCU-710A', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-46, -34, 'CCU-711', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-47, -35, 'CCU-711', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-48, -36, 'CCU-711', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-49, -37, 'CCU-711', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-50, -38, 'Emetcon Group', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-51, -39, 'Emetcon Group', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-52, -40, 'Emetcon Group', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-53, -41, 'Emetcon Group', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-54, -42, 'Emetcon Group', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-55, -43, 'Emetcon Group', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-56, -27, 'Expresscom Group', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-57, -28, 'Expresscom Group', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-58, -27, 'Golay Group', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-59, -28, 'Golay Group', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-60, -27, 'Macro Group', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-61, -28, 'Macro Group', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-62, -27, 'Ripple Group', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-63, -28, 'Ripple Group', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-64, -27, 'SA-205 Group', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-65, -28, 'SA-205 Group', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-66, -27, 'SA-305 Group', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-67, -28, 'SA-305 Group', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-68, -27, 'SA-Digital Group', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-69, -28, 'SA-Digital Group', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-70, -27, 'Versacom Group', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-71, -28, 'Versacom Group', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-72, -44, 'Versacom Group', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-73, -45, 'Versacom Group', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-74, -46, 'Versacom Group', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-75, -47, 'Versacom Group', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-76, -48, 'Versacom Group', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-77, -49, 'Versacom Group', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-78, -34, 'TCU-5000', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-79, -35, 'TCU-5000', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-80, -36, 'TCU-5000', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-81, -34, 'TCU-5500', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-82, -35, 'TCU-5500', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-83, -36, 'TCU-5500', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-84, -52, 'Repeater 800', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-85, -3, 'Repeater 800', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-86, -53, 'Repeater 800', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-87, -54, 'Repeater 800', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-88, -52, 'Repeater', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-89, -3, 'Repeater', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-90, -53, 'Repeater', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-91, -54, 'Repeater', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-92, -34, 'RTU-DART', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-93, -35, 'RTU-DART', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-94, -36, 'RTU-DART', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-95, -34, 'RTU-DNP', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-96, -35, 'RTU-DNP', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-97, -36, 'RTU-DNP', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-98, -34, 'RTU-ILEX', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-99, -35, 'RTU-ILEX', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-100, -36, 'RTU-ILEX', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-101, -34, 'RTU-WELCO', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-102, -35, 'RTU-WELCO', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-103, -36, 'RTU-WELCO', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-104, -55, 'ION-7330', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-105, -56, 'ION-7330', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-106, -57, 'ION-7330', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-107, -55, 'ION-7700', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-108, -56, 'ION-7700', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-109, -57, 'ION-7700', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-110, -55, 'ION-8300', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-111, -56, 'ION-8300', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-112, -57, 'ION-8300', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-113, -34, 'LCU-415', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-114, -35, 'LCU-415', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-115, -36, 'LCU-415', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-116, -34, 'LCU-EASTRIVER', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-117, -35, 'LCU-EASTRIVER', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-118, -36, 'LCU-EASTRIVER', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-119, -34, 'LCU-LG', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-120, -35, 'LCU-LG', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-121, -36, 'LCU-LG', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-122, -34, 'LCU-T3026', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-123, -35, 'LCU-T3026', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-124, -36, 'LCU-T3026', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-125, -63, 'ExpresscomSerial', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-126, -64, 'ExpresscomSerial', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-127, -65, 'ExpresscomSerial', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-128, -66, 'ExpresscomSerial', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-129, -67, 'ExpresscomSerial', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-130, -68, 'ExpresscomSerial', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-131, -69, 'ExpresscomSerial', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-132, -70, 'LCRSerial', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-133, -71, 'LCRSerial', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-134, -72, 'LCRSerial', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-135, -73, 'LCRSerial', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-136, -74, 'LCRSerial', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-137, -46, 'LCRSerial', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-138, -47, 'LCRSerial', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-139, -75, 'LCRSerial', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-140, -76, 'LCRSerial', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-141, -77, 'LCRSerial', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-142, -78, 'LCRSerial', 11, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-143, -79, 'LCRSerial', 12, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-144, -80, 'LCRSerial', 13, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-145, -49, 'LCRSerial', 14, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-146, -48, 'LCRSerial', 15, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-147, -1, 'MCT-210', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-148, -2, 'MCT-210', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-149, -3, 'MCT-210', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-150, -4, 'MCT-210', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-151, -5, 'MCT-210', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-152, -6, 'MCT-210', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-153, -7, 'MCT-210', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-154, -8, 'MCT-210', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-155, -9, 'MCT-210', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-156, -10, 'MCT-210', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-157, -1, 'MCT-213', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-158, -2, 'MCT-213', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-159, -3, 'MCT-213', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-160, -4, 'MCT-213', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-161, -5, 'MCT-213', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-162, -6, 'MCT-213', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-163, -7, 'MCT-213', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-164, -8, 'MCT-213', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-165, -9, 'MCT-213', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-166, -10, 'MCT-213', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-167, -11, 'MCT-213', 11, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-168, -12, 'MCT-213', 12, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-169, -13, 'MCT-213', 13, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-170, -1, 'MCT-240', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-171, -2, 'MCT-240', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-172, -3, 'MCT-240', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-173, -4, 'MCT-240', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-174, -5, 'MCT-240', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-175, -6, 'MCT-240', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-176, -7, 'MCT-240', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-177, -8, 'MCT-240', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-178, -9, 'MCT-240', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-179, -10, 'MCT-240', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-180, -15, 'MCT-240', 11, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-181, -18, 'MCT-240', 12, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-182, -19, 'MCT-240', 13, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-183, -1, 'MCT-248', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-184, -2, 'MCT-248', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-185, -3, 'MCT-248', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-186, -4, 'MCT-248', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-187, -5, 'MCT-248', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-188, -6, 'MCT-248', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-189, -7, 'MCT-248', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-190, -8, 'MCT-248', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-191, -9, 'MCT-248', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-192, -10, 'MCT-248', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-193, -15, 'MCT-248', 11, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-194, -18, 'MCT-248', 12, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-195, -19, 'MCT-248', 13, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-196, -1, 'MCT-250', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-197, -2, 'MCT-250', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-198, -3, 'MCT-250', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-199, -4, 'MCT-250', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-200, -5, 'MCT-250', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-201, -6, 'MCT-250', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-202, -7, 'MCT-250', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-203, -8, 'MCT-250', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-204, -9, 'MCT-250', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-205, -10, 'MCT-250', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-206, -14, 'MCT-250', 11, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-207, -15, 'MCT-250', 12, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-208, -18, 'MCT-250', 13, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-209, -19, 'MCT-250', 14, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-210, -1, 'MCT-310', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-211, -2, 'MCT-310', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-212, -3, 'MCT-310', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-213, -4, 'MCT-310', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-214, -5, 'MCT-310', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-215, -6, 'MCT-310', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-216, -7, 'MCT-310', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-217, -8, 'MCT-310', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-218, -9, 'MCT-310', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-219, -10, 'MCT-310', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-220, -1, 'MCT-310CT', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-221, -2, 'MCT-310CT', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-222, -3, 'MCT-310CT', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-223, -4, 'MCT-310CT', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-224, -5, 'MCT-310CT', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-225, -6, 'MCT-310CT', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-226, -7, 'MCT-310CT', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-227, -8, 'MCT-310CT', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-228, -9, 'MCT-310CT', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-229, -10, 'MCT-310CT', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-230, -1, 'MCT-310IDL', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-231, -2, 'MCT-310IDL', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-232, -3, 'MCT-310IDL', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-233, -4, 'MCT-310IDL', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-234, -5, 'MCT-310IDL', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-235, -6, 'MCT-310IDL', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-236, -7, 'MCT-310IDL', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-237, -8, 'MCT-310IDL', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-238, -9, 'MCT-310IDL', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-239, -10, 'MCT-310IDL', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-240, -1, 'MCT-310ID', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-241, -2, 'MCT-310ID', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-242, -3, 'MCT-310ID', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-243, -4, 'MCT-310ID', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-244, -5, 'MCT-310ID', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-245, -6, 'MCT-310ID', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-246, -7, 'MCT-310ID', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-247, -8, 'MCT-310ID', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-248, -9, 'MCT-310ID', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-249, -10, 'MCT-310ID', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-250, -11, 'MCT-310ID', 11, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-251, -12, 'MCT-310ID', 12, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-252, -13, 'MCT-310ID', 13, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-253, -1, 'MCT-310IL', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-254, -2, 'MCT-310IL', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-255, -3, 'MCT-310IL', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-256, -4, 'MCT-310IL', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-257, -5, 'MCT-310IL', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-258, -6, 'MCT-310IL', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-259, -7, 'MCT-310IL', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-260, -8, 'MCT-310IL', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-261, -9, 'MCT-310IL', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-262, -10, 'MCT-310IL', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-263, -15, 'MCT-310IL', 11, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-264, -18, 'MCT-310IL', 12, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-265, -19, 'MCT-310IL', 13, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-266, -1, 'MCT-318', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-267, -2, 'MCT-318', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-268, -3, 'MCT-318', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-269, -4, 'MCT-318', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-270, -5, 'MCT-318', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-271, -6, 'MCT-318', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-272, -7, 'MCT-318', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-273, -8, 'MCT-318', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-274, -9, 'MCT-318', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-275, -10, 'MCT-318', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-276, -14, 'MCT-318', 11, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-277, -1, 'MCT-318L', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-278, -2, 'MCT-318L', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-279, -3, 'MCT-318L', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-280, -4, 'MCT-318L', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-281, -5, 'MCT-318L', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-282, -6, 'MCT-318L', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-283, -7, 'MCT-318L', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-284, -8, 'MCT-318L', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-285, -9, 'MCT-318L', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-286, -10, 'MCT-318L', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-287, -14, 'MCT-318L', 11, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-288, -1, 'MCT-360', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-289, -2, 'MCT-360', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-290, -3, 'MCT-360', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-291, -4, 'MCT-360', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-292, -5, 'MCT-360', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-293, -6, 'MCT-360', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-294, -7, 'MCT-360', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-295, -8, 'MCT-360', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-296, -9, 'MCT-360', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-297, -10, 'MCT-360', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-298, -14, 'MCT-360', 11, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-299, -26, 'MCT-360', 12, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-300, -1, 'MCT-370', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-301, -2, 'MCT-370', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-302, -3, 'MCT-370', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-303, -4, 'MCT-370', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-304, -5, 'MCT-370', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-305, -6, 'MCT-370', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-306, -7, 'MCT-370', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-307, -8, 'MCT-370', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-308, -9, 'MCT-370', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-309, -10, 'MCT-370', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-310, -14, 'MCT-370', 11, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-311, -20, 'MCT-370', 12, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-312, -21, 'MCT-370', 13, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-313, -22, 'MCT-370', 14, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-314, -23, 'MCT-370', 15, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-315, -24, 'MCT-370', 16, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-316, -25, 'MCT-370', 17, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-317, -26, 'MCT-370', 18, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-318, -1, 'MCT-410IL', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-319, -81, 'MCT-410IL', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-320, -3, 'MCT-410IL', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-321, -6, 'MCT-410IL', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-322, -34, 'MCT-410IL', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-323, -82, 'MCT-410IL', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-324, -18, 'MCT-410IL', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-325, -19, 'MCT-410IL', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-326, -83, 'MCT-410IL', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-327, -84, 'MCT-410IL', 10, 'Y');
alter table DeviceTypeCommand
   add constraint PK_DEVICETYPECOMMAND primary key  (DeviceCommandID)
go


/*==============================================================*/
/* Table: DeviceVerification                                    */
/*==============================================================*/
create table DeviceVerification (
   ReceiverID           numeric              not null,
   TransmitterID        numeric              not null,
   ResendOnFail         char(1)              not null,
   Disable              char(1)              not null
)
go


alter table DeviceVerification
   add constraint PK_DEVICEVERIFICATION primary key  (ReceiverID, TransmitterID)
go


/*==============================================================*/
/* Table: DeviceWindow                                          */
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
/* Table: DynamicCCCapBank                                      */
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
/* Table: DynamicCCFeeder                                       */
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
/* Table: DynamicCCSubstationBus                                */
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
/* Table: DynamicCalcHistorical                                 */
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
/* Table: DynamicLMControlArea                                  */
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
/* Table: DynamicLMControlAreaTrigger                           */
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
/* Table: DynamicLMControlHistory                               */
/*==============================================================*/
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
)
go


alter table DynamicLMControlHistory
   add constraint PK_DYNLMCONTROLHISTORY primary key  (PAObjectID)
go


/*==============================================================*/
/* Table: DynamicLMGroup                                        */
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
   ControlCompleteTime  datetime             not null,
   NextControlTime      datetime             not null,
   InternalState        numeric              not null
)
go


alter table DynamicLMGroup
   add constraint PK_DYNAMICLMGROUP primary key  (DeviceID, LMProgramID)
go


/*==============================================================*/
/* Table: DynamicLMProgram                                      */
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
/* Table: DynamicLMProgramDirect                                */
/*==============================================================*/
create table DynamicLMProgramDirect (
   DeviceID             numeric              not null,
   CurrentGearNumber    numeric              not null,
   LastGroupControlled  numeric              not null,
   StartTime            datetime             not null,
   StopTime             datetime             not null,
   TimeStamp            datetime             not null,
   DailyOps             numeric              not null,
   NotifyTime           datetime             not null,
   StartedRampingOut    datetime             not null
)
go


alter table DynamicLMProgramDirect
   add constraint PK_DYNAMICLMPROGRAMDIRECT primary key  (DeviceID)
go


/*==============================================================*/
/* Table: DynamicPAOStatistics                                  */
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
/* Table: DynamicPointAlarming                                  */
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
/* Table: DynamicTags                                           */
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
/* Table: DynamicVerification                                   */
/*==============================================================*/
create table DynamicVerification (
   LogID                numeric              not null,
   TimeArrival          datetime             not null,
   ReceiverID           numeric              not null,
   TransmitterID        numeric              not null,
   Command              varchar(256)         not null,
   Code                 varchar(128)         not null,
   CodeSequence         numeric              not null,
   Received             char(1)              not null,
   CodeStatus           varchar(32)          not null
)
go


alter table DynamicVerification
   add constraint PK_DYNAMICVERIFICATION primary key  (LogID)
go


/*==============================================================*/
/* Index: Index_DYNVER_CS                                       */
/*==============================================================*/
create   index Index_DYNVER_CS on DynamicVerification (
CodeSequence
)
go


/*==============================================================*/
/* Index: Indx_DYNV_TIME                                        */
/*==============================================================*/
create   index Indx_DYNV_TIME on DynamicVerification (
TimeArrival
)
go


/*==============================================================*/
/* Table: EnergyCompany                                         */
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
/* Table: EnergyCompanyCustomerList                             */
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
/* Table: EnergyCompanyOperatorLoginList                        */
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
/* Table: FDRInterface                                          */
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

insert into fdrinterface values (16,'LODESTAR_STD','Receive','f');
insert into fdrinterface values (17,'LODESTAR_ENH','Receive','f');
insert into fdrinterface values (18, 'DSM2FILEIN', 'Receive,Receive for control', 'f');
insert into FDRInterface values (19, 'XA21LM','Receive,Send', 't' );
alter table FDRInterface
   add constraint PK_FDRINTERFACE primary key  (InterfaceID)
go


/*==============================================================*/
/* Table: FDRInterfaceOption                                    */
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
insert into FDRInterfaceOption values(9, 'Client',1,'Text','(none)');
insert into FDRInterfaceOption values(10, 'Point',1,'Text','(none)');
insert into FDRInterfaceOption values(11, 'Point', 1, 'Text', '(none)' );
insert into FDRInterfaceOption values(11, 'Interval (sec)', 2, 'Text', '(none)' );
insert into FDRInterfaceOption values(12, 'Point ID',1,'Text','(none)');
insert into FDRInterfaceOption values(13, 'Point ID',1,'Text','(none)');

insert into fdrinterfaceoption values(16, 'Customer',1,'Text','(none)');
insert into fdrinterfaceoption values(16, 'Channel',2,'Text','(none)');
insert into fdrinterfaceoption values(16, 'DrivePath',3,'Text','(none)');
insert into fdrinterfaceoption values(16, 'Filename',4,'Text','(none)');
insert into fdrinterfaceoption values(17, 'Customer',1,'Text','(none)');
insert into fdrinterfaceoption values(17, 'Channel',2,'Text','(none)');
insert into fdrinterfaceoption values(17, 'DrivePath',3,'Text','(none)');
insert into fdrinterfaceoption values(17, 'Filename',4,'Text','(none)');
insert into fdrinterfaceoption values(18, 'Option Number', 1, 'Combo', '1');
insert into fdrinterfaceoption values(18, 'Point ID', 2, 'Text', '(none)');
insert into FDRInterfaceOption values(19, 'Translation', 1, 'Text', '(none)' );
alter table FDRInterfaceOption
   add constraint PK_FDRINTERFACEOPTION primary key  (InterfaceID, Ordering)
go


/*==============================================================*/
/* Table: FDRTRANSLATION                                        */
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
   add constraint PK_FDRTrans primary key  (POINTID, DIRECTIONTYPE, InterfaceType, TRANSLATION)
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
/* Table: GRAPHDATASERIES                                       */
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
/* Table: GRAPHDEFINITION                                       */
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
/* Table: GatewayEndDevice                                      */
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
/* Table: GenericMacro                                          */
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
/* Table: GraphCustomerList                                     */
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
/* Table: HolidaySchedule                                       */
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
/* Table: LMCONTROLAREAPROGRAM                                  */
/*==============================================================*/
create table LMCONTROLAREAPROGRAM (
   DEVICEID             numeric              not null,
   LMPROGRAMDEVICEID    numeric              not null,
   StartPriority        numeric              not null,
   StopPriority         numeric              not null
)
go


alter table LMCONTROLAREAPROGRAM
   add constraint PK_LMCONTROLAREAPROGRAM primary key  (DEVICEID, LMPROGRAMDEVICEID)
go


/*==============================================================*/
/* Table: LMCONTROLAREATRIGGER                                  */
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
/* Table: LMControlArea                                         */
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
/* Table: LMControlHistory                                      */
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
/* Table: LMControlScenarioProgram                              */
/*==============================================================*/
create table LMControlScenarioProgram (
   ScenarioID           numeric              not null,
   ProgramID            numeric              not null,
   StartOffset          numeric              not null,
   StopOffset           numeric              not null,
   StartGear            numeric              not null
)
go


alter table LMControlScenarioProgram
   add constraint PK_LMCONTROLSCENARIOPROGRAM primary key  (ScenarioID, ProgramID)
go


/*==============================================================*/
/* Table: LMCurtailCustomerActivity                             */
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
/* Table: LMCurtailProgramActivity                              */
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
/* Table: LMDirectCustomerList                                  */
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
/* Table: LMDirectNotifGrpList                                  */
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
/* Table: LMEnergyExchangeCustomerList                          */
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
/* Table: LMEnergyExchangeCustomerReply                         */
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
/* Table: LMEnergyExchangeHourlyCustomer                        */
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
/* Table: LMEnergyExchangeHourlyOffer                           */
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
/* Table: LMEnergyExchangeOfferRevision                         */
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
/* Table: LMEnergyExchangeProgramOffer                          */
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
/* Table: LMGroup                                               */
/*==============================================================*/
create table LMGroup (
   DeviceID             numeric              not null,
   KWCapacity           float                not null
)
go


insert into lmgroup values( 0, 0 );
alter table LMGroup
   add constraint PK_LMGROUP primary key  (DeviceID)
go


/*==============================================================*/
/* Table: LMGroupEmetcon                                        */
/*==============================================================*/
create table LMGroupEmetcon (
   DEVICEID             numeric              not null,
   GOLDADDRESS          numeric              not null,
   SILVERADDRESS        numeric              not null,
   ADDRESSUSAGE         char(1)              not null,
   RELAYUSAGE           char(1)              not null,
   ROUTEID              numeric              not null
)
go


alter table LMGroupEmetcon
   add constraint PK_LMGROUPEMETCON primary key  (DEVICEID)
go


/*==============================================================*/
/* Table: LMGroupExpressCom                                     */
/*==============================================================*/
create table LMGroupExpressCom (
   LMGroupID            numeric              not null,
   RouteID              numeric              not null,
   SerialNumber         varchar(10)          not null,
   ServiceProviderID    numeric              not null,
   GeoID                numeric              not null,
   SubstationID         numeric              not null,
   FeederID             numeric              not null,
   ZipID                numeric              not null,
   UserID               numeric              not null,
   ProgramID            numeric              not null,
   SplinterID           numeric              not null,
   AddressUsage         varchar(10)          not null,
   RelayUsage           char(15)             not null
)
go


alter table LMGroupExpressCom
   add constraint PK_LMGROUPEXPRESSCOM primary key  (LMGroupID)
go


/*==============================================================*/
/* Table: LMGroupExpressComAddress                              */
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
/* Table: LMGroupMCT                                            */
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
/* Table: LMGroupPoint                                          */
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
/* Table: LMGroupRipple                                         */
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
/* Table: LMGroupSA205105                                       */
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
/* Table: LMGroupSA305                                          */
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
/* Table: LMGroupSASimple                                       */
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
/* Table: LMGroupVersacom                                       */
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
/* Table: LMMacsScheduleCustomerList                            */
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
/* Table: LMPROGRAM                                             */
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
/* Table: LMProgramConstraints                                  */
/*==============================================================*/
create table LMProgramConstraints (
   ConstraintID         numeric              not null,
   ConstraintName       varchar(60)          not null,
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


insert into LMProgramConstraints values (0, 'Default Constraint', 'YYYYYYYN', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
alter table LMProgramConstraints
   add constraint PK_PRGCONSTR primary key  (ConstraintID)
go


/*==============================================================*/
/* Table: LMProgramControlWindow                                */
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
/* Table: LMProgramCurtailCustomerList                          */
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
/* Table: LMProgramCurtailment                                  */
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
/* Table: LMProgramDirect                                       */
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
/* Table: LMProgramDirectGear                                   */
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
/* Table: LMProgramDirectGroup                                  */
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
/* Table: LMProgramEnergyExchange                               */
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
/* Table: LMThermoStatGear                                      */
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
/* Table: LOGIC                                                 */
/*==============================================================*/
create table LOGIC (
   LOGICID              numeric              not null,
   LOGICNAME            varchar(20)          not null,
   PERIODICRATE         numeric              not null,
   STATEFLAG            varchar(10)          not null,
   SCRIPTNAME           varchar(20)          not null
)
go


alter table LOGIC
   add constraint SYS_C0013445 primary key  (LOGICID)
go


/*==============================================================*/
/* Table: MACROROUTE                                            */
/*==============================================================*/
create table MACROROUTE (
   ROUTEID              numeric              not null,
   SINGLEROUTEID        numeric              not null,
   ROUTEORDER           numeric              not null
)
go


alter table MACROROUTE
   add constraint PK_MACROROUTE primary key  (ROUTEID, ROUTEORDER)
go


/*==============================================================*/
/* Table: MACSchedule                                           */
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
   Template             numeric              null
)
go


alter table MACSchedule
   add constraint PK_MACSCHEDULE primary key  (ScheduleID)
go


/*==============================================================*/
/* Table: MACSimpleSchedule                                     */
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
/* Table: MCTBroadCastMapping                                   */
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
/* Table: MCTConfig                                             */
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
   Ke3                  float                not null,
   DisplayDigits        numeric              not null
)
go


alter table MCTConfig
   add constraint PK_MCTCONFIG primary key  (ConfigID)
go


/*==============================================================*/
/* Table: MCTConfigMapping                                      */
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
/* Table: NotificationDestination                               */
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
/* Table: NotificationGroup                                     */
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
/* Table: OperatorLoginGraphList                                */
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
/* Table: PAOExclusion                                          */
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
/* Table: PAOowner                                              */
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
/* Table: POINT                                                 */
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
/* Table: POINTACCUMULATOR                                      */
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
/* Table: POINTANALOG                                           */
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
/* Table: POINTLIMITS                                           */
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
/* Table: POINTSTATUS                                           */
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
/* Table: POINTUNIT                                             */
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
/* Table: PORTDIALUPMODEM                                       */
/*==============================================================*/
create table PORTDIALUPMODEM (
   PORTID               numeric              not null,
   MODEMTYPE            varchar(30)          not null,
   INITIALIZATIONSTRING varchar(50)          not null,
   PREFIXNUMBER         varchar(10)          not null,
   SUFFIXNUMBER         varchar(10)          not null
)
go


alter table PORTDIALUPMODEM
   add constraint PK_PORTDIALUPMODEM primary key  (PORTID)
go


/*==============================================================*/
/* Table: PORTLOCALSERIAL                                       */
/*==============================================================*/
create table PORTLOCALSERIAL (
   PORTID               numeric              not null,
   PHYSICALPORT         varchar(8)           not null
)
go


alter table PORTLOCALSERIAL
   add constraint PK_PORTLOCALSERIAL primary key  (PORTID)
go


/*==============================================================*/
/* Table: PORTRADIOSETTINGS                                     */
/*==============================================================*/
create table PORTRADIOSETTINGS (
   PORTID               numeric              not null,
   RTSTOTXWAITSAMED     numeric              not null,
   RTSTOTXWAITDIFFD     numeric              not null,
   RADIOMASTERTAIL      numeric              not null,
   REVERSERTS           numeric              not null
)
go


alter table PORTRADIOSETTINGS
   add constraint PK_PORTRADIOSETTINGS primary key  (PORTID)
go


/*==============================================================*/
/* Table: PORTSETTINGS                                          */
/*==============================================================*/
create table PORTSETTINGS (
   PORTID               numeric              not null,
   BAUDRATE             numeric              not null,
   CDWAIT               numeric              not null,
   LINESETTINGS         varchar(8)           not null
)
go


alter table PORTSETTINGS
   add constraint PK_PORTSETTINGS primary key  (PORTID)
go


/*==============================================================*/
/* Table: PORTTERMINALSERVER                                    */
/*==============================================================*/
create table PORTTERMINALSERVER (
   PORTID               numeric              not null,
   IPADDRESS            varchar(64)          not null,
   SOCKETPORTNUMBER     numeric              not null
)
go


alter table PORTTERMINALSERVER
   add constraint PK_PORTTERMINALSERVER primary key  (PORTID)
go


/*==============================================================*/
/* Table: PointAlarming                                         */
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
/* Table: PortTiming                                            */
/*==============================================================*/
create table PortTiming (
   PORTID               numeric              not null,
   PRETXWAIT            numeric              not null,
   RTSTOTXWAIT          numeric              not null,
   POSTTXWAIT           numeric              not null,
   RECEIVEDATAWAIT      numeric              not null,
   EXTRATIMEOUT         numeric              not null
)
go


alter table PortTiming
   add constraint PK_PORTTIMING primary key  (PORTID)
go


/*==============================================================*/
/* Table: RAWPOINTHISTORY                                       */
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
/* Table: RepeaterRoute                                         */
/*==============================================================*/
create table RepeaterRoute (
   ROUTEID              numeric              not null,
   DEVICEID             numeric              not null,
   VARIABLEBITS         numeric              not null,
   REPEATERORDER        numeric              not null
)
go


alter table RepeaterRoute
   add constraint PK_REPEATERROUTE primary key  (ROUTEID, DEVICEID)
go


/*==============================================================*/
/* Table: Route                                                 */
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
/* Table: STATE                                                 */
/*==============================================================*/
create table STATE (
   STATEGROUPID         numeric              not null,
   RAWSTATE             numeric              not null,
   TEXT                 varchar(20)          not null,
   FOREGROUNDCOLOR      numeric              not null,
   BACKGROUNDCOLOR      numeric              not null,
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
/* Table: STATEGROUP                                            */
/*==============================================================*/
create table STATEGROUP (
   STATEGROUPID         numeric              not null,
   NAME                 varchar(20)          not null,
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
/* Table: SYSTEMLOG                                             */
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
/* Table: SeasonSchedule                                        */
/*==============================================================*/
create table SeasonSchedule (
   ScheduleID           numeric              not null,
   ScheduleName         varchar(40)          not null
)
go


insert into SeasonSchedule values( 0, 'Empty Schedule' );
alter table SeasonSchedule
   add constraint PK_SEASONSCHEDULE primary key  (ScheduleID)
go


/*==============================================================*/
/* Table: TEMPLATE                                              */
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
/* Table: TEMPLATECOLUMNS                                       */
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
/* Table: TOUDay                                                */
/*==============================================================*/
create table TOUDay (
   TOUDayID             numeric              not null,
   TOUDayName           varchar(32)          not null
)
go


alter table TOUDay
   add constraint PK_TOUDAY primary key  (TOUDayID)
go


/*==============================================================*/
/* Table: TOUDayMapping                                         */
/*==============================================================*/
create table TOUDayMapping (
   TOUScheduleID        numeric              not null,
   TOUDayID             numeric              not null,
   TOUDayOffset         numeric              not null
)
go


alter table TOUDayMapping
   add constraint PK_TOUDAYMAPPING primary key  (TOUScheduleID, TOUDayOffset)
go


/*==============================================================*/
/* Table: TOUDayRateSwitches                                    */
/*==============================================================*/
create table TOUDayRateSwitches (
   TOURateSwitchID      numeric              not null,
   SwitchRate           varchar(4)           not null,
   SwitchOffset         numeric              not null,
   TOUDayID             numeric              not null
)
go


alter table TOUDayRateSwitches
   add constraint PK_TOURATESWITCH primary key  (TOURateSwitchID)
go


/*==============================================================*/
/* Table: TOUSchedule                                           */
/*==============================================================*/
create table TOUSchedule (
   TOUScheduleID        numeric              not null,
   TOUScheduleName      varchar(32)          not null,
   TOUDefaultRate       varchar(4)           not null
)
go


insert into TOUSchedule values (0, '(none)', 0);
alter table TOUSchedule
   add constraint PK_TOUSCHEDULE primary key  (TOUScheduleID)
go


/*==============================================================*/
/* Table: TagLog                                                */
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
/* Table: Tags                                                  */
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
/* Table: UNITMEASURE                                           */
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
/* Table: UserPAOowner                                          */
/*==============================================================*/
create table UserPAOowner (
   UserID               numeric              not null,
   PaoID                numeric              not null
)
go


alter table UserPAOowner
   add constraint PK_USERPAOOWNER primary key  (UserID, PaoID)
go


/*==============================================================*/
/* Table: VersacomRoute                                         */
/*==============================================================*/
create table VersacomRoute (
   ROUTEID              numeric              not null,
   UTILITYID            numeric              not null,
   SECTIONADDRESS       numeric              not null,
   CLASSADDRESS         numeric              not null,
   DIVISIONADDRESS      numeric              not null,
   BUSNUMBER            numeric              not null,
   AMPCARDSET           numeric              not null
)
go


alter table VersacomRoute
   add constraint PK_VERSACOMROUTE primary key  (ROUTEID)
go


/*==============================================================*/
/* Table: YukonGroup                                            */
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
insert into yukongroup values (-303,'STARS Operators Grp','The default group for STARS operators');
insert into yukongroup values (-304,'STARS Residential Customers Grp','The default group for STARS residential customers');
alter table YukonGroup
   add constraint PK_YUKONGROUP primary key  (GroupID)
go


/*==============================================================*/
/* Table: YukonGroupRole                                        */
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
insert into YukonGroupRole values(-16,-1,-1,-1015,'(none)');
insert into YukonGroupRole values(-17,-1,-1,-1016,'(none)');
insert into YukonGroupRole values(-18,-1,-1,-1017,'(none)');

insert into YukonGroupRole values(-70,-1,-5,-1400,'(none)');
insert into YukonGroupRole values(-71,-1,-5,-1401,'(none)');
insert into YukonGroupRole values(-72,-1,-5,-1402,'(none)');

insert into YukonGroupRole values(-85,-1,-4,-1300,'(none)');
insert into YukonGroupRole values(-86,-1,-4,-1301,'(none)');
insert into YukonGroupRole values(-87,-1,-4,-1302,'(none)');
insert into YukonGroupRole values(-88,-1,-4,-1303,'(none)');
insert into YukonGroupRole values(-89,-1,-4,-1304,'(none)');
insert into YukonGroupRole values(-90,-1,-4,-1305,'(none)');


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

insert into yukongrouprole values (-551,-300,-400,-40051,'false');
insert into yukongrouprole values (-552,-300,-400,-40052,'false');
insert into yukongrouprole values (-554,-300,-400,-40054,'false');
insert into yukongrouprole values (-555,-300,-400,-40055,'(none)');

insert into yukongrouprole values (-600,-300,-400,-40100,'(none)');
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
insert into yukongrouprole values (-635,-300,-400,-40135,'(none)');
insert into yukongrouprole values (-636,-300,-400,-40136,'(none)');
insert into yukongrouprole values (-637,-300,-400,-40137,'(none)');
insert into yukongrouprole values (-638,-300,-400,-40138,'(none)');
insert into yukongrouprole values (-639,-300,-400,-40139,'(none)');

insert into yukongrouprole values (-650,-300,-400,-40150,'(none)');
insert into yukongrouprole values (-651,-300,-400,-40151,'(none)');
insert into yukongrouprole values (-652,-300,-400,-40152,'(none)');
insert into yukongrouprole values (-653,-300,-400,-40153,'(none)');
insert into yukongrouprole values (-654,-300,-400,-40154,'(none)');
insert into yukongrouprole values (-655,-300,-400,-40155,'(none)');
insert into yukongrouprole values (-656,-300,-400,-40156,'(none)');
insert into yukongrouprole values (-657,-300,-400,-40157,'(none)');
insert into yukongrouprole values (-658,-300,-400,-40158,'(none)');
insert into yukongrouprole values (-659,-300,-400,-40159,'(none)');

insert into yukongrouprole values (-670,-300,-400,-40170,'(none)');
insert into yukongrouprole values (-671,-300,-400,-40171,'(none)');
insert into yukongrouprole values (-672,-300,-400,-40172,'(none)');
insert into yukongrouprole values (-673,-300,-400,-40173,'(none)');

insert into yukongrouprole values (-680,-300,-400,-40180,'(none)');
insert into yukongrouprole values (-681,-300,-400,-40181,'(none)');

insert into yukongrouprole values (-690,-300,-400,-40190,'(none)');
insert into yukongrouprole values (-691,-300,-400,-40191,'(none)');
insert into yukongrouprole values (-692,-300,-400,-40192,'(none)');
insert into yukongrouprole values (-693,-300,-400,-40193,'(none)');
insert into yukongrouprole values (-694,-300,-400,-40194,'(none)');
insert into yukongrouprole values (-695,-300,-400,-40195,'(none)');
insert into yukongrouprole values (-696,-300,-400,-40196,'(none)');

insert into yukongrouprole values (-700,-301,-108,-10800,'/operator/Operations.jsp');
insert into yukongrouprole values (-702,-301,-108,-10802,'(none)');
insert into yukongrouprole values (-703,-301,-108,-10803,'(none)');
insert into yukongrouprole values (-704,-301,-108,-10804,'(none)');
insert into yukongrouprole values (-705,-301,-108,-10805,'(none)');
insert into yukongrouprole values (-706,-301,-108,-10806,'(none)');
insert into yukongrouprole values (-707,-301,-108,-10807,'(none)');
insert into yukongrouprole values (-708,-301,-108,-10808,'(none)');

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
insert into yukongrouprole values (-738,-301,-201,-20118,'true');

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
insert into yukongrouprole values (-796,-301,-209,-20905,'(none)');
insert into yukongrouprole values (-797,-301,-209,-20906,'(none)');

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
insert into yukongrouprole values (-835,-301,-201,-20835,'(none)');
insert into yukongrouprole values (-836,-301,-201,-20836,'(none)');
insert into yukongrouprole values (-837,-301,-201,-20837,'(none)');
insert into yukongrouprole values (-838,-301,-201,-20838,'(none)');
insert into yukongrouprole values (-839,-301,-201,-20839,'(none)');
insert into yukongrouprole values (-840,-301,-201,-20840,'(none)');
insert into yukongrouprole values (-841,-301,-201,-20841,'(none)');
insert into yukongrouprole values (-842,-301,-201,-20842,'(none)');
insert into yukongrouprole values (-843,-301,-201,-20843,'(none)');
insert into yukongrouprole values (-844,-301,-201,-20844,'(none)');
insert into yukongrouprole values (-845,-301,-201,-20845,'(none)');

insert into yukongrouprole values (-850,-301,-201,-20850,'(none)');
insert into yukongrouprole values (-851,-301,-201,-20851,'(none)');
insert into yukongrouprole values (-852,-301,-201,-20852,'(none)');
insert into yukongrouprole values (-853,-301,-201,-20853,'(none)');
insert into yukongrouprole values (-854,-301,-201,-20854,'(none)');
insert into yukongrouprole values (-855,-301,-201,-20855,'(none)');
insert into yukongrouprole values (-856,-301,-201,-20856,'(none)');
insert into yukongrouprole values (-857,-301,-201,-20857,'(none)');
insert into yukongrouprole values (-858,-301,-201,-20858,'(none)');
insert into yukongrouprole values (-859,-301,-201,-20859,'(none)');
insert into yukongrouprole values (-860,-301,-201,-20860,'(none)');
insert into yukongrouprole values (-861,-301,-201,-20861,'(none)');
insert into yukongrouprole values (-862,-301,-201,-20862,'(none)');
insert into yukongrouprole values (-863,-301,-201,-20863,'(none)');
insert into yukongrouprole values (-864,-301,-201,-20864,'(none)');

insert into yukongrouprole values (-870,-301,-201,-20870,'(none)');

insert into yukongrouprole values (-880,-301,-201,-20880,'(none)');
insert into yukongrouprole values (-881,-301,-201,-20881,'(none)');
insert into yukongrouprole values (-882,-301,-201,-20882,'(none)');
insert into yukongrouprole values (-883,-301,-201,-20883,'(none)');
insert into yukongrouprole values (-884,-301,-201,-20884,'(none)');
insert into yukongrouprole values (-885,-301,-201,-20885,'(none)');
insert into yukongrouprole values (-886,-301,-201,-20886,'(none)');
insert into yukongrouprole values (-887,-301,-201,-20887,'(none)');
insert into yukongrouprole values (-888,-301,-201,-20888,'(none)');
insert into yukongrouprole values (-889,-301,-201,-20889,'(none)');

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
insert into YukonGroupRole values(-1010,-2,-100,-10010,'00000000');

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
insert into YukonGroupRole values(-1031,-2,-101,-10112,'(none)');

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
insert into YukonGroupRole values (-1153,-2,-400,-40051,'(none)');
insert into YukonGroupRole values (-1154,-2,-400,-40052,'(none)');
insert into YukonGroupRole values (-1155,-2,-400,-40054,'(none)');
insert into YukonGroupRole values (-1156,-2,-400,-40055,'(none)');
insert into YukonGroupRole values (-1157,-2,-400,-40100,'(none)');
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

insert into yukongrouprole values (-2000,-303,-108,-10800,'/operator/Operations.jsp');
insert into yukongrouprole values (-2002,-303,-108,-10802,'(none)');
insert into yukongrouprole values (-2003,-303,-108,-10803,'(none)');
insert into yukongrouprole values (-2004,-303,-108,-10804,'(none)');
insert into yukongrouprole values (-2005,-303,-108,-10805,'(none)');
insert into yukongrouprole values (-2006,-303,-108,-10806,'(none)');
insert into yukongrouprole values (-2007,-303,-108,-10807,'(none)');
insert into yukongrouprole values (-2008,-303,-108,-10808,'(none)');

insert into yukongrouprole values (-2021,-303,-201,-20101,'(none)');
insert into yukongrouprole values (-2022,-303,-201,-20102,'true');
insert into yukongrouprole values (-2023,-303,-201,-20103,'(none)');
insert into yukongrouprole values (-2024,-303,-201,-20104,'(none)');
insert into yukongrouprole values (-2025,-303,-201,-20105,'(none)');
insert into yukongrouprole values (-2026,-303,-201,-20106,'(none)');
insert into yukongrouprole values (-2027,-303,-201,-20107,'(none)');
insert into yukongrouprole values (-2028,-303,-201,-20108,'(none)');
insert into yukongrouprole values (-2029,-303,-201,-20109,'(none)');
insert into yukongrouprole values (-2030,-303,-201,-20110,'(none)');
insert into yukongrouprole values (-2031,-303,-201,-20111,'(none)');
insert into yukongrouprole values (-2032,-303,-201,-20112,'(none)');
insert into yukongrouprole values (-2033,-303,-201,-20113,'(none)');
insert into yukongrouprole values (-2034,-303,-201,-20114,'true');
insert into yukongrouprole values (-2035,-303,-201,-20115,'false');
insert into yukongrouprole values (-2036,-303,-201,-20116,'(none)');
insert into yukongrouprole values (-2037,-303,-201,-20117,'(none)');
insert into yukongrouprole values (-2038,-303,-201,-20118,'(none)');

insert into yukongrouprole values (-2051,-303,-201,-20151,'(none)');
insert into yukongrouprole values (-2052,-303,-201,-20152,'(none)');
insert into yukongrouprole values (-2053,-303,-201,-20153,'(none)');
insert into yukongrouprole values (-2054,-303,-201,-20154,'(none)');
insert into yukongrouprole values (-2055,-303,-201,-20155,'true');
insert into yukongrouprole values (-2056,-303,-201,-20156,'(none)');
insert into yukongrouprole values (-2057,-303,-201,-20157,'(none)');

insert into yukongrouprole values (-2070,-303,-210,-21000,'(none)');
insert into yukongrouprole values (-2071,-303,-210,-21001,'(none)');

insert into yukongrouprole values (-2080,-303,-209,-20900,'(none)');
insert into yukongrouprole values (-2081,-303,-209,-20901,'(none)');
insert into yukongrouprole values (-2082,-303,-209,-20902,'(none)');
insert into yukongrouprole values (-2083,-303,-209,-20903,'(none)');
insert into yukongrouprole values (-2084,-303,-209,-20904,'(none)');
insert into yukongrouprole values (-2085,-303,-209,-20905,'(none)');
insert into yukongrouprole values (-2086,-303,-209,-20906,'(none)');

insert into yukongrouprole values (-2100,-303,-201,-20800,'(none)');
insert into yukongrouprole values (-2101,-303,-201,-20801,'(none)');
insert into yukongrouprole values (-2110,-303,-201,-20810,'(none)');
insert into yukongrouprole values (-2113,-303,-201,-20813,'(none)');
insert into yukongrouprole values (-2114,-303,-201,-20814,'(none)');
insert into yukongrouprole values (-2115,-303,-201,-20815,'(none)');
insert into yukongrouprole values (-2116,-303,-201,-20816,'(none)');
insert into yukongrouprole values (-2119,-303,-201,-20819,'(none)');
insert into yukongrouprole values (-2120,-303,-201,-20820,'(none)');

insert into yukongrouprole values (-2130,-303,-201,-20830,'(none)');
insert into yukongrouprole values (-2131,-303,-201,-20831,'(none)');
insert into yukongrouprole values (-2132,-303,-201,-20832,'(none)');
insert into yukongrouprole values (-2133,-303,-201,-20833,'(none)');
insert into yukongrouprole values (-2134,-303,-201,-20834,'(none)');
insert into yukongrouprole values (-2135,-303,-201,-20835,'(none)');
insert into yukongrouprole values (-2136,-303,-201,-20836,'(none)');
insert into yukongrouprole values (-2137,-303,-201,-20837,'(none)');
insert into yukongrouprole values (-2138,-303,-201,-20838,'(none)');
insert into yukongrouprole values (-2139,-303,-201,-20839,'(none)');
insert into yukongrouprole values (-2140,-303,-201,-20840,'(none)');
insert into yukongrouprole values (-2141,-303,-201,-20841,'(none)');
insert into yukongrouprole values (-2142,-303,-201,-20842,'(none)');
insert into yukongrouprole values (-2143,-303,-201,-20843,'(none)');
insert into yukongrouprole values (-2144,-303,-201,-20844,'(none)');
insert into yukongrouprole values (-2145,-303,-201,-20845,'(none)');

insert into yukongrouprole values (-2150,-303,-201,-20850,'(none)');
insert into yukongrouprole values (-2151,-303,-201,-20851,'(none)');
insert into yukongrouprole values (-2152,-303,-201,-20852,'(none)');
insert into yukongrouprole values (-2153,-303,-201,-20853,'(none)');
insert into yukongrouprole values (-2154,-303,-201,-20854,'(none)');
insert into yukongrouprole values (-2155,-303,-201,-20855,'(none)');
insert into yukongrouprole values (-2156,-303,-201,-20856,'(none)');
insert into yukongrouprole values (-2157,-303,-201,-20857,'(none)');
insert into yukongrouprole values (-2158,-303,-201,-20858,'(none)');
insert into yukongrouprole values (-2159,-303,-201,-20859,'(none)');
insert into yukongrouprole values (-2160,-303,-201,-20860,'(none)');
insert into yukongrouprole values (-2161,-303,-201,-20861,'(none)');
insert into yukongrouprole values (-2162,-303,-201,-20862,'(none)');
insert into yukongrouprole values (-2163,-303,-201,-20863,'(none)');
insert into yukongrouprole values (-2164,-303,-201,-20864,'(none)');

insert into yukongrouprole values (-2170,-303,-201,-20870,'(none)');
insert into yukongrouprole values (-2180,-303,-201,-20880,'(none)');
insert into yukongrouprole values (-2181,-303,-201,-20881,'(none)');
insert into yukongrouprole values (-2182,-303,-201,-20882,'(none)');
insert into yukongrouprole values (-2183,-303,-201,-20883,'(none)');
insert into yukongrouprole values (-2184,-303,-201,-20884,'(none)');
insert into yukongrouprole values (-2185,-303,-201,-20885,'(none)');
insert into yukongrouprole values (-2186,-303,-201,-20886,'(none)');
insert into yukongrouprole values (-2187,-303,-201,-20887,'(none)');
insert into yukongrouprole values (-2188,-303,-201,-20888,'(none)');
insert into yukongrouprole values (-2189,-303,-201,-20889,'(none)');

insert into yukongrouprole values (-2200,-304,-108,-10800,'/user/ConsumerStat/stat/General.jsp');
insert into yukongrouprole values (-2202,-304,-108,-10802,'(none)');
insert into yukongrouprole values (-2203,-304,-108,-10803,'(none)');
insert into yukongrouprole values (-2204,-304,-108,-10804,'(none)');
insert into yukongrouprole values (-2205,-304,-108, -10805,'yukon/DemoHeaderCES.gif');
insert into yukongrouprole values (-2206,-304,-108,-10806,'(none)');
insert into yukongrouprole values (-2207,-304,-108,-10807,'(none)');
insert into yukongrouprole values (-2208,-304,-108,-10808,'(none)');

insert into yukongrouprole values (-2221,-304,-400,-40001,'(none)');
insert into yukongrouprole values (-2222,-304,-400,-40002,'(none)');
insert into yukongrouprole values (-2223,-304,-400,-40003,'(none)');
insert into yukongrouprole values (-2224,-304,-400,-40004,'(none)');
insert into yukongrouprole values (-2225,-304,-400,-40005,'(none)');
insert into yukongrouprole values (-2226,-304,-400,-40006,'(none)');
insert into yukongrouprole values (-2227,-304,-400,-40007,'(none)');
insert into yukongrouprole values (-2228,-304,-400,-40008,'(none)');
insert into yukongrouprole values (-2229,-304,-400,-40009,'(none)');
insert into yukongrouprole values (-2230,-304,-400,-40010,'(none)');

insert into yukongrouprole values (-2251,-304,-400,-40051,'(none)');
insert into yukongrouprole values (-2252,-304,-400,-40052,'(none)');
insert into yukongrouprole values (-2254,-304,-400,-40054,'(none)');
insert into yukongrouprole values (-2255,-304,-400,-40055,'(none)');

insert into yukongrouprole values (-2300,-304,-400,-40100,'(none)');
insert into yukongrouprole values (-2302,-304,-400,-40102,'(none)');
insert into yukongrouprole values (-2310,-304,-400,-40110,'(none)');
insert into yukongrouprole values (-2311,-304,-400,-40111,'(none)');
insert into yukongrouprole values (-2312,-304,-400,-40112,'(none)');
insert into yukongrouprole values (-2313,-304,-400,-40113,'(none)');
insert into yukongrouprole values (-2314,-304,-400,-40114,'(none)');
insert into yukongrouprole values (-2315,-304,-400,-40115,'(none)');
insert into yukongrouprole values (-2316,-304,-400,-40116,'(none)');
insert into yukongrouprole values (-2317,-304,-400,-40117,'(none)');
insert into yukongrouprole values (-2330,-304,-400,-40130,'(none)');
insert into yukongrouprole values (-2331,-304,-400,-40131,'(none)');
insert into yukongrouprole values (-2332,-304,-400,-40132,'(none)');
insert into yukongrouprole values (-2333,-304,-400,-40133,'(none)');
insert into yukongrouprole values (-2334,-304,-400,-40134,'(none)');
insert into yukongrouprole values (-2335,-304,-400,-40135,'(none)');
insert into yukongrouprole values (-2336,-304,-400,-40136,'(none)');
insert into yukongrouprole values (-2337,-304,-400,-40137,'(none)');
insert into yukongrouprole values (-2338,-304,-400,-40138,'(none)');
insert into yukongrouprole values (-2339,-304,-400,-40139,'(none)');

insert into yukongrouprole values (-2350,-304,-400,-40150,'(none)');
insert into yukongrouprole values (-2351,-304,-400,-40151,'(none)');
insert into yukongrouprole values (-2352,-304,-400,-40152,'(none)');
insert into yukongrouprole values (-2353,-304,-400,-40153,'(none)');
insert into yukongrouprole values (-2354,-304,-400,-40154,'(none)');
insert into yukongrouprole values (-2355,-304,-400,-40155,'(none)');
insert into yukongrouprole values (-2356,-304,-400,-40156,'(none)');
insert into yukongrouprole values (-2357,-304,-400,-40157,'(none)');
insert into yukongrouprole values (-2358,-304,-400,-40158,'(none)');
insert into yukongrouprole values (-2359,-304,-400,-40159,'(none)');

insert into yukongrouprole values (-2370,-304,-400,-40170,'(none)');
insert into yukongrouprole values (-2371,-304,-400,-40171,'(none)');
insert into yukongrouprole values (-2372,-304,-400,-40172,'(none)');
insert into yukongrouprole values (-2373,-304,-400,-40173,'(none)');
insert into yukongrouprole values (-2380,-304,-400,-40180,'(none)');
insert into yukongrouprole values (-2381,-304,-400,-40181,'(none)');
insert into yukongrouprole values (-2390,-304,-400,-40190,'(none)');
insert into yukongrouprole values (-2391,-304,-400,-40191,'(none)');
insert into yukongrouprole values (-2392,-304,-400,-40192,'(none)');
insert into yukongrouprole values (-2393,-304,-400,-40193,'(none)');
insert into yukongrouprole values (-2394,-304,-400,-40194,'(none)');
insert into yukongrouprole values (-2395,-304,-400,-40195,'(none)');
insert into yukongrouprole values (-2396,-304,-400,-40196,'(none)');
alter table YukonGroupRole
   add constraint PK_YUKONGRPROLE primary key  (GroupRoleID)
go


/*==============================================================*/
/* Table: YukonImage                                            */
/*==============================================================*/
create table YukonImage (
   ImageID              numeric              not null,
   ImageCategory        varchar(20)          not null,
   ImageName            varchar(80)          not null,
   ImageValue           ntext                null
)
go


insert into YukonImage values( 0, '(none)', '(none)', null );
alter table YukonImage
   add constraint PK_YUKONIMAGE primary key  (ImageID)
go


/*==============================================================*/
/* Table: YukonListEntry                                        */
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

insert into YukonListEntry values (1051,1005,0,'LCR-5000(EXPRESSCOM)',1302);
insert into YukonListEntry values (1052,1005,-1,'LCR-4000',1305);
insert into YukonListEntry values (1053,1005,0,'LCR-3000',1306);
insert into YukonListEntry values (1054,1005,-1,'LCR-2000',1307);
insert into YukonListEntry values (1055,1005,-1,'LCR-1000',1308);
insert into YukonListEntry values (1056,1005,-1,'ExpressStat',1301);
insert into YukonListEntry values (1057,1005,-1,'EnergyPro',3100);
insert into YukonListEntry values (1058,1005,-1,'MCT',1303);
insert into YukonListEntry values (1059,1005,-1,'Commercial ExpressStat',1304);
insert into YukonListEntry values (1060,1005,-1,'SA-205',1309);
insert into YukonListEntry values (1061,1005,-1,'SA-305',1310);
insert into YukonListEntry values (1062,1005,-1,'LCR-5000(VERSACOM)',1311);
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
/* Table: YukonPAObject                                         */
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
/* Table: YukonRole                                             */
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
insert into YukonRole values(-4,'Authentication','Yukon','Settings for using an authentication server to login instead of standard yukon login.');
insert into YukonRole values(-5,'Voice Server','Yukon','Inbound and outbound voice interface.');
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

/* Billing AMR role */
insert into YukonRole values (-500,'Billing','AMR','Access to billing file generation.');

/* Reporting Analysis role */
insert into YukonRole values (-600,'Reporting','Analysis','Access to reports generation.');
insert into YukonRole values (-601,'Trending','Analysis','Access to trending functionality.');

/* Capacitor Control roles */
insert into YukonRole values (-700,'CBC Control','CapBank Control','Allows the user to control change states of the CapControl system .');

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
/* Table: YukonRoleProperty                                     */
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
insert into YukonRoleProperty values(-1013,-1,'stars_preload_data','true','Controls whether the STARS application should preload data into the cache.');
insert into YukonRoleProperty values(-1014,-1,'web_logo','CannonLogo.gif','The logo that is used for the yukon web applications');
insert into YukonRoleProperty values(-1015,-1,'voice_host','127.0.0.1','Name or IP address of the voice server');
insert into YukonRoleProperty values(-1016,-1,'notification_host','127.0.0.1','Name or IP address of the Yukon Notification service');
insert into YukonRoleProperty values(-1017,-1,'notification_port','1515','TCP/IP port of the Yukon Notification service');

/* Energy Company Role Properties */
insert into YukonRoleProperty values(-1100,-2,'admin_email_address','info@cannontech.com','Sender address of emails sent on behalf of energy company, e.g. control odds and opt out notification emails.');
insert into YukonRoleProperty values(-1101,-2,'optout_notification_recipients','(none)','Recipients of the opt out notification email');
insert into YukonRoleProperty values(-1102,-2,'default_time_zone','CST','Default time zone of the energy company');
insert into YukonRoleProperty values(-1105,-2,'customer_group_ids','-300','Group IDs of all the residential customer logins');
insert into YukonRoleProperty values(-1106,-2,'operator_group_ids','-301','Group IDs of all the web client operator logins');
insert into YukonRoleProperty values(-1107,-2,'track_hardware_addressing','false','Controls whether to track the hardware addressing information.');
insert into YukonRoleProperty values(-1108,-2,'single_energy_company','true','Indicates whether this is a single energy company system.');

insert into YukonRoleProperty values(-1300,-4,'server_address','127.0.0.1','Authentication server machine address');
insert into YukonRoleProperty values(-1301,-4,'auth_port','1812','Authentication port.');
insert into YukonRoleProperty values(-1302,-4,'acct_port','1813','Accounting port.');
insert into YukonRoleProperty values(-1303,-4,'secret_key','cti','Client machine secret key value, defined by the server.');
insert into YukonRoleProperty values(-1304,-4,'auth_method','(none)','Authentication method. Possible values are (none) | PAP, [chap, others to follow soon]');
insert into YukonRoleProperty values(-1305,-4,'authentication_mode','Yukon','Authentication mode to use.  Valid values are:   Yukon | Radius');

insert into YukonRoleProperty values(-1400,-5,'voice_app','login','The voice server application that Yukon should use');
insert into YukonRoleProperty values(-1401,-5,'call_timeout','30','The time-out in seconds given to each outbound call');
insert into YukonRoleProperty values(-1402,-5,'call_response_timeout','240','The time-out in seconds given to each outbound call response');


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
insert into YukonRoleProperty values(-10010,-100,'z_optional_product_dev','00000000','This feature is for development purposes only');

/* TDC Role */
insert into YukonRoleProperty values(-10100,-101,'loadcontrol_edit','00000000','(No settings yet)');
insert into YukonRoleProperty values(-10101,-101,'macs_edit','00000CCC','The following settings are valid: CREATE_SCHEDULE(0x0000000C), ENABLE_SCHEDULE(0x000000C0), ABLE_TO_START_SCHEDULE(0x00000C00)');
insert into YukonRoleProperty values(-10102,-101,'tdc_express','ludicrous_speed','<description>');
insert into YukonRoleProperty values(-10103,-101,'tdc_max_rows','500','The number of rows shown before creating a new page of data');
insert into YukonRoleProperty values(-10104,-101,'tdc_rights','00000000','The following settings are valid: HIDE_MACS(0x00001000), HIDE_CAPCONTROL(0x00002000), HIDE_LOADCONTROL(0x00004000), HIDE_ALL_DISPLAYS(0x0000F000), CONTROL_YUKON_SERVICES(0x00010000), HIDE_ALARM_COLORS(0x80000000)');
insert into YukonRoleProperty values(-10105,-101,'CAP_CONTROL_INTERFACE','amfm','<description>');
insert into YukonRoleProperty values(-10106,-101,'cbc_creation_name','CBC %PAOName%','What text will be added onto CBC names when they are created');
insert into YukonRoleProperty values(-10107,-101,'tdc_alarm_count','3','Total number alarms that are displayed in the quick access list');
insert into YukonRoleProperty values(-10108,-101,'decimal_places','2','How many decimal places to show for real values');
insert into YukonRoleProperty values(-10109,-101,'pfactor_decimal_places','1','How many decimal places to show for real values for PowerFactor');
insert into YukonRoleProperty values(-10111,-101,'lc_reduction_col','true','Tells TDC to show the LoadControl reduction column or not');
insert into YukonRoleProperty values(-10112,-101,'cbc_allow_ovuv','false','Allows users to toggle OV/UV usage on capbanks');


/* Trending Role */
insert into YukonRoleProperty values(-10200,-102,'graph_edit_graphdefinition','true','<description>');

/* Commander Role Properties */ 
insert into YukonRoleProperty values(-10300,-103,'msg_priority','14','Tells commander what the outbound priority of messages are (low)1 - 14(high)');
insert into YukonRoleProperty values(-10301,-103,'Versacom Serial','true','Show a Versacom Serial Number SortBy display');
insert into YukonRoleProperty values(-10302,-103,'Expresscom Serial','true','Show an Expresscom Serial Number SortBy display');
insert into YukonRoleProperty values(-10303,-103,'DCU SA205 Serial','false','Show a DCU SA205 Serial Number SortBy display');
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
insert into YukonRoleProperty values(-10805,-108,'header_logo','yukon/DefaultHeader.gif','The main header logo');
insert into YukonRoleProperty values(-10806, -108,'log_in_url','/login.jsp','The url where the user login from. It is used as the url to send the users to when they log off.');
insert into YukonRoleProperty values(-10807,-108,'nav_connector_bottom','yukon/BottomConnector.gif','The connector icon in the nav used for showing the hardware tree structure, in front of the last hardware under each category');
insert into YukonRoleProperty values(-10808,-108,'nav_connector_middle','yukon/MidConnector.gif','The connector icon in the nav used for showing the hardware tree structure, in front of every hardware except the last one under each category');

/* Operator Consumer Info Role Properties */
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
insert into YukonRoleProperty values(-20835,-201,'Label Account General','General','Text of the account general link');
insert into YukonRoleProperty values(-20836,-201,'Label Account Contacts','Contacts','Text of the account contacts link');
insert into YukonRoleProperty values(-20837,-201,'Label Account Residence','Residence','Text of the account residence link');
insert into YukonRoleProperty values(-20838,-201,'Label Call Tracking','Call Tracking','Text of the call tracking link');
insert into YukonRoleProperty values(-20839,-201,'Label Create Call','Create Call','Text of the create call link');
insert into YukonRoleProperty values(-20840,-201,'Label Service Request','Service Request','Text of the service request link');
insert into YukonRoleProperty values(-20841,-201,'Label Service History','Service History','Text of the service history link');
insert into YukonRoleProperty values(-20842,-201,'Label Change Login','Change Login','Text of the change login link');
insert into YukonRoleProperty values(-20843,-201,'Label FAQ','FAQ','Text of the FAQ link');
insert into YukonRoleProperty values(-20844,-201,'Label Interval Data','Interval Data','Text of the interval data link');
insert into YukonRoleProperty values(-20845,-201,'Label Thermostat Saved Schedules','Saved Schedules','Text of the thermostat saved schedules link');

insert into YukonRoleProperty values(-20850,-201,'Title Programs Control History','PROGRAMS - CONTROL HISTORY','Title of the programs control history page');
insert into YukonRoleProperty values(-20851,-201,'Title Program Control History','PROGRAM - CONTROL HISTORY','Title of the control history page of a particular program');
insert into YukonRoleProperty values(-20852,-201,'Title Program Control Summary','PROGRAM - CONTROL SUMMARY','Title of the control summary page of a particular program');
insert into YukonRoleProperty values(-20853,-201,'Title Programs Enrollment','PROGRAMS - ENROLLMENT','Title of the programs enrollment page');
insert into YukonRoleProperty values(-20854,-201,'Title Programs Opt Out','PROGRAMS - OPT OUT','Title of the programs opt out page');
insert into YukonRoleProperty values(-20855,-201,'Title Thermostat Schedule','THERMOSTAT - SCHEDULE','Title of the thermostat schedule page');
insert into YukonRoleProperty values(-20856,-201,'Title Thermostat Manual','THERMOSTAT - MANUAL','Title of the thermostat manual page');
insert into YukonRoleProperty values(-20857,-201,'Title Call Tracking','ACCOUNT - CALL TRACKING','Title of the call tracking page');
insert into YukonRoleProperty values(-20858,-201,'Title Create Call','ACCOUNT - CREATE NEW CALL','Title of the create call page');
insert into YukonRoleProperty values(-20859,-201,'Title Service Request','WORK ORDERS - SERVICE REQUEST','Title of the service request page');
insert into YukonRoleProperty values(-20860,-201,'Title Service History','WORK ORDERS - SERVICE HISTORY','Title of the service history page');
insert into YukonRoleProperty values(-20861,-201,'Title Change Login','ADMINISTRATION - CHANGE LOGIN','Title of the change login page');
insert into YukonRoleProperty values(-20862,-201,'Title Create Trend','METERING - CREATE NEW TREND','Title of the create trend page');
insert into YukonRoleProperty values(-20863,-201,'Title Thermostat Saved Schedules','THERMOSTAT - SAVED SCHEDULES','Title of the thermostat saved schedules page');
insert into YukonRoleProperty values(-20864,-201,'Title Hardware Overriding','HARDWARE - OVERRIDING','Title of the hardware overriding page');

insert into YukonRoleProperty values(-20870,-201,'Description Opt Out','If you would like to temporarily opt out of all programs, select the time frame below, then click Submit.','Description on the programs opt out page');

insert into YukonRoleProperty values(-20880,-201,'Heading Account','Account','Heading of the account links');
insert into YukonRoleProperty values(-20881,-201,'Heading Metering','Metering','Heading of the metering links');
insert into YukonRoleProperty values(-20882,-201,'Heading Programs','Programs','Heading of the program links');
insert into YukonRoleProperty values(-20883,-201,'Heading Appliances','Appliances','Heading of the appliance links');
insert into YukonRoleProperty values(-20884,-201,'Heading Hardwares','Hardwares','Heading of the hardware links');
insert into YukonRoleProperty values(-20885,-201,'Heading Work Orders','Work Orders','Heading of the work order links');
insert into YukonRoleProperty values(-20886,-201,'Heading Administration','Administration','Heading of the administration links');
insert into YukonRoleProperty values(-20887,-201,'Sub-Heading Switches','Switches','Sub-heading of the switch links');
insert into YukonRoleProperty values(-20888,-201,'Sub-Heading Thermostats','Thermostats','Sub-heading of the thermostat links');
insert into YukonRoleProperty values(-20889,-201,'Sub-Heading Meters','Meters','Sub-heading of the meter links');

/* Operator Hardware Inventory Role Properties */
insert into YukonRoleProperty values(-20900,-209,'Show All Inventory','true','Controls whether to allow showing all inventory');
insert into YukonRoleProperty values(-20901,-209,'Add SN Range','true','Controls whether to allow adding hardwares by serial number range');
insert into YukonRoleProperty values(-20902,-209,'Update SN Range','true','Controls whether to allow updating hardwares by serial number range');
insert into YukonRoleProperty values(-20903,-209,'Config SN Range','true','Controls whether to allow configuring hardwares by serial number range');
insert into YukonRoleProperty values(-20904,-209,'Delete SN Range','true','Controls whether to allow deleting hardwares by serial number range');
insert into YukonRoleProperty values(-20905,-209,'Create Hardware','true','Controls whether to allow creating new hardware');
insert into YukonRoleProperty values(-20906,-209,'Create MCT','true','Controls whether to allow creating MCT devices');

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

insert into YukonRoleProperty values(-40051,-400,'Hide Opt Out Box','false','Controls whether to show the opt out box on the programs opt out page');
insert into YukonRoleProperty values(-40052,-400,'Automatic Configuration','false','Controls whether to automatically send out config command when changing program enrollment');
insert into YukonRoleProperty values(-40054,-400,'Disable Program Signup','false','Controls whether to prevent the customers from enrolling in or out of the programs');
insert into YukonRoleProperty values(-40055,-400,'Opt Out Rules','(none)','Defines the rules for opting out.');

insert into YukonRoleProperty values(-40100,-400,'Link FAQ','(none)','The customized FAQ link');
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
insert into YukonRoleProperty values(-40135,-400,'Label Account General','General','Text of the account general link');
insert into YukonRoleProperty values(-40136,-400,'Label Contact Us','Contact Us','Text of the contact us link');
insert into YukonRoleProperty values(-40137,-400,'Label FAQ','FAQ','Text of the FAQ link');
insert into YukonRoleProperty values(-40138,-400,'Label Change Login','Change Login','Text of the change login link');
insert into YukonRoleProperty values(-40139,-400,'Label Thermostat Saved Schedules','Saved Schedules','Text of the thermostat saved schedules link');

insert into YukonRoleProperty values(-40150,-400,'Title General','WELCOME TO ENERGY COMPANY SERVICES!','Title of the general page');
insert into YukonRoleProperty values(-40151,-400,'Title Programs Control History','PROGRAMS - CONTROL HISTORY','Title of the programs control history page');
insert into YukonRoleProperty values(-40152,-400,'Title Program Control History','PROGRAM - CONTROL HISTORY','Title of the control history page of a particular program');
insert into YukonRoleProperty values(-40153,-400,'Title Program Control Summary','PROGRAM - CONTROL SUMMARY','Title of the control summary page of a particular program');
insert into YukonRoleProperty values(-40154,-400,'Title Programs Enrollment','PROGRAMS - ENROLLMENT','Title of the programs enrollment page');
insert into YukonRoleProperty values(-40155,-400,'Title Programs Opt Out','PROGRAMS - OPT OUT','Title of the programs opt out page');
insert into YukonRoleProperty values(-40156,-400,'Title Utility','QUESTIONS - UTILITY','Title of the utility page');
insert into YukonRoleProperty values(-40157,-400,'Title Thermostat Schedule','THERMOSTAT - SCHEDULE','Title of the thermostat schedule page');
insert into YukonRoleProperty values(-40158,-400,'Title Thermostat Manual','THERMOSTAT - MANUAL','Title of the thermostat manual page');
insert into YukonRoleProperty values(-40159,-400,'Title Thermostat Saved Schedules','THERMOSTAT - SAVED SCHEDULES','Title of the thermostat saved schedules page');

insert into YukonRoleProperty values(-40170,-400,'Description General','Thank you for participating in our Consumer Energy Services programs. By participating, you have helped to optimize our delivery of energy, stabilize rates, and reduce energy costs. Best of all, you are saving energy dollars!<br><br>This site is designed to help manage your programs on-line from anywhere with access to a Web browser.','Description on the general page');
insert into YukonRoleProperty values(-40171,-400,'Description Opt Out','If you would like to temporarily opt out of all programs, select the time frame below, then click Submit.','Description on the programs opt out page');
insert into YukonRoleProperty values(-40172,-400,'Description Enrollment','Select the check boxes and corresponding radio button of the programs you would like to be enrolled in.','Description on the program enrollment page');
insert into YukonRoleProperty values(-40173,-400,'Description Utility',' <<COMPANY_ADDRESS>><br><<PHONE_NUMBER>><<FAX_NUMBER>><<EMAIL>>','Description on the contact us page. The special fields will be replaced by real information when displayed on the web.');

insert into YukonRoleProperty values(-40180,-400,'Image Corner','yukon/Mom.jpg','Image at the upper-left corner');
insert into YukonRoleProperty values(-40181,-400,'Image General','yukon/Family.jpg','Image on the general page');

insert into YukonRoleProperty values(-40190,-400,'Heading Account','Account','Heading of the account links');
insert into YukonRoleProperty values(-40191,-400,'Heading Thermostat','Thermostat','Heading of the thermostat links');
insert into YukonRoleProperty values(-40192,-400,'Heading Metering','Metering','Heading of the metering links');
insert into YukonRoleProperty values(-40193,-400,'Heading Programs','Programs','Heading of the program links');
insert into YukonRoleProperty values(-40194,-400,'Heading Trending','Trending','Heading of the trending links');
insert into YukonRoleProperty values(-40195,-400,'Heading Questions','Questions','Heading of the questions links');
insert into YukonRoleProperty values(-40196,-400,'Heading Administration','Administration','Heading of the administration links');

/* Billing AMR role properties */
insert into YukonRoleProperty values(-50000,-500,'Header Label','Billing','The header label for billing.');
insert into YukonRoleProperty values(-50001,-500,'Default File Format','CTI-CSV','The Default file formats.  See table BillingFileFormats.format for other valid values.');
insert into YukonRoleProperty values(-50002,-500,'Demand Days Previous','30','Integer value for number of days for demand readings to query back from billing end date.');
insert into YukonRoleProperty values(-50003,-500,'Energy Days Previous','7','Integer value for number of days for energy readings to query back from billing end date.');
insert into YukonRoleProperty values(-50004,-500,'Append To File','false','Append to existing file.');
insert into YukonRoleProperty values(-50005,-500,'Remove Multiplier','false','Remove the multiplier value from the reading.');
insert into YukonRoleProperty values(-50006,-500,'Input File Location','c:\yukon\client\bin\BillingIn.txt','The NCDC format takes in an input file.');
insert into YukonRoleProperty values(-50007,-500,'Coop ID - CADP Only','(none)','CADP format requires a coop id number.');

/* Reporting Analysis role properties */
insert into YukonRoleProperty values(-60000,-600,'Header Label','Reporting','The header label for reporting.');
insert into YukonRoleProperty values(-60001,-600,'Download Reports Enable','true','Access to download the report files..');
insert into YukonRoleProperty values(-60002,-600,'Download Reports Default Filename','report.txt','A default filename for the downloaded report.');
insert into YukonRoleProperty values(-60003,-600,'Admin Reports Group','true','Access to administrative group reports.');
insert into YukonRoleProperty values(-60004,-600,'AMR Reports Group','true','Access to AMR group reports.');
insert into YukonRoleProperty values(-60005,-600,'Statistical Reports Group','true','Access to statistical group reports.');
insert into YukonRoleProperty values(-60006,-600,'Load Managment Reports Group','false','Acces to Load Management group reports.');
insert into YukonRoleProperty values(-60007,-600,'Cap Control Reports Group','false','Access to Cap Control group reports.');
insert into YukonRoleProperty values(-60008,-600,'Database Reports Group','true','Access to Database group reports.');
insert into YukonRoleProperty values(-60009,-600,'Stars Reports Group','true','Access to Stars group reports.');
insert into YukonRoleProperty values(-60010,-600,'Other Reports Group','true','Access to Other group reports.');

insert into YukonRoleProperty values(-60013,-600,'Admin Reports Group Label','Administor','Label (header) for administrative group reports.');
insert into YukonRoleProperty values(-60014,-600,'AMR Reports Group Label','Metering','Label (header) for AMR group reports.');
insert into YukonRoleProperty values(-60015,-600,'Statistical Reports Group Label','Statistical','Label (header) for statistical group reports.');
insert into YukonRoleProperty values(-60016,-600,'Load Managment Reports Group Label','Load Management','Label (header) for Load Management group reports.');
insert into YukonRoleProperty values(-60017,-600,'Cap Control Reports Group Label','Cap Control','Label (header) for Cap Control group reports.');
insert into YukonRoleProperty values(-60018,-600,'Database Reports Group Label','Database','Label (header) for Database group reports.');
insert into YukonRoleProperty values(-60019,-600,'Stars Reports Group Label','Stars','Label (header) for Stars group reports.');
insert into YukonRoleProperty values(-60020,-600,'Other Reports Group Label','Other','Label (header) for Other group reports.');

/* Trending Analysis role properties */
insert into YukonRoleProperty values(-60100, -601, 'Trending Disclaimer',' ','The disclaimer that appears with trends.');
insert into yukonroleproperty values(-60101, -601, 'Scan Now Enabled', 'false', 'Controls access to retrieve meter data on demand.');
insert into yukonroleproperty values(-60102, -601, 'Scan Now Label', 'Get Data Now', 'The label for the scan data now option.');
insert into yukonroleproperty values(-60103, -601, 'Minimum Scan Frequency', '15', 'Minimum duration (in minutes) between get data now events.');
insert into yukonroleproperty values(-60104, -601, 'Maximum Daily Scans', '2', 'Maximum number of get data now scans available daily.');
insert into yukonroleproperty values(-60105, -601, 'Reset Peaks Enabled', 'false', 'Allow access to reset the peak time period.');
insert into yukonroleproperty values(-60106, -601, 'Header Label', 'Trending', 'The header label for trends.');
insert into yukonroleproperty values(-60107, -601, 'Header Secondary Label', 'Interval Data', 'A secondary header label for grouping trends.');
insert into yukonroleproperty values(-60108, -601, 'Trend Assignment', 'false', 'Allow assignment of trends to users.');
insert into yukonroleproperty values(-60109, -601, 'Trend Create', 'false', 'Allow creation of new trends.');
insert into yukonroleproperty values(-60110, -601, 'Trend Delete', 'false', 'Allow deletion of old trends.');
insert into yukonroleproperty values(-60111, -601, 'Trend Edit', 'false', 'Allow ditting of existing trends.');
insert into yukonroleproperty values(-60112, -601, 'Options Button Enabled', 'true', 'Display the Options link to additional trending configuration properties.');
insert into yukonroleproperty values(-60113, -601, 'Export/Print Button Enabled', 'true', 'Display the Export/Print options button (drop down menu).');
insert into yukonroleproperty values(-60114, -601, 'View Button Enabled', 'true', 'Display the View options button (drop down menu).');
insert into yukonroleproperty values(-60115, -601, 'Export/Print Button Label', 'Trend', 'The label for the trend print/export button (drop down menu).');
insert into yukonroleproperty values(-60116, -601, 'View Button Label', 'View', 'The label for the trend view options button (drop down menu).');
insert into yukonroleproperty values(-60117, -601, 'Trending Usage', 'false', 'Allow access to trending time of use.');
insert into yukonroleproperty values(-60118, -601, 'Default Start Date Offset', '0', 'Offset the start date by this number.');
insert into yukonroleproperty values(-60119, -601, 'Default Time Period', '(none)', 'Default the time period.');

/* Capacitor Control role properties */
insert into YukonRoleProperty values(-70000,-700,'Access','false','Sets accessibility to the CapControl module.');
insert into YukonRoleProperty values(-70001,-700,'Allow Control','true','Enables or disables field and local controls for the given user');
insert into YukonRoleProperty values(-70002,-700,'Hide Reports','false','Sets the visibility of reports.');
insert into YukonRoleProperty values(-70003,-700,'Hide Graphs','false','Sets the visibility of graphs.');
insert into YukonRoleProperty values(-70004,-700,'Hide One-Lines','false','Sets the visibility of one-line displays.');
alter table YukonRoleProperty
   add constraint PK_YUKONROLEPROPERTY primary key  (RolePropertyID)
go


/*==============================================================*/
/* Table: YukonSelectionList                                    */
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
/* Table: YukonServices                                         */
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
/* insert into YukonServices values( 4, 'CBC_OneLine_Gen', 'com.cannontech.jmx.services.DynamicCBCOneLine', '(none)', '(none)'); */
alter table YukonServices
   add constraint PK_YUKSER primary key  (ServiceID)
go


/*==============================================================*/
/* Table: YukonUser                                             */
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
/* Table: YukonUserGroup                                        */
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
/* Table: YukonUserRole                                         */
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
insert into YukonUserRole values(-107,-1,-100,-10007,'true');
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

insert into YukonUserRole values (-551,-1,-400,-40051,'(none)');
insert into YukonUserRole values (-552,-1,-400,-40052,'(none)');
insert into YukonUserRole values (-554,-1,-400,-40054,'(none)');
insert into YukonUserRole values (-555,-1,-400,-40055,'(none)');

insert into YukonUserRole values (-600,-1,-400,-40100,'(none)');
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
/* Table: YukonWebConfiguration                                 */
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
create view DISPLAY2WAYDATA_VIEW as
select POINTID as PointID, POINTNAME as PointName, POINTTYPE as PointType, SERVICEFLAG as PointState, YukonPAObject.PAOName as DeviceName, YukonPAObject.Type as DeviceType, YukonPAObject.Description as DeviceCurrentState, YukonPAObject.PAObjectID as DeviceID, '**DYNAMIC**' as PointValue, '**DYNAMIC**' as PointQuality, '**DYNAMIC**' as PointTimeStamp, (select uomname from pointunit,unitmeasure where pointunit.pointid=point.pointid and pointunit.uomid=unitmeasure.uomid) as UofM, '**DYNAMIC**' as Tags
from YukonPAObject, POINT
where YukonPAObject.PAObjectID = POINT.PAObjectID
go


/*==============================================================*/
/* View: ExpressComAddress_View                                 */
/*==============================================================*/
create view ExpressComAddress_View as
select x.LMGroupID, x.RouteID, x.SerialNumber, s.Address as serviceaddress,
g.Address as geoaddress, b.Address as substationaddress, f.Address as feederaddress,
z.Address as ZipCodeAddress, us.Address as UDAddress, p.Address as programaddress, sp.Address as SplinterAddress, x.AddressUsage, x.RelayUsage
from LMGroupExpressCom x, LMGroupExpressComAddress s, 
LMGroupExpressComAddress g, LMGroupExpressComAddress b, LMGroupExpressComAddress f,
LMGroupExpressComAddress p,
LMGroupExpressComAddress sp, LMGroupExpressComAddress us, LMGroupExpressComAddress z
where ( x.ServiceProviderID = s.AddressID and ( s.AddressType = 'SERVICE' or s.AddressID = 0 ) )
and ( x.FeederID = f.AddressID and ( f.AddressType = 'FEEDER' or f.AddressID = 0 ) )
and ( x.GeoID = g.AddressID and ( g.AddressType = 'GEO' or g.AddressID = 0 ) )
and ( x.ProgramID = p.AddressID and ( p.AddressType = 'PROGRAM' or p.AddressID = 0 ) )
and ( x.SubstationID = b.AddressID and ( b.AddressType = 'SUBSTATION' or b.AddressID = 0 ) )
and ( x.SplinterID = sp.AddressID and ( sp.AddressType = 'SPLINTER' or sp.AddressID = 0 ) )
and ( x.UserID = us.AddressID and ( us.AddressType = 'USER' or us.AddressID = 0 ) )
and ( x.ZipID = z.AddressID and ( z.AddressType = 'ZIP' or z.AddressID = 0 ) )
go


/*==============================================================*/
/* View: FeederAddress_View                                     */
/*==============================================================*/
create view FeederAddress_View as
select x.LMGroupID, a.Address as FeederAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.FeederID = a.AddressID and ( a.AddressType = 'FEEDER' or a.AddressID = 0 ) )
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
/* View: GeoAddress_View                                        */
/*==============================================================*/
create view GeoAddress_View as
select x.LMGroupID, a.Address as GeoAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.GeoID = a.AddressID and ( a.AddressType = 'GEO' or a.AddressID = 0 ) )
go


/*==============================================================*/
/* View: LMCurtailCustomerActivity_View                         */
/*==============================================================*/
create view LMCurtailCustomerActivity_View as
select cust.CustomerID, prog.CurtailmentStartTime, prog.CurtailReferenceID, prog.CurtailmentStopTime, cust.AcknowledgeStatus, cust.AckDateTime, cust.NameOfAckPerson, cust.AckLateFlag
from LMCurtailProgramActivity prog, LMCurtailCustomerActivity cust
where prog.CurtailReferenceID = cust.CurtailReferenceID
go


/*==============================================================*/
/* View: LMProgram_View                                         */
/*==============================================================*/
create view LMProgram_View as
select t.DeviceID, t.ControlType, u.ConstraintID, u.ConstraintName, u.AvailableWeekDays, u.MaxHoursDaily, u.MaxHoursMonthly, u.MaxHoursSeasonal, u.MaxHoursAnnually, u.MinActivateTime, u.MinRestartTime, u.MaxDailyOps, u.MaxActivateTime, u.HolidayScheduleID, u.SeasonScheduleID
from LMPROGRAM t, LMProgramConstraints u
where u.ConstraintID = t.ConstraintID
go


/*==============================================================*/
/* View: Peakpointhistory_View                                  */
/*==============================================================*/
create view Peakpointhistory_View as
select rph1.POINTID pointid, rph1.VALUE value, min(rph1.timestamp) timestamp
from RAWPOINTHISTORY rph1
where VALUE in ( select max ( value ) from rawpointhistory rph2 where rph1.pointid = rph2.pointid )
group by POINTID, VALUE
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


/*==============================================================*/
/* View: ProgramAddress_View                                    */
/*==============================================================*/
create view ProgramAddress_View as
select x.LMGroupID, a.Address as ProgramAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.ProgramID = a.AddressID and ( a.AddressType = 'PROGRAM' or a.AddressID = 0 ) )
go


/*==============================================================*/
/* View: ServiceAddress_View                                    */
/*==============================================================*/
create view ServiceAddress_View as
select x.LMGroupID, a.Address as ServiceAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.ServiceProviderID = a.AddressID and ( a.AddressType = 'SERVICE' or a.AddressID = 0 ) )
go


/*==============================================================*/
/* View: SubstationAddress_View                                 */
/*==============================================================*/
create view SubstationAddress_View as
select x.LMGroupID, a.Address as SubstationAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.SubstationID = a.AddressID and ( a.AddressType = 'SUBSTATION' or a.AddressID = 0 ) )
go


alter table AlarmCategory
   add constraint FK_ALRMCAT_NOTIFGRP foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID)
go


alter table CALCBASE
   add constraint SYS_C0013434 foreign key (POINTID)
      references POINT (POINTID)
go


alter table CALCCOMPONENT
   add constraint FK_ClcCmp_ClcBs foreign key (PointID)
      references CALCBASE (POINTID)
go


alter table CALCCOMPONENT
   add constraint FK_ClcCmp_Pt foreign key (COMPONENTPOINTID)
      references POINT (POINTID)
go


alter table CAPBANK
   add constraint SYS_C0013453 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table CAPBANK
   add constraint SYS_C0013454 foreign key (CONTROLPOINTID)
      references POINT (POINTID)
go


alter table CAPBANK
   add constraint SYS_C0013455 foreign key (CONTROLDEVICEID)
      references DEVICE (DEVICEID)
go


alter table CAPCONTROLSUBSTATIONBUS
   add constraint FK_CpSbBus_YPao foreign key (SubstationBusID)
      references YukonPAObject (PAObjectID)
go


alter table CAPCONTROLSUBSTATIONBUS
   add constraint SYS_C0013478 foreign key (CurrentWattLoadPointID)
      references POINT (POINTID)
go


alter table CAPCONTROLSUBSTATIONBUS
   add constraint SYS_C0013479 foreign key (CurrentVarLoadPointID)
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


alter table CCFeederSubAssignment
   add constraint FK_CCSub_CCFeed foreign key (SubStationBusID)
      references CAPCONTROLSUBSTATIONBUS (SubstationBusID)
go


alter table CICustomerBase
   add constraint FK_CICstBas_CstAddrs foreign key (MainAddressID)
      references Address (AddressID)
go


alter table CICustomerBase
   add constraint FK_CstCI_Cst foreign key (CustomerID)
      references Customer (CustomerID)
go


alter table CalcPointBaseline
   add constraint FK_CLCBS_BASL foreign key (BaselineID)
      references BaseLine (BaselineID)
go


alter table CalcPointBaseline
   add constraint FK_ClcPtBs_ClcBs foreign key (PointID)
      references CALCBASE (POINTID)
go


alter table CapControlFeeder
   add constraint FK_PAObj_CCFeed foreign key (FeederID)
      references YukonPAObject (PAObjectID)
go


alter table CarrierRoute
   add constraint SYS_C0013264 foreign key (ROUTEID)
      references Route (RouteID)
go


alter table CommErrorHistory
   add constraint FK_ComErrHis_YPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID)
go


alter table CommPort
   add constraint FK_COMMPORT_REF_COMPO_YUKONPAO foreign key (PORTID)
      references YukonPAObject (PAObjectID)
go


alter table Contact
   add constraint FK_RefCstLg_CustCont foreign key (LogInID)
      references YukonUser (UserID)
go


alter table Contact
   add constraint FK_CONTACT_REF_CNT_A_ADDRESS foreign key (AddressID)
      references Address (AddressID)
go


alter table ContactNotification
   add constraint FK_CntNot_YkLs foreign key (NotificationCategoryID)
      references YukonListEntry (EntryID)
go


alter table ContactNotification
   add constraint FK_Cnt_CntNot foreign key (ContactID)
      references Contact (ContactID)
go


alter table Customer
   add constraint FK_Cst_Cnt foreign key (PrimaryContactID)
      references Contact (ContactID)
go


alter table CustomerAdditionalContact
   add constraint FK_CstCont_CICstCont foreign key (ContactID)
      references Contact (ContactID)
go


alter table CustomerAdditionalContact
   add constraint FK_Cust_CustAddCnt foreign key (CustomerID)
      references Customer (CustomerID)
go


alter table CustomerBaseLinePoint
   add constraint FK_CstBseLn_CICust foreign key (CustomerID)
      references CICustomerBase (CustomerID)
go


alter table CustomerBaseLinePoint
   add constraint FK_CstBseLn_ClcBse foreign key (PointID)
      references CALCBASE (POINTID)
go


alter table CustomerLoginSerialGroup
   add constraint FK_CsLgSG_CsL foreign key (LoginID)
      references YukonUser (UserID)
go


alter table CustomerLoginSerialGroup
   add constraint FK_CsLgSG_LMG foreign key (LMGroupID)
      references LMGroup (DeviceID)
go


alter table DEVICE
   add constraint FK_Dev_YukPAO foreign key (DEVICEID)
      references YukonPAObject (PAObjectID)
go


alter table DEVICE2WAYFLAGS
   add constraint SYS_C0013208 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table DEVICECARRIERSETTINGS
   add constraint SYS_C0013216 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table DEVICEDIALUPSETTINGS
   add constraint SYS_C0013193 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
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


alter table DEVICESCANRATE
   add constraint SYS_C0013198 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table DEVICETAPPAGINGSETTINGS
   add constraint SYS_C0013237 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table DISPLAY2WAYDATA
   add constraint FK_DISPLAY2W_REF_POINT foreign key (POINTID)
      references POINT (POINTID)
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


alter table DYNAMICPOINTDISPATCH
   add constraint SYS_C0013331 foreign key (POINTID)
      references POINT (POINTID)
go


alter table DateOfHoliday
   add constraint FK_HolSchID foreign key (HolidayScheduleID)
      references HolidaySchedule (HolidayScheduleID)
go


alter table DateOfSeason
   add constraint FK_DaOfSe_SeSc foreign key (SeasonScheduleID)
      references SeasonSchedule (ScheduleID)
go


alter table DeviceAddress
   add constraint FK_Dev_DevDNP foreign key (DeviceID)
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


alter table DeviceCustomerList
   add constraint FK_DvStLsCst foreign key (CustomerID)
      references Customer (CustomerID)
go


alter table DeviceCustomerList
   add constraint FK_DvStLsDev foreign key (DeviceID)
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


alter table DeviceMCT400Series
   add constraint FK_Dev4_DevC foreign key (DeviceID)
      references DEVICECARRIERSETTINGS (DEVICEID)
go


alter table DeviceMCT400Series
   add constraint FK_Dev4_TOU foreign key (TOUScheduleID)
      references TOUSchedule (TOUScheduleID)
go


alter table DeviceRTC
   add constraint FK_Dev_DevRTC foreign key (DeviceID)
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


alter table DeviceSeries5RTU
   add constraint FK_DvS5r_Dv2w foreign key (DeviceID)
      references DEVICE2WAYFLAGS (DEVICEID)
go


alter table DeviceTypeCommand
   add constraint FK_DevCmd_Cmd foreign key (CommandID)
      references Command (CommandID)
go


alter table DeviceVerification
   add constraint FK_DevV_Dev1 foreign key (ReceiverID)
      references DEVICE (DEVICEID)
go


alter table DeviceVerification
   add constraint FK_DevV_Dev2 foreign key (TransmitterID)
      references DEVICE (DEVICEID)
go


alter table DeviceWindow
   add constraint FK_DevScWin_Dev foreign key (DeviceID)
      references DEVICE (DEVICEID)
go


alter table DynamicCCCapBank
   add constraint FK_CpBnk_DynCpBnk foreign key (CapBankID)
      references CAPBANK (DEVICEID)
go


alter table DynamicCCFeeder
   add constraint FK_CCFeed_DyFeed foreign key (FeederID)
      references CapControlFeeder (FeederID)
go


alter table DynamicCCSubstationBus
   add constraint FK_CCSubBs_DySubBs foreign key (SubstationBusID)
      references CAPCONTROLSUBSTATIONBUS (SubstationBusID)
go


alter table DynamicCalcHistorical
   add constraint FK_DynClc_ClcB foreign key (PointID)
      references CALCBASE (POINTID)
go


alter table DynamicLMControlArea
   add constraint FK_LMCntlAr_DynLMCntAr foreign key (DeviceID)
      references LMControlArea (DEVICEID)
go


alter table DynamicLMControlAreaTrigger
   add constraint FK_LMCntArTr_DyLMCnArTr foreign key (DeviceID, TriggerNumber)
      references LMCONTROLAREATRIGGER (DEVICEID, TRIGGERNUMBER)
go


alter table DynamicLMControlHistory
   add constraint FK_DYNLMCNT_PAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID)
go


alter table DynamicLMGroup
   add constraint FK_LMGrp_DynLmGrp foreign key (DeviceID)
      references LMGroup (DeviceID)
go


alter table DynamicLMGroup
   add constraint FK_DyLmGr_LmPrDGr foreign key (LMProgramID)
      references LMProgramDirect (DeviceID)
go


alter table DynamicLMProgram
   add constraint FK_LMProg_DynLMPrg foreign key (DeviceID)
      references LMPROGRAM (DeviceID)
go


alter table DynamicLMProgramDirect
   add constraint FK_DYNAMICL_LMPROGDIR_LMPROGRA foreign key (DeviceID)
      references LMProgramDirect (DeviceID)
go


alter table DynamicPAOStatistics
   add constraint FK_PASt_YkPA foreign key (PAOBjectID)
      references YukonPAObject (PAObjectID)
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
   add constraint FK_DYNAMICT_REF_DYNTG_TAGS foreign key (TagID)
      references Tags (TagID)
go


alter table DynamicVerification
   add constraint FK_DynV_Dev1 foreign key (ReceiverID)
      references DEVICE (DEVICEID)
go


alter table DynamicVerification
   add constraint FK_DynV_Dev2 foreign key (TransmitterID)
      references DEVICE (DEVICEID)
go


alter table EnergyCompany
   add constraint FK_EnCm_Cnt foreign key (PrimaryContactID)
      references Contact (ContactID)
go


alter table EnergyCompany
   add constraint FK_EngCmp_YkUs foreign key (UserID)
      references YukonUser (UserID)
go


alter table EnergyCompanyCustomerList
   add constraint FK_CICstBsEnCmpCsLs foreign key (CustomerID)
      references CICustomerBase (CustomerID)
go


alter table EnergyCompanyCustomerList
   add constraint FK_EnCmpEnCmpCsLs foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID)
go


alter table EnergyCompanyOperatorLoginList
   add constraint FK_EnCmpEnCmpOpLs foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID)
go


alter table EnergyCompanyOperatorLoginList
   add constraint FK_OpLgEnCmpOpLs foreign key (OperatorLoginID)
      references YukonUser (UserID)
go


alter table FDRInterfaceOption
   add constraint FK_FDRINTER_REFERENCE_FDRINTER foreign key (InterfaceID)
      references FDRInterface (InterfaceID)
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


alter table GraphCustomerList
   add constraint FK_GRAPHCUS_REFGRPHCU_GRAPHDEF foreign key (GraphDefinitionID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID)
go


alter table GraphCustomerList
   add constraint FK_GrphCstLst_Cst foreign key (CustomerID)
      references Customer (CustomerID)
go


alter table LMCONTROLAREAPROGRAM
   add constraint FK_LMCntlArea_LMCntlArProg foreign key (DEVICEID)
      references LMControlArea (DEVICEID)
go


alter table LMCONTROLAREAPROGRAM
   add constraint FK_LMPrg_LMCntlArProg foreign key (LMPROGRAMDEVICEID)
      references LMPROGRAM (DeviceID)
go


alter table LMCONTROLAREATRIGGER
   add constraint FK_LMCntlArea_LMCntlArTrig foreign key (DEVICEID)
      references LMControlArea (DEVICEID)
go


alter table LMCONTROLAREATRIGGER
   add constraint FK_Point_LMCntlArTrig foreign key (POINTID)
      references POINT (POINTID)
go


alter table LMCONTROLAREATRIGGER
   add constraint FK_Point_LMCtrlArTrigPk foreign key (PEAKPOINTID)
      references POINT (POINTID)
go


alter table LMControlArea
   add constraint FK_LmCntAr_YukPAO foreign key (DEVICEID)
      references YukonPAObject (PAObjectID)
go


alter table LMControlHistory
   add constraint FK_LmCtrlHis_YPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID)
go


alter table LMControlScenarioProgram
   add constraint FK_LmCScP_YkPA foreign key (ScenarioID)
      references YukonPAObject (PAObjectID)
go


alter table LMControlScenarioProgram
   add constraint FK_LMCONTRO_REF_LMSCP_LMPROGRA foreign key (ProgramID)
      references LMPROGRAM (DeviceID)
go


alter table LMCurtailCustomerActivity
   add constraint FK_CICBas_LMCrtCstAct foreign key (CustomerID)
      references CICustomerBase (CustomerID)
go


alter table LMCurtailCustomerActivity
   add constraint FK_LMCURTAI_REFLMCST__LMCURTAI foreign key (CurtailReferenceID)
      references LMCurtailProgramActivity (CurtailReferenceID)
go


alter table LMCurtailProgramActivity
   add constraint FK_LMPrgCrt_LMCrlPAct foreign key (DeviceID)
      references LMProgramCurtailment (DeviceID)
go


alter table LMDirectCustomerList
   add constraint FK_CICstB_LMPrDi foreign key (CustomerID)
      references CICustomerBase (CustomerID)
go


alter table LMDirectCustomerList
   add constraint FK_LMDIRECT_REFLMPDIR_LMPROGRA foreign key (ProgramID)
      references LMProgramDirect (DeviceID)
go


alter table LMDirectNotifGrpList
   add constraint FK_LMDi_DNGrpL foreign key (ProgramID)
      references LMProgramDirect (DeviceID)
go


alter table LMDirectNotifGrpList
   add constraint FK_NtGr_DNGrpL foreign key (NotificationGrpID)
      references NotificationGroup (NotificationGroupID)
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
      references LMEnergyExchangeOfferRevision (OfferID, RevisionNumber)
go


alter table LMEnergyExchangeOfferRevision
   add constraint FK_EExOffR_ExPrOff foreign key (OfferID)
      references LMEnergyExchangeProgramOffer (OfferID)
go


alter table LMEnergyExchangeProgramOffer
   add constraint FK_EnExOff_PrgEnEx foreign key (DeviceID)
      references LMProgramEnergyExchange (DeviceID)
go


alter table LMGroup
   add constraint FK_Device_LMGrpBase2 foreign key (DeviceID)
      references DEVICE (DEVICEID)
go


alter table LMGroupEmetcon
   add constraint SYS_C0013356 foreign key (DEVICEID)
      references LMGroup (DeviceID)
go


alter table LMGroupEmetcon
   add constraint SYS_C0013357 foreign key (ROUTEID)
      references Route (RouteID)
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


alter table LMGroupExpressCom
   add constraint FK_LGrEx_LMG foreign key (LMGroupID)
      references LMGroup (DeviceID)
go


alter table LMGroupExpressCom
   add constraint FK_LGrEx_Rt foreign key (RouteID)
      references Route (RouteID)
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


alter table LMGroupRipple
   add constraint FK_LmGr_LmGrpRip foreign key (DeviceID)
      references LMGroup (DeviceID)
go


alter table LMGroupRipple
   add constraint FK_LmGrpRip_Rout foreign key (RouteID)
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


alter table LMGroupSASimple
   add constraint FK_LmGrSa_LmG foreign key (GroupID)
      references LMGroup (DeviceID)
go


alter table LMGroupSASimple
   add constraint FK_LmGrSa_Rt foreign key (RouteID)
      references Route (RouteID)
go


alter table LMGroupVersacom
   add constraint FK_LMGrp_LMGrpVers foreign key (DEVICEID)
      references LMGroup (DeviceID)
go


alter table LMGroupVersacom
   add constraint SYS_C0013367 foreign key (ROUTEID)
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


alter table LMPROGRAM
   add constraint FK_LMPr_PrgCon foreign key (ConstraintID)
      references LMProgramConstraints (ConstraintID)
go


alter table LMPROGRAM
   add constraint FK_LmProg_YukPAO foreign key (DeviceID)
      references YukonPAObject (PAObjectID)
go


alter table LMProgramConstraints
   add constraint FK_HlSc_LmPrC foreign key (HolidayScheduleID)
      references HolidaySchedule (HolidayScheduleID)
go


alter table LMProgramConstraints
   add constraint FK_SesSch_LmPrC foreign key (SeasonScheduleID)
      references SeasonSchedule (ScheduleID)
go


alter table LMProgramControlWindow
   add constraint FK_LMPrg_LMPrgCntWind foreign key (DeviceID)
      references LMPROGRAM (DeviceID)
go


alter table LMProgramCurtailCustomerList
   add constraint FK_CICstBase_LMProgCList foreign key (CustomerID)
      references CICustomerBase (CustomerID)
go


alter table LMProgramCurtailCustomerList
   add constraint FK_LMPrgCrt_LMPrCstLst foreign key (ProgramID)
      references LMProgramCurtailment (DeviceID)
      on delete cascade
go


alter table LMProgramCurtailment
   add constraint FK_LMPrg_LMPrgCurt foreign key (DeviceID)
      references LMPROGRAM (DEVICEID)
go


alter table LMProgramDirect
   add constraint FK_LMPrg_LMPrgDirect foreign key (DeviceID)
      references LMPROGRAM (DeviceID)
go


alter table LMProgramDirectGear
   add constraint FK_LMProgD_LMProgDGr foreign key (DeviceID)
      references LMProgramDirect (DeviceID)
go


alter table LMProgramDirectGroup
   add constraint FK_LMGrp_LMPrgDGrp foreign key (LMGroupDeviceID)
      references LMGroup (DeviceID)
go


alter table LMProgramDirectGroup
   add constraint FK_LMPrgD_LMPrgDGrp foreign key (DeviceID)
      references LMProgramDirect (DeviceID)
go


alter table LMProgramEnergyExchange
   add constraint FK_LmPrg_LmPrEEx foreign key (DeviceID)
      references LMPROGRAM (DeviceID)
go


alter table LMThermoStatGear
   add constraint FK_ThrmStG_PrDiGe foreign key (GearID)
      references LMProgramDirectGear (GearID)
go


alter table MACROROUTE
   add constraint SYS_C0013274 foreign key (ROUTEID)
      references Route (RouteID)
go


alter table MACROROUTE
   add constraint SYS_C0013275 foreign key (SINGLEROUTEID)
      references Route (RouteID)
go


alter table MACSchedule
   add constraint FK_SchdID_PAOID foreign key (ScheduleID)
      references YukonPAObject (PAObjectID)
go


alter table MACSimpleSchedule
   add constraint FK_MACSIMPLE_MACSCHED_ID foreign key (ScheduleID)
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


alter table NotificationDestination
   add constraint FK_NotifDest_NotifGrp foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID)
go


alter table NotificationDestination
   add constraint FK_CntNt_NtDst foreign key (RecipientID)
      references ContactNotification (ContactNotifID)
go


alter table OperatorLoginGraphList
   add constraint FK_OpLgOpLgGrLs foreign key (GraphDefinitionID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID)
go


alter table OperatorLoginGraphList
   add constraint FK_OpLgOpLgGrLs2 foreign key (OperatorLoginID)
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
   add constraint FK_PAOEXCLU_REF_PAOEX_YUKONPAO foreign key (ExcludedPaoID)
      references YukonPAObject (PAObjectID)
go


alter table PAOowner
   add constraint FK_YukPAO_PAOOwn foreign key (ChildID)
      references YukonPAObject (PAObjectID)
go


alter table PAOowner
   add constraint FK_YukPAO_PAOid foreign key (OwnerID)
      references YukonPAObject (PAObjectID)
go


alter table POINT
   add constraint FK_Pt_YukPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID)
go


alter table POINT
   add constraint Ref_STATGRP_PT foreign key (STATEGROUPID)
      references STATEGROUP (STATEGROUPID)
go


alter table POINTACCUMULATOR
   add constraint SYS_C0013317 foreign key (POINTID)
      references POINT (POINTID)
go


alter table POINTANALOG
   add constraint SYS_C0013300 foreign key (POINTID)
      references POINT (POINTID)
go


alter table POINTLIMITS
   add constraint SYS_C0013289 foreign key (POINTID)
      references POINT (POINTID)
go


alter table POINTSTATUS
   add constraint Ref_ptstatus_pt foreign key (POINTID)
      references POINT (POINTID)
go


alter table POINTUNIT
   add constraint FK_PtUnit_UoM foreign key (UOMID)
      references UNITMEASURE (UOMID)
go


alter table POINTUNIT
   add constraint Ref_ptunit_point foreign key (POINTID)
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


alter table PointAlarming
   add constraint FK_POINTALAARM_POINT_POINTID foreign key (PointID)
      references POINT (POINTID)
go


alter table PointAlarming
   add constraint FK_POINTALARMING foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID)
go


alter table PointAlarming
   add constraint FK_CntNt_PtAl foreign key (RecipientID)
      references ContactNotification (ContactNotifID)
go


alter table PortTiming
   add constraint SYS_C0013163 foreign key (PORTID)
      references CommPort (PORTID)
go


alter table RAWPOINTHISTORY
   add constraint FK_RawPt_Point foreign key (POINTID)
      references POINT (POINTID)
go


alter table RepeaterRoute
   add constraint SYS_C0013269 foreign key (ROUTEID)
      references Route (RouteID)
go


alter table RepeaterRoute
   add constraint SYS_C0013270 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table Route
   add constraint FK_Route_DevID foreign key (DeviceID)
      references DEVICE (DEVICEID)
go


alter table Route
   add constraint FK_Route_YukPAO foreign key (RouteID)
      references YukonPAObject (PAObjectID)
go


alter table STATE
   add constraint FK_YkIm_St foreign key (ImageID)
      references YukonImage (ImageID)
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


alter table TOUDayMapping
   add constraint FK_TOUd_TOUSc foreign key (TOUScheduleID)
      references TOUSchedule (TOUScheduleID)
go


alter table TOUDayMapping
   add constraint FK_TOUm_TOUd foreign key (TOUDayID)
      references TOUDay (TOUDayID)
go


alter table TOUDayRateSwitches
   add constraint FK_TOUdRS_TOUd foreign key (TOUDayID)
      references TOUDay (TOUDayID)
go


alter table TagLog
   add constraint FK_TagLg_Pt foreign key (PointID)
      references POINT (POINTID)
go


alter table TagLog
   add constraint FK_TagLg_Tgs foreign key (TagID)
      references Tags (TagID)
go


alter table UserPAOowner
   add constraint FK_UsPow_YkP foreign key (PaoID)
      references YukonPAObject (PAObjectID)
go


alter table UserPAOowner
   add constraint FK_UsPow_YkUsr foreign key (UserID)
      references YukonUser (UserID)
go


alter table VersacomRoute
   add constraint FK_VERSACOM_ROUTE_VER_ROUTE foreign key (ROUTEID)
      references Route (RouteID)
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


alter table YukonListEntry
   add constraint FK_LstEnty_SelLst foreign key (ListID)
      references YukonSelectionList (ListID)
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
   add constraint FK_YUKONUSE_REF_YKUSG_YUKONUSE foreign key (UserID)
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


