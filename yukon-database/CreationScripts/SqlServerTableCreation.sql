/*==============================================================*/
/* Database name:  YukonDatabase                                */
/* DBMS name:      CTI SqlServer 2000                           */
/* Created on:     6/6/2003 4:09:23 PM                          */
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
           where  id = object_id('PK_ADDRESS')
            and   type = 'K')
alter table Address
   drop constraint PK_ADDRESS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('Address')
            and   type = 'U')
   drop table Address
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_ALARMCATEGORYID')
            and   type = 'K')
alter table AlarmCategory
   drop constraint PK_ALARMCATEGORYID
go


if exists (select 1
            from  sysobjects
           where  id = object_id('AlarmCategory')
            and   type = 'U')
   drop table AlarmCategory
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_BILLINGFILEFORMATS')
            and   type = 'K')
alter table BillingFileFormats
   drop constraint PK_BILLINGFILEFORMATS
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
           where  id = object_id('PK_CALCBASE')
            and   type = 'K')
alter table CALCBASE
   drop constraint PK_CALCBASE
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
           where  id = object_id('PK_CALCCOMPONENT')
            and   type = 'K')
alter table CALCCOMPONENT
   drop constraint PK_CALCCOMPONENT
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CALCCOMPONENT')
            and   type = 'U')
   drop table CALCCOMPONENT
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_CAPBANK')
            and   type = 'K')
alter table CAPBANK
   drop constraint PK_CAPBANK
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CAPBANK')
            and   type = 'U')
   drop table CAPBANK
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
            from  sysobjects
           where  id = object_id('SYS_C0013476')
            and   type = 'K')
alter table CAPCONTROLSUBSTATIONBUS
   drop constraint SYS_C0013476
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CAPCONTROLSUBSTATIONBUS')
            and   type = 'U')
   drop table CAPCONTROLSUBSTATIONBUS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_CCFEEDERBANKLIST')
            and   type = 'K')
alter table CCFeederBankList
   drop constraint PK_CCFEEDERBANKLIST
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CCFeederBankList')
            and   type = 'U')
   drop table CCFeederBankList
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_CCFEEDERSUBASSIGNMENT')
            and   type = 'K')
alter table CCFeederSubAssignment
   drop constraint PK_CCFEEDERSUBASSIGNMENT
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CCFeederSubAssignment')
            and   type = 'U')
   drop table CCFeederSubAssignment
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_CICUSTOMERBASE')
            and   type = 'K')
alter table CICustomerBase
   drop constraint PK_CICUSTOMERBASE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CICustomerBase')
            and   type = 'U')
   drop table CICustomerBase
go


if exists (select 1
            from  sysobjects
           where  id = object_id('SYS_C0013414')
            and   type = 'K')
alter table COLUMNTYPE
   drop constraint SYS_C0013414
go


if exists (select 1
            from  sysobjects
           where  id = object_id('COLUMNTYPE')
            and   type = 'U')
   drop table COLUMNTYPE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_CTIDATABASE')
            and   type = 'K')
alter table CTIDatabase
   drop constraint PK_CTIDATABASE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CTIDatabase')
            and   type = 'U')
   drop table CTIDatabase
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
            from  sysobjects
           where  id = object_id('PK_CAPCONTROLFEEDER')
            and   type = 'K')
alter table CapControlFeeder
   drop constraint PK_CAPCONTROLFEEDER
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CapControlFeeder')
            and   type = 'U')
   drop table CapControlFeeder
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_CARRIERROUTE')
            and   type = 'K')
alter table CarrierRoute
   drop constraint PK_CARRIERROUTE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CarrierRoute')
            and   type = 'U')
   drop table CarrierRoute
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_COMMERRORHISTORY')
            and   type = 'K')
alter table CommErrorHistory
   drop constraint PK_COMMERRORHISTORY
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CommErrorHistory')
            and   type = 'U')
   drop table CommErrorHistory
go


if exists (select 1
            from  sysobjects
           where  id = object_id('SYS_C0013112')
            and   type = 'K')
alter table CommPort
   drop constraint SYS_C0013112
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CommPort')
            and   type = 'U')
   drop table CommPort
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_CONTACT')
            and   type = 'K')
alter table Contact
   drop constraint PK_CONTACT
go


if exists (select 1
            from  sysobjects
           where  id = object_id('Contact')
            and   type = 'U')
   drop table Contact
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_CONTACTNOTIFICATION')
            and   type = 'K')
alter table ContactNotification
   drop constraint PK_CONTACTNOTIFICATION
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ContactNotification')
            and   type = 'U')
   drop table ContactNotification
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_CUSTOMER')
            and   type = 'K')
alter table Customer
   drop constraint PK_CUSTOMER
go


if exists (select 1
            from  sysobjects
           where  id = object_id('Customer')
            and   type = 'U')
   drop table Customer
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_CUSTOMERADDITIONALCONTACT')
            and   type = 'K')
alter table CustomerAdditionalContact
   drop constraint PK_CUSTOMERADDITIONALCONTACT
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerAdditionalContact')
            and   type = 'U')
   drop table CustomerAdditionalContact
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_CUSTOMERBASELINE')
            and   type = 'K')
alter table CustomerBaseLine
   drop constraint PK_CUSTOMERBASELINE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerBaseLine')
            and   type = 'U')
   drop table CustomerBaseLine
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_CUSTOMERBASELINEPOINT')
            and   type = 'K')
alter table CustomerBaseLinePoint
   drop constraint PK_CUSTOMERBASELINEPOINT
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerBaseLinePoint')
            and   type = 'U')
   drop table CustomerBaseLinePoint
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_CUSTOMERLOGINSERIALGROUP')
            and   type = 'K')
alter table CustomerLoginSerialGroup
   drop constraint PK_CUSTOMERLOGINSERIALGROUP
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerLoginSerialGroup')
            and   type = 'U')
   drop table CustomerLoginSerialGroup
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_DEV_DEVICEID2')
            and   type = 'K')
alter table DEVICE
   drop constraint PK_DEV_DEVICEID2
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DEVICE')
            and   type = 'U')
   drop table DEVICE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_DEVICE2WAYFLAGS')
            and   type = 'K')
alter table DEVICE2WAYFLAGS
   drop constraint PK_DEVICE2WAYFLAGS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DEVICE2WAYFLAGS')
            and   type = 'U')
   drop table DEVICE2WAYFLAGS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_DEVICECARRIERSETTINGS')
            and   type = 'K')
alter table DEVICECARRIERSETTINGS
   drop constraint PK_DEVICECARRIERSETTINGS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DEVICECARRIERSETTINGS')
            and   type = 'U')
   drop table DEVICECARRIERSETTINGS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_DEVICEDIALUPSETTINGS')
            and   type = 'K')
alter table DEVICEDIALUPSETTINGS
   drop constraint PK_DEVICEDIALUPSETTINGS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DEVICEDIALUPSETTINGS')
            and   type = 'U')
   drop table DEVICEDIALUPSETTINGS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_DEVICEIDLCREMOTE')
            and   type = 'K')
alter table DEVICEIDLCREMOTE
   drop constraint PK_DEVICEIDLCREMOTE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DEVICEIDLCREMOTE')
            and   type = 'U')
   drop table DEVICEIDLCREMOTE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_DEVICEIED')
            and   type = 'K')
alter table DEVICEIED
   drop constraint PK_DEVICEIED
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DEVICEIED')
            and   type = 'U')
   drop table DEVICEIED
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_DEVICELOADPROFILE')
            and   type = 'K')
alter table DEVICELOADPROFILE
   drop constraint PK_DEVICELOADPROFILE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DEVICELOADPROFILE')
            and   type = 'U')
   drop table DEVICELOADPROFILE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_DEVICEMCTIEDPORT')
            and   type = 'K')
alter table DEVICEMCTIEDPORT
   drop constraint PK_DEVICEMCTIEDPORT
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DEVICEMCTIEDPORT')
            and   type = 'U')
   drop table DEVICEMCTIEDPORT
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_DEVICEMETERGROUP')
            and   type = 'K')
alter table DEVICEMETERGROUP
   drop constraint PK_DEVICEMETERGROUP
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DEVICEMETERGROUP')
            and   type = 'U')
   drop table DEVICEMETERGROUP
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_DEVICESCANRATE')
            and   type = 'K')
alter table DEVICESCANRATE
   drop constraint PK_DEVICESCANRATE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DEVICESCANRATE')
            and   type = 'U')
   drop table DEVICESCANRATE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_DEVICETAPPAGINGSETTINGS')
            and   type = 'K')
alter table DEVICETAPPAGINGSETTINGS
   drop constraint PK_DEVICETAPPAGINGSETTINGS
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
           where  id = object_id('SYS_C0013412')
            and   type = 'K')
alter table DISPLAY
   drop constraint SYS_C0013412
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
           where  id = object_id('PK_DISPLAY2WAYDATA')
            and   type = 'K')
alter table DISPLAY2WAYDATA
   drop constraint PK_DISPLAY2WAYDATA
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DISPLAY2WAYDATA')
            and   type = 'U')
   drop table DISPLAY2WAYDATA
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_DISPLAYCOLUMNS')
            and   type = 'K')
alter table DISPLAYCOLUMNS
   drop constraint PK_DISPLAYCOLUMNS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DISPLAYCOLUMNS')
            and   type = 'U')
   drop table DISPLAYCOLUMNS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_DYNAMICACCUMULATOR')
            and   type = 'K')
