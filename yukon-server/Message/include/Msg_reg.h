#pragma once

#include <rw/thr/threadid.h>

#include "message.h"       // get the base class
#include "dlldefs.h"


class IM_EX_MSG CtiRegistrationMsg : public CtiMessage
{
private:

   std::string      _appName;
   int            _appId;
   int            _appIsUnique;

   int            _appKnownPort;
   int            _appExpirationDelay;     // How many seconds till I believe this guy is DEAD.

public:
   RWDECLARE_COLLECTABLE( CtiRegistrationMsg );

   typedef CtiMessage Inherited;

   CtiRegistrationMsg();
   CtiRegistrationMsg(std::string str, int id, RWBoolean bUnique, int port = -1, int delay = 900);
   CtiRegistrationMsg(const CtiRegistrationMsg &aRef);
   virtual ~CtiRegistrationMsg();
   // Assignement operator
   CtiRegistrationMsg& CtiRegistrationMsg::operator=(const CtiRegistrationMsg& aRef);

   void saveGuts(RWvostream &aStream) const;
   void restoreGuts(RWvistream& aStream);
   virtual CtiMessage* replicateMessage() const;


   std::string   getAppName() const;
   std::string&  getAppName();
   void        setAppName(std::string str);

   int         getAppId() const;
   void        setAppID(int id);

   RWBoolean   getAppIsUnique() const;
   void        setAppIsUnique(RWBoolean b);

   int         getAppKnownPort() const;
   void        setAppKnownPort(int p);

   int         getAppExpirationDelay() const;
   void        setAppExpirationDelay(int d);

   virtual void dump() const;
};
