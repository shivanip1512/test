#pragma once

#include "prot_dnp.h"

namespace Cti {
namespace Protocols {

class IM_EX_PROT DNPSlaveInterface : public DNPInterface
{
    struct input_point;

    typedef DNPInterface Inherited;
    std::vector<input_point> _input_point_list;
    void addObjectBlock(DNP::ObjectBlock *objBlock);

public:

    DNPSlaveInterface();

    bool setSlaveCommand( Command command );
    void setOptions( int options, int seqNumber=0 );

    YukonError_t slaveDecode( CtiXfer &xfer );
    int slaveGenerate( CtiXfer &xfer );
    void slaveTransactionComplete();

    void addInputPoint(const input_point &ip);

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
            } din;

            struct ci_point
            {
                double value;
            } counterin;
        };

        unsigned long control_offset;
        InputPointType type;
        unsigned long expiration;
        bool online;
        bool includeTime;
        CtiTime timestamp;
    };
};

}
}

