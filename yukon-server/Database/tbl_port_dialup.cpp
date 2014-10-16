#include "precompiled.h"

#include "tbl_port_dialup.h"
#include "logger.h"

using std::string;
using std::endl;

CtiTablePortDialup::CtiTablePortDialup() :
_portID(0),
_modemType("Unknown"),
_modemInitString("AT&F"),
_prefixString(" "),
_suffixString(" ")
{}

CtiTablePortDialup::~CtiTablePortDialup() {}

string CtiTablePortDialup::getModemType() const
{
   return _modemType;
}

string& CtiTablePortDialup::getModemType()
{
   return _modemType;
}

CtiTablePortDialup& CtiTablePortDialup::setModemType(const string& str)
{
   _modemType = str;
   return *this;
}

LONG CtiTablePortDialup::getPortID() const
{
   return _portID;
}

CtiTablePortDialup& CtiTablePortDialup::setPortID( const LONG portID )
{
   _portID = portID;
   return *this;
}

string CtiTablePortDialup::getTableName()
{
   return "PortDialupModem";
}

string CtiTablePortDialup::getModemInitString() const
{
   return _modemInitString;
}

string& CtiTablePortDialup::getModemInitString()
{
   return _modemInitString;
}

CtiTablePortDialup& CtiTablePortDialup::setModemInitString(const string& str)
{
   _modemInitString = str;
   return *this;
}

string CtiTablePortDialup::getPrefixString() const
{
   return _prefixString;
}

string& CtiTablePortDialup::getPrefixString()
{
   return _prefixString;
}

CtiTablePortDialup& CtiTablePortDialup::setPrefixString(const string& str)
{
   _prefixString = str;
   return *this;
}

string CtiTablePortDialup::getSuffixString() const
{
   return _suffixString;
}

string& CtiTablePortDialup::getSuffixString()
{
   return _suffixString;
}

CtiTablePortDialup& CtiTablePortDialup::setSuffixString(const string& str)
{
   _suffixString = str;
   return *this;
}

string CtiTablePortDialup::getSQLCoreStatement()
{
   static const string sql =  "SELECT YP.paobjectid, PDM.modemtype, PDM.initializationstring, PDM.prefixnumber, PDM.suffixnumber "
                              "FROM YukonPAObject YP, PortDialupModem PDM "
                              "WHERE YP.paobjectid = PDM.portid";

   return sql;
}

void CtiTablePortDialup::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    rdr["modemtype"]              >> _modemType;
    rdr["initializationstring"]   >> _modemInitString;
    rdr["prefixnumber"]           >> _prefixString;
    rdr["suffixnumber"]           >> _suffixString;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        Cti::FormattedList itemList;

        itemList.add("Modem Type")        << _modemType;
        itemList.add("Modem Init String") << _modemInitString;
        itemList.add("Prefix String")     << _prefixString;
        itemList.add("Suffix String")     << _suffixString;

        CTILOG_DEBUG(dout, "Decoding DB read from "<< getTableName() <<
                itemList
                );
    }
}

