/*==============================================================*/
/* Database name:  YUKON                                        */
/* DBMS name:      Microsoft SQL Server 7.x                     */
/* Created on:     4/11/2001 4:15:05 PM                         */
/*==============================================================*/


if exists (select 1
            from  sysindexes
           where  id    = object_id('DISPLAY')
            and   name  = 'Index_DISPLAYNAME'
            and   indid > 0
            and   indid < 255)
   drop index DISPLAY.Index_DISPLAYNAME
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('CustomerLogin')
            and   name  = 'Index_UserIDName'
            and   indid > 0
            and   indid < 255)
   drop index CustomerLogin.Index_UserIDName
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('CAPCONTROLSTRATEGY')
            and   name  = 'Indx_CAPCNTST2'
            and   indid > 0
            and   indid < 255)
   drop index CAPCONTROLSTRATEGY.Indx_CAPCNTST2
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('CAPCONTROLSTRATEGY')
            and   name  = 'Indx_CAPCNTSTR1'
            and   indid > 0
            and   indid < 255)
   drop index CAPCONTROLSTRATEGY.Indx_CAPCNTSTR1
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('COLUMNTYPE')
            and   name  = 'Indx_COLUMNTYPE'
            and   indid > 0
            and   indid < 255)
   drop index COLUMNTYPE.Indx_COLUMNTYPE
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('COMMPORT')
            and   name  = 'Indx_COMMPORT1'
            and   indid > 0
            and   indid < 255)
   drop index COMMPORT.Indx_COMMPORT1
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('COMMPORT')
            and   name  = 'Indx_COMMPORT2'
            and   indid > 0
            and   indid < 255)
   drop index COMMPORT.Indx_COMMPORT2
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('DEVICE')
            and   name  = 'Indx_DEVICE1'
            and   indid > 0
            and   indid < 255)
   drop index DEVICE.Indx_DEVICE1
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('DEVICE')
            and   name  = 'Indx_DEVICE2'
            and   indid > 0
            and   indid < 255)
   drop index DEVICE.Indx_DEVICE2
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('DISPLAY')
            and   name  = 'Indx_DISPLAY'
            and   indid > 0
            and   indid < 255)
   drop index DISPLAY.Indx_DISPLAY
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('GRAPHDATASERIES')
            and   name  = 'Indx_GPHDATSER'
            and   indid > 0
            and   indid < 255)
   drop index GRAPHDATASERIES.Indx_GPHDATSER
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('GRAPHDEFINITION')
            and   name  = 'Indx_GPHDEF'
            and   indid > 0
            and   indid < 255)
   drop index GRAPHDEFINITION.Indx_GPHDEF
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('LOGIC')
            and   name  = 'Indx_LOGIC'
            and   indid > 0
            and   indid < 255)
   drop index LOGIC.Indx_LOGIC
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('MCSCHEDULE')
            and   name  = 'Indx_MCSCHED'
            and   indid > 0
            and   indid < 255)
   drop index MCSCHEDULE.Indx_MCSCHED
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('NotificationGroup')
            and   name  = 'Indx_NOTIFGRP'
            and   indid > 0
            and   indid < 255)
   drop index NotificationGroup.Indx_NOTIFGRP
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('POINT')
            and   name  = 'Indx_POINT'
            and   indid > 0
            and   indid < 255)
   drop index POINT.Indx_POINT
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('PROGRAM')
            and   name  = 'Indx_PROGRAM1'
            and   indid > 0
            and   indid < 255)
   drop index PROGRAM.Indx_PROGRAM1
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('PROGRAM')
            and   name  = 'Indx_PROGRAM2'
            and   indid > 0
            and   indid < 255)
   drop index PROGRAM.Indx_PROGRAM2
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('RAWPOINTHISTORY')
            and   name  = 'Indx_RAWPTHIS'
            and   indid > 0
            and   indid < 255)
   drop index RAWPOINTHISTORY.Indx_RAWPTHIS
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('ROUTE')
            and   name  = 'Indx_ROUTE1'
            and   indid > 0
            and   indid < 255)
   drop index ROUTE.Indx_ROUTE1
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('ROUTE')
            and   name  = 'Indx_ROUTE2'
            and   indid > 0
            and   indid < 255)
   drop index ROUTE.Indx_ROUTE2
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('STATEGROUP')
            and   name  = 'Indx_STATEGRP1'
            and   indid > 0
            and   indid < 255)
   drop index STATEGROUP.Indx_STATEGRP1
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('STATEGROUP')
            and   name  = 'Indx_STATEGRP2'
            and   indid > 0
            and   indid < 255)
   drop index STATEGROUP.Indx_STATEGRP2
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('SYSTEMLOG')
            and   name  = 'Indx_SYSTEMLOG'
            and   indid > 0
            and   indid < 255)
   drop index SYSTEMLOG.Indx_SYSTEMLOG
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('TEMPLATE')
            and   name  = 'Indx_TEMPLATE'
            and   indid > 0
            and   indid < 255)
   drop index TEMPLATE.Indx_TEMPLATE
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('UNITMEASURE')
            and   name  = 'Indx_UOM'
            and   indid > 0
            and   indid < 255)
   drop index UNITMEASURE.Indx_UOM
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('USERINFO')
            and   name  = 'Indx_USERINFO'
            and   indid > 0
            and   indid < 255)
   drop index USERINFO.Indx_USERINFO
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('MACSchedule')
            and   name  = 'SchedNameIndx'
            and   indid > 0
            and   indid < 255)
   drop index MACSchedule.SchedNameIndx
go


if exists (select 1
            from  sysobjects
           where  id = object_id('AlarmState')
            and   type = 'U')
   drop table AlarmState
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
           where  id = object_id('CAPCONTROLSTRATEGY')
            and   type = 'U')
   drop table CAPCONTROLSTRATEGY
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CARRIERROUTE')
            and   type = 'U')
   drop table CARRIERROUTE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CCSTRATEGYBANKLIST')
            and   type = 'U')
   drop table CCSTRATEGYBANKLIST
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
           where  id = object_id('COMMUNICATIONROUTE')
            and   type = 'U')
   drop table COMMUNICATIONROUTE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerAddress')
            and   type = 'U')
   drop table CustomerAddress
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerContact')
            and   type = 'U')
   drop table CustomerContact
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerLogin')
            and   type = 'U')
   drop table CustomerLogin
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
           where  id = object_id('DYNAMICCAPCONTROLSTRATEGY')
            and   type = 'U')
   drop table DYNAMICCAPCONTROLSTRATEGY
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DYNAMICDEVICESCANDATA')
            and   type = 'U')
   drop table DYNAMICDEVICESCANDATA
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DynamicLMControlArea')
            and   type = 'U')
   drop table DynamicLMControlArea
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
           where  id = object_id('GroupRecipient')
            and   type = 'U')
   drop table GroupRecipient
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LASTPOINTSCAN')
            and   type = 'U')
   drop table LASTPOINTSCAN
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
           where  id = object_id('LMGROUPVERSACOMSERIAL')
            and   type = 'U')
   drop table LMGROUPVERSACOMSERIAL
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMGroup')
            and   type = 'U')
   drop table LMGroup
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
           where  id = object_id('MCSCHEDULE')
            and   type = 'U')
   drop table MCSCHEDULE
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
           where  id = object_id('POINTCONTROL')
            and   type = 'U')
   drop table POINTCONTROL
go


if exists (select 1
            from  sysobjects
           where  id = object_id('POINTDISPATCH')
            and   type = 'U')
   drop table POINTDISPATCH
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
           where  id = object_id('PROGRAM')
            and   type = 'U')
   drop table PROGRAM
go


if exists (select 1
            from  sysobjects
           where  id = object_id('PointAlarming')
            and   type = 'U')
   drop table PointAlarming
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
            from  sysobjects
           where  id = object_id('ROUTE')
            and   type = 'U')
   drop table ROUTE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('SERIALNUMBER')
            and   type = 'U')
   drop table SERIALNUMBER
go


if exists (select 1
            from  sysobjects
           where  id = object_id('STARTABSOLUTETIME')
            and   type = 'U')
   drop table STARTABSOLUTETIME
go


if exists (select 1
            from  sysobjects
           where  id = object_id('STARTDATETIME')
            and   type = 'U')
   drop table STARTDATETIME
go


if exists (select 1
            from  sysobjects
           where  id = object_id('STARTDELTATIME')
            and   type = 'U')
   drop table STARTDELTATIME
go


if exists (select 1
            from  sysobjects
           where  id = object_id('STARTDOMTIME')
            and   type = 'U')
   drop table STARTDOMTIME
go


if exists (select 1
            from  sysobjects
           where  id = object_id('STARTDOWTIME')
            and   type = 'U')
   drop table STARTDOWTIME
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
           where  id = object_id('STOPABSOLUTETIME')
            and   type = 'U')
   drop table STOPABSOLUTETIME
go


if exists (select 1
            from  sysobjects
           where  id = object_id('STOPDURATIONTIME')
            and   type = 'U')
   drop table STOPDURATIONTIME
go


if exists (select 1
            from  sysobjects
           where  id = object_id('SYSTEMLOG')
            and   type = 'U')
   drop table SYSTEMLOG
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
           where  id = object_id('USERINFO')
            and   type = 'U')
   drop table USERINFO
go


if exists (select 1
            from  sysobjects
           where  id = object_id('USERPROGRAM')
            and   type = 'U')
   drop table USERPROGRAM
go


if exists (select 1
            from  sysobjects
           where  id = object_id('USERWEB')
            and   type = 'U')
   drop table USERWEB
go


if exists (select 1
            from  sysobjects
           where  id = object_id('UserGraph')
            and   type = 'U')
   drop table UserGraph
go


if exists (select 1
            from  sysobjects
           where  id = object_id('VERSACOMROUTE')
            and   type = 'U')
   drop table VERSACOMROUTE
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DISPLAY2WAYDATA_VIEW')
            and   type = 'V')
   drop view DISPLAY2WAYDATA_VIEW
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMCurtailCustomerActivity_View')
            and   type = 'V')
   drop view LMCurtailCustomerActivity_View
go


if exists (select 1
            from  sysobjects
           where  id = object_id('POINTHISTORY')
            and   type = 'V')
   drop view POINTHISTORY
go


/*==============================================================*/
/* Table : AlarmState                                           */
/*==============================================================*/
create table AlarmState (
AlarmStateID         numeric                        not null,
AlarmStateName       varchar(40)                    not null,
NotificationGroupID  numeric                        not null,
constraint PK_ALARMSTATE primary key  (AlarmStateID)
)
go


insert into alarmstate values(1,'None',1);
insert into alarmstate values(2,'Priority 1',1);
insert into alarmstate values(3,'Priority 2',1);
insert into alarmstate values(4,'Priority 3',1);
insert into alarmstate values(5,'Priority 4',1);
insert into alarmstate values(6,'Priority 5',1);
insert into alarmstate values(7,'Priority 6',1);
insert into alarmstate values(8,'Priority 7',1);
insert into alarmstate values(9,'Priority 8',1);
insert into alarmstate values(10,'Priority 9',1);
insert into alarmstate values(11,'Priority 10',1);

