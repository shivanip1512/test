#include "yukon.h"

#include "CCU711Manager.h"
#include "logger.h"

DLLIMPORT extern CtiLogger   dout;

CCU711Manager::CCU711Manager(CTINEXUS* s) : SimulatedCCU(s)
{}

bool CCU711Manager::validateRequest(unsigned char req) {
    return req == 0x7e;
}

void CCU711Manager::processRequest(unsigned long addressFound)
{
    CCU711* ccuPtr;

    if( ccuList.find(addressFound) == ccuList.end() )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);  
        dout << '\n' << addressFound << " is not in the map!";
        ccuPtr = new CCU711(addressFound);
        ccuList[addressFound] = ccuPtr;
        ccuPtr->setStrategy(getStrategy());
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);  
        dout << addressFound << " is in the map";
        ccuPtr = ccuList[addressFound];
    }

    ccuPtr->handleRequest(getSocket());
}
