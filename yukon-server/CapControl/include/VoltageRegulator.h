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

#include <rw/collect.h>

#include <map>
#include <set>
#include <string>
#include <stdexcept>

class CtiSignalMsg;

namespace Cti           {
namespace CapControl    {

class VoltageRegulator : public CapControlPao, public UpdatablePao
{
public:

    enum TapOperation
    {
        None,
        LowerTap,
        RaiseTap
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

    virtual IDSet getVoltagePointIDs() = 0;

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
    virtual void executeIntegrityScan() = 0;
    virtual void executeEnableRemoteControl() = 0;
    virtual void executeDisableRemoteControl() = 0;
    virtual void executeEnableKeepAlive() = 0;
    virtual void executeDisableKeepAlive() = 0;

    void setKeepAliveConfig(const long value);
    bool isTimeToSendKeepAlive();

    void        setPhase( const Phase phase );
    Phase       getPhase() const;
    std::string getPhaseString() const;

    double getVoltageChangePerTap() const;

    TapOperation getLastTapOperation() const     { return _lastTapOperation; }
    CtiTime      getLastTapOperationTime() const { return _lastTapOperationTime; }

    virtual bool            getRecentTapOperation() const = 0;
    virtual OperatingMode   getLastOperatingMode() const = 0;
    virtual OperatingMode   getLastCommandedOperatingMode() const = 0;

protected:

    Phase   _phase;

    bool            _updated;
    OperatingMode   _mode;

    TapOperation    _lastTapOperation;
    CtiTime         _lastTapOperationTime;

    AttributeMap    _attributes;

    PointValueHolder    _pointValues;

    long        _keepAliveConfig;
    long        _keepAliveTimer;
    CtiTime     _nextKeepAliveSendTime;

    CtiTime         _lastMissingAttributeComplainTime;

    double      _voltChangePerTap;

    virtual void loadPointAttributes(AttributeService * service, const PointAttribute & attribute);

    void executeIntegrityScanHelper( const LitePoint & point );

    void executeDigitalOutputHelper( const LitePoint & point,
                                     const std::string & textDescription,
                                     const int recordEventType = capControlNoEvent );

    void executeRemoteControlHelper( const LitePoint & point,
                                     const int keepAliveValue,
                                     const std::string & textDescription,
                                     const int recordEventType = capControlNoEvent );

    void executeKeepAliveHelper(const LitePoint & point, const int keepAliveValue);

    CtiSignalMsg * createDispatchMessage( const long ID, const std::string &text );

    void notifyTapOperation(const TapOperation & operation, const CtiTime & timeStamp = CtiTime() );
};

// this is added to use voltageRegulator with boost::ptr_vector, since it is an abstract class
inline VoltageRegulator* new_clone(VoltageRegulator const& other)
{
  return other.replicate();
}

}
}

