/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_alarm
*
* Class:  CtiTablePointAlarming
* Date:   10/9/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_pt_alarm.h-arc  $
* REVISION     :  $Revision: 1.18 $
* DATE         :  $Date: 2008/10/08 14:17:03 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __TBL_PT_ALARM_H__
#define __TBL_PT_ALARM_H__
#pragma warning( disable : 4786)

#include <limits.h>

#include <rw/db/db.h>

#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>
#include <limits.h>
#include <rw/db/nullind.h>
#include <rw/db/datetime.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "desolvers.h"
#include "yukon.h"


#define ALARM_STATE_SIZE 32

class IM_EX_CTIYUKONDB CtiTablePointAlarming : public CtiMemDBObject
{
public:

   //  This enumeration is tied VERY tightly to the order of the list elements in the
   //  dbeditor.  If they change, this too must change!

   enum CtiStatusPointAlarmStates_t
   {
      nonUpdatedStatus,
      abnormal,
      uncommandedStateChange,
      commandFailure,
      staleStatus,
      state0,
      state1,
      state2,
      state3,
      state4,
      state5,
      state6,
      state7,
      state8,
      state9,
      changeOfState,

      invalidstatusstate // = 32
   };

   enum CtiNumericPointAlarmStates_t
   {
      nonUpdatedNumeric,
      rateOfChange,            // for numeric points
      limit0,
      limit1,
      highReasonability,
      lowReasonability,
      limitLow0,
      limitLow1,
      limitHigh0,
      limitHigh1,
      staleNumeric,

      invalidnumericstate // = 32
   };

protected:

   LONG        _pointID;

   UINT        _alarmCategory[ ALARM_STATE_SIZE ];
   UINT        _excludeNotifyStates;
   UINT        _autoAckStates;

   LONG        _recipientID;

   BOOL        _notifyOnAcknowledge;
   BOOL        _notifyOnClear;
   UINT        _notificationGroupID;

   CtiTablePointAlarming& setPointID            ( const LONG &aLong );
   CtiTablePointAlarming& setRecipientID        ( const LONG &aLong );
   CtiTablePointAlarming& setAlarmCategory      ( const INT offset, const UINT &aInt );
   CtiTablePointAlarming& setAlarmCategory      ( const string str );
   CtiTablePointAlarming& setExcludeNotifyStates( const UINT &aInt );
   CtiTablePointAlarming& setAutoAckStates      ( const UINT &aInt );
   CtiTablePointAlarming& setNotifyOnAcknowledge( const BOOL &aBool );
   CtiTablePointAlarming& setNotifyOnClear      ( const BOOL &aBool );
   CtiTablePointAlarming& setNotificationGroupID( const UINT &aInt );

private:

   static UINT resolveExcludeStates( string &str );
   static UINT resolveAutoAcknowledgeStates( string &str );

public:

   CtiTablePointAlarming(LONG pid = 0);
   CtiTablePointAlarming(const CtiTablePointAlarming& aRef);
   CtiTablePointAlarming(RWDBReader& rdr);
   virtual ~CtiTablePointAlarming();

   CtiTablePointAlarming& operator=(const CtiTablePointAlarming& aRef);

   bool operator<(const CtiTablePointAlarming &rhs) const;

   static void getSQL(string &sql, LONG pointID = 0, LONG paoID = 0, const std::set<long> &pointIds = std::set<long>());
   static string getTableName();

   LONG getPointID()                        const;
   LONG getRecipientID()                    const;
   UINT getAlarmCategory(const INT offset)  const;
   UINT getExcludeNotifyStates()            const;
   UINT getAutoAckStates()                  const;
   BOOL getNotifyOnAcknowledge()            const;
   BOOL getNotifyOnClear()                  const;
   UINT getNotificationGroupID()            const;

   bool isNotifyExcluded( int alarm ) const;
   bool isAutoAcked     ( int alarm ) const;
   bool alarmOn         ( int alarm ) const;
   INT  alarmPriority   ( int alarm ) const;
};

#endif // #ifndef __TBL_PT_ALARM_H__
