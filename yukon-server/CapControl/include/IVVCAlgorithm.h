#pragma once

#include "ccsubstationbus.h"
#include "IVVCState.h"
#include "PointDataRequestFactory.h"
#include "ZoneManager.h"

using Cti::CapControl::Zone;
using Cti::CapControl::ZoneLoader;
using Cti::CapControl::ZoneManager;

class IVVCStrategy;

class IVVCAlgorithm
{
    public:

        IVVCAlgorithm(const PointDataRequestFactoryPtr& factory);

        void execute(IVVCStatePtr p, CtiCCSubstationBusPtr subbus, IVVCStrategy* strategy, bool allowScanning);

        void setPointDataRequestFactory(const PointDataRequestFactoryPtr& factory);

      //  typedef std::map<Zone::IdSet::value_type, int>  TapOperationZoneMap;

    protected:

        bool checkConfigAllZonesHaveRegulator(IVVCStatePtr state, CtiCCSubstationBusPtr subbus);

        virtual bool checkForStaleData(const PointDataRequestPtr& request, CtiTime timeNow);
        virtual bool checkForStaleData(const PointDataRequestPtr& request, CtiTime timeNow, double desiredRatio, PointRequestType pointRequestType);
        virtual bool determineWatchPoints(CtiCCSubstationBusPtr subbus, DispatchConnectionPtr conn, bool sendScan, std::set<PointRequest>& pointRequests);

        double calculateTargetPFVars(const double targetPF, const double wattValue);
        double calculateVf(const PointValueMap &voltages);
        int calculateVte(const PointValueMap &voltages, IVVCStrategy* strategy,
                         const std::map<long, CtiCCMonitorPointPtr> & _monitorMap, const bool isPeakTime);

        double calculateBusWeight(const double Kv, const double Vf,
                                  const double Kp, const double powerFactor, const double targetPowerFactor);

        void tapOperation(IVVCStatePtr state, CtiCCSubstationBusPtr subbus, IVVCStrategy* strategy, const PointValueMap & pointValues);
        void tapOpZoneNormalization(const long parentID, const ZoneManager &zoneManager, IVVCState::TapOperationZoneMap &tapOp);

        virtual void operateBank(long bankId, CtiCCSubstationBusPtr subbus, DispatchConnectionPtr dispatchConnection);
        virtual void sendPointChangesAndEvents(DispatchConnectionPtr dispatchConnection, CtiMultiMsg_vec& pointChanges, CtiMultiMsg* ccEvents);
        virtual void sendPointChanges(DispatchConnectionPtr dispatchConnection, CtiMultiMsg_vec& pointChanges);
        virtual void sendEvents(DispatchConnectionPtr dispatchConnection, CtiMultiMsg* ccEvents);

        virtual void sendKeepAlive(CtiCCSubstationBusPtr subbus);

        virtual bool isVoltageRegulatorInRemoteMode(const long regulatorID) const;

        virtual bool busAnalysisState(IVVCStatePtr state, CtiCCSubstationBusPtr subbus, IVVCStrategy* strategy, DispatchConnectionPtr dispatchConnection);

        bool allRegulatorsInRemoteMode(const long subbusId) const;

        PointDataRequestFactoryPtr _requestFactory;
};

