#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   dev_gwstat
*
* Date:   6/11/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.21 $
* DATE         :  $Date: 2006/03/23 15:29:16 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include "dsm2.h"
#include "dev_gwstat.h"
#include "devicetypes.h"
#include "numstr.h"
#include "logger.h"
#include "msg_multi.h"
#include "pt_numeric.h"
#include "tbl_gateway_end_device.h"
#include "utility.h"
#include "rwutil.h"
#include "ctidate.h"
#include "ctitime.h"


using std::make_pair;

#define GATEWAY_TEMPERATURE_PRECISION 0

CtiDeviceGatewayStat::CtiDeviceGatewayStat(ULONG sn) :
_pMulti(0),
_controlOutMessage(0),
_primed(false),
_deviceSN(sn)
{
    _lastRuntimeRead = YUKONEOT;

    _allowedSystemSwitch._utime = YUKONEOT;
    _battery._utime = YUKONEOT;
    _clock._utime = YUKONEOT;
    _runtime._utime = YUKONEOT;
    _setpoints._utime = YUKONEOT;
    _setpoints._ch_utime = YUKONEOT;
    _deadband._utime = YUKONEOT;
    _deviceAbsent._utime = YUKONEOT;
    _deviceType._utime = YUKONEOT;
    _displayedTemp._utime = YUKONEOT;
    _DLC._utime = YUKONEOT;
    _DLC._ch_utime = YUKONEOT;
    _fanSwitch._utime = YUKONEOT;
    _fanSwitch._ch_utime = YUKONEOT;
    _filter._utime = YUKONEOT;
    _filter._ch_utime = YUKONEOT;
    _heatPumpFault._utime = YUKONEOT;
    _setpointLimits._utime = YUKONEOT;
    _setpointLimits._ch_utime = YUKONEOT;
    _outdoorTemp._utime = YUKONEOT;

    for(int day = 0; day < 7; day++)
    {
        for(int per = 0; per < EP_PERIODS_PER_DAY; per ++)
        {
            _schedule[day][per]._utime = YUKONEOT;         // [day][period]
            _schedule[day][per]._ch_utime = YUKONEOT;         // [day][period]
        }
    }
    _systemSwitch._utime = YUKONEOT;
    _systemSwitch._ch_utime = YUKONEOT;
    _utilSetpoint._utime = YUKONEOT;
    _utilSetpoint._ch_utime = YUKONEOT;
    _deviceBound._utime = YUKONEOT;
    _deviceUnbound._utime = YUKONEOT;
}

CtiDeviceGatewayStat::~CtiDeviceGatewayStat()
{
    if(_pMulti)
    {
        delete _pMulti;
        _pMulti = 0;
    }
    if(_controlOutMessage)
    {
        delete _controlOutMessage;
        _controlOutMessage = 0;
    }
}

CtiDeviceGatewayStat& CtiDeviceGatewayStat::operator=(const CtiDeviceGatewayStat& aRef)
{
    if(this != &aRef)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return *this;
}

bool CtiDeviceGatewayStat::isPrimed() const
{
    return _primed;
}
CtiDeviceGatewayStat& CtiDeviceGatewayStat::setPrimed(bool prime)
{
    _primed = prime;
    return *this;
}

// The stat stores most temps as hundreths of a degree C
short CtiDeviceGatewayStat::convertFromStatTemp(short Temp, int tempScale)
{
    long TempTemp = (long)Temp;

    {
        if(tempScale == scaleCelsius || (tempScale == scaleToStat && _displayedTemp._displayedTempUnits == 1))      // Celsius
        {
            TempTemp = (((TempTemp + 1)) / 100);

            if(TempTemp < 0 || TempTemp > 100)
            {
                TempTemp = 0;
            }
        }
        else
        {
            TempTemp = (((TempTemp + 1) * 9) / 500) + 32;

            if(TempTemp < 32 || TempTemp > 212)
            {
                TempTemp = 0;
            }
        }

        return((short) TempTemp);
    }
}

// The stat stores most temps as hundreths of a degree C
// This routine converts from degrees F for sending
short CtiDeviceGatewayStat::convertToStatTemp(short Temp, int tempScale)
{
    long TempTemp = (long) Temp;

    if(tempScale == scaleCelsius || (tempScale == scaleToStat && _displayedTemp._displayedTempUnits == 1))      // Celsius
    {
        TempTemp = (TempTemp * 100);
    }
    else
    {
        TempTemp = ((TempTemp - 32) * 500) / 9;
    }

    return((short) TempTemp);
}


