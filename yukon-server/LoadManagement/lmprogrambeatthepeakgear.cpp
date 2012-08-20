#include "precompiled.h"
#include "lmprogrambeatthepeakgear.h"
#include "BtpControlInterface.h"
#include "logger.h"

using std::endl;
using namespace Cti::LoadManagement;

CtiLMProgramBeatThePeakGear::CtiLMProgramBeatThePeakGear(Cti::RowReader &rdr)
{
    restore(rdr);
}

CtiLMProgramBeatThePeakGear::CtiLMProgramBeatThePeakGear(const CtiLMProgramBeatThePeakGear& gear)
{
    operator=(gear);
}

CtiLMProgramDirectGear * CtiLMProgramBeatThePeakGear::replicate() const
{
    return(CTIDBG_new CtiLMProgramBeatThePeakGear(*this));
}

CtiLMProgramBeatThePeakGear& CtiLMProgramBeatThePeakGear::operator=(const CtiLMProgramBeatThePeakGear& right)
{
    if( this != &right )
    {
        Inherited::operator=(right);
        _tier =right._tier;
    }

    return *this;
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
    if( BtpControlInterfacePtr controllableGroup = boost::dynamic_pointer_cast<BtpControlInterface>(currentLMGroup) )
    {
        long controlMinutes = controlSeconds / 60;
        long controlTimeout = getMethodPeriod();
        if( 0 < controlMinutes && controlMinutes < controlTimeout )
        {
            controlTimeout = controlMinutes;
        }

        bool controlSent = controllableGroup->sendBtpControl( getTier(), controlTimeout );
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
        dout << CtiTime() << " - Group does not implement BTP control interface: " << currentLMGroup->getPAOName() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
        return false;
    }
}

bool CtiLMProgramBeatThePeakGear::stopControl(CtiLMGroupPtr currentLMGroup)
{
    if( BtpControlInterfacePtr controllableGroup = boost::dynamic_pointer_cast<BtpControlInterface>(currentLMGroup) )
    {
        controllableGroup->sendBtpControl( 0, 0 );

    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Group does not implement basic control interface: " << currentLMGroup->getPAOName() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
        return false;
    }

    return true;

}

unsigned long CtiLMProgramBeatThePeakGear::estimateOffTime(long controlSeconds)
{
    return (getMethodRate()/100.0)*controlSeconds;
}

int CtiLMProgramBeatThePeakGear::getTier() const
{
    return _tier;
}