/*==============================================================*/
/* Table : CALCBASE                                             */
/*==============================================================*/
create table CALCBASE (
POINTID              numeric                        not null
	
	
	constraint SYS_C0013431 check ("POINTID" IS NOT NULL),
UPDATETYPE           varchar(16)                    not null
	
	
	constraint SYS_C0013432 check ("UPDATETYPE" IS NOT NULL),
PERIODICRATE         numeric                        not null
	
	
	constraint SYS_C0013433 check ("PERIODICRATE" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : CALCCOMPONENT                                        */
/*==============================================================*/
create table CALCCOMPONENT (
POINTID              numeric                        not null
	
	
	constraint SYS_C0013435 check ("POINTID" IS NOT NULL),
COMPONENTORDER       numeric                        not null
	
	
	constraint SYS_C0013436 check ("COMPONENTORDER" IS NOT NULL),
COMPONENTTYPE        varchar(10)                    not null
	
	
	constraint SYS_C0013437 check ("COMPONENTTYPE" IS NOT NULL),
COMPONENTPOINTID     numeric                        not null
	
	
	constraint SYS_C0013438 check ("COMPONENTPOINTID" IS NOT NULL),
OPERATION            varchar(10)                    null,
CONSTANT             float                          not null
	
	
	constraint SYS_C0013439 check ("CONSTANT" IS NOT NULL),
FUNCTIONNAME         varchar(20)                    null
)
go


/*==============================================================*/
/* Table : CAPBANK                                              */
/*==============================================================*/
create table CAPBANK (
DEVICEID             numeric                        not null
	
	
	constraint SYS_C0013446 check ("DEVICEID" IS NOT NULL),
BANKADDRESS          varchar(40)                    not null
	
	
	constraint SYS_C0013447 check ("BANKADDRESS" IS NOT NULL),
OPERATIONALSTATE     varchar(8)                     not null
	
	
	constraint SYS_C0013448 check ("OPERATIONALSTATE" IS NOT NULL),
CONTROLPOINTID       numeric                        not null
	
	
	constraint SYS_C0013449 check ("CONTROLPOINTID" IS NOT NULL),
BANKSIZE             float                          not null
	
	
	constraint SYS_C0013450 check ("BANKSIZE" IS NOT NULL),
CONTROLDEVICEID      numeric                        not null
	
	
	constraint SYS_C0013451 check ("CONTROLDEVICEID" IS NOT NULL),
constraint PK_CAPBANK primary key  (BANKADDRESS)
)
go


/*==============================================================*/
/* Table : CAPCONTROLSTRATEGY                                   */
/*==============================================================*/
create table CAPCONTROLSTRATEGY (
CAPSTRATEGYID        numeric                        not null,
STRATEGYNAME         varchar(30)                    not null
	
	
	constraint SYS_C0013461 check ("STRATEGYNAME" IS NOT NULL),
DISTRICTNAME         varchar(30)                    not null
	
	
	constraint SYS_C0013462 check ("DISTRICTNAME" IS NOT NULL),
ACTUALVARPOINTID     numeric                        not null
	
	
	constraint SYS_C0013463 check ("ACTUALVARPOINTID" IS NOT NULL),
MAXDAILYOPERATION    numeric                        not null
	
	
	constraint SYS_C0013464 check ("MAXDAILYOPERATION" IS NOT NULL),
PEAKSETPOINT         float                          not null
	
	
	constraint SYS_C0013465 check ("PEAKSETPOINT" IS NOT NULL),
OFFPEAKSETPOINT      float                          not null
	
	
	constraint SYS_C0013466 check ("OFFPEAKSETPOINT" IS NOT NULL),
PEAKSTARTTIME        datetime                       not null
	
	
	constraint SYS_C0013467 check ("PEAKSTARTTIME" IS NOT NULL),
PEAKSTOPTIME         datetime                       not null
	
	
	constraint SYS_C0013468 check ("PEAKSTOPTIME" IS NOT NULL),
CALCULATEDVARLOADPOINTID numeric                        not null
	
	
	constraint SYS_C0013469 check ("CALCULATEDVARLOADPOINTID" IS NOT NULL),
BANDWIDTH            float                          not null
	
	
	constraint SYS_C0013470 check ("BANDWIDTH" IS NOT NULL),
CONTROLINTERVAL      numeric                        not null
	
	
	constraint SYS_C0013471 check ("CONTROLINTERVAL" IS NOT NULL),
MINRESPONSETIME      numeric                        not null
	
	
	constraint SYS_C0013472 check ("MINRESPONSETIME" IS NOT NULL),
MINCONFIRMPERCENT    numeric                        not null
	
	
	constraint SYS_C0013473 check ("MINCONFIRMPERCENT" IS NOT NULL),
FAILUREPERCENT       numeric                        not null
	
	
	constraint SYS_C0013474 check ("FAILUREPERCENT" IS NOT NULL),
STATUS               varchar(12)                    not null
	
	
	constraint SYS_C0013475 check ("STATUS" IS NOT NULL),
DAYSOFWEEK           char(8)                        not null
	
	
	constraint SYS_C0014656 check ("DAYSOFWEEK" IS NOT NULL),
constraint SYS_C0013476 primary key  (CAPSTRATEGYID)
)
go


/*==============================================================*/
/* Table : CARRIERROUTE                                         */
/*==============================================================*/
create table CARRIERROUTE (
ROUTEID              numeric                        not null
	
	
	constraint SYS_C0013259 check ("ROUTEID" IS NOT NULL),
BUSNUMBER            numeric                        not null
	
	
	constraint SYS_C0013260 check ("BUSNUMBER" IS NOT NULL),
AMPUSETYPE           varchar(20)                    not null
	
	
	constraint SYS_C0013261 check ("AMPUSETYPE" IS NOT NULL),
CCUFIXBITS           numeric                        not null
	
	
	constraint SYS_C0013262 check ("CCUFIXBITS" IS NOT NULL),
CCUVARIABLEBITS      numeric                        not null
	
	
	constraint SYS_C0013263 check ("CCUVARIABLEBITS" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : CCSTRATEGYBANKLIST                                   */
/*==============================================================*/
create table CCSTRATEGYBANKLIST (
CAPSTRATEGYID        numeric                        not null
	
	
	constraint SYS_C0013480 check ("CAPSTRATEGYID" IS NOT NULL),
DEVICEID             numeric                        not null
	
	
	constraint SYS_C0013481 check ("DEVICEID" IS NOT NULL),
CONTROLORDER         numeric                        not null
	
	
	constraint SYS_C0013482 check ("CONTROLORDER" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : CICustContact                                        */
/*==============================================================*/
create table CICustContact (
DeviceID             numeric                        not null,
ContactID            numeric                        not null,
constraint PK_CICUSTCONTACT primary key  (ContactID, DeviceID)
)
go


/*==============================================================*/
/* Table : CICustomerBase                                       */
/*==============================================================*/
create table CICustomerBase (
DeviceID             numeric                        not null,
AddressID            numeric                        not null,
MainPhoneNumber      varchar(18)                    not null,
MainFaxNumber        varchar(18)                    not null,
CustPDL              float                          not null,
PrimeContactID       numeric                        not null,
CustTimeZone         varchar(6)                     not null,
CurtailmentAgreement varchar(100)                   not null,
constraint PK_CICUSTOMERBASE primary key  (DeviceID)
)
go


/*==============================================================*/
/* Table : COLUMNTYPE                                           */
/*==============================================================*/
create table COLUMNTYPE (
TYPENUM              numeric                        not null,
NAME                 varchar(20)                    not null
	
	
	constraint SYS_C0013413 check ("NAME" IS NOT NULL),
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
PORTID               numeric                        not null,
DESCRIPTION          varchar(20)                    not null
	
	
	constraint SYS_C0013104 check ("DESCRIPTION" IS NOT NULL),
PORTTYPE             varchar(30)                    not null
	
	
	constraint SYS_C0013105 check ("PORTTYPE" IS NOT NULL),
CURRENTSTATE         varchar(8)                     not null
	
	
	constraint SYS_C0013106 check ("CURRENTSTATE" IS NOT NULL),
DISABLEFLAG          varchar(1)                     not null
	
	
	constraint SYS_C0013107 check ("DISABLEFLAG" IS NOT NULL),
ALARMINHIBIT         varchar(1)                     not null
	
	
	constraint SYS_C0013108 check ("ALARMINHIBIT" IS NOT NULL),
COMMONPROTOCOL       varchar(8)                     not null
	
	
	constraint SYS_C0013109 check ("COMMONPROTOCOL" IS NOT NULL),
PERFORMTHRESHOLD     numeric                        not null
	
	
	constraint SYS_C0013110 check ("PERFORMTHRESHOLD" IS NOT NULL),
PERFORMANCEALARM     varchar(1)                     not null
	
	
	constraint SYS_C0013111 check ("PERFORMANCEALARM" IS NOT NULL),
constraint SYS_C0013112 primary key  (PORTID)
)
go


/*==============================================================*/
/* Table : COMMUNICATIONROUTE                                   */
/*==============================================================*/
create table COMMUNICATIONROUTE (
ROUTEID              numeric                        not null
	
	
	constraint SYS_C0013254 check ("ROUTEID" IS NOT NULL),
DEVICEID             numeric                        not null
	
	
	constraint SYS_C0013255 check ("DEVICEID" IS NOT NULL),
DEFAULTROUTE         varchar(1)                     not null
	
	
	constraint SYS_C0013256 check ("DEFAULTROUTE" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : CustomerAddress                                      */
/*==============================================================*/
create table CustomerAddress (
AddressID            numeric                        not null,
LocationAddress1     varchar(40)                    not null,
LocationAddress2     varchar(40)                    not null,
CityName             varchar(32)                    not null,
StateCode            char(2)                        not null,
ZipCode              varchar(12)                    not null,
constraint PK_CUSTOMERADDRESS primary key  (AddressID)
)
go


/*==============================================================*/
/* Table : CustomerContact                                      */
/*==============================================================*/
create table CustomerContact (
ContactID            numeric                        not null,
ContFirstName        varchar(20)                    not null,
ContLastName         varchar(32)                    not null,
ContPhone1           varchar(14)                    not null,
ContPhone2           varchar(14)                    not null,
LocationID           numeric                        not null,
LogInID              numeric                        not null,
constraint PK_CUSTOMERCONTACT primary key  (ContactID)
)
go


insert into CustomerContact(contactID, contFirstName, contLastName, contPhone1, contPhone2, locationID,loginID)
values (-1,'(none)','(none)','(none)','(none)',0,-1)

/*==============================================================*/
/* Table : CustomerLogin                                        */
/*==============================================================*/
create table CustomerLogin (
LogInID              numeric                        not null,
UserName             varchar(20)                    not null,
Password             varchar(20)                    not null,
LoginType            varchar(25)                    not null,
LoginCount           numeric                        not null,
LastLogin            datetime                       not null,
Status               varchar(20)                    not null,
constraint PK_CUSTOMERLOGIN primary key  (LogInID)
)
go


insert into CustomerLogin(LogInID,UserName,Password,LoginType,LoginCount,LastLogin,Status)
values (-1,'(none)','(none)','(none)',0,'01-JAN-1990', 'Disabled');

/*==============================================================*/
/* Table : DEVICE                                               */
/*==============================================================*/
create table DEVICE (
DEVICEID             numeric                        not null,
NAME                 varchar(60)                    not null
	
	
	constraint SYS_C0013114 check ("NAME" IS NOT NULL),
TYPE                 varchar(18)                    not null
	
	
	constraint SYS_C0013115 check ("TYPE" IS NOT NULL),
CURRENTSTATE         varchar(8)                     not null
	
	
	constraint SYS_C0013116 check ("CURRENTSTATE" IS NOT NULL),
DISABLEFLAG          varchar(1)                     not null
	
	
	constraint SYS_C0013117 check ("DISABLEFLAG" IS NOT NULL),
ALARMINHIBIT         varchar(1)                     not null
	
	
	constraint SYS_C0013118 check ("ALARMINHIBIT" IS NOT NULL),
CONTROLINHIBIT       varchar(1)                     not null
	
	
	constraint SYS_C0013119 check ("CONTROLINHIBIT" IS NOT NULL),
CLASS                varchar(12)                    not null
	
	
	constraint SYS_C0013120 check ("CLASS" IS NOT NULL),
constraint SYS_C0013121 primary key  (DEVICEID)
)
go


INSERT into device values (0, 'System Device', 'System', 'Normal', 'N', 'N', 'N', 'System');

/*==============================================================*/
/* Table : DEVICE2WAYFLAGS                                      */
/*==============================================================*/
create table DEVICE2WAYFLAGS (
DEVICEID             numeric                        not null
	
	
	constraint SYS_C0013199 check ("DEVICEID" IS NOT NULL),
MONTHLYSTATS         varchar(1)                     not null
	
	
	constraint SYS_C0013200 check ("MONTHLYSTATS" IS NOT NULL),
TWENTYFOURHOURSTATS  varchar(1)                     not null
	
	
	constraint SYS_C0013201 check ("TWENTYFOURHOURSTATS" IS NOT NULL),
HOURLYSTATS          varchar(1)                     not null
	
	
	constraint SYS_C0013202 check ("HOURLYSTATS" IS NOT NULL),
FAILUREALARM         varchar(1)                     not null
	
	
	constraint SYS_C0013203 check ("FAILUREALARM" IS NOT NULL),
PERFORMANCETHRESHOLD numeric                        not null
	
	
	constraint SYS_C0013204 check ("PERFORMANCETHRESHOLD" IS NOT NULL),
PERFORMANCEALARM     varchar(1)                     not null
	
	
	constraint SYS_C0013205 check ("PERFORMANCEALARM" IS NOT NULL),
PERFORMANCETWENTYFOURALARM varchar(1)                     not null
	
	
	constraint SYS_C0013206 check ("PERFORMANCETWENTYFOURALARM" IS NOT NULL),
CONFIGURATIONNAME    varchar(14)                    not null
	
	
	constraint SYS_C0013207 check ("CONFIGURATIONNAME" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : DEVICECARRIERSETTINGS                                */
/*==============================================================*/
create table DEVICECARRIERSETTINGS (
DEVICEID             numeric                        not null
	
	
	constraint SYS_C0013214 check ("DEVICEID" IS NOT NULL),
ADDRESS              numeric                        not null
	
	
	constraint SYS_C0013215 check ("ADDRESS" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : DEVICECBC                                            */
/*==============================================================*/
create table DEVICECBC (
DEVICEID             numeric                        not null
	
	
	constraint SYS_C0013456 check ("DEVICEID" IS NOT NULL),
SERIALNUMBER         numeric                        not null
	
	
	constraint SYS_C0013457 check ("SERIALNUMBER" IS NOT NULL),
ROUTEID              numeric                        not null
	
	
	constraint SYS_C0013458 check ("ROUTEID" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : DEVICEDIALUPSETTINGS                                 */
/*==============================================================*/
create table DEVICEDIALUPSETTINGS (
DEVICEID             numeric                        not null
	
	
	constraint SYS_C0013188 check ("DEVICEID" IS NOT NULL),
PHONENUMBER          varchar(40)                    not null
	
	
	constraint SYS_C0013189 check ("PHONENUMBER" IS NOT NULL),
MINCONNECTTIME       numeric                        not null
	
	
	constraint SYS_C0013190 check ("MINCONNECTTIME" IS NOT NULL),
MAXCONNECTTIME       numeric                        not null
	
	
	constraint SYS_C0013191 check ("MAXCONNECTTIME" IS NOT NULL),
LINESETTINGS         varchar(4)                     not null
	
	
	constraint SYS_C0013192 check ("LINESETTINGS" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : DEVICEDIRECTCOMMSETTINGS                             */
/*==============================================================*/
create table DEVICEDIRECTCOMMSETTINGS (
DEVICEID             numeric                        not null
	
	
	constraint SYS_C0013184 check ("DEVICEID" IS NOT NULL),
PORTID               numeric                        not null
	
	
	constraint SYS_C0013185 check ("PORTID" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : DEVICEIDLCREMOTE                                     */
/*==============================================================*/
create table DEVICEIDLCREMOTE (
DEVICEID             numeric                        not null
	
	
	constraint SYS_C0013238 check ("DEVICEID" IS NOT NULL),
ADDRESS              numeric                        not null
	
	
	constraint SYS_C0013239 check ("ADDRESS" IS NOT NULL),
POSTCOMMWAIT         numeric                        not null
	
	
	constraint SYS_C0013240 check ("POSTCOMMWAIT" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : DEVICEIED                                            */
/*==============================================================*/
create table DEVICEIED (
DEVICEID             numeric                        not null
	
	
	constraint SYS_C0013242 check ("DEVICEID" IS NOT NULL),
PASSWORD             varchar(20)                    not null
	
	
	constraint SYS_C0013243 check ("PASSWORD" IS NOT NULL),
SLAVEADDRESS         varchar(20)                    not null
	
	
	constraint SYS_C0013244 check ("SLAVEADDRESS" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : DEVICELOADPROFILE                                    */
/*==============================================================*/
create table DEVICELOADPROFILE (
DEVICEID             numeric                        not null
	
	
	constraint SYS_C0013230 check ("DEVICEID" IS NOT NULL),
LASTINTERVALDEMANDRATE numeric                        not null
	
	
	constraint SYS_C0013231 check ("LASTINTERVALDEMANDRATE" IS NOT NULL),
LOADPROFILEDEMANDRATE numeric                        not null
	
	
	constraint SYS_C0013232 check ("LOADPROFILEDEMANDRATE" IS NOT NULL),
LOADPROFILECOLLECTION varchar(4)                     not null
	
	
	constraint SYS_C0013233 check ("LOADPROFILECOLLECTION" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : DEVICEMCTIEDPORT                                     */
/*==============================================================*/
create table DEVICEMCTIEDPORT (
DEVICEID             numeric                        not null
	
	
	constraint SYS_C0013246 check ("DEVICEID" IS NOT NULL),
CONNECTEDIED         varchar(20)                    not null
	
	
	constraint SYS_C0013247 check ("CONNECTEDIED" IS NOT NULL),
IEDSCANRATE          numeric                        not null
	
	
	constraint SYS_C0013248 check ("IEDSCANRATE" IS NOT NULL),
DEFAULTDATACLASS     numeric                        not null
	
	
	constraint SYS_C0013249 check ("DEFAULTDATACLASS" IS NOT NULL),
DEFAULTDATAOFFSET    numeric                        not null
	
	
	constraint SYS_C0013250 check ("DEFAULTDATAOFFSET" IS NOT NULL),
PASSWORD             varchar(6)                     not null
	
	
	constraint SYS_C0013251 check ("PASSWORD" IS NOT NULL),
REALTIMESCAN         varchar(1)                     not null
	
	
	constraint SYS_C0013252 check ("REALTIMESCAN" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : DEVICEMETERGROUP                                     */
/*==============================================================*/
create table DEVICEMETERGROUP (
DEVICEID             numeric                        not null
	
	
	constraint SYS_C0013209 check ("DEVICEID" IS NOT NULL),
CYCLEGROUP           varchar(20)                    not null
	
	
	constraint SYS_C0013210 check ("CYCLEGROUP" IS NOT NULL),
AREACODEGROUP        varchar(20)                    not null
	
	
	constraint SYS_C0013211 check ("AREACODEGROUP" IS NOT NULL),
METERNUMBER          varchar(15)                    not null
	
	
	constraint SYS_C0013212 check ("METERNUMBER" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : DEVICEROUTES                                         */
/*==============================================================*/
create table DEVICEROUTES (
DEVICEID             numeric                        not null
	
	
	constraint SYS_C0013217 check ("DEVICEID" IS NOT NULL),
ROUTEID              numeric                        not null
	
	
	constraint SYS_C0013218 check ("ROUTEID" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : DEVICESCANRATE                                       */
/*==============================================================*/
create table DEVICESCANRATE (
DEVICEID             numeric                        not null
	
	
	constraint SYS_C0013194 check ("DEVICEID" IS NOT NULL),
SCANTYPE             varchar(20)                    not null
	
	
	constraint SYS_C0013195 check ("SCANTYPE" IS NOT NULL),
INTERVALRATE         numeric                        not null
	
	
	constraint SYS_C0013196 check ("INTERVALRATE" IS NOT NULL),
SCANGROUP            numeric                        not null
	
	
	constraint SYS_C0013197 check ("SCANGROUP" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : DEVICESTATISTICS                                     */
/*==============================================================*/
create table DEVICESTATISTICS (
DEVICEID             numeric                        not null
	
	
	constraint SYS_C0013221 check ("DEVICEID" IS NOT NULL),
STATISTICTYPE        varchar(16)                    not null
	
	
	constraint SYS_C0013222 check ("STATISTICTYPE" IS NOT NULL),
ATTEMPTS             numeric                        not null
	
	
	constraint SYS_C0013223 check ("ATTEMPTS" IS NOT NULL),
COMLINEERRORS        numeric                        not null
	
	
	constraint SYS_C0013224 check ("COMLINEERRORS" IS NOT NULL),
SYSTEMERRORS         numeric                        not null
	
	
	constraint SYS_C0013225 check ("SYSTEMERRORS" IS NOT NULL),
DLCERRORS            numeric                        not null
	
	
	constraint SYS_C0013226 check ("DLCERRORS" IS NOT NULL),
STARTDATETIME        datetime                       not null
	
	
	constraint SYS_C0013227 check ("STARTDATETIME" IS NOT NULL),
STOPDATETIME         datetime                       not null
	
	
	constraint SYS_C0013228 check ("STOPDATETIME" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : DEVICETAPPAGINGSETTINGS                              */
/*==============================================================*/
create table DEVICETAPPAGINGSETTINGS (
DEVICEID             numeric                        not null
	
	
	constraint SYS_C0013235 check ("DEVICEID" IS NOT NULL),
PAGERNUMBER          varchar(20)                    not null
	
	
	constraint SYS_C0013236 check ("PAGERNUMBER" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : DISPLAY                                              */
/*==============================================================*/
create table DISPLAY (
DISPLAYNUM           numeric                        not null
	
	
	constraint SYS_C0013409 check ("DISPLAYNUM" IS NOT NULL),
NAME                 varchar(40)                    not null
	
	
	constraint SYS_C0013410 check ("NAME" IS NOT NULL),
TYPE                 varchar(20)                    not null
	
	
	constraint SYS_C0013411 check ("TYPE" IS NOT NULL),
TITLE                varchar(30)                    null,
DESCRIPTION          varchar(200)                   null,
constraint SYS_C0013412 primary key  (DISPLAYNUM)
)
go


insert into display values(-1, 'Scheduler Client', 'Scheduler Client', 'Metering And Control Scheduler', 'com.cannontech.macs.gui.Scheduler');
insert into display values(-2, 'All Areas', 'Cap Control Client', 'Capacitor Bank Controller', 'com.cannontech.cbc.gui.StrategyReceiver');
insert into display values(-3, 'Load Control Client', 'Load Control Client', 'Load Control', 'com.cannontech.loadcontrol.gui.LoadControlMainPanel');

insert into display values(99, 'Basic User Created', 'Custom Displays', 'A Predefined Generic Display', 'This display is is used to show what a user created display looks like.');
insert into display values(1, 'Event Viewer', 'Alarms and Events', 'Current Event Viewer', 'This display will recieve current events as they happen in the system.');
insert into display values(2, 'Historical Viewer', 'Alarms and Events', 'Historical Event Viewer', 'This display will allow the user to select a range of dates and show the events that occured.');
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
insert into display values(14, 'Priority 10 Alarms', 'Alarms and Events', 'Priority 10 Alarm Viewer', 'This display will recieve all priority 10 alarm events as they happen in the system.');

/*==============================================================*/
/* Table : DISPLAY2WAYDATA                                      */
/*==============================================================*/
create table DISPLAY2WAYDATA (
DISPLAYNUM           numeric                        not null,
ORDERING             numeric                        not null
	
	
	constraint SYS_C0013421 check ("ORDERING" IS NOT NULL),
POINTID              numeric                        null
)
go


/*==============================================================*/
/* Table : DISPLAYCOLUMNS                                       */
/*==============================================================*/
create table DISPLAYCOLUMNS (
DISPLAYNUM           numeric                        not null,
TITLE                varchar(50)                    null,
TYPENUM              numeric                        not null
	
	
	constraint SYS_C0013415 check ("TYPENUM" IS NOT NULL),
ORDERING             numeric                        not null
	
	
	constraint SYS_C0013416 check ("ORDERING" IS NOT NULL),
WIDTH                numeric                        not null
	
	
	constraint SYS_C0013417 check ("WIDTH" IS NOT NULL)
)
go


insert into displaycolumns values(99, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(99, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(99, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(99, 'Value', 9, 4, 160 );

insert into displaycolumns values(1, 'Time Stamp', 11, 1, 60 );
insert into displaycolumns values(1, 'Device Name', 5, 2, 70 );
insert into displaycolumns values(1, 'Point Name', 2, 3, 70 );
insert into displaycolumns values(1, 'Text Message', 12, 4, 180 );
insert into displaycolumns values(1, 'Additional Info', 10, 5, 180 );
insert into displaycolumns values(1, 'User Name', 8, 6, 35 );

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

/*==============================================================*/
/* Table : DYNAMICACCUMULATOR                                   */
/*==============================================================*/
create table DYNAMICACCUMULATOR (
POINTID              numeric                        null,
PREVIOUSPULSES       numeric                        not null
	
	
	constraint SYS_C0015127 check ("PREVIOUSPULSES" IS NOT NULL),
PRESENTPULSES        numeric                        not null
	
	
	constraint SYS_C0015128 check ("PRESENTPULSES" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : DYNAMICCAPCONTROLSTRATEGY                            */
/*==============================================================*/
create table DYNAMICCAPCONTROLSTRATEGY (
CapStrategyID        numeric                        not null,
NewPointDataReceived char(1)                        not null,
StrategyUpdated      char(1)                        not null,
ActualVarPointValue  float                          not null,
NextCheckTime        datetime                       not null,
CalcVarPointValue    float                          not null,
Operations           numeric                        not null,
LastOperation        datetime                       not null,
LastCapBankControlled numeric                        not null,
PeakOrOffPeak        char(1)                        not null,
RecentlyControlled   char(1)                        not null,
CalcValueBeforeControl float                          not null,
TimeStamp            datetime                       not null,
constraint PK_DYNAMICCAPCONTROLSTRATEGY primary key  (CapStrategyID)
)
go


/*==============================================================*/
/* Table : DYNAMICDEVICESCANDATA                                */
/*==============================================================*/
create table DYNAMICDEVICESCANDATA (
DEVICEID             numeric                        null,
LASTFREEZETIME       datetime                       not null
	
	
	constraint SYS_C0015130 check ("LASTFREEZETIME" IS NOT NULL),
PREVFREEZETIME       datetime                       not null
	
	
	constraint SYS_C0015131 check ("PREVFREEZETIME" IS NOT NULL),
LASTLPTIME           datetime                       not null
	
	
	constraint SYS_C0015132 check ("LASTLPTIME" IS NOT NULL),
LASTFREEZENUMBER     numeric                        not null
	
	
	constraint SYS_C0015133 check ("LASTFREEZENUMBER" IS NOT NULL),
PREVFREEZENUMBER     numeric                        not null
	
	
	constraint SYS_C0015134 check ("PREVFREEZENUMBER" IS NOT NULL),
NEXTSCAN0            datetime                       not null
	
	
	constraint SYS_C0015135 check ("NEXTSCAN0" IS NOT NULL),
NEXTSCAN1            datetime                       not null
	
	
	constraint SYS_C0015136 check ("NEXTSCAN1" IS NOT NULL),
NEXTSCAN2            datetime                       not null
	
	
	constraint SYS_C0015137 check ("NEXTSCAN2" IS NOT NULL),
NEXTSCAN3            datetime                       not null
	
	
	constraint SYS_C0015138 check ("NEXTSCAN3" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : DynamicLMControlArea                                 */
/*==============================================================*/
create table DynamicLMControlArea (
DeviceID             numeric                        not null,
NextCheckTime        datetime                       not null,
NewPointDataReceivedFlag char(1)                        not null,
UpdatedFlag          char(1)                        not null,
CurrentPriority      numeric                        not null,
TimeStamp            datetime                       not null,
constraint PK_DYNAMICLMCONTROLAREA primary key  (DeviceID)
)
go


/*==============================================================*/
/* Table : DynamicLMGroup                                       */
/*==============================================================*/
create table DynamicLMGroup (
DeviceID             numeric                        not null,
GroupControlState    numeric                        not null,
CurrentHoursDaily    numeric                        not null,
CurrentHoursMonthly  numeric                        not null,
CurrentHoursSeasonal numeric                        not null,
CurrentHoursAnnually numeric                        not null,
LastControlSent      datetime                       not null,
TimeStamp            datetime                       not null,
constraint PK_DYNAMICLMGROUP primary key  (DeviceID)
)
go


/*==============================================================*/
/* Table : DynamicLMProgram                                     */
/*==============================================================*/
create table DynamicLMProgram (
DeviceID             numeric                        not null,
ProgramState         numeric                        not null,
ReductionTotal       float                          not null,
StartedControlling   datetime                       not null,
LastControlSent      datetime                       not null,
ManualControlReceivedFlag char(1)                        not null,
TimeStamp            datetime                       not null,
constraint PK_DYNAMICLMPROGRAM primary key  (DeviceID)
)
go


/*==============================================================*/
/* Table : DynamicLMProgramDirect                               */
/*==============================================================*/
create table DynamicLMProgramDirect (
DeviceID             numeric                        not null,
CurrentGearNumber    numeric                        not null,
LastGroupControlled  numeric                        not null,
TimeStamp            datetime                       not null,
constraint PK_DYNAMICLMPROGRAMDIRECT primary key  (DeviceID)
)
go


/*==============================================================*/
/* Table : FDRTRANSLATION                                       */
/*==============================================================*/
create table FDRTRANSLATION (
POINTID              numeric                        not null
	
	
	constraint SYS_C0015061 check ("POINTID" IS NOT NULL),
DIRECTIONTYPE        varchar(20)                    not null
	
	
	constraint SYS_C0015062 check ("DIRECTIONTYPE" IS NOT NULL),
INTERFACETYPE        varchar(20)                    not null
	
	
	constraint SYS_C0015063 check ("INTERFACETYPE" IS NOT NULL),
DESTINATION          varchar(20)                    not null
	
	
	constraint SYS_C0015064 check ("DESTINATION" IS NOT NULL),
TRANSLATION          varchar(100)                   not null
	
	
	constraint SYS_C0015065 check ("TRANSLATION" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : GRAPHDATASERIES                                      */
/*==============================================================*/
create table GRAPHDATASERIES (
GRAPHDATASERIESID    numeric                        not null,
GRAPHDEFINITIONID    numeric                        not null
	
	
	constraint SYS_C0015111 check ("GRAPHDEFINITIONID" IS NOT NULL),
POINTID              numeric                        not null
	
	
	constraint SYS_C0015112 check ("POINTID" IS NOT NULL),
Label                varchar(40)                    not null,
Axis                 char(1)                        not null,
Color                numeric                        not null,
Type                 varchar(12)                    not null,
constraint SYS_C0015113 primary key  (GRAPHDATASERIESID)
)
go


/*==============================================================*/
/* Table : GRAPHDEFINITION                                      */
/*==============================================================*/
create table GRAPHDEFINITION (
GRAPHDEFINITIONID    numeric                        not null,
NAME                 varchar(40)                    not null
	
	
	constraint SYS_C0015108 check ("NAME" IS NOT NULL),
AutoScaleTimeAxis    char(1)                        not null,
AutoScaleLeftAxis    char(1)                        not null,
AutoScaleRightAxis   char(1)                        not null,
STARTDATE            datetime                       not null,
STOPDATE             datetime                       not null,
LeftMin              float                          not null,
LeftMax              float                          not null,
RightMin             float                          not null,
RightMax             float                          not null,
Type                 char(1)                        not null,
constraint SYS_C0015109 primary key  (GRAPHDEFINITIONID)
)
go


/*==============================================================*/
/* Table : GroupRecipient                                       */
/*==============================================================*/
create table GroupRecipient (
LocationID           numeric                        not null,
LocationName         varchar(30)                    not null,
EmailAddress         varchar(60)                    not null,
EmailSendType        numeric                        not null,
PagerNumber          varchar(20)                    not null,
DisableFlag          char(1)                        not null,
constraint PK_GROUPRECIPIENT primary key  (LocationID)
)
go


insert into GroupRecipient values(0,'(none)','(none)',1,'(none)','N');

/*==============================================================*/
/* Table : LASTPOINTSCAN                                        */
/*==============================================================*/
create table LASTPOINTSCAN (
POINTID              numeric                        null,
VALUE                float                          not null
	
	
	constraint SYS_C0013345 check ("VALUE" IS NOT NULL),
TIME                 datetime                       not null
	
	
	constraint SYS_C0013346 check ("TIME" IS NOT NULL),
QUALITY              numeric                        not null
	
	
	constraint SYS_C0013347 check ("QUALITY" IS NOT NULL),
ALARMSTATE           numeric                        not null
	
	
	constraint SYS_C0013348 check ("ALARMSTATE" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : LMCONTROLAREA                                        */
/*==============================================================*/
create table LMCONTROLAREA (
DEVICEID             numeric                        not null,
DEFOPERATIONALSTATE  varchar(20)                    not null,
CONTROLINTERVAL      numeric                        not null,
MINRESPONSETIME      numeric                        not null,
DEFDAILYSTARTTIME    numeric                        not null,
DEFDAILYSTOPTIME     numeric                        not null,
REQUIREALLTRIGGERSACTIVEFLAG varchar(1)                     not null,
constraint PK_LMCONTROLAREA primary key  (DEVICEID)
)
go


/*==============================================================*/
/* Table : LMCONTROLAREAPROGRAM                                 */
/*==============================================================*/
create table LMCONTROLAREAPROGRAM (
DEVICEID             numeric                        not null,
LMPROGRAMDEVICEID    numeric                        not null,
USERORDER            numeric                        not null,
STOPORDER            numeric                        not null,
DEFAULTPRIORITY      numeric                        not null
)
go


/*==============================================================*/
/* Table : LMCONTROLAREATRIGGER                                 */
/*==============================================================*/
create table LMCONTROLAREATRIGGER (
DEVICEID             numeric                        not null,
TRIGGERNUMBER        numeric                        not null,
TRIGGERTYPE          varchar(20)                    not null,
POINTID              numeric                        not null,
NORMALSTATE          numeric                        not null,
THRESHOLD            float                          not null,
PROJECTIONTYPE       varchar(14)                    not null,
PROJECTIONPOINTS     numeric                        not null,
PROJECTAHEADDURATION numeric                        not null,
THRESHOLDKICKPERCENT numeric                        not null,
MINRESTOREOFFSET     float                          not null,
PEAKPOINTID          numeric                        not null
)
go


/*==============================================================*/
/* Table : LMCurtailCustomerActivity                            */
/*==============================================================*/
create table LMCurtailCustomerActivity (
CustomerID           numeric                        not null,
AcknowledgeStatus    varchar(30)                    not null,
AckDateTime          datetime                       not null,
IPAddressOfAckUser   varchar(15)                    not null,
UserIDName           varchar(40)                    not null,
NameOfAckPerson      varchar(40)                    not null,
CurtailmentNotes     varchar(120)                   not null,
CurrentPDL           float                          not null,
AckLateFlag          char(1)                        not null,
constraint PK_LMCURTAILCUSTOMERACTIVITY primary key  (CustomerID)
)
go


/*==============================================================*/
/* Table : LMCurtailProgramActivity                             */
/*==============================================================*/
create table LMCurtailProgramActivity (
DeviceID             numeric                        not null,
CurtailReferenceID   numeric                        not null,
ActionDateTime       datetime                       not null,
NotificationDateTime datetime                       not null,
CurtailmentStartTime datetime                       not null,
CurtailmentStopTime  datetime                       not null,
RunStatus            varchar(20)                    not null,
AdditionalInfo       varchar(100)                   not null,
constraint PK_LMCURTAILPROGRAMACTIVITY primary key  (CurtailReferenceID)
)
go


/*==============================================================*/
/* Table : LMGROUPEMETCON                                       */
/*==============================================================*/
create table LMGROUPEMETCON (
DEVICEID             numeric                        not null,
GOLDADDRESS          numeric                        not null
	
	
	constraint SYS_C0013351 check ("GOLDADDRESS" IS NOT NULL),
SILVERADDRESS        numeric                        not null
	
	
	constraint SYS_C0013352 check ("SILVERADDRESS" IS NOT NULL),
ADDRESSUSAGE         char(1)                        not null
	
	
	constraint SYS_C0013353 check ("ADDRESSUSAGE" IS NOT NULL),
RELAYUSAGE           char(1)                        not null
	
	
	constraint SYS_C0013354 check ("RELAYUSAGE" IS NOT NULL),
ROUTEID              numeric                        not null
	
	
	constraint SYS_C0013355 check ("ROUTEID" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : LMGROUPVERSACOM                                      */
/*==============================================================*/
create table LMGROUPVERSACOM (
DEVICEID             numeric                        not null,
ROUTEID              numeric                        not null
	
	
	constraint SYS_C0013359 check ("ROUTEID" IS NOT NULL),
UTILITYADDRESS       numeric                        not null
	
	
	constraint SYS_C0013360 check ("UTILITYADDRESS" IS NOT NULL),
SECTIONADDRESS       numeric                        not null
	
	
	constraint SYS_C0013361 check ("SECTIONADDRESS" IS NOT NULL),
CLASSADDRESS         numeric                        not null
	
	
	constraint SYS_C0013362 check ("CLASSADDRESS" IS NOT NULL),
DIVISIONADDRESS      numeric                        not null
	
	
	constraint SYS_C0013363 check ("DIVISIONADDRESS" IS NOT NULL),
ADDRESSUSAGE         char(4)                        not null
	
	
	constraint SYS_C0013364 check ("ADDRESSUSAGE" IS NOT NULL),
RELAYUSAGE           char(3)                        not null
	
	
	constraint SYS_C0013365 check ("RELAYUSAGE" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : LMGROUPVERSACOMSERIAL                                */
/*==============================================================*/
create table LMGROUPVERSACOMSERIAL (
DEVICEID             numeric                        not null
	
	
	constraint SYS_C0013368 check ("DEVICEID" IS NOT NULL),
DEVICEIDOFGROUP      numeric                        not null
	
	
	constraint SYS_C0013369 check ("DEVICEIDOFGROUP" IS NOT NULL),
ROUTEID              numeric                        not null
	
	
	constraint SYS_C0013370 check ("ROUTEID" IS NOT NULL),
SERIALNUMBER         numeric                        not null
	
	
	constraint SYS_C0013371 check ("SERIALNUMBER" IS NOT NULL),
RELAYUSAGE           char(3)                        not null
	
	
	constraint SYS_C0013372 check ("RELAYUSAGE" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : LMGroup                                              */
/*==============================================================*/
create table LMGroup (
DeviceID             numeric                        not null,
KWCapacity           float                          not null,
RecordControlHistoryFlag varchar(1)                     not null,
constraint PK_LMGROUP primary key  (DeviceID)
)
go


/*==============================================================*/
/* Table : LMPROGRAM                                            */
/*==============================================================*/
create table LMPROGRAM (
DEVICEID             numeric                        not null,
CONTROLTYPE          varchar(20)                    not null,
AVAILABLESEASONS     varchar(4)                     not null,
AVAILABLEWEEKDAYS    varchar(8)                     not null,
MAXHOURSDAILY        numeric                        not null,
MAXHOURSMONTHLY      numeric                        not null,
MAXHOURSSEASONAL     numeric                        not null,
MAXHOURSANNUALLY     numeric                        not null,
MINACTIVATETIME      numeric                        not null,
MINRESTARTTIME       numeric                        not null,
constraint PK_LMPROGRAM primary key  (DEVICEID)
)
go


/*==============================================================*/
/* Table : LMProgramControlWindow                               */
/*==============================================================*/
create table LMProgramControlWindow (
DeviceID             numeric                        not null,
WindowNumber         numeric                        not null,
AvailableStartTime   numeric                        not null,
AvailableStopTime    numeric                        not null
)
go


/*==============================================================*/
/* Table : LMProgramCurtailCustomerList                         */
/*==============================================================*/
create table LMProgramCurtailCustomerList (
DeviceID             numeric                        not null,
LMCustomerDeviceID   numeric                        not null,
CustomerOrder        numeric                        not null,
RequireAck           char(1)                        not null,
constraint PK_LMPROGRAMCURTAILCUSTOMERLIS primary key  (LMCustomerDeviceID, DeviceID)
)
go


/*==============================================================*/
/* Table : LMProgramCurtailment                                 */
/*==============================================================*/
create table LMProgramCurtailment (
DeviceID             numeric                        not null,
MinNotifyTime        numeric                        not null,
Heading              varchar(40)                    not null,
MessageHeader        varchar(160)                   not null,
MessageFooter        varchar(160)                   not null,
AckTimeLimit         numeric                        not null,
CanceledMsg          varchar(80)                    not null
	default 'THIS CURTAILMENT HAS BEEN CANCELED, PLEASE DISREGARD.',
StoppedEarlyMsg      varchar(80)                    not null
	default 'THIS CURTAILMENT HAS STOPPED EARLY, YOU MAY RESUME NORMAL OPERATIONS.',
constraint PK_LMPROGRAMCURTAILMENT primary key  (DeviceID)
)
go


/*==============================================================*/
/* Table : LMProgramDirect                                      */
/*==============================================================*/
create table LMProgramDirect (
DeviceID             numeric                        not null,
GroupSelectionMethod varchar(30)                    not null,
constraint PK_LMPROGRAMDIRECT primary key  (DeviceID)
)
go


/*==============================================================*/
/* Table : LMProgramDirectGear                                  */
/*==============================================================*/
create table LMProgramDirectGear (
DeviceID             numeric                        not null,
GearName             varchar(30)                    not null,
GearNumber           numeric                        not null,
ControlMethod        varchar(30)                    not null,
MethodRate           numeric                        not null,
MethodPeriod         numeric                        not null,
MethodRateCount      numeric                        not null,
CycleRefreshRate     numeric                        not null,
MethodStopType       varchar(20)                    not null,
ChangeCondition      varchar(24)                    not null,
ChangeDuration       numeric                        not null,
ChangePriority       numeric                        not null,
ChangeTriggerNumber  numeric                        not null,
ChangeTriggerOffset  float                          not null,
PercentReduction     numeric                        not null
)
go


/*==============================================================*/
/* Table : LMProgramDirectGroup                                 */
/*==============================================================*/
create table LMProgramDirectGroup (
DeviceID             numeric                        not null,
LMGroupDeviceID      numeric                        not null,
GroupOrder           numeric                        not null
)
go


/*==============================================================*/
/* Table : LOGIC                                                */
/*==============================================================*/
create table LOGIC (
LOGICID              numeric                        not null,
LOGICNAME            varchar(20)                    not null
	
	
	constraint SYS_C0013441 check ("LOGICNAME" IS NOT NULL),
PERIODICRATE         numeric                        not null
	
	
	constraint SYS_C0013442 check ("PERIODICRATE" IS NOT NULL),
STATEFLAG            varchar(10)                    not null
	
	
	constraint SYS_C0013443 check ("STATEFLAG" IS NOT NULL),
SCRIPTNAME           varchar(20)                    not null
	
	
	constraint SYS_C0013444 check ("SCRIPTNAME" IS NOT NULL),
constraint SYS_C0013445 primary key  (LOGICID)
)
go


/*==============================================================*/
/* Table : MACROROUTE                                           */
/*==============================================================*/
create table MACROROUTE (
ROUTEID              numeric                        not null
	
	
	constraint SYS_C0013271 check ("ROUTEID" IS NOT NULL),
SINGLEROUTEID        numeric                        not null
	
	
	constraint SYS_C0013272 check ("SINGLEROUTEID" IS NOT NULL),
ROUTEORDER           numeric                        not null
	
	
	constraint SYS_C0013273 check ("ROUTEORDER" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : MACSchedule                                          */
/*==============================================================*/
create table MACSchedule (
ScheduleID           int                            not null,
ScheduleName         varchar(50)                    not null,
CategoryName         varchar(20)                    not null,
ScheduleType         varchar(12)                    not null,
HolidayScheduleID    int                            null,
CommandFile          varchar(180)                   null,
CurrentState         varchar(12)                    not null,
StartPolicy          varchar(20)                    not null,
StopPolicy           varchar(20)                    not null,
LastRunTime          datetime                       null,
LastRunStatus        varchar(12)                    null,
StartDay             int                            null,
StartMonth           int                            null,
StartYear            int                            null,
StartTime            varchar(8)                     null,
StopTime             varchar(8)                     null,
ValidWeekDays        char(8)                        null,
Duration             int                            null,
ManualStartTime      datetime                       null,
ManualStopTime       datetime                       null,
constraint PK_MACSCHEDULE primary key  (ScheduleID)
)
go


/*==============================================================*/
/* Table : MACSimpleSchedule                                    */
/*==============================================================*/
create table MACSimpleSchedule (
ScheduleID           int                            not null,
TargetSelect         varchar(40)                    null,
StartCommand         varchar(120)                   null,
StopCommand          varchar(120)                   null,
RepeatInterval       int                            null,
constraint PK_MACSIMPLESCHEDULE primary key  (ScheduleID)
)
go


/*==============================================================*/
/* Table : MCSCHEDULE                                           */
/*==============================================================*/
create table MCSCHEDULE (
SCHEDULEID           numeric                        not null,
SCHEDULENAME         varchar(20)                    not null
	
	
	constraint SYS_C0013376 check ("SCHEDULENAME" IS NOT NULL),
COMMANDFILE          varchar(48)                    not null
	
	
	constraint SYS_C0013377 check ("COMMANDFILE" IS NOT NULL),
CURRENTSTATE         varchar(12)                    not null
	
	
	constraint SYS_C0013378 check ("CURRENTSTATE" IS NOT NULL),
STARTTYPE            varchar(20)                    not null
	
	
	constraint SYS_C0013379 check ("STARTTYPE" IS NOT NULL),
STOPTYPE             varchar(20)                    not null
	
	
	constraint SYS_C0013380 check ("STOPTYPE" IS NOT NULL),
LASTRUNTIME          datetime                       not null
	
	
	constraint SYS_C0013381 check ("LASTRUNTIME" IS NOT NULL),
LASTRUNSTATUS        varchar(12)                    not null
	
	
	constraint SYS_C0013382 check ("LASTRUNSTATUS" IS NOT NULL),
constraint SYS_C0013383 primary key  (SCHEDULEID)
)
go


/*==============================================================*/
/* Table : NotificationDestination                              */
/*==============================================================*/
create table NotificationDestination (
DestinationOrder     numeric                        not null,
NotificationGroupID  numeric                        not null,
LocationID           numeric                        not null,
constraint PK_NOTIFICATIONDESTINATION primary key  (NotificationGroupID, DestinationOrder)
)
go


/*==============================================================*/
/* Table : NotificationGroup                                    */
/*==============================================================*/
create table NotificationGroup (
NotificationGroupID  numeric                        not null,
GroupName            varchar(40)                    not null,
EmailSubject         varchar(30)                    not null,
EmailFromAddress     varchar(40)                    not null,
EmailMessage         varchar(160)                   not null,
NumericPagerMessage  varchar(14)                    not null,
DisableFlag          char(1)                        not null,
constraint PK_NOTIFICATIONGROUP primary key  (NotificationGroupID)
)
go


insert into notificationgroup values(1,'None','None','None','None','None','N');

/*==============================================================*/
/* Table : POINT                                                */
/*==============================================================*/
create table POINT (
POINTID              numeric                        not null,
POINTTYPE            varchar(20)                    not null
	
	
	constraint SYS_C0013130 check ("POINTTYPE" IS NOT NULL),
POINTNAME            varchar(60)                    not null
	
	
	constraint SYS_C0013131 check ("POINTNAME" IS NOT NULL),
DEVICEID             numeric                        not null
	
	
	constraint SYS_C0013132 check ("DEVICEID" IS NOT NULL),
LOGICALGROUP         varchar(14)                    not null
	
	
	constraint SYS_C0013133 check ("LOGICALGROUP" IS NOT NULL),
STATEGROUPID         numeric                        not null
	
	
	constraint SYS_C0013134 check ("STATEGROUPID" IS NOT NULL),
SERVICEFLAG          varchar(1)                     not null
	
	
	constraint SYS_C0013135 check ("SERVICEFLAG" IS NOT NULL),
ALARMINHIBIT         varchar(1)                     not null
	
	
	constraint SYS_C0013136 check ("ALARMINHIBIT" IS NOT NULL),
ALARMCLASS           numeric                        not null
	
	
	constraint SYS_C0013137 check ("ALARMCLASS" IS NOT NULL),
PSEUDOFLAG           varchar(1)                     not null
	
	
	constraint SYS_C0013138 check ("PSEUDOFLAG" IS NOT NULL),
POINTOFFSET          numeric                        not null
	
	
	constraint SYS_C0013139 check ("POINTOFFSET" IS NOT NULL),
ARCHIVETYPE          varchar(12)                    not null
	
	
	constraint SYS_C0013140 check ("ARCHIVETYPE" IS NOT NULL),
ARCHIVEINTERVAL      numeric                        not null
	
	
	constraint SYS_C0013141 check ("ARCHIVEINTERVAL" IS NOT NULL),
constraint SYS_C0013142 primary key  (POINTID)
)
go


INSERT into point  values (0,   'System', 'System Point', 0, 'Default', 0, 'N', 'N', 0, 'S', 0  ,'None', 0);
INSERT into point  values (-1,  'System', 'Porter', 0, 'Default', 0, 'N', 'N', 0, 'S', 1  ,'None', 0);
INSERT into point  values (-2,  'System', 'Scanner', 0, 'Default', 0, 'N', 'N', 0, 'S', 2  ,'None', 0);
INSERT into point  values (-3,  'System', 'Dispatch', 0, 'Default', 0, 'N', 'N', 0, 'S', 3  ,'None', 0);
INSERT into point  values (-4,  'System', 'Macs', 0, 'Default', 0, 'N', 'N', 0, 'S', 4  ,'None', 0);
INSERT into point  values (-5,  'System', 'Cap Control', 0, 'Default', 0, 'N', 'N', 0, 'S', 5  ,'None', 0);
INSERT into point  values (-10, 'System', 'Load Management' , 0, 'Default', 0, 'N', 'N', 0, 'S', 10 ,'None', 0);

/*==============================================================*/
/* Table : POINTACCUMULATOR                                     */
/*==============================================================*/
create table POINTACCUMULATOR (
POINTID              numeric                        not null
	
	
	constraint SYS_C0013313 check ("POINTID" IS NOT NULL),
MULTIPLIER           float                          not null
	
	
	constraint SYS_C0013314 check ("MULTIPLIER" IS NOT NULL),
DATAOFFSET           float                          not null
	
	
	constraint SYS_C0013315 check ("DATAOFFSET" IS NOT NULL),
READINGTYPE          varchar(14)                    not null
	
	
	constraint SYS_C0013316 check ("READINGTYPE" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : POINTANALOG                                          */
/*==============================================================*/
create table POINTANALOG (
POINTID              numeric                        not null
	
	
	constraint SYS_C0013295 check ("POINTID" IS NOT NULL),
DEADBAND             float                          not null
	
	
	constraint SYS_C0013296 check ("DEADBAND" IS NOT NULL),
TRANSDUCERTYPE       varchar(14)                    not null
	
	
	constraint SYS_C0013297 check ("TRANSDUCERTYPE" IS NOT NULL),
MULTIPLIER           float                          not null
	
	
	constraint SYS_C0013298 check ("MULTIPLIER" IS NOT NULL),
DATAOFFSET           float                          not null
	
	
	constraint SYS_C0013299 check ("DATAOFFSET" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : POINTCONTROL                                         */
/*==============================================================*/
create table POINTCONTROL (
POINTID              numeric                        not null
	
	
	constraint SYS_C0013308 check ("POINTID" IS NOT NULL),
CONTROLOFFSET        numeric                        not null
	
	
	constraint SYS_C0013309 check ("CONTROLOFFSET" IS NOT NULL),
CLOSETIME1           numeric                        not null
	
	
	constraint SYS_C0013310 check ("CLOSETIME1" IS NOT NULL),
CLOSETIME2           numeric                        not null
	
	
	constraint SYS_C0013311 check ("CLOSETIME2" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : POINTDISPATCH                                        */
/*==============================================================*/
create table POINTDISPATCH (
POINTID              numeric                        not null
	
	
	constraint SYS_C0013324 check ("POINTID" IS NOT NULL),
TIMESTAMP            datetime                       not null
	
	
	constraint SYS_C0013325 check ("TIMESTAMP" IS NOT NULL),
QUALITY              numeric                        not null
	
	
	constraint SYS_C0013326 check ("QUALITY" IS NOT NULL),
VALUE                float                          not null
	
	
	constraint SYS_C0013327 check ("VALUE" IS NOT NULL),
TAGS                 numeric                        not null
	
	
	constraint SYS_C0013328 check ("TAGS" IS NOT NULL),
NEXTARCHIVE          datetime                       not null
	
	
	constraint SYS_C0013329 check ("NEXTARCHIVE" IS NOT NULL),
STALECOUNT           numeric                        not null
	
	
	constraint SYS_C0013330 check ("STALECOUNT" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : POINTLIMITS                                          */
/*==============================================================*/
create table POINTLIMITS (
POINTID              numeric                        not null
	
	
	constraint SYS_C0013283 check ("POINTID" IS NOT NULL),
LIMITNUMBER          numeric                        not null
	
	
	constraint SYS_C0013284 check ("LIMITNUMBER" IS NOT NULL),
HIGHLIMIT            float                          not null
	
	
	constraint SYS_C0013285 check ("HIGHLIMIT" IS NOT NULL),
LOWLIMIT             float                          not null
	
	
	constraint SYS_C0013286 check ("LOWLIMIT" IS NOT NULL),
DATAFILTERTYPE       varchar(14)                    not null
	
	
	constraint SYS_C0013287 check ("DATAFILTERTYPE" IS NOT NULL),
NAME                 varchar(14)                    not null
	
	
	constraint SYS_C0013288 check ("NAME" IS NOT NULL),
LIMITDURATION        numeric                        not null
)
go


/*==============================================================*/
/* Table : POINTSTATUS                                          */
/*==============================================================*/
create table POINTSTATUS (
POINTID              numeric                        not null
	
	
	constraint SYS_C0013301 check ("POINTID" IS NOT NULL),
NORMALSTATE          numeric                        not null
	
	
	constraint SYS_C0013302 check ("NORMALSTATE" IS NOT NULL),
ALARMSTATE           numeric                        not null
	
	
	constraint SYS_C0013303 check ("ALARMSTATE" IS NOT NULL),
INITIALSTATE         numeric                        not null
	
	
	constraint SYS_C0013304 check ("INITIALSTATE" IS NOT NULL),
CONTROLTYPE          varchar(12)                    not null
	
	
	constraint SYS_C0013305 check ("CONTROLTYPE" IS NOT NULL),
CONTROLINHIBIT       varchar(1)                     not null
	
	
	constraint SYS_C0013306 check ("CONTROLINHIBIT" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : POINTUNIT                                            */
/*==============================================================*/
create table POINTUNIT (
POINTID              numeric                        not null
	
	
	constraint SYS_C0013290 check ("POINTID" IS NOT NULL),
UNIT                 varchar(10)                    not null
	
	
	constraint SYS_C0013291 check ("UNIT" IS NOT NULL),
LOGFREQUENCY         numeric                        not null
	
	
	constraint SYS_C0013292 check ("LOGFREQUENCY" IS NOT NULL),
DEFAULTVALUE         float                          not null
	
	
	constraint SYS_C0013293 check ("DEFAULTVALUE" IS NOT NULL),
DECIMALPLACES        numeric                        not null
)
go


/*==============================================================*/
/* Table : PORTDIALUPMODEM                                      */
/*==============================================================*/
create table PORTDIALUPMODEM (
PORTID               numeric                        not null
	
	
	constraint SYS_C0013170 check ("PORTID" IS NOT NULL),
MODEMTYPE            varchar(30)                    not null
	
	
	constraint SYS_C0013171 check ("MODEMTYPE" IS NOT NULL),
INITIALIZATIONSTRING varchar(50)                    not null
	
	
	constraint SYS_C0013172 check ("INITIALIZATIONSTRING" IS NOT NULL),
PREFIXNUMBER         varchar(10)                    not null
	
	
	constraint SYS_C0013173 check ("PREFIXNUMBER" IS NOT NULL),
SUFFIXNUMBER         varchar(10)                    not null
	
	
	constraint SYS_C0013174 check ("SUFFIXNUMBER" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : PORTLOCALSERIAL                                      */
/*==============================================================*/
create table PORTLOCALSERIAL (
PORTID               numeric                        not null
	
	
	constraint SYS_C0013145 check ("PORTID" IS NOT NULL),
PHYSICALPORT         varchar(8)                     not null
	
	
	constraint SYS_C0013146 check ("PHYSICALPORT" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : PORTRADIOSETTINGS                                    */
/*==============================================================*/
create table PORTRADIOSETTINGS (
PORTID               numeric                        not null
	
	
	constraint SYS_C0013164 check ("PORTID" IS NOT NULL),
RTSTOTXWAITSAMED     numeric                        not null
	
	
	constraint SYS_C0013165 check ("RTSTOTXWAITSAMED" IS NOT NULL),
RTSTOTXWAITDIFFD     numeric                        not null
	
	
	constraint SYS_C0013166 check ("RTSTOTXWAITDIFFD" IS NOT NULL),
RADIOMASTERTAIL      numeric                        not null
	
	
	constraint SYS_C0013167 check ("RADIOMASTERTAIL" IS NOT NULL),
REVERSERTS           numeric                        not null
	
	
	constraint SYS_C0013168 check ("REVERSERTS" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : PORTSETTINGS                                         */
/*==============================================================*/
create table PORTSETTINGS (
PORTID               numeric                        not null
	
	
	constraint SYS_C0013152 check ("PORTID" IS NOT NULL),
BAUDRATE             numeric                        not null
	
	
	constraint SYS_C0013153 check ("BAUDRATE" IS NOT NULL),
CDWAIT               numeric                        not null
	
	
	constraint SYS_C0013154 check ("CDWAIT" IS NOT NULL),
LINESETTINGS         varchar(4)                     not null
	
	
	constraint SYS_C0013155 check ("LINESETTINGS" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : PORTSTATISTICS                                       */
/*==============================================================*/
create table PORTSTATISTICS (
PORTID               numeric                        not null
	
	
	constraint SYS_C0013176 check ("PORTID" IS NOT NULL),
STATISTICTYPE        numeric                        not null
	
	
	constraint SYS_C0013177 check ("STATISTICTYPE" IS NOT NULL),
ATTEMPTS             numeric                        not null
	
	
	constraint SYS_C0013178 check ("ATTEMPTS" IS NOT NULL),
DATAERRORS           numeric                        not null
	
	
	constraint SYS_C0013179 check ("DATAERRORS" IS NOT NULL),
SYSTEMERRORS         numeric                        not null
	
	
	constraint SYS_C0013180 check ("SYSTEMERRORS" IS NOT NULL),
STARTDATETIME        datetime                       not null
	
	
	constraint SYS_C0013181 check ("STARTDATETIME" IS NOT NULL),
STOPDATETIME         datetime                       not null
	
	
	constraint SYS_C0013182 check ("STOPDATETIME" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : PORTTERMINALSERVER                                   */
/*==============================================================*/
create table PORTTERMINALSERVER (
PORTID               numeric                        not null
	
	
	constraint SYS_C0013148 check ("PORTID" IS NOT NULL),
IPADDRESS            varchar(16)                    not null
	
	
	constraint SYS_C0013149 check ("IPADDRESS" IS NOT NULL),
SOCKETPORTNUMBER     numeric                        not null
	
	
	constraint SYS_C0013150 check ("SOCKETPORTNUMBER" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : PORTTIMING                                           */
/*==============================================================*/
create table PORTTIMING (
PORTID               numeric                        not null
	
	
	constraint SYS_C0013157 check ("PORTID" IS NOT NULL),
PRETXWAIT            numeric                        not null
	
	
	constraint SYS_C0013158 check ("PRETXWAIT" IS NOT NULL),
RTSTOTXWAIT          numeric                        not null
	
	
	constraint SYS_C0013159 check ("RTSTOTXWAIT" IS NOT NULL),
POSTTXWAIT           numeric                        not null
	
	
	constraint SYS_C0013160 check ("POSTTXWAIT" IS NOT NULL),
RECEIVEDATAWAIT      numeric                        not null
	
	
	constraint SYS_C0013161 check ("RECEIVEDATAWAIT" IS NOT NULL),
EXTRATIMEOUT         numeric                        not null
	
	
	constraint SYS_C0013162 check ("EXTRATIMEOUT" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : PROGRAM                                              */
/*==============================================================*/
create table PROGRAM (
PROGRAMID            numeric                        not null,
NAME                 varchar(20)                    not null
	
	
	constraint SYS_C0015116 check ("NAME" IS NOT NULL),
STATE                varchar(20)                    not null
	
	
	constraint SYS_C0015117 check ("STATE" IS NOT NULL),
STARTTIMESTAMP       datetime                       null,
STOPTIMESTAMP        datetime                       null,
TOTALCONTROLTIME     numeric                        not null
	
	
	constraint SYS_C0015118 check ("TOTALCONTROLTIME" IS NOT NULL),
constraint SYS_C0015119 primary key  (PROGRAMID)
)
go


/*==============================================================*/
/* Table : PointAlarming                                        */
/*==============================================================*/
create table PointAlarming (
PointID              numeric                        not null,
AlarmStates          varchar(32)                    not null,
ExcludeNotifyStates  varchar(32)                    not null,
NotifyOnAcknowledge  char(1)                        not null,
NotificationGroupID  numeric                        not null,
LocationID           numeric                        not null,
constraint PK_POINTALARMING primary key  (PointID)
)
go


insert into pointalarming(pointid, alarmstates, excludenotifystates, notifyonacknowledge, notificationgroupid, locationid)
	select pointid,
	'',
	'NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN',
	'N',
	1, 0  from point;

/*==============================================================*/
/* Table : RAWPOINTHISTORY                                      */
/*==============================================================*/
create table RAWPOINTHISTORY (
CHANGEID             numeric                        not null,
POINTID              numeric                        not null
	
	
	constraint SYS_C0013318 check ("POINTID" IS NOT NULL),
TIMESTAMP            datetime                       not null
	
	
	constraint SYS_C0013319 check ("TIMESTAMP" IS NOT NULL),
QUALITY              numeric                        not null
	
	
	constraint SYS_C0013320 check ("QUALITY" IS NOT NULL),
VALUE                float                          not null
	
	
	constraint SYS_C0013321 check ("VALUE" IS NOT NULL),
BOOKMARK             varchar(16)                    null,
constraint SYS_C0013322 primary key  (CHANGEID)
)
go


/*==============================================================*/
/* Table : REPEATERROUTE                                        */
/*==============================================================*/
create table REPEATERROUTE (
ROUTEID              numeric                        not null
	
	
	constraint SYS_C0013265 check ("ROUTEID" IS NOT NULL),
DEVICEID             numeric                        not null
	
	
	constraint SYS_C0013266 check ("DEVICEID" IS NOT NULL),
VARIABLEBITS         numeric                        not null
	
	
	constraint SYS_C0013267 check ("VARIABLEBITS" IS NOT NULL),
REPEATERORDER        numeric                        not null
	
	
	constraint SYS_C0013268 check ("REPEATERORDER" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : ROUTE                                                */
/*==============================================================*/
create table ROUTE (
ROUTEID              numeric                        not null,
NAME                 varchar(20)                    not null
	
	
	constraint SYS_C0013123 check ("NAME" IS NOT NULL),
TYPE                 varchar(20)                    not null
	
	
	constraint SYS_C0013124 check ("TYPE" IS NOT NULL),
constraint SYS_C0013125 primary key  (ROUTEID)
)
go


/*==============================================================*/
/* Table : SERIALNUMBER                                         */
/*==============================================================*/
create table SERIALNUMBER (
OWNER                numeric                        not null
	
	
	constraint SYS_C0015123 check ("OWNER" IS NOT NULL),
SERIALNUMBER         varchar(15)                    null,
NAME                 varchar(20)                    null
)
go


/*==============================================================*/
/* Table : STARTABSOLUTETIME                                    */
/*==============================================================*/
create table STARTABSOLUTETIME (
SCHEDULEID           numeric                        null,
STARTTIME            varchar(8)                     not null
	
	
	constraint SYS_C0013384 check ("STARTTIME" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : STARTDATETIME                                        */
/*==============================================================*/
create table STARTDATETIME (
SCHEDULEID           numeric                        null,
MONTH                varchar(9)                     not null
	
	
	constraint SYS_C0013386 check ("MONTH" IS NOT NULL),
DAYOFMONTH           numeric                        not null
	
	
	constraint SYS_C0013387 check ("DAYOFMONTH" IS NOT NULL),
STARTTIME            varchar(8)                     not null
	
	
	constraint SYS_C0013388 check ("STARTTIME" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : STARTDELTATIME                                       */
/*==============================================================*/
create table STARTDELTATIME (
SCHEDULEID           numeric                        null,
DELTATIME            varchar(8)                     not null
	
	
	constraint SYS_C0013390 check ("DELTATIME" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : STARTDOMTIME                                         */
/*==============================================================*/
create table STARTDOMTIME (
SCHEDULEID           numeric                        null,
DAYOFMONTH           numeric                        not null
	
	
	constraint SYS_C0013392 check ("DAYOFMONTH" IS NOT NULL),
STARTTIME            varchar(8)                     not null
	
	
	constraint SYS_C0013393 check ("STARTTIME" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : STARTDOWTIME                                         */
/*==============================================================*/
create table STARTDOWTIME (
SCHEDULEID           numeric                        null,
DAYOFWEEK            varchar(9)                     not null
	
	
	constraint SYS_C0013395 check ("DAYOFWEEK" IS NOT NULL),
STARTTIME            varchar(8)                     not null
	
	
	constraint SYS_C0013396 check ("STARTTIME" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : STATE                                                */
/*==============================================================*/
create table STATE (
STATEGROUPID         numeric                        not null
	
	
	constraint SYS_C0013337 check ("STATEGROUPID" IS NOT NULL),
RAWSTATE             numeric                        not null
	
	
	constraint SYS_C0013338 check ("RAWSTATE" IS NOT NULL),
TEXT                 varchar(20)                    not null
	
	
	constraint SYS_C0013339 check ("TEXT" IS NOT NULL),
FOREGROUNDCOLOR      numeric                        not null
	
	
	constraint SYS_C0013340 check ("FOREGROUNDCOLOR" IS NOT NULL),
BACKGROUNDCOLOR      numeric                        not null
	
	
	constraint SYS_C0013341 check ("BACKGROUNDCOLOR" IS NOT NULL),
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
/* Table : STATEGROUP                                           */
/*==============================================================*/
create table STATEGROUP (
STATEGROUPID         numeric                        not null,
NAME                 varchar(20)                    not null
	
	
	constraint SYS_C0013127 check ("NAME" IS NOT NULL),
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
/* Table : STOPABSOLUTETIME                                     */
/*==============================================================*/
create table STOPABSOLUTETIME (
SCHEDULEID           numeric                        null,
STOPTIME             varchar(8)                     not null
	
	
	constraint SYS_C0013398 check ("STOPTIME" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : STOPDURATIONTIME                                     */
/*==============================================================*/
create table STOPDURATIONTIME (
SCHEDULEID           numeric                        null,
DURATIONTIME         varchar(8)                     not null
	
	
	constraint SYS_C0013400 check ("DURATIONTIME" IS NOT NULL)
)
go


/*==============================================================*/
/* Table : SYSTEMLOG                                            */
/*==============================================================*/
create table SYSTEMLOG (
LOGID                numeric                        not null,
POINTID              numeric                        not null
	
	
	constraint SYS_C0013402 check ("POINTID" IS NOT NULL),
DATETIME             datetime                       not null
	
	
	constraint SYS_C0013403 check ("DATETIME" IS NOT NULL),
SOE_TAG              numeric                        not null
	
	
	constraint SYS_C0013404 check ("SOE_TAG" IS NOT NULL),
TYPE                 numeric                        not null
	
	
	constraint SYS_C0013405 check ("TYPE" IS NOT NULL),
PRIORITY             numeric                        not null
	
	
	constraint SYS_C0013406 check ("PRIORITY" IS NOT NULL),
ACTION               varchar(60)                    null,
DESCRIPTION          varchar(120)                   null,
USERNAME             varchar(12)                    null,
constraint SYS_C0013407 primary key  (LOGID)
)
go


/*==============================================================*/
/* Table : TEMPLATE                                             */
/*==============================================================*/
create table TEMPLATE (
TEMPLATENUM          numeric                        not null
	
	
	constraint SYS_C0013423 check ("TEMPLATENUM" IS NOT NULL),
NAME                 varchar(40)                    not null
	
	
	constraint SYS_C0013424 check ("NAME" IS NOT NULL),
DESCRIPTION          varchar(200)                   null,
constraint SYS_C0013425 primary key  (TEMPLATENUM)
)
go


insert into template values( 1, 'Standard', 'First Standard Cannon Template');
insert into template values( 2, 'Standard - No PtName', 'Second Standard Cannon 
Template');
insert into template values( 3, 'Standard - No DevName', 'Third Standard Cannon 
Template');

/*==============================================================*/
/* Table : TEMPLATECOLUMNS                                      */
/*==============================================================*/
create table TEMPLATECOLUMNS (
TEMPLATENUM          numeric                        not null,
TITLE                varchar(50)                    null,
TYPENUM              numeric                        not null
	
	
	constraint SYS_C0013426 check ("TYPENUM" IS NOT NULL),
ORDERING             numeric                        not null
	
	
	constraint SYS_C0013427 check ("ORDERING" IS NOT NULL),
WIDTH                numeric                        not null
	
	
	constraint SYS_C0013428 check ("WIDTH" IS NOT NULL)
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
NAME                 varchar(8)                     not null,
CALCTYPE             numeric                        not null
	
	
	constraint SYS_C0013343 check ("CALCTYPE" IS NOT NULL),
constraint SYS_C0013344 primary key  (NAME)
)
go


INSERT INTO UnitMeasure VALUES ( 'KW', 0 );
INSERT INTO UnitMeasure VALUES ( 'KWH', 0 );
INSERT INTO UnitMeasure VALUES ( 'KVA', 0 );
INSERT INTO UnitMeasure VALUES ( 'KVAR', 0 );
INSERT INTO UnitMeasure VALUES ( 'KVAH', 0 );
INSERT INTO UnitMeasure VALUES ( 'KVARH', 0 );
INSERT INTO UnitMeasure VALUES ( 'MW', 0 );
INSERT INTO UnitMeasure VALUES ( 'MWH', 0 );
INSERT INTO UnitMeasure VALUES ( 'MVA', 0 );
INSERT INTO UnitMeasure VALUES ( 'MVAR', 0 );
INSERT INTO UnitMeasure VALUES ( 'MVAH', 0 );
INSERT INTO UnitMeasure VALUES ( 'MVARH', 0 );
INSERT INTO UnitMeasure VALUES ( 'Volts', 0 );
INSERT INTO UnitMeasure VALUES ( 'VA', 0 );
INSERT INTO UnitMeasure VALUES ( 'Temp-F', 0 );
INSERT INTO UnitMeasure VALUES ( 'Counts', 0 );
INSERT INTO UnitMeasure VALUES ( 'Temp-C', 0 );
INSERT INTO UnitMeasure VALUES ( 'Amps', 0 );
INSERT INTO UnitMeasure VALUES ( 'Gallons', 0 );
INSERT INTO UnitMeasure VALUES ( 'Gal/PM', 0 );
INSERT INTO UnitMeasure VALUES ( 'Watts', 0 );
INSERT INTO UnitMeasure VALUES ( 'Vars', 0 );
INSERT INTO UnitMeasure VALUES ( 'VoltAmps', 0 );
INSERT INTO UnitMeasure VALUES ( 'Degrees', 0 );
INSERT INTO UnitMeasure VALUES ( 'PF', 0 );
INSERT INTO UnitMeasure VALUES ( 'Percent', 0 );
INSERT INTO UnitMeasure VALUES ( '%', 0 );
INSERT INTO UnitMeasure VALUES ( 'Hours', 0 );
INSERT INTO UnitMeasure VALUES ( 'Minutes', 0 );
INSERT INTO UnitMeasure VALUES ( 'Seconds', 0 );
INSERT INTO UnitMeasure VALUES ( 'Watr-CFT', 0 );
INSERT INTO UnitMeasure VALUES ( 'GAS-CFT', 0 );
INSERT INTO UnitMeasure VALUES ( 'Feet', 0 );
INSERT INTO UnitMeasure VALUES ( 'Level', 0 );
INSERT INTO UnitMeasure VALUES ( 'PSI', 0 );
INSERT INTO UnitMeasure VALUES ( 'Ops.', 0 );
INSERT INTO UnitMeasure VALUES ( 'KVolts', 0 );

/*==============================================================*/
/* Table : USERINFO                                             */
/*==============================================================*/
create table USERINFO (
USERID               numeric                        not null,
USERNAME             varchar(20)                    not null
	
	
	constraint SYS_C0015103 check ("USERNAME" IS NOT NULL),
PASSWORD             varchar(20)                    not null
	
	
	constraint SYS_C0015104 check ("PASSWORD" IS NOT NULL),
DATABASEALIAS        varchar(20)                    not null
	
	
	constraint SYS_C0015105 check ("DATABASEALIAS" IS NOT NULL),
LOGINCOUNT           numeric                        not null,
LASTLOGIN            numeric                        not null,
constraint SYS_C0015106 primary key  (USERID)
)
go


/*==============================================================*/
/* Table : USERPROGRAM                                          */
/*==============================================================*/
create table USERPROGRAM (
USERID               numeric                        null,
PROGRAMID            numeric                        null,
ATTRIB               numeric                        null
)
go


/*==============================================================*/
/* Table : USERWEB                                              */
/*==============================================================*/
create table USERWEB (
USERID               numeric                        not null
	
	
	constraint SYS_C0015125 check ("USERID" IS NOT NULL),
HOMEURL              varchar(200)                   null,
LOGO                 varchar(40)                    null
)
go


/*==============================================================*/
/* Table : UserGraph                                            */
/*==============================================================*/
create table UserGraph (
UserID               numeric                        not null,
GraphDefinitionID    numeric                        not null
)
go


/*==============================================================*/
/* Table : VERSACOMROUTE                                        */
/*==============================================================*/
create table VERSACOMROUTE (
ROUTEID              numeric                        null,
UTILITYID            numeric                        not null
	
	
	constraint SYS_C0013276 check ("UTILITYID" IS NOT NULL),
SECTIONADDRESS       numeric                        not null
	
	
	constraint SYS_C0013277 check ("SECTIONADDRESS" IS NOT NULL),
CLASSADDRESS         numeric                        not null
	
	
	constraint SYS_C0013278 check ("CLASSADDRESS" IS NOT NULL),
DIVISIONADDRESS      numeric                        not null
	
	
	constraint SYS_C0013279 check ("DIVISIONADDRESS" IS NOT NULL),
BUSNUMBER            numeric                        not null
	
	
	constraint SYS_C0013280 check ("BUSNUMBER" IS NOT NULL),
AMPCARDSET           numeric                        not null
	
	
	constraint SYS_C0013281 check ("AMPCARDSET" IS NOT NULL)
)
go


/*==============================================================*/
/* Index: Index_DISPLAYNAME                                     */
/*==============================================================*/
create unique  index Index_DISPLAYNAME on DISPLAY (NAME)
go


/*==============================================================*/
/* Index: Index_UserIDName                                      */
/*==============================================================*/
create unique  index Index_UserIDName on CustomerLogin (UserName)
go


/*==============================================================*/
/* Index: Indx_CAPCNTST2                                        */
/*==============================================================*/
create unique  index Indx_CAPCNTST2 on CAPCONTROLSTRATEGY (STRATEGYNAME)
go


/*==============================================================*/
/* Index: Indx_CAPCNTSTR1                                       */
/*==============================================================*/
create unique  index Indx_CAPCNTSTR1 on CAPCONTROLSTRATEGY (CAPSTRATEGYID)
go


/*==============================================================*/
/* Index: Indx_COLUMNTYPE                                       */
/*==============================================================*/
create unique  index Indx_COLUMNTYPE on COLUMNTYPE (TYPENUM)
go


/*==============================================================*/
/* Index: Indx_COMMPORT1                                        */
/*==============================================================*/
create unique  index Indx_COMMPORT1 on COMMPORT (PORTID)
go


/*==============================================================*/
/* Index: Indx_COMMPORT2                                        */
/*==============================================================*/
create unique  index Indx_COMMPORT2 on COMMPORT (DESCRIPTION)
go


/*==============================================================*/
/* Index: Indx_DEVICE1                                          */
/*==============================================================*/
create unique  index Indx_DEVICE1 on DEVICE (DEVICEID)
go


/*==============================================================*/
/* Index: Indx_DEVICE2                                          */
/*==============================================================*/
create unique  index Indx_DEVICE2 on DEVICE (NAME)
go


/*==============================================================*/
/* Index: Indx_DISPLAY                                          */
/*==============================================================*/
create unique  index Indx_DISPLAY on DISPLAY (DISPLAYNUM)
go


/*==============================================================*/
/* Index: Indx_GPHDATSER                                        */
/*==============================================================*/
create unique  index Indx_GPHDATSER on GRAPHDATASERIES (GRAPHDATASERIESID)
go


/*==============================================================*/
/* Index: Indx_GPHDEF                                           */
/*==============================================================*/
create unique  index Indx_GPHDEF on GRAPHDEFINITION (GRAPHDEFINITIONID)
go


/*==============================================================*/
/* Index: Indx_LOGIC                                            */
/*==============================================================*/
create unique  index Indx_LOGIC on LOGIC (LOGICID)
go


/*==============================================================*/
/* Index: Indx_MCSCHED                                          */
/*==============================================================*/
create unique  index Indx_MCSCHED on MCSCHEDULE (SCHEDULEID)
go


/*==============================================================*/
/* Index: Indx_NOTIFGRP                                         */
/*==============================================================*/
create unique  index Indx_NOTIFGRP on NotificationGroup (GroupName)
go


/*==============================================================*/
/* Index: Indx_POINT                                            */
/*==============================================================*/
create unique  index Indx_POINT on POINT (POINTID)
go


/*==============================================================*/
/* Index: Indx_PROGRAM1                                         */
/*==============================================================*/
create unique  index Indx_PROGRAM1 on PROGRAM (NAME)
go


/*==============================================================*/
/* Index: Indx_PROGRAM2                                         */
/*==============================================================*/
create unique  index Indx_PROGRAM2 on PROGRAM (PROGRAMID)
go


/*==============================================================*/
/* Index: Indx_RAWPTHIS                                         */
/*==============================================================*/
create unique  index Indx_RAWPTHIS on RAWPOINTHISTORY (CHANGEID)
go


/*==============================================================*/
/* Index: Indx_ROUTE1                                           */
/*==============================================================*/
create unique  index Indx_ROUTE1 on ROUTE (NAME)
go


/*==============================================================*/
/* Index: Indx_ROUTE2                                           */
/*==============================================================*/
create unique  index Indx_ROUTE2 on ROUTE (ROUTEID)
go


/*==============================================================*/
/* Index: Indx_STATEGRP1                                        */
/*==============================================================*/
create unique  index Indx_STATEGRP1 on STATEGROUP (STATEGROUPID)
go


/*==============================================================*/
/* Index: Indx_STATEGRP2                                        */
/*==============================================================*/
create unique  index Indx_STATEGRP2 on STATEGROUP (NAME)
go


/*==============================================================*/
/* Index: Indx_SYSTEMLOG                                        */
/*==============================================================*/
create unique  index Indx_SYSTEMLOG on SYSTEMLOG (LOGID)
go


/*==============================================================*/
/* Index: Indx_TEMPLATE                                         */
/*==============================================================*/
create unique  index Indx_TEMPLATE on TEMPLATE (TEMPLATENUM)
go


/*==============================================================*/
/* Index: Indx_UOM                                              */
/*==============================================================*/
create unique  index Indx_UOM on UNITMEASURE (NAME)
go


/*==============================================================*/
/* Index: Indx_USERINFO                                         */
/*==============================================================*/
create unique  index Indx_USERINFO on USERINFO (USERID)
go


/*==============================================================*/
/* Index: SchedNameIndx                                         */
/*==============================================================*/
create unique  index SchedNameIndx on MACSchedule (ScheduleName)
go


/*==============================================================*/
/* View: DISPLAY2WAYDATA_VIEW                                   */
/*==============================================================*/
create view DISPLAY2WAYDATA_VIEW (POINTID, POINTNAME , POINTTYPE , POINTSTATE , DEVICENAME , DEVICETYPE , DEVICECURRENTSTATE , DEVICEID , POINTVALUE, POINTQUALITY, POINTTIMESTAMP, UofM) as
select point.POINTID, point.POINTNAME, point.POINTTYPE, point.SERVICEFLAG, device.NAME, device.TYPE, device.CURRENTSTATE, device.DEVICEID, '**DYNAMIC**', '**DYNAMIC**', '**DYNAMIC**', (select unit from pointunit where pointunit.pointid=point.pointid)
from POINT, DEVICE
where point.DEVICEID = device.DEVICEID
go


/*==============================================================*/
/* View: LMCurtailCustomerActivity_View                         */
/*==============================================================*/
create view LMCurtailCustomerActivity_View  as
select cust.CustomerID, prog.CurtailmentStartTime, prog.CurtailmentStopTime, cust.AcknowledgeStatus, cust.AckDateTime, cust.NameOfAckPerson, cust.AckLateFlag
from LMCurtailProgramActivity prog, LMCurtailCustomerActivity cust
order by cust.AckDateTime ASC
go


/*==============================================================*/
/* View: POINTHISTORY                                           */
/*==============================================================*/
create view POINTHISTORY (POINTID , POINTTIMESTAMP , QUALITY , VALUE) as
select RawPointHistory.POINTID, RawPointHistory.TIMESTAMP, RawPointHistory.QUALITY, (RawPointHistory.Value*PointAnalog.Multiplier)
from RAWPOINTHISTORY, POINTANALOG
where RawPointHistory.POINTID = PointAnalog.POINTID
go


alter table AlarmState
   add constraint FK_ALARMSTA_ALARMSTAT_NOTIFICA foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID)
go


alter table NotificationDestination
   add constraint FK_NOTIFICA_REFERENCE_GROUPREC foreign key (LocationID)
      references GroupRecipient (LocationID)
go


alter table LMGROUPEMETCON
   add constraint SYS_C0013356 foreign key (DEVICEID)
      references LMGroup (DeviceID)
go


alter table DynamicLMProgramDirect
   add constraint FK_DYNAMICL_LMPROGDIR_LMPROGRA foreign key (DeviceID)
      references LMProgramDirect (DeviceID)
go


alter table LMProgramControlWindow
   add constraint FK_LMPrg_LMPrgCntWind foreign key (DeviceID)
      references LMPROGRAM (DEVICEID)
go


alter table MACSimpleSchedule
   add constraint FK_MACSIMPLE_MACSCHED_ID foreign key (ScheduleID)
      references MACSchedule (ScheduleID)
go


alter table NotificationDestination
   add constraint FK_NOTIFICAGROUP_NOTIFICATDEST foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID)
go


alter table PointAlarming
   add constraint FK_POINTALA_POINTALAR_GROUPREC foreign key (LocationID)
      references GroupRecipient (LocationID)
go


alter table PointAlarming
   add constraint FK_POINTALAARM_POINT_POINTID foreign key (PointID)
      references POINT (POINTID)
go


alter table PointAlarming
   add constraint FK_POINTALARMING foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID)
go


alter table CICustContact
   add constraint FK_CICstBase_CICstCont foreign key (DeviceID)
      references CICustomerBase (DeviceID)
go


alter table CICustomerBase
   add constraint FK_CICstBas_CstAddrs foreign key (AddressID)
      references CustomerAddress (AddressID)
go


alter table LMCurtailCustomerActivity
   add constraint FK_CICBas_LMCrtCstAct foreign key (CustomerID)
      references CICustomerBase (DeviceID)
go


alter table LMProgramCurtailCustomerList
   add constraint FK_CICstBase_LMProgCList foreign key (LMCustomerDeviceID)
      references CICustomerBase (DeviceID)
go


alter table CustomerContact
   add constraint FK_CstCont_GrpRecip foreign key (LocationID)
      references GroupRecipient (LocationID)
go


alter table CICustContact
   add constraint FK_CstCont_CICstCont foreign key (ContactID)
      references CustomerContact (ContactID)
go


alter table CustomerContact
   add constraint FK_RefCstLg_CustCont foreign key (LogInID)
      references CustomerLogin (LogInID)
go


alter table CICustomerBase
   add constraint FK_Dev_CICustBase foreign key (DeviceID)
      references DEVICE (DEVICEID)
go


alter table LMCONTROLAREA
   add constraint FK_Device_LMCctrlArea foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table LMGroup
   add constraint FK_Device_LMGrpBase foreign key (DeviceID)
      references DEVICE (DEVICEID)
go


alter table LMPROGRAM
   add constraint FK_Device_LMProgram foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table LMCONTROLAREAPROGRAM
   add constraint FK_LMCntlArea_LMCntlArProg foreign key (DEVICEID)
      references LMCONTROLAREA (DEVICEID)
go


alter table LMCONTROLAREATRIGGER
   add constraint FK_LMCntlArea_LMCntlArTrig foreign key (DEVICEID)
      references LMCONTROLAREA (DEVICEID)
go


alter table LMGROUPVERSACOM
   add constraint FK_LMGrp_LMGrpVers foreign key (DEVICEID)
      references LMGroup (DeviceID)
go


alter table LMProgramDirectGroup
   add constraint FK_LMGrp_LMPrgDGrp foreign key (LMGroupDeviceID)
      references LMGroup (DeviceID)
go


alter table LMCurtailProgramActivity
   add constraint FK_LMPrgCrt_LMCrlPAct foreign key (DeviceID)
      references LMProgramCurtailment (DeviceID)
go


alter table DynamicLMProgram
   add constraint FK_LMProg_DynLMPrg foreign key (DeviceID)
      references LMPROGRAM (DEVICEID)
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


alter table LMProgramCurtailment
   add constraint FK_LMPrg_LMPrgCurt foreign key (DeviceID)
      references LMPROGRAM (DEVICEID)
go


alter table LMProgramDirect
   add constraint FK_LMPrg_LMPrgDirect foreign key (DeviceID)
      references LMPROGRAM (DEVICEID)
go


alter table LMCONTROLAREATRIGGER
   add constraint FK_Point_LMCntlArTrig foreign key (POINTID)
      references POINT (POINTID)
go


alter table LMCONTROLAREATRIGGER
   add constraint FK_Point_LMCtrlArTrigPk foreign key (PEAKPOINTID)
      references POINT (POINTID)
go


alter table DynamicLMControlArea
   add constraint FK_LMCntlAr_DynLMCntAr foreign key (DeviceID)
      references LMCONTROLAREA (DEVICEID)
go


alter table LMCurtailProgramActivity
   add constraint FK__LMCurtPrgAct_LMCurCusAc foreign key (DeviceID)
      references LMCurtailCustomerActivity (CustomerID)
go


alter table DynamicLMGroup
   add constraint FK_LMGrp_DynLmGrp foreign key (DeviceID)
      references LMGroup (DeviceID)
go


alter table CALCBASE
   add constraint SYS_C0013434 foreign key (POINTID)
      references POINT (POINTID)
go


alter table CCSTRATEGYBANKLIST
   add constraint SYS_C0013484 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table COMMUNICATIONROUTE
   add constraint SYS_C0013257 foreign key (ROUTEID)
      references ROUTE (ROUTEID)
go


alter table COMMUNICATIONROUTE
   add constraint SYS_C0013258 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
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


alter table CALCCOMPONENT
   add constraint SYS_C0013440 foreign key (POINTID)
      references POINT (POINTID)
go


alter table DYNAMICCAPCONTROLSTRATEGY
   add constraint FK_DYNAMICC_REFERENCE_CAPCONTR foreign key (CapStrategyID)
      references CAPCONTROLSTRATEGY (CAPSTRATEGYID)
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
   add constraint SYS_C0015114 foreign key (GRAPHDEFINITIONID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID)
go


alter table GRAPHDATASERIES
   add constraint SYS_C0015115 foreign key (POINTID)
      references POINT (POINTID)
go


alter table LASTPOINTSCAN
   add constraint SYS_C0013349 foreign key (POINTID)
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


alter table LMGROUPVERSACOMSERIAL
   add constraint SYS_C0013373 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table LMGROUPVERSACOMSERIAL
   add constraint SYS_C0013374 foreign key (DEVICEIDOFGROUP)
      references DEVICE (DEVICEID)
go


alter table LMGROUPVERSACOMSERIAL
   add constraint SYS_C0013375 foreign key (ROUTEID)
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


alter table POINT
   add constraint SYS_C0013143 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table CAPBANK
   add constraint SYS_C0013455 foreign key (CONTROLDEVICEID)
      references DEVICE (DEVICEID)
go


alter table POINT
   add constraint SYS_C0013144 foreign key (STATEGROUPID)
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


alter table POINTCONTROL
   add constraint SYS_C0013312 foreign key (POINTID)
      references POINT (POINTID)
go


alter table POINTDISPATCH
   add constraint SYS_C0013331 foreign key (POINTID)
      references POINT (POINTID)
go


alter table POINTLIMITS
   add constraint SYS_C0013289 foreign key (POINTID)
      references POINT (POINTID)
go


alter table POINTSTATUS
   add constraint SYS_C0013307 foreign key (POINTID)
      references POINT (POINTID)
go


alter table POINTUNIT
   add constraint SYS_C0013294 foreign key (POINTID)
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


alter table CAPCONTROLSTRATEGY
   add constraint SYS_C0013478 foreign key (ACTUALVARPOINTID)
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


alter table RAWPOINTHISTORY
   add constraint SYS_C0013323 foreign key (POINTID)
      references POINT (POINTID)
go


alter table REPEATERROUTE
   add constraint SYS_C0013269 foreign key (ROUTEID)
      references ROUTE (ROUTEID)
go


alter table REPEATERROUTE
   add constraint SYS_C0013270 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


alter table SERIALNUMBER
   add constraint SYS_C0015124 foreign key (OWNER)
      references USERINFO (USERID)
go


alter table STARTABSOLUTETIME
   add constraint SYS_C0013385 foreign key (SCHEDULEID)
      references MCSCHEDULE (SCHEDULEID)
go


alter table CAPCONTROLSTRATEGY
   add constraint SYS_C0013479 foreign key (CALCULATEDVARLOADPOINTID)
      references POINT (POINTID)
go


alter table STARTDATETIME
   add constraint SYS_C0013389 foreign key (SCHEDULEID)
      references MCSCHEDULE (SCHEDULEID)
go


alter table STARTDELTATIME
   add constraint SYS_C0013391 foreign key (SCHEDULEID)
      references MCSCHEDULE (SCHEDULEID)
go


alter table STARTDOMTIME
   add constraint SYS_C0013394 foreign key (SCHEDULEID)
      references MCSCHEDULE (SCHEDULEID)
go


alter table STARTDOWTIME
   add constraint SYS_C0013397 foreign key (SCHEDULEID)
      references MCSCHEDULE (SCHEDULEID)
go


alter table STATE
   add constraint SYS_C0013342 foreign key (STATEGROUPID)
      references STATEGROUP (STATEGROUPID)
go


alter table STOPABSOLUTETIME
   add constraint SYS_C0013399 foreign key (SCHEDULEID)
      references MCSCHEDULE (SCHEDULEID)
go


alter table STOPDURATIONTIME
   add constraint SYS_C0013401 foreign key (SCHEDULEID)
      references MCSCHEDULE (SCHEDULEID)
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


alter table USERPROGRAM
   add constraint SYS_C0015121 foreign key (USERID)
      references USERINFO (USERID)
go


alter table USERPROGRAM
   add constraint SYS_C0015122 foreign key (PROGRAMID)
      references PROGRAM (PROGRAMID)
go


alter table USERWEB
   add constraint SYS_C0015126 foreign key (USERID)
      references USERINFO (USERID)
go


alter table CCSTRATEGYBANKLIST
   add constraint SYS_C0013483 foreign key (CAPSTRATEGYID)
      references CAPCONTROLSTRATEGY (CAPSTRATEGYID)
go


alter table VERSACOMROUTE
   add constraint FK_VERSACOM_ROUTE_VER_ROUTE foreign key (ROUTEID)
      references ROUTE (ROUTEID)
go


alter table UserGraph
   add constraint FK_USERGRAP_USERGRAPH_GRAPHDEF foreign key (GraphDefinitionID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID)
go


alter table UserGraph
   add constraint FK_USERGRAP_USERGRAPH_USERINFO foreign key (UserID)
      references USERINFO (USERID)
go