bool CtiDeviceGatewayStat::convertGatewayRXStruct( GATEWAYRXSTRUCT &GatewayRX )
{
    bool status = false;

    CtiTime now;

    USHORT Type = ntohs(GatewayRX.Type);

    int day = 0;
    int period = 0;

    switch(Type)
    {
    case TYPE_ALLOWEDSYSTEMSWITCH:
        {
            // Record the occurrence.
            _allowedSystemSwitch._utime = now.seconds();
            _allowedSystemSwitch._allowedSystemSwitch = GatewayRX.U.AllowedSystemSwitch.AllowedSystemSwitch;

            break;
        }
    case TYPE_BATTERY:
        {
            _battery._utime = now.seconds();
            _battery._battery = GatewayRX.U.Battery.Battery;

            break;
        }
    case TYPE_RSSI:
        {
            _rssi._utime = now.seconds();
            _rssi._local = ntohs(GatewayRX.U.Rssi.LocalRSSI);
            _rssi._remote = ntohs(GatewayRX.U.Rssi.RemoteRSSI);

            break;
        }
    case TYPE_RUNTIME:
        {
            _runtime._utime = now.seconds();
            _runtime._coolRuntime = ntohs(GatewayRX.U.Runtime.CoolRuntime);
            _runtime._heatRuntime = ntohs(GatewayRX.U.Runtime.HeatRuntime);

            break;
        }
    case TYPE_SETPOINTS_CH:
    case TYPE_SETPOINTS:
        {
            if(Type == TYPE_SETPOINTS_CH)
            {
                _setpoints._ch_utime = now.seconds();
            }
            else
            {
                _setpoints._utime = now.seconds();
            }

            SHORT heat = ntohs(GatewayRX.U.Setpoints.HeatSetpoint);
            SHORT cool = ntohs(GatewayRX.U.Setpoints.CoolSetpoint);

            _setpoints._coolSetpoint = cool;
            _setpoints._heatSetpoint = heat;
            _setpoints._setpointStatus = GatewayRX.U.Setpoints.SetpointStatus;
            _setpoints._vacationHoldDays = GatewayRX.U.Setpoints.VacationHoldDays;
            _setpoints._vacationHoldPeriod = GatewayRX.U.Setpoints.VacationHoldPeriod;

            break;
        }
    case TYPE_DEADBAND:
        {
            _deadband._utime = now.seconds();
            _deadband._deadband = GatewayRX.U.Deadband.Deadband;

            break;
        }
    case TYPE_DEVICEABSENT:
        {
            _deviceAbsent._utime = now.seconds();
            _deviceAbsent._deviceAbsent = GatewayRX.U.DeviceAbsent.DeviceAbsent;

            break;
        }
    case TYPE_DEVICETYPE:
        {
            _deviceType._utime = now.seconds();
            _deviceType._deviceType = GatewayRX.U.DeviceType.DeviceType;

            break;
        }
    case TYPE_DISPLAYEDTEMPERATURE:
        {
            _displayedTemp._utime = now.seconds();
            _displayedTemp._displayedTempUnits = GatewayRX.U.DisplayedTemp.DisplayedTempUnits;
            _displayedTemp._displayedTemperature = ntohs (GatewayRX.U.DisplayedTemp.DisplayedTemperature);

            break;
        }
    case TYPE_DLC_CH:
    case TYPE_DLC:
        {
            if(Type == TYPE_DLC_CH)
            {
                _DLC._ch_utime = now.seconds();
            }
            else
            {
                _DLC._utime = now.seconds();
            }
            _DLC._cycleDuration = ntohs(GatewayRX.U.DLC.CycleDuration);
            _DLC._cyclePeriod = ntohs(GatewayRX.U.DLC.CyclePeriod);
            _DLC._duration = ntohs(GatewayRX.U.DLC.Duration);
            _DLC._override = GatewayRX.U.DLC.Override;
            _DLC._overrideDisable = GatewayRX.U.DLC.OverrideDisable;

            break;
        }
    case TYPE_FANSWITCH_CH:
    case TYPE_FANSWITCH:
        {
            if(Type == TYPE_FANSWITCH_CH)
            {
                _fanSwitch._ch_utime = now.seconds();
            }
            else
            {
                _fanSwitch._utime = now.seconds();
            }
            _fanSwitch._fanSwitch= GatewayRX.U.FanSwitch.FanSwitch;

            break;
        }
    case TYPE_FILTER_CH:
    case TYPE_FILTER:
        {
            if(Type == TYPE_FILTER_CH)
            {
                _filter._ch_utime = now.seconds();
            }
            else
            {
                _filter._utime = now.seconds();
            }
            _filter._filterRemaining = GatewayRX.U.Filter.FilterRemaining;
            _filter._filterRestart = GatewayRX.U.Filter.FilterRestart;

            break;
        }
    case TYPE_HEATPUMPFAULT:
        {
            _heatPumpFault._utime = now.seconds();
            _heatPumpFault._heatPumpFault = GatewayRX.U.HeatPumpFault.HeatPumpFault;

            break;
        }
    case TYPE_SETPOINTLIMITS_CH:
    case TYPE_SETPOINTLIMITS:
        {
            if(Type == TYPE_SETPOINTLIMITS_CH)
            {
                _setpointLimits._ch_utime = now.seconds();
            }
            else
            {
                _setpointLimits._utime = now.seconds();
            }
            _setpointLimits._lowerCoolSetpointLimit = ntohs (GatewayRX.U.SetpointLimits.LowerCoolSetpointLimit);
            _setpointLimits._upperHeatSetpointLimit = ntohs (GatewayRX.U.SetpointLimits.UpperHeatSetpointLimit);

            break;
        }
    case TYPE_OUTDOORTEMP:
        {
            _outdoorTemp._utime = now.seconds();
            _outdoorTemp._outdoorTemperature = ntohs (GatewayRX.U.OutdoorTemp.OutdoorTemperature);

            break;
        }
    case TYPE_SCHEDULE_CH:
    case TYPE_SCHEDULE:
        {
            // On a __GET__ these things are true!
            //
            //  Day - the day for which information is requested
            //      0 = Monday
            //      1 = Tuesday
            //      2 = Wednesday
            //      3 = Thursday
            //      4 = Friday
            //      5 = Saturday
            //      6 = Sunday
            //  Period - the period for which information is requested.
            //      0 = Wake period
            //      1 = Leave period
            //      2 = Return period
            //      3 = Sleep period

            day = convertStatDayToCDay(GatewayRX.U.Schedule.Day);
            period = GatewayRX.U.Schedule.Period;

            try
            {
                if( (day >= 0 && day < 7) && (period >= 0 && period < EP_PERIODS_PER_DAY) )   // valid day & period.
                {
                    if(Type == TYPE_SCHEDULE_CH)
                    {
                        _schedule[day][period]._ch_utime = now.seconds();
                    }
                    else
                    {
                        _schedule[day][period]._utime = now.seconds();
                    }
                    _schedule[day][period]._fan = GatewayRX.U.Schedule.Fan;
                    _schedule[day][period]._coolSetpoint = ntohs(GatewayRX.U.Schedule.CoolSetpoint);
                    _schedule[day][period]._heatSetpoint = ntohs(GatewayRX.U.Schedule.HeatSetpoint);
                    _schedule[day][period]._hour = GatewayRX.U.Schedule.Hour;
                    _schedule[day][period]._minute = GatewayRX.U.Schedule.Minute;
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Invalid Schedule day/period received." << endl;
                }
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            break;
        }
    case TYPE_SYSTEMSWITCH_CH:
    case TYPE_SYSTEMSWITCH:
        {
            if(Type == TYPE_SYSTEMSWITCH_CH)
            {
                _systemSwitch._ch_utime = now.seconds();
            }
            else
            {
                _systemSwitch._utime = now.seconds();
            }
            _systemSwitch._systemSwitch = GatewayRX.U.SystemSwitch.SystemSwitch;

            break;
        }
    case TYPE_UTILSETPOINT_CH:
    case TYPE_UTILSETPOINT:
        {
            if(Type == TYPE_UTILSETPOINT_CH)
            {
                _utilSetpoint._ch_utime = now.seconds();
            }
            else
            {
                _utilSetpoint._utime = now.seconds();
            }
            _utilSetpoint._utilAIRDisable = GatewayRX.U.UtilSetpoint.UtilAIRDisable;
            _utilSetpoint._utilCoolSetpoint = ntohs(GatewayRX.U.UtilSetpoint.UtilCoolSetpoint);
            _utilSetpoint._utilDuration = ntohs(GatewayRX.U.UtilSetpoint.UtilDuration);
            _utilSetpoint._utilHeatSetpoint = ntohs(GatewayRX.U.UtilSetpoint.UtilHeatSetpoint);
            _utilSetpoint._utilMode = GatewayRX.U.UtilSetpoint.UtilMode;
            _utilSetpoint._utilPriceTier = ntohs(GatewayRX.U.UtilSetpoint.UtilPriceTier);
            _utilSetpoint._utilUserOverride = GatewayRX.U.UtilSetpoint.UtilUserOverride;
            _utilSetpoint._utilUserOverrideDisable = GatewayRX.U.UtilSetpoint.UtilUserOverrideDisable;

            break;
        }
    case TYPE_CLOCK:
        {
            // Return value:
            //  0 = Monday
            //  1 = Tuesday
            //  2 = Wednesday
            //  3 = Thursday
            //  4 = Friday
            //  5 = Saturday
            //  6 = Sunday
            //  255 = unknown
            //  At initialization, until the first EVNT_CLOCK event, the value will be 255.

            _clock._utime = now.seconds();
            _clock._day = GatewayRX.U.Clock.Day;
            _clock._hour = GatewayRX.U.Clock.Hour;
            _clock._minute = GatewayRX.U.Clock.Minute;
            _clock._second = GatewayRX.U.Clock.Second;

            break;
        }
    case TYPE_CLOCKDST:
        {
            // Return value:
            //  0 = Not DST
            //  1 = DST Active
            //  255 = unknown
            //  At initialization, until the first EVNT_CLOCK event, the value will be 255.

            _clockDST._utime = now.seconds();
            _clockDST._dst = GatewayRX.U.ClockDST.DST;

            break;
        }
    case TYPE_DEVICEBOUND:
        {
            _deviceBound._utime = now.seconds();
            break;
        }
    case TYPE_DEVICEUNBOUND:
        {
            _deviceUnbound._utime = now.seconds();
            break;
        }
    case TYPE_RETURNCODE:
        {
            _returnCode._utime = now.seconds();
            _returnCode._returnCode = ntohs(GatewayRX.Return.ReturnCode);
            _returnCode._setType = ntohs(GatewayRX.Return.SetType);

            {
                CtiLockGuard< CtiMutex > gd(_collMux);
                _responseVector.push_back( make_pair(_returnCode._setType, _returnCode._returnCode) );
            }

            break;
        }
    default:
        break;
    }

    generatePacketData(Type, day, period);
    generateTidbitToDatabase(Type, day, period);

    return status;
}


bool CtiDeviceGatewayStat::clearPrintList()
{
    CtiLockGuard< CtiMutex > gd(_collMux);

    bool hasentries = !_printlist.empty();
    _printlist.clear();

    return hasentries;
}

string CtiDeviceGatewayStat::printListAsString(UINT Type) const
{
    CtiLockGuard< CtiMutex > gd(_collMux);

    string retStr;
    {
        StatPrintList_t::const_iterator itr = _printlist.find(Type);

        if(itr != _printlist.end())
        {
            retStr += (*itr).second;
        }
    }

    return retStr;
}

bool CtiDeviceGatewayStat::printPacketData( )
{
    CtiLockGuard< CtiMutex > gd(_collMux);

    {
        StatPrintList_t::const_iterator itr;

        CtiLockGuard<CtiLogger> doubt_guard(dout);
        for(itr = _printlist.begin() ; itr != _printlist.end(); itr++)
        {
            dout << (*itr).second << endl;
        }
    }

    return !_printlist.empty();
}

bool CtiDeviceGatewayStat::updatePrintList(USHORT Type, string &str )
{
    CtiLockGuard< CtiMutex > gd(_collMux);

    pair< StatPrintList_t::iterator, bool > insertpair  = _printlist.insert( StatPrintList_t::value_type(Type, str) );

    if(insertpair.second == false)
    {
        StatPrintList_t::iterator &itr = insertpair.first;
        (*itr).second = str; // first is an iterator!
    }

    return !_printlist.empty();
}

bool CtiDeviceGatewayStat::generatePrintList( )
{
    bool status = false;

    clearPrintList();
    generatePacketData(TYPE_ALLOWEDSYSTEMSWITCH);
    generatePacketData(TYPE_BATTERY);
    generatePacketData(TYPE_RUNTIME);
    generatePacketData(TYPE_SETPOINTS);
    generatePacketData(TYPE_DEADBAND);
    generatePacketData(TYPE_DEVICEABSENT);
    generatePacketData(TYPE_DEVICETYPE);
    generatePacketData(TYPE_DISPLAYEDTEMPERATURE);
    generatePacketData(TYPE_DLC);
    generatePacketData(TYPE_FANSWITCH);
    generatePacketData(TYPE_FILTER);
    generatePacketData(TYPE_HEATPUMPFAULT);
    generatePacketData(TYPE_SETPOINTLIMITS);
    generatePacketData(TYPE_OUTDOORTEMP);
    generatePacketData(TYPE_SYSTEMSWITCH);
    generatePacketData(TYPE_UTILSETPOINT);
    generatePacketData(TYPE_CLOCK);
    generatePacketData(TYPE_CLOCKDST);
    generatePacketData(TYPE_DEVICEBOUND);
    generatePacketData(TYPE_DEVICEUNBOUND);

    generatePacketDataSchedule();

    return status;
}

bool CtiDeviceGatewayStat::generatePacketData( USHORT Type, int day, int period )
{
    string astr;
    bool status = false;
    CtiTime now;


    switch(Type)
    {
    case TYPE_ALLOWEDSYSTEMSWITCH:
        {
            now = CtiTime(_allowedSystemSwitch._utime);
            astr = now.asString() + string(" Stat ");
            astr += CtiNumStr(getDeviceSerialNumber()).spad(3);
            astr += string(" Received Allowed System Switch: ");
            if(_allowedSystemSwitch._allowedSystemSwitch == 0)
            {
                astr += (string("Allowed System Switch Unknown"));
            }
            else
            {
                astr += string("  Allowed:  ");
                if(_allowedSystemSwitch._allowedSystemSwitch & 0x01)
                {
                    astr += string("ER Heat ");
                }
                if(_allowedSystemSwitch._allowedSystemSwitch & 0x02)
                {
                    astr += string("Heat ");
                }
                if(_allowedSystemSwitch._allowedSystemSwitch & 0x04)
                {
                    astr += string("Off ");
                }
                if(_allowedSystemSwitch._allowedSystemSwitch & 0x08)
                {
                    astr += string("Cool ");
                }
                if(_allowedSystemSwitch._allowedSystemSwitch & 0x10)
                {
                    astr += string("Auto ");
                }

                updatePrintList(Type, astr);
            }

            break;
        }
    case TYPE_BATTERY:
        {
            now = CtiTime(_battery._utime);
            astr = now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3);
            astr += string(" Received Battery Status: ");

            astr += (" Battery Status:  ");
            switch(_battery._battery)
            {
            case 0:
                astr += string("Bad");
                break;

            case 1:
                astr += string("Good");
                break;

            case 2:
                astr += string("Unknown");
                break;

            default:
                astr += string("Invalid");
                break;
            }

            updatePrintList(Type, astr);
            postAnalogOutputPoint(Type, PO_Filter, _battery._battery );
            break;
        }
    case TYPE_RSSI:
        {
            now = CtiTime(_rssi._utime);
            astr = now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3);
            astr += string(" RSSI: ");
            astr += (string("Local:  " + CtiNumStr(_rssi._local)));
            astr += (string(" Remote:  " + CtiNumStr(_rssi._remote)));

            updatePrintList(Type, astr);

            break;
        }
    case TYPE_RUNTIME:
        {
            now = CtiTime(_runtime._utime);
            astr = now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3);
            astr += string(" Runtimes: ");
            astr += (string("Cool Runtime:  " + CtiNumStr(_runtime._coolRuntime)+ string(" Minutes / ")));
            astr += (string("Heat Runtime:  " + CtiNumStr(_runtime._heatRuntime)+ string(" Minutes")));

            updatePrintList(Type, astr);

            postAnalogOutputPoint(Type, PO_CoolRuntime, _runtime._coolRuntime );
            postAnalogOutputPoint(Type, PO_HeatRuntime, _runtime._heatRuntime );

            if( _lastRuntimeRead != CtiTime(YUKONEOT) )
            {
                ULONG elapsedTime = (_runtime._utime - _lastRuntimeRead + 60) / 60;  // Elapsed time in minutes (round up always)!

                postAnalogOutputPoint(Type, PO_CoolRuntimePercentage, ((_runtime._coolRuntime - _lastCoolRuntime) / elapsedTime) );
                postAnalogOutputPoint(Type, PO_HeatRuntimePercentage, ((_runtime._heatRuntime - _lastHeatRuntime) / elapsedTime) );
            }

            // Get ready for the next read and "usage calc"
            _lastCoolRuntime = _runtime._coolRuntime;
            _lastHeatRuntime = _runtime._heatRuntime;
            _lastRuntimeRead = _runtime._utime;

            break;
        }
    case TYPE_SETPOINTS_CH:
    case TYPE_SETPOINTS:
        {
            if(Type == TYPE_SETPOINTS_CH)
            {
                now = CtiTime(_setpoints._ch_utime);
                astr = string(now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" Setpoint Confirmed: \n"));
            }
            else
            {
                now = CtiTime(_setpoints._utime);
                astr = string(now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" Setpoint Received: \n"));
            }

            astr += (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + " Cool Setpoint:  ");
            if(_setpoints._coolSetpoint >= 0x7f00)
            {
                astr += string("Unknown\n");
            }
            else
            {
                astr += string(CtiNumStr(convertFromStatTemp(_setpoints._coolSetpoint)) + getUnitName() + "\n");
            }

            astr += string(now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + " Heat Setpoint:  ");
            if(_setpoints._heatSetpoint >= 0x7f00)
            {
                astr += string("Unknown\n");
            }
            else
            {
                astr += string(CtiNumStr(convertFromStatTemp(_setpoints._heatSetpoint)) + getUnitName() + "\n");
            }

            astr += string(now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + " Setpoint Status:  ");
            switch(_setpoints._setpointStatus)
            {
            case 0:
                astr += string(" Schedule Setpoint ");
                break;

            case 1:
                astr += string(" Temporary Setpoint ");
                break;

            case 2:
                astr += string(" Permanent Hold Setpoint ");
                break;

            case 254:
                astr += string(" Vacation Hold Setpoint ");

                if(_setpoints._vacationHoldDays == 0xffff)
                {
                    astr += string("Vacation Hold Days Invalid");
                }
                else
                {
                    astr += string(" Days:  " + CtiNumStr(_setpoints._vacationHoldDays).zpad(3) );
                }

                astr += string(" Period:  ");

                switch(_setpoints._vacationHoldPeriod)
                {
                case 0:
                    astr += string("Wake");
                    break;

                case 1:
                    astr += string("Leave");
                    break;

                case 2:
                    astr += string("Return");
                    break;

                case 3:
                    astr += string("Sleep");
                    break;

                case 255:
                    astr += string("Unknown");
                    break;

                default:
                    astr += string("Invalid");
                    break;
                }
                break;

            case 255:
                astr += string("Unknown");
                break;

            default:
                astr += string("Invalid");
            }

            updatePrintList(Type, astr);

            postAnalogOutputPoint(Type, PO_CoolSetpoint, convertFromStatTemp(_setpoints._coolSetpoint) );
            postAnalogOutputPoint(Type, PO_HeatSetpoint, convertFromStatTemp(_setpoints._heatSetpoint) );

            break;
        }
    case TYPE_DEADBAND:
        {
            now = CtiTime(_deadband._utime);
            astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" Deadband Received: "));

            if(_deadband._deadband == 255)
            {
                astr += string("Unknown");
            }
            else
            {
                astr += string(CtiNumStr(_deadband._deadband) + getUnitName());
            }

            updatePrintList(Type, astr);

            break;
        }
    case TYPE_DEVICEABSENT:
        {
            now = CtiTime(_deviceAbsent._utime);
            astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" Device Absent Received: " ));

            if(_deviceAbsent._deviceAbsent)
            {
                astr += string("TRUE");
            }
            else
            {
                astr += string("FALSE");
            }

            updatePrintList(Type, astr);

            break;
        }
    case TYPE_DEVICETYPE:
        {
            now = CtiTime(_deviceType._utime);
            astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" Device Type Received: "));

            switch(_deviceType._deviceType)
            {
            case 0:
                astr += string("Unknown");
                break;

            case 1:
                astr += string("T8665C");
                break;

            case 2:
                astr += string("T8665D");
                break;

            case 3:
                astr += string("T8665E");
                break;

            default:
                astr += string("Invalid = " + CtiNumStr((int)_deviceType._deviceType));
                break;
            }

            updatePrintList(Type, astr);

            break;
        }
    case TYPE_DISPLAYEDTEMPERATURE:
        {
            now = CtiTime(_displayedTemp._utime);
            astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" Displayed Temperature Received: " ));

            float dt = (float)_displayedTemp._displayedTemperature;
            if(_displayedTemp._displayedTempUnits == 1)     // Celsius is given in 1/2 degrees.
            {
                dt = dt / 2 ;
            }

            astr += string(CtiNumStr(dt, GATEWAY_TEMPERATURE_PRECISION) + getUnitName());
            updatePrintList(Type, astr);

            postAnalogOutputPoint(Type, PO_DisplayedTemperature, dt );

            break;
        }
    case TYPE_DLC_CH:
    case TYPE_DLC:
        {
            if(Type == TYPE_DLC_CH)
            {
                now = CtiTime(_DLC._ch_utime);
                astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" DLC Confirmed: "));
            }
            else
            {
                now = CtiTime(_DLC._utime);
                astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" DLC Received: " ));
            }

            if(_DLC._cycleDuration == 0xffff)
            {
                astr += (string("Unknown"));
            }
            else if(_DLC._cycleDuration <= 255)
            {
                astr += (CtiNumStr(_DLC._cycleDuration));
            }
            else
            {
                astr += (string("Invalid"));
            }

            astr += " of ";

            if(_DLC._cyclePeriod == 0xffff)
            {
                astr += (string("Unknown"));
            }
            else if(_DLC._cyclePeriod <= 255)
            {
                astr += (CtiNumStr(_DLC._cyclePeriod));
            }
            else
            {
                astr += (string("Invalid"));
            }

            astr +=  " off for ";

            if(_DLC._duration == 0xffff)
            {
                astr += (string("Unknown"));
            }
            else
            {
                astr += (CtiNumStr(_DLC._duration));
            }

            astr += " minutes.  ";

            switch(_DLC._override)
            {
            case 0:
                astr += (string("No Override"));
                break;

            case 1:
                astr += (string("Override Active"));
                break;

            case 255:
                astr += (string("Unknown Override"));
                break;

            default:
                astr += (string("Invalid Override"));
                break;
            }


            switch(_DLC._overrideDisable)
            {
            case 0:
                astr += (string("  / Available."));
                break;

            case 1:
                astr += (string("  / Prohibited."));
                break;

            case 255:
                astr += (string("  / Unknown."));
                break;

            default:
                astr += (string("  / Invalid."));
                break;
            }
            updatePrintList(Type, astr);

            break;
        }
    case TYPE_FANSWITCH_CH:
    case TYPE_FANSWITCH:
        {
            if(Type == TYPE_FANSWITCH_CH)
            {
                now = CtiTime(_fanSwitch._ch_utime);
                astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" FanSwitch Confirmed: "));
            }
            else
            {
                now = CtiTime(_fanSwitch._utime);
                astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" FanSwitch Received: "));
            }

            if(_fanSwitch._fanSwitch == 0)
            {
                astr += (string("Auto"));
            }
            else if(_fanSwitch._fanSwitch == 255)
            {
                astr += (string("Unknown"));
            }
            else if(_fanSwitch._fanSwitch >= 1 && _fanSwitch._fanSwitch <= 200)
            {
                astr += (string("On"));
            }
            else
            {
                astr += (string("Invalid"));
            }

            updatePrintList(Type, astr);
            postAnalogOutputPoint(Type, PO_Fanswitch, _fanSwitch._fanSwitch);

            break;
        }
    case TYPE_FILTER_CH:
    case TYPE_FILTER:
        {
            if(Type == TYPE_FILTER_CH)
            {
                now = CtiTime(_filter._ch_utime);
                astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" Filter Confirmed: "));
            }
            else
            {
                now = CtiTime(_filter._utime);
                astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" Filter Received: " ));
            }

            astr += ("Filter Restart:  ");
            if(_filter._filterRestart == 0)
            {
                astr += (string("Filter Timer Disabled"));
            }
            else if(_filter._filterRestart == 255)
            {
                astr += (string("Unknown"));
            }
            else
            {
                astr += (string(CtiNumStr(_filter._filterRestart) + " Days"));
                astr += (string(".  Filter Remaining:  "));
                if(_filter._filterRemaining == 0)
                {
                    astr += (string("Dirty"));
                }
                else if(_filter._filterRemaining == 255)
                {
                    astr += (string("Unknown"));
                }
                else
                {
                    astr += (string(CtiNumStr(_filter._filterRemaining) + " Days"));
                }

                updatePrintList(Type, astr);
                postAnalogOutputPoint(Type, PO_Filter, _fanSwitch._fanSwitch);
            }

            break;
        }
    case TYPE_HEATPUMPFAULT:
        {
            now = CtiTime(_heatPumpFault._utime);
            astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" Heatpump Fault Received: " ));

            switch(_heatPumpFault._heatPumpFault)
            {
            case 0:
                astr += (string("No Fault"));
                break;

            case 1:
                astr += (string("Fault"));
                break;

            case 255:
                astr += (string("Unknown"));
                break;

            default:
                astr += (string("Invalid"));
                break;
            }

            updatePrintList(Type, astr);

            break;
        }
    case TYPE_SETPOINTLIMITS_CH:
    case TYPE_SETPOINTLIMITS:
        {
            if(Type == TYPE_SETPOINTLIMITS_CH)
            {
                now = CtiTime(_setpointLimits._ch_utime);
                astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" SetPoint Limits Confirmed: "));
            }
            else
            {
                now = CtiTime(_setpointLimits._utime);
                astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" Setpoint Limits Received: " ));
            }

            astr += ((" Cool Limit: "));
            if(_setpointLimits._lowerCoolSetpointLimit >= 0x7f00)
            {
                astr += (string("Unknown"));
            }
            else
            {
                astr += (string(CtiNumStr(convertFromStatTemp(_setpointLimits._lowerCoolSetpointLimit)) + getUnitName()));
            }

            astr += string(" Heat Limit: ");
            if(_setpointLimits._upperHeatSetpointLimit >= 0x7f00)
            {
                astr += (string("Unknown"));
            }
            else
            {
                astr += (string(CtiNumStr(convertFromStatTemp(_setpointLimits._upperHeatSetpointLimit)) + getUnitName()));
            }
            updatePrintList(Type, astr);

            break;
        }
    case TYPE_OUTDOORTEMP:
        {
            now = CtiTime(_outdoorTemp._utime);
            astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" Outdoor Temperature Received: " ));

            if(_outdoorTemp._outdoorTemperature >= 0x7f00)
            {
                astr += (string("Unknown"));
            }
            else if((_outdoorTemp._outdoorTemperature & 0xff00) == 0x8000)
            {
                astr += (string("Shorted Sensor"));
            }
            else if((_outdoorTemp._outdoorTemperature & 0xff00) == 0x8100)
            {
                astr += (string("Open Sensor"));
            }
            else if((_outdoorTemp._outdoorTemperature & 0xff00) == 0x8200)
            {
                astr += (string("Not Available"));
            }
            else if((_outdoorTemp._outdoorTemperature & 0xff00) == 0x8300)
            {
                astr += (string("Out of Range High"));
            }
            else if((_outdoorTemp._outdoorTemperature & 0xff00) == 0x8400)
            {
                astr += (string("Out of Range Low"));
            }
            else if((_outdoorTemp._outdoorTemperature & 0xff00) == 0x8500)
            {
                astr += (string("Unreliable"));
            }
            else
            {
                astr += (string(CtiNumStr(convertFromStatTemp(_outdoorTemp._outdoorTemperature)) + getUnitName()));
            }
            updatePrintList(Type, astr);
            postAnalogOutputPoint(Type, PO_OutdoorTemp, convertFromStatTemp(_outdoorTemp._outdoorTemperature));

            break;
        }
    case TYPE_SCHEDULE_CH:
    case TYPE_SCHEDULE:
        {
            astr = generateSchedulePeriod(day, period);

            // Screwy packing here to create a unique schedule element.
            updatePrintList( (10000 + (day * 100) + period), astr);
            break;
        }
    case TYPE_SYSTEMSWITCH_CH:
    case TYPE_SYSTEMSWITCH:
        {
            if(Type == TYPE_SYSTEMSWITCH_CH)
            {
                now = CtiTime(_systemSwitch._ch_utime);
                astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" System Switch Confirmed: "));
            }
            else
            {
                now = CtiTime(_systemSwitch._utime);
                astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" System Switch Received: "));
            }

            switch(_systemSwitch._systemSwitch)
            {
            case 0:
                astr += (string("Emergency Heat"));
                break;

            case 1:
                astr += (string("Heat"));
                break;

            case 2:
                astr += (string("Off"));
                break;

            case 3:
                astr += (string("Cool"));
                break;

            case 4:
                astr += (string("Auto"));
                break;

            case 255:
                astr += (string("Unknown"));
                break;

            default:
                astr += (string("Invalid"));
                break;
            }
            updatePrintList(Type, astr);
            postAnalogOutputPoint(Type, PO_SystemSwitch, convertFromStatTemp(_outdoorTemp._outdoorTemperature));

            break;

        }
    case TYPE_UTILSETPOINT_CH:
    case TYPE_UTILSETPOINT:
        {
            if(Type == TYPE_UTILSETPOINT_CH)
            {
                now = CtiTime(_utilSetpoint._ch_utime);
                astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" Utility Setpoints Confirmed"));
            }
            else
            {
                now = CtiTime(_utilSetpoint._utime);
                astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" Util Setpoint Received: "));
            }

            astr += (" Cool Setpoint: ");
            if(_utilSetpoint._utilCoolSetpoint >= 0x7f00)
            {
                astr += (string("Unknown"));
            }
            else
            {
                astr += (string(CtiNumStr(convertFromStatTemp(_utilSetpoint._utilCoolSetpoint)) + getUnitName()));
            }

            astr += (string(" Heat Setpoint: "));
            if(_utilSetpoint._utilHeatSetpoint >= 0x7f00)
            {
                astr += (string("Unknown\n"));
            }
            else
            {
                astr += (string(CtiNumStr(convertFromStatTemp(_utilSetpoint._utilHeatSetpoint)) + getUnitName() + "\n"));
            }

            // updatePrintList(Type, astr);

            astr += (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" Util Setpoint Received: "));
            astr += (string(" Util Mode: "));
            switch(_utilSetpoint._utilMode)
            {
            case 0:
                astr += (string(" Inactive"));
                break;

            case 1:
                astr += (string(" Price tier "));
                break;

            case 2:
                astr += (string(" Temperature offset "));
                break;

            case 3:
                astr += (string(" Precondition "));
                break;

            case 255:
                astr += (string(" Unknown "));
                break;

            default:
                astr += (string(" Invalid "));
                break;
            }

            astr += (" for ");

            if(_utilSetpoint._utilDuration == 0)
            {
                astr += (string("Inactive"));
            }
            else if(_utilSetpoint._utilDuration <= 1440)
            {
                astr += CtiNumStr(_utilSetpoint._utilDuration);
            }
            else
            {
                astr += (string("Invalid"));
            }

            astr += " Minutes.\n";

            // updatePrintList(Type, astr);
            astr += (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" Util Setpoint Received: "));
            if(_utilSetpoint._utilMode == 1)
            {
                astr += string("Tier: ");
                switch(_utilSetpoint._utilPriceTier)
                {
                case 0:
                    astr += (string("None"));
                    break;

                case 1:
                    astr += (string("Low"));
                    break;

                case 2:
                    astr += (string("Medium"));
                    break;

                case 3:
                    astr += (string("High"));
                    break;

                case 4:
                    astr += (string("Critical"));
                    break;

                case 255:
                    astr += (string("Unknown"));
                    break;

                default:
                    astr += (string("Invalid"));
                    break;
                }
            }

            astr += string(" AIR: ");
            switch(_utilSetpoint._utilAIRDisable)
            {
            case 0:
                astr += (string("Enabled"));
                break;

            case 1:
                astr += (string("Disabled"));
                break;

            case 255:
                astr += (string("Unknown"));
                break;

            default:
                astr += (string("Invalid"));
                break;
            }

            astr += string(".  ");
            switch(_utilSetpoint._utilUserOverride)
            {
            case 0:
                astr += (string("No Override"));
                break;

            case 1:
                astr += (string("Override Active"));
                break;

            case 255:
                astr += (string("Override Unknown"));
                break;

            default:
                astr += (string("Override Invalid"));
                break;
            }

            switch(_utilSetpoint._utilUserOverrideDisable)
            {
            case 0:
                astr += (string(" / Prohibited."));
                break;

            case 1:
                astr += (string(" / Available."));
                break;

            case 255:
                astr += (string(" / Unknown."));
                break;

            default:
                astr += (string(" / Invalid."));
                break;
            }

            updatePrintList(Type, astr);
            postAnalogOutputPoint(Type, PO_UtilCoolSetpoint, convertFromStatTemp(_utilSetpoint._utilCoolSetpoint));
            postAnalogOutputPoint(Type, PO_UtilHeatSetpoint, convertFromStatTemp(_utilSetpoint._utilHeatSetpoint));

            break;
        }
    case TYPE_CLOCK:
        {
            now = CtiTime(_clock._utime);
            astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" Clock Received: "));

            // Return value:
            //  0 = Monday
            //  1 = Tuesday
            //  2 = Wednesday
            //  3 = Thursday
            //  4 = Friday
            //  5 = Saturday
            //  6 = Sunday
            //  255 = unknown
            //  At initialization, until the first EVNT_CLOCK event, the value will be 255.

            switch(_clock._day)
            {
            case 6:
                astr += (string("Sunday"));
                break;

            case 0:
                astr += (string("Monday"));
                break;

            case 1:
                astr += (string("Tuesday"));
                break;

            case 2:
                astr += (string("Wednesday"));
                break;

            case 3:
                astr += (string("Thursday"));
                break;

            case 4:
                astr += (string("Friday"));
                break;

            case 5:
                astr += (string("Saturday"));
                break;

            case 255:
                astr += (string("Unknown"));
                break;

            default:
                astr += (string("Invalid"));
                break;
            }

            astr += (string(", "));
            if(_clock._hour == 255)
            {
                astr += (string("UU:"));
            }
            else if(_clock._hour < 24)
            {
                astr += (string(CtiNumStr(_clock._hour).zpad(2) + ":"));
            }
            else
            {
                astr += (string("II:"));
            }

            if(_clock._minute == 255)
            {
                astr += (string("II:"));
            }
            else if(_clock._minute < 60)
            {
                astr += (string(CtiNumStr(_clock._minute).zpad(2) + ":"));
            }
            else
            {
                astr += (string("II:"));
            }

            if(_clock._second == 255)
            {
                astr += (string("UU"));
            }
            else if(_clock._second < 60)
            {
                astr += (string(CtiNumStr(_clock._second).zpad(2)));
            }
            else
            {
                astr += (string("II"));
            }

            switch(_clockDST._dst)
            {
            case 0:
                astr += (string(" Standard Time"));
                break;

            case 1:
                astr += (string(" DST"));
                break;

            case 255:
                //astr += (string(" Unknown"));
                break;

            default:
                //astr += (string("Invalid"));
                break;
            }

            updatePrintList(Type, astr);
            break;
        }
    case TYPE_CLOCKDST:
        {
            #if 0
            now = CtiTime(_clockDST._utime);
            astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" Clock DST Setting Received: "));

            // Return value:
            //  0 = Not DST
            //  1 = DST Active
            //  At initialization, until the first EVNT_CLOCK event, the value will be 255.

            switch(_clockDST._dst)
            {
            case 0:
                astr += (string("Standard Time"));
                break;

            case 1:
                astr += (string("DST"));
                break;

            case 255:
                astr += (string("Unknown"));
                break;

            default:
                astr += (string("Invalid"));
                break;
            }

            updatePrintList(Type, astr);

            #endif

            break;
        }
    case TYPE_DEVICEBOUND:
        {
            now = CtiTime(_deviceBound._utime);
            astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" Device Bound Received: "));
            updatePrintList(Type, astr);

            break;
        }
    case TYPE_DEVICEUNBOUND:
        {
            now = CtiTime(_deviceUnbound._utime);
            astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" Device Unbound Received: "));
            updatePrintList(Type, astr);
            break;
        }
    case TYPE_RETURNCODE:
        {
            now = CtiTime(_returnCode._utime);
            astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" ReturnCode Received: "));
            astr += "Responded with condition: " + CtiNumStr(_returnCode._returnCode) + string(" from a command of type: ") + CtiNumStr(_returnCode._setType);
            updatePrintList(Type, astr);
            break;
        }
    default:
        break;
    }

    return status;
}

