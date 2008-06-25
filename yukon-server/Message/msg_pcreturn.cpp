/*-----------------------------------------------------------------------------*
*
* File:   msg_pcreturn
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/msg_pcreturn.cpp-arc  $
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2008/06/25 17:08:42 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <windows.h>
#include <iostream>

#include "msg_pcreturn.h"
#include "logger.h"
#include "collectable.h"
#include "utility.h"

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

RWDEFINE_COLLECTABLE( CtiReturnMsg, MSG_PCRETURN );

CtiReturnMsg::~CtiReturnMsg()
{
}

void CtiReturnMsg::saveGuts(RWvostream &aStream) const
{
    Inherited::saveGuts(aStream);

    aStream << DeviceId() << CommandString() << ResultString() << Status() << RouteID() << MacroOffset() <<
       AttemptNum() << TranmissionId() << UserMessageId() << ExpectMore();
}

void CtiReturnMsg::restoreGuts(RWvistream& aStream)
{
    Inherited::restoreGuts(aStream);

    aStream >> _device_id >> _command_string >> _result_string >> _status >> _routeid >> _macro_offset
    >> _attempt_num >> _transmission_id >> _user_message_id >> _expectMore;
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

int CtiReturnMsg::MacroOffset() const
{
    return _macro_offset;
}

CtiReturnMsg& CtiReturnMsg::setMacroOffset(int macro_offset)
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

long CtiReturnMsg::TranmissionId() const
{
    return _transmission_id;
}

CtiReturnMsg& CtiReturnMsg::setTransmissionId(long transmission_id)
{
    _transmission_id = transmission_id;
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

int CtiReturnMsg::ExpectMore() const
{
   return _expectMore;
}
CtiReturnMsg& CtiReturnMsg::setExpectMore(int more)
{
   _expectMore = more;
   return *this;
}
CtiReturnMsg& CtiReturnMsg::resetExpectMore(int more)
{
   _expectMore = more;
   return *this;
}

void CtiReturnMsg::dump() const
{
   Inherited::dump();

   CtiLockGuard<CtiLogger> doubt_guard(dout);
   dout << " Device ID                     " << _device_id << endl;
   dout << " Command String                " << _command_string << endl;
   dout << " Result String                 " << _result_string << endl;
   dout << " Status                        " << _status << endl;
   dout << " Route ID                      " << _routeid << endl;
   dout << " Macro Offset                  " << _macro_offset << endl;
   dout << " Attempt Number                " << _attempt_num << endl;
   dout << " Expect More                   " << _expectMore << endl;           // Another message shall be coming listen for more!    long       _transmission_id << endl;
   dout << " Transmission ID               " << _transmission_id << endl;
   dout << " User Message ID               " << _user_message_id << endl;

}

CtiReturnMsg::CtiReturnMsg() :
    _expectMore(0),
    _device_id(-1L),
    _status(0),
    _routeid(0),
    _macro_offset(0),
    _attempt_num(0),
    _transmission_id(0),
    _user_message_id(0)
 {};

CtiReturnMsg::CtiReturnMsg(long device_id,
                 const string& command_string,
                 const string& result_string,
                 int status,
                 int routeid,
                 int macro_offset,
                 int attempt_num,
                 long transmission_id,
                 long user_message_id,
                 int soe,
                 CtiMultiMsg_vec &data) :
     _expectMore(0),
     _device_id(device_id),
     _command_string(command_string),
     _result_string(result_string),
     _status(status),
     _routeid(routeid),
     _macro_offset(macro_offset),
     _attempt_num(attempt_num),
     _transmission_id(transmission_id),
     _user_message_id(user_message_id),
     Inherited(data)
 {
    Inherited::setSOE(soe);
 }

CtiReturnMsg::CtiReturnMsg(const CtiReturnMsg &aRef)
 {
    *this = aRef;
 }

CtiReturnMsg& CtiReturnMsg::operator=(const CtiReturnMsg& aRef)
 {
    int i;

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
       _transmission_id    = aRef.TranmissionId();
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

