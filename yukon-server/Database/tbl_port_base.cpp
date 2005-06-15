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
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/06/15 23:56:34 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_port_base.h"
#include "logger.h"

CtiTablePortBase::CtiTablePortBase() :
_protocol(ProtocolWrapNone),
_alarmInhibit(false),
_performanceAlarm(false),
_performanceThreshold(false),
_sharedSocketNumber(-1)
{
}

CtiTablePortBase::CtiTablePortBase(const CtiTablePortBase& aRef) :
_protocol(ProtocolWrapNone),
_alarmInhibit(false),
_performanceAlarm(false),
_performanceThreshold(false),
_sharedSocketNumber(-1)
{
   *this = aRef;
}

CtiTablePortBase::~CtiTablePortBase()
{
}

CtiTablePortBase& CtiTablePortBase::operator=(const CtiTablePortBase& aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);


      _protocol                = aRef.getProtocol();
      _alarmInhibit            = aRef.getAlarmInhibit();
      _performanceAlarm        = aRef.isPerformanceAlarm();
      _performanceThreshold    = aRef.getPerformanceThreshold();
      _sharedPortType          = aRef.getSharedPortType();
      _sharedSocketNumber      = aRef.getSharedSocketNumber();
   }
   return *this;
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

bool CtiTablePortBase::isPerformanceAlarm() const
{
   return _performanceAlarm;
}

void CtiTablePortBase::setPerformanceAlarm(bool b)
{
   _performanceAlarm = b;
}

void CtiTablePortBase::setSharedPortType(RWCString str)
{
   _sharedPortType = str;
}

RWCString CtiTablePortBase::getSharedPortType() const
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

INT  CtiTablePortBase::getPerformanceThreshold() const
{
   return _performanceThreshold;
}

INT& CtiTablePortBase::getPerformanceThreshold()
{
   return _performanceThreshold;
}

CtiTablePortBase& CtiTablePortBase::setPerformanceThreshold( const INT aPerformanceThreshold )
{
   _performanceThreshold = aPerformanceThreshold;
   return *this;
}

void CtiTablePortBase::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
   RWDBTable devTbl  = db.table(getTableName());

   selector <<
      devTbl["alarminhibit"] <<
      devTbl["commonprotocol"] <<
      devTbl["performancealarm"] <<
      devTbl["performthreshold"] <<
      devTbl["sharedporttype"] <<
      devTbl["sharedsocketnumber"];

   selector.from(devTbl);

   selector.where( keyTable["paobjectid"] == devTbl["portid"] && selector.where() );
}

void CtiTablePortBase::DecodeDatabaseReader(RWDBReader &rdr)
{
   INT iTemp;
   RWCString rwsTemp;

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
   rwsTemp.toLower();
   _alarmInhibit = (rwsTemp(0) == 'y' ? true : false);

   rdr["commonprotocol"] >> rwsTemp;
   if(getDebugLevel() & DEBUGLEVEL_DATABASE)
   {
       CtiLockGuard<CtiLogger> logger_guard(dout);
       dout << " Protocol wrap        = " << rwsTemp  << endl;
   }
   _protocol = resolveProtocol(rwsTemp);

   rdr["performancealarm"] >> rwsTemp;
   if(getDebugLevel() & DEBUGLEVEL_DATABASE)
   {
       CtiLockGuard<CtiLogger> logger_guard(dout);
       dout << " Performance Alarming ? " << rwsTemp << endl;
   }
   rwsTemp.toLower();
   _performanceAlarm = (rwsTemp(0) == 'y' ? true : false);

   rdr["performthreshold"] >> _performanceThreshold;
   if(getDebugLevel() & DEBUGLEVEL_DATABASE)
   {
       CtiLockGuard<CtiLogger> logger_guard(dout);
       dout << "  Performance Thresh. = " << _performanceThreshold  << endl;
   }

   rdr["sharedporttype"] >> rwsTemp;
   if(getDebugLevel() & DEBUGLEVEL_DATABASE)
   {
       CtiLockGuard<CtiLogger> logger_guard(dout);
       dout << " Shared Port Type     = " << rwsTemp << endl;
   }
   rwsTemp.toLower();
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

RWCString CtiTablePortBase::getTableName()
{
   return "CommPort";
}