bool CtiDeviceGatewayStat::generatePacketDataSchedule()
{
    bool status = false;

    CtiTime now;
    string astr;

    {
        int day;
        int period;

        for(day = 0; day < 7; day++)
        {
            for(period = 0; period < EP_PERIODS_PER_DAY; period++)
            {
                if(_schedule[day][period]._utime == YUKONEOT)
                {
                    continue;
                }
                astr = generateSchedulePeriod(day, period);

                // Screwy packing here to create a unique schedule element.
                updatePrintList( (10000 + (day * 100) + period), astr);
            }
        }
    }

    return status;
}

string CtiDeviceGatewayStat::generateSchedulePeriod(int day, int period)
{
    bool status = false;

    string updated_str("Update Time Unknown");
    string confirmed_str;
    string astr;

    {
        if( (day >= 0 && day < 7) && (period >= 0 && period < EP_PERIODS_PER_DAY) )   // valid day & period.
        {
            if(_schedule[day][period]._utime != YUKONEOT)
            {
                updated_str = CtiTime(_schedule[day][period]._utime).asString();
            }

            if(_schedule[day][period]._ch_utime == 86400)
            {
                confirmed_str = string("Confirm Pending");
            }
            else if(_schedule[day][period]._ch_utime != YUKONEOT)
            {
                confirmed_str = string("CNFM: ") + CtiTime(_schedule[day][period]._ch_utime).asString();
            }


            astr = (updated_str + string(" Stat ")  + CtiNumStr(getDeviceSerialNumber()).spad(3) + " " + confirmed_str + string(" Schedule:"));

            switch(day)
            {
            case 0:
                astr += (string(" Sun"));
                break;

            case 1:
                astr += (string(" Mon"));
                break;

            case 2:
                astr += (string(" Tue"));
                break;

            case 3:
                astr += (string(" Wed"));
                break;

            case 4:
                astr += (string(" Thu"));
                break;

            case 5:
                astr += (string(" Fri"));
                break;

            case 6:
                astr += (string(" Sat"));
                break;

            case 255:
                astr += (string(" Unk"));
                break;

            default:
                astr += (string(" Inv"));
                break;
            }

            switch(period)
            {
            case 0:
                astr += (string(" Wake   "));
                break;

            case 1:
                astr += (string(" Leave  "));
                break;

            case 2:
                astr += (string(" Return "));
                break;

            case 3:
                astr += (string(" Sleep  "));
                break;

            case 255:
                astr += (string(" Unknown"));
                break;

            default:
                astr += (string(" Invalid"));
                break;
            }

            if(_schedule[day][period]._hour == 254)
            {
                astr += (string(" Unscheduled"));             // Unscheduled.
                return astr;
            }
            else if(_schedule[day][period]._hour == 255)
            {
                astr += (string(" UU:"));
            }
            else if(_schedule[day][period]._hour < 24)
            {
                astr += (string(CtiNumStr(_schedule[day][period]._hour).zpad(2) + ":"));
            }
            else
            {
                astr += (string(" II:"));
            }

            switch(_schedule[day][period]._minute)
            {
            case 0:
            case 15:
            case 30:
            case 45:
                astr += (string(CtiNumStr(_schedule[day][period]._minute).zpad(2)));
                break;

            default:
                astr += (string("II"));
                break;
            }

            astr += (string(" Fan: "));
            switch(_schedule[day][period]._fan)
            {
            case 0:
                astr += (string("NSch"));
                break;

            case 1:
                astr += (string("Auto"));
                break;

            case 2:
                astr += (string("Circ"));
                break;

            case 3:
                astr += (string("On  "));
                break;

            case 255:
                astr += (string("Unk "));
                break;

            default:
                astr += (string("Inv "));
                break;
            }

            astr += (string(" Cool: "));
            if(_schedule[day][period]._coolSetpoint >= 0x7f00)
            {
                astr += (string("Unknown"));
            }
            else
            {
                astr += (string(CtiNumStr(convertFromStatTemp(_schedule[day][period]._coolSetpoint)).spad(3) + getUnitName()));
            }

            astr += (string(" \\ Heat: "));
            if(_schedule[day][period]._heatSetpoint >= 0x7f00)
            {
                astr += (string("Unknown"));
            }
            else
            {
                astr += (string(CtiNumStr(convertFromStatTemp(_schedule[day][period]._heatSetpoint)).spad(3) + getUnitName()));
            }
        }
    }

    return astr;
}

