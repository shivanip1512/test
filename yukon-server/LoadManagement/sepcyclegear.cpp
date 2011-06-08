#include "yukon.h"
#include "sepcyclegear.h"
#include "logger.h"

using std::endl;

SEPCycleGear::SEPCycleGear(Cti::RowReader &rdr)
{
    restore(rdr);
}

SEPCycleGear::SEPCycleGear(const SEPCycleGear& gear)
{
    operator=(gear);
}

CtiLMProgramDirectGear * SEPCycleGear::replicate() const
{
    return(CTIDBG_new SEPCycleGear(*this));
}

SEPCycleGear& SEPCycleGear::operator=(const SEPCycleGear& right)
{
    if( this != &right )
    {
        Inherited::operator=(right);
    }

    return *this;
}

int SEPCycleGear::operator==(const SEPCycleGear& right) const
{
    return Inherited::operator==(right);
}
int SEPCycleGear::operator!=(const SEPCycleGear& right) const
{
    return Inherited::operator!=(right);
}

void SEPCycleGear::restore(Cti::RowReader &rdr)
{
    Inherited::restore(rdr);
}


bool SEPCycleGear::attemptControl(CtiLMGroupPtr currentLMGroup, long controlSeconds, DOUBLE &expectedLoadReduced)
{
    bool isTrueCycle = (getMethodOptionType() == CtiLMProgramDirectGear::TrueCycleMethod);
    bool randomizeStart = (getFrontRampOption() == CtiLMProgramDirectGear::RandomizeRandomOptionType);
    bool randomizeStop = (getBackRampOption() == CtiLMProgramDirectGear::RandomizeRandomOptionType);

    long criticality = getMethodPeriod(); // We are using the MethodPeriod in the database to hold the criticality

    if( getPercentReduction() > 0.0 )
    {
        expectedLoadReduced += (getPercentReduction() / 100.0) * currentLMGroup->getKWCapacity();
    }
    else
    {
        expectedLoadReduced += currentLMGroup->getKWCapacity();
    }

    return currentLMGroup->sendSEPCycleControl(controlSeconds/60, getMethodRate(), criticality, isTrueCycle, randomizeStart, randomizeStop);
}

bool SEPCycleGear::stopControl(CtiLMGroupPtr currentLMGroup)
{
    if( ciStringEqual(getMethodStopType(),CtiLMProgramDirectGear::RestoreStopType ) )
    {
        currentLMGroup->sendStopControl(true);
    }
    else if( ciStringEqual(getMethodStopType(),CtiLMProgramDirectGear::StopCycleStopType ) ||
             ciStringEqual(getMethodStopType(),CtiLMProgramDirectGear::TimeInStopType ) )
    {
        currentLMGroup->sendStopControl(false);
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Invalid current gear method stop type: " << getMethodStopType() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
        return false;
    }

    return true;

}

unsigned long SEPCycleGear::estimateOffTime(long controlSeconds)
{
    return (getMethodRate()/100.0)*controlSeconds;
}
