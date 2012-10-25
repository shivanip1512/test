#pragma once

#include "ccsubstationbus.h"
#include "IVVCState.h"
#include "PointDataRequestFactory.h"
#include "ZoneManager.h"


namespace Cti           {
namespace Messaging     {
namespace CapControl    {
    class IVVCAnalysisMessage;
}
}
}
class IVVCStrategy;


class IVVCAlgorithm
{
    public:

        IVVCAlgorithm(const PointDataRequestFactoryPtr& factory);

        void execute(IVVCStatePtr p, CtiCCSubstationBusPtr subbus, IVVCStrategy* strategy, bool allowScanning);

        void setPointDataRequestFactory(const PointDataRequestFactoryPtr& factory);

      //  typedef std::map<Zone::IdSet::value_type, int>  TapOperationZoneMap;

    protected:

        bool isBusInDisabledIvvcState( IVVCStatePtr state, CtiCCSubstationBusPtr subbus );

        void sendIVVCAnalysisMessage( Cti::Messaging::CapControl::IVVCAnalysisMessage * message );

        bool checkConfigAllZonesHaveRegulator(IVVCStatePtr state, CtiCCSubstationBusPtr subbus);

        enum DataStatus
        {
            DataStatus_Good,
            DataStatus_Incomplete,
            DataStatus_Stale,
        };
        typedef std::pair<DataStatus, double>   DataCheckResult;

        virtual bool            hasValidData( const PointDataRequestPtr& request, CtiTime timeNow, CtiCCSubstationBusPtr subbus, IVVCStrategy* strategy );
        virtual DataCheckResult hasEnoughRecentData(const PointDataRequestPtr& request, CtiTime timeNow, double desiredRatio, PointRequestType pointRequestType, const std::string & requestTypeString);

        virtual bool determineWatchPoints(CtiCCSubstationBusPtr subbus, DispatchConnectionPtr conn, bool sendScan, std::set<PointRequest>& pointRequests, IVVCStrategy* strategy);

        double calculateTargetPFVars(const double targetPF, const double wattValue);
        double calculateVf(const PointValueMap &voltages);
        int calculateVte(const PointValueMap &voltages, IVVCStrategy* strategy,
                         const std::map<long, CtiCCMonitorPointPtr> & _monitorMap,
                         const bool isPeakTime, const double voltChangePerTap);

        double voltageViolationCalculator(const double voltage, const IVVCStrategy * strategy, const bool isPeakTime);

        double calculateVoltageViolation(const PointValueMap & voltages,
                                         const IVVCStrategy * strategy, const bool isPeakTime);

        double calculatePowerFactorCost( const double powerFactor, const IVVCStrategy * strategy, const bool isPeakTime ) const;

        double feederPFCorrectionCalculator( const double actualFeederPF,
                                             const IVVCStrategy * strategy, const bool isPeakTime ) const;

        bool checkForMultiTapOperation( const PointValueMap & voltages,
                                        const std::map<long, CtiCCMonitorPointPtr> & _monitorMap,
                                        IVVCStrategy * strategy,
                                        const bool isPeakTime ) const;

        void tapOperation(IVVCStatePtr state, CtiCCSubstationBusPtr subbus, IVVCStrategy* strategy, const PointValueMap & pointValues);
        void tapOpZoneNormalization(const long parentID, const Cti::CapControl::ZoneManager &zoneManager, IVVCState::TapOperationZoneMap &tapOp);

        virtual void operateBank(long bankId, CtiCCSubstationBusPtr subbus, DispatchConnectionPtr dispatchConnection);
        virtual void sendPointChangesAndEvents(DispatchConnectionPtr dispatchConnection, CtiMultiMsg_vec& pointChanges, CtiMultiMsg* ccEvents);
        virtual void sendPointChanges(DispatchConnectionPtr dispatchConnection, CtiMultiMsg_vec& pointChanges);
        virtual void sendEvents(DispatchConnectionPtr dispatchConnection, CtiMultiMsg* ccEvents);

        virtual void sendKeepAlive(CtiCCSubstationBusPtr subbus);

        virtual bool busAnalysisState(IVVCStatePtr state, CtiCCSubstationBusPtr subbus, IVVCStrategy* strategy, DispatchConnectionPtr dispatchConnection);

        bool busVerificationAnalysisState(IVVCStatePtr state, CtiCCSubstationBusPtr subbus, IVVCStrategy* strategy, DispatchConnectionPtr dispatchConnection);
        void setupNextBankToVerify(IVVCStatePtr state, CtiCCSubstationBusPtr subbus, CtiMultiMsg_vec& ccEvents);
        bool allRegulatorsInRemoteMode(const long subbusId) const;

        void sendDisableRemoteControl( CtiCCSubstationBusPtr subbus );
        void handleCommsLost(IVVCStatePtr state, CtiCCSubstationBusPtr subbus);

        bool hasTapOpsRemaining(const IVVCState::TapOperationZoneMap & tapOp) const;

        void updateCommsState( const long busCommsPointId, const bool isCommsLost ) const;

        void calculateMultiTapOperation( PointValueMap & voltages,
                                         CtiCCSubstationBusPtr subbus,                                   
                                         IVVCStrategy * strategy,
                                         IVVCState::TapOperationZoneMap & solution );

        void calculateMultiTapOperationHelper( const long zoneID,
                                               PointValueMap & voltages,
                                               const double cumulativeVoltageOffset,
                                               CtiCCSubstationBusPtr subbus,
                                               IVVCStrategy * strategy,
                                               IVVCState::TapOperationZoneMap & solution );

        double getVmaxForPoint( const long pointID, CtiCCSubstationBusPtr subbus, IVVCStrategy * strategy ) const;

        PointDataRequestFactoryPtr _requestFactory;
};

