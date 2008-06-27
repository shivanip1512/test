#ifndef  __711MANAGER_CCU_H__
#define  __711MANAGER_CCU_H__

#include "ctinexus.h"
#include "SimulatedCCU.h"
#include "CCU711.h"

class CCU711Manager : public SimulatedCCU
{
    public:
        CCU711Manager(CTINEXUS* s = NULL);

        virtual void processRequest(unsigned long addressFound);
        virtual bool validateRequest(unsigned char req); 

    private:
        std::map <int, CCU711*> ccuList;
};
#endif