alter table DYNAMICACCUMULATOR
   drop constraint PK_DYNAMICACCUMULATOR
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DYNAMICACCUMULATOR')
            and   type = 'U')
   drop table DYNAMICACCUMULATOR
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_DYNAMICDEVICESCANDATA')
            and   type = 'K')
alter table DYNAMICDEVICESCANDATA
   drop constraint PK_DYNAMICDEVICESCANDATA
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DYNAMICDEVICESCANDATA')
            and   type = 'U')
   drop table DYNAMICDEVICESCANDATA
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_DYNAMICPOINTDISPATCH')
            and   type = 'K')
alter table DYNAMICPOINTDISPATCH
   drop constraint PK_DYNAMICPOINTDISPATCH
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DYNAMICPOINTDISPATCH')
            and   type = 'U')
   drop table DYNAMICPOINTDISPATCH
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_DATEOFHOLIDAY')
            and   type = 'K')
alter table DateOfHoliday
   drop constraint PK_DATEOFHOLIDAY
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DateOfHoliday')
            and   type = 'U')
   drop table DateOfHoliday
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_DEVICECBC')
            and   type = 'K')
alter table DeviceCBC
   drop constraint PK_DEVICECBC
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DeviceCBC')
            and   type = 'U')
   drop table DeviceCBC
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_DEVICEDNP')
            and   type = 'K')
alter table DeviceDNP
   drop constraint PK_DEVICEDNP
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DeviceDNP')
            and   type = 'U')
   drop table DeviceDNP
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_DEVICEDIRECTCOMMSETTINGS')
            and   type = 'K')
alter table DeviceDirectCommSettings
   drop constraint PK_DEVICEDIRECTCOMMSETTINGS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DeviceDirectCommSettings')
            and   type = 'U')
   drop table DeviceDirectCommSettings
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_DEVICEROUTES')
            and   type = 'K')
alter table DeviceRoutes
   drop constraint PK_DEVICEROUTES
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DeviceRoutes')
            and   type = 'U')
   drop table DeviceRoutes
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_DEVICEWINDOW')
            and   type = 'K')
alter table DeviceWindow
   drop constraint PK_DEVICEWINDOW
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DeviceWindow')
            and   type = 'U')
   drop table DeviceWindow
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_DYNAMICCCCAPBANK')
            and   type = 'K')
alter table DynamicCCCapBank
   drop constraint PK_DYNAMICCCCAPBANK
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DynamicCCCapBank')
            and   type = 'U')
   drop table DynamicCCCapBank
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_DYNAMICCCFEEDER')
            and   type = 'K')
alter table DynamicCCFeeder
   drop constraint PK_DYNAMICCCFEEDER
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DynamicCCFeeder')
            and   type = 'U')
   drop table DynamicCCFeeder
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_DYNAMICCCSUBSTATIONBUS')
            and   type = 'K')
alter table DynamicCCSubstationBus
   drop constraint PK_DYNAMICCCSUBSTATIONBUS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DynamicCCSubstationBus')
            and   type = 'U')
   drop table DynamicCCSubstationBus
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_DYNAMICCALCHISTORICAL')
            and   type = 'K')
alter table DynamicCalcHistorical
   drop constraint PK_DYNAMICCALCHISTORICAL
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DynamicCalcHistorical')
            and   type = 'U')
   drop table DynamicCalcHistorical
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_DYNAMICLMCONTROLAREA')
            and   type = 'K')
alter table DynamicLMControlArea
   drop constraint PK_DYNAMICLMCONTROLAREA
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DynamicLMControlArea')
            and   type = 'U')
   drop table DynamicLMControlArea
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_DYNAMICLMCONTROLAREATRIGGER')
            and   type = 'K')
alter table DynamicLMControlAreaTrigger
   drop constraint PK_DYNAMICLMCONTROLAREATRIGGER
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DynamicLMControlAreaTrigger')
            and   type = 'U')
   drop table DynamicLMControlAreaTrigger
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_DYNAMICLMGROUP')
            and   type = 'K')
alter table DynamicLMGroup
   drop constraint PK_DYNAMICLMGROUP
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DynamicLMGroup')
            and   type = 'U')
   drop table DynamicLMGroup
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_DYNAMICLMPROGRAM')
            and   type = 'K')
alter table DynamicLMProgram
   drop constraint PK_DYNAMICLMPROGRAM
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DynamicLMProgram')
            and   type = 'U')
   drop table DynamicLMProgram
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_DYNAMICLMPROGRAMDIRECT')
            and   type = 'K')
alter table DynamicLMProgramDirect
   drop constraint PK_DYNAMICLMPROGRAMDIRECT
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DynamicLMProgramDirect')
            and   type = 'U')
   drop table DynamicLMProgramDirect
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_DYNAMICPAOSTATISTICS')
            and   type = 'K')
alter table DynamicPAOStatistics
   drop constraint PK_DYNAMICPAOSTATISTICS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DynamicPAOStatistics')
            and   type = 'U')
   drop table DynamicPAOStatistics
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
           where  id = object_id('PK_ENERGYCOMPANY')
            and   type = 'K')
alter table EnergyCompany
   drop constraint PK_ENERGYCOMPANY
go


if exists (select 1
            from  sysobjects
           where  id = object_id('EnergyCompany')
            and   type = 'U')
   drop table EnergyCompany
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_ENERGYCOMPANYCUSTOMERLIST')
            and   type = 'K')
alter table EnergyCompanyCustomerList
   drop constraint PK_ENERGYCOMPANYCUSTOMERLIST
go


if exists (select 1
            from  sysobjects
           where  id = object_id('EnergyCompanyCustomerList')
            and   type = 'U')
   drop table EnergyCompanyCustomerList
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_ENERGYCOMPANYOPERATORLOGINL')
            and   type = 'K')
alter table EnergyCompanyOperatorLoginList
   drop constraint PK_ENERGYCOMPANYOPERATORLOGINL
go


if exists (select 1
            from  sysobjects
           where  id = object_id('EnergyCompanyOperatorLoginList')
            and   type = 'U')
   drop table EnergyCompanyOperatorLoginList
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_FDRINTERFACE')
            and   type = 'K')
alter table FDRInterface
   drop constraint PK_FDRINTERFACE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('FDRInterface')
            and   type = 'U')
   drop table FDRInterface
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_FDRINTERFACEOPTION')
            and   type = 'K')
alter table FDRInterfaceOption
   drop constraint PK_FDRINTERFACEOPTION
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
           where  id = object_id('PK_FDRTrans')
            and   type = 'K')
alter table FDRTRANSLATION
   drop constraint PK_FDRTrans
go


if exists (select 1
            from  sysobjects
           where  id = object_id('FDRTRANSLATION')
            and   type = 'U')
   drop table FDRTRANSLATION
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_FDRTELEGYRGROUP')
            and   type = 'K')
alter table FDRTelegyrGroup
   drop constraint PK_FDRTELEGYRGROUP
go


if exists (select 1
            from  sysobjects
           where  id = object_id('FDRTelegyrGroup')
            and   type = 'U')
   drop table FDRTelegyrGroup
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
           where  id = object_id('SYS_GrphDserID')
            and   type = 'K')
alter table GRAPHDATASERIES
   drop constraint SYS_GrphDserID
go


if exists (select 1
            from  sysobjects
           where  id = object_id('GRAPHDATASERIES')
            and   type = 'U')
   drop table GRAPHDATASERIES
go


if exists (select 1
            from  sysobjects
           where  id = object_id('SYS_C0015109')
            and   type = 'K')
alter table GRAPHDEFINITION
   drop constraint SYS_C0015109
go


if exists (select 1
            from  sysobjects
           where  id = object_id('AK_GRNMUQ_GRAPHDEF')
            and   type = 'K')
alter table GRAPHDEFINITION
   drop constraint AK_GRNMUQ_GRAPHDEF
go


if exists (select 1
            from  sysobjects
           where  id = object_id('GRAPHDEFINITION')
            and   type = 'U')
   drop table GRAPHDEFINITION
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_GENERICMACRO')
            and   type = 'K')
alter table GenericMacro
   drop constraint PK_GENERICMACRO
go


if exists (select 1
            from  sysobjects
           where  id = object_id('GenericMacro')
            and   type = 'U')
   drop table GenericMacro
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_GRAPHCUSTOMERLIST')
            and   type = 'K')
alter table GraphCustomerList
   drop constraint PK_GRAPHCUSTOMERLIST
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
           where  id = object_id('PK_HOLIDAYSCHEDULE')
            and   type = 'K')
alter table HolidaySchedule
   drop constraint PK_HOLIDAYSCHEDULE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('HolidaySchedule')
            and   type = 'U')
   drop table HolidaySchedule
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_LMCONTROLAREAPROGRAM')
            and   type = 'K')
alter table LMCONTROLAREAPROGRAM
   drop constraint PK_LMCONTROLAREAPROGRAM
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMCONTROLAREAPROGRAM')
            and   type = 'U')
   drop table LMCONTROLAREAPROGRAM
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_LMCONTROLAREATRIGGER')
            and   type = 'K')
