
/*-----------------------------------------------------------------------------*
*
* File:   msg_queuedata
*
* Date:   1/22/2007
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/INCLUDE/msg_queuedata.h-arc  $
* REVISION     :  $Revision: 1.3.20.1 $
* DATE         :  $Date: 2008/11/13 17:23:45 $
*
* Copyright (c) 2007 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include <iostream>
#include <iomanip>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore


#include "message.h"
#include "logger.h"
#include "msg_queuedata.h"
#include "collectable.h"


RWDEFINE_COLLECTABLE( CtiQueueDataMsg, MSG_QUEUEDATA );

CtiQueueDataMsg::CtiQueueDataMsg(
                                long       queid,
                                unsigned   queueCount,
                                unsigned   rate,
                                long       requestId,
                                unsigned   requestIdCount,
                                long       userMessageId ) :
_queueID(queid),
_queueCount(queueCount),
_rate(rate),
_requestId(requestId),
_requestIdCount(requestIdCount),
_userMessageID(userMessageId),
_time(CtiTime())
{
}

CtiQueueDataMsg::CtiQueueDataMsg(const CtiQueueDataMsg &aRef)
{
    *this = aRef;
}

CtiQueueDataMsg::~CtiQueueDataMsg()
{
}

CtiQueueDataMsg& CtiQueueDataMsg::operator=(const CtiQueueDataMsg& aRef)
{
    if( this != &aRef )
    {
        Inherited::operator=(aRef);

        _queueID            = aRef.getId();
        _requestId          = aRef.getRequestId();
        _rate               = aRef.getRate();
        _queueCount         = aRef.getQueueCount();
        _requestIdCount     = aRef.getRequestIdCount();
        _userMessageID      = aRef.UserMessageId();

        _time               = aRef.getTime();
    }

    return *this;
}

void CtiQueueDataMsg::saveGuts(RWvostream &aStream) const
{
    Inherited::saveGuts(aStream);
    aStream << getId() << getQueueCount() << getRate() << getRequestId() << getRequestIdCount() << getTime() << UserMessageId();
}

void CtiQueueDataMsg::restoreGuts(RWvistream& aStream)
{
    Inherited::restoreGuts(aStream);

    long           id;
    unsigned       rate;              // Time in ms to send out 1 entry
    unsigned       queueCount;        // Count of items in queue
    long           requestId;         // RequestID, if any.
    unsigned       requestIdCount;    // Count of items with requestID _requestID
    long           userMessageId;
    CtiTime        aTime;

    aStream >> id >> queueCount >> rate >> requestId >> requestIdCount >> aTime >> userMessageId;

    setId(id);
    setQueueCount(queueCount);
    setRate(rate);
    setRequestId(requestId);
    setRequestIdCount(requestIdCount);
    setTime(aTime);
    setUserMessageId(userMessageId);
}

long  CtiQueueDataMsg::getId() const
{
    return _queueID;
}
CtiQueueDataMsg& CtiQueueDataMsg::setId( const long a_id )
{
    _queueID = a_id;
    return *this;
}

long  CtiQueueDataMsg::getRequestId() const
{
    return _requestId;
}
CtiQueueDataMsg& CtiQueueDataMsg::setRequestId( const long a_id )
{
    _requestId = a_id;
    return *this;
}

unsigned  CtiQueueDataMsg::getRate() const
{
    return _rate;
}
CtiQueueDataMsg& CtiQueueDataMsg::setRate( const unsigned rate )
{
    _rate = rate;
    return *this;
}

unsigned  CtiQueueDataMsg::getQueueCount() const
{
    return _queueCount;
}
CtiQueueDataMsg& CtiQueueDataMsg::setQueueCount( const unsigned queueCount )
{
    _queueCount = queueCount;
    return *this;
}

unsigned  CtiQueueDataMsg::getRequestIdCount() const
{
    return _requestIdCount;
}
CtiQueueDataMsg& CtiQueueDataMsg::setRequestIdCount( const unsigned requestIdCount )
{
    _requestIdCount = requestIdCount;
    return *this;
}

long  CtiQueueDataMsg::UserMessageId() const
{
    return _userMessageID;
}
CtiQueueDataMsg& CtiQueueDataMsg::setUserMessageId( const long userMessageID )
{
    _userMessageID = userMessageID;
    return *this;
}

const CtiTime& CtiQueueDataMsg::getTime() const
{
    return _time;
}

CtiTime& CtiQueueDataMsg::getTime()
{
    return _time;
}

CtiQueueDataMsg& CtiQueueDataMsg::setTime(const CtiTime& aTime)
{
    _time = aTime;
    return *this;
}

void CtiQueueDataMsg::dump() const
{
    Inherited::dump();

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);

        dout << " QueueID                       " << getId() << endl;
        dout << " Rate                          " << getRate() << endl;
        dout << " QueueCount                    " << getQueueCount() << endl;
        dout << " RequestId                     " << getRequestId() << endl;
        dout << " RequestIdCount                " << getRequestIdCount() << endl;
        dout << " Time                          " << getTime() << endl;
        dout << " UserMessageID                 " << UserMessageId() << endl;
    }

}

// Return a new'ed copy of this message!
CtiMessage* CtiQueueDataMsg::replicateMessage() const
{
    CtiQueueDataMsg *ret = CTIDBG_new CtiQueueDataMsg(*this);

    return( (CtiMessage*)ret );
}

