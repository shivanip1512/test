#include "precompiled.h"
#include "msg_server_resp.h"
#include "logger.h"

using std::endl;
using std::string;
using std::auto_ptr;

using boost::shared_ptr;

DEFINE_COLLECTABLE( CtiServerResponseMsg, MSG_SERVER_RESPONSE );

CtiServerResponseMsg::CtiServerResponseMsg(const CtiServerResponseMsg& resp)
: _id(-1), _status(UNINIT), _message(""), _payload(NULL)
{
    operator=(resp);
}

void CtiServerResponseMsg::dump() const
{
    Inherited::dump();

    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << " Response ID:  " << _id << endl;
    dout << " Status:  " << endl;
    dout << " Message:  " << endl;
}

CtiMessage* CtiServerResponseMsg::replicateMessage() const
{
    CtiServerResponseMsg *ret = CTIDBG_new CtiServerResponseMsg(*this);
    return((CtiMessage*) ret);
}

CtiServerResponseMsg::CtiServerResponseMsg()
: _id(-1), _status(UNINIT), _message(""), _payload(NULL)
{
}

CtiServerResponseMsg::CtiServerResponseMsg(int id, int status, string message)
: _id(id), _status(status), _message(message), _payload(NULL)
{
}

CtiServerResponseMsg::~CtiServerResponseMsg()
{
    if(_payload) delete _payload;
}

/* Boring, canonical "getter" and "setter" member functions below */
int CtiServerResponseMsg::getID() const
{
    return _id;
}

CtiServerResponseMsg& CtiServerResponseMsg::setID(int id)
{
    _id = id;
    return *this;
}

int CtiServerResponseMsg::getStatus() const
{
    return _status;
}

CtiServerResponseMsg& CtiServerResponseMsg::setStatus(int status)
{
    _status = status;
    return *this;
}

const string& CtiServerResponseMsg::getMessage() const
{
    return _message;
}

CtiServerResponseMsg& CtiServerResponseMsg::setMessage(const string& message)
{
    _message = message;
    return *this;
}

CtiMessage* CtiServerResponseMsg::releasePayload()
{
    CtiMessage *p = _payload;
    _payload = 0;
    return p;
}

CtiMessage* CtiServerResponseMsg::getPayload() const
{
    return _payload;
}

CtiServerResponseMsg& CtiServerResponseMsg::setPayload(CtiMessage* payload)
{
    if(_payload) delete _payload;
    _payload = payload;

    return *this;
}

CtiServerResponseMsg& CtiServerResponseMsg::operator=(const CtiServerResponseMsg& aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);
      setID(aRef.getID());
      setStatus(aRef.getStatus());
      setMessage(aRef.getMessage());
      setPayload(aRef.getPayload() ? aRef.getPayload()->replicateMessage() : 0);
   }

   return *this;
}


