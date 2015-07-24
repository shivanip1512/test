#include "precompiled.h"

#include "dbaccess.h"
#include "lmgroupexpresscom.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"
#include "ctitokenizer.h"
#include "numstr.h"
#include "BeatThePeakControlInterface.h"
#include "lmProgramThermostatGear.h"

#include <boost/algorithm/string/case_conv.hpp>
#include <boost/optional.hpp>

using namespace Cti::LoadManagement;
using std::string;
using std::endl;

using Cti::MacroOffset;

extern ULONG _LM_DEBUG;


namespace {

/*
    Looks at the internal type of thermostat gear and builds the appropriate mode string
        based on its internal settings.
        On error - returns an uninitialized optional string.  (boost::none)
*/
boost::optional<std::string> getSetPointMode( const CtiLMProgramThermostatGear & gear )
{
    // Validate

    std::string settings( boost::algorithm::to_upper_copy( gear.getSettings() ) );

    if ( settings.length() != 4 )
    {
        CTILOG_ERROR(dout, "LM Gear: " << gear.getGearName() << " - corrupted settings (invalid length): "<< settings);

        return boost::none;
    }

    const bool hasHeat = settings[ 2 ] == 'H';
    const bool hasCool = settings[ 3 ] == 'I';

    if ( gear.getControlMethod() == CtiLMProgramDirectGear::ThermostatRampingMethod )
    {
        //  settings is a string of the form '(A|D)(F|C)(H|-)(I|-)'
        //      where either 'H' or 'I' or both are required.  It must not be '??--'

        if ( ! ( hasHeat || hasCool ) )
        {
            CTILOG_ERROR(dout, "LM Gear: " << gear.getGearName() << " - missing (heat|cool) mode setting: "<< settings);

            return boost::none;
        }
    }
    else if ( gear.getControlMethod() == CtiLMProgramDirectGear::SimpleThermostatRampingMethod )
    {
        //  settings is a string of the form '--H-' or '---I'
        //      '--H-' is 'delta mode heat'
        //      '---I' is 'delta mode cool'

        if ( ! ( hasHeat ^ hasCool ) )    // either-or but not both or neither
        {
            CTILOG_ERROR(dout, "LM Gear: " << gear.getGearName() << " - invalid (heat|cool) mode setting: "<< settings);

            return boost::none;
        }

        // set our required fields
        settings[ 0 ] = 'D';    // delta
        settings[ 1 ] = 'F';    // fahrenheit
    }
    else
    {
        CTILOG_ERROR(dout, "LM Gear: " << gear.getGearName() << " - unsupported control method: "<< gear.getControlMethod());

        return boost::none;
    }

    // Build mode string

    std::string mode;

    if ( settings[ 0 ] == 'D' )
    {
        mode += " delta";
    }

    if ( settings[ 1 ] == 'C' )
    {
        mode += " celsius";
    }

    if ( hasHeat && hasCool )
    {
        mode += " mode both";
    }
    else if ( hasHeat )
    {
        mode += " mode heat";
    }
    else    // hasCool
    {
        mode += " mode cool";
    }

    return mode;
}

std::string formatXcomSetPointProfile( const CtiLMProgramThermostatGear::ProfileSettings & profile )
{
    std::ostringstream  profileFormatter;

    if ( profile.minValue )
    {
        profileFormatter << " min " << profile.minValue;
    }
    if ( profile.maxValue )
    {
        profileFormatter << " max " << profile.maxValue;
    }

    if ( profile.random )
    {
        profileFormatter << " tr " << profile.random;
    }

    if ( profile.valueTA )
    {
        profileFormatter << " ta " << profile.valueTA;
    }

    if ( profile.valueTB )
    {
        profileFormatter << " tb " << profile.valueTB;
    }
    if ( profile.valueB )
    {
        profileFormatter << " dsb " << profile.valueB;
    }

    if ( profile.valueTC )
    {
        profileFormatter << " tc " << profile.valueTC;
    }

    if ( profile.valueTD )
    {
        profileFormatter << " td " << profile.valueTD;
    }
    if ( profile.valueD )
    {
        profileFormatter << " dsd " << profile.valueD;
    }

    if ( profile.valueTE )
    {
        profileFormatter << " te " << profile.valueTE;
    }

    if ( profile.valueTF )
    {
        profileFormatter << " tf " << profile.valueTF;
    }
    if ( profile.valueF )
    {
        profileFormatter << " dsf " << profile.valueF;
    }

    return profileFormatter.str();
}

std::string formatXcomSetPointLogMessage( const std::string & mode,
                                          const CtiLMProgramThermostatGear::ProfileSettings & profile )
{
    std::ostringstream  logFormatter;

    logFormatter << mode;

    if ( profile.valueTA )
    {
        logFormatter << ", " << profile.valueTA << "m hold";
    }

    if ( profile.valueB && profile.valueTB )
    {
        logFormatter << ", " << std::showpos << profile.valueB << std::noshowpos << " over " << profile.valueTB << "m";
    }

    if ( profile.valueTC )
    {
        logFormatter << ", " << profile.valueTC << "m hold";
    }

    if ( profile.valueD && profile.valueTD )
    {
        logFormatter << ", " << std::showpos << profile.valueD << std::noshowpos << " over " << profile.valueTD << "m";
    }

    if ( profile.valueTE )
    {
        logFormatter << ", " << profile.valueTE << "m hold";
    }

    if ( profile.valueF && profile.valueTF )
    {
        logFormatter << ", " << std::showpos << profile.valueF << std::noshowpos << " over " << profile.valueTF << "m";
    }

    if ( profile.random )
    {
        logFormatter << ", " << profile.random << "m rand";
    }

    return logFormatter.str();
}

boost::optional<std::string> calculateSetPointSimpleProfileSettings( CtiLMProgramThermostatGear::ProfileSettings & profile,
                                                                     const long totalTime )
{
    LONG rampUpTime = totalTime - (profile.valueTB + profile.valueTF + profile.valueTC);

    if( rampUpTime < 60 )
    {
        return "improperly configured time, not enough ramp time.";
    }
    if ( ! profile.rampRate )
    {
        return "improperly configured ramp time/rate.";
    }

    // rampRate D is the degree/hour rate, degrees is a long because we can only send a whole number.
    LONG degrees = ( rampUpTime / 60.0 ) * profile.rampRate;
    if( std::abs( degrees ) > profile.valueD )
    {
        degrees = ( degrees < 0 ) ? -profile.valueD : profile.valueD;
    }
    profile.valueD = degrees;

    // valueTD: Control time = (total time to do X degrees) - (hold time for the final degree)
    // valueTE: Hold Time = (hold time for final degree) + any leftover time;
    // This works because of the way degrees(valueD) is calculated before we get here.
    profile.valueTD = abs( 60.0 * profile.valueD / profile.rampRate ) - abs( 60.0 / profile.rampRate );
    profile.valueTE = rampUpTime - profile.valueTD;
    profile.valueF  = -(profile.valueD + profile.valueB);

    return boost::none;
}

std::string formatXcomSetPointSimpleProfile( const CtiLMProgramThermostatGear::ProfileSettings & profile )
{
    std::ostringstream  simpleProfileFormatter;

    if ( profile.minValue )
    {
        simpleProfileFormatter << " min " << profile.minValue;
    }
    if ( profile.maxValue )
    {
        simpleProfileFormatter << " max " << profile.maxValue;
    }

    if ( profile.random )
    {
        simpleProfileFormatter << " tr " << profile.random;
    }

    if ( profile.valueTB )
    {
        simpleProfileFormatter << " tb " << profile.valueTB;
    }
    if ( profile.valueB )
    {
        simpleProfileFormatter << " dsb " << profile.valueB;
    }

    if ( profile.valueTC )
    {
        simpleProfileFormatter << " tc " << profile.valueTC;
    }

    simpleProfileFormatter
        << " td "  << profile.valueTD
        << " dsd " << profile.valueD
        << " te "  << profile.valueTE;

    if ( profile.valueTF )
    {
        simpleProfileFormatter << " tf " << profile.valueTF;
    }

    simpleProfileFormatter << " dsf " << profile.valueF;

    return simpleProfileFormatter.str();
}

std::string formatXcomSetPointSimpleLogMessage( const std::string & mode,
                                                const CtiLMProgramThermostatGear::ProfileSettings & profile )
{
    std::ostringstream  simpleLogFormatter;

    simpleLogFormatter << mode;

    if ( profile.valueB && profile.valueTB )
    {
        simpleLogFormatter << ", " << std::showpos << profile.valueB << std::noshowpos << " over " << profile.valueTB << "m";
    }

    if ( profile.valueTC )
    {
        simpleLogFormatter << ", " << profile.valueTC << "m hold";
    }

    simpleLogFormatter << ", " << std::showpos << profile.valueD << std::noshowpos << " over " << profile.valueTD << "m";

    simpleLogFormatter << ", " << profile.valueTE << "m hold";

    if ( profile.valueTF )
    {
        simpleLogFormatter << ", " << std::showpos << profile.valueF << std::noshowpos << " over " << profile.valueTF << "m";
    }

    if ( profile.random )
    {
        simpleLogFormatter << ", " << profile.random << "m rand";
    }

    return simpleLogFormatter.str();
}

}