alter table LMCONTROLAREATRIGGER
   drop constraint PK_LMCONTROLAREATRIGGER
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMCONTROLAREATRIGGER')
            and   type = 'U')
   drop table LMCONTROLAREATRIGGER
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_LMCONTROLAREA')
            and   type = 'K')
alter table LMControlArea
   drop constraint PK_LMCONTROLAREA
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMControlArea')
            and   type = 'U')
   drop table LMControlArea
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
           where  id = object_id('PK_LMCONTROLHISTORY')
            and   type = 'K')
alter table LMControlHistory
   drop constraint PK_LMCONTROLHISTORY
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
           where  id = object_id('PK_LMCURTAILCUSTOMERACTIVITY')
            and   type = 'K')
alter table LMCurtailCustomerActivity
   drop constraint PK_LMCURTAILCUSTOMERACTIVITY
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
           where  id = object_id('PK_LMCURTAILPROGRAMACTIVITY')
            and   type = 'K')
alter table LMCurtailProgramActivity
   drop constraint PK_LMCURTAILPROGRAMACTIVITY
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMCurtailProgramActivity')
            and   type = 'U')
   drop table LMCurtailProgramActivity
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_LMDIRECTCUSTOMERLIST')
            and   type = 'K')
alter table LMDirectCustomerList
   drop constraint PK_LMDIRECTCUSTOMERLIST
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMDirectCustomerList')
            and   type = 'U')
   drop table LMDirectCustomerList
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_LMDIRECTOPERATORLIST')
            and   type = 'K')
alter table LMDirectOperatorList
   drop constraint PK_LMDIRECTOPERATORLIST
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMDirectOperatorList')
            and   type = 'U')
   drop table LMDirectOperatorList
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_LMENERGYEXCHANGECUSTOMERLIS')
            and   type = 'K')
alter table LMEnergyExchangeCustomerList
   drop constraint PK_LMENERGYEXCHANGECUSTOMERLIS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMEnergyExchangeCustomerList')
            and   type = 'U')
   drop table LMEnergyExchangeCustomerList
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_LMENERGYEXCHANGECUSTOMERREP')
            and   type = 'K')
alter table LMEnergyExchangeCustomerReply
   drop constraint PK_LMENERGYEXCHANGECUSTOMERREP
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMEnergyExchangeCustomerReply')
            and   type = 'U')
   drop table LMEnergyExchangeCustomerReply
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_LMENERGYEXCHANGEHOURLYCUSTO')
            and   type = 'K')
alter table LMEnergyExchangeHourlyCustomer
   drop constraint PK_LMENERGYEXCHANGEHOURLYCUSTO
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMEnergyExchangeHourlyCustomer')
            and   type = 'U')
   drop table LMEnergyExchangeHourlyCustomer
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_LMENERGYEXCHANGEHOURLYOFFER')
            and   type = 'K')
alter table LMEnergyExchangeHourlyOffer
   drop constraint PK_LMENERGYEXCHANGEHOURLYOFFER
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMEnergyExchangeHourlyOffer')
            and   type = 'U')
   drop table LMEnergyExchangeHourlyOffer
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_LMENERGYEXCHANGEOFFERREVISI')
            and   type = 'K')
alter table LMEnergyExchangeOfferRevision
   drop constraint PK_LMENERGYEXCHANGEOFFERREVISI
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMEnergyExchangeOfferRevision')
            and   type = 'U')
   drop table LMEnergyExchangeOfferRevision
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_LMENERGYEXCHANGEPROGRAMOFFE')
            and   type = 'K')
alter table LMEnergyExchangeProgramOffer
   drop constraint PK_LMENERGYEXCHANGEPROGRAMOFFE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMEnergyExchangeProgramOffer')
            and   type = 'U')
   drop table LMEnergyExchangeProgramOffer
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_LMGROUP')
            and   type = 'K')
alter table LMGroup
   drop constraint PK_LMGROUP
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMGroup')
            and   type = 'U')
   drop table LMGroup
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_LMGROUPEMETCON')
            and   type = 'K')
alter table LMGroupEmetcon
   drop constraint PK_LMGROUPEMETCON
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMGroupEmetcon')
            and   type = 'U')
   drop table LMGroupEmetcon
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_LMGROUPEXPRESSCOM')
            and   type = 'K')
alter table LMGroupExpressCom
   drop constraint PK_LMGROUPEXPRESSCOM
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMGroupExpressCom')
            and   type = 'U')
   drop table LMGroupExpressCom
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_LMGROUPEXPRESSCOMADDRESS')
            and   type = 'K')
alter table LMGroupExpressComAddress
   drop constraint PK_LMGROUPEXPRESSCOMADDRESS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMGroupExpressComAddress')
            and   type = 'U')
   drop table LMGroupExpressComAddress
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_LMGrpMCTPK')
            and   type = 'K')
alter table LMGroupMCT
   drop constraint PK_LMGrpMCTPK
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMGroupMCT')
            and   type = 'U')
   drop table LMGroupMCT
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_LMGROUPPOINT')
            and   type = 'K')
alter table LMGroupPoint
   drop constraint PK_LMGROUPPOINT
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMGroupPoint')
            and   type = 'U')
   drop table LMGroupPoint
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_LMGROUPRIPPLE')
            and   type = 'K')
alter table LMGroupRipple
   drop constraint PK_LMGROUPRIPPLE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMGroupRipple')
            and   type = 'U')
   drop table LMGroupRipple
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_LMGROUPVERSACOM')
            and   type = 'K')
alter table LMGroupVersacom
   drop constraint PK_LMGROUPVERSACOM
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMGroupVersacom')
            and   type = 'U')
   drop table LMGroupVersacom
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_LMMACSSCHEDULEOPERATORLIST')
            and   type = 'K')
alter table LMMACSScheduleOperatorList
   drop constraint PK_LMMACSSCHEDULEOPERATORLIST
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMMACSScheduleOperatorList')
            and   type = 'U')
   drop table LMMACSScheduleOperatorList
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_LMMACSSCHEDULECUSTOMERLIST')
            and   type = 'K')
alter table LMMacsScheduleCustomerList
   drop constraint PK_LMMACSSCHEDULECUSTOMERLIST
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMMacsScheduleCustomerList')
            and   type = 'U')
   drop table LMMacsScheduleCustomerList
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_LMPROGRAM')
            and   type = 'K')
alter table LMPROGRAM
   drop constraint PK_LMPROGRAM
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMPROGRAM')
            and   type = 'U')
   drop table LMPROGRAM
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_LMPROGRAMCONTROLWINDOW')
            and   type = 'K')
alter table LMProgramControlWindow
   drop constraint PK_LMPROGRAMCONTROLWINDOW
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMProgramControlWindow')
            and   type = 'U')
   drop table LMProgramControlWindow
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_LMPROGRAMCURTAILCUSTOMERLIS')
            and   type = 'K')
alter table LMProgramCurtailCustomerList
   drop constraint PK_LMPROGRAMCURTAILCUSTOMERLIS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMProgramCurtailCustomerList')
            and   type = 'U')
   drop table LMProgramCurtailCustomerList
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_LMPROGRAMCURTAILMENT')
            and   type = 'K')
alter table LMProgramCurtailment
   drop constraint PK_LMPROGRAMCURTAILMENT
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMProgramCurtailment')
            and   type = 'U')
   drop table LMProgramCurtailment
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_LMPROGRAMDIRECT')
            and   type = 'K')
alter table LMProgramDirect
   drop constraint PK_LMPROGRAMDIRECT
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMProgramDirect')
            and   type = 'U')
   drop table LMProgramDirect
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_LMPROGRAMDIRECTGEAR')
            and   type = 'K')
alter table LMProgramDirectGear
   drop constraint PK_LMPROGRAMDIRECTGEAR
go


if exists (select 1
            from  sysobjects
           where  id = object_id('AK_AKEY_LMPRGDIRG_LMPROGRA')
            and   type = 'K')
alter table LMProgramDirectGear
   drop constraint AK_AKEY_LMPRGDIRG_LMPROGRA
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMProgramDirectGear')
            and   type = 'U')
   drop table LMProgramDirectGear
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_LMPROGRAMDIRECTGROUP')
            and   type = 'K')
alter table LMProgramDirectGroup
   drop constraint PK_LMPROGRAMDIRECTGROUP
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMProgramDirectGroup')
            and   type = 'U')
   drop table LMProgramDirectGroup
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_LMPROGRAMENERGYEXCHANGE')
            and   type = 'K')
alter table LMProgramEnergyExchange
   drop constraint PK_LMPROGRAMENERGYEXCHANGE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMProgramEnergyExchange')
            and   type = 'U')
   drop table LMProgramEnergyExchange
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_LMTHERMOSTATGEAR')
            and   type = 'K')
alter table LMThermoStatGear
   drop constraint PK_LMTHERMOSTATGEAR
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMThermoStatGear')
            and   type = 'U')
   drop table LMThermoStatGear
go


if exists (select 1
            from  sysobjects
           where  id = object_id('SYS_C0013445')
            and   type = 'K')
alter table LOGIC
   drop constraint SYS_C0013445
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LOGIC')
            and   type = 'U')
   drop table LOGIC
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_MACROROUTE')
            and   type = 'K')
alter table MACROROUTE
   drop constraint PK_MACROROUTE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('MACROROUTE')
            and   type = 'U')
   drop table MACROROUTE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_MACSCHEDULE')
            and   type = 'K')
