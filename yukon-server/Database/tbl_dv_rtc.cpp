#include "precompiled.h"

////#include "row_reader.h"

#include "dbaccess.h"
#include "dlldefs.h"
#include "dllbase.h"
#include "logger.h"
#include "tbl_dv_rtc.h"

using std::string;
using std::endl;

CtiTableDeviceRTC::CtiTableDeviceRTC() :
    _deviceID(-1),
    _rtcAddress(0),
    _lbtMode(CtiTableDeviceRTC::NoLBT),
    _responseBit(true)
{}

CtiTableDeviceRTC::~CtiTableDeviceRTC()
{
}

LONG CtiTableDeviceRTC::getDeviceID() const
{
    return _deviceID;
}

int CtiTableDeviceRTC::getRTCAddress() const
{
    return _rtcAddress;
}

bool CtiTableDeviceRTC::getResponseBit() const
{
    return _responseBit;
}
int  CtiTableDeviceRTC::getLBTMode() const
{
    return _lbtMode;
}

void CtiTableDeviceRTC::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    string rwsTemp;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"]      >> _deviceID;
    rdr["rtcaddress"] >> _rtcAddress;
    rdr["response"]  >> rwsTemp;

    if(rwsTemp.find("Y")!=string::npos||rwsTemp.find("y")!=string::npos) _responseBit = true;
    else _responseBit = false;

    rdr["lbtmode"]  >> _lbtMode;
}

string CtiTableDeviceRTC::getTableName()
{
    return "DeviceRTC";
}


