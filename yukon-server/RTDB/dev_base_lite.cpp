

#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dev_base_lite
*
* Date:   1/2/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_base_lite.cpp-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:45 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dev_base_lite.h"

CtiDeviceBaseLite::CtiDeviceBaseLite(LONG id) :
    _deviceID(id),
    _disableFlag("N"),
    _controlInhibitFlag("N")
{
}

CtiDeviceBaseLite::CtiDeviceBaseLite(const CtiDeviceBaseLite& aRef)
{
    LockGuard guard( monitor() );
    *this = aRef;
}

CtiDeviceBaseLite::~CtiDeviceBaseLite()
{
}

LONG CtiDeviceBaseLite::getID() const
{
    LockGuard guard( monitor() );
    return _deviceID;
}
RWCString CtiDeviceBaseLite::getName() const
{
    LockGuard guard( monitor() );
    return _name;
}

RWCString CtiDeviceBaseLite::getObjectType() const
{
    LockGuard guard( monitor() );
    return _objectType;
}

RWCString CtiDeviceBaseLite::getDisableFlag() const
{
    LockGuard guard( monitor() );
    return _disableFlag;
}

CtiDeviceBaseLite& CtiDeviceBaseLite::setDisableFlag( const RWCString &str )
{
    LockGuard guard( monitor() );
    _disableFlag = str;
    return *this;
}
CtiDeviceBaseLite& CtiDeviceBaseLite::setControlInhibitFlag( const RWCString &str )
{
    LockGuard guard( monitor() );
    _controlInhibitFlag = str;
    return *this;
}

RWCString CtiDeviceBaseLite::getControlInhibitFlag() const
{
    LockGuard guard( monitor() );
    return _controlInhibitFlag;
}


CtiDeviceBaseLite& CtiDeviceBaseLite::setID( LONG id )
{
    LockGuard guard( monitor() );
    _deviceID = id;
    return *this;
}
CtiDeviceBaseLite& CtiDeviceBaseLite::setName( const RWCString &str )
{
    LockGuard guard( monitor() );
    _name = str;
    return *this;
}

bool CtiDeviceBaseLite::operator<( const CtiDeviceBaseLite &rhs ) const
{
    LockGuard guard( monitor() );
    return(getID() < rhs.getID() );
}
bool CtiDeviceBaseLite::operator==( const CtiDeviceBaseLite &rhs ) const
{
    LockGuard guard( monitor() );
    return(getID() == rhs.getID() );
}
bool CtiDeviceBaseLite::operator()(const CtiDeviceBaseLite& aRef) const
{
    LockGuard guard( monitor() );
    return operator<(aRef);
}

void CtiDeviceBaseLite::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table(getTableName());
    RWDBTable devtable = getDatabase().table( "Device" );

    selector <<
    keyTable["paobjectid"] <<
    keyTable["paoname"] <<
    keyTable["type"] <<
    keyTable["disableflag"] <<
    devtable["controlinhibit"];

    selector.from(keyTable);
    selector.from(devtable);

    selector.where( keyTable["paobjectid"] == devtable["deviceid"] );
}

void CtiDeviceBaseLite::DecodeDatabaseReader(RWDBReader &rdr)
{
    LockGuard guard( monitor() );
    rdr["paobjectid"] >> _deviceID;
    rdr["paoname"] >> _name;
    rdr["type"] >> _objectType;
    rdr["disableflag"] >> _disableFlag;
    rdr["controlinhibit"] >> _controlInhibitFlag;
}

RWCString CtiDeviceBaseLite::getTableName()
{
    return RWCString("YukonPAObject");
}

RWDBStatus CtiDeviceBaseLite::Restore()
{
    LockGuard guard( monitor() );
    RWDBConnection conn = getConnection();
    RWLockGuard<RWDBConnection> conn_guard(conn);

    RWDBStatus dbstat;

    {
        RWDBTable table = getDatabase().table( getTableName() );
        RWDBTable devtable = getDatabase().table( "Device" );

        RWDBSelector selector = getDatabase().selector();

        selector << table["paobjectid"];
        selector << table["paoname"];
        selector << table["type"];
        selector << table["disableflag"];
        selector << devtable["controlinhibit"];

        selector.from(table);
        selector.from(devtable);

        selector.where( table["paobjectid"] == getID() && devtable["deviceid"] == getID() );

        RWDBReader reader = selector.reader( conn );
        dbstat = selector.status();

        if( reader() )
        {
            DecodeDatabaseReader( reader );
        }
    }

    return dbstat;
}

CtiDeviceBaseLite& CtiDeviceBaseLite::operator=(const CtiDeviceBaseLite& aRef)
{
    if(this != &aRef)
    {
        LockGuard guard( monitor() );
        _deviceID = aRef.getID();
        _name = aRef.getName();
        _objectType = aRef.getObjectType();
        _disableFlag = aRef.getDisableFlag();
        _controlInhibitFlag = aRef.getControlInhibitFlag();
    }
    return *this;
}

