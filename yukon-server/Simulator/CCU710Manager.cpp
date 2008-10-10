#include "yukon.h"

#include "CCU710Manager.h"
#include "logger.h"

DLLIMPORT extern CtiLogger   dout;

CCU710Manager::CCU710Manager(CTINEXUS *socket, int strategy) :
    SimulatedCCU(socket, strategy)
{
}

bool CCU710Manager::validateRequest(unsigned char req)
{
    return req != 0x00;
}

void CCU710Manager::processRequest(unsigned long ccu_address)
{
    ccu.setStrategy(getStrategy());
    ccu.handleRequest(getSocket());
}