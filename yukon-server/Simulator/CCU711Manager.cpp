#include "yukon.h"

#include "CCU711Manager.h"
#include "logger.h"

using namespace std;

DLLIMPORT extern CtiLogger   dout;

CCU711Manager::CCU711Manager(CTINEXUS* socket, int strategy) :
    SimulatedCCU(socket, strategy)
{
}

bool CCU711Manager::validateRequest(unsigned char req)
{
    return req == 0x7e;
}

void CCU711Manager::processRequest(unsigned long ccu_address)
{
    if( ccuList.find(ccu_address) == ccuList.end() )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << ccu_address << " is not in the map!";
        }

        CCU711 *new_ccu = new CCU711(ccu_address);
        new_ccu->setStrategy(getStrategy());

        ccuList.insert(make_pair(ccu_address, new_ccu));
    }

    ccuList[ccu_address]->handleRequest(getSocket());
}