alter table MACSchedule
   drop constraint PK_MACSCHEDULE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('MACSchedule')
            and   type = 'U')
   drop table MACSchedule
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_MACSIMPLESCHEDULE')
            and   type = 'K')
alter table MACSimpleSchedule
   drop constraint PK_MACSIMPLESCHEDULE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('MACSimpleSchedule')
            and   type = 'U')
   drop table MACSimpleSchedule
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_MCTBROADCASTMAPPING')
            and   type = 'K')
alter table MCTBroadCastMapping
   drop constraint PK_MCTBROADCASTMAPPING
go


if exists (select 1
            from  sysobjects
           where  id = object_id('MCTBroadCastMapping')
            and   type = 'U')
   drop table MCTBroadCastMapping
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PKey_NotDestID')
            and   type = 'K')
alter table NotificationDestination
   drop constraint PKey_NotDestID
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
           where  id = object_id('PK_NOTIFICATIONGROUP')
            and   type = 'K')
alter table NotificationGroup
   drop constraint PK_NOTIFICATIONGROUP
go


if exists (select 1
            from  sysobjects
           where  id = object_id('NotificationGroup')
            and   type = 'U')
   drop table NotificationGroup
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_OPERATORLOGINGRAPHLIST')
            and   type = 'K')
alter table OperatorLoginGraphList
   drop constraint PK_OPERATORLOGINGRAPHLIST
go


if exists (select 1
            from  sysobjects
           where  id = object_id('OperatorLoginGraphList')
            and   type = 'U')
   drop table OperatorLoginGraphList
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_OpSerGrp')
            and   type = 'K')
alter table OperatorSerialGroup
   drop constraint PK_OpSerGrp
go


if exists (select 1
            from  sysobjects
           where  id = object_id('OperatorSerialGroup')
            and   type = 'U')
   drop table OperatorSerialGroup
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
            from  sysobjects
           where  id = object_id('PK_PAOEXCLUSION')
            and   type = 'K')
alter table PAOExclusion
   drop constraint PK_PAOEXCLUSION
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PAOExclusion')
            and   type = 'U')
   drop table PAOExclusion
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_PAOOWNER')
            and   type = 'K')
alter table PAOowner
   drop constraint PK_PAOOWNER
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
            and   name  = 'Indx_PointStGrpID'
            and   indid > 0
            and   indid < 255)
   drop index POINT.Indx_PointStGrpID
go


if exists (select 1
            from  sysobjects
           where  id = object_id('Key_PT_PTID')
            and   type = 'K')
alter table POINT
   drop constraint Key_PT_PTID
go


if exists (select 1
            from  sysobjects
           where  id = object_id('AK_KEY_PTNM_YUKPAOID')
            and   type = 'K')
alter table POINT
   drop constraint AK_KEY_PTNM_YUKPAOID
go


if exists (select 1
            from  sysobjects
           where  id = object_id('POINT')
            and   type = 'U')
   drop table POINT
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_POINTACCUMULATOR')
            and   type = 'K')
alter table POINTACCUMULATOR
   drop constraint PK_POINTACCUMULATOR
go


if exists (select 1
            from  sysobjects
           where  id = object_id('POINTACCUMULATOR')
            and   type = 'U')
   drop table POINTACCUMULATOR
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_POINTANALOG')
            and   type = 'K')
alter table POINTANALOG
   drop constraint PK_POINTANALOG
go


if exists (select 1
            from  sysobjects
           where  id = object_id('POINTANALOG')
            and   type = 'U')
   drop table POINTANALOG
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_POINTLIMITS')
            and   type = 'K')
alter table POINTLIMITS
   drop constraint PK_POINTLIMITS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('POINTLIMITS')
            and   type = 'U')
   drop table POINTLIMITS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_PtStatus')
            and   type = 'K')
alter table POINTSTATUS
   drop constraint PK_PtStatus
go


if exists (select 1
            from  sysobjects
           where  id = object_id('POINTSTATUS')
            and   type = 'U')
   drop table POINTSTATUS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_POINTUNITID')
            and   type = 'K')
alter table POINTUNIT
   drop constraint PK_POINTUNITID
go


if exists (select 1
            from  sysobjects
           where  id = object_id('POINTUNIT')
            and   type = 'U')
   drop table POINTUNIT
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_PORTDIALUPMODEM')
            and   type = 'K')
alter table PORTDIALUPMODEM
   drop constraint PK_PORTDIALUPMODEM
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PORTDIALUPMODEM')
            and   type = 'U')
   drop table PORTDIALUPMODEM
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_PORTLOCALSERIAL')
            and   type = 'K')
alter table PORTLOCALSERIAL
   drop constraint PK_PORTLOCALSERIAL
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PORTLOCALSERIAL')
            and   type = 'U')
   drop table PORTLOCALSERIAL
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_PORTRADIOSETTINGS')
            and   type = 'K')
alter table PORTRADIOSETTINGS
   drop constraint PK_PORTRADIOSETTINGS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PORTRADIOSETTINGS')
            and   type = 'U')
   drop table PORTRADIOSETTINGS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_PORTSETTINGS')
            and   type = 'K')
alter table PORTSETTINGS
   drop constraint PK_PORTSETTINGS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PORTSETTINGS')
            and   type = 'U')
   drop table PORTSETTINGS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_PORTTERMINALSERVER')
            and   type = 'K')
alter table PORTTERMINALSERVER
   drop constraint PK_PORTTERMINALSERVER
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PORTTERMINALSERVER')
            and   type = 'U')
   drop table PORTTERMINALSERVER
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_POINTALARMING')
            and   type = 'K')
alter table PointAlarming
   drop constraint PK_POINTALARMING
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PointAlarming')
            and   type = 'U')
   drop table PointAlarming
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_PORTSTATISTICS')
            and   type = 'K')
alter table PortStatistics
   drop constraint PK_PORTSTATISTICS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PortStatistics')
            and   type = 'U')
   drop table PortStatistics
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_PORTTIMING')
            and   type = 'K')
alter table PortTiming
   drop constraint PK_PORTTIMING
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PortTiming')
            and   type = 'U')
   drop table PortTiming
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
            from  sysobjects
           where  id = object_id('SYS_C0013322')
            and   type = 'K')
alter table RAWPOINTHISTORY
   drop constraint SYS_C0013322
go


if exists (select 1
            from  sysobjects
           where  id = object_id('RAWPOINTHISTORY')
            and   type = 'U')
   drop table RAWPOINTHISTORY
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_REPEATERROUTE')
            and   type = 'K')
alter table RepeaterRoute
   drop constraint PK_REPEATERROUTE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('RepeaterRoute')
            and   type = 'U')
   drop table RepeaterRoute
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
            from  sysobjects
           where  id = object_id('SYS_RoutePK')
            and   type = 'K')
alter table Route
   drop constraint SYS_RoutePK
go


if exists (select 1
            from  sysobjects
           where  id = object_id('Route')
            and   type = 'U')
   drop table Route
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
           where  id = object_id('PK_STATE')
            and   type = 'K')
alter table STATE
   drop constraint PK_STATE
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
           where  id = object_id('SYS_C0013128')
            and   type = 'K')
alter table STATEGROUP
   drop constraint SYS_C0013128
go


if exists (select 1
            from  sysobjects
           where  id = object_id('STATEGROUP')
            and   type = 'U')
   drop table STATEGROUP
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
           where  id    = object_id('SYSTEMLOG')
            and   name  = 'Indx_SYSLG_Date'
            and   indid > 0
            and   indid < 255)
   drop index SYSTEMLOG.Indx_SYSLG_Date
go


if exists (select 1
            from  sysobjects
           where  id = object_id('SYS_C0013407')
            and   type = 'K')
alter table SYSTEMLOG
   drop constraint SYS_C0013407
go


if exists (select 1
            from  sysobjects
           where  id = object_id('SYSTEMLOG')
            and   type = 'U')
   drop table SYSTEMLOG
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_SEASONSCHEDULE')
            and   type = 'K')
alter table SeasonSchedule
   drop constraint PK_SEASONSCHEDULE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('SeasonSchedule')
            and   type = 'U')
   drop table SeasonSchedule
go


if exists (select 1
            from  sysobjects
           where  id = object_id('SYS_C0013425')
            and   type = 'K')
alter table TEMPLATE
   drop constraint SYS_C0013425
go


if exists (select 1
            from  sysobjects
           where  id = object_id('TEMPLATE')
            and   type = 'U')
   drop table TEMPLATE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_TEMPLATECOLUMNS')
            and   type = 'K')
alter table TEMPLATECOLUMNS
   drop constraint PK_TEMPLATECOLUMNS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('TEMPLATECOLUMNS')
            and   type = 'U')
   drop table TEMPLATECOLUMNS
go


if exists (select 1
            from  sysobjects
           where  id = object_id('SYS_C0013344')
            and   type = 'K')
alter table UNITMEASURE
   drop constraint SYS_C0013344
go


if exists (select 1
            from  sysobjects
           where  id = object_id('UNITMEASURE')
            and   type = 'U')
   drop table UNITMEASURE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_VERSACOMROUTE')
            and   type = 'K')
alter table VersacomRoute
   drop constraint PK_VERSACOMROUTE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('VersacomRoute')
            and   type = 'U')
   drop table VersacomRoute
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_YUKONGROUP')
            and   type = 'K')
