#pragma once

#include "message.h"       // get the base class
#include "dlldefs.h"


class IM_EX_MSG CtiRegistrationMsg : public CtiMessage
{
public:
    DECLARE_COLLECTABLE( CtiRegistrationMsg )

private:

   std::string    _appName;
   int            _appId;
   bool           _appIsUnique;

   int            _appExpirationDelay;     // How many seconds till I believe this guy is DEAD.

public:

   typedef  CtiMessage  Inherited;

   CtiRegistrationMsg();
   CtiRegistrationMsg(std::string str, int id, bool unique);
   CtiRegistrationMsg(const CtiRegistrationMsg &aRef);
   virtual ~CtiRegistrationMsg();
   // Assignement operator
   CtiRegistrationMsg& operator=(const CtiRegistrationMsg& aRef);

   CtiMessage* replicateMessage() const override;

   std::string   getAppName() const;
   std::string&  getAppName();
   void          setAppName(std::string str);

   int   getAppId() const;
   void  setAppID(int id);

   bool  getAppIsUnique() const;
   void  setAppIsUnique(bool b);

   int   getAppExpirationDelay() const;
   void  setAppExpirationDelay(int d);

   virtual std::string toString() const override;
};
