#include "precompiled.h"

#include "tbl_port_tcpip.h"
#include "logger.h"

using std::string;
using std::endl;

CtiTablePortTCPIP::CtiTablePortTCPIP() :
    _ipPort(1000),
    _portID(0)
{
}

CtiTablePortTCPIP::~CtiTablePortTCPIP()
{
}

void CtiTablePortTCPIP::setIPAddress(const string &str)
{
    _ipAddress = str;
}

void CtiTablePortTCPIP::setIPPort(const INT i)
{
    _ipPort = i;
}

const string &CtiTablePortTCPIP::getIPAddress() const
{
    return _ipAddress;
}

INT CtiTablePortTCPIP::getIPPort() const
{
    return _ipPort;
}

const string & CtiTablePortTCPIP::getEncodingKey() const
{
    return _encodingKey;
}

void CtiTablePortTCPIP::setEncodingKey(const string& encodingKey)
{
    _encodingKey = encodingKey;
}

const string & CtiTablePortTCPIP::getEncodingType() const
{
    return _encodingType;
}

void CtiTablePortTCPIP::setEncodingType(const string& encodingType)
{
    _encodingType = encodingType;
}

void CtiTablePortTCPIP::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    try
    {
        rdr["ipaddress"] >> _ipAddress;
        rdr["socketportnumber"] >> _ipPort;
        rdr["encodingkey"] >> _encodingKey;
        rdr["encodingtype"] >> _encodingType;

        if(getDebugLevel() & DEBUGLEVEL_DATABASE)
        {
            Cti::FormattedList itemList;

            itemList.add("IP Address")    << _ipAddress;
            itemList.add("IP Port")       << _ipPort;
            itemList.add("Encoding Key")  << _encodingKey;
            itemList.add("Encoding Type") << _encodingType;

            CTILOG_DEBUG(dout, "Decoding DB read from "<< getTableName() <<
                    itemList
                    );
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

string CtiTablePortTCPIP::getTableName()
{
    return "PortTerminalServer";
}
