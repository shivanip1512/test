
#include "precompiled.h"
#include "ccid.h"
#include "CParms.h"
#include "logger.h"
#include "utility.h"
#include "string_util.h"

#include <boost/range/adaptor/transformed.hpp>
using std::endl;
using std::string;

/*
    CapControl CParms
*/
unsigned long   _CC_DEBUG;
unsigned long   _DB_RELOAD_WAIT;
bool    _IGNORE_NOT_NORMAL_FLAG;
unsigned long   _SEND_TRIES;
bool    _USE_FLIP_FLAG;
unsigned long   _POST_CONTROL_WAIT;
unsigned long   _POINT_AGE;
unsigned long   _SCAN_WAIT_EXPIRE;
bool    _ALLOW_PARALLEL_TRUING;
bool    _RETRY_FAILED_BANKS;
unsigned long   _MAX_KVAR;
unsigned long   _MAX_KVAR_TIMEOUT;
bool    _USE_PHASE_INDICATORS;
bool    _LOG_MAPID_INFO;
unsigned long   _LIKEDAY_OVERRIDE_TIMEOUT;
bool    _RETRY_ADJUST_LAST_OP_TIME;
unsigned long   _REFUSAL_TIMEOUT;
unsigned long   _MSG_PRIORITY;
bool    CC_TERMINATE_THREAD_TEST;
unsigned long   _OP_STATS_USER_DEF_PERIOD;
unsigned long   _OP_STATS_REFRESH_RATE;
bool    _OP_STATS_DYNAMIC_UPDATE;
unsigned long   _LINK_STATUS_TIMEOUT;
bool    _RATE_OF_CHANGE;
unsigned long   _RATE_OF_CHANGE_DEPTH;
long    _VOLT_REDUCTION_SYSTEM_POINTID;
bool    _AUTO_VOLT_REDUCTION;
unsigned long   _VOLT_REDUCTION_COMMANDS;
unsigned long   _VOLT_REDUCTION_COMMAND_DELAY;
string  _MAXOPS_ALARM_CAT;
long    _MAXOPS_ALARM_CATID;
bool    _LIMIT_ONE_WAY_COMMANDS;

bool    _ENABLE_IVVC;
unsigned long   _IVVC_MIN_TAP_PERIOD_MINUTES;
unsigned long   _IVVC_COMMS_RETRY_COUNT;
double  _IVVC_NONWINDOW_MULTIPLIER;
double  _IVVC_DEFAULT_DELTA;
bool    _IVVC_STATIC_DELTA_VOLTAGES;
bool    _IVVC_INDIVIDUAL_DEVICE_VOLTAGE_TARGETS;
unsigned long _IVVC_REGULATOR_AUTO_MODE_MSG_DELAY;

bool    _DMV_TEST_ENABLED;


