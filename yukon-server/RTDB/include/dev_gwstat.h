
/*-----------------------------------------------------------------------------*
*
* File:   dev_gwstat
*
* Class:  CtiDeviceGatewayStat
* Date:   6/11/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2004/06/30 14:39:00 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __DEV_GWSTAT_H__
#define __DEV_GWSTAT_H__

#include <set>
#include <map>
#include <vector>
using namespace std;

#include "cmdparse.h"
#include "ctitypes.h"
#include "dev_ied.h"
#include "dlldefs.h"
#include "dsm2.h"
#include "gateway.h"
#include "pending_stat_operation.h"

#define EP_PERIOD_WAKE          0
#define EP_PERIOD_LEAVE         1
#define EP_PERIOD_RETURN        2
#define EP_PERIOD_SLEEP         3


#define EP_PERIODS_PER_DAY          4   // Number of schedule periods per day

#define ID_TIMESTAMP             3201   // e.g. "2003/07/11 10:39:09 GMT"
#define ID_SETPOINTS             3202   // CoolSetpoint,HeatSetpoint,SetpointStatus
#define ID_FAN_SWITCH            3203   // FanSwitch
#define ID_SYSTEM_SWITCH         3204   // SystemSwitch
#define ID_DISPLAYED_TEMP        3205   // DisplayedTemperature,DisaplyedTempUnits
#define ID_SCHEDULE_MON_WAKE     3206   // Fan,Hour,Minute,CoolSetpoint,HeatSetpoint
#define ID_SCHEDULE_MON_LEAVE    3207
#define ID_SCHEDULE_MON_RETURN   3208
#define ID_SCHEDULE_MON_SLEEP    3209
#define ID_SCHEDULE_TUE_WAKE     3210   // Fan,Hour,Minute,CoolSetpoint,HeatSetpoint
#define ID_SCHEDULE_TUE_LEAVE    3211
#define ID_SCHEDULE_TUE_RETURN   3212
#define ID_SCHEDULE_TUE_SLEEP    3213
#define ID_SCHEDULE_WED_WAKE     3214   // Fan,Hour,Minute,CoolSetpoint,HeatSetpoint
#define ID_SCHEDULE_WED_LEAVE    3215
#define ID_SCHEDULE_WED_RETURN   3216
#define ID_SCHEDULE_WED_SLEEP    3217
#define ID_SCHEDULE_THU_WAKE     3218   // Fan,Hour,Minute,CoolSetpoint,HeatSetpoint
#define ID_SCHEDULE_THU_LEAVE    3219
#define ID_SCHEDULE_THU_RETURN   3220
#define ID_SCHEDULE_THU_SLEEP    3221
#define ID_SCHEDULE_FRI_WAKE     3222   // Fan,Hour,Minute,CoolSetpoint,HeatSetpoint
#define ID_SCHEDULE_FRI_LEAVE    3223
#define ID_SCHEDULE_FRI_RETURN   3224
#define ID_SCHEDULE_FRI_SLEEP    3225
#define ID_SCHEDULE_SAT_WAKE     3226   // Fan,Hour,Minute,CoolSetpoint,HeatSetpoint
#define ID_SCHEDULE_SAT_LEAVE    3227
#define ID_SCHEDULE_SAT_RETURN   3228
#define ID_SCHEDULE_SAT_SLEEP    3229
#define ID_SCHEDULE_SUN_WAKE     3230   // Fan,Hour,Minute,CoolSetpoint,HeatSetpoint
#define ID_SCHEDULE_SUN_LEAVE    3231
#define ID_SCHEDULE_SUN_RETURN   3232
#define ID_SCHEDULE_SUN_SLEEP    3233
#define ID_OUTDOOR_TEMP          3234   // OutdoorTemperature
#define ID_DLC                   3235   // CycleDuration,CyclePeriod,Duration,Override,OverrideDisable
#define ID_FILTER                3236   // FilterRemaining,FilterRestart
#define ID_SETPOINT_LIMITS       3237   // LowerCoolSetpointLimit,UpperHeatSetpointLimit

#define ID_RUNTIMES              3238   // CoolRuntime Minutes, HeatRuntime Minutes.  Value -1 indicates unknown.
#define ID_BATTERY               3239   // Battery Status (GOOD,BAD,(UNKNOWN))
#define ID_UTILITY_SETPOINTS     3240   // CoolSetpoint,HeatSetpoint,Duration,Mode,Tier,Override,OverrideDisable,AIRDisable

#define ID_STRING                3299   // (Any string that you want to throw in here, could have multiple entries in the table)


class IM_EX_DEVDB CtiDeviceGatewayStat : public CtiDeviceIED
{
public:

    typedef set< CtiPendingStatOperation > OpCol_t;        // The outstanding operaitons.
    typedef map< UINT, RWCString > StatPrintList_t;
    typedef vector< pair<USHORT, USHORT> > StatResponse_t;

    CtiDeviceGatewayStat(ULONG sn = 0);
    CtiDeviceGatewayStat(const CtiDeviceGatewayStat& aRef)
    {
        *this = aRef;
    }

    virtual ~CtiDeviceGatewayStat();

    CtiDeviceGatewayStat& operator=(const CtiDeviceGatewayStat& aRef);

    bool isPrimed() const;
    CtiDeviceGatewayStat& setPrimed(bool prime = true);

    LONG getDeviceSerialNumber() const
    {
        return _deviceSN;
    }

    bool operator<(const CtiDeviceGatewayStat& aRef)
    {
        return _deviceSN < aRef.getDeviceSerialNumber();
    }

    bool setLastControlSent(CtiOutMessage *&OutMessage);
    int processParse(SOCKET msgsock, CtiCommandParser &parse, CtiOutMessage *&OutMessage);
    int processSchedule(SOCKET msgsock, CtiCommandParser &parse, CtiOutMessage *&OutMessage);

    bool convertGatewayRXStruct( GATEWAYRXSTRUCT &GatewayRX );
    void sendGet(SOCKET msgsock, USHORT Type);
    bool sendSetDLC(SOCKET msgsock, UCHAR OffCycleDuration, UCHAR CyclePeriod, USHORT DLCDuration, UCHAR OverrideDisable);
    void sendSetDLCOverride(SOCKET msgsock, UCHAR DLCOverride);
    void sendSetFanSwitch(SOCKET msgsock, UCHAR FanSwitch);
    void sendSetFilterRestart(SOCKET msgsock, UCHAR Restart);
    void sendSetSchedule(SOCKET msgsock, UCHAR Day, UCHAR Period, SHORT HeatSetpoint, SHORT CoolSetpoint, UCHAR Hour, UCHAR Minute, UCHAR Fan);
    void sendSetpointLimits(SOCKET msgsock, SHORT UpperHeatLimit, SHORT LowerCoolLimit);

    #define EP_SETPOINT_PRIORITY_COOL           FALSE
    #define EP_SETPOINT_PRIORITY_HEAT           TRUE

    #define EP_SETPOINT_STATUS_RUNPROGRAM       0
    #define EP_SETPOINT_STATUS_TEMPORARY        1
    #define EP_SETPOINT_STATUS_HOLD             2
    #define EP_SETPOINT_STATUS_VACATIONHOLD     254

    void sendSetSetpoints(SOCKET msgsock, USHORT HeatSetpoint, USHORT CoolSetpoint, UCHAR SetpointPriority, UCHAR SetpointStatus, UCHAR VacationHoldDays, UCHAR VacationPeriod);

    #define EP_SETSYSTEM_EMHEAT         0       // = Emergency Heat
    #define EP_SETSYSTEM_HEAT           1       // = Heat
    #define EP_SETSYSTEM_OFF            2       // = Off
    #define EP_SETSYSTEM_COOL           3       // = Cool
    #define EP_SETSYSTEM_AUTO           4       // = Auto

    void sendSetSystemSwitch(SOCKET msgsock, UCHAR SystemSwitch);
    void sendSetUtilSetpoints(SOCKET msgsock, SHORT UtilHeatSetpoint, SHORT UtilCoolSetpoint, USHORT UtilDuration, UCHAR PriceTier, UCHAR Mode, UCHAR UserOverrideDisable, UCHAR AdaptiveRecoveryDisable);
    void sendSetUtilOverride(SOCKET msgsock, UCHAR Override);
    void sendUnbindDevice(SOCKET msgsock);
    void sendRestartFilter(SOCKET msgsock);
    void sendQueryRuntime(SOCKET msgsock, UCHAR Reset);
    void sendControlSetpoint(SOCKET msgsock, unsigned char flaghi, unsigned char flaglo, unsigned char minTemp, unsigned char maxTemp, unsigned short T_r, unsigned short T_a, unsigned short T_b, unsigned char delta_S_b,  unsigned short T_c, unsigned short T_d, unsigned char delta_S_d, unsigned short T_e, unsigned short T_f, unsigned char delta_S_f, unsigned char hold );

    CtiOutMessage*& getLastControlSent();
    bool CtiDeviceGatewayStat::generatePacketDataSchedule();

    bool printPacketData( );
    bool generatePrintList( );
    bool generatePacketData( USHORT Type, int day = 0, int period = 0 );
    bool clearPrintList();
    bool updatePrintList(USHORT Type, RWCString &str );
    RWCString generateSchedulePeriod(int day, int period);

    int returnCodeCount(USHORT message_type);
    int removeReturnCode(USHORT message_type);

    enum {
        scaleToStat = 0,
        scaleFahrenheit,
        scaleCelsius
    };

    short convertFromStatTemp (short Temp, int tempScale = scaleToStat);
    short convertToStatTemp (short Temp, int tempScale = scaleToStat);
    RWCString getUnitName(bool abbreviated = true, bool nospaces = false);


    static int convertCDayToStatDay(int dow);
    static int convertStatDayToCDay(UCHAR Day);

    void addOperation(const CtiPendingStatOperation &op);
    int checkPendingOperations(  );

    bool getCompletedOperation( CtiPendingStatOperation &op );

    SHORT getCurrentHeatSchedule() const;
    SHORT getCurrentCoolSchedule() const;

    static int estimateSetpointPriority();

    static void BuildHeader(GWHEADER *pGWH, unsigned short  Type, unsigned short Length, unsigned myid);
    virtual CtiMessage* rsvpToDispatch(bool clearMessage = true);
    RWCString printListAsString(UINT Type) const;


protected:

    mutable CtiMutex        _collMux;
    StatPrintList_t _printlist;          // Last collected stuff from the gw.
    StatResponse_t  _responseVector;
    OpCol_t         _operations;        // Outstanding operations on the stat.

private:

    enum
    {
        PO_CoolRuntime = 1,
        PO_HeatRuntime,
        PO_CoolSetpoint,
        PO_HeatSetpoint,
        PO_DisplayedTemperature,
        PO_Fanswitch,
        PO_OutdoorTemp,
        PO_SystemSwitch,
        PO_UtilCoolSetpoint,
        PO_UtilHeatSetpoint,
        PO_Filter,
        PO_Battery
    };


    CtiMultiMsg *_pMulti;
    CtiOutMessage *_controlOutMessage;

    bool _primed;
    LONG _deviceSN;     // This is the GUID serial number of the stat passed on every GW communication!

    struct {
        ULONG _utime;
        UCHAR _allowedSystemSwitch;

    } _allowedSystemSwitch;

    struct {
        ULONG _utime;
        UCHAR _battery;

    } _battery;

    struct {
        ULONG _utime;
        UCHAR _day;
        UCHAR _hour;
        UCHAR _minute;
        UCHAR _second;

    } _clock;

    struct {
        ULONG _utime;
        USHORT _local;
        USHORT _remote;

    } _rssi;

    struct {
        ULONG _utime;
        USHORT _coolRuntime;
        USHORT _heatRuntime;

    } _runtime;

    struct {
        ULONG _utime;
        ULONG _ch_utime;
        SHORT _coolSetpoint;
        SHORT _heatSetpoint;
        UCHAR _setpointStatus;
        UCHAR _vacationHoldDays;
        UCHAR _vacationHoldPeriod;

    } _setpoints;

    struct {
        ULONG _utime;
        UCHAR _deadband;

    } _deadband;

    struct {
        ULONG _utime;
        UCHAR _deviceAbsent;

    } _deviceAbsent;

    struct {
        ULONG _utime;
        UCHAR _deviceType;

    } _deviceType;

    struct {
        ULONG _utime;
        SHORT _displayedTemperature;
        UCHAR _displayedTempUnits;

    } _displayedTemp;

    struct {
        ULONG _utime;
        ULONG _ch_utime;
        USHORT _cycleDuration;
        USHORT _cyclePeriod;
        USHORT _duration;
        UCHAR _override;
        UCHAR _overrideDisable;

    } _DLC;

    struct {
        ULONG _utime;
        ULONG _ch_utime;
        UCHAR _fanSwitch;

    } _fanSwitch;

    struct {
        ULONG _utime;
        ULONG _ch_utime;
        UCHAR _filterRemaining;
        UCHAR _filterRestart;

    } _filter;

    struct {
        ULONG _utime;
        UCHAR _heatPumpFault;

    } _heatPumpFault;

    struct {
        ULONG _utime;
        ULONG _ch_utime;
        SHORT _lowerCoolSetpointLimit;
        SHORT _upperHeatSetpointLimit;

    } _setpointLimits;

    struct {
        ULONG _utime;
        SHORT _outdoorTemperature;

    } _outdoorTemp;

    struct {
        ULONG _utime;
        ULONG _ch_utime;
        UCHAR _day;
        UCHAR _period;

    } _lastSchedule;         // [day][period]

    struct {
        ULONG _utime;
        ULONG _ch_utime;
        // UCHAR _day;
        // UCHAR _period;
        UCHAR _fan;
        UCHAR _hour;
        UCHAR _minute;
        SHORT _coolSetpoint;
        SHORT _heatSetpoint;

    } _schedule[7][EP_PERIODS_PER_DAY];         // [day][period]

    struct {
        ULONG _utime;
        ULONG _ch_utime;
        UCHAR _systemSwitch;

    } _systemSwitch;

    struct {
        ULONG _utime;
        ULONG _ch_utime;
        SHORT _utilCoolSetpoint;
        SHORT _utilHeatSetpoint;
        UCHAR _utilMode;
        USHORT _utilDuration;
        USHORT _utilPriceTier;
        UCHAR _utilAIRDisable;
        UCHAR _utilUserOverride;
        UCHAR _utilUserOverrideDisable;

    } _utilSetpoint;

    struct {
        ULONG _utime;

    } _deviceBound;

    struct {
        ULONG _utime;

    } _deviceUnbound;

    struct {
        ULONG _utime;
        USHORT _returnCode;
        USHORT _setType;

    } _returnCode;

    int parseGetValueRequest(CtiCommandParser &parse);
    int parsePutConfigRequest(CtiCommandParser &parse);
    void generateReplyVector(OpCol_t::value_type &valtype, UINT operation = 0);
    bool generateTidbitToDatabase( USHORT Type, int day, int period );
    RWCString generateTidbitScheduleToDatabase(int day, int period);
    int processSchedulePeriod(SOCKET msgsock, CtiCommandParser &parse, CtiOutMessage *&OutMessage, int dow, int pod, BYTE per);

    bool verifyGatewayDid();
    void postAnalogOutputPoint(UINT Type, UINT pointoffset, double value);


};
#endif // #ifndef __DEV_GWSTAT_H__
