#include "yukon.h"

#include "tbl_dv_tappaging.h"
#include "logger.h"

#include "database_connection.h"
#include "database_writer.h"

CtiTableDeviceTapPaging::CtiTableDeviceTapPaging(string pn) :
_deviceID(-1),
_pagerNumber(pn)
{}

CtiTableDeviceTapPaging::CtiTableDeviceTapPaging(const CtiTableDeviceTapPaging& aRef)
{
    *this = aRef;
}

CtiTableDeviceTapPaging::~CtiTableDeviceTapPaging()
{}

CtiTableDeviceTapPaging& CtiTableDeviceTapPaging::operator=(const CtiTableDeviceTapPaging& aRef)
{


    if(this != &aRef)
    {
        _deviceID    = aRef.getDeviceID();
        _pagerNumber = aRef.getPagerNumber();
        _senderID = aRef.getSenderID();
        _securityCode = aRef.getSecurityCode();
        _postPath = aRef.getPOSTPath();
    }
    return *this;
}

string CtiTableDeviceTapPaging::getPagerNumber() const
{

    return _pagerNumber;
}

string& CtiTableDeviceTapPaging::getPagerNumber()
{

    return _pagerNumber;
}

CtiTableDeviceTapPaging&   CtiTableDeviceTapPaging::setPagerNumber(const string &aStr)
{


    _pagerNumber = aStr;
    return *this;
}

void CtiTableDeviceTapPaging::DecodeDatabaseReader(Cti::RowReader &rdr)
{

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"] >> _deviceID;
    rdr["pagernumber"] >> _pagerNumber;
    rdr["sender"] >> _senderID;
    rdr["securitycode"] >> _securityCode;
    rdr["postpath"] >> _postPath;
    if( _securityCode.find("none")!=string::npos || _securityCode.compare("0")==string::npos )
    {
        _securityCode = string();    // Make it NULL.
    }
}

string CtiTableDeviceTapPaging::getTableName()
{
    return "DeviceTapPagingSettings";
}

LONG CtiTableDeviceTapPaging::getDeviceID() const
{

    return _deviceID;
}

CtiTableDeviceTapPaging& CtiTableDeviceTapPaging::setDeviceID( const LONG deviceID )
{

    _deviceID = deviceID;
    return *this;
}

string CtiTableDeviceTapPaging::getSenderID() const
{
    return _senderID;
}

string CtiTableDeviceTapPaging::getSecurityCode() const
{
    return _securityCode;
}
string CtiTableDeviceTapPaging::getPOSTPath() const
{
    return _postPath;
}


