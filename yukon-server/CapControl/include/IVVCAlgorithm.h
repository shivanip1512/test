#pragma once

#include "ccsubstationbus.h"
#include "IVVCState.h"
#include "PointDataRequestFactory.h"

class IVVCStrategy;

class IVVCAlgorithm
{
    public:
        IVVCAlgorithm(const PointDataRequestFactoryPtr& factory);

        void execute(IVVCStatePtr p, CtiCCSubstationBusPtr subbus, IVVCStrategy* strategy, bool allowScanning);

        void setPointDataRequestFactory(const PointDataRequestFactoryPtr& factory);

    private:
        static bool checkForStaleData(const PointValueMap& pointValues, CtiTime timeNow);
        static void determineWatchPoints(CtiCCSubstationBusPtr subbus, DispatchConnectionPtr conn, bool sendScan, std::set<long>& pointIds, std::set<long>& requestPoints);

        static double calculateVf(const PointValueMap &voltages, const long varPointID, const long wattPointID);
        static int calculateVte(const PointValueMap &voltages, const double Vmin, const double Vrm, const double Vmax,
                                const long varPointID, const long wattPointID);
        static double calculateBusWeight(const double Kv, const double Vf, const double Kp, const double powerFactor);

        static void operateBank(long bankId, CtiCCSubstationBusPtr subbus, DispatchConnectionPtr dispatchConnection);
        static void sendPointChangesAndEvents(DispatchConnectionPtr dispatchConnection, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents);

        static void sendKeepAlive(CtiCCSubstationBusPtr subbus);

        PointDataRequestFactoryPtr _requestFactory;
};

