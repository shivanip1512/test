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

    protected:

        virtual bool checkForStaleData(const PointValueMap& pointValues, CtiTime timeNow);
        virtual void determineWatchPoints(CtiCCSubstationBusPtr subbus, DispatchConnectionPtr conn, bool sendScan, std::set<long>& pointIds, std::set<long>& requestPoints);

        double calculateTargetPFVars(const double targetPF, const double wattValue);
        double calculatePowerFactor(const double varValue, const double wattValue);
        double calculateVf(const PointValueMap &voltages);
        int calculateVte(const PointValueMap &voltages, const double Vmin, const double Vrm, const double Vmax);
        double calculateBusWeight(const double Kv, const double Vf,
                                  const double Kp, const double powerFactor, const double targetPowerFactor);

        virtual void operateBank(long bankId, CtiCCSubstationBusPtr subbus, DispatchConnectionPtr dispatchConnection);
        virtual void sendPointChangesAndEvents(DispatchConnectionPtr dispatchConnection, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents);

        virtual void sendKeepAlive(CtiCCSubstationBusPtr subbus);

        virtual bool isLtcInRemoteMode(const long ltcId);

        virtual bool busAnalysisState(IVVCStatePtr state, CtiCCSubstationBusPtr subbus, IVVCStrategy* strategy, DispatchConnectionPtr dispatchConnection);

        PointDataRequestFactoryPtr _requestFactory;
};

