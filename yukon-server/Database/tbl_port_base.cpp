/*-----------------------------------------------------------------------------*
*
* File:   tbl_port_base
*
* Date:   8/28/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_port_base.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/12/20 17:16:06 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "tbl_port_base.h"
#include "logger.h"

using std::transform;
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
   INT iTemp;
   string rwsTemp;

   if(getDebugLevel() & DEBUGLEVEL_DATABASE)
   {
      CtiLockGuard<CtiLogger> logger_guard(dout);
      dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

   rdr["alarminhibit"] >> rwsTemp;
   if(getDebugLevel() & DEBUGLEVEL_DATABASE)
   {
       CtiLockGuard<CtiLogger> logger_guard(dout);
       dout << " Alarm Inhibit        ? " << rwsTemp << endl;
   }
   transform( rwsTemp.begin(), rwsTemp.end(), rwsTemp.begin(), ::tolower);
   _alarmInhibit = (rwsTemp[0] == 'y' ? true : false);

   rdr["commonprotocol"] >> rwsTemp;
   if(getDebugLevel() & DEBUGLEVEL_DATABASE)
   {
       CtiLockGuard<CtiLogger> logger_guard(dout);
       dout << " Protocol wrap        = " << rwsTemp  << endl;
   }
   _protocol = resolveProtocol(rwsTemp);

   rdr["sharedporttype"] >> rwsTemp;
   if(getDebugLevel() & DEBUGLEVEL_DATABASE)
   {
       CtiLockGuard<CtiLogger> logger_guard(dout);
       dout << " Shared Port Type     = " << rwsTemp << endl;
   }
   transform( rwsTemp.begin(), rwsTemp.end(), rwsTemp.begin(), ::tolower);
   _sharedPortType = rwsTemp;

   rdr["sharedsocketnumber"] >> iTemp;
   if(getDebugLevel() & DEBUGLEVEL_DATABASE)
   {
       CtiLockGuard<CtiLogger> logger_guard(dout);
       dout << "  Shared Socket Number = " << rwsTemp << endl;
   }
   _sharedSocketNumber = iTemp;

}

void CtiTablePortBase::DumpData()
{
   CtiLockGuard<CtiLogger> logger_guard(dout);
   dout << " Alarm Inhibit                              : " << _alarmInhibit << endl;
}

string CtiTablePortBase::getTableName()
{
   return "CommPort";
}



