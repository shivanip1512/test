
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   msg_email
*
* Date:   8/3/2003
*
* Author: Aaron Lauinger
*
* Copyright (c) 2003 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <windows.h>
#include <iostream>
#include <iomanip>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include "collectable.h"
#include "logger.h"
#include "msg_tag.h"
#include "numstr.h"
#include "yukon.h"

RWDEFINE_COLLECTABLE( CtiTagMsg, MSG_TAG );

void CtiTagMsg::saveGuts(RWvostream &aStream) const
{
   Inherited::saveGuts(aStream);
   aStream << _instanceid
       << _pointid
       << _tagid
       << _descriptionStr
       << _action
       << _tagtime
       << _referenceStr
       << _taggedForStr
       << _clientMsgId;
}

void CtiTagMsg::restoreGuts(RWvistream& aStream)
{
   Inherited::restoreGuts(aStream);
   aStream >> _instanceid
       >> _pointid
       >> _tagid
       >> _descriptionStr
       >> _action
       >> _tagtime
       >> _referenceStr
       >> _taggedForStr
       >> _clientMsgId;
}

int CtiTagMsg::getInstanceID() const
{
  return _instanceid;
}

CtiTagMsg& CtiTagMsg::setInstanceID(int id)
{
  _instanceid = id;
  return *this;
}

int CtiTagMsg::getPointID() const
{
  return _pointid;
}

CtiTagMsg& CtiTagMsg::setPointID(int id)
{
  _pointid = id;
  return *this;
}

int CtiTagMsg::getTagID() const
{
  return _tagid;
}

CtiTagMsg& CtiTagMsg::setTagID(int id)
{
  _tagid = id;
  return *this;
}

const RWCString& CtiTagMsg::getDescriptionStr() const
{
  return _descriptionStr;
}

CtiTagMsg& CtiTagMsg::setDescriptionStr(const RWCString& desc)
{
  _descriptionStr = desc;
  return *this;
}

int CtiTagMsg::getAction() const
{
  return _action;
}

CtiTagMsg& CtiTagMsg::setAction(int action)
{
  _action = action;
  return *this;
}

const RWTime& CtiTagMsg::getTagTime() const
{
  return _tagtime;
}

CtiTagMsg& CtiTagMsg::setTagTime(const RWTime& tagtime)
{
  _tagtime = tagtime;
  return *this;
}

const RWCString& CtiTagMsg::getReferenceStr() const
{
  return _referenceStr;
}

CtiTagMsg& CtiTagMsg::setReferenceStr(const RWCString& ref)
{
  _referenceStr = ref;
  return *this;
}

const RWCString& CtiTagMsg::getTaggedForStr() const
{
  return _taggedForStr;
}

CtiTagMsg& CtiTagMsg::setTaggedForStr(const RWCString& forStr)
{
  _taggedForStr = forStr;
  return *this;
}

CtiMessage* CtiTagMsg::replicateMessage() const
{
  CtiTagMsg *pNew = CTIDBG_new CtiTagMsg;

   if(pNew != NULL)
   {
      *pNew = *this;
   }

   return pNew;
}

void CtiTagMsg::dump() const
{
    RWCString actn;
    switch(getAction())
    {
    case (CtiTagMsg::AddAction):
        actn = "AddAction";
        break;
    case (CtiTagMsg::UpdateAction):
        actn = "UpdateAction";
        break;
    case (CtiTagMsg::RemoveAction):
        actn = "RemoveAction";
        break;
    case (CtiTagMsg::ReportAction):
        actn = "ReportAction";
        break;
    default:
        actn = RWCString("Unknown ") + CtiNumStr(getAction());
        break;
    }

   CtiLockGuard<CtiLogger> doubt_guard(dout);
   Inherited::dump();

   dout << "InstanceID                     " << getInstanceID() << endl;
   dout << "PointID                        " << getPointID() << endl;
   dout << "TagID                          " << getTagID() << endl;
   dout << "Description                    " << getDescriptionStr() << endl;
   dout << "Action                         " << getAction() << ": " << actn << endl;
   dout << "TagTime                        " << getTagTime() << endl;
   dout << "RefStr                         " << getReferenceStr() << endl;
   dout << "TaggedFor                      " << getTaggedForStr() << endl;
   dout << "ClientMsgId                    " << getClientMsgId() << endl;
}

CtiTagMsg::CtiTagMsg() :
_instanceid(0),        // no two tags share the same one
_pointid(0),
_tagid(0),             // refers to id in tag table
_action(0),             // refers to id in tag table
_clientMsgId(0)
{}

CtiTagMsg::CtiTagMsg(const CtiTagMsg& aRef)
{
  *this = aRef;
}

CtiTagMsg::~CtiTagMsg() {}

CtiTagMsg& CtiTagMsg::operator=(const CtiTagMsg& aRef)
{
  if(this != &aRef)
    {
      Inherited::operator=(aRef);
      setInstanceID(aRef.getInstanceID());
      setPointID(aRef.getPointID());
      setTagID(aRef.getTagID());
      setDescriptionStr(aRef.getDescriptionStr());
      setAction(aRef.getAction());
      setTagTime(aRef.getTagTime());
      setReferenceStr(aRef.getReferenceStr());
      setTaggedForStr(aRef.getTaggedForStr());
      setClientMsgId(aRef.getClientMsgId());
    }
  return *this;
}

int CtiTagMsg::getClientMsgId() const
{
    return _clientMsgId;        // id sourced and returned to clients.  Untouched and unused by dispatch.
}
CtiTagMsg& CtiTagMsg::setClientMsgId(int id)
{
    _clientMsgId = id;
    return *this;
}




