/*-----------------------------------------------------------------------------*
*
* File:   tbl_scanrate
*
* Date:   8/16/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_scanrate.cpp-arc  $
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2005/12/20 17:16:07 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_scanrate.h"

#include "database_connection.h"
#include "database_reader.h"

using std::string;
using std::endl;

CtiTableDeviceScanRate::CtiTableDeviceScanRate() :
_deviceID(-1),
_scanGroup(0),
_scanRate(-1L),
_scanType(0),
_alternateRate(-1L),
_updated(FALSE)
{}

CtiTableDeviceScanRate::CtiTableDeviceScanRate(const CtiTableDeviceScanRate &aRef)
{
    *this = aRef;
}

CtiTableDeviceScanRate& CtiTableDeviceScanRate::operator=(const CtiTableDeviceScanRate &aRef)
{
    if(this != &aRef)
    {
        _deviceID      = aRef.getDeviceID();
        _scanRate      = aRef.getScanRate();
        _scanGroup     = aRef.getScanGroup();
        _scanType      = aRef.getScanType();
        _alternateRate = aRef.getAlternateRate();

        _updated = FALSE;
    }

    return *this;
}

LONG CtiTableDeviceScanRate::getScanType() const
{

    return _scanType;
}

CtiTableDeviceScanRate& CtiTableDeviceScanRate::setScanType( const LONG aScanType )
{

    _scanType = aScanType;
    return *this;
}

INT CtiTableDeviceScanRate::getScanGroup() const
{

    return _scanGroup;
}

CtiTableDeviceScanRate& CtiTableDeviceScanRate::setScanGroup( const INT aInt )
{

    _scanGroup = aInt;
    return *this;
}

INT CtiTableDeviceScanRate::getAlternateRate() const
{

    return _alternateRate;
}

LONG CtiTableDeviceScanRate::getScanRate() const
{

    return _scanRate;
}

CtiTableDeviceScanRate& CtiTableDeviceScanRate::setScanRate( const LONG rate )
{

    _scanRate = rate;
    return *this;
}

BOOL CtiTableDeviceScanRate::getUpdated() const
{

    return _updated;
}

CtiTableDeviceScanRate& CtiTableDeviceScanRate::setUpdated( const BOOL aBool )
{

    _updated = aBool;
    return *this;
}

string CtiTableDeviceScanRate::addIDSQLClause(const Cti::Database::id_set &paoids)
{
    string sqlIDs;

    if( !paoids.empty() )
    {
        std::ostringstream in_list;

        if( paoids.size() == 1 )
        {
            //  special single id case
    
            in_list << *(paoids.begin());
             
            sqlIDs += "AND DV.deviceid = " + in_list.str();
    
            return sqlIDs;
        }
        else
        {
            in_list << "(";
        
            copy(paoids.begin(), paoids.end(), csv_output_iterator<long, std::ostringstream>(in_list));
        
            in_list << ")";

            sqlIDs += "AND DV.deviceid IN " + in_list.str();

            return sqlIDs;
        }
    }

    return string();
}

string CtiTableDeviceScanRate::getSQLCoreStatement()
{
    static const string sqlCore =  "SELECT DV.deviceid, DSR.scantype, DSR.intervalrate, DSR.scangroup, DSR.alternaterate "
                                   "FROM Device DV, DeviceScanRate DSR "
                                   "WHERE DV.deviceid = DSR.deviceid";

    return sqlCore;
}

void CtiTableDeviceScanRate::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    string rwstemp;

    rdr["scantype"] >> rwstemp;

    _scanType = resolveScanType( rwstemp ); //??????
        rdr["intervalrate"] >> _scanRate;
        rdr["scangroup"] >> _scanGroup;
        rdr["alternaterate"] >> _alternateRate;

        _updated = TRUE;                    // _ONLY_ _ONLY_ place this is set.
    }

void CtiTableDeviceScanRate::DumpData()
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);

    dout << " Scan Type                                   : " << desolveScanType( _scanType) << endl;
    dout << " Scan Rate                                   : " << _scanRate  << endl;
    dout << " Scan Group                                  : " << _scanGroup << endl;
    dout << " Alternate Rate                              : " << _alternateRate << endl;
}

LONG CtiTableDeviceScanRate::getDeviceID() const
{

    return _deviceID;
}

CtiTableDeviceScanRate& CtiTableDeviceScanRate::setDeviceID( const LONG deviceID )
{

    _deviceID = deviceID;
    return *this;
}

string CtiTableDeviceScanRate::getTableName()
{
    return "DeviceScanRate";
}