void CtiDeviceGatewayStat::sendGet(SOCKET msgsock, USHORT Type)
{
    GET Get;

    Get.Type = htons (Type);
    Get.DeviceID = htonl(_deviceSN);

    send (msgsock, (char *)&Get, sizeof(GET), 0);
}

bool CtiDeviceGatewayStat::sendSetDLC(SOCKET msgsock, UCHAR OffCycleDuration, UCHAR CyclePeriod, USHORT DLCDuration, UCHAR OverrideDisable)
{
    bool controlmatch = false;
    SETDLC SetDLC;

    if(OffCycleDuration == _DLC._cycleDuration &&
       CyclePeriod == _DLC._cyclePeriod &&
       DLCDuration == _DLC._duration &&
       OverrideDisable == _DLC._overrideDisable )
    {
        controlmatch = true;
    }

    {
        SetDLC.Type = htons (TYPE_SETDLC);
        SetDLC.DeviceID = htonl(_deviceSN);
        SetDLC.OffCycleDuration = OffCycleDuration;
        SetDLC.CyclePeriod = CyclePeriod;
        SetDLC.DLCDuration = htons (DLCDuration);
        SetDLC.OverrideDisable = OverrideDisable;

        send (msgsock, (char *)&SetDLC, sizeof (SetDLC), 0);
    }

    return controlmatch;
}

void CtiDeviceGatewayStat::sendSetDLCOverride(SOCKET msgsock, UCHAR DLCOverride)
{
    SETDLCOVERRIDE SetDLCOverride;

    SetDLCOverride.Type = htons (TYPE_SETDLCOVERRIDE);
    SetDLCOverride.DeviceID = htonl(_deviceSN);
    SetDLCOverride.DLCOverride = DLCOverride;

    send (msgsock, (char *)&SetDLCOverride, sizeof (SETDLCOVERRIDE), 0);
}

void CtiDeviceGatewayStat::sendSetFanSwitch(SOCKET msgsock, UCHAR FanSwitch)
{
    SETFANSWITCH SetFanSwitch;

    SetFanSwitch.Type = htons (TYPE_SETFANSWITCH);
    SetFanSwitch.DeviceID = htonl(_deviceSN);
    SetFanSwitch.FanSwitch = FanSwitch;

    send (msgsock, (char *)&SetFanSwitch, sizeof (SETFANSWITCH), 0);
}


void CtiDeviceGatewayStat::sendSetFilterRestart(SOCKET msgsock, UCHAR Restart)
{
    SETFILTERRESTART SetFilterRestart;

    SetFilterRestart.Type = htons (TYPE_SETFILTERRESTART);
    SetFilterRestart.DeviceID = htonl(_deviceSN);
    SetFilterRestart.Restart = Restart;

    send (msgsock, (char *)&SetFilterRestart, sizeof (SETFILTERRESTART), 0);
}

void CtiDeviceGatewayStat::sendSetSchedule(SOCKET msgsock, UCHAR Day, UCHAR Period, SHORT HeatSetpoint, SHORT CoolSetpoint, UCHAR Hour, UCHAR Minute, UCHAR Fan)
{
    SETSCHEDULE SetSchedule;

    SetSchedule.Type = htons (TYPE_SETSCHEDULE);
    SetSchedule.DeviceID = htonl(_deviceSN);
    SetSchedule.Day = Day;
    SetSchedule.Period = Period;
    SetSchedule.HeatSetpoint = htons (HeatSetpoint);
    SetSchedule.CoolSetpoint = htons (CoolSetpoint);
    SetSchedule.Hour = Hour;
    SetSchedule.Minute = Minute;
    SetSchedule.Fan = Fan;

    send (msgsock, (char *)&SetSchedule, sizeof (SETSCHEDULE), 0);
}

void CtiDeviceGatewayStat::sendSetpointLimits(SOCKET msgsock, SHORT UpperHeatLimit, SHORT LowerCoolLimit)
{
    SETSETPOINTLIMITS SetSetpointLimits;

    SetSetpointLimits.Type = htons (TYPE_SETSETPOINTLIMITS);
    SetSetpointLimits.DeviceID = htonl(_deviceSN);
    SetSetpointLimits.UpperHeatLimit = htons (UpperHeatLimit);
    SetSetpointLimits.LowerCoolLimit = htons (LowerCoolLimit);

    send (msgsock, (char *)&SetSetpointLimits, sizeof (SETSETPOINTLIMITS), 0);
}

void CtiDeviceGatewayStat::sendSetSetpoints(SOCKET msgsock, USHORT HeatSetpoint, USHORT CoolSetpoint, UCHAR SetpointPriority, UCHAR SetpointStatus, UCHAR VacationHoldDays, UCHAR VacationPeriod)
{
    SETSETPOINTS SetSetpoints;

    SetSetpoints.Type = htons (TYPE_SETSETPOINTS);
    SetSetpoints.DeviceID = htonl(_deviceSN);
    SetSetpoints.HeatSetpoint = htons (HeatSetpoint);
    SetSetpoints.CoolSetpoint = htons (CoolSetpoint);
    SetSetpoints.SetpointPriority = SetpointPriority;
    SetSetpoints.SetpointStatus = SetpointStatus;
    SetSetpoints.VacationHoldDays = VacationHoldDays;
    SetSetpoints.VacationPeriod = VacationPeriod;

    send (msgsock, (char *)&SetSetpoints, sizeof (SETSETPOINTS), 0);
}




void CtiDeviceGatewayStat::sendSetSystemSwitch(SOCKET msgsock, UCHAR SystemSwitch)
{
    SETSYSTEMSWITCH SetSystemSwitch;

    SetSystemSwitch.Type = htons (TYPE_SETSYSTEMSWITCH);
    SetSystemSwitch.DeviceID = htonl(_deviceSN);
    SetSystemSwitch.SystemSwitch = SystemSwitch;

    send (msgsock, (char *)&SetSystemSwitch, sizeof (SETSYSTEMSWITCH), 0);
}

void CtiDeviceGatewayStat::sendSetUtilSetpoints(SOCKET msgsock, SHORT UtilHeatSetpoint, SHORT UtilCoolSetpoint, USHORT UtilDuration, UCHAR PriceTier, UCHAR Mode, UCHAR UserOverrideDisable, UCHAR AdaptiveRecoveryDisable)
{
    SETUTILSETPOINTS SetUtilSetpoints;

    SetUtilSetpoints.Type = htons (TYPE_SETUTILSETPOINTS);
    SetUtilSetpoints.DeviceID = htonl(_deviceSN);
    SetUtilSetpoints.UtilHeatSetpoint = htons (UtilHeatSetpoint);
    SetUtilSetpoints.UtilCoolSetpoint = htons (UtilCoolSetpoint);
    SetUtilSetpoints.UtilDuration = htons (UtilDuration);
    SetUtilSetpoints.PriceTier = PriceTier;
    SetUtilSetpoints.Mode = Mode;
    SetUtilSetpoints.UserOverrideDisable = UserOverrideDisable;
    SetUtilSetpoints.AdaptiveRecoveryDisable = AdaptiveRecoveryDisable;

    send (msgsock, (char *)&SetUtilSetpoints, sizeof (SETUTILSETPOINTS), 0);
}

void CtiDeviceGatewayStat::sendSetUtilOverride(SOCKET msgsock, UCHAR Override)
{
    SETUTILOVERRIDE SetUtilOverride;

    SetUtilOverride.Type = htons (TYPE_SETUTILOVERRIDE);
    SetUtilOverride.DeviceID = htonl(_deviceSN);
    SetUtilOverride.Override = Override;

    send (msgsock, (char *)&SetUtilOverride, sizeof (SETUTILOVERRIDE), 0);
}

void CtiDeviceGatewayStat::sendUnbindDevice(SOCKET msgsock)
{
    UNBINDDEVICE UnbindDevice;

    UnbindDevice.Type = ntohs (TYPE_UNBINDDEVICE);
    UnbindDevice.DeviceID = htonl(_deviceSN);

    send (msgsock, (char *)&UnbindDevice, sizeof (UNBINDDEVICE), 0);
}

void CtiDeviceGatewayStat::sendRestartFilter(SOCKET msgsock)
{
    RESTARTFILTER RestartFilter;

    RestartFilter.Type = htons (TYPE_RESTARTFILTER);
    RestartFilter.DeviceID = htonl(_deviceSN);

    send (msgsock, (char *)&RestartFilter, sizeof (RESTARTFILTER), 0);
}

void CtiDeviceGatewayStat::sendQueryRuntime(SOCKET msgsock, UCHAR Reset)
{
    QUERYRUNTIME QueryRuntime;

    QueryRuntime.Type = htons (TYPE_QUERYRUNTIME);
    QueryRuntime.DeviceID = htonl(_deviceSN);
    QueryRuntime.Reset = Reset;

    send (msgsock, (char *)&QueryRuntime, sizeof (QUERYRUNTIME), 0);
}

