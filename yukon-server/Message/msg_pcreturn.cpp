#include "precompiled.h"

#include "msg_pcreturn.h"
#include "dsm2.h"  //  for PIL_ECHO
#include "logger.h"

using namespace std;

DEFINE_COLLECTABLE( CtiReturnMsg, MSG_PCRETURN );

CtiReturnMsg::~CtiReturnMsg()
{
}

long CtiReturnMsg::DeviceId() const
{
    return _device_id;
}

CtiReturnMsg& CtiReturnMsg::setDeviceId(long device_id)
{
    _device_id = device_id;
    return *this;
}

const string& CtiReturnMsg::CommandString() const
{
    return _command_string;
}

CtiReturnMsg& CtiReturnMsg::setCommandString(const string& command_string)
{
    _command_string = command_string;
    return *this;
}

const string& CtiReturnMsg::ResultString() const
{
    return _result_string;
}

CtiReturnMsg& CtiReturnMsg::setResultString(const string& result_string)
{
    _result_string = result_string;
    return *this;
}

int CtiReturnMsg::Status() const
{
    return _status;
}

CtiReturnMsg& CtiReturnMsg::setStatus(int status)
{
    _status = status;
    return *this;
}

int CtiReturnMsg::RouteID() const
{
    return _routeid;
}

CtiReturnMsg& CtiReturnMsg::setRouteID(int route)
{
    _routeid = route;
    return *this;
}

Cti::MacroOffset CtiReturnMsg::MacroOffset() const
{
    return _macro_offset;
}

CtiReturnMsg& CtiReturnMsg::setMacroOffset(const Cti::MacroOffset& macro_offset)
{
    _macro_offset = macro_offset;
    return *this;
}

int CtiReturnMsg::AttemptNum() const
{
    return _attempt_num;
}

CtiReturnMsg& CtiReturnMsg::setAttemptNum(int attempt_num)
{
    _attempt_num = attempt_num;
    return *this;
}

long CtiReturnMsg::GroupMessageId() const
{
    return _group_message_id;
}

CtiReturnMsg& CtiReturnMsg::setGroupMessageId(long group_message_id)
{
    _group_message_id = group_message_id;
    return *this;
}

long CtiReturnMsg::UserMessageId() const
{
    return _user_message_id;
}

CtiReturnMsg& CtiReturnMsg::setUserMessageId(long user_message_id)
{
    _user_message_id = user_message_id;
    return *this;
}

const CtiMultiMsg_vec& CtiReturnMsg::PointData() const
{
    return Inherited::getData();
}

CtiMultiMsg_vec& CtiReturnMsg::PointData()
{
    return Inherited::getData();
}

CtiReturnMsg& CtiReturnMsg::setPointData(const CtiMultiMsg_vec& point_data)
{
    Inherited::setData( point_data );
    return *this;
}

// Return a new'ed copy of this message!
CtiMessage* CtiReturnMsg::replicateMessage() const
{
   CtiReturnMsg *ret = CTIDBG_new CtiReturnMsg(*this);

   return( (CtiMessage*)ret );
}

bool CtiReturnMsg::ExpectMore() const
{
   return _expectMore;
}
CtiReturnMsg& CtiReturnMsg::setExpectMore(bool more)
{
   _expectMore = more;
   return *this;
}

std::string CtiReturnMsg::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiReturnMsg";
    itemList.add("Device ID")        << _device_id;
    itemList.add("Command String")   << _command_string;
    itemList.add("Result String")    << _result_string;
    itemList.add("Status")           << _status;
    itemList.add("Route ID")         << _routeid;
    itemList.add("Macro Offset")     << _macro_offset.asString();
    itemList.add("Attempt Number")   << _attempt_num;
    itemList.add("Expect More")      << _expectMore; // Another message shall be coming listen for more!
    itemList.add("Group Message ID") << _group_message_id;
    itemList.add("User Message ID")  << _user_message_id;

    return (Inherited::toString() += itemList.toString());
}

CtiReturnMsg::CtiReturnMsg() :
    _expectMore(false),
    _device_id(-1L),
    _status(0),
    _routeid(0),
    _macro_offset(Cti::MacroOffset::none),
    _attempt_num(0),
    _group_message_id(0),
    _user_message_id(0)
{
}

CtiReturnMsg::CtiReturnMsg(long device_id,
                 const string& command_string,
                 const string& result_string,
                 int status,
                 int routeid,
                 Cti::MacroOffset macro_offset,
                 int attempt_num,
                 long group_message_id,
                 long user_message_id,
                 int soe,
                 CtiMultiMsg_vec &data) :
     _expectMore(false),
     _device_id(device_id),
     _command_string(command_string),
     _result_string(result_string),
     _status(status),
     _routeid(routeid),
     _macro_offset(macro_offset),
     _attempt_num(attempt_num),
     _group_message_id(group_message_id),
     _user_message_id(user_message_id),
     Inherited(data)
{
    Inherited::setSOE(soe);
}


CtiReturnMsg::CtiReturnMsg(long device_id,
                           const PIL_ECHO &pil_echo,
                           const string &result_string,
                           int status) :
     _expectMore(false),
     _device_id(device_id),
     _command_string(pil_echo.CommandStr),
     _result_string(result_string),
     _status(status),
     _routeid(pil_echo.RouteID),
     _macro_offset(pil_echo.RetryMacroOffset),
     _attempt_num(pil_echo.Attempt),
     _group_message_id(pil_echo.GrpMsgID),
     _user_message_id(pil_echo.UserID)
{
    Inherited::setSOE(pil_echo.SOE);
}


CtiReturnMsg::CtiReturnMsg(const CtiReturnMsg &aRef)
{
    *this = aRef;
}

CtiReturnMsg& CtiReturnMsg::operator=(const CtiReturnMsg& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);

        _device_id          = aRef.DeviceId();
        _command_string     = aRef.CommandString();
        _result_string      = aRef.ResultString();
        _status             = aRef.Status();
        _routeid            = aRef.RouteID();
        _macro_offset       = aRef.MacroOffset();
        _attempt_num        = aRef.AttemptNum();
        _group_message_id    = aRef.GroupMessageId();
        _user_message_id    = aRef.UserMessageId();
        _expectMore         = aRef.ExpectMore();

        delete_container( PointData() );
        PointData().clear();     // Make sure it is empty!

        for(int i = 0; i < aRef.PointData().size(); i++)
        {
            // This guy creates a copy of himself and returns a CtiMessage pointer to the copy!
            CtiMessage* newp = ((CtiMessage*)aRef.PointData()[i])->replicateMessage();
            PointData().push_back(newp);
        }
    }

    return *this;
}

