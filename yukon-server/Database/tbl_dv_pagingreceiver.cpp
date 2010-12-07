#include "yukon.h"

#include "tbl_dv_pagingreceiver.h"
#include "logger.h"
#include "database_connection.h"
#include "database_writer.h"

CtiTableDevicePagingReceiver::CtiTableDevicePagingReceiver() :
_frequency(0),
_capcode1(0), _capcode2(0),
_capcode3(0), _capcode4(0), _capcode5(0),
_capcode6(0), _capcode7(0), _capcode8(0), _capcode9(0),
_capcode10(0), _capcode11(0), _capcode12(0), _capcode13(0),
_capcode14(0), _capcode15(0), _capcode16(0), _deviceID(0)
{}

CtiTableDevicePagingReceiver::CtiTableDevicePagingReceiver(const CtiTableDevicePagingReceiver& aRef)
{
    *this = aRef;
}

CtiTableDevicePagingReceiver::~CtiTableDevicePagingReceiver()
{}

CtiTableDevicePagingReceiver& CtiTableDevicePagingReceiver::operator=(const CtiTableDevicePagingReceiver& aRef)
{
    if(this != &aRef)
    {
        _frequency = aRef.getFrequency();
        _capcode1  = aRef.getCapcode( 1);
        _capcode2  = aRef.getCapcode( 2);
        _capcode3  = aRef.getCapcode( 3);
        _capcode4  = aRef.getCapcode( 4);
        _capcode5  = aRef.getCapcode( 5);
        _capcode6  = aRef.getCapcode( 6);
        _capcode7  = aRef.getCapcode( 7);
        _capcode8  = aRef.getCapcode( 8);
        _capcode9  = aRef.getCapcode( 9);
        _capcode10 = aRef.getCapcode(10);
        _capcode11 = aRef.getCapcode(11);
        _capcode12 = aRef.getCapcode(12);
        _capcode13 = aRef.getCapcode(13);
        _capcode14 = aRef.getCapcode(14);
        _capcode15 = aRef.getCapcode(15);
        _capcode16 = aRef.getCapcode(16);
    }

    return *this;
}

float CtiTableDevicePagingReceiver::getFrequency() const
{
    return _frequency;
}

float CtiTableDevicePagingReceiver::getCapcode(int codeNumber) const
{
    switch(codeNumber)
    {
        case 1:     return _capcode1;
        case 2:     return _capcode2;
        case 3:     return _capcode3;
        case 4:     return _capcode4;
        case 5:     return _capcode5;
        case 6:     return _capcode6;
        case 7:     return _capcode7;
        case 8:     return _capcode8;
        case 9:     return _capcode9;
        case 10:    return _capcode10;
        case 11:    return _capcode11;
        case 12:    return _capcode12;
        case 13:    return _capcode13;
        case 14:    return _capcode14;
        case 15:    return _capcode15;
        case 16:    return _capcode16;

        default:
            return -1;
    }
}

void CtiTableDevicePagingReceiver::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"]  >> _deviceID;
    rdr["frequency"] >> _frequency;
    rdr["capcode1"]  >> _capcode1;
    rdr["capcode2"]  >> _capcode2;
    rdr["capcode3"]  >> _capcode3;
    rdr["capcode4"]  >> _capcode4;
    rdr["capcode5"]  >> _capcode5;
    rdr["capcode6"]  >> _capcode6;
    rdr["capcode7"]  >> _capcode7;
    rdr["capcode8"]  >> _capcode8;
    rdr["capcode9"]  >> _capcode9;
    rdr["capcode10"] >> _capcode10;
    rdr["capcode11"] >> _capcode11;
    rdr["capcode12"] >> _capcode12;
    rdr["capcode13"] >> _capcode13;
    rdr["capcode14"] >> _capcode14;
    rdr["capcode15"] >> _capcode15;
    rdr["capcode16"] >> _capcode16;
}

string CtiTableDevicePagingReceiver::getTableName()
{
    return "DevicePagingReceiverSettings";
}

LONG CtiTableDevicePagingReceiver::getDeviceID() const
{
    return _deviceID;
}

