#pragma once

#include "dnp_application.h"
#include "dnp_object_analogoutput.h"
#include "dnp_object_binaryoutput.h"

namespace Cti {
namespace Protocols {

namespace DnpSlave {
    struct output_point;
    struct output_analog;
    struct output_digital;
    struct output_accumulator;
    struct output_demand_accumulator;

    struct analog_output_request;
    struct control_request;
}

class IM_EX_PROT DnpSlaveProtocol
{
public:

    enum class Commands
    {
        Class1230Read,
        SetAnalogOut_Select,
        SetAnalogOut_Operate,
        SetAnalogOut_Direct,
        SetDigitalOut_Select,
        SetDigitalOut_Operate,
        SetDigitalOut_Direct,
        DelayMeasurement,
        UnsolicitedEnable,
        UnsolicitedDisable,
        LinkStatus,
        ResetLink,
        Complete,
        Unsupported,
        Invalid
    };

    std::vector<unsigned char> createDatalinkConfirmation();
    std::vector<unsigned char> createDatalinkAck();

    std::pair<Commands, DNP::ObjectBlockPtr> identifyRequest(const char* data, unsigned int size);
    void setScanCommand( std::vector<std::unique_ptr<DnpSlave::output_point>> outputPoints );
    void setControlCommand( const DnpSlave::control_request &control );
    void setAnalogOutputCommand( const DnpSlave::analog_output_request &analog );
    void setDelayMeasurementCommand();
    void setUnsupportedCommand();
    void setUnsolicitedDisableCommand();
    void setUnsolicitedEnableCommand();

    YukonError_t decode( CtiXfer &xfer );
    YukonError_t generate( CtiXfer &xfer );
    void setTransactionComplete();

    bool isTransactionComplete() const;

    unsigned short getSrcAddr() const;
    unsigned short getDstAddr() const;

private:

    DNP::ApplicationLayer _application;
    DNP::TransportLayer   _transport;
    DNP::DatalinkLayer    _datalink { DNP::DatalinkLayer::Slave() };

    Commands _command;
};

namespace DnpSlave {

struct OutputPointHandler
{
    using analog_handler                = std::function<void (const output_analog  &)>;
    using digital_handler               = std::function<void (const output_digital &)>;
    using accumulator_handler           = std::function<void (const output_accumulator &)>;
    using demand_accumulator_handler    = std::function<void (const output_demand_accumulator &)>;

    virtual void handle(const output_analog  &) = 0;
    virtual void handle(const output_digital &) = 0;
    virtual void handle(const output_accumulator &) = 0;
    virtual void handle(const output_demand_accumulator &) = 0;
};

struct output_point
{
    virtual ~output_point() = default;

    unsigned long offset;
    bool online;

    virtual void identify(OutputPointHandler &) = 0;
};

struct output_analog : output_point
{
    void identify(OutputPointHandler &h) override { h.handle(*this); }

    double value;
};

struct output_digital : output_point
{
    void identify(OutputPointHandler &h) override { h.handle(*this); }

    bool status;
};

struct output_accumulator : output_point
{
    void identify(OutputPointHandler &h) override { h.handle(*this); }

    double value;
};

struct output_demand_accumulator : output_point
{
    void identify(OutputPointHandler &h) override { h.handle(*this); }

    double value;
};

enum class ControlAction
{
    Select,
    Operate,
    Direct
};

struct control_request
{
    unsigned long offset;

    DNP::BinaryOutputControl::ControlCode control;
    DNP::BinaryOutputControl::TripClose trip_close;
    unsigned long on_time;
    unsigned long off_time;
    bool queue;
    bool clear;
    unsigned char count;
    DNP::ControlStatus status;

    ControlAction action;

    bool isLongIndexed;
};

struct analog_output_request
{
    unsigned long offset;

    double value;
    DNP::AnalogOutput::Variation type;
    DNP::ControlStatus status;

    ControlAction action;

    bool isLongIndexed;
};

}

}
}

