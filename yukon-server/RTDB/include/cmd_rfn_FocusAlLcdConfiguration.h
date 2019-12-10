#pragma once

#include "cmd_rfn_Individual.h"

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB RfnFocusAlLcdConfigurationCommand : public RfnTwoWayCommand
{
public:
    enum Metrics
    {
        deliveredKwh6x1,
        deliveredKwh5x1,
        deliveredKwh4x1,
        deliveredKwh4x10,
        reverseKwh6x1,
        reverseKwh5x1,
        reverseKwh4x1,
        receivedKwh4x10,
        totalKwh6x1,
        totalKwh5x1,
        totalKwh4x1,
        totalKwh4x10,
        netKwh6x1,
        netKwh5x1,
        netKwh4x1,
        netKwh4x10,
        diagnosticFlags,
        allSegments,
        firmwareVersion
    };

    typedef std::vector<Metrics> MetricVector;

protected:
    virtual unsigned char getCommandCode() const;
    virtual unsigned char getOperation() const = 0;
    virtual Bytes         getCommandData() = 0;
};

class IM_EX_DEVDB RfnFocusAlLcdConfigurationReadCommand : public RfnFocusAlLcdConfigurationCommand,
       InvokerFor<RfnFocusAlLcdConfigurationReadCommand>
{
public:
    boost::optional<MetricVector> getDisplayItemsReceived() const;
    boost::optional<unsigned char> getDisplayItemDurationReceived() const;

    RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload &response) override;
    std::string getCommandName() override;

protected:
    virtual unsigned char getOperation() const;
    virtual Bytes         getCommandData();

private:
    boost::optional<MetricVector>   _displayItemsReceived;
    boost::optional<unsigned char>  _displayItemDurationReceived;
};

class IM_EX_DEVDB RfnFocusAlLcdConfigurationWriteCommand : public RfnFocusAlLcdConfigurationCommand,
       InvokerFor<RfnFocusAlLcdConfigurationWriteCommand>
{
public:
    RfnFocusAlLcdConfigurationWriteCommand( const MetricVector &metrics_, const unsigned char displayItemDuration_ );

    RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload &response) override;
    std::string getCommandName() override;

    const MetricVector metrics;
    const unsigned char displayItemDuration;

protected:
    virtual unsigned char getOperation() const;
    virtual Bytes         getCommandData();
};


}
}
}
