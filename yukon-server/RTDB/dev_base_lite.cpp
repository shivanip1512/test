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
* REVISION     :  $Revision: 1.13 $
* DATE         :  $Date: 2005/02/10 23:23:59 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"



#include "dev_base_lite.h"

CtiDeviceBaseLite::CtiDeviceBaseLite(LONG id) :
_deviceID(id),
_disableFlag("N"),
_controlInhibitFlag("N")
{
}

CtiDeviceBaseLite::CtiDeviceBaseLite(const CtiDeviceBaseLite& aRef)
{
    *this = aRef;
}

CtiDeviceBaseLite::~CtiDeviceBaseLite()
{
}

LONG CtiDeviceBaseLite::getID() const
{
    return _deviceID;
}
RWCString CtiDeviceBaseLite::getClass() const
{
    return _class;
}
RWCString CtiDeviceBaseLite::getName() const
{
    return _name;
}

RWCString CtiDeviceBaseLite::getDescription() const
{
    return _description;
}

RWCString CtiDeviceBaseLite::getObjectType() const
{
    return _objectType;
}

RWCString CtiDeviceBaseLite::getDisableFlag() const
{
    return _disableFlag;
}

CtiDeviceBaseLite& CtiDeviceBaseLite::setDisableFlag( const RWCString &str )
{
    _disableFlag = str;
    return *this;
}
CtiDeviceBaseLite& CtiDeviceBaseLite::setControlInhibitFlag( const RWCString &str )
{
    _controlInhibitFlag = str;
    return *this;
}

RWCString CtiDeviceBaseLite::getControlInhibitFlag() const
{
    return _controlInhibitFlag;
}


CtiDeviceBaseLite& CtiDeviceBaseLite::setID( LONG id )
{
    _deviceID = id;
    return *this;
}
CtiDeviceBaseLite& CtiDeviceBaseLite::setName( const RWCString &str )
{
    _name = str;
    return *this;
}
CtiDeviceBaseLite& CtiDeviceBaseLite::setClass( const RWCString &str )
{
    _class = str;
    return *this;
}

CtiDeviceBaseLite& CtiDeviceBaseLite::setDescription( const RWCString &str )
{
    _description = str;
    return *this;
}

bool CtiDeviceBaseLite::operator<( const CtiDeviceBaseLite &rhs ) const
{
    return(getID() < rhs.getID() );
}
bool CtiDeviceBaseLite::operator==( const CtiDeviceBaseLite &rhs ) const
{
    return(getID() == rhs.getID() );
}
bool CtiDeviceBaseLite::operator()(const CtiDeviceBaseLite& aRef) const
{
    return operator<(aRef);
}

void CtiDeviceBaseLite::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table(getTableName());
    RWDBTable devtable = getDatabase().table( "Device" );

    selector <<
    keyTable["paobjectid"] <<
    keyTable["paoclass"] <<
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
    rdr["paoclass"] >> _class;
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
        selector << table["paoclass"];
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
        _class = aRef.getClass();
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

