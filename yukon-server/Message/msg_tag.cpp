#include "precompiled.h"

#include "collectable.h"
#include "logger.h"
#include "msg_tag.h"
#include "numstr.h"

using namespace std;

DEFINE_COLLECTABLE( CtiTagMsg, MSG_TAG );

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

const string& CtiTagMsg::getDescriptionStr() const
{
  return _descriptionStr;
}

CtiTagMsg& CtiTagMsg::setDescriptionStr(const string& desc)
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

const CtiTime& CtiTagMsg::getTagTime() const
{
  return _tagtime;
}

CtiTagMsg& CtiTagMsg::setTagTime(const CtiTime& tagtime)
{
  _tagtime = tagtime;
  return *this;
}

const string& CtiTagMsg::getReferenceStr() const
{
  return _referenceStr;
}

CtiTagMsg& CtiTagMsg::setReferenceStr(const string& ref)
{
  _referenceStr = ref;
  return *this;
}

const string& CtiTagMsg::getTaggedForStr() const
{
  return _taggedForStr;
}

CtiTagMsg& CtiTagMsg::setTaggedForStr(const string& forStr)
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

std::string CtiTagMsg::toString() const
{
    string actn;
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
        actn = "Unknown";
    }

    Cti::FormattedList itemList;

    itemList <<"CtiTagMsg";
    itemList.add("InstanceID")  << getInstanceID();
    itemList.add("PointID")     << getPointID();
    itemList.add("TagID")       << getTagID();
    itemList.add("Description") << getDescriptionStr();
    itemList.add("Action")      << actn <<" ("<< getAction() <<")";
    itemList.add("TagTime")     << getTagTime();
    itemList.add("RefStr")      << getReferenceStr();
    itemList.add("TaggedFor")   << getTaggedForStr();
    itemList.add("ClientMsgId") << getClientMsgId();

    return (Inherited::toString() += itemList.toString());
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

std::size_t CtiTagMsg::getVariableSize() const
{
    return  Inherited::getVariableSize()
      +   dynamic_sizeof( _descriptionStr )
      +   dynamic_sizeof( _referenceStr )
      +   dynamic_sizeof( _taggedForStr );
}

