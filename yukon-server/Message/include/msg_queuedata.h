#pragma once

#include "dlldefs.h"
#include "message.h"

class IM_EX_MSG CtiQueueDataMsg : public CtiMessage
{
public:
    DECLARE_COLLECTABLE( CtiQueueDataMsg )

protected:

   long           _queueID;
   long           _requestId;         // RequestID, if any.
   unsigned       _rate;              // Time in ms to send out 1 entry
   unsigned       _queueCount;        // Count of items in queue
   unsigned       _requestIdCount;    // Count of items with requestID _requestID
   long           _userMessageID;     // ID sent by user in request message

   CtiTime        _time;

public:

   typedef CtiMessage Inherited;


   CtiQueueDataMsg(long       _queueID          = 0,
                   unsigned   _queueCount       = 0,
                   unsigned   _rate             = 0,
                   long       _requestId        = 0,
                   unsigned   _requestIDCount   = 0,
                   long       _userMessageID    = 0
                   );

   CtiQueueDataMsg(const CtiQueueDataMsg &aRef);
   virtual ~CtiQueueDataMsg();

   CtiQueueDataMsg& operator=(const CtiQueueDataMsg& aRef);

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

   long UserMessageId() const;
   CtiQueueDataMsg& setUserMessageId(long user_message_id );

   const CtiTime& getTime() const;
   CtiTime& getTime();
   CtiQueueDataMsg& setTime(const CtiTime& aTime);

   std::size_t getFixedSize() const override    { return sizeof( *this ); }
   std::size_t getVariableSize() const override;

   std::string toString() const override;
};