DEFINE_COLLECTABLE( CtiLMGroupExpresscom, CTILMGROUPEXPRESSCOM_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMGroupExpresscom::CtiLMGroupExpresscom()
{
}

CtiLMGroupExpresscom::CtiLMGroupExpresscom(Cti::RowReader &rdr)
{
    restore(rdr);
}

CtiLMGroupExpresscom::CtiLMGroupExpresscom(const CtiLMGroupExpresscom& groupexp)
{
    operator=(groupexp);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMGroupExpresscom::~CtiLMGroupExpresscom()
{
}


/*-------------------------------------------------------------------------
    createTimeRefreshRequestMsg

    Creates a new CtiRequestMsg pointer for a program gear with a control
    method of time refresh with the appropriate refresh rate and shed time.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupExpresscom::createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const
{
    string controlString("control xcom shed ");
    controlString += buildShedString(shedTime);

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Sending time refresh command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority);
    }
    return CTIDBG_new CtiRequestMsg(getPAOId(),
                                    controlString,
                                    0,
                                    0,
                                    0,
                                    MacroOffset::none,
                                    0,
                                    0,
                                    priority);
}

/*-------------------------------------------------------------------------
    createSmartCycleRequestMsg

    Creates a new CtiRequestMsg pointer for a program gear with a control
    method of smart cycle with the appropriate cycle percent, period length
    in minutes, and the default count of periods.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupExpresscom::createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const
{
    char tempchar[64];
    string controlString("control xcom cycle ");
    _ltoa(percent,tempchar,10);
    controlString += tempchar;
    controlString += " count ";
    _ltoa(defaultCount,tempchar,10);
    controlString += tempchar;
    controlString += " period ";
    controlString += buildPeriodString(period);

    if( no_ramp )
    {
        controlString += " noramp";
    }

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Sending smart cycle command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority);
    }
    return CTIDBG_new CtiRequestMsg(getPAOId(),
                                    controlString,
                                    0,
                                    0,
                                    0,
                                    MacroOffset::none,
                                    0,
                                    0,
                                    priority);
}

/*-------------------------------------------------------------------------
    createTrueCycleRequestMsg

    Creates true cycle request msg which is exactly like a smart cycle but
    with the "truecycle" string at the end of the control string.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupExpresscom::createTrueCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const
{
    char tempchar[64];
    string controlString("control xcom cycle ");
    _ltoa(percent,tempchar,10);
    controlString += tempchar;
    controlString += " count ";
    _ltoa(defaultCount,tempchar,10);
    controlString += tempchar;
    controlString += " period ";
    controlString += buildPeriodString(period);
    controlString += " truecycle";

    if( no_ramp )
    {
        controlString += " noramp";
    }

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Sending true cycle command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority);
    }
    return CTIDBG_new CtiRequestMsg(getPAOId(),
                                    controlString,
                                    0,
                                    0,
                                    0,
                                    MacroOffset::none,
                                    0,
                                    0,
                                    priority);
}

/*-------------------------------------------------------------------------
    createTargetCycleRequestMsg

    Creates true cycle request msg which is exactly like a smart cycle but
    with the "truecycle" string at the end of the control string.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupExpresscom::createTargetCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority, DOUBLE kwh, CtiTime ctrlStartTime, const string& additionalInfo) const
{
    //ctrlStartTime is now on the hour.
    ctrlStartTime = ctrlStartTime.addMinutes(-1*ctrlStartTime.minute());
    ctrlStartTime = ctrlStartTime.addSeconds(-1*ctrlStartTime.second());

    //control cycle 50 period 30 count 8 relay 1 delay 10 truecycle targetcycle 10.1 adjustments 50 60
    char tempchar[64];
    string controlString = "control cycle ";
    _ltoa(percent,tempchar,10);
    controlString += tempchar;
    controlString += " count ";
    _ltoa(defaultCount,tempchar,10);
    controlString += tempchar;
    controlString += " period ";
    controlString += buildPeriodString(period);

    if( no_ramp )
    {
        controlString += " noramp";
    }

    controlString += " truecycle";
    controlString += " targetcycle ";
    controlString += CtiNumStr(kwh, 1);

    if( additionalInfo.size() > 12 )// Magic number = length of "adjustments "
    {
        int iValue[8];
        int count = 0;

        std::string token;
        std::string temp;
        std::string str_hexnum = "(0x[0-9a-f]+)";
        std::string str_num = "([0-9]+)";
        std::string str_anynum = "(" + str_num + "|" + str_hexnum + ")";
        std::string tempStr = "adjustments";
        tempStr += "( ";
        tempStr += str_anynum;
        tempStr += ")+";
        std::string CmdStr = additionalInfo.c_str();

        if( !(token = Cti::matchRegex(CmdStr, tempStr)).empty() )
        {
            CtiTokenizer cmdtok(token);
            cmdtok(); //go past adjustment

            while( !(temp = cmdtok()).empty() )
            {
                if( count < 8 )
                {
                    iValue[count] = atoi(temp.data());
                    count++;
                }


            }
        }

        const long RoundingSeconds = 60 * 57;   // Round up on minute 58 or greater
        const long SecondsPerHour  = 60 * 60;
        int incHours = 0;
        long timeChange;

        //Are we at least 59 minutes past the start time? If not we send all of the adjustment flags
        if( (timeChange = CtiTime::now().seconds() - ctrlStartTime.seconds()) > RoundingSeconds )
        {
            incHours = timeChange / SecondsPerHour;
            if( timeChange - incHours * SecondsPerHour >= RoundingSeconds ) //Account for RoundingSeconds rounding errors
            {
                incHours ++;
            }
        }

        if( count > incHours )
        {
            controlString += " adjustments";
            for( int i = incHours; i<count; i++ )
            {
                controlString += " ";
                controlString += CtiNumStr(iValue[i]);
            }
        }
    }

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Sending target cycle command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority);
    }
    return CTIDBG_new CtiRequestMsg(getPAOId(),
                                    controlString,
                                    0,
                                    0,
                                    0,
                                    MacroOffset::none,
                                    0,
                                    0,
                                    priority);
}

// Sends a message that tells device to finish out it's current cycle then end. A "soft" stop.
// To work around FW differences between LCR's made in or after 2013 and previous LCR's this now sends a period to match the current gear period
CtiRequestMsg* CtiLMGroupExpresscom::createStopCycleMsg(LONG period, CtiTime &currentTime)
{
    const int priority = 11;
    string controlString("control terminate period ");
    controlString += buildPeriodString(period);

    CTILOG_INFO(dout, "Sending terminate to LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority);

    setLastControlString(controlString);
    setLastControlSent(currentTime);

    return new CtiRequestMsg(getPAOId(), controlString, 0, 0, 0, 0, 0, 0, priority);
}

/*-------------------------------------------------------------------------
    createRotationRequestMsg

    Creates a new CtiRequestMsg pointer for a program gear with a control
    method of rotation with the appropriate send rate and shed time.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupExpresscom::createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const
{
    string controlString("control xcom shed ");
    controlString += buildShedString(shedTime);

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Sending rotation command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority);
    }
    return CTIDBG_new CtiRequestMsg(getPAOId(),
                                    controlString,
                                    0,
                                    0,
                                    0,
                                    MacroOffset::none,
                                    0,
                                    0,
                                    priority);
}

/*-------------------------------------------------------------------------
    createMasterCycleRequestMsg

    Creates a new CtiRequestMsg pointer for a program gear with a control
    method of master cycle with the appropriate off time, period length.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupExpresscom::createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const
{
    string controlString("control xcom shed ");
    controlString += buildShedString(offTime-60);

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Sending master cycle command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority);
    }
    return CTIDBG_new CtiRequestMsg(getPAOId(),
                                    controlString,
                                    0,
                                    0,
                                    0,
                                    MacroOffset::none,
                                    0,
                                    0,
                                    priority);
}

/*-------------------------------------------------------------------------
    createThermoStatRequestMsg

    Creates a new CtiRequestMsg pointer for a thermostat program gear with
    all the appropriate settings.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupExpresscom::createSetPointRequestMsg(const CtiLMProgramThermostatGear & gear,
                                                              int priority,
                                                              std::string & logMessage) const
{
    boost::optional<std::string> mode = getSetPointMode( gear );

    if ( ! mode )
    {
        CTILOG_ERROR(dout, "LM Group " << getPAOName() << " has improperly configured thermostat gear mode.");

        return 0;
    }

    CtiLMProgramThermostatGear::ProfileSettings profile = gear.getProfileSettings();

    std::ostringstream  controlString;

    controlString
        << "control xcom setpoint"
        << *mode
        << formatXcomSetPointProfile( gear.getProfileSettings() );

    logMessage = formatXcomSetPointLogMessage( mode->substr( mode->length() - 4, 4 ),
                                               gear.getProfileSettings() );

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Sending set point command, LM Group: " << getPAOName() << ", string: " << controlString.str() << ", priority: " << priority);
    }
    return CTIDBG_new CtiRequestMsg(getPAOId(),
                                    controlString.str(),
                                    0,
                                    0,
                                    0,
                                    MacroOffset::none,
                                    0,
                                    0,
                                    priority);
}

//Create a setpoint message using slopes instead of absolute values.
//Sends a bump message only when minutesFromBegin is > 0.
CtiRequestMsg* CtiLMGroupExpresscom::createSetPointSimpleMsg(const CtiLMProgramThermostatGear & gear,
                                                             LONG totalTime,
                                                             LONG minutesFromBegin,
                                                             int priority,
                                                             std::string & logMessage) const
{
    boost::optional<std::string> mode = getSetPointMode( gear );

    if ( ! mode )
    {
        CTILOG_ERROR(dout, "LM Group " << getPAOName() << " has improperly configured thermostat gear mode.");

        return 0;
    }

    CtiLMProgramThermostatGear::ProfileSettings profile = gear.getProfileSettings();

    int errorCode;

    if ( const boost::optional<std::string> errorMessage = calculateSetPointSimpleProfileSettings( profile, totalTime ) )
    {
        CTILOG_ERROR(dout, "LM Group: " << getPAOName() << " - " << *errorMessage);

        return 0;
    }

    std::ostringstream  controlString;

    controlString
        << "control xcom setpoint"
        << *mode
        << formatXcomSetPointSimpleProfile( profile );

    if ( minutesFromBegin > 0 )
    {
        controlString << " bump stage " << minutesFromBegin;
    }

    logMessage = formatXcomSetPointSimpleLogMessage( mode->substr( mode->length() - 4, 4 ),
                                                     profile );

    if ( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Sending set point command, LM Group: " << getPAOName() << ", string: " << controlString.str() << ", priority: " << priority);
    }

    return CTIDBG_new CtiRequestMsg(getPAOId(),
                                    controlString.str(),
                                    0,
                                    0,
                                    0,
                                    MacroOffset::none,
                                    0,
                                    0,
                                    priority);
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMGroupExpresscom& CtiLMGroupExpresscom::operator=(const CtiLMGroupExpresscom& right)
{
    if( this != &right )
    {
        CtiLMGroupBase::operator=(right);
    }

    return *this;
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMGroupBase* CtiLMGroupExpresscom::replicate() const
{
    return(CTIDBG_new CtiLMGroupExpresscom(*this));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a Reader
---------------------------------------------------------------------------*/
void CtiLMGroupExpresscom::restore(Cti::RowReader &rdr)
{
    CtiLMGroupBase::restore(rdr);
}

bool CtiLMGroupExpresscom::sendBeatThePeakControl(Cti::BeatThePeak::AlertLevel level, int timeout)
{
    std::string command = std::string("control btp ") + level.asString();

    if(timeout != 0)
    {
        command += std::string(" ") + CtiNumStr(timeout).toString() + std::string("m");
    }

    return sendBeatThePeakCommandToPorter(command);
}

bool CtiLMGroupExpresscom::sendBeatThePeakRestore()
{
    return sendBeatThePeakCommandToPorter("control btp restore");
}

bool CtiLMGroupExpresscom::sendBeatThePeakCommandToPorter(std::string command)
{
    CtiRequestMsg* msg = CTIDBG_new CtiRequestMsg( getPAOId(), command );

    CtiLoadManager::getInstance()->sendMessageToPIL(msg);

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Sending BTP command: " << command);
    }

    setLastControlSent(CtiTime());

    if( getGroupControlState() != CtiLMGroupBase::ActiveState )
    {
        setControlStartTime(CtiTime());
        incrementDailyOps();
    }

    setGroupControlState(CtiLMGroupBase::ActiveState);
    return true;
}
