#pragma once

#include <limits.h>


#include <limits.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
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

   static std::string getSqlForFullLoad();
   static std::string getSqlForPaoId();
   static std::string getSqlForPaoIdAndPointId();
   static std::string getSqlForPointId();
   static std::string getSqlForPointIds(const size_t count);

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
