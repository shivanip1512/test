#include "precompiled.h"
#include "lmprogrambeatthepeakgear.h"
#include "BeatThePeakControlInterface.h"
#include "logger.h"

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
    return Inherited::operator==(right) && _tier == right._tier;
}
int CtiLMProgramBeatThePeakGear::operator!=(const CtiLMProgramBeatThePeakGear& right) const
{
    return Inherited::operator!=(right) || _tier != right._tier;
}

void CtiLMProgramBeatThePeakGear::restore(Cti::RowReader &rdr)
{
    Inherited::restore(rdr);
    rdr["tier"] >> _tier;
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

        bool controlSent = controllableGroup->sendBeatThePeakControl( getTier(), controlTimeout );
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
    if( getTier() == BeatThePeakControlInterface::Red)
    {
        return controlSeconds;
    }
    return 0;
}

BeatThePeakControlInterface::Tier CtiLMProgramBeatThePeakGear::getTier() const
{
    switch(_tier)
    {
    case 0:
        return BeatThePeakControlInterface::Green;
    case 2:
        return BeatThePeakControlInterface::Yellow;
    case 4:
        return BeatThePeakControlInterface::Red;
    default:
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << " **** EXCEPTION Checkpoint **** Invalid tier: " << _tier << ". Setting to Green." << std::endl;
        }
        return BeatThePeakControlInterface::Green;
    }
}
