
#include "yukon.h"
#include "Zone.h"


namespace Cti           {
namespace CapControl    {

Zone::Zone( const long Id,
            const long parentId,
            const long subbusId,
            const std::string & name, 
            const std::string & type) :
    _Id(Id),
    _parentId(parentId),
    _subbusId(subbusId),
    _name(name)
{
    _gangOperated = ( type == "GANG_OPERATED" );
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


long Zone::getSubbusId() const
{
    return _subbusId;
}


std::string Zone::getName() const
{
    return _name;
}


bool Zone::isGangOperated() const
{
    return _gangOperated;
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


void Zone::addBankId( const long Id )
{
    _bankPaos.insert(Id);
}


Zone::IdSet Zone::getBankIds() const
{
    return _bankPaos;
}


void Zone::addPointId( const Phase & Phase, const long Id )
{
    _voltagePoints.insert( std::make_pair(Phase, Id) );
}


Zone::IdSet Zone::getPointIds() const
{
    IdSet   IDs;

    return IDs;     // jmoc -- fix me!?!??!?!?
}


void Zone::addRegulatorId( const Phase & Phase, const long Id )
{
    _regulatorIds.insert( std::make_pair(Phase, Id) );
}


long Zone::getRegulatorId() const
{
    return -1;      // jmoc -- fix me!?!??!?!?
}




}
}

