#include "yukon.h"
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

CtiTableDeviceIDLC::CtiTableDeviceIDLC(const CtiTableDeviceIDLC& aRef)
{
    *this = aRef;
}

CtiTableDeviceIDLC::~CtiTableDeviceIDLC()
{
}

CtiTableDeviceIDLC& CtiTableDeviceIDLC::operator=(const CtiTableDeviceIDLC& aRef)
{
    if(this != &aRef)
    {
        _deviceID      = aRef.getDeviceID();
        _address       = aRef.getAddress();
        _postdelay     = aRef.getPostDelay();
        _ccuAmpUseType = aRef.getCCUAmpUseType();
    }
    return *this;
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
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** ACH Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
    string rwsTemp;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"] >> _deviceID;
    rdr["address"]       >> _address;
    rdr["postcommwait"]  >> _postdelay;
    rdr["ccuampusetype"] >> rwsTemp;

    _ccuAmpUseType = resolveAmpUseType(rwsTemp);
}

string CtiTableDeviceIDLC::getTableName()
{
    return "DeviceIDLCRemote";
}

