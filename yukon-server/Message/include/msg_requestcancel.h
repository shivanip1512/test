  #pragma warning( disable : 4786 )  // No truncated debug name warnings please....

/*-----------------------------------------------------------------------------*
*
* File:   msg_requestcancel
*
* Date:   1/22/2007
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/INCLUDE/msg_queuedata.h-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2007/02/22 17:49:48 $
*
* Copyright (c) 2007 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#ifndef __MSG_REQUESTCANCEL_H__
#define __MSG_REQUESTCANCEL_H__

#include <windows.h>
#include <rw/rwset.h>

#include "dlldefs.h"
#include "message.h"

#include "yukon.h"

class IM_EX_MSG CtiRequestCancelMsg : public CtiMessage
{
protected:

   long           _requestId;         // RequestID, if any.
   unsigned       _requestIdCount;    // Count of items with requestID _requestID
   long           _userMessageID;     // ID sent by user in request message

   CtiTime        _time;

public:

   RWDECLARE_COLLECTABLE( CtiRequestCancelMsg );

   typedef CtiMessage Inherited;

   CtiRequestCancelMsg(long   _requestId        = 0,
                   unsigned   _requestIDCount   = 0,
                   long       _userMessageID    = 0
                   );

   CtiRequestCancelMsg(const CtiRequestCancelMsg &aRef);
   virtual ~CtiRequestCancelMsg();

   CtiRequestCancelMsg& operator=(const CtiRequestCancelMsg& aRef);

   virtual void saveGuts(RWvostream &aStream) const;
   virtual void restoreGuts(RWvistream& aStream);
   virtual CtiMessage* replicateMessage() const;

   long  getRequestId() const;
   CtiRequestCancelMsg& setRequestId( const long a_id );

   unsigned  getRequestIdCount() const;
   CtiRequestCancelMsg& setRequestIdCount( const unsigned count );

   long UserMessageId() const;
   CtiRequestCancelMsg& setUserMessageId(long user_message_id );

   const CtiTime& getTime() const;
   CtiTime& getTime();
   CtiRequestCancelMsg& setTime(const CtiTime& aTime);

   virtual void dump() const;
};
#endif
