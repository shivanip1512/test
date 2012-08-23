/*---------------------------------------------------------------------------
        Filename:  lmgroupexpress.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiLMGroupExpresscom.
                        CtiLMGroupExpresscom maintains the state and handles
                        the persistence of expresscom groups in Load Management.

        Initial Date:  2/9/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "precompiled.h"

#include "dbaccess.h"
#include "lmgroupexpresscom.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"
#include "ctistring.h"
#include "ctitokenizer.h"
#include "numstr.h"
#include "BeatThePeakControlInterface.h"

using namespace Cti::LoadManagement;
using std::string;
using std::endl;

extern ULONG _LM_DEBUG;
#define ROUNDING_SECONDS (60*57) //Round up on minute 58 or greater

RWDEFINE_COLLECTABLE( CtiLMGroupExpresscom, CTILMGROUPEXPRESSCOM_ID )

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
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Sending time refresh command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    return CTIDBG_new CtiRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
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
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Sending smart cycle command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    return CTIDBG_new CtiRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
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
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Sending true cycle command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    return CTIDBG_new CtiRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
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
        int incHours = 0;
        long timeChange;
        int count = 0;

        CtiString token;
        CtiString temp;
        CtiString str_hexnum = "(0x[0-9a-f]+)";
        CtiString str_num = "([0-9]+)";
        CtiString str_anynum = CtiString ("(") + str_num + CtiString("|") + str_hexnum + CtiString(")");
        CtiString tempStr = "adjustments";
        tempStr += "( ";
        tempStr += str_anynum;
        tempStr += ")+";
        CtiString CmdStr = additionalInfo.c_str();

        if( !(token = CmdStr.match(tempStr)).empty() )
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

        //Are we at least 59 minutes past the start time? If not we send all of the adjustment flags
        if( (timeChange = CtiTime::now().seconds() - ctrlStartTime.seconds()) > (ROUNDING_SECONDS) )
        {
            incHours = timeChange / (60*60);
            if( timeChange - incHours*60*60 >= ROUNDING_SECONDS ) //Account for ROUNDING_SECONDS rounding errors
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
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Sending target cycle command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    return CTIDBG_new CtiRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
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
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Sending rotation command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    return CTIDBG_new CtiRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
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
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Sending master cycle command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    return CTIDBG_new CtiRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
}

/*-------------------------------------------------------------------------
    createThermoStatRequestMsg

    Creates a new CtiRequestMsg pointer for a thermostat program gear with
    all the appropriate settings.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupExpresscom::createSetPointRequestMsg(string settings, LONG minValue, LONG maxValue,
                                                              LONG valueB, LONG valueD, LONG valueF, LONG random,
                                                              LONG valueTA, LONG valueTB, LONG valueTC, LONG valueTD,
                                                              LONG valueTE, LONG valueTF, int priority) const
{
    string controlString("control xcom setpoint ");

    std::transform(settings.begin(), settings.end(), settings.begin(), tolower);
    if( settings.length() > 0 && settings[(size_t)0]=='d' )
    {
        controlString += "delta ";
    }
    if( settings.length() > 1 && settings[(size_t)1]=='c' )
    {
        controlString += "celsius ";
    }
    if( settings.length() > 3 && settings[(size_t)2]=='h' && settings[(size_t)3]=='i' )
    {
        controlString += "mode both ";
    }
    if( settings.length() > 2 && settings[(size_t)2]=='h' )
    {
        controlString += "mode heat ";
    }
    if( settings.length() > 3 && settings[(size_t)3]=='i' )
    {
        controlString += "mode cool ";
    }

    if( minValue != 0 )
    {
        controlString += "min ";
        char tempchar[64];
        _ltoa(minValue,tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }
    if( maxValue != 0 )
    {
        controlString += "max ";
        char tempchar[64];
        _ltoa(maxValue,tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }
    if( random != 0 )
    {
        controlString += "tr ";
        char tempchar[64];
        _ltoa(random,tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }
    if( valueTA != 0 )
    {
        controlString += "ta ";
        char tempchar[64];
        _ltoa(valueTA,tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }
    if( valueTB != 0 )
    {
        controlString += "tb ";
        char tempchar[64];
        _ltoa(valueTB,tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }
    if( valueB != 0 )
    {
        controlString += "dsb ";
        char tempchar[64];
        _ltoa(valueB,tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }
    if( valueTC != 0 )
    {
        controlString += "tc ";
        char tempchar[64];
        _ltoa(valueTC,tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }
    if( valueTD != 0 )
    {
        controlString += "td ";
        char tempchar[64];
        _ltoa(valueTD,tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }
    if( valueD != 0 )
    {
        controlString += "dsd ";
        char tempchar[64];
        _ltoa(valueD,tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }
    if( valueTE != 0 )
    {
        controlString += "te ";
        char tempchar[64];
        _ltoa(valueTE,tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }
    if( valueTF != 0 )
    {
        controlString += "tf ";
        char tempchar[64];
        _ltoa(valueTF,tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }
    if( valueF != 0 )
    {
        controlString += "dsf ";
        char tempchar[64];
        _ltoa(valueF,tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Sending set point command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    return CTIDBG_new CtiRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
}

//Create a setpoint message using slopes instead of absolute values.
//Sends a bump message only when minutesFromBegin is > 0.
CtiRequestMsg* CtiLMGroupExpresscom::createSetPointSimpleMsg(string settings, LONG minValue, LONG maxValue,
                                                              LONG precoolTemp, LONG random, float rampRate,
                                                              LONG precoolTime, LONG precoolHoldTime, LONG maxTempChange,
                                                              LONG totalTime, LONG rampOutTime, LONG minutesFromBegin,
                                                              int priority) const
{
    LONG controlTime, controlHoldTime;
    LONG rampUpTime = totalTime - (precoolTime + rampOutTime + precoolHoldTime);
    bool retFlag = true;
    if( rampUpTime < 60 )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " *** CHECKPOINT *** LM Group " << getPAOName() << " improperly configured time, not enough ramp time " << endl;
        retFlag = false;
    }
    string controlString("control xcom setpoint ");
    controlString += "delta "; //Simple setpoint is always in delta format. This should really be the default!!!!!

    std::transform(settings.begin(), settings.end(), settings.begin(), tolower);
    if( settings.length() > 1 && settings[(size_t)1]=='c' )
    {
        controlString += "celsius ";
    }
    if( settings.length() > 3 && settings[(size_t)2]=='h' && settings[(size_t)3]=='i' )
    {
        controlString += "mode both ";
    }
    else if( settings.length() > 2 && settings[(size_t)2]=='h' )
    {
        controlString += "mode heat ";
        if( precoolTemp < 0 )
        {
            precoolTemp = precoolTemp*-1;
        }
        if( rampRate < 0 )
        {
            rampRate = rampRate*-1;
        }
    }
    else if( settings.length() > 3 && settings[(size_t)3]=='i' )
    {
        controlString += "mode cool ";
        if( precoolTemp > 0 )
        {
            precoolTemp = precoolTemp*-1;
        }
        if( rampRate < 0 )
        {
            rampRate = rampRate*-1;
        }
    }

    if( minValue != 0 )
    {
        controlString += "min ";
        char tempchar[64];
        _ltoa(minValue,tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }
    if( maxValue != 0 )
    {
        controlString += "max ";
        char tempchar[64];
        _ltoa(maxValue,tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }
    if( random != 0 )
    {
        controlString += "tr ";
        char tempchar[64];
        _ltoa(random,tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }
    if( precoolTime != 0 )
    {
        controlString += "tb ";
        char tempchar[64];
        _ltoa(precoolTime,tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }
    if( precoolTemp != 0 )
    {
        controlString += "dsb ";
        char tempchar[64];
        _ltoa(precoolTemp,tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }
    if( precoolHoldTime != 0 )
    {
        controlString += "tc ";
        char tempchar[64];
        _ltoa(precoolHoldTime,tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }

    int degrees = ((float)rampUpTime/60) * rampRate; //rampRate D is the degree/hour rate, degrees is an int because we can only send a whole number.

    if( degrees > maxTempChange )
    {
        degrees = maxTempChange;
    }
    if( rampUpTime != 0 && degrees != 0 )
    {
        // Control time = (total time to do X degrees) - (hold time for the final degree)
        // Hold Time = (hold time for final degree) + any leftover time;
        // This works because of the way degrees is calculated before we get here.
        controlTime = (float)degrees / rampRate * 60 - ((float)1/rampRate * 60);
        controlHoldTime = rampUpTime -controlTime;

        controlString += "td ";
        char tempchar[64];
        _ltoa(controlTime,tempchar,10);
        controlString += tempchar;
        controlString += " ";

        controlString += "dsd ";
        _ltoa(degrees,tempchar,10);
        controlString += tempchar;
        controlString += " ";

        controlString += "te ";
        _ltoa(controlHoldTime,tempchar,10);
        controlString += tempchar;
        controlString += " ";

    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " *** CHECKPOINT *** LM Group " << getPAOName() << " improperly configured ramp time/rate. " << endl;
        retFlag = false;
    }

    if( rampOutTime != 0 )
    {
        controlString += "tf ";
        char tempchar[64];
        _ltoa(rampOutTime,tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }
    if( degrees != 0 )
    {
        //Set number of degrees to count down.
        controlString += "dsf ";
        char tempchar[64];
        _ltoa((-1*degrees - precoolTemp),tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }

    if( minutesFromBegin > 0 )
    {
        controlString += "bump stage ";//these are seperate commands
        char tempchar[64];
        _ltoa(minutesFromBegin,tempchar,10);
        controlString += tempchar;
        controlString += " ";

    }

    if( retFlag && _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Sending set point command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    if( retFlag )
    {
        return CTIDBG_new CtiRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
    }
    else
    {
        return NULL;
    }
}

/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiLMGroupExpresscom::restoreGuts(RWvistream& istrm)
{
    CtiLMGroupBase::restoreGuts( istrm );
}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiLMGroupExpresscom::saveGuts(RWvostream& ostrm ) const
{
    CtiLMGroupBase::saveGuts( ostrm );
    return;
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
    operator==
---------------------------------------------------------------------------*/
int CtiLMGroupExpresscom::operator==(const CtiLMGroupExpresscom& right) const
{

    return CtiLMGroupBase::operator==(right);
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMGroupExpresscom::operator!=(const CtiLMGroupExpresscom& right) const
{

    return CtiLMGroupBase::operator!=(right);
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

bool CtiLMGroupExpresscom::sendBeatThePeakControl(Tier tier, int timeout)
{
    std::string command;
    if(tier == BeatThePeakControlInterface::Green)
    {
        command = std::string("control btp restore");
    }
    else
    {
        command = std::string("control btp ") + tierAsString(tier);

        if(timeout != 0)
        {
            command += std::string(" ") + CtiNumStr(timeout).toString() + std::string("m");
        }
    }

    CtiRequestMsg* msg = CTIDBG_new CtiRequestMsg( getPAOId(), command );

    CtiLoadManager::getInstance()->sendMessageToPIL(msg);

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Sending BTP command: " << command << endl;
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

bool CtiLMGroupExpresscom::sendBeatThePeakRestore()
{
    return sendBeatThePeakControl(BeatThePeakControlInterface::Green,0);
}
