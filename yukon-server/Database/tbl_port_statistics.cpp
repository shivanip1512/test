#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   tbl_port_statistics
*
* Date:   9/6/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_port_statistics.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/02/10 23:23:48 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include "tbl_port_statistics.h"
#include "logger.h"

CtiTablePortStatistics::CtiTablePortStatistics() :
   _type(0),
   _attemptCount(0),
   _dataErrorCount(0),
   _systemErrorCount(0)
{}

CtiTablePortStatistics::CtiTablePortStatistics(const CtiTablePortStatistics& aRef)
{
   *this = aRef;
}

CtiTablePortStatistics::~CtiTablePortStatistics() {}

CtiTablePortStatistics& CtiTablePortStatistics::operator=(const CtiTablePortStatistics& aRef)
{
   if(this != &aRef)
   {
      _type                = aRef.getType();
      _attemptCount        = aRef.getAttemptCount();
      _dataErrorCount      = aRef.getDataErrorCount();
      _systemErrorCount    = aRef.getSystemErrorCount();
      _startTime           = aRef.getStartTime();
      _stopTime            = aRef.getStopTime();
   }
   return *this;
}

ULONG          CtiTablePortStatistics::getType() const                        { return _type; }
ULONG          CtiTablePortStatistics::getAttemptCount() const                { return _attemptCount; }
ULONG          CtiTablePortStatistics::getDataErrorCount() const              { return _dataErrorCount; }
ULONG          CtiTablePortStatistics::getSystemErrorCount() const            { return _systemErrorCount; }
RWTime         CtiTablePortStatistics::getStartTime() const                   { return _startTime; }
RWTime         CtiTablePortStatistics::getStopTime() const                    { return _stopTime; }

ULONG&         CtiTablePortStatistics::getType()                              { return _type; }
ULONG&         CtiTablePortStatistics::getAttemptCount()                      { return _attemptCount; }
ULONG&         CtiTablePortStatistics::getDataErrorCount()                    { return _dataErrorCount; }
ULONG&         CtiTablePortStatistics::getSystemErrorCount()                  { return _systemErrorCount; }
RWTime&        CtiTablePortStatistics::getStartTime()                         { return _startTime; }
RWTime&        CtiTablePortStatistics::getStopTime()                          { return _stopTime; }

CtiTablePortStatistics& CtiTablePortStatistics::setType(const ULONG t)
{
   _type = t;
   return *this;
}

CtiTablePortStatistics& CtiTablePortStatistics::setAttemptCount(const ULONG c)
{
   _attemptCount = c;
   return *this;

}
CtiTablePortStatistics& CtiTablePortStatistics::getDataErrorCount(const ULONG c)
{
   _dataErrorCount = c;
   return *this;

}
CtiTablePortStatistics& CtiTablePortStatistics::getSystemErrorCount(const ULONG c)
{
   _systemErrorCount = c;
   return *this;

}
CtiTablePortStatistics& CtiTablePortStatistics::getStartTime(const RWTime& t)
{
   _startTime = t;
   return *this;

}
CtiTablePortStatistics& CtiTablePortStatistics::getStopTime(const RWTime& t)
{
   _stopTime = t;
   return *this;

}

/* These guys are handled different since they are multi-keyed */

void CtiTablePortStatistics::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
   RWDBTable portTbl       = db.table("PortStatistics");
   keyTable      = db.table("CommPort");

   selector <<
      keyTable["portid"] <<
      portTbl["statistictype"] <<
      portTbl["attempts"] <<
      portTbl["dataerrors"] <<
      portTbl["systemerrors"] <<
      portTbl["startdatetime"] <<
      portTbl["stopdatetime"];

   selector.from(keyTable);
   selector.from(portTbl);

   selector.where( keyTable["portid"] == portTbl["portid"] );
}

void CtiTablePortStatistics::DecodeDatabaseReader(RWDBReader &rdr)
{
   {
      CtiLockGuard<CtiLogger> logger_guard(dout);
      if(getDebugLevel() & 0x0800) dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
   
      rdr["statistictype"]          >> _type;
      if(getDebugLevel() & 0x00000800) dout << " Statistic Type       = " << _type << endl;
      rdr["attempts"]               >> _attemptCount;
      if(getDebugLevel() & 0x00000800) dout << " Stats: Attampts      = " << _attemptCount << endl;
      rdr["dataerrors"]             >> _dataErrorCount;
      if(getDebugLevel() & 0x00000800) dout << " Stats: Data Error    = " << _dataErrorCount << endl;
      rdr["systemerrors"]           >> _systemErrorCount;
      if(getDebugLevel() & 0x00000800) dout << " Stats: Sys. Errors   = " << _systemErrorCount << endl;
      rdr["startdatetime"]          >> _startTime;
      if(getDebugLevel() & 0x00000800) dout << " Stats: Start time    = " << _startTime << endl;
      rdr["stopdatetime"]           >> _stopTime;
      if(getDebugLevel() & 0x00000800) dout << " Stats: Stop time     = " << _stopTime << endl;
   }
}

