
#pragma warning( disable : 4786)
#ifndef __MSG_EMAIL_H__
#define __MSG_EMAIL_H__

/*-----------------------------------------------------------------------------*
*
* File:   msg_email
*
* Class:  CtiEmailMsg
* Date:   4/2/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/INCLUDE/msg_email.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2002/04/30 16:26:59 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw/cstring.h>
#include "dllbase.h"
#include "message.h"
#include "yukon.h"

class IM_EX_MSG CtiEmailMsg : public CtiMessage
{
public:

   typedef enum
   {
      DefaultEmailType,
      CICustomerEmailType,
      DeviceIDEmailType,
      PointIDEmailType,
      NGroupIDEmailType,
      LocationIDEmailType,

      InvalidEmailType

   } CtiEmailType;

protected:

   LONG        _id;                 // This is an id based upon the EmailType.
   INT         _idType;
   RWCString   _sender;
   RWCString   _subject;
   RWCString   _text;

private:

public:

   RWDECLARE_COLLECTABLE( CtiEmailMsg );

   typedef CtiMessage Inherited;

   CtiEmailMsg( LONG id = -1L, INT type = DefaultEmailType);
   CtiEmailMsg(const CtiEmailMsg& aRef);
   virtual ~CtiEmailMsg();

   CtiEmailMsg& operator=(const CtiEmailMsg& aRef);
   LONG getID() const;
   CtiEmailMsg& setID(const LONG id);

   INT getType() const;
   CtiEmailMsg& setType(const INT type);

   RWCString getSubject() const;
   CtiEmailMsg& setSubject(const RWCString &str);

   RWCString getSender() const;
   CtiEmailMsg& setSender(const RWCString &str);

   RWCString getText() const;
   CtiEmailMsg& setText(const RWCString &str);

   virtual void saveGuts(RWvostream &aStream) const;
   virtual void restoreGuts(RWvistream& aStream);
   virtual CtiMessage* replicateMessage() const;

   virtual void dump() const;

};
#endif // #ifndef __MSG_EMAIL_H__
