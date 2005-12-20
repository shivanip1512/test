/*-----------------------------------------------------------------------------*
*
* File:   tbl_port_statistics
*
* Date:   3/29/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_port_statistics.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/12/20 17:16:08 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_PORT_STATISTICS_H__
#define __TBL_PORT_STATISTICS_H__


#include "dbaccess.h"
#include "dbmemobject.h"
#include "dlldefs.h"
#include "ctitime.h"


class IM_EX_CTIYUKONDB CtiTablePortStatistics : public CtiMemDBObject
{
protected:

   ULONG          _type;
   ULONG          _attemptCount;
   ULONG          _dataErrorCount;
   ULONG          _systemErrorCount;
   CtiTime         _startTime;
   CtiTime         _stopTime;

private:

public:
   CtiTablePortStatistics();

   CtiTablePortStatistics(const CtiTablePortStatistics& aRef);

   virtual ~CtiTablePortStatistics();

   CtiTablePortStatistics& operator=(const CtiTablePortStatistics& aRef);

   ULONG          getType() const;
   ULONG          getAttemptCount() const;
   ULONG          getDataErrorCount() const;
   ULONG          getSystemErrorCount() const;
   CtiTime         getStartTime() const;
   CtiTime         getStopTime() const;

   ULONG&         getType();
   ULONG&         getAttemptCount();
   ULONG&         getDataErrorCount();
   ULONG&         getSystemErrorCount();
   CtiTime&        getStartTime();
   CtiTime&        getStopTime();

   CtiTablePortStatistics& setType(const ULONG t);

   CtiTablePortStatistics& setAttemptCount(const ULONG c);
   CtiTablePortStatistics& getDataErrorCount(const ULONG c);
   CtiTablePortStatistics& getSystemErrorCount(const ULONG c);
   CtiTablePortStatistics& getStartTime(const CtiTime& t);
   CtiTablePortStatistics& getStopTime(const CtiTime& t);

   /* These guys are handled different since they are multi-keyed */

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   static string& getSQLConditions(string &str);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);
};
#endif // #ifndef __TBL_PORT_STATISTICS_H__