int CtiDeviceGatewayStat::checkPendingOperations(  )
{
    int processed = 0;
    CtiTime theend(YUKONEOT);

    try
    {
        CtiLockGuard< CtiMutex > gd(_collMux, 5000);

        if(gd.isAcquired() && !_operations.empty())
        {
            CtiDeviceGatewayStat::OpCol_t::iterator oper_itr;
            for( oper_itr = _operations.begin(); oper_itr != _operations.end(); oper_itr++ )
            {
                OpCol_t::value_type &valtype = *oper_itr;

                USHORT message_type = valtype.getOperation();

                switch(message_type)
                {
                case TYPE_SETDLC:
                    {
                        string astr;
                        CtiTime arrival(_DLC._utime);
                        CtiTime confirm(_DLC._ch_utime);

                        if(!valtype.isResponded() && theend != arrival && arrival >= valtype.getTimeSubmitted() )
                        {
                            astr = CtiTime().asString() + " " + CtiNumStr(getDeviceSerialNumber()) + " **** DLC SUBMITTED **** ";
                            valtype.addReplyVector( astr );
                            generateReplyVector(valtype);
                            valtype.setTimeResponded( arrival );
                        }

                        if( !valtype.isConfirmed() && theend != confirm && confirm >= valtype.getTimeSubmitted() )
                        {
                            astr = CtiTime().asString() + " " + CtiNumStr(getDeviceSerialNumber()) + " **** DLC CONFIRMED **** ";
                            valtype.addReplyVector( astr );
                            generateReplyVector(valtype);
                            valtype.setTimeConfirmed( confirm );
                        }

                        int rcCount = returnCodeCount( message_type );
                        if( rcCount )
                        {
                            removeReturnCode(message_type);

                            astr = CtiTime().asString() + " " + CtiNumStr(getDeviceSerialNumber()) + " **** DLC Repeat Match. **** ";
                            valtype.addReplyVector( astr );
                            valtype.setTimeResponded( CtiTime() );
                            valtype.setTimeConfirmed( CtiTime() );
                        }


                        break;
                    }
                case TYPE_GETALL:
                    {
                        CtiTime arrival(_clock._utime);      // Clock is toward the end of reportables from the gateway.

                        if(!valtype.isResponded() && theend != arrival && arrival >= valtype.getTimeSubmitted() )
                        {
                            valtype.setTimeResponded( arrival );
                            valtype.setTimeConfirmed( arrival );

                            clearPrintList();
                            generatePrintList();
                            // printPacketData();

                            // Add the reply data into the pending op.
                            if(1)
                            {
                                CtiLockGuard< CtiMutex > gd(_collMux);
                                StatPrintList_t::iterator itr;

                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                for(itr = _printlist.begin() ; itr != _printlist.end(); itr++)
                                {
                                    valtype.addReplyVector( ((*itr).second) );
                                }
                            }
                        }

                        break;
                    }
                case TYPE_SETSCHEDULE:
                    {
                        CtiTime arrival;

                        int waiting_on = 0;         // Number of data elements we are waiting on.
                        int dow;
                        int pod;

                        for(dow = 0; dow < 7; dow ++)
                        {
                            for(pod = 0; pod < EP_PERIODS_PER_DAY; pod ++)
                            {
                                if( _schedule[dow][pod]._ch_utime == 86400 )    // Has not been reported back to us yet!
                                {
                                    waiting_on++;
                                }
                            }
                        }

                        int rcCount = returnCodeCount( message_type );

                        if( waiting_on == 0 || waiting_on <= rcCount )
                        {
                            removeReturnCode(message_type);

                            for(dow = 0; dow < 7; dow ++)
                            {
                                for(pod = 0; pod < EP_PERIODS_PER_DAY; pod ++)
                                {
                                    // Undo them for the next  schedule command!
                                    if( _schedule[dow][pod]._utime == 86400 ) _schedule[dow][pod]._utime = arrival.seconds();
                                    if( _schedule[dow][pod]._ch_utime == 86400 ) _schedule[dow][pod]._ch_utime = arrival.seconds();
                                }
                            }

                            valtype.setTimeResponded( arrival );
                            valtype.setTimeConfirmed( arrival );

                            generatePacketDataSchedule();

                            // Add the reply data into the pending op.

                            if(1)
                            {
                                CtiLockGuard< CtiMutex > gd(_collMux);
                                StatPrintList_t::iterator itr;

                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                for(itr = _printlist.begin() ; itr != _printlist.end(); itr++)
                                {
                                    valtype.addReplyVector( ((*itr).second) );
                                }
                            }
                        }
                        else
                        {
                            valtype.setTimeExpires( CtiTime() + 300 );   // Five minute expiration time, from now.

                            if(0)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** Pending SCHEDULE incomplete. **** Stat " << getDeviceSerialNumber() << ".  Waiting on " << waiting_on << " and  " << rcCount << " command matches." << endl;
                            }
                        }

                        break;
                    }
                case TYPE_SETSETPOINTS:
                    {
                        CtiTime arrival(_setpoints._utime);
                        CtiTime confirm(_setpoints._ch_utime);

                        if(!valtype.isResponded() && theend != arrival && arrival >= valtype.getTimeSubmitted() )
                        {
                            valtype.setTimeResponded( arrival );
                        }

                        if( !valtype.isConfirmed() && theend != confirm && confirm >= valtype.getTimeSubmitted() )
                        {
                            valtype.setTimeConfirmed( confirm );

                            generateReplyVector(valtype);
                        }

                        break;
                    }
                case TYPE_SETDLCOVERRIDE:
                case TYPE_SETFANSWITCH:
                case TYPE_SETFILTERRESTART:
                case TYPE_SETSETPOINTLIMITS:
                case TYPE_SETSYSTEMSWITCH:
                case TYPE_SETUTILSETPOINTS:
                case TYPE_SETUTILOVERRIDE:
                    {
                        {
                            string astr("Operation Complete");
                            CtiTime arrival(_allowedSystemSwitch._utime);

                            if(!valtype.isResponded() && theend != arrival && arrival >= valtype.getTimeSubmitted() )
                            {
                                valtype.setTimeResponded( arrival );
                                valtype.setTimeConfirmed( arrival );

                                valtype.addReplyVector(astr);
                            }
                            break;
                        }

                        break;
                    }
                case TYPE_GETALLOWEDSYSTEMSWITCH:
                    {
                        CtiTime arrival(_allowedSystemSwitch._utime);

                        if(!valtype.isResponded() && theend != arrival && arrival >= valtype.getTimeSubmitted() )
                        {
                            valtype.setTimeResponded( arrival );
                            valtype.setTimeConfirmed( arrival );

                            generateReplyVector(valtype);
                        }
                        break;
                    }
                case TYPE_GETBATTERY:
                    {
                        CtiTime arrival(_battery._utime);

                        if(!valtype.isResponded() && theend != arrival && arrival >= valtype.getTimeSubmitted() )
                        {
                            valtype.setTimeResponded( arrival );
                            valtype.setTimeConfirmed( arrival );

                            generateReplyVector(valtype);
                        }
                        break;
                    }
                case TYPE_GETRUNTIME:
                    {
                        CtiTime arrival(_runtime._utime);

                        if(!valtype.isResponded() && theend != arrival && arrival >= valtype.getTimeSubmitted() )
                        {
                            valtype.setTimeResponded( arrival );
                            valtype.setTimeConfirmed( arrival );

                            generateReplyVector(valtype);
                        }
                        break;
                    }
                case TYPE_GETSETPOINTS:
                    {
                        CtiTime arrival(_setpoints._utime);

                        if(!valtype.isResponded() && theend != arrival && arrival >= valtype.getTimeSubmitted() )
                        {
                            valtype.setTimeResponded( arrival );
                            valtype.setTimeConfirmed( arrival );

                            generateReplyVector(valtype);
                        }
                        break;
                    }
                case TYPE_GETDEADBAND:
                    {
                        CtiTime arrival(_deadband._utime);

                        if(!valtype.isResponded() && theend != arrival && arrival >= valtype.getTimeSubmitted() )
                        {
                            valtype.setTimeResponded( arrival );
                            valtype.setTimeConfirmed( arrival );

                            generateReplyVector(valtype);
                        }
                        break;
                    }
                case TYPE_GETDEVICEABSENT:
                    {
                        CtiTime arrival(_deviceAbsent._utime);

                        if(!valtype.isResponded() && theend != arrival && arrival >= valtype.getTimeSubmitted() )
                        {
                            valtype.setTimeResponded( arrival );
                            valtype.setTimeConfirmed( arrival );

                            generateReplyVector(valtype);
                        }
                        break;
                    }
                case TYPE_GETDEVICETYPE:
                    {
                        CtiTime arrival(_deviceType._utime);

                        if(!valtype.isResponded() && theend != arrival && arrival >= valtype.getTimeSubmitted() )
                        {
                            valtype.setTimeResponded( arrival );
                            valtype.setTimeConfirmed( arrival );

                            generateReplyVector(valtype);
                        }
                        break;
                    }
                case TYPE_GETDISPLAYEDTEMPERATURE:
                    {
                        CtiTime arrival(_displayedTemp._utime);

                        if(!valtype.isResponded() && theend != arrival && arrival >= valtype.getTimeSubmitted() )
                        {
                            valtype.setTimeResponded( arrival );
                            valtype.setTimeConfirmed( arrival );

                            generateReplyVector(valtype);
                        }
                        break;
                    }
                case TYPE_GETDLC:
                    {
                        CtiTime arrival(_DLC._utime);

                        if(!valtype.isResponded() && theend != arrival && arrival >= valtype.getTimeSubmitted() )
                        {
                            valtype.setTimeResponded( arrival );
                            valtype.setTimeConfirmed( arrival );

                            generateReplyVector(valtype);
                        }
                        break;
                    }
                case TYPE_GETFANSWITCH:
                    {
                        CtiTime arrival(_fanSwitch._utime);

                        if(!valtype.isResponded() && theend != arrival && arrival >= valtype.getTimeSubmitted() )
                        {
                            valtype.setTimeResponded( arrival );
                            valtype.setTimeConfirmed( arrival );

                            generateReplyVector(valtype);
                        }
                        break;
                    }
                case TYPE_GETFILTER:
                    {
                        CtiTime arrival(_filter._utime);

                        if(!valtype.isResponded() && theend != arrival && arrival >= valtype.getTimeSubmitted() )
                        {
                            valtype.setTimeResponded( arrival );
                            valtype.setTimeConfirmed( arrival );

                            generateReplyVector(valtype);
                        }
                        break;
                    }
                case TYPE_GETHEATPUMPFAULT:
                    {
                        CtiTime arrival(_heatPumpFault._utime);

                        if(!valtype.isResponded() && theend != arrival && arrival >= valtype.getTimeSubmitted() )
                        {
                            valtype.setTimeResponded( arrival );
                            valtype.setTimeConfirmed( arrival );

                            generateReplyVector(valtype);
                        }
                        break;
                    }
                case TYPE_GETSETPOINTLIMITS:
                    {
                        CtiTime arrival(_setpointLimits._utime);

                        if(!valtype.isResponded() && theend != arrival && arrival >= valtype.getTimeSubmitted() )
                        {
                            valtype.setTimeResponded( arrival );
                            valtype.setTimeConfirmed( arrival );

                            generateReplyVector(valtype);
                        }
                        break;
                    }
                case TYPE_GETOUTDOORTEMP:
                    {
                        CtiTime arrival(_outdoorTemp._utime);

                        if(!valtype.isResponded() && theend != arrival && arrival >= valtype.getTimeSubmitted() )
                        {
                            valtype.setTimeResponded( arrival );
                            valtype.setTimeConfirmed( arrival );

                            generateReplyVector(valtype);
                        }
                        break;
                    }
                case TYPE_GETSYSTEMSWITCH:
                    {
                        CtiTime arrival(_systemSwitch._utime);

                        if(!valtype.isResponded() && theend != arrival && arrival >= valtype.getTimeSubmitted() )
                        {
                            valtype.setTimeResponded( arrival );
                            valtype.setTimeConfirmed( arrival );

                            generateReplyVector(valtype);
                        }
                        break;
                    }
                case TYPE_GETUTILSETPOINT:
                    {
                        CtiTime arrival(_utilSetpoint._utime);

                        if(!valtype.isResponded() && theend != arrival && arrival >= valtype.getTimeSubmitted() )
                        {
                            valtype.setTimeResponded( arrival );
                            valtype.setTimeConfirmed( arrival );

                            generateReplyVector(valtype);
                        }
                        break;
                    }
                case TYPE_GETCLOCK:
                    {
                        CtiTime arrival(_clock._utime);

                        if(!valtype.isResponded() && theend != arrival && arrival >= valtype.getTimeSubmitted() )
                        {
                            valtype.setTimeResponded( arrival );
                            valtype.setTimeConfirmed( arrival );

                            generateReplyVector(valtype);
                        }
                        break;
                    }
                case TYPE_ADDRESSING:
                case TYPE_GETADDRESSING:
                    {
                        CtiTime arrival;

                        valtype.setTimeResponded( arrival );
                        valtype.setTimeConfirmed( arrival );
                        generateReplyVector(valtype);

                        break;
                    }
                case TYPE_GETDEVICEBOUND:
                    {
                        string astr;
                        CtiTime arrival;

                        astr = "Stat: " + CtiNumStr(getDeviceSerialNumber()) + string(" unknown message type in a pending operation: ") + CtiNumStr(message_type);

                        valtype.setTimeResponded( arrival );
                        valtype.setTimeConfirmed( arrival );
                        generateReplyVector(valtype);

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << astr << endl;
                        }

                        break;
                    }
                case TYPE_RESTARTFILTER:
                    {
                        CtiTime arrival(_filter._utime);
                        CtiTime confirm(_filter._ch_utime);

                        if(!valtype.isResponded() && theend != arrival && arrival >= valtype.getTimeSubmitted() )
                        {
                            valtype.setTimeResponded( arrival );
                            generateReplyVector(valtype, TYPE_FILTER);
                        }

                        if( !valtype.isConfirmed() && theend != confirm && confirm >= valtype.getTimeSubmitted() )
                        {
                            valtype.setTimeConfirmed( confirm );
                            generateReplyVector(valtype, TYPE_FILTER_CH);
                        }

                        break;
                    }
                case TYPE_QUERYRUNTIME:
                    {
                        CtiTime arrival(_runtime._utime);
                        CtiTime confirm(_runtime._utime);

                        if(!valtype.isResponded() && theend != arrival && arrival >= valtype.getTimeSubmitted() )
                        {
                            valtype.setTimeResponded( arrival );
                            generateReplyVector(valtype, TYPE_RUNTIME);
                        }

                        if( !valtype.isConfirmed()                      &&
                            valtype.isResponded()                       &&
                            confirm >= valtype.getTimeSubmitted()       &&
                            confirm > valtype.getTimeResponded())
                        {
                            valtype.setTimeConfirmed( confirm );
                            generateReplyVector(valtype, TYPE_RUNTIME);
                        }


                        break;
                    }
                case TYPE_GETSCHEDULE:
                default:
                    {
                        string astr;
                        CtiTime arrival;

                        astr = "Stat: " + CtiNumStr(getDeviceSerialNumber()) + string(" unknown message type in a pending operation: ") + CtiNumStr(message_type);

                        valtype.setTimeResponded( arrival );
                        valtype.setTimeConfirmed( arrival );
                        generateReplyVector(valtype);

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << astr << endl;
                        }

                        break;
                    }
                }
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return processed;
}

