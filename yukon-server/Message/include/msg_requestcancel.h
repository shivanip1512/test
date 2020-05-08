#pragma once

#include "dlldefs.h"
#include "message.h"

class IM_EX_MSG CtiRequestCancelMsg : public CtiMessage
{
public:
    DECLARE_COLLECTABLE( CtiRequestCancelMsg )

protected:

   long           _requestId;         // RequestID, if any.
   unsigned       _requestIdCount;    // Count of items with requestID _requestID
   long           _userMessageID;     // ID sent by user in request message

   CtiTime        _time;

public:

   typedef  CtiMessage  Inherited;

   CtiRequestCancelMsg(long   _requestId        = 0,
                   unsigned   _requestIDCount   = 0,
                   long       _userMessageID    = 0
                   );

   CtiRequestCancelMsg(const CtiRequestCancelMsg &aRef);
   virtual ~CtiRequestCancelMsg();

   CtiRequestCancelMsg& operator=(const CtiRequestCancelMsg& aRef);

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

   std::size_t getFixedSize() const override    { return sizeof( *this ); }
   std::size_t getVariableSize() const override;

   std::string toString() const override;
};