void refreshGlobalCParms()
{
    string  str;
    char    var[128];

    _CC_DEBUG = CC_DEBUG_NONE;

    strcpy(var, "CAP_CONTROL_DEBUG");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        CtiToLower(str);
        _CC_DEBUG = (str == "true" ? (CC_DEBUG_STANDARD | CC_DEBUG_POINT_DATA) : CC_DEBUG_NONE);

        if ( !_CC_DEBUG )
        {
            if ( str == "false" )
            {
                _CC_DEBUG = CC_DEBUG_NONE;
            }
            else
            {
                char *eptr;
                _CC_DEBUG = strtoul(str.c_str(), &eptr, 16);
            }
        }

        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, var << ":  " << str);
        }
    }
    else
    {
        CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
    }

    _DB_RELOAD_WAIT = 5;  // seconds

    strcpy(var, "CAP_CONTROL_DB_RELOAD_WAIT");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        _DB_RELOAD_WAIT = atoi(str.c_str());

        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, var << ":  " << str);
        }
    }
    else
    {
        CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
    }

    _IGNORE_NOT_NORMAL_FLAG = false;

    strcpy(var, "CAP_CONTROL_IGNORE_NOT_NORMAL");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        CtiToLower(str);
        _IGNORE_NOT_NORMAL_FLAG = (str == "true" );

        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, var << ":  " << str);
        }
    }
    else
    {
        CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
    }

    _SEND_TRIES = 1;

    strcpy(var, "CAP_CONTROL_SEND_RETRIES");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        _SEND_TRIES = atoi(str.c_str()) + 1;

        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, var << ":  " << str);
        }
    }
    else
    {
        CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
    }

    _USE_FLIP_FLAG = false;

    strcpy(var, "CAP_CONTROL_USE_FLIP");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        CtiToLower(str);
        _USE_FLIP_FLAG = (str == "true" );

        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, var << ":  " << str);
        }
    }
    else
    {
        CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
    }

    _POST_CONTROL_WAIT = 30;    // seconds

    strcpy(var, "CAP_CONTROL_POST_CONTROL_WAIT");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        _POST_CONTROL_WAIT = atoi(str.c_str()) + 1;
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, var << ":  " << str);
        }
    }
    else
    {
        CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
    }

    _POINT_AGE = 3;  // minutes

    strcpy(var, "CAP_CONTROL_POINT_AGE");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        _POINT_AGE = atoi(str.c_str()) + 1;
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, var << ":  " << str);
        }
    }
    else
    {
        CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
    }

    _SCAN_WAIT_EXPIRE = 1;  // minutes

    strcpy(var, "CAP_CONTROL_SCAN_WAIT_EXPIRE");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        _SCAN_WAIT_EXPIRE = atoi(str.c_str()) + 1;
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, var << ":  " << str);
        }
    }
    else
    {
        CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
    }

    _ALLOW_PARALLEL_TRUING = false;

    strcpy(var, "CAP_CONTROL_ALLOW_PARALLEL_TRUING");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        CtiToLower(str);
        _ALLOW_PARALLEL_TRUING = (str == "true" );
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, var << ":  " << str);
        }
    }
    else
    {
        CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
    }

    _RETRY_FAILED_BANKS = false;

    strcpy(var, "CAP_CONTROL_RETRY_FAILED_BANKS");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        CtiToLower(str);
        _RETRY_FAILED_BANKS = (str == "true" );
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, var << ":  " << str);
        }
    }
    else
    {
        CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
    }

    _MAX_KVAR = gConfigParms.getValueAsULong("CAP_CONTROL_MAX_KVAR", -1);
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "CAP_CONTROL_MAX_KVAR: " << _MAX_KVAR);
    }

    _MAX_KVAR_TIMEOUT = gConfigParms.getValueAsULong("CAP_CONTROL_MAX_KVAR_TIMEOUT", 300);
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "CAP_CONTROL_MAX_KVAR_TIMEOUT: " << _MAX_KVAR_TIMEOUT);
    }

    _USE_PHASE_INDICATORS = false;

    strcpy(var, "CAP_CONTROL_USE_PHASE_INDICATORS");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        CtiToLower(str);
        _USE_PHASE_INDICATORS = (str == "true" );
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, var << ":  " << str);
        }
    }
    else
    {
        CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
    }

    _LOG_MAPID_INFO = false;

    strcpy(var, "CAP_CONTROL_LOG_MAPID_INFO");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        CtiToLower(str);
        _LOG_MAPID_INFO = (str == "true" );
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, var << ":  " << str);
        }
    }
    else
    {
        CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
    }

    _LIKEDAY_OVERRIDE_TIMEOUT = 604800;  // seconds: 7 days

    strcpy(var, "CAP_CONTROL_LIKEDAY_OVERRIDE_TIMEOUT");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        _LIKEDAY_OVERRIDE_TIMEOUT = atoi(str.c_str());
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, var << ":  " << str);
        }
    }
    else
    {
        CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
    }

    _RETRY_ADJUST_LAST_OP_TIME = true;

    strcpy(var, "CAP_CONTROL_RETRY_ADJUST_LAST_OP_TIME");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        CtiToLower(str);
        _RETRY_ADJUST_LAST_OP_TIME = (str == "true" );
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, var << ":  " << str);
        }
    }
    else
    {
        CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
    }

    _REFUSAL_TIMEOUT = gConfigParms.getValueAsULong("CAP_CONTROL_REFUSAL_TIMEOUT", 240);    // minutes
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "CAP_CONTROL_REFUSAL_TIMEOUT: " << _REFUSAL_TIMEOUT);
    }

    _MSG_PRIORITY = gConfigParms.getValueAsULong("CAP_CONTROL_MSG_PRIORITY", 13);
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "CAP_CONTROL_MSG_PRIORITY: " << _MSG_PRIORITY);
    }

    //DO NOT PRINT THIS OUT TO DEBUG unless true
    CC_TERMINATE_THREAD_TEST = gConfigParms.isTrue("CC_TERMINATE_THREAD_TEST", false);
    if ( CC_TERMINATE_THREAD_TEST )
    {
        CTILOG_INFO(dout, "CC_TERMINATE_THREAD_TEST: " << CC_TERMINATE_THREAD_TEST);
    }

    _OP_STATS_USER_DEF_PERIOD = 0;  // minutes

    strcpy(var, "CAP_CONTROL_OP_STATS_USER_DEF_PERIOD");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        _OP_STATS_USER_DEF_PERIOD = atoi(str.c_str());
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, var << ":  " << str);
        }
    }
    else
    {
        CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
    }

    _OP_STATS_REFRESH_RATE = 3600;  // seconds

    strcpy(var, "CAP_CONTROL_OP_STATS_REFRESH_RATE");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        _OP_STATS_REFRESH_RATE = atoi(str.c_str());
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, var << ":  " << str);
        }
    }
    else
    {
        CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
    }

    _OP_STATS_DYNAMIC_UPDATE = gConfigParms.isTrue("CAP_CONTROL_OP_STATS_DYNAMIC_UPDATE", false);
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "CAP_CONTROL_OP_STATS_DYNAMIC_UPDATE: " << _OP_STATS_DYNAMIC_UPDATE);
    }

    _LINK_STATUS_TIMEOUT = 5;   // minutes

    strcpy(var, "CAP_CONTROL_LINK_STATUS_TIMEOUT");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        _LINK_STATUS_TIMEOUT = atoi(str.c_str()) + 1;
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, var << ":  " << str);
        }
    }
    else
    {
        CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
    }

    _RATE_OF_CHANGE = gConfigParms.isTrue("CAP_CONTROL_RATE_OF_CHANGE");
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "CAP_CONTROL_RATE_OF_CHANGE: " << _RATE_OF_CHANGE);
    }

    _RATE_OF_CHANGE_DEPTH = gConfigParms.getValueAsULong("CAP_CONTROL_RATE_OF_CHANGE_DEPTH", 10);
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "CAP_CONTROL_RATE_OF_CHANGE_DEPTH: " << _RATE_OF_CHANGE_DEPTH);
    }


    _VOLT_REDUCTION_SYSTEM_POINTID = 0; // pointid

    strcpy(var, "CAP_CONTROL_VOLT_REDUCTION_SYSTEM_POINTID");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        _VOLT_REDUCTION_SYSTEM_POINTID = atol(str.c_str());
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, var << ":  " << str);
        }
    }
    else
    {
        CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
    }

    _AUTO_VOLT_REDUCTION = false;

    strcpy(var, "CAP_CONTROL_AUTO_VOLT_REDUCTION");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        CtiToLower(str);
        _AUTO_VOLT_REDUCTION = (str == "true" );
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, var << ":  " << str);
        }
    }
    else
    {
        CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
    }

    _VOLT_REDUCTION_COMMANDS = 0; // number of command/retries for disable ovuv

    strcpy(var, "CAP_CONTROL_VOLT_REDUCTION_COMMANDS");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        _VOLT_REDUCTION_COMMANDS = atoi(str.c_str());
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, var << ":  " << str);
        }
    }
    else
    {
        CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
    }

    _VOLT_REDUCTION_COMMAND_DELAY = 0;  //delay between commands sent.

    strcpy(var, "CAP_CONTROL_VOLT_REDUCTION_COMMAND_DELAY");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        _VOLT_REDUCTION_COMMAND_DELAY = atoi(str.c_str());
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, var << ":  " << str);
        }
    }
    else
    {
        CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
    }

    _MAXOPS_ALARM_CAT   = "(NONE)";
    _MAXOPS_ALARM_CATID = 1;

    strcpy(var, "CAP_CONTROL_MAXOPS_ALARM_CAT");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        CtiToLower(str);
        _MAXOPS_ALARM_CAT = "%"+str+"%";

        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, var << ":  " << str);
        }
    }
    else
    {
        CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
    }

    _LIMIT_ONE_WAY_COMMANDS = gConfigParms.isTrue("CAP_CONTROL_LIMIT_ONE_WAY_COMMANDS", false);
    if ( _CC_DEBUG & CC_DEBUG_STANDARD)
    {
        CTILOG_DEBUG(dout, "CAP_CONTROL_LIMIT_ONE_WAY_COMMANDS: " << _LIMIT_ONE_WAY_COMMANDS);
    }

    _ENABLE_IVVC = false;

    strcpy(var, "CAP_CONTROL_ENABLE_IVVC");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        CtiToLower(str);
        _ENABLE_IVVC = (str == "true" );
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, var << ":  " << str);
        }
    }
    else
    {
        CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
    }

    _IVVC_MIN_TAP_PERIOD_MINUTES = gConfigParms.getValueAsULong("CAP_CONTROL_IVVC_MIN_TAP_PERIOD_MINUTES", 15);
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "CAP_CONTROL_IVVC_MIN_TAP_PERIOD_MINUTES: " << _IVVC_MIN_TAP_PERIOD_MINUTES);
    }

    _IVVC_COMMS_RETRY_COUNT = gConfigParms.getValueAsULong("CAP_CONTROL_IVVC_COMMS_RETRY_COUNT", 3);
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "CAP_CONTROL_IVVC_COMMS_RETRY_COUNT: " << _IVVC_COMMS_RETRY_COUNT);
    }

    _IVVC_NONWINDOW_MULTIPLIER = gConfigParms.getValueAsDouble("CAP_CONTROL_IVVC_NONWINDOW_MULTIPLIER", 1.5);
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "CAP_CONTROL_IVVC_NONWINDOW_MULTIPLIER: " << _IVVC_NONWINDOW_MULTIPLIER);
    }

    _IVVC_DEFAULT_DELTA = gConfigParms.getValueAsDouble("CAP_CONTROL_IVVC_DEFAULT_DELTA", 0);
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "CAP_CONTROL_IVVC_DEFAULT_DELTA: " << _IVVC_DEFAULT_DELTA);
    }

    _IVVC_STATIC_DELTA_VOLTAGES = gConfigParms.isTrue("CAP_CONTROL_IVVC_STATIC_DELTA_VOLTAGES");
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "CAP_CONTROL_IVVC_STATIC_DELTA_VOLTAGES: " << _IVVC_STATIC_DELTA_VOLTAGES);
    }

    _IVVC_INDIVIDUAL_DEVICE_VOLTAGE_TARGETS = gConfigParms.isTrue("CAP_CONTROL_IVVC_INDIVIDUAL_DEVICE_VOLTAGE_TARGETS");
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "CAP_CONTROL_IVVC_INDIVIDUAL_DEVICE_VOLTAGE_TARGETS: " << _IVVC_INDIVIDUAL_DEVICE_VOLTAGE_TARGETS);
    }

    _IVVC_REGULATOR_AUTO_MODE_MSG_DELAY = gConfigParms.getValueAsULong("CAP_CONTROL_IVVC_REGULATOR_AUTO_MODE_MSG_DELAY", 2);    // seconds
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "CAP_CONTROL_IVVC_REGULATOR_AUTO_MODE_MSG_DELAY: " << _IVVC_REGULATOR_AUTO_MODE_MSG_DELAY << " seconds.");
    }

    /*
        Demand Management & Verification Test
     
            DEMAND_MEASUREMENT_VERIFICATION_ENABLED     :   452C3B88-1BD2-468A-6D62-FDFB58B528B5
    */

    // Deliberate obfuscation so the raw key doesn't show up in the binary in readable form. Stored
    //  as hex bytes "45" --> \x45, the key is decoded in memory and discarded after use.
    const std::array<std::string, 5>    key
    {
        "\x45\x2C\x3B\x88", "\x1B\xD2", "\x46\x8A", "\x6D\x62", "\xFD\xFB\x58\xB5\x28\xB5"
    };

    auto keyFormatter = []( const std::string & input ) -> std::string
    {
        std::string result;

        for ( auto b : input )
        {
            result += Cti::toAsciiHex( ( b >> 4 ) & 0x0F );
            result += Cti::toAsciiHex( b & 0x0F );
        }

        return result;
    };

    _DMV_TEST_ENABLED = ciStringEqual(
        gConfigParms.getValueAsString( "DEMAND_MEASUREMENT_VERIFICATION_ENABLED" ),
        boost::join( key | boost::adaptors::transformed( keyFormatter ), "-" ) );

    if ( _DMV_TEST_ENABLED && ( _CC_DEBUG & CC_DEBUG_STANDARD ) )
    {
        CTILOG_DEBUG( dout, "Demand Measurement & Verification Testing is ENABLED" );
    }
}

