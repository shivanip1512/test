
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
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/03/02 21:17:00 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include <rw/cstring.h>
#include <rw/ordcltn.h>
#include <rw/collstr.h>

#include "dllbase.h"
#include "message.h"
#include "yukon.h"

class IM_EX_MSG CtiNotifEmailAttachmentMsg : public CtiMessage
{
private:

   RWCString   _fileName;
   char        *_contents;
//   RWOrdered   _attachmentList;
   int         _length;

public:

   RWDECLARE_COLLECTABLE( CtiNotifEmailAttachmentMsg );

   CtiNotifEmailAttachmentMsg( RWCString fileName, char *fileGuts );
   CtiNotifEmailAttachmentMsg( RWCString fileName );
   CtiNotifEmailAttachmentMsg();
   virtual ~CtiNotifEmailAttachmentMsg();

   void saveGuts( RWvostream &aStream ) const;
   void restoreGuts( RWvistream& aStream );

   RWCString getFileName( void );
   void setFileName( RWCString name );
   int getLength( void );
   void setLength( int len );
   void readFile( RWCString name );
   void setContents( char * );
   char* getContents( void );
   CtiMessage* replicateMessage() const;

protected:

};

#endif // #ifndef __MSG_NOTIF_EMAIL_ATTACHMENT_H__
