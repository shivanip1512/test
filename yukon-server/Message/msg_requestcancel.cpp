#include "precompiled.h"

#include "message.h"
#include "logger.h"
#include "msg_requestcancel.h"
#include "collectable.h"

using namespace std;

DEFINE_COLLECTABLE( CtiRequestCancelMsg, MSG_REQUESTCANCEL );

CtiRequestCancelMsg::CtiRequestCancelMsg(
                                long       requestId,
                                unsigned   requestIdCount,
                                long       userMessageId ) :
_requestId(requestId),
_requestIdCount(requestIdCount),
_userMessageID(userMessageId),
_time(CtiTime())
{
}

CtiRequestCancelMsg::CtiRequestCancelMsg(const CtiRequestCancelMsg &aRef)
{
    *this = aRef;
}

CtiRequestCancelMsg::~CtiRequestCancelMsg()
{
}

CtiRequestCancelMsg& CtiRequestCancelMsg::operator=(const CtiRequestCancelMsg& aRef)
{
    if( this != &aRef )
    {
        Inherited::operator=(aRef);

        _requestId          = aRef.getRequestId();
        _requestIdCount     = aRef.getRequestIdCount();
        _userMessageID      = aRef.UserMessageId();

        _time               = aRef.getTime();
    }

    return *this;
}

long  CtiRequestCancelMsg::getRequestId() const
{
    return _requestId;
}
CtiRequestCancelMsg& CtiRequestCancelMsg::setRequestId( const long a_id )
{
    _requestId = a_id;
    return *this;
}

unsigned  CtiRequestCancelMsg::getRequestIdCount() const
{
    return _requestIdCount;
}
CtiRequestCancelMsg& CtiRequestCancelMsg::setRequestIdCount( const unsigned requestIdCount )
{
    _requestIdCount = requestIdCount;
    return *this;
}

long  CtiRequestCancelMsg::UserMessageId() const
{
    return _userMessageID;
}
CtiRequestCancelMsg& CtiRequestCancelMsg::setUserMessageId( const long userMessageID )
{
    _userMessageID = userMessageID;
    return *this;
}

const CtiTime& CtiRequestCancelMsg::getTime() const
{
    return _time;
}

CtiTime& CtiRequestCancelMsg::getTime()
{
    return _time;
}

CtiRequestCancelMsg& CtiRequestCancelMsg::setTime(const CtiTime& aTime)
{
    _time = aTime;
    return *this;
}

std::string CtiRequestCancelMsg::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiRequestCancelMsg";
    itemList.add("RequestId")          << getRequestId();
    itemList.add("Cancelled ID Count") << getRequestIdCount();
    itemList.add("Time")               << getTime();
    itemList.add("UserMessageID")      << UserMessageId();

    return (Inherited::toString() += itemList.toString());
}

// Return a new'ed copy of this message!
CtiMessage* CtiRequestCancelMsg::replicateMessage() const
{
    CtiRequestCancelMsg *ret = CTIDBG_new CtiRequestCancelMsg(*this);

    return( (CtiMessage*)ret );
}

