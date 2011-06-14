
#pragma once

#include <string>
#include <set>

#include <boost/noncopyable.hpp>

#include "ccutil.h"

namespace Cti           {
namespace CapControl    {

class Zone : private boost::noncopyable
{

public:

    typedef std::set<long>          IdSet;
    typedef std::map<Phase, long>   PhaseIdMap;

    Zone( const long Id,
          const long parentId,
          const long subbusId,
          const std::string & name,
          const std::string & type );

    ~Zone();

    const bool operator == ( const Zone & rhs ) const;
    const bool operator != ( const Zone & rhs ) const;
    const bool operator <  ( const Zone & rhs ) const;

    long getId() const;
    long getParentId() const;
    long getSubbusId() const;
    std::string getName() const;
    bool isGangOperated() const;

    void addChildId( const long Id );
    IdSet getChildIds() const;
    void clearChildIds();

    void addBankId( const long Id );
    IdSet getBankIds() const;

    void addPointId( const Phase & Phase, const long Id );
    IdSet getPointIds() const;

    void addRegulatorId( const Phase & Phase, const long Id );
    long getRegulatorId() const;

private:

    long _Id;
    long _parentId;
    long _subbusId;
    std::string _name;
    bool _gangOperated;

    IdSet _childIds;
    IdSet _bankPaos;

    PhaseIdMap _regulatorIds;
    PhaseIdMap _voltagePoints;
};

}
}

