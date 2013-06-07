#pragma once

#include "message.h"
#include "yukon.h"

class IM_EX_MSG CtiCommErrorHistoryMsg : public CtiMessage
{
public:
    DECLARE_COLLECTABLE( CtiCommErrorHistoryMsg )

public:

   long        _commErrorId;        // .
   long        _paoId;              // .
   CtiTime      _dateTime;           // .
   int         _errorType;          // .
   long        _errorNumber;        // .
   std::string   _command;            // .
   std::string   _outMessage;         // .
   std::string   _inMessage;          // .

public:
   typedef CtiMessage Inherited;

   CtiCommErrorHistoryMsg(
                          long       paoid      = 0,
                          int        type       = 0,
                          long       errornum   = 0,
                          std::string  command    = std::string(),
                          std::string  outmess    = std::string(),
                          std::string  inmess     = std::string(),
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

   const std::string& getCommand() const;
   CtiCommErrorHistoryMsg& setCommand(const std::string& aString);

   const std::string& getOutMessage() const;
   CtiCommErrorHistoryMsg& setOutMessage(const std::string& aString);

   const std::string& getInMessage() const;
   CtiCommErrorHistoryMsg& setInMessage(const std::string& aString);

   virtual CtiMessage* replicateMessage() const;

   virtual void dump() const;

   virtual bool isValid();
};
