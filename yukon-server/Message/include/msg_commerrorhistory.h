/*-----------------------------------------------------------------------------*
*
* File:   msg_commerrorhistory
*
* Date:   9/21/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/INCLUDE/msg_commerrorhistory.h-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/12/20 17:18:54 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __MSG_COMMERRORHISTORY_H__
#define __MSG_COMMERRORHISTORY_H__


#include "message.h"
#include "yukon.h"

class IM_EX_MSG CtiCommErrorHistoryMsg : public CtiMessage
{
protected:

   long        _commErrorId;        // .
   long        _paoId;              // .
   CtiTime      _dateTime;           // .
   int         _errorType;          // .
   long        _errorNumber;        // .
   string   _command;            // .
   string   _outMessage;         // .
   string   _inMessage;          // .

private:

public:

   RWDECLARE_COLLECTABLE( CtiCommErrorHistoryMsg );

   typedef CtiMessage Inherited;

   CtiCommErrorHistoryMsg(
                          long       paoid      = 0,
                          int        type       = 0,
                          long       errornum   = 0,
                          string  command    = string(),
                          string  outmess    = string(),
                          string  inmess     = string(),
                          int        pri        = 7,
                          CtiTime     time       = CtiTime(),
                          long       ceid       = 0
                          );

   CtiCommErrorHistoryMsg(const CtiCommErrorHistoryMsg& aRef);
   virtual ~CtiCommErrorHistoryMsg();

   CtiCommErrorHistoryMsg& operator=(const CtiCommErrorHistoryMsg& aRef);

   long  getCommErrorId() const;
   CtiCommErrorHistoryMsg& setCommErrorId( const long a_id );

   long  getPAOId() const;
   CtiCommErrorHistoryMsg& setPAOId( const long a_id );

   const CtiTime& getDateTime() const;
   CtiCommErrorHistoryMsg& setDateTime(const CtiTime& time);

   int  getErrorType() const;
   CtiCommErrorHistoryMsg& setErrorType( const int type );

   long  getErrorNumber() const;
   CtiCommErrorHistoryMsg& setErrorNumber( const long number );

   const string& getCommand() const;
   CtiCommErrorHistoryMsg& setCommand(const string& string);

   const string& getOutMessage() const;
   CtiCommErrorHistoryMsg& setOutMessage(const string& string);

   const string& getInMessage() const;
   CtiCommErrorHistoryMsg& setInMessage(const string& string);

   virtual void saveGuts(RWvostream &aStream) const;
   virtual void restoreGuts(RWvistream& aStream);
   virtual CtiMessage* replicateMessage() const;

   virtual void dump() const;

   virtual bool isValid();
};
#endif // #ifndef __MSG_COMMERRORHISTORY_H__
