#include "yukon.h"

#include "SimulatedCCU.h"

boost::mutex SimulatedCCU::io_mutex;

SimulatedCCU::SimulatedCCU(CTINEXUS* s) 
{
    _strategy = 0;
    _socket = s;
}

CTINEXUS* SimulatedCCU::getSocket()
{
    return _socket;
}

void SimulatedCCU::setSocket(CTINEXUS* s)
{
    _socket = s;
}

void SimulatedCCU::setStrategy(int strategy)
{
    _strategy = strategy;
}

int SimulatedCCU::getStrategy()
{
    return _strategy;
}
