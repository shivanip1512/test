#pragma once

#include "prot_dnp.h"

namespace Cti {
namespace Protocols {

class IM_EX_PROT DnpSlaveProtocol
{
public:

    DnpSlaveProtocol();

    void setAddresses(unsigned short dstAddr, unsigned short srcAddr);

    enum class Commands
    {
        Class1230Read,
        SetDigitalOut_Direct,
        Complete,
        Invalid
    };

    void setSlaveCommand( Commands command );
    void setSequence( int seqNumber );

    YukonError_t slaveDecode( CtiXfer &xfer );
    YukonError_t slaveGenerate( CtiXfer &xfer );
    void slaveTransactionComplete();

    bool isTransactionComplete() const;

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

        unsigned long control_offset;
        InputPointType type;
        bool online;
    };

    void addInputPoint(const input_point &ip);

private:

    std::vector<input_point> _input_point_list;

    DNP::ApplicationLayer _app_layer;
    DNP::TransportLayer   _transport;
    DNP::DatalinkLayer    _datalink;

    Commands _command;
};

}
}

