#pragma once

#include <string>

#include "collectable.h"
#include "logger.h"
#include "dllbase.h"
#include "message.h"
#include "yukon.h"


class IM_EX_MSG CtiNotifEmailMsg : public CtiMessage
{
public:
    DECLARE_COLLECTABLE( CtiNotifEmailMsg )

public:

   int                             _notifGroupID;
   std::string                     _to;
   std::string                     _subject;
   std::string                     _body;
   std::string                     _toCC;
   std::string                     _toBCC;

public:

   typedef CtiMessage Inherited;

   CtiNotifEmailMsg();
   //NEED TO ADD A COPY CONSTRUCTOR!
   virtual ~CtiNotifEmailMsg();
   virtual std::string toString() const override;

   CtiMessage* replicateMessage() const;
   int getNotifGroupId() const;
   void setNotifGroupId( int );
   std::string getTo() const;
   void setTo( std::string );
   std::string getSubject() const;
   void setSubject( std::string );
   std::string getBody() const;
   void setBody( std::string );
   std::string getToCC() const;
   void setToCC( std::string );
   std::string getToBCC() const;
   void setToBCC( std::string );
};


/**
 * DEPRECATED, dont use this in any new code.
 * It exists so that old CtiEmail code can use this for now
 */
class IM_EX_MSG CtiCustomerNotifEmailMsg : public CtiMessage
{
public:
    DECLARE_COLLECTABLE( CtiCustomerNotifEmailMsg )

public:

   int                             _customerID;
   std::string                     _to;
   std::string                     _subject;
   std::string                     _body;
   std::string                     _toCC;
   std::string                     _toBCC;

public:

   typedef CtiMessage Inherited;

   CtiCustomerNotifEmailMsg();
   //NEED TO ADD A COPY CONSTRUCTOR!
   virtual ~CtiCustomerNotifEmailMsg();
   virtual std::string toString() const override;

   CtiMessage* replicateMessage() const;
   int getCustomerId() const;
   void setCustomerId( int );
   std::string getTo() const;
   void setTo( std::string );
   std::string getSubject() const;
   void setSubject( std::string );
   std::string getBody() const;
   void setBody( std::string );
   std::string getToCC() const;
   void setToCC( std::string );
   std::string getToBCC() const;
   void setToBCC( std::string );
};
