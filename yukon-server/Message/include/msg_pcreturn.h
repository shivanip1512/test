#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

/*-----------------------------------------------------------------------------*
*
* File:   msg_pcreturn
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/INCLUDE/msg_pcreturn.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:59:26 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#ifndef __MSG_PCRETURN_H__
#define __MSG_PCRETURN_H__

#include <rw/cstring.h>
#include <rw/ordcltn.h>

#include "dlldefs.h"
#include "msg_multi.h"
#include "message.h"

class IM_EX_MSG CtiReturnMsg : public CtiMultiMsg
{
private:

    long       _device_id;
    RWCString  _command_string;        // Replica of the original request (only first 80 characters)
    RWCString  _result_string;         // String representation of the result of the request.
    int        _status;                // Result code of the operation.  Zero if successful
    int        _routeid;               // Route ID which just succeeded, or failed.
    int        _macro_offset;          // Offset into a macro which should/could be tried next, Zero if there are no more.
    int        _attempt_num;           // Number of attempts before we succeeded or failed.. Therefore, should always be at least one.
    int        _expectMore;            // Another message shall be coming related to the request which caused this message. listen for more!
    long       _transmission_id;       // A replica of the request's _transmission_id
    long       _user_message_id;       // A replica of the request's _user_message_id

public:
    RWDECLARE_COLLECTABLE( CtiReturnMsg );

    typedef CtiMultiMsg Inherited;

    CtiReturnMsg();
    CtiReturnMsg(long device_id,
                    const RWCString& command_string = RWCString(),
                    const RWCString& result_string = RWCString(),
                    int status       = 0,
                    int routeid = 0,
                    int macro_offset = 0,
                    int attempt_num  = 0,
                    long transmission_id = -1L,
                    long user_message_id = -1L,
                    int soe = 0,
                    const RWOrdered &data = RWOrdered());
    CtiReturnMsg(const CtiReturnMsg &aRef);
    virtual ~CtiReturnMsg();


    CtiReturnMsg& operator=(const CtiReturnMsg& aRef);
    void saveGuts(RWvostream &aStream) const;
    void restoreGuts(RWvistream& aStream);
    CtiMessage* replicateMessage() const;

    long DeviceId() const;
    CtiReturnMsg& setDeviceId(long device_id);

    const RWCString& CommandString() const;
    CtiReturnMsg& setCommandString(const RWCString& command_string);

    const RWCString& ResultString() const;
    CtiReturnMsg& setResultString(const RWCString& result_string);

    int Status() const;
    CtiReturnMsg& setStatus(int status);

    int ExpectMore() const;
    CtiReturnMsg& setExpectMore(int more = 1);
    CtiReturnMsg& resetExpectMore(int more = 0);

    int RouteID() const;
    CtiReturnMsg& setRouteID(int route);

    int MacroOffset() const;
    CtiReturnMsg& setMacroOffset(int macro_offset);

    int AttemptNum() const;
    CtiReturnMsg& setAttemptNum(int attempt_num);

    long TranmissionId() const;
    CtiReturnMsg& setTransmissionId(long transmission_id);

    long UserMessageId() const;
    CtiReturnMsg& setUserMessageId(long user_message_id);

    const RWOrdered& PointData() const;
    RWOrdered&       PointData();
    CtiReturnMsg&    setPointData(const RWOrdered& point_data);

    virtual void dump() const;
};

#endif
