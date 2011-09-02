#pragma once

#include <limits.h>

#include <rw/thr/mutex.h>
#include <rw/thr/thrfunc.h>
#include <rw/toolpro/sockport.h>
#include <rw/toolpro/inetaddr.h>

#include "connection.h"
#include "ctibase.h"
#include "string_utility.h"

class IM_EX_CTISVR CtiConnectionManager : public CtiConnection
{
protected:

   int  _clientExpirationDelay;                  // Number of seconds until we need to be pinged

   struct         // Bit field status definitions
   {
      unsigned       ClientRegistered   : 1;
      unsigned       ClientUnique       : 1; // Is this the only allowed client of this name?
      unsigned       ClientQuestionable : 1;
   };

   std::string         ClientName;
   int               ClientAppId;

   int              _serverRequestId;

   CtiConnectionManager() :
      ClientRegistered(FALSE),
      ClientName("DEFAULT")
   {
      std::cout << "Default Constructor may break things!" << FO(__FILE__) << " " << __LINE__ << std::endl;
      std::cout << "**** Connection Manager!!! *****" << std::endl;
   }

   virtual void writeIncomingMessageToQueue(CtiMessage *msgPtr);
public:

   typedef CtiConnection Inherited;

   CtiConnectionManager( const INT &Port, const std::string &HostMachine, Que_t *inQ = NULL );

   CtiConnectionManager(CtiExchange *xchg, Que_t *inQ = NULL);

   virtual ~CtiConnectionManager();
   int         getClientAppId() const;//              { return ClientAppId; }
   int         setClientAppId(int id);//       { return ClientAppId = id; }

   std::string   getClientName() const;//               { return ClientName; }
   void        setClientName(std::string str);
   RWBoolean   getClientUnique() const;//             { return RWBoolean(ClientUnique);  }
   void        setClientUnique(RWBoolean b = TRUE);// { ClientUnique = RWBoolean(b);     }

   RWBoolean   getClientQuestionable() const;//             { return RWBoolean(ClientQuestionable);  }
   void        setClientQuestionable(RWBoolean b = TRUE);// { ClientQuestionable = RWBoolean(b);     }

   RWBoolean   getClientRegistered();//               { return RWBoolean(ClientRegistered);}
   void        setClientRegistered(RWBoolean b = TRUE);// { ClientRegistered = RWBoolean(b); }

   int   getClientExpirationDelay() const;//    {return _clientExpirationDelay; }
   void  setClientExpirationDelay(int p);//     {_clientExpirationDelay = p; }

   RWBoolean operator==(const CtiConnectionManager& aRef) const;
   static unsigned hash(const CtiConnectionManager& aRef);

   int getRequestId() const;
   void setRequestId(int rid);

   int WriteConnQue(CtiMessage*, unsigned millitimeout = 0, bool cleaniftimedout = true, int payload_status = 0, std::string payload_string = std::string() );
};
