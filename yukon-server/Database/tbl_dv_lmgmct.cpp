/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_lmgmct
*
* Date:   5/23/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2005/02/10 23:23:48 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <rw/db/reader.h>

#include "tbl_dv_lmgmct.h"
#include "dllbase.h"
#include "logger.h"

CtiTableLMGroupMCT::CtiTableLMGroupMCT()
{
}


CtiTableLMGroupMCT::CtiTableLMGroupMCT( const CtiTableLMGroupMCT& aRef )
{
    *this = aRef;
}


CtiTableLMGroupMCT::~CtiTableLMGroupMCT()
{
}


CtiTableLMGroupMCT &CtiTableLMGroupMCT::operator=( const CtiTableLMGroupMCT& aRef )
{
    if( this != &aRef )
    {
    }

    return *this;
}


RWCString CtiTableLMGroupMCT::getTableName()
{
    return RWCString("lmgroupmct");
}


unsigned int CtiTableLMGroupMCT::getRelays()
{
    return _relays;
}


unsigned long CtiTableLMGroupMCT::getAddress() const
{
    return _address;
}


CtiTableLMGroupMCT::AddressLevels CtiTableLMGroupMCT::getAddressLevel() const
{
    return _addressLevel;
}


long CtiTableLMGroupMCT::getRouteID() const
{
    return _routeID;
}


long CtiTableLMGroupMCT::getMCTUniqueAddress() const
{
    return _mctUniqueAddress;
}


void CtiTableLMGroupMCT::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable devTbl       = db.table(getTableName());
    RWDBTable mctAddrTable = db.table("devicecarriersettings");

    selector << devTbl["deviceid"  ] <<
                devTbl["mctaddress"] <<
                devTbl["mctlevel"  ] <<
                devTbl["relayusage"] <<
                devTbl["routeid"   ] <<
                mctAddrTable["address"];

    selector.from(devTbl);
    selector.from(mctAddrTable);

    selector.where(keyTable["paobjectid"] == devTbl["deviceid"] && devTbl["mctdeviceid"].leftOuterJoin(mctAddrTable["deviceid"]) && selector.where());
}


void CtiTableLMGroupMCT::DecodeDatabaseReader(RWDBReader &rdr)
{
    RWCString tmpStr;
    char *buf;

    if(getDebugLevel() & 0x0800)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"   ] >> _deviceID;
    rdr["mctaddress" ] >> _address;

    if( _address > 0 )
    {
        //  one-based in DB, zero-based elsewhere
        _address--;
    }

    rdr["mctlevel"   ] >> tmpStr;
    tmpStr.toLower();

    switch( (tmpStr.data())[0] )
    {
        case 'b':   _addressLevel = Addr_Bronze;    break;
        case 'm':   _addressLevel = Addr_Unique;    break;
        case 'l':   _addressLevel = Addr_Lead;      break;
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime( ) << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Unknown/bad address level specifier \"" << (tmpStr.data())[0] << "\" for device ID " << _deviceID << endl;
            }

            _addressLevel = Addr_Invalid;
        }
    }

    rdr["relayusage" ] >> tmpStr;

    _relays = 0;
    for( int i = 0; i < tmpStr.length(); i++ )
    {
        switch( (tmpStr.data())[i] )
        {
            case '1':   _relays |= 0x01;  break;
            case '2':   _relays |= 0x02;  break;
            case '3':   _relays |= 0x04;  break;
            case '4':   _relays |= 0x08;  break;
        }
    }

    if( !_relays )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime( ) << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "No relays selected for control in MCT load group, device ID " << _deviceID << endl;
            dout << "All shed commands will fail (restores will work, however)" << endl;
        }
    }

    rdr["routeid"] >> _routeID;
    rdr["address"] >> _mctUniqueAddress;
}


RWDBStatus CtiTableLMGroupMCT::Restore()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return RWDBStatus::notSupported;
}


RWDBStatus CtiTableLMGroupMCT::Insert()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return RWDBStatus::notSupported;
}


RWDBStatus CtiTableLMGroupMCT::Update()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    return RWDBStatus::notSupported;
}


RWDBStatus CtiTableLMGroupMCT::Delete()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    return RWDBStatus::notSupported;
}

