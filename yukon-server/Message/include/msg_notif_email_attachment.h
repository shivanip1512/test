
#pragma warning( disable : 4786)
#ifndef __MSG_NOTIF_EMAIL_ATTACHMENT_H__
#define __MSG_NOTIF_EMAIL_ATTACHMENT_H__

/*---------------------------------------------------------------------------------*
*
* File:   msg_notif_email_attachment
*
* Class:  
* Date:   1/22/2004
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2005/12/20 17:18:54 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include <rw/ordcltn.h>
#include <rw/collstr.h>

#include "logger.h"
#include "dllbase.h"
#include "message.h"
#include "yukon.h"

class IM_EX_MSG CtiNotifEmailAttachmentMsg : public CtiMessage
{
private:

   string   _fileName;

public:

   RWDECLARE_COLLECTABLE( CtiNotifEmailAttachmentMsg );

   CtiNotifEmailAttachmentMsg( string fileName );
   CtiNotifEmailAttachmentMsg();
   virtual ~CtiNotifEmailAttachmentMsg();
   virtual void dump() const;
   void saveGuts( RWvostream &aStream ) const;
   void restoreGuts( RWvistream& aStream );
   const string &getFileName( void ) const;
   void setFileName( string name );
   int getLength( void );
   void setLength( int len );
   CtiMessage* replicateMessage() const;

protected:

};

#endif // #ifndef __MSG_NOTIF_EMAIL_ATTACHMENT_H__
