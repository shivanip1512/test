
#include "yukon.h"
#include "septempoffsetgear.h"
#include "ctistring.h"
#include "logger.h"

SEPTemperatureOffsetGear::SEPTemperatureOffsetGear(Cti::RowReader &rdr) : 
CtiLMProgramThermoStatGear(rdr)
{
}

SEPTemperatureOffsetGear::SEPTemperatureOffsetGear(const SEPTemperatureOffsetGear& gear) :
CtiLMProgramThermoStatGear(gear)
{
}

CtiLMProgramDirectGear * SEPTemperatureOffsetGear::replicate() const
{
    return(CTIDBG_new SEPTemperatureOffsetGear(*this));
}

SEPTemperatureOffsetGear& SEPTemperatureOffsetGear::operator=(const SEPTemperatureOffsetGear& right)
{
    if( this != &right )
    {
        Inherited::operator=(right);
    }

    return *this;
}

int SEPTemperatureOffsetGear::operator==(const SEPTemperatureOffsetGear& right) const
{
    return Inherited::operator==(right);
}
int SEPTemperatureOffsetGear::operator!=(const SEPTemperatureOffsetGear& right) const
{
    return Inherited::operator!=(right);
}

void SEPTemperatureOffsetGear::restore(Cti::RowReader &rdr)
{
    Inherited::restore(rdr);
}


bool SEPTemperatureOffsetGear::attemptControl(CtiLMGroupPtr currentLMGroup, long controlSeconds, DOUBLE &expectedLoadReduced)
{
    bool randomizeStart = (getFrontRampOption() == CtiLMProgramDirectGear::RandomizeRandomOptionType);
    bool randomizeStop = (getBackRampOption() == CtiLMProgramDirectGear::RandomizeRandomOptionType);

    bool isCelsius = CtiString(getSettings()).contains("c", CtiString::ignoreCase);

    int heatOffset = 0; //TODO FIX_ME JESS
    int coolOffset = 0;

    long criticality = getMethodPeriod(); // We are using the MethodPeriod in the database to hold the criticality

    if( getPercentReduction() > 0.0 )
    {
        expectedLoadReduced += (getPercentReduction() / 100.0) * currentLMGroup->getKWCapacity();
    }
    else
    {
        expectedLoadReduced += currentLMGroup->getKWCapacity();
    }

    return currentLMGroup->sendSEPTempOffsetControl(controlSeconds/60, heatOffset, coolOffset, isCelsius, criticality, randomizeStart, randomizeStop);
}

bool SEPTemperatureOffsetGear::stopControl(CtiLMGroupPtr currentLMGroup)
{
    if( !stringCompareIgnoreCase(getMethodStopType(),CtiLMProgramDirectGear::RestoreStopType ) )
    {
        currentLMGroup->sendStopControl(true);
    }
    else if( !stringCompareIgnoreCase(getMethodStopType(),CtiLMProgramDirectGear::StopCycleStopType ) ||
             !stringCompareIgnoreCase(getMethodStopType(),CtiLMProgramDirectGear::TimeInStopType ) )
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

unsigned long SEPTemperatureOffsetGear::estimateOffTime(long controlSeconds)
{
    return controlSeconds;
}
