#pragma once

#include "dnp_application.h"
#include "dnp_object_binaryoutput.h"

namespace Cti {
namespace Protocols {

namespace DnpSlave {
    struct output_point;
    struct output_analog;
    struct output_digital;
    struct output_counter;
}

class IM_EX_PROT DnpSlaveProtocol
{
public:

    enum class Commands
    {
        Class1230Read,
        SetDigitalOut_Direct,
        LinkStatus,
        ResetLink,
        Complete,
        Invalid
    };

    std::vector<unsigned char> createDatalinkConfirmation();
    std::vector<unsigned char> createDatalinkAck();

    std::pair<Commands, DNP::ObjectBlockPtr> identifyRequest(const char* data, unsigned int size);
    void setScanCommand( std::vector<std::unique_ptr<DnpSlave::output_point>> outputPoints );
    void setControlCommand( const DnpSlave::output_digital point );

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
    using analog_handler  = std::function<void (const output_analog  &)>;
    using counter_handler = std::function<void (const output_counter &)>;
    using digital_handler = std::function<void (const output_digital &)>;

    virtual void handle(const output_analog  &) = 0;
    virtual void handle(const output_counter &) = 0;
    virtual void handle(const output_digital &) = 0;
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

    DNP::BinaryOutputControl::ControlCode control;
    DNP::BinaryOutputControl::TripClose trip_close;
    unsigned long on_time;
    unsigned long off_time;
    bool queue;
    bool clear;
    unsigned char count;
    DNP::BinaryOutputControl::Status status;

    bool isLongIndexed;
};

struct output_counter : output_point
{
    void identify(OutputPointHandler &h) override { h.handle(*this); }

    double value;
};

}

}
}

