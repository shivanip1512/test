/*-----------------------------------------------------------------------------*
*
* File:   msg_commerrorhistory
*
* Date:   9/21/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/INCLUDE/msg_commerrorhistory.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2002/04/22 18:25:15 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __MSG_COMMERRORHISTORY_H__
#define __MSG_COMMERRORHISTORY_H__

#include <rw/rwtime.h>
#include <rw/cstring.h>
#include "message.h"
#include "yukon.h"

class IM_EX_MSG CtiCommErrorHistoryMsg : public CtiMessage
{
protected:

   long        _commErrorId;        // .
   long        _paoId;              // .
   RWTime      _dateTime;           // .
   int         _errorType;          // .
   long        _errorNumber;        // .
   RWCString   _command;            // .
   RWCString   _outMessage;         // .
   RWCString   _inMessage;          // .

private:

public:

   RWDECLARE_COLLECTABLE( CtiCommErrorHistoryMsg );

   typedef CtiMessage Inherited;

   CtiCommErrorHistoryMsg(long       ceid   = 0,
                          long       paoid  = 0,
                          RWTime     time   = RWTime(),
                          int        type   = 0,
                          long       numb   = 0,
                          RWCString  comm   = RWCString(),
                          RWCString  out    = RWCString(),
                          RWCString  in     = RWCString(),
                          int        pri    = 7);

   CtiCommErrorHistoryMsg(const CtiCommErrorHistoryMsg& aRef);
   virtual ~CtiCommErrorHistoryMsg();

   CtiCommErrorHistoryMsg& operator=(const CtiCommErrorHistoryMsg& aRef);

   long  getCommErrorId() const;
   CtiCommErrorHistoryMsg& setCommErrorId( const long a_id );

   long  getPAOId() const;
   CtiCommErrorHistoryMsg& setPAOId( const long a_id );

   const RWTime& getDateTime() const;
   CtiCommErrorHistoryMsg& setDateTime(const RWTime& time);

   int  getErrorType() const;
   CtiCommErrorHistoryMsg& setErrorType( const int type );

   long  getErrorNumber() const;
   CtiCommErrorHistoryMsg& setErrorNumber( const long number );

   const RWCString& getCommand() const;
   CtiCommErrorHistoryMsg& setCommand(const RWCString& string);

   const RWCString& getOutMessage() const;
   CtiCommErrorHistoryMsg& setOutMessage(const RWCString& string);

   const RWCString& getInMessage() const;
   CtiCommErrorHistoryMsg& setInMessage(const RWCString& string);

   virtual void saveGuts(RWvostream &aStream) const;
   virtual void restoreGuts(RWvistream& aStream);
   virtual CtiMessage* replicateMessage() const;

   virtual void dump() const;

   virtual bool isValid();
};
#endif // #ifndef __MSG_COMMERRORHISTORY_H__
