#include "yukon.h"
#include "msg_server_resp.h"
#include "logger.h"
#include "rwutil.h"

RWDEFINE_COLLECTABLE( CtiServerResponseMsg, MSG_SERVER_RESPONSE );

CtiServerResponseMsg::CtiServerResponseMsg(const CtiServerResponseMsg& resp)
{
    operator=(resp);
}

void CtiServerResponseMsg::saveGuts(RWvostream &strm) const
{
    /*
     * payload is optional, send a TRUE if we plan on passing
     * some payload on
     */
    Inherited::saveGuts(strm);
    strm << _id << _status << _message;
    if(_payload != NULL)
    {
	strm << 1 << _payload;
    }
    else
    {
	strm << 0;
    }
}

void CtiServerResponseMsg::restoreGuts(RWvistream &strm)
{
    int has_payload;

    /*
     * payload is optional, check for TRUE before reading
     * the payload
     */
    Inherited::restoreGuts(strm);
    strm >> _id >> _status >> _message >> has_payload;
    if(has_payload)
    {
	strm >> _payload;
    }
}

void CtiServerResponseMsg::What() const
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << "CtiServerResponseMsg.... " << endl;
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
: _id(-1), _status(UNINIT), _message(""), _payload(NULL) {}

CtiServerResponseMsg::CtiServerResponseMsg(int id, int status, string message)
: _id(id), _status(status), _message(message), _payload(NULL) { }
CtiServerResponseMsg::~CtiServerResponseMsg() {}

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

RWCollectable* CtiServerResponseMsg::getPayload() const
{
    return _payload;
}

CtiServerResponseMsg& CtiServerResponseMsg::setPayload(RWCollectable* payload)
{
    _payload = payload;
    return *this;
}
