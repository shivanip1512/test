/*-----------------------------------------------------------------------------*
*
* File:   tbl_port_dialup
*
* Date:   8/28/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_port_dialup.cpp-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2005/12/20 17:16:07 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

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

CtiTablePortDialup::CtiTablePortDialup(const CtiTablePortDialup& aRef)
{
   *this = aRef;
}

CtiTablePortDialup::~CtiTablePortDialup() {}

CtiTablePortDialup& CtiTablePortDialup::operator=(const CtiTablePortDialup& aRef)
{
   if(this != &aRef)
   {
      _portID           = aRef.getPortID();
      _modemType        = aRef.getModemType();
      _modemInitString  = aRef.getModemInitString();
      _prefixString     = aRef.getPrefixString();
      _suffixString     = aRef.getSuffixString();
   }
   return *this;
}

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
   {
      if(getDebugLevel() & DEBUGLEVEL_DATABASE)
      {
          CtiLockGuard<CtiLogger> logger_guard(dout);
          dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
      }

      rdr["modemtype"]              >> _modemType;
      if(getDebugLevel() & DEBUGLEVEL_DATABASE) { CtiLockGuard<CtiLogger> logger_guard(dout); dout << " Modem Type           = " << _modemType << endl; }

      rdr["initializationstring"]   >> _modemInitString;
      if(getDebugLevel() & DEBUGLEVEL_DATABASE) { CtiLockGuard<CtiLogger> logger_guard(dout); dout << " Modem Init String    = " << _modemInitString << endl; }

      rdr["prefixnumber"]           >> _prefixString;
      if(getDebugLevel() & DEBUGLEVEL_DATABASE) { CtiLockGuard<CtiLogger> logger_guard(dout); dout << " Prefix String        = " << _prefixString << endl; }

      rdr["suffixnumber"]           >> _suffixString;
      if(getDebugLevel() & DEBUGLEVEL_DATABASE) { CtiLockGuard<CtiLogger> logger_guard(dout); dout << " Suffix String        = " << _suffixString << endl; }
   }
}