alter table YukonGroup
   drop constraint PK_YUKONGROUP
go


if exists (select 1
            from  sysobjects
           where  id = object_id('YukonGroup')
            and   type = 'U')
   drop table YukonGroup
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_YUKONGRPROLE')
            and   type = 'K')
alter table YukonGroupRole
   drop constraint PK_YUKONGRPROLE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('YukonGroupRole')
            and   type = 'U')
   drop table YukonGroupRole
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_YUKONIMAGE')
            and   type = 'K')
alter table YukonImage
   drop constraint PK_YUKONIMAGE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('YukonImage')
            and   type = 'U')
   drop table YukonImage
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
            from  sysobjects
           where  id = object_id('PK_YUKONLISTENTRY')
            and   type = 'K')
alter table YukonListEntry
   drop constraint PK_YUKONLISTENTRY
go


if exists (select 1
            from  sysobjects
           where  id = object_id('YukonListEntry')
            and   type = 'U')
   drop table YukonListEntry
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
           where  id = object_id('PK_YUKONPAOBJECT')
            and   type = 'K')
alter table YukonPAObject
   drop constraint PK_YUKONPAOBJECT
go


if exists (select 1
            from  sysobjects
           where  id = object_id('YukonPAObject')
            and   type = 'U')
   drop table YukonPAObject
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
            from  sysobjects
           where  id = object_id('PK_YUKONROLE')
            and   type = 'K')
alter table YukonRole
   drop constraint PK_YUKONROLE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('YukonRole')
            and   type = 'U')
   drop table YukonRole
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_YUKONROLEPROPERTY')
            and   type = 'K')
alter table YukonRoleProperty
   drop constraint PK_YUKONROLEPROPERTY
go


if exists (select 1
            from  sysobjects
           where  id = object_id('YukonRoleProperty')
            and   type = 'U')
   drop table YukonRoleProperty
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_YUKONSELECTIONLIST')
            and   type = 'K')
alter table YukonSelectionList
   drop constraint PK_YUKONSELECTIONLIST
go


if exists (select 1
            from  sysobjects
           where  id = object_id('YukonSelectionList')
            and   type = 'U')
   drop table YukonSelectionList
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
           where  id = object_id('PK_YUKONUSER')
            and   type = 'K')
alter table YukonUser
   drop constraint PK_YUKONUSER
go


if exists (select 1
            from  sysobjects
           where  id = object_id('YukonUser')
            and   type = 'U')
   drop table YukonUser
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_YUKONUSERGROUP')
            and   type = 'K')
alter table YukonUserGroup
   drop constraint PK_YUKONUSERGROUP
go


if exists (select 1
            from  sysobjects
           where  id = object_id('YukonUserGroup')
            and   type = 'U')
   drop table YukonUserGroup
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_YKONUSRROLE')
            and   type = 'K')
alter table YukonUserRole
   drop constraint PK_YKONUSRROLE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('YukonUserRole')
            and   type = 'U')
   drop table YukonUserRole
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PK_YUKONWEBCONFIGURATION')
            and   type = 'K')
alter table YukonWebConfiguration
   drop constraint PK_YUKONWEBCONFIGURATION
go


if exists (select 1
            from  sysobjects
           where  id = object_id('YukonWebConfiguration')
            and   type = 'U')
   drop table YukonWebConfiguration
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
insert into BillingFileFormats values(1,'CAPD');
insert into BillingFileFormats values(2,'CAPDXL2');
insert into BillingFileFormats values(3,'WLT-40');
insert into BillingFileFormats values(4,'CTI-CSV');
insert into BillingFileFormats values(5,'OPU');
insert into BillingFileFormats values(6,'DAFRON');
insert into BillingFileFormats values(7,'NCDC');
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
MapLocationID        numeric              not null
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
ControlUnits         varchar(20)          not null
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
insert into columntype values (13, 'Tags');
insert into columntype values (14, 'PointImage' );

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
Notes                varchar(300)         null
)
go


insert into CTIDatabase values('2.41', 'Ryan', '29-APR-2003', 'Major changes to roles,groups. Added SeasonSchedule, modified LMProgram,DynamicLMGroup,EnergyCompany');


alter table CTIDatabase
   add constraint PK_CTIDATABASE primary key  (Version)
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
/* Table : CustomerBaseLine                                     */
/*==============================================================*/
create table CustomerBaseLine (
CustomerID           numeric              not null,
DaysUsed             numeric              not null,
PercentWindow        numeric              not null,
CalcDays             numeric              not null,
ExcludedWeekDays     char(7)              not null,
HolidaysUsed         numeric              not null
)
go


alter table CustomerBaseLine
   add constraint PK_CUSTOMERBASELINE primary key  (CustomerID)
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
/* Table : DEVICECARRIERSETTINGS                                */
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


alter table DEVICEDIALUPSETTINGS
   add constraint PK_DEVICEDIALUPSETTINGS primary key  (DEVICEID)
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
LOADPROFILECOLLECTION varchar(4)           not null
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
TIMESTAMP            datetime             not null,
QUALITY              numeric              not null,
VALUE                float                not null,
TAGS                 numeric              not null,
NEXTARCHIVE          datetime             not null,
STALECOUNT           numeric              not null,
LastAlarmLogID       numeric              not null
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
/* Table : DeviceDNP                                            */
/*==============================================================*/
create table DeviceDNP (
DeviceID             numeric              not null,
MasterAddress        numeric              not null,
SlaveAddress         numeric              not null,
PostCommWait         numeric              not null
)
go


alter table DeviceDNP
   add constraint PK_DEVICEDNP primary key  (DeviceID)
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
CurrentVarPointQuality numeric              not null
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
CurrentVarPointQuality numeric              not null
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
TimeStamp            datetime             not null
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
/* Table : EnergyCompany                                        */
/*==============================================================*/
create table EnergyCompany (
EnergyCompanyID      numeric              not null,
Name                 varchar(60)          not null,
PrimaryContactID     numeric              not null,
UserID               numeric              not null
)
go


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
insert into fdrinterface values (12,'TEXTIMPORT','Receive,Receive for control','f');
insert into fdrinterface values (13,'TEXTEXPORT','Send','f');

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
insert into FDRInterfaceOption values(11, 'Point', 1, 'Text', '(none)' );
insert into FDRInterfaceOption values(11, 'Group', 2, 'Query', 'select GroupName from FDRTelegyrGroup' );
insert into fdrinterfaceoption values (12,'Point ID',1,'Text','(none)');
insert into fdrinterfaceoption values (13,'Point ID',1,'Text','(none)');

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
   add constraint PK_FDRTrans primary key  (POINTID, TRANSLATION)
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
Multiplier           float                not null
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
DEVICEID             numeric              not null,
CONTROLTYPE          varchar(20)          not null,
AVAILABLESEASONS     varchar(4)           not null,
AvailableWeekDays    varchar(8)           not null,
MAXHOURSDAILY        numeric              not null,
MAXHOURSMONTHLY      numeric              not null,
MAXHOURSSEASONAL     numeric              not null,
MAXHOURSANNUALLY     numeric              not null,
MINACTIVATETIME      numeric              not null,
MINRESTARTTIME       numeric              not null,
HolidayScheduleID    numeric              not null,
SeasonScheduleID     numeric              not null
)
go


alter table LMPROGRAM
   add constraint PK_LMPROGRAM primary key  (DEVICEID)
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
DeviceID             numeric              not null
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
GearID               numeric              not null
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
/* Table : MACROROUTE                                           */
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
FuncRequeue          numeric              not null
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
/* Table : PORTLOCALSERIAL                                      */
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


alter table PORTRADIOSETTINGS
   add constraint PK_PORTRADIOSETTINGS primary key  (PORTID)
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


alter table PORTSETTINGS
   add constraint PK_PORTSETTINGS primary key  (PORTID)
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
/* Table : PortStatistics                                       */
/*==============================================================*/
create table PortStatistics (
PORTID               numeric              not null,
STATISTICTYPE        numeric              not null,
ATTEMPTS             numeric              not null,
DATAERRORS           numeric              not null,
SYSTEMERRORS         numeric              not null,
STARTDATETIME        datetime             not null,
STOPDATETIME         datetime             not null
)
go


alter table PortStatistics
   add constraint PK_PORTSTATISTICS primary key  (PORTID, STATISTICTYPE)
go


