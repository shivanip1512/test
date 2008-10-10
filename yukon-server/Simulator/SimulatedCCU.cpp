#include "yukon.h"

#include "SimulatedCCU.h"

boost::mutex SimulatedCCU::io_mutex;

SimulatedCCU::SimulatedCCU(CTINEXUS *socket, int strategy) :
    _socket(socket),
    _strategy(strategy)
{
}

CTINEXUS *SimulatedCCU::getSocket() const
{
    return _socket;
}

int SimulatedCCU::getStrategy() const
{
    return _strategy;
}


