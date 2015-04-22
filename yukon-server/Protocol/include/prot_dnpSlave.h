#pragma once

#include "dnp_application.h"
#include "dnp_object_binaryoutput.h"

namespace Cti {
namespace Protocols {

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

    enum InputPointType
    {
        AnalogInputType,
        DigitalInput,
        Counters
    };

    struct input_point
    {
        union
        {
            struct ai_point
            {
                double value;
            } ain;

            struct di_point
            {
                DNP::BinaryOutputControl::ControlCode control;
                DNP::BinaryOutputControl::TripClose trip_close;
                unsigned long on_time;
                unsigned long off_time;
                bool queue;
                bool clear;
                unsigned char count;
                DNP::BinaryOutputControl::Status status;
            } din;

            struct ci_point
            {
                double value;
            } counterin;
        };

        unsigned long offset;
        InputPointType type;
        bool online;
    };

    std::vector<unsigned char> createDatalinkConfirmation();
    std::vector<unsigned char> createDatalinkAck();

    std::pair<Commands, DNP::ObjectBlockPtr> identifyRequest(const char* data, unsigned int size);
    void setScanCommand( std::vector<input_point> inputPoints );
    void setControlCommand( const DNP::ObjectBlock &ob, const DNP::BinaryOutputControl::Status status );

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

}
}

