/*-----------------------------------------------------------------------------*
*
* File:   gateway
*
* Class:
* Date:   6/12/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2003/12/17 15:28:04 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma pack(1)
#pragma warning( disable : 4786)
#ifndef __GATEWAY_H__
#define __GATEWAY_H__

#define MODIFIED_VALUES       0x01  /* bit that indicates that the API modified the parameter values of a set function */
#define CHANGE_PENDING        0x02  /* bit that indicates that the API has queued a change request to the thermostat */

#define TYPE_KEEPALIVE                   100

#define TYPE_SETBINDMODE                1000
#define TYPE_SETPINGMODE                1001
#define TYPE_SETNETWORKID               1002
#define TYPE_UNBINDDEVICE               1003
#define TYPE_CLEARSTATISTICS            1004
#define TYPE_SETRSSICONFIGURATION       1005
#define TYPE_RESTARTFILTER              1006
#define TYPE_QUERYRUNTIME               1007

#define TYPE_SETDLC                     2000
#define TYPE_SETDLCOVERRIDE             2001
#define TYPE_SETFANSWITCH               2002
#define TYPE_SETFILTERRESTART           2003
#define TYPE_SETSCHEDULE                2004
#define TYPE_SETSETPOINTLIMITS          2005
#define TYPE_SETSETPOINTS               2006
#define TYPE_SETSYSTEMSWITCH            2007
#define TYPE_SETUTILSETPOINTS           2008
#define TYPE_SETUTILOVERRIDE            2009
#define TYPE_TM_CLOCK                   2010

#define TYPE_SETADDRESSING              2990
#define TYPE_SETTIMEZONE                2991

#define TYPE_ALLOWEDSYSTEMSWITCH        3000
#define TYPE_BATTERY                    3001
#define TYPE_RUNTIME                    3002
#define TYPE_SETPOINTS                  3003
#define TYPE_DEADBAND                   3004
#define TYPE_DEVICEABSENT               3005
#define TYPE_DEVICETYPE                 3006
#define TYPE_DISPLAYEDTEMPERATURE       3007
#define TYPE_DLC                        3008
#define TYPE_FANSWITCH                  3009
#define TYPE_FILTER                     3010
#define TYPE_HEATPUMPFAULT              3011
#define TYPE_SETPOINTLIMITS             3012
#define TYPE_OUTDOORTEMP                3013
#define TYPE_SCHEDULE                   3014
#define TYPE_SYSTEMSWITCH               3015
#define TYPE_UTILSETPOINT               3016
#define TYPE_CLOCK                      3017
#define TYPE_DEVICEBOUND                3018
#define TYPE_DEVICEUNBOUND              3019

#define TYPE_SETPOINTS_CH               3103
#define TYPE_DLC_CH                     3108
#define TYPE_FANSWITCH_CH               3109
#define TYPE_FILTER_CH                  3110
#define TYPE_SETPOINTLIMITS_CH          3112
#define TYPE_SCHEDULE_CH                3114
#define TYPE_SYSTEMSWITCH_CH            3115
#define TYPE_UTILSETPOINT_CH            3116

#define TYPE_RSSI                       3200
#define TYPE_ADDRESSING                 3990
#define TYPE_TIMEZONE_MWG               3991

#define TYPE_GETALLOWEDSYSTEMSWITCH     4000
#define TYPE_GETBATTERY                 4001
#define TYPE_GETRUNTIME                 4002
#define TYPE_GETSETPOINTS               4003
#define TYPE_GETDEADBAND                4004
#define TYPE_GETDEVICEABSENT            4005
#define TYPE_GETDEVICETYPE              4006
#define TYPE_GETDISPLAYEDTEMPERATURE    4007
#define TYPE_GETDLC                     4008
#define TYPE_GETFANSWITCH               4009
#define TYPE_GETFILTER                  4010
#define TYPE_GETHEATPUMPFAULT           4011
#define TYPE_GETSETPOINTLIMITS          4012
#define TYPE_GETOUTDOORTEMP             4013
#define TYPE_GETSCHEDULE                4014
#define TYPE_GETSYSTEMSWITCH            4015
#define TYPE_GETUTILSETPOINT            4016
#define TYPE_GETCLOCK                   4017
#define TYPE_GETDEVICEBOUND             4018
#define TYPE_GETDEVICERSSI              4019

#define TYPE_GETADDRESSING              4990
#define TYPE_GETALL                     4999


