#include "precompiled.h"

#include <boost/algorithm/string.hpp>
#include "tbl_port_base.h"
#include "logger.h"

using std::string;
using std::endl;

CtiTablePortBase::CtiTablePortBase() :
_protocol(ProtocolWrapNone),
_alarmInhibit(false),
_sharedSocketNumber(-1)
{
}

CtiTablePortBase::~CtiTablePortBase()
{
}

INT   CtiTablePortBase::getProtocol() const
{
   return _protocol;
}

void  CtiTablePortBase::setProtocol(int t)
{
   _protocol = t;
}

CtiTablePortBase& CtiTablePortBase::setAlarmInhibit(bool b)
{
    _alarmInhibit = b;
    return *this;
}
bool  CtiTablePortBase::getAlarmInhibit() const
{
    return _alarmInhibit;
}

void CtiTablePortBase::setSharedPortType(string str)
{
   _sharedPortType = str;
}

string CtiTablePortBase::getSharedPortType() const
{
   return _sharedPortType;
}

INT CtiTablePortBase::getSharedSocketNumber() const
{
   return _sharedSocketNumber;
}

void CtiTablePortBase::setSharedSocketNumber(INT sockNum)
{
   _sharedSocketNumber = sockNum;
}

void CtiTablePortBase::DecodeDatabaseReader(Cti::RowReader &rdr)
{
   int iSharedSocketNumber;
   string zAlarmInhibit, zProtocol, zSharedporttype;

   rdr["alarminhibit"]       >> zAlarmInhibit;
   rdr["commonprotocol"]     >> zProtocol;
   rdr["sharedporttype"]     >> zSharedporttype;
   rdr["sharedsocketnumber"] >> iSharedSocketNumber;

   if(getDebugLevel() & DEBUGLEVEL_DATABASE)
   {
       Cti::FormattedList itemList;

       itemList.add("Alarm Inhibit")        << zAlarmInhibit;
       itemList.add("Protocol wrap")        << zProtocol;
       itemList.add("Shared Port Type")     << zSharedporttype;
       itemList.add("Shared Socket Number") << iSharedSocketNumber;

       CTILOG_DEBUG(dout, "Decoding DB read from "<< getTableName() <<
               itemList
               );
   }

   _alarmInhibit       = (! zAlarmInhibit.empty() && ::tolower(zAlarmInhibit[0]) == 'y');
   _protocol           = resolveProtocol(zProtocol);
   _sharedPortType.swap(boost::algorithm::to_lower_copy(zSharedporttype));
   _sharedSocketNumber = iSharedSocketNumber;
}

std::string CtiTablePortBase::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiTablePortBase";
    itemList.add("Alarm Inhibit") << _alarmInhibit;

    return itemList.toString();
}

string CtiTablePortBase::getTableName()
{
   return "CommPort";
}



