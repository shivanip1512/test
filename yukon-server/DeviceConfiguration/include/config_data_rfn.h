#pragma once

#include "yukon.h"
#include "dllbase.h"
#include <string>

namespace Cti {
namespace Config {

struct IM_EX_CONFIG RfnStrings
{
    static const std::string displayItem01;
    static const std::string displayItem02;
    static const std::string displayItem03;
    static const std::string displayItem04;
    static const std::string displayItem05;
    static const std::string displayItem06;
    static const std::string displayItem07;
    static const std::string displayItem08;
    static const std::string displayItem09;
    static const std::string displayItem10;
    static const std::string displayItem11;
    static const std::string displayItem12;
    static const std::string displayItem13;
    static const std::string displayItem14;
    static const std::string displayItem15;
    static const std::string displayItem16;
    static const std::string displayItem17;
    static const std::string displayItem18;
    static const std::string displayItem19;
    static const std::string displayItem20;
    static const std::string displayItem21;
    static const std::string displayItem22;
    static const std::string displayItem23;
    static const std::string displayItem24;
    static const std::string displayItem25;
    static const std::string displayItem26;

    // lcd configuration parameters
    static const std::string LcdCycleTime;
    static const std::string DisconnectDisplayDisabled;
    static const std::string DisplayDigits;

    static const std::string voltageAveragingInterval;
    static const std::string demandInterval;
    static const std::string enableDataStreaming;
    static const std::string voltageDataStreamingIntervalMinutes;

    static const std::string demandFreezeDay;

    static const std::string touEnabled;

    // day table
    static const std::string MondaySchedule;
    static const std::string TuesdaySchedule;
    static const std::string WednesdaySchedule;
    static const std::string ThursdaySchedule;
    static const std::string FridaySchedule;
    static const std::string SaturdaySchedule;
    static const std::string SundaySchedule;
    static const std::string HolidaySchedule;

    // default rate
    static const std::string DefaultTouRate;

    // schedule 1
    static const std::string Schedule1Time0;
    static const std::string Schedule1Rate0;
    static const std::string Schedule1Time1;
    static const std::string Schedule1Rate1;
    static const std::string Schedule1Time2;
    static const std::string Schedule1Rate2;
    static const std::string Schedule1Time3;
    static const std::string Schedule1Rate3;
    static const std::string Schedule1Time4;
    static const std::string Schedule1Rate4;
    static const std::string Schedule1Time5;
    static const std::string Schedule1Rate5;

    // schedule 2
    static const std::string Schedule2Time0;
    static const std::string Schedule2Rate0;
    static const std::string Schedule2Time1;
    static const std::string Schedule2Rate1;
    static const std::string Schedule2Time2;
    static const std::string Schedule2Rate2;
    static const std::string Schedule2Time3;
    static const std::string Schedule2Rate3;
    static const std::string Schedule2Time4;
    static const std::string Schedule2Rate4;
    static const std::string Schedule2Time5;
    static const std::string Schedule2Rate5;

    // schedule 3
    static const std::string Schedule3Time0;
    static const std::string Schedule3Rate0;
    static const std::string Schedule3Time1;
    static const std::string Schedule3Rate1;
    static const std::string Schedule3Time2;
    static const std::string Schedule3Rate2;
    static const std::string Schedule3Time3;
    static const std::string Schedule3Rate3;
    static const std::string Schedule3Time4;
    static const std::string Schedule3Rate4;
    static const std::string Schedule3Time5;
    static const std::string Schedule3Rate5;

    // schedule 4
    static const std::string Schedule4Time0;
    static const std::string Schedule4Rate0;
    static const std::string Schedule4Time1;
    static const std::string Schedule4Rate1;
    static const std::string Schedule4Time2;
    static const std::string Schedule4Rate2;
    static const std::string Schedule4Time3;
    static const std::string Schedule4Rate3;
    static const std::string Schedule4Time4;
    static const std::string Schedule4Rate4;
    static const std::string Schedule4Time5;
    static const std::string Schedule4Rate5;

    // OV/UV Configuration
    static const std::string OvUvEnabled;
    static const std::string OvThreshold;
    static const std::string UvThreshold;
    static const std::string OvUvAlarmReportingInterval;
    static const std::string OvUvAlarmRepeatInterval;
    static const std::string OvUvRepeatCount;

    // Disconnect configuration
    static const std::string DisconnectMode;
    static const std::string ReconnectParam;
    static const std::string DisconnectDemandInterval;
    static const std::string DisconnectDemandThreshold;
    static const std::string LoadLimitConnectDelay;
    static const std::string MaxDisconnects;
    static const std::string DisconnectMinutes;
    static const std::string ConnectMinutes;

    // Temperature Alarm configuration
    static const std::string TemperatureAlarmEnabled;
    static const std::string TemperatureAlarmRepeatInterval;
    static const std::string TemperatureAlarmRepeatCount;
    static const std::string TemperatureAlarmHighTempThreshold;

    struct IM_EX_CONFIG ChannelConfiguration
    {
        static const std::string RecordingIntervalMinutes;
        static const std::string ReportingIntervalMinutes;

        static const std::string EnabledChannels_Prefix;

        struct IM_EX_CONFIG EnabledChannels
        {
            static const std::string Attribute;
            static const std::string Read;
        };
    };

    struct IM_EX_CONFIG WaterNodeConfiguration
    {
        static const std::string ReportingIntervalSeconds;
        static const std::string RecordingIntervalSeconds;
    };

    static const std::string MetrologyLibraryEnabled;
};

}
}
