#pragma once

#include "yukon.h"
#include "CapControlPao.h"
#include "UpdatablePao.h"
#include "LitePoint.h"
#include "PointValueHolder.h"
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

    static const std::string LoadTapChanger;
    static const std::string GangOperatedVoltageRegulator;
    static const std::string PhaseOperatedVoltageRegulator;

    Type getType() const;

    typedef std::set<long>  IDSet;

    VoltageRegulator();
    VoltageRegulator(Cti::RowReader & rdr);
    VoltageRegulator(const VoltageRegulator & toCopy);

//    VoltageRegulator &operator=(const VoltageRegulator & rhs);

    void handlePointData(CtiPointDataMsg * message);

    IDSet getRegistrationPoints();

    LitePoint getPointByAttribute(const PointAttribute & attribute);

    OperatingMode getOperatingMode();

    bool isUpdated() const;
    void setUpdated(const bool updated);

    CtiTime updateMissingAttributeComplainTime();
    bool isTimeForMissingAttributeComplain(CtiTime time = CtiTime());

    void loadAttributes( AttributeService * service );

    void updateFlags(const unsigned tapDelay);

//    virtual VoltageRegulator * replicate() const = 0;
    VoltageRegulator * replicate() const
    {
        return new VoltageRegulator( *this );
    }

    void executeTapUpOperation();
    void executeTapDownOperation();
    void executeAdjustSetPointOperation( const double changeAmount );

    void executeIntegrityScan();

    void executeEnableRemoteControl();
    void executeDisableRemoteControl();

    long executeEnableKeepAlive();
    void executeDisableKeepAlive();

    bool executePeriodicKeepAlive();

    void        setPhase( const Phase phase );
    Phase       getPhase() const;
    std::string getPhaseString() const;

    double getVoltageChangePerTap() const;
    double requestVoltageChange( const double changeAmount,
                                 const bool isEmergency = false );

    void canExecuteVoltageRequest( const double changeAmount );

    double adjustVoltage( const double changeAmount );

    ControlOperation getLastControlOperation() const     { return _lastControlOperation; }
    CtiTime          getLastControlOperationTime() const { return _lastControlOperationTime; }

    ControlMode getControlMode() const;
    std::string getHeartbeatMode() const;


    double getVoltage();

    boost::optional<long> getTapPosition();


    long getKeepAliveConfig();
    long getKeepAliveTimer();

    bool          getRecentTapOperation()         const { return _recentTapOperation; }
    OperatingMode getLastOperatingMode()          const { return _lastOperatingMode; }
    OperatingMode getLastCommandedOperatingMode() const { return _lastCommandedOperatingMode; }

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

    void notifyControlOperation(const ControlOperation & operation, const CtiTime & timeStamp = CtiTime() );


    void submitControlCommands( Policy::Action                    & action,
                                const ControlOperation              operation,
                                const std::string                 & opDescription,
                                const RegulatorEvent::EventTypes    eventType,
                                const double                        changeAmount );

    void submitRemoteControlCommands( Policy::Action                  & action,
                                      const std::string               & description,
                                      const RegulatorEvent::EventTypes  eventType );

    long submitKeepAliveCommands( Policy::Actions & actions);
};

// this is added to use voltageRegulator with boost::ptr_vector, since it is an abstract class
inline VoltageRegulator* new_clone(VoltageRegulator const& other)
{
  return other.replicate();
}


// free functions to inject the user name into the event log.
// call using these instead of directly calling execute..()

void issueIntegrityScanCommand( VoltageRegulator & regulator, const std::string & user );

void issueEnableRemoteControlCommand( VoltageRegulator & regulator, const std::string & user );
void issueDisableRemoteControlCommand( VoltageRegulator & regulator, const std::string & user );

void issueTapUpCommand( VoltageRegulator & regulator, const std::string & user );
void issueTapDownCommand( VoltageRegulator & regulator, const std::string & user );


}
}

