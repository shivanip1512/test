#include "msg_server_req.h"
#include "logger.h"

RWDEFINE_COLLECTABLE( CtiServerRequestMsg, MSG_SERVER_REQUEST );

CtiServerRequestMsg::CtiServerRequestMsg(const CtiServerRequestMsg& req)
{
    operator=(req);
}

void CtiServerRequestMsg::saveGuts(RWvostream &strm) const
{
    Inherited::saveGuts(strm);
    strm << _id << _payload;
}

void CtiServerRequestMsg::restoreGuts(RWvistream &strm)
{
    Inherited::restoreGuts(strm);
    strm >> _id >> _payload;
}

void CtiServerRequestMsg::What() const
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << "CtiServerRequestMsg.... " << endl;
}

void CtiServerRequestMsg::dump() const
{
   Inherited::dump();

   CtiLockGuard<CtiLogger> doubt_guard(dout);
   dout << " Request ID:  " << _id << endl;
}

CtiMessage* CtiServerRequestMsg::replicateMessage() const
{
    CtiServerRequestMsg *ret = CTIDBG_new CtiServerRequestMsg(*this);
    return((CtiMessage*) ret);
}

CtiServerRequestMsg::CtiServerRequestMsg()
: _id(-1), _payload(NULL) {}

CtiServerRequestMsg::CtiServerRequestMsg(int id, RWCollectable* payload)
: _id(id), _payload(payload) { }

CtiServerRequestMsg::~CtiServerRequestMsg() {}

/* Boring, canonical "getter" and "setter" member functions below */
int CtiServerRequestMsg::getID() const
{
    return _id;
}

CtiServerRequestMsg& CtiServerRequestMsg::setID(int id)
{
    _id = id;
    return *this;
}

RWCollectable* CtiServerRequestMsg::getPayload() const
{
    return _payload;
}

CtiServerRequestMsg& CtiServerRequestMsg::setPayload(RWCollectable* payload)
{
    _payload = payload;
    return *this;
}
