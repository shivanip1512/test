/*-----------------------------------------------------------------------------*
*
* File:   msg_signal
*
* Date:   1/21/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:59:27 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __MSG_SIGNAL_H__
#define __MSG_SIGNAL_H__

#include <rw/cstring.h>
#include "message.h"
#include "yukon.h"

class IM_EX_MSG CtiSignalMsg : public CtiMessage
{
protected:

   long        _id;                 // System point or point id this message is associated with.
   int         _logType;            // Identifies the signal type.
   unsigned    _signalGroup;        // Which alarm group should be addressed. One is an event class and not sent.
   //RWCString   _desc;               // What is this textually
   //RWCString   _action;             // What is the short form of what happened?
   RWCString   _text;               // What happened
   RWCString   _additional;         // Additional Info.  What is special about this happening?
   unsigned    _tags;               // Alarm states;.. Bit field frome pointdefs.h
   RWCString   _user;               // Who caused this to happen?

private:

public:

   RWDECLARE_COLLECTABLE( CtiSignalMsg );

   typedef CtiMessage Inherited;

   CtiSignalMsg(long       pid   = 0,
                int        soe   = 0,
                RWCString  text  = RWCString(),
                RWCString  addl  = RWCString(),
                int        lt    = GeneralLogType,
                unsigned   cls   = SignalEvent,
                RWCString  usr   = RWCString(""),
                unsigned   tag   = 0,
                int        pri   = 7 );/* :
      Inherited(pri),
      _id(pid),
      _logType(lt),
      _signalGroup(cls),
      _text(text),
      _additional(addl),
      _tags(tag)
   {
      Inherited::setSOE(soe);
      Inherited::setUser(usr);
   }                                     */

   CtiSignalMsg(const CtiSignalMsg& aRef);
   virtual ~CtiSignalMsg();

   CtiSignalMsg& operator=(const CtiSignalMsg& aRef);
   long  getId() const;
   CtiSignalMsg& setId( const long a_id );

   const RWCString& getText() const;
   CtiSignalMsg& setText(const RWCString& string);

   const RWCString& getAdditionalInfo() const;
   CtiSignalMsg& setAdditionalInfo(const RWCString& string);

   unsigned getSignalGroup() const;
   CtiSignalMsg& setSignalGroup(const unsigned cls);

   unsigned getTags() const;
   CtiSignalMsg& setTags(const unsigned s);
   CtiSignalMsg& resetTags(const unsigned s = 0xffffffff);

   int getLogType() const;
   CtiSignalMsg& setLogType(const int soe);

   virtual void saveGuts(RWvostream &aStream) const;
   virtual void restoreGuts(RWvistream& aStream);
   virtual CtiMessage* replicateMessage() const;

   virtual void dump() const;

   BOOL isAlarm() const;
   BOOL isEvent() const;

};
#endif // #ifndef __MSG_SIGNAL_H__
