#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   tbl_gateway_end_device
*
* Date:   7/16/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2005/02/10 23:23:48 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include "dbaccess.h"
#include "numstr.h"
#include "tbl_gateway_end_device.h"


CtiTableGatewayEndDevice::CtiTableGatewayEndDevice()
{

}

CtiTableGatewayEndDevice::CtiTableGatewayEndDevice(const CtiTableGatewayEndDevice& aRef)
{
    *this = aRef;
}

CtiTableGatewayEndDevice::~CtiTableGatewayEndDevice()
{
}

CtiTableGatewayEndDevice& CtiTableGatewayEndDevice::operator=(const CtiTableGatewayEndDevice& aRef)
{
    if(this != &aRef)
    {
        setSerialNumber(aRef.getSerialNumber());
        setHardwareType(aRef.getHardwareType());
        setDataType(aRef.getDataType());
        setDataValue(aRef.getDataValue());
    }
    return *this;
}

CtiTableGatewayEndDevice& CtiTableGatewayEndDevice::setSerialNumber(ULONG sn)
{
    _serialNumber = CtiNumStr( sn );
    return *this;
}
CtiTableGatewayEndDevice& CtiTableGatewayEndDevice::setSerialNumber(RWCString sn)
{
    if(sn.length() > MAX_SERIAL_NUMBER_LENGTH)
    {
        sn.resize(  MAX_SERIAL_NUMBER_LENGTH - 1 );

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " Serial Number resized.  The defined column may be too small." << endl;
        }
    }

    _serialNumber = sn;
    return *this;
}
CtiTableGatewayEndDevice& CtiTableGatewayEndDevice::setHardwareType(UINT hwt)
{
    _hardwareType = hwt;
    return *this;
}
CtiTableGatewayEndDevice& CtiTableGatewayEndDevice::setDataType(UINT dt)
{
    _dataType = dt;
    return *this;
}
CtiTableGatewayEndDevice& CtiTableGatewayEndDevice::setDataValue(RWCString dv)
{
    if(dv.length() > MAX_DATA_VALUE_LENGTH)
    {
        dv.resize(MAX_DATA_VALUE_LENGTH-1);

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " Data value resized.  The defined column may be too small." << endl;
        }
    }

    _dataValue = dv;
    return *this;
}

RWCString CtiTableGatewayEndDevice::getSerialNumber() const
{
    return _serialNumber;
}
UINT  CtiTableGatewayEndDevice::getHardwareType() const
{
    return _hardwareType;
}
UINT  CtiTableGatewayEndDevice::getDataType() const
{
    return _dataType;
}
RWCString CtiTableGatewayEndDevice::getDataValue() const
{
    return _dataValue;
}


RWCString CtiTableGatewayEndDevice::getTableName()
{
    return RWCString("GatewayEndDevice");
}
void CtiTableGatewayEndDevice::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable tbl = db.table( getTableName() );

    selector <<
        tbl["serialnumber"] <<
        tbl["hardwaretype"] <<
        tbl["datatype"] <<
        tbl["datavalue"];

    selector.from(tbl);
}
void CtiTableGatewayEndDevice::DumpData()
{

}
void CtiTableGatewayEndDevice::DecodeDatabaseReader(RWDBReader &rdr)
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << RWTime() << " **** ACHACHACHACHACHACH Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
}

RWDBStatus CtiTableGatewayEndDevice::Insert()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    return Insert(conn);
}

RWDBStatus CtiTableGatewayEndDevice::Insert(RWDBConnection &conn)
{
    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();

    inserter <<
        getSerialNumber() <<
        getHardwareType() <<
        getDataType() <<
        getDataValue();

    if( inserter.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }
    else
    {
        RWDBResult res =  inserter.execute( conn );

        if( res.status().errorCode() != RWDBStatus::ok )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Unable to insert GatewayEndDevice for serial number " << getSerialNumber() << ". " << __FILE__ << " (" << __LINE__ << ") " << res.status().errorCode() << endl;
            dout << "   " << inserter.asString() << endl;
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " InsertedGatewayEndDevice for serial number " << getSerialNumber() << ". " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "   " << inserter.asString() << endl;
            }
            setDirty(false);
        }
    }
    return inserter.status();
}


RWDBStatus CtiTableGatewayEndDevice::Update()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["serialnumber"] == getSerialNumber() &&
                   table["hardwaretype"] == getHardwareType() &&
                   table["datatype"] == getDataType() );

    updater << table["datavalue"].assign( getDataValue() );

    RWDBResult myResult = updater.execute( conn );
    RWDBStatus stat = myResult.status();
    RWDBStatus::ErrorCode ec = stat.errorCode();

    RWDBTable myTable = myResult.table();
    long rowsAffected = myResult.rowCount();

    if( ec == RWDBStatus::ok && rowsAffected > 0)
    {
        setDirty(false);
    }
    else
    {
        stat = Insert();        // Try a vanilla insert if the update failed!
    }

    return stat;
}

RWDBStatus CtiTableGatewayEndDevice::Delete()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["serialnumber"] == getSerialNumber() &&
                   table["hardwaretype"] == getHardwareType() &&
                   table["datatype"] == getDataType() );
    deleter.execute( conn );
    return deleter.status();
}


