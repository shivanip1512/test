
#pragma warning( disable : 4786)
#ifndef __MSG_NOTIF_EMAIL_H__
#define __MSG_NOTIF_EMAIL_H__

/*---------------------------------------------------------------------------------*
*
* File:   msg_notif_email
*
* Class:
* Date:   1/22/2004
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2006/04/26 19:43:31 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#include <string>

#include "collectable.h"
#include "logger.h"
#include "dllbase.h"
#include "message.h"
#include "yukon.h"
#include "msg_notif_email_attachment.h"

#include <rw/ordcltn.h>

class IM_EX_MSG CtiNotifEmailMsg : public CtiMessage
{
private:

   int                                          _notifGroupID;
   std::string                                       _to;
   std::string                                       _subject;
   std::string                                       _body;
   std::string                                       _toCC;
   std::string                                       _toBCC;
   std::vector<CtiNotifEmailAttachmentMsg*>      _attachments;

   CtiNotifEmailAttachmentMsg    *_notifAttachment;

public:

   RWDECLARE_COLLECTABLE( CtiNotifEmailMsg );

   typedef CtiMessage Inherited;

   CtiNotifEmailMsg();
   //NEED TO ADD A COPY CONSTRUCTOR!
   virtual ~CtiNotifEmailMsg();

   void saveGuts( RWvostream &aStream ) const;
   void restoreGuts( RWvistream& aStream );
   virtual void dump() const;

   CtiMessage* replicateMessage() const;
   void setAttachment( std::string file );
   int getNotifGroupId( void );
   void setNotifGroupId( int );
   std::string getTo( void );
   void setTo( std::string );
   std::string getSubject( void );
   void setSubject( std::string );
   std::string getBody( void );
   void setBody( std::string );
   std::string getToCC( void );
   void setToCC( std::string );
   std::string getToBCC( void );
   void setToBCC( std::string );
   std::vector<CtiNotifEmailAttachmentMsg*>& getAttachments( void );

protected:

};


/**
 * DEPRECATED, dont use this in any new code.
 * It exists so that old CtiEmail code can use this for now
 */
class IM_EX_MSG CtiCustomerNotifEmailMsg : public CtiMessage
{
private:

   int                           _customerID;
   std::string                     _to;
   std::string                     _subject;
   std::string                     _body;
   std::string                     _toCC;
   std::string                     _toBCC;
   RWOrdered                     _attachments;

   CtiNotifEmailAttachmentMsg    *_notifAttachment;

public:

   RWDECLARE_COLLECTABLE( CtiCustomerNotifEmailMsg );

   typedef CtiMessage Inherited;

   CtiCustomerNotifEmailMsg();
   //NEED TO ADD A COPY CONSTRUCTOR!
   virtual ~CtiCustomerNotifEmailMsg();

   void saveGuts( RWvostream &aStream ) const;
   void restoreGuts( RWvistream& aStream );
   virtual void dump() const;

   CtiMessage* replicateMessage() const;
   void setAttachment( std::string file );
   int getCustomerId( void );
   void setCustomerId( int );
   std::string getTo( void );
   void setTo( std::string );
   std::string getSubject( void );
   void setSubject( std::string );
   std::string getBody( void );
   void setBody( std::string );
   std::string getToCC( void );
   void setToCC( std::string );
   std::string getToBCC( void );
   void setToBCC( std::string );
   RWOrdered& getAttachments( void );

protected:

};
#endif // #ifndef __MSG_NOTIF_EMAIL_H__
