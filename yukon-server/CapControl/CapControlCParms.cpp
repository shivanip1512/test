
#include "precompiled.h"
#include "ccid.h"
#include "CParms.h"
#include "logger.h"
#include "utility.h"

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
bool    _ENABLE_IVVC;
unsigned long   _IVVC_MIN_TAP_PERIOD_MINUTES;
unsigned long   _IVVC_COMMS_RETRY_COUNT;
double  _IVVC_NONWINDOW_MULTIPLIER;
double  _IVVC_BANKS_REPORTING_RATIO;
double  _IVVC_REGULATOR_REPORTING_RATIO;
double  _IVVC_VOLTAGEMONITOR_REPORTING_RATIO;
double  _IVVC_DEFAULT_DELTA;
bool    _LIMIT_ONE_WAY_COMMANDS;
bool    _IVVC_STATIC_DELTA_VOLTAGES;
bool    _IVVC_INDIVIDUAL_DEVICE_VOLTAGE_TARGETS;

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
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << var << ":  " << str << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    strcpy(var, "CAP_CONTROL_LOG_FILE");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        dout.setOutputFile(str.c_str());

        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << var << ":  " << str << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    _DB_RELOAD_WAIT = 5;  // seconds

    strcpy(var, "CAP_CONTROL_DB_RELOAD_WAIT");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        _DB_RELOAD_WAIT = atoi(str.c_str());

        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << var << ":  " << str << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    _IGNORE_NOT_NORMAL_FLAG = false;

    strcpy(var, "CAP_CONTROL_IGNORE_NOT_NORMAL");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        CtiToLower(str);
        _IGNORE_NOT_NORMAL_FLAG = (str == "true" ? true : false);

        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << var << ":  " << str << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    _SEND_TRIES = 1;

    strcpy(var, "CAP_CONTROL_SEND_RETRIES");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        _SEND_TRIES = atoi(str.c_str()) + 1;

        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << var << ":  " << str << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    _USE_FLIP_FLAG = false;

    strcpy(var, "CAP_CONTROL_USE_FLIP");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        CtiToLower(str);
        _USE_FLIP_FLAG = (str == "true" ? true : false);

        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << var << ":  " << str << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    _POST_CONTROL_WAIT = 30;    // seconds

    strcpy(var, "CAP_CONTROL_POST_CONTROL_WAIT");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        _POST_CONTROL_WAIT = atoi(str.c_str()) + 1;
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << var << ":  " << str << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    _POINT_AGE = 3;  // minutes

    strcpy(var, "CAP_CONTROL_POINT_AGE");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        _POINT_AGE = atoi(str.c_str()) + 1;
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << var << ":  " << str << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    _SCAN_WAIT_EXPIRE = 1;  // minutes

    strcpy(var, "CAP_CONTROL_SCAN_WAIT_EXPIRE");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        _SCAN_WAIT_EXPIRE = atoi(str.c_str()) + 1;
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << var << ":  " << str << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    _ALLOW_PARALLEL_TRUING = false;

    strcpy(var, "CAP_CONTROL_ALLOW_PARALLEL_TRUING");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        CtiToLower(str);
        _ALLOW_PARALLEL_TRUING = (str == "true" ? true : false);
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << var << ":  " << str << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    _RETRY_FAILED_BANKS = false;

    strcpy(var, "CAP_CONTROL_RETRY_FAILED_BANKS");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        CtiToLower(str);
        _RETRY_FAILED_BANKS = (str == "true" ? true : false);
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << var << ":  " << str << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    _MAX_KVAR = gConfigParms.getValueAsULong("CAP_CONTROL_MAX_KVAR", -1);
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CAP_CONTROL_MAX_KVAR: " << _MAX_KVAR << endl;
    }

    _MAX_KVAR_TIMEOUT = gConfigParms.getValueAsULong("CAP_CONTROL_MAX_KVAR_TIMEOUT", 300);
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CAP_CONTROL_MAX_KVAR_TIMEOUT: " << _MAX_KVAR_TIMEOUT << endl;
    }

    _USE_PHASE_INDICATORS = false;

    strcpy(var, "CAP_CONTROL_USE_PHASE_INDICATORS");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        CtiToLower(str);
        _USE_PHASE_INDICATORS = (str == "true" ? true : false);
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << var << ":  " << str << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    _LOG_MAPID_INFO = false;

    strcpy(var, "CAP_CONTROL_LOG_MAPID_INFO");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        CtiToLower(str);
        _LOG_MAPID_INFO = (str == "true" ? true : false);
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << var << ":  " << str << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    _LIKEDAY_OVERRIDE_TIMEOUT = 604800;  // seconds: 7 days

    strcpy(var, "CAP_CONTROL_LIKEDAY_OVERRIDE_TIMEOUT");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        _LIKEDAY_OVERRIDE_TIMEOUT = atoi(str.c_str());
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << var << ":  " << str << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    _RETRY_ADJUST_LAST_OP_TIME = true;

    strcpy(var, "CAP_CONTROL_RETRY_ADJUST_LAST_OP_TIME");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        CtiToLower(str);
        _RETRY_ADJUST_LAST_OP_TIME = (str == "true" ? true : false);
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << var << ":  " << str << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    _REFUSAL_TIMEOUT = gConfigParms.getValueAsULong("CAP_CONTROL_REFUSAL_TIMEOUT", 240);    // minutes
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CAP_CONTROL_REFUSAL_TIMEOUT: " << _REFUSAL_TIMEOUT << endl;
    }

    _MSG_PRIORITY = gConfigParms.getValueAsULong("CAP_CONTROL_MSG_PRIORITY", 13);
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CAP_CONTROL_MSG_PRIORITY: " << _MSG_PRIORITY << endl;
    }

    //DO NOT PRINT THIS OUT TO DEBUG unless true
    CC_TERMINATE_THREAD_TEST = gConfigParms.isTrue("CC_TERMINATE_THREAD_TEST", false);
    if ( CC_TERMINATE_THREAD_TEST )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CC_TERMINATE_THREAD_TEST: " << CC_TERMINATE_THREAD_TEST << endl;
    }

    _OP_STATS_USER_DEF_PERIOD = 0;  // minutes

    strcpy(var, "CAP_CONTROL_OP_STATS_USER_DEF_PERIOD");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        _OP_STATS_USER_DEF_PERIOD = atoi(str.c_str());
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << var << ":  " << str << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    _OP_STATS_REFRESH_RATE = 3600;  // seconds

    strcpy(var, "CAP_CONTROL_OP_STATS_REFRESH_RATE");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        _OP_STATS_REFRESH_RATE = atoi(str.c_str());
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << var << ":  " << str << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    _OP_STATS_DYNAMIC_UPDATE = gConfigParms.isTrue("CAP_CONTROL_OP_STATS_DYNAMIC_UPDATE", false);
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CAP_CONTROL_OP_STATS_DYNAMIC_UPDATE: " << _OP_STATS_DYNAMIC_UPDATE << endl;
    }

    _LINK_STATUS_TIMEOUT = 5;   // minutes

    strcpy(var, "CAP_CONTROL_LINK_STATUS_TIMEOUT");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        _LINK_STATUS_TIMEOUT = atoi(str.c_str()) + 1;
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << var << ":  " << str << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    _RATE_OF_CHANGE = gConfigParms.isTrue("CAP_CONTROL_RATE_OF_CHANGE");
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CAP_CONTROL_RATE_OF_CHANGE: " << _RATE_OF_CHANGE << endl;
    }

    _RATE_OF_CHANGE_DEPTH = gConfigParms.getValueAsULong("CAP_CONTROL_RATE_OF_CHANGE_DEPTH", 10);
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CAP_CONTROL_RATE_OF_CHANGE_DEPTH: " << _RATE_OF_CHANGE_DEPTH << endl;
    }


    _VOLT_REDUCTION_SYSTEM_POINTID = 0; // pointid

    strcpy(var, "CAP_CONTROL_VOLT_REDUCTION_SYSTEM_POINTID");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        _VOLT_REDUCTION_SYSTEM_POINTID = atol(str.c_str());
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << var << ":  " << str << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    _AUTO_VOLT_REDUCTION = false;

    strcpy(var, "CAP_CONTROL_AUTO_VOLT_REDUCTION");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        CtiToLower(str);
        _AUTO_VOLT_REDUCTION = (str == "true" ? true : false);
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << var << ":  " << str << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    _VOLT_REDUCTION_COMMANDS = 0; // number of command/retries for disable ovuv

    strcpy(var, "CAP_CONTROL_VOLT_REDUCTION_COMMANDS");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        _VOLT_REDUCTION_COMMANDS = atoi(str.c_str());
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << var << ":  " << str << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    _VOLT_REDUCTION_COMMAND_DELAY = 0;  //delay between commands sent.

    strcpy(var, "CAP_CONTROL_VOLT_REDUCTION_COMMAND_DELAY");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        _VOLT_REDUCTION_COMMAND_DELAY = atoi(str.c_str());
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << var << ":  " << str << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
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
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << var << ":  " << str << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    _ENABLE_IVVC = false;

    strcpy(var, "CAP_CONTROL_ENABLE_IVVC");
    if ( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        CtiToLower(str);
        _ENABLE_IVVC = (str == "true" ? true : false);
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << var << ":  " << str << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    _IVVC_MIN_TAP_PERIOD_MINUTES = gConfigParms.getValueAsULong("CAP_CONTROL_IVVC_MIN_TAP_PERIOD_MINUTES", 15);
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CAP_CONTROL_IVVC_MIN_TAP_PERIOD_MINUTES: " << _IVVC_MIN_TAP_PERIOD_MINUTES << endl;
    }

    _IVVC_COMMS_RETRY_COUNT = gConfigParms.getValueAsULong("CAP_CONTROL_IVVC_COMMS_RETRY_COUNT", 3);
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CAP_CONTROL_IVVC_COMMS_RETRY_COUNT: " << _IVVC_COMMS_RETRY_COUNT << endl;
    }

    _IVVC_NONWINDOW_MULTIPLIER = gConfigParms.getValueAsDouble("CAP_CONTROL_IVVC_NONWINDOW_MULTIPLIER", 1.5);
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CAP_CONTROL_IVVC_NONWINDOW_MULTIPLIER: " << _IVVC_NONWINDOW_MULTIPLIER << endl;
    }

    _IVVC_BANKS_REPORTING_RATIO = gConfigParms.getValueAsULong("CAP_CONTROL_IVVC_BANKS_REPORTING_RATIO", 100) / 100.0; //store as 0-1.0
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CAP_CONTROL_IVVC_BANKS_REPORTING_RATIO: " << _IVVC_BANKS_REPORTING_RATIO * 100 << endl;
    }

    _IVVC_REGULATOR_REPORTING_RATIO = gConfigParms.getValueAsULong("CAP_CONTROL_IVVC_REGULATOR_REPORTING_RATIO", 100) / 100.0; //store as 0-1.0
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CAP_CONTROL_IVVC_REGULATOR_REPORTING_RATIO: " << _IVVC_REGULATOR_REPORTING_RATIO * 100 << endl;
    }

    _IVVC_VOLTAGEMONITOR_REPORTING_RATIO = gConfigParms.getValueAsULong("CAP_CONTROL_IVVC_VOLTAGEMONITOR_REPORTING_RATIO", 100) / 100.0; //store as 0-1.0
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CAP_CONTROL_IVVC_VOLTAGEMONITOR_REPORTING_RATIO: " << _IVVC_VOLTAGEMONITOR_REPORTING_RATIO * 100 << endl;
    }

    _IVVC_DEFAULT_DELTA = gConfigParms.getValueAsDouble("CAP_CONTROL_IVVC_DEFAULT_DELTA", 0);
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CAP_CONTROL_IVVC_DEFAULT_DELTA: " << _IVVC_DEFAULT_DELTA << endl;
    }
    _LIMIT_ONE_WAY_COMMANDS = gConfigParms.isTrue("CAP_CONTROL_LIMIT_ONE_WAY_COMMANDS", false);
    if ( _CC_DEBUG & CC_DEBUG_STANDARD)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CAP_CONTROL_LIMIT_ONE_WAY_COMMANDS: " << _LIMIT_ONE_WAY_COMMANDS << endl;
    }

    _IVVC_STATIC_DELTA_VOLTAGES = gConfigParms.isTrue("CAP_CONTROL_IVVC_STATIC_DELTA_VOLTAGES");
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CAP_CONTROL_IVVC_STATIC_DELTA_VOLTAGES: " << _IVVC_STATIC_DELTA_VOLTAGES << endl;
    }

    _IVVC_INDIVIDUAL_DEVICE_VOLTAGE_TARGETS = gConfigParms.isTrue("CAP_CONTROL_IVVC_INDIVIDUAL_DEVICE_VOLTAGE_TARGETS");
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CAP_CONTROL_IVVC_INDIVIDUAL_DEVICE_VOLTAGE_TARGETS: " << _IVVC_INDIVIDUAL_DEVICE_VOLTAGE_TARGETS << endl;
    }
}

