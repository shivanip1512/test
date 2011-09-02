#pragma once

#include "dllbase.h"
#include <string>

namespace Cti {
namespace Config {

class IM_EX_CONFIG CBCStrings
{
public:
    static const std::string ActiveSettings;
    static const std::string UVClosePoint;
    static const std::string OVTripPoint;
    static const std::string OVUVControlTriggerTime;
    static const std::string AdaptiveVoltageHysteresis;
    static const std::string AdaptiveVoltageFlag;
    static const std::string EmergencyUVPoint;
    static const std::string EmergencyOVPoint;
    static const std::string EmergencyVoltageTime;
    
    static const std::string CommsLostUVClosePoint;
    static const std::string CommsLostOVTripPoint;
    static const std::string CommsLostTime;
    static const std::string CommsLostAction;
    
    static const std::string FaultCurrentSetPoint;
    static const std::string StateChangeSetPoint;
    static const std::string NeutralCurrentRetryCount;
    
    static const std::string FaultDetectionActive;
    static const std::string AI1AverageTime;
    static const std::string AI2AverageTime;
    static const std::string AI3AverageTime;
    static const std::string AI1PeakSamples;
    static const std::string AI2PeakSamples;
    static const std::string AI3PeakSamples;
    static const std::string AI1RatioThreshold;
    static const std::string AI2RatioThreshold;
    static const std::string AI3RatioThreshold;
    static const std::string BatteryOnTime;
    
    static const std::string Season1Start;
    static const std::string WeekdayTimedControlClose1;
    static const std::string WeekendTimedControlClose1;
    static const std::string WeekdayTimedControlTrip1;
    static const std::string WeekendTimedControlTrip1;
    static const std::string OffTimeState1;
    static const std::string TempMinThreshold1;
    static const std::string TempMinThresholdAction1;
    static const std::string TempMinHysterisis1;
    static const std::string TempMinThresholdTrigTime1;
    static const std::string TempMaxThreshold1;
    static const std::string TempMaxThresholdAction1;
    static const std::string TempMaxHysterisis1;
    static const std::string TempMaxThresholdTrigTime1;
    
    static const std::string Season2Start;
    static const std::string WeekdayTimedControlClose2;
    static const std::string WeekendTimedControlClose2;
    static const std::string WeekdayTimedControlTrip2;
    static const std::string WeekendTimedControlTrip2;
    static const std::string OffTimeState2;
    static const std::string TempMinThreshold2;
    static const std::string TempMinThresholdAction2;
    static const std::string TempMinHysterisis2;
    static const std::string TempMinThresholdTrigTime2;
    static const std::string TempMaxThreshold2;
    static const std::string TempMaxThresholdAction2;
    static const std::string TempMaxHysterisis2;
    static const std::string TempMaxThresholdTrigTime2;
    
    static const std::string ContactClosureTime;
    static const std::string ManualControlDelayTrip;
    static const std::string ManualControlDelayClose;
    static const std::string RecloseDelayTime;
    
    static const std::string DataLogFlags;
    static const std::string LogTimeInterval;
    
    static const std::string Geo;
    static const std::string Substation;
    static const std::string Feeder;
    static const std::string Zip;
    static const std::string UserDefined;
    static const std::string Program;
    static const std::string Splinter;
    static const std::string RequiredAddressLevel;
    
    static const std::string LineVoltageDeadBand;
    static const std::string DeltaVoltageDeadBand;
    static const std::string AnalogDeadBand;
    
    static const std::string RetryDelay;
    static const std::string PollTimeout;
};

}
}
