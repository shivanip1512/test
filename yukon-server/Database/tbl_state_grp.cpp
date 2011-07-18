#include "precompiled.h"


/*-----------------------------------------------------------------------------*
*
* File:   tbl_state_grp
*
* Date:   12/29/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_state_grp.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/12/20 17:16:07 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dbaccess.h"
#include "mutex.h"
#include "guard.h"
#include "tbl_state_grp.h"
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
const CtiTableStateGroup::CtiStateSet_t& CtiTableStateGroup::getStateSet() const
{

    return _stateSet;
}

CtiTableStateGroup& CtiTableStateGroup::setStateGroupID( const LONG id )
{

    _stateGroupID = id;
    return *this;
}
CtiTableStateGroup& CtiTableStateGroup::setName( const string &str )
{

    _name = str;
    return *this;
}
CtiTableStateGroup& CtiTableStateGroup::setStateSet( const CtiStateSet_t& aSet )
{

    _stateSet = aSet;
    return *this;
}

string CtiTableStateGroup::getTableName()
{
    return string("StateGroup");
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
    {
        CtiLockGuard<CtiMutex> stateguard(_stateMux);   // Lock down the states!

        if( !_stateSet.empty() )
        {
            bool bStatesBad = false;
            CtiStateSet_t::iterator sit;

            for(sit = _stateSet.begin(); sit != _stateSet.end(); sit++)
            {
                CtiTableState &theState = *sit;

                if(!theState.Restore())
                {
                    // There is something very very wrong with this state, it should be blown away.
                    bStatesBad = true;
                    break;
                }
            }

            if(bStatesBad)
            {
                _stateSet.clear();      // Get rid of the offender by making all good states be reloaded on next usage
            }
        }
    }

    return retVal;
}


bool CtiTableStateGroup::operator<( const CtiTableStateGroup &rhs ) const
{

    return(getStateGroupID() < rhs.getStateGroupID());
}

bool CtiTableStateGroup::operator==( const CtiTableStateGroup &rhs ) const
{

    return(getStateGroupID() == rhs.getStateGroupID());
}

bool CtiTableStateGroup::operator()(const CtiTableStateGroup& aRef) const
{

    return operator<(aRef);
}

void CtiTableStateGroup::DecodeDatabaseReader(Cti::RowReader &rdr)
{


    rdr["stategroupid"] >>   _stateGroupID;
    rdr["name"] >>           _name;

    return;
}

void CtiTableStateGroup::dump() const
{


    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "State Group ID " << getStateGroupID() << endl;
        dout << "Name" << " Value " << getName() << endl;
    }

    CtiStateSet_t::const_iterator sit;

    for(sit = _stateSet.begin(); sit != _stateSet.end(); sit++)
    {
        const CtiTableState &theState = *sit;
        theState.dump();
    }
}

string CtiTableStateGroup::getRawState(LONG rawValue)
{
    string rStr;      // NULL string


    CtiTableState mystate( getStateGroupID(), rawValue  );
    CtiLockGuard<CtiMutex> stateguard(_stateMux);   // Lock down the states!
    CtiStateSet_t::iterator sit = _stateSet.find( mystate );

    if( sit == _stateSet.end() )
    {
        // We need to load it up, and/or then insert it!
        if( mystate.Restore() )
        {
            pair< CtiStateSet_t::iterator, bool > resultpair;

            // Try to insert. Return indicates success.
            resultpair = _stateSet.insert( mystate );

            if(resultpair.second == true)   // Insertion occured
            {
                sit = resultpair.first;      // Iterator which points to the set entry.
            }
        }
    }

    if( sit != _stateSet.end() )
    {
        CtiTableState &theState = *sit;

        rStr = theState.getText();
    }

    return rStr;
}

CtiTableStateGroup::CtiTableStateGroup(LONG id) :
_stateGroupID(id)
{}

CtiTableStateGroup::CtiTableStateGroup(const CtiTableStateGroup& aRef)
{
    *this = aRef;
}

CtiTableStateGroup::~CtiTableStateGroup() {}

CtiTableStateGroup& CtiTableStateGroup::operator=(const CtiTableStateGroup& aRef)
{
    if(this != &aRef)
    {

        setStateGroupID(aRef.getStateGroupID());
        setName(aRef.getName());
        setStateSet(aRef.getStateSet());
    }

    return *this;
}


