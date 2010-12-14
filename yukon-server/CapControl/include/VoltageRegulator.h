
#pragma warning( disable : 4786)

#pragma once

#include "yukon.h"
#include "CapControlPao.h"
#include "UpdatablePao.h"
#include "LitePoint.h"
#include "PointValueHolder.h"
#include "AttributeService.h"
#include "ccid.h"

#include <rw/collect.h>

#include <map>
#include <set>
#include <string>
#include <stdexcept>


namespace Cti           {
namespace CapControl    {

class VoltageRegulator : public RWCollectable, public CapControlPao, public UpdatablePao
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

    virtual void saveGuts(RWvostream & ostrm) const;

    virtual void handlePointData(CtiPointDataMsg * message);

    IDSet getRegistrationPoints();

    LitePoint getPointByAttribute(const PointAttribute & attribute) const;

    void setOperatingMode(const OperatingMode & mode);
    OperatingMode getOperatingMode() const;

    bool isUpdated() const;
    void setUpdated(const bool updated);

    void notifyTapOperation(const TapOperation & operation, const CtiTime & timeStamp);

    bool getPointValue(int pointId, double & value);

    virtual void loadAttributes(AttributeService * service) = 0;
    virtual void updateFlags(const unsigned tapDelay) = 0;

    virtual VoltageRegulator * replicate() const = 0;

protected:

    bool            _updated;
    OperatingMode   _mode;

    TapOperation    _lastTapOperation;
    CtiTime         _lastTapOperationTime;

    AttributeMap    _attributes;

    PointValueHolder    _pointValues;

    virtual void loadPointAttributes(AttributeService * service, const PointAttribute & attribute);
};

}
}

