#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   tbl_lmg_sa_205105
*
* Date:   3/9/2004
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/05/27 02:35:32 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "cparms.h"
#include "logger.h"
#include "tbl_lmg_sa205105.h"

//=============================================================================================================
//=============================================================================================================

CtiTableSA205105Group::CtiTableSA205105Group( void )
{
}

//=============================================================================================================
//=============================================================================================================

CtiTableSA205105Group::CtiTableSA205105Group( const CtiTableSA205105Group &aRef )
{
    *this = aRef;
}

//=============================================================================================================
//=============================================================================================================

CtiTableSA205105Group::~CtiTableSA205105Group( void )
{
}

//=============================================================================================================
//=============================================================================================================

CtiTableSA205105Group& CtiTableSA205105Group::operator=( const CtiTableSA205105Group &aRef )
{
    return *this;
}

//=============================================================================================================
//=============================================================================================================

LONG CtiTableSA205105Group::getLmGroupId() const
{
    return( _lmGroupId );
}

//=============================================================================================================
//=============================================================================================================

RWCString CtiTableSA205105Group::getOperationalAddress() const
{
    return( _operationalAddress );
}

//=============================================================================================================
//=============================================================================================================

LONG CtiTableSA205105Group::getRouteId( void ) const
{
    return _routeId;
}

//=============================================================================================================
//=============================================================================================================
int CtiTableSA205105Group::getFunction(bool shed) const
{
    int function = 2;       // default to test off to prevent any bad errors.

    if(!getLoadNumber().compareTo("load 1", RWCString::ignoreCase))
    {
        function = shed ? 8 : 9;
    }
    else if(!getLoadNumber().compareTo("load 2", RWCString::ignoreCase))
    {
        function = shed ? 10 : 11;
    }
    else if(!getLoadNumber().compareTo("load 3", RWCString::ignoreCase))
    {
        if( gConfigParms.getValueAsString("PROTOCOL_SA_RESTORE123").contains("true", RWCString::ignoreCase) )
        {
            function = shed ? 1 : 6;        // Restores to 1,2,3
        }
        else
        {
            function = 1; // restores must be handled with a 7.5m shed!
        }
    }
    else if(!getLoadNumber().compareTo("load 4", RWCString::ignoreCase))
    {
        function = shed ? 3 : 4;
    }
    else if(!getLoadNumber().compareTo("load 1,2", RWCString::ignoreCase))
    {
        function = shed ? 14 : 15;
    }
    else if(!getLoadNumber().compareTo("load 1,2,3", RWCString::ignoreCase))
    {
        function = shed ? 5 : 6;
    }
    else if(!getLoadNumber().compareTo("load 1,2,3,4", RWCString::ignoreCase))
    {
        function = shed ? 12 : 13;
    }
    else if(!getLoadNumber().compareTo("test", RWCString::ignoreCase))
    {
        function = shed ? 7 : 2;                // shed ? TEST_ON : TEST_OFF;
    }
    else if(!getLoadNumber().compareTo("memory erase", RWCString::ignoreCase))
    {
        function = shed ? 0 : 0;
    }

    return function;
}
//=============================================================================================================
//=============================================================================================================

RWCString CtiTableSA205105Group::getLoadNumber( void ) const
{
    return( _loadNumber );
}

//=============================================================================================================
//=============================================================================================================

CtiTableSA205105Group& CtiTableSA205105Group::setLoadNumber( RWCString newVal )
{
    _loadNumber = newVal;
    return *this;
}

//=============================================================================================================
//=============================================================================================================

CtiTableSA205105Group& CtiTableSA205105Group::setLmGroupId( LONG newVal )
{
    _lmGroupId = newVal;
    return *this;
}

//=============================================================================================================
//=============================================================================================================

CtiTableSA205105Group& CtiTableSA205105Group::setRouteId( LONG newVal )
{
    _routeId = newVal;
    return *this;
}

//=============================================================================================================
//=============================================================================================================

CtiTableSA205105Group& CtiTableSA205105Group::setOperationalAddress( RWCString newVal )
{
    _operationalAddress = newVal;
    return *this;
}

//=============================================================================================================
//=============================================================================================================


//=============================================================================================================
//=============================================================================================================
/*
CtiTableSA205105Group& CtiTableSA205105Group::setAddressUsage( int newVal )
{
    return ;
}
*/
//=============================================================================================================
//=============================================================================================================

RWCString CtiTableSA205105Group::getTableName( void )
{
    return( RWCString( "LMGroupSA205105" ) );
}

//=============================================================================================================
//=============================================================================================================

void CtiTableSA205105Group::getSQL(RWDBDatabase &db, RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable devTbl = db.table( getTableName() );

    selector <<
        devTbl["groupid"] <<        //are these supposed to be case sensitive? the table scripts are caps!
        devTbl["routeid"] <<
        devTbl["operationaladdress"] <<
        devTbl["loadnumber"];

    selector.from(devTbl);

    selector.where( keyTable["paobjectid"] == devTbl["groupid"] && selector.where() );
}

//=============================================================================================================
//=============================================================================================================

void CtiTableSA205105Group::DecodeDatabaseReader(RWDBReader &rdr)
{
    rdr["groupid"] >> _lmGroupId;
    rdr["routeid"] >> _routeId;
    rdr["operationaladdress"] >> _operationalAddress;
    rdr["loadnumber"] >> _loadNumber;
}

//=============================================================================================================
//=============================================================================================================

RWDBStatus CtiTableSA205105Group::Restore()
{
    return RWDBStatus::notSupported;
}

//=============================================================================================================
//=============================================================================================================

RWDBStatus CtiTableSA205105Group::Insert()
{
    return RWDBStatus::notSupported;
}

//=============================================================================================================
//=============================================================================================================

RWDBStatus CtiTableSA205105Group::Update()
{
    return RWDBStatus::notSupported;
}

//=============================================================================================================
//=============================================================================================================

RWDBStatus CtiTableSA205105Group::Delete()
{
    return RWDBStatus::notSupported;
}






