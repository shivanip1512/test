/*-----------------------------------------------------------------------------*
*
* File:   msg_pcrequest
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/msg_pcrequest.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/02/10 23:23:53 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <windows.h>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw/collect.h>

#include "msg_pcrequest.h"
#include "logger.h"
#include "collectable.h"
#include "dllbase.h"

RWDEFINE_COLLECTABLE( CtiRequestMsg, MSG_PCREQUEST );

void CtiRequestMsg::saveGuts(RWvostream &aStream) const
{
    CtiMessage::saveGuts(aStream);

    aStream <<
       DeviceId() <<
       CommandString() <<
       RouteId() <<
       MacroOffset() <<
       AttemptNum() <<
       TransmissionId()  <<
       UserMessageId() <<
       OptionsField();
}

void CtiRequestMsg::restoreGuts(RWvistream& aStream)
{
    CtiMessage::restoreGuts(aStream);

    aStream >>
       _device_id >>
       _command_string >>
       _route_id >>
       _macro_offset >>
       _attempt_num >>
       _transmission_id >>
       _user_message_id >>
       _options_field;
}

long CtiRequestMsg::DeviceId() const
{
    return _device_id;
}

CtiRequestMsg& CtiRequestMsg::setDeviceId( long device_id )
{
    _device_id = device_id;
    return *this;
}

const RWCString& CtiRequestMsg::CommandString() const
{
    return _command_string;
}

CtiRequestMsg& CtiRequestMsg::setCommandString(const RWCString& command_string)
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

int CtiRequestMsg::MacroOffset() const
{
    return _macro_offset;
}

CtiRequestMsg& CtiRequestMsg::setMacroOffset(int macro_offset)
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

long CtiRequestMsg::TransmissionId() const
{
    return _transmission_id;
}

CtiRequestMsg& CtiRequestMsg::setTransmissionId(long transmission_id )
{
    _transmission_id = transmission_id;
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

int CtiRequestMsg::OptionsField() const
{
    return _options_field;
}

CtiRequestMsg& CtiRequestMsg::setOptionsField(int options_field)
{
    _options_field = options_field;
    return *this;
}

// Return a new'ed copy of this message!
CtiMessage* CtiRequestMsg::replicateMessage() const
{
   CtiRequestMsg *ret = CTIDBG_new CtiRequestMsg(*this);

   return( (CtiMessage*)ret );
}

void CtiRequestMsg::dump() const
{
   Inherited::dump();

   CtiLockGuard<CtiLogger> doubt_guard(dout);
   dout << " Device ID                     " << _device_id << endl;
   dout << " Command String                " << _command_string << endl;
   dout << " Route ID                      " << _route_id << endl;
   dout << " Macro Offset                  " << _macro_offset << endl;
   dout << " Attempt Number                " << _attempt_num << endl;
   dout << " Transmission ID               " << _transmission_id << endl;
   dout << " User Message ID               " << _user_message_id << endl;
   dout << " Options Field                 " << _options_field << endl;
}

CtiRequestMsg::CtiRequestMsg() :
 _device_id(0),
 _command_string(""),
 _user_message_id(0),
 _transmission_id(0),
 _route_id(0),
 _macro_offset(0),
 _attempt_num(0),
 _options_field(0),
 CtiMessage(0)
 {};

CtiRequestMsg::CtiRequestMsg(long device_id,
               const RWCString& command_string,
               long user_message_id,
               long transmission_id,
               long route_id,
               int macro_offset,
               int attempt_num,
               int options_field,
               int priority_base) :
 _device_id(device_id),
 _command_string(command_string),
 _user_message_id(user_message_id),
 _transmission_id(transmission_id),
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
    Inherited::operator=(aRef);

    _device_id          = aRef.DeviceId();
    _command_string     = aRef.CommandString();
    _route_id           = aRef.RouteId();
    _macro_offset       = aRef.MacroOffset();
    _attempt_num        = aRef.AttemptNum();
    _transmission_id    = aRef.TransmissionId();
    _user_message_id    = aRef.UserMessageId();
    _options_field      = aRef.OptionsField();
 }
 return *this;
}

