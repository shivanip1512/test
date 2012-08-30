#include "precompiled.h"
#include "lmprogrambeatthepeakgear.h"
#include "BeatThePeakControlInterface.h"
#include "logger.h"
#include "BeatThePeakAlertLevel.h"

using std::endl;
using namespace Cti::LoadManagement;
using Cti::LoadManagement::BeatThePeakControlInterface;

CtiLMProgramBeatThePeakGear::CtiLMProgramBeatThePeakGear(Cti::RowReader &rdr)
{
    restore(rdr);
}

CtiLMProgramDirectGear * CtiLMProgramBeatThePeakGear::replicate() const
{
    return(CTIDBG_new CtiLMProgramBeatThePeakGear(*this));
}

int CtiLMProgramBeatThePeakGear::operator==(const CtiLMProgramBeatThePeakGear& right) const
{
    return Inherited::operator==(right) && _alertLevel == right._alertLevel;
}
int CtiLMProgramBeatThePeakGear::operator!=(const CtiLMProgramBeatThePeakGear& right) const
{
    return Inherited::operator!=(right) || _alertLevel != right._alertLevel;
}

void CtiLMProgramBeatThePeakGear::restore(Cti::RowReader &rdr)
{
    Inherited::restore(rdr);
    std::string temp;
    rdr["alertlevel"] >> temp;
    try
    {
        _alertLevel = Cti::BeatThePeak::AlertLevel(temp);
    }
    catch (Cti::BeatThePeak::AlertLevel::InvalidAlertLevel & invalidLevel)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - " << invalidLevel.what() << ". Setting to Green." << endl;
        _alertLevel = Cti::BeatThePeak::AlertLevelGreen;
    }

}

bool CtiLMProgramBeatThePeakGear::attemptControl(CtiLMGroupPtr currentLMGroup, long controlSeconds, DOUBLE &expectedLoadReduced)
{
    if( BeatThePeakControlInterfacePtr controllableGroup = boost::dynamic_pointer_cast<BeatThePeakControlInterface>(currentLMGroup) )
    {
        long controlMinutes = controlSeconds / 60;
        long controlTimeout = getMethodPeriod();
        if( 0 < controlMinutes && controlMinutes < controlTimeout )
        {
            controlTimeout = controlMinutes;
        }

        bool controlSent = controllableGroup->sendBeatThePeakControl( getAlertLevel(), controlTimeout );
        if( getMethodRate() == 0 )
        {
            currentLMGroup->setNextControlTime( CtiTime(CtiTime::pos_infin) );
        }
        else{
            currentLMGroup->setNextControlTime( CtiTime().seconds() + getMethodRate() );
        }
        return controlSent;
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Group does not implement Beat the Peak control interface: " << currentLMGroup->getPAOName() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
        return false;
    }
}

bool CtiLMProgramBeatThePeakGear::stopControl(CtiLMGroupPtr currentLMGroup)
{
    if( BeatThePeakControlInterfacePtr controllableGroup = boost::dynamic_pointer_cast<BeatThePeakControlInterface>(currentLMGroup) )
    {
        controllableGroup->sendBeatThePeakRestore();

    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Group does not implement Beat the Peak control interface: " << currentLMGroup->getPAOName() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
        return false;
    }

    return true;

}

unsigned long CtiLMProgramBeatThePeakGear::estimateOffTime(long controlSeconds)
{
    if( getAlertLevel() == Cti::BeatThePeak::AlertLevelRed || getAlertLevel() == Cti::BeatThePeak::AlertLevelYellow )
    {
        return controlSeconds;
    }
    return 0;
}

Cti::BeatThePeak::AlertLevel CtiLMProgramBeatThePeakGear::getAlertLevel() const
{
    return _alertLevel;
}
