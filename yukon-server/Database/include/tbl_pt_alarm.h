
#pragma warning( disable : 4786)
#ifndef __TBL_PT_ALARM_H__
#define __TBL_PT_ALARM_H__

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
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2004/05/19 14:51:17 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <limits.h>

#include <rw/db/db.h>
#include <rw/rwtime.h>

#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>
#include <rw\cstring.h>
#include <rw\rwtime.h>
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


   /*
    *  This enumeration is tied VERY tightly to the order of the list elements in the
    *  dbeditor.  If they change, this too must change!
    */

   typedef enum {

      nonUpdatedStatus,
      abnormal,
      uncommandedStateChange,
      commandFailure,
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

      invalidstatusstate // = 32

   } CtiStatusPointAlarmStates_t;

   typedef enum {

      nonUpdatedNumeric,
      rateOfChange,            // for numeric points
      limit0,
      limit1,
      highReasonability,
      lowReasonability,

      invalidnumericstate // = 32

   } CtiNumericPointAlarmStates_t;



protected:

   LONG        _pointID;

   UINT        _alarmCategory[ ALARM_STATE_SIZE ];
   UINT        _excludeNotifyStates;

   LONG        _recipientID;

   BOOL        _notifyOnAcknowledge;
   BOOL        _notifyOnClear;
   UINT        _notificationGroupID;

private:

   RWCString statesAsString( );
   RWCString excludeAsString( );

   static UINT resolveStates( UINT &states, RWCString &str );
   static UINT resolveStates( RWCString &str );

public:

   CtiTablePointAlarming( LONG pid = 0 );
   CtiTablePointAlarming(const CtiTablePointAlarming& aRef);
   virtual ~CtiTablePointAlarming();

   CtiTablePointAlarming& operator=(const CtiTablePointAlarming& aRef);

   LONG getPointID() const;
   LONG getRecipientID() const;
   UINT getAlarmCategory(const INT offset) const;
   UINT getExcludeNotifyStates() const;
   BOOL getNotifyOnAcknowledge() const;
   BOOL getNotifyOnClear() const;
   UINT getNotificationGroupID() const;


   CtiTablePointAlarming& setPointID( const LONG &aLong );
   CtiTablePointAlarming& setRecipientID( const LONG &aLong );
   CtiTablePointAlarming& setAlarmCategory( const INT offset, const UINT &aInt );
   CtiTablePointAlarming& setAlarmCategory( const RWCString str );
   CtiTablePointAlarming& setExcludeNotifyStates( const UINT &aInt );
   CtiTablePointAlarming& setNotifyOnAcknowledge( const BOOL &aBool );
   CtiTablePointAlarming& setNotifyOnClear( const BOOL &aBool );
   CtiTablePointAlarming& setNotificationGroupID( const UINT &aInt );


   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   static RWCString getTableName();

   virtual RWDBStatus Insert();
   virtual RWDBStatus Update();
   virtual RWDBStatus Restore();
   virtual RWDBStatus Delete();

   virtual void DecodeDatabaseReader(RWDBReader& rdr);


   bool isExcluded( int alarm) const;
   bool alarmOn( int alarm ) const;
   INT alarmPriority( int alarm ) const;

};
#endif // #ifndef __TBL_PT_ALARM_H__
