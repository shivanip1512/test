
#pragma once

#include "ccutil.h"

#include <string>
#include <set>

#include <boost/noncopyable.hpp>

namespace Cti           {
namespace CapControl    {

class Zone : private boost::noncopyable
{

public:

    static const std::string GangOperated;

    typedef std::set<long>              IdSet;
    typedef std::map<Phase, long>       PhaseIdMap;
    typedef std::multimap<Phase, long>  PhaseToVoltagePointIds;

    Zone( const long Id,
          const long parentId,
          const long subbusId,
          const std::string & name,
          const std::string & type = GangOperated );    // this 'type' field is unnecessary for the server because
                                                        //  we can get this info from the attached regulator(s) phase
                                                        // information.

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

    void addPointId( const Phase phase, const long Id );
    PhaseToVoltagePointIds getPointIds() const;

    void addRegulatorId( const Phase phase, const long Id );
    PhaseIdMap getRegulatorIds() const;

private:

    long _Id;
    long _parentId;
    long _subbusId;
    std::string _name;
    bool _gangOperated;

    IdSet _childIds;
    IdSet _bankPaos;

    PhaseIdMap _regulatorIds;
    PhaseToVoltagePointIds _voltagePoints;
};

}
}

