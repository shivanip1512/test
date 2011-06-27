#pragma once

#include "CommInterface.h"
#include "portlogger.h"

namespace Cti {
namespace Simulator {

class PlcTransmitter
{
protected:

    enum BitTimings
    {
        BitLength_TransmitOverhead = 23,

//  TODO-P4: Add case for 9.6 transmit speed?
//  TODO-P4: Add arbitrary slowdowns?
        BitTime_usec = 13121  //  1000000 sec / 71.26 bps, assumes 12.5 kHz
    };

    static unsigned dlc_time(unsigned bits);
    static unsigned dlc_time(unsigned bits_out, unsigned bits_in);
public:
    virtual bool handleRequest   (Comms & comms, Logger &logger) = 0;
};

}
}
