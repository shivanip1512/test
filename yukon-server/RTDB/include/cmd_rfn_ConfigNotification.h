#pragma once

#include "cmd_rfn_Individual.h"
#include "cmd_rfn_TouConfiguration.h"
#include "cmd_rfn_TemperatureAlarm.h"
#include "cmd_rfn_FocusAlLcdConfiguration.h"
#include "cmd_rfn_CentronLcdConfiguration.h"
#include "cmd_rfn_OvUvConfiguration.h"
#include "cmd_rfn_LoadProfile.h"
#include "cmd_rfn_ChannelConfiguration.h"

#include <boost/variant.hpp>

namespace Cti        {
namespace Devices    {
namespace Commands   {

/**
 * Channel Configuration Command
 */
class IM_EX_DEVDB RfnConfigNotificationCommand : public RfnIndividualCommand,
       InvokerFor<RfnConfigNotificationCommand>
{
    enum
    {
        Command_DeviceConfigurationRequest  = 0x1d,
        Command_DeviceConfigurationResponse = 0x1e
    };

public:

    RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload &response) override;
    std::string getCommandName() override;

    ASID getApplicationServiceId() const override;

    static unsigned char getUnsolicitedCommandCode();

protected:

    unsigned char getCommandCode() const override;

    unsigned char getOperation() const override;

    Bytes getCommandHeader() override;
    Bytes getCommandData()   override;

    //  Config notifications have a special TLV format
    struct TLV {
        uint16_t type;
        uint16_t length;
        std::vector<uint8_t> data;
    };
    
    std::string decodeTlvs(const std::vector<TLV> tlvs);

    std::string decodeTouEnableDisable  (Bytes payload);
    std::string decodeTouSchedule       (Bytes payload);
    std::string decodeTouHoliday        (Bytes payload);
    std::string decodeDemandFreezeDay   (Bytes payload);
    std::string decodeIntervalRecordingReporting(Bytes payload);
    std::string decodeChannelSelection  (Bytes payload);
    std::string decodeDisconnect        (Bytes payload);
    std::string decodeVoltageProfile    (Bytes payload);
    std::string decodeC2sxDisplay       (Bytes payload);
    std::string decodeFocusAlDisplay    (Bytes payload);
    std::string decodeOvUvAlarm         (Bytes payload);
    std::string decodeTemperature       (Bytes payload);

public:
    boost::optional<RfnTouConfigurationCommand::TouState> touEnabled;
    boost::optional<RfnTouScheduleConfigurationCommand::Schedule> touSchedule;
    boost::optional<uint8_t> demandFreezeDay;
    boost::optional<RfnTouHolidayConfigurationCommand::Holidays> touHolidays;
    boost::optional<RfnChannelConfigurationCommand::MetricIds> channelSelections;
    
    struct IntervalRecording : RfnChannelIntervalRecording::ActiveConfiguration
    {
        unsigned recordingInterval;
        unsigned reportingInterval;
        RfnChannelConfigurationCommand::MetricIds intervalMetrics;

        unsigned getIntervalRecordingSeconds() const override { return recordingInterval; }
        unsigned getIntervalReportingSeconds() const override { return reportingInterval; }
        RfnChannelConfigurationCommand::MetricIds getIntervalMetrics() const override { return intervalMetrics; }
    };

    boost::optional<IntervalRecording> intervalRecording;

    struct Disconnect
    {
        struct OnDemand {
            uint8_t reconnectMethod;
        };
        struct DemandThreshold {
            uint8_t reconnectMethod;
            std::chrono::minutes demandInterval;
            float demandThreshold;
            std::chrono::minutes connectDelay;
            uint8_t maxDisconnects;
        };
        struct Cycling {
            uint8_t reconnectMethod;
            std::chrono::minutes disconnectTime;
            std::chrono::minutes connectTime;
        };
        boost::variant<OnDemand, DemandThreshold, Cycling> format;
    };

    boost::optional<Disconnect> disconnect;

    struct VoltageProfile : RfnVoltageProfileConfigurationCommand::Read
    {
        unsigned voltageDemandInterval;
        unsigned voltageProfileInterval;

        boost::optional<unsigned> getVoltageAveragingInterval() const override { return voltageDemandInterval;  }
        boost::optional<unsigned> getLoadProfileInterval()      const override { return voltageProfileInterval; }
    };

    boost::optional<VoltageProfile> voltageProfile;

	struct C2sxDisplay : RfnCentronLcdConfigurationCommand::Read
	{
        RfnCentronLcdConfigurationCommand::metric_map_t displayItems;
		boost::optional<uint8_t> lcdCycleTime;
		boost::optional<uint8_t> displayDigits;
		boost::optional<bool> disconnectDisplay;

        RfnCentronLcdConfigurationCommand::metric_map_t getDisplayMetrics() const override { return displayItems; }
        boost::optional<bool> getDisconnectDisplayDisabled()   const override { return disconnectDisplay; }
        boost::optional<unsigned char> getDigitConfiguration() const override { return displayDigits;     }
        boost::optional<unsigned char> getLcdCycleTime()       const override { return lcdCycleTime;      }
	};
	
    boost::optional<C2sxDisplay> c2sxDisplay;

    struct FocusDisplay 
    {
        unsigned displayItemDuration;
        RfnFocusAlLcdConfigurationCommand::MetricVector displayItems;
    };

    boost::optional<FocusDisplay> focusDisplay;

    boost::optional<RfnGetOvUvAlarmConfigurationCommand::AlarmConfiguration> ovuv;

    struct Temperature
    {
        uint8_t enableDisable;
        uint16_t highTemperatureThreshold;
        uint16_t lowTemperatureThreshold;
        std::chrono::minutes repeatInterval;
        uint8_t maxRepeats;
    };

    boost::optional<RfnTemperatureAlarmCommand::AlarmConfiguration> temperature;
};

}
}
}