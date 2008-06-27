#include "yukon.h"

#include "CCU710Manager.h"
#include "logger.h"

DLLIMPORT extern CtiLogger   dout;

CCU710Manager::CCU710Manager(CTINEXUS* s) : SimulatedCCU(s)
{}

bool CCU710Manager::validateRequest(unsigned char req) {
    return req != 0x00;
}

void CCU710Manager::processRequest(unsigned long addressFound)
{
    ccu.setStrategy(getStrategy());
    ccu.handleRequest(getSocket());
}