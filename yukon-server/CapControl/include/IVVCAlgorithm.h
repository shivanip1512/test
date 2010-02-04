#pragma once

#include "ccsubstationbus.h"
#include "IVVCStrategy.h"
#include "GroupPointDataRequest.h"

class IVVCAlgorithm
{
    public:
        static void execute(IVVCStatePtr p, CtiCCSubstationBusPtr subbus, IVVCStrategy* strategy);

    private:
        static bool checkForStaleData(const PointValueMap& pointValues, CtiTime timeNow);
        static std::list<long> determineWatchPoints(CtiCCSubstationBusPtr subbus, DispatchConnectionPtr conn, bool sendScan);

        static double calculateVf(const PointValueMap &voltages);
        static int calculateVte(const PointValueMap &voltages, const double Vmin, const double Vrm, const double Vmax);
        static double calculateBusWeight(const double Kv, const double Vf, const double Kp, const double powerFactor);
        static double calculatePowerFactor(const double KWattBus, const double KVarBus, const double bankSize);

};

