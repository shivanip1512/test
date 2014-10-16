#include "precompiled.h"

#include "tbl_port_serial.h"
#include "logger.h"

using std::string;
using std::endl;

CtiTablePortLocalSerial::CtiTablePortLocalSerial()
{}

CtiTablePortLocalSerial::~CtiTablePortLocalSerial() {}

string CtiTablePortLocalSerial::getPhysicalPort() const    { return _physicalPort; }
string& CtiTablePortLocalSerial::getPhysicalPort()          { return _physicalPort; }

CtiTablePortLocalSerial&   CtiTablePortLocalSerial::setPhysicalPort(const string& str )
{
   _physicalPort = str;
   return *this;
}

void CtiTablePortLocalSerial::DecodeDatabaseReader(Cti::RowReader &rdr)
{
   rdr["physicalport"] >> _physicalPort;

   if(getDebugLevel() & DEBUGLEVEL_DATABASE)
   {
       Cti::FormattedList itemList;

       itemList.add("Physical Port Desc") << _physicalPort;

       CTILOG_DEBUG(dout, "Decoding DB read from PortLocalSerial"<<
               itemList
               );
   }
}


