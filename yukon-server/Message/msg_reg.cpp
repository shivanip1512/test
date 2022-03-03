#include "precompiled.h"

#include "collectable.h"
#include "logger.h"
#include "msg_reg.h"

using namespace std;

DEFINE_COLLECTABLE( CtiRegistrationMsg, MSG_REGISTER );

// Return a new'ed copy of this message!
CtiMessage* CtiRegistrationMsg::replicateMessage() const
{
   CtiRegistrationMsg *ret = CTIDBG_new CtiRegistrationMsg(*this);

   return( (CtiMessage*)ret );
}


std::string CtiRegistrationMsg::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiRegistrationMsg";
    itemList.add("Client Name")                  << _appName;
    itemList.add("Client App Id")                << _appId;
    itemList.add("Is client unique?")            << _appIsUnique;
    itemList.add("Client Expiration Delay")      << _appExpirationDelay; // How many seconds till I believe this guy is DEAD.

    return (Inherited::toString() += itemList.toString());
}

CtiRegistrationMsg::CtiRegistrationMsg() :
   CtiMessage(15),
   _appIsUnique(false),
   _appId(0),
   _appExpirationDelay(900)          // 15 minute default delay...
{
}

CtiRegistrationMsg::CtiRegistrationMsg(string str, int id, bool unique) :
   CtiMessage(15),
   _appIsUnique(unique),
   _appName(str),
   _appId(id),
   _appExpirationDelay(900)          // 15 minute default delay...
{
}

CtiRegistrationMsg::CtiRegistrationMsg(const CtiRegistrationMsg &aRef)
{
   *this = aRef;
}

CtiRegistrationMsg::~CtiRegistrationMsg()
{
}

// Assignement operator
CtiRegistrationMsg& CtiRegistrationMsg::operator=(const CtiRegistrationMsg& aRef)
{
   if(this != &aRef)
   {
      // From the base class
      Inherited::operator=(aRef);

      _appIsUnique          = aRef.getAppIsUnique();
      _appName              = aRef.getAppName();
      _appId                = aRef.getAppId();
      _appExpirationDelay   = aRef.getAppExpirationDelay();
   }

   return (*this);
}

string   CtiRegistrationMsg::getAppName() const            { return _appName; }
string&  CtiRegistrationMsg::getAppName()                  { return _appName; }
void     CtiRegistrationMsg::setAppName(string str)        { _appName = str; }

int      CtiRegistrationMsg::getAppId() const              { return _appId;   }
void     CtiRegistrationMsg::setAppID(int id)              { _appId = id;    }

bool     CtiRegistrationMsg::getAppIsUnique() const        { return _appIsUnique; }
void     CtiRegistrationMsg::setAppIsUnique(bool b)        { _appIsUnique = b;    }

int      CtiRegistrationMsg::getAppExpirationDelay() const { return _appExpirationDelay; }
void     CtiRegistrationMsg::setAppExpirationDelay(int d)  { _appExpirationDelay = d; }

std::size_t CtiRegistrationMsg::getVariableSize() const
{
    return  Inherited::getVariableSize()
        +   dynamic_sizeof( _appName );
}

