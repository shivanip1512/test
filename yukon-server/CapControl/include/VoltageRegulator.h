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

    virtual const Type getType() const = 0;

    typedef std::map<PointAttribute, LitePoint> AttributeMap;
    typedef std::set<long>                      IDSet;

    VoltageRegulator();
    VoltageRegulator(Cti::RowReader & rdr);
    VoltageRegulator(const VoltageRegulator & toCopy);

    VoltageRegulator &operator=(const VoltageRegulator & rhs);

    virtual void handlePointData(CtiPointDataMsg * message);

    IDSet getRegistrationPoints();

//    virtual IDSet getVoltagePointIDs() = 0;

    LitePoint getPointByAttribute(const PointAttribute & attribute);

    OperatingMode getOperatingMode();

    bool isUpdated() const;
    void setUpdated(const bool updated);

    bool getPointValue(int pointId, double & value);

    CtiTime updateMissingAttributeComplainTime();
    bool isTimeForMissingAttributeComplain(CtiTime time = CtiTime());

    virtual void loadAttributes(AttributeService * service) = 0;
    virtual void updateFlags(const unsigned tapDelay) = 0;

    virtual VoltageRegulator * replicate() const = 0;

    virtual void executeTapUpOperation();
    virtual void executeTapDownOperation();
    virtual void executeAdjustSetPointOperation( const double changeAmount );

    virtual void executeIntegrityScan();

    virtual void executeEnableRemoteControl();
    virtual void executeDisableRemoteControl();

    virtual void executeEnableKeepAlive();
    virtual void executeDisableKeepAlive();

    void setKeepAliveConfig(const long value);
    bool isTimeToSendKeepAlive();

    void        setPhase( const Phase phase );
    Phase       getPhase() const;
    std::string getPhaseString() const;

    double getVoltageChangePerTap() const;
    double requestVoltageChange( const double changeAmount,
                                 const bool isEmergency = false );

    void canExecuteVoltageRequest( const double changeAmount ) ;//const;

    double adjustVoltage( const double changeAmount );

    ControlOperation getLastControlOperation() const     { return _lastControlOperation; }
    CtiTime          getLastControlOperationTime() const { return _lastControlOperationTime; }

    ControlMode getControlMode() const;

    double getVoltage();

    long getKeepAliveConfig();
    long getKeepAliveTimer();

    virtual bool          getRecentTapOperation()         const { return _recentTapOperation; }
    virtual OperatingMode getLastOperatingMode()          const { return _lastOperatingMode; }
    virtual OperatingMode getLastCommandedOperatingMode() const { return _lastCommandedOperatingMode; }

protected:

    bool            _recentTapOperation;

    OperatingMode   _lastOperatingMode;
    OperatingMode   _lastCommandedOperatingMode;

    std::unique_ptr<ControlPolicy>   _controlPolicy;
    std::unique_ptr<KeepAlivePolicy> _keepAlivePolicy;
    std::unique_ptr<ScanPolicy>      _scanPolicy;

    Phase   _phase;

    bool            _updated;
    OperatingMode   _mode;

    ControlOperation    _lastControlOperation;
    CtiTime             _lastControlOperationTime;

    AttributeMap    _attributes;

    PointValueHolder    _pointValues;

    long        _keepAliveConfig;
    long        _keepAliveTimer;
    CtiTime     _nextKeepAliveSendTime;

    CtiTime         _lastMissingAttributeComplainTime;

    virtual void loadPointAttributes(AttributeService * service, const PointAttribute & attribute);

    void executeDigitalOutputHelper( const LitePoint & point,
                                     const std::string & textDescription,
                                     const int recordEventType = capControlNoEvent );

    void executeRemoteControlHelper( const LitePoint & point,
                                     const int keepAliveValue,
                                     const std::string & textDescription,
                                     const int recordEventType = capControlNoEvent );

    void executeKeepAliveHelper(const LitePoint & point, const int keepAliveValue);

    CtiSignalMsg * createDispatchMessage( const long ID, const std::string &text );

    void notifyControlOperation(const ControlOperation & operation, const CtiTime & timeStamp = CtiTime() );


    void submitControlCommands( Policy::Action                & action,
                                const ControlOperation          operation,
                                const std::string             & opDescription,
                                const CtiCCEventType_t          eventType,
                                const double                    changeAmount );

    void submitRemoteControlCommands( Policy::Action    & action,
                                      const std::string & description );

    long submitKeepAliveCommands( Policy::Actions & actions);
};

// this is added to use voltageRegulator with boost::ptr_vector, since it is an abstract class
inline VoltageRegulator* new_clone(VoltageRegulator const& other)
{
  return other.replicate();
}

}
}

