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
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/12/20 17:16:06 $
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

string CtiTableSA205105Group::getOperationalAddress() const
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

    if(!stringCompareIgnoreCase(getLoadNumber(),"load 1"))
    {
        function = shed ? 8 : 9;
    }
    else if(!stringCompareIgnoreCase(getLoadNumber(),"load 2"))
    {
        function = shed ? 10 : 11;
    }
    else if(!stringCompareIgnoreCase(getLoadNumber(),"load 3"))
    {
        string s = gConfigParms.getValueAsString("PROTOCOL_SA_RESTORE123");
        std::transform(s.begin(), s.end(), s.begin(), tolower);
        if( s.find("true")!=string::npos )
        {
            function = shed ? 1 : 6;        // Restores to 1,2,3
        }
        else
        {
            function = 1; // restores must be handled with a 7.5m shed!
        }
    }
    else if(!stringCompareIgnoreCase(getLoadNumber(),"load 4"))
    {
        function = shed ? 3 : 4;
    }
    else if(!stringCompareIgnoreCase(getLoadNumber(),"load 1,2"))
    {
        function = shed ? 14 : 15;
    }
    else if(!stringCompareIgnoreCase(getLoadNumber(),"load 1,2,3"))
    {
        function = shed ? 5 : 6;
    }
    else if(!stringCompareIgnoreCase(getLoadNumber(),"load 1,2,3,4"))
    {
        function = shed ? 12 : 13;
    }
    else if(!stringCompareIgnoreCase(getLoadNumber(),"test"))
    {
        function = shed ? 7 : 2;                // shed ? TEST_ON : TEST_OFF;
    }
    else if(!stringCompareIgnoreCase(getLoadNumber(),"memory erase"))
    {
        function = shed ? 0 : 0;
    }

    return function;
}
//=============================================================================================================
//=============================================================================================================

string CtiTableSA205105Group::getLoadNumber( void ) const
{
    return( _loadNumber );
}

//=============================================================================================================
//=============================================================================================================

CtiTableSA205105Group& CtiTableSA205105Group::setLoadNumber( string newVal )
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

CtiTableSA205105Group& CtiTableSA205105Group::setOperationalAddress( string newVal )
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

string CtiTableSA205105Group::getTableName( void )
{
    return( string( "LMGroupSA205105" ) );
}

//=============================================================================================================
//=============================================================================================================

void CtiTableSA205105Group::getSQL(RWDBDatabase &db, RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable devTbl = db.table( getTableName().c_str() );

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






