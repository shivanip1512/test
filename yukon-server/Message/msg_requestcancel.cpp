
/*-----------------------------------------------------------------------------*
*
* File:   msg_requestcancel
*
* Date:   1/22/2007
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/INCLUDE/msg_queuedata.h-arc  $
* REVISION     :  $Revision: 1.3.2.1 $
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
#include "msg_requestcancel.h"
#include "collectable.h"


DEFINE_COLLECTABLE( CtiRequestCancelMsg, MSG_REQUESTCANCEL );

//REGISTER_MSG_FACTORY( CtiRequestCancelMsg );

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

void CtiRequestCancelMsg::dump() const
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);

        dout << " ------ Cancel Response ------ " << typeString() << endl;
        dout << " RequestId                     " << getRequestId() << endl;
        dout << " Cancelled ID Count            " << getRequestIdCount() << endl;
        dout << " Time                          " << getTime() << endl;
        dout << " UserMessageID                 " << UserMessageId() << endl;
    }

}

// Return a new'ed copy of this message!
CtiMessage* CtiRequestCancelMsg::replicateMessage() const
{
    CtiRequestCancelMsg *ret = CTIDBG_new CtiRequestCancelMsg(*this);

    return( (CtiMessage*)ret );
}

