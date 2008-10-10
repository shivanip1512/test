#ifndef  __710MANAGER_CCU_H__
#define  __710MANAGER_CCU_H__

#include "ctinexus.h"
#include "CCU710.h"
#include "SimulatedCCU.h"

class CCU710Manager : public SimulatedCCU
{
    public:
        CCU710Manager(CTINEXUS *socket, int strategy);

        virtual void processRequest (unsigned long ccu_address);
        virtual bool validateRequest(unsigned char req);

    private:
        CCU710 ccu;
};
#endif
