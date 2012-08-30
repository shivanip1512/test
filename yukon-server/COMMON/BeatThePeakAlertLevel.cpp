#include "precompiled.h"
#include "BeatThePeakAlertLevel.h"

using namespace Cti::BeatThePeak;

AlertLevel::AlertLevel(std::string alertLevel)
{
    if( ciStringEqual(alertLevel, BTP_RED) ||
        ciStringEqual(alertLevel, BTP_YELLOW) ||
        ciStringEqual(alertLevel, BTP_GREEN)  )
    {
        _alertLevel = alertLevel;
    }
    else if(ciStringEqual(alertLevel, BTP_RESTORE))
    {
        _alertLevel = BTP_GREEN;
    }
    else 
    {
        _alertLevel = BTP_GREEN;
        throw InvalidAlertLevel(alertLevel);
    }
}

AlertLevel::AlertLevel()
{
    _alertLevel = BTP_GREEN;

}

std::string AlertLevel::asString()
{
    return _alertLevel;

}

int AlertLevel::operator==(const AlertLevel& right) const
{
    return ciStringEqual(_alertLevel, right._alertLevel);
}

int AlertLevel::operator!=(const AlertLevel& right) const
{
    return ! operator==(right);
}
