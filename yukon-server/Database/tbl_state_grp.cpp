#include "precompiled.h"

#include "tbl_state_grp.h"

#include "database_connection.h"
#include "database_reader.h"
#include "guard.h"
#include "logger.h"

using namespace std;

LONG CtiTableStateGroup::getStateGroupID() const
{
    return _stateGroupID;
}

const string& CtiTableStateGroup::getName() const
{
    return _name;
}

bool CtiTableStateGroup::Restore()
{
    bool retVal = false;
    {
        static const string sql =  "SELECT SG.stategroupid, SG.name "
                                   "FROM StateGroup SG "
                                   "WHERE SG.stategroupid = ?";

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader reader(connection, sql);

        reader << getStateGroupID();

        reader.execute();

        if( reader() )
        {
            DecodeDatabaseReader( reader );
            retVal = true;
        }
    }

    // Now refresh any states I've already loaded up in the past!
    if( !_stateMap.empty() )
    {
        bool bStatesBad = false;

        for( auto &kv : _stateMap )
        {
            CtiTableState &theState = kv.second;

            if(!theState.Restore())
            {
                // There is something very very wrong with this state, it should be blown away.
                bStatesBad = true;
                break;
            }
        }

        if(bStatesBad)
        {
            _stateMap.clear();      // Get rid of the offender by making all good states be reloaded on next usage
        }
    }

    return retVal;
}


void CtiTableStateGroup::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    rdr["stategroupid"] >>   _stateGroupID;
    rdr["name"] >>           _name;
}

std::string CtiTableStateGroup::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiTableStateGroup";
    itemList.add("State Group ID") << getStateGroupID();
    itemList.add("Name")           << getName();

    unsigned index = 0;
    for( const auto &kv : _stateMap )
    {
        itemList<<"state index "<< index++;
        itemList<<kv.second;
    }

    return itemList.toString();
}

string CtiTableStateGroup::getRawState(LONG rawValue)
{
    string rStr;      // NULL string

    auto sit = _stateMap.find( rawValue );

    if( sit == _stateMap.end() )
    {
        CtiTableState mystate( getStateGroupID(), rawValue  );
        // We need to load it up, and/or then insert it!
        if( mystate.Restore() )
        {
            // Try to insert. Return indicates success.
            auto resultpair = _stateMap.emplace( rawValue, mystate );

            if(resultpair.second == true)   // Insertion occured
            {
                sit = resultpair.first;      // Iterator which points to the set entry.
            }
        }
    }

    if( sit != _stateMap.end() )
    {
        CtiTableState &theState = sit->second;

        rStr = theState.getText();
    }

    return rStr;
}

CtiTableStateGroup::CtiTableStateGroup(LONG id) :
_stateGroupID(id)
{}