int CtiDeviceGatewayStat::processParse(SOCKET msgsock, CtiCommandParser &parse, CtiOutMessage *&OutMessage)
{
    bool controlmatch = false;
    int processed = 0;
    USHORT operation = 0xffff;

    static int cycleofftime;
    static int period;
    static int repeat;
    static int duration;

    BOOL overridedisable = parse.getiValue("overridedisable", FALSE);

    try
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Parse Processed by EnergyPro Stat ID: " << getDeviceSerialNumber() << ": " << parse.getCommandStr() << endl;
        }

        int shedminutes = parse.getiValue("shed", 0) / 60;
        int cycle = parse.getiValue("cycle", 0);
        int parseflags = parse.getiValue("flag", 0);

        if(parse.getCommand() == ScanRequest)
        {
            if( parse.getiValue("scantype") == ScanRateIntegrity )
            {
                sendGet( msgsock, TYPE_GETALL );
            }
            else
            {
                sendGet( msgsock, TYPE_GETCLOCK );
            }

            processed++;
            operation = TYPE_GETALL;
        }
        else if(parse.getCommand() == ControlRequest)
        {
            if( shedminutes > 0 )
            {
                setLastControlSent(OutMessage);

                USHORT duration = shedminutes;

                if(shedminutes > 255)
                {
                    if(shedminutes < 65535)
                    {
                        shedminutes = 1;
                        duration = 65535;
                    }
                }

                // We have a shed.  Create a DLC message here!
                controlmatch = sendSetDLC( msgsock, (BYTE)shedminutes, (BYTE)shedminutes, (USHORT)shedminutes, overridedisable );
                processed++;

                operation = TYPE_SETDLC;
            }
            else if( cycle > 0 )
            {
                setLastControlSent(OutMessage);

                period     = parse.getiValue("cycle_period", 30);
                repeat     = parse.getiValue("cycle_count", 8);
                duration   = period * repeat;

                // Add these two items to the list for control accounting!
                parse.setValue("control_reduction", parse.getiValue("cycle", 0) );
                parse.setValue("control_interval", 60 * period * repeat);

                cycleofftime = (period * cycle) / 100;

                controlmatch = sendSetDLC( msgsock, (BYTE)cycleofftime, (BYTE)period, (BYTE)duration, overridedisable );
                processed++;

                operation = TYPE_SETDLC;
            }
            else if( parse.getFlags() & CMD_FLAG_CTL_RESTORE )
            {
                CtiTime now;
                CtiTime pageconfirmed( _DLC._ch_utime );

                setLastControlSent(OutMessage);
                processed++;
                operation = TYPE_SETDLC;

                if(pageconfirmed < now && now < (pageconfirmed + (_DLC._duration * 60)) )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Stat: " << getDeviceSerialNumber() << " randomizing out of control." << endl;
                    }
                    // We are still under control as far as we know.  Try to randomize someday!
                    controlmatch = sendSetDLC( msgsock, (BYTE)0, (BYTE)0, (BYTE)0, FALSE );
                }
                else
                {
                    // Do not cause a new shed!  Make sure though.
                    controlmatch = sendSetDLC( msgsock, (BYTE)0, (BYTE)0, (BYTE)0, FALSE );
                }
            }
            else if( parse.getFlags() & CMD_FLAG_CTL_TERMINATE )
            {
                CtiTime now;
                CtiTime pageconfirmed( _DLC._ch_utime );

                if(pageconfirmed < now && now < (pageconfirmed + (_DLC._duration * 60)) )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Stat: " << getDeviceSerialNumber() << " randomizing out of control." << endl;
                    }
                    // We are still under control as far as we know.  Try to randomize someday!
                    controlmatch = sendSetDLC( msgsock, (BYTE)0, (BYTE)0, (BYTE)0, FALSE );
                }
                else
                {
                    // We are still under control as far as we know.  Try to randomize someday!
                    controlmatch = sendSetDLC( msgsock, (BYTE)0, (BYTE)0, (BYTE)0, FALSE );
                }
            }
        }
        else if(parse.getCommand() == PutConfigRequest)
        {
            parsePutConfigRequest(parse);

            if( parse.isKeyValid("xcschedule") )
            {
                operation = TYPE_SETSCHEDULE;
                processed = processSchedule(msgsock, parse, OutMessage);
            }
            else if( parse.isKeyValid("xcsetstate") )
            {
                int system      = parse.getiValue("xcsysstate",-1);
                int fan         = parse.getiValue("xcfanstate",0);
                int temp        = parse.getiValue("xcsettemp",0);
                int holdstatus  = (parse.getiValue("xcholdprog",0) != 0 ? EP_SETPOINT_STATUS_HOLD : EP_SETPOINT_STATUS_TEMPORARY);
                int runprog     = parse.getiValue("xcrunprog",0);

                int sppriority;

                switch(system)
                {
                case 0x04:      // This is OFF
                    {
                        system = EP_SETSYSTEM_OFF;
                        break;
                    }
                case 0x08:      // This is HEAT
                    {
                        system = EP_SETSYSTEM_HEAT;
                        break;
                    }
                case 0x0c:      // This is COOL
                    {
                        system = EP_SETSYSTEM_COOL;
                        break;
                    }
                case 0x10:      // This is EMHEAT
                    {
                        system = EP_SETSYSTEM_EMHEAT;
                        break;
                    }
                case 0x80:
                    {
                        system = EP_SETSYSTEM_AUTO;
                        break;
                    }
                default:
                    {
                        system = ( (_systemSwitch._systemSwitch < EP_SETSYSTEM_AUTO) ? _systemSwitch._systemSwitch : -1);
                        break;
                    }
                }

                if(system == EP_SETSYSTEM_COOL)
                {
                    sppriority = EP_SETPOINT_PRIORITY_COOL;
                }
                else if(system == EP_SETSYSTEM_COOL)
                {
                    sppriority = EP_SETPOINT_PRIORITY_HEAT;
                }
                else
                {
                    sppriority = estimateSetpointPriority();
                }

                switch(fan)
                {
                case 0x03:  // On.
                    {
                        processed = 1;
                        operation = TYPE_SETFANSWITCH;
                        sendSetFanSwitch( msgsock, 200 );
                        break;
                    }
                case 0x02:  // Auto
                case 0x01:  // Off which I guess is auto too.
                    {
                        processed = 1;
                        operation = TYPE_SETFANSWITCH;
                        sendSetFanSwitch( msgsock, 0 );
                        break;
                    }
                case 0x00:
                default:
                    {
                        // Don't send anything...
                        break;
                    }
                }

                if(runprog)
                {
                    processed = 1;
                    operation = TYPE_SETSETPOINTS;

                    sendSetSetpoints(msgsock,
                                     getCurrentHeatSchedule(),  // Heat setpoint.  Not used on a run program
                                     getCurrentCoolSchedule(),  // Cool setpoint.  Not used on a run program
                                     estimateSetpointPriority(),            // Setpoint priority (false is cool (not heat))
                                     EP_SETPOINT_STATUS_RUNPROGRAM,         // This is a run program operation
                                     0,                                     // Vacation hold days N/A
                                     0);                                    // Vacation hold period N/A
                }
                else
                {
                    if(system >= 0)
                    {
                        processed = 1;
                        operation = TYPE_SETSYSTEMSWITCH;
                        sendSetSystemSwitch( msgsock, system );
                    }

                    if(temp != 0)
                    {
                        processed = 1;
                        operation = TYPE_SETSETPOINTS;
                        sendSetSetpoints(msgsock,
                                         convertToStatTemp(temp),   // Heat setpoint.  Not used on a run program
                                         convertToStatTemp(temp),   // Cool setpoint.  Not used on a run program
                                         sppriority,                // Setpoint priority (false is cool (not heat))
                                         holdstatus,                // Temporary or permanent hold.
                                         0,                         // Vacation hold days N/A
                                         0);                        // Vacation hold period N/A
                    }
                }
            }
            else if(parse.getiValue("epresetfilter", FALSE) != FALSE)
            {
                processed = 1;
                operation = TYPE_RESTARTFILTER;
                sendRestartFilter(msgsock);
            }
            else if(parse.getiValue("epresetruntimes", FALSE) != FALSE)
            {
                processed = 1;
                operation = TYPE_QUERYRUNTIME;
                sendQueryRuntime(msgsock, TRUE);
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Putconfig unknown: " << parse.getCommandStr() << endl;
                }
            }
        }
        else if(parse.getCommand() == GetValueRequest)
        {
            parseGetValueRequest(parse);

            if( parse.isKeyValid("epget") )
            {
                operation = parse.getiValue("epget");
                BOOL resetval = parse.getiValue("epruntimereset", FALSE);
                switch(operation)
                {
                case TYPE_GETRUNTIME:
                    {
                        sendQueryRuntime( msgsock, (UCHAR)resetval );
                        break;
                    }
                default:
                    {
                        sendGet( msgsock, operation );
                        break;
                    }
                }
                processed++;
            }
        }

        /*
         *  Check and add to the pendingOperation
         */
        if(processed > 0)
        {
            try
            {
                {
                    CtiPendingStatOperation op( getDeviceSerialNumber(), operation );
                    op.setOutMessage( OutMessage );

                    addOperation(op);
                }
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** processParse Failed **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return processed;
}

bool CtiDeviceGatewayStat::setLastControlSent(CtiOutMessage *&OutMessage)
{
    bool replaced = false;

    if(_controlOutMessage)
    {
        delete _controlOutMessage;
        replaced = true;
    }

    _controlOutMessage = CTIDBG_new CtiOutMessage(*OutMessage);

    return replaced;
}

CtiOutMessage*& CtiDeviceGatewayStat::getLastControlSent()
{
    return _controlOutMessage;
}

int CtiDeviceGatewayStat::processSchedule(SOCKET msgsock, CtiCommandParser &parse, CtiOutMessage *&OutMessage)
{
    int processed = 0;

    int dow = 0;
    int pod = 0;

    string allwake  = string("xctodshh_") + CtiNumStr(( (9) << 4 | (pod & 0x0f) ));
    string endwake  = string("xctodshh_") + CtiNumStr(( (8) << 4 | (pod & 0x0f) ));
    string dayswake = string("xctodshh_") + CtiNumStr(( (7) << 4 | (pod & 0x0f) ));

    if(parse.isKeyValid(allwake))      // All Days!
    {
        for(dow = 0 ; dow < 7; dow ++)
        {
            for(pod = 0; pod < EP_PERIODS_PER_DAY; pod ++)
            {
                BYTE per = ( (9) << 4 | (pod & 0x0f) );
                processed = processSchedulePeriod(msgsock, parse, OutMessage, dow, pod, per);
            }
        }
    }
    else if(parse.isKeyValid(endwake))      // Weekend Days!
    {
        for(dow = 0 ; dow < 7; dow += 6)
        {
            for(pod = 0; pod < EP_PERIODS_PER_DAY; pod ++)
            {
                BYTE per = ( (8) << 4 | (pod & 0x0f) );
                processed = processSchedulePeriod(msgsock, parse, OutMessage, dow, pod, per);
            }
        }
    }
    else if(parse.isKeyValid(dayswake))      // Week Days!
    {
        for(dow = 1 ; dow < 6; dow++)
        {
            for(pod = 0; pod < EP_PERIODS_PER_DAY; pod ++)
            {
                BYTE per = ( (7) << 4 | (pod & 0x0f) );
                processed = processSchedulePeriod(msgsock, parse, OutMessage, dow, pod, per);
            }
        }
    }

    if(!processed)
    {
        for(dow = 0; dow < 7; dow ++)
        {
            for(pod = 0; pod < EP_PERIODS_PER_DAY; pod ++)
            {
                BYTE per = ( (dow) << 4 | (pod & 0x0f) );
                processed = processSchedulePeriod(msgsock, parse, OutMessage, dow, pod, per);
            }
        }
    }

    return processed;
}

int CtiDeviceGatewayStat::convertCDayToStatDay(int dow)
{
    int convert = 0;

    switch(dow)
    {
    case 0:
        {
            convert = 7;
            break;
        }
    case 1:
    case 2:
    case 3:
    case 4:
    case 5:
    case 6:
        {
            convert = dow;
            break;
        }
    case 7:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            convert = 0;
            break;
        }
    case 8:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            convert = 0;
            break;
        }
    case 9:
        {
            convert = 0;
            break;
        }
    }

    return convert;
}



int CtiDeviceGatewayStat::convertStatDayToCDay(UCHAR Day)
{
    int convert = 0;

    switch(Day)
    {
    case 0:
    case 1:
    case 2:
    case 3:
    case 4:
    case 5:
        {
            convert = (int)(Day + 1);
            break;
        }
    case 6:
        {
            convert = 0;
            break;
        }
    default:
        {
            convert = (int)(Day);
            break;
        }
    }

    return convert;
}

