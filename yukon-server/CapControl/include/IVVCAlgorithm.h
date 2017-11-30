#pragma once

#include "ccsubstationbus.h"
#include "IVVCState.h"
#include "PointDataRequestFactory.h"
#include "ZoneManager.h"
#include "VoltageRegulatorManager.h"


namespace Cti           {
namespace Messaging     {
namespace CapControl    {
    class IVVCAnalysisMessage;
    enum IVVCAnalysisScenarios;
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

        bool checkAllBanksAreInControlZones( CtiCCSubstationBusPtr subbus );

        bool checkZoneRegulatorsInProperConfig(IVVCStatePtr state, CtiCCSubstationBusPtr subbus);

        bool isBusInDisabledIvvcState( IVVCStatePtr state, CtiCCSubstationBusPtr subbus );

        void sendIVVCAnalysisMessage( Cti::Messaging::CapControl::IVVCAnalysisMessage * message );

        bool checkBusHasAtLeastOneZone(IVVCStatePtr state, CtiCCSubstationBusPtr subbus);

        bool checkConfigAllZonesHaveRegulator(IVVCStatePtr state, CtiCCSubstationBusPtr subbus);

        enum class ValidityCheckResults
        {
            Invalid,
            Valid,
            MissingObject
        };

        ValidityCheckResults hasValidData( PointDataRequestPtr& request, CtiTime timeNow, CtiCCSubstationBusPtr subbus, IVVCStrategy* strategy );
        bool processZoneByPhase( PointDataRequestPtr& request, 
                                 CtiTime timeNow,
                                 CtiCCSubstationBusPtr subbus,
                                 IVVCStrategy& strategy, 
                                 long ZoneId,
                                 bool dataIsValid );
        bool processZoneByAggregate( PointDataRequestPtr& request, 
                                     CtiTime timeNow,
                                     CtiCCSubstationBusPtr subbus,
                                     IVVCStrategy& strategy, 
                                     long ZoneId,
                                     bool dataIsValid );

        virtual bool determineWatchPoints(CtiCCSubstationBusPtr subbus, bool sendScan, std::set<PointRequest>& pointRequests, IVVCStrategy* strategy);

        double calculateTargetPFVars(const double targetPF, const double wattValue);
        double calculateVf(const PointValueMap &voltages);
        double calculateVte(const PointValueMap &voltages, IVVCStrategy* strategy,
                            const std::map<long, CtiCCMonitorPointPtr> & _monitorMap,
                            const bool isPeakTime,
                            Cti::CapControl::VoltageRegulatorManager::SharedPtr  regulator);

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

        virtual bool operateBank(long bankId, CtiCCSubstationBusPtr subbus, DispatchConnectionPtr dispatchConnection, IVVCStrategy* strategy);
        virtual void sendPointChangesAndEvents(DispatchConnectionPtr dispatchConnection, CtiMultiMsg_vec& pointChanges, const Cti::CapControl::EventLogEntries &ccEvents);
        virtual void sendPointChanges(DispatchConnectionPtr dispatchConnection, CtiMultiMsg_vec& pointChanges);

        virtual void sendKeepAlive(CtiCCSubstationBusPtr subbus);

        virtual void stopDisabledDeviceHeartbeats(CtiCCSubstationBusPtr subbus);

        virtual bool busAnalysisState(IVVCStatePtr state, CtiCCSubstationBusPtr subbus, IVVCStrategy* strategy, DispatchConnectionPtr dispatchConnection);

        bool busVerificationAnalysisState(IVVCStatePtr state, CtiCCSubstationBusPtr subbus, IVVCStrategy* strategy, DispatchConnectionPtr dispatchConnection);
        void setupNextBankToVerify(IVVCStatePtr state, CtiCCSubstationBusPtr subbus, Cti::CapControl::EventLogEntries &ccEvents);
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
                                               std::map<Cti::CapControl::Phase, double> cumulativeVoltageOffsets,
                                               CtiCCSubstationBusPtr subbus,
                                               IVVCStrategy * strategy,
                                               IVVCState::TapOperationZoneMap & solution );

        double getVmaxForPoint( const long pointID, CtiCCSubstationBusPtr subbus, IVVCStrategy * strategy ) const;

        void findPointInRequest( const long pointID, const PointValueMap & pointValues, PointDataRequestPtr & request, const CtiTime & timeNow,
                                 int & totalPoints, int & missingPoints, int & stalePoints );
        bool analysePointRequestData( const long subbusID, const int totalPoints, const int missingPoints, 
                                      const int stalePoints, const double minimum, 
                                      const Cti::Messaging::CapControl::IVVCAnalysisScenarios & incompleteScenario,
                                      const Cti::Messaging::CapControl::IVVCAnalysisScenarios & staleScenario,
                                      const CtiTime & timeNow, const std::string & type );

        void updateMaxOvervoltages( const long pointID,
                                    const Cti::CapControl::Phase & phase,
                                    const double Vmax,
                                    std::map<Cti::CapControl::Phase, double> cumulativeOffsets,
                                    PointValueMap & voltages, 
                                    std::map<Cti::CapControl::Phase, double> & maxOverages );

        bool handleReverseFlow( CtiCCSubstationBusPtr subbus );


        PointDataRequestFactoryPtr _requestFactory;
};

