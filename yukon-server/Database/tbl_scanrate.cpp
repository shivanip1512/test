#include "precompiled.h"

#include "tbl_scanrate.h"
#include "database_connection.h"
#include "database_reader.h"
#include "desolvers.h"

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
        CTILOG_DEBUG(dout, "Decoding DB read from "<< getTableName());
    }

    string scantypeStr;

    rdr["scantype"] >> scantypeStr;

    _scanType = resolveScanType( scantypeStr ); //??????
    rdr["intervalrate"] >> _scanRate;
    rdr["scangroup"] >> _scanGroup;
    rdr["alternaterate"] >> _alternateRate;

    _updated = TRUE;                    // _ONLY_ _ONLY_ place this is set.
}

std::string CtiTableDeviceScanRate::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiTableDeviceScanRate";
    itemList.add("Scan Type")      << desolveScanType(_scanType);
    itemList.add("Scan Rate")      << _scanRate;
    itemList.add("Scan Group")     << _scanGroup;
    itemList.add("Alternate Rate") << _alternateRate;

    return itemList.toString();
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
