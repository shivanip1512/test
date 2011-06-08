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
* REVISION     :  $Revision: 1.20 $
* DATE         :  $Date: 2008/11/12 22:10:40 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __TBL_PT_ALARM_H__
#define __TBL_PT_ALARM_H__
#pragma warning( disable : 4786)

#include <limits.h>


#include <limits.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "desolvers.h"
#include "yukon.h"
#include "row_reader.h"

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

   CtiTablePointAlarming& setAlarmCategory      ( const INT offset, const UINT &aInt );
   CtiTablePointAlarming& setAlarmCategory      ( const std::string str );
   CtiTablePointAlarming& setExcludeNotifyStates( const UINT &aInt );
   CtiTablePointAlarming& setAutoAckStates      ( const UINT &aInt );
   CtiTablePointAlarming& setNotifyOnAcknowledge( const BOOL &aBool );
   CtiTablePointAlarming& setNotifyOnClear      ( const BOOL &aBool );
   CtiTablePointAlarming& setNotificationGroupID( const UINT &aInt );

private:

   LONG        _pointID;

   UINT        _alarmCategory[ ALARM_STATE_SIZE ];
   UINT        _excludeNotifyStates;
   UINT        _autoAckStates;

   BOOL        _notifyOnAcknowledge;
   BOOL        _notifyOnClear;
   UINT        _notificationGroupID;

   static UINT resolveExcludeStates( std::string &str );
   static UINT resolveAutoAcknowledgeStates( std::string &str );

public:

   CtiTablePointAlarming(LONG pid = 0);
   CtiTablePointAlarming(const CtiTablePointAlarming& aRef);
   CtiTablePointAlarming(Cti::RowReader &rdr);
   virtual ~CtiTablePointAlarming();

   CtiTablePointAlarming& operator=(const CtiTablePointAlarming& aRef);

   bool operator<(const CtiTablePointAlarming &rhs) const;

   static std::string getTableName();

   static void getSQL(std::string &sql, LONG pointID, LONG paoID, const std::set<long> &pointIds);
   virtual UINT getAlarmCategory(const INT offset)  const;
   virtual UINT getExcludeNotifyStates()            const;
   virtual UINT getAutoAckStates()                  const;
   virtual BOOL getNotifyOnAcknowledge()            const;
   virtual BOOL getNotifyOnClear()                  const;
   virtual UINT getNotificationGroupID()            const;
   LONG         getPointID()                        const;

   virtual bool isNotifyExcluded( int alarm ) const;
   virtual bool isAutoAcked     ( int alarm ) const;
   virtual bool alarmOn         ( int alarm ) const;
   virtual INT  alarmPriority   ( int alarm ) const;
};

class IM_EX_CTIYUKONDB Test_CtiTablePointAlarming : public CtiTablePointAlarming
{
private:
   typedef CtiTablePointAlarming Inherited;
public:
   CtiTablePointAlarming& operator=(const CtiTablePointAlarming& aRef) { Inherited::operator=(aRef); return *this; }

   CtiTablePointAlarming& setAlarmCategory      ( const INT offset, const UINT &aInt ) { return Inherited::setAlarmCategory(offset,aInt); }
};

#endif // #ifndef __TBL_PT_ALARM_H__
