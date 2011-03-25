
#include "yukon.h"
#include "Zone.h"


namespace Cti           {
namespace CapControl    {

Zone::Zone( const long Id,
            const long parentId,
            const long regulatorId,
            const long subbusId,
            const std::string & name ) :
    _Id(Id),
    _parentId(parentId),
    _regulatorId(regulatorId),
    _subbusId(subbusId),
    _name(name)
{
    // empty!
}


Zone::~Zone()
{
    // empty!
}


const bool Zone::operator == ( const Zone & rhs ) const
{
    return _Id == rhs._Id;
}


const bool Zone::operator != ( const Zone & rhs ) const
{
    return _Id != rhs._Id;
}


const bool Zone::operator <  ( const Zone & rhs ) const
{
    return _Id <  rhs._Id;
}


long Zone::getId() const
{
    return _Id;
}


long Zone::getParentId() const
{
    return _parentId;
}


long Zone::getRegulatorId() const
{
    return _regulatorId;
}


long Zone::getSubbusId() const
{
    return _subbusId;
}


std::string Zone::getName() const
{
    return _name;
}


void Zone::addChildId( const long Id )
{
    if ( Id != _Id )
    {
        _childIds.insert(Id);
    }
}


Zone::IdSet Zone::getChildIds() const
{
    return _childIds;
}


void Zone::clearChildIds()
{
    _childIds.clear();
}


Zone::IdSet Zone::getBankIds() const
{
    return _bankPaos;
}


Zone::IdSet Zone::getPointIds() const
{
    return _voltagePoints;
}


void Zone::addBankId( const long Id )
{
    _bankPaos.insert(Id);
}


void Zone::addPointId( const long Id )
{
    _voltagePoints.insert(Id);
}

}   // namespace Cti
}   // namespace CapControl

