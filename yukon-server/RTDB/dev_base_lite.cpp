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
* REVISION     :  $Revision: 1.16 $
* DATE         :  $Date: 2008/10/28 19:21:41 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include <iostream>
#include <sstream>

#include "dev_base_lite.h"

using std::string;

CtiDeviceBaseLite::CtiDeviceBaseLite(LONG id) :
_deviceID(id),
_portID(0),
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

LONG CtiDeviceBaseLite::getPortID() const
{
    return _portID;
}

string CtiDeviceBaseLite::getClass() const
{
    return _class;
}
string CtiDeviceBaseLite::getName() const
{
    return _name;
}

string CtiDeviceBaseLite::getDescription() const
{
    return _description;
}

string CtiDeviceBaseLite::getObjectType() const
{
    return _objectType;
}

string CtiDeviceBaseLite::getDisableFlag() const
{
    return _disableFlag;
}

CtiDeviceBaseLite& CtiDeviceBaseLite::setDisableFlag( const string &str )
{
    _disableFlag = str;
    return *this;
}
CtiDeviceBaseLite& CtiDeviceBaseLite::setControlInhibitFlag( const string &str )
{
    _controlInhibitFlag = str;
    return *this;
}

string CtiDeviceBaseLite::getControlInhibitFlag() const
{
    return _controlInhibitFlag;
}


CtiDeviceBaseLite& CtiDeviceBaseLite::setID( LONG id )
{
    _deviceID = id;
    return *this;
}
CtiDeviceBaseLite& CtiDeviceBaseLite::setPortID( LONG id )
{
    _portID = id;
    return *this;
}

CtiDeviceBaseLite& CtiDeviceBaseLite::setName( const string &str )
{
    _name = str;
    return *this;
}
CtiDeviceBaseLite& CtiDeviceBaseLite::setClass( const string &str )
{
    _class = str;
    return *this;
}

CtiDeviceBaseLite& CtiDeviceBaseLite::setDescription( const string &str )
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

string CtiDeviceBaseLite::getSQLCoreStatement(long paoid)
{
    static const string sqlNoID = "SELECT YP.paobjectid, YP.paoclass, YP.paoname, YP.type, YP.description, YP.disableflag, "
                                    "DV.controlinhibit "
                                  "FROM YukonPAObject YP "
                                  "JOIN Device DV ON YP.paobjectid = DV.deviceid";

    if( paoid )
    {
        return string(sqlNoID + " WHERE YP.paobjectid = ?");
    }
    else
    {
        return sqlNoID;
    }
}

void CtiDeviceBaseLite::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    rdr["paobjectid"] >> _deviceID;
    rdr["paoclass"] >> _class;
    rdr["paoname"] >> _name;
    rdr["type"] >> _objectType;
    rdr["description"] >> _description;
    rdr["disableflag"] >> _disableFlag;
    rdr["controlinhibit"] >> _controlInhibitFlag;
}

string CtiDeviceBaseLite::getTableName()
{
    return string("YukonPAObject");
}

bool CtiDeviceBaseLite::Restore()
{
    {
        static const string sql =  "SELECT YP.paobjectid, YP.paoclass, YP.paoname, YP.type, YP.description, "
                                       "YP.disableflag, DV.controlinhibit "
                                   "FROM YukonPAObject YP "
                                   "JOIN Device DV ON YP.paobjectid = DV.deviceid "
                                   "WHERE YP.paobjectid = ?";

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader rdr(connection, sql);

        rdr << getID();

        rdr.execute();

        if( rdr() )
        {
            DecodeDatabaseReader( rdr );
            return true;
        }
    }
    return false;
}

CtiDeviceBaseLite& CtiDeviceBaseLite::operator=(const CtiDeviceBaseLite& aRef)
{
    if(this != &aRef)
    {
        _deviceID = aRef.getID();
        _portID = aRef.getPortID();
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
    string str(getDisableFlag());
    std::transform(str.begin(), str.end(), str.begin(), tolower);
    return (str == "y");
}

bool CtiDeviceBaseLite::isControlInhibited() const
{
    string str(getControlInhibitFlag());
    std::transform(str.begin(), str.end(), str.begin(), tolower);
    return (str == "y");
}

