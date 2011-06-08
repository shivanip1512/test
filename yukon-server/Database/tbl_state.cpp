#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   tbl_state
*
* Date:   12/29/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_state.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/12/20 17:16:07 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "dbaccess.h"
#include "tbl_state.h"
#include "logger.h"
#include "database_connection.h"
#include "database_reader.h"

using std::string;
using std::endl;

LONG CtiTableState::getStateGroupID() const
{

    return _stateGroupID;
}

CtiTableState& CtiTableState::setStateGroupID( const LONG id )
{

    _stateGroupID = id;
    return *this;
}

LONG CtiTableState::getRawState() const
{

    return _rawState;
}

CtiTableState& CtiTableState::setRawState( const LONG state )
{

    _rawState = state;
    return *this;
}

const string& CtiTableState::getText() const
{

    return _text;
}

CtiTableState& CtiTableState::setText( const string &str )
{

    _text = str;
    return *this;
}

bool CtiTableState::Restore()
{
    {
        static const string sql =  "SELECT ST.stategroupid, ST.rawstate, ST.text "
                                   "FROM State ST "
                                   "WHERE ST.stategroupid = ? AND ST.rawstate = ?";

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader reader(connection, sql);

        reader << getStateGroupID()
               << getRawState();

        reader.execute();

        if( reader() )
        {
            DecodeDatabaseReader( reader );
            return true;
        }
        else
        {
            char temp[40];
            sprintf(temp, "Unknown State. Value = %d", getRawState());
            _text = string(temp);
            return false;
        }
    }
}

void CtiTableState::DecodeDatabaseReader(Cti::RowReader& rdr)
{


    rdr["stategroupid"]  >> _stateGroupID;
    rdr["rawstate"]      >> _rawState;
    rdr["text"]          >> _text;

    return;
}

bool CtiTableState::operator<( const CtiTableState &rhs ) const
{

    return((getStateGroupID() < rhs.getStateGroupID()) ||
           ((getStateGroupID() == rhs.getStateGroupID()) && (getRawState() < rhs.getRawState())));
}

bool CtiTableState::operator==( const CtiTableState &rhs ) const
{

    return((getStateGroupID() == rhs.getStateGroupID()) &&
           (getRawState() == rhs.getRawState()));
}

bool CtiTableState::operator()(const CtiTableState& aRef) const
{

    return operator<(aRef);
}

void CtiTableState::dump() const
{

    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "  State Group ID " << getStateGroupID() << endl;
        dout << "  Raw State      " << getRawState() << endl;
        dout << "  Text           " << getText() << endl;
    }
    return;
}

CtiTableState::CtiTableState(LONG id, LONG raw) :             //I think this needed to add the pointID here ??
_pointID(-1),
_stateGroupID(id),
_rawState(raw)
{
    char temp[40];
    sprintf(temp, "Unknown. Value = %d", _rawState);
    _text = string(temp);
}

CtiTableState::CtiTableState(const CtiTableState& aRef)
{
    *this = aRef;
}

CtiTableState::~CtiTableState()
{
}

CtiTableState& CtiTableState::operator=(const CtiTableState& aRef)
{
    if(this != &aRef)
    {
        setStateGroupID(aRef.getStateGroupID());
        setRawState(aRef.getRawState());
        setText(aRef.getText());
    }
    return *this;
}

string CtiTableState::getTableName()
{
    return "State";
}

CtiTableState& CtiTableState::setPointID( const LONG pointID)
{
    _pointID = pointID;
    return *this;
}

LONG CtiTableState::getPointID()
{
    return _pointID;
}
