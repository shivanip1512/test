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
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/12/20 17:16:06 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include "dbaccess.h"
#include "numstr.h"
#include "tbl_gateway_end_device.h"

#include "database_writer.h"

CtiTableGatewayEndDevice::CtiTableGatewayEndDevice() :
    _hardwareType(0),
    _dataType(0)
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
CtiTableGatewayEndDevice& CtiTableGatewayEndDevice::setSerialNumber(string sn)
{
    if(sn.length() > MAX_SERIAL_NUMBER_LENGTH)
    {
        sn.resize(  MAX_SERIAL_NUMBER_LENGTH - 1 );

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
CtiTableGatewayEndDevice& CtiTableGatewayEndDevice::setDataValue(string dv)
{
    if(dv.length() > MAX_DATA_VALUE_LENGTH)
    {
        dv.resize(MAX_DATA_VALUE_LENGTH-1);

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " Data value resized.  The defined column may be too small." << endl;
        }
    }

    _dataValue = dv;
    return *this;
}

string CtiTableGatewayEndDevice::getSerialNumber() const
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
string CtiTableGatewayEndDevice::getDataValue() const
{
    return _dataValue;
}


string CtiTableGatewayEndDevice::getTableName()
{
    return string("GatewayEndDevice");
}

void CtiTableGatewayEndDevice::DumpData()
{

}
void CtiTableGatewayEndDevice::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << CtiTime() << " **** ACHACHACHACHACHACH Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
}

bool CtiTableGatewayEndDevice::Insert()
{
    Cti::Database::DatabaseConnection   conn;

    return Insert(conn);
}

bool CtiTableGatewayEndDevice::Insert(Cti::Database::DatabaseConnection &conn)
{
    static const std::string sql = "insert into " + getTableName() + " values (?, ?, ?, ?)";

    Cti::Database::DatabaseWriter   inserter(conn, sql);

    inserter
        << getSerialNumber()
        << getHardwareType()
        << getDataType()
        << getDataValue();

    bool success = inserter.execute();

    if( success )
    {
        setDirty(false);
    }
    else
    {
        success = inserter.execute();

        if( ! success )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Unable to insert GatewayEndDevice for serial number " << getSerialNumber() << ". " << __FILE__ << " (" << __LINE__ << ") " << endl;
            dout << "   " << inserter.asString() << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " InsertedGatewayEndDevice for serial number " << getSerialNumber() << ". " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "   " << inserter.asString() << endl;
            setDirty(false);
        }
    }
    return success;
}


bool CtiTableGatewayEndDevice::Update()
{
    static const std::string sql = "update " + getTableName() +
                                   " set "
                                        "datavalue = ?"
                                    " where "
                                        "serialnumber = ? and "
                                        "hardwaretype = ? and "
                                        "datatype = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       updater(conn, sql);

    updater
        << getDataValue().c_str()
        << getSerialNumber().c_str()
        << getHardwareType()
        << getDataType();

    bool success      = updater.execute();
    long rowsAffected = updater.rowsAffected();

    if( success && rowsAffected > 0)
    {
        setDirty(false);
    }
    else
    {
        success = Insert();        // Try a vanilla insert if the update failed!
    }

    return success;
}

bool CtiTableGatewayEndDevice::Delete()
{
    static const std::string sql = "delete from " + getTableName() + " where "
                                   "serialnumber = ? and hardwaretype = ? and datatype = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       deleter(conn, sql);

    deleter 
        << getSerialNumber()
        << getHardwareType()
        << getDataType();

    return deleter.execute();
}


