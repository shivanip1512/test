#include "precompiled.h"

#include "dlldefs.h"
#include "collectable.h"
#include "con_mgr.h"
#include "msg_server_resp.h"
#include "msg_cmd.h"


CtiConnectionManager::CtiConnectionManager( const std::string& replyToName, const std::string& serverQueueName, Que_t *inQ ) :
   ClientName("DEFAULT"),
   ClientAppId(0),
   ClientUnique(FALSE),
   ClientQuestionable(FALSE),
   ClientRegistered(FALSE),
   _clientExpirationDelay(900),
   _serverRequestId(0),
   CtiServerConnection( replyToName, serverQueueName, inQ )
{
    CTILOG_DEBUG( dout, who() << " - CtiConnectionManager::CtiConnectionManager() @0x" << std::hex << this );
}

CtiConnectionManager::~CtiConnectionManager()
{
    CTILOG_DEBUG( dout, who() << " - CtiConnectionManager::~CtiConnectionManager() @0x" << std::hex << this );

   // We better just do the normal stuff one at a time... Make sure this is virtual
   // so base destructor gets called in all cases.

    // Inherited::ShutdownConnection(); // Handled in the base class...
}

int CtiConnectionManager::getClientAppId() const              { return ClientAppId; }
int CtiConnectionManager::setClientAppId(int id)       { return ClientAppId = id; }

std::string   CtiConnectionManager::getClientName() const               { return ClientName; }
void          CtiConnectionManager::setClientName(std::string str)
{
   ClientName = str;
   Inherited::setName( str );
}

bool CtiConnectionManager::getClientUnique() const          {  return ClientUnique;  }
void CtiConnectionManager::setClientUnique(bool b)          {  ClientUnique = b;     }

bool CtiConnectionManager::getClientQuestionable() const    {  return ClientQuestionable;  }
void CtiConnectionManager::setClientQuestionable(bool b)    {  ClientQuestionable = b;     }

bool CtiConnectionManager::getClientRegistered()            {  return ClientRegistered;  }
void CtiConnectionManager::setClientRegistered(bool b)      {  ClientRegistered = b;     }

int  CtiConnectionManager::getClientExpirationDelay() const {  return _clientExpirationDelay;  }
void CtiConnectionManager::setClientExpirationDelay(int p)  {  _clientExpirationDelay = p;     }

bool CtiConnectionManager::operator==(const CtiConnectionManager& aRef) const
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

YukonError_t CtiConnectionManager::WriteConnQue( std::unique_ptr<CtiMessage> pMsg, Cti::CallSite cs, unsigned millitimeout, int payload_status, std::string payload_string )
{
    return WriteConnQue(pMsg.release(), cs, millitimeout, payload_status, payload_string);
}

YukonError_t CtiConnectionManager::WriteConnQue( CtiMessage *pMsg, Cti::CallSite cs, unsigned millitimeout, int payload_status, std::string payload_string )
{
    CtiMessage *pWrite = pMsg;  // Default to sending the original message.

    if( getRequestId() )    // This client is waiting to send a response!  Wrap it the way he wants it.
    {
        CtiServerResponseMsg* resp = new CtiServerResponseMsg(getRequestId(), payload_status, payload_string);
        resp->setPayload(pMsg);
        setRequestId(0);        // Only one per request please!
        pWrite = resp;
    }

    return Inherited::WriteConnQue(pWrite, CALLSITE, millitimeout);
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
