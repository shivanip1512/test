#include "precompiled.h"

#include "msg_pcrequest.h"
#include "logger.h"
#include "collectable.h"
#include "dllbase.h"

using namespace std;

DEFINE_COLLECTABLE( CtiRequestMsg, MSG_PCREQUEST );

long CtiRequestMsg::DeviceId() const
{
    return _device_id;
}

CtiRequestMsg& CtiRequestMsg::setDeviceId( long device_id )
{
    _device_id = device_id;
    return *this;
}

const string& CtiRequestMsg::CommandString() const
{
    return _command_string;
}

CtiRequestMsg& CtiRequestMsg::setCommandString(const string& command_string)
{
    _command_string = command_string;
    return *this;
}

long CtiRequestMsg::RouteId() const
{
    return _route_id;
}

CtiRequestMsg& CtiRequestMsg::setRouteId(long route_id)
{
    _route_id = route_id;
    return *this;
}

Cti::MacroOffset CtiRequestMsg::MacroOffset() const
{
    return _macro_offset;
}

CtiRequestMsg& CtiRequestMsg::setMacroOffset(const Cti::MacroOffset& macro_offset)
{
    _macro_offset = macro_offset;
    return *this;
}
int CtiRequestMsg::AttemptNum() const
{
    return _attempt_num;
}

CtiRequestMsg& CtiRequestMsg::setAttemptNum(long attempt_num )
{
    _attempt_num = attempt_num;
    return *this;
}

long CtiRequestMsg::GroupMessageId() const
{
    return _group_message_id;
}

CtiRequestMsg& CtiRequestMsg::setGroupMessageId(long group_message_id )
{
    _group_message_id = group_message_id;
    return *this;
}

long CtiRequestMsg::UserMessageId() const
{
    return _user_message_id;
}

CtiRequestMsg& CtiRequestMsg::setUserMessageId(long user_message_id )
{
    _user_message_id = user_message_id;
    return *this;
}

long CtiRequestMsg::OptionsField() const
{
    return _options_field;
}

void CtiRequestMsg::setOptionsField(long options_field)
{
    _options_field = options_field;
}

// Return a new'ed copy of this message!
CtiMessage* CtiRequestMsg::replicateMessage() const
{
   CtiRequestMsg *ret = CTIDBG_new CtiRequestMsg(*this);

   return( (CtiMessage*)ret );
}

std::string CtiRequestMsg::toString() const
{
    Cti::FormattedList itemList;

    itemList << "CtiRequestMsg";
    itemList.add("Device ID")        << _device_id;
    itemList.add("Command String")   << _command_string;
    itemList.add("Route ID")         << _route_id;
    itemList.add("Macro Offset")     << _macro_offset.asString();
    itemList.add("Attempt Number")   << _attempt_num;
    itemList.add("Message Group ID") << _group_message_id;
    itemList.add("User Message ID")  << _user_message_id;
    itemList.add("Options Field")    << _options_field;

    return (Inherited::toString() += itemList.toString());
}

CtiRequestMsg::CtiRequestMsg() :
 _device_id(0),
 _command_string(""),
 _user_message_id(0),
 _group_message_id(0),
 _route_id(0),
 _macro_offset(Cti::MacroOffset::none),
 _attempt_num(0),
 _options_field(0),
 CtiMessage(0)
 {};

CtiRequestMsg::CtiRequestMsg(long device_id,
               const string& command_string,
               long user_message_id,
               long group_message_id,
               long route_id,
               Cti::MacroOffset macro_offset,
               int attempt_num,
               long options_field,
               int priority_base) :
 _device_id(device_id),
 _command_string(command_string),
 _user_message_id(user_message_id),
 _group_message_id(group_message_id),
 _route_id(route_id),
 _macro_offset(macro_offset),
 _attempt_num(attempt_num),
 _options_field(options_field),
 CtiMessage(priority_base)
{};

CtiRequestMsg::CtiRequestMsg(const CtiRequestMsg &aRef)
{
 *this = aRef;
}

CtiRequestMsg& CtiRequestMsg::operator=(const CtiRequestMsg& aRef)
{
 int i;

 if(this != &aRef)
 {
     //  Note that Inherited::ConnectionHandle is NOT copied
     Inherited::operator=(aRef);

    _device_id          = aRef.DeviceId();
    _command_string     = aRef.CommandString();
    _route_id           = aRef.RouteId();
    _macro_offset       = aRef.MacroOffset();
    _attempt_num        = aRef.AttemptNum();
    _group_message_id    = aRef.GroupMessageId();
    _user_message_id    = aRef.UserMessageId();
    _options_field      = aRef.OptionsField();
 }
 return *this;
}

std::size_t CtiRequestMsg::getVariableSize() const
{
    return  Inherited::getVariableSize()
        +   dynamic_sizeof( _command_string );
}