int CtiDeviceGatewayStat::returnCodeCount(USHORT message_type)
{
    int yep_count = 0;
    StatResponse_t::iterator itr;

    try
    {
        {
            CtiLockGuard< CtiMutex > gd(_collMux);

            for( itr = _responseVector.begin(); itr != _responseVector.end(); itr++)
            {
                StatResponse_t::value_type &val = *itr;

                if(val.first == message_type)
                {
                    yep_count++;
                }
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return yep_count;
}

int CtiDeviceGatewayStat::removeReturnCode(USHORT message_type)
{
    int yep_count = 0;
    StatResponse_t::iterator itr;

    try
    {
        {
            CtiLockGuard< CtiMutex > gd(_collMux);

            for( itr = _responseVector.begin(); itr != _responseVector.end(); )
            {
                StatResponse_t::value_type &val = *itr;

                if(val.first == message_type)
                {
                    yep_count++;
                    itr = _responseVector.erase(itr);
                    continue;
                }

                itr++;
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return yep_count;
}

// This private member deals with the particulars of a getvalue parse for the energypro!
int CtiDeviceGatewayStat::parseGetValueRequest(CtiCommandParser &parse)
{
    int status = NORMAL;

    string CmdStr = parse.getCommandStr();
    std::transform(CmdStr.begin(), CmdStr.end(), CmdStr.begin(), ::tolower);
    //CmdStr.toLower();

    if(CmdStr.find(" allowed system switch")!=string::npos)
    {
        parse.setValue("epget", TYPE_GETALLOWEDSYSTEMSWITCH);
    }
    else if(CmdStr.find(" battery")!=string::npos)
    {
        parse.setValue("epget", TYPE_GETBATTERY);
    }
    else if( CmdStr.find(" runtime")!=string::npos)
    {
        if( CmdStr.find(" reset")!=string::npos )
        {
            parse.setValue("epruntimereset", TRUE);
        }

        parse.setValue("epget", TYPE_GETRUNTIME);
    }
    else if(CmdStr.find(" setpoints")!=string::npos)
    {
        parse.setValue("epget", TYPE_GETSETPOINTS);
    }
    else if(CmdStr.find(" deadband")!=string::npos)
    {
        parse.setValue("epget", TYPE_GETDEADBAND);
    }
    else if(CmdStr.find(" device absent")!=string::npos)
    {
        parse.setValue("epget", TYPE_GETDEVICEABSENT);
    }
    else if(CmdStr.find(" device type")!=string::npos)
    {
        parse.setValue("epget", TYPE_GETDEVICETYPE);
    }
    else if(CmdStr.find(" displayed temp")!=string::npos)
    {
        parse.setValue("epget", TYPE_GETDISPLAYEDTEMPERATURE);
    }
    else if(CmdStr.find(" dlc")!=string::npos)
    {
        parse.setValue("epget", TYPE_GETDLC);
    }
    else if(CmdStr.find(" fanswitch")!=string::npos || CmdStr.find(" fan switch")!=string::npos)
    {
        parse.setValue("epget", TYPE_GETFANSWITCH);
    }
    else if(CmdStr.find(" filter")!=string::npos)
    {
        parse.setValue("epget", TYPE_GETFILTER);
    }
    else if(CmdStr.find(" heatpump fault")!=string::npos)
    {
        parse.setValue("epget", TYPE_GETHEATPUMPFAULT);
    }
    else if(CmdStr.find(" setpoint limits")!=string::npos)
    {
        parse.setValue("epget", TYPE_GETSETPOINTLIMITS);
    }
    else if(CmdStr.find(" outdoor temp")!=string::npos)
    {
        parse.setValue("epget", TYPE_GETOUTDOORTEMP);
    }
    else if(CmdStr.find(" schedule")!=string::npos)
    {
        parse.setValue("epget", TYPE_GETSCHEDULE);
    }
    else if(CmdStr.find(" system switch")!=string::npos)
    {
        parse.setValue("epget", TYPE_GETSYSTEMSWITCH);
    }
    else if(CmdStr.find(" utility setpoint")!=string::npos)
    {
        parse.setValue("epget", TYPE_GETUTILSETPOINT);
    }
    else if(CmdStr.find(" clock")!=string::npos)
    {
        parse.setValue("epget", TYPE_GETCLOCK);
    }
    else if(CmdStr.find(" device bound")!=string::npos)
    {
        parse.setValue("epget", TYPE_GETDEVICEBOUND);
    }
    else if(CmdStr.find(" all")!=string::npos)
    {
        parse.setValue("epget", TYPE_GETALL);
    }
    else if(CmdStr.find(" addressing")!=string::npos)
    {
        parse.setValue("epget", TYPE_GETADDRESSING);
    }

    return status;
}

// This private member deals with the particulars of a getvalue parse for the energypro!
int CtiDeviceGatewayStat::parsePutConfigRequest(CtiCommandParser &parse)
{
    int status = NORMAL;

    string CmdStr = parse.getCommandStr();
    std::transform(CmdStr.begin(), CmdStr.end(), CmdStr.begin(), ::tolower);

    return status;
}



void CtiDeviceGatewayStat::generateReplyVector(CtiDeviceGatewayStat::OpCol_t::value_type &valtype, UINT operation)
{
    UINT op = operation;

    if(op == 0)
    {
        if(valtype.getOperation() >= 4000)
        {
            op = valtype.getOperation() - 1000;
        }
        else if(valtype.getOperation() < 2000)
        {
            switch(valtype.getOperation())
            {
            default:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "Operation " << valtype.getOperation() << " too low" << endl;
                    }
                    break;
                }
            }
        }
    }

    if(op != 0)
    {
        generatePacketData(op);
    }


    // Add the reply data into the pending op.
    {
        CtiLockGuard< CtiMutex > gd(_collMux);
        StatPrintList_t::iterator itr;

        CtiLockGuard<CtiLogger> doubt_guard(dout);
        for(itr = _printlist.begin() ; itr != _printlist.end(); itr++)
        {
            valtype.addReplyVector( ((*itr).second) );
        }
    }
}

bool CtiDeviceGatewayStat::generateTidbitToDatabase( USHORT Type, int day, int period )
{
    string astr;
    bool error = true;
    CtiTime now;

    CtiTableGatewayEndDevice tstamp;
    CtiTableGatewayEndDevice ged;

    ged.setSerialNumber( getDeviceSerialNumber() );
    ged.setHardwareType( TYPE_ENERGYPRO );

    switch(Type)
    {
    case TYPE_ALLOWEDSYSTEMSWITCH:
        {
            now = CtiTime(_allowedSystemSwitch._utime);
            astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" Received Allowed System Switch: "));
            if(_allowedSystemSwitch._allowedSystemSwitch == 0)
            {
                astr += (string("Allowed System Switch Unknown"));
            }
            else
            {
                astr += string("  Allowed:  ");
                if(_allowedSystemSwitch._allowedSystemSwitch & 0x01)
                {
                    astr += string("ER Heat ");
                }
                if(_allowedSystemSwitch._allowedSystemSwitch & 0x02)
                {
                    astr += string("Heat ");
                }
                if(_allowedSystemSwitch._allowedSystemSwitch & 0x04)
                {
                    astr += string("Off ");
                }
                if(_allowedSystemSwitch._allowedSystemSwitch & 0x08)
                {
                    astr += string("Cool ");
                }
                if(_allowedSystemSwitch._allowedSystemSwitch & 0x10)
                {
                    astr += string("Auto ");
                }
            }

            break;
        }
    case TYPE_BATTERY:
        {
            switch(_battery._battery)
            {
            case 0:
                astr = string("BAD");
                break;

            case 1:
                astr = string("GOOD");
                break;

            case 2:
                astr = string("(UNKNOWN)");
                break;

            default:
                astr += string("(UNKNOWN)");
                break;
            }

            if(!astr.empty())
            {
                ged.setDataType(ID_BATTERY);
                ged.setDataValue(astr);
                error = false;
            }

            break;
        }
    case TYPE_RSSI:
        {
            break;
        }
    case TYPE_RUNTIME:
        {
            astr = string(CtiNumStr(_runtime._coolRuntime)+ ",");
            astr += CtiNumStr(_runtime._heatRuntime);

            if(!astr.empty())
            {
                ged.setDataType(ID_RUNTIMES);
                ged.setDataValue(astr);
                error = false;
            }

            break;
        }
    case TYPE_SETPOINTS_CH:
    case TYPE_SETPOINTS:
        {
            if(_setpoints._coolSetpoint >= 0x7f00 || _setpoints._heatSetpoint >= 0x7f00)
            {
                astr = string("0,");
                astr += string("0,");
            }
            else
            {
                astr = string(CtiNumStr(convertFromStatTemp(_setpoints._coolSetpoint)) + ",");
                astr += string(CtiNumStr(convertFromStatTemp(_setpoints._heatSetpoint)) + ",");
            }

            switch(_setpoints._setpointStatus)
            {
            case 0:
                astr += string("SCHEDULE");
                break;

            case 1:
                astr += string("TEMP");
                break;

            case 2:
                astr += string("HOLD");
                break;

            case 254:
                astr += string("VACATION");

                if(_setpoints._vacationHoldDays == 0xffff)
                {
                    astr += string(",0");
                }
                else
                {
                    astr += string("," + CtiNumStr(_setpoints._vacationHoldDays) );
                }

                astr += string(",");

                switch(_setpoints._vacationHoldPeriod)
                {
                case 0:
                    astr += string("WAKE");
                    break;

                case 1:
                    astr += string("LEAVE");
                    break;

                case 2:
                    astr += string("RETURN");
                    break;

                case 3:
                    astr += string("SLEEP");
                    break;

                case 255:
                    astr += string("UNKNOWN");
                    break;

                default:
                    astr += string("INVALID");
                    break;
                }
                break;

            case 255:
                astr += string("UNKNOWN");
                break;

            default:
                astr += string("INVALID");
            }

            if(!astr.empty())
            {
                ged.setDataType(ID_SETPOINTS);
                ged.setDataValue(astr);
                error = false;
            }

            break;
        }
    case TYPE_DEADBAND:
        {
            now = CtiTime(_deadband._utime);
            astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" Deadband Received: "));

            if(_deadband._deadband == 255)
            {
                astr += string("Unknown");
            }
            else
            {
                astr += string(CtiNumStr(_deadband._deadband) + getUnitName(true, true));
            }



            break;
        }
    case TYPE_DEVICEABSENT:
        {
            now = CtiTime(_deviceAbsent._utime);
            astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" Device Absent Received: " ));

            if(_deviceAbsent._deviceAbsent)
            {
                astr += string("TRUE");
            }
            else
            {
                astr += string("FALSE");
            }

            break;
        }
    case TYPE_DEVICETYPE:
        {
            now = CtiTime(_deviceType._utime);
            astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" Device Type Received: "));

            switch(_deviceType._deviceType)
            {
            case 0:
                astr += string("UNKNOWN");
                break;

            case 1:
                astr += string("T8665C");
                break;

            case 2:
                astr += string("T8665D");
                break;

            case 3:
                astr += string("T8665E");
                break;

            default:
                astr += string("Invalid = " + CtiNumStr((int)_deviceType._deviceType));
                break;
            }

            break;
        }
    case TYPE_DISPLAYEDTEMPERATURE:
        {
            float dt = (float)_displayedTemp._displayedTemperature;
            if(_displayedTemp._displayedTempUnits == 1)     // Celsius is given in 1/2 degrees.
            {
                dt = dt / 2 ;
            }

            astr = string(CtiNumStr(dt, GATEWAY_TEMPERATURE_PRECISION) + "," + getUnitName(true, true));

            if(!astr.empty())
            {
                ged.setDataType(ID_DISPLAYED_TEMP);
                ged.setDataValue(astr);
                error = false;
            }

            break;
        }
    case TYPE_DLC_CH:
    case TYPE_DLC:
        {
            if(_DLC._cycleDuration == 0xffff)
            {
                astr += string("0,"); // (string("Unknown"));
            }
            else if(_DLC._cycleDuration <= 255)
            {
                astr += (CtiNumStr(_DLC._cycleDuration) + ",");
            }
            else
            {
                astr += string("0,"); // (string("Invalid"));
            }

            if(_DLC._cyclePeriod == 0xffff)
            {
                astr += string("0,");    // (string("Unknown"));
            }
            else if(_DLC._cyclePeriod <= 255)
            {
                astr += (CtiNumStr(_DLC._cyclePeriod) + ",");
            }
            else
            {
                astr += string("0,");    // (string("Invalid"));
            }

            if(_DLC._duration == 0xffff)
            {
                astr += (string("0,"));
            }
            else
            {
                astr += (CtiNumStr(_DLC._duration) + ",");
            }

            switch(_DLC._override)
            {
            case 0:
                astr += (string("NO OVERRIDE"));
                break;

            case 1:
                astr += (string("ACTIVE OVERRIDE"));
                break;

            case 255:
                astr += (string("UNKNOWN OVERRIDE"));
                break;

            default:
                astr += (string("INVALID OVERRIDE"));
                break;
            }


            switch(_DLC._overrideDisable)
            {
            case 0:
                astr += (string(",AVAILABLE"));
                break;

            case 1:
                astr += (string(",PROHIBITED"));
                break;

            case 255:
                astr += (string(",UNKNOWN"));
                break;

            default:
                astr += (string(",INVALID"));
                break;
            }


            if(!astr.empty())
            {
                ged.setDataType(ID_DLC);
                ged.setDataValue(astr);
                error = false;
            }

            break;
        }
    case TYPE_FANSWITCH_CH:
    case TYPE_FANSWITCH:
        {
            if(_fanSwitch._fanSwitch == 0)
            {
                astr = (string("AUTO"));
            }
            else if(_fanSwitch._fanSwitch == 255)
            {
                astr = (string("AUTO"));
            }
            else if(_fanSwitch._fanSwitch >= 1 && _fanSwitch._fanSwitch <= 200)
            {
                astr += (string("ON"));
            }
            else
            {
                astr += (string("AUTO"));
            }

            if(!astr.empty())
            {
                ged.setDataType(ID_FAN_SWITCH);
                ged.setDataValue(astr);
                error = false;
            }

            break;
        }
    case TYPE_FILTER_CH:
    case TYPE_FILTER:
        {
            if(_filter._filterRestart == 0)
            {
                astr += (string("-1,-1"));
            }
            else if(_filter._filterRestart == 255)
            {
                astr += (string("-1,-1"));
            }
            else
            {
                if(_filter._filterRemaining == 0)
                {
                    astr += (string("0,"));
                }
                else if(_filter._filterRemaining == 255)
                {
                    astr += (string("-1,"));
                }
                else
                {
                    astr += (string(CtiNumStr(_filter._filterRemaining) + ","));
                }
                astr += CtiNumStr(_filter._filterRestart);
            }

            if(!astr.empty())
            {
                ged.setDataType(ID_FILTER);
                ged.setDataValue(astr);
                error = false;
            }

            break;
        }
    case TYPE_HEATPUMPFAULT:
        {
            now = CtiTime(_heatPumpFault._utime);
            astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" Heatpump Fault Received: " ));

            switch(_heatPumpFault._heatPumpFault)
            {
            case 0:
                astr += (string("NO FAULT"));
                break;

            case 1:
                astr += (string("FAULT"));
                break;

            case 255:
                astr += (string("(UNKNOWN)"));
                break;

            default:
                astr += (string("INVALID"));
                break;
            }



            break;
        }
    case TYPE_SETPOINTLIMITS_CH:
    case TYPE_SETPOINTLIMITS:
        {
            if(_setpointLimits._lowerCoolSetpointLimit >= 0x7f00)
            {
                astr += (string("-1,"));
            }
            else
            {
                astr += (string(CtiNumStr(convertFromStatTemp(_setpointLimits._lowerCoolSetpointLimit)) + ","));
            }

            if(_setpointLimits._upperHeatSetpointLimit >= 0x7f00)
            {
                astr += (string("-1"));
            }
            else
            {
                astr += string(CtiNumStr(convertFromStatTemp(_setpointLimits._upperHeatSetpointLimit)));
            }

            if(!astr.empty())
            {
                ged.setDataType(ID_SETPOINT_LIMITS);
                ged.setDataValue(astr);
                error = false;
            }

            break;
        }
    case TYPE_OUTDOORTEMP:
        {
            if(_outdoorTemp._outdoorTemperature >= 0x7f00)
            {
                // astr += (string("Unknown"));
                astr = CtiNumStr(-100);
            }
            else if((_outdoorTemp._outdoorTemperature & 0xff00) == 0x8000)
            {
                // astr += (string("Shorted Sensor"));
                astr = CtiNumStr(-101);
            }
            else if((_outdoorTemp._outdoorTemperature & 0xff00) == 0x8100)
            {
                // astr += (string("Open Sensor"));
                astr = CtiNumStr(-102);
            }
            else if((_outdoorTemp._outdoorTemperature & 0xff00) == 0x8200)
            {
                // astr += (string("Not Available"));
                astr = CtiNumStr(-103);
            }
            else if((_outdoorTemp._outdoorTemperature & 0xff00) == 0x8300)
            {
                // astr += (string("Out of Range High"));
                astr = CtiNumStr(-104);
            }
            else if((_outdoorTemp._outdoorTemperature & 0xff00) == 0x8400)
            {
                // astr += (string("Out of Range Low"));
                astr = CtiNumStr(-105);
            }
            else if((_outdoorTemp._outdoorTemperature & 0xff00) == 0x8500)
            {
                // astr += (string("Unreliable"));
                astr = CtiNumStr(-106);
            }
            else
            {
                astr += CtiNumStr(convertFromStatTemp(_outdoorTemp._outdoorTemperature));
            }

            if(!astr.empty())
            {
                ged.setDataType(ID_OUTDOOR_TEMP);
                ged.setDataValue(astr);
                error = false;
            }

            break;
        }
    case TYPE_SCHEDULE_CH:
    case TYPE_SCHEDULE:
        {
            INT id = (ID_SCHEDULE_MON_WAKE + ( ((day + 6) % 7) * EP_PERIODS_PER_DAY ) + period);      // This is the first schedule ID!
            astr = generateTidbitScheduleToDatabase(day, period);

            if(Type == TYPE_SCHEDULE_CH && !astr.empty())
            {
                astr += ",CONFIRMED";
            }

            if(!astr.empty())
            {
                ged.setDataType(id);
                ged.setDataValue(astr);
                error = false;
            }

            break;
        }
    case TYPE_SYSTEMSWITCH_CH:
    case TYPE_SYSTEMSWITCH:
        {
            switch(_systemSwitch._systemSwitch)
            {
            case 0:
                astr = (string("EMERGENCY HEAT"));
                break;

            case 1:
                astr = (string("HEAT"));
                break;

            case 2:
                astr = (string("OFF"));
                break;

            case 3:
                astr = (string("COOL"));
                break;

            case 4:
                {
                    short coolsp = convertFromStatTemp(_setpoints._coolSetpoint);
                    short heatsp = convertFromStatTemp(_setpoints._heatSetpoint);

                    float dt = (float)_displayedTemp._displayedTemperature;
                    if(_displayedTemp._displayedTempUnits == 1)     // Celsius is given in 1/2 degrees.
                    {
                        dt = dt / 2 ;
                    }

                    if(_displayedTemp._utime == YUKONEOT)   // Take a guess!
                    {
                        switch(CtiDate().month())
                        {
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                            astr = (string("COOL,(AUTO)"));
                            break;
                        default:
                            astr = (string("HEAT,(AUTO)"));
                            break;
                        }
                    }
                    else if( _displayedTemp._displayedTemperature > coolsp )
                    {
                        astr = (string("COOL,(AUTO)"));
                    }
                    else if(_displayedTemp._displayedTemperature < heatsp)
                    {
                        astr = (string("HEAT,(AUTO)"));
                    }
                    else
                    {
                        astr = string(); // Don't change the table!.
                    }

                    break;
                }
            case 255:
                astr = (string("(UNKNOWN)"));
                break;

            default:
                astr = (string("(UNKNOWN)")); // INVALID"));
                break;
            }

            if(!astr.empty())
            {
                ged.setDataType(ID_SYSTEM_SWITCH);
                ged.setDataValue(astr);
                error = false;
            }

            break;
        }
    case TYPE_UTILSETPOINT_CH:
    case TYPE_UTILSETPOINT:
        {
            if(_utilSetpoint._utilCoolSetpoint >= 0x7f00)
            {
                astr = string("(UNKNOWN),");
            }
            else
            {
                astr = string(CtiNumStr(convertFromStatTemp(_utilSetpoint._utilCoolSetpoint)) + ",");
            }

            if(_utilSetpoint._utilHeatSetpoint >= 0x7f00)
            {
                astr += string("(UNKNOWN),");
            }
            else
            {
                astr += string(CtiNumStr(convertFromStatTemp(_utilSetpoint._utilHeatSetpoint)) + ",");
            }

            if(_utilSetpoint._utilDuration == 0)
            {
                astr += (string("INACTIVE,"));
            }
            else if(_utilSetpoint._utilDuration <= 1440)
            {
                astr += string(CtiNumStr(_utilSetpoint._utilDuration) + ",");
            }
            else
            {
                astr += (string("INVALID,"));
            }

            switch(_utilSetpoint._utilMode)
            {
            case 0:
                astr += (string("INACTIVE,"));
                break;

            case 1:
                astr += (string("PRICE TIER,"));
                break;

            case 2:
                astr += (string("TEMPERATURE OFFSET,"));
                break;

            case 3:
                astr += (string("PRECONDITION,"));
                break;

            case 255:
                astr += (string("UNKNOWN,"));
                break;

            default:
                astr += (string("INVALID,"));
                break;
            }

            switch(_utilSetpoint._utilPriceTier)
            {
            case 0:
                astr += (string("NONE,"));
                break;

            case 1:
                astr += (string("LOW,"));
                break;

            case 2:
                astr += (string("MEDIUM,"));
                break;

            case 3:
                astr += (string("HIGH,"));
                break;

            case 4:
                astr += (string("CRITICAL,"));
                break;

            case 255:
                astr += (string("UNKNOWN,"));
                break;

            default:
                astr += (string("INVALID,"));
                break;
            }

            switch(_utilSetpoint._utilUserOverride)
            {
            case 0:
                astr += (string("NO OVERRIDE,"));
                break;

            case 1:
                astr += (string("USER OVERRIDE ACTIVE,"));
                break;

            case 255:
                astr += (string("USER OVERRIDE UNKNOWN,"));
                break;

            default:
                astr += (string("USER OVERRIDE INVALID,"));
                break;
            }

            switch(_utilSetpoint._utilUserOverrideDisable)
            {
            case 0:
                astr += (string("PROHIBITED,"));
                break;

            case 1:
                astr += (string("AVAILABLE,"));
                break;

            case 255:
                astr += (string("UNKNOWN,"));
                break;

            default:
                astr += (string("INVALID,"));
                break;
            }

            switch(_utilSetpoint._utilAIRDisable)
            {
            case 0:
                astr += (string("ENABLED"));
                break;

            case 1:
                astr += (string("DISABLED"));
                break;

            case 255:
                astr += (string("UNKNOWN"));
                break;

            default:
                astr += (string("INVALID"));
                break;
            }

            if(!astr.empty())
            {
                ged.setDataType(ID_UTILITY_SETPOINTS);
                ged.setDataValue(astr);
                error = false;
            }

            break;
        }
    case TYPE_CLOCK:
        {
            now = CtiTime(_clock._utime);
            astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" Clock Received: "));

            // Return value:
            //  0 = Monday
            //  1 = Tuesday
            //  2 = Wednesday
            //  3 = Thursday
            //  4 = Friday
            //  5 = Saturday
            //  6 = Sunday
            //  255 = unknown
            //  At initialization, until the first EVNT_CLOCK event, the value will be 255.

            switch(_clock._day)
            {
            case 6:
                astr += (string("Sunday"));
                break;

            case 0:
                astr += (string("Monday"));
                break;

            case 1:
                astr += (string("Tuesday"));
                break;

            case 2:
                astr += (string("Wednesday"));
                break;

            case 3:
                astr += (string("Thursday"));
                break;

            case 4:
                astr += (string("Friday"));
                break;

            case 5:
                astr += (string("Saturday"));
                break;

            case 255:
                astr += (string("Unknown"));
                break;

            default:
                astr += (string("Invalid"));
                break;
            }

            astr += (string(", "));
            if(_clock._hour == 255)
            {
                astr += (string("UU:"));
            }
            else if(_clock._hour < 24)
            {
                astr += (string(CtiNumStr(_clock._hour).zpad(2) + ":"));
            }
            else
            {
                astr += (string("II:"));
            }

            if(_clock._minute == 255)
            {
                astr += (string("II:"));
            }
            else if(_clock._minute < 60)
            {
                astr += (string(CtiNumStr(_clock._minute).zpad(2) + ":"));
            }
            else
            {
                astr += (string("II:"));
            }

            if(_clock._second == 255)
            {
                astr += (string("UU"));
            }
            else if(_clock._second < 60)
            {
                astr += (string(CtiNumStr(_clock._second).zpad(2)));
            }
            else
            {
                astr += (string("II"));
            }


            break;
        }
    case TYPE_DEVICEBOUND:
        {
            now = CtiTime(_deviceBound._utime);
            astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" Device Bound Received: "));


            break;
        }
    case TYPE_DEVICEUNBOUND:
        {
            now = CtiTime(_deviceUnbound._utime);
            astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" Device Unbound Received: "));

            break;
        }
    case TYPE_RETURNCODE:
        {
            now = CtiTime(_returnCode._utime);
            astr = (now.asString() + string(" Stat ") + CtiNumStr(getDeviceSerialNumber()).spad(3) + string(" ReturnCode Received: "));
            astr += "Responded with condition: " + CtiNumStr(_returnCode._returnCode) + string(" from a command of type: ") + CtiNumStr(_returnCode._setType);

            break;
        }
    default:
        {
            error = true;
            break;
        }
    }

    if( !error )
    {
        try
        {
            CtiTime _now = now;
            CtiDate gmtdate( _now.toUTCtime() );
            // Always record one of these
            tstamp.setSerialNumber(getDeviceSerialNumber());
            tstamp.setHardwareType(TYPE_ENERGYPRO);
            tstamp.setDataType(ID_TIMESTAMP);
            string gmt_str = gmtdate.asString() + " " + CtiNumStr(now.hourGMT()).zpad(2) + ":" + CtiNumStr(now.minuteGMT()).zpad(2) + ":" + CtiNumStr(now.second()).zpad(2) + " GMT";
            tstamp.setDataValue( gmt_str );

            ged.Update();
            tstamp.Update();
        }
        catch(...)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " Exception recording data for thermostat!" << endl;
            }
        }
    }

    return error;
}