/*==============================================================*/
/* Table : PortTiming                                           */
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
/* Table : RAWPOINTHISTORY                                      */
/*==============================================================*/
create table RAWPOINTHISTORY (
CHANGEID             numeric              not null,
POINTID              numeric              not null,
TIMESTAMP            datetime             not null,
QUALITY              numeric              not null,
VALUE                float                not null
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
VARIABLEBITS         numeric              not null,
REPEATERORDER        numeric              not null
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
INSERT INTO State VALUES(-5, 9, 'Priority 9', 6, 6, 0);
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
USERNAME             varchar(30)          null
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
insert into templatecolumns values( 1, 'Tags', 13, 6, 80 );

insert into templatecolumns values( 2, 'Device Name', 5, 1, 85 );
insert into templatecolumns values( 2, 'Value', 9, 2, 85 );
insert into templatecolumns values( 2, 'Quality', 10, 3, 80 );
insert into templatecolumns values( 2, 'Time', 11, 4, 135 );
insert into templatecolumns values( 2, 'Tags', 13, 5, 80 );

insert into templatecolumns values( 3, 'Point Name', 2, 1, 85 );
insert into templatecolumns values( 3, 'Value', 9, 2, 85 );
insert into templatecolumns values( 3, 'Quality', 10, 3, 80 );
insert into templatecolumns values( 3, 'Time', 11, 4, 135 );
insert into templatecolumns values( 3, 'Tags', 13, 5, 80 );


alter table TEMPLATECOLUMNS
   add constraint PK_TEMPLATECOLUMNS primary key  (TEMPLATENUM, TITLE)
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

alter table UNITMEASURE
   add constraint SYS_C0013344 primary key  (UOMID)
go


/*==============================================================*/
/* Table : VersacomRoute                                        */
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
/* Table : YukonGroup                                           */
/*==============================================================*/
create table YukonGroup (
GroupID              numeric              not null,
GroupName            varchar(120)         not null,
GroupDescription     varchar(200)         not null
)
go


insert into YukonGroup values(-1,'yukon','The default system user group that allows limited user interaction.');
insert into YukonGroup values(-100,'operators', 'The default group of yukon operators');
insert into yukongroup values(-200,'Esub Users', 'The default group of esubstation users');
insert into yukongroup values(-201,'Esub Operators', 'The default group of esubstation operators');


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
insert into YukonGroupRole values(1,-1,-1,-1000,'(none)');
insert into YukonGroupRole values(2,-1,-1,-1001,'(none)');
insert into YukonGroupRole values(3,-1,-1,-1002,'(none)');
insert into YukonGroupRole values(4,-1,-1,-1003,'(none)');
insert into YukonGroupRole values(5,-1,-1,-1004,'(none)');
insert into YukonGroupRole values(6,-1,-1,-1005,'(none)');
insert into YukonGroupRole values(7,-1,-1,-1006,'(none)');
insert into YukonGroupRole values(8,-1,-1,-1007,'(none)');
insert into YukonGroupRole values(9,-1,-1,-1008,'(none)');
insert into YukonGroupRole values(10,-1,-1,-1009,'(none)');

/* Assign roles to the default operator group to allow them to use all the main rich Yukon applications */
/* Database Editor */
insert into YukonGroupRole values(100,-100,-100,-10000,'(none)');
insert into YukonGroupRole values(101,-100,-100,-10001,'(none)');
insert into YukonGroupRole values(102,-100,-100,-10002,'(none)');
insert into YukonGroupRole values(103,-100,-100,-10003,'(none)');
insert into YukonGroupRole values(104,-100,-100,-10004,'(none)');
insert into YukonGroupRole values(105,-100,-100,-10005,'(none)');
insert into YukonGroupRole values(106,-100,-100,-10006,'(none)');
insert into YukonGroupRole values(107,-100,-100,-10007,'(none)');

/* TDC */
insert into YukonGroupRole values(120,-100,-101,-10100,'(none)');
insert into YukonGroupRole values(121,-100,-101,-10101,'(none)');
insert into YukonGroupRole values(122,-100,-101,-10102,'(none)');
insert into YukonGroupRole values(123,-100,-101,-10103,'(none)');
insert into YukonGroupRole values(124,-100,-101,-10104,'(none)');
insert into YukonGroupRole values(125,-100,-101,-10105,'(none)');
insert into YukonGroupRole values(126,-100,-101,-10106,'(none)');
insert into YukonGroupRole values(127,-100,-101,-10107,'(none)');
insert into YukonGroupRole values(128,-100,-101,-10108,'(none)');
insert into YukonGroupRole values(129,-100,-101,-10109,'(none)');
insert into YukonGroupRole values(130,-100,-101,-10110,'(none)');

/* Trending */
insert into YukonGroupRole values(150,-100,-102,-10200,'(none)');
insert into YukonGroupRole values(151,-100,-102,-10201,'(none)');

/* Commander */
insert into YukonGroupRole values(170,-100,-103,-10300,'(none)');

/* Calc Historical */
insert into YukonGroupRole values(190,-100,-104,-10400,'(none)');
insert into YukonGroupRole values(191,-100,-104,-10401,'(none)');
insert into YukonGroupRole values(192,-100,-104,-10402,'(none)');
insert into YukonGroupRole values(193,-100,-104,-10403,'(none)');

/* Web Graph */
insert into YukonGroupRole values(210,-100,-105,-10500,'(none)');
insert into YukonGroupRole values(211,-100,-105,-10501,'(none)');
insert into YukonGroupRole values(212,-100,-105,-10502,'(none)');

/* Billing */
insert into YukonGroupRole values(230,-100,-106,-10600,'(none)');
insert into YukonGroupRole values(231,-100,-106,-10601,'(none)');
insert into YukonGroupRole values(232,-100,-106,-10602,'(none)');

/* Esubstation Editor */
insert into YukonGroupRole values(250,-100,-107,-10700,'(none)');

/* Assign roles to the default Esub Users */
insert into YukonGroupRole values(300,-200,-206,-20600,'(none)');
insert into YukonGroupRole values(301,-200,-206,-20601,'(none)');
insert into YukonGroupRole values(302,-200,-206,-20602,'(none)');

/* Assign roles to the default Esub Operators */
insert into YukonGroupRole values(350,-201,-206,-20600,'(none)');
insert into YukonGroupRole values(351,-201,-206,-20601,'true');
insert into YukonGroupRole values(352,-201,-206,-20602,'false');



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
insert into YukonListEntry values (1003,1002,0,'Signup',1101);
insert into YukonListEntry values (1004,1002,0,'Activation Pending',1102);
insert into YukonListEntry values (1005,1002,0,'Activation Completed',1103);
insert into YukonListEntry values (1006,1002,0,'Termination',1104);
insert into YukonListEntry values (1007,1002,0,'Temp Opt Out',1105);
insert into YukonListEntry values (1008,1002,0,'Future Activation',1106);
insert into YukonListEntry values (1009,1002,0,'Install',1107);
insert into YukonListEntry values (1010,1002,0,'Configure',1108);
insert into YukonListEntry values (1011,1003,0,'OneWayReceiver',1201);
insert into YukonListEntry values (1012,1004,0,'120/120',0);
insert into YukonListEntry values (1013,1005,0,'LCR-5000',0);
insert into YukonListEntry values (1014,1005,0,'LCR-4000',0);
insert into YukonListEntry values (1015,1005,0,'LCR-3000',0);
insert into YukonListEntry values (1016,1005,0,'LCR-2000',0);
insert into YukonListEntry values (1017,1005,0,'LCR-1000',0);
insert into YukonListEntry values (1018,1005,0,'Thermostat',1301);
insert into YukonListEntry values (1019,1007,0,'Air Conditioner',1401);
insert into YukonListEntry values (1020,1007,0,'Water Heater',1402);
insert into YukonListEntry values (1021,1007,0,'Heating System',1403);
insert into YukonListEntry values (1022,1007,0,'Pool Pump',1404);
insert into YukonListEntry values (1023,1007,0,'Hot Tub',1405);
insert into YukonListEntry values (1024,1008,0,'General',0);
insert into YukonListEntry values (1025,1008,0,'Credit',0);
insert into YukonListEntry values (1026,1009,0,'Service Call',0);
insert into YukonListEntry values (1027,1009,0,'Install',0);
insert into YukonListEntry values (1028,1010,0,'Unscheduled',1501);
insert into YukonListEntry values (1029,1010,0,'Scheduled',1502);
insert into YukonListEntry values (1030,1010,0,'Completed',1503);
insert into YukonListEntry values (1031,1011,0,'Acct #',1601);
insert into YukonListEntry values (1032,1011,0,'Phone #',1602);
insert into YukonListEntry values (1033,1011,0,'Last name',1603);
insert into YukonListEntry values (1034,1011,0,'Serial #',1604);
insert into YukonListEntry values (1035,1006,0,'Available',1701);
insert into YukonListEntry values (1036,1006,0,'Temp Unavail',1702);
insert into YukonListEntry values (1037,1006,0,'Unavailable',1703);
insert into YukonListEntry values (1038,1012,0,'(Unknown);',1801);
insert into YukonListEntry values (1039,1012,0,'Century',0);
insert into YukonListEntry values (1040,1012,0,'Universal',0);
insert into YukonListEntry values (1041,1013,0,'(Unknown);',1901);
insert into YukonListEntry values (1042,1013,0,'Basement',0);
insert into YukonListEntry values (1043,1013,0,'North Side',0);
insert into YukonListEntry values (1044,1014,0,'Likely',2001);
insert into YukonListEntry values (1045,1014,0,'Unlikely',2002);
insert into YukonListEntry values (1046,1015,0,'Weekday',2101);
insert into YukonListEntry values (1047,1015,0,'Weekend',2102);
insert into YukonListEntry values (1048,1015,0,'Saturday',2103);
insert into YukonListEntry values (1049,1015,0,'Sunday',2104);
insert into YukonListEntry values (1050,1016,0,'Signup',2201);
insert into YukonListEntry values (1051,1016,0,'Exit',2202);
insert into YukonListEntry values (1052,1017,0,'Selection',2301);
insert into YukonListEntry values (1053,1017,0,'Free Form',2302);
insert into YukonListEntry values (1054,1018,0,'Cool',2401);
insert into YukonListEntry values (1055,1018,0,'Heat',2402);
insert into YukonListEntry values (1056,1018,0,'Off',2403);
insert into YukonListEntry values (1057,1019,0,'Auto',2501);
insert into YukonListEntry values (1058,1019,0,'On',2502);

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


/* Default role for all users */
insert into YukonRole values(-1,'Yukon','Yukon','Default Yukon role');
insert into YukonRole values(-2,'Energy Company','Yukon','Energy company role');

/* Application specific roles */
insert into YukonRole values(-100,'Database Editor','Application','Access to the Yukon Database Editor application');
insert into YukonRole values(-101,'Tabular Display Console','Application','Access to the Yukon Tabular Display Console application');
insert into YukonRole values(-102,'Trending','Application','Access to the Yukon Trending application');
insert into YukonRole values(-103,'Commander','Application','Access to the Yukon Commander application');
insert into YukonRole values(-104,'Calc Historical','Application','Calc Historical');
insert into YukonRole values(-105,'Web Graph','Application','Web Graph');
insert into YukonRole values(-106,'Billing','Application','Billing');
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

/* CI customer roles */
insert into YukonRole values(-300,'Direct Loadcontrol','CICustomer','Customer access to commercial/industrial customer direct loadcontrol');
insert into YukonRole values(-301,'Curtailment','CICustomer','Customer access to commercial/industrial customer direct curtailment');
insert into YukonRole values(-302,'Energy Buyback','CICustomer','Customer access to commercial/industrial customer energy buyback');
insert into YukonRole values(-303,'Esubstation Drawings','CICustomer','Customer access to esubstation drawings');
insert into YukonRole values(-304,'Commercial Metering','CICustomer','Customer access to commercial metering');

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

/* Database Editor Role */
insert into YukonRoleProperty values(-10000,-100,'point_id_edit','true','Controls whether point ids can be edited');
insert into YukonRoleProperty values(-10001,-100,'dbeditor_core','true','Controls whether the Core menu item in the View menu is displayed');
insert into YukonRoleProperty values(-10002,-100,'dbeditor_lm','true','Controls whether the Loadmanagement menu item in the View menu is displayed');
insert into YukonRoleProperty values(-10003,-100,'dbeditor_cap_control','true','Controls whether the Cap Control menu item in the View menu is displayed');
insert into YukonRoleProperty values(-10004,-100,'dbeditor_system','true','Controls whether the System menu item in the View menu is displayed');
insert into YukonRoleProperty values(-10005,-100,'utility_id_range','1-254','<description>');
insert into YukonRoleProperty values(-10006,-100,'client_log_level','INFO','Sets the logging level for the application.  Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-10007,-100,'dbeditor_trans_exclusion','false','Allows the editor panel for the mutual exclusion of transmissions to be shown');

/* Energy Company Role Properties */
insert into YukonRoleProperty values(-1100,-2,'admin_email_address','info@cannontech.com','Sender address of the emails sent by the STARS server');
insert into YukonRoleProperty values(-1101,-2,'optout_notification_recipients','info@cannontech.com','Recipients of the opt out notification email');
insert into YukonRoleProperty values(-1102,-2,'default_time_zone','CST','Default time zone of the energy company');
insert into YukonRoleProperty values(-1103,-2,'switch_command_file','c:/yukon/switch_command/default_switch.txt','Location of the file to temporarily store the switch commands');
insert into YukonRoleProperty values(-1104,-2,'optout_command_file','c:/yukon/switch_command/default_optout.txt','Location of the file to temporarily store the opt out commands');
insert into YukonRoleProperty values(-1105,-2,'customer_group_name','Web Demo Residential Customers','Group name of all the customer logins');

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
insert into YukonRoleProperty values(-10110,-101,'client_log_level','INFO','Sets the logging level for the application.  Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');

/* Trending Role */
insert into YukonRoleProperty values(-10200,-102,'graph_edit_graphdefinition','true','<description>');
insert into YukonRoleProperty values(-10201,-102,'client_log_level','INFO','Sets the logging level for the application.  Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');

/* Commander Role Properties */ 
insert into YukonRoleProperty values(-10300,-103,'client_log_level','INFO','Sets the logging level for the application.  Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');

/* Calc Historical Role Properties */
insert into YukonRoleProperty values(-10400,-104,'interval','900','<description>');
insert into YukonRoleProperty values(-10401,-104,'baseline_calctime','4','<description>');
insert into YukonRoleProperty values(-10402,-104,'daysprevioustocollect','30','<description>');
insert into YukonRoleProperty values(-10403,-104,'client_log_level','INFO','Sets the logging level for the application.  Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');

/* Web Graph Role Properties */
insert into YukonRoleProperty values(-10500,-105,'home_directory','c:\yukon\client\webgraphs','<description>');
insert into YukonRoleProperty values(-10501,-105,'run_interval','900','<description>');
insert into YukonRoleProperty values(-10502,-105,'client_log_level','INFO','Sets the logging level for the application.  Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');

/* Billing Role Properties */
insert into YukonRoleProperty values(-10600,-106,'wiz_activate','false','<description>');
insert into YukonRoleProperty values(-10601,-106,'input_file','c:\yukon\client\bin\BillingIn.txt','<description>');
insert into YukonRoleProperty values(-10602,-106,'client_log_level','INFO','Sets the logging level for the application.  Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');

/* Esubstation Editor Role Properties */
insert into YukonRoleProperty values(-10700,-107,'client_log_level','INFO','Sets the logging level for the application.  Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');

/* Web Client Role Properties */
insert into YukonRoleProperty values(-10800,-108,'home_url','/default.jsp','The url to take the user immediately after logging into the Yukon web applicatoin');
insert into YukonRoleProperty values(-10801,-108,'client_log_level','INFO','Sets the logging level for the application.  Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into yukonroleproperty values (-10802, -108,'style_sheet','CannonStyle.css','The web client cascading style sheet.');
insert into yukonroleproperty values (-10803, -108,'nav_bullet_selected','Bullet.gif','The bullet used when an item in the nav is selected.');
insert into yukonroleproperty values (-10804,-108,'nav_bullet','Bullet2.gif','The bullet used when an item in the nav is NOT selected.');
insert into yukonroleproperty values (-10805,-108,'header_logo','DemoHeader.gif','The main header logo');

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
insert into YukonRoleProperty values(-20130,-201,'Super Operator','false','Used for some testing functions (not recommended)');
insert into YukonRoleProperty values(-20131,-201,'New Account Wizard','true','Controls whether to enable the new account wizard');
insert into YukonRoleProperty values(-20132,-201,'Customized FAQ Link','false','Controls whether the FAQ link links to a customized location provided by the energy company');
insert into YukonRoleProperty values(-20150,-201,'Web Link FAQ','FAQ.jsp','The customized FAQ link provided by the energy company');
insert into YukonRoleProperty values(-20151,-201,'Web Text Control','control','The energy company specific term for control');
insert into YukonRoleProperty values(-20152,-201,'Recommended Settings Button','Recommended Settings','The energy company specific term for Recommended Settings button on the thermostat schedule page');
insert into YukonRoleProperty values(-20153,-201,'Programs Control History Title','PROGRAMS - CONTROL SUMMARY','Title of the programs control summary page');
insert into YukonRoleProperty values(-20154,-201,'Program Control History Title','PROGRAM - CONTROL HISTORY','Title of the control history page of a particular program');
insert into YukonRoleProperty values(-20155,-201,'Program Control Summary Title','PROGRAM - CONTROL SUMMARY','Title of the control summary page of a particular program');
insert into YukonRoleProperty values(-20156,-201,'Programs Enrollment Title','PROGRAMS - ENROLLMENT','Title of the programs enrollment page');
insert into YukonRoleProperty values(-20157,-201,'Programs Opt Out Title','PROGRAMS - OPT OUT','Title of the programs opt out page');
insert into YukonRoleProperty values(-20158,-201,'Change Login Title','ADMINISTRATION - CHANGE LOGIN','Title of the change login page');
insert into YukonRoleProperty values(-20159,-201,'Programs Control History Label','Control History','Text of the programs control history link');
insert into YukonRoleProperty values(-20160,-201,'Programs Enrollment Label','Enrollment','Text of the programs enrollment link');
insert into YukonRoleProperty values(-20161,-201,'Programs Opt Out Label','Opt Out','Text of the programs opt out link');
insert into YukonRoleProperty values(-20162,-201,'Opt Out Description','If you would like to temporarily opt out of all programs, select the time frame from the drop-down box below, then select Submit.','Description on the programs opt out page');

/* Operator Administrator Role Properties */
/* Operator Commercial Metering Role Properties*/
insert into YukonRoleProperty values(-20200,-202,'Trending Disclaimer',' ','The disclaimer that appears with trends');

/* Operator Direct Loadcontrol Role Properties */
insert into YukonRoleProperty values(-20300,-203,'Direct Loadcontrol Label','Direct Control','The operator specific name for direct loadcontrol');
insert into YukonRoleProperty values(-20301,-203,'Individual Switch','true','Controls access to operator individual switch control');

/* Operator Direct Curtailment Role Properties */
insert into YukonRoleProperty values(-20400,-204,'Direct Curtailment Label','Notification','The operator specific name for direct curtailment');

/* Operator Energy Exchange Role Properties */
insert into YukonRoleProperty values(-20500,-205,'Energy Buyback Label','Energy Buyback','The operator specific name for Energy Buyback');

/* Odds For Control Role Properties */
insert into YukonRoleProperty values(-20700,-207,'Odds For Control Label','Odds for Control','The operator specific name for odds for control');

/* Operator Hardware Inventory Role Properties */

/* CICustomer Direct Loadcontrol Role Properties */
insert into YukonRoleProperty values(-30000,-300,'Direct Loadcontrol Label','Direct Control','The customer specific name for direct loadcontrol');
insert into YukonRoleProperty values(-30001,-300,'Individual Switch','false','Controls access to customer individual switch control');

/* CICustomer Curtailment Role Properties */
insert into YukonRoleProperty values(-30100,-301,'Direct Curtailment Label','Notification','The customer specific name for direct curtailment');
insert into YukonRoleProperty values(-30101,-301,'Direct Curtailment Provider','<curtailment provider>','This customers direct curtailment provider');

/* CICustomer Energy Buyback Role Properties */
insert into YukonRoleProperty values(-30200,-302,'Energy Buyback Label','Energy Buyback','The customer specific name for Energy Buyback');
insert into YukonRoleProperty values(-30201,-302,'Energy Buyback Phone Number','1-800-555-5555','The phone number to call if the customer has questions');

/* CICustomer Esubstation Drawings Role Properties */
insert into YukonRoleProperty values(-30300,-303,'View Drawings','true','Controls viewing of Esubstations drawings');
insert into YukonRoleProperty values(-30301,-303,'Edit Limits','false','Controls editing of point limits');
insert into YukonRoleProperty values(-30302,-303,'Control','false','Controls control from Esubstation drawings');

/* CICustomer Commercial Metering Role Properties */
insert into YukonRoleProperty values(-30400,-304,'Trending Disclaimer',' ','The disclaimer that appears with trends');
insert into yukonroleproperty values(-30401, -304, 'Trending Get Data Now Button', 'false', 'Controls access to retrieve meter data on demand');

/* Operator Esubstation Drawings Role Properties */
insert into YukonRoleProperty values(-20600,-206,'View Drawings','true','Controls viewing of Esubstations drawings');
insert into YukonRoleProperty values(-20601,-206,'Edit Limits','false','Controls editing of point limits');
insert into YukonRoleProperty values(-20602,-206,'Control','false','Controls control from Esubstation drawings');

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
insert into YukonRoleProperty values(-40030,-400,'Notification on General Page','false','Controls whether to show the notification email box on the general page (useful only when the programs enrollment feature is not selected)');
insert into YukonRoleProperty values(-40031,-400,'Hide Opt Out Box','true','Controls whether to show the opt out box on the programs opt out page');
insert into YukonRoleProperty values(-40032,-400,'Customized FAQ Link','false','Controls whether the FAQ link links to a customized location provided by the energy company');
insert into YukonRoleProperty values(-40033,-400,'Customized Utility Email Link','false','Controls whether the utility email links to a customized location provided by the energy company');
insert into YukonRoleProperty values(-40050,-400,'Web Link FAQ','FAQ.jsp','The customized FAQ link provided by the energy company');
insert into YukonRoleProperty values(-40051,-400,'Web Link Utility Email','FAQ.jsp','The customized utility email provided by the energy company');
insert into YukonRoleProperty values(-40052,-400,'Web Text Control','control','The energy company specific term for control');
insert into YukonRoleProperty values(-40053,-400,'Web Text Controlled','controlled','The energy company specific term for controlled');
insert into YukonRoleProperty values(-40054,-400,'Web Text Controlling','controlling','The energy company specific term for controlling');
insert into YukonRoleProperty values(-40055,-400,'Recommended Settings Button','Recommended Settings','The energy company specific term for Recommended Settings button on the thermostat schedule page');
insert into YukonRoleProperty values(-40056,-400,'General Title','WELCOME TO ENERGY COMPANY SERVICES!','Title of the general page');
insert into YukonRoleProperty values(-40057,-400,'Programs Control History Title','PROGRAMS - CONTROL SUMMARY','Title of the programs control summary page');
insert into YukonRoleProperty values(-40058,-400,'Program Control History Title','PROGRAM - CONTROL HISTORY','Title of the control history page of a particular program');
insert into YukonRoleProperty values(-40059,-400,'Program Control Summary Title','PROGRAM - CONTROL SUMMARY','Title of the control summary page of a particular program');
insert into YukonRoleProperty values(-40060,-400,'Programs Enrollment Title','PROGRAMS - ENROLLMENT','Title of the programs enrollment page');
insert into YukonRoleProperty values(-40061,-400,'Programs Opt Out Title','PROGRAMS - OPT OUT','Title of the programs opt out page');
insert into YukonRoleProperty values(-40062,-400,'Utility Title','QUESTIONS - UTILITY','Title of the utility page');
insert into YukonRoleProperty values(-40063,-400,'Change Login Title','ADMINISTRATION - CHANGE LOGIN','Title of the change login page');
insert into YukonRoleProperty values(-40064,-400,'Programs Control History Label','Control History','Text of the programs control history link');
insert into YukonRoleProperty values(-40065,-400,'Programs Enrollment Label','Enrollment','Text of the programs enrollment link');
insert into YukonRoleProperty values(-40066,-400,'Programs Opt Out Label','Opt Out','Text of the programs opt out link');
insert into YukonRoleProperty values(-40067,-400,'General Description','Thank you for participating in our Consumer Energy Services programs. By participating, you have helped to optimize our delivery of energy, stabilize rates, and reduce energy costs. Best of all, you are saving energy dollars!<br><br>This site is designed to help manage your programs on-line from anywhere with access to a Web browser.','Description on the general page');
insert into YukonRoleProperty values(-40068,-400,'Opt Out Description','If you would like to temporarily opt out of all programs, select the time frame from the drop-down box below, then select Submit.','Description on the programs opt out page');

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
insert into YukonSelectionList values (1004,'A','(none)','Device voltage selection','DeviceVoltage','N');
insert into YukonSelectionList values (1005,'A','(none)','Device type selection','DeviceType','N');
insert into YukonSelectionList values (1006,'N','(none)','Hardware status selection', 'DeviceStatus','N');
insert into YukonSelectionList values (1007,'A','(none)','Appliance category','ApplianceCategory','N');
insert into YukonSelectionList values (1008,'A','(none)','Call type selection','CallType','N');
insert into YukonSelectionList values (1009,'A','(none)','Service type selection', 'ServiceType','N');
insert into YukonSelectionList values (1010,'N','(none)','Service request status', 'ServiceStatus','N');
insert into YukonSelectionList values (1011,'N','(none)','Search by selection','SearchBy','N');
insert into YukonSelectionList values (1012,'A','(none)','Appliance manufacturer selection', 'Manufacturer','N');
insert into YukonSelectionList values (1013,'A','(none)','Appliance location selection', 'ApplianceLocation','N');
insert into YukonSelectionList values (1014,'N','(none)','Chance of control selection', 'ChanceOfControl','N');
insert into YukonSelectionList values (1015,'N','(none)','Thermostat settings time of week selection', 'TimeOfWeek','N');
insert into YukonSelectionList values (1016,'N','(none)','Question type selection', 'QuestionType','N');
insert into YukonSelectionList values (1017,'N','(none)','Answer type selection','AnswerType','N');
insert into YukonSelectionList values (1018,'N','(none)','Thermostat mode selection', 'ThermostatMode','N');
insert into YukonSelectionList values (1019,'N','(none)','Thermostat fan state selection', 'ThermostatFanState','N');


alter table YukonSelectionList
   add constraint PK_YUKONSELECTIONLIST primary key  (ListID)
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


insert into YukonUser values(-1,'yukon','yukon',0,'01-JAN-00','Enabled');

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
insert into YukonUserGroup values(-1,-100);

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
AlternateDisplayName varchar(50)          null,
URL                  varchar(100)         null
)
go


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
      references LMPROGRAM (DEVICEID)
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


alter table CustomerBaseLine
   add constraint FK_CICst_CstBsLne foreign key (CustomerID)
      references CICustomerBase (CustomerID)
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


alter table DeviceDNP
   add constraint FK_Dev_DevDNP foreign key (DeviceID)
      references DEVICE (DEVICEID)
go


alter table DEVICE
   add constraint FK_Dev_YukPAO foreign key (DEVICEID)
      references YukonPAObject (PAObjectID)
go


alter table DynamicLMGroup
   add constraint FK_DyLmGr_LmPrDGr foreign key (LMProgramID)
      references LMProgramDirect (DeviceID)
go


alter table DynamicCalcHistorical
   add constraint FK_DynClc_ClcB foreign key (PointID)
      references CALCBASE (POINTID)
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


alter table LMPROGRAM
   add constraint FK_HlSc_LmPr foreign key (HolidayScheduleID)
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


alter table LMDirectOperatorList
   add constraint FK_LMDirOpLs_LMPrD foreign key (ProgramID)
      references LMProgramDirect (DeviceID)
go


alter table LMGroupMCT
   add constraint FK_LMGrMC_Dev foreign key (DeviceID)
      references DEVICE (DEVICEID)
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


alter table LMControlArea
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


alter table LMPROGRAM
   add constraint FK_SesSch_LmPr foreign key (SeasonScheduleID)
      references SeasonSchedule (ScheduleID)
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


alter table PortStatistics
   add constraint SYS_C0013183 foreign key (PORTID)
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


