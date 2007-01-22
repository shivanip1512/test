  #pragma warning( disable : 4786 )  // No truncated debug name warnings please....

/*-----------------------------------------------------------------------------*
*
* File:   msg_queuedata
*
* Date:   1/22/2007
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/INCLUDE/msg_queuedata.h-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2007/01/22 21:39:32 $
*
* Copyright (c) 2007 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#ifndef __MSG_QUEUEDATA_H__
#define __MSG_QUEUEDATA_H__

#include <windows.h>
#include <rw/rwset.h>

#include "dlldefs.h"
#include "message.h"

#include "yukon.h"

class IM_EX_MSG CtiQueueDataMsg : public CtiMessage
{
protected:

   long           _id;
   long           _requestId;         // RequestID, if any.
   unsigned       _rate;              // Time in ms to send out 1 entry
   unsigned       _queueCount;        // Count of items in queue
   unsigned       _requestIdCount;    // Count of items with requestID _requestID

   CtiTime        _time;

public:

   RWDECLARE_COLLECTABLE( CtiQueueDataMsg );

   typedef CtiMessage Inherited;

   CtiQueueDataMsg(long       _id               = 0,
                   unsigned   _queueCount       = 0,
                   unsigned   _rate             = 0,
                   long       _requestId        = 0,
                   unsigned   _requestIDCount   = 0
                   );

   CtiQueueDataMsg(const CtiQueueDataMsg &aRef);
   virtual ~CtiQueueDataMsg();

   CtiQueueDataMsg& operator=(const CtiQueueDataMsg& aRef);

   virtual void saveGuts(RWvostream &aStream) const;
   virtual void restoreGuts(RWvistream& aStream);
   virtual CtiMessage* replicateMessage() const;


   long  getId() const;
   CtiQueueDataMsg& setId( const long a_id );

   long  getRequestId() const;
   CtiQueueDataMsg& setRequestId( const long a_id );

   unsigned  getRate() const;
   CtiQueueDataMsg& setRate( const unsigned rate );

   unsigned  getQueueCount() const;
   CtiQueueDataMsg& setQueueCount( const unsigned count );

   unsigned  getRequestIdCount() const;
   CtiQueueDataMsg& setRequestIdCount( const unsigned count );

   const CtiTime& getTime() const;
   CtiTime& getTime();
   CtiQueueDataMsg& setTime(const CtiTime& aTime);

   virtual void dump() const;
};
#endif