#define TYPE_COMMSTATUS                 5000
#define TYPE_BINDMODE                   5001
#define TYPE_PINGMODE                   5002
#define TYPE_RESET                      5003
#define TYPE_ERROR                      5004
#define TYPE_RETURNCODE                 5500


#define TYPE_CONTROLSETPOINT            6001        // Expresscom Setpoint command


// Error Types
#define UNKNOWNDEVICEID                 0001
#define INVALIDDAY                      0002
#define INVALIDPERIOD                   0003
#define DUPLICATEDEVICEID               0004


typedef struct {
   unsigned short Type;
   unsigned short Length;
   unsigned long DeviceID;
} GWHEADER;


// Structure to receive messages from the gateway

typedef     union {
    struct {
        unsigned char AllowedSystemSwitch;
    } AllowedSystemSwitch;

    struct {
        unsigned char Battery;
    } Battery;

    struct {
        unsigned char Day;
        unsigned char Hour;
        unsigned char Minute;
        unsigned char Second;
    } Clock;

    struct {
        unsigned short CoolRuntime;
        unsigned short HeatRuntime;
    } Runtime;

    struct {
        unsigned short LocalRSSI;
        unsigned short RemoteRSSI;
    } Rssi;

    struct {
        short CoolSetpoint;
        short HeatSetpoint;
        unsigned char SetpointStatus;
        unsigned char VacationHoldDays;
        unsigned char VacationHoldPeriod;
    } Setpoints;

    struct {
        unsigned char Deadband;
    } Deadband;

    struct {
        unsigned char DeviceAbsent;
    } DeviceAbsent;

    struct {
        unsigned char DeviceType;
    } DeviceType;

    struct {
        short DisplayedTemperature;
        unsigned char DisplayedTempUnits;
    } DisplayedTemp;

    struct {
        unsigned short CycleDuration;
        unsigned short CyclePeriod;
        unsigned short Duration;
        unsigned char Override;
        unsigned char OverrideDisable;
    } DLC;

    struct {
        unsigned char FanSwitch;
    } FanSwitch;

    struct {
        unsigned char FilterRemaining;
        unsigned char FilterRestart;
    } Filter;

    struct {
        unsigned char HeatPumpFault;
    } HeatPumpFault;

    struct {
        short LowerCoolSetpointLimit;
        short UpperHeatSetpointLimit;
    } SetpointLimits;

    struct {
        short OutdoorTemperature;
    } OutdoorTemp;

    struct {
        unsigned char Day;
        unsigned char Period;
        unsigned char Fan;
        unsigned char Hour;
        unsigned char Minute;
        short CoolSetpoint;
        short HeatSetpoint;
    } Schedule;

    struct {
        unsigned char SystemSwitch;
    } SystemSwitch;

    struct {
        short UtilHeatSetpoint;
        short UtilCoolSetpoint;
        unsigned short UtilDuration;
        unsigned char UtilPriceTier;
        unsigned char UtilMode;
        unsigned char UtilUserOverrideDisable;
        unsigned char UtilAIRDisable;
        unsigned char UtilUserOverride;
    } UtilSetpoint;

    struct {
        unsigned char CommFaultStatus;
    } CommFaultStatus;

    struct {
        unsigned char BindMode;
    } BindMode;

    struct {
        unsigned char PingMode;
    } PingMode;

    struct {
        unsigned short Error;
    } ErrorReport;

} GWCOMMAND;

typedef struct {
    unsigned short ReturnCode;
    unsigned short SetType;

    GWCOMMAND Command;

} RETURNCODEREPORT;

typedef struct {
    char Address[24];

} RESETREPORT;

typedef struct {
    unsigned char flaghi;
    unsigned char flaglo;
    unsigned char minTemp;
    unsigned char maxTemp;
    unsigned short T_r;
    unsigned short T_a;
    unsigned short T_b;
    unsigned char delta_S_b;
    unsigned short T_c;
    unsigned short T_d;
    unsigned char delta_S_d;
    unsigned short T_e;
    unsigned short T_f;
    unsigned char delta_S_f;
    unsigned char hold;
} CONTROL_SETPOINT;

typedef struct
{
    unsigned char   Mac[6];
    unsigned long   IPAddress;

    unsigned long   DefaultServer;

    unsigned short  Spid;
    unsigned short  Geo;
    unsigned short  Feeder;
    unsigned long   Zip;
    unsigned short  Uda;
    unsigned char   Program;
    unsigned char   Splinter;


} ADDRESSING_REPORT;

typedef struct
{
    unsigned long   ZoneMinutesWestOfGreenwich;
    unsigned char   DoDST;
    unsigned short  DSTMinutesOffset;

} TIMEZONE_REPORT;

