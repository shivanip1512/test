#include "precompiled.h"

#include "tbl_dv_tnpp.h"
#include "logger.h"

#include "database_connection.h"
#include "database_writer.h"

using std::string;
using std::endl;

CtiTableDeviceTnpp::CtiTableDeviceTnpp() :
    _inertia(2),
    _destinationAddress(0),
    _deviceID(0),
    _originAddress(0)
{}

CtiTableDeviceTnpp::~CtiTableDeviceTnpp()
{}

void CtiTableDeviceTnpp::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB read from "<< getTableName());
    }

    rdr["deviceid"]           >>   _deviceID;
    rdr["inertia"]            >>   _inertia;
    rdr["destinationaddress"] >>   _destinationAddress;
    rdr["originaddress"]      >>   _originAddress;
    rdr["identifierformat"]   >>   _identifierFormat;
    rdr["protocol"]           >>   _pagerProtocol;
    rdr["dataformat"]         >>   _dataFormat;
    rdr["channel"]            >>   _channel;
    rdr["zone"]               >>   _zone;
    rdr["functioncode"]       >>   _functionCode;
    rdr["pagerid"]            >>   _pagerID;

}

string CtiTableDeviceTnpp::getTableName()
{
    return "DeviceTNPPSettings";
}

LONG CtiTableDeviceTnpp::getDeviceID() const
{

    return _deviceID;
}

unsigned short CtiTableDeviceTnpp::getInertia() const
{
    return _inertia;
}

int CtiTableDeviceTnpp::getDestinationAddress() const
{ //The tnpp devices address
    return _destinationAddress;
}

int CtiTableDeviceTnpp::getOriginAddress() const
{ //The Computer's address
    return _originAddress;
}

string CtiTableDeviceTnpp::getIdentifierFormat()
{
    return _identifierFormat;
}

string CtiTableDeviceTnpp::getPagerProtocol()
{
    return _pagerProtocol;
}

string CtiTableDeviceTnpp::getPagerDataFormat()
{
    return _dataFormat;
}

string CtiTableDeviceTnpp::getChannel()
{
    return _channel;
}

string CtiTableDeviceTnpp::getZone()
{
    return _zone;
}

string CtiTableDeviceTnpp::getFunctionCode()
{
    return _functionCode;
}

string CtiTableDeviceTnpp::getPagerID()
{
    return _pagerID;
}
