#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   pending_stat_operation
*
* Date:   7/23/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/02/10 23:23:45 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "ctidbgmem.h"
#include "logger.h"
#include "pending_stat_operation.h"

CtiPendingStatOperation::CtiPendingStatOperation(ULONG ser, UINT op) :
        _serial(ser),
        _operation(op),
        _expires(RWTime() + 300L),      // 5 minutes
        _responded(RWTime(86400)),      // Dawn of time or so
        _confirmed(RWTime(86400)),      // Dawn of time or so
        _match(false),                  // No match.
        _reportType(NoReport),
        _outMessage(0)
{
}

CtiPendingStatOperation::CtiPendingStatOperation(const CtiPendingStatOperation& aRef) :
    _outMessage(0)
{
    *this = aRef;
}

CtiPendingStatOperation::~CtiPendingStatOperation()
{
    if(_outMessage)
    {
        delete _outMessage;
        _outMessage = 0;
    }
}

CtiPendingStatOperation& CtiPendingStatOperation::operator=(const CtiPendingStatOperation& aRef)
{
    if(this != &aRef)
    {
        _serial = aRef.getSerial();
        _operation = aRef.getOperation();

        _submitted = aRef.getTimeSubmitted();
        _expires = aRef.getTimeExpires();
        _responded = aRef.getTimeResponded();
        _confirmed = aRef.getTimeConfirmed();

        _match = aRef.getMatched();

        _reportType = aRef.getReportType();

        _replyVector = aRef.getConstReplyVector();

        if(_outMessage)
        {
            delete _outMessage;
            _outMessage = 0;
        }
        const CtiOutMessage *pOld = aRef.getOutMessage();
        if(pOld) _outMessage = CTIDBG_new CtiOutMessage(*pOld);
    }

    return *this;
}

bool CtiPendingStatOperation::operator<(const CtiPendingStatOperation& aRef) const
{
    bool less;

    if( _serial == aRef.getSerial() )
    {
        less = _operation < aRef.getOperation();
    }
    else
    {
        less = _serial < aRef.getSerial();
    }

    return less;
}
bool CtiPendingStatOperation::operator()(const CtiPendingStatOperation& aRef) const
{
    return operator<(aRef);
}

ULONG CtiPendingStatOperation::getSerial() const
{
    return _serial;
}
CtiPendingStatOperation& CtiPendingStatOperation::setSerial(ULONG ul)
{
    _serial = ul;
    return *this;
}

INT CtiPendingStatOperation::getReportType() const
{
    return _reportType;
}
CtiPendingStatOperation& CtiPendingStatOperation::setReportType(INT i)
{
    _reportType = i;
    return *this;
}

UINT CtiPendingStatOperation::getOperation() const
{
    return _operation;
}
CtiPendingStatOperation& CtiPendingStatOperation::setOperation(UINT ui)
{
    _operation = ui;
    return *this;
}

RWTime CtiPendingStatOperation::getTimeSubmitted() const
{
    return _submitted;
}
CtiPendingStatOperation& CtiPendingStatOperation::setTimeSubmitted(const RWTime &rwt)
{
    _submitted = rwt;
    return *this;
}

RWTime CtiPendingStatOperation::getTimeExpires() const
{
    return _expires;
}
CtiPendingStatOperation& CtiPendingStatOperation::setTimeExpires(const RWTime &rwt)
{
    _expires = rwt;
    return *this;
}

RWTime CtiPendingStatOperation::getTimeResponded() const
{
    return _responded;
}
CtiPendingStatOperation& CtiPendingStatOperation::setTimeResponded(const RWTime &rwt)
{
    _responded = rwt;
    return *this;
}

RWTime CtiPendingStatOperation::getTimeConfirmed() const
{
    return _confirmed;
}
CtiPendingStatOperation& CtiPendingStatOperation::setTimeConfirmed(const RWTime &rwt)
{
    _confirmed = rwt;
    return *this;
}

bool CtiPendingStatOperation::getMatched() const
{
    return _match;
}
CtiPendingStatOperation& CtiPendingStatOperation::setMatched(bool match)
{
    _match = match;
    return *this;
}

const CtiOutMessage* CtiPendingStatOperation::getOutMessage() const
{
    return _outMessage;
}

CtiPendingStatOperation& CtiPendingStatOperation::setOutMessage(const CtiOutMessage *&OM)
{
    if(_outMessage) delete _outMessage;
    _outMessage = 0;

    if(OM)
    {
        _outMessage = CTIDBG_new CtiOutMessage(*OM);
    }

    return *this;
}


CtiPendingStatOperation::PGRReplyVector_t& CtiPendingStatOperation::getReplyVector()
{
    return _replyVector;
}
CtiPendingStatOperation::PGRReplyVector_t CtiPendingStatOperation::getConstReplyVector() const
{
    return _replyVector;
}

void CtiPendingStatOperation::addReplyVector(RWCString &str)
{
    _replyVector.push_back(str);
    return;
}


