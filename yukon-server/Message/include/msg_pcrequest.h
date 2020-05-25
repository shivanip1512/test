#pragma once

#include "dlldefs.h"
#include "message.h"
#include "dsm2.h"

class IM_EX_MSG CtiRequestMsg : public CtiMessage
{
public:
    DECLARE_COLLECTABLE( CtiRequestMsg )

public:

    long                _device_id;
    std::string         _command_string;
    long                _route_id;             // What route is to be used to address this device..
    Cti::MacroOffset    _macro_offset;         // Which offset into a macro route should be attempted if the routeid is a macro.
    int                 _attempt_num;          // Number of tries on this particular route. A zero or one have the same effect.
    long                _group_message_id;     // A number which will be echo'd back in any CtiReturnMsg caused by this CtiRequestMsg
    long                _user_message_id;      // A number which will be echo'd back in any CtiReturnMsg caused by this CtiRequestMsg
    long                _options_field;        // Options specific to the message.  One such is a "NO STATS' flag telling the bg to not process statistics on the request.

public:

    typedef CtiMessage Inherited;

    CtiRequestMsg();

    CtiRequestMsg(long device_id,
                  const std::string& command_string,
                  long user_message_id = 0L,
                  long group_message_id= 0L,
                  long route_id        = 0L,
                  Cti::MacroOffset macro_offset = Cti::MacroOffset::none,
                  int  attempt_num     = 0,
                  long options_field   = 0,
                  int  priority_base   = 7);

    CtiRequestMsg(const CtiRequestMsg &aRef);
    CtiRequestMsg& operator=(const CtiRequestMsg& aRef);

    CtiMessage* replicateMessage() const;

    long DeviceId() const;
    CtiRequestMsg& setDeviceId( long device_id );

    const std::string& CommandString() const;
    CtiRequestMsg& setCommandString(const std::string& command_string);

    long RouteId() const;
    CtiRequestMsg& setRouteId(long route_id);

    Cti::MacroOffset MacroOffset() const;
    CtiRequestMsg& setMacroOffset(const Cti::MacroOffset& macro_offset);

    int AttemptNum() const;
    CtiRequestMsg& setAttemptNum(long attempt_num );

    long GroupMessageId() const;
    CtiRequestMsg& setGroupMessageId(long group_message_id );

    long UserMessageId() const;
    CtiRequestMsg& setUserMessageId(long user_message_id );

    long OptionsField() const;
    void setOptionsField(long options_field);

    std::size_t getFixedSize() const override    { return sizeof( *this ); }
    std::size_t getVariableSize() const override;

    std::string toString() const override;
};
