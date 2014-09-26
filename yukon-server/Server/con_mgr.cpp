#include "precompiled.h"

#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include "dlldefs.h"
#include "collectable.h"
#include "con_mgr.h"
#include "msg_server_resp.h"
#include "msg_cmd.h"


CtiConnectionManager::CtiConnectionManager( CtiListenerConnection& listenerConn, Que_t *inQ ) :
   ClientName("DEFAULT"),
   ClientAppId(0),
   ClientUnique(FALSE),
   ClientQuestionable(FALSE),
   ClientRegistered(FALSE),
   _clientExpirationDelay(180),
   _serverRequestId(0),
   CtiServerConnection(listenerConn, inQ)
{}

CtiConnectionManager::~CtiConnectionManager()
{
   // We better just do the normal stuff one at a time... Make sure this is virtual
   // so base destructor gets called in all cases.

    // Inherited::ShutdownConnection(); // Handled in the base class...
}

int CtiConnectionManager::getClientAppId() const              { return ClientAppId; }
int CtiConnectionManager::setClientAppId(int id)       { return ClientAppId = id; }

string   CtiConnectionManager::getClientName() const               { return ClientName; }
void        CtiConnectionManager::setClientName(string str)
{
   ClientName = str;
   Inherited::setName( str );
}

RWBoolean   CtiConnectionManager::getClientUnique() const             { return RWBoolean(ClientUnique);  }
void        CtiConnectionManager::setClientUnique(RWBoolean b) { ClientUnique = RWBoolean(b);     }

RWBoolean   CtiConnectionManager::getClientQuestionable() const             { return RWBoolean(ClientQuestionable);  }
void        CtiConnectionManager::setClientQuestionable(RWBoolean b) { ClientQuestionable = RWBoolean(b);     }

RWBoolean   CtiConnectionManager::getClientRegistered()               { return RWBoolean(ClientRegistered);}
void        CtiConnectionManager::setClientRegistered(RWBoolean b) { ClientRegistered = RWBoolean(b); }

int   CtiConnectionManager::getClientExpirationDelay() const    {return _clientExpirationDelay; }
void  CtiConnectionManager::setClientExpirationDelay(int p)     {_clientExpirationDelay = p; }

RWBoolean CtiConnectionManager::operator==(const CtiConnectionManager& aRef) const
{
   return (this == &aRef);
}

unsigned CtiConnectionManager::hash(const CtiConnectionManager& aRef)
{
   return (unsigned)&aRef;            // The address of the Object?
}

int CtiConnectionManager::getRequestId() const
{
    return _serverRequestId;
}
void CtiConnectionManager::setRequestId(int rid)
{
    _serverRequestId = rid;
}

int CtiConnectionManager::WriteConnQue(CtiMessage *pMsg, unsigned millitimeout, int payload_status, string payload_string )
{
    CtiMessage *pWrite = pMsg;  // Default to sending the original message.

    if( getRequestId() )    // This client is waiting to send a response!  Wrap it the way he wants it.
    {
        CtiServerResponseMsg* resp = new CtiServerResponseMsg(getRequestId(), payload_status, payload_string);
        resp->setPayload(pMsg);
        setRequestId(0);        // Only one per request please!
        pWrite = resp;
    }

    return Inherited::WriteConnQue(pWrite, millitimeout);
}

void CtiConnectionManager::writeIncomingMessageToQueue(CtiMessage *msgPtr)
{
    if( msgPtr != NULL && msgPtr->isA() == MSG_COMMAND )
    {
        CtiCommandMsg *cmdMsg = (CtiCommandMsg *)msgPtr;

        if( cmdMsg->getOperation() == CtiCommandMsg::AreYouThere )
        {
            setClientQuestionable(FALSE);
        }
    }

    Inherited::writeIncomingMessageToQueue(msgPtr);
}
