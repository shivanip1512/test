
/*-----------------------------------------------------------------------------*
*
* File:   msg_queuedata
*
* Date:   1/22/2007
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/INCLUDE/msg_queuedata.h-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2007/01/22 21:39:32 $
*
* Copyright (c) 2007 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <windows.h>
#include <iostream>
#include <iomanip>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore


#include "message.h"
#include "logger.h"
#include "msg_queuedata.h"
#include "collectable.h"


RWDEFINE_COLLECTABLE( CtiQueueDataMsg, MSG_POINTDATA );

CtiQueueDataMsg::CtiQueueDataMsg(
                                long       id,
                                unsigned   queueCount,
                                unsigned   rate,
                                long       requestId,
                                unsigned   requestIdCount ) :
_id(id),
_queueCount(queueCount),
_rate(rate),
_requestId(requestId),
_requestIdCount(requestIdCount),
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

        _id                 = aRef.getId();            
        _requestId          = aRef.getRequestId();
        _rate               = aRef.getRate();
        _queueCount         = aRef.getQueueCount();
        _requestIdCount     = aRef.getRequestIdCount();

        _time               = aRef.getTime();
    }

    return *this;
}

void CtiQueueDataMsg::saveGuts(RWvostream &aStream) const
{
    Inherited::saveGuts(aStream);
    aStream << getId() << getQueueCount() << getRate() << getRequestId() << getRequestIdCount() << getTime();
}

void CtiQueueDataMsg::restoreGuts(RWvistream& aStream)
{
    Inherited::restoreGuts(aStream);

    long           id;
    unsigned       rate;              // Time in ms to send out 1 entry
    unsigned       queueCount;        // Count of items in queue
    long           requestId;         // RequestID, if any.
    unsigned       requestIdCount;    // Count of items with requestID _requestID
    CtiTime        time;

    aStream >> id >> queueCount >> rate >> requestId >> requestIdCount >> time;

    setId(id);
    setQueueCount(queueCount);
    setRate(rate);
    setRequestId(requestId);
    setRequestIdCount(requestIdCount);
    setTime(time);
}

long  CtiQueueDataMsg::getId() const
{
    return _id;
}
CtiQueueDataMsg& CtiQueueDataMsg::setId( const long a_id )
{
    _id = a_id;
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

        dout << " Id                            " << getId() << endl;
        dout << " Rate                          " << getRate() << endl;
        dout << " QueueCount                    " << getQueueCount() << endl;
        dout << " RequestId                     " << getRequestId() << endl;
        dout << " RequestIdCount                " << getRequestIdCount() << endl;
        dout << " Time                          " << getTime() << endl;
    }

}

// Return a new'ed copy of this message!
CtiMessage* CtiQueueDataMsg::replicateMessage() const
{
    CtiQueueDataMsg *ret = CTIDBG_new CtiQueueDataMsg(*this);

    return( (CtiMessage*)ret );
}

