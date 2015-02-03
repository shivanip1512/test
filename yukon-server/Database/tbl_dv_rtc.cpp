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
    string tmpStr;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB read from "<< getTableName());
    }

    rdr["deviceid"]      >> _deviceID;
    rdr["rtcaddress"] >> _rtcAddress;
    rdr["response"]  >> tmpStr;

    if(tmpStr.find("Y")!=string::npos||tmpStr.find("y")!=string::npos) _responseBit = true;
    else _responseBit = false;

    rdr["lbtmode"]  >> _lbtMode;
}

string CtiTableDeviceRTC::getTableName()
{
    return "DeviceRTC";
}


