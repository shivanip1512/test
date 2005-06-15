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
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/06/15 23:56:34 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

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
   if(getDebugLevel() & DEBUGLEVEL_DATABASE) 
   {
      CtiLockGuard<CtiLogger> logger_guard(dout);
      dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

   rdr["statistictype"]          >> _type;
   rdr["attempts"]               >> _attemptCount;
   rdr["dataerrors"]             >> _dataErrorCount;
   rdr["systemerrors"]           >> _systemErrorCount;
   rdr["startdatetime"]          >> _startTime;
   rdr["stopdatetime"]           >> _stopTime;

   if(getDebugLevel() & DEBUGLEVEL_DATABASE) 
   {
      CtiLockGuard<CtiLogger> logger_guard(dout);
      dout << " Statistic Type       = " << _type << endl;
      dout << " Stats: Attampts      = " << _attemptCount << endl;
      dout << " Stats: Data Error    = " << _dataErrorCount << endl;
      dout << " Stats: Sys. Errors   = " << _systemErrorCount << endl;
      dout << " Stats: Start time    = " << _startTime << endl;
      dout << " Stats: Stop time     = " << _stopTime << endl;
   }
}

