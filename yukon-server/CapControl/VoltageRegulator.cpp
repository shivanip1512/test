

#include "yukon.h"
#include "VoltageRegulator.h"
#include "ccutil.h"


namespace Cti           {
namespace CapControl    {


// If these strings change, remember to update them in resolveCapControlType()
const std::string VoltageRegulator::LoadTapChanger                  = "LTC";
const std::string VoltageRegulator::GangOperatedVoltageRegulator    = "GO_REGULATOR";
const std::string VoltageRegulator::PhaseOperatedVoltageRegulator   = "PO_REGULATOR";


VoltageRegulator::VoltageRegulator()
    : CapControlPao(),
    _updated(true),
    _mode(VoltageRegulator::RemoteMode),
    _lastTapOperation(VoltageRegulator::None),
    _lastMissingAttributeComplainTime(CtiTime(neg_infin))
{
    // empty...
}

VoltageRegulator::VoltageRegulator(Cti::RowReader & rdr)
    : CapControlPao(rdr),
    _updated(true),
    _mode(VoltageRegulator::RemoteMode),
    _lastTapOperation(VoltageRegulator::None),
    _lastMissingAttributeComplainTime(CtiTime(neg_infin))
{
    // empty...
}


VoltageRegulator::VoltageRegulator(const VoltageRegulator & toCopy)
    : CapControlPao(),
    _updated(true),
    _mode(VoltageRegulator::RemoteMode),
    _lastTapOperation(VoltageRegulator::None),
    _lastMissingAttributeComplainTime(CtiTime(neg_infin))
{
    operator=(toCopy);
}


VoltageRegulator & VoltageRegulator::operator=(const VoltageRegulator & rhs)
{
    if ( this != &rhs )
    {
        CapControlPao::operator=(rhs);

        _updated    = rhs._updated;
        _mode       = rhs._mode;

        _lastTapOperation       = rhs._lastTapOperation;
        _lastTapOperationTime   = rhs._lastTapOperationTime;

        _attributes = rhs._attributes;

        _pointValues = rhs._pointValues;

        _lastMissingAttributeComplainTime = rhs._lastMissingAttributeComplainTime;
    }

    return *this;
}


void VoltageRegulator::saveGuts(RWvostream& ostrm) const
{
    RWCollectable::saveGuts(ostrm);
    CapControlPao::saveGuts(ostrm);

    ostrm << 0//parentId Must be here for clients...
          << _lastTapOperation
          << _lastTapOperationTime;
}


void VoltageRegulator::handlePointData(CtiPointDataMsg * message)
{
    setUpdated(true);
    _pointValues.updatePointValue(message);
}


VoltageRegulator::IDSet VoltageRegulator::getRegistrationPoints()
{
    IDSet IDs;

    for each ( const AttributeMap::value_type & attribute in _attributes  )
    {
        IDs.insert( attribute.second.getPointId() );
    }

    return IDs;
}


LitePoint VoltageRegulator::getPointByAttribute(const PointAttribute & attribute) 
{
    AttributeMap::const_iterator iter = _attributes.find(attribute);

    if ( iter == _attributes.end() )
    {
        throw MissingPointAttribute( getPaoId(), attribute, getPaoType(), isTimeForMissingAttributeComplain()  );
    }

    return iter->second;
}


void VoltageRegulator::setOperatingMode(const OperatingMode & mode)
{
    _mode = mode;
}


VoltageRegulator::OperatingMode VoltageRegulator::getOperatingMode() const
{
    return _mode;
}


bool VoltageRegulator::isUpdated() const
{
    return _updated;
}


void VoltageRegulator::setUpdated(const bool updated)
{
    _updated = updated;
}


void VoltageRegulator::notifyTapOperation(const TapOperation & operation, const CtiTime & timeStamp)
{
    _lastTapOperation     = operation;
    _lastTapOperationTime = timeStamp;
}


bool VoltageRegulator::getPointValue(int pointId, double & value)
{
    return _pointValues.getPointValue( pointId, value );
}


void VoltageRegulator::loadPointAttributes(AttributeService * service, const PointAttribute & attribute)
{
    LitePoint point = service->getPointByPaoAndAttribute( getPaoId(), attribute );

    if (point.getPointType() != InvalidPointType)
    {
        _attributes.insert( std::make_pair( attribute, point ) );
    }
}
CtiTime VoltageRegulator::updateMissingAttributeComplainTime()
{
    _lastMissingAttributeComplainTime = CtiTime();
    return _lastMissingAttributeComplainTime;
}
bool VoltageRegulator::isTimeForMissingAttributeComplain(CtiTime time)
{
    if ( _lastMissingAttributeComplainTime + 300 < time )
    {
        updateMissingAttributeComplainTime();
        return true;
    }
    return false;
}

}
}

