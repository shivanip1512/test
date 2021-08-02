#pragma once

#include "yukon.h"
#include "CapControlPao.h"
#include "UpdatablePao.h"
#include "LitePoint.h"
#include "AttributeService.h"
#include "ccid.h"
#include "ccutil.h"

#include "EventTypes.h"

#include "ControlPolicy.h"
#include "KeepAlivePolicy.h"
#include "ScanPolicy.h"
#include "RegulatorEvents.h"

#include <map>
#include <set>
#include <string>
#include <stdexcept>
#include <memory>

class CtiSignalMsg;


namespace Cti           {
namespace CapControl    {


class VoltageRegulator : public CapControlPao, public UpdatablePao
{
public:
    DECLARE_COLLECTABLE( VoltageRegulator );

    enum ControlMode
    {
        ManualTap,
        SetPoint
    };

    enum ControlOperation
    {
        None,
        LowerTap,
        RaiseTap,
        LowerSetPoint,
        RaiseSetPoint
    };

    enum OperatingMode
    {
        UnknownMode = -1,
        RemoteMode,
        LocalMode
    };

    enum Type
    {
        LoadTapChangerType,
        GangOperatedVoltageRegulatorType,
        PhaseOperatedVoltageRegulatorType
    };

    enum VoltageAdjuster
    {
        Single,
        Exclusive,
        Inclusive
    };

    enum class InstallOrientation
    {
        Forward,
        Reverse
    };

    enum class TapPositionLimits
    {
        Minimum =  -16,
        Maximum =   16
    };

    enum class TapInhibit
    {
        None,
        NoTapUp,
        NoTapDown,
        NoTap
    };

    static const std::string LoadTapChanger;
    static const std::string GangOperatedVoltageRegulator;
    static const std::string PhaseOperatedVoltageRegulator;

    Type getType() const;

    typedef std::set<long>  IDSet;

    VoltageRegulator();
    VoltageRegulator(Cti::RowReader & rdr);
    VoltageRegulator(const VoltageRegulator & toCopy);

    virtual ~VoltageRegulator() = default;

    void handlePointData( const CtiPointDataMsg & message ) override;

    IDSet getRegistrationPoints();

    LitePoint getPointByAttribute(const Attribute & attribute);

    OperatingMode getOperatingMode();

    bool isUpdated() const;
    void setUpdated(const bool updated);

    CtiTime updateMissingAttributeComplainTime();
    bool isTimeForMissingAttributeComplain(CtiTime time = CtiTime());

    void loadAttributes( AttributeService * service );

    void updateFlags(const unsigned tapDelay);

    VoltageRegulator * replicate() const
    {
        return new VoltageRegulator( *this );
    }

    void executeTapUpOperation( const std::string & user );
    void executeTapDownOperation( const std::string & user );
    void executeAdjustSetPointOperation( const double changeAmount, const std::string & user );

    void executeIntegrityScan( const std::string & user );

    void executeEnableRemoteControl( const std::string & user );
    void executeDisableRemoteControl( const std::string & user );

    long executeEnableKeepAlive( const std::string & user );
    void executeDisableKeepAlive( const std::string & user );

    bool executePeriodicKeepAlive( const std::string & user );

    void        setPhase( const Phase phase );
    Phase       getPhase() const;
    std::string getPhaseString() const;

    double getVoltageChangePerTap() const;
    double requestVoltageChange( const double changeAmount,
                                 const VoltageAdjuster adjuster = Single );
    void canExecuteVoltageRequest( const double changeAmount );

    double adjustVoltage( const double changeAmount );

    ControlOperation getLastControlOperation() const     { return _lastControlOperation; }
    CtiTime          getLastControlOperationTime() const { return _lastControlOperationTime; }

    ControlMode getControlMode() const;
    std::string getHeartbeatMode() const;

    InstallOrientation getInstallOrientation() const;

    double getVoltage();

    boost::optional<long> getTapPosition();

    PointValue getCompleteTapPosition();

    long getMinTapPosition() const;
    long getMaxTapPosition() const;

    TapInhibit isTapInhibited();

    long getKeepAliveConfig();
    long getKeepAliveTimer();

    bool          getRecentTapOperation()         const { return _recentTapOperation; }
    OperatingMode getLastOperatingMode()          const { return _lastOperatingMode; }
    OperatingMode getLastCommandedOperatingMode() const { return _lastCommandedOperatingMode; }

    bool isReverseFlowDetected();

    ControlPolicy::ControlModes getConfigurationMode();

    double getSetPointValue() const;
    Policy::Action setSetPointValue( const double newSetPoint );

    std::string detailedDescription();

    enum class PowerFlowSituations
    {
        OK,
        IndeterminateFlow,
        ReverseInstallation,
        ReverseFlow,
        ReverseControlPowerFlow,
        UnsupportedMode
    };

    PowerFlowSituations determinePowerFlowSituation();

protected:

    bool            _recentTapOperation;

    OperatingMode   _lastOperatingMode;
    OperatingMode   _lastCommandedOperatingMode;

    std::unique_ptr<ControlPolicy>   _controlPolicy;
    std::unique_ptr<KeepAlivePolicy> _keepAlivePolicy;
    std::unique_ptr<ScanPolicy>      _scanPolicy;

    long    _keepAlivePeriod;
    long    _keepAliveValue;

    Phase   _phase;

    bool            _updated;

    ControlOperation    _lastControlOperation;
    CtiTime             _lastControlOperationTime;

    CtiTime     _nextKeepAliveSendTime;

    CtiTime         _lastMissingAttributeComplainTime;

    InstallOrientation  _installOrientation;

    void notifyControlOperation(const ControlOperation & operation, const CtiTime & timeStamp = CtiTime() );


    void submitControlCommands( Policy::Action                    & action,
                                const ControlOperation              operation,
                                const std::string                 & opDescription,
                                const RegulatorEvent::EventTypes    eventType,
                                const double                        changeAmount,
                                const std::string                 & user );

    void submitRemoteControlCommands( Policy::Actions                 & actions,
                                      const RegulatorEvent::EventTypes  eventType,
                                      const std::string               & user  );

    long submitKeepAliveCommands( Policy::Actions & actions);
};

// this is added to use voltageRegulator with boost::ptr_vector, since it is an abstract class
inline VoltageRegulator* new_clone(VoltageRegulator const& other)
{
  return other.replicate();
}


}
}