string CtiDeviceGatewayStat::generateTidbitScheduleToDatabase(int day, int period)
{
    bool status = false;

    string astr;

    {
        if( (day >= 0 && day < 7) && (period >= 0 && period < EP_PERIODS_PER_DAY))   // valid day & period.
        {
            switch(_schedule[day][period]._fan)
            {
            case 3:
                astr += (string("ON,  "));
                break;
            case 1:
                astr += (string("AUTO,"));
                break;
            case 0:                                 // astr += (string("NSch"));
            case 2:                                 // astr += (string("Circ"));
            case 255:                               // astr += (string("Unk "));
            default:                                // astr += (string("Inv "));
                astr += (string("NONE,"));
                break;
            }


            if(_schedule[day][period]._hour == 254)
            {
                // astr += (string("CC:"));      // Restart/Unknown
                astr += (string("-1,"));
            }
            else if(_schedule[day][period]._hour == 255)
            {
                // astr += (string("UU:"));      // Unknown
                astr += (string("-1,"));
            }
            else if(_schedule[day][period]._hour < 24)
            {
                astr += (string(CtiNumStr(_schedule[day][period]._hour).zpad(2) + ","));
            }
            else
            {
                // astr += (string("II:"));      // Invalid
                astr += (string("-1,"));
            }

            switch(_schedule[day][period]._minute)
            {
            case 0:
            case 15:
            case 30:
            case 45:
                astr += (string(CtiNumStr(_schedule[day][period]._minute).zpad(2) + ","));
                break;

            case 254:   // astr += (string("CC"));
            case 255:   // astr += (string("UU"));
            default:    // astr += (string("II"));
                astr += (string("-1,"));
                break;
            }

            short sp = convertFromStatTemp(_schedule[day][period]._coolSetpoint);
            if(_schedule[day][period]._coolSetpoint >= 0x7f00)
            {
                astr += string("0,");
            }
            else
            {
                astr += string(CtiNumStr(sp) + ",");
            }

            sp = convertFromStatTemp(_schedule[day][period]._heatSetpoint);

            if(_schedule[day][period]._heatSetpoint >= 0x7f00)
            {
                astr += (string("0"));
            }
            else
            {
                astr += (string(CtiNumStr(sp)));
            }
        }
    }

    return astr;
}

string CtiDeviceGatewayStat::getUnitName(bool abbreviated, bool nospaces)
{
    string str;

    if(_displayedTemp._displayedTempUnits == 1)     // This is Celsius
    {
        if(abbreviated)
            str = " C";
        else
            str = " Celsius";
    }
    else
    {
        if(abbreviated)
            str = " F";
        else
            str = " Fahrenheit";
    }

    if(nospaces)
    {
        str = trim(str);
    }

    return str;
}

void CtiDeviceGatewayStat::addOperation(const CtiPendingStatOperation &op)
{
    CtiLockGuard< CtiMutex > gd(_collMux);

    if(op.getOperation() > 0)
    {
        pair<OpCol_t::iterator, bool> ins_pair = _operations.insert(op);

        if(ins_pair.second == false)    // operation was already in the list.
        {
            *(ins_pair.first) = op;     // Update it with the new one.
        }
    }

    return;
}

bool CtiDeviceGatewayStat::getCompletedOperation( CtiPendingStatOperation &op )
{
    bool gotone = false;
    int line = 0;

    CtiLockGuard< CtiMutex > gd(_collMux, 1000);

    try
    {
        line = __LINE__;
        if(gd.isAcquired() && !_operations.empty())
        {
            CtiDeviceGatewayStat::OpCol_t::iterator oper_itr;
            line = __LINE__;
            for( oper_itr = _operations.begin(); oper_itr != _operations.end(); oper_itr++ )
            {
                OpCol_t::value_type &valtype = *oper_itr;

                line = __LINE__;
                if(valtype.isConfirmed())
                {
                    line = __LINE__;
                    op = valtype;
                    line = __LINE__;
                    gotone = true;
                    line = __LINE__;
                    _operations.erase(oper_itr);
                    line = __LINE__;
                    break;
                }
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " dumped after line " << line << endl;
        }
    }

    return gotone;
}

int CtiDeviceGatewayStat::processSchedulePeriod(SOCKET msgsock, CtiCommandParser &parse, CtiOutMessage *&OutMessage, int dow, int pod, BYTE per)
{
    int processed = 0;

    string hhstr("xctodshh_" + CtiNumStr(per));
    string mmstr("xctodsmm_" + CtiNumStr(per));
    string heatstr("xctodsheat_" + CtiNumStr(per));
    string coolstr("xctodscool_" + CtiNumStr(per));

    BYTE hh = (BYTE)parse.getiValue(hhstr, 0xff);
    BYTE mm = (BYTE)parse.getiValue(mmstr, 0xff);
    int heat = (BYTE)parse.getiValue(heatstr, 0xff);
    int cool = (BYTE)parse.getiValue(coolstr, 0xff);

    if(hh != 0xff || mm != 0xff || heat != 0xff || cool != 0xff)
    {
        // One of them were defined!
        processed = 1;

        if(dow < 7 && pod < EP_PERIODS_PER_DAY)
        {
            if(hh == 0xff)
            {
                hh = _schedule[dow][pod]._hour;
            }
            if(mm == 0xff)
            {
                mm = _schedule[dow][pod]._minute;
            }

            if(heat == 0xff)
            {
                if(_schedule[dow][pod]._heatSetpoint < 0x7f00)
                {
                    heat = convertFromStatTemp(_schedule[dow][pod]._heatSetpoint, scaleFahrenheit);
                }
                else
                {
                    heat = 68;
                }
            }
            if(cool == 0xff)
            {
                if(_schedule[dow][pod]._coolSetpoint < 0x7f00)
                {
                    cool = convertFromStatTemp(_schedule[dow][pod]._coolSetpoint, scaleFahrenheit);
                }
                else
                {
                    cool = 80;
                }
            }

#if 0
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " Day    " << dow << endl;
                dout << " Period " << pod+1 << endl;
            }
#endif

            _schedule[dow][pod]._ch_utime = 86400;
            _schedule[dow][pod]._utime = 86400;
        }

        sendSetSchedule(msgsock, convertCDayToStatDay(dow), pod+1, convertToStatTemp(heat, scaleFahrenheit), convertToStatTemp(cool, scaleFahrenheit), hh, mm, 1 );
    }

    return processed;
}

// Assumes consecutive periods!
SHORT CtiDeviceGatewayStat::getCurrentHeatSchedule() const
{
    SHORT val = 0;
    CtiDate today;
    CtiTime now;

    int day = today.weekDay();
    int period;

    unsigned sec = now.hour() * 3600 + now.minute() * 60 + now.second();
    unsigned lastpersec = 0;
    unsigned persec;

    for(period = 0; period < EP_PERIODS_PER_DAY; period++)
    {
        persec = _schedule[day][period]._hour * 3600 + _schedule[day][period]._minute * 60;

        if(lastpersec < sec && sec < persec)        // This is the period we are looking for!
        {
            period = (period + 3) % 4;
            val = _schedule[day][period]._heatSetpoint;
            break;
        }

        lastpersec = persec;
    }

    return val;
}

SHORT CtiDeviceGatewayStat::getCurrentCoolSchedule() const
{
    SHORT val = 0;
    CtiDate today;
    CtiTime now;

    int day = today.weekDay();
    int period;

    unsigned sec = now.hour() * 3600 + now.minute() * 60 + now.second();
    unsigned lastpersec = 0;
    unsigned persec;

    for(period = 0; period < EP_PERIODS_PER_DAY; period++)
    {
        persec = _schedule[day][period]._hour * 3600 + _schedule[day][period]._minute * 60;

        if(lastpersec < sec && sec < persec)        // This is the period we are looking for!
        {
            period = (period + 3) % 4;
            val = _schedule[day][period]._coolSetpoint;
            break;
        }

        lastpersec = persec;
    }

    return val;
}

int CtiDeviceGatewayStat::estimateSetpointPriority()
{
    int sppriority;

    switch(CtiDate().month())
    {
    case 6:
    case 7:
    case 8:
        sppriority = EP_SETPOINT_PRIORITY_COOL;
        break;
    default:
        sppriority = EP_SETPOINT_PRIORITY_HEAT;
        break;
    }

    return sppriority;
}

bool CtiDeviceGatewayStat::verifyGatewayDid()
{
    if(getID() < 0)
    {
        // This is a once per restart check.  ADDING stats to the DB will NOT make them reload properly without additional code.
        // Try to reload it based upon our known statid.
        setID(GetPAOIdOfEnergyPro(_deviceSN));
    }

    return (getID() > 0);
}

void CtiDeviceGatewayStat::postAnalogOutputPoint(UINT Type, UINT pointoffset, double value)
{
    if(verifyGatewayDid())     // Is my paoid determined???
    {
        CtiPointNumericSPtr pNumericPoint;

        if (pNumericPoint = boost::static_pointer_cast<CtiPointNumeric>(getDevicePointOffsetTypeEqual(pointoffset, AnalogPointType)))
        {
            string valReport = printListAsString(Type);

            LockGuard guard(monitor());

            // This point exists and is in the DB.  Let's process and create an rsvpToDispatch.
            if(!_pMulti)
            {
                _pMulti = new CtiMultiMsg;
            }

            value = pNumericPoint->computeValueForUOM(value);

            CtiPointDataMsg *pData = new CtiPointDataMsg(pNumericPoint->getPointID(),
                                                         value,
                                                         NormalQuality,
                                                         AnalogPointType,
                                                         valReport);
            _pMulti->insert(pData);
        }
    }

    return;
}

/*
 *  Last transaction produced this batch of responses to be sent to dispatch!
 */
CtiMessage* CtiDeviceGatewayStat::rsvpToDispatch(bool clearMessage)
{
    LockGuard guard(monitor());

    CtiMultiMsg *pTemp = _pMulti;
    _pMulti = 0;

    return pTemp;
}
