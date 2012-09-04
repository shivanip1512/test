#include "precompiled.h"
#include "BeatThePeakAlertLevel.h"

using namespace Cti::BeatThePeak;

AlertLevel::AlertLevel(std::string alertLevel)
{
    if( ciStringEqual(alertLevel, AlertLevel::BTP_RED) ||
        ciStringEqual(alertLevel, AlertLevel::BTP_YELLOW) ||
        ciStringEqual(alertLevel, AlertLevel::BTP_GREEN)  )
    {
        _alertLevel = alertLevel;
    }
    else if(ciStringEqual(alertLevel, AlertLevel::BTP_RESTORE))
    {
        _alertLevel = AlertLevel::BTP_GREEN;
    }
    else 
    {
        _alertLevel = AlertLevel::BTP_GREEN;
        throw InvalidAlertLevel(alertLevel);
    }
}

AlertLevel::AlertLevel()
{
    _alertLevel = AlertLevel::BTP_GREEN;

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

const std::string AlertLevel::BTP_GREEN = "green";
const std::string AlertLevel::BTP_YELLOW = "yellow";
const std::string AlertLevel::BTP_RED = "red";
const std::string AlertLevel::BTP_RESTORE = "restore";
