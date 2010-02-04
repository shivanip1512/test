#pragma once

#include "ccsubstationbus.h"
#include "IVVCStrategy.h"

class IVVCAlgorithm
{
    public:
        static void execute(IVVCStatePtr p, CtiCCSubstationBusPtr subbus, IVVCStrategy* strategy);

    private:
        static bool checkForStaleData(const PointValueMap& pointValues, CtiTime timeNow);
        static std::list<long> determineWatchPoints(CtiCCSubstationBusPtr subbus, DispatchConnectionPtr conn, bool sendScan);
};

