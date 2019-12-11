#pragma once

#include "cmd_rfn_Individual.h"
#include "cmd_rfn_TouConfiguration.h"
#include "cmd_rfn_TemperatureAlarm.h"
#include "cmd_rfn_FocusAlLcdConfiguration.h"
#include "cmd_rfn_CentronLcdConfiguration.h"
#include "cmd_rfn_RemoteDisconnect.h"
#include "cmd_rfn_OvUvConfiguration.h"
#include "cmd_rfn_LoadProfile.h"
#include "cmd_rfn_ChannelConfiguration.h"
#include "cmd_rfn_DataStreamingConfiguration.h"

#include <boost/variant.hpp>

namespace Cti        {
namespace Devices    {
namespace Commands   {

/**
 * Channel Configuration Command
 */
class IM_EX_DEVDB RfnConfigNotificationCommand : public RfnTwoWayCommand,
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

    std::string getDataStreamingJson(DeviceTypes type) const;

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

    std::string decodeTouEnableDisable    (Bytes payload);
    std::string decodeTouSchedule         (Bytes payload);
    std::string decodeTouHoliday          (Bytes payload);
    std::string decodeDemandFreezeDay     (Bytes payload);
    std::string decodeIntervalRecordingReporting(Bytes payload);
    std::string decodeChannelSelection    (Bytes payload);
    std::string decodeDisconnect          (Bytes payload);
    std::string decodeVoltageProfile      (Bytes payload);
    std::string decodeC2sxDisplay         (Bytes payload);
    std::string decodeFocusAlDisplay      (Bytes payload);
    std::string decodeOvUvAlarm           (Bytes payload);
    std::string decodeTemperature         (Bytes payload);
    std::string decodeDataStreaming       (Bytes payload);
    std::string decodeDemandInterval      (Bytes payload);
    std::string decodeVoltageProfileStatus(Bytes payload);

public:

    boost::optional<RfnTouConfigurationCommand::TouState> touEnabled;
    boost::optional<RfnTouScheduleConfigurationCommand::Schedule> touSchedule;
    std::optional<uint8_t> demandFreezeDay;
    std::optional<RfnTouHolidayConfigurationCommand::Holidays> touHolidays;
    std::optional<RfnChannelConfigurationCommand::MetricIds> channelSelections;
    
    struct IntervalRecording : RfnChannelIntervalRecording::ActiveConfiguration
    {
        unsigned recordingInterval;
        unsigned reportingInterval;
        RfnChannelConfigurationCommand::MetricIds intervalMetrics;

        unsigned getIntervalRecordingSeconds() const override { return recordingInterval; }
        unsigned getIntervalReportingSeconds() const override { return reportingInterval; }
        RfnChannelConfigurationCommand::MetricIds getIntervalMetrics() const override { return intervalMetrics; }
    };

    std::optional<IntervalRecording> intervalRecording;

    struct Disconnect : RfnRemoteDisconnectConfigurationCommand::Read
    {
        using RDCC = RfnRemoteDisconnectConfigurationCommand;

        boost::optional<RDCC::DisconnectMode> getDisconnectMode() const override { return disconnectMode; }
        boost::optional<RDCC::Reconnect>      getReconnectParam() const override { return reconnect;      }

        boost::optional<unsigned>  getDemandInterval()    const override { return demandInterval;  }
        boost::optional<double>    getDemandThreshold()   const override { return demandThreshold; }
        boost::optional<unsigned>  getConnectDelay()      const override { return connectDelay;    }
        boost::optional<unsigned>  getMaxDisconnects()    const override { return maxDisconnects;  }
        boost::optional<unsigned>  getDisconnectMinutes() const override { return disconnectTime;  }
        boost::optional<unsigned>  getConnectMinutes()    const override { return connectTime;     }

        RDCC::DisconnectMode disconnectMode;
        RDCC::Reconnect      reconnect;

        unsigned demandInterval;
        double   demandThreshold;
        unsigned connectDelay;
        unsigned maxDisconnects;

        boost::optional<unsigned> disconnectTime;
        boost::optional<unsigned> connectTime;
    };

    std::optional<Disconnect> disconnect;

    struct VoltageProfile : RfnVoltageProfileConfigurationCommand::Read
    {
        unsigned voltageDemandInterval;
        unsigned voltageProfileInterval;

        boost::optional<unsigned> getVoltageAveragingInterval() const override { return voltageDemandInterval;  }
        boost::optional<unsigned> getLoadProfileInterval()      const override { return voltageProfileInterval; }
    };

    std::optional<VoltageProfile> voltageProfile;

    struct C2sxDisplay : RfnCentronLcdConfigurationCommand::Read
    {
        RfnCentronLcdConfigurationCommand::metric_map_t displayItems;
        boost::optional<uint8_t> lcdCycleTime;
        boost::optional<uint8_t> displayDigits;
        boost::optional<bool> disconnectDisplayDisabled;

        RfnCentronLcdConfigurationCommand::metric_map_t getDisplayMetrics() const override { return displayItems; }
        boost::optional<bool> getDisconnectDisplayDisabled()   const override { return disconnectDisplayDisabled; }
        boost::optional<unsigned char> getDigitConfiguration() const override { return displayDigits;     }
        boost::optional<unsigned char> getLcdCycleTime()       const override { return lcdCycleTime;      }
    };
    
    std::optional<C2sxDisplay> c2sxDisplay;

    struct FocusDisplay 
    {
        unsigned displayItemDuration;
        RfnFocusAlLcdConfigurationCommand::MetricVector displayItems;
    };

    std::optional<FocusDisplay> focusDisplay;

    std::optional<RfnGetOvUvAlarmConfigurationCommand::AlarmConfiguration> ovuv;

    std::optional<RfnTemperatureAlarmCommand::AlarmConfiguration> temperature;

    std::optional<RfnDataStreamingConfigurationCommand::ConfigResponse> dataStreaming;

    std::optional<std::chrono::minutes> demandInterval;

    struct VoltageProfileStatus
    {
        bool enabled;
        std::optional<CtiTime> temporaryEnd;
    };

    std::optional<VoltageProfileStatus> voltageProfileStatus;
};

}
}
}