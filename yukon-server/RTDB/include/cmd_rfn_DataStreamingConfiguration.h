#pragma once

#include "ctidate.h"
#include "cmd_rfn.h"

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB RfnDataStreamingConfigurationCommand : public RfnCommand
{
public:
    RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload & response) override;

    enum StreamingState : unsigned char
    {
        StreamingDisabled,
        StreamingEnabled
    };

    struct MetricState 
    {
        unsigned short metricId;
        unsigned char interval;
        StreamingState enabled;
    };

protected:

    virtual unsigned char getResponseCode() const = 0;

    std::vector<MetricState> _deviceMetrics;
    StreamingState _deviceStreamingState;

private:
    unsigned char getOperation() const override;
    Bytes getCommandHeader() override;
};


class IM_EX_DEVDB RfnDataStreamingGetMetricsListCommand : public RfnDataStreamingConfigurationCommand, NoResultHandler
{
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
    RfnDataStreamingSetMetricsCommand(std::vector<MetricState> states);

private:
    const StreamingState _enabled;
    const std::vector<MetricState> _states;

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
