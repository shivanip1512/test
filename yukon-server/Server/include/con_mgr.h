#pragma once

#include <limits.h>

#include "connection_server.h"
#include "dllbase.h"


class IM_EX_CTISVR CtiConnectionManager : public CtiServerConnection
{
protected:

   int  _clientExpirationDelay;                  // Number of seconds until we need to be pinged

   struct         // Bit field status definitions
   {
      unsigned       ClientRegistered   : 1;
      unsigned       ClientUnique       : 1; // Is this the only allowed client of this name?
      unsigned       ClientQuestionable : 1;
   };

   std::string       ClientName;
   int               ClientAppId;

   int              _serverRequestId;

   virtual void writeIncomingMessageToQueue(CtiMessage *msgPtr);

public:

   typedef CtiConnection Inherited;

   CtiConnectionManager( const std::string & replyToName, const std::string & serverQueueName, Que_t* inQ = NULL );

   virtual ~CtiConnectionManager();

   int         getClientAppId() const;
   int         setClientAppId(int id);

   std::string getClientName() const;
   void        setClientName(std::string str);
   bool getClientUnique() const;
   void setClientUnique(bool b = true);

   bool getClientQuestionable() const;
   void setClientQuestionable(bool b = true);

   bool getClientRegistered();
   void setClientRegistered(bool b = true);

   int   getClientExpirationDelay() const;
   void  setClientExpirationDelay(int p);

   bool operator==(const CtiConnectionManager& aRef) const;
   static unsigned hash(const CtiConnectionManager& aRef);

   int getRequestId() const;
   void setRequestId(int rid);

   YukonError_t WriteConnQue( std::unique_ptr<CtiMessage>, Cti::CallSite cs, unsigned millitimeout = 0, int payload_status = 0, std::string payload_string = std::string() );
   YukonError_t WriteConnQue( CtiMessage*, Cti::CallSite cs, unsigned millitimeout = 0, int payload_status = 0, std::string payload_string = std::string() );
};
