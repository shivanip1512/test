
#pragma warning( disable : 4786)

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
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:58:03 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "tbl_port_dialup.h"
#include "logger.h"

CtiTablePortDialup::CtiTablePortDialup() {}

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

RWCString CtiTablePortDialup::getModemType() const
{
   return _modemType;
}

RWCString& CtiTablePortDialup::getModemType()
{
   return _modemType;
}

CtiTablePortDialup& CtiTablePortDialup::setModemType(const RWCString& str)
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

RWCString CtiTablePortDialup::getTableName()
{
   return "PortDialupModem";
}

RWCString CtiTablePortDialup::getModemInitString() const
{
   return _modemInitString;
}

RWCString& CtiTablePortDialup::getModemInitString()
{
   return _modemInitString;
}

CtiTablePortDialup& CtiTablePortDialup::setModemInitString(const RWCString& str)
{
   _modemInitString = str;
   return *this;
}

RWCString CtiTablePortDialup::getPrefixString() const
{
   return _prefixString;
}

RWCString& CtiTablePortDialup::getPrefixString()
{
   return _prefixString;
}

CtiTablePortDialup& CtiTablePortDialup::setPrefixString(const RWCString& str)
{
   _prefixString = str;
   return *this;
}

RWCString CtiTablePortDialup::getSuffixString() const
{
   return _suffixString;
}

RWCString& CtiTablePortDialup::getSuffixString()
{
   return _suffixString;
}

CtiTablePortDialup& CtiTablePortDialup::setSuffixString(const RWCString& str)
{
   _suffixString = str;
   return *this;
}

void CtiTablePortDialup::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
   RWDBTable portDialup    = db.table(getTableName() );

   selector <<
      portDialup["modemtype"] <<
      portDialup["initializationstring"] <<
      portDialup["prefixnumber"] <<
      portDialup["suffixnumber"];

   selector.from(portDialup);

   selector.where( selector.where() && keyTable["paobjectid"].leftOuterJoin(portDialup["portid"]) );
}

void CtiTablePortDialup::DecodeDatabaseReader(RWDBReader &rdr)
{
   {
      CtiLockGuard<CtiLogger> logger_guard(dout);
      if(getDebugLevel() & 0x00000800) dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;

      rdr["modemtype"]              >> _modemType;
      if(getDebugLevel() & 0x00000800) dout << " Modem Type           = " << _modemType << endl;

      rdr["initializationstring"]   >> _modemInitString;
      if(getDebugLevel() & 0x00000800) dout << " Modem Init String    = " << _modemInitString << endl;

      rdr["prefixnumber"]           >> _prefixString;
      if(getDebugLevel() & 0x00000800) dout << " Prefix String        = " << _prefixString << endl;

      rdr["suffixnumber"]           >> _suffixString;
      if(getDebugLevel() & 0x00000800) dout << " Suffix String        = " << _suffixString << endl;
   }
}

