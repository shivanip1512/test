/* 
Put the base class here. This class will hold the socket and 
set up the virual functions to be extended by each class. processRequest()?
*/

#ifndef  __SIMULATEDCCU_H__
#define  __SIMULATEDCCU_H__

#include "ctinexus.h"
#include "ctiTime.h"
#include <boost/thread/mutex.hpp>

class SimulatedCCU
{

    public:
        static boost::mutex io_mutex;
        SimulatedCCU(CTINEXUS* s=NULL);

        virtual void processRequest(unsigned long addressFound)=0;
        virtual bool validateRequest(unsigned char req)=0;

        CTINEXUS* getSocket();
        void setSocket(CTINEXUS* s);

        void setStrategy(int strategy);
        int getStrategy();

    private:
        CTINEXUS* _socket;
        int _strategy;
};
#endif
