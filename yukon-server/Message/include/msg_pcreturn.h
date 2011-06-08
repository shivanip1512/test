#pragma once

#include "dlldefs.h"

#include "msg_multi.h"

class IM_EX_MSG CtiReturnMsg : public CtiMultiMsg
{
private:

    long       _device_id;
    std::string  _command_string;        // Replica of the original request (only first 80 characters)
    std::string  _result_string;         // String representation of the result of the request.
    int        _status;                // Result code of the operation.  Zero if successful
    int        _routeid;               // Route ID which just succeeded, or failed.
    int        _macro_offset;          // Offset into a macro which should/could be tried next, Zero if there are no more.
    int        _attempt_num;           // Number of attempts before we succeeded or failed.. Therefore, should always be at least one.
    int        _expectMore;            // Another message shall be coming related to the request which caused this message. listen for more!
    long       _group_message_id;      // A replica of the request's _group_message_id
    long       _user_message_id;       // A replica of the request's _user_message_id

    typedef CtiMultiMsg Inherited;

public:
    RWDECLARE_COLLECTABLE( CtiReturnMsg );

    CtiReturnMsg();

    CtiReturnMsg(long device_id,
                 const std::string& command_string = std::string(),
                 const std::string& result_string = std::string(),
                 int status       = 0,
                 int routeid = 0,
                 int macro_offset = 0,
                 int attempt_num  = 0,
                 long group_message_id = -1L,
                 long user_message_id = -1L,
                 int soe = 0,
                 CtiMultiMsg_vec &data = CtiMultiMsg_vec());

    CtiReturnMsg(long device_id,
                 const PIL_ECHO &pil_echo,
                 const std::string &result_string = std::string(),
                 int status = NoError);

    CtiReturnMsg(const CtiReturnMsg &aRef);
    virtual ~CtiReturnMsg();


    CtiReturnMsg& operator=(const CtiReturnMsg& aRef);
    void saveGuts(RWvostream &aStream) const;
    void restoreGuts(RWvistream& aStream);
    CtiMessage* replicateMessage() const;

    long DeviceId() const;
    CtiReturnMsg& setDeviceId(long device_id);

    const std::string& CommandString() const;
    CtiReturnMsg& setCommandString(const std::string& command_string);

    const std::string& ResultString() const;
    CtiReturnMsg& setResultString(const std::string& result_string);

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

    long GroupMessageId() const;
    CtiReturnMsg& setGroupMessageId(long group_message_id);

    long UserMessageId() const;
    CtiReturnMsg& setUserMessageId(long user_message_id);

    const CtiMultiMsg_vec& PointData() const;
    CtiMultiMsg_vec&       PointData();
    CtiReturnMsg&    setPointData(const CtiMultiMsg_vec& point_data);

    virtual void dump() const;
};

