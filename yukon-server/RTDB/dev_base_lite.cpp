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
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2003/06/05 21:12:02 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)


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

RWCString CtiDeviceBaseLite::getDescription() const
{
    LockGuard guard( monitor() );
    return _description;
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

CtiDeviceBaseLite& CtiDeviceBaseLite::setDescription( const RWCString &str )
{
    LockGuard guard( monitor() );
    _description = str;
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
    keyTable["description"] <<
    keyTable["disableflag"] <<
    devtable["controlinhibit"];

    selector.from(keyTable);
    selector.from(devtable);

    selector.where( keyTable["paobjectid"] == devtable["deviceid"] );
}

void CtiDeviceBaseLite::DecodeDatabaseReader(RWDBReader &rdr)
{
    rdr["paobjectid"] >> _deviceID;
    rdr["paoname"] >> _name;
    rdr["type"] >> _objectType;
    rdr["description"] >> _description;
    rdr["disableflag"] >> _disableFlag;
    rdr["controlinhibit"] >> _controlInhibitFlag;
}

RWCString CtiDeviceBaseLite::getTableName()
{
    return RWCString("YukonPAObject");
}

RWDBStatus CtiDeviceBaseLite::Restore()
{
    RWDBSelector selector;
    RWDBStatus dbstat = selector.status(); // Make it a not initialized ....
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema, 5000);

    if(cg.isAcquired())
    {
    RWDBConnection conn = getConnection();

        {
        RWDBTable table = getDatabase().table( getTableName() );
        RWDBTable devtable = getDatabase().table( "Device" );

            selector = getDatabase().selector();

        selector << table["paobjectid"];
        selector << table["paoname"];
        selector << table["type"];
        selector << table["description"];
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
    }

    return dbstat;
}

CtiDeviceBaseLite& CtiDeviceBaseLite::operator=(const CtiDeviceBaseLite& aRef)
{
    if(this != &aRef)
    {
        _deviceID = aRef.getID();
        _name = aRef.getName();
        _objectType = aRef.getObjectType();
        _description = aRef.getDescription();
        _disableFlag = aRef.getDisableFlag();
        _controlInhibitFlag = aRef.getControlInhibitFlag();
    }
    return *this;
}

bool CtiDeviceBaseLite::isDisabled() const
{
    RWCString str(getDisableFlag());
    str.toLower();
    return (str == "y");
}

bool CtiDeviceBaseLite::isControlInhibited() const
{
    RWCString str(getControlInhibitFlag());
    str.toLower();
    return (str == "y");
}

