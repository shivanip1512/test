#pragma once

#include "ctidate.h"
#include "cmd_rfn.h"

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB RfnDataStreamingConfigurationCommand : public RfnCommand
{
public:
    enum StreamingState : unsigned char
    {
        StreamingDisabled,
        StreamingEnabled
    };

    struct MetricState 
    {
        unsigned short metricId;
        unsigned char interval;  //  interval of 0 means disabled
    };

    using MetricList = std::vector<MetricState>;

protected:

    struct ConfigResponse
    {
        bool streamingEnabled;
        struct MetricConfiguration
        {
            uint16_t metricId;
            bool enabled;
            uint8_t interval;
            uint8_t status;
        };
        std::vector<MetricConfiguration> metrics;
        uint32_t sequence;
    };
    
    ConfigResponse decodeConfigResponse(const RfnResponsePayload & response) const;
    static std::string createJson(const ConfigResponse& response);

    virtual unsigned char getResponseCode() const = 0;

    MetricList _deviceMetrics;
    StreamingState _deviceStreamingState;

private:
    unsigned char getOperation() const override;
    Bytes getCommandHeader() override;
};


class IM_EX_DEVDB RfnDataStreamingGetMetricsListCommand : public RfnDataStreamingConfigurationCommand, NoResultHandler
{
public:
    RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload & response) override;

private:
    enum
    {
        CommandCode_Request = 0x84,
        CommandCode_Response = 0x85,
    };

    unsigned char getCommandCode() const override;
    Bytes getCommandData() override;

    unsigned char getResponseCode() const override;
};


class IM_EX_DEVDB RfnDataStreamingSetMetricsCommand : public RfnDataStreamingConfigurationCommand, NoResultHandler
{
public:
    RfnDataStreamingSetMetricsCommand(StreamingState enabled);
    RfnDataStreamingSetMetricsCommand(MetricList&& states);

    RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload & response) override;

private:
    const StreamingState _enabled;
    const MetricList _states;

    enum
    {
        CommandCode_Request = 0x86,
        CommandCode_Response = 0x87
    };

    unsigned char getCommandCode() const override;
    Bytes getCommandData() override;

    unsigned char getResponseCode() const override;
};

}
}
}
