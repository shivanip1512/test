#include "precompiled.h"
#include "logger.h"
#include "dbaccess.h"
#include "resolvers.h"

#include "tbl_dv_idlcremote.h"

using std::string;
using std::endl;

CtiTableDeviceIDLC::CtiTableDeviceIDLC() :
_deviceID(-1),
_address(-1),
_postdelay(-1),
_ccuAmpUseType(RouteAmp1),
_currentAmp(0)
{}

CtiTableDeviceIDLC::~CtiTableDeviceIDLC()
{
}

LONG CtiTableDeviceIDLC::getDeviceID() const
{
    return _deviceID;
}

LONG CtiTableDeviceIDLC::getAddress() const
{
    return _address;
}

INT CtiTableDeviceIDLC::getCCUAmpUseType() const
{
    return _ccuAmpUseType;
}

void CtiTableDeviceIDLC::setCCUAmpUseType( const INT aAmpUseType )
{
    _ccuAmpUseType = aAmpUseType;
}

INT  CtiTableDeviceIDLC::getPostDelay() const
{
    return _postdelay;
}

INT CtiTableDeviceIDLC::getAmp()
{
    switch(_ccuAmpUseType)
    {
        case RouteAmpAlternating:
        case RouteAmpAltFail:
        {
            return (_currentAmp ^= 1);
        }

        case RouteAmpUndefined:
        case RouteAmp2:
        case RouteAmpDefault2Fail1:
        {
            return 1;
        }

        default:
        {
            CTILOG_ERROR(dout, "ACH Error: unexpected ccuAmpUseType ("<< _ccuAmpUseType << ")");
        }
        case RouteAmp1:
        case RouteAmpDefault1Fail2:
        {
            return 0;
        }
    }
}

void CtiTableDeviceIDLC::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    string ampStr;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB read from "<< getTableName());
    }

    rdr["deviceid"] >> _deviceID;
    rdr["address"]       >> _address;
    rdr["postcommwait"]  >> _postdelay;
    rdr["ccuampusetype"] >> ampStr;

    _ccuAmpUseType = resolveAmpUseType(ampStr);
}

string CtiTableDeviceIDLC::getTableName()
{
    return "DeviceIDLCRemote";
}

