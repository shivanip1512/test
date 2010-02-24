#pragma once

#include "ccsubstationbus.h"
#include "IVVCState.h"
#include "PointDataRequestFactory.h"

class IVVCStrategy;

class IVVCAlgorithm
{
    public:
        IVVCAlgorithm();

        void execute(IVVCStatePtr p, CtiCCSubstationBusPtr subbus, IVVCStrategy* strategy, bool allowScanning);

        void setPointDataRequestFactory(PointDataRequestFactoryPtr& factory);

    private:
        static bool checkForStaleData(const PointValueMap& pointValues, CtiTime timeNow);
        static std::list<long> determineWatchPoints(CtiCCSubstationBusPtr subbus, DispatchConnectionPtr conn, bool sendScan);

        static double calculateVf(const PointValueMap &voltages, const long varPointID, const long wattPointID);
        static int calculateVte(const PointValueMap &voltages, const double Vmin, const double Vrm, const double Vmax,
                                const long varPointID, const long wattPointID);
        static double calculateBusWeight(const double Kv, const double Vf, const double Kp, const double powerFactor);

        static void operateBank(long bankId, CtiCCSubstationBusPtr subbus, DispatchConnectionPtr dispatchConnection);
        static void sendPointChangesAndEvents(DispatchConnectionPtr dispatchConnection, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents);

        PointDataRequestFactoryPtr _requestFactory;
};

