

#include "yukon.h"
#include "VoltageRegulator.h"


namespace Cti           {
namespace CapControl    {


MissingPointAttribute::MissingPointAttribute(const long ID, const PointAttribute & attribute)
    : std::exception(),
      _description("Missing Point Attribute: '")
{
    _description += attribute.name() + "' on Voltage Regulator with ID: " + CtiNumStr(ID);
}


const char * MissingPointAttribute::what( ) const
{
    return _description.c_str();
}


const std::string VoltageRegulator::LoadTapChanger                  = "LTC";
const std::string VoltageRegulator::GangOperatedVoltageRegulator    = "GO_REGULATOR";
const std::string VoltageRegulator::PhaseOperatedVoltageRegulator   = "PO_REGULATOR";


VoltageRegulator::VoltageRegulator()
    : CapControlPao(),
    _updated(true),
    _mode(VoltageRegulator::RemoteMode),
    _lastTapOperation(VoltageRegulator::None)
{
    // empty...
}

VoltageRegulator::VoltageRegulator(Cti::RowReader & rdr)
    : CapControlPao(rdr),
    _updated(true),
    _mode(VoltageRegulator::RemoteMode),
    _lastTapOperation(VoltageRegulator::None)
{
    // empty...
}


VoltageRegulator::VoltageRegulator(const VoltageRegulator & toCopy)
    : CapControlPao(),
    _updated(true),
    _mode(VoltageRegulator::RemoteMode),
    _lastTapOperation(VoltageRegulator::None)
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
    }

    return *this;
}


void VoltageRegulator::saveGuts(RWvostream& ostrm) const
{
    RWCollectable::saveGuts(ostrm);
    CapControlPao::saveGuts(ostrm);
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


LitePoint VoltageRegulator::getPointByAttribute(const PointAttribute & attribute) const
{
    AttributeMap::const_iterator iter = _attributes.find(attribute);

    if ( iter == _attributes.end() )
    {
        throw MissingPointAttribute( getPaoId(), attribute );
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

}
}

