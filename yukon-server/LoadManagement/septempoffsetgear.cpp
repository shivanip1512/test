
#include "precompiled.h"
#include "septempoffsetgear.h"
#include "GroupControlInterface.h"
#include "SepControlInterface.h"
#include "ctistring.h"
#include "logger.h"

using std::endl;
using namespace Cti::LoadManagement;

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
    if( SEPControlInterfacePtr controllableGroup = boost::dynamic_pointer_cast<SEPControlInterface>(currentLMGroup) )
    {
        bool randomizeStart = (getFrontRampOption() == CtiLMProgramDirectGear::RandomizeRandomOptionType);
        bool randomizeStop = (getBackRampOption() == CtiLMProgramDirectGear::RandomizeRandomOptionType);

        bool isCelsius = CtiString(getSettings()).contains("c", CtiString::ignoreCase);

        int heatOffset = getDelayTime();
        int coolOffset = getPrecoolTime();

        long criticality = getMethodPeriod(); // We are using the MethodPeriod in the database to hold the criticality

        if( getPercentReduction() > 0.0 )
        {
            expectedLoadReduced += (getPercentReduction() / 100.0) * currentLMGroup->getKWCapacity();
        }
        else
        {
            expectedLoadReduced += currentLMGroup->getKWCapacity();
        }

        return controllableGroup->sendSEPTempOffsetControl(controlSeconds/60, heatOffset, coolOffset, isCelsius, criticality, randomizeStart, randomizeStop);
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Group does not implement SEP control interface: " << currentLMGroup->getPAOName() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
        return false;
    }
}

bool SEPTemperatureOffsetGear::stopControl(CtiLMGroupPtr currentLMGroup)
{
    if( GroupControlInterfacePtr controllableGroup = boost::dynamic_pointer_cast<GroupControlInterface>(currentLMGroup) )
    {
        if( ciStringEqual(getMethodStopType(),CtiLMProgramDirectGear::RestoreStopType ) )
        {
            controllableGroup->sendStopControl(true);
        }
        else if( ciStringEqual(getMethodStopType(),CtiLMProgramDirectGear::StopCycleStopType ) ||
                 ciStringEqual(getMethodStopType(),CtiLMProgramDirectGear::TimeInStopType ) )
        {
            controllableGroup->sendStopControl(false);
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Invalid current gear method stop type: " << getMethodStopType() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
            return false;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Group does not implement basic control interface: " << currentLMGroup->getPAOName() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
        return false;
    }

    return true;

}

unsigned long SEPTemperatureOffsetGear::estimateOffTime(long controlSeconds)
{
    return controlSeconds;
}
