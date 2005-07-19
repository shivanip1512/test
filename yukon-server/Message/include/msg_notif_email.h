
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
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/07/19 22:48:54 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include <rw/cstring.h>
#include "collectable.h"
#include "logger.h"
#include "dllbase.h"
#include "message.h"
#include "yukon.h"
#include "msg_notif_email_attachment.h"

class IM_EX_MSG CtiNotifEmailMsg : public CtiMessage
{
private:
   
   int                           _notifGroupID;
   RWCString                     _to;          
   RWCString                     _subject;
   RWCString                     _body;
   RWCString                     _toCC;        
   RWCString                     _toBCC;       
   RWOrdered                     _attachments;

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
   void setAttachment( RWCString file );
   int getNotifGroupId( void );
   void setNotifGroupId( int );
   RWCString getTo( void );
   void setTo( RWCString );
   RWCString getSubject( void );
   void setSubject( RWCString );
   RWCString getBody( void );
   void setBody( RWCString );
   RWCString getToCC( void );
   void setToCC( RWCString );
   RWCString getToBCC( void );
   void setToBCC( RWCString );
   RWOrdered& getAttachments( void );

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
   RWCString                     _to;          
   RWCString                     _subject;
   RWCString                     _body;
   RWCString                     _toCC;        
   RWCString                     _toBCC;       
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
   void setAttachment( RWCString file );
   int getCustomerId( void );
   void setCustomerId( int );
   RWCString getTo( void );
   void setTo( RWCString );
   RWCString getSubject( void );
   void setSubject( RWCString );
   RWCString getBody( void );
   void setBody( RWCString );
   RWCString getToCC( void );
   void setToCC( RWCString );
   RWCString getToBCC( void );
   void setToBCC( RWCString );
   RWOrdered& getAttachments( void );

protected:

};
#endif // #ifndef __MSG_NOTIF_EMAIL_H__
