#pragma once

#include "dnp_application.h"
#include "dnp_object_analogoutput.h"
#include "dnp_object_binaryoutput.h"

namespace Cti {
namespace Protocols {

namespace DnpSlave {
    struct output_point;

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
        WriteTime,
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
    void setScanCommand( std::vector<DnpSlave::output_point> outputPoints );
    void setControlCommand( const DnpSlave::control_request &control );
    void setAnalogOutputCommand( const DnpSlave::analog_output_request &analog );
    void setDelayMeasurementCommand( const std::chrono::milliseconds delay );
    void setWriteTimeCommand();
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

enum class PointType
{
    AnalogInput,
    AnalogOutput,
    BinaryInput,
    BinaryOutput,
    Accumulator,
    DemandAccumulator
};
    
struct output_point
{
    output_point(unsigned long offset_, bool online_, PointType type_, double value_)
        :   offset(offset_),
            online(online_),
            value (value_),
            type  (type_)
    {}

    unsigned long offset;
    bool online;
    double value;
    PointType type;
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

