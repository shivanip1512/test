#pragma once

#include "ctidate.h"
#include "cmd_rfn.h"

namespace Cti        {
namespace Devices    {
namespace Commands   {

class IM_EX_DEVDB RfnDataStreamingConfigurationCommand : public RfnCommand
{
public:
    virtual ~RfnDataStreamingConfigurationCommand()
    {};

    enum StreamingState : unsigned char 
    {
        StreamingDisabled,
        StreamingEnabled
    };

    struct MetricState 
    {
        unsigned short metricId;
        unsigned char interval;
        enum State 
        {
            Disabled,
            Enabled
        } 
        enabled;
    };

protected:
    MetricState _deviceMetrics;

private:
    unsigned char getOperation() const override;
    Bytes getCommandHeader() override;
};


class IM_EX_DEVDB RfnDataStreamingGetMetricsListCommand : public RfnDataStreamingConfigurationCommand,
       InvokerFor<RfnDataStreamingGetMetricsListCommand>
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
};


class IM_EX_DEVDB RfnDataStreamingSetMetricsCommand : public RfnDataStreamingConfigurationCommand,
       InvokerFor<RfnDataStreamingSetMetricsCommand>
{
public:
    RfnDataStreamingSetMetricsCommand(StreamingState enabled) : _enabled(enabled)  {}
    RfnDataStreamingSetMetricsCommand(std::vector<MetricState> states) : _enabled(StreamingEnabled), _states(states) {}

    RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload & response) override;

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
};

}
}
}
