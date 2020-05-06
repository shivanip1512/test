#include "precompiled.h"

#include "logger.h"
#include "msg_queuedata.h"
#include "collectable.h"

DEFINE_COLLECTABLE( CtiQueueDataMsg, MSG_QUEUEDATA );

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

std::string CtiQueueDataMsg::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiQueueDataMsg";
    itemList.add("QueueID")        << getId();
    itemList.add("Rate")           << getRate();
    itemList.add("QueueCount")     << getQueueCount();
    itemList.add("RequestId")      << getRequestId();
    itemList.add("RequestIdCount") << getRequestIdCount();
    itemList.add("Time")           << getTime();
    itemList.add("UserMessageID")  << UserMessageId();

    return (Inherited::toString() += itemList.toString());
}

// Return a new'ed copy of this message!
CtiMessage* CtiQueueDataMsg::replicateMessage() const
{
    CtiQueueDataMsg *ret = CTIDBG_new CtiQueueDataMsg(*this);

    return( (CtiMessage*)ret );
}

std::size_t CtiQueueDataMsg::getVariableSize() const
{
    return Inherited::getVariableSize();
}