// This is the _only_ structure we receive from the gateway.
typedef struct {

    GWHEADER Hdr;
    union {
        GWCOMMAND U;
        RETURNCODEREPORT Return;
        RESETREPORT Reset;
        ADDRESSING_REPORT Addressing;
        TIMEZONE_REPORT Timezone;
    };

} GATEWAYRXSTRUCT;



// Various structures to send messages to the gateway

typedef struct
{
    GWHEADER Hdr;                       // DeviceID not used (0) or represents the gateway.
    ADDRESSING_REPORT Addressing;

} ADDRESSING;

typedef struct
{
    GWHEADER Hdr;                       // DeviceID not used (0) or represents the gateway.
    TIMEZONE_REPORT Timezone;

} TIMEZONEMSG;

typedef struct {
    GWHEADER Hdr;                       // DeviceID not used (0) or represents the gateway.
    unsigned char tm_sec;
    unsigned char tm_min;
    unsigned char tm_hour;
    unsigned char tm_mday;
    unsigned char tm_mon;
    unsigned char tm_year;
    unsigned char tm_wday;
    unsigned char tm_isdst;
} TM_CLOCK;

typedef struct {
    GWHEADER Hdr;                       // DeviceID not used (0) or represents the gateway.
    unsigned char BindMode;
} SETBINDMODE;


typedef struct {
    GWHEADER Hdr;                       // DeviceID not used (0) or represents the gateway.
    unsigned char PingMode;
} SETPINGMODE;


typedef struct {
    GWHEADER Hdr;                       // DeviceID not used (0) or represents the gateway.
    unsigned short NetworkID;
} SETNETWORKID;

typedef struct {
    GWHEADER Hdr;                       // DeviceID not used (0) or represents the gateway.
    unsigned char AllMessages;
} SETRSSICONFIGURATION;

typedef struct {
    GWHEADER Hdr;                       // DeviceID not used (0) or represents the gateway.
} KEEPALIVE;

typedef struct {
    GWHEADER Hdr;
} RESTARTFILTER;

typedef struct {
    GWHEADER Hdr;
    unsigned char Reset;
} QUERYRUNTIME;

typedef struct {
    GWHEADER Hdr;
    unsigned char OffCycleDuration;
    unsigned char CyclePeriod;
    unsigned short DLCDuration;
    unsigned char OverrideDisable;
} SETDLC;

typedef struct {
    GWHEADER Hdr;
    unsigned char DLCOverride;
} SETDLCOVERRIDE;

typedef struct {
    GWHEADER Hdr;
    unsigned char FanSwitch;
} SETFANSWITCH;

typedef struct {
    GWHEADER Hdr;
    unsigned char Restart;
} SETFILTERRESTART;

typedef struct {
    GWHEADER Hdr;
    unsigned char Day;
    unsigned char Period;
    short HeatSetpoint;
    short CoolSetpoint;
    unsigned char Hour;
    unsigned char Minute;
    unsigned char Fan;
} SETSCHEDULE;

typedef struct {
    GWHEADER Hdr;
    short UpperHeatLimit;
    short LowerCoolLimit;
} SETSETPOINTLIMITS;

typedef struct {
    GWHEADER Hdr;
    short HeatSetpoint;
    short CoolSetpoint;
    unsigned char SetpointPriority;
    unsigned char SetpointStatus;
    unsigned char VacationHoldDays;
    unsigned char VacationPeriod;
} SETSETPOINTS;

typedef struct {
    GWHEADER Hdr;
    unsigned char SystemSwitch;
} SETSYSTEMSWITCH;

typedef struct {
    GWHEADER Hdr;
    short UtilHeatSetpoint;
    short UtilCoolSetpoint;
    unsigned short UtilDuration;
    unsigned char PriceTier;
    unsigned char Mode;
    unsigned char UserOverrideDisable;
    unsigned char AdaptiveRecoveryDisable;
} SETUTILSETPOINTS;

typedef struct {
    GWHEADER Hdr;
    unsigned char Override;
} SETUTILOVERRIDE;

typedef struct {
    GWHEADER Hdr;
} UNBINDDEVICE;

typedef struct {
    GWHEADER Hdr;
} GET;

typedef struct {
    GWHEADER Hdr;
    unsigned char Day;
    unsigned char Period;
} GETSCHEDULE;

typedef struct {
    GWHEADER Hdr;
    CONTROL_SETPOINT Control;
} SETCONTROLSETPOINT;

#endif // #ifndef __GATEWAY_H__
