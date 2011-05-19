
#pragma once

#include <string>
#include <set>

#include <boost/noncopyable.hpp>


namespace Cti           {
namespace CapControl    {

class Zone : private boost::noncopyable
{

public:

    typedef std::set<long>          IdSet;
    typedef std::map<char, long>    RegulatorIdMap;

    Zone( const long Id,
          const long parentId,
          const long subbusId,
          const std::string & name,
          const std::string & type = std::string("GANG_OPERATED") );

    ~Zone();

    const bool operator == ( const Zone & rhs ) const;
    const bool operator != ( const Zone & rhs ) const;
    const bool operator <  ( const Zone & rhs ) const;

    // Accessors

    long getId() const;
    long getParentId() const;

    long getRegulatorId() const;

    long getSubbusId() const;
    std::string getName() const;

    IdSet getChildIds() const;

    IdSet getBankIds() const;
    IdSet getPointIds() const;

    // Mutators

    void addRegulatorId( const char Phase, const long Id );

    void addChildId( const long Id );
    void clearChildIds();

    void addBankId( const long Id );
    void addPointId( const long Id );

private:

    long _Id;
    long _parentId;

    RegulatorIdMap _regulatorIds;

    long _subbusId;

    std::string _name;
    std::string _type;

    IdSet _childIds;

    IdSet _bankPaos;
    IdSet _voltagePoints;
};

}   // namespace Cti
}   // namespace CapControl

