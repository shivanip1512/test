
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_lmg_sa_simple
*
* Date:   3/9/2004
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/03/10 18:44:35 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include "tbl_lmg_sa_simple.h"

//=====================================================================================================================
//=====================================================================================================================

CtiTableSASimpleGroup::CtiTableSASimpleGroup()
{
}

//=====================================================================================================================
//=====================================================================================================================

CtiTableSASimpleGroup::CtiTableSASimpleGroup(const CtiTableSASimpleGroup &aRef)
{
    *this = aRef;
}

//=====================================================================================================================
//=====================================================================================================================

CtiTableSASimpleGroup::~CtiTableSASimpleGroup()
{
}

//=====================================================================================================================
//=====================================================================================================================

CtiTableSASimpleGroup& CtiTableSASimpleGroup::operator=( const CtiTableSASimpleGroup &aRef )
{
    return *this;
}

//=====================================================================================================================
//=====================================================================================================================

LONG CtiTableSASimpleGroup::getLmGroupId() const
{
    return _lmGroupId;
}

//=====================================================================================================================
//=====================================================================================================================

LONG CtiTableSASimpleGroup::getRouteId() const
{
    return _routeId;
}

//=====================================================================================================================
//=====================================================================================================================

RWCString CtiTableSASimpleGroup::getOperationalAddress() const
{
    return _operationalAddress;
}

//=====================================================================================================================
//=====================================================================================================================

int CtiTableSASimpleGroup::getFunction() const
{
    return _function;
}

//=====================================================================================================================
//=====================================================================================================================

int CtiTableSASimpleGroup::getNominalTimeout() const
{
    return _nominalTimeout;
}

//=====================================================================================================================
//=====================================================================================================================

int CtiTableSASimpleGroup::getVirtualTimeout() const
{
    return _virtualTimeout;
}

//=====================================================================================================================
//=====================================================================================================================
                                                                    
CtiTableSASimpleGroup& CtiTableSASimpleGroup::setLmGroupId( LONG newVal )
{                                                                   
    _lmGroupId = newVal;
    return *this;
}

//=====================================================================================================================
//=====================================================================================================================

CtiTableSASimpleGroup& CtiTableSASimpleGroup::setRouteId( LONG newVal )
{
    _routeId = newVal;
    return *this;
}

//=====================================================================================================================
//=====================================================================================================================

CtiTableSASimpleGroup& CtiTableSASimpleGroup::setOperationalAddress( RWCString newVal )
{
    _operationalAddress = newVal;
    return *this;
}

//=====================================================================================================================
//=====================================================================================================================

CtiTableSASimpleGroup& CtiTableSASimpleGroup::setFunction( int newVal )
{
    _function = newVal;
    return *this;
}

//=====================================================================================================================
//=====================================================================================================================

CtiTableSASimpleGroup& CtiTableSASimpleGroup::setNominalTimeout( int newVal )
{
    _nominalTimeout = newVal;
    return *this;
}

//=====================================================================================================================
//=====================================================================================================================

CtiTableSASimpleGroup& CtiTableSASimpleGroup::setVirtualTimeout( int newVal )
{
    _virtualTimeout = newVal;
    return *this;
}

//=====================================================================================================================
//=====================================================================================================================

RWCString CtiTableSASimpleGroup::getTableName( void )
{
    return RWCString( "LMGroupSASimple" );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTableSASimpleGroup::getSQL( RWDBDatabase &db, RWDBTable &keyTable, RWDBSelector &selector )
{
    RWDBTable devTbl = db.table( getTableName() );

    selector <<
        devTbl["groupid"] <<        //are these supposed to be case sensitive? the table scripts are caps!
        devTbl["routeid"] <<
        devTbl["operationaladdress"] <<
        devTbl["nominaltimeout"] <<
        devTbl["virtualtimeout"];

    selector.from(devTbl);

    selector.where( keyTable["paobjectid"] == devTbl["deviceid"] && selector.where() );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTableSASimpleGroup::DecodeDatabaseReader( RWDBReader &rdr )
{
    rdr["groupid"]              >> _lmGroupId;
    rdr["routeid"]              >> _routeId;
    rdr["operationaladdress"]   >> _operationalAddress;
    rdr["nominalTimeout"]       >> _nominalTimeout;
    rdr["virtualTimeout"]       >> _virtualTimeout;
    rdr["function"]             >> _function;
}

//=====================================================================================================================
//=====================================================================================================================

RWDBStatus CtiTableSASimpleGroup::Restore()
{
    return RWDBStatus::notSupported;
}

//=====================================================================================================================
//=====================================================================================================================

RWDBStatus CtiTableSASimpleGroup::Insert()
{
    return RWDBStatus::notSupported;
}

//=====================================================================================================================
//=====================================================================================================================

RWDBStatus CtiTableSASimpleGroup::Update()
{
    return RWDBStatus::notSupported;
}

//=====================================================================================================================
//=====================================================================================================================

RWDBStatus CtiTableSASimpleGroup::Delete()
{
    return RWDBStatus::notSupported;
}



