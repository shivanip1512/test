

/*-----------------------------------------------------------------------------*
*
* File:   pending_gwresult
*
* Date:   6/23/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2003/07/21 21:34:41 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)

#include "ctidbgmem.h"
#include "logger.h"
#include "pending_gwresult.h"



CtiPendingGatewayResult::CtiPendingGatewayResult() :
    _messageType(0)
{
    _outMessage = 0;
    setTimeExpires( getTimeSubmitted() + 300 );   // Five minute expiration time!
}

CtiPendingGatewayResult::CtiPendingGatewayResult(const CtiPendingGatewayResult& aRef)
{
    _outMessage = 0;
    *this = aRef;
}

CtiPendingGatewayResult::~CtiPendingGatewayResult()
{
    if(_outMessage)
    {
        delete _outMessage;
        _outMessage = 0;
    }
}

CtiPendingGatewayResult& CtiPendingGatewayResult::operator=(const CtiPendingGatewayResult& aRef)
{
    if(this != &aRef)
    {
        _messageType = aRef.getMessageType();

        _submitted = aRef.getTimeSubmitted();
        _expires = aRef.getTimeExpires();

        if(_outMessage)
        {
            delete _outMessage;
            _outMessage = 0;
        }
        const CtiOutMessage *pOld = aRef.getLastControlMessage();
        if(pOld) _outMessage = CTIDBG_new CtiOutMessage(*pOld);

        _activeGWSet = aRef.getConstGWSet();

        _postedSet.clear();
        _respondedSet.clear();
        _confirmedSet.clear();
        _matchingOpSet.clear();

        _matchingOpSet = aRef.getConstMatchedSet();

        _postedSet = aRef.getConstPostedSet();
        _respondedSet = aRef.getConstRespondedSet();
        _confirmedSet = aRef.getConstConfirmedSet();
    }

    return *this;
}

bool CtiPendingGatewayResult::operator<(const CtiPendingGatewayResult& aRef) const
{
    bool lessthan = false;

    if( _messageType < aRef.getMessageType() )
    {
        lessthan = true;
    }
    else if(_messageType == aRef.getMessageType() && (_submitted < aRef.getTimeSubmitted()) )
    {
        lessthan = true;
    }

    return lessthan;
}
bool CtiPendingGatewayResult::operator()(const CtiPendingGatewayResult& aRef) const
{
    return operator<(aRef);
}

const CtiOutMessage* CtiPendingGatewayResult::getLastControlMessage() const
{
    return _outMessage;
}

CtiPendingGatewayResult& CtiPendingGatewayResult::setLastControlMessage(const CtiOutMessage *&OM)
{
    if(_outMessage) delete _outMessage;
    _outMessage = CTIDBG_new CtiOutMessage(*OM);
    return *this;
}

CtiPendingGatewayResult::PGWSet_t& CtiPendingGatewayResult::getGWSet()
{
    return _activeGWSet;
}
CtiPendingGatewayResult::PGWSet_t CtiPendingGatewayResult::getConstGWSet() const
{
    return _activeGWSet;
}

CtiPendingGatewayResult::ResSet_t& CtiPendingGatewayResult::getPostedSet()
{
    return _postedSet;
}

CtiPendingGatewayResult::ResSet_t& CtiPendingGatewayResult::getRespondedSet()
{
    return _respondedSet;
}

CtiPendingGatewayResult::ResSet_t& CtiPendingGatewayResult::getConfirmedSet()
{
    return _confirmedSet;
}

CtiPendingGatewayResult::ResSet_t& CtiPendingGatewayResult::getMatchedSet()
{
    return _matchingOpSet;
}

CtiPendingGatewayResult::ResSet_t CtiPendingGatewayResult::getConstPostedSet() const
{
    return _postedSet;
}

CtiPendingGatewayResult::ResSet_t CtiPendingGatewayResult::getConstRespondedSet() const
{
    return _respondedSet;
}

CtiPendingGatewayResult::ResSet_t CtiPendingGatewayResult::getConstConfirmedSet() const
{
    return _confirmedSet;
}

CtiPendingGatewayResult::ResSet_t CtiPendingGatewayResult::getConstMatchedSet() const
{
    return _matchingOpSet;
}

void CtiPendingGatewayResult::addPostedSet(LONG id)
{
    _postedSet.insert(id);
    return;
}
void CtiPendingGatewayResult::addRespondedSet(LONG id)
{
    _respondedSet.insert(id);
    return;
}
void CtiPendingGatewayResult::addConfirmedSet(LONG id)
{
    _confirmedSet.insert(id);
    return;
}
void CtiPendingGatewayResult::addMatchedSet(LONG id)
{
    _matchingOpSet.insert(id);
    return;
}
void CtiPendingGatewayResult::addGWSet(SOCKET sock)
{
    _activeGWSet.insert(sock);
    return;
}

bool CtiPendingGatewayResult::isPosted(LONG id) const
{
    ResSet_t::const_iterator itr = _postedSet.find( id );
    return (itr != _postedSet.end());
}
bool CtiPendingGatewayResult::isResponded(LONG id) const
{
    ResSet_t::const_iterator itr = _respondedSet.find( id );
    return (itr != _respondedSet.end());
}
bool CtiPendingGatewayResult::isConfirmed(LONG id) const
{
    ResSet_t::const_iterator itr = _confirmedSet.find( id );
    return (itr != _confirmedSet.end());
}

bool CtiPendingGatewayResult::includesGW(SOCKET sock) const
{
    PGWSet_t::const_iterator itr = _activeGWSet.find( sock );
    return (itr != _activeGWSet.end());
}

USHORT CtiPendingGatewayResult::getMessageType() const
{
    return _messageType;
}

RWTime CtiPendingGatewayResult::getTimeSubmitted() const
{
    return _submitted;
}

RWTime CtiPendingGatewayResult::getTimeExpires() const
{
    return _expires;
}

CtiPendingGatewayResult& CtiPendingGatewayResult::setMessageType(USHORT type)
{
    _messageType = type;
    return *this;
}

CtiPendingGatewayResult& CtiPendingGatewayResult::setTimeSubmitted(const RWTime &rwt)
{
    _submitted = rwt;
    return *this;
}

CtiPendingGatewayResult& CtiPendingGatewayResult::setTimeExpires(const RWTime &rwt)
{
    _expires = rwt;
    return *this;
}

bool CtiPendingGatewayResult::isExpired( RWTime &now ) const
{
    return now > _expires;
}

bool CtiPendingGatewayResult::isComplete( RWTime &now ) const
{
    bool status = false;

    if((_confirmedSet.size() + _matchingOpSet.size()) >= _postedSet.size())
    {
        // Everyone has been confirmed, or was already controling this way!
        status = true;
    }
    else if( _confirmedSet.size() >= _postedSet.size() )
    {
        // Everyone has been confirmed.
        status = true;
    }
    else
    {
        status = isExpired(now);
    }

    return status;
}


size_t CtiPendingGatewayResult::matchCount() const
{
    return _matchingOpSet.size();
}

size_t CtiPendingGatewayResult::postedCount() const
{
    return _postedSet.size();
}
size_t CtiPendingGatewayResult::respondedCount() const
{
    return _respondedSet.size();
}
size_t CtiPendingGatewayResult::confirmedCount() const
{
    return _confirmedSet.size();
}


CtiPendingGatewayResult::PGRReplyVector_t& CtiPendingGatewayResult::getReplyVector()
{
    return _replyVector;
}
CtiPendingGatewayResult::PGRReplyVector_t CtiPendingGatewayResult::getConstReplyVector() const
{
    return _replyVector;
}

void CtiPendingGatewayResult::addReplyVector(RWCString &str)
{
    _replyVector.push_back(str);
    return;
}


