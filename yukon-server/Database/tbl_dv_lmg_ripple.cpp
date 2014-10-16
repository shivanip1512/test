#include "precompiled.h"

#include <assert.h>
#include "logger.h"
#include "tbl_dv_lmg_ripple.h"

#include "database_connection.h"
#include "database_writer.h"

using std::string;
using std::endl;

CtiTableRippleLoadGroup::CtiTableRippleLoadGroup() :
_controlBits((size_t)7, (char)0),      // 7 zeros
_restoreBits((size_t)7, (char)0),      // 7 zeros
_routeID(-1),
_deviceID(0),
_shedTime(0)
{}

CtiTableRippleLoadGroup::~CtiTableRippleLoadGroup() {}

LONG  CtiTableRippleLoadGroup::getRouteID() const
{
    return _routeID;
}

CtiTableRippleLoadGroup& CtiTableRippleLoadGroup::setRouteID( const LONG a_routeID )
{
    _routeID = a_routeID;
    return *this;
}

string CtiTableRippleLoadGroup::getControlBits() const
{
    return _controlBits;
}

BYTE  CtiTableRippleLoadGroup::getControlBit(INT i)
{
    assert( i >= 0 && i < 8);
    return _controlBits[(size_t)i];
}

CtiTableRippleLoadGroup& CtiTableRippleLoadGroup::setControlBits( const string str )
{
    _controlBits = str;
    return *this;
}

CtiTableRippleLoadGroup& CtiTableRippleLoadGroup::setControlBit( INT pos, const BYTE ch )
{
    _controlBits[(size_t)pos] = ch;
    return *this;
}

string  CtiTableRippleLoadGroup::getRestoreBits() const
{
    return _restoreBits;
}

BYTE  CtiTableRippleLoadGroup::getRestoreBit(INT i)
{
    assert( i >= 0 && i < 8);
    return _restoreBits[(size_t)i];
}

CtiTableRippleLoadGroup& CtiTableRippleLoadGroup::setRestoreBits( const string str )
{
    _restoreBits = str;
    return *this;
}

CtiTableRippleLoadGroup& CtiTableRippleLoadGroup::setRestoreBit( INT pos, const BYTE ch )
{
    _restoreBits[(size_t)pos] = ch;
    return *this;
}

string CtiTableRippleLoadGroup::getTableName()
{
    return string("LMGroupRipple");
}

LONG CtiTableRippleLoadGroup::getDeviceID() const
{

    return _deviceID;
}


LONG CtiTableRippleLoadGroup::getShedTime() const
{

    return _shedTime > 0 ? _shedTime : (31536000);       // Continuous Latch devices (0 == _shedtime) will control for 1 year.
}

CtiTableRippleLoadGroup& CtiTableRippleLoadGroup::setDeviceID( const LONG deviceID)
{

    _deviceID = deviceID;
    return *this;
}

CtiTableRippleLoadGroup& CtiTableRippleLoadGroup::setShedTime( const LONG shedTime)
{

    _shedTime = shedTime;
    return *this;
}

void CtiTableRippleLoadGroup::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB read from "<< getTableName());
    }

    rdr["deviceid"] >> _deviceID;
    rdr["controlvalue"] >> _controlBits;        // Fix Me -ecs
    rdr["restorevalue"] >> _restoreBits;
    rdr["shedtime"] >> _shedTime;
    rdr["routeid"] >> _routeID;
}

bool CtiTableRippleLoadGroup::copyMessage(BYTE *bptr, bool shed) const
{
    try
    {
        int i;
        if(shed == true)
        {
            for(i = 0; i < 50; i++)
            {
                if(_controlBits[(size_t) i ] == '1')
                {
                    int offset = i / 8;
                    int shift = (7 - i % 8);

                    bptr[ offset ] |= (0x01 << shift);
                }
            }
        }
        else
        {
            for(i = 0; i < 50; i++)
            {
                if(_restoreBits[(size_t) i ] == '1')
                {
                    int offset = i / 8;
                    int shift = (7 - i % 8);

                    bptr[ offset ] |= (0x01 << shift);
                }
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
    return false;
}
