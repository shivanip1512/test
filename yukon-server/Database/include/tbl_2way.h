#ifndef __TBL_2WAY_H__
#define __TBL_2WAY_H__

#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_2way
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_2way.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:39 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <rw/db/reader.h>
#include <rw\cstring.h>


//ECS added from here on out
#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/datetime.h>
#include <rw/rwtime.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dbmemobject.h"
#include "dbaccess.h"

//except these
#include "resolvers.h"
#include "yukon.h"



class IM_EX_CTIYUKONDB CtiTableDevice2Way : public CtiMemDBObject
{
protected:

   //added by ECS
   LONG              _deviceID;
   INT               PerformanceThreshold;

   union
   {
      INT            Flag;

      struct
      {
         INT         MonthlyStats   : 1;
         INT         DailyStats     : 1;
         INT         HourlyStats    : 1;
         INT         FailureAlarm   : 1;
         INT         PerformAlarm   : 1;
         INT         Perform24Alarm : 1;
      };
   };

public:

   CtiTableDevice2Way(LONG did = -1);

   CtiTableDevice2Way(const CtiTableDevice2Way &aRef);

   CtiTableDevice2Way& operator=(const CtiTableDevice2Way &aRef);

   LONG getDeviceID() const;

   INT  getPerformanceThreshold() const;
   CtiTableDevice2Way& setPerformanceThreshold( const INT aPerformanceThreshold );

   INT  getMonthlyStats() const;
   CtiTableDevice2Way& setMonthlyStats( const INT theMonthlyStats );

   INT  getDailyStats() const;
   CtiTableDevice2Way& setDailyStats( const INT theDailyStats );

   INT  getHourlyStats() const;
   CtiTableDevice2Way& setHourlyStats( const INT theHourlyStats );

   INT  getFailureAlarm() const;
   CtiTableDevice2Way& setFailureAlarm( const INT aFailureAlarm );

   INT  getPerformAlarm() const;
   CtiTableDevice2Way& setPerformAlarm( const INT aPerformAlarm );

   INT  getPerform24Alarm() const;
   CtiTableDevice2Way& setPerform24Alarm( const INT aPerform24Alarm );

   static RWCString getTableName();

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   CtiTableDevice2Way& setDeviceID(LONG deviceID);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);
   virtual RWDBStatus Restore();
   virtual RWDBStatus Insert();
   virtual RWDBStatus Update();
   virtual RWDBStatus Delete();
};

#endif
