#include "yukon.h"


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
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2006/04/17 20:12:07 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include "logger.h"
#include "numstr.h"
#include "tbl_lmg_sasimple.h"

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
        char *p;
        char tmp[8];
        long op = strtoul(_operationalAddress.data(), &p, 10);
        long opAA, opbb, opBB;

        string opAddr;

        memset(tmp, 0, sizeof(tmp));

        tmp[0] = _operationalAddress[(size_t)0];
        tmp[1] = _operationalAddress[(size_t)1];
        opBB = strtoul(tmp, &p, 10);

        tmp[0] = _operationalAddress[(size_t)2];
        tmp[1] = _operationalAddress[(size_t)3];
        opAA = strtoul(tmp, &p, 10);

        tmp[0] = _operationalAddress[(size_t)4];
        tmp[1] = _operationalAddress[(size_t)5];
        opbb = strtoul(tmp, &p, 10);

        // This code attempts to determine the "function" based upon the bits of the A & B words (BBAABB).
        bool a_word_even = (opAA % 2) ? false : true;
        bool b_word_even = (opbb % 2) ? false : true;

        if(a_word_even && b_word_even)
        {
            // Function 1
            _function = 1;
        }
        else if(a_word_even && !b_word_even)
        {
            // Function 2
            _function = 2;
            opbb -= 1;
        }
        else if(!a_word_even && b_word_even)
        {
            // Function 3
            _function = 3;
            opAA -=1;
        }
        else if(!a_word_even && !b_word_even)
        {
            // Function 4
            _function = 4;
            opAA -= 1;
            opbb -= 1;
        }

        op = (opBB * 10000) + (opAA * 100) + opbb;  // This is the base address.
        opAddr = CtiNumStr( op );      // This is the opAddr BASE string

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Golay Opaddr: " << _operationalAddress << " == " << opAddr << " Function " << _function << endl;
        }
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
        char *p;
        char tmp[8];
        long op = strtoul(_operationalAddress.data(), &p, 10);
        long opAA, opbb, opBB;

        memset(tmp, 0, sizeof(tmp));

        tmp[0] = _operationalAddress[(size_t)0];
        tmp[1] = _operationalAddress[(size_t)1];
        opBB = strtoul(tmp, &p, 10);

        tmp[0] = _operationalAddress[(size_t)2];
        tmp[1] = _operationalAddress[(size_t)3];
        opAA = strtoul(tmp, &p, 10);

        tmp[0] = _operationalAddress[(size_t)4];
        tmp[1] = _operationalAddress[(size_t)5];
        opbb = strtoul(tmp, &p, 10);

        op = (opBB * 10000) + (opAA * 100) + opbb;  // This is the base address.
        opAddr = CtiNumStr( op );                   // This is the opAddr BASE string
    }

    return opAddr;
}
