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
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2006/04/21 15:17:07 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "logger.h"
#include "numstr.h"
#include "tbl_lmg_sasimple.h"
#include "prot_sa3rdparty.h"

#include "rwutil.h"

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

string CtiTableSASimpleGroup::getOperationalAddress() const
{
    return _operationalAddress;
}

//=====================================================================================================================
//=====================================================================================================================

int CtiTableSASimpleGroup::getFunction(bool control) const
{
    return _function;
}

//=====================================================================================================================
//=====================================================================================================================

int CtiTableSASimpleGroup::getNominalTimeout() const
{
    return _nominalTimeout;
}

int CtiTableSASimpleGroup::getMarkIndex() const
{
    return _markIndex;
}

int CtiTableSASimpleGroup::getSpaceIndex() const
{
    return _spaceIndex;
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

CtiTableSASimpleGroup& CtiTableSASimpleGroup::setOperationalAddress( string newVal )
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

string CtiTableSASimpleGroup::getTableName( void )
{
    return string( "LMGroupSASimple" );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTableSASimpleGroup::getSQL( RWDBDatabase &db, RWDBTable &keyTable, RWDBSelector &selector )
{
    RWDBTable devTbl = db.table( getTableName().c_str() );

    selector <<
        devTbl["groupid"] <<        //are these supposed to be case sensitive? the table scripts are caps!
        devTbl["routeid"] <<
        devTbl["operationaladdress"] <<
        devTbl["nominaltimeout"] <<
        devTbl["markindex"] <<
        devTbl["spaceindex"];

    selector.from(devTbl);

    selector.where( keyTable["paobjectid"] == devTbl["groupid"] && selector.where() );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTableSASimpleGroup::DecodeDatabaseReader( RWDBReader &rdr )
{
    rdr["groupid"]              >> _lmGroupId;
    rdr["routeid"]              >> _routeId;
    rdr["operationaladdress"]   >> _operationalAddress;
    rdr["nominaltimeout"]       >> _nominalTimeout;
    rdr["markindex"]            >> _markIndex;
    rdr["spaceindex"]           >> _spaceIndex;

    if( _operationalAddress.length() >= 6 )
    {
        std::pair< int, int > golay_address = CtiProtocolSA3rdParty::parseGolayAddress(_operationalAddress.data());

        _function = golay_address.second;

        #if 0
        string padded_address = CtiNumStr(golay_address.first).zpad(6);

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Golay Opaddr: " << _operationalAddress << " == " << padded_address << " Function " << golay_address.second << endl;
        }
        #endif
    }
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

string CtiTableSASimpleGroup::getGolayOperationalAddress() const
{
    string opAddr("000000");

    if( _operationalAddress.length() >= 6 )
    {
        std::pair< int, int > golay_address = CtiProtocolSA3rdParty::parseGolayAddress(_operationalAddress.data());

        opAddr = CtiNumStr(golay_address.first).zpad(6);    // This is the opAddr BASE string

        #if 0
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Golay Opaddr: " << _operationalAddress << " == " << golay_address.first << " Function " << golay_address.second << endl;
        }
        #endif
    }

    return opAddr;
}
