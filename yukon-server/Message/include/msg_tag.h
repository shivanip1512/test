
#pragma warning( disable : 4786)
#ifndef __MSG_TAG_H__
#define __MSG_TAG_H__

/*-----------------------------------------------------------------------------*
*
* File:   msg_tag.h
*
* Class:  CtiTagMsg
* Date:   8/2/2003
*
* Author: Aaron Lauinger
*
* Copyright (c) 2003 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw/cstring.h>
#include <rw/rwtime.h>

#include "dllbase.h"
#include "message.h"
#include "yukon.h"

class IM_EX_MSG CtiTagMsg : public CtiMessage
{
public:
   typedef enum
     {
       AddAction,
       RemoveAction,
       UpdateAction,
       ReportAction

     } CtiTagAction;

protected:

   int _instanceid;        // no two tags share the same one
   int _pointid;
   int _tagid;             // refers to id in tag table
   RWCString _descriptionStr;
   int _action;    // one of CtiTagAction
   RWTime _tagtime;         // when was tag created
   RWCString _referenceStr; // job id, etc, user field
   RWCString _taggedForStr; // user field


   int _clientMsgId;        // id sourced and returned to clients.  Untouched and unused by dispatch.

private:

public:

   RWDECLARE_COLLECTABLE(CtiTagMsg);

   typedef CtiMessage Inherited;

   CtiTagMsg();
   CtiTagMsg(const CtiTagMsg& aRef);
   virtual ~CtiTagMsg();

   CtiTagMsg& operator=(const CtiTagMsg& aRef);

   int getInstanceID() const;
   CtiTagMsg& setInstanceID(int id);

   int getPointID() const;
   CtiTagMsg& setPointID(int id);

   int getTagID() const;
   CtiTagMsg& setTagID(int id);

   const RWCString& getDescriptionStr() const;
   CtiTagMsg& setDescriptionStr(const RWCString& desc);

   int getAction() const;
   CtiTagMsg& setAction(int action);

   const RWTime& getTagTime() const;
   CtiTagMsg& setTagTime(const RWTime& tagtime);

   const RWCString& getReferenceStr() const;
   CtiTagMsg& setReferenceStr(const RWCString& refStr);

   const RWCString& getTaggedForStr() const;
   CtiTagMsg& setTaggedForStr(const RWCString& forStr);

   int getClientMsgId() const;
   CtiTagMsg& setClientMsgId(int id);

   virtual void saveGuts(RWvostream &aStream) const;
   virtual void restoreGuts(RWvistream& aStream);
   virtual CtiMessage* replicateMessage() const;

   virtual void dump() const;

};
#endif // #ifndef __MSG_TAG_H__
