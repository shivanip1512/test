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
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/02/10 23:23:49 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "dbaccess.h"
#include "tbl_state.h"
#include "logger.h"

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

const RWCString& CtiTableState::getText() const
{

    return _text;
}

CtiTableState& CtiTableState::setText( const RWCString &str )
{

    _text = str;
    return *this;
}

void CtiTableState::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table( getTableName() );

    selector <<
    keyTable["stategroupid"] <<
    keyTable["rawstate"] <<
    keyTable["text"];

    selector.from(keyTable);
}

RWDBStatus CtiTableState::Insert()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();



    inserter <<
    getStateGroupID() <<
    getText() <<
    getRawState() <<
    (INT)0 <<      // Foreground
    (INT)6;        // Background

    inserter.execute( conn );

    return inserter.status();
}

RWDBStatus CtiTableState::Update()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();



    updater.where( table["stategroupid"] == getStateGroupID() && table["rawstate"] == getRawState());

    updater << table["text"].assign( getText() );

    updater.execute( conn );

    return updater.status();
}

RWDBStatus CtiTableState::Restore()
{

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBStatus dbstat;

    {
        RWDBTable table = getDatabase().table( getTableName() );
        RWDBSelector selector = getDatabase().selector();

        selector << table["stategroupid"]
        << table["rawstate"]
        << table["text"];

        selector.where( table["stategroupid"] == getStateGroupID() && table["rawstate"] == getRawState());

        RWDBReader reader = selector.reader( conn );

        dbstat = selector.status();

        if( reader() )
        {
            DecodeDatabaseReader( reader );
        }
        else
        {
            char temp[40];
            sprintf(temp, "Unknown State. Value = %d", getRawState());
            _text = RWCString(temp);
        }
    }

    return dbstat;
}

RWDBStatus CtiTableState::Delete()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["stategroupid"] == getStateGroupID() && table["rawstate"] == getRawState());

    return deleter.execute( conn ).status();
}

void CtiTableState::DecodeDatabaseReader(RWDBReader& rdr)
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
    _text = RWCString(temp);
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

RWCString CtiTableState::getTableName()
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
